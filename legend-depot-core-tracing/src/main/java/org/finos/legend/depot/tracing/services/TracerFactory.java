//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.tracing.services;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.tracing.TracingException;
import org.finos.legend.depot.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.engine.shared.core.operational.prometheus.TracingExports;
import org.finos.legend.opentracing.JerseyClientSender;
import org.finos.legend.opentracing.OpenTracing;
import zipkin2.reporter.InMemoryReporterMetrics;

import javax.inject.Singleton;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;

@Singleton
public final class TracerFactory
{
    private static final TracerFactory INSTANCE = new TracerFactory();
    public static final String DEFAULT_SERVICE_NAME = "legend-depot";
    private static Tracer tracer = NoopTracerFactory.create();
    private static boolean isEnabled = false;

    private TracerFactory()
    {
    }

    public static TracerFactory get()
    {
        return INSTANCE;
    }

    public synchronized Tracer getTracer()
    {
        return tracer;
    }

    public static synchronized TracerFactory configure(OpenTracingConfiguration openTracingConfiguration)
    {
        if (openTracingConfiguration != null && openTracingConfiguration.isEnabled())
        {
            if (openTracingConfiguration.getOpenTracingUri() == null || openTracingConfiguration.getOpenTracingUri().isEmpty())
            {
                throw new TracingException("Invalid uri, openTracingUri cannot be empty");
            }
            if (openTracingConfiguration.getAuthenticationProvider() == null)
            {
                throw new TracingException("Invalid authentication provider, authenticationProvider must be specified");
            }
            String serviceName = openTracingConfiguration.getServiceName() != null && !openTracingConfiguration.getServiceName().isEmpty() ?
                    openTracingConfiguration.getServiceName() : DEFAULT_SERVICE_NAME;
            try (JerseyClientSender sender = new JerseyClientSender(new URI(openTracingConfiguration.getOpenTracingUri()), openTracingConfiguration.getAuthenticationProvider());)
            {

                tracer = OpenTracing.create(sender, serviceName, null, getMemoryMetricsReporter());
                LogManager.getLogManager().getLogger("").setLevel(Level.FINE);
                GlobalTracer.registerIfAbsent(tracer);
            }
            catch (Exception e)
            {
                throw new TracingException("Invalid openTracingUri provided", e);
            }
        }
        return INSTANCE;
    }

    private static InMemoryReporterMetrics getMemoryMetricsReporter()
    {
        CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        InMemoryReporterMetrics tracingMetrics = new InMemoryReporterMetrics();
        collectorRegistry.register(new TracingExports(tracingMetrics));
        return tracingMetrics;
    }

    private Span startSpan(String trace)
    {
        Tracer globalTrace = GlobalTracer.get();
        Span span = globalTrace.activeSpan();
        Span childSPan = null;
        if (span != null)
        {
            childSPan = globalTrace.buildSpan(trace)
                    .asChildOf(span)
                    .start();
        }

        return childSPan;
    }

    private void stopSpan(Span childSPan)
    {
        if (childSPan != null)
        {
            childSPan.finish();
        }
    }

    public void addTags(Map<String, String> tags)
    {
        Span currentSpan = GlobalTracer.get().activeSpan();
        if (currentSpan != null)
        {
            tags.keySet().forEach(key -> currentSpan.setTag(key, tags.get(key)));
        }
    }

    public void addLog(Map<String, String> tags)
    {
        Span currentSpan = GlobalTracer.get().activeSpan();
        if (currentSpan != null)
        {
            currentSpan.log(tags);
        }
    }

    public void log(String value)
    {
        Span currentSpan = GlobalTracer.get().activeSpan();
        if (currentSpan != null)
        {
            currentSpan.log(value);
        }
    }

    public <T> T executeWithTrace(String label, Supplier<T> supplier)
    {
        Span child = INSTANCE.startSpan(label);

        try
        {
            return supplier.get();
        }
        catch (Exception e)
        {
            Span currentSpan = GlobalTracer.get().activeSpan();
            Tags.ERROR.set(currentSpan, true);
            Map<String, String> map = new HashMap<>();
            map.put(Fields.EVENT, "error");
            map.put(Fields.ERROR_OBJECT, e.toString());
            map.put(Fields.MESSAGE, e.getMessage());
            currentSpan.log(map);
            String traceId = currentSpan.context().toTraceId();
            String trace = traceId.isEmpty() ? "" : ". TraceId: " + traceId;
            throw new TracingException(trace, e);
        }
        finally
        {
            INSTANCE.stopSpan(child);
        }
    }


}

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

package org.finos.legend.depot.core.services.tracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.core.services.api.tracing.TracingException;
import org.finos.legend.depot.core.services.api.tracing.VoidAuthenticationProvider;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.engine.shared.core.operational.prometheus.TracingExports;
import org.finos.legend.opentracing.AuthenticationProvider;
import org.finos.legend.opentracing.JerseyClientSender;
import org.finos.legend.opentracing.OpenTracing;
import org.slf4j.Logger;
import zipkin2.reporter.InMemoryReporterMetrics;

import javax.inject.Singleton;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;

@Singleton
public final class TracerFactory
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TracerFactory.class);
    private static final String DEFAULT_SERVICE_NAME = "legend-depot";
    private static TracerFactory INSTANCE;
    private final Tracer tracer;

    private TracerFactory(Tracer tracer)
    {
        this.tracer = tracer;
    }

    public static TracerFactory get()
    {
        if (INSTANCE == null)
        {
            configure(null);
        }
        return INSTANCE;
    }

    public static synchronized Tracer getTracer()
    {
        return get().tracer;
    }

    public static TracerFactory configure(OpenTracingConfiguration openTracingConfiguration)
    {
        if (openTracingConfiguration != null && openTracingConfiguration.isEnabled())
        {
            if (openTracingConfiguration.getOpenTracingUri() == null || openTracingConfiguration.getOpenTracingUri().isEmpty())
            {
                throw new TracingException("Invalid uri, openTracingUri cannot be empty");
            }
            AuthenticationProvider authenticationProvider = openTracingConfiguration.getAuthenticationProvider() != null ? openTracingConfiguration.getAuthenticationProvider() : new VoidAuthenticationProvider();
            String serviceName = openTracingConfiguration.getServiceName() != null && !openTracingConfiguration.getServiceName().isEmpty() ?
                    openTracingConfiguration.getServiceName() : DEFAULT_SERVICE_NAME;
            try (JerseyClientSender sender = new JerseyClientSender(new URI(openTracingConfiguration.getOpenTracingUri()), authenticationProvider);)
            {

                Tracer tracer = OpenTracing.create(sender, serviceName, null, getMemoryMetricsReporter());
                LogManager.getLogManager().getLogger("").setLevel(Level.FINE);
                GlobalTracer.registerIfAbsent(tracer);
                INSTANCE = new TracerFactory(tracer);
            }
            catch (Exception e)
            {
                LOGGER.error("tracing configuration failed",e);
                throw new TracingException("Invalid openTracingUri provided", e);
            }
        }
        else
        {
            INSTANCE = new TracerFactory(NoopTracerFactory.create());
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

    public void addTags(Map<String, String> tags,Span span)
    {
        if (tags != null & !tags.keySet().isEmpty() && span != null)
        {
            tags.keySet().forEach(key -> new StringTag(key).set(span,tags.get(key)));
        }
    }

    public void log(String value)
    {
        if (value != null)
        {
            Span currentSpan = GlobalTracer.get().activeSpan();
            if (currentSpan != null)
            {
                currentSpan.log(value);
            }
        }
    }

    public <T> T executeWithTrace(String label, Supplier<T> supplier)
    {
       return (T) executeWithTrace(label,supplier, Collections.EMPTY_MAP);
    }

    public <T> T executeWithTrace(String label, Supplier<T> supplier, Map<String,String> tags)
    {
        Span child = null;
        try
        {
            child = INSTANCE.startSpan(label);
            addTags(tags,child);
            return supplier.get();
        }
        catch (Exception e)
        {
            Span currentSpan = GlobalTracer.get().activeSpan();
            String message;
            if (currentSpan != null)
            {
                Tags.ERROR.set(currentSpan, true);
                Map<String, String> map = new HashMap<>();
                map.put(Fields.EVENT, "error");
                map.put(Fields.ERROR_OBJECT, e.toString());
                map.put(Fields.MESSAGE, e.getMessage());
                currentSpan.log(map);
                String traceId = currentSpan.context().toTraceId();
                message = String.format("[%s] failed with error:[%s] (TraceId: [%s])", label, e.getMessage(), traceId);
                LOGGER.error(message);
            }
            else
            {
                message = String.format("[%s] failed with error:[%s])", label, e.getMessage());
                LOGGER.error(message);
                LOGGER.error("{} ( TraceId: current span not found)",message);
            }
            throw new TracingException(message, e);
        }
        finally
        {
            INSTANCE.stopSpan(child);
        }
    }

}

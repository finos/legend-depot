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

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.opentracing.BraveTracer;
import brave.propagation.CurrentTraceContext;
import brave.sampler.Sampler;
import io.opentracing.Tracer;
import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.TracerProvider;
import org.finos.legend.engine.shared.core.operational.prometheus.TracingExports;
import zipkin2.codec.Encoding;
import zipkin2.reporter.InMemoryReporterMetrics;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

import static org.finos.legend.depot.core.services.tracing.TracerFactory.DEFAULT_SERVICE_NAME;

public class DefaultTracerProvider implements TracerProvider
{
    private static final int TRACE_MAX_BYTES = 5 * 1024 * 1024;

    @Override
    public Tracer create(OpenTracingConfiguration configuration)
    {
        if (configuration.getOpenTracingUri() == null || configuration.getOpenTracingUri().isEmpty())
        {
            throw new IllegalArgumentException("Invalid uri, openTracingUri cannot be empty");
        }
        String serviceName = configuration.getServiceName() != null && !configuration.getServiceName().isEmpty() ?
                configuration.getServiceName() : DEFAULT_SERVICE_NAME;
        try
        {
            OkHttpSender sender = OkHttpSender.newBuilder()
                    .encoding(Encoding.JSON)
                    .messageMaxBytes(TRACE_MAX_BYTES)
                    .endpoint(configuration.getOpenTracingUri())
                    .build();

            SpanHandler spanHandler = AsyncZipkinSpanHandler.newBuilder(sender).metrics(getMemoryMetricsReporter()).build();
            Tracer tracer = BraveTracer.create(Tracing.newBuilder().localServiceName(serviceName)
                    .sampler(Sampler.ALWAYS_SAMPLE)
                    .currentTraceContext(CurrentTraceContext.Default.newBuilder().build())
                    .addSpanHandler(spanHandler)
                    .build());
            return tracer;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Invalid openTracingUri provided", e);
        }

    }

    protected InMemoryReporterMetrics getMemoryMetricsReporter()
    {
        CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        InMemoryReporterMetrics tracingMetrics = new InMemoryReporterMetrics();
        collectorRegistry.register(new TracingExports(tracingMetrics));
        return tracingMetrics;
    }
}

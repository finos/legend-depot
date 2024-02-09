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
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.TracerProvider;
import org.slf4j.Logger;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
public final class TracerFactory
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TracerFactory.class);
    static final String DEFAULT_SERVICE_NAME = "legend-depot";
    private static final String ERROR_TAG = "error";
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

    public static Tracer getTracer()
    {
        return get().tracer;
    }

    public static TracerFactory configure(OpenTracingConfiguration openTracingConfiguration)
    {
        if (openTracingConfiguration != null && openTracingConfiguration.isEnabled())
        {
            TracerProvider tracerProvider = openTracingConfiguration.getTracerProvider() != null ? openTracingConfiguration.getTracerProvider() : new DefaultTracerProvider();
            Tracer tracer = tracerProvider.create(openTracingConfiguration);
            GlobalTracer.registerIfAbsent(tracer);
            INSTANCE = new TracerFactory(tracer);
        }
        else
        {
            INSTANCE = new TracerFactory(NoopTracerFactory.create());
        }
        return INSTANCE;
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

    public void addTags(Map<String, ?> tags)
    {
        addTags(tags, GlobalTracer.get().activeSpan());
    }

    public void addTags(Map<String, ?> tags,Span span)
    {
        if (tags != null & !tags.keySet().isEmpty() && span != null)
        {
            tags.keySet().forEach(key -> new StringTag(key).set(span,String.valueOf(tags.get(key))));
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
                map.put(Fields.EVENT, ERROR_TAG);
                map.put(Fields.ERROR_OBJECT, e.toString());
                map.put(Fields.MESSAGE, e.getMessage());
                currentSpan.log(map);
                String traceId = currentSpan.context().toTraceId();
                message = String.format("[%s] - TraceId [%s]: %s", label, traceId,e.getMessage());
            }
            else
            {
                message = String.format("[%s]: %s", label,e.getMessage());
            }
            LOGGER.error(message);
            throw new RuntimeException(message,e);
        }
        finally
        {
            INSTANCE.stopSpan(child);
        }
    }

}

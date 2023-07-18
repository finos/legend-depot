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

package org.finos.legend.depot.tracing.resources;

import io.swagger.annotations.ApiOperation;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Supplier;

public class BaseResource
{
    static final ConcurrentHashMap<String,String> resourceMetricsRegistration = ConcurrentHashMap.newMap();

    public BaseResource()
    {
        registerResourceApisMetrics(this);
    }

    private Logger getLogger()
    {
        return LoggerFactory.getLogger(this.getClass());
    }

    private String buildLoggingErrorMessage(Throwable t, String description, long durationNanos)
    {
        StringBuilder builder = (new StringBuilder(description.length() + 29)).append("Error ").append(description).append(" (");
        builder.append(durationNanos);
        builder.append(" s)");
        String message = t.getMessage();
        if (message != null)
        {
            builder.append(": ").append(message);
        }

        return builder.toString();
    }

    private <T> T handleWithLogging(String resourceAPIMetricName, String logsLabel, Supplier<T> supplier)
    {
        Logger logger = this.getLogger();
        boolean isInfoLogging = logger.isInfoEnabled();
        String sanitizedDescription = isInfoLogging ? logsLabel : null;
        long start = System.currentTimeMillis();
        if (isInfoLogging)
        {
            logger.info("Starting {}", sanitizedDescription);
        }
        long duration;
        try
        {
            T result = supplier.get();
            long end = System.currentTimeMillis();
            PrometheusMetricsFactory.getInstance().observe(resourceAPIMetricName, start, end);
            if (isInfoLogging)
            {
                duration = end - start;
                String builder = "Finished " + sanitizedDescription + " (" + duration + "ms)";
                logger.info(builder);
            }
            return result;
        }
        catch (Exception var15)
        {
            PrometheusMetricsFactory.getInstance().incrementErrorCount(resourceAPIMetricName);
            if (logger.isErrorEnabled())
            {
                duration = System.nanoTime() - start;
                logger.error(this.buildLoggingErrorMessage(var15, logsLabel, duration), var15);
            }
            throw var15;
        }
    }

    protected <T> T handle(String resourceAPIMetricName, String label, Supplier<T> supplier)
    {
        return TracerFactory.get().executeWithTrace(label, () -> handleWithLogging(resourceAPIMetricName, label, supplier));
    }

    protected <T> Response handle(String resourceAPIMetricName, String label, Supplier<T> supplier, Request request, Supplier<EntityTag> etagSupplier)
    {
        Logger logger = this.getLogger();
        EntityTag serverTag;
        try
        {
            serverTag = etagSupplier.get();
        }
        catch (Exception e)
        {
            logger.error("Etag generation failed ", e);
            serverTag = null;
        }
        if (serverTag != null && request != null && request.evaluatePreconditions(serverTag) != null)
        {
            return Response.noContent().status(Response.Status.NOT_MODIFIED).build();
        }
        T output = handle(resourceAPIMetricName, label, supplier);
        Response.ResponseBuilder responseBuilder = Response.ok(output);
        if (serverTag != null)
        {
            responseBuilder.tag(serverTag);
        }
        return responseBuilder.build();
    }

    protected <T> T handle(String label, Supplier<T> supplier)
    {
        return handle(label, label, supplier);
    }

    private void registerResourceApisMetrics(BaseResource baseResource)
    {
        resourceMetricsRegistration.getIfAbsentPutWithKey(baseResource.getClass().getCanonicalName(), resourceName ->
        {
            Arrays.stream(baseResource.getClass().getMethods()).forEach(m ->
            {
                if (m.isAnnotationPresent(ApiOperation.class))
                {
                    ApiOperation val = m.getAnnotation(ApiOperation.class);
                    String metricName = (val.nickname() != null && !val.nickname().isEmpty() ? val.nickname() : val.value());
                    PrometheusMetricsFactory.getInstance().registerSummary(metricName, metricName);
                }
            });
            return resourceName;
        });
    }

    protected <T> Response handle(String label, Supplier<T> supplier, Request request, Supplier<EntityTag> entityTagSupplier)
    {
        return handle(label, label, supplier, request, entityTagSupplier);
    }

    protected EntityTag calculateEtag(List<String> params)
    {
        StringBuilder etagBuilder = new StringBuilder();
        params.forEach(param -> etagBuilder.append(param));
        return new EntityTag(etagBuilder.toString());
    }
}

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

package org.finos.legend.depot.core.services.tracing.resources;

import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import java.util.function.Supplier;

public class TracingResource
{
    private static final String ERROR_ = "Error ";
    private static final String OPEN_PAR = " (";
    private static final String END_PAR = " s)";
    private static final String SEPARATOR = ": ";
    private static final int PADDING = 29;
    private static final String FINISHED = "Finished ";
    private static final String MS = "ms)";

    public TracingResource()
    {
    }

    private Logger getLogger()
    {
        return LoggerFactory.getLogger(this.getClass());
    }

    private String buildLoggingErrorMessage(Throwable t, String description, long durationMillis)
    {
        StringBuilder builder = (new StringBuilder(description.length() + PADDING)).append(ERROR_).append(description).append(OPEN_PAR);
        builder.append(durationMillis);
        builder.append(END_PAR);
        String message = t.getMessage();
        if (message != null)
        {
            builder.append(SEPARATOR).append(message);
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
                logger.info(FINISHED + sanitizedDescription + OPEN_PAR + duration + MS);
            }
            return result;
        }
        catch (Exception e)
        {
            PrometheusMetricsFactory.getInstance().incrementErrorCount(resourceAPIMetricName);
            if (logger.isErrorEnabled())
            {
                duration = System.currentTimeMillis() - start;
                logger.error(this.buildLoggingErrorMessage(e, logsLabel, duration), e);
            }
            throw e;
        }
    }

    protected <T> T handle(String resourceAPIMetricName, String label, Supplier<T> supplier)
    {
        return TracerFactory.get().executeWithTrace(label, () -> handleWithLogging(resourceAPIMetricName, label, supplier));
    }

    protected <T> Response handle(String resourceAPIMetricName, String label, Supplier<T> supplier, Request request, Supplier<String> etagSupplier)
    {
        String eTagStringValue = etagSupplier.get();
        EntityTag serverTag =  eTagStringValue != null ? new EntityTag(eTagStringValue) : null;
        if (serverTag != null && request != null && request.evaluatePreconditions(serverTag) != null)
        {
            return Response.noContent().status(Response.Status.NOT_MODIFIED).build();
        }
        CacheControl cc = new CacheControl();
        Response.ResponseBuilder responseBuilder = Response.ok(handle(resourceAPIMetricName, label, supplier));
        if (serverTag != null)
        {
            responseBuilder.tag(serverTag);
            cc.setMustRevalidate(true);
        }
        else
        {
            cc.setNoCache(true);
            cc.setNoStore(true);
        }

        responseBuilder.cacheControl(cc);
        return responseBuilder.build();
    }

    protected <T> Response handleResponse(String label, Supplier<T> supplier)
    {
        return handleResponse(label, label, supplier);
    }

    protected <T> Response handleResponse(String resourceAPIMetricName,String label, Supplier<T> supplier)
    {
        return handle(resourceAPIMetricName, label, supplier, null, () -> null);
    }

    protected <T> T handle(String label, Supplier<T> supplier)
    {
        return handle(label, label, supplier);
    }

    protected <T> Response handle(String label, Supplier<T> supplier, Request request, Supplier<String> entityTagSupplier)
    {
        return handle(label, label, supplier, request, entityTagSupplier);
    }
}

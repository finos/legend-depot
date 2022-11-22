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

import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.util.function.Supplier;

public class BaseResource
{
    public BaseResource()
    {
        PrometheusMetricsFactory.getInstance().registerResourceApis(this);
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
        long start = System.nanoTime();
        if (isInfoLogging)
        {
            logger.info("Starting {}", sanitizedDescription);
        }
        long duration;
        try
        {
            T result = supplier.get();
            long end = System.nanoTime();
            PrometheusMetricsFactory.getInstance().observe(resourceAPIMetricName, start, end);
            if (isInfoLogging)
            {
                duration = end - start;
                String builder = "Finished " + sanitizedDescription + " (" + duration + "s)";
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
        try
        {
            return TracerFactory.get().executeWithTrace(label, () -> handleWithLogging(resourceAPIMetricName, label, supplier));
        }
        catch (Exception e)
        {
            throw new WebApplicationException(e.getMessage(), e);
        }
    }

    protected <T> T handle(String label, Supplier<T> supplier)
    {
        return handle(label, label, supplier);
    }
}

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

package org.finos.legend.depot.core.http.filters;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.filter.FilterFactory;

import java.util.concurrent.atomic.AtomicInteger;

@JsonTypeName("healthcheck-filter-factory")
public class HealthcheckFilterFactory implements FilterFactory<IAccessEvent>
{
    @Override
    public Filter<IAccessEvent> build()
    {
        final AtomicInteger healthCheckLogCount = new AtomicInteger();
        return new Filter<IAccessEvent>()
        {
            @Override
            public FilterReply decide(IAccessEvent event)
            {
                if (event.getRequestURI().endsWith("/admin/healthcheck")
                        && healthCheckLogCount.get() > 10)
                {
                    return FilterReply.DENY;
                }
                else
                {
                    healthCheckLogCount.incrementAndGet();
                    return FilterReply.NEUTRAL;
                }
            }
        };
    }
}

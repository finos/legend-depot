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

package org.finos.legend.depot.store.metrics;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;

public class MetricsModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(QueryMetricsHandler.class);
        expose(QueryMetricsHandler.class);
    }

    @Provides
    @Singleton
    @Named("consolidate-query-metrics")
    boolean scheduleMetricsPersistence(SchedulesFactory schedulesFactory, QueryMetricsHandler queryMetrics)
    {
        schedulesFactory.registerSingleInstance("consolidate-query-metrics", SchedulesFactory.MINUTE, 12 * SchedulesFactory.HOUR, () ->
        {
            queryMetrics.consolidateMetrics();
            return true;
        });
        return true;
    }
}

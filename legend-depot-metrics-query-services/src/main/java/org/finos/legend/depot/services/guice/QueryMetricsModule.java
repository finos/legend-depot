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

package org.finos.legend.depot.services.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.metrics.query.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.services.metrics.query.QueryMetricsServiceImpl;

public class QueryMetricsModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(QueryMetricsService.class).to(QueryMetricsServiceImpl.class);
        expose(QueryMetricsService.class);
        expose(QueryMetricsRegistry.class).annotatedWith(Names.named("queryMetricsRegistry"));
    }

    @Provides
    @Singleton
    @Named("queryMetricsRegistry")
    QueryMetricsRegistry getQueryMetricsRegistry()
    {
        return new InMemoryQueryMetricsRegistry();
    }

}

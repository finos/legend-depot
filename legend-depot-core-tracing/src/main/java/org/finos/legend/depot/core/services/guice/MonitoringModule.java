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

package org.finos.legend.depot.core.services.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;

public class MonitoringModule extends PrivateModule
{

    @Override
    protected void configure()
    {
        expose(TracerFactory.class);
        expose(PrometheusMetricsHandler.class);
    }

    @Provides
    @Singleton
    public TracerFactory initTracerFactory(OpenTracingConfiguration openTracingConfiguration)
    {
        return TracerFactory.configure(openTracingConfiguration);
    }


    @Provides
    @Singleton
    public PrometheusMetricsHandler initialisePrometheusMetrics(PrometheusConfiguration configuration)
    {
        return PrometheusMetricsFactory.configure(configuration);
    }
}

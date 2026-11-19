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

package org.finos.legend.depot.core.services.metrics;

import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.VoidPrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;

import jakarta.inject.Singleton;

@Singleton
public final class PrometheusMetricsFactory
{
    private static PrometheusMetricsHandler INSTANCE;

    public static PrometheusMetricsHandler getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = configure(null);
        }
        return INSTANCE;
    }

    public static PrometheusMetricsHandler configure(PrometheusConfiguration configuration)
    {
        if (configuration != null && configuration.isEnabled() && configuration.getMetricsHandler() != null)
        {
            INSTANCE = configuration.getMetricsHandler();
        }
        else
        {
            INSTANCE = new VoidPrometheusMetricsHandler();
        }
        return INSTANCE;
    }

}

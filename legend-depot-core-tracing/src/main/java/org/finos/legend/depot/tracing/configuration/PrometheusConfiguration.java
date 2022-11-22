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

package org.finos.legend.depot.tracing.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;

public class PrometheusConfiguration
{
    @JsonProperty
    private boolean enabled = false;

    @JsonProperty
    private PrometheusMetricsHandler metricsHandler;

    public PrometheusConfiguration()
    {
    }

    public PrometheusConfiguration(boolean enabled, PrometheusMetricsHandler metricsHandler)
    {
        this.enabled = enabled;
        this.metricsHandler = metricsHandler;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public PrometheusMetricsHandler getMetricsHandler()
    {
        return metricsHandler;
    }

    public void setMetricsHandler(PrometheusMetricsHandler metricsHandler)
    {
        this.metricsHandler = metricsHandler;
    }
}

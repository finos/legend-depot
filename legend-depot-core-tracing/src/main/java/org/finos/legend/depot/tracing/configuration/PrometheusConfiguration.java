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

public class PrometheusConfiguration
{
    @JsonProperty
    private boolean enabled = false;

    @JsonProperty
    private String prefix;

    @JsonProperty
    private PrometheusMetricsHandler prometheusMetricsHandler;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public PrometheusMetricsHandler getPrometheusMetricsHandler()
    {
        return prometheusMetricsHandler;
    }

    public void setPrometheusMetricsHandler(PrometheusMetricsHandler prometheusMetricsHandler)
    {
        this.prometheusMetricsHandler = prometheusMetricsHandler;
    }
}

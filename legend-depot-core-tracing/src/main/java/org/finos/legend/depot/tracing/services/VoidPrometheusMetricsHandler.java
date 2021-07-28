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

package org.finos.legend.depot.tracing.services;

import org.finos.legend.depot.tracing.configuration.PrometheusMetricsHandler;

public class VoidPrometheusMetricsHandler implements PrometheusMetricsHandler
{
    @Override
    public void observe(String metricsLabel, long start, long end)
    {
        //do nothing implementation
    }

    @Override
    public void observeError(String metricsLabel)
    {
        //do nothing implementation
    }
}

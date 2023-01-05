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

package org.finos.legend.depot.tracing.services.prometheus;

import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;
import org.finos.legend.depot.tracing.resources.BaseResource;


public class VoidPrometheusMetricsHandler implements PrometheusMetricsHandler
{
    @Override
    public void registerResourceApis(BaseResource baseResource)
    {
    }

    @Override
    public void incrementCount(String counter)
    {
    }

    @Override
    public void incrementErrorCount(String counter)
    {
    }

    @Override
    public void registerCounter(String counter, String helpMessage)
    {
    }

    @Override
    public void observe(String uriMetricName, long start, long end)
    {
    }

    @Override
    public void registerSummary(String summaryName, String helpMessage)
    {
    }

    @Override
    public void setGauge(String name, double value)
    {
    }

    @Override
    public void registerGauge(String name, String help)
    {
    }

    @Override
    public void increaseGauge(String name, int value)
    {
    }

    @Override
    public void registerHistogram(String name, String helpMessage)
    {
    }

    @Override
    public void observeHistogram(String name, long start, long end)
    {
    }
}

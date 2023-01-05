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

package org.finos.legend.depot.tracing.api;

import org.finos.legend.depot.tracing.resources.BaseResource;

public interface PrometheusMetricsHandler
{
    void registerSummary(String summaryName,String helpMessage);

    void registerHistogram(String name,String helpMessage);

    void observe(String summaryName, long start, long end);

    void observeHistogram(String name, long start, long end);

    void registerCounter(String counter, String helpMessage);

    void incrementCount(String counter);

    void incrementErrorCount(String counter);

    void setGauge(String name, double value);

    void registerGauge(String name, String help);

    void increaseGauge(String name, int value);

    void registerResourceApis(BaseResource baseResource);

}

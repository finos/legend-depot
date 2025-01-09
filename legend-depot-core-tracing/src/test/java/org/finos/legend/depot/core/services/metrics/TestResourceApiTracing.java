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

import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestResourceApiTracing
{
    DepotPrometheusMetricsHandler prometheusMetrics = (DepotPrometheusMetricsHandler) PrometheusMetricsFactory.configure(new PrometheusConfiguration(true,new DepotPrometheusMetricsHandler("test")));

    @Test
    public void testMetricRegistrationForResource()
    {
        prometheusMetrics.registerResourceSummaries(TestTracingResource.class);
        Assertions.assertEquals(1,prometheusMetrics.allSummaries.keySet().size());

    }
}

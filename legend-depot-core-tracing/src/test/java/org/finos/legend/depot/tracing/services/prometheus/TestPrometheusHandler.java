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

import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.tracing.configuration.PrometheusConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPrometheusHandler
{
    DepotPrometheusMetricsHandler prometheusMetrics = (DepotPrometheusMetricsHandler) PrometheusMetricsFactory.configure(new PrometheusConfiguration(true,new DepotPrometheusMetricsHandler("test")));

    @Before
    public void setUp()
    {
        CollectorRegistry.defaultRegistry.clear();
    }

    @Test
    public void canConfigureHandler()
    {
       Assert.assertNotNull(PrometheusMetricsFactory.getInstance());
    }

    @Test
    public void canCreateCounterAndIncrement()
    {
        prometheusMetrics.registerCounter("test1","help");
        Assert.assertEquals(1, prometheusMetrics.allCounters.size());
        Assert.assertEquals(1, prometheusMetrics.allErrorCounters.size());

        Assert.assertEquals(0.0D, prometheusMetrics.allCounters.get("test_test1").get(),0.0D);
        Assert.assertEquals(0.0D, prometheusMetrics.allErrorCounters.get("test_test1_errors").get(),0.0);

        prometheusMetrics.incrementCount("test1");
        Assert.assertEquals(1.0D, prometheusMetrics.allCounters.get("test_test1").get(),0.0);
        Assert.assertEquals(0.0D, prometheusMetrics.allErrorCounters.get("test_test1_errors").get(),0.0);

        prometheusMetrics.incrementErrorCount("test1");
        Assert.assertEquals(1.0D, prometheusMetrics.allCounters.get("test_test1").get(),0.0);
        Assert.assertEquals(1.0D, prometheusMetrics.allErrorCounters.get("test_test1_errors").get(),0.0);
    }

    @Test
    public void cannotRegisterSameCounterTwice()
    {
        prometheusMetrics.registerCounter("test2","help");
        prometheusMetrics.incrementCount("test2");
        Assert.assertEquals(1, prometheusMetrics.allCounters.size());
        Assert.assertEquals(1, prometheusMetrics.allErrorCounters.size());
        prometheusMetrics.registerCounter("test2","help");
        Assert.assertEquals(1, prometheusMetrics.allCounters.size());
        Assert.assertEquals(1, prometheusMetrics.allErrorCounters.size());
        prometheusMetrics.incrementCount("test2");
        Assert.assertEquals(2.0D, prometheusMetrics.allCounters.get("test_test2").get(),0.0);
        Assert.assertEquals(0.0D, prometheusMetrics.allErrorCounters.get("test_test2_errors").get(),0.0);

    }

    @Test
    public void incrementUnknownMetric()
    {
        prometheusMetrics.incrementCount("test2");
        Assert.assertEquals(1, prometheusMetrics.allCounters.size());
        Assert.assertEquals(0, prometheusMetrics.allErrorCounters.size());

        prometheusMetrics.incrementCount("test2");
        Assert.assertEquals(2.0D, prometheusMetrics.allCounters.get("test_test2").get(),0.0);
        Assert.assertNull(prometheusMetrics.allErrorCounters.get("test_test2_errors"));

        prometheusMetrics.incrementErrorCount("test2");
        Assert.assertEquals(1.0D, prometheusMetrics.allErrorCounters.get("test_test2_errors").get(),0.0);
    }

    @Test
    public void testMetricRegistrationForResource()
    {
         new TestBaseResource();
         Assert.assertEquals(1,prometheusMetrics.allSummaries.keySet().size());
         Assert.assertEquals(1, prometheusMetrics.registeredResources.size());
         new TestBaseResource();
         Assert.assertEquals(1,prometheusMetrics.allSummaries.keySet().size());
         Assert.assertEquals(1, prometheusMetrics.registeredResources.size());
    }

    @Test
    public void testSummaryRegistration()
    {
        prometheusMetrics.registerSummary("test","test");
        Assert.assertEquals(1,prometheusMetrics.allSummaries.keySet().size());

        prometheusMetrics.registerSummary("test","test");
        Assert.assertEquals(1,prometheusMetrics.allSummaries.keySet().size());
    }
}

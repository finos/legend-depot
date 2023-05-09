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

package org.finos.legend.depot.store.metrics;

import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.store.mongo.admin.metrics.QueryMetricsMongo;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TestMetricsServices extends TestStoreMongo
{

    private QueryMetricsMongo metricsStore = new QueryMetricsMongo(mongoProvider);
    private QueryMetricsHandler metricsHandler = new QueryMetricsHandler(metricsStore);

    @Before
    public void setup() throws InterruptedException
    {
        setUpProjectsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/projects.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/versioned-entities.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/revision-entities.json"));

        metricsStore.getCollection().drop();
        metricsStore.record("group1", "art1", "2.2.0");
        metricsStore.record("group1", "art1", "2.2.0");
        metricsStore.record("group1", "art1", "2.2.0");
        TimeUnit.SECONDS.sleep(1);
        metricsStore.record("group1", "art1", "1.0.0");
    }

    @After
    public void tearDown()
    {
        metricsStore.getCollection().drop();
    }


    @Test
    public void canGetMetricsSummary()
    {

        Optional<VersionQueryMetric> metrics = metricsHandler.getSummary("group1", "art1", "2.2.0");
        Assert.assertTrue(metrics.isPresent());
        Assert.assertEquals("2.2.0", metrics.get().getVersionId());
    }


    @Test
    public void canGetMostRecentlyQueriedMetrics()
    {
        Assert.assertEquals(4, metricsStore.getAllStoredEntities().size());
        List<VersionQueryMetric> metrics = metricsHandler.getSummaryByProjectVersion();
        Assert.assertEquals(2, metrics.size());
        Assert.assertEquals("1.0.0", metrics.get(0).getVersionId());
        Assert.assertEquals("2.2.0", metrics.get(1).getVersionId());
    }


}

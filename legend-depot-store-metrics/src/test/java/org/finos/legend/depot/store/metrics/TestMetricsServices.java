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

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.store.metrics.services.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.store.mongo.QueryMetricsMongo;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.DatesHandler.toDate;

public class TestMetricsServices extends TestStoreMongo
{

    private QueryMetricsMongo metricsStore = new QueryMetricsMongo(mongoProvider);
    private QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private QueryMetricsHandler metricsHandler = new QueryMetricsHandler(metricsStore, metricsRegistry);

    @Before
    public void setup() throws InterruptedException
    {
        setUpProjectsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/projects.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/versioned-entities.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/revision-entities.json"));

        metricsStore.getCollection().drop();
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "2.2.0"));
        TimeUnit.SECONDS.sleep(1);
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "1.0.0"));
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
    public void canFindMetricsUsingProjectCoordinates()
    {
        List<VersionQueryMetric> metrics = metricsHandler.findMetricsForProjectCoordinates("group1", "art1");
        Assert.assertEquals(4, metrics.size());

        Set<String> versionsUsed = metrics.stream().map(metric -> metric.getVersionId()).collect(Collectors.toSet());

        Assert.assertEquals(2, versionsUsed.size());

        Assert.assertTrue(versionsUsed.contains("1.0.0"));
        Assert.assertTrue(versionsUsed.contains("2.2.0"));
        Assert.assertFalse(versionsUsed.contains("master-SNAPSHOT"));
    }

    @Test
    public void canGetAllStoredCoordinates()
    {
        List<ProjectVersion> projectVersions = metricsStore.getAllStoredEntitiesCoordinates();
        Assert.assertEquals(2, projectVersions.size());
        Assert.assertEquals(Arrays.asList(new ProjectVersion("group1", "art1", "1.0.0"),new ProjectVersion("group1", "art1", "2.2.0")), projectVersions);
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

    @Test
    public void canConsolidateQueryMetrics() throws InterruptedException
    {
        metricsRegistry.record("group1", "art1", "3.0.0");
        Thread.sleep(10);
        metricsRegistry.record("group1", "art1", "3.0.0");
        metricsHandler.persistMetrics();
        Assert.assertEquals(6, metricsStore.getAllStoredEntities().size());
        List<VersionQueryMetric> summary = metricsHandler.getSummaryByProjectVersion();
        metricsHandler.consolidateMetrics();
        List<VersionQueryMetric> metrics = metricsStore.getAllStoredEntities();
        Assert.assertEquals(3, metrics.size());
        Assert.assertEquals("2.2.0", metrics.get(0).getVersionId());
        Assert.assertEquals("2.2.0", summary.get(1).getVersionId());
        Assert.assertEquals(summary.get(1).getLastQueryTime(), metrics.get(0).getLastQueryTime());
        Assert.assertEquals("1.0.0", metrics.get(1).getVersionId());
        Assert.assertEquals("1.0.0", summary.get(0).getVersionId());
        Assert.assertEquals(summary.get(0).getLastQueryTime(), metrics.get(1).getLastQueryTime());
        Assert.assertEquals("3.0.0", metrics.get(2).getVersionId());
        Assert.assertEquals("3.0.0", summary.get(2).getVersionId());
        Assert.assertEquals(summary.get(2).getLastQueryTime(), metrics.get(2).getLastQueryTime());
    }

    @Test
    public void canConsolidateMetricsAtDiffDates()
    {
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "3.0.0", toDate(LocalDateTime.parse("2023-03-22T14:02:49", DateTimeFormatter.ISO_DATE_TIME))));
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "3.0.0", toDate(LocalDateTime.parse("2023-03-21T14:02:49", DateTimeFormatter.ISO_DATE_TIME))));

        Assert.assertEquals(6, metricsStore.getAllStoredEntities().size());
        metricsHandler.consolidateMetrics();
        List<VersionQueryMetric> metrics = metricsStore.getAllStoredEntities();
        Assert.assertEquals(3, metrics.size());
        Assert.assertEquals("3.0.0", metrics.get(2).getVersionId());
        Assert.assertEquals(toDate(LocalDateTime.parse("2023-03-22T14:02:49", DateTimeFormatter.ISO_DATE_TIME)), metrics.get(2).getLastQueryTime());
    }

    @Test
    public void canPersistMetrics() throws InterruptedException
    {
        Assert.assertEquals(4, metricsStore.getAllStoredEntities().size());
        metricsRegistry.record("group1", "art1", "3.0.0");
        Thread.sleep(10);
        metricsRegistry.record("group1", "art1", "3.0.0");
        metricsRegistry.record("group1", "art1", "2.0.0");
        metricsHandler.persistMetrics();
        Assert.assertEquals(7, metricsStore.getAllStoredEntities().size());
    }

    @Test
    public void testMetricsForThreadSafety() throws ExecutionException, InterruptedException
    {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Runnable recordTask1 = () -> metricsRegistry.record("examples.metadata", "test", "2.0.0");
        Runnable recordTask2 = () -> metricsRegistry.record("examples.metadata", "test", "2.0.0");
        Runnable recordTask3 = () -> metricsRegistry.record("examples.metadata", "test", "2.0.0");

        Callable<VersionQueryMetric> pollTask = () ->
        {
            Optional<VersionQueryMetric> versionQueryMetric = metricsRegistry.findFirst();
            while (versionQueryMetric.isPresent())
            {
                return versionQueryMetric.get();
            }
            return null;
        };

        executorService.submit(recordTask1);
        executorService.submit(recordTask2);
        executorService.submit(recordTask3);
        Future<VersionQueryMetric> returnedElement = executorService.submit(pollTask);
        Thread.sleep(10);
        Assert.assertEquals(returnedElement.get().getGroupId(), "examples.metadata");
        Assert.assertEquals(returnedElement.get().getArtifactId(), "test");
        Assert.assertEquals(returnedElement.get().getVersionId(), "2.0.0");
    }
}

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

package org.finos.legend.depot.services.metrics.query;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.finos.legend.depot.store.mongo.metrics.query.QueryMetricsMongo;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private QueryMetricsService metricsHandler = new QueryMetricsServiceImpl(metricsStore);


    @BeforeEach
    public void setup() throws InterruptedException
    {

        metricsStore.getCollection().drop();
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "2.2.0"));
        TimeUnit.SECONDS.sleep(1);
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "1.0.0"));
    }

    @AfterEach
    public void tearDown()
    {
        metricsStore.getCollection().drop();
    }


    @Test
    public void canGetMetricsSummary()
    {
        Optional<VersionQueryMetric> metrics = metricsHandler.getSummary("group1", "art1", "2.2.0");
        Assertions.assertTrue(metrics.isPresent());
        Assertions.assertEquals("2.2.0", metrics.get().getVersionId());
    }

    @Test
    public void canFindMetricsUsingProjectCoordinates()
    {
        List<VersionQueryMetric> metrics = metricsHandler.findMetricsForProjectCoordinates("group1", "art1");
        Assertions.assertEquals(4, metrics.size());

        Set<String> versionsUsed = metrics.stream().map(metric -> metric.getVersionId()).collect(Collectors.toSet());

        Assertions.assertEquals(2, versionsUsed.size());

        Assertions.assertTrue(versionsUsed.contains("1.0.0"));
        Assertions.assertTrue(versionsUsed.contains("2.2.0"));
        Assertions.assertFalse(versionsUsed.contains("master-SNAPSHOT"));
    }

    @Test
    public void canGetAllStoredCoordinates()
    {
        List<ProjectVersion> projectVersions = metricsStore.getAllStoredEntitiesCoordinates();
        Assertions.assertEquals(2, projectVersions.size());
        Assertions.assertEquals(Arrays.asList(new ProjectVersion("group1", "art1", "1.0.0"),new ProjectVersion("group1", "art1", "2.2.0")), projectVersions);
    }

    @Test
    public void canGetMostRecentlyQueriedMetrics()
    {
        Assertions.assertEquals(4, metricsStore.getAllStoredEntities().size());
        List<VersionQueryMetric> metrics = metricsHandler.getSummaryByProjectVersion();
        Assertions.assertEquals(2, metrics.size());
        Assertions.assertEquals("1.0.0", metrics.get(0).getVersionId());
        Assertions.assertEquals("2.2.0", metrics.get(1).getVersionId());
    }

    @Test
    public void canConsolidateQueryMetrics() throws InterruptedException
    {
        metricsRegistry.record("group1", "art1", "3.0.0");
        Thread.sleep(10);
        metricsRegistry.record("group1", "art1", "3.0.0");
        metricsHandler.persist(metricsRegistry);
        Assertions.assertEquals(6, metricsStore.getAllStoredEntities().size());
        List<VersionQueryMetric> summary = metricsHandler.getSummaryByProjectVersion();
        metricsHandler.consolidateMetrics();
        List<VersionQueryMetric> metrics = metricsStore.getAllStoredEntities();
        Assertions.assertEquals(3, metrics.size());
        Assertions.assertEquals("2.2.0", metrics.get(0).getVersionId());
        Assertions.assertEquals("2.2.0", summary.get(1).getVersionId());
        Assertions.assertEquals(summary.get(1).getLastQueryTime(), metrics.get(0).getLastQueryTime());
        Assertions.assertEquals("1.0.0", metrics.get(1).getVersionId());
        Assertions.assertEquals("1.0.0", summary.get(0).getVersionId());
        Assertions.assertEquals(summary.get(0).getLastQueryTime(), metrics.get(1).getLastQueryTime());
        Assertions.assertEquals("3.0.0", metrics.get(2).getVersionId());
        Assertions.assertEquals("3.0.0", summary.get(2).getVersionId());
        Assertions.assertEquals(summary.get(2).getLastQueryTime(), metrics.get(2).getLastQueryTime());
    }

    @Test
    public void canConsolidateMetricsAtDiffDates()
    {
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "3.0.0", toDate(LocalDateTime.parse("2023-03-22T14:02:49", DateTimeFormatter.ISO_DATE_TIME))));
        metricsStore.insert(new VersionQueryMetric("group1", "art1", "3.0.0", toDate(LocalDateTime.parse("2023-03-21T14:02:49", DateTimeFormatter.ISO_DATE_TIME))));

        Assertions.assertEquals(6, metricsStore.getAllStoredEntities().size());
        metricsHandler.consolidateMetrics();
        List<VersionQueryMetric> metrics = metricsStore.getAllStoredEntities();
        Assertions.assertEquals(3, metrics.size());
        Assertions.assertEquals("3.0.0", metrics.get(2).getVersionId());
        Assertions.assertEquals(toDate(LocalDateTime.parse("2023-03-22T14:02:49", DateTimeFormatter.ISO_DATE_TIME)), metrics.get(2).getLastQueryTime());
    }

    @Test
    public void canPersistMetrics() throws InterruptedException
    {
        Assertions.assertEquals(4, metricsStore.getAllStoredEntities().size());
        metricsRegistry.record("group1", "art1", "3.0.0");
        Thread.sleep(10);
        metricsRegistry.record("group1", "art1", "3.0.0");
        metricsRegistry.record("group1", "art1", "2.0.0");
        metricsHandler.persist(metricsRegistry);
        Assertions.assertEquals(7, metricsStore.getAllStoredEntities().size());
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
        Assertions.assertEquals(returnedElement.get().getGroupId(), "examples.metadata");
        Assertions.assertEquals(returnedElement.get().getArtifactId(), "test");
        Assertions.assertEquals(returnedElement.get().getVersionId(), "2.0.0");
    }
}

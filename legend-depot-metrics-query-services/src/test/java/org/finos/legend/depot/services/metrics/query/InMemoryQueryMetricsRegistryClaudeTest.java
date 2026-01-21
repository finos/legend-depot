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

import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryQueryMetricsRegistryClaudeTest


{
    private InMemoryQueryMetricsRegistry registry;

    @BeforeEach
    public void setUp()
  {
        registry = new InMemoryQueryMetricsRegistry();
    }

    @Test
    public void testConstructorInitializesEmptyRegistry()
  {
        InMemoryQueryMetricsRegistry newRegistry = new InMemoryQueryMetricsRegistry();
        Optional<VersionQueryMetric> result = newRegistry.findFirst();
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testRecordSingleMetric()
  {
        Date testDate = new Date();
        registry.record("groupId1", "artifactId1", "versionId1", testDate);

        Optional<VersionQueryMetric> result = registry.findFirst();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("groupId1", result.get().getGroupId());
        Assertions.assertEquals("artifactId1", result.get().getArtifactId());
        Assertions.assertEquals("versionId1", result.get().getVersionId());
        Assertions.assertEquals(testDate, result.get().getLastQueryTime());
    }

    @Test
    public void testRecordMultipleMetrics()
  {
        Date date1 = new Date(1000000);
        Date date2 = new Date(2000000);
        Date date3 = new Date(3000000);

        registry.record("group1", "artifact1", "version1", date1);
        registry.record("group2", "artifact2", "version2", date2);
        registry.record("group3", "artifact3", "version3", date3);

        Optional<VersionQueryMetric> result1 = registry.findFirst();
        Assertions.assertTrue(result1.isPresent());
        Assertions.assertEquals("group1", result1.get().getGroupId());

        Optional<VersionQueryMetric> result2 = registry.findFirst();
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals("group2", result2.get().getGroupId());

        Optional<VersionQueryMetric> result3 = registry.findFirst();
        Assertions.assertTrue(result3.isPresent());
        Assertions.assertEquals("group3", result3.get().getGroupId());
    }

    @Test
    public void testRecordWithNullValues()
  {
        registry.record(null, null, null, null);

        Optional<VersionQueryMetric> result = registry.findFirst();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getGroupId());
        Assertions.assertNull(result.get().getArtifactId());
        Assertions.assertNull(result.get().getVersionId());
        Assertions.assertNull(result.get().getLastQueryTime());
    }

    @Test
    public void testFindFirstOnEmptyRegistry()
  {
        Optional<VersionQueryMetric> result = registry.findFirst();
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testFindFirstRemovesElement()
  {
        Date testDate = new Date();
        registry.record("group1", "artifact1", "version1", testDate);
        registry.record("group2", "artifact2", "version2", testDate);

        Optional<VersionQueryMetric> result1 = registry.findFirst();
        Assertions.assertTrue(result1.isPresent());
        Assertions.assertEquals("group1", result1.get().getGroupId());

        Optional<VersionQueryMetric> result2 = registry.findFirst();
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals("group2", result2.get().getGroupId());

        Optional<VersionQueryMetric> result3 = registry.findFirst();
        Assertions.assertFalse(result3.isPresent());
    }

    @Test
    public void testFindFirstMultipleTimesOnEmptyRegistry()
  {
        Optional<VersionQueryMetric> result1 = registry.findFirst();
        Assertions.assertFalse(result1.isPresent());

        Optional<VersionQueryMetric> result2 = registry.findFirst();
        Assertions.assertFalse(result2.isPresent());
    }

    @Test
    public void testRecordAndFindFirstMaintainsFIFOOrder()
  {
        Date date1 = new Date(1000);
        Date date2 = new Date(2000);
        Date date3 = new Date(3000);

        registry.record("first", "a1", "v1", date1);
        registry.record("second", "a2", "v2", date2);
        registry.record("third", "a3", "v3", date3);

        Optional<VersionQueryMetric> result1 = registry.findFirst();
        Assertions.assertEquals("first", result1.get().getGroupId());

        Optional<VersionQueryMetric> result2 = registry.findFirst();
        Assertions.assertEquals("second", result2.get().getGroupId());

        Optional<VersionQueryMetric> result3 = registry.findFirst();
        Assertions.assertEquals("third", result3.get().getGroupId());
    }

    @Test
    public void testConcurrentRecordOperations() throws InterruptedException
    {
        int numberOfThreads = 10;
        int recordsPerThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++)
        {
            final int threadId = i;
            executorService.submit(() ->
            
        {
                try
                {
                    for (int j = 0; j < recordsPerThread; j++)
                    {
                        registry.record("group" + threadId, "artifact" + j, "version" + j, new Date());
                    }
                }
                finally
                {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        int count = 0;
        while (registry.findFirst().isPresent())
        {
            count++;
        }

        Assertions.assertEquals(numberOfThreads * recordsPerThread, count);
    }

    @Test
    public void testConcurrentFindFirstOperations() throws InterruptedException
    {
        int numberOfMetrics = 1000;
        for (int i = 0; i < numberOfMetrics; i++)
        {
            registry.record("group" + i, "artifact" + i, "version" + i, new Date());
        }

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger totalRetrieved = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++)
        {
            executorService.submit(() ->
            
        {
                try
                {
                    while (registry.findFirst().isPresent())
                    {
                        totalRetrieved.incrementAndGet();
                    }
                }
                finally
                {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        Assertions.assertEquals(numberOfMetrics, totalRetrieved.get());
        Assertions.assertFalse(registry.findFirst().isPresent());
    }

    @Test
    public void testConcurrentRecordAndFindFirst() throws InterruptedException
    {
        int numberOfProducers = 5;
        int numberOfConsumers = 5;
        int recordsPerProducer = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfProducers + numberOfConsumers);
        CountDownLatch producerLatch = new CountDownLatch(numberOfProducers);
        CountDownLatch consumerLatch = new CountDownLatch(numberOfConsumers);
        AtomicInteger consumed = new AtomicInteger(0);

        for (int i = 0; i < numberOfProducers; i++)
        {
            final int producerId = i;
            executorService.submit(() ->
            
        {
                try
                {
                    for (int j = 0; j < recordsPerProducer; j++)
                    {
                        registry.record("producer" + producerId, "artifact" + j, "version" + j, new Date());
                    }
                }
                finally
                {
                    producerLatch.countDown();
                }
            });
        }

        for (int i = 0; i < numberOfConsumers; i++)
        {
            executorService.submit(() ->
            
        {
                try
                {
                    producerLatch.await(10, TimeUnit.SECONDS);
                    Optional<VersionQueryMetric> metric;
                    while ((metric = registry.findFirst()).isPresent())
                    {
                        consumed.incrementAndGet();
                    }
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
                finally
                {
                    consumerLatch.countDown();
                }
            });
        }

        consumerLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        Assertions.assertEquals(numberOfProducers * recordsPerProducer, consumed.get());
        Assertions.assertFalse(registry.findFirst().isPresent());
    }

    @Test
    public void testRecordWithEmptyStrings()
  {
        Date testDate = new Date();
        registry.record("", "", "", testDate);

        Optional<VersionQueryMetric> result = registry.findFirst();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("", result.get().getGroupId());
        Assertions.assertEquals("", result.get().getArtifactId());
        Assertions.assertEquals("", result.get().getVersionId());
        Assertions.assertEquals(testDate, result.get().getLastQueryTime());
    }

    @Test
    public void testMultipleRecordsWithSameValues()
  {
        Date testDate = new Date();
        registry.record("sameGroup", "sameArtifact", "sameVersion", testDate);
        registry.record("sameGroup", "sameArtifact", "sameVersion", testDate);
        registry.record("sameGroup", "sameArtifact", "sameVersion", testDate);

        int count = 0;
        while (registry.findFirst().isPresent())
        {
            count++;
        }

        Assertions.assertEquals(3, count);
    }

    @Test
    public void testRecordAfterEmptyingRegistry()
  {
        Date date1 = new Date(1000);
        Date date2 = new Date(2000);

        registry.record("group1", "artifact1", "version1", date1);
        registry.findFirst();

        Assertions.assertFalse(registry.findFirst().isPresent());

        registry.record("group2", "artifact2", "version2", date2);
        Optional<VersionQueryMetric> result = registry.findFirst();
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("group2", result.get().getGroupId());
    }
}

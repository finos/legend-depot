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
import org.finos.legend.depot.store.api.metrics.query.QueryMetrics;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueryMetricsServiceImplClaude_getStaleMetricsTest
{
    private StaleMetricsQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new StaleMetricsQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testGetStaleMetricsReturnsEmptyWhenNoMetrics()
    {
        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetStaleMetricsWithOnlyRecentMetrics()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", now));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetStaleMetricsReturnsOldReleasedVersion()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        Date oldDate = new Date(fortyDaysAgo);
        Date recentDate = new Date();

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", recentDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testGetStaleMetricsReturnsOldSnapshotVersion()
    {
        long tenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
        Date oldDate = new Date(tenDaysAgo);
        Date recentDate = new Date();

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", recentDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testGetStaleMetricsWithDifferentTTLs()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        long tenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", new Date(fortyDaysAgo)));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", new Date(tenDaysAgo)));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testGetStaleMetricsSnapshotsUseShorterTTL()
    {
        long twentyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(20);
        Date oldDate = new Date(twentyDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("2.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testGetStaleMetricsWithZeroTTLForVersions()
    {
        long oneDayAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        Date oldDate = new Date(oneDayAgo);
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(0, 7);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetStaleMetricsWithZeroTTLForSnapshots()
    {
        long oneDayAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        Date oldDate = new Date(oneDayAgo);
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 0);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetStaleMetricsWithVeryLargeTTLs()
    {
        long oneYearAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365);
        Date oldDate = new Date(oneYearAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(1000, 1000);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetStaleMetricsWithMultipleOldMetrics()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        Date oldDate = new Date(fortyDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", oldDate));
        store.insert(new VersionQueryMetric("group3", "artifact3", "3.0.0", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testGetStaleMetricsWithMixedAges()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        long twentyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(20);
        long tenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
        Date recentDate = new Date();

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", new Date(fortyDaysAgo)));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", new Date(twentyDaysAgo)));
        store.insert(new VersionQueryMetric("group3", "artifact3", "3.0.0-SNAPSHOT", new Date(tenDaysAgo)));
        store.insert(new VersionQueryMetric("group4", "artifact4", "4.0.0", recentDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testGetStaleMetricsWithBoundaryConditions()
    {
        long exactlyThirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30);
        Date boundaryDate = new Date(exactlyThirtyDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", boundaryDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testGetStaleMetricsWithDifferentSnapshotFormats()
    {
        long tenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10);
        Date oldDate = new Date(tenDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "master-SNAPSHOT", oldDate));
        store.insert(new VersionQueryMetric("group3", "artifact3", "feature-SNAPSHOT", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(3, result.size());

        for (VersionQueryMetric metric : result)
        {
            Assertions.assertTrue(metric.getVersionId().endsWith("-SNAPSHOT"));
        }
    }

    @Test
    public void testGetStaleMetricsSnapshotCanMatchEitherTTL()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        Date veryOldDate = new Date(fortyDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", veryOldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 100);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetStaleMetricsWithNegativeTTL()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.getStaleMetrics(-1, -1);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetStaleMetricsPreservesMetricDetails()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        Date oldDate = new Date(fortyDaysAgo);

        VersionQueryMetric original = new VersionQueryMetric("org.example", "test-artifact", "5.4.3", oldDate);
        store.insert(original);

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 7);

        Assertions.assertEquals(1, result.size());
        VersionQueryMetric returned = result.get(0);

        Assertions.assertEquals("org.example", returned.getGroupId());
        Assertions.assertEquals("test-artifact", returned.getArtifactId());
        Assertions.assertEquals("5.4.3", returned.getVersionId());
        Assertions.assertEquals(oldDate, returned.getLastQueryTime());
    }

    @Test
    public void testGetStaleMetricsWithSameTTLForBoth()
    {
        long twentyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(20);
        Date oldDate = new Date(twentyDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(15, 15);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void testGetStaleMetricsWithOnlySnapshotsStale()
    {
        long twentyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(20);
        Date oldDate = new Date(twentyDaysAgo);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", oldDate));

        List<VersionQueryMetric> result = service.getStaleMetrics(25, 15);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("2.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testGetStaleMetricsWithOnlyReleasedVersionsStale()
    {
        long fortyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(40);
        long fiveDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", new Date(fortyDaysAgo)));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0-SNAPSHOT", new Date(fiveDaysAgo)));

        List<VersionQueryMetric> result = service.getStaleMetrics(30, 3);

        Assertions.assertEquals(2, result.size());
    }

    private static class StaleMetricsQueryMetricsStore implements QueryMetrics
    {
        private final List<VersionQueryMetric> metrics = new ArrayList<>();

        @Override
        public List<VersionQueryMetric> get(String groupId, String artifactId, String versionId)
        {
            return Collections.emptyList();
        }

        @Override
        public List<VersionQueryMetric> find(String groupId, String artifactId)
        {
            return Collections.emptyList();
        }

        @Override
        public List<VersionQueryMetric> getAll()
        {
            return new ArrayList<>(metrics);
        }

        @Override
        public void insert(VersionQueryMetric versionQueryMetric)
        {
            metrics.add(versionQueryMetric);
        }

        @Override
        public long consolidate(VersionQueryMetric metric)
        {
            return 0;
        }

        @Override
        public List<ProjectVersion> getAllStoredEntitiesCoordinates()
        {
            return Collections.emptyList();
        }

        @Override
        public List<VersionQueryMetric> findMetricsBefore(Date date)
        {
            return Collections.emptyList();
        }

        @Override
        public long delete(String groupId, String artifactId, String versionId)
        {
            return 0;
        }
    }
}

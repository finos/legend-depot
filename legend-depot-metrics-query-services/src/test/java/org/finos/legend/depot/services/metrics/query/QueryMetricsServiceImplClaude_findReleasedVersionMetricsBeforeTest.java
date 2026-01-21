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
import java.util.stream.Collectors;

public class QueryMetricsServiceImplClaude_findReleasedVersionMetricsBeforeTest
{
    private DateFilteringQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new DateFilteringQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeReturnsEmptyWhenNoMetrics()
    {
        Date cutoffDate = new Date(5000000L);

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeReturnsSingleReleasedVersion()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeFiltersOutSnapshotVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "master-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeReturnsMultipleReleasedVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "3.0.0", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertEquals("1.0.0", versions.get(0));
        Assertions.assertEquals("2.0.0", versions.get(1));
        Assertions.assertEquals("3.0.0", versions.get(2));
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeExcludesMetricsAfterDate()
    {
        Date cutoffDate = new Date(3000000L);
        Date beforeDate = new Date(2000000L);
        Date afterDate = new Date(4000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", beforeDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", afterDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithMixedVersionTypes()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.1-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "feature-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "3.0.0", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .collect(Collectors.toList());

        Assertions.assertTrue(versions.contains("1.0.0"));
        Assertions.assertTrue(versions.contains("2.0.0"));
        Assertions.assertTrue(versions.contains("3.0.0"));
        Assertions.assertFalse(versions.contains("1.0.1-SNAPSHOT"));
        Assertions.assertFalse(versions.contains("feature-SNAPSHOT"));
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeExcludesExactCutoffDate()
    {
        Date cutoffDate = new Date(3000000L);
        Date beforeDate = new Date(2000000L);
        Date exactDate = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", beforeDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", exactDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithOnlySnapshotVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "master-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "develop-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithAlphaAndBetaVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-alpha", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-beta", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithManyMetrics()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        for (int i = 0; i < 50; i++)
        {
            store.insert(new VersionQueryMetric("group" + i, "artifact" + i, "1.0." + i, metricDate));
        }

        for (int i = 0; i < 50; i++)
        {
            store.insert(new VersionQueryMetric("group" + i, "artifact" + i, "2.0." + i + "-SNAPSHOT", metricDate));
        }

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(50, result.size());

        long snapshotCount = result.stream()
                .filter(m -> m.getVersionId().endsWith("-SNAPSHOT"))
                .count();

        Assertions.assertEquals(0, snapshotCount);
    }

    @Test
    public void testFindReleasedVersionMetricsBeforePreservesMetricDetails()
    {
        Date metricDate = new Date(1234567890000L);
        Date cutoffDate = new Date(1234567890000L + 1000000L);

        VersionQueryMetric original = new VersionQueryMetric("org.example", "test-artifact", "5.4.3", metricDate);
        store.insert(original);

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        VersionQueryMetric returned = result.get(0);

        Assertions.assertEquals("org.example", returned.getGroupId());
        Assertions.assertEquals("test-artifact", returned.getArtifactId());
        Assertions.assertEquals("5.4.3", returned.getVersionId());
        Assertions.assertEquals(metricDate, returned.getLastQueryTime());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithVeryOldCutoffDate()
    {
        Date cutoffDate = new Date(1L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithVeryRecentCutoffDate()
    {
        Date cutoffDate = new Date(System.currentTimeMillis() + 1000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithDifferentSnapshotSuffixes()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "main-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "feature-branch-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(2, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertEquals("1.0.0", versions.get(0));
        Assertions.assertEquals("2.0.0", versions.get(1));
    }

    @Test
    public void testFindReleasedVersionMetricsBeforeWithComplexVersionNumbers()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "10.20.30", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.1.3-rc1", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.1.3-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .collect(Collectors.toList());

        Assertions.assertTrue(versions.contains("1.0.0"));
        Assertions.assertTrue(versions.contains("10.20.30"));
        Assertions.assertTrue(versions.contains("2.1.3-rc1"));
        Assertions.assertFalse(versions.contains("2.1.3-SNAPSHOT"));
    }

    private static class DateFilteringQueryMetricsStore implements QueryMetrics
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
            List<VersionQueryMetric> result = new ArrayList<>();
            for (VersionQueryMetric metric : metrics)
            {
                if (metric.getLastQueryTime().before(date))
                {
                    result.add(metric);
                }
            }
            return result;
        }

        @Override
        public long delete(String groupId, String artifactId, String versionId)
        {
            return 0;
        }
    }
}

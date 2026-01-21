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

public class QueryMetricsServiceImplClaude_findSnapshotVersionMetricsBeforeTest
{
    private SnapshotDateFilteringQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new SnapshotDateFilteringQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeReturnsEmptyWhenNoMetrics()
    {
        Date cutoffDate = new Date(5000000L);

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeReturnsSingleSnapshotVersion()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeFiltersOutReleasedVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "master-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(2, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertEquals("2.0.0-SNAPSHOT", versions.get(0));
        Assertions.assertEquals("master-SNAPSHOT", versions.get(1));
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeReturnsMultipleSnapshotVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "master-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());

        for (VersionQueryMetric metric : result)
        {
            Assertions.assertTrue(metric.getVersionId().endsWith("-SNAPSHOT"));
        }
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeExcludesMetricsAfterDate()
    {
        Date cutoffDate = new Date(3000000L);
        Date beforeDate = new Date(2000000L);
        Date afterDate = new Date(4000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", beforeDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", afterDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithMixedVersionTypes()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.1-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "feature-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "3.0.0", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(2, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertEquals("1.0.1-SNAPSHOT", versions.get(0));
        Assertions.assertEquals("feature-SNAPSHOT", versions.get(1));
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeExcludesExactCutoffDate()
    {
        Date cutoffDate = new Date(3000000L);
        Date beforeDate = new Date(2000000L);
        Date exactDate = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", beforeDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", exactDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithOnlyReleasedVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "3.0.0", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithAlphaAndBetaVersions()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-alpha", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-beta", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithManyMetrics()
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

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(50, result.size());

        long releasedCount = result.stream()
                .filter(m -> !m.getVersionId().endsWith("-SNAPSHOT"))
                .count();

        Assertions.assertEquals(0, releasedCount);
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforePreservesMetricDetails()
    {
        Date metricDate = new Date(1234567890000L);
        Date cutoffDate = new Date(1234567890000L + 1000000L);

        VersionQueryMetric original = new VersionQueryMetric("org.example", "test-artifact", "5.4.3-SNAPSHOT", metricDate);
        store.insert(original);

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        VersionQueryMetric returned = result.get(0);

        Assertions.assertEquals("org.example", returned.getGroupId());
        Assertions.assertEquals("test-artifact", returned.getArtifactId());
        Assertions.assertEquals("5.4.3-SNAPSHOT", returned.getVersionId());
        Assertions.assertEquals(metricDate, returned.getLastQueryTime());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithVeryOldCutoffDate()
    {
        Date cutoffDate = new Date(1L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithVeryRecentCutoffDate()
    {
        Date cutoffDate = new Date(System.currentTimeMillis() + 1000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("2.0.0-SNAPSHOT", result.get(0).getVersionId());
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithDifferentSnapshotSuffixes()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "main-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "feature-branch-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertEquals("2.0.0-SNAPSHOT", versions.get(0));
        Assertions.assertEquals("feature-branch-SNAPSHOT", versions.get(1));
        Assertions.assertEquals("main-SNAPSHOT", versions.get(2));
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeWithBranchSnapshots()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "master-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "develop-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "feature-xyz-SNAPSHOT", metricDate));

        List<VersionQueryMetric> result = service.findSnapshotVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(3, result.size());

        for (VersionQueryMetric metric : result)
        {
            Assertions.assertTrue(metric.getVersionId().endsWith("-SNAPSHOT"));
        }
    }

    @Test
    public void testFindSnapshotVersionMetricsBeforeComplementsReleasedVersionMethod()
    {
        Date cutoffDate = new Date(5000000L);
        Date metricDate = new Date(1000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", metricDate));
        store.insert(new VersionQueryMetric("group1", "artifact1", "3.0.0", metricDate));
        store.insert(new VersionQueryMetric("group2", "artifact2", "master-SNAPSHOT", metricDate));

        List<VersionQueryMetric> snapshotResult = service.findSnapshotVersionMetricsBefore(cutoffDate);
        List<VersionQueryMetric> releasedResult = service.findReleasedVersionMetricsBefore(cutoffDate);

        Assertions.assertEquals(2, snapshotResult.size());
        Assertions.assertEquals(2, releasedResult.size());

        Assertions.assertEquals(4, snapshotResult.size() + releasedResult.size());

        for (VersionQueryMetric metric : snapshotResult)
        {
            Assertions.assertTrue(metric.getVersionId().endsWith("-SNAPSHOT"));
        }

        for (VersionQueryMetric metric : releasedResult)
        {
            Assertions.assertFalse(metric.getVersionId().endsWith("-SNAPSHOT"));
        }
    }

    private static class SnapshotDateFilteringQueryMetricsStore implements QueryMetrics
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

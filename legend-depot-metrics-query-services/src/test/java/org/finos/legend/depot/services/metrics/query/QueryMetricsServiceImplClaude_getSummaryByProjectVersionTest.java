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

public class QueryMetricsServiceImplClaude_getSummaryByProjectVersionTest
{
    private CompleteQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new CompleteQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testGetSummaryByProjectVersionReturnsEmptyListWhenNoMetrics()
    {
        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSummaryByProjectVersionReturnsSingleMetric()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("group1", result.get(0).getGroupId());
        Assertions.assertEquals("artifact1", result.get(0).getArtifactId());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
        Assertions.assertEquals(now, result.get(0).getLastQueryTime());
    }

    @Test
    public void testGetSummaryByProjectVersionReturnsMultipleMetrics()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", now));
        store.insert(new VersionQueryMetric("group3", "artifact3", "3.0.0", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

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
    public void testGetSummaryByProjectVersionReturnsLatestMetricForEachVersion()
    {
        Date old = new Date(1000000L);
        Date middle = new Date(2000000L);
        Date newest = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", old));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", middle));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", newest));

        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", old));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", newest));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(2, result.size());

        for (VersionQueryMetric metric : result)
        {
            Assertions.assertEquals(newest, metric.getLastQueryTime());
        }
    }

    @Test
    public void testGetSummaryByProjectVersionDoesNotReturnDuplicates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetSummaryByProjectVersionWithMultipleVersionsOfSameArtifact()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "3.0.0", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

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
    public void testGetSummaryByProjectVersionWithManyProjectVersions()
    {
        Date now = new Date();

        for (int i = 0; i < 50; i++)
        {
            store.insert(new VersionQueryMetric("group" + i, "artifact" + i, "1.0." + i, now));
        }

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(50, result.size());
    }

    @Test
    public void testGetSummaryByProjectVersionReturnsCorrectMetricsForMixedScenario()
    {
        Date veryOld = new Date(1000000L);
        Date old = new Date(2000000L);
        Date recent = new Date(3000000L);
        Date newest = new Date(4000000L);

        // Project 1 has multiple metrics
        store.insert(new VersionQueryMetric("com.example", "project1", "1.0.0", veryOld));
        store.insert(new VersionQueryMetric("com.example", "project1", "1.0.0", newest));

        // Project 2 has single metric
        store.insert(new VersionQueryMetric("com.example", "project2", "2.0.0", recent));

        // Project 3 has multiple versions
        store.insert(new VersionQueryMetric("org.test", "project3", "1.0.0", old));
        store.insert(new VersionQueryMetric("org.test", "project3", "2.0.0", recent));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(4, result.size());

        VersionQueryMetric project1Metric = result.stream()
                .filter(m -> m.getArtifactId().equals("project1"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(project1Metric);
        Assertions.assertEquals(newest, project1Metric.getLastQueryTime());
    }

    @Test
    public void testGetSummaryByProjectVersionWithNullCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric(null, null, null, now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(1, result.size());
        Assertions.assertNull(result.get(0).getGroupId());
        Assertions.assertNull(result.get(0).getArtifactId());
        Assertions.assertNull(result.get(0).getVersionId());
    }

    @Test
    public void testGetSummaryByProjectVersionWithEmptyStringCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("", "", "", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("", result.get(0).getGroupId());
        Assertions.assertEquals("", result.get(0).getArtifactId());
        Assertions.assertEquals("", result.get(0).getVersionId());
    }

    @Test
    public void testGetSummaryByProjectVersionWithSnapshotVersions()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "master-SNAPSHOT", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(3, result.size());

        long snapshotCount = result.stream()
                .filter(m -> m.getVersionId().contains("SNAPSHOT"))
                .count();

        Assertions.assertEquals(2, snapshotCount);
    }

    @Test
    public void testGetSummaryByProjectVersionPreservesMetricDetails()
    {
        Date specificDate = new Date(1234567890000L);
        VersionQueryMetric original = new VersionQueryMetric("org.example", "test-artifact", "5.4.3", specificDate);
        store.insert(original);

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(1, result.size());
        VersionQueryMetric returned = result.get(0);

        Assertions.assertEquals("org.example", returned.getGroupId());
        Assertions.assertEquals("test-artifact", returned.getArtifactId());
        Assertions.assertEquals("5.4.3", returned.getVersionId());
        Assertions.assertEquals(specificDate, returned.getLastQueryTime());
    }

    @Test
    public void testGetSummaryByProjectVersionWithComplexVersionNumbers()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-alpha", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.1.3-beta.2", now));
        store.insert(new VersionQueryMetric("group3", "artifact3", "10.20.30-rc1", now));

        List<VersionQueryMetric> result = service.getSummaryByProjectVersion();

        Assertions.assertEquals(3, result.size());

        List<String> versions = result.stream()
                .map(VersionQueryMetric::getVersionId)
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertTrue(versions.contains("1.0.0-alpha"));
        Assertions.assertTrue(versions.contains("2.1.3-beta.2"));
        Assertions.assertTrue(versions.contains("10.20.30-rc1"));
    }

    private static class CompleteQueryMetricsStore implements QueryMetrics
    {
        private final List<VersionQueryMetric> metrics = new ArrayList<>();

        @Override
        public List<VersionQueryMetric> get(String groupId, String artifactId, String versionId)
        {
            List<VersionQueryMetric> result = new ArrayList<>();
            for (VersionQueryMetric metric : metrics)
            {
                if (isEqual(metric.getGroupId(), groupId) &&
                    isEqual(metric.getArtifactId(), artifactId) &&
                    isEqual(metric.getVersionId(), versionId))
                {
                    result.add(metric);
                }
            }
            return result;
        }

        private boolean isEqual(Object a, Object b)
        {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.equals(b);
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
            List<ProjectVersion> coordinates = new ArrayList<>();
            List<String> seen = new ArrayList<>();

            for (VersionQueryMetric metric : metrics)
            {
                String key = metric.getGroupId() + ":" + metric.getArtifactId() + ":" + metric.getVersionId();
                if (!seen.contains(key))
                {
                    seen.add(key);
                    coordinates.add(new ProjectVersion(metric.getGroupId(), metric.getArtifactId(), metric.getVersionId()));
                }
            }

            return coordinates;
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

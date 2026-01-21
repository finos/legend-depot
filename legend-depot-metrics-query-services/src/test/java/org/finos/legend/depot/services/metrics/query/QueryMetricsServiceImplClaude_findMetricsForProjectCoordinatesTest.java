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

public class QueryMetricsServiceImplClaude_findMetricsForProjectCoordinatesTest
{
    private ProjectCoordinatesQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new ProjectCoordinatesQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testFindMetricsForProjectCoordinatesReturnsEmptyWhenNoMetrics()
    {
        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesReturnsSingleMetric()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("group1", result.get(0).getGroupId());
        Assertions.assertEquals("artifact1", result.get(0).getArtifactId());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesReturnsMultipleVersions()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "3.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

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
    public void testFindMetricsForProjectCoordinatesReturnsAllMetricsForSameVersion()
    {
        Date time1 = new Date(1000000L);
        Date time2 = new Date(2000000L);
        Date time3 = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", time1));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", time2));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", time3));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesFiltersOutDifferentGroup()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group3", "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("group1", result.get(0).getGroupId());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesFiltersOutDifferentArtifact()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact2", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact3", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("artifact1", result.get(0).getArtifactId());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithNonExistentCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("nonexistent", "project");

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithNullGroupId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric(null, "artifact1", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates(null, "artifact1");

        Assertions.assertEquals(1, result.size());
        Assertions.assertNull(result.get(0).getGroupId());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithNullArtifactId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", null, "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertNull(result.get(0).getArtifactId());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithEmptyStrings()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("", "", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("", "");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("", result.get(0).getGroupId());
        Assertions.assertEquals("", result.get(0).getArtifactId());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesReturnsMultipleVersionsAndInstances()
    {
        Date time1 = new Date(1000000L);
        Date time2 = new Date(2000000L);
        Date time3 = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", time1));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", time2));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", time1));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", time3));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertEquals(4, result.size());

        long version1Count = result.stream()
                .filter(m -> m.getVersionId().equals("1.0.0"))
                .count();
        long version2Count = result.stream()
                .filter(m -> m.getVersionId().equals("2.0.0"))
                .count();

        Assertions.assertEquals(2, version1Count);
        Assertions.assertEquals(2, version2Count);
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithManyMetrics()
    {
        Date now = new Date();

        for (int i = 0; i < 100; i++)
        {
            store.insert(new VersionQueryMetric("targetGroup", "targetArtifact", "1.0." + i, now));
        }

        for (int i = 0; i < 50; i++)
        {
            store.insert(new VersionQueryMetric("otherGroup", "otherArtifact", "1.0.0", now));
        }

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("targetGroup", "targetArtifact");

        Assertions.assertEquals(100, result.size());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithSnapshotVersions()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0-SNAPSHOT", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "master-SNAPSHOT", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");

        Assertions.assertEquals(3, result.size());

        long snapshotCount = result.stream()
                .filter(m -> m.getVersionId().contains("SNAPSHOT"))
                .count();

        Assertions.assertEquals(2, snapshotCount);
    }

    @Test
    public void testFindMetricsForProjectCoordinatesPreservesAllMetricDetails()
    {
        Date date1 = new Date(1234567890000L);
        Date date2 = new Date(9876543210000L);

        VersionQueryMetric metric1 = new VersionQueryMetric("org.example", "test-artifact", "1.0.0", date1);
        VersionQueryMetric metric2 = new VersionQueryMetric("org.example", "test-artifact", "2.0.0", date2);

        store.insert(metric1);
        store.insert(metric2);

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("org.example", "test-artifact");

        Assertions.assertEquals(2, result.size());

        VersionQueryMetric returned1 = result.stream()
                .filter(m -> m.getVersionId().equals("1.0.0"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(returned1);
        Assertions.assertEquals("org.example", returned1.getGroupId());
        Assertions.assertEquals("test-artifact", returned1.getArtifactId());
        Assertions.assertEquals("1.0.0", returned1.getVersionId());
        Assertions.assertEquals(date1, returned1.getLastQueryTime());
    }

    @Test
    public void testFindMetricsForProjectCoordinatesWithSpecialCharacters()
    {
        Date now = new Date();
        String specialGroup = "org.example-test.v2";
        String specialArtifact = "my-artifact_test.module";

        store.insert(new VersionQueryMetric(specialGroup, specialArtifact, "1.0.0", now));
        store.insert(new VersionQueryMetric(specialGroup, specialArtifact, "2.0.0-beta", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates(specialGroup, specialArtifact);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().allMatch(m -> m.getGroupId().equals(specialGroup)));
        Assertions.assertTrue(result.stream().allMatch(m -> m.getArtifactId().equals(specialArtifact)));
    }

    @Test
    public void testFindMetricsForProjectCoordinatesReturnsEmptyWhenOnlyDifferentProjectsExist()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "1.0.0", now));
        store.insert(new VersionQueryMetric("group3", "artifact3", "1.0.0", now));

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group4", "artifact4");

        Assertions.assertTrue(result.isEmpty());
    }

    private static class ProjectCoordinatesQueryMetricsStore implements QueryMetrics
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
            List<VersionQueryMetric> result = new ArrayList<>();
            for (VersionQueryMetric metric : metrics)
            {
                if (isEqual(metric.getGroupId(), groupId) &&
                    isEqual(metric.getArtifactId(), artifactId))
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

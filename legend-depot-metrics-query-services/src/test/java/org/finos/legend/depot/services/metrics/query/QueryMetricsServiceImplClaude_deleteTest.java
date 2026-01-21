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

public class QueryMetricsServiceImplClaude_deleteTest
{
    private DeleteQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new DeleteQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testDeleteRemovesSingleMetric()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertTrue(remaining.isEmpty());
    }

    @Test
    public void testDeleteRemovesOnlyMatchingMetric()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", now));

        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("group2", remaining.get(0).getGroupId());
    }

    @Test
    public void testDeleteWithNonExistentCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("nonexistent", "project", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
    }

    @Test
    public void testDeleteFromEmptyStore()
    {
        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertTrue(remaining.isEmpty());
    }

    @Test
    public void testDeleteRemovesAllMetricsWithSameCoordinates()
    {
        Date date1 = new Date(1000000L);
        Date date2 = new Date(2000000L);
        Date date3 = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", date1));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", date2));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", date3));

        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertTrue(remaining.isEmpty());
    }

    @Test
    public void testDeleteByGroupIdOnly()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact2", "2.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact3", "3.0.0", now));

        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(2, remaining.size());

        List<String> groups = remaining.stream()
                .map(VersionQueryMetric::getGroupId)
                .collect(Collectors.toList());

        Assertions.assertTrue(groups.contains("group1"));
        Assertions.assertTrue(groups.contains("group2"));
    }

    @Test
    public void testDeleteDoesNotRemoveDifferentVersion()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));

        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("2.0.0", remaining.get(0).getVersionId());
    }

    @Test
    public void testDeleteWithNullGroupId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric(null, "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete(null, "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("group1", remaining.get(0).getGroupId());
    }

    @Test
    public void testDeleteWithNullArtifactId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", null, "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("group1", null, "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("artifact1", remaining.get(0).getArtifactId());
    }

    @Test
    public void testDeleteWithNullVersionId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", null, now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("group1", "artifact1", null);

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("1.0.0", remaining.get(0).getVersionId());
    }

    @Test
    public void testDeleteWithEmptyStringCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("", "", "", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("", "", "");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("group1", remaining.get(0).getGroupId());
    }

    @Test
    public void testDeleteCalledMultipleTimes()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", now));
        store.insert(new VersionQueryMetric("group3", "artifact3", "3.0.0", now));

        service.delete("group1", "artifact1", "1.0.0");
        service.delete("group2", "artifact2", "2.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("group3", remaining.get(0).getGroupId());
    }

    @Test
    public void testDeleteWithSnapshotVersion()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("group1", "artifact1", "1.0.0-SNAPSHOT");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("1.0.0", remaining.get(0).getVersionId());
    }

    @Test
    public void testDeleteWithComplexVersionNumber()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "10.20.30-rc1", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete("group1", "artifact1", "10.20.30-rc1");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("1.0.0", remaining.get(0).getVersionId());
    }

    @Test
    public void testDeleteWithSpecialCharactersInCoordinates()
    {
        Date now = new Date();
        String specialGroup = "org.example-test.v2";
        String specialArtifact = "my-artifact_test.module";
        String specialVersion = "1.0.0-SNAPSHOT";

        store.insert(new VersionQueryMetric(specialGroup, specialArtifact, specialVersion, now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.delete(specialGroup, specialArtifact, specialVersion);

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("group1", remaining.get(0).getGroupId());
    }

    @Test
    public void testDeletePreservesUnrelatedMetrics()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact2", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact1", "1.0.0", now));

        service.delete("group1", "artifact1", "1.0.0");

        List<VersionQueryMetric> remaining = store.getAll();
        Assertions.assertEquals(3, remaining.size());

        boolean hasGroup1Artifact1Version2 = remaining.stream()
                .anyMatch(m -> m.getGroupId().equals("group1") &&
                              m.getArtifactId().equals("artifact1") &&
                              m.getVersionId().equals("2.0.0"));
        Assertions.assertTrue(hasGroup1Artifact1Version2);

        boolean hasGroup1Artifact2 = remaining.stream()
                .anyMatch(m -> m.getGroupId().equals("group1") &&
                              m.getArtifactId().equals("artifact2"));
        Assertions.assertTrue(hasGroup1Artifact2);

        boolean hasGroup2 = remaining.stream()
                .anyMatch(m -> m.getGroupId().equals("group2"));
        Assertions.assertTrue(hasGroup2);
    }

    private static class DeleteQueryMetricsStore implements QueryMetrics
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
            List<VersionQueryMetric> toRemove = new ArrayList<>();
            for (VersionQueryMetric metric : metrics)
            {
                if (isEqual(metric.getGroupId(), groupId) &&
                    isEqual(metric.getArtifactId(), artifactId) &&
                    isEqual(metric.getVersionId(), versionId))
                {
                    toRemove.add(metric);
                }
            }
            metrics.removeAll(toRemove);
            return toRemove.size();
        }

        private boolean isEqual(Object a, Object b)
        {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.equals(b);
        }
    }
}

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
import java.util.Optional;

public class QueryMetricsServiceImplClaude_getSummaryTest
{
    private SimpleQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new SimpleQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testGetSummaryReturnsEmptyWhenNoMetricsExist()
    {
        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testGetSummaryReturnsSingleMetric()
    {
        Date now = new Date();
        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0", now);
        store.insert(metric);

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("group1", result.get().getGroupId());
        Assertions.assertEquals("artifact1", result.get().getArtifactId());
        Assertions.assertEquals("1.0.0", result.get().getVersionId());
        Assertions.assertEquals(now, result.get().getLastQueryTime());
    }

    @Test
    public void testGetSummaryReturnsLatestMetricFromMultiple()
    {
        Date oldest = new Date(1000000L);
        Date middle = new Date(2000000L);
        Date newest = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", middle));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldest));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", newest));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(newest, result.get().getLastQueryTime());
    }

    @Test
    public void testGetSummaryDoesNotReturnMetricsFromDifferentGroup()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact1", "1.0.0", now));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("group1", result.get().getGroupId());
    }

    @Test
    public void testGetSummaryDoesNotReturnMetricsFromDifferentArtifact()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact2", "1.0.0", now));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("artifact1", result.get().getArtifactId());
    }

    @Test
    public void testGetSummaryDoesNotReturnMetricsFromDifferentVersion()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("1.0.0", result.get().getVersionId());
    }

    @Test
    public void testGetSummaryWithNullGroupId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric(null, "artifact1", "1.0.0", now));

        Optional<VersionQueryMetric> result = service.getSummary(null, "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getGroupId());
    }

    @Test
    public void testGetSummaryWithNullArtifactId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", null, "1.0.0", now));

        Optional<VersionQueryMetric> result = service.getSummary("group1", null, "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getArtifactId());
    }

    @Test
    public void testGetSummaryWithNullVersionId()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", null, now));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", null);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getVersionId());
    }

    @Test
    public void testGetSummaryWithEmptyStrings()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("", "", "", now));

        Optional<VersionQueryMetric> result = service.getSummary("", "", "");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("", result.get().getGroupId());
        Assertions.assertEquals("", result.get().getArtifactId());
        Assertions.assertEquals("", result.get().getVersionId());
    }

    @Test
    public void testGetSummaryReturnsLatestWithManyMetrics()
    {
        Date latest = new Date(10000000L);

        for (int i = 0; i < 100; i++)
        {
            store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", new Date(i * 1000L)));
        }

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", latest));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(latest, result.get().getLastQueryTime());
    }

    @Test
    public void testGetSummaryWithIdenticalTimestamps()
    {
        Date sameTime = new Date(1000000L);
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", sameTime));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", sameTime));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", sameTime));

        Optional<VersionQueryMetric> result = service.getSummary("group1", "artifact1", "1.0.0");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(sameTime, result.get().getLastQueryTime());
    }

    @Test
    public void testGetSummaryReturnsEmptyForNonMatchingCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        Optional<VersionQueryMetric> result = service.getSummary("group2", "artifact2", "2.0.0");

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testGetSummaryWithSpecialCharactersInCoordinates()
    {
        Date now = new Date();
        String specialGroup = "org.example-test.v2";
        String specialArtifact = "my-artifact_test";
        String specialVersion = "1.0.0-SNAPSHOT";

        store.insert(new VersionQueryMetric(specialGroup, specialArtifact, specialVersion, now));

        Optional<VersionQueryMetric> result = service.getSummary(specialGroup, specialArtifact, specialVersion);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(specialGroup, result.get().getGroupId());
        Assertions.assertEquals(specialArtifact, result.get().getArtifactId());
        Assertions.assertEquals(specialVersion, result.get().getVersionId());
    }

    private static class SimpleQueryMetricsStore implements QueryMetrics
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

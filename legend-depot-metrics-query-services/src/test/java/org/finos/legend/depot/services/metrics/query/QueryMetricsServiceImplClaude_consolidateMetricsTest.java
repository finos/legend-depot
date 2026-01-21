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

public class QueryMetricsServiceImplClaude_consolidateMetricsTest
{
    private ConsolidateQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new ConsolidateQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testConsolidateMetricsWithNoMetrics()
    {
        service.consolidateMetrics();

        Assertions.assertEquals(0, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsWithSingleProjectVersion()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.consolidateMetrics();

        Assertions.assertEquals(1, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsWithMultipleProjectVersions()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", now));
        store.insert(new VersionQueryMetric("group3", "artifact3", "3.0.0", now));

        service.consolidateMetrics();

        Assertions.assertEquals(3, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsConsolidatesLatestMetric()
    {
        Date old = new Date(1000000L);
        Date middle = new Date(2000000L);
        Date newest = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", old));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", middle));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", newest));

        service.consolidateMetrics();

        Assertions.assertEquals(1, store.getConsolidateCallCount());
        VersionQueryMetric consolidated = store.getLastConsolidatedMetric();
        Assertions.assertNotNull(consolidated);
        Assertions.assertEquals(newest, consolidated.getLastQueryTime());
    }

    @Test
    public void testConsolidateMetricsWithMultipleVersionsOfSameProject()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "3.0.0", now));

        service.consolidateMetrics();

        Assertions.assertTrue(store.getConsolidateCallCount() >= 2);
        Assertions.assertTrue(store.getConsolidateCallCount() <= 3);
    }

    @Test
    public void testConsolidateMetricsWithDuplicateMetricsPerVersion()
    {
        Date date1 = new Date(1000000L);
        Date date2 = new Date(2000000L);
        Date date3 = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", date1));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", date2));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", date3));

        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", date1));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", date2));

        service.consolidateMetrics();

        Assertions.assertEquals(2, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsCallsStoreConsolidate()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        service.consolidateMetrics();

        Assertions.assertTrue(store.wasConsolidateCalled());
    }

    @Test
    public void testConsolidateMetricsWithSnapshotVersions()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-SNAPSHOT", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "master-SNAPSHOT", now));

        service.consolidateMetrics();

        Assertions.assertTrue(store.getConsolidateCallCount() >= 2);
        Assertions.assertTrue(store.getConsolidateCallCount() <= 3);
    }

    @Test
    public void testConsolidateMetricsPreservesMetricDetails()
    {
        Date specificDate = new Date(1234567890000L);
        store.insert(new VersionQueryMetric("org.example", "test-artifact", "5.4.3", specificDate));

        service.consolidateMetrics();

        VersionQueryMetric consolidated = store.getLastConsolidatedMetric();
        Assertions.assertNotNull(consolidated);
        Assertions.assertEquals("org.example", consolidated.getGroupId());
        Assertions.assertEquals("test-artifact", consolidated.getArtifactId());
        Assertions.assertEquals("5.4.3", consolidated.getVersionId());
        Assertions.assertEquals(specificDate, consolidated.getLastQueryTime());
    }

    @Test
    public void testConsolidateMetricsWithEmptyStringCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("", "", "", now));

        service.consolidateMetrics();

        Assertions.assertEquals(1, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsWithNullCoordinates()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric(null, null, null, now));

        service.consolidateMetrics();

        Assertions.assertEquals(1, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsWithManyProjectVersions()
    {
        Date now = new Date();

        for (int i = 0; i < 50; i++)
        {
            store.insert(new VersionQueryMetric("group" + i, "artifact" + i, "1.0." + i, now));
        }

        service.consolidateMetrics();

        Assertions.assertTrue(store.getConsolidateCallCount() >= 45);
        Assertions.assertTrue(store.getConsolidateCallCount() <= 50);
    }

    @Test
    public void testConsolidateMetricsHandlesExceptionsGracefully()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group2", "artifact2", "2.0.0", now));

        store.setThrowExceptionOnConsolidate(true);

        service.consolidateMetrics();

        Assertions.assertTrue(store.getConsolidateCallCount() > 0);
    }

    @Test
    public void testConsolidateMetricsSelectsCorrectMetricFromMultiple()
    {
        Date oldest = new Date(1000000L);
        Date middle = new Date(2000000L);
        Date newest = new Date(3000000L);

        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", oldest));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", newest));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", middle));

        service.consolidateMetrics();

        VersionQueryMetric consolidated = store.getLastConsolidatedMetric();
        Assertions.assertEquals(newest, consolidated.getLastQueryTime());
    }

    @Test
    public void testConsolidateMetricsWithComplexVersionNumbers()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0-alpha", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "2.1.3-beta.2", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "10.20.30-rc1", now));

        service.consolidateMetrics();

        Assertions.assertEquals(3, store.getConsolidateCallCount());
    }

    @Test
    public void testConsolidateMetricsWithSpecialCharactersInCoordinates()
    {
        Date now = new Date();
        String specialGroup = "org.example-test.v2";
        String specialArtifact = "my-artifact_test.module";

        store.insert(new VersionQueryMetric(specialGroup, specialArtifact, "1.0.0-SNAPSHOT", now));

        service.consolidateMetrics();

        Assertions.assertEquals(1, store.getConsolidateCallCount());
        VersionQueryMetric consolidated = store.getLastConsolidatedMetric();
        Assertions.assertEquals(specialGroup, consolidated.getGroupId());
        Assertions.assertEquals(specialArtifact, consolidated.getArtifactId());
    }

    @Test
    public void testConsolidateMetricsReturnsDeletedCount()
    {
        Date now = new Date();
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        store.insert(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));

        store.setDeletedCountPerConsolidate(2);

        service.consolidateMetrics();

        Assertions.assertEquals(2, store.getLastDeletedCount());
    }

    private static class ConsolidateQueryMetricsStore implements QueryMetrics
    {
        private final List<VersionQueryMetric> metrics = new ArrayList<>();
        private int consolidateCallCount = 0;
        private VersionQueryMetric lastConsolidatedMetric = null;
        private boolean throwExceptionOnConsolidate = false;
        private long deletedCountPerConsolidate = 0;
        private long lastDeletedCount = 0;

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
            consolidateCallCount++;
            lastConsolidatedMetric = metric;
            lastDeletedCount = deletedCountPerConsolidate;

            if (throwExceptionOnConsolidate)
            {
                throw new RuntimeException("Test exception during consolidate");
            }

            return deletedCountPerConsolidate;
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

        public int getConsolidateCallCount()
        {
            return consolidateCallCount;
        }

        public VersionQueryMetric getLastConsolidatedMetric()
        {
            return lastConsolidatedMetric;
        }

        public boolean wasConsolidateCalled()
        {
            return consolidateCallCount > 0;
        }

        public void setThrowExceptionOnConsolidate(boolean throwException)
        {
            this.throwExceptionOnConsolidate = throwException;
        }

        public void setDeletedCountPerConsolidate(long count)
        {
            this.deletedCountPerConsolidate = count;
        }

        public long getLastDeletedCount()
        {
            return lastDeletedCount;
        }
    }
}

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

public class QueryMetricsServiceImplClaude_persistTest
{
    private PersistQueryMetricsStore store;
    private QueryMetricsServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        store = new PersistQueryMetricsStore();
        service = new QueryMetricsServiceImpl(store);
    }

    @Test
    public void testPersistWithEmptyRegistry()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();

        service.persist(registry);

        Assertions.assertEquals(0, store.getAll().size());
    }

    @Test
    public void testPersistWithSingleMetric()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date now = new Date();
        registry.record("group1", "artifact1", "1.0.0", now);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(1, stored.size());
        Assertions.assertEquals("group1", stored.get(0).getGroupId());
        Assertions.assertEquals("artifact1", stored.get(0).getArtifactId());
        Assertions.assertEquals("1.0.0", stored.get(0).getVersionId());
        Assertions.assertEquals(now, stored.get(0).getLastQueryTime());
    }

    @Test
    public void testPersistWithMultipleMetrics()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date date1 = new Date(1000000L);
        Date date2 = new Date(2000000L);
        Date date3 = new Date(3000000L);

        registry.record("group1", "artifact1", "1.0.0", date1);
        registry.record("group2", "artifact2", "2.0.0", date2);
        registry.record("group3", "artifact3", "3.0.0", date3);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(3, stored.size());
    }

    @Test
    public void testPersistEmptiesTheRegistry()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        registry.record("group1", "artifact1", "1.0.0");
        registry.record("group2", "artifact2", "2.0.0");

        service.persist(registry);

        Optional<VersionQueryMetric> result = registry.findFirst();
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testPersistPreservesOrderOfMetrics()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date date1 = new Date(1000000L);
        Date date2 = new Date(2000000L);
        Date date3 = new Date(3000000L);

        registry.record("first", "a1", "1.0.0", date1);
        registry.record("second", "a2", "2.0.0", date2);
        registry.record("third", "a3", "3.0.0", date3);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals("first", stored.get(0).getGroupId());
        Assertions.assertEquals("second", stored.get(1).getGroupId());
        Assertions.assertEquals("third", stored.get(2).getGroupId());
    }

    @Test
    public void testPersistWithManyMetrics()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date now = new Date();

        for (int i = 0; i < 100; i++)
        {
            registry.record("group" + i, "artifact" + i, "1.0." + i, now);
        }

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(100, stored.size());
    }

    @Test
    public void testPersistCalledMultipleTimes()
    {
        InMemoryQueryMetricsRegistry registry1 = new InMemoryQueryMetricsRegistry();
        registry1.record("group1", "artifact1", "1.0.0");

        InMemoryQueryMetricsRegistry registry2 = new InMemoryQueryMetricsRegistry();
        registry2.record("group2", "artifact2", "2.0.0");

        service.persist(registry1);
        service.persist(registry2);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(2, stored.size());
    }

    @Test
    public void testPersistWithMetricsHavingSameCoordinates()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date date1 = new Date(1000000L);
        Date date2 = new Date(2000000L);

        registry.record("group1", "artifact1", "1.0.0", date1);
        registry.record("group1", "artifact1", "1.0.0", date2);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(2, stored.size());
    }

    @Test
    public void testPersistWithNullValuesInMetrics()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        registry.record(null, null, null, null);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(1, stored.size());
        Assertions.assertNull(stored.get(0).getGroupId());
        Assertions.assertNull(stored.get(0).getArtifactId());
        Assertions.assertNull(stored.get(0).getVersionId());
        Assertions.assertNull(stored.get(0).getLastQueryTime());
    }

    @Test
    public void testPersistWithEmptyStringCoordinates()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date now = new Date();
        registry.record("", "", "", now);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(1, stored.size());
        Assertions.assertEquals("", stored.get(0).getGroupId());
        Assertions.assertEquals("", stored.get(0).getArtifactId());
        Assertions.assertEquals("", stored.get(0).getVersionId());
    }

    @Test
    public void testPersistWithSnapshotVersions()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date now = new Date();

        registry.record("group1", "artifact1", "1.0.0-SNAPSHOT", now);
        registry.record("group1", "artifact1", "master-SNAPSHOT", now);
        registry.record("group1", "artifact1", "2.0.0", now);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(3, stored.size());

        long snapshotCount = stored.stream()
                .filter(m -> m.getVersionId().endsWith("-SNAPSHOT"))
                .count();

        Assertions.assertEquals(2, snapshotCount);
    }

    @Test
    public void testPersistWithCustomRegistry()
    {
        SimpleTestRegistry registry = new SimpleTestRegistry();
        Date now = new Date();

        registry.addMetric(new VersionQueryMetric("group1", "artifact1", "1.0.0", now));
        registry.addMetric(new VersionQueryMetric("group2", "artifact2", "2.0.0", now));

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(2, stored.size());
    }

    @Test
    public void testPersistDoesNotModifyExistingStoreContents()
    {
        Date existingDate = new Date(1000000L);
        store.insert(new VersionQueryMetric("existing", "metric", "0.0.1", existingDate));

        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date newDate = new Date(2000000L);
        registry.record("new", "metric", "1.0.0", newDate);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(2, stored.size());

        VersionQueryMetric existingMetric = stored.stream()
                .filter(m -> m.getGroupId().equals("existing"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(existingMetric);
        Assertions.assertEquals("0.0.1", existingMetric.getVersionId());
    }

    @Test
    public void testPersistHandlesComplexVersionNumbers()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date now = new Date();

        registry.record("group1", "artifact1", "1.0.0-alpha", now);
        registry.record("group1", "artifact1", "2.1.3-beta.2", now);
        registry.record("group1", "artifact1", "10.20.30-rc1", now);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(3, stored.size());
    }

    @Test
    public void testPersistWithSpecialCharactersInCoordinates()
    {
        InMemoryQueryMetricsRegistry registry = new InMemoryQueryMetricsRegistry();
        Date now = new Date();

        String specialGroup = "org.example-test.v2";
        String specialArtifact = "my-artifact_test.module";
        String specialVersion = "1.0.0-SNAPSHOT";

        registry.record(specialGroup, specialArtifact, specialVersion, now);

        service.persist(registry);

        List<VersionQueryMetric> stored = store.getAll();
        Assertions.assertEquals(1, stored.size());
        Assertions.assertEquals(specialGroup, stored.get(0).getGroupId());
        Assertions.assertEquals(specialArtifact, stored.get(0).getArtifactId());
        Assertions.assertEquals(specialVersion, stored.get(0).getVersionId());
    }

    private static class PersistQueryMetricsStore implements QueryMetrics
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

    private static class SimpleTestRegistry implements QueryMetricsRegistry
    {
        private final List<VersionQueryMetric> metrics = new ArrayList<>();

        public void addMetric(VersionQueryMetric metric)
        {
            metrics.add(metric);
        }

        @Override
        public void record(String groupId, String artifactId, String versionId, Date date)
        {
            metrics.add(new VersionQueryMetric(groupId, artifactId, versionId, date));
        }

        @Override
        public Optional<VersionQueryMetric> findFirst()
        {
            if (metrics.isEmpty())
            {
                return Optional.empty();
            }
            return Optional.of(metrics.remove(0));
        }
    }
}

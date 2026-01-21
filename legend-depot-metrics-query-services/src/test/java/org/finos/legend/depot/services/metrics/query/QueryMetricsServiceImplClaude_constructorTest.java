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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QueryMetricsServiceImplClaude_constructorTest
{
    @Test
    public void testConstructorWithValidQueryMetrics()
    {
        QueryMetrics mockStore = new SimpleQueryMetricsImpl();
        QueryMetricsServiceImpl service = new QueryMetricsServiceImpl(mockStore);

        Assertions.assertNotNull(service);
    }

    @Test
    public void testConstructorWithNullQueryMetrics()
    {
        QueryMetricsServiceImpl service = new QueryMetricsServiceImpl(null);

        Assertions.assertNotNull(service);
    }

    @Test
    public void testConstructorCreatesUsableService()
    {
        SimpleQueryMetricsImpl mockStore = new SimpleQueryMetricsImpl();
        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        mockStore.insert(metric);

        QueryMetricsServiceImpl service = new QueryMetricsServiceImpl(mockStore);

        List<VersionQueryMetric> result = service.findMetricsForProjectCoordinates("group1", "artifact1");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("group1", result.get(0).getGroupId());
        Assertions.assertEquals("artifact1", result.get(0).getArtifactId());
        Assertions.assertEquals("1.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testConstructorAllowsMultipleInstances()
    {
        QueryMetrics store1 = new SimpleQueryMetricsImpl();
        QueryMetrics store2 = new SimpleQueryMetricsImpl();

        QueryMetricsServiceImpl service1 = new QueryMetricsServiceImpl(store1);
        QueryMetricsServiceImpl service2 = new QueryMetricsServiceImpl(store2);

        Assertions.assertNotNull(service1);
        Assertions.assertNotNull(service2);
    }

    private static class SimpleQueryMetricsImpl implements QueryMetrics
    {
        private final List<VersionQueryMetric> metrics = new ArrayList<>();

        @Override
        public List<VersionQueryMetric> get(String groupId, String artifactId, String versionId)
        {
            List<VersionQueryMetric> result = new ArrayList<>();
            for (VersionQueryMetric metric : metrics)
            {
                if (metric.getGroupId().equals(groupId) &&
                    metric.getArtifactId().equals(artifactId) &&
                    metric.getVersionId().equals(versionId))
                {
                    result.add(metric);
                }
            }
            return result;
        }

        @Override
        public List<VersionQueryMetric> find(String groupId, String artifactId)
        {
            List<VersionQueryMetric> result = new ArrayList<>();
            for (VersionQueryMetric metric : metrics)
            {
                if (metric.getGroupId().equals(groupId) &&
                    metric.getArtifactId().equals(artifactId))
                {
                    result.add(metric);
                }
            }
            return result;
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

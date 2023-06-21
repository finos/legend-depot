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

package org.finos.legend.depot.store.status;

import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.services.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.store.metrics.store.mongo.QueryMetricsMongo;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.status.domain.StoreStatus;
import org.finos.legend.depot.store.status.services.StoreStatusService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public class TestStatusServices extends TestStoreMongo
{
    private QueryMetricsMongo metricsStore = new QueryMetricsMongo(mongoProvider);
    private QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private QueryMetricsHandler queryMetricsHandler = new QueryMetricsHandler(metricsStore, metricsRegistry);
    private UpdateProjectsVersions projectsVersions  = new ProjectsVersionsMongo(mongoProvider);

    private UpdateProjects projects = new ProjectsMongo(mongoProvider);
    private UpdateEntities entities = new EntitiesMongo(mongoProvider);
    private StoreStatusService statusService = new StoreStatusService(projectsVersions, projects, entities, queryMetricsHandler);

    @Before
    public void setup()
    {
        setUpProjectsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/projects.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/versioned-entities.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/revision-entities.json"));

        metricsStore.getCollection().drop();
        metricsStore.insert(new VersionQueryMetric("examples.metadata", "test", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("examples.metadata", "test", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("examples.metadata", "test", "2.2.0"));
        metricsStore.insert(new VersionQueryMetric("examples.metadata", "test", "1.0.0"));
    }

    @After
    public void tearDown()
    {
        metricsStore.getCollection().drop();
    }

    @Test
    public void testStatus()
    {
        List<StoreStatus.ProjectSummary> status = statusService.getStatus();
        Assert.assertNotNull(status);
        Assert.assertEquals(3, status.size());

        StoreStatus.DocumentCounts counts = statusService.getDocumentCounts();
        Assert.assertNotNull(counts);
        Assert.assertEquals(3, counts.totalVersionEntities);
    }

    @Test
    public void getVersionsCount()
    {
        StoreStatus.DocumentCounts counts = statusService.getDocumentCounts("examples.metadata", "test", "2.2.0");
        Assert.assertNotNull(counts);
        Assert.assertEquals(3, counts.totalVersionEntities);
    }

    @Test
    public void getRevisionsCount()
    {
        StoreStatus.DocumentCounts counts = statusService.getDocumentCounts("examples.metadata", "test",BRANCH_SNAPSHOT("master"));
        Assert.assertNotNull(counts);
        Assert.assertEquals(8, counts.totalVersionEntities);
    }

    @Test
    public void getMetricsStoreStatus()
    {
        metricsStore.insert(new VersionQueryMetric("examples.metadata", "test", "1.0.0"));
        Assert.assertEquals(5, metricsStore.getAll().size());
        Assert.assertNotNull(metricsStore.get("examples.metadata","test","2.2.0").get(0).getLastQueryTime());
        Assert.assertNotNull(metricsStore.get("examples.metadata","test","1.0.0").get(0).getLastQueryTime());
    }

}

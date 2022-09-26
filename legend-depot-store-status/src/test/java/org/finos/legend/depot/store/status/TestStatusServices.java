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

import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.store.mongo.MongoRefreshStatus;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.store.metrics.store.mongo.MongoQueryMetrics;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.status.domain.StoreStatus;
import org.finos.legend.depot.store.status.services.StoreStatusService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class TestStatusServices extends TestStoreMongo
{

    private MongoRefreshStatus refreshStatus = new MongoRefreshStatus(mongoProvider);
    private MongoQueryMetrics queryMetrics = new MongoQueryMetrics(mongoProvider);
    private UpdateProjects projects = new ProjectsMongo(mongoProvider);
    private UpdateEntities entities = new EntitiesMongo(mongoProvider);
    private ArtifactsRefreshService artifactsRefreshService = mock(ArtifactsRefreshService.class);
    private StoreStatusService statusService = new StoreStatusService(projects, entities, artifactsRefreshService, refreshStatus, queryMetrics);

    @Before
    public void setup()
    {
        setUpProjectsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/projects.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/versioned-entities.json"));
        setUpEntitiesDataFromFile(TestStoreMongo.class.getClassLoader().getResource("data/revision-entities.json"));

        QueryMetricsContainer.flush();
        queryMetrics.getCollection().drop();
        QueryMetricsContainer.record("examples.metadata", "test", "2.2.0");
        QueryMetricsContainer.record("examples.metadata", "test", "2.2.0");
        QueryMetricsContainer.record("examples.metadata", "test", "2.2.0");
        QueryMetricsContainer.record("examples.metadata", "test", "1.0.0");
        queryMetrics.persistMetrics();
    }

    @After
    public void tearDown()
    {
        QueryMetricsContainer.flush();
        queryMetrics.getCollection().drop();
    }

    @Test
    public void testStatus()
    {
        StoreStatus status = statusService.getStatus();
        Assert.assertNotNull(status);
        Assert.assertEquals(3, status.totalProjects);

        StoreStatus.DocumentCounts counts = statusService.getDocumentCounts();
        Assert.assertNotNull(counts);
        Assert.assertEquals(3, counts.totalVersionEntities);
        Assert.assertEquals(8, counts.totalRevisionEntities);
    }

    @Test
    public void getVersionsCount()
    {
        StoreStatus.DocumentCounts counts = statusService.getDocumentCounts("examples.metadata", "test", "2.2.0");
        Assert.assertNotNull(counts);
        Assert.assertEquals(3, counts.totalVersionEntities);
        Assert.assertEquals(0, counts.totalRevisionEntities);
    }

    @Test
    public void getRevisionsCount()
    {
        StoreStatus.DocumentCounts counts = statusService.getRevisionDocumentCounts("examples.metadata", "test");
        Assert.assertNotNull(counts);
        Assert.assertEquals(0, counts.totalVersionEntities);
        Assert.assertEquals(8, counts.totalRevisionEntities);
    }



}

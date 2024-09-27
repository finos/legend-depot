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

package org.finos.legend.depot.server.resources;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.metrics.query.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.services.metrics.query.QueryMetricsServiceImpl;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.server.resources.entities.EntitiesResource;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.depot.store.mongo.metrics.query.QueryMetricsMongo;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEntitiesResource extends TestBaseServices
{
    private UpdateProjects projects = mock(UpdateProjects.class);
    private UpdateProjectsVersions projectsVersions = mock(UpdateProjectsVersions.class);

    private final QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private final Queue queue = mock(Queue.class);
    private UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    private  EntitiesMongoTestUtils entityUtils = new EntitiesMongoTestUtils(mongoProvider);
    private ProjectsService projectsService = new ProjectsServiceImpl(projectsVersions, projects, metricsRegistry, queue, new ProjectsConfiguration("master"));
    private final EntitiesService entitiesService = new EntitiesServiceImpl(entitiesStore,projectsService);
    private EntitiesResource entitiesResource = new EntitiesResource(entitiesService, projectsService);
    private QueryMetricsMongo metricsStore = new QueryMetricsMongo(mongoProvider);
    private QueryMetricsService metricsHandler = new QueryMetricsServiceImpl(metricsStore);


    @Before
    public void setupMetadata()
    {
        super.setUpData();
        metricsStore.getCollection().drop();
        entityUtils.loadEntities("PROD-A", "2.3.0");
        entityUtils.loadEntities("PROD-A", BRANCH_SNAPSHOT("master"));
        when(projects.find("examples.metadata","test")).thenReturn(Optional.of(new StoreProjectData("mock","examples.metadata","test")));
        when(projects.find("example.services.test", "test")).thenReturn(Optional.of(new StoreProjectData("mock","example.services.test", "test")));
        when(projectsVersions.find("examples.metadata","test", "2.3.0")).thenReturn(Optional.of(new StoreProjectVersionData("examples.metadata","test", "2.3.0")));
        when(projectsVersions.find("example.services.test", "test", "1.0.1")).thenReturn(Optional.of(new StoreProjectVersionData("example.services.test", "test", "1.0.1")));
    }

    @After
    public void tearDown()
    {
        metricsStore.getCollection().drop();
    }

    @Test
    public void canGetEntitiesForProjectAndVersion()
    {
        Response response = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", null);
        List<Entity> entityList = (List<Entity>) response.getEntity();
        Assert.assertNotNull(entityList);
        Assert.assertEquals(7, entityList.size());
    }

    @Test
    public void canGetEntityByPathForProjectAndVersion()
    {
        Response response = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile", null);
        Entity entity = ((Optional<Entity>) response.getEntity()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());

    }

    @Test
    public void canGetEntitiesByPackageForProjectAndVersion()
    {
        Response response = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", null, true, null);
        List<Entity> entityList = (List<Entity>) response.getEntity();
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());

    }


    @Test
    public void canGetMetrics() throws InterruptedException
    {
        Assert.assertTrue(metricsStore.getAllStoredEntities().isEmpty());
        Assert.assertEquals(0, metricsStore.get("examples.metadata", "test", "2.3.0").size());

        when(projects.find("examples.metadata","test")).thenReturn(Optional.of(new StoreProjectData("mock","examples.metadata","test")));
        Response responseOne  = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", null, true, null);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), responseOne.getStatus());
        metricsHandler.persist(metricsRegistry);

        Assert.assertEquals(1, metricsStore.get("examples.metadata", "test", "2.3.0").size());
        Assert.assertNotNull(metricsStore.get("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime());
        TimeUnit.SECONDS.sleep(30);

        Response responseTwo = entitiesResource.getEntities("example.services.test", "test", "1.0.1", null);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), responseTwo.getStatus());
        metricsHandler.persist(metricsRegistry);

        Assert.assertNotNull(metricsStore.get("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime());
        Assert.assertEquals(3, metricsStore.getAllStoredEntities().size());
    }

    @Test
    public void canGetMetricsForTransitiveDependencies() throws InterruptedException
    {
        Assert.assertTrue(metricsStore.getAllStoredEntities().isEmpty());
        Assert.assertEquals(0, metricsStore.get("examples.metadata", "test", "2.3.0").size());

        StoreProjectVersionData versionData = new StoreProjectVersionData("examples.metadata", "test-master", "2.3.0");
        versionData.getVersionData().setDependencies(Collections.singletonList(new ProjectVersion("examples.metadata","test", "2.3.0")));
        when(projects.find("examples.metadata","test-master")).thenReturn(Optional.of(new StoreProjectData("mock02","examples.metadata","test-master")));
        when(projectsVersions.find("examples.metadata","test-master", "2.3.0")).thenReturn(Optional.of(versionData));

        entitiesService.getDependenciesEntities("examples.metadata", "test-master", "2.3.0", true, false);
        metricsHandler.persist(metricsRegistry);

        Assert.assertEquals(2, metricsStore.getAll().size());
        Assert.assertNotNull(metricsStore.get("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime());
        Assert.assertNotNull(metricsStore.get("examples.metadata", "test-master", "2.3.0").get(0).getLastQueryTime());
    }


    @Test
    public void canGetEntityByElementPath()
    {
        Response response = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile", null);
        Entity entity = ((Optional<Entity>) response.getEntity()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
    }

    @Test
    public void canGetEntityByPathWithVersion()
    {
        Response response = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile", null);
        Entity entity = ((Optional<Entity>) response.getEntity()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
    }

    @Test
    public void canGetEntitiesByPackage()
    {
        Response response = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", null, true, null);
        List<Entity> entityList = (List<Entity>) response.getEntity();
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());

    }
}

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

package org.finos.legend.depot.server;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.server.resources.entities.EntitiesResource;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.store.metrics.services.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.store.mongo.QueryMetricsMongo;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestQueryEntitiesResource extends TestBaseServices
{
    private UpdateProjects projects = mock(UpdateProjects.class);
    private UpdateProjectsVersions projectsVersions = mock(UpdateProjectsVersions.class);

    private final QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private final Queue queue = mock(Queue.class);
    private final EntitiesService entitiesService = new EntitiesServiceImpl(entitiesStore,new ProjectsServiceImpl(projectsVersions, projects, metricsRegistry, queue, new ProjectsConfiguration("master")));
    private EntitiesResource entitiesResource = new EntitiesResource(entitiesService);
    private QueryMetricsMongo metricsStore = new QueryMetricsMongo(mongoProvider);
    private QueryMetricsHandler metricsHandler = new QueryMetricsHandler(metricsStore, metricsRegistry);

    static
    {
        JerseyGuiceUtils.install((s, serviceLocator) -> null);
    }

    @Before
    public void setupMetadata()
    {
        super.setUpData();
        metricsStore.getCollection().drop();
        loadEntities("PROD-A", "2.3.0");
        loadEntities("PROD-A", BRANCH_SNAPSHOT("master"));
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
        List<Entity> entityList = entitiesResource.getEntities("examples.metadata", "test", "2.3.0");
        Assert.assertNotNull(entityList);
        Assert.assertEquals(7, entityList.size());
    }

    @Test
    public void canGetEntityByPathForProjectAndVersion()
    {
        Entity entity = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());

    }

    @Test
    public void canGetEntitiesByPackageForProjectAndVersion()
    {
        List<Entity> entityList = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", null, true);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());

    }


    @Test
    public void canGetMetrics() throws InterruptedException
    {
        Assert.assertTrue(metricsStore.getAllStoredEntities().isEmpty());
        Assert.assertEquals(0, metricsStore.get("examples.metadata", "test", "2.3.0").size());

        when(projects.find("examples.metadata","test")).thenReturn(Optional.of(new StoreProjectData("mock","examples.metadata","test")));
        entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", null, true);
        metricsHandler.persistMetrics();

        Assert.assertEquals(1, metricsStore.get("examples.metadata", "test", "2.3.0").size());
        Assert.assertNotNull(metricsStore.get("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime());
        TimeUnit.SECONDS.sleep(30);

        entitiesResource.getEntities("example.services.test", "test", "1.0.1");
        metricsHandler.persistMetrics();

        Assert.assertNotNull(metricsStore.get("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime());
        Assert.assertEquals(2, metricsStore.getAllStoredEntities().size());
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
        metricsHandler.persistMetrics();

        Assert.assertEquals(2, metricsStore.getAll().size());
        Assert.assertNotNull(metricsStore.get("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime());
        Assert.assertNotNull(metricsStore.get("examples.metadata", "test-master", "2.3.0").get(0).getLastQueryTime());
    }


    @Test
    public void canGetEntityByElementPath()
    {
        Entity entity = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
    }

    @Test
    public void canGetEntityByPathWithVersion()
    {
        Entity entity = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
    }

    @Test
    public void canGetEntitiesByPackage()
    {
        List<Entity> entityList = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", null, true);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());

    }
}

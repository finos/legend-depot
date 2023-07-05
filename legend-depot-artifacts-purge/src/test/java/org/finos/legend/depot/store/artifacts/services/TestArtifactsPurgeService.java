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

package org.finos.legend.depot.store.artifacts.services;

import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.domain.VersionMismatch;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.DatesHandler;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generation.file.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.artifacts.purge.api.ArtifactsPurgeService;
import org.finos.legend.depot.store.artifacts.purge.services.ArtifactsPurgeServiceImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.services.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.metrics.store.mongo.QueryMetricsMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.Collections;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestArtifactsPurgeService extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";

    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateProjectsVersions projectsVersionsStore = new ProjectsVersionsMongo(mongoProvider);
    private final QueryMetricsMongo metrics = new QueryMetricsMongo(mongoProvider);
    private final QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private final QueryMetricsHandler metricHandler = new QueryMetricsHandler(metrics, metricsRegistry);
    private final Queue queue = mock(Queue.class);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore, projectsStore, metricsRegistry, queue, new ProjectsConfiguration("master"));
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    protected UpdateFileGenerations fileGenerationsStore = new FileGenerationsMongo(mongoProvider);
    protected ArtifactRepository repository = mock(ArtifactRepository.class);
    protected RepositoryServices repositoryServices = new RepositoryServices(repository, projectsService);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, projectsService);
    protected ArtifactsPurgeService purgeService = new ArtifactsPurgeServiceImpl(projectsService, repositoryServices, metricHandler);



    @Before
    public void setUpData()
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, mock(EntityProvider.class)));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(mock(ArtifactRepository.class), mock(FileGenerationsProvider.class), new ManageFileGenerationsServiceImpl(fileGenerationsStore, entitiesStore, projectsService)));

        setUpProjectsFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/projects.json"));
        setUpProjectsVersionsFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/projectsVersions.json"));
        Assert.assertEquals(3, projectsStore.getAll().size());

        setUpEntitiesDataFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/entities.json"));
        setUpFileGenerationFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/generations.json"));
    }


    @Test
    public void canEvictOldVersions()
    {

        StoreProjectData project = projectsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).get();
        Assert.assertNotNull(project);
        List<String> projectVersions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertEquals(3, projectVersions.size());
        Assert.assertEquals("2.0.0", projectVersions.get(0));
        Assert.assertEquals("2.3.0", projectsService.getLatestVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID).get().toVersionIdString());

        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        purgeService.evictOldestProjectVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID,1);

        List<String> afterVersionsOrdered = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertEquals(3, afterVersionsOrdered.size());
        Assert.assertTrue(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get().isEvicted());
        Assert.assertTrue(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").get().isEvicted());
        Assert.assertTrue(afterVersionsOrdered.contains("2.3.0"));
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.0").size());
    }

    @Test
    public void canEvictOldVersionsKeepMoreThanExists()
    {

        StoreProjectData project = projectsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).get();
        Assert.assertNotNull(project);
        List<String> projectVersions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertEquals(3, projectVersions.size());
        MetadataEventResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID,5);
        Assert.assertNotNull(response);

        List<String> afterVersionsOrdered = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertEquals(3, afterVersionsOrdered.size());
        Assert.assertTrue(!projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get().isEvicted());
        Assert.assertTrue(!projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").get().isEvicted());
        Assert.assertTrue(!projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.0").get().isEvicted());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.0").size());
    }

    @Test
    public void canEvictOldVersionsNotEnoughVersionsToKeep()
    {

        StoreProjectData project = projectsStore.find(TEST_GROUP_ID,"test1").get();
        Assert.assertNotNull(project);
        List<String> projectVersions = projectsService.getVersions(TEST_GROUP_ID, "test1");
        Assert.assertEquals(0, projectVersions.size());
        Assert.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, "test1", BRANCH_SNAPSHOT("master")).size());

        MetadataEventResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID,"test1",1);
        Assert.assertNotNull(response);

        List<String> afterVersionsOrdered = projectsService.getVersions(TEST_GROUP_ID, "test1");
        Assert.assertEquals(0, afterVersionsOrdered.size());

        Assert.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, "test1", BRANCH_SNAPSHOT("master")).size());
    }

    @Test
    public void canEvictVersion()
    {
        String versionId = "2.0.0";

        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(3, fileGenerationsStore.getAll().size());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(0, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertTrue(projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID).contains(versionId));
        Assert.assertTrue(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).get().isEvicted());
    }

    @Test
    public void canDeleteVersion()
    {
        String versionId = "2.0.0";

        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(3, fileGenerationsStore.getAll().size());

        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(0, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertFalse(projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID).contains(versionId));
        Assert.assertFalse(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).isPresent());
    }

    @Test
    public void canDeprecateVersion()
    {
        String versionId = "2.0.0";
        List<String> versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        Assert.assertEquals(versions.size(), 3);
        Assert.assertEquals(1, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        //deprecating version
        purgeService.deprecate(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        //no versions deleted
        Assert.assertEquals(versions, Arrays.asList("2.0.0", "2.2.0", "2.3.0"));
        //no artifacts deleted
        Assert.assertEquals(1, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Optional<StoreProjectVersionData> storeProjectData = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertTrue(storeProjectData.get().getVersionData().isDeprecated());
    }

    @Test
    public void canDeprecateVersionIfNotInRepository()
    {
        String versionId = "2.0.0";
        VersionMismatch versionMismatch = new VersionMismatch("PROD-A", TEST_GROUP_ID, TEST_ARTIFACT_ID, Collections.EMPTY_LIST, Collections.singletonList(versionId), Collections.EMPTY_LIST);
        List<String> versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        Assert.assertEquals(versions.size(), 3);
        Assert.assertEquals(3, fileGenerationsStore.getAll().size());
        when(repositoryServices.findVersionsMismatches()).thenReturn(Collections.singletonList(versionMismatch));
        //deleting the version not present in the repository
        purgeService.deprecateVersionsNotInRepository();
        versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        Assert.assertEquals(versions.size(), 3);
        //checking if version data shows deprecation
        StoreProjectVersionData projectVersionData = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).get();
        Assert.assertTrue(projectVersionData.getVersionData().isDeprecated());
    }

    @Test
    public void canEvictLeastRecentlyUsed()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID,"branch1-SNAPSHOT"));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID,"2.0.0", DatesHandler.toDate(LocalDateTime.now().minusDays(366))));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT", DatesHandler.toDate(LocalDateTime.now().minusDays(30))));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT", new Date()));
        purgeService.evictLeastRecentlyUsed(365, 30);

        StoreProjectVersionData version1 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assert.assertTrue(version1.isEvicted());
        StoreProjectVersionData version2 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT").get();
        Assert.assertTrue(version2.isEvicted());
        StoreProjectVersionData version3 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT").get();
        Assert.assertFalse(version3.isEvicted());
    }

    @Test
    public void canEvictVersionsNotUsed()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID,"branch1-SNAPSHOT"));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT", new Date()));
        purgeService.evictVersionsNotUsed();

        StoreProjectVersionData version1 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assert.assertTrue(version1.isEvicted());
        StoreProjectVersionData version2 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT").get();
        Assert.assertTrue(version2.isEvicted());
        StoreProjectVersionData version3 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT").get();
        Assert.assertFalse(version3.isEvicted());
    }

}

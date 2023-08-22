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

package org.finos.legend.depot.services.artifacts.refresh;

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.dependencies.ManageDependenciesService;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.dependencies.ManageDependenciesServiceImpl;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generations.impl.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;

import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generations.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.artifacts.repository.maven.TestMavenArtifactsRepository;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntityProvider;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationsProvider;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.finos.legend.depot.store.notifications.services.NotificationsQueueManager;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.finos.legend.depot.services.artifacts.refresh.TestArtifactsRefreshServiceExceptionEscenarios.PARENT_EVENT_ID;
import static org.mockito.Mockito.mock;

public class TestArtifactsRefreshService extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_DEPENDENCIES_ARTIFACT_ID = "test-dependencies";
    public static final String PROJECT_A = "PROD-23992";
    public static final String PROJECT_B = "PROD-23993";
    protected ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(mongoProvider);
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateProjectsVersions projectsVersionsStore = new ProjectsVersionsMongo(mongoProvider);
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    protected Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore,projectsStore,metrics,queue,new ProjectsConfiguration("master"));
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected List<String> manifestProperties = Arrays.asList("commit-[a-zA-Z0-9]+", "release-[a-zA-Z0-9]+");
    protected UpdateFileGenerations fileGenerationsStore = new FileGenerationsMongo(mongoProvider);

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, projectsService);
    protected ArtifactRepository repository = new TestMavenArtifactsRepository();
    protected ManageDependenciesService manageDependenciesService = new ManageDependenciesServiceImpl(projectsService, repository);

    protected ProjectVersionRefreshHandler versionHandler = new ProjectVersionRefreshHandler(projectsService, repository, queue, artifacts, new IncludeProjectPropertiesConfiguration(properties, manifestProperties), manageDependenciesService, 10);

    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, repository, queue);

    protected NotificationsQueueManager notificationsQueueManager = new NotificationsQueueManager(new NotificationsMongo(mongoProvider),queue, versionHandler);


    @Before
    public void setUpData()
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new ManageFileGenerationsServiceImpl(fileGenerationsStore, entitiesStore, projectsService)));

        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID,"2.3.3"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")));

        projectsStore.createOrUpdate(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID));
        projectsStore.createOrUpdate(new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID));
        projectsStore.createOrUpdate(new StoreProjectData("PROD-101", TEST_GROUP_ID, "art101"));
        Assert.assertEquals(3, projectsStore.getAll().size());
    }

    @After
    public void afterTest()
    {
        Assert.assertTrue("should not have events in queue",queue.getAll().isEmpty());
    }

    @Test
    public void canUpdateCreateVersion()
    {

        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", BRANCH_SNAPSHOT("master")).isEmpty());
        Assert.assertEquals(0, projectsVersionsStore.find(TEST_GROUP_ID, "art101").size());
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        Assert.assertNotNull(files);
        Assert.assertEquals(1,files.size());
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101",BRANCH_SNAPSHOT("master"),true,true,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        notificationsQueueManager.handleAll();
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(0, projectsVersionsStore.find(TEST_GROUP_ID, "art101").stream().filter(x -> !x.getVersionId().equals(BRANCH_SNAPSHOT("master"))).collect(Collectors.toList()).size());
        List<Entity> entities = entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", BRANCH_SNAPSHOT("master"));
        Assert.assertNotNull(entities);
        Assert.assertEquals(9, entities.size());
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());
    }

    @Test
    public void canUpdateVersionForNewDLCProjectStyle()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101",BRANCH_SNAPSHOT("master"),false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void canRefreshArtifactWithProjectProperties()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"),false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Optional<StoreProjectVersionData> updatedProjectData = projectsVersionsStore.find(TEST_GROUP_ID, "art101", BRANCH_SNAPSHOT("master"));
        Assert.assertTrue(updatedProjectData.isPresent());
        Assert.assertEquals("0.0.0", updatedProjectData.get().getVersionData().getProperties().get(0).getValue());

        Map<String, String> manifestProperties = updatedProjectData.get().getVersionData().getManifestProperties();
        Assert.assertNotNull(manifestProperties);
        Assert.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assert.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");
    }

    @Test
    public void canUpdateCreateMasterRevision()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        Assert.assertNotNull(files);
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"),true,true,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();
        Assert.assertTrue(notificationsQueueManager.getAllInQueue().isEmpty());
        Assert.assertEquals(2,notificationsQueueManager.findProcessedEvents(null,null,null,null,PARENT_EVENT_ID,null,null,null).size());
    }


    @Test
    public void canRefreshProjectVersion()
    {
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3").size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0",true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        Assert.assertEquals(12, fileGenerationsStore.getAll().size());
        Assert.assertEquals(3, fileGenerationsStore.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "examples::avrogen").size());
        Assert.assertEquals(2, fileGenerationsStore.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "examples::metadata::test::ClientBasic").size());
        Assert.assertTrue(fileGenerationsStore.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").isPresent());
        Assert.assertEquals("My Output1",  fileGenerationsStore.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").get().getFile().getContent());
    }

    @Test
    public void canRefreshExcludedProjectVersionIfLoadable()
    {
        StoreProjectVersionData storeProjectVersionData = projectsService.excludeProjectVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "unknown error");
        Assert.assertTrue(storeProjectVersionData.getVersionData().isExcluded());
        Assert.assertEquals(storeProjectVersionData.getVersionData().getDependencies().size(), 0);

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0",false,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        storeProjectVersionData = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assert.assertFalse(storeProjectVersionData.getVersionData().isExcluded());
        Assert.assertNull(storeProjectVersionData.getVersionData().getExclusionReason());
        Assert.assertEquals(storeProjectVersionData.getVersionData().getDependencies().size(), 1);

        Map<String, String> manifestProperties = storeProjectVersionData.getVersionData().getManifestProperties();
        Assert.assertNotNull(manifestProperties);
        Assert.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assert.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");
    }

    @Test
    public void canRefreshEvictedProjectVersion()
    {
        StoreProjectVersionData storeProjectVersionData = new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        storeProjectVersionData.setEvicted(true);
        projectsService.createOrUpdate(storeProjectVersionData);
        Assert.assertTrue(storeProjectVersionData.isEvicted());

        Assert.assertThrows("Project version: examples.metadata-test-2.0.0 is being restored, please retry in 5 minutes", IllegalStateException.class, () -> projectsService.resolveAliasesAndCheckVersionExists(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0"));
        Assert.assertEquals(1, notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        storeProjectVersionData = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assert.assertFalse(storeProjectVersionData.isEvicted());

    }

    @Test
    public void canRefreshAllVersionForProject()
    {
        Assert.assertTrue(entitiesStore.getAllStoredEntities().isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").isEmpty());
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(3,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertEquals(3,projectsStore.getAll().size());
        Assert.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

    }

    @Test
    public void canRefreshOnlyDefaultSnapshotVersions()
    {
        MetadataEventResponse response = artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(false, false, PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT"));

        response = artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(false, false, PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();
    }

    @Test
    public void canRefreshAllVersionExceptForEvictedSnapshot()
    {
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(3,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        StoreProjectVersionData pv = new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "dummy-SNAPSHOT");
        pv.setEvicted(true);
        projectsService.createOrUpdate(pv);

        response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(3,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();
    }

    @Test
    public void canRefreshAllVersionForProjectWithExcludedVersion()
    {
        Assert.assertTrue(entitiesStore.getAllStoredEntities().isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").isEmpty());

        projectsService.excludeProjectVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0","test");
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false, false,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());

        Assert.assertEquals(BRANCH_SNAPSHOT("master"), notificationsQueueManager.getAllInQueue().get(0).getVersionId());
        notificationsQueueManager.handleAll();

    }

    @Test
    public void partialRefreshAllVersionForProjectOnlyRefreshLatest()
    {
        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true, true,PARENT_EVENT_ID);
        Assert.assertEquals(3,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        List<String> versions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertEquals(3, versions.size());
        Assert.assertEquals("2.3.3",projectsService.getLatestVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID).get().toVersionIdString());


        projectsService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        Assert.assertEquals("2.3.3",projectsService.getLatestVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().toVersionIdString());

        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false, false, false,PARENT_EVENT_ID);
        Assert.assertEquals("2.3.3",projectsService.getLatestVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().toVersionIdString());
        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size());
        Assert.assertEquals("1.0.0",notificationsQueueManager.getAllInQueue().get(1).getVersionId());
        notificationsQueueManager.handleAll();

        projectsService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false, false, false,PARENT_EVENT_ID);
        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size());
        Assert.assertEquals("2.0.0",notificationsQueueManager.getAllInQueue().get(1).getVersionId());
        notificationsQueueManager.handleAll();

    }

    @Test
    public void canRefreshAllVersionsAllProjects()
    {
        Assert.assertEquals(3,projectsService.getAllProjectCoordinates().size());
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForAllProjects(true,true,true,PARENT_EVENT_ID);
        Assert.assertEquals(5,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        List<Entity> entityList2 = entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        Assert.assertNotNull(entityList2);
        Assert.assertEquals(9, entityList2.size());

        MetadataEventResponse partialUpdate = artifactsRefreshService.refreshAllVersionsForAllProjects(false,false,true,PARENT_EVENT_ID);
        Assert.assertEquals(3,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.getAllInQueue().stream().allMatch(notification -> BRANCH_SNAPSHOT("master").equals(notification.getVersionId()));
        notificationsQueueManager.handleAll();
    }

    @Test
    public void canRefreshProjectMasterVersion()
    {

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"),true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

        Assert.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        ProjectVersionData projectVersionData = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).get().getVersionData();
        List<ProjectVersion> dependencies = projectVersionData.getDependencies();
        Assert.assertEquals(2, dependencies.size());

        Map<String, String> manifestProperties = projectVersionData.getManifestProperties();
        Assert.assertNotNull(manifestProperties);
        Assert.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assert.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");

    }


    @Test
    public void canRefreshProjectMasterVersionWithAllDependenciesTransitively()
    {

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", "2.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"),true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handle();
        List<ProjectVersion> dependencies = projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).get().getVersionData().getDependencies();
        Assert.assertEquals(2, dependencies.size());

        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());

    }

    @Test
    public void dependenciesAreLoadedCorrectly()
    {
        Assert.assertEquals(1, repository.findDependenciesByArtifactType(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0").size());
        Assert.assertEquals(0, entitiesStore.getStoredEntities(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID).size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0").size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").size());

        artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertEquals(1,projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,"2.0.0").get().getVersionData().getDependencies().size());
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());
        Assert.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        Assert.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
    }


    @Test
    public void canLoadEntitiesWithVersionInPackageName()
    {
        MetadataEventResponse partialUpdate = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),false,false,PARENT_EVENT_ID));
        Assert.assertNotNull(partialUpdate);
        Assert.assertEquals(2, entitiesStore.getStoredEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

        MetadataEventResponse fullUpdate = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),true,false,PARENT_EVENT_ID));
        Assert.assertNotNull(fullUpdate);

        Assert.assertEquals(2, entitiesStore.getStoredEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

    }



    @Test
    public void canRefreshExistingVersion()
    {
        String versionId = "2.0.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
    }



    @Test(expected = IllegalArgumentException.class)
    public void cantRefreshNonExistingProjectInRepo()
    {
        String versionId = "2.111.0";
        artifactsRefreshService.refreshVersionForProject("i.dont.exist", "in-repo", versionId,false,PARENT_EVENT_ID);
    }

    @Test
    public void cantRefreshSameVersionTwice()
    {
        String versionId = "2.0.0";
        Assert.assertFalse(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        Assert.assertFalse(artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,PARENT_EVENT_ID).hasErrors());
        notificationsQueueManager.handleAll();
        Assert.assertTrue(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        int docsBefore = this.entitiesStore.getAllStoredEntities().size();
        Assert.assertFalse(artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,PARENT_EVENT_ID).hasErrors());
        notificationsQueueManager.handleAll();
        Assert.assertEquals(docsBefore,entitiesStore.getAllStoredEntities().size());
        Assert.assertTrue(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        Assert.assertEquals(3,projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).stream().filter(x -> !x.getVersionId().equals(BRANCH_SNAPSHOT("master"))).collect(Collectors.toList()).size());
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());
    }





}

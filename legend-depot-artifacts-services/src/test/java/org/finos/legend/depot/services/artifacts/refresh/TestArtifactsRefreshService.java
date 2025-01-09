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

import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotificationStatus;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.dependencies.DependencyUtil;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generations.impl.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.notifications.NotificationsQueueManager;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;

import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generations.UpdateFileGenerations;
import org.finos.legend.depot.store.api.notifications.Notifications;
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
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.notifications.NotificationsMongo;
import org.finos.legend.depot.store.mongo.notifications.queue.NotificationsQueueMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.services.api.notifications.queue.Queue;

import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    protected RefreshDependenciesService refreshDependenciesService = new RefreshDependenciesServiceImpl(projectsService, repository, new DependencyUtil());

    protected ProjectVersionRefreshHandler versionHandler = new ProjectVersionRefreshHandler(projectsService, repository, queue, artifacts, new IncludeProjectPropertiesConfiguration(properties, manifestProperties), refreshDependenciesService, 10);

    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, repository, queue);
    protected Notifications notifications = new NotificationsMongo(mongoProvider);
    protected NotificationsQueueManager notificationsQueueManager = new NotificationsQueueManager(notifications,queue, versionHandler);


    @BeforeEach
    public void setUpData()
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new ManageFileGenerationsServiceImpl(fileGenerationsStore, projectsService)));

        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")));

        projectsStore.createOrUpdate(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID));
        projectsStore.createOrUpdate(new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID));
        projectsStore.createOrUpdate(new StoreProjectData("PROD-101", TEST_GROUP_ID, "art101"));
        Assertions.assertEquals(3, projectsStore.getAll().size());
    }

    @AfterEach
    public void afterTest()
    {
        Assertions.assertTrue(queue.getAll().isEmpty(), "should not have events in queue");
    }

    @Test
    public void canUpdateCreateVersion()
    {

        Assertions.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", BRANCH_SNAPSHOT("master")).isEmpty());
        Assertions.assertEquals(0, projectsVersionsStore.find(TEST_GROUP_ID, "art101").size());
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1,files.size());
        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101",BRANCH_SNAPSHOT("master"),true,true,PARENT_EVENT_ID);
        Assertions.assertNotNull(response);
        notificationsQueueManager.handleAll();
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(0, projectsVersionsStore.find(TEST_GROUP_ID, "art101").stream().filter(x -> !x.getVersionId().equals(BRANCH_SNAPSHOT("master"))).collect(Collectors.toList()).size());
        List<Entity> entities = entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", BRANCH_SNAPSHOT("master"));
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(9, entities.size());
        Assertions.assertEquals(0,queue.size());
    }

    @Test
    public void canUpdateVersionForNewDLCProjectStyle()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101",BRANCH_SNAPSHOT("master"),false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void canRefreshArtifactWithProjectProperties()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"),false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Optional<StoreProjectVersionData> updatedProjectData = projectsVersionsStore.find(TEST_GROUP_ID, "art101", BRANCH_SNAPSHOT("master"));
        Assertions.assertTrue(updatedProjectData.isPresent());
        Assertions.assertEquals("0.0.0", updatedProjectData.get().getVersionData().getProperties().get(0).getValue());

        Map<String, String> manifestProperties = updatedProjectData.get().getVersionData().getManifestProperties();
        Assertions.assertNotNull(manifestProperties);
        Assertions.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assertions.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");
    }

    @Test
    public void canUpdateCreateMasterRevision()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"));
        Assertions.assertNotNull(files);
        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(projectData.getGroupId(), projectData.getArtifactId(), BRANCH_SNAPSHOT("master"),true,true,PARENT_EVENT_ID);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(1,queue.size());
        notificationsQueueManager.handleAll();
        Assertions.assertTrue(queue.getAll().isEmpty());
        Assertions.assertEquals(2,notifications.find(null,null,null,null,PARENT_EVENT_ID,null,null,null).size());
    }


    @Test
    public void canRefreshProjectVersion()
    {
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0",true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(1,queue.size());
        notificationsQueueManager.handleAll();

        Assertions.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assertions.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        Assertions.assertEquals(12, fileGenerationsStore.getAll().size());
        Assertions.assertEquals(3, fileGenerationsStore.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "examples::avrogen").size());
        Assertions.assertEquals(2, fileGenerationsStore.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "examples::metadata::test::ClientBasic").size());
        Assertions.assertTrue(fileGenerationsStore.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").isPresent());
        Assertions.assertEquals("My Output1",  fileGenerationsStore.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").get().getFile().getContent());
    }

    @Test
    public void canRefreshExcludedProjectVersionIfLoadable()
    {
        StoreProjectVersionData storeProjectVersionData = projectsService.excludeProjectVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "unknown error");
        Assertions.assertTrue(storeProjectVersionData.getVersionData().isExcluded());
        Assertions.assertEquals(storeProjectVersionData.getVersionData().getDependencies().size(), 0);

        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0",false,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(1,queue.size());
        notificationsQueueManager.handleAll();

        storeProjectVersionData = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assertions.assertFalse(storeProjectVersionData.getVersionData().isExcluded());
        Assertions.assertNull(storeProjectVersionData.getVersionData().getExclusionReason());
        Assertions.assertEquals(storeProjectVersionData.getVersionData().getDependencies().size(), 1);

        Map<String, String> manifestProperties = storeProjectVersionData.getVersionData().getManifestProperties();
        Assertions.assertNotNull(manifestProperties);
        Assertions.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assertions.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");
    }

    @Test
    public void canRefreshEvictedProjectVersion()
    {
        StoreProjectVersionData storeProjectVersionData = new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        storeProjectVersionData.setEvicted(true);
        projectsService.createOrUpdate(storeProjectVersionData);
        Assertions.assertTrue(storeProjectVersionData.isEvicted());

        Assertions.assertThrows(IllegalStateException.class, () -> projectsService.resolveAliasesAndCheckVersionExists(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0"), "Project version: examples.metadata-test-2.0.0 is being restored, please retry in 5 minutes");
        Assertions.assertEquals(1, queue.size());
        notificationsQueueManager.handleAll();

        storeProjectVersionData = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assertions.assertFalse(storeProjectVersionData.isEvicted());

    }

    @Test
    public void canRefreshAllVersionForProject()
    {
        Assertions.assertTrue(entitiesStore.getAllStoredEntities().isEmpty());
        Assertions.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").isEmpty());
        Assertions.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").isEmpty());
        MetadataNotificationResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true,true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());

        Assertions.assertEquals(3,queue.size());
        notificationsQueueManager.handleAll();

        Assertions.assertEquals(3,projectsStore.getAll().size());
        Assertions.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").size());
        Assertions.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assertions.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

    }

    @Test
    public void canRefreshOnlyDefaultSnapshotVersions()
    {
        MetadataNotificationResponse response = artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(false, false, PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(2,queue.size());
        notificationsQueueManager.handleAll();

        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT"));

        response = artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(false, false, PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(2,queue.size());
        notificationsQueueManager.handleAll();
    }

    @Test
    public void canRefreshAllVersionExceptForEvictedSnapshot()
    {
        MetadataNotificationResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true,true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());

        Assertions.assertEquals(3,queue.size());
        notificationsQueueManager.handleAll();

        StoreProjectVersionData pv = new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "dummy-SNAPSHOT");
        pv.setEvicted(true);
        projectsService.createOrUpdate(pv);

        response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true,true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());

        Assertions.assertEquals(3,queue.size());
        notificationsQueueManager.handleAll();
    }

    @Test
    public void canRefreshAllVersionForProjectWithExcludedVersion()
    {
        Assertions.assertTrue(entitiesStore.getAllStoredEntities().isEmpty());
        Assertions.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").isEmpty());
        Assertions.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").isEmpty());

        projectsService.excludeProjectVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0","test");
        MetadataNotificationResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false, false,true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());

        Assertions.assertEquals(1,queue.size());

        Assertions.assertEquals(BRANCH_SNAPSHOT("master"), queue.getAll().get(0).getVersionId());
        notificationsQueueManager.handleAll();

    }

    @Test
    public void partialRefreshAllVersionForProjectOnlyRefreshLatest()
    {
        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true, true, true,PARENT_EVENT_ID);
        Assertions.assertEquals(3,queue.size());
        notificationsQueueManager.handleAll();

        List<String> versions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals("2.0.0",projectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID).get().getLatestVersion());


        projectsService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        Assertions.assertEquals("2.0.0",projectsService.findCoordinates(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().getLatestVersion());

        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false, false, false,PARENT_EVENT_ID);
        Assertions.assertEquals("2.0.0",projectsService.findCoordinates(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().getLatestVersion());
        Assertions.assertEquals(2,queue.size());
        Assertions.assertEquals("1.0.0",queue.getAll().get(1).getVersionId());
        notificationsQueueManager.handleAll();

        projectsService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false, false, false,PARENT_EVENT_ID);
        Assertions.assertEquals(2,queue.size());
        Assertions.assertEquals("2.0.0",queue.getAll().get(1).getVersionId());
        notificationsQueueManager.handleAll();

    }

    @Test
    public void canRefreshAllVersionsAllProjects()
    {
        Assertions.assertEquals(3,projectsService.getAllProjectCoordinates().size());
        MetadataNotificationResponse response = artifactsRefreshService.refreshAllVersionsForAllProjects(true,true,true,PARENT_EVENT_ID);
        Assertions.assertEquals(5,queue.size());
        notificationsQueueManager.handleAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());

        List<Entity> entityList2 = entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        Assertions.assertNotNull(entityList2);
        Assertions.assertEquals(9, entityList2.size());

        MetadataNotificationResponse partialUpdate = artifactsRefreshService.refreshAllVersionsForAllProjects(false,false,true,PARENT_EVENT_ID);
        Assertions.assertEquals(3,queue.size());
        queue.getAll().stream().allMatch(notification -> BRANCH_SNAPSHOT("master").equals(notification.getVersionId()));
        notificationsQueueManager.handleAll();
    }

    @Test
    public void canRefreshProjectMasterVersion()
    {

        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"),true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(1,queue.size());
        notificationsQueueManager.handleAll();

        Assertions.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

        Assertions.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        ProjectVersionData projectVersionData = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).get().getVersionData();
        List<ProjectVersion> dependencies = projectVersionData.getDependencies();
        Assertions.assertEquals(2, dependencies.size());

        Map<String, String> manifestProperties = projectVersionData.getManifestProperties();
        Assertions.assertNotNull(manifestProperties);
        Assertions.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assertions.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");

    }


    @Test
    public void canRefreshProjectMasterVersionWithAllDependenciesTransitively()
    {

        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", "2.0.0").size());

        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"),true,PARENT_EVENT_ID);
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(1,queue.size());
        notificationsQueueManager.handle();
        List<ProjectVersion> dependencies = projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).get().getVersionData().getDependencies();
        Assertions.assertEquals(2, dependencies.size());

        Assertions.assertEquals(2,queue.size());
        notificationsQueueManager.handleAll();
        Assertions.assertEquals(0,queue.size());

    }

    @Test
    public void dependenciesAreLoadedCorrectly()
    {
        Assertions.assertEquals(1, repository.findDependenciesByArtifactType(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0").size());
        Assertions.assertEquals(0, entitiesStore.getStoredEntities(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID).size());
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0").size());
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").size());

        artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assertions.assertEquals(1,projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,"2.0.0").get().getVersionData().getDependencies().size());
        Assertions.assertEquals(0,queue.size());
        Assertions.assertEquals(9, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        Assertions.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
    }


    @Test
    public void canLoadEntitiesWithVersionInPackageName()
    {
        MetadataNotificationResponse partialUpdate = versionHandler.handleNotification(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),false,false,PARENT_EVENT_ID));
        Assertions.assertNotNull(partialUpdate);
        Assertions.assertEquals(2, entitiesStore.getStoredEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

        MetadataNotificationResponse fullUpdate = versionHandler.handleNotification(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),true,false,PARENT_EVENT_ID));
        Assertions.assertNotNull(fullUpdate);

        Assertions.assertEquals(2, entitiesStore.getStoredEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).size());

    }



    @Test
    public void canRefreshExistingVersion()
    {
        String versionId = "2.0.0";
        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }



    @Test
    public void cantRefreshNonExistingProjectInRepo()
    {
        String versionId = "2.111.0";
        Assertions.assertThrows(IllegalArgumentException.class, () -> artifactsRefreshService.refreshVersionForProject("i.dont.exist", "in-repo", versionId,false,PARENT_EVENT_ID));
    }

    @Test
    public void cantRefreshSameVersionTwice()
    {
        String versionId = "2.0.0";
        Assertions.assertFalse(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        Assertions.assertFalse(artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,PARENT_EVENT_ID).hasErrors());
        notificationsQueueManager.handleAll();
        Assertions.assertTrue(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        int docsBefore = this.entitiesStore.getAllStoredEntities().size();
        Assertions.assertFalse(artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,PARENT_EVENT_ID).hasErrors());
        notificationsQueueManager.handleAll();
        Assertions.assertEquals(docsBefore,entitiesStore.getAllStoredEntities().size());
        Assertions.assertTrue(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        Assertions.assertEquals(2,projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).stream().filter(x -> !x.getVersionId().equals(BRANCH_SNAPSHOT("master"))).collect(Collectors.toList()).size());
        Assertions.assertEquals(0,queue.size());
    }





}

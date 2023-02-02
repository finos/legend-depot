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
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.maven.impl.TestMavenArtifactsRepository;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generation.file.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsRefreshStatusMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.services.NotificationsQueueManager;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsQueueMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceExceptionEscenarios.PARENT_EVENT_ID;

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
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore,projectsStore);
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected UpdateFileGenerations fileGenerationsStore = new FileGenerationsMongo(mongoProvider);
    protected RefreshStatusStore refreshStatusStore = new ArtifactsRefreshStatusMongo(mongoProvider);

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected VersionedEntityProvider versionedEntityProvider = new VersionedEntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, projectsService);
    protected ArtifactRepository repository = new TestMavenArtifactsRepository();
    protected RepositoryServices repositoryServices = new RepositoryServices(repository,projectsService);
    protected Queue queue = new NotificationsQueueMongo(mongoProvider);

    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, refreshStatusStore, repositoryServices, artifacts, queue, new IncludeProjectPropertiesConfiguration(properties));
    protected NotificationEventHandler eventHandler = new ArtifactRefreshEventHandler(projectsService,artifactsRefreshService);

    protected NotificationsQueueManager notificationsQueueManager = new NotificationsQueueManager(projectsService,new NotificationsMongo(mongoProvider),queue,eventHandler);


    @Before
    public void setUpData()
    {
        ArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new ManageFileGenerationsServiceImpl(fileGenerationsStore, entitiesStore, projectsService)));
        ArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, new VersionedEntitiesHandlerImpl(new ManageEntitiesServiceImpl(entitiesStore, projectsService), versionedEntityProvider));

        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID,"2.3.3"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT));

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

        Assert.assertTrue(entitiesStore.getEntities(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, false).isEmpty());
        Assert.assertEquals(0, projectsVersionsStore.getVersions(TEST_GROUP_ID, "art101").size());
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        Assert.assertNotNull(files);
        Assert.assertEquals(1,files.size());
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, true,true,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertTrue(refreshStatusStore.find(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, true).isEmpty());
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(0, projectsVersionsStore.getVersions(TEST_GROUP_ID, "art101").size());
        List<Entity> entities = entitiesStore.getEntities(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, false);
        Assert.assertNotNull(entities);
        Assert.assertEquals(9, entities.size());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();


    }

    @Test
    public void canUpdateVersionForNewDLCProjectStyle()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT,false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void canRefreshArtifactWithProjectProperties()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT, false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Optional<StoreProjectVersionData> updatedProjectData = projectsVersionsStore.find(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT);
        Assert.assertTrue(updatedProjectData.isPresent());
        Assert.assertEquals("0.0.0", updatedProjectData.get().getVersionData().getProperties().get(0).getValue());
    }

    @Test
    public void canUpdateCreateMasterRevision()
    {
        StoreProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        Assert.assertNotNull(files);
        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForProject(projectData.getGroupId(), projectData.getArtifactId(), true,true,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertFalse(artifactsRefreshService.refreshMasterSnapshotForProject(projectData.getGroupId(), projectData.getArtifactId(), true,false,PARENT_EVENT_ID).hasErrors());
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());

    }


    @Test
    public void canRefreshProjectVersion()
    {
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3").size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0",true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        Assert.assertEquals(12, fileGenerationsStore.getAll().size());
        Assert.assertEquals(3, fileGenerationsStore.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "examples::avrogen").size());
        Assert.assertEquals(2, fileGenerationsStore.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "examples::metadata::test::ClientBasic").size());
        Assert.assertTrue(fileGenerationsStore.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").isPresent());
        Assert.assertEquals("My Output1",  fileGenerationsStore.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").get().getFile().getContent());

        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(2,statuses.size());
    }

    @Test
    public void canRefreshAllVersionForProject()
    {
        Assert.assertTrue(entitiesStore.getAllStoredEntities().isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").isEmpty());
        refreshStatusStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,"ALL",true);
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(4,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertEquals(3,projectsStore.getAll().size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(6,statuses.size());

    }

    @Test
    public void partialRefreshAllVersionForProjectOnlyRefreshLatest()
    {
        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(4,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        List<String> versions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertEquals(3, versions.size());
        Assert.assertEquals("2.3.3",projectsService.getLatestVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID).get().toVersionIdString());


        projectsService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        Assert.assertEquals("2.3.3",projectsService.getLatestVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().toVersionIdString());

        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false,false,PARENT_EVENT_ID);
        Assert.assertEquals("2.3.3",projectsService.getLatestVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().toVersionIdString());
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());

        projectsService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, false,false,PARENT_EVENT_ID);
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

    }

    @Test
    public void canRefreshAllVersionsAllProjects()
    {
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForAllProjects(true,true,PARENT_EVENT_ID);
        Assert.assertEquals(3,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        List<Entity> entityList2 = entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        Assert.assertNotNull(entityList2);
        Assert.assertEquals(18, entityList2.size());

        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(4,statuses.size());

        MetadataEventResponse partialUpdate = artifactsRefreshService.refreshAllVersionsForAllProjects(false,true,PARENT_EVENT_ID);
        Assert.assertEquals(0,notificationsQueueManager.getAllInQueue().size());

    }

    @Test
    public void canRefreshProjectMasterVersion()
    {

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size()); //dependencies already present
        notificationsQueueManager.handleAll();

        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).size());

        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        List<ProjectVersion> dependencies = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).get().getVersionData().getDependencies();
        Assert.assertEquals(2, dependencies.size());
        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(3,statuses.size());

    }


    @Test
    public void canRefreshProjectMasterVersionWithAllDependenciesTransitively()
    {

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", "2.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true,true,PARENT_EVENT_ID);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        List<ProjectVersion> dependencies = projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID, MASTER_SNAPSHOT).get().getVersionData().getDependencies();
        Assert.assertEquals(2, dependencies.size());
        Assert.assertEquals(2,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

    }


    @Test
    public void canRefreshRevisionsAllProjects()
    {
       Assert.assertTrue(refreshStatusStore.getAll().isEmpty());

        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForAllProjects(true,true,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(3,queue.getAll().size());
        notificationsQueueManager.handleAll();

        List<StoredEntity> all = entitiesStore.getAllStoredEntities();
        Assert.assertNotNull(all);
        Assert.assertEquals(42, all.size());

        List<Entity> entityList = entitiesStore.getAllLatestEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(18, entityList.size());

        List<Entity> entityList2 = entitiesStore.getAllLatestEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID);
        Assert.assertNotNull(entityList2);
        Assert.assertEquals(4, entityList2.size());
        Assert.assertEquals(4, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT).size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(1, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0", false).size());


        List<ProjectVersion> dependencies = projectsVersionsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).get().getVersionData().getDependencies();
        Assert.assertFalse(dependencies.isEmpty());
        Assert.assertEquals(2, dependencies.size());

        List<RefreshStatus> refreshStatuses = refreshStatusStore.getAll();
       projectsService.getDependencies(TEST_GROUP_ID,TEST_ARTIFACT_ID,MASTER_SNAPSHOT,true);
        Assert.assertEquals(5,refreshStatuses.size());

    }

    @Test
    public void dependenciesAreLoadedCorrectly()
    {
        Assert.assertEquals(1, repository.findDependenciesByArtifactType(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0").size());
        Assert.assertEquals(0, entitiesStore.getStoredEntities(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID).size());
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0", true).size());
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", true).size());

        artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", true,true,PARENT_EVENT_ID);
        Assert.assertEquals(1,projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,"2.0.0").get().getVersionData().getDependencies().size());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

        Assert.assertEquals(9, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", true).size());
        Assert.assertEquals(1, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0", true).size());
    }


    @Test
    public void cantRefreshANonExistingVersion()
    {
        String versionId = "4.0.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false,false,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());
    }


    @Test
    public void canRefreshExistingVersion()
    {
        String versionId = "2.0.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false,true,PARENT_EVENT_ID);
        notificationsQueueManager.handleAll();
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
    }

    @Test
    public void cantRefreshNonExistingVersionInRepo()
    {
        String versionId = "2.111.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false,false,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());
    }

    @Test
    public void cantRefreshNonExistingProjectInRepo()
    {
        String versionId = "2.111.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject("i.dont.exist", "in-repo", versionId,false,false,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());
    }

    @Test
    public void cantRefreshSameVersionTwice()
    {
        String versionId = "2.0.0";
        Assert.assertFalse(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        Assert.assertFalse(artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,true,PARENT_EVENT_ID).hasErrors());
        Assert.assertTrue(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        int docsBefore = this.entitiesStore.getAllStoredEntities().size();
        Assert.assertFalse(artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true,true,PARENT_EVENT_ID).hasErrors());
        Assert.assertEquals(docsBefore,entitiesStore.getAllStoredEntities().size());
        Assert.assertTrue(projectsVersionsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId).isPresent());
        Assert.assertEquals(3,projectsVersionsStore.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID).size());
        Assert.assertEquals(1,notificationsQueueManager.getAllInQueue().size());
        notificationsQueueManager.handleAll();

    }

    @Test
    public void canLoadEntitiesWithVersionInPackageName()
    {
        MetadataEventResponse partialUpdate = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT,false,false,PARENT_EVENT_ID);
        Assert.assertNotNull(partialUpdate);

        MetadataEventResponse fullUpdate = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT,true,false,PARENT_EVENT_ID);
        Assert.assertNotNull(fullUpdate);

        Assert.assertEquals(4, entitiesStore.getStoredEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT).size());
        Assert.assertEquals(2, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT,false).size());
        Assert.assertEquals(2, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT,true).size());

    }

    @Test
    public void cantRefreshAVersionProjectHasNoVersionsInRepo() throws ArtifactRepositoryException
    {
        projectsStore.createOrUpdate(new StoreProjectData("PROD-1011", TEST_GROUP_ID, "art1011"));
        String versionId = "4.0.0";
        List<VersionId> versionsInRepo = repository.findVersions(TEST_GROUP_ID, "art1011");
        Assert.assertNotNull(versionsInRepo);
        Assert.assertTrue(versionsInRepo.isEmpty());
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art1011", versionId,false,false,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(),MetadataEventStatus.FAILED);
    }

    @Test
    public void canDeleteOldStatuses()
    {
        RefreshStatus status1 = new RefreshStatus("test","artifact","2.0.0");
        RefreshStatus status2 = new RefreshStatus("test","artifact","1.0.0");
        status1.setStartTime(Date.from(LocalDateTime.now().minusDays(12).atZone(ZoneId.systemDefault()).toInstant()));
        status2.setStartTime(Date.from(LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()).toInstant()));
        refreshStatusStore.createOrUpdate(status1);
        refreshStatusStore.createOrUpdate(status2);
        Assert.assertEquals(2, refreshStatusStore.getAll().size());
        artifactsRefreshService.deleteOldRefreshStatuses(10);
        Assert.assertEquals(1, refreshStatusStore.getAll().size());
        artifactsRefreshService.deleteOldRefreshStatuses(1);
        Assert.assertEquals(0, refreshStatusStore.getAll().size());


    }

}

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
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.generation.file.FileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.artifacts.store.mongo.ArtifactsMongo;
import org.finos.legend.depot.store.artifacts.store.mongo.MongoRefreshStatus;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;


public class TestArtifactsRefreshService extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_DEPENDENCIES_ARTIFACT_ID = "test-dependencies";
    public static final String PROJECT_A = "PROD-23992";
    public static final String PROJECT_B = "PROD-23993";
    protected UpdateArtifacts artifacts = new ArtifactsMongo(mongoProvider);
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ProjectsServiceImpl(projectsStore);
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected UpdateFileGenerations fileGenerationsStore = new FileGenerationsMongo(mongoProvider);
    protected ManageRefreshStatusService refreshStatusStore = new MongoRefreshStatus(mongoProvider);

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected VersionedEntityProvider versionedEntityProvider = new VersionedEntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();

    protected ArtifactRepository repository = new TestMavenArtifactsRepository();
    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, refreshStatusStore, repository, artifacts, new IncludeProjectPropertiesConfiguration(properties));

    protected ManageEntitiesService entitiesService = new EntitiesServiceImpl(entitiesStore, projectsService);

    @Before
    public void setUpData()
    {
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new FileGenerationsServiceImpl(fileGenerationsStore, entitiesStore)));
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, new VersionedEntitiesHandlerImpl(new EntitiesServiceImpl(entitiesStore, projectsService), versionedEntityProvider));

        projectsStore.createOrUpdate(new ProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID));
        projectsStore.createOrUpdate(new ProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID));
        projectsStore.createOrUpdate(new ProjectData("PROD-101", TEST_GROUP_ID, "art101"));
        Assert.assertEquals(3, projectsStore.getAll().size());
    }

    @Test
    public void canUpdateCreateVersion()
    {

        Assert.assertTrue(entitiesStore.getEntities(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, false).isEmpty());
        Assert.assertEquals(0, projectsStore.getVersions(TEST_GROUP_ID, "art101").size());
        ProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        Assert.assertNotNull(files);
        Assert.assertEquals(1,files.size());
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, true);
        Assert.assertNotNull(response);
        Assert.assertTrue(refreshStatusStore.find(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, true).isEmpty());
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        Assert.assertEquals(0, projectsStore.getVersions(TEST_GROUP_ID, "art101").size());
        List<Entity> entities = entitiesStore.getEntities(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT, false);
        Assert.assertNotNull(entities);
        Assert.assertEquals(9, entities.size());

    }

    @Test
    public void canUpdateVersionForNewDLCProjectStyle()
    {
        ProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art101", MASTER_SNAPSHOT);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void canRefreshArtifactWithProjectProperties()
    {
        ProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT, false);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
        ProjectData updatedProjectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        Assert.assertEquals(1, updatedProjectData.getPropertiesForProjectVersionID(MASTER_SNAPSHOT).size());
        Assert.assertEquals("0.0.0", updatedProjectData.getPropertiesForProjectVersionID(MASTER_SNAPSHOT).get(0).getValue());
    }

    @Test
    public void canUpdateCreateMasterRevision()
    {
        ProjectData projectData = projectsStore.find(TEST_GROUP_ID, "art101").get();
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, projectData.getGroupId(), projectData.getArtifactId(), MASTER_SNAPSHOT);
        Assert.assertNotNull(files);
        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForProject(projectData.getGroupId(), projectData.getArtifactId(), true);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
    }


    @Test
    public void canRefreshProjectVersion()
    {

        Assert.assertEquals(0, projectsStore.findByProjectId(PROJECT_A).get(0).getVersions().size());
        Assert.assertEquals(0, projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies().size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3").size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_A).get(0).getVersions().size());
        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies().size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_B).get(0).getVersions().size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(0, projectsStore.findByProjectId(PROJECT_B).get(0).getDependencies().size());

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
        ProjectData projectData = projectsStore.findByProjectId(PROJECT_A).get(0);
        Assert.assertTrue(entitiesStore.getAllStoredEntities().isEmpty());
        projectData.setVersions(Collections.emptyList());
        projectsStore.createOrUpdate(projectData);
        Assert.assertTrue(projectsStore.findByProjectId(PROJECT_A).get(0).getVersions().isEmpty());
        Assert.assertTrue(projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies().isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").isEmpty());
        Assert.assertTrue(entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").isEmpty());
        refreshStatusStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,"ALL",true);
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(2, projectsStore.findByProjectId(PROJECT_A).get(0).getVersions().size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());


        Assert.assertEquals(4, projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies().size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());

        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_B).get(0).getVersions().size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(0, projectsStore.findByProjectId(PROJECT_B).get(0).getDependencies().size());
        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(6,statuses.size());

    }

    @Test
    public void canRefreshAllVersionsAllProjects()
    {

        List<ProjectData> found = projectsStore.findByProjectId(PROJECT_A);
        Assert.assertFalse(found.isEmpty());
        Assert.assertEquals(1, found.size());
        Assert.assertEquals(0, found.get(0).getVersions().size());
        MetadataEventResponse response = artifactsRefreshService.refreshAllVersionsForAllProjects();
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        List<Entity> entityList2 = entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        Assert.assertNotNull(entityList2);
        Assert.assertEquals(18, entityList2.size());
        List<ProjectData> foundAfter = projectsStore.findByProjectId(PROJECT_A);
        Assert.assertFalse(foundAfter.isEmpty());
        Assert.assertEquals(1, foundAfter.size());
        Assert.assertEquals(2, foundAfter.get(0).getVersions().size());
        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(7,statuses.size());
    }

    @Test
    public void canRefreshProjectMasterVersion()
    {

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        Assert.assertEquals(2, projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies().size());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).size());

        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_B).get(0).getVersions().size());
        Assert.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(0, projectsStore.findByProjectId(PROJECT_B).get(0).getDependencies().size());

        List<ProjectVersionDependency> dependencies = projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies();
        Assert.assertEquals(2, dependencies.size());
        List<RefreshStatus> statuses = refreshStatusStore.getAll();
        Assert.assertNotNull(statuses);
        Assert.assertEquals(3,statuses.size());

    }


    @Test
    public void canRefreshProjectMasterVersionWithAllDependencies()
    {

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).size());

        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0").size());
        Assert.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, "art101", "2.0.0").size());

        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, true);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());

        List<ProjectVersionDependency> dependencies = projectsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).get().getDependencies();
        Assert.assertEquals(2, dependencies.size());

    }

    @Test
    public void canRefreshRevisionsAllProjects()
    {
       Assert.assertTrue(refreshStatusStore.getAll().isEmpty());

        MetadataEventResponse response = artifactsRefreshService.refreshMasterSnapshotForAllProjects();
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());


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


        List<ProjectVersionDependency> dependencies = projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies();
        Assert.assertFalse(dependencies.isEmpty());
        Assert.assertEquals(2, dependencies.size());

        List<RefreshStatus> refreshStatuses = refreshStatusStore.getAll();
       projectsService.getDependencies(TEST_GROUP_ID,TEST_ARTIFACT_ID,MASTER_SNAPSHOT,true);
        Assert.assertEquals(5,refreshStatuses.size());

    }


    @Test
    public void canPurgeOldVersions()
    {

        artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");


        ProjectData project = projectsStore.findByProjectId(PROJECT_A).get(0);
        Assert.assertNotNull(project);
        List<VersionId> projectVersions = project.getVersionsOrdered();
        Assert.assertEquals(2, projectVersions.size());
        Assert.assertEquals("1.0.0", projectVersions.get(0).toVersionIdString());
        Assert.assertEquals("2.0.0", projectsService.getLatestVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID).get().toVersionIdString());

        Assert.assertEquals(9, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", false).size());
        artifactsRefreshService.retireOldProjectVersions(1);


        ProjectData after = projectsStore.findByProjectId(PROJECT_A).get(0);
        Assert.assertNotNull(after);
        List<VersionId> afterVersionsOrdered = after.getVersionsOrdered();
        Assert.assertEquals(1, afterVersionsOrdered.size());
        Assert.assertFalse(after.getVersions().contains("1.0.0"));
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", false).size());
    }

    @Test
    public void dependenciesAreLoadedCorrectly()
    {
        ProjectData project = projectsStore.findByProjectId(PROJECT_A).get(0);
        Assert.assertEquals(0, project.getVersions().size());
        Assert.assertEquals(0, project.getDependencies().size());

        Assert.assertEquals(1, repository.findDependenciesByArtifactType(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0").size());
        Assert.assertEquals(0, entitiesStore.getStoredEntities(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID).size());
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "2.0.0", true).size());
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", true).size());

        artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", true);
        Assert.assertEquals(9, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", true).size());
        Assert.assertEquals(1, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "1.0.0", true).size());
        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_A).get(0).getVersions().size());
        Assert.assertEquals(1, projectsStore.findByProjectId(PROJECT_A).get(0).getDependencies().size());
    }

    @Test
    public void canDeleteVersion()
    {
        String versionId = "2.0.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertFalse(response.hasErrors());
        Assert.assertEquals(18, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertEquals(9, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true).size());
        Assert.assertEquals(9, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false).size());
        Assert.assertEquals(12, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());

        artifactsRefreshService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false).size());
        Assert.assertEquals(0, entitiesStore.getEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,true).size());
        Assert.assertEquals(0, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assert.assertFalse(projectsStore.findByProjectId(PROJECT_A).get(0).getVersions().contains(versionId));
    }

    @Test
    public void cantRefreshANonExistingVersion()
    {
        String versionId = "4.0.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());
    }


    @Test
    public void cantRefreshAExistingVersion()
    {
        String versionId = "2.0.0";
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
    }

    @Test
    public void canLoadEntitiesWithVersionInPackageName()
    {
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT);
        Assert.assertNotNull(response);
        Assert.assertEquals(4, entitiesStore.getStoredEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT).size());
        Assert.assertEquals(2, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT,false).size());
        Assert.assertEquals(2, entitiesStore.getEntities(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT,true).size());

    }

    @Test
    public void cantRefreshAVersionProjectHasNoVersionsInRepo() throws ArtifactRepositoryException
    {
        projectsStore.createOrUpdate(new ProjectData("PROD-1011", TEST_GROUP_ID, "art1011"));
        String versionId = "4.0.0";
        List<VersionId> versionsInRepo = repository.findVersions(TEST_GROUP_ID, "art1011");
        Assert.assertNotNull(versionsInRepo);
        Assert.assertTrue(versionsInRepo.isEmpty());
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, "art1011", versionId);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(),MetadataEventStatus.FAILED);
    }

}

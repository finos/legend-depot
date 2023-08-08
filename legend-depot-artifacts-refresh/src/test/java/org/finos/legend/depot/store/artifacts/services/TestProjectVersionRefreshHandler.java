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

import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.store.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.store.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.store.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.services.api.dependencies.ManageDependenciesService;
import org.finos.legend.depot.services.dependencies.ManageDependenciesServiceImpl;
import org.finos.legend.depot.store.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceExceptionEscenarios.PARENT_EVENT_ID;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceWithMocks.PROJECT_A;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceWithMocks.PROJECT_B;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceWithMocks.TEST_ARTIFACT_ID;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceWithMocks.TEST_DEPENDENCIES_ARTIFACT_ID;
import static org.finos.legend.depot.store.artifacts.services.TestArtifactsRefreshServiceWithMocks.TEST_GROUP_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestProjectVersionRefreshHandler extends TestStoreMongo
{
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected List<String> manifestProperties = Arrays.asList("commit-[a-zA-Z0-9]+", "release-[a-zA-Z0-9]+");
    protected UpdateProjects projectsStore = mock(UpdateProjects.class);
    protected ArtifactsFilesStore artifactsStore =  new ArtifactsFilesMongo(mongoProvider);
    protected UpdateProjectsVersions versionsStore = new ProjectsVersionsMongo(mongoProvider);
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);

    protected Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(versionsStore, projectsStore, metrics, queue, new ProjectsConfiguration("master"));
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore,projectsService);
    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected RepositoryServices repositoryServices = mock(RepositoryServices.class);

    protected ManageDependenciesService manageDependenciesService = new ManageDependenciesServiceImpl(projectsService, repositoryServices);

    protected ProjectVersionRefreshHandler versionHandler = new ProjectVersionRefreshHandler(projectsService, repositoryServices, queue, artifactsStore, new IncludeProjectPropertiesConfiguration(properties, manifestProperties), manageDependenciesService, 3);


    @Before
    public void setUpData()
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));

        List<StoreProjectData> projects = Arrays.asList(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID),
                new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID),
                new StoreProjectData("C", TEST_GROUP_ID, "C"));
        when(projectsStore.getAll()).thenReturn(projects);
        when(projectsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(projectsStore.find(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)));

    }



    @Test
    public void cantRefreshANonExistingVersion()
    {
        String versionId = "4.0.0";
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());
    }

    @Test
    public void cantRefreshAVersionProjectHasNoVersionsInRepo() throws ArtifactRepositoryException
    {
        projectsStore.createOrUpdate(new StoreProjectData("PROD-1011", TEST_GROUP_ID, "art1011"));
        String versionId = "4.0.0";
        List<VersionId> versionsInRepo = repositoryServices.findVersions(TEST_GROUP_ID, "art1011");
        Assert.assertNotNull(versionsInRepo);
        Assert.assertTrue(versionsInRepo.isEmpty());
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID, "art1011", versionId,true,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
    }

    @Test
    public void cantRefreshNonExistingVersionInRepo()
    {
        String versionId = "2.111.0";
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());
    }

    @Test
    public void cannotRefreshExcludedVersion()
    {
        String versionId = "2.111.0";
        final String EXCLUSION_REASON = "version missing in repository";
        StoreProjectVersionData storeProjectVersionData = projectsService.excludeProjectVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId, EXCLUSION_REASON);
        Assert.assertTrue(storeProjectVersionData.getVersionData().isExcluded());

        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId,false,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertTrue(response.hasErrors());

        storeProjectVersionData = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).get();
        Assert.assertTrue(storeProjectVersionData.getVersionData().isExcluded());
        Assert.assertEquals(storeProjectVersionData.getVersionData().getExclusionReason(), EXCLUSION_REASON);
    }

    @Test
    public void cannotFindDependentProject() throws ArtifactRepositoryException
    {
        Set<ArtifactDependency> deps = new HashSet<>();
        deps.add(new ArtifactDependency(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID,"1.0.0"));
        deps.add(new ArtifactDependency(TEST_GROUP_ID,"c","1.0.0"));
        when(repositoryServices.findDependencies(TEST_GROUP_ID,TEST_ARTIFACT_ID,"1.0.0")).thenReturn(deps);
        when(repositoryServices.findVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        when(repositoryServices.findVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID,"1.0.0")).thenReturn(Optional.of("1.0.0"));
        when(repositoryServices.findVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID,BRANCH_SNAPSHOT("master"))).thenReturn(Optional.of(BRANCH_SNAPSHOT("master")));
        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID,TEST_ARTIFACT_ID,"1.0.0"));
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID,TEST_ARTIFACT_ID,"1.0.0",true,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
        Assert.assertTrue(response.getErrors().contains("Dependency examples.metadata-c-1.0.0 not found in store"));
    }

    @Test
    public void cannotUpdateUnknownProjectVersion()
    {
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1",TEST_GROUP_ID,TEST_ARTIFACT_ID,"1.0.0",true,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
        Assert.assertEquals("Version 1.0.0 does not exist for examples.metadata-test in repository", response.getErrors().get(0));
    }

    @Test
    public void cannotUpdateUnknownProject()
    {
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("prod-1","i.am.not.in",TEST_ARTIFACT_ID,"1.0.0",true,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
        Assert.assertEquals("No Project with coordinates i.am.not.in-test found", response.getErrors().get(0));
    }

    @Test
    public void cannotUpdateProjectWithDuplicateCoordinates() throws ArtifactRepositoryException
    {
        when(repositoryServices.findVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID,BRANCH_SNAPSHOT("master"))).thenReturn(Optional.of(BRANCH_SNAPSHOT("master")));
        List<String> response = versionHandler.validateEvent(new MetadataNotification("prod-d",TEST_GROUP_ID,TEST_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),true,false,PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals("Invalid projectId [prod-d]. Existing project [PROD-1] has same [examples.metadata-test] coordinates", response.get(0));
        Assert.assertEquals("Invalid projectId [null]. Existing project [PROD-1] has same [examples.metadata-test] coordinates",  versionHandler.validateEvent(new MetadataNotification(null,TEST_GROUP_ID,TEST_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),true,false,PARENT_EVENT_ID)).get(0));
        Assert.assertEquals("Invalid projectId []. Existing project [PROD-1] has same [examples.metadata-test] coordinates",  versionHandler.validateEvent(new MetadataNotification("",TEST_GROUP_ID,TEST_ARTIFACT_ID,BRANCH_SNAPSHOT("master"),true,false,PARENT_EVENT_ID)).get(0));
    }

    @Test
    public void canRefreshMasterWithMasterSnapshotDependency() throws ArtifactRepositoryException
    {
        Set<ArtifactDependency> artifactDependency = Collections.singleton(new ArtifactDependency(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")));
        when(repositoryServices.findVersion(TEST_GROUP_ID,TEST_ARTIFACT_ID,BRANCH_SNAPSHOT("master"))).thenReturn(Optional.of(BRANCH_SNAPSHOT("master")));
        when(repositoryServices.findVersion(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID,BRANCH_SNAPSHOT("master"))).thenReturn(Optional.of(BRANCH_SNAPSHOT("master")));
        when(repositoryServices.findDependencies(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"))).thenReturn(artifactDependency);
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("",TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"), false, false, PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED, response.getStatus());
        Assert.assertTrue(response.getErrors().contains("Dependency examples.metadata-test-dependencies-master-SNAPSHOT not found in store"));
        Assert.assertFalse(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master")).get().getVersionData().getDependencies().isEmpty());
    }

    @Test
    public void cantRefreshVersionWithMasterDependencies()
    {
        Set<ArtifactDependency> artifactDependency = Collections.singleton(new ArtifactDependency(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, BRANCH_SNAPSHOT("master")));
        when(repositoryServices.findDependencies(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(artifactDependency);
        MetadataEventResponse response = versionHandler.handleEvent(new MetadataNotification("",TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", true,false, PARENT_EVENT_ID));
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED, response.getStatus());
    }

    @Test
    public void cannotLoadSnapshotVersionIfLimitExceeded()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata","test","branch1-SNAPSHOT"));
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata","test","branch2-SNAPSHOT"));
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata","test","branch3-SNAPSHOT"));

        List<String> errors = versionHandler.validateEvent(new MetadataNotification("PROD-1", "examples.metadata", "test", "branch4-SNAPSHOT"));

        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void canLoadSnapshotVersionIfAlreadyStored()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata","test","branch1-SNAPSHOT"));
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata","test","branch2-SNAPSHOT"));
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata","test","branch3-SNAPSHOT"));

        List<String> errors = versionHandler.validateEvent(new MetadataNotification("PROD-1", "examples.metadata", "test", "branch3-SNAPSHOT"));

        Assert.assertEquals(0, errors.size());
    }
}

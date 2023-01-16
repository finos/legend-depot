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
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generation.file.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsRefreshStatusMongo;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsQueueMongo;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestArtifactsRefreshServiceViaMock extends TestStoreMongo
{
    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_DEPENDENCIES_ARTIFACT_ID = "test-dependencies";
    public static final String PROJECT_A = "PROD-1";
    public static final String PROJECT_B = "PROD-2";
    static final String PARENT_EVENT_ID = "unit-test";
    protected ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected RefreshStatusStore refreshStatusStore = new ArtifactsRefreshStatusMongo(mongoProvider);

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected VersionedEntityProvider versionedEntityProvider = new VersionedEntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();

    protected ArtifactRepository repository = mock(ArtifactRepository.class);
    protected UpdateProjects mongoProjects = mock(UpdateProjects.class);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(mongoProjects);
    protected UpdateEntities mongoEntities = mock(UpdateEntities.class);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(mongoEntities,projectsService);
    protected UpdateFileGenerations mongoGenerations = mock(UpdateFileGenerations.class);
    protected RepositoryServices repositoryServices = new RepositoryServices(repository,projectsService);
    protected Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, refreshStatusStore, repositoryServices, artifacts, queue, new IncludeProjectPropertiesConfiguration(properties));


    @Before
    public void setUpData() throws ArtifactRepositoryException
    {
        ArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, new EntitiesHandlerImpl(entitiesService, versionedEntityProvider));
        ArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new ManageFileGenerationsServiceImpl(mongoGenerations, mongoEntities, projectsService)));

        List<ProjectData> projects = Arrays.asList(new ProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID),
                new ProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID),
                new ProjectData("C", TEST_GROUP_ID, "C"));
        when(mongoProjects.getAll()).thenReturn(projects);
        when(mongoProjects.find(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Optional.of(new ProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(mongoProjects.find(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Optional.of(new ProjectData(PROJECT_B,TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)));
        when(repository.findVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        when(repository.findVersions(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        when(repository.findVersions(TEST_GROUP_ID,"c")).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
    }

    @After
    public void afterTest()
    {
        Assert.assertTrue("should not have events in queue",queue.getAll().isEmpty());
    }

    @Test
    public void canRefreshVersionWithMasterSnapshotDependency()
    {
        Set<ArtifactDependency> artifactDependency = Collections.singleton(new ArtifactDependency(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, "master-SNAPSHOT"));
        when(repositoryServices.findDependencies(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(artifactDependency);
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", false, false, PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.SUCCESS, response.getStatus());
    }
}

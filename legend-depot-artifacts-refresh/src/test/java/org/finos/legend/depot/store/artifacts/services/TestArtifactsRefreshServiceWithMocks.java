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
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generation.file.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
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
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestArtifactsRefreshServiceWithMocks extends TestStoreMongo
{
    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_DEPENDENCIES_ARTIFACT_ID = "test-dependencies";
    public static final String PROJECT_A = "PROD-1";
    public static final String PROJECT_B = "PROD-2";

    protected ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected List<String> manifestProperties = Arrays.asList("commit-[a-zA-Z0-9]+", "release-[a-zA-Z0-9]+");
    protected RefreshStatusStore refreshStatusStore = new ArtifactsRefreshStatusMongo(mongoProvider);

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected VersionedEntityProvider versionedEntityProvider = new VersionedEntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();

    protected ArtifactRepository repository = mock(ArtifactRepository.class);
    protected UpdateProjects mongoProjects = mock(UpdateProjects.class);
    protected UpdateProjectsVersions mongoProjectsVersions = mock(UpdateProjectsVersions.class);
    private final QueryMetricsStore metrics = mock(QueryMetricsStore.class);
    protected Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(mongoProjectsVersions,mongoProjects, metrics, queue);
    protected UpdateEntities mongoEntities = mock(UpdateEntities.class);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(mongoEntities,projectsService);
    protected UpdateFileGenerations mongoGenerations = mock(UpdateFileGenerations.class);
    protected RepositoryServices repositoryServices = new RepositoryServices(repository,projectsService);
    protected DependencyManager dependencyManager = new DependencyManager(projectsService, repositoryServices);
    protected ProjectVersionRefreshHandler versionHandler = new ProjectVersionRefreshHandler(projectsService, repositoryServices, queue, refreshStatusStore,artifacts, new IncludeProjectPropertiesConfiguration(properties, manifestProperties), dependencyManager, 10);

    protected ArtifactsRefreshServiceImpl artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, repositoryServices,queue);


    @Before
    public void setUpData() throws ArtifactRepositoryException
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, new EntitiesHandlerImpl(entitiesService, versionedEntityProvider));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new ManageFileGenerationsServiceImpl(mongoGenerations, mongoEntities, projectsService)));

        List<StoreProjectData> projects = Arrays.asList(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID),
                new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID),
                new StoreProjectData("C", TEST_GROUP_ID, "C"));
        when(mongoProjects.getAll()).thenReturn(projects);
        when(mongoProjects.find(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(mongoProjects.find(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_B,TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)));
        when(mongoProjectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID,MASTER_SNAPSHOT)).thenReturn(Optional.of(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT)));
        when(mongoProjectsVersions.find(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID,MASTER_SNAPSHOT)).thenReturn(Optional.of(new StoreProjectVersionData(TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID, MASTER_SNAPSHOT)));
        when(repository.findVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0"), VersionId.parseVersionId("2.0.0")));
        when(repository.findVersions(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        when(repository.findVersions(TEST_GROUP_ID,"c")).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
    }

    @After
    public void afterTest()
    {
        Assert.assertTrue("should not have events in queue",queue.getAll().isEmpty());
    }

    
    @Test
    public void canCalculateCandidateVersionsToUpdate()
    {
        List<VersionId> repoVersions = Arrays.asList(VersionId.parseVersionId("1.0.0"),VersionId.parseVersionId("2.0.0"));
        List<String> versions = Arrays.asList("1.0.0");
        List<VersionId> candidates = artifactsRefreshService.calculateCandidateVersions(repoVersions,versions);
        Assert.assertEquals("2.0.0",candidates.get(0).toVersionIdString());
    }
}

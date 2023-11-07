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
import org.finos.legend.depot.domain.notifications.MetadataNotificationStatus;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generations.impl.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generations.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntityProvider;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationsProvider;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.mongo.notifications.queue.NotificationsQueueMongo;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestArtifactsRefreshServiceExceptionEscenarios extends TestStoreMongo
{
    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_DEPENDENCIES_ARTIFACT_ID = "test-dependencies";
    public static final String PROJECT_A = "PROD-1";
    public static final String PROJECT_B = "PROD-2";
    static final String PARENT_EVENT_ID = "unit-test";
    protected ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();

    protected ArtifactRepository repository = mock(ArtifactRepository.class);
    protected UpdateProjects mongoProjects = mock(UpdateProjects.class);
    protected UpdateProjectsVersions mongoProjectsVersions = mock(UpdateProjectsVersions.class);
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    protected Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(mongoProjectsVersions,mongoProjects,metrics,queue,new ProjectsConfiguration("master"));
    protected UpdateEntities mongoEntities = mock(UpdateEntities.class);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(mongoEntities,projectsService);
    protected UpdateFileGenerations mongoGenerations = mock(UpdateFileGenerations.class);

    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, repository, queue);


    @Before
    public void setUpData() throws ArtifactRepositoryException
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new ManageFileGenerationsServiceImpl(mongoGenerations, mongoEntities, projectsService)));

        List<StoreProjectData> projects = Arrays.asList(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID),
                                                   new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID),
                                                   new StoreProjectData("C", TEST_GROUP_ID, "C"));
        when(mongoProjects.getAll()).thenReturn(projects);
        when(mongoProjects.find(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(mongoProjects.find(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_B,TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)));
        when(repository.findVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        when(repository.findVersions(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        when(repository.findVersions(TEST_GROUP_ID,"c")).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
    }

    @After
    public void afterTest()
    {
        Assert.assertTrue("should not have events in queue",queue.getAll().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotFindProject()
    {
      artifactsRefreshService.refreshVersionForProject("test.test","missing.project","1.0.0",true, PARENT_EVENT_ID);
    }



    @Test
    @Ignore
    public void projectFailsToPersistEntities()
    {
        when(mongoProjects.find(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)));

        MetadataNotificationResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID,"1.0.0",true,PARENT_EVENT_ID);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataNotificationStatus.FAILED,response.getStatus());
        Assert.assertEquals("Could not find dependent project: [examples.metadata-test-dependencies-1.0.0]", response.getErrors().get(0));
    }

}

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
import org.finos.legend.depot.artifacts.repository.maven.impl.TestMavenArtifactsRepository;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectData;
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
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.artifacts.store.mongo.ArtifactsMongo;
import org.finos.legend.depot.store.artifacts.store.mongo.MongoRefreshStatus;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestArtifactsRefreshServiceExeptionEscenarios extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_DEPENDENCIES_ARTIFACT_ID = "test-dependencies";
    public static final String PROJECT_A = "PROD-1";
    public static final String PROJECT_B = "PROD-2";
    protected UpdateArtifacts artifacts = new ArtifactsMongo(mongoProvider);
    protected List<String> properties = Arrays.asList("[a-zA-Z0-9]+.version");
    protected ManageRefreshStatusService refreshStatusStore = new MongoRefreshStatus(mongoProvider);

    protected EntityArtifactsProvider entitiesProvider = new EntityProvider();
    protected VersionedEntityProvider versionedEntityProvider = new VersionedEntityProvider();
    protected FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();

    protected ArtifactRepository repository = new TestMavenArtifactsRepository();
    protected UpdateProjects mongoProjects = mock(UpdateProjects.class);
    protected ManageProjectsService projectsService = new ProjectsServiceImpl(mongoProjects);
    protected UpdateEntities mongoEntities = mock(UpdateEntities.class);
    protected ManageEntitiesService entitiesService = new EntitiesServiceImpl(mongoEntities,projectsService);
    protected UpdateFileGenerations mongoGenerations = mock(UpdateFileGenerations.class);
    protected ArtifactsRefreshService artifactsRefreshService = new ArtifactsRefreshServiceImpl(projectsService, refreshStatusStore, repository, artifacts, new IncludeProjectPropertiesConfiguration(properties));


    @Before
    public void setUpData()
    {
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, entitiesProvider));
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, new EntitiesHandlerImpl(entitiesService, versionedEntityProvider));
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(repository, fileGenerationsProvider, new FileGenerationsServiceImpl(mongoGenerations, mongoEntities)));

        List<ProjectData> projects = Arrays.asList(new ProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID),new ProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID));
        when(mongoProjects.getAll()).thenReturn(projects);
        when(mongoProjects.find(TEST_GROUP_ID,TEST_ARTIFACT_ID)).thenReturn(Optional.of(new ProjectData(PROJECT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
    }


    @Test(expected = IllegalArgumentException.class)
    public void cannotFindProject()
    {
        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject("test.test","missing.project","1.0.0",true);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
        Assert.assertEquals("Project does not exists for test.test-missing.project", response.getErrors().get(0));


    }

    @Test
    public void cannotFindDependentProject()
    {
      MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID,TEST_ARTIFACT_ID,"1.0.0",true);
      Assert.assertNotNull(response);
      Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
      Assert.assertEquals("Could not find dependent project: [examples.metadata-test-dependencies-1.0.0]", response.getErrors().get(0));
    }

    @Test
    @Ignore
    public void projectFailsToPersistEntities()
    {
        when(mongoProjects.find(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID)).thenReturn(Optional.of(new ProjectData(PROJECT_B, TEST_GROUP_ID, TEST_DEPENDENCIES_ARTIFACT_ID)));

        MetadataEventResponse response = artifactsRefreshService.refreshVersionForProject(TEST_GROUP_ID,TEST_DEPENDENCIES_ARTIFACT_ID,"1.0.0",true);
        Assert.assertNotNull(response);
        Assert.assertEquals(MetadataEventStatus.FAILED,response.getStatus());
        Assert.assertEquals("Could not find dependent project: [examples.metadata-test-dependencies-1.0.0]", response.getErrors().get(0));
    }

}

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

package org.finos.legend.depot.services.artifacts.handlers.generations;

import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.artifacts.repository.maven.TestMavenArtifactsRepository;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.services.api.generations.ManageFileGenerationsService;
import org.finos.legend.depot.services.generations.impl.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestGenerationsProvider extends TestStoreMongo
{

    public static final String PRODUCT_A = "PROD-23992";
    protected static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    private final ArtifactRepository repository = new TestMavenArtifactsRepository();
    private FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();
    private final UpdateProjects projects = mock(UpdateProjects.class);
    private final UpdateProjectsVersions projectsVersions = mock(UpdateProjectsVersions.class);
    private final UpdateEntities entities = mock(UpdateEntities.class);
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = mock(Queue.class);
    private final ManageFileGenerationsService generations = new ManageFileGenerationsServiceImpl(new FileGenerationsMongo(mongoProvider), new ProjectsServiceImpl(projectsVersions,projects,metrics,queue,new ProjectsConfiguration("master")));

    @Before
    public void setup()
    {
       when(projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PRODUCT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
       when(projectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"))).thenReturn(Optional.of(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"))));
       when(projectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0")).thenReturn(Optional.of(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0")));
    }

    private List<File> getFiles(String versionId)
    {
        return repository.findFiles(ArtifactType.FILE_GENERATIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
    }

    private List<File> getDependenciesFiles(String versionId)
    {
        return repository.findDependenciesFiles(ArtifactType.FILE_GENERATIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
    }

    @Test
    public void canResolveGenerationsInJar()
    {
        List<File> files = getFiles("2.0.0");
        Assert.assertNotNull(files);

        Assert.assertEquals(1, files.size());

        Assert.assertEquals(0, getDependenciesFiles("2.0.0").size());

        List<DepotGeneration> gens = fileGenerationsProvider.extractArtifacts(files);
        Assert.assertFalse(gens.isEmpty());
    }

    @Test
    public void canRefreshRevisions()
    {

        Assert.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), BRANCH_SNAPSHOT("master"), getFiles(BRANCH_SNAPSHOT("master")));
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(14, fileGenerations.size());

    }

    @Test
    public void canRefreshRevisionWithChangeInGenerations()
    {
        URL filePath = this.getClass().getClassLoader().getResource("repository/examples/metadata/test-file-generation/master-SNAPSHOT/test-file-generation-new-master-SNAPSHOT.jar");
        Assert.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        //deleted one generation as part of new master snapshot version
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), BRANCH_SNAPSHOT("master"),Arrays.asList(new File(filePath.getFile())));
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(13, fileGenerations.size());
    }

    @Test
    public void canRefreshVersions()
    {

        Assert.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), "2.0.0", getFiles("2.0.0"));
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(12, fileGenerations.size());

    }


    @Test
    public void canReadFileGenerationArtifactsWithMultipleGenerations()
    {
        Assert.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), "2.0.0", getFiles("2.0.0"));
        Assert.assertNotNull(response);

        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(12, fileGenerations.size());

        Assert.assertEquals(4, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "java").size());
        Assert.assertEquals(2, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "my-ext").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetGenerationsForNonExistentVersion()
    {
        generations.getFileGenerations(TEST_GROUP_ID,TEST_ARTIFACT_ID,"10.0.0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotGetGenerationsForExcludedVersions()
    {
        String versionId = "3.0.0";
        StoreProjectVersionData storeProjectVersion = new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        storeProjectVersion.getVersionData().setExcluded(true);
        when(projectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId)).thenReturn(Optional.of(storeProjectVersion));
        generations.getFileGenerations(TEST_GROUP_ID,TEST_ARTIFACT_ID,versionId);
    }
}

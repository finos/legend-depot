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

package org.finos.legend.depot.store.artifacts.services.generation.file;

import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.maven.impl.TestMavenArtifactsRepository;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.services.generation.file.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private final UpdateEntities entities = new EntitiesMongo(mongoProvider);
    private final ManageFileGenerationsService generations = new ManageFileGenerationsServiceImpl(new FileGenerationsMongo(mongoProvider), entities, new ProjectsServiceImpl(projects));

    @Before
    public void setup()
    {
       when(projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(new ProjectData(PRODUCT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID).withVersions("2.0.0")));
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

        List<FileGeneration> gens = fileGenerationsProvider.loadArtifacts(files);
        Assert.assertFalse(gens.isEmpty());
    }

    @Test
    public void canRefreshRevisions()
    {

        Assert.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        ProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataEventResponse response = handler.refreshProjectRevisionArtifacts(projectData, getFiles(VersionValidator.MASTER_SNAPSHOT));
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
        ProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        //deleted one generation as part of new master snapshot version
        MetadataEventResponse response = handler.refreshProjectRevisionArtifacts(projectData, Arrays.asList(new File(filePath.getFile())));
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
        ProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataEventResponse response = handler.refreshProjectVersionArtifacts(projectData, "2.0.0", getFiles("2.0.0"));
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
        ProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataEventResponse response = handler.refreshProjectVersionArtifacts(projectData, "2.0.0", getFiles("2.0.0"));
        Assert.assertNotNull(response);

        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(12, fileGenerations.size());

        Assert.assertEquals(4, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "java").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canGetGenerationsForNonExistentVersion()
    {
        generations.getFileGenerations(TEST_GROUP_ID,TEST_ARTIFACT_ID,"10.0.0");
    }
}

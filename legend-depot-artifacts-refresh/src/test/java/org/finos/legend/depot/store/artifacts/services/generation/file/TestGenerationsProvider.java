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
import org.finos.legend.depot.services.generation.file.FileGenerationsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationVersionsHandler;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProviderImpl;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;


public class TestGenerationsProvider extends TestStoreMongo
{

    public static final String PRODUCT_A = "PROD-23992";
    protected static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    private final ArtifactRepository repository = new TestMavenArtifactsRepository();
    private FileGenerationsProvider fileGenerationsProvider = new FileGenerationsProviderImpl();
    private final UpdateProjects projects = new ProjectsMongo(mongoProvider);
    private final UpdateEntities entities = new EntitiesMongo(mongoProvider);
    private final ManageFileGenerationsService generations = new FileGenerationsServiceImpl(new FileGenerationsMongo(mongoProvider), entities);

    @Before
    public void setup()
    {
        projects.createOrUpdate(new ProjectData(PRODUCT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID));
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
        FileGenerationVersionsHandler handler = new FileGenerationVersionsHandler(repository, fileGenerationsProvider, generations);
        ProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataEventResponse response = handler.refreshProjectRevisionArtifacts(projectData, getFiles(VersionValidator.MASTER_SNAPSHOT));
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(14, fileGenerations.size());

    }

    @Test
    public void canRefreshVersions()
    {

        Assert.assertTrue(generations.getAll().isEmpty());
        FileGenerationVersionsHandler handler = new FileGenerationVersionsHandler(repository, fileGenerationsProvider, generations);
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
        FileGenerationVersionsHandler handler = new FileGenerationVersionsHandler(repository, fileGenerationsProvider, generations);
        ProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataEventResponse response = handler.refreshProjectVersionArtifacts(projectData, "2.0.0", getFiles("2.0.0"));
        Assert.assertNotNull(response);
        Assert.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assert.assertNotNull(fileGenerations);
        Assert.assertEquals(12, fileGenerations.size());

        Assert.assertEquals(4, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "java").size());
    }

}

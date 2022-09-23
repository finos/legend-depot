//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.services.generation.artifact;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;
import org.finos.legend.depot.domain.generation.artifact.StoredArtifactGeneration;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.generation.artifact.ManageArtifactGenerationsService;
import org.finos.legend.depot.services.generation.file.TestFileGenerationsService;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.generation.artifact.UpdateArtifactGenerations;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.artifact.ArtifactGenerationsMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestArtifactGenerationsService extends TestStoreMongo
{

    protected Entities entities = new EntitiesMongo(mongoProvider);

    private static final URL filePath = TestFileGenerationsService.class.getClassLoader().getResource("file-generation/test-file-generation-master-SNAPSHOT.jar");

    private UpdateArtifactGenerations generations = new ArtifactGenerationsMongo(mongoProvider);

    private ManageArtifactGenerationsService service = new ArtifactGenerationsServiceImpl(generations);

    private static String GROUP_ID = "group.test";
    private static String ARTIFACT_ID = "test";

    @Before
    public void loadData() throws Exception
    {

        try (FileGenerationLoader file = FileGenerationLoader.newFileGenerationsLoader(new File(filePath.toURI())))
        {
            Stream<FileGeneration> data = file.getAllFileGenerations();
            Assert.assertNotNull(data);
            data.forEach(gen ->
            {
                ArtifactGeneration generation = new ArtifactGeneration(gen.getPath(), gen.getContent());
                generations.createOrUpdate(new StoredArtifactGeneration(GROUP_ID, ARTIFACT_ID, VersionValidator.MASTER_SNAPSHOT, "model::generator::MyGenerator", generation));
                generations.createOrUpdate(new StoredArtifactGeneration(GROUP_ID, ARTIFACT_ID, "1.0.1", null, generation));
                generations.createOrUpdate(new StoredArtifactGeneration(GROUP_ID, ARTIFACT_ID, "1.0.0", "model::generator::MyGenerator", generation));
                generations.createOrUpdate(new StoredArtifactGeneration("group.test.otherproject", "test", "1.0.0", null, generation));
            });

            Assert.assertEquals(52, generations.getAll().size());
        }
    }

    @Test
    public void canDelete()
    {

        service.deleteLatest(GROUP_ID, ARTIFACT_ID);
        Assert.assertEquals(39, generations.getAll().size());
        service.delete("group.test.otherproject", "test", "1.0.0");
        Assert.assertEquals(26, generations.getAll().size());
        service.delete(GROUP_ID, ARTIFACT_ID, "1.1.0");
        Assert.assertEquals(26, generations.getAll().size());
    }

    @Test

    public void canGetArtifactGeneration()
    {
        List<ArtifactGeneration> masterArtifactGenerations = service.getArtifactGenerations(GROUP_ID, ARTIFACT_ID, VersionValidator.MASTER_SNAPSHOT);
        Assert.assertEquals(13, masterArtifactGenerations.size());
        List<ArtifactGeneration> lastestArtifactGenerations = service.getLatestArtifactGenerations(GROUP_ID, ARTIFACT_ID);
        Assert.assertEquals(13, lastestArtifactGenerations.size());
        List<ArtifactGeneration> artifactGenerationsFirst = service.getArtifactGenerations(GROUP_ID, ARTIFACT_ID, "1.0.0");
        Assert.assertEquals(13, artifactGenerationsFirst.size());
        List<ArtifactGeneration> artifactGenerations = service.getArtifactGenerations(GROUP_ID, ARTIFACT_ID, "1.0.1");
        Assert.assertEquals(13, artifactGenerations.size());
    }


    @Test
    public void canGetArtifactsByGenerator()
    {
        List<ArtifactGeneration> masterArtifactGenerations = service.getLatestArtifactsGenerationsByGenerator(GROUP_ID, ARTIFACT_ID, "model::generator::MyGenerator");
        Assert.assertEquals(13, masterArtifactGenerations.size());

        List<ArtifactGeneration> artifactGenerations = service.getArtifactsGenerationsByGenerator(GROUP_ID, ARTIFACT_ID, "1.0.0", "model::generator::MyGenerator");
        Assert.assertEquals(13, artifactGenerations.size());

        List<ArtifactGeneration> noArtifactsGeneration = service.getArtifactsGenerationsByGenerator(GROUP_ID, ARTIFACT_ID, "1.0.0", "model::generator::MISSING");
        Assert.assertEquals(0, noArtifactsGeneration.size());

    }


    @Test
    public void canGetArtifactsByPath()
    {
        String path = "/model/generator/MyGenerator/my-ext/Artifactgeneration.txt";
        String expectedContent = "My Generated Artifact for model::generator::MyGenerator.";
        Optional<ArtifactGeneration> artifactGenerationOptional = service.getArtifactGenerationByPath(GROUP_ID, ARTIFACT_ID, "1.0.1", path);
        Assert.assertTrue(artifactGenerationOptional.isPresent());
        Assert.assertEquals(expectedContent, artifactGenerationOptional.get().getContent());

        Optional<ArtifactGeneration> latestArtifact = service.getLatestArtifactGenerationByPath(GROUP_ID, ARTIFACT_ID,path);
        Assert.assertTrue(latestArtifact.isPresent());
        Assert.assertEquals(expectedContent, latestArtifact.get().getContent());

        Optional<ArtifactGeneration> missingGeneration = service.getArtifactGenerationByPath(GROUP_ID, ARTIFACT_ID, "10.0.1", path);
        Assert.assertTrue(!missingGeneration.isPresent());

        Optional<String> artifactContent = service.getArtifactGenerationContentByPath(GROUP_ID, ARTIFACT_ID, "1.0.1", path);
        Assert.assertTrue(artifactContent.isPresent());
        Assert.assertEquals(expectedContent, artifactContent.get());

        Optional<String> latestContent = service.getLatestArtifactGenerationContentByPath(GROUP_ID, ARTIFACT_ID, path);
        Assert.assertTrue(latestContent.isPresent());
        Assert.assertEquals(expectedContent, latestContent.get());
    }



}

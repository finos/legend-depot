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


package org.finos.legend.depot.store.mongo.generation.artifact;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.finos.legend.depot.domain.generation.artifact.StoredArtifactGeneration;
import org.finos.legend.depot.store.api.generation.artifact.UpdateArtifactGenerations;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestArtifactsGenerationStore extends TestStoreMongo
{

    private UpdateArtifactGenerations generations = new ArtifactGenerationsMongo(mongoProvider);
    private static String TEST_GROUP_ID = "examples.metadata";
    private static String TEST_ARTIFACT_ID = "test";
    private static String GENERATOR = "examples::metadata::test::MainGenerator";

    private List<StoredArtifactGeneration> readArtifactGenerations(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<StoredArtifactGeneration> generations = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoredArtifactGeneration>>()
            {
            });
            Assert.assertNotNull("testing file" + fileName.getFile(), generations);
            return generations;
        } catch (Exception e)
        {
            Assert.fail("an error has occurred loading test metadata" + e.getMessage());
        }
        return null;
    }

    private MongoCollection getMongoArtifactGenerations()
    {
        return getMongoDatabase().getCollection(ArtifactGenerationsMongo.COLLECTION);
    }

    private void setUpArtifactGenerationFromFile(URL generationsData)
    {
        try
        {
            readArtifactGenerations(generationsData).forEach(project ->
            {
                try
                {
                    getMongoArtifactGenerations().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                } catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
                }
            });
            Assert.assertNotNull(getMongoArtifactGenerations());
        } catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    @Before
    public void loadData()
    {
        setUpArtifactGenerationFromFile(this.getClass().getClassLoader().getResource("data/artifact-generations.json"));
        Assert.assertEquals(9, generations.getAll().size());
    }

    @Test
    public void canQueryByVersion()
    {
        List<StoredArtifactGeneration> result = generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3");
        Assert.assertEquals(7, result.size());

        List<StoredArtifactGeneration> snapShotVersionResult = generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT);
        Assert.assertEquals(2, snapShotVersionResult.size());

        Assert.assertEquals(0, generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2").size());
    }

    @Test
    public void canQueryByPath()
    {
        String filePath = "/examples/metadata/test/dependency/Test.json";
        Optional<StoredArtifactGeneration> result = generations.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "/examples/metadata/test/dependency/Test.json");
        Assert.assertTrue(result.isPresent());
        StoredArtifactGeneration storedArtifactGeneration = result.get();
        Assert.assertEquals(storedArtifactGeneration.getArtifact().getPath(), filePath);
        Assert.assertEquals(storedArtifactGeneration.getArtifact().getContent(), "my test content 3");
        Assert.assertEquals(storedArtifactGeneration.getGenerator(), GENERATOR);

        Optional<StoredArtifactGeneration> noResult = generations.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "Does/Not/Exist.json");
        Assert.assertTrue(noResult.isEmpty());

    }


    @Test
    public void canQueryAll()
    {
        List<StoredArtifactGeneration> artifactGenerations = generations.getAll();
        Assert.assertEquals(9, artifactGenerations.size());
    }


    @Test
    public void canFindByGenerator()
    {
        List<StoredArtifactGeneration> artifactGenerations = generations.findByGenerator(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", GENERATOR);
        Assert.assertEquals(3, artifactGenerations.size());
        artifactGenerations.forEach(a -> Assert.assertEquals(a.getGenerator(), GENERATOR));
    }

    @Test
    public void canFindByProject()
    {

        List<StoredArtifactGeneration> artifactGenerations = generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3");
        Assert.assertEquals(7, artifactGenerations.size());
        artifactGenerations.forEach(a ->
        {
            Assert.assertEquals(TEST_GROUP_ID, a.getGroupId());
            Assert.assertEquals(TEST_ARTIFACT_ID, a.getArtifactId());
            Assert.assertEquals("2.3.3", a.getVersionId());
        });

        List<StoredArtifactGeneration> noGenerations = generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "10.0.0");
        Assert.assertEquals(0, noGenerations.size());
    }


    @Test
    public void canDelete()
    {
        List<StoredArtifactGeneration> artifactGenerations = generations.getAll();
        Assert.assertEquals(9, artifactGenerations.size());
        generations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3");

        List<StoredArtifactGeneration> afterGenerations = generations.getAll();
        Assert.assertEquals(2, afterGenerations.size());
    }


}

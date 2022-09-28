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

package org.finos.legend.depot.store.mongo.generation.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestFileGenerationsStore extends TestStoreMongo
{

    private UpdateFileGenerations generations = new FileGenerationsMongo(mongoProvider);
    private static String TEST_GROUP_ID = "examples.metadata";
    private static String TEST_ARTIFACT_ID = "test";


    private List<StoredFileGeneration> readGenerationsFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<StoredFileGeneration> generations = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoredFileGeneration>>()
            {
            });
            Assert.assertNotNull("testing file" + fileName.getFile(), generations);
            return generations;
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test metadata" + e.getMessage());
        }
        return null;
    }

    private void setUpFileGenerationFromFile(URL generationsData)
    {
        try
        {
            readGenerationsFile(generationsData).forEach(project ->
            {
                try
                {
                    getMongoFileGnerations().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
                }
            });
            Assert.assertNotNull(getMongoFileGnerations());
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    private MongoCollection getMongoFileGnerations()
    {
        return getMongoDatabase().getCollection(FileGenerationsMongo.COLLECTION);
    }


    @Before
    public void loadData()
    {
        setUpFileGenerationFromFile(this.getClass().getClassLoader().getResource("data/file-generations.json"));
        Assert.assertEquals(11, generations.getAll().size());
    }


    @Test
    public void canQueryByVersion()
    {
        List<StoredFileGeneration> result = generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3");
        Assert.assertEquals(11, result.size());

        Assert.assertEquals(0, generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2").size());

    }

    @Test
    public void canQueryByElementPath()
    {
        List<StoredFileGeneration> result = generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "com::avrogen");
        Assert.assertEquals(3, result.size());

        Assert.assertEquals(0, generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2", "com::avrogen").size());
        Assert.assertEquals(0, generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT, "com::jsonGen").size());

        List<StoredFileGeneration> result2 = generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "com::avrogen");
        Assert.assertEquals(3, result2.size());

        List<StoredFileGeneration> result3 = generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "com::MyElementPath");
        Assert.assertEquals(1, result3.size());
    }

    @Test
    public void canQueryByGenerationType()
    {
        List<StoredFileGeneration> result = generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "avro");
        Assert.assertEquals(3, result.size());

        Assert.assertEquals(0, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2", "java").size());
        Assert.assertEquals(0, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT, "java").size());

    }

    @Test
    public void canQueryByGenerationFilePath()
    {
        Assert.assertTrue(generations.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assert.assertTrue(generations.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "/examples/generated/test/other/MyOutput.json").isPresent());
        Assert.assertFalse(generations.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "0.01.1", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assert.assertFalse(generations.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT, "com/finos/sdgashdf").isPresent());
    }

}

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

package org.finos.legend.depot.store.mongo.generations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public abstract class TestGenerationsStoreMongo extends TestStoreMongo
{

    private static List<StoredFileGeneration> readGenerationsFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();
            List<StoredFileGeneration> generations = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoredFileGeneration>>()
            {
            });
            Assertions.assertNotNull(generations, "testing file" + fileName.getFile());
            return generations;
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test metadata" + e.getMessage());
        }
        return null;
    }

    public static void setUpFileGenerationFromFile(URL generationsData, MongoDatabase database)
    {
        try
        {
            Assertions.assertNotNull(getMongoFileGenerations(database));
            readGenerationsFile(generationsData).forEach(project ->
            {
                try
                {
                    getMongoFileGenerations(database).insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assertions.fail("an error has occurred loading test project metadata" + e.getMessage());
                }
            });

        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    private static MongoCollection getMongoFileGenerations(MongoDatabase database)
    {
        return database.getCollection(FileGenerationsMongo.COLLECTION);
    }

}

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

package org.finos.legend.depot.store.mongo.entities.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.junit.jupiter.api.Assertions;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public final class EntitiesMongoTestUtils
{

    private final MongoDatabase database;

    public EntitiesMongoTestUtils(MongoDatabase database)
    {
        this.database = database;
    }

    private  MongoCollection getMongoEntities()
    {
        return database.getCollection(EntitiesMongo.COLLECTION);
    }

    public List<StoredEntity> readEntitiesFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<StoredEntity> entities = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoredEntity>>()
            {
            });
            Assertions.assertNotNull(entities, "testing file" + fileName.getFile());
            return entities;
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test versioned entity metadata" + e.getMessage());
        }
        return null;
    }

    public void loadEntities(URL entitiesFile)
    {
        try
        {
            Assertions.assertNotNull(getMongoEntities());
            readEntitiesFile(entitiesFile).forEach(entity ->
            {
                try
                {
                    getMongoEntities().insertOne(Document.parse(new ObjectMapper().writeValueAsString(entity)));
                }
                catch (JsonProcessingException e)
                {
                    Assertions.fail("an error has occurred loading test entity" + e.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test entity" + e.getMessage());
        }
    }


    public void loadEntities(String projectId, String versionId)
    {
        String fileName = "data/" + projectId + "/entities-" + versionId + ".json";
        loadEntities(EntitiesMongoTestUtils.class.getClassLoader().getResource(fileName));
    }
}

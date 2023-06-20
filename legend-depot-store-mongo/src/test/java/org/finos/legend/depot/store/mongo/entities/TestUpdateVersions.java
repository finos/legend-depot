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

package org.finos.legend.depot.store.mongo.entities;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestUpdateVersions extends TestStoreMongo
{

    private static final URL ENTITIES_FILE = TestUpdateVersions.class.getClassLoader().getResource("data/versioned-entities.json");
    private EntitiesMongo entitiesMongo = new EntitiesMongo(mongoProvider);

    @Test
    public void canStoreANewVersion()
    {

        List<StoredEntity> entitiesList = readEntitiesFile(ENTITIES_FILE);
        Assert.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        List result = entitiesMongo.createOrUpdate(Arrays.asList(entity));
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.countDocuments());
        Document doc = (Document)entities.find().iterator().next();
        Assert.assertEquals(entity.getVersionId(), doc.getString(EntitiesMongo.VERSION_ID));
        Assert.assertEquals(entity.getEntity().getPath(), ((Map)doc.get(EntitiesMongo.ENTITY)).get("path"));
        Assert.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc.get(EntitiesMongo.ENTITY)).get("classifierPath"));

    }

    @Test
    public void canStoreEntitiesWithDotCharacterAsFieldContent()
    {
        List<StoredEntity> entitiesList = readEntitiesFile(TestUpdateVersions.class.getClassLoader().getResource("data/versioned-entity-dot-character.json"));
        Assert.assertNotNull(entitiesList);
        List result = entitiesMongo.createOrUpdate(entitiesList);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.countDocuments());
    }

    @Test
    public void canUpdateAVersion()
    {

        List<StoredEntity> entitiesList = readEntitiesFile(ENTITIES_FILE);
        Assert.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        List result = entitiesMongo.createOrUpdate(Arrays.asList(entity));
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.countDocuments());
        Document doc = (Document)entities.find().iterator().next();
        Assert.assertEquals(entity.getVersionId(), doc.getString(EntitiesMongo.VERSION_ID));
        Assert.assertEquals(entity.getEntity().getPath(), ((Map)doc.get(EntitiesMongo.ENTITY)).get("path"));
        Assert.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc.get(EntitiesMongo.ENTITY)).get("classifierPath"));

        List result2 = entitiesMongo.createOrUpdate(Arrays.asList(entity));
        Assert.assertNotNull(result2);
        Assert.assertEquals(1, result2.size());
        Assert.assertEquals(1, entities.countDocuments());

        MongoCollection entities1 = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assert.assertNotNull(entities1);
        Assert.assertEquals(1, entities1.countDocuments());
        Document doc1 = (Document)entities.find().iterator().next();
        Assert.assertEquals(entity.getVersionId(), doc1.getString(EntitiesMongo.VERSION_ID));
        Assert.assertEquals(entity.getEntity().getPath(), ((Map)doc1.get(EntitiesMongo.ENTITY)).get("path"));
        Assert.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc1.get(EntitiesMongo.ENTITY)).get("classifierPath"));

    }

    @Test
    public void updatingSameEntityDoesNotCreateAnewONe()
    {
        List<StoredEntity> entitiesList = readEntitiesFile(ENTITIES_FILE);
        Assert.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        entitiesMongo.createOrUpdate(Arrays.asList(entity));
        entitiesMongo.createOrUpdate(Arrays.asList(entity));

        List<Entity> entities = entitiesMongo.getAllEntities("examples.metadata", "test", entity.getVersionId());
        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.size());

    }

    @Test
    public void canDeleteVersion()
    {
        setUpEntitiesDataFromFile(ENTITIES_FILE);
        long count = entitiesMongo.getVersionEntityCount("examples.metadata", "test", "2.2.0");
        Assert.assertEquals(3, count);
        entitiesMongo.delete("examples.metadata", "test", "2.2.0",true);
        entitiesMongo.delete("examples.metadata", "test", "2.2.0",false);
        Assert.assertEquals(0, entitiesMongo.getVersionEntityCount("examples.metadata", "test", "2.2.0"));
    }


    @Test
    public void canDeleteStoreEntities()
    {
        setUpEntitiesDataFromFile(ENTITIES_FILE);
        long count = entitiesMongo.getEntityCount("examples.metadata", "test");
        Assert.assertEquals(3, count);
        entitiesMongo.delete("examples.metadata", "test");
        Assert.assertEquals(0, entitiesMongo.getEntityCount("examples.metadata", "test"));
    }

    @Test
    public void canCreateUpdateStoreEntities()
    {
        List<StoredEntity> entities = readEntitiesFile(ENTITIES_FILE);
        Assert.assertEquals(3, entities.size());

        //lets do a change
        StoredEntity first = entities.get(0);
        Map<String,Object> entity = (Map<String, Object>) first.getEntity().getContent();
        entity.put("new","stuff");
        entitiesMongo.createOrUpdate(entities);

       Optional<Entity> found = entitiesMongo.getEntity(first.getGroupId(),first.getArtifactId(),first.getVersionId(),first.getEntity().getPath());
       Assert.assertTrue(found.isPresent());
       Assert.assertTrue(found.get().getContent().containsKey("new"));
       Assert.assertEquals("stuff",found.get().getContent().get("new"));
    }

}

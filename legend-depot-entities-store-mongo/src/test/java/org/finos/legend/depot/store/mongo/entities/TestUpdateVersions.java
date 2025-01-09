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
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.store.model.entities.StoredEntityData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.finos.legend.depot.store.mongo.entities.AbstractEntitiesMongo.CLASSIFIER_PATH;
import static org.finos.legend.depot.store.mongo.entities.AbstractEntitiesMongo.PATH;

public class TestUpdateVersions extends TestStoreMongo
{

    private static final URL ENTITIES_FILE = TestUpdateVersions.class.getClassLoader().getResource("data/versioned-entities.json");
    private EntitiesMongo entitiesMongo = new EntitiesMongo(mongoProvider);
    private final EntitiesMongoTestUtils entityUtils = new EntitiesMongoTestUtils(mongoProvider);

    @BeforeEach
    public void setUp()
    {

    }

    @Test
    public void canStoreANewVersion()
    {

        List<StoredEntity> entitiesList = entityUtils.readEntitiesFile(ENTITIES_FILE);
        Assertions.assertNotNull(entitiesList);
        StoredEntityData entity = (StoredEntityData) entitiesList.get(0);
        List result = entitiesMongo.createOrUpdate(Arrays.asList(entity));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(1, entities.countDocuments());
        Document doc = (Document)entities.find().iterator().next();
        Assertions.assertEquals(entity.getVersionId(), doc.getString(EntitiesMongo.VERSION_ID));
        Assertions.assertEquals(entity.getEntity().getPath(), ((Map)doc.get(EntitiesMongo.ENTITY_ATTRIBUTES)).get(PATH));
        Assertions.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc.get(EntitiesMongo.ENTITY_ATTRIBUTES)).get(CLASSIFIER_PATH));

    }

    @Test
    public void canStoreEntitiesWithDotCharacterAsFieldContent()
    {
        List<StoredEntity> entitiesList = entityUtils.readEntitiesFile(TestUpdateVersions.class.getClassLoader().getResource("data/versioned-entity-dot-character.json"));
        Assertions.assertNotNull(entitiesList);
        List result = entitiesMongo.createOrUpdate(entitiesList);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(1, entities.countDocuments());
    }

    @Test
    public void canUpdateAVersion()
    {

        List<StoredEntity> entitiesList = entityUtils.readEntitiesFile(ENTITIES_FILE);
        Assertions.assertNotNull(entitiesList);
        StoredEntityData entity = (StoredEntityData) entitiesList.get(0);
        List result = entitiesMongo.createOrUpdate(Arrays.asList(entity));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(1, entities.countDocuments());
        Document doc = (Document)entities.find().iterator().next();
        Assertions.assertEquals(entity.getVersionId(), doc.getString(EntitiesMongo.VERSION_ID));
        Assertions.assertEquals(entity.getEntity().getPath(), ((Map)doc.get(EntitiesMongo.ENTITY_ATTRIBUTES)).get(PATH));
        Assertions.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc.get(EntitiesMongo.ENTITY_ATTRIBUTES)).get(CLASSIFIER_PATH));

        List result2 = entitiesMongo.createOrUpdate(Arrays.asList(entity));
        Assertions.assertNotNull(result2);
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(1, entities.countDocuments());

        MongoCollection entities1 = getMongoDatabase().getCollection(EntitiesMongo.COLLECTION);
        Assertions.assertNotNull(entities1);
        Assertions.assertEquals(1, entities1.countDocuments());
        Document doc1 = (Document)entities.find().iterator().next();
        Assertions.assertEquals(entity.getVersionId(), doc1.getString(EntitiesMongo.VERSION_ID));
        Assertions.assertEquals(entity.getEntity().getPath(), ((Map)doc1.get(EntitiesMongo.ENTITY_ATTRIBUTES)).get(PATH));
        Assertions.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc1.get(EntitiesMongo.ENTITY_ATTRIBUTES)).get(CLASSIFIER_PATH));

    }

    @Test
    public void updatingSameEntityDoesNotCreateAnewONe()
    {
        List<StoredEntity> entitiesList = entityUtils.readEntitiesFile(ENTITIES_FILE);
        Assertions.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        entitiesMongo.createOrUpdate(Arrays.asList(entity));
        entitiesMongo.createOrUpdate(Arrays.asList(entity));

        List<Entity> entities = entitiesMongo.getAllEntities("examples.metadata", "test", entity.getVersionId());
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(1, entities.size());

    }

    @Test
    public void canDeleteVersion()
    {
        entityUtils.loadEntities(ENTITIES_FILE);
        long count = entitiesMongo.getAllEntities("examples.metadata", "test", "2.2.0").size();
        Assertions.assertEquals(3, count);
        entitiesMongo.delete("examples.metadata", "test", "2.2.0");
        Assertions.assertEquals(0, entitiesMongo.getAllEntities("examples.metadata", "test", "2.2.0").size());
    }


    @Test
    public void canDeleteStoreEntities()
    {
        entityUtils.loadEntities(ENTITIES_FILE);
        long count = entitiesMongo.getStoredEntities("examples.metadata", "test").stream().count();
        Assertions.assertEquals(3, count);
        entitiesMongo.delete("examples.metadata", "test");
        Assertions.assertEquals(0, entitiesMongo.getStoredEntities("examples.metadata", "test").size());
    }

    @Test
    public void canCreateUpdateStoreEntities()
    {
        List<StoredEntity> entities = entityUtils.readEntitiesFile(ENTITIES_FILE);
        Assertions.assertEquals(3, entities.size());

        //lets do a change
        StoredEntityData first = (StoredEntityData) entities.get(0);
        Map<String,Object> entity = (Map<String, Object>) first.getEntity().getContent();
        entity.put("new","stuff");
        entitiesMongo.createOrUpdate(entities);

       Optional<Entity> found = entitiesMongo.getEntity(first.getGroupId(),first.getArtifactId(),first.getVersionId(),first.getEntity().getPath());
       Assertions.assertTrue(found.isPresent());
       Assertions.assertTrue(found.get().getContent().containsKey("new"));
       Assertions.assertEquals("stuff",found.get().getContent().get("new"));
    }

}

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
import org.finos.legend.depot.domain.entity.EntityDefinition;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.entity.StoredEntityOverview;
import org.finos.legend.depot.domain.status.StoreOperationResult;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TestUpdateRevisions extends TestStoreMongo
{
    URL ENTITIES_FILE = TestUpdateRevisions.class.getClassLoader().getResource("data/revision-entities.json");
    private EntitiesMongo revisionsMongo = new EntitiesMongo(mongoProvider);

    @Test
    public void canStoreANewRevision()
    {

        List<StoredEntity> entitiesList = readEntitiesFile(ENTITIES_FILE);
        Assert.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        entity.setVersionId(VersionValidator.MASTER_SNAPSHOT);
        StoreOperationResult result = revisionsMongo.newOrUpdate(entity);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getInsertedCount());
        Assert.assertEquals(0, result.getModifiedCount());

        MongoCollection entities = getMongoDatabase().getCollection(EntitiesMongo.ENTITIES_VERSIONS);
        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.countDocuments());
        Document doc = (Document)entities.find().iterator().next();
        Assert.assertEquals(VersionValidator.MASTER_SNAPSHOT, doc.getString(EntitiesMongo.VERSION_ID));
        Assert.assertEquals(entity.getEntity().getPath(), ((Map)doc.get(EntitiesMongo.ENTITY)).get("path"));
        Assert.assertEquals(entity.getEntity().getClassifierPath(), ((Map)doc.get(EntitiesMongo.ENTITY)).get("classifierPath"));
    }

    @Test
    public void updatingSameEntityDoesNotCreateANewOne()
    {
        List<StoredEntity> entitiesList = readEntitiesFile(ENTITIES_FILE);
        Assert.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        StoreOperationResult result = revisionsMongo.newOrUpdate(entity);
        Assert.assertNotNull(result);
        StoreOperationResult result1 = revisionsMongo.newOrUpdate(entity);
        Assert.assertNotNull(result1);

        List<Entity> entities = revisionsMongo.getAllEntities(entity.getGroupId(),entity.getArtifactId(),entity.getVersionId());
        Assert.assertNotNull(entities);
        Assert.assertEquals(1, entities.size());

    }

    @Test
    public void canUpdateRevision()
    {

        List<StoredEntity> entitiesList = readEntitiesFile(ENTITIES_FILE);
        Assert.assertNotNull(entitiesList);
        StoredEntity entity = entitiesList.get(0);
        entity.setVersionId(VersionValidator.MASTER_SNAPSHOT);
        StoreOperationResult result = revisionsMongo.newOrUpdate(entity);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getInsertedCount());
        Assert.assertEquals(0, result.getModifiedCount());


        EntityDefinition entityDefinition = entity.getEntity();
        String changes = "changed" + entityDefinition.getClassifierPath();
        entityDefinition.setClassifierPath(changes);
        Map content = entityDefinition.getContent();
        content.put("package", "changed::change");


        StoredEntity updated = new StoredEntity(entity.getGroupId(),entity.getArtifactId(),entity.getVersionId(), entity.isVersionedEntity(), entityDefinition);
        revisionsMongo.newOrUpdate(null, updated);

        Document doc = (Document) revisionsMongo.getCollection().find().iterator().next();
        Assert.assertNotNull(doc);
        
        Assert.assertEquals(entity.getVersionId(), doc.getString(EntitiesMongo.VERSION_ID));
        Assert.assertEquals(changes, ((Map)doc.get(EntitiesMongo.ENTITY)).get("classifierPath"));
        Assert.assertEquals("changed::change", ((Map)((Map)doc.get(EntitiesMongo.ENTITY)).get("content")).get("package"));

        revisionsMongo.deleteLatest(entity.getGroupId(),entity.getArtifactId(),entity.isVersionedEntity());

        Assert.assertTrue(revisionsMongo.getStoredEntities(entity.getGroupId(),entity.getArtifactId()).isEmpty());

    }

    @Test
    public void canCreateIndexesIfAbsent()
    {
        List<Document> indexes = new ArrayList();
        this.mongoProvider.getCollection(EntitiesMongo.ENTITIES_VERSIONS).listIndexes().forEach((Consumer<Document>)indexes::add);

        boolean result = revisionsMongo.createIndexesIfAbsent();
        Assert.assertTrue(result);
        List indexes1 = new ArrayList();
        this.mongoProvider.getCollection(EntitiesMongo.ENTITIES_VERSIONS).listIndexes().forEach((Consumer<Document>)indexes1::add);
        Assert.assertFalse(indexes1.isEmpty());
        Assert.assertEquals(6, indexes1.size());
    }

    @Test
    public void canQueryByClassifier()
    {
        setUpEntitiesDataFromFile(ENTITIES_FILE);
        List<StoredEntity> entities = revisionsMongo.findLatestEntitiesByClassifier("meta::pure::metamodel::type::Class", null, null, true, true);
        Assert.assertNotNull(entities);
        Assert.assertEquals(2, entities.size());
        for (StoredEntity entity : entities)
        {
            Assert.assertEquals("meta::pure::metamodel::type::Class", ((StoredEntityOverview)entity).getClassifierPath());
        }
    }

    @Test
    public void canDeleteRevision()
    {
        setUpEntitiesDataFromFile(ENTITIES_FILE);
        long count = revisionsMongo.getRevisionEntityCount("examples.metadata","test");
        Assert.assertEquals(8, count);
        revisionsMongo.deleteLatest("examples.metadata","test",false);
        Assert.assertEquals(4, revisionsMongo.getRevisionEntityCount("examples.metadata","test"));
        revisionsMongo.deleteLatest("examples.metadata","test",true);
        Assert.assertEquals(0, revisionsMongo.getRevisionEntityCount("examples.metadata","test"));
    }
}

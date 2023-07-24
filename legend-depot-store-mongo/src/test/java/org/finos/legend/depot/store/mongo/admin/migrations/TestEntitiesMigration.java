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

package org.finos.legend.depot.store.mongo.admin.migrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.store.model.entities.StoredEntityData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.GROUP_ID;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.VERSION_ID;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.ARTIFACT_ID;

public class TestEntitiesMigration extends TestStoreMongo
{
    MongoAdminStore mongoAdminStore = new MongoAdminStore(mongoProvider);

    @Before
    public void setupTestData()
    {
        setUpEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/versioned-entities-deletion.json"));
        Assert.assertEquals(3,mongoProvider.getCollection("entities").countDocuments());
    }

    private Bson getArtifactAndVersionFilter(String groupId, String artifactId, String versionId)
    {
        return and(eq(VERSION_ID, versionId),
                and(eq(GROUP_ID, groupId),
                        eq(ARTIFACT_ID, artifactId)));
    }

    @Test
    public void canDeleteVersionedEntities()
    {
        mongoProvider.getCollection("entities").updateOne(and(Filters.eq("entity.path", "examples::metadata::test::TestProfile"), getArtifactAndVersionFilter("examples.metadata", "test", "2.2.0")),
                Updates.combine(Updates.set("versionedEntity", true)));
        DeleteResult result = mongoAdminStore.deleteVersionedEntities();
        Assert.assertEquals(2, mongoProvider.getCollection("entities").countDocuments());
    }

    @Test
    public void canDeleteAllVersionedEntities()
    {
        mongoProvider.getCollection("entities").updateMany(getArtifactAndVersionFilter("examples.metadata", "test", "2.2.0"),
                Updates.combine(Updates.set("versionedEntity", true)));
        DeleteResult result = mongoAdminStore.deleteVersionedEntities();
        Assert.assertEquals(0, mongoProvider.getCollection("entities").countDocuments());
    }

    @Test
    public void canMigrateToStoredEntityData()
    {
        mongoProvider.getCollection("entities").drop();
        setUpLegacyEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/legacy-entities.json"));
        mongoAdminStore.migrateEntitiesToStoredEntityData();

        Assert.assertEquals(3, mongoProvider.getCollection("entities").countDocuments());
        Assert.assertNotNull(mongoProvider.getCollection("entities").find().first().getString("_type"));
        Assert.assertEquals("entityData", mongoProvider.getCollection("entities").find().first().getString("_type"));

        EntitiesMongo entitiesMongo = new EntitiesMongo(mongoProvider);
        List<StoredEntity> storedEntities = entitiesMongo.getAllStoredEntities();
        Assert.assertTrue(storedEntities.get(0) instanceof StoredEntityData);
        Assert.assertTrue(storedEntities.get(1) instanceof StoredEntityData);
        Assert.assertTrue(storedEntities.get(2) instanceof StoredEntityData);
    }

    protected void setUpLegacyEntitiesDataFromFile(URL versionedEntities)
    {
        try
        {
            readLegacyEntitiesFile(versionedEntities).forEach(entity ->
            {
                try
                {
                    getMongoDatabase().getCollection("entities").insertOne(Document.parse(new ObjectMapper().writeValueAsString(entity)));
                }
                catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test entity" + e.getMessage());
                }
            });
            Assert.assertNotNull(getMongoDatabase().getCollection("entities"));
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test entity" + e.getMessage());
        }
    }

    protected List<LegacyStoredEntities> readLegacyEntitiesFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<LegacyStoredEntities> entities = new ObjectMapper().readValue(jsonInput, new TypeReference<List<LegacyStoredEntities>>()
            {
            });
            Assert.assertNotNull("testing file" + fileName.getFile(), entities);
            return entities;
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test versioned entity metadata" + e.getMessage());
        }
        return null;
    }


    static class LegacyStoredEntities extends VersionedData
    {
        @JsonProperty
        @Deprecated
        private boolean versionedEntity;

        @JsonProperty
        private EntityDefinition entity;

        public LegacyStoredEntities()
        {

        }

        public boolean isVersionedEntity()
        {
            return versionedEntity;
        }

        public Entity getEntity()
        {
            return entity;
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }


    }
}


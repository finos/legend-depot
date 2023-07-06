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

import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.GROUP_ID;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.VERSION_ID;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.ARTIFACT_ID;

public class TestVersionedEntitiesMigration extends TestStoreMongo
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
        mongoProvider.getCollection("entities").updateOne(getArtifactAndVersionFilter("examples.metadata", "test", "2.2.0"),
                Updates.combine(Updates.set("versionedEntity", true)));
        DeleteResult result = mongoAdminStore.deleteVersionedEntities();
        Assert.assertEquals(2,mongoProvider.getCollection("entities").countDocuments());
    }

    @Test
    public void canDeleteAllVersionedEntities()
    {
        mongoProvider.getCollection("entities").updateMany(getArtifactAndVersionFilter("examples.metadata", "test", "2.2.0"),
                Updates.combine(Updates.set("versionedEntity", true)));
        DeleteResult result = mongoAdminStore.deleteVersionedEntities();
        Assert.assertEquals(0,mongoProvider.getCollection("entities").countDocuments());
    }
}

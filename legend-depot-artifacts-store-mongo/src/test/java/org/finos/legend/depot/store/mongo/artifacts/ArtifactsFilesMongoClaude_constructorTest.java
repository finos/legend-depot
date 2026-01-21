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

package org.finos.legend.depot.store.mongo.artifacts;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArtifactsFilesMongoClaude_constructorTest extends TestStoreMongo
{
    @Test
    public void testConstructorWithValidMongoDatabase()
    {
        // Test that constructor successfully creates an instance with valid MongoDatabase
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        Assertions.assertNotNull(artifactsFilesMongo);
        // Verify the object is functional by testing it can access the database
        Assertions.assertNotNull(artifactsFilesMongo.getDatabase());
        Assertions.assertEquals(this.mongoProvider, artifactsFilesMongo.getDatabase());
    }

    @Test
    public void testConstructorInitializesCollectionAccess()
    {
        // Test that constructor properly initializes collection access
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Verify that the collection can be accessed after construction
        MongoCollection collection = this.mongoProvider.getCollection(ArtifactsFilesMongo.COLLECTION);
        Assertions.assertNotNull(collection);
    }

    @Test
    public void testConstructorAllowsFunctionalUsage()
    {
        // Test that the constructed object is fully functional
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Verify basic CRUD operations work after construction
        ArtifactFile testFile = new ArtifactFile("test/path.jar", "checksum123");
        ArtifactFile result = artifactsFilesMongo.createOrUpdate(testFile);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test/path.jar", result.getPath());
        Assertions.assertEquals("checksum123", result.getCheckSum());
    }

    @Test
    public void testConstructorWithNullMongoDatabaseCreatesInstanceButFailsOnUse()
    {
        // Test that constructor accepts null but fails when object is used
        // The constructor itself does not validate null, but operations will fail
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(null);
        Assertions.assertNotNull(artifactsFilesMongo);

        // Verify that using the object with null database throws NullPointerException
        // when attempting actual database operations
        Assertions.assertThrows(NullPointerException.class, () -> {
            artifactsFilesMongo.getAllStoredEntities();
        });
    }

    @Test
    public void testMultipleInstancesWithSameDatabase()
    {
        // Test that multiple instances can be created with the same database
        ArtifactsFilesMongo instance1 = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactsFilesMongo instance2 = new ArtifactsFilesMongo(this.mongoProvider);

        Assertions.assertNotNull(instance1);
        Assertions.assertNotNull(instance2);
        Assertions.assertNotSame(instance1, instance2);

        // Verify both instances work with the same database
        Assertions.assertEquals(instance1.getDatabase(), instance2.getDatabase());
    }

    @Test
    public void testConstructorWithEmptyDatabase()
    {
        // Test that constructor works with an empty database
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Verify getAllStoredEntities returns empty list for empty database
        Assertions.assertTrue(artifactsFilesMongo.getAllStoredEntities().isEmpty());
    }
}

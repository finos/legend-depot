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
import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArtifactsFilesMongoClaude_getCollectionTest extends TestStoreMongo
{
    @Test
    public void testGetCollectionReturnsNonNull()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        MongoCollection collection = artifactsFilesMongo.getCollection();

        Assertions.assertNotNull(collection);
    }

    @Test
    public void testGetCollectionReturnsCorrectCollectionName()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        MongoCollection collection = artifactsFilesMongo.getCollection();

        Assertions.assertEquals("artifacts-files", collection.getNamespace().getCollectionName());
    }

    @Test
    public void testGetCollectionReturnsCorrectDatabase()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        MongoCollection collection = artifactsFilesMongo.getCollection();

        Assertions.assertEquals(
            this.mongoProvider.getName(),
            collection.getNamespace().getDatabaseName()
        );
    }

    @Test
    public void testGetCollectionConsistentResults()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Call getCollection multiple times
        MongoCollection collection1 = artifactsFilesMongo.getCollection();
        MongoCollection collection2 = artifactsFilesMongo.getCollection();

        // Both should refer to the same collection
        Assertions.assertEquals(
            collection1.getNamespace().getFullName(),
            collection2.getNamespace().getFullName()
        );
    }

    @Test
    public void testGetCollectionCanBeUsedForOperations()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        MongoCollection collection = artifactsFilesMongo.getCollection();

        // Verify the collection can be used for basic operations
        // Initially should be empty
        long count = collection.countDocuments();
        Assertions.assertEquals(0, count);

        // Add a document through the store
        ArtifactFile testFile = new ArtifactFile("test/path.jar", "checksum123");
        artifactsFilesMongo.createOrUpdate(testFile);

        // Verify count through the collection
        long countAfter = collection.countDocuments();
        Assertions.assertEquals(1, countAfter);
    }

    @Test
    public void testGetCollectionMatchesStaticConstant()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        MongoCollection collection = artifactsFilesMongo.getCollection();

        // Verify it matches the static COLLECTION constant
        Assertions.assertEquals(
            ArtifactsFilesMongo.COLLECTION,
            collection.getNamespace().getCollectionName()
        );
    }

    @Test
    public void testGetCollectionMultipleInstances()
    {
        // Create two instances
        ArtifactsFilesMongo instance1 = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactsFilesMongo instance2 = new ArtifactsFilesMongo(this.mongoProvider);

        MongoCollection collection1 = instance1.getCollection();
        MongoCollection collection2 = instance2.getCollection();

        // Both should point to the same collection in the database
        Assertions.assertEquals(
            collection1.getNamespace().getFullName(),
            collection2.getNamespace().getFullName()
        );

        // Operations through one instance should be visible through the other
        ArtifactFile testFile = new ArtifactFile("shared/path.jar", "checksum456");
        instance1.createOrUpdate(testFile);

        long count = collection2.countDocuments();
        Assertions.assertEquals(1, count);
    }
}

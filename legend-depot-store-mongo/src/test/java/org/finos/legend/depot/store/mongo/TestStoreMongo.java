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

package org.finos.legend.depot.store.mongo;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.finos.legend.depot.store.model.HasIdentifier;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.junit.jupiter.api.AfterEach;

public abstract class TestStoreMongo
{
    private MongoServer server = new MongoServer(new MemoryBackend());
    private MongoClient mongoClient = new MongoClient(new ServerAddress(server.bind()));
    protected MongoDatabase mongoProvider = mongoClient.getDatabase("test-db");

    @AfterEach
    public void tearDownData()
    {
        this.mongoProvider.drop();
    }

    protected MongoDatabase getMongoDatabase()
    {
        return mongoProvider;
    }

    protected void insertRaw(String collectionName, HasIdentifier rawObject)
    {
        mongoProvider.getCollection(collectionName).insertOne(BaseMongo.buildDocument(rawObject));
    }
}

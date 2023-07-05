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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;

@Deprecated
public final class VersionedEntitiesMigration
{
    private final MongoDatabase mongoDatabase;

    public VersionedEntitiesMigration(MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    @Deprecated
    public com.mongodb.client.result.DeleteResult versionedEntitiesDeletion()
    {
        MongoCollection<Document> collection = mongoDatabase.getCollection(EntitiesMongo.COLLECTION);
        return collection.deleteMany(Filters.eq("versionedEntity", true));
    }
}


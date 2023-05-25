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

package org.finos.legend.depot.store.notifications.store.mongo;

import com.mongodb.client.MongoDatabase;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.finos.legend.depot.store.notifications.store.api.NotificationsStore;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static org.finos.legend.depot.store.mongo.core.BaseMongo.createIndexesIfAbsent;

public class NotificationsStoreImpl implements NotificationsStore
{
    private final MongoDatabase mongoDatabase;

    @Inject
    public NotificationsStoreImpl(@Named("mongoDatabase") MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public List<String> createIndexes()
    {
        List<String> results = new ArrayList<>();
        results.addAll(createIndexesIfAbsent(mongoDatabase,NotificationsMongo.COLLECTION,NotificationsMongo.buildIndexes()));
        results.addAll(createIndexesIfAbsent(mongoDatabase, NotificationsQueueMongo.COLLECTION,NotificationsQueueMongo.buildIndexes()));
        return results;
    }
}

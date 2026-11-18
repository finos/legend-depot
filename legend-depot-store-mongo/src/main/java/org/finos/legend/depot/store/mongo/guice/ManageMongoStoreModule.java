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

package org.finos.legend.depot.store.mongo.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.admin.metrics.StorageMetricsHandler;
import org.finos.legend.depot.store.mongo.resources.MongoStoreAdministrationResource;

import jakarta.inject.Named;

public class ManageMongoStoreModule extends PrivateModule
{
  
    @Override
    protected void configure()
    {
        bind(MongoStoreAdministrationResource.class);
        bind(StorageMetricsHandler.class);

        expose(MongoStoreAdministrationResource.class);
        expose(MongoAdminStore.class);
        expose(StorageMetricsHandler.class);
    }

    @Provides
    @Singleton
    MongoAdminStore buildMongoAdminStore(@Named("mongoDatabase")MongoDatabase mongoDatabase)
    {
        return new MongoAdminStore(mongoDatabase);
    }

}

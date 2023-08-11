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

package org.finos.legend.depot.store.mongo.admin.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.admin.api.metrics.StorageMetrics;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.admin.metrics.StorageMetricsHandler;
import org.finos.legend.depot.store.mongo.admin.migrations.MongoMigrations;
import org.finos.legend.depot.store.mongo.resources.MongoStoreAdministrationResource;
import org.finos.legend.depot.store.mongo.resources.MongoStoreMigrationsResource;

import javax.inject.Named;

public class ManageAdminDataStoreMongoModule extends PrivateModule
{
  
    @Override
    protected void configure()
    {
        bind(MongoStoreAdministrationResource.class);
        bind(MongoStoreMigrationsResource.class);
        bind(MongoMigrations.class);
        bind(ArtifactsFilesStore.class).to(ArtifactsFilesMongo.class);
        bind(StorageMetrics.class).to(StorageMetricsHandler.class);

        expose(MongoStoreAdministrationResource.class);
        expose(MongoStoreMigrationsResource.class);
        expose(MongoMigrations.class);
        expose(ArtifactsFilesStore.class);
        expose(StorageMetrics.class);
        expose(MongoAdminStore.class);
    }

    @Provides
    @Singleton
    MongoAdminStore buildMongoAdminStore(@Named("mongoDatabase")MongoDatabase mongoDatabase)
    {
        return new MongoAdminStore(mongoDatabase);
    }


    @Singleton
    @Provides
    @Named("register-indexes")
    public boolean registerIndexes(MongoAdminStore adminStore)
    {
        adminStore.registerIndexes(ArtifactsFilesMongo.COLLECTION,ArtifactsFilesMongo.buildIndexes());
        return true;
    }

}

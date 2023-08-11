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
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.versionedEntities.UpdateVersionedEntities;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.admin.migrations.MongoEntitiesMigrations;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.versionedEntities.VersionedEntitiesMongo;
import org.finos.legend.depot.store.resources.EntitiesMigrationResource;

import javax.inject.Named;

public class EntitiesMigrationsStoreMongoModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(MongoEntitiesMigrations.class);
        bind(EntitiesMigrationResource.class);

        expose(MongoEntitiesMigrations.class);
        expose(EntitiesMigrationResource.class);
    }
}

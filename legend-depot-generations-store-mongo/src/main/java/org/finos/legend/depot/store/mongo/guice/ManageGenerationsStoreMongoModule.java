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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.store.api.generations.FileGenerations;
import org.finos.legend.depot.store.api.generations.UpdateFileGenerations;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;

import jakarta.inject.Named;

public class ManageGenerationsStoreMongoModule extends GenerationsStoreMongoModule
{
    @Override
    protected void configure()
    {
        super.configure();

        bind(FileGenerations.class).to(FileGenerationsMongo.class);
        bind(UpdateFileGenerations.class).to(FileGenerationsMongo.class);

        expose(FileGenerations.class);
        expose(UpdateFileGenerations.class);

    }

    @Provides
    @Singleton
    @Named("generation-indexes")
    public boolean registerGenerationsIndexes(MongoAdminStore adminStore)
    {
        adminStore.registerIndexes(FileGenerationsMongo.COLLECTION,FileGenerationsMongo.buildIndexes());
        return  true;
    }
}

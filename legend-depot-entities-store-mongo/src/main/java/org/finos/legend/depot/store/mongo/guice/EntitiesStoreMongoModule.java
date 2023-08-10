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
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.versionedEntities.VersionedEntities;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.versionedEntities.VersionedEntitiesMongo;

public class EntitiesStoreMongoModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(Entities.class).to(EntitiesMongo.class);
        bind(VersionedEntities.class).to(VersionedEntitiesMongo.class);

        expose(Entities.class);
        expose(VersionedEntities.class);
    }
}

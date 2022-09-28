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

package org.finos.legend.depot.store.admin;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.store.admin.api.ManageSchedulesService;
import org.finos.legend.depot.store.admin.api.ManageStoreService;
import org.finos.legend.depot.store.admin.resources.AdminResource;
import org.finos.legend.depot.store.admin.resources.SchedulesResource;
import org.finos.legend.depot.store.admin.services.ManageStoreServiceImpl;
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.admin.store.mongo.MongoAdminStore;
import org.finos.legend.depot.store.admin.store.mongo.MongoSchedules;

public class StoreAdminModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(AdminResource.class);
        bind(MongoAdminStore.class);
        bind(MongoSchedules.class);
        bind(ManageSchedulesService.class).to(MongoSchedules.class);
        bind(ManageStoreService.class).to(ManageStoreServiceImpl.class);
        bind(SchedulesResource.class);

        expose(AdminResource.class);
        expose(SchedulesResource.class);
        expose(ManageStoreService.class);
        expose(SchedulesFactory.class);

    }

    @Provides
    @Singleton
    public SchedulesFactory getFactory(ManageSchedulesService manageSchedulesService)
    {
        return new SchedulesFactory(manageSchedulesService);
    }

}

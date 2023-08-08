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
import org.finos.legend.depot.store.admin.api.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.admin.api.schedules.SchedulesStore;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.admin.schedules.ScheduleInstancesMongo;
import org.finos.legend.depot.store.mongo.admin.schedules.SchedulesMongo;

import javax.inject.Named;

public class SchedulesStoreMongoModule extends PrivateModule
{
    @Override
    protected void configure()
    {

        bind(SchedulesStore.class).to(SchedulesMongo.class);
        bind(ScheduleInstancesStore.class).to(ScheduleInstancesMongo.class);

        expose(SchedulesStore.class);
        expose(ScheduleInstancesStore.class);
    }


}

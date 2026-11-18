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
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.store.mongo.admin.metrics.StorageMetricsHandler;

import jakarta.inject.Named;

public class ManageMongoStoreSchedulesModule extends PrivateModule
{
  
    @Override
    protected void configure()
    {
    }




    @Provides
    @Singleton
    @Named("storage-metrics")
    boolean scheduleStorageMetrics(SchedulesFactory schedulesFactory, StorageMetricsHandler storageMetrics)
    {
        storageMetrics.init();
        schedulesFactory.register("storage-metrics", 5 * SchedulesFactory.MINUTE, 5 * SchedulesFactory.MINUTE,storageMetrics::reportMetrics);
        return true;
    }

}

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

package org.finos.legend.depot.store.status;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.status.resources.StatusStoreResource;
import org.finos.legend.depot.store.status.services.StoreStatusService;
import javax.inject.Named;
import java.time.LocalDateTime;


public class StoreStatusModule extends PrivateModule
{
    private static final String MISMATCH_VERSIONS_SCHEDULE = "versions-mismatch-schedule";

    @Override
    protected void configure()
    {
        bind(StoreStatusService.class);
        expose(StoreStatusService.class);

        bind(StatusStoreResource.class);
        expose(StatusStoreResource.class);
    }

    @Provides
    @Singleton
    @Named("check-versions-mismatch")
    boolean initVersionsMismatchDaemon(SchedulesFactory schedulesFactory, StoreStatusService storeStatusService)
    {
        schedulesFactory.register(MISMATCH_VERSIONS_SCHEDULE, LocalDateTime.now().plusMinutes(40), 6 * 36000, false,storeStatusService::getVersionsMismatches);
        return true;
    }
}

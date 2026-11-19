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

package org.finos.legend.depot.services.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.services.api.notifications.NotificationsService;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;

import jakarta.inject.Named;

public class NotificationsSchedulesModule extends PrivateModule
{

    private static final String CLEANUP_NOTIFICATIONS_SCHEDULE = "clean-notifications-schedule";

    @Override
    protected void configure()
    {
    }

    @Provides
    @Named("clean-old-notifications")
    @Singleton
    boolean notificationsCleanUp(SchedulesFactory schedulesFactory, NotificationsService notificationsService)
    {
        schedulesFactory.register(CLEANUP_NOTIFICATIONS_SCHEDULE, SchedulesFactory.MINUTE, 1 * SchedulesFactory.HOUR,  () -> notificationsService.deleteOldNotifications(30));
        return true;
    }
}

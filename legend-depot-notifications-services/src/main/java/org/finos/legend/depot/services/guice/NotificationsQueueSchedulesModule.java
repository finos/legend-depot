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
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.services.notifications.NotificationsQueueManager;

import jakarta.inject.Named;

public class NotificationsQueueSchedulesModule extends PrivateModule
{

    private static final String QUEUE_OBSERVER = "queue-observer";

    @Override
    protected void configure()
    {
    }

    @Provides
    @Singleton
    @Named("queue-observer")
    boolean initQueue(SchedulesFactory schedulesFactory, QueueManagerConfiguration config, NotificationsQueueManager notificationsManager)
    {
        long numberOfWorkers = config.getNumberOfQueueWorkers();
        if (numberOfWorkers <= 0)
        {
            throw new IllegalArgumentException("Number of queue workers must be a positive number >1 ");
        }
        for (long worker = 1;numberOfWorkers >= worker;worker++)
        {
            schedulesFactory.register(QUEUE_OBSERVER + "_" + worker, config.getQueueDelay(), config.getQueueInterval(), notificationsManager::handle);
        }
        return true;
    }


}

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

package org.finos.legend.depot.store.notifications;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.QueueManagerConfiguration;
import org.finos.legend.depot.store.notifications.resources.NotificationsManagerResource;
import org.finos.legend.depot.store.notifications.services.NotificationsQueueManager;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.notifications.store.mongo.QueueMongo;

import javax.inject.Named;
import java.time.LocalDateTime;

public class NotificationsModule extends PrivateModule
{
    private static final String QUEUE_OBSERVER = "queue-observer";

    @Override
    protected void configure()
    {

        bind(NotificationsManager.class).to(NotificationsQueueManager.class);
        bind(Notifications.class).to(NotificationsMongo.class);
        bind(Queue.class).to(QueueMongo.class);
        bind(NotificationsManagerResource.class);

        expose(Notifications.class);
        expose(Queue.class);
        expose(NotificationsManager.class);
        expose(NotificationsManagerResource.class);
    }

    @Provides
    @Singleton
    @Named("queue-observer")
    NotificationsQueueManager initQueue(SchedulesFactory schedulesFactory, QueueManagerConfiguration config, ProjectsService projectsService, Notifications events, Queue queue, NotificationEventHandler notificationHandler)
    {
        NotificationsQueueManager eventsQueueManager = new NotificationsQueueManager(projectsService, events, queue, notificationHandler);
        schedulesFactory.register(QUEUE_OBSERVER, LocalDateTime.now().plusNanos(config.getQueueDelay() * 1000000L), config.getQueueInterval(), true, eventsQueueManager::handle);
        return eventsQueueManager;
    }

}

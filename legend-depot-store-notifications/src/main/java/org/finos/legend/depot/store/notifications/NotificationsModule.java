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
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.QueueManagerConfiguration;
import org.finos.legend.depot.store.notifications.resources.NotificationsManagerResource;
import org.finos.legend.depot.store.notifications.services.NotificationsQueueManager;
import org.finos.legend.depot.store.notifications.store.api.NotificationsStore;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsQueueMongo;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsStoreImpl;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;

import javax.inject.Named;
import java.time.LocalDateTime;

import static org.finos.legend.depot.store.notifications.services.NotificationsQueueManager.NOTIFICATIONS_COUNTER;
import static org.finos.legend.depot.store.notifications.services.NotificationsQueueManager.NOTIFICATIONS_COUNTER_HELP;
import static org.finos.legend.depot.store.notifications.services.NotificationsQueueManager.QUEUE_WAITING;
import static org.finos.legend.depot.store.notifications.services.NotificationsQueueManager.QUEUE_WAITING_HELP;

public class NotificationsModule extends PrivateModule
{
    private static final String QUEUE_OBSERVER = "queue-observer";
    private static final String CLEANUP_NOTIFICATIONS_SCHEDULE = "clean-notifications-schedule";

    @Override
    protected void configure()
    {

        bind(NotificationsManager.class).to(NotificationsQueueManager.class);
        bind(Notifications.class).to(NotificationsMongo.class);
        bind(Queue.class).to(NotificationsQueueMongo.class);
        bind(NotificationsStore.class).to(NotificationsStoreImpl.class);
        bind(NotificationsManagerResource.class);

        expose(Notifications.class);
        expose(Queue.class);
        expose(NotificationsManagerResource.class);
    }

    @Provides
    @Singleton
    @Named("queue-observer")
    boolean initQueue(SchedulesFactory schedulesFactory, QueueManagerConfiguration config, NotificationsManager notificationsManager)
    {
        long numberOfWorkers = config.getNumberOfQueueWorkers();
        if (numberOfWorkers <= 0)
        {
            throw new IllegalArgumentException("Number of queue workers must be a positive number >1 ");
        }
        for (long worker = 1;numberOfWorkers >= worker;worker++)
        {
            schedulesFactory.register(QUEUE_OBSERVER + "_" + worker, LocalDateTime.now().plusNanos(config.getQueueDelay() * 1000000L), config.getQueueInterval(), true, notificationsManager::handle);
        }
        return true;
    }

    @Provides
    @Named("notifications-metrics")
    @Singleton
    boolean registerMetrics(PrometheusMetricsHandler metricsHandler)
    {
        metricsHandler.registerCounter(NOTIFICATIONS_COUNTER, NOTIFICATIONS_COUNTER_HELP);
        metricsHandler.registerGauge(QUEUE_WAITING, QUEUE_WAITING_HELP);
        return true;
    }

    @Provides
    @Named("clean-old-notifications")
    @Singleton
    boolean notificationsCleanUp(SchedulesFactory schedulesFactory, NotificationsManager eventsQueueManager)
    {
        schedulesFactory.register(CLEANUP_NOTIFICATIONS_SCHEDULE, LocalDateTime.now().plusSeconds(1), 12 * 3600000, false, () -> eventsQueueManager.deleteOldNotifications(60));
        return true;
    }

}

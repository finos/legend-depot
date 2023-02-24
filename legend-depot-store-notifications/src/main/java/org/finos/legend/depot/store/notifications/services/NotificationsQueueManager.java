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

package org.finos.legend.depot.store.notifications.services;

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.notifications.EventPriority;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50SSSrcLnSeg;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class NotificationsQueueManager implements NotificationsManager
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationsQueueManager.class);
    public static final String NOTIFICATIONS_COUNTER = "notifications";
    public static final String NOTIFICATIONS_COUNTER_HELP = "total notifications received";
    public static final String QUEUE_WAITING = "queue_waiting";
    public static final String QUEUE_WAITING_HELP = "waiting in queue";
    public static final String DELIMITER = ",";

    private final Notifications events;
    private final Queue queue;
    private final NotificationEventHandler eventHandler;
    private final ProjectsService projectsService;

    @Inject
    public NotificationsQueueManager(ProjectsService projectsService, Notifications events, Queue queue, NotificationEventHandler eventHandler)
    {
        this.events = events;
        this.queue = queue;
        this.eventHandler = eventHandler;
        this.projectsService = projectsService;
    }


    public int handle()
    {
        long waitingInQueue = queue.size();
        PrometheusMetricsFactory.getInstance().setGauge(QUEUE_WAITING,waitingInQueue);
        LOGGER.info("waiting in queue {}",waitingInQueue);
        return TracerFactory.get().executeWithTrace(ResourceLoggingAndTracing.HANDLE_EVENTS_IN_QUEUE, () -> handleEvents(queue.getFirstInQueue()));
    }

    private int handleEvents(Optional<MetadataNotification> foundEvent)
    {
        if (foundEvent.isPresent())
        {
            handleEvent(foundEvent.get());
            LOGGER.info("Finished processing events");
            return 1;
        }
        return 0;
    }


    void handleEvent(MetadataNotification event)
    {
        PrometheusMetricsFactory.getInstance().incrementCount(NOTIFICATIONS_COUNTER);
        List<String> validationErrors = eventHandler.validateEvent(event);
        if (!validationErrors.isEmpty())
        {
            String message = String.format("eventId:[%s],parentEventId:[%s],gav:[%s-%s-%s],attempt [%s] completed with validation errors [%s]",
                    event.getEventId(), event.getParentEventId(),event.getGroupId(),event.getArtifactId(),event.getVersionId(),event.getAttempt(),String.join(DELIMITER,validationErrors));
            LOGGER.error(message);
            events.complete(event.addError(message));
            PrometheusMetricsFactory.getInstance().incrementErrorCount(NOTIFICATIONS_COUNTER);
            return;
        }

        MetadataEventResponse response = new MetadataEventResponse();
        try
        {
            event.increaseAttempts();
            String message = String.format("Handling eventId:[%s],parentEventId:[%s],gav: [%s-%s-%s],attempt [%s]",
                    event.getEventId(),event.getParentEventId(),event.getGroupId(),event.getArtifactId(),event.getVersionId(),event.getAttempt());
            response.addMessage(message);
            LOGGER.info(message);
            response.combine(eventHandler.handleEvent(event));
        }
        catch (Exception e)
        {
            response.addError(e.getMessage());
        }
        finally
        {
            if (response.hasErrors())
            {
                PrometheusMetricsFactory.getInstance().incrementErrorCount(NOTIFICATIONS_COUNTER);
                if (event.retriesExceeded())
                {
                    String messageRetry = String.format("eventId:[%s],parentEventId:[%s],gav:[%s-%s-%s], attempt [%s] completed with errors [%s] will not retry [%s] maximum retries exceeded",
                            event.getEventId(), event.getParentEventId(), event.getGroupId(), event.getArtifactId(), event.getVersionId(), event.getAttempt(), String.join(DELIMITER, response.getErrors()), event.getMaxAttempts());
                    event.addError(messageRetry);
                    LOGGER.error(messageRetry);
                    events.complete(event.combineResponse(response));
                }
                else
                {
                    String message = String.format("eventId:[%s],parentEventId:[%s],gav:[%s-%s-%s], attempt [%s] completed with errors [%s] will retry",
                            event.getEventId(), event.getParentEventId(), event.getGroupId(), event.getArtifactId(), event.getVersionId(), event.getAttempt(), String.join(DELIMITER, response.getErrors()));
                    response.addError(message);
                    LOGGER.error(message);
                    queue.push(event.combineResponse(response).setFullUpdate(true));
                }
            }
            else
            {
                events.complete(event.combineResponse(response));
                LOGGER.info("eventId:[{}],parentEventId:[{}],gav: [{}-{}-{}] ,attempt [{}] completed successfully", event.getEventId(), event.getParentEventId(), event.getGroupId(), event.getArtifactId(), event.getVersionId(), event.getAttempt());
            }
        }
    }


    @Override
    public String notify(String projectId, String groupId, String artifactId, String versionId)
    {
        PrometheusMetricsFactory.getInstance().incrementCount(NOTIFICATIONS_COUNTER);
        //create a notification event with fullUpdate/transitive flag = false/false
        //it means, it will only process changed master-SNAPSHOT jar files and wont force dependencies to load
        MetadataNotification event = new MetadataNotification(projectId, groupId, artifactId, versionId,false,false, null, EventPriority.HIGH);
        List<String> validationResponse = eventHandler.validateEvent(event);
        if (validationResponse.isEmpty())
        {
            String eventId = queue.push(event);
            TracerFactory.get().log("eventId=" + eventId);
            LOGGER.info("Notification received : project[{}] [{}-{}-{}], eventId:[{}]", projectId, groupId, artifactId, versionId, eventId);
            return eventId;
        }
        else
        {
            return String.format("Notification failed validation : project[{}] [{}-{}-{}]",projectId, groupId, artifactId, versionId, String.join(",",validationResponse));
        }

    }

    @Override
    public List<MetadataNotification> findProcessedEvents(String group, String artifact, String version, String eventId,String parentId, Boolean success, LocalDateTime from, LocalDateTime to)
    {
        return this.events.find(group,artifact,version,eventId,parentId,success,from,to);
    }

    @Override
    public Optional<MetadataNotification> getProcessedEvent(String eventId)
    {
        return this.events.get(eventId);
    }

    @Override
    public List<MetadataNotification> getAllInQueue()
    {
        return this.queue.getAll();
    }

    @Override
    public Optional<MetadataNotification> findInQueue(String eventId)
    {
        return this.queue.get(eventId);
    }

    @Override
    public long deleteOldNotifications(long days)
    {
        LocalDateTime timeToLive = LocalDateTime.now().minusDays(days);
        List<MetadataNotification> notifications = this.events.find(null,null,null,null,null,null,null,timeToLive);
        notifications.forEach(notification -> this.events.delete(notification.getId()));
        LOGGER.info("deleted [{}] notifications older than [{}] days",notifications.size(),days);
        return notifications.size();
    }

    public void handleAll()
    {
        Optional<MetadataNotification> event = queue.getFirstInQueue();
        do
        {
            handleEvents(event);
            event = queue.getFirstInQueue();
        }
        while (event.isPresent());
    }

    @Override
    public long waitingInQueue()
    {
        long waiting = this.queue.size();

        return waiting;
    }

    @Override
    public long purgeQueue()
    {
        return this.queue.deleteAll();
    }
}

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
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;

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
            MetadataNotification event = foundEvent.get();
            if (event.retriesExceeded())
            {
                String message = String.format("%s [%s-%s-%s] event has exceeded %s maximum retries", event.getEventId(),event.getGroupId(),event.getArtifactId(),event.getVersionId(),event.getMaxRetries());
                event.getResponse().addError(message);
                events.complete(event);
                LOGGER.info(message);
            }
            else
            {
                handleEvent(event);
            }
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
            event.addError(String.join(DELIMITER,validationErrors));
            events.complete(event);
            LOGGER.info("event {}-{}-{} completed with validation errors [{}]", event.getGroupId(),event.getArtifactId(),event.getVersionId(),event.getErrors());
            PrometheusMetricsFactory.getInstance().incrementErrorCount(NOTIFICATIONS_COUNTER);
            return;
        }

        MetadataEventResponse response = new MetadataEventResponse();
        try
        {
            response.combine(eventHandler.handleEvent(event));
        }
        catch (Exception e)
        {
            response.addError(e.getMessage());
        }
        if (response.hasErrors())
        {
            queue.push(event.increaseRetries().setResponse(response));
            LOGGER.info("event {}-{}-{} completed with errors [{}]", event.getGroupId(),event.getArtifactId(),event.getVersionId(),response.getErrors());
            PrometheusMetricsFactory.getInstance().incrementErrorCount(NOTIFICATIONS_COUNTER);
        }
        else
        {
            events.complete(event.setResponse(response));
            LOGGER.info("event {}-{}-{} completed successfully", event.getGroupId(),event.getArtifactId(),event.getVersionId());
        }
    }

    private void validateMavenCoordinates(String projectId, String groupId, String artifactId)
    {
        Optional<StoreProjectData> project = projectsService.findCoordinates(groupId, artifactId);
        if (project.isPresent() && !project.get().getProjectId().equals(projectId))
        {
            throw new IllegalArgumentException(String.format("%s:%s coordinates already registered with project %s", groupId, artifactId, project.get().getProjectId()));
        }
    }

    @Override
    public String notify(String projectId, String groupId, String artifactId, String versionId)
    {
        PrometheusMetricsFactory.getInstance().incrementCount(NOTIFICATIONS_COUNTER);
        validateMavenCoordinates(projectId, groupId, artifactId);
        //we create a notification event with fullUpdate/transitive flag set to false(ie partial update)
        //this means, it will only process changed jar files and will only handle those entities,etc
        MetadataNotification event = new MetadataNotification(projectId, groupId, artifactId, versionId,false,false, null);
        return queue.push(event);
    }

    @Override
    public List<MetadataNotification> findProcessedEvents(String group, String artifact, String version, String parentId, Boolean success, LocalDateTime from, LocalDateTime to)
    {
        return this.events.find(group,artifact,version,parentId,success,from,to);
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
        List<MetadataNotification> notifications = this.events.find(null,null,null,null,null,null,timeToLive);
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

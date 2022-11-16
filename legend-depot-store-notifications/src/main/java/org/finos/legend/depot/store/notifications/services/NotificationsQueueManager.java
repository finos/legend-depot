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
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class NotificationsQueueManager implements NotificationsManager
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationsQueueManager.class);
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
        return TracerFactory.get().executeWithTrace(ResourceLoggingAndTracing.HANDLE_EVENTS_IN_QUEUE, () -> handleEvents(queue.getFirstInQueue()));
    }

    private int handleEvents(Optional<MetadataNotification> foundEvent)
    {
        if (foundEvent.isPresent())
        {
            MetadataNotification event = foundEvent.get();
            if (event.retriesExceeded())
            {
                events.complete(event.failEvent("Max number of tries exceed"));
                LOGGER.info(" event has exceeded maximum retries {}", event.getEventId());
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
        List<String> validationErrors = eventHandler.validateEvent(event);
        if (!validationErrors.isEmpty())
        {
            events.completeWithOutRetry(event.failEvent(String.join(",",validationErrors)));
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
            queue.push(event.increaseRetries().setStatus(MetadataEventStatus.RETRY).addErrors(response.getErrors()));
            LOGGER.info("event completed with errors [{}]", response.getErrors());
        }
        else
        {
            events.complete(event.completedSuccessfully());
            LOGGER.info("event completed successfully");
        }
    }

    private void validateMavenCoordinates(String projectId, String groupId, String artifactId)
    {
        Optional<ProjectData> project = projectsService.find(groupId, artifactId);
        if (project.isPresent() && !project.get().getProjectId().equals(projectId))
        {
            throw new IllegalArgumentException(String.format("%s:%s coordinates already registered with project %s", groupId, artifactId, project.get().getProjectId()));
        }
    }

    @Override
    public String notify(String projectId, String groupId, String artifactId, String versionId, int maxRetries)
    {
        validateMavenCoordinates(projectId, groupId, artifactId);
        //we create a notification event with fullUpdate/transitive flag set to false(ie partial update)
        //this means, it will only process changed jar files and will only handle those entities,etc
        MetadataNotification event = new MetadataNotification(projectId, groupId, artifactId, versionId, false,false,maxRetries);
        return queue.push(event);
    }

    @Override
    public List<MetadataNotification> findProcessedEvents(LocalDateTime from, LocalDateTime to)
    {
        return this.events.find(from,to);
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
}
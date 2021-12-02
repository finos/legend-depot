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

import org.finos.legend.depot.domain.EntityValidator;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Optional;

public final class NotificationsQueueManager
{

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationsQueueManager.class);
    public static final String ALL = "all";
    private final ManageProjectsService projects;
    private final Notifications events;
    private final Queue queue;
    private final ArtifactsRefreshService artifactsRefreshService;

    @Inject
    public NotificationsQueueManager(Notifications events, Queue queue, ManageProjectsService projects, ArtifactsRefreshService artifactsRefreshService)
    {
        this.events = events;
        this.queue = queue;
        this.projects = projects;
        this.artifactsRefreshService = artifactsRefreshService;
    }

    public int run()
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
                if (isAllProjectsVersionsEvent(event))
                {
                    handleRefreshAllEvent();
                }
                else
                {
                    handleEvent(event);
                }
            }

            LOGGER.info("Finished processing events");
            return 1;
        }
        return 0;
    }

    private boolean isAllProjectsVersionsEvent(MetadataNotification event)
    {
        return ALL.equalsIgnoreCase(event.getVersionId()) && ALL.equalsIgnoreCase(event.getGroupId()) && ALL.equalsIgnoreCase(event.getArtifactId());
    }

    private void handleRefreshAllEvent()
    {
        projects.getAll().forEach(p ->
        {
            queue.push(new MetadataNotification(p.getProjectId(), p.getGroupId(), p.getArtifactId(), ALL, true));
        });
    }

    private void handleEvent(MetadataNotification event)
    {
        if (!isValidEvent(event))
        {
            events.completeWithOutRetry(event.failEvent("Invalid project configuration provided"));
            return;
        }
        if (!ALL.equalsIgnoreCase(event.getVersionId()) && !VersionValidator.isValid(event.getVersionId()))
        {
            events.completeWithOutRetry(event.failEvent(String.format("Invalid versionId %s provided ", event.getVersionId())));
            return;
        }

        Optional<ProjectData> existingProject = projects.find(event.getGroupId(), event.getArtifactId());
        if (!existingProject.isPresent())
        {
            projects.createOrUpdate(new ProjectData(event.getProjectId(), event.getGroupId(), event.getArtifactId()));
        }

        MetadataEventResponse response = ALL.equalsIgnoreCase(event.getVersionId()) ?
                artifactsRefreshService.refreshAllProjectArtifacts(event.getGroupId(), event.getArtifactId()) :
                artifactsRefreshService.refreshProjectVersionArtifacts(event.getGroupId(), event.getArtifactId(), event.getVersionId(), event.isFullUpdate());
        if (response != null)
        {
            if (response.hasErrors())
            {
                queue.push(event.increaseRetries().setStatus(MetadataEventStatus.RETRY).addErrors(response.getErrors()));
            }
            else
            {
                events.complete(event.completedSuccessfully());
            }
        }
        LOGGER.info("event completed with status {}", event.getStatus());

    }


    private boolean isValidEvent(MetadataNotification event)
    {
        return EntityValidator.isValidGroupId(event.getGroupId())
                && EntityValidator.isValidArtifactId(event.getArtifactId());
    }
}

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

package org.finos.legend.depot.services.notifications;

import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.Priority;
import org.finos.legend.depot.services.api.notifications.NotificationHandler;
import org.finos.legend.depot.store.api.notifications.Notifications;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;
import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public final class NotificationsQueueManager
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationsQueueManager.class);
    public static final String NOTIFICATIONS_COUNTER = "notifications";
    public static final String NOTIFICATIONS_COUNTER_HELP = "total notifications received";
    public static final String QUEUE_WAITING = "queue_waiting";
    public static final String QUEUE_WAITING_HELP = "waiting in queue";
    public static final String DELIMITER = ",";
    public static final String NOTIFICATION_COMPLETE = "notification_complete";
    public static final String NOTIFICATION_COMPLETE_HELP = " time to precess notification";

    private final Notifications notifications;
    private final Queue queue;
    private final NotificationHandler eventHandler;

    @Inject
    public NotificationsQueueManager(Notifications notifications, Queue queue, NotificationHandler eventHandler)
    {
        this.notifications = notifications;
        this.queue = queue;
        this.eventHandler = eventHandler;
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
        List<String> validationErrors = eventHandler.validate(event);
        if (!validationErrors.isEmpty())
        {
            String message = String.format("eventId:[%s],parentEventId:[%s],gav:[%s-%s-%s],attempt [%s] completed with validation errors [%s]",
                    event.getEventId(), event.getParentEventId(),event.getGroupId(),event.getArtifactId(),event.getVersionId(),event.getAttempt(),String.join(DELIMITER,validationErrors));
            LOGGER.error(message);
            notifications.createOrUpdate(event.addError(message).complete());
            PrometheusMetricsFactory.getInstance().incrementErrorCount(NOTIFICATIONS_COUNTER);
            return;
        }

        MetadataNotificationResponse response = new MetadataNotificationResponse();
        try
        {
            event.increaseAttempts();
            String message = String.format("Handling eventId:[%s],parentEventId:[%s],gav: [%s-%s-%s],attempt [%s]",
                    event.getEventId(),event.getParentEventId(),event.getGroupId(),event.getArtifactId(),event.getVersionId(),event.getAttempt());
            response.addMessage(message);
            LOGGER.info(message);
            response.combine(eventHandler.handleNotification(event));
        }
        catch (Exception e)
        {
            response.addError(e.getMessage());
        }
        finally
        {
            if (response.hasErrors())
            {
            
                if (event.retriesExceeded())
                {
                    String messageRetry = String.format("eventId:[%s],parentEventId:[%s],gav:[%s-%s-%s], attempt [%s] completed with errors [%s] will not retry [%s] maximum retries exceeded",
                            event.getEventId(), event.getParentEventId(), event.getGroupId(), event.getArtifactId(), event.getVersionId(), event.getAttempt(), String.join(DELIMITER, response.getErrors()), event.getMaxAttempts());
                    event.addError(messageRetry);
                    LOGGER.error(messageRetry);
                    notifications.createOrUpdate(event.combineResponse(response).complete());
                    PrometheusMetricsFactory.getInstance().observeHistogram(NOTIFICATION_COMPLETE,event.getCreated().getTime(),System.currentTimeMillis(),event.getEventPriority().name());
                    PrometheusMetricsFactory.getInstance().incrementErrorCount(NOTIFICATIONS_COUNTER);
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
                notifications.createOrUpdate(event.combineResponse(response).complete());
                PrometheusMetricsFactory.getInstance().observeHistogram(NOTIFICATION_COMPLETE,event.getCreated().getTime(),System.currentTimeMillis(),event.getEventPriority().name());
                LOGGER.info("eventId:[{}],parentEventId:[{}],gav: [{}-{}-{}] ,attempt [{}] completed successfully", event.getEventId(), event.getParentEventId(), event.getGroupId(), event.getArtifactId(), event.getVersionId(), event.getAttempt());
            }
        }
    }



    public String notify(String projectId, String groupId, String artifactId, String versionId)
    {
        PrometheusMetricsFactory.getInstance().incrementCount(NOTIFICATIONS_COUNTER);
        //create a notification event with fullUpdate/transitive flag = false/false
        //it means, it will only process changed master-SNAPSHOT jar files and wont force dependencies to load
        MetadataNotification event = new MetadataNotification(projectId, groupId, artifactId, versionId,false,false, null, Priority.HIGH);
        List<String> validationResponse = eventHandler.validate(event);
        if (validationResponse.isEmpty())
        {
            String eventId = queue.push(event);
            TracerFactory.get().log("eventId=" + eventId);
            LOGGER.info("Notification received : project[{}] [{}-{}-{}], eventId:[{}]", projectId, groupId, artifactId, versionId, eventId);
            return eventId;
        }
        else
        {
            String errorMessage = String.format("Notification failed validation for project :[%s] gav:[%s-%s-%s] %s",projectId, groupId, artifactId, versionId, String.join(",",validationResponse));
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

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

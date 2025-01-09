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
import org.finos.legend.depot.domain.notifications.MetadataNotificationStatus;
import org.finos.legend.depot.services.api.notifications.NotificationHandler;
import org.finos.legend.depot.services.api.notifications.NotificationsService;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.notifications.NotificationsMongo;
import org.finos.legend.depot.store.mongo.notifications.queue.NotificationsQueueMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;

import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.finos.legend.depot.domain.DatesHandler.toDate;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestNotificationQueueManager extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_PROJECT_ID = "PROD-1";
    public static final String VERSION_ID = "2.3.1";
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateEntities entitiesStore = mock(UpdateEntities.class);
    private final NotificationsMongo notifications = new NotificationsMongo(mongoProvider);
    private final NotificationsQueueMongo queue = new NotificationsQueueMongo(mongoProvider);
    private final NotificationHandler notificationEventHandler = mock(NotificationHandler.class);
    private final NotificationsService notificationsService = new NotificationsServiceImpl(notifications);
    private final NotificationsQueueManager eventsManager = new NotificationsQueueManager(notifications, queue, notificationEventHandler);

    @BeforeEach
    public void setUpData()
    {
        projectsStore.createOrUpdate(new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, "test"));
        when(notificationEventHandler.handleNotification(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID, "test", VERSION_ID))).thenReturn(loadEntities(TEST_PROJECT_ID, VERSION_ID));
        when(notificationEventHandler.handleNotification(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID,"test","10.0.0"))).thenReturn(new MetadataNotificationResponse());
        when(notificationEventHandler.validate(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID,"test","10.0.0"))).thenReturn(Arrays.asList("bad version"));
    }

    protected MetadataNotificationResponse loadEntities(String projectId, String versionId)
    {
        String fileName = "data/" + projectId + "/entities-" + versionId + ".json";
        try
        {
          //  setUpEntitiesDataFromFile(TestNotificationManager.class.getClassLoader().getResource(fileName));
        }
        catch (Exception e)
        {
            return null;
        }
        return new MetadataNotificationResponse();
    }

    @Test
    public void canProcessNewVersionEvent()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", VERSION_ID);
        queue.push(event);
        int result = eventsManager.handle();
        Assertions.assertEquals(1, result);
        List<Entity> entities = entitiesStore.getAllEntities(event.getGroupId(), event.getArtifactId(), event.getVersionId());
        Assertions.assertNotNull(entities);
        checkEventResult(event);
    }


    private void checkEventResult(MetadataNotification event)
    {

        List<MetadataNotification> newEvents = queue.getAllStoredEntities();
        Assertions.assertEquals(0, newEvents.size());

        List<MetadataNotification> events = notifications.getAllStoredEntities();
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals(events.get(0).getGroupId(), event.getGroupId());
        Assertions.assertEquals(events.get(0).getArtifactId(), event.getArtifactId());
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, events.get(0).getStatus());
    }


    @Test
    public void processNewVersionEventForNonExistingVersion()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "10.0.0");
        eventsManager.handleEvent(event);
        Assertions.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response = notifications.getAll().get(0);
        Assertions.assertTrue(response.getStatus().equals(MetadataNotificationStatus.FAILED));
    }

    @Test
    public void testRetry()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        String eventId = queue.push(event);
        Assertions.assertFalse(queue.getAll().isEmpty());
        MetadataNotification mockEnt = queue.getAllStoredEntities().get(0).increaseAttempts();
        when(notificationEventHandler.handleNotification(mockEnt)).thenReturn(new MetadataNotificationResponse().addError("i have failed, need to retry"));

        eventsManager.handle();
        Assertions.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getAll().get(0);
        Assertions.assertTrue(response.getStatus().equals(MetadataNotificationStatus.FAILED));

        eventsManager.handle();
        Assertions.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response1 = notificationsService.getProcessedEvent(eventId).get();
        Assertions.assertTrue(response1.getStatus().equals(MetadataNotificationStatus.SUCCESS));
    }



    @Test
    public void testMaxRetry()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        String eventId = queue.push(event);
        Assertions.assertFalse(queue.getAll().isEmpty());
        MetadataNotification mockEnt = queue.getAllStoredEntities().get(0);
        MetadataNotificationResponse responseTryOne = new MetadataNotificationResponse().addError("i have failed, need to retry");
        when(notificationEventHandler.handleNotification(mockEnt.increaseAttempts())).thenReturn(responseTryOne);

        eventsManager.handle();
        Assertions.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getAll().get(0);
        Assertions.assertTrue(response.getStatus().equals(MetadataNotificationStatus.FAILED));

        MetadataNotification updatedMockEvent = queue.getAllStoredEntities().get(0);
        MetadataNotificationResponse responseTryTwo = new MetadataNotificationResponse().addError("i have failed again, cant retry");
        when(notificationEventHandler.handleNotification(updatedMockEvent.increaseAttempts())).thenReturn(responseTryTwo);
        eventsManager.handle();
        Assertions.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response1 = notificationsService.getProcessedEvent(eventId).get();
        Assertions.assertTrue(response1.getStatus().equals(MetadataNotificationStatus.FAILED));

        Assertions.assertFalse(notifications.getAll().isEmpty());
        MetadataNotification notification = notifications.getAll().get(0);
        Assertions.assertEquals(2,notification.getAttempt());
        Assertions.assertEquals(2,notification.getResponses().size());
    }

    @Test
    public void testRetryIsSuccessfull()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        String eventId = queue.push(event);
        Assertions.assertFalse(queue.getAll().isEmpty());
        MetadataNotification mockEnt = queue.getAllStoredEntities().get(0);
        MetadataNotificationResponse responseTryOne = new MetadataNotificationResponse().addError("i have failed, need to retry");
        when(notificationEventHandler.handleNotification(mockEnt.increaseAttempts())).thenReturn(responseTryOne);

        eventsManager.handle();
        Assertions.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getAll().get(0);
        Assertions.assertTrue(response.getStatus().equals(MetadataNotificationStatus.FAILED));

        MetadataNotification updatedMockEvent = queue.getAllStoredEntities().get(0);
        MetadataNotificationResponse responseTryTwo = new MetadataNotificationResponse().addMessage("i am ok now , did not fail");
        when(notificationEventHandler.handleNotification(updatedMockEvent.increaseAttempts())).thenReturn(responseTryTwo);
        eventsManager.handle();
        Assertions.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response1 = notificationsService.getProcessedEvent(eventId).get();
        Assertions.assertTrue(response1.getStatus().equals(MetadataNotificationStatus.SUCCESS));

        Assertions.assertFalse(notifications.getAll().isEmpty());
        MetadataNotification notification = notifications.getAll().get(0);
        Assertions.assertEquals(2,notification.getAttempt());
        Assertions.assertEquals(2,notification.getResponses().size());
    }

}

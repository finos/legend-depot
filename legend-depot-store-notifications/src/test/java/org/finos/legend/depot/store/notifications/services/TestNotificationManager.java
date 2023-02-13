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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsQueueMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestNotificationManager extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_PROJECT_ID = "PROD-1";
    public static final String VERSION_ID = "2.3.1";
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateProjectsVersions projectsVersionsStore = new ProjectsVersionsMongo(mongoProvider);
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    private final NotificationsMongo notifications = new NotificationsMongo(mongoProvider);
    private final NotificationsQueueMongo queue = new NotificationsQueueMongo(mongoProvider);
    private final NotificationEventHandler notificationEventHandler = mock(NotificationEventHandler.class);
    private final NotificationsQueueManager eventsManager = new NotificationsQueueManager(new ProjectsServiceImpl(projectsVersionsStore,projectsStore), notifications, queue, notificationEventHandler);

    @Before
    public void setUpData()
    {
        projectsStore.createOrUpdate(new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, "test"));
        when(notificationEventHandler.handleEvent(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID, "test", VERSION_ID))).thenReturn(loadEntities(TEST_PROJECT_ID, VERSION_ID));
        when(notificationEventHandler.handleEvent(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID,"test","10.0.0"))).thenReturn(new MetadataEventResponse());
        when(notificationEventHandler.validateEvent(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID,"test","10.0.0"))).thenReturn(Arrays.asList("bad version"));
    }

    @After
    public void tearDown()
    {
      mongoProvider.drop();
    }


    protected MetadataEventResponse loadEntities(String projectId, String versionId)
    {
        String fileName = "data/" + projectId + "/entities-" + versionId + ".json";
        try
        {
            setUpEntitiesDataFromFile(TestNotificationManager.class.getClassLoader().getResource(fileName));
        }
        catch (Exception e)
        {
            return null;
        }
        return new MetadataEventResponse();
    }

    @Test
    public void canProcessNewVersionEvent()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", VERSION_ID);
        queue.push(event);
        int result = eventsManager.handle();
        Assert.assertEquals(1, result);
        List<Entity> entities = entitiesStore.getAllEntities(event.getGroupId(), event.getArtifactId(), event.getVersionId());
        Assert.assertNotNull(entities);
        checkEventResult(event);
    }


    private void checkEventResult(MetadataNotification event)
    {

        List<MetadataNotification> newEvents = queue.getAllStoredEntities();
        Assert.assertEquals(0, newEvents.size());

        List<MetadataNotification> events = notifications.getAllStoredEntities();
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(events.get(0).getGroupId(), event.getGroupId());
        Assert.assertEquals(events.get(0).getArtifactId(), event.getArtifactId());
        Assert.assertEquals(MetadataEventStatus.SUCCESS, events.get(0).getStatus());
    }


    @Test
    @Ignore
    public void processNewVersionEventForNonExistingVersion()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "10.0.0");
        eventsManager.handleEvent(event);
        Assert.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response = notifications.getAll().get(0);
        Assert.assertTrue(response.getStatus().equals(MetadataEventStatus.FAILED));
    }

    @Test
    public void testRetry()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        String eventId = queue.push(event);
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification mockEnt = queue.getAllStoredEntities().get(0).increaseAttempts();
        when(notificationEventHandler.handleEvent(mockEnt)).thenReturn(new MetadataEventResponse().addError("i have failed, need to retry"));

        eventsManager.handle();
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getAll().get(0);
        Assert.assertTrue(response.getStatus().equals(MetadataEventStatus.FAILED));

        eventsManager.handle();
        Assert.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response1 = eventsManager.getProcessedEvent(eventId).get();
        Assert.assertTrue(response1.getStatus().equals(MetadataEventStatus.SUCCESS));
    }

    @Test
    public void testDeleteOldNotifications()
    {
        MetadataNotification ev1 = new MetadataNotification("prod-123","test","artifacts","1.0.0");
        ev1.setEventId("609a5af62ccc9300c2e02581");
        ev1.setLastUpdated(Date.from(LocalDateTime.now().minusDays(12).atZone(ZoneId.systemDefault()).toInstant()));
        notifications.insert(ev1);
        MetadataNotification ev2 = new MetadataNotification("prod-123","test","artifacts","2.0.0");
        ev2.setEventId("609a631a2ccc9300c2edafb8");
        notifications.insert(ev2);

        Assert.assertEquals(2, notifications.getAll().size());

        eventsManager.deleteOldNotifications(10);
        Assert.assertEquals(1, notifications.getAll().size());
    }

    @Test
    public void testMaxRetry()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        String eventId = queue.push(event);
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification mockEnt = queue.getAllStoredEntities().get(0);
        MetadataEventResponse responseTryOne = new MetadataEventResponse().addError("i have failed, need to retry");
        when(notificationEventHandler.handleEvent(mockEnt.increaseAttempts())).thenReturn(responseTryOne);

        eventsManager.handle();
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getAll().get(0);
        Assert.assertTrue(response.getStatus().equals(MetadataEventStatus.FAILED));

        MetadataNotification updatedMockEvent = queue.getAllStoredEntities().get(0);
        MetadataEventResponse responseTryTwo = new MetadataEventResponse().addError("i have failed again, cant retry");
        when(notificationEventHandler.handleEvent(updatedMockEvent.increaseAttempts())).thenReturn(responseTryTwo);
        eventsManager.handle();
        Assert.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response1 = eventsManager.getProcessedEvent(eventId).get();
        Assert.assertTrue(response1.getStatus().equals(MetadataEventStatus.FAILED));

        Assert.assertFalse(notifications.getAll().isEmpty());
        MetadataNotification notification = notifications.getAll().get(0);
        Assert.assertEquals(2,notification.getAttempt());
        Assert.assertEquals(2,notification.getResponses().size());
    }

    @Test
    public void testRetryIsSuccessfull()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        String eventId = queue.push(event);
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification mockEnt = queue.getAllStoredEntities().get(0);
        MetadataEventResponse responseTryOne = new MetadataEventResponse().addError("i have failed, need to retry");
        when(notificationEventHandler.handleEvent(mockEnt.increaseAttempts())).thenReturn(responseTryOne);

        eventsManager.handle();
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getAll().get(0);
        Assert.assertTrue(response.getStatus().equals(MetadataEventStatus.FAILED));

        MetadataNotification updatedMockEvent = queue.getAllStoredEntities().get(0);
        MetadataEventResponse responseTryTwo = new MetadataEventResponse().addMessage("i am ok now , did not fail");
        when(notificationEventHandler.handleEvent(updatedMockEvent.increaseAttempts())).thenReturn(responseTryTwo);
        eventsManager.handle();
        Assert.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response1 = eventsManager.getProcessedEvent(eventId).get();
        Assert.assertTrue(response1.getStatus().equals(MetadataEventStatus.SUCCESS));

        Assert.assertFalse(notifications.getAll().isEmpty());
        MetadataNotification notification = notifications.getAll().get(0);
        Assert.assertEquals(2,notification.getAttempt());
        Assert.assertEquals(2,notification.getResponses().size());
    }
}

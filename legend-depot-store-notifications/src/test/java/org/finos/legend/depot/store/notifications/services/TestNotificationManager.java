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
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.notifications.store.mongo.QueueMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestNotificationManager extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_PROJECT_ID = "PROD-A";
    public static final String VERSION_ID = "2.3.1";
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    private final NotificationsMongo eventsMongo = new NotificationsMongo(mongoProvider);
    private final QueueMongo queue = new QueueMongo(mongoProvider);
    private final NotificationEventHandler notificationEventHandler = mock(NotificationEventHandler.class);
    private final NotificationsQueueManager eventsManager = new NotificationsQueueManager(new ProjectsServiceImpl(projectsStore), eventsMongo, queue, notificationEventHandler);

    @Before
    public void setUpData()
    {
        projectsStore.createOrUpdate(new ProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, "test"));
        when(notificationEventHandler.handleEvent(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID, "test", VERSION_ID, false,false))).thenReturn(loadEntities(TEST_PROJECT_ID, VERSION_ID));
        when(notificationEventHandler.handleEvent(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID,"test","10.0.0",false,false))).thenReturn(new MetadataEventResponse());
        when(notificationEventHandler.validateEvent(new MetadataNotification(TEST_PROJECT_ID,TEST_GROUP_ID,"test","10.0.0",false,false))).thenReturn(Arrays.asList("bad version"));
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

        List<MetadataNotification> events = eventsMongo.getAllStoredEntities();
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(events.get(0).getGroupId(), event.getGroupId());
        Assert.assertEquals(events.get(0).getArtifactId(), event.getArtifactId());
        Assert.assertEquals(MetadataEventStatus.SUCCESS, events.get(0).getStatus());
    }


    @Test
    public void processNewVersionEventForNonExistingVersion()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "10.0.0");
        eventsManager.handleEvent(event);
        Assert.assertTrue(queue.getAll().isEmpty());
        MetadataNotification response = eventsMongo.getAll().get(0);
        Assert.assertTrue(response.getStatus().equals(MetadataEventStatus.FAILED));
    }

    @Test
    public void testRetry()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", "2.3.1");
        queue.push(event);
        when(notificationEventHandler.handleEvent(queue.getAllStoredEntities().get(0))).thenReturn(new MetadataEventResponse().addError("i have failed, need to retry"));

        eventsManager.handle();
        Assert.assertFalse(queue.getAll().isEmpty());
        MetadataNotification response = queue.getFirstInQueue().get();
        Assert.assertTrue(response.getStatus().equals(MetadataEventStatus.RETRY));
    }

}

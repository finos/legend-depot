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
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.store.notifications.domain.RefreshAllMetadataNotification;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.notifications.store.mongo.QueueMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    private NotificationsMongo eventsMongo = new NotificationsMongo(mongoProvider);
    private QueueMongo queue = new QueueMongo(mongoProvider);
    private ManageProjectsService projectsService = new ProjectsServiceImpl(projectsStore);
    private ArtifactsRefreshService artifactsRefreshService = mock(ArtifactsRefreshService.class);
    private NotificationsQueueManager eventsManager = new NotificationsQueueManager(eventsMongo, queue, projectsService, artifactsRefreshService);

    @Before
    public void setUpData()
    {
        projectsStore.createOrUpdate(new ProjectData("PROD-B", TEST_GROUP_ID, "test-dependencies"));
        projectsStore.createOrUpdate(new ProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, "test"));
        when(artifactsRefreshService.refreshProjectVersionArtifacts(TEST_GROUP_ID, "test", VERSION_ID, false)).thenReturn(loadEntities(TEST_PROJECT_ID, VERSION_ID));
        when(artifactsRefreshService.refreshProjectVersionsArtifacts(TEST_GROUP_ID, "test", true)).thenReturn(loadEntities(TEST_PROJECT_ID, VERSION_ID));
        when(artifactsRefreshService.refreshProjectVersionsArtifacts(TEST_GROUP_ID, "test-dependencies", true)).thenReturn(loadEntities(TEST_PROJECT_ID, VERSION_ID));

    }

    protected MetadataEventResponse loadEntities(String projectId, String versionId)
    {
        String fileName = "data/" + projectId + "/entities-" + versionId + ".json";
        setUpEntitiesDataFromFile(TestNotificationManager.class.getClassLoader().getResource(fileName));
        return new MetadataEventResponse();
    }

    @Test
    public void createProjectIfAbsent()
    {
        Assert.assertEquals(2, projectsService.getAll().size());
        MetadataNotification event = new MetadataNotification("PROD-A1", TEST_GROUP_ID, "test1", VERSION_ID);
        queue.push(event);
        eventsManager.run();
        Assert.assertEquals(3, projectsService.getAll().size());

    }

    @Test
    public void canProcessNewVersionEvent()
    {
        MetadataNotification event = new MetadataNotification(TEST_PROJECT_ID, TEST_GROUP_ID, "test", VERSION_ID);
        queue.push(event);
        int result = eventsManager.run();
        Assert.assertEquals(1, result);
        List<Entity> entities = entitiesStore.getAllEntities(event.getGroupId(), event.getArtifactId(), event.getVersionId());
        Assert.assertNotNull(entities);
        checkEventResult(event);
    }

    @Test
    public void canProcessRefreshAllVersionEvent()
    {
        RefreshAllMetadataNotification event = new RefreshAllMetadataNotification();
        queue.push(event);
        int result = eventsManager.run();
        Assert.assertEquals(1, result);
        List<Entity> entities = entitiesStore.getAllEntities(TEST_GROUP_ID, "test", VERSION_ID);
        Assert.assertNotNull(entities);
        List<MetadataNotification> newEvents = queue.getAllStoredEntities();
        Assert.assertEquals(projectsService.getAll().size(), newEvents.size());
        eventsManager.run();
        Assert.assertEquals(1, queue.getAll().size());
        eventsManager.run();
        Assert.assertTrue(queue.getAll().isEmpty());
        List<MetadataNotification> events = eventsMongo.getAllStoredEntities();
        Assert.assertNotNull(events);
        Assert.assertEquals(2, events.size());
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


}

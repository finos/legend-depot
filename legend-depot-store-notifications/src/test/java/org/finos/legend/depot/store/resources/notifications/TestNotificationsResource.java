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

package org.finos.legend.depot.store.resources.notifications;

import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.finos.legend.depot.store.notifications.services.NotificationsQueueManager;
import org.finos.legend.depot.store.notifications.store.mongo.NotificationsMongo;
import org.finos.legend.depot.store.resources.notifications.NotificationsManagerResource;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.finos.legend.depot.domain.DatesHandler.toDate;
import static org.mockito.Mockito.mock;


public class TestNotificationsResource extends TestStoreMongo
{
    public static final String VERSION = "1.0.0";
    private final NotificationsMongo eventsMongo = new NotificationsMongo(mongoProvider);
    private final Queue queue = new NotificationsQueueMongo(mongoProvider);
    private final NotificationEventHandler handler = mock(NotificationEventHandler.class);
    private final NotificationsManager notificationsManager = new NotificationsQueueManager(eventsMongo,queue,handler);
    private final NotificationsManagerResource resource = new NotificationsManagerResource(notificationsManager);

    @Test
    public void canRetrieveEventsByDate()
    {

        MetadataNotification event1 = new MetadataNotification("testproject1", "test.com", "test", VERSION);
        MetadataNotification event2 = new MetadataNotification("testproject2", "test.comm", "test", VERSION);
        MetadataNotification event3 = new MetadataNotification("testproject3", "test.org", "test", VERSION);
        MetadataNotification event4 = new MetadataNotification("testproject4", "org.test", "test", VERSION);

        LocalDateTime aPointInTime = LocalDateTime.parse("2019-01-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME);
        insertRaw(eventsMongo.COLLECTION,event1.setUpdated(toDate(aPointInTime)));
        insertRaw(eventsMongo.COLLECTION,event2.setUpdated(toDate(aPointInTime.plusHours(1))));
        insertRaw(eventsMongo.COLLECTION,event3.setUpdated(toDate(aPointInTime.plusHours(2))));
        insertRaw(eventsMongo.COLLECTION,event4.setUpdated(toDate(aPointInTime.plusHours(2).plusMinutes(35))));


        List<MetadataNotification> allEvents = resource.getPastEventNotifications(null,null,null,null,null,null,aPointInTime.minusDays(100).format(DateTimeFormatter.ISO_DATE_TIME), null);
        Assert.assertNotNull(allEvents);
        Assert.assertEquals(4, allEvents.size());

        LocalDateTime lunchTime = LocalDateTime.parse("2019-01-01T12:00:00", DateTimeFormatter.ISO_DATE_TIME);
        List<MetadataNotification> afterLunch = resource.getPastEventNotifications(null,null,null,null,null,null,lunchTime.format(DateTimeFormatter.ISO_DATE_TIME), null);
        Assert.assertNotNull(afterLunch);
        Assert.assertEquals(2, afterLunch.size());

    }

    @Test
    public void canRetrieveEventsByDateAsEpocMillis()
    {

        MetadataNotification event1 = new MetadataNotification("1", "test.com", "test", VERSION);
        MetadataNotification event2 = new MetadataNotification("2", "test.com", "test1", VERSION);

        eventsMongo.insert(event1);
        insertRaw(eventsMongo.COLLECTION,event2.setUpdated(toDate(LocalDateTime.now().plusDays(1))));
        Assert.assertEquals(2, eventsMongo.getAll().size());


        List<MetadataNotification> found = resource.getPastEventNotifications(null,null,null,null,null,null,null, String.valueOf(System.currentTimeMillis()));
        Assert.assertNotNull(found);
        Assert.assertEquals(1, found.size());
        Assert.assertTrue(found.stream().anyMatch(e -> e.getProjectId().equals("1")));

    }
}

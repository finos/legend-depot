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
import org.finos.legend.depot.services.api.notifications.NotificationsService;
import org.finos.legend.depot.services.notifications.NotificationsServiceImpl;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.notifications.NotificationsMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.finos.legend.depot.domain.DatesHandler.toDate;


public class TestNotificationsResource extends TestStoreMongo
{
    public static final String VERSION = "1.0.0";
    private final NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

    private final NotificationsService notificationsService = new NotificationsServiceImpl(notificationsMongo);
    private final NotificationsResource resource = new NotificationsResource(notificationsService);

    @Test
    public void canRetrieveEventsByDate()
    {

        MetadataNotification event1 = new MetadataNotification("testproject1", "test.com", "test", VERSION);
        MetadataNotification event2 = new MetadataNotification("testproject2", "test.comm", "test", VERSION);
        MetadataNotification event3 = new MetadataNotification("testproject3", "test.org", "test", VERSION);
        MetadataNotification event4 = new MetadataNotification("testproject4", "org.test", "test", VERSION);

        LocalDateTime aPointInTime = LocalDateTime.parse("2019-01-01T10:00:00", DateTimeFormatter.ISO_DATE_TIME);
        insertRaw(notificationsMongo.COLLECTION,event1.setUpdated(toDate(aPointInTime)));
        insertRaw(notificationsMongo.COLLECTION,event2.setUpdated(toDate(aPointInTime.plusHours(1))));
        insertRaw(notificationsMongo.COLLECTION,event3.setUpdated(toDate(aPointInTime.plusHours(2))));
        insertRaw(notificationsMongo.COLLECTION,event4.setUpdated(toDate(aPointInTime.plusHours(2).plusMinutes(35))));


        List<MetadataNotification> allEvents = resource.getPastEventNotifications(null,null,null,null,null,null,aPointInTime.minusDays(100).format(DateTimeFormatter.ISO_DATE_TIME), null);
        Assertions.assertNotNull(allEvents);
        Assertions.assertEquals(4, allEvents.size());

        LocalDateTime lunchTime = LocalDateTime.parse("2019-01-01T12:00:00", DateTimeFormatter.ISO_DATE_TIME);
        List<MetadataNotification> afterLunch = resource.getPastEventNotifications(null,null,null,null,null,null,lunchTime.format(DateTimeFormatter.ISO_DATE_TIME), null);
        Assertions.assertNotNull(afterLunch);
        Assertions.assertEquals(2, afterLunch.size());

    }

    @Test
    public void canRetrieveEventsByDateAsEpocMillis()
    {

        MetadataNotification event1 = new MetadataNotification("1", "test.com", "test", VERSION);
        MetadataNotification event2 = new MetadataNotification("2", "test.com", "test1", VERSION);

        notificationsMongo.insert(event1);
        insertRaw(notificationsMongo.COLLECTION,event2.setUpdated(toDate(LocalDateTime.now().plusDays(1))));
        Assertions.assertEquals(2, notificationsMongo.getAll().size());


        List<MetadataNotification> found = resource.getPastEventNotifications(null,null,null,null,null,null,null, String.valueOf(System.currentTimeMillis()));
        Assertions.assertNotNull(found);
        Assertions.assertEquals(1, found.size());
        Assertions.assertTrue(found.stream().anyMatch(e -> e.getProjectId().equals("1")));

    }


    @Test
    public void testDeleteOldNotifications()
    {
        MetadataNotification ev1 = new MetadataNotification("prod-123","test","artifacts","1.0.0");
        ev1.setUpdated(toDate(LocalDateTime.now().minusDays(12)));
        insertRaw(NotificationsMongo.COLLECTION,ev1);
        Assertions.assertEquals(1, notificationsMongo.getAll().size());
        MetadataNotification ev2 = new MetadataNotification("prod-123","test","artifacts","2.0.0");
        notificationsMongo.createOrUpdate(ev2);

        Assertions.assertEquals(2, notificationsMongo.getAll().size());

        long deleted = notificationsService.deleteOldNotifications(10);
        Assertions.assertEquals(1,deleted);
        Assertions.assertEquals(1, notificationsMongo.getAll().size());
    }
}

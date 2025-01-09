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

package org.finos.legend.depot.store.mongo.notifications;

import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.notifications.queue.NotificationsQueueMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.DatesHandler.toDate;

public class TestNotificationsMongo extends TestStoreMongo
{


    public static final String VERSION = "1.0.0";
    public static final String TESTPROJECT = "testproject";
    public static final String TESTPROJECT_1 = "testproject1";
    public static final String TESTPROJECT_2 = "testproject2";
    public static final String TEST = "test";
    private Queue queue = new NotificationsQueueMongo(mongoProvider);
    private NotificationsMongo eventsMongo = new NotificationsMongo(mongoProvider);

    @Test
    public void canStoreEvents()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_1, TEST, "test1",VERSION);
        MetadataNotification event2 = new MetadataNotification(TESTPROJECT_2, TEST, "test2","1.0.1");
        MetadataNotification event3 = new MetadataNotification(TESTPROJECT_2, TEST, "test2", VERSION);
        queue.push(event);
        queue.push(event1);
        queue.push(event2);
        queue.push(event3);


        List<MetadataNotification> eventList = queue.pullAll();
        Assertions.assertNotNull(eventList);
        Assertions.assertEquals(4, eventList.size());
        for (MetadataNotification ev : eventList)
        {
            eventsMongo.createOrUpdate(ev);
        }

        List<MetadataNotification> eventList2 = eventsMongo.getAll();
        Assertions.assertNotNull(eventList2);
        Assertions.assertEquals(4, eventList2.size());

    }

    @Test
    public void eventIdIsKeptOnCompletion()
    {

        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        queue.push(event);
        List<MetadataNotification> pulled = queue.pullAll();
        Assertions.assertNotNull(pulled);
        Assertions.assertEquals(1, pulled.size());
        Assertions.assertNotNull(pulled.get(0).getEventId());

        eventsMongo.createOrUpdate(pulled.get(0));
        List<MetadataNotification> eventList2 = eventsMongo.getAll();
        Assertions.assertNotNull(eventList2);
        Assertions.assertEquals(1, eventList2.size());
        Assertions.assertNotNull(eventList2.get(0).getEventId());
        Assertions.assertEquals(pulled.get(0).getEventId(), eventList2.get(0).getEventId());
    }

    @Test
    public void canRetrieveEventById()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        String eventId = queue.push(event);
        List<MetadataNotification> events = queue.pullAll();
        eventsMongo.createOrUpdate(events.get(0));

        List<MetadataNotification> eventList = eventsMongo.getAll();
        Assertions.assertNotNull(eventList);
        Assertions.assertEquals(1, eventList.size());
        Assertions.assertNotNull(eventList.get(0).getEventId());

        Optional<MetadataNotification> foundEvent = eventsMongo.get(eventList.get(0).getEventId());
        Assertions.assertEquals(eventId, eventList.get(0).getEventId());
        Assertions.assertNotNull(foundEvent);
        Assertions.assertTrue(foundEvent.isPresent());
        Assertions.assertEquals(eventList.get(0).getEventId(), foundEvent.get().getEventId());
    }



    @Test
    public void canRetrieveEventsFromDateToDate()
    {
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_1, "test.com", TEST, VERSION);
        MetadataNotification event2 = new MetadataNotification(TESTPROJECT_2, "test.comm", TEST, VERSION);
        MetadataNotification event3 = new MetadataNotification("testproject3", "test.org", TEST, VERSION);
        MetadataNotification event4 = new MetadataNotification("testproject4", "org.test", TEST, VERSION);

        LocalDateTime aPointInTime = LocalDateTime.of(2020, 10, 12, 12, 0).minusDays(12);
        insertRaw(eventsMongo.COLLECTION,event1.setUpdated(toDate(aPointInTime)));
        insertRaw(eventsMongo.COLLECTION,event2.setUpdated(toDate(aPointInTime.plusHours(1))));
        insertRaw(eventsMongo.COLLECTION,event3.setUpdated(toDate(aPointInTime.plusHours(2))));
        insertRaw(eventsMongo.COLLECTION,event4.setUpdated(toDate(aPointInTime.plusHours(2).plusMinutes(35))));

        List<MetadataNotification> allEvents = eventsMongo.getAll();
        Assertions.assertNotNull(allEvents);
        Assertions.assertEquals(4, allEvents.size());
        List<MetadataNotification> eventsBeforeLunch = eventsMongo.find(null,null,null,null,null,null,aPointInTime.toLocalDate().atStartOfDay(), aPointInTime);
        Assertions.assertNotNull(eventsBeforeLunch);
        Assertions.assertEquals(1, eventsBeforeLunch.size());

        List<MetadataNotification> afterLunch = eventsMongo.find(null,null,null,null,null,null,aPointInTime.withHour(12).withMinute(0).withSecond(1), null);
        Assertions.assertNotNull(afterLunch);
        Assertions.assertEquals(3, afterLunch.size());
    }
}
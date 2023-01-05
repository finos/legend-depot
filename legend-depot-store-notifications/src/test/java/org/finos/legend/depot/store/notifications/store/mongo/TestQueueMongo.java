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

package org.finos.legend.depot.store.notifications.store.mongo;

import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TestQueueMongo extends TestStoreMongo
{


    public static final String VERSION = "1.0.0";
    public static final String TESTPROJECT = "testproject";
    public static final String TESTPROJECT_1 = "testproject1";
    public static final String TESTPROJECT_2 = "testproject2";
    public static final String TEST = "test";
    private Queue queue = new NotificationsQueueMongo(mongoProvider);
    private Notifications eventsMongo = new NotificationsMongo(mongoProvider);

    @Test
    public void canStoreEvents()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_1, TEST, TEST,VERSION);
        MetadataNotification event2 = new MetadataNotification(TESTPROJECT_2, TEST, TEST,"1.0.1");
        MetadataNotification event3 = new MetadataNotification(TESTPROJECT_2, TEST, TEST, VERSION);
        queue.push(event);
        queue.push(event1);
        queue.push(event2);
        queue.push(event3);


        List<MetadataNotification> eventList = queue.pullAll();
        Assert.assertNotNull(eventList);
        Assert.assertEquals(4, eventList.size());
        for (MetadataNotification ev : eventList)
        {
            eventsMongo.complete(ev);
        }

        List<MetadataNotification> eventList2 = eventsMongo.getAll();
        Assert.assertNotNull(eventList2);
        Assert.assertEquals(4, eventList2.size());

    }

    @Test
    public void eventIdIsKeptOnCompletion()
    {

        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        queue.push(event);
        List<MetadataNotification> pulled = queue.pullAll();
        Assert.assertNotNull(pulled);
        Assert.assertEquals(1, pulled.size());
        Assert.assertNotNull(pulled.get(0).getEventId());

        eventsMongo.complete(pulled.get(0));
        List<MetadataNotification> eventList2 = eventsMongo.getAll();
        Assert.assertNotNull(eventList2);
        Assert.assertEquals(1, eventList2.size());
        Assert.assertNotNull(eventList2.get(0).getEventId());
        Assert.assertEquals(pulled.get(0).getEventId(), eventList2.get(0).getEventId());
    }

    @Test
    public void canRetrieveEventById()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        String eventId = queue.push(event);
        List<MetadataNotification> events = queue.pullAll();
        eventsMongo.complete(events.get(0));

        List<MetadataNotification> eventList = eventsMongo.getAll();
        Assert.assertNotNull(eventList);
        Assert.assertEquals(1, eventList.size());
        Assert.assertNotNull(eventList.get(0).getEventId());

        Optional<MetadataNotification> foundEvent = eventsMongo.get(eventList.get(0).getEventId());
        Assert.assertEquals(eventId, eventList.get(0).getEventId());
        Assert.assertNotNull(foundEvent);
        Assert.assertTrue(foundEvent.isPresent());
        Assert.assertEquals(eventList.get(0).getEventId(), foundEvent.get().getEventId());
    }


    @Test
    public void canQueryContentsOfQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_1, TEST, TEST,VERSION);
        MetadataNotification event2 = new MetadataNotification(TESTPROJECT_2, TEST, TEST,"1.0.1");
        MetadataNotification event3 = new MetadataNotification(TESTPROJECT_2, TEST, TEST, VERSION);
        queue.push(event);
        queue.push(event1);
        queue.push(event2);
        queue.push(event3);

        List<MetadataNotification> inQueue = queue.getAll();
        Assert.assertNotNull(inQueue);
        Assert.assertEquals(4, inQueue.size());
    }

    @Test
    public void canRetrieveEventByIdInQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        queue.push(event);

        List<MetadataNotification> eventList = queue.getAll();
        Assert.assertNotNull(eventList);
        Assert.assertEquals(1, eventList.size());
        Assert.assertNotNull(eventList.get(0).getEventId());

        MetadataNotification foundEvent = queue.get(eventList.get(0).getEventId()).get();
        Assert.assertNotNull(foundEvent);
        Assert.assertEquals(eventList.get(0).getEventId(), foundEvent.getEventId());
    }

    @Test
    public void canStoreErrors()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        queue.push(event.addError("this is an error"));

        List<MetadataNotification> eventList = queue.getAll();
        Assert.assertNotNull(eventList);
        Assert.assertEquals(1, eventList.size());
        Assert.assertNotNull(eventList.get(0).getEventId());

        MetadataNotification foundEvent = queue.get(eventList.get(0).getEventId()).get();
        Assert.assertNotNull(foundEvent);
        Assert.assertEquals(eventList.get(0).getEventId(), foundEvent.getEventId());
        Assert.assertNotNull(eventList.get(0).getErrors());
        Assert.assertEquals(1, eventList.get(0).getErrors().size());
    }

    @Test
    public void canRetrieveEventsFromDateToDate()
    {
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_1, "test.com", TEST, VERSION);
        MetadataNotification event2 = new MetadataNotification(TESTPROJECT_2, "test.comm", TEST, VERSION);
        MetadataNotification event3 = new MetadataNotification("testproject3", "test.org", TEST, VERSION);
        MetadataNotification event4 = new MetadataNotification("testproject4", "org.test", TEST, VERSION);

        LocalDateTime aPointInTime = LocalDateTime.of(2020, 10, 12, 12, 0).minusDays(12);
        eventsMongo.insert(event1.setLastUpdated(toDate(aPointInTime)));
        eventsMongo.insert(event2.setLastUpdated(toDate(aPointInTime.plusHours(1))));
        eventsMongo.insert(event3.setLastUpdated(toDate(aPointInTime.plusHours(2))));
        eventsMongo.insert(event4.setLastUpdated(toDate(aPointInTime.plusHours(2).plusMinutes(35))));

        List<MetadataNotification> allEvents = eventsMongo.getAll();
        Assert.assertNotNull(allEvents);
        Assert.assertEquals(4, allEvents.size());
        List<MetadataNotification> eventsBeforeLunch = eventsMongo.find(null,null,null,null,null,aPointInTime.toLocalDate().atStartOfDay(), aPointInTime);
        Assert.assertNotNull(eventsBeforeLunch);
        Assert.assertEquals(1, eventsBeforeLunch.size());

        List<MetadataNotification> afterLunch = eventsMongo.find(null,null,null,null,null,aPointInTime.withHour(12).withMinute(0).withSecond(1), null);
        Assert.assertNotNull(afterLunch);
        Assert.assertEquals(3, afterLunch.size());
    }


    @Test
    public void doesNotInsertDuplicateEvents()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        String id = queue.push(event);
        Assert.assertNotNull(id);
        String id2 = queue.push(event);
        Assert.assertNotNull(id2);
        Assert.assertEquals(id, id2);
    }

    @Test
    public void canGetFirstInQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_2, TEST, TEST,VERSION);
        queue.push(event);
        queue.push(event1);

        Assert.assertTrue(queue.getFirstInQueue().isPresent());
        Assert.assertTrue(queue.getFirstInQueue().isPresent());
        Assert.assertFalse(queue.getFirstInQueue().isPresent());


    }

}
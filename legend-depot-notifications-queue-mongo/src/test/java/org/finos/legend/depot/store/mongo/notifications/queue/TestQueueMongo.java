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

package org.finos.legend.depot.store.mongo.notifications.queue;

import org.finos.legend.depot.domain.notifications.MetadataNotification;

import org.finos.legend.depot.domain.notifications.Priority;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.store.mongo.TestStoreMongo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
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


    @Test
    public void canQueryContentsOfQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT_1, TEST, "test1",VERSION);
        MetadataNotification event2 = new MetadataNotification(TESTPROJECT_2, TEST, "test2","1.0.1");
        MetadataNotification event3 = new MetadataNotification(TESTPROJECT_2, TEST, "test2", VERSION);
        queue.push(event);
        queue.push(event1);
        queue.push(event2);
        queue.push(event3);

        List<MetadataNotification> inQueue = queue.getAll();
        Assertions.assertNotNull(inQueue);
        Assertions.assertEquals(4, inQueue.size());
    }

    @Test
    public void canRetrieveEventByIdInQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        queue.push(event);

        List<MetadataNotification> eventList = queue.getAll();
        Assertions.assertNotNull(eventList);
        Assertions.assertEquals(1, eventList.size());
        Assertions.assertNotNull(eventList.get(0).getEventId());

        MetadataNotification foundEvent = queue.get(eventList.get(0).getEventId()).get();
        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(eventList.get(0).getEventId(), foundEvent.getEventId());
    }

    @Test
    public void canStoreErrors()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        queue.push(event.addError("this is an error"));

        List<MetadataNotification> eventList = queue.getAll();
        Assertions.assertNotNull(eventList);
        Assertions.assertEquals(1, eventList.size());
        Assertions.assertNotNull(eventList.get(0).getEventId());

        MetadataNotification foundEvent = queue.get(eventList.get(0).getEventId()).get();
        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(eventList.get(0).getEventId(), foundEvent.getEventId());
        Assertions.assertNotNull(eventList.get(0).getCurrentResponse().getErrors());
        Assertions.assertEquals(1, eventList.get(0).getCurrentResponse().getErrors().size());
    }



    @Test
    public void doesNotInsertDuplicateEvents()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        String id = queue.push(event);
        Assertions.assertNotNull(id);
        String id2 = queue.push(event);
        Assertions.assertNotNull(id2);
        Assertions.assertEquals(id, id2);
    }

    @Test
    public void canGetFirstInQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT, TEST, TEST,"1.0.1");
        queue.push(event);
        queue.push(event1);

        Assertions.assertTrue(queue.getFirstInQueue().isPresent());
        Assertions.assertTrue(queue.getFirstInQueue().isPresent());
        Assertions.assertFalse(queue.getFirstInQueue().isPresent());


    }

    @Test
    public void canGetFirstInQueueByFIFO()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT, TEST, TEST,"1.0.1");
        queue.push(event);
        queue.push(event1);

        Optional<MetadataNotification> first = queue.getFirstInQueue();
        Optional<MetadataNotification> second = queue.getFirstInQueue();

        Assertions.assertEquals(VERSION,first.get().getVersionId());
        Assertions.assertEquals("1.0.1",second.get().getVersionId());
        Assertions.assertTrue(first.get().getCreated().before(second.get().getCreated()));
    }

    @Test
    public void canGetFirstInQueueByPriority()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST, VERSION, null, null, null, null, null, null, null, null, null, null, Priority.LOW);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT, TEST, TEST,"1.0.1", null, null, null, null, null, null, null, null, null, null, Priority.HIGH);
        queue.push(event);
        queue.push(event1);

        Optional<MetadataNotification> first = queue.getFirstInQueue();
        Optional<MetadataNotification> second = queue.getFirstInQueue();

        Assertions.assertEquals("1.0.1", first.get().getVersionId());
        Assertions.assertEquals(VERSION, second.get().getVersionId());
    }

    @Test
    public void canGetFirstInQueueBySamePriorityWithDifferentCreatedTime()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST, VERSION, null, null, null, null, null, null, null, new Date(), null,null, Priority.HIGH);
        queue.push(event);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT, TEST, TEST,"1.0.1", null, null, null, null, null, null, null, new Date(System.currentTimeMillis() + 60000), null,null, Priority.HIGH);
        queue.push(event1);

        Optional<MetadataNotification> first = queue.getFirstInQueue();
        Optional<MetadataNotification> second = queue.getFirstInQueue();

        Assertions.assertEquals(VERSION, first.get().getVersionId());
        Assertions.assertEquals("1.0.1", second.get().getVersionId());
    }

    @Test
    public void canGetFirstInQueueByDifferentPriorityWithSameTime()
    {
        long date = System.currentTimeMillis();
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST, VERSION, null, null, null, null, null, null, null, new Date(date), null, null, Priority.HIGH);
        queue.push(event);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT, TEST, TEST,"1.0.1", null, null, null, null, null, null, null, new Date(date), null,null, Priority.LOW);
        queue.push(event1);

        Optional<MetadataNotification> first = queue.getFirstInQueue();
        Optional<MetadataNotification> second = queue.getFirstInQueue();

        Assertions.assertEquals(VERSION, first.get().getVersionId());
        Assertions.assertEquals("1.0.1", second.get().getVersionId());
    }

    @Test
    public void canPurgeQueue()
    {
        MetadataNotification event = new MetadataNotification(TESTPROJECT, TEST, TEST,VERSION);
        MetadataNotification event1 = new MetadataNotification(TESTPROJECT, TEST, TEST,"1.0.1");
        queue.push(event);
        queue.push(event1);
        Assertions.assertEquals(2, queue.size());
        long deleted = queue.deleteAll();
        Assertions.assertEquals(2, deleted);
        Assertions.assertEquals(0, queue.size());

    }

}
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

import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.Priority;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.notifications.NotificationsQueueManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Comprehensive test class for NotificationsQueueManagerResource.
 * Tests all methods with good branch and condition coverage.
 */
public class NotificationsQueueManagerResourceClaudeTest


{
    private NotificationsQueueManagerResource resource;
    private TestAuthorisationProvider authProvider;
    private TestPrincipalProvider principalProvider;
    private TestNotificationsQueueManager notificationsManager;
    private TestQueue queue;

    @BeforeEach
    public void setUp()
  {
        authProvider = new TestAuthorisationProvider();
        principalProvider = new TestPrincipalProvider();
        notificationsManager = new TestNotificationsQueueManager();
        queue = new TestQueue();

        resource = new NotificationsQueueManagerResource(
            notificationsManager,
            authProvider,
            principalProvider,
            queue
        );
    }

    // Test constructor
    @Test
    public void testConstructor()
  {
        NotificationsQueueManagerResource testResource = new NotificationsQueueManagerResource(
            notificationsManager,
            authProvider,
            principalProvider,
            queue
        );
        Assertions.assertNotNull(testResource);
    }

    // Test getResourceName
    @Test
    public void testGetResourceName()
  {
        String resourceName = resource.getResourceName();
        Assertions.assertEquals("Notifications", resourceName);
    }

    // Test getAllEventsInQueue with no events
    @Test
    public void testGetAllEventsInQueueEmpty()
  {
        List<MetadataNotification> result = resource.getAllEventsInQueue();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getAllEventsInQueue with multiple events
    @Test
    public void testGetAllEventsInQueueWithEvents()
  {
        MetadataNotification event1 = new MetadataNotification("proj1", "group1", "artifact1", "1.0.0");
        event1.setEventId("event1");
        MetadataNotification event2 = new MetadataNotification("proj2", "group2", "artifact2", "2.0.0");
        event2.setEventId("event2");

        queue.events.add(event1);
        queue.events.add(event2);

        List<MetadataNotification> result = resource.getAllEventsInQueue();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(event1));
        Assertions.assertTrue(result.contains(event2));
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getAllEventsInQueue with authorization failure
    @Test
    public void testGetAllEventsInQueueWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.getAllEventsInQueue();
        });
    }

    // Test getAllEventsInQueueCount with no events
    @Test
    public void testGetAllEventsInQueueCountEmpty()
  {
        long count = resource.getAllEventsInQueueCount();

        Assertions.assertEquals(0L, count);
    }

    // Test getAllEventsInQueueCount with multiple events
    @Test
    public void testGetAllEventsInQueueCountWithEvents()
  {
        queue.events.add(new MetadataNotification("proj1", "group1", "artifact1", "1.0.0"));
        queue.events.add(new MetadataNotification("proj2", "group2", "artifact2", "2.0.0"));
        queue.events.add(new MetadataNotification("proj3", "group3", "artifact3", "3.0.0"));

        long count = resource.getAllEventsInQueueCount();

        Assertions.assertEquals(3L, count);
    }

    // Test geEventsInQueue when event exists
    @Test
    public void testGeEventsInQueueFound()
  {
        MetadataNotification event = new MetadataNotification("proj1", "group1", "artifact1", "1.0.0");
        event.setEventId("test-event-id");
        queue.eventsById.put("test-event-id", event);

        Optional<MetadataNotification> result = resource.geEventsInQueue("test-event-id");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("test-event-id", result.get().getEventId());
        Assertions.assertEquals("proj1", result.get().getProjectId());
    }

    // Test geEventsInQueue when event does not exist
    @Test
    public void testGeEventsInQueueNotFound()
  {
        Optional<MetadataNotification> result = resource.geEventsInQueue("non-existent-id");

        Assertions.assertFalse(result.isPresent());
    }

    // Test geEventsInQueue with null eventId
    @Test
    public void testGeEventsInQueueWithNullEventId()
  {
        Optional<MetadataNotification> result = resource.geEventsInQueue(null);

        Assertions.assertFalse(result.isPresent());
    }

    // Test queueEvent successfully
    @Test
    public void testQueueEventSuccess()
  {
        String eventId = resource.queueEvent("proj1", "group1", "artifact1", "1.0.0");

        Assertions.assertNotNull(eventId);
        Assertions.assertTrue(notificationsManager.notifyCalled);
        Assertions.assertEquals("proj1", notificationsManager.lastProjectId);
        Assertions.assertEquals("group1", notificationsManager.lastGroupId);
        Assertions.assertEquals("artifact1", notificationsManager.lastArtifactId);
        Assertions.assertEquals("1.0.0", notificationsManager.lastVersionId);
    }

    // Test queueEvent with different parameters
    @Test
    public void testQueueEventWithDifferentParameters()
  {
        String eventId1 = resource.queueEvent("projectA", "groupA", "artifactA", "1.0.0");
        Assertions.assertNotNull(eventId1);

        String eventId2 = resource.queueEvent("projectB", "groupB", "artifactB", "2.0.0-SNAPSHOT");
        Assertions.assertNotNull(eventId2);

        // Verify both calls were made
        Assertions.assertEquals(2, notificationsManager.notifyCallCount);
    }

    // Test queueEvent with master-SNAPSHOT version
    @Test
    public void testQueueEventWithMasterSnapshot()
  {
        String eventId = resource.queueEvent("proj1", "group1", "artifact1", "master-SNAPSHOT");

        Assertions.assertNotNull(eventId);
        Assertions.assertEquals("master-SNAPSHOT", notificationsManager.lastVersionId);
    }

    // Test purgeQueue with no events
    @Test
    public void testPurgeQueueEmpty()
  {
        long deletedCount = resource.purgeQueue();

        Assertions.assertEquals(0L, deletedCount);
        Assertions.assertTrue(authProvider.authoriseCalled);
        Assertions.assertTrue(queue.deleteAllCalled);
    }

    // Test purgeQueue with events
    @Test
    public void testPurgeQueueWithEvents()
  {
        queue.events.add(new MetadataNotification("proj1", "group1", "artifact1", "1.0.0"));
        queue.events.add(new MetadataNotification("proj2", "group2", "artifact2", "2.0.0"));
        queue.events.add(new MetadataNotification("proj3", "group3", "artifact3", "3.0.0"));

        long deletedCount = resource.purgeQueue();

        Assertions.assertEquals(3L, deletedCount);
        Assertions.assertTrue(authProvider.authoriseCalled);
        Assertions.assertTrue(queue.deleteAllCalled);
        Assertions.assertEquals(0, queue.events.size());
    }

    // Test purgeQueue with authorization failure
    @Test
    public void testPurgeQueueWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;
        queue.events.add(new MetadataNotification("proj1", "group1", "artifact1", "1.0.0"));

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.purgeQueue();
        });

        // Verify that deleteAll was not called when authorization failed
        Assertions.assertFalse(queue.deleteAllCalled);
    }

    // Helper classes for testing

    private static class TestAuthorisationProvider implements AuthorisationProvider
    {
        boolean authoriseCalled = false;
        boolean shouldThrowException = false;
        String lastRole = null;

        @Override
        public void authorise(Provider<Principal> principalProvider, String role)
  {
            authoriseCalled = true;
            lastRole = role;
            if (shouldThrowException)
            {
                throw new RuntimeException("Authorization failed");
            }
        }
    }

    private static class TestPrincipalProvider implements Provider<Principal>
    {
        @Override
        public Principal get()
  {
            return () -> "testUser";
        }
    }

    private static class TestNotificationsQueueManager extends NotificationsQueueManager
    {
        boolean notifyCalled = false;
        int notifyCallCount = 0;
        String lastProjectId = null;
        String lastGroupId = null;
        String lastArtifactId = null;
        String lastVersionId = null;

        public TestNotificationsQueueManager()
  {
            super(null, null, null);
        }

        @Override
        public String notify(String projectId, String groupId, String artifactId, String versionId)
  {
            notifyCalled = true;
            notifyCallCount++;
            lastProjectId = projectId;
            lastGroupId = groupId;
            lastArtifactId = artifactId;
            lastVersionId = versionId;
            return UUID.randomUUID().toString();
        }
    }

    private static class TestQueue implements Queue
    {
        List<MetadataNotification> events = new ArrayList<>();
        Map<String, MetadataNotification> eventsById = new HashMap<>();
        boolean deleteAllCalled = false;

        @Override
        public List<MetadataNotification> getAll()
        {
            return new ArrayList<>(events);
        }

        @Override
        public List<MetadataNotification> pullAll()
        {
            List<MetadataNotification> result = new ArrayList<>(events);
            events.clear();
            return result;
        }

        @Override
        public Optional<MetadataNotification> getFirstInQueue()
        {
            if (events.isEmpty())
            {
                return Optional.empty();
            }
            return Optional.of(events.get(0));
        }

        @Override
        public Optional<MetadataNotification> get(String eventId)
        {
            if (eventId == null)
            {
                return Optional.empty();
            }
            return Optional.ofNullable(eventsById.get(eventId));
        }

        @Override
        public String push(MetadataNotification metadataEvent)
  {
            String eventId = UUID.randomUUID().toString();
            metadataEvent.setEventId(eventId);
            events.add(metadataEvent);
            eventsById.put(eventId, metadataEvent);
            return eventId;
        }

        @Override
        public long size()
  {
            return events.size();
        }

        @Override
        public long deleteAll()
  {
            deleteAllCalled = true;
            long count = events.size();
            events.clear();
            eventsById.clear();
            return count;
        }
    }
}

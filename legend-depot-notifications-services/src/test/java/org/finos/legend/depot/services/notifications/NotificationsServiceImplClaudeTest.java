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

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.api.notifications.Notifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationsServiceImplClaudeTest


{
    private Notifications notifications;
    private NotificationsServiceImpl service;

    @BeforeEach
    public void setUp()
  {
        notifications = mock(Notifications.class);
        service = new NotificationsServiceImpl(notifications);
    }

    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#NotificationsServiceImpl(Notifications)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsServiceImpl.<init>(Notifications)"})
    public void testConstructor()
  {
        // Arrange and Act
        NotificationsServiceImpl actualService = new NotificationsServiceImpl(notifications);

        // Assert
        assertNotNull(actualService);
    }

    /**
     * Test findProcessedEvents with all parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents with all parameters")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsWithAllParameters()
  {
        // Arrange
        String group = "test.group";
        String artifact = "test-artifact";
        String version = "1.0.0";
        String eventId = "event-123";
        String parentId = "parent-456";
        Boolean success = true;
        LocalDateTime from = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2023, 12, 31, 23, 59);

        MetadataNotification event1 = new MetadataNotification("project1", group, artifact, version);
        event1.setEventId(eventId);
        MetadataNotification event2 = new MetadataNotification("project2", group, artifact, version);
        event2.setEventId("event-456");

        List<MetadataNotification> expectedEvents = Arrays.asList(event1, event2);
        when(notifications.find(group, artifact, version, eventId, parentId, success, from, to))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = service.findProcessedEvents(group, artifact, version, eventId, parentId, success, from, to);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(eventId, result.get(0).getEventId());
        verify(notifications, times(1)).find(group, artifact, version, eventId, parentId, success, from, to);
    }

    /**
     * Test findProcessedEvents with null parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents with null parameters")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsWithNullParameters()
  {
        // Arrange
        List<MetadataNotification> expectedEvents = Collections.emptyList();
        when(notifications.find(null, null, null, null, null, null, null, null))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = service.findProcessedEvents(null, null, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notifications, times(1)).find(null, null, null, null, null, null, null, null);
    }

    /**
     * Test findProcessedEvents with date range only.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents with date range only")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsWithDateRangeOnly()
  {
        // Arrange
        LocalDateTime from = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2023, 6, 30, 23, 59);

        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notifications.find(null, null, null, null, null, null, from, to))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = service.findProcessedEvents(null, null, null, null, null, null, from, to);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notifications, times(1)).find(null, null, null, null, null, null, from, to);
    }

    /**
     * Test findProcessedEvents filtering by success status.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents filtering by success status")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsFilteringBySuccessStatus()
  {
        // Arrange
        Boolean success = false;
        List<MetadataNotification> expectedEvents = Collections.emptyList();

        when(notifications.find(null, null, null, null, null, success, null, null))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = service.findProcessedEvents(null, null, null, null, null, success, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notifications, times(1)).find(null, null, null, null, null, success, null, null);
    }

    /**
     * Test findProcessedEvents returns empty list.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents returns empty list")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsReturnsEmptyList()
  {
        // Arrange
        when(notifications.find(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        List<MetadataNotification> result = service.findProcessedEvents("group", "artifact", "version", null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notifications, times(1)).find(any(), any(), any(), any(), any(), any(), any(), any());
    }

    /**
     * Test getProcessedEvent with existing event.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#getProcessedEvent(String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getProcessedEvent with existing event")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Optional NotificationsServiceImpl.getProcessedEvent(String)"})
    public void testGetProcessedEventWithExistingEvent()
  {
        // Arrange
        String eventId = "event-123";
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId(eventId);

        when(notifications.get(eventId)).thenReturn(Optional.of(event));

        // Act
        Optional<MetadataNotification> result = service.getProcessedEvent(eventId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().getEventId());
        verify(notifications, times(1)).get(eventId);
    }

    /**
     * Test getProcessedEvent with non-existent event.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#getProcessedEvent(String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getProcessedEvent with non-existent event")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Optional NotificationsServiceImpl.getProcessedEvent(String)"})
    public void testGetProcessedEventWithNonExistentEvent()
  {
        // Arrange
        String eventId = "non-existent-event";
        when(notifications.get(eventId)).thenReturn(Optional.empty());

        // Act
        Optional<MetadataNotification> result = service.getProcessedEvent(eventId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(notifications, times(1)).get(eventId);
    }

    /**
     * Test getProcessedEvent with null eventId.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#getProcessedEvent(String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getProcessedEvent with null eventId")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Optional NotificationsServiceImpl.getProcessedEvent(String)"})
    public void testGetProcessedEventWithNullEventId()
  {
        // Arrange
        when(notifications.get(null)).thenReturn(Optional.empty());

        // Act
        Optional<MetadataNotification> result = service.getProcessedEvent(null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(notifications, times(1)).get(null);
    }

    /**
     * Test deleteOldNotifications deletes old notifications.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#deleteOldNotifications(long)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteOldNotifications deletes old notifications")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"long NotificationsServiceImpl.deleteOldNotifications(long)"})
    public void testDeleteOldNotificationsDeletesOldNotifications()
  {
        // Arrange
        long days = 10;
        MetadataNotification oldEvent1 = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        oldEvent1.setId("old-event-1");
        MetadataNotification oldEvent2 = new MetadataNotification("project2", "group2", "artifact2", "2.0.0");
        oldEvent2.setId("old-event-2");

        List<MetadataNotification> oldEvents = Arrays.asList(oldEvent1, oldEvent2);

        when(notifications.find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class)))
                .thenReturn(oldEvents);

        // Act
        long result = service.deleteOldNotifications(days);

        // Assert
        assertEquals(2, result);
        verify(notifications, times(1)).find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class));
        verify(notifications, times(1)).delete("old-event-1");
        verify(notifications, times(1)).delete("old-event-2");
    }

    /**
     * Test deleteOldNotifications with no old notifications.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#deleteOldNotifications(long)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteOldNotifications with no old notifications")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"long NotificationsServiceImpl.deleteOldNotifications(long)"})
    public void testDeleteOldNotificationsWithNoOldNotifications()
  {
        // Arrange
        long days = 30;
        when(notifications.find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        long result = service.deleteOldNotifications(days);

        // Assert
        assertEquals(0, result);
        verify(notifications, times(1)).find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class));
        verify(notifications, times(0)).delete(any());
    }

    /**
     * Test deleteOldNotifications with zero days.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#deleteOldNotifications(long)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteOldNotifications with zero days")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"long NotificationsServiceImpl.deleteOldNotifications(long)"})
    public void testDeleteOldNotificationsWithZeroDays()
  {
        // Arrange
        long days = 0;
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setId("event-1");

        when(notifications.find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(event));

        // Act
        long result = service.deleteOldNotifications(days);

        // Assert
        assertEquals(1, result);
        verify(notifications, times(1)).find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class));
        verify(notifications, times(1)).delete("event-1");
    }

    /**
     * Test deleteOldNotifications with large number of days.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#deleteOldNotifications(long)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteOldNotifications with large number of days")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"long NotificationsServiceImpl.deleteOldNotifications(long)"})
    public void testDeleteOldNotificationsWithLargeNumberOfDays()
  {
        // Arrange
        long days = 365;
        MetadataNotification veryOldEvent = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        veryOldEvent.setId("very-old-event");

        when(notifications.find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(veryOldEvent));

        // Act
        long result = service.deleteOldNotifications(days);

        // Assert
        assertEquals(1, result);
        verify(notifications, times(1)).find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class));
        verify(notifications, times(1)).delete("very-old-event");
    }

    /**
     * Test deleteOldNotifications deletes multiple notifications.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#deleteOldNotifications(long)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteOldNotifications deletes multiple notifications")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"long NotificationsServiceImpl.deleteOldNotifications(long)"})
    public void testDeleteOldNotificationsDeletesMultipleNotifications()
  {
        // Arrange
        long days = 7;
        MetadataNotification event1 = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event1.setId("event-1");
        MetadataNotification event2 = new MetadataNotification("project2", "group2", "artifact2", "2.0.0");
        event2.setId("event-2");
        MetadataNotification event3 = new MetadataNotification("project3", "group3", "artifact3", "3.0.0");
        event3.setId("event-3");
        MetadataNotification event4 = new MetadataNotification("project4", "group4", "artifact4", "4.0.0");
        event4.setId("event-4");
        MetadataNotification event5 = new MetadataNotification("project5", "group5", "artifact5", "5.0.0");
        event5.setId("event-5");

        List<MetadataNotification> oldEvents = Arrays.asList(event1, event2, event3, event4, event5);

        when(notifications.find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class)))
                .thenReturn(oldEvents);

        // Act
        long result = service.deleteOldNotifications(days);

        // Assert
        assertEquals(5, result);
        verify(notifications, times(1)).find(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class));
        verify(notifications, times(1)).delete("event-1");
        verify(notifications, times(1)).delete("event-2");
        verify(notifications, times(1)).delete("event-3");
        verify(notifications, times(1)).delete("event-4");
        verify(notifications, times(1)).delete("event-5");
    }

    /**
     * Test findProcessedEvents with specific group and artifact.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents with specific group and artifact")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsWithSpecificGroupAndArtifact()
  {
        // Arrange
        String group = "com.example";
        String artifact = "my-artifact";
        MetadataNotification event = new MetadataNotification("project1", group, artifact, "1.0.0");

        when(notifications.find(eq(group), eq(artifact), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(Collections.singletonList(event));

        // Act
        List<MetadataNotification> result = service.findProcessedEvents(group, artifact, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(group, result.get(0).getGroupId());
        assertEquals(artifact, result.get(0).getArtifactId());
        verify(notifications, times(1)).find(eq(group), eq(artifact), isNull(), isNull(), isNull(), isNull(), isNull(), isNull());
    }

    /**
     * Test findProcessedEvents with parent event ID.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsServiceImpl#findProcessedEvents(String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime)}
     * </ul>
     */
    @Test
    @DisplayName("Test findProcessedEvents with parent event ID")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"List NotificationsServiceImpl.findProcessedEvents(String,String,String,String,String,Boolean,LocalDateTime,LocalDateTime)"})
    public void testFindProcessedEventsWithParentEventId()
  {
        // Arrange
        String parentId = "parent-789";
        MetadataNotification childEvent = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        childEvent.setParentEventId(parentId);

        when(notifications.find(isNull(), isNull(), isNull(), isNull(), eq(parentId), isNull(), isNull(), isNull()))
                .thenReturn(Collections.singletonList(childEvent));

        // Act
        List<MetadataNotification> result = service.findProcessedEvents(null, null, null, null, parentId, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(parentId, result.get(0).getParentEventId());
        verify(notifications, times(1)).find(isNull(), isNull(), isNull(), isNull(), eq(parentId), isNull(), isNull(), isNull());
    }
}

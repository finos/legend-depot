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
import org.finos.legend.depot.services.api.notifications.NotificationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationsResourceClaudeTest


{
    private NotificationsService notificationsService;
    private AuthorisationProvider authorisationProvider;
    private Provider<Principal> principalProvider;
    private NotificationsResource resource;

    @BeforeEach
    public void setUp()
  {
        notificationsService = mock(NotificationsService.class);
        authorisationProvider = mock(AuthorisationProvider.class);
        principalProvider = mock(Provider.class);
    }

    /**
     * Test constructor with all dependencies (injected constructor).
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#NotificationsResource(NotificationsService, AuthorisationProvider, Provider)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with all dependencies")
    public void testConstructorWithAllDependencies()
  {
        // Arrange and Act
        NotificationsResource actualResource = new NotificationsResource(
                notificationsService,
                authorisationProvider,
                principalProvider
        );

        // Assert
        assertNotNull(actualResource);
    }

    /**
     * Test constructor with NotificationsService only (package-private constructor).
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#NotificationsResource(NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with NotificationsService only")
    public void testConstructorWithNotificationsServiceOnly()
  {
        // Arrange and Act
        NotificationsResource actualResource = new NotificationsResource(notificationsService);

        // Assert
        assertNotNull(actualResource);
    }

    /**
     * Test getResourceName returns correct resource name.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName returns 'Notifications'")
    public void testGetResourceName()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);

        // Act
        String resourceName = resource.getResourceName();

        // Assert
        assertEquals("Notifications", resourceName);
    }

    /**
     * Test getPastEventNotifications with all parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with all parameters")
    public void testGetPastEventNotificationsWithAllParameters()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String group = "test.group";
        String artifact = "test-artifact";
        String version = "1.0.0";
        String eventId = "event-123";
        String parentId = "parent-456";
        Boolean success = true;
        String from = "2023-01-01T00:00:00";
        String to = "2023-12-31T23:59:59";

        MetadataNotification event1 = new MetadataNotification("project1", group, artifact, version);
        event1.setEventId(eventId);
        MetadataNotification event2 = new MetadataNotification("project2", group, artifact, version);
        event2.setEventId("event-456");

        List<MetadataNotification> expectedEvents = Arrays.asList(event1, event2);
        when(notificationsService.findProcessedEvents(
                eq(group), eq(artifact), eq(version), eq(eventId), eq(parentId), eq(success), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                group, artifact, version, eventId, parentId, success, from, to);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(eventId, result.get(0).getEventId());
        verify(notificationsService, times(1)).findProcessedEvents(
                eq(group), eq(artifact), eq(version), eq(eventId), eq(parentId), eq(success), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with null from and to parameters (default date range).
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with null from and to parameters")
    public void testGetPastEventNotificationsWithNullFromAndTo()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act - when from and to are null, should use default values
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with epoch millis as date parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with epoch millis")
    public void testGetPastEventNotificationsWithEpochMillis()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        long fromMillis = System.currentTimeMillis() - 3600000; // 1 hour ago
        long toMillis = System.currentTimeMillis();

        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, null, null, null, String.valueOf(fromMillis), String.valueOf(toMillis));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications filtering by success status.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications filtering by success status")
    public void testGetPastEventNotificationsFilteringBySuccess()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        Boolean success = false;
        List<MetadataNotification> expectedEvents = Collections.emptyList();

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), any(), eq(success), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, null, null, success, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), any(), eq(success), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with specific group and artifact.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with specific group and artifact")
    public void testGetPastEventNotificationsWithSpecificGroupAndArtifact()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String group = "com.example";
        String artifact = "my-artifact";

        MetadataNotification event = new MetadataNotification("project1", group, artifact, "1.0.0");
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notificationsService.findProcessedEvents(
                eq(group), eq(artifact), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                group, artifact, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(group, result.get(0).getGroupId());
        assertEquals(artifact, result.get(0).getArtifactId());
        verify(notificationsService, times(1)).findProcessedEvents(
                eq(group), eq(artifact), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with parent event ID.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with parent event ID")
    public void testGetPastEventNotificationsWithParentEventId()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String parentId = "parent-789";

        MetadataNotification childEvent = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        childEvent.setParentEventId(parentId);
        List<MetadataNotification> expectedEvents = Collections.singletonList(childEvent);

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), eq(parentId), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, null, parentId, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(parentId, result.get(0).getParentEventId());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), eq(parentId), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications returns empty list.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications returns empty list")
    public void testGetPastEventNotificationsReturnsEmptyList()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                "group", "artifact", "version", null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with version parameter.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with version parameter")
    public void testGetPastEventNotificationsWithVersion()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String version = "2.5.3";

        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", version);
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notificationsService.findProcessedEvents(
                any(), any(), eq(version), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, version, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(version, result.get(0).getVersionId());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), eq(version), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getNotificationById with existing event.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getNotificationById(String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getNotificationById with existing event")
    public void testGetNotificationByIdWithExistingEvent()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String eventId = "event-123";
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId(eventId);

        when(notificationsService.getProcessedEvent(eventId)).thenReturn(Optional.of(event));

        // Act
        Optional<MetadataNotification> result = resource.getNotificationById(eventId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().getEventId());
        verify(notificationsService, times(1)).getProcessedEvent(eventId);
    }

    /**
     * Test getNotificationById with non-existent event.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getNotificationById(String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getNotificationById with non-existent event")
    public void testGetNotificationByIdWithNonExistentEvent()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String eventId = "non-existent-event";
        when(notificationsService.getProcessedEvent(eventId)).thenReturn(Optional.empty());

        // Act
        Optional<MetadataNotification> result = resource.getNotificationById(eventId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(notificationsService, times(1)).getProcessedEvent(eventId);
    }

    /**
     * Test getNotificationById with null eventId.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getNotificationById(String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getNotificationById with null eventId")
    public void testGetNotificationByIdWithNullEventId()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        when(notificationsService.getProcessedEvent(null)).thenReturn(Optional.empty());

        // Act
        Optional<MetadataNotification> result = resource.getNotificationById(null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(notificationsService, times(1)).getProcessedEvent(null);
    }

    /**
     * Test getPastEventNotifications with eventId parameter.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with eventId parameter")
    public void testGetPastEventNotificationsWithEventId()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String eventId = "specific-event-id";

        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId(eventId);
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), eq(eventId), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, eventId, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).getEventId());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), eq(eventId), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with only from parameter.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with only from parameter")
    public void testGetPastEventNotificationsWithOnlyFrom()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        String from = "2023-06-01T00:00:00";

        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        List<MetadataNotification> expectedEvents = Collections.singletonList(event);

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, null, null, null, from, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * Test getPastEventNotifications with multiple events returned.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsResource#getPastEventNotifications(String, String, String, String, String, Boolean, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getPastEventNotifications with multiple events returned")
    public void testGetPastEventNotificationsWithMultipleEvents()
  {
        // Arrange
        resource = new NotificationsResource(notificationsService);
        MetadataNotification event1 = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event1.setEventId("event-1");
        MetadataNotification event2 = new MetadataNotification("project2", "group2", "artifact2", "2.0.0");
        event2.setEventId("event-2");
        MetadataNotification event3 = new MetadataNotification("project3", "group3", "artifact3", "3.0.0");
        event3.setEventId("event-3");

        List<MetadataNotification> expectedEvents = Arrays.asList(event1, event2, event3);

        when(notificationsService.findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedEvents);

        // Act
        List<MetadataNotification> result = resource.getPastEventNotifications(
                null, null, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("event-1", result.get(0).getEventId());
        assertEquals("event-2", result.get(1).getEventId());
        assertEquals("event-3", result.get(2).getEventId());
        verify(notificationsService, times(1)).findProcessedEvents(
                any(), any(), any(), any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}

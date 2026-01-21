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
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.notifications.Priority;
import org.finos.legend.depot.services.api.notifications.NotificationHandler;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.store.api.notifications.Notifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationsQueueManagerClaudeTest


{
    private Notifications notifications;
    private Queue queue;
    private NotificationHandler eventHandler;
    private NotificationsQueueManager manager;

    @BeforeEach
    public void setUp()
  {
        notifications = mock(Notifications.class);
        queue = mock(Queue.class);
        eventHandler = mock(NotificationHandler.class);
        manager = new NotificationsQueueManager(notifications, queue, eventHandler);
    }

    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#NotificationsQueueManager(Notifications, Queue, NotificationHandler)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.<init>(Notifications,Queue,NotificationHandler)"})
    public void testConstructor()
  {
        // Arrange and Act
        NotificationsQueueManager actualManager = new NotificationsQueueManager(notifications, queue, eventHandler);

        // Assert
        assertNotNull(actualManager);
    }

    /**
     * Test handle method when queue is empty.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handle()}
     * </ul>
     */
    @Test
    @DisplayName("Test handle when queue is empty")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"int NotificationsQueueManager.handle()"})
    public void testHandleWhenQueueIsEmpty()
  {
        // Arrange
        when(queue.size()).thenReturn(0L);
        when(queue.getFirstInQueue()).thenReturn(Optional.empty());

        // Act
        int result = manager.handle();

        // Assert
        assertEquals(0, result);
        verify(queue, times(1)).size();
        verify(queue, times(1)).getFirstInQueue();
    }

    /**
     * Test handle method when queue has events.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handle()}
     * </ul>
     */
    @Test
    @DisplayName("Test handle when queue has events")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"int NotificationsQueueManager.handle()"})
    public void testHandleWhenQueueHasEvents()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setCreated(new Date());
        when(queue.size()).thenReturn(1L);
        when(queue.getFirstInQueue()).thenReturn(Optional.of(event));
        when(eventHandler.validate(any())).thenReturn(Collections.emptyList());
        when(eventHandler.handleNotification(any())).thenReturn(new MetadataNotificationResponse());
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        int result = manager.handle();

        // Assert
        assertEquals(1, result);
        verify(queue, times(1)).size();
        verify(queue, times(1)).getFirstInQueue();
    }

    /**
     * Test handleEvent method with validation errors.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent with validation errors")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventWithValidationErrors()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setParentEventId("parent-456");
        when(eventHandler.validate(event)).thenReturn(Arrays.asList("Error 1", "Error 2"));
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        manager.handleEvent(event);

        // Assert
        verify(eventHandler, times(1)).validate(event);
        verify(eventHandler, never()).handleNotification(any());
        verify(notifications, times(1)).createOrUpdate(argThat(e ->
                e.getCompleted() != null &&
                e.getCurrentResponse() != null &&
                e.getCurrentResponse().hasErrors()
        ));
    }

    /**
     * Test handleEvent method with successful processing.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent with successful processing")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventWithSuccessfulProcessing()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setCreated(new Date());
        when(eventHandler.validate(event)).thenReturn(Collections.emptyList());
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Success");
        when(eventHandler.handleNotification(any())).thenReturn(response);
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        manager.handleEvent(event);

        // Assert
        verify(eventHandler, times(1)).validate(event);
        verify(eventHandler, times(1)).handleNotification(any());
        verify(notifications, times(1)).createOrUpdate(argThat(e ->
                e.getCompleted() != null
        ));
        verify(queue, never()).push(any());
    }

    /**
     * Test handleEvent method with handler exception and retry.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent with handler exception and retry")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventWithHandlerExceptionAndRetry()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setCreated(new Date());
        event.setAttempt(0);
        event.setMaxAttempts(3);
        when(eventHandler.validate(event)).thenReturn(Collections.emptyList());
        when(eventHandler.handleNotification(any())).thenThrow(new RuntimeException("Processing failed"));
        when(queue.push(any())).thenReturn("event-123");

        // Act
        manager.handleEvent(event);

        // Assert
        verify(eventHandler, times(1)).validate(event);
        verify(eventHandler, times(1)).handleNotification(any());
        verify(queue, times(1)).push(any());
        verify(notifications, never()).createOrUpdate(any());
    }

    /**
     * Test handleEvent method with handler error and retry.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent with handler error and retry")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventWithHandlerErrorAndRetry()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setCreated(new Date());
        event.setAttempt(0);
        event.setMaxAttempts(3);
        when(eventHandler.validate(event)).thenReturn(Collections.emptyList());
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Handler error");
        when(eventHandler.handleNotification(any())).thenReturn(response);
        when(queue.push(any())).thenReturn("event-123");

        // Act
        manager.handleEvent(event);

        // Assert
        verify(eventHandler, times(1)).validate(event);
        verify(eventHandler, times(1)).handleNotification(any());
        verify(queue, times(1)).push(any());
        verify(notifications, never()).createOrUpdate(any());
    }

    /**
     * Test handleEvent method with retries exceeded.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent with retries exceeded")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventWithRetriesExceeded()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setCreated(new Date());
        event.setAttempt(2);
        event.setMaxAttempts(2);
        when(eventHandler.validate(event)).thenReturn(Collections.emptyList());
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addError("Handler error");
        when(eventHandler.handleNotification(any())).thenReturn(response);
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        manager.handleEvent(event);

        // Assert
        verify(eventHandler, times(1)).validate(event);
        verify(eventHandler, times(1)).handleNotification(any());
        verify(notifications, times(1)).createOrUpdate(argThat(e ->
                e.getCompleted() != null &&
                e.getCurrentResponse() != null &&
                e.getCurrentResponse().hasErrors()
        ));
        verify(queue, never()).push(any());
    }

    /**
     * Test notify method with valid notification.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#notify(String, String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test notify with valid notification")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String NotificationsQueueManager.notify(String,String,String,String)"})
    public void testNotifyWithValidNotification()
  {
        // Arrange
        when(eventHandler.validate(any())).thenReturn(Collections.emptyList());
        when(queue.push(any())).thenReturn("event-789");

        // Act
        String eventId = manager.notify("project1", "group1", "artifact1", "1.0.0");

        // Assert
        assertEquals("event-789", eventId);
        verify(eventHandler, times(1)).validate(any());
        verify(queue, times(1)).push(argThat(e ->
                e.getProjectId().equals("project1") &&
                e.getGroupId().equals("group1") &&
                e.getArtifactId().equals("artifact1") &&
                e.getVersionId().equals("1.0.0") &&
                e.getEventPriority() == Priority.HIGH &&
                !e.isFullUpdate() &&
                !e.isTransitive()
        ));
    }

    /**
     * Test notify method with validation failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#notify(String, String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test notify with validation failure")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String NotificationsQueueManager.notify(String,String,String,String)"})
    public void testNotifyWithValidationFailure()
  {
        // Arrange
        when(eventHandler.validate(any())).thenReturn(Arrays.asList("Invalid artifact"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                manager.notify("project1", "group1", "artifact1", "1.0.0")
        );

        assertNotNull(exception.getMessage());
        verify(eventHandler, times(1)).validate(any());
        verify(queue, never()).push(any());
    }

    /**
     * Test handleAll method with empty queue.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleAll()}
     * </ul>
     */
    @Test
    @DisplayName("Test handleAll with empty queue")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleAll()"})
    public void testHandleAllWithEmptyQueue()
  {
        // Arrange
        when(queue.getFirstInQueue()).thenReturn(Optional.empty());

        // Act
        manager.handleAll();

        // Assert
        // handleAll() calls getFirstInQueue() initially (line 166) and then in the do-while condition (line 170)
        // When queue is empty, both calls return empty, resulting in 2 calls total
        verify(queue, times(2)).getFirstInQueue();
        verify(eventHandler, never()).validate(any());
        verify(eventHandler, never()).handleNotification(any());
    }

    /**
     * Test handleAll method with single event.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleAll()}
     * </ul>
     */
    @Test
    @DisplayName("Test handleAll with single event")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleAll()"})
    public void testHandleAllWithSingleEvent()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setCreated(new Date());
        when(queue.getFirstInQueue())
                .thenReturn(Optional.of(event))
                .thenReturn(Optional.empty());
        when(eventHandler.validate(any())).thenReturn(Collections.emptyList());
        when(eventHandler.handleNotification(any())).thenReturn(new MetadataNotificationResponse());
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        manager.handleAll();

        // Assert
        verify(queue, times(2)).getFirstInQueue();
        verify(eventHandler, times(1)).validate(any());
        verify(eventHandler, times(1)).handleNotification(any());
    }

    /**
     * Test handleAll method with multiple events.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleAll()}
     * </ul>
     */
    @Test
    @DisplayName("Test handleAll with multiple events")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleAll()"})
    public void testHandleAllWithMultipleEvents()
  {
        // Arrange
        MetadataNotification event1 = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event1.setEventId("event-1");
        event1.setCreated(new Date());
        MetadataNotification event2 = new MetadataNotification("project2", "group2", "artifact2", "2.0.0");
        event2.setEventId("event-2");
        event2.setCreated(new Date());
        MetadataNotification event3 = new MetadataNotification("project3", "group3", "artifact3", "3.0.0");
        event3.setEventId("event-3");
        event3.setCreated(new Date());

        when(queue.getFirstInQueue())
                .thenReturn(Optional.of(event1))
                .thenReturn(Optional.of(event2))
                .thenReturn(Optional.of(event3))
                .thenReturn(Optional.empty());
        when(eventHandler.validate(any())).thenReturn(Collections.emptyList());
        when(eventHandler.handleNotification(any())).thenReturn(new MetadataNotificationResponse());
        when(notifications.createOrUpdate(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        manager.handleAll();

        // Assert
        verify(queue, times(4)).getFirstInQueue();
        verify(eventHandler, times(3)).validate(any());
        verify(eventHandler, times(3)).handleNotification(any());
    }

    /**
     * Test handleEvent with exception during increaseAttempts.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent increases attempt count")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventIncreasesAttemptCount()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        event.setEventId("event-123");
        event.setCreated(new Date());
        event.setAttempt(0);
        when(eventHandler.validate(event)).thenReturn(Collections.emptyList());
        when(eventHandler.handleNotification(any())).thenReturn(new MetadataNotificationResponse());
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        manager.handleEvent(event);

        // Assert
        assertEquals(1, event.getAttempt());
        verify(notifications, times(1)).createOrUpdate(any());
    }

    /**
     * Test handleEvent with parent event ID.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#handleEvent(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test handleEvent with parent event ID")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueManager.handleEvent(MetadataNotification)"})
    public void testHandleEventWithParentEventId()
  {
        // Arrange
        MetadataNotification event = new MetadataNotification("project1", "group1", "artifact1", "1.0.0", true, true, "parent-event-456");
        event.setEventId("event-123");
        event.setCreated(new Date());
        when(eventHandler.validate(event)).thenReturn(Collections.emptyList());
        when(eventHandler.handleNotification(any())).thenReturn(new MetadataNotificationResponse());
        when(notifications.createOrUpdate(any())).thenReturn(event);

        // Act
        manager.handleEvent(event);

        // Assert
        verify(eventHandler, times(1)).validate(event);
        verify(eventHandler, times(1)).handleNotification(any());
        verify(notifications, times(1)).createOrUpdate(any());
    }

    /**
     * Test notify creates notification with correct priority.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueManager#notify(String, String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test notify creates notification with HIGH priority")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"String NotificationsQueueManager.notify(String,String,String,String)"})
    public void testNotifyCreatesNotificationWithHighPriority()
  {
        // Arrange
        when(eventHandler.validate(any())).thenReturn(new ArrayList<>());
        when(queue.push(any())).thenReturn("event-id-999");

        // Act
        String eventId = manager.notify("project1", "group1", "artifact1", "1.0.0");

        // Assert
        assertEquals("event-id-999", eventId);
        verify(queue, times(1)).push(argThat(event ->
                event.getEventPriority() == Priority.HIGH
        ));
    }
}

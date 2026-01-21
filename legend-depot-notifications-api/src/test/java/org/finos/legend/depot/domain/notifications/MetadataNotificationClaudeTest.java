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

package org.finos.legend.depot.domain.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for MetadataNotification class focusing on edge cases,
 * boundary conditions, and method interactions.
 */
class MetadataNotificationClaudeTest 

{

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Default constructor creates object with null fields and default values")
    void testDefaultConstructor()
  {
        MetadataNotification notification = new MetadataNotification();

        assertNull(notification.getId());
        assertNull(notification.getProjectId());
        assertNull(notification.getEventId());
        assertNull(notification.getParentEventId());
        assertNull(notification.getGroupId());
        assertNull(notification.getArtifactId());
        assertNull(notification.getVersionId());
        assertNull(notification.getCreated());
        assertNull(notification.getUpdated());
        assertNull(notification.getCompleted());
        assertNull(notification.getEventPriority());
        assertEquals(0, notification.getAttempt());
        assertEquals(0, notification.getMaxAttempts());
        assertFalse(notification.isFullUpdate());
        assertFalse(notification.isTransitive());
    }

    @Test
    @DisplayName("Full constructor with all parameters sets all fields correctly")
    void testFullConstructorWithAllParameters()
  {
        Date created = new Date();
        Date updated = new Date();
        Date completed = new Date();
        Map<Integer, MetadataNotificationResponse> responses = new HashMap<>();
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        responses.put(0, response);

        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0",
                "event1", "parentEvent1", true, true,
                5, 10, responses, created, updated, completed, Priority.HIGH
        );

        assertEquals("project1", notification.getProjectId());
        assertEquals("group1", notification.getGroupId());
        assertEquals("artifact1", notification.getArtifactId());
        assertEquals("1.0.0", notification.getVersionId());
        assertEquals("event1", notification.getEventId());
        assertEquals("parentEvent1", notification.getParentEventId());
        assertTrue(notification.isFullUpdate());
        assertTrue(notification.isTransitive());
        assertEquals(5, notification.getAttempt());
        assertEquals(10, notification.getMaxAttempts());
        assertEquals(Priority.HIGH, notification.getEventPriority());
        assertSame(created, notification.getCreated());
        assertSame(updated, notification.getUpdated());
        assertSame(completed, notification.getCompleted());
        assertSame(responses, notification.getResponses());
    }

    @Test
    @DisplayName("Full constructor with null optional parameters uses defaults")
    void testFullConstructorWithNullOptionalParameters()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0",
                null, null, null, null,
                null, null, null, null, null, null, null
        );

        assertEquals(0, notification.getAttempt());
        assertEquals(2, notification.getMaxAttempts()); // DEFAULT_MAX_ATTEMPTS
        assertFalse(notification.isFullUpdate());
        assertFalse(notification.isTransitive());
        assertNotNull(notification.getResponses());
        assertTrue(notification.getResponses().isEmpty());
    }

    @Test
    @DisplayName("Constructor with project, group, artifact, version sets Priority.LOW by default")
    void testSimpleConstructorDefaultsToLowPriority()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );

        assertEquals(Priority.LOW, notification.getEventPriority());
        assertEquals(2, notification.getMaxAttempts());
    }

    @Test
    @DisplayName("Constructor with boolean flags and parent event")
    void testConstructorWithBooleanFlagsAndParent()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0",
                true, false, "parentEvent1"
        );

        assertTrue(notification.isFullUpdate());
        assertFalse(notification.isTransitive());
        assertEquals("parentEvent1", notification.getParentEventId());
        assertEquals(Priority.LOW, notification.getEventPriority());
    }

    @Test
    @DisplayName("Constructor with boolean flags, parent event, and priority")
    void testConstructorWithBooleanFlagsParentAndPriority()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0",
                false, true, "parentEvent1", Priority.HIGH
        );

        assertFalse(notification.isFullUpdate());
        assertTrue(notification.isTransitive());
        assertEquals("parentEvent1", notification.getParentEventId());
        assertEquals(Priority.HIGH, notification.getEventPriority());
    }

    // ========== Getter/Setter Tests ==========

    @Test
    @DisplayName("Fluent setters return same instance for chaining")
    void testFluentSettersReturnSameInstance()
  {
        MetadataNotification notification = new MetadataNotification();

        assertSame(notification, notification.setEventId("event1"));
        assertSame(notification, notification.setProjectId("project1"));
        assertSame(notification, notification.setFullUpdate(true));
        assertSame(notification, notification.setAttempt(5));
        assertSame(notification, notification.setUpdated(new Date()));
    }

    @Test
    @DisplayName("Void setters work correctly")
    void testVoidSetters()
  {
        MetadataNotification notification = new MetadataNotification();
        Date created = new Date();
        Date completed = new Date();
        Map<Integer, MetadataNotificationResponse> responses = new HashMap<>();

        notification.setId("id1");
        notification.setParentEventId("parent1");
        notification.setTransitive(true);
        notification.setCreated(created);
        notification.setCompleted(completed);
        notification.setMaxAttempts(5);
        notification.setEventPriority(Priority.HIGH);
        notification.setResponses(responses);

        assertEquals("id1", notification.getId());
        assertEquals("parent1", notification.getParentEventId());
        assertTrue(notification.isTransitive());
        assertSame(created, notification.getCreated());
        assertSame(completed, notification.getCompleted());
        assertEquals(5, notification.getMaxAttempts());
        assertEquals(Priority.HIGH, notification.getEventPriority());
        assertSame(responses, notification.getResponses());
    }

    @Test
    @DisplayName("Fluent API can be chained together")
    void testFluentApiChaining()
  {
        Date updated = new Date();
        MetadataNotification notification = new MetadataNotification()
                .setProjectId("project1")
                .setEventId("event1")
                .setFullUpdate(true)
                .setAttempt(3)
                .setUpdated(updated);

        assertEquals("project1", notification.getProjectId());
        assertEquals("event1", notification.getEventId());
        assertTrue(notification.isFullUpdate());
        assertEquals(3, notification.getAttempt());
        assertSame(updated, notification.getUpdated());
    }

    // ========== Business Logic Tests ==========

    @Test
    @DisplayName("complete() sets completed date to current time")
    void testCompleteSetDate()
  {
        MetadataNotification notification = new MetadataNotification();
        assertNull(notification.getCompleted());

        Date beforeComplete = new Date();
        notification.complete();
        Date afterComplete = new Date();

        assertNotNull(notification.getCompleted());
        assertTrue(notification.getCompleted().getTime() >= beforeComplete.getTime());
        assertTrue(notification.getCompleted().getTime() <= afterComplete.getTime());
    }

    @Test
    @DisplayName("complete() returns same instance for chaining")
    void testCompleteReturnsSameInstance()
  {
        MetadataNotification notification = new MetadataNotification();
        assertSame(notification, notification.complete());
    }

    @Test
    @DisplayName("increaseAttempts() increments attempt counter by 1")
    void testIncreaseAttemptsIncrementsByOne()
  {
        MetadataNotification notification = new MetadataNotification();
        assertEquals(0, notification.getAttempt());

        notification.increaseAttempts();
        assertEquals(1, notification.getAttempt());

        notification.increaseAttempts();
        assertEquals(2, notification.getAttempt());

        notification.increaseAttempts();
        assertEquals(3, notification.getAttempt());
    }

    @Test
    @DisplayName("increaseAttempts() returns same instance for chaining")
    void testIncreaseAttemptsReturnsSameInstance()
  {
        MetadataNotification notification = new MetadataNotification();
        assertSame(notification, notification.increaseAttempts());
    }

    @Test
    @DisplayName("retriesExceeded() returns false when attempt < maxAttempts")
    void testRetriesExceededWhenBelowMax()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification.setAttempt(0);
        notification.setMaxAttempts(2);

        assertFalse(notification.retriesExceeded());

        notification.setAttempt(1);
        assertFalse(notification.retriesExceeded());
    }

    @Test
    @DisplayName("retriesExceeded() returns true when attempt equals maxAttempts")
    void testRetriesExceededWhenEqualToMax()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setAttempt(2);
        notification.setMaxAttempts(2);

        assertTrue(notification.retriesExceeded());
    }

    @Test
    @DisplayName("retriesExceeded() returns true when attempt exceeds maxAttempts")
    void testRetriesExceededWhenAboveMax()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setAttempt(5);
        notification.setMaxAttempts(3);

        assertTrue(notification.retriesExceeded());
    }

    @Test
    @DisplayName("retriesExceeded() boundary test at max attempts")
    void testRetriesExceededBoundary()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setMaxAttempts(3);

        notification.setAttempt(2);
        assertFalse(notification.retriesExceeded());

        notification.setAttempt(3);
        assertTrue(notification.retriesExceeded());

        notification.setAttempt(4);
        assertTrue(notification.retriesExceeded());
    }

    // ========== Response Handling Tests ==========

    @Test
    @DisplayName("getResponses() returns empty map when responses is null")
    void testGetResponsesWhenNull()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setResponses(null);

        Map<Integer, MetadataNotificationResponse> responses = notification.getResponses();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("addError() creates response for current attempt and adds error")
    void testAddErrorCreatesResponseAndAddsError()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification.setAttempt(0);

        notification.addError("Error message 1");

        Map<Integer, MetadataNotificationResponse> responses = notification.getResponses();
        assertEquals(1, responses.size());
        assertTrue(responses.containsKey(0));

        MetadataNotificationResponse response = responses.get(0);
        assertNotNull(response);
        assertTrue(response.hasErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("Error message 1", response.getErrors().get(0));
        assertEquals(MetadataNotificationStatus.FAILED, response.getStatus());
    }

    @Test
    @DisplayName("addError() adds multiple errors to same attempt")
    void testAddErrorMultipleErrorsSameAttempt()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );

        notification.addError("Error 1");
        notification.addError("Error 2");
        notification.addError("Error 3");

        MetadataNotificationResponse response = notification.getResponses().get(0);
        assertEquals(3, response.getErrors().size());
        assertEquals("Error 1", response.getErrors().get(0));
        assertEquals("Error 2", response.getErrors().get(1));
        assertEquals("Error 3", response.getErrors().get(2));
    }

    @Test
    @DisplayName("addError() returns same instance for chaining")
    void testAddErrorReturnsSameInstance()
  {
        MetadataNotification notification = new MetadataNotification();
        assertSame(notification, notification.addError("Error"));
    }

    @Test
    @DisplayName("setResponse() stores response at current attempt")
    void testSetResponseStoresAtCurrentAttempt()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setAttempt(2);

        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Test message");
        notification.setResponse(response);

        Map<Integer, MetadataNotificationResponse> responses = notification.getResponses();
        assertEquals(1, responses.size());
        assertTrue(responses.containsKey(2));
        assertSame(response, responses.get(2));
    }

    @Test
    @DisplayName("setResponse() replaces existing response at current attempt")
    void testSetResponseReplacesExisting()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setAttempt(0);

        MetadataNotificationResponse response1 = new MetadataNotificationResponse();
        response1.addMessage("Message 1");
        notification.setResponse(response1);

        MetadataNotificationResponse response2 = new MetadataNotificationResponse();
        response2.addMessage("Message 2");
        notification.setResponse(response2);

        Map<Integer, MetadataNotificationResponse> responses = notification.getResponses();
        assertEquals(1, responses.size());
        assertSame(response2, responses.get(0));
        assertEquals(1, response2.getMessages().size());
        assertEquals("Message 2", response2.getMessages().get(0));
    }

    @Test
    @DisplayName("combineResponse() merges response into current attempt")
    void testCombineResponseMerges()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setAttempt(0);
        notification.addError("Existing error");

        MetadataNotificationResponse newResponse = new MetadataNotificationResponse();
        newResponse.addError("New error");
        newResponse.addMessage("New message");

        notification.combineResponse(newResponse);

        MetadataNotificationResponse combinedResponse = notification.getResponses().get(0);
        assertEquals(2, combinedResponse.getErrors().size());
        assertTrue(combinedResponse.getErrors().contains("Existing error"));
        assertTrue(combinedResponse.getErrors().contains("New error"));
        assertEquals(1, combinedResponse.getMessages().size());
        assertEquals("New message", combinedResponse.getMessages().get(0));
    }

    @Test
    @DisplayName("combineResponse() with null response does nothing")
    void testCombineResponseWithNull()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.addError("Existing error");

        notification.combineResponse(null);

        MetadataNotificationResponse response = notification.getResponses().get(0);
        assertEquals(1, response.getErrors().size());
        assertEquals("Existing error", response.getErrors().get(0));
    }

    @Test
    @DisplayName("combineResponse() returns same instance for chaining")
    void testCombineResponseReturnsSameInstance()
  {
        MetadataNotification notification = new MetadataNotification();
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        assertSame(notification, notification.combineResponse(response));
    }

    @Test
    @DisplayName("getCurrentResponse() returns null when no response for current attempt")
    void testGetCurrentResponseWhenNoResponse()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setAttempt(5);

        assertNull(notification.getCurrentResponse());
    }

    @Test
    @DisplayName("getCurrentResponse() returns correct response for current attempt")
    void testGetCurrentResponseReturnsCorrectResponse()
  {
        MetadataNotification notification = new MetadataNotification();

        notification.setAttempt(0);
        notification.addError("Error attempt 0");

        notification.setAttempt(1);
        notification.addError("Error attempt 1");

        notification.setAttempt(0);
        MetadataNotificationResponse response0 = notification.getCurrentResponse();
        assertNotNull(response0);
        assertEquals("Error attempt 0", response0.getErrors().get(0));

        notification.setAttempt(1);
        MetadataNotificationResponse response1 = notification.getCurrentResponse();
        assertNotNull(response1);
        assertEquals("Error attempt 1", response1.getErrors().get(0));
    }

    @Test
    @DisplayName("getStatus() returns SUCCESS when no response exists")
    void testGetStatusWhenNoResponse()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        assertEquals(MetadataNotificationStatus.SUCCESS, notification.getStatus());
    }

    @Test
    @DisplayName("getStatus() returns SUCCESS when response has no errors")
    void testGetStatusWhenNoErrors()
  {
        MetadataNotification notification = new MetadataNotification();
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        response.addMessage("Success message");
        notification.setResponse(response);

        assertEquals(MetadataNotificationStatus.SUCCESS, notification.getStatus());
    }

    @Test
    @DisplayName("getStatus() returns FAILED when response has errors")
    void testGetStatusWhenHasErrors()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.addError("Some error");

        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());
    }

    @Test
    @DisplayName("getStatus() reflects status of current attempt")
    void testGetStatusReflectsCurrentAttempt()
  {
        MetadataNotification notification = new MetadataNotification();

        notification.setAttempt(0);
        notification.addError("Error in attempt 0");
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());

        notification.setAttempt(1);
        MetadataNotificationResponse successResponse = new MetadataNotificationResponse();
        successResponse.addMessage("Success in attempt 1");
        notification.setResponse(successResponse);
        assertEquals(MetadataNotificationStatus.SUCCESS, notification.getStatus());

        notification.setAttempt(0);
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());
    }

    // ========== Multiple Attempts Integration Tests ==========

    @Test
    @DisplayName("Multiple attempts scenario: increment, add response, check status")
    void testMultipleAttemptsScenario()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification.setMaxAttempts(3);

        // Attempt 0: Fail
        assertEquals(0, notification.getAttempt());
        assertFalse(notification.retriesExceeded());
        notification.addError("First attempt failed");
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());

        // Attempt 1: Fail
        notification.increaseAttempts();
        assertEquals(1, notification.getAttempt());
        assertFalse(notification.retriesExceeded());
        notification.addError("Second attempt failed");
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());

        // Attempt 2: Fail
        notification.increaseAttempts();
        assertEquals(2, notification.getAttempt());
        assertFalse(notification.retriesExceeded());
        notification.addError("Third attempt failed");
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());

        // Attempt 3: Exceeded
        notification.increaseAttempts();
        assertEquals(3, notification.getAttempt());
        assertTrue(notification.retriesExceeded());

        // Verify all attempts are recorded
        Map<Integer, MetadataNotificationResponse> responses = notification.getResponses();
        assertEquals(3, responses.size());
        assertTrue(responses.containsKey(0));
        assertTrue(responses.containsKey(1));
        assertTrue(responses.containsKey(2));
    }

    @Test
    @DisplayName("Multiple attempts with eventual success")
    void testMultipleAttemptsWithSuccess()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification.setMaxAttempts(3);

        // Attempt 0: Fail
        notification.addError("First failed");
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());

        // Attempt 1: Fail
        notification.increaseAttempts();
        notification.addError("Second failed");
        assertEquals(MetadataNotificationStatus.FAILED, notification.getStatus());

        // Attempt 2: Success
        notification.increaseAttempts();
        MetadataNotificationResponse successResponse = new MetadataNotificationResponse();
        successResponse.addMessage("Success on third attempt");
        notification.setResponse(successResponse);
        assertEquals(MetadataNotificationStatus.SUCCESS, notification.getStatus());
        assertFalse(notification.retriesExceeded());

        notification.complete();
        assertNotNull(notification.getCompleted());
    }

    // ========== equals() and hashCode() Tests ==========

    @Test
    @DisplayName("equals() returns true for same object")
    void testEqualsSameObject()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        assertEquals(notification, notification);
    }

    @Test
    @DisplayName("equals() returns true for equal objects")
    void testEqualsEqualObjects()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification1.setEventId("event1");
        notification1.setFullUpdate(true);

        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification2.setEventId("event1");
        notification2.setFullUpdate(true);

        assertEquals(notification1, notification2);
    }

    @Test
    @DisplayName("equals() returns false for different projectId")
    void testEqualsWithDifferentProjectId()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        MetadataNotification notification2 = new MetadataNotification(
                "project2", "group1", "artifact1", "1.0.0"
        );

        assertNotEquals(notification1, notification2);
    }

    @Test
    @DisplayName("equals() returns false for different groupId")
    void testEqualsWithDifferentGroupId()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group2", "artifact1", "1.0.0"
        );

        assertNotEquals(notification1, notification2);
    }

    @Test
    @DisplayName("equals() returns false for different artifactId")
    void testEqualsWithDifferentArtifactId()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group1", "artifact2", "1.0.0"
        );

        assertNotEquals(notification1, notification2);
    }

    @Test
    @DisplayName("equals() returns false for different versionId")
    void testEqualsWithDifferentVersionId()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group1", "artifact1", "2.0.0"
        );

        assertNotEquals(notification1, notification2);
    }

    @Test
    @DisplayName("equals() returns false for null")
    void testEqualsWithNull()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        assertNotEquals(notification, null);
    }

    @Test
    @DisplayName("equals() returns false for different type")
    void testEqualsWithDifferentType()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        assertNotEquals(notification, "Not a MetadataNotification");
    }

    @Test
    @DisplayName("equals() ignores id field (marked with @EqualsExclude)")
    void testEqualsIgnoresId()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification1.setId("id1");

        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification2.setId("id2");

        // Should be equal despite different IDs
        assertEquals(notification1, notification2);
    }

    @Test
    @DisplayName("equals() ignores responses field (marked with @EqualsExclude)")
    void testEqualsIgnoresResponses()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification1.addError("Error 1");

        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification2.addError("Error 2");

        // Should be equal despite different responses
        assertEquals(notification1, notification2);
    }

    @Test
    @DisplayName("hashCode() returns same value for equal objects")
    void testHashCodeForEqualObjects()
  {
        MetadataNotification notification1 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification1.setEventId("event1");

        MetadataNotification notification2 = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );
        notification2.setEventId("event1");

        assertEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    @DisplayName("hashCode() is consistent across multiple calls")
    void testHashCodeConsistency()
  {
        MetadataNotification notification = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0"
        );

        int hashCode1 = notification.hashCode();
        int hashCode2 = notification.hashCode();
        int hashCode3 = notification.hashCode();

        assertEquals(hashCode1, hashCode2);
        assertEquals(hashCode2, hashCode3);
    }

    // ========== Edge Cases and Boundary Tests ==========

    @Test
    @DisplayName("Setting maxAttempts to 0 makes retriesExceeded immediately true")
    void testMaxAttemptsZero()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setMaxAttempts(0);
        notification.setAttempt(0);

        assertTrue(notification.retriesExceeded());
    }

    @Test
    @DisplayName("Setting maxAttempts to 1 allows exactly one attempt")
    void testMaxAttemptsOne()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setMaxAttempts(1);

        notification.setAttempt(0);
        assertFalse(notification.retriesExceeded());

        notification.setAttempt(1);
        assertTrue(notification.retriesExceeded());
    }

    @Test
    @DisplayName("Negative attempt value with retriesExceeded")
    void testNegativeAttemptValue()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setMaxAttempts(2);
        notification.setAttempt(-1);

        assertFalse(notification.retriesExceeded());
    }

    @Test
    @DisplayName("Large attempt values")
    void testLargeAttemptValues()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.setMaxAttempts(Integer.MAX_VALUE);
        notification.setAttempt(Integer.MAX_VALUE - 1);

        assertFalse(notification.retriesExceeded());

        notification.setAttempt(Integer.MAX_VALUE);
        assertTrue(notification.retriesExceeded());
    }

    @Test
    @DisplayName("Working with responses after setting to null")
    void testResponsesAfterSettingNull()
  {
        MetadataNotification notification = new MetadataNotification();
        notification.addError("Error 1");
        assertEquals(1, notification.getResponses().size());

        notification.setResponses(null);

        // getResponses() should create new map
        Map<Integer, MetadataNotificationResponse> responses = notification.getResponses();
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        // Should be able to add errors after null
        notification.addError("Error 2");
        assertEquals(1, notification.getResponses().size());
    }

    @Test
    @DisplayName("Complete can be called multiple times")
    void testCompleteMultipleTimes()
  {
        MetadataNotification notification = new MetadataNotification();

        notification.complete();
        Date firstCompleted = notification.getCompleted();
        assertNotNull(firstCompleted);

        // Sleep to ensure different timestamp
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        notification.complete();
        Date secondCompleted = notification.getCompleted();
        assertNotNull(secondCompleted);

        // Second complete should update the date
        assertTrue(secondCompleted.getTime() >= firstCompleted.getTime());
    }

    @Test
    @DisplayName("Priority enum values")
    void testPriorityEnumValues()
  {
        MetadataNotification highPriority = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0",
                true, false, "parent", Priority.HIGH
        );
        assertEquals(Priority.HIGH, highPriority.getEventPriority());

        MetadataNotification lowPriority = new MetadataNotification(
                "project1", "group1", "artifact1", "1.0.0",
                true, false, "parent", Priority.LOW
        );
        assertEquals(Priority.LOW, lowPriority.getEventPriority());
    }

    @Test
    @DisplayName("All boolean flag combinations in constructor")
    void testAllBooleanCombinations()
  {
        MetadataNotification n1 = new MetadataNotification(
                "p", "g", "a", "v", false, false, "parent", Priority.LOW
        );
        assertFalse(n1.isFullUpdate());
        assertFalse(n1.isTransitive());

        MetadataNotification n2 = new MetadataNotification(
                "p", "g", "a", "v", false, true, "parent", Priority.LOW
        );
        assertFalse(n2.isFullUpdate());
        assertTrue(n2.isTransitive());

        MetadataNotification n3 = new MetadataNotification(
                "p", "g", "a", "v", true, false, "parent", Priority.LOW
        );
        assertTrue(n3.isFullUpdate());
        assertFalse(n3.isTransitive());

        MetadataNotification n4 = new MetadataNotification(
                "p", "g", "a", "v", true, true, "parent", Priority.LOW
        );
        assertTrue(n4.isFullUpdate());
        assertTrue(n4.isTransitive());
    }

    @Test
    @DisplayName("Date fields can be set to past, present, and future dates")
    void testDateFieldsWithVariousDates()
  {
        MetadataNotification notification = new MetadataNotification();

        Date past = Date.from(LocalDate.of(2020, 1, 1)
                .atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        Date present = new Date();
        Date future = Date.from(LocalDate.of(2030, 12, 31)
                .atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

        notification.setCreated(past);
        notification.setUpdated(present);
        notification.setCompleted(future);

        assertSame(past, notification.getCreated());
        assertSame(present, notification.getUpdated());
        assertSame(future, notification.getCompleted());
    }
}

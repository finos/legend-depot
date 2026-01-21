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

package org.finos.legend.depot.services.api.notifications.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * Comprehensive tests for VoidQueue.
 * VoidQueue is a no-op implementation of Queue that returns empty results for all operations.
 */
class VoidQueueClaudeTest 

{

    private VoidQueue voidQueue;

    @BeforeEach
    void setUp()
  {
        voidQueue = new VoidQueue();
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Constructor creates a valid VoidQueue instance")
    void testConstructorCreatesValidInstance()
  {
        VoidQueue queue = new VoidQueue();

        assertNotNull(queue);
    }

    @Test
    @DisplayName("Multiple instances can be created independently")
    void testMultipleInstancesCanBeCreatedIndependently()
  {
        VoidQueue queue1 = new VoidQueue();
        VoidQueue queue2 = new VoidQueue();

        assertNotNull(queue1);
        assertNotNull(queue2);
    }

    // ========== getAll Tests ==========

    @Test
    @DisplayName("getAll returns an empty list")
    void testGetAllReturnsEmptyList()
  {
        List<MetadataNotification> result = voidQueue.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("getAll returns empty list consistently")
    void testGetAllReturnsEmptyListConsistently()
  {
        List<MetadataNotification> result1 = voidQueue.getAll();
        List<MetadataNotification> result2 = voidQueue.getAll();

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    @DisplayName("getAll returns empty list after push operation")
    void testGetAllReturnsEmptyListAfterPush()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        voidQueue.push(notification);

        List<MetadataNotification> result = voidQueue.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAll returns empty list after multiple push operations")
    void testGetAllReturnsEmptyListAfterMultiplePushes()
  {
        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));
        voidQueue.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0"));
        voidQueue.push(new MetadataNotification("project3", "group3", "artifact3", "3.0.0"));

        List<MetadataNotification> result = voidQueue.getAll();

        assertTrue(result.isEmpty());
    }

    // ========== pullAll Tests ==========

    @Test
    @DisplayName("pullAll returns an empty list")
    void testPullAllReturnsEmptyList()
  {
        List<MetadataNotification> result = voidQueue.pullAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("pullAll returns empty list consistently")
    void testPullAllReturnsEmptyListConsistently()
  {
        List<MetadataNotification> result1 = voidQueue.pullAll();
        List<MetadataNotification> result2 = voidQueue.pullAll();

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    @DisplayName("pullAll returns empty list after push operation")
    void testPullAllReturnsEmptyListAfterPush()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        voidQueue.push(notification);

        List<MetadataNotification> result = voidQueue.pullAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("pullAll and getAll both return empty lists")
    void testPullAllAndGetAllBothReturnEmptyLists()
  {
        List<MetadataNotification> getAllResult = voidQueue.getAll();
        List<MetadataNotification> pullAllResult = voidQueue.pullAll();

        assertTrue(getAllResult.isEmpty());
        assertTrue(pullAllResult.isEmpty());
    }

    // ========== getFirstInQueue Tests ==========

    @Test
    @DisplayName("getFirstInQueue returns empty Optional")
    void testGetFirstInQueueReturnsEmptyOptional()
  {
        Optional<MetadataNotification> result = voidQueue.getFirstInQueue();

        assertNotNull(result);
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getFirstInQueue returns empty Optional consistently")
    void testGetFirstInQueueReturnsEmptyOptionalConsistently()
  {
        Optional<MetadataNotification> result1 = voidQueue.getFirstInQueue();
        Optional<MetadataNotification> result2 = voidQueue.getFirstInQueue();

        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
    }

    @Test
    @DisplayName("getFirstInQueue returns empty Optional after push operation")
    void testGetFirstInQueueReturnsEmptyOptionalAfterPush()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        voidQueue.push(notification);

        Optional<MetadataNotification> result = voidQueue.getFirstInQueue();

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("getFirstInQueue returns empty Optional after multiple push operations")
    void testGetFirstInQueueReturnsEmptyOptionalAfterMultiplePushes()
  {
        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));
        voidQueue.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0"));

        Optional<MetadataNotification> result = voidQueue.getFirstInQueue();

        assertFalse(result.isPresent());
    }

    // ========== get(String) Tests ==========

    @Test
    @DisplayName("get returns empty Optional for any eventId")
    void testGetReturnsEmptyOptionalForAnyEventId()
  {
        Optional<MetadataNotification> result = voidQueue.get("event123");

        assertNotNull(result);
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("get returns empty Optional for null eventId")
    void testGetReturnsEmptyOptionalForNullEventId()
  {
        Optional<MetadataNotification> result = voidQueue.get(null);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("get returns empty Optional for empty string eventId")
    void testGetReturnsEmptyOptionalForEmptyStringEventId()
  {
        Optional<MetadataNotification> result = voidQueue.get("");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("get returns empty Optional for various eventIds")
    void testGetReturnsEmptyOptionalForVariousEventIds()
  {
        assertFalse(voidQueue.get("event1").isPresent());
        assertFalse(voidQueue.get("event2").isPresent());
        assertFalse(voidQueue.get("123").isPresent());
        assertFalse(voidQueue.get("abc-def-ghi").isPresent());
    }

    @Test
    @DisplayName("get returns empty Optional even after pushing notification with that eventId")
    void testGetReturnsEmptyOptionalEvenAfterPushingWithEventId()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        notification.setEventId("event123");
        voidQueue.push(notification);

        Optional<MetadataNotification> result = voidQueue.get("event123");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("get returns empty Optional for long eventId string")
    void testGetReturnsEmptyOptionalForLongEventId()
  {
        String longEventId = "a".repeat(1000);
        Optional<MetadataNotification> result = voidQueue.get(longEventId);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("get returns empty Optional for special character eventId")
    void testGetReturnsEmptyOptionalForSpecialCharacterEventId()
  {
        assertFalse(voidQueue.get("!@#$%^&*()").isPresent());
        assertFalse(voidQueue.get("event-with-dashes").isPresent());
        assertFalse(voidQueue.get("event_with_underscores").isPresent());
    }

    // ========== push Tests ==========

    @Test
    @DisplayName("push returns null for any notification")
    void testPushReturnsNullForAnyNotification()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");

        String result = voidQueue.push(notification);

        assertNull(result);
    }

    @Test
    @DisplayName("push returns null for notification with eventId")
    void testPushReturnsNullForNotificationWithEventId()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0");
        notification.setEventId("event123");

        String result = voidQueue.push(notification);

        assertNull(result);
    }

    @Test
    @DisplayName("push returns null for notification with all fields set")
    void testPushReturnsNullForNotificationWithAllFields()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0", true, true, "parentEvent", Priority.HIGH);
        notification.setEventId("event123");

        String result = voidQueue.push(notification);

        assertNull(result);
    }

    @Test
    @DisplayName("push returns null for null notification")
    void testPushReturnsNullForNullNotification()
  {
        String result = voidQueue.push(null);

        assertNull(result);
    }

    @Test
    @DisplayName("push returns null consistently for multiple pushes")
    void testPushReturnsNullConsistentlyForMultiplePushes()
  {
        assertNull(voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0")));
        assertNull(voidQueue.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0")));
        assertNull(voidQueue.push(new MetadataNotification("project3", "group3", "artifact3", "3.0.0")));
    }

    @Test
    @DisplayName("push does not affect queue size")
    void testPushDoesNotAffectQueueSize()
  {
        assertEquals(0L, voidQueue.size());

        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));

        assertEquals(0L, voidQueue.size());
    }

    @Test
    @DisplayName("push with HIGH priority returns null")
    void testPushWithHighPriorityReturnsNull()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0", false, false, null, Priority.HIGH);

        String result = voidQueue.push(notification);

        assertNull(result);
    }

    @Test
    @DisplayName("push with LOW priority returns null")
    void testPushWithLowPriorityReturnsNull()
  {
        MetadataNotification notification = new MetadataNotification("project1", "group1", "artifact1", "1.0.0", false, false, null, Priority.LOW);

        String result = voidQueue.push(notification);

        assertNull(result);
    }

    // ========== size Tests ==========

    @Test
    @DisplayName("size returns zero")
    void testSizeReturnsZero()
  {
        long result = voidQueue.size();

        assertEquals(0L, result);
    }

    @Test
    @DisplayName("size returns zero consistently")
    void testSizeReturnsZeroConsistently()
  {
        assertEquals(0L, voidQueue.size());
        assertEquals(0L, voidQueue.size());
        assertEquals(0L, voidQueue.size());
    }

    @Test
    @DisplayName("size returns zero after push operation")
    void testSizeReturnsZeroAfterPush()
  {
        assertEquals(0L, voidQueue.size());

        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));

        assertEquals(0L, voidQueue.size());
    }

    @Test
    @DisplayName("size returns zero after multiple push operations")
    void testSizeReturnsZeroAfterMultiplePushes()
  {
        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));
        voidQueue.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0"));
        voidQueue.push(new MetadataNotification("project3", "group3", "artifact3", "3.0.0"));

        assertEquals(0L, voidQueue.size());
    }

    @Test
    @DisplayName("size returns zero after pullAll operation")
    void testSizeReturnsZeroAfterPullAll()
  {
        voidQueue.pullAll();

        assertEquals(0L, voidQueue.size());
    }

    @Test
    @DisplayName("size returns zero after deleteAll operation")
    void testSizeReturnsZeroAfterDeleteAll()
  {
        voidQueue.deleteAll();

        assertEquals(0L, voidQueue.size());
    }

    // ========== deleteAll Tests ==========

    @Test
    @DisplayName("deleteAll returns zero")
    void testDeleteAllReturnsZero()
  {
        long result = voidQueue.deleteAll();

        assertEquals(0L, result);
    }

    @Test
    @DisplayName("deleteAll returns zero consistently")
    void testDeleteAllReturnsZeroConsistently()
  {
        assertEquals(0L, voidQueue.deleteAll());
        assertEquals(0L, voidQueue.deleteAll());
        assertEquals(0L, voidQueue.deleteAll());
    }

    @Test
    @DisplayName("deleteAll returns zero after push operation")
    void testDeleteAllReturnsZeroAfterPush()
  {
        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));

        long result = voidQueue.deleteAll();

        assertEquals(0L, result);
    }

    @Test
    @DisplayName("deleteAll returns zero after multiple push operations")
    void testDeleteAllReturnsZeroAfterMultiplePushes()
  {
        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));
        voidQueue.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0"));

        long result = voidQueue.deleteAll();

        assertEquals(0L, result);
    }

    @Test
    @DisplayName("deleteAll does not affect subsequent operations")
    void testDeleteAllDoesNotAffectSubsequentOperations()
  {
        voidQueue.deleteAll();

        assertTrue(voidQueue.getAll().isEmpty());
        assertTrue(voidQueue.pullAll().isEmpty());
        assertFalse(voidQueue.getFirstInQueue().isPresent());
        assertFalse(voidQueue.get("event1").isPresent());
        assertEquals(0L, voidQueue.size());
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("All operations return empty/null/zero consistently")
    void testAllOperationsReturnEmptyNullZeroConsistently()
  {
        assertTrue(voidQueue.getAll().isEmpty());
        assertTrue(voidQueue.pullAll().isEmpty());
        assertFalse(voidQueue.getFirstInQueue().isPresent());
        assertFalse(voidQueue.get("event1").isPresent());
        assertNull(voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0")));
        assertEquals(0L, voidQueue.size());
        assertEquals(0L, voidQueue.deleteAll());
    }

    @Test
    @DisplayName("Multiple operations in sequence return empty/null/zero")
    void testMultipleOperationsInSequenceReturnEmptyNullZero()
  {
        voidQueue.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));
        assertTrue(voidQueue.getAll().isEmpty());

        voidQueue.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0"));
        assertTrue(voidQueue.pullAll().isEmpty());

        voidQueue.push(new MetadataNotification("project3", "group3", "artifact3", "3.0.0"));
        assertFalse(voidQueue.getFirstInQueue().isPresent());

        assertEquals(0L, voidQueue.size());
        assertEquals(0L, voidQueue.deleteAll());
    }

    @Test
    @DisplayName("Queue behavior is consistent across multiple instances")
    void testQueueBehaviorIsConsistentAcrossMultipleInstances()
  {
        VoidQueue queue1 = new VoidQueue();
        VoidQueue queue2 = new VoidQueue();

        queue1.push(new MetadataNotification("project1", "group1", "artifact1", "1.0.0"));
        queue2.push(new MetadataNotification("project2", "group2", "artifact2", "2.0.0"));

        assertTrue(queue1.getAll().isEmpty());
        assertTrue(queue2.getAll().isEmpty());
        assertEquals(0L, queue1.size());
        assertEquals(0L, queue2.size());
    }

    @Test
    @DisplayName("VoidQueue maintains no-op behavior after many operations")
    void testVoidQueueMaintainsNoOpBehaviorAfterManyOperations()
  {
        // Perform many operations
        for (int i = 0; i < 100; i++) {
            voidQueue.push(new MetadataNotification("project" + i, "group" + i, "artifact" + i, i + ".0.0"));
        }

        // Verify still returns empty/null/zero
        assertTrue(voidQueue.getAll().isEmpty());
        assertTrue(voidQueue.pullAll().isEmpty());
        assertFalse(voidQueue.getFirstInQueue().isPresent());
        assertFalse(voidQueue.get("event1").isPresent());
        assertEquals(0L, voidQueue.size());
        assertEquals(0L, voidQueue.deleteAll());
    }

    @Test
    @DisplayName("VoidQueue operations are safe with null inputs")
    void testVoidQueueOperationsAreSafeWithNullInputs()
  {
        assertNull(voidQueue.push(null));
        assertFalse(voidQueue.get(null).isPresent());
    }
}

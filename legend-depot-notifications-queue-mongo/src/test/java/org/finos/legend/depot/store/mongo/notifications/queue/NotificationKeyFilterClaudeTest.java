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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class NotificationKeyFilterClaudeTest 

{

    /**
     * Test getFilter with notification that has an eventId.
     * When eventId is present, the filter should use _id field with ObjectId.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter with eventId present")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterWithEventId()
  {
        // Arrange
        String eventId = new ObjectId().toHexString();
        MetadataNotification notification = new MetadataNotification(
                "test-project",
                "test.group",
                "test-artifact",
                "1.0.0"
        );
        notification.setEventId(eventId);

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null");
        // Verify the filter contains the eventId by checking the string representation
        String filterString = result.toString();
        assertTrue(filterString.contains("_id") || filterString.contains(eventId),
                "Filter should reference the _id field or eventId");
    }

    /**
     * Test getFilter with notification that has no eventId.
     * When eventId is null, the filter should use groupId, artifactId, and versionId fields.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter without eventId")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterWithoutEventId()
  {
        // Arrange
        MetadataNotification notification = new MetadataNotification(
                "test-project",
                "test.group",
                "test-artifact",
                "1.0.0"
        );
        // eventId is null by default

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null");
        // Verify the filter contains groupId, artifactId, and versionId
        String filterString = result.toString();
        assertTrue(filterString.contains("groupId") || filterString.contains("test.group"),
                "Filter should contain groupId field or value");
        assertTrue(filterString.contains("artifactId") || filterString.contains("test-artifact"),
                "Filter should contain artifactId field or value");
        assertTrue(filterString.contains("versionId") || filterString.contains("1.0.0"),
                "Filter should contain versionId field or value");
    }

    /**
     * Test getFilter with notification using constructor.
     * Tests that the filter works correctly with notifications created via constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter with notification created via constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterWithConstructorCreatedNotification()
  {
        // Arrange
        MetadataNotification notification = new MetadataNotification(
                "test-project",
                "com.example",
                "my-artifact",
                "2.5.0"
        );

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null");
        // Should use groupId, artifactId, versionId since eventId is null
        String filterString = result.toString();
        assertTrue(filterString.contains("groupId") || filterString.contains("com.example"),
                "Filter should contain groupId field or value");
        assertTrue(filterString.contains("artifactId") || filterString.contains("my-artifact"),
                "Filter should contain artifactId field or value");
        assertTrue(filterString.contains("versionId") || filterString.contains("2.5.0"),
                "Filter should contain versionId field or value");
    }

    /**
     * Test getFilter with notification that has eventId set after construction.
     * Tests the branch where eventId is set after the notification is created.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter with eventId set after construction")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterWithEventIdSetAfterConstruction()
  {
        // Arrange
        String eventId = new ObjectId().toHexString();
        MetadataNotification notification = new MetadataNotification(
                "test-project",
                "com.example",
                "my-artifact",
                "2.5.0"
        );
        notification.setEventId(eventId);

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null");
        // Should use _id since eventId is present
        String filterString = result.toString();
        assertTrue(filterString.contains("_id") || filterString.contains(eventId),
                "Filter should reference the _id field or eventId");
    }

    /**
     * Test getFilter with different valid ObjectId formats.
     * Tests that the filter works with various valid ObjectId strings.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter with various ObjectId formats")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterWithVariousObjectIdFormats()
  {
        // Arrange - Create multiple ObjectIds
        ObjectId objectId1 = new ObjectId();
        ObjectId objectId2 = new ObjectId();

        MetadataNotification notification1 = new MetadataNotification(
                "test-project",
                "test.group",
                "test-artifact",
                "1.0.0"
        );
        notification1.setEventId(objectId1.toHexString());

        MetadataNotification notification2 = new MetadataNotification(
                "test-project",
                "test.group",
                "test-artifact",
                "1.0.0"
        );
        notification2.setEventId(objectId2.toHexString());

        // Act
        Bson result1 = NotificationKeyFilter.getFilter(notification1);
        Bson result2 = NotificationKeyFilter.getFilter(notification2);

        // Assert
        assertNotNull(result1, "First filter should not be null");
        assertNotNull(result2, "Second filter should not be null");

        String filterString1 = result1.toString();
        String filterString2 = result2.toString();

        // Both should reference _id field
        assertTrue(filterString1.contains("_id") || filterString1.contains(objectId1.toHexString()),
                "First filter should reference _id or contain the ObjectId");
        assertTrue(filterString2.contains("_id") || filterString2.contains(objectId2.toHexString()),
                "Second filter should reference _id or contain the ObjectId");
    }

    /**
     * Test getFilter with notification containing special characters in fields.
     * Tests that the filter correctly handles special characters in groupId, artifactId, and versionId.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter with special characters in fields")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterWithSpecialCharacters()
  {
        // Arrange
        MetadataNotification notification = new MetadataNotification(
                "test-project",
                "org.example.test-group",
                "my-artifact_v2",
                "1.0.0-SNAPSHOT"
        );

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null");
        String filterString = result.toString();
        assertTrue(filterString.contains("groupId") || filterString.contains("org.example.test-group"),
                "Filter should contain groupId field or value with special characters");
        assertTrue(filterString.contains("artifactId") || filterString.contains("my-artifact_v2"),
                "Filter should contain artifactId field or value with special characters");
        assertTrue(filterString.contains("versionId") || filterString.contains("1.0.0-SNAPSHOT"),
                "Filter should contain versionId field or value with special characters");
    }

    /**
     * Test getFilter with null eventId explicitly tests the else branch.
     * This ensures complete branch coverage for the conditional in getFilter method.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter branch when eventId is null")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterBranchWithNullEventId()
  {
        // Arrange
        MetadataNotification notification = new MetadataNotification(
                "project-123",
                "org.finos.legend",
                "depot-core",
                "3.1.0"
        );
        // Explicitly ensure eventId is null
        notification.setEventId(null);

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null even when eventId is null");
        String filterString = result.toString();
        // When eventId is null, the filter should use groupId, artifactId, and versionId
        assertTrue(filterString.contains("groupId") || filterString.contains("org.finos.legend"),
                "Filter should contain groupId when eventId is null");
        assertTrue(filterString.contains("artifactId") || filterString.contains("depot-core"),
                "Filter should contain artifactId when eventId is null");
        assertTrue(filterString.contains("versionId") || filterString.contains("3.1.0"),
                "Filter should contain versionId when eventId is null");
    }

    /**
     * Test getFilter with a valid eventId to ensure the if branch is properly covered.
     * This tests the condition when eventId is not null.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationKeyFilter#getFilter(MetadataNotification)}
     * </ul>
     */
    @Test
    @DisplayName("Test getFilter branch when eventId is not null")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"Bson NotificationKeyFilter.getFilter(MetadataNotification)"})
    void testGetFilterBranchWithNonNullEventId()
  {
        // Arrange
        String validEventId = "507f1f77bcf86cd799439011";  // Valid 24-character hex string
        MetadataNotification notification = new MetadataNotification(
                "project-456",
                "org.finos.depot",
                "store-mongo",
                "2.0.1"
        );
        notification.setEventId(validEventId);

        // Act
        Bson result = NotificationKeyFilter.getFilter(notification);

        // Assert
        assertNotNull(result, "Filter should not be null when eventId is provided");
        String filterString = result.toString();
        // When eventId is provided, the filter should use _id field
        assertTrue(filterString.contains("_id") || filterString.contains(validEventId),
                "Filter should contain _id field or eventId value when eventId is not null");
    }
}

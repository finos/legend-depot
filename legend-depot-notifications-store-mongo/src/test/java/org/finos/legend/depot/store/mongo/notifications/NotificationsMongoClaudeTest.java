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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexModel;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.notifications.MetadataNotificationStatus;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.DatesHandler.toDate;

public class NotificationsMongoClaudeTest extends TestStoreMongo
{
    private static final String GROUP_ID = "test.group";
    private static final String ARTIFACT_ID = "test-artifact";
    private static final String VERSION = "1.0.0";
    private static final String PROJECT_ID = "test-project";

    @Test
    public void testConstructor()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);
        Assertions.assertNotNull(notificationsMongo);
    }

    @Test
    public void testGetCollection()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);
        MongoCollection collection = notificationsMongo.getCollection();

        Assertions.assertNotNull(collection);
        Assertions.assertEquals("notifications", collection.getNamespace().getCollectionName());
    }

    @Test
    public void testBuildIndexes()
  {
        List<IndexModel> indexes = NotificationsMongo.buildIndexes();

        Assertions.assertNotNull(indexes);
        Assertions.assertEquals(5, indexes.size());

        // Verify index names exist
        boolean hasParentId = false;
        boolean hasStatus = false;
        boolean hasLastUpdated = false;
        boolean hasGroupArtifactVersion = false;
        boolean hasEventId = false;

        for (IndexModel index : indexes)
        {
            String indexName = index.getOptions().getName();
            if ("parentId".equals(indexName)) hasParentId = true;
            if ("status".equals(indexName)) hasStatus = true;
            if ("lastUpdated".equals(indexName)) hasLastUpdated = true;
            if ("groupId-artifactId-versionId".equals(indexName)) hasGroupArtifactVersion = true;
            if ("eventId".equals(indexName)) hasEventId = true;
        }

        Assertions.assertTrue(hasParentId, "Should have parentId index");
        Assertions.assertTrue(hasStatus, "Should have status index");
        Assertions.assertTrue(hasLastUpdated, "Should have lastUpdated index");
        Assertions.assertTrue(hasGroupArtifactVersion, "Should have groupId-artifactId-versionId index");
        Assertions.assertTrue(hasEventId, "Should have eventId index");
    }

    @Test
    public void testGetKeyFilterWithEventId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        // Create notification with eventId
        String eventId = new ObjectId().toString();
        MetadataNotification notification = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        notification.setEventId(eventId);

        Bson filter = notificationsMongo.getKeyFilter(notification);

        Assertions.assertNotNull(filter);
        // Filter should use ObjectId when eventId is present
        String filterString = filter.toString();
        Assertions.assertTrue(filterString.contains("_id"));
    }

    @Test
    public void testGetKeyFilterWithoutEventId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        // Create notification without eventId
        MetadataNotification notification = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);

        Bson filter = notificationsMongo.getKeyFilter(notification);

        Assertions.assertNotNull(filter);
        // Filter should use groupId, artifactId, versionId when eventId is null
        String filterString = filter.toString();
        Assertions.assertTrue(filterString.contains("groupId"));
        Assertions.assertTrue(filterString.contains("artifactId"));
        Assertions.assertTrue(filterString.contains("versionId"));
    }

    @Test
    public void testGetAll()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        // Initially empty
        List<MetadataNotification> empty = notificationsMongo.getAll();
        Assertions.assertNotNull(empty);
        Assertions.assertEquals(0, empty.size());

        // Add some notifications
        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        MetadataNotification n3 = new MetadataNotification(PROJECT_ID, "group2", ARTIFACT_ID, VERSION);

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);
        notificationsMongo.createOrUpdate(n3);

        List<MetadataNotification> all = notificationsMongo.getAll();
        Assertions.assertNotNull(all);
        Assertions.assertEquals(3, all.size());
    }

    @Test
    public void testValidateNewData()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);
        MetadataNotification notification = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);

        // validateNewData does nothing, just ensure it doesn't throw
        Assertions.assertDoesNotThrow(() -> notificationsMongo.validateNewData(notification));
    }

    @Test
    public void testGetByEventId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        // Create and store notification with eventId set
        MetadataNotification notification = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        String testEventId = new ObjectId().toString();
        notification.setEventId(testEventId);
        notificationsMongo.createOrUpdate(notification);

        // Test get by eventId
        Optional<MetadataNotification> found = notificationsMongo.get(testEventId);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(testEventId, found.get().getEventId());
        Assertions.assertEquals(GROUP_ID, found.get().getGroupId());
        Assertions.assertEquals(ARTIFACT_ID, found.get().getArtifactId());
    }

    @Test
    public void testGetByEventIdNotFound()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        Optional<MetadataNotification> found = notificationsMongo.get("nonexistent-event-id");
        Assertions.assertFalse(found.isPresent());
    }

    @Test
    public void testFindByGroupId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, "group1", ARTIFACT_ID, VERSION);
        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, "group2", ARTIFACT_ID, VERSION);

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);

        List<MetadataNotification> found = notificationsMongo.find("group1", null, null, null, null, null, null, null);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("group1", found.get(0).getGroupId());
    }

    @Test
    public void testFindByArtifactId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact1", VERSION);
        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);

        List<MetadataNotification> found = notificationsMongo.find(null, "artifact1", null, null, null, null, null, null);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("artifact1", found.get(0).getArtifactId());
    }

    @Test
    public void testFindByVersion()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, "1.0.0");
        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, "2.0.0");

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);

        List<MetadataNotification> found = notificationsMongo.find(null, null, "2.0.0", null, null, null, null, null);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("2.0.0", found.get(0).getVersionId());
    }

    @Test
    public void testFindByEventId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        String testEventId = new ObjectId().toString();
        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        n1.setEventId(testEventId);
        notificationsMongo.createOrUpdate(n1);

        List<MetadataNotification> found = notificationsMongo.find(null, null, null, testEventId, null, null, null, null);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(testEventId, found.get(0).getEventId());
    }

    @Test
    public void testFindByParentEventId()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        String parentEventId = "parent-event-123";
        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        n1.setParentEventId(parentEventId);

        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        n2.setParentEventId("different-parent");

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);

        List<MetadataNotification> found = notificationsMongo.find(null, null, null, null, parentEventId, null, null, null);
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(parentEventId, found.get(0).getParentEventId());
    }

    @Test
    public void testFindBySuccessStatus()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        // Create notification with success status (no errors)
        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        MetadataNotificationResponse response1 = new MetadataNotificationResponse();
        response1.addMessage("Success message");
        n1.setResponse(response1);

        // Create notification with failed status (has errors)
        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        MetadataNotificationResponse response2 = new MetadataNotificationResponse();
        response2.addError("Some error occurred");
        n2.setResponse(response2);

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);

        // Find successful notifications
        List<MetadataNotification> successFound = notificationsMongo.find(null, null, null, null, null, true, null, null);
        Assertions.assertEquals(1, successFound.size());
        Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, successFound.get(0).getStatus());

        // Find failed notifications
        List<MetadataNotification> failedFound = notificationsMongo.find(null, null, null, null, null, false, null, null);
        Assertions.assertEquals(1, failedFound.size());
        Assertions.assertEquals(MetadataNotificationStatus.FAILED, failedFound.get(0).getStatus());
    }

    @Test
    public void testFindByDateRange()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact1", VERSION);
        n1.setUpdated(toDate(baseTime));

        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        n2.setUpdated(toDate(baseTime.plusHours(2)));

        MetadataNotification n3 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact3", VERSION);
        n3.setUpdated(toDate(baseTime.plusHours(4)));

        insertRaw(NotificationsMongo.COLLECTION, n1);
        insertRaw(NotificationsMongo.COLLECTION, n2);
        insertRaw(NotificationsMongo.COLLECTION, n3);

        // Find notifications between baseTime and baseTime+3 hours
        List<MetadataNotification> found = notificationsMongo.find(
            null, null, null, null, null, null,
            baseTime, baseTime.plusHours(3)
        );

        Assertions.assertEquals(2, found.size());
    }

    @Test
    public void testFindWithOnlyFromDate()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact1", VERSION);
        n1.setUpdated(toDate(baseTime));

        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        n2.setUpdated(toDate(baseTime.plusHours(2)));

        insertRaw(NotificationsMongo.COLLECTION, n1);
        insertRaw(NotificationsMongo.COLLECTION, n2);

        // Find notifications after baseTime+1 hour (should get only n2)
        List<MetadataNotification> found = notificationsMongo.find(
            null, null, null, null, null, null,
            baseTime.plusHours(1), null
        );

        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("artifact2", found.get(0).getArtifactId());
    }

    @Test
    public void testFindWithOnlyToDate()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact1", VERSION);
        n1.setUpdated(toDate(baseTime));

        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        n2.setUpdated(toDate(baseTime.plusHours(2)));

        insertRaw(NotificationsMongo.COLLECTION, n1);
        insertRaw(NotificationsMongo.COLLECTION, n2);

        // Find notifications before baseTime+1 hour (should get only n1)
        List<MetadataNotification> found = notificationsMongo.find(
            null, null, null, null, null, null,
            null, baseTime.plusHours(1)
        );

        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("artifact1", found.get(0).getArtifactId());
    }

    @Test
    public void testFindWithMultipleFilters()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, "group1", "artifact1", "1.0.0");
        MetadataNotificationResponse response1 = new MetadataNotificationResponse();
        response1.addMessage("Success");
        n1.setResponse(response1);

        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, "group1", "artifact2", "1.0.0");
        MetadataNotificationResponse response2 = new MetadataNotificationResponse();
        response2.addMessage("Success");
        n2.setResponse(response2);

        MetadataNotification n3 = new MetadataNotification(PROJECT_ID, "group2", "artifact1", "1.0.0");
        MetadataNotificationResponse response3 = new MetadataNotificationResponse();
        response3.addError("Failed");
        n3.setResponse(response3);

        notificationsMongo.createOrUpdate(n1);
        notificationsMongo.createOrUpdate(n2);
        notificationsMongo.createOrUpdate(n3);

        // Find notifications with group1 and success status
        List<MetadataNotification> found = notificationsMongo.find(
            "group1", null, null, null, null, true, null, null
        );

        Assertions.assertEquals(2, found.size());
        found.forEach(n ->
                {
            Assertions.assertEquals("group1", n.getGroupId());
            Assertions.assertEquals(MetadataNotificationStatus.SUCCESS, n.getStatus());
        });
    }

    @Test
    public void testFindResultsSortedByUpdatedDescending()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 12, 0);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact1", VERSION);
        n1.setUpdated(toDate(baseTime));

        MetadataNotification n2 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact2", VERSION);
        n2.setUpdated(toDate(baseTime.plusHours(1)));

        MetadataNotification n3 = new MetadataNotification(PROJECT_ID, GROUP_ID, "artifact3", VERSION);
        n3.setUpdated(toDate(baseTime.plusHours(2)));

        insertRaw(NotificationsMongo.COLLECTION, n1);
        insertRaw(NotificationsMongo.COLLECTION, n2);
        insertRaw(NotificationsMongo.COLLECTION, n3);

        List<MetadataNotification> found = notificationsMongo.find(null, null, null, null, null, null, null, null);

        Assertions.assertEquals(3, found.size());
        // Should be sorted descending by updated time
        Assertions.assertEquals("artifact3", found.get(0).getArtifactId());
        Assertions.assertEquals("artifact2", found.get(1).getArtifactId());
        Assertions.assertEquals("artifact1", found.get(2).getArtifactId());
    }

    @Test
    public void testDelete()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        MetadataNotification notification = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        notificationsMongo.createOrUpdate(notification);

        List<MetadataNotification> all = notificationsMongo.getAll();
        Assertions.assertEquals(1, all.size());

        String id = all.get(0).getId();
        Assertions.assertNotNull(id);

        // Delete the notification
        notificationsMongo.delete(id);

        // Verify it's deleted
        List<MetadataNotification> afterDelete = notificationsMongo.getAll();
        Assertions.assertEquals(0, afterDelete.size());
    }

    @Test
    public void testDeleteNonExistent()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        // Try to delete a non-existent notification with a valid ObjectId format
        String fakeId = new ObjectId().toString();

        // Should not throw exception
        Assertions.assertDoesNotThrow(() -> notificationsMongo.delete(fakeId));
    }

    @Test
    public void testFindWithNoResults()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        List<MetadataNotification> found = notificationsMongo.find(
            "nonexistent-group", null, null, null, null, null, null, null
        );

        Assertions.assertNotNull(found);
        Assertions.assertEquals(0, found.size());
    }

    @Test
    public void testFindWithAllNullParameters()
  {
        NotificationsMongo notificationsMongo = new NotificationsMongo(mongoProvider);

        MetadataNotification n1 = new MetadataNotification(PROJECT_ID, GROUP_ID, ARTIFACT_ID, VERSION);
        notificationsMongo.createOrUpdate(n1);

        // Find with all null parameters should return all notifications
        List<MetadataNotification> found = notificationsMongo.find(
            null, null, null, null, null, null, null, null
        );

        Assertions.assertEquals(1, found.size());
    }
}

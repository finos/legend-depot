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

package org.finos.legend.depot.store.mongo.schedules;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexModel;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInfo;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class SchedulesMongoClaudeTest extends TestStoreMongo
{

    @Test
    public void testConstructor()
  {
        // Test that constructor properly initializes the object
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);
        Assertions.assertNotNull(store);
    }

    @Test
    public void testValidateNewData()
  {
        // validateNewData is empty implementation, but we test it doesn't throw
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);
        ScheduleInfo scheduleInfo = new ScheduleInfo("test-schedule");

        // This should not throw an exception
        Assertions.assertDoesNotThrow(() -> store.createOrUpdate(scheduleInfo));
    }

    @Test
    public void testGetCollection()
  {
        // Test that getCollection returns the correct collection
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        // Insert a test schedule to verify collection is accessible
        ScheduleInfo scheduleInfo = new ScheduleInfo("test-schedule");
        store.createOrUpdate(scheduleInfo);

        // Verify by retrieving
        List<ScheduleInfo> all = store.getAll();
        Assertions.assertEquals(1, all.size());
    }

    @Test
    public void testGetKeyFilter()
  {
        // Test that schedules can be filtered by name (the key field)
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        // Insert two schedules with different names
        ScheduleInfo schedule1 = new ScheduleInfo("schedule-1");
        ScheduleInfo schedule2 = new ScheduleInfo("schedule-2");

        store.createOrUpdate(schedule1);
        store.createOrUpdate(schedule2);

        // Get by name (which uses getKeyFilter internally)
        Optional<ScheduleInfo> found = store.get("schedule-1");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("schedule-1", found.get().getName());
    }

    @Test
    public void testGet_ExistingSchedule()
  {
        // Test getting an existing schedule by name
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("test-schedule");
        scheduleInfo.setFrequency(3600L);
        scheduleInfo.setDisabled(false);
        scheduleInfo.setSingleInstance(true);
        scheduleInfo.setExternalTrigger(false);

        store.createOrUpdate(scheduleInfo);

        Optional<ScheduleInfo> found = store.get("test-schedule");

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("test-schedule", found.get().getName());
        Assertions.assertEquals(3600L, found.get().getFrequency());
        Assertions.assertFalse(found.get().isDisabled());
        Assertions.assertTrue(found.get().getSingleInstance());
        Assertions.assertFalse(found.get().getExternalTrigger());
    }

    @Test
    public void testGet_NonExistingSchedule()
  {
        // Test getting a schedule that doesn't exist
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("existing-schedule");
        store.createOrUpdate(scheduleInfo);

        Optional<ScheduleInfo> found = store.get("non-existing-schedule");

        Assertions.assertFalse(found.isPresent());
    }

    @Test
    public void testGet_EmptyCollection()
  {
        // Test get on empty collection
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        Optional<ScheduleInfo> found = store.get("any-schedule");

        Assertions.assertFalse(found.isPresent());
    }

    @Test
    public void testGetAll_EmptyCollection()
  {
        // Test getAll with empty collection
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        List<ScheduleInfo> all = store.getAll();
        Assertions.assertNotNull(all);
        Assertions.assertTrue(all.isEmpty());
    }

    @Test
    public void testGetAll_WithSchedules()
  {
        // Test getAll with multiple schedules
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo schedule1 = new ScheduleInfo("schedule-1");
        ScheduleInfo schedule2 = new ScheduleInfo("schedule-2");
        ScheduleInfo schedule3 = new ScheduleInfo("schedule-3");

        store.createOrUpdate(schedule1);
        store.createOrUpdate(schedule2);
        store.createOrUpdate(schedule3);

        List<ScheduleInfo> all = store.getAll();
        Assertions.assertEquals(3, all.size());
    }

    @Test
    public void testDelete_ExistingSchedule()
  {
        // Test deleting an existing schedule
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("to-be-deleted");
        store.createOrUpdate(scheduleInfo);

        // Verify it exists
        Optional<ScheduleInfo> found = store.get("to-be-deleted");
        Assertions.assertTrue(found.isPresent());

        // Delete it
        store.delete("to-be-deleted");

        // Verify it's gone
        found = store.get("to-be-deleted");
        Assertions.assertFalse(found.isPresent());
    }

    @Test
    public void testDelete_NonExistingSchedule()
  {
        // Test deleting a schedule that doesn't exist (should not throw)
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("existing-schedule");
        store.createOrUpdate(scheduleInfo);

        // Delete a non-existing schedule should not throw
        Assertions.assertDoesNotThrow(() -> store.delete("non-existing-schedule"));

        // Existing schedule should still be there
        Optional<ScheduleInfo> found = store.get("existing-schedule");
        Assertions.assertTrue(found.isPresent());
    }

    @Test
    public void testDelete_MultipleSchedules()
  {
        // Test that delete only removes the specified schedule
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo schedule1 = new ScheduleInfo("schedule-1");
        ScheduleInfo schedule2 = new ScheduleInfo("schedule-2");
        ScheduleInfo schedule3 = new ScheduleInfo("schedule-3");

        store.createOrUpdate(schedule1);
        store.createOrUpdate(schedule2);
        store.createOrUpdate(schedule3);

        // Delete one schedule
        store.delete("schedule-2");

        // Verify only schedule-2 is deleted
        Assertions.assertTrue(store.get("schedule-1").isPresent());
        Assertions.assertFalse(store.get("schedule-2").isPresent());
        Assertions.assertTrue(store.get("schedule-3").isPresent());

        List<ScheduleInfo> all = store.getAll();
        Assertions.assertEquals(2, all.size());
    }

    @Test
    public void testBuildIndexes()
  {
        // Test that buildIndexes returns correct index definitions
        List<IndexModel> indexes = SchedulesMongo.buildIndexes();

        Assertions.assertNotNull(indexes);
        Assertions.assertEquals(1, indexes.size());

        // Verify the index is on the "name" field
        IndexModel index = indexes.get(0);
        Assertions.assertNotNull(index);
        Assertions.assertNotNull(index.getKeys());
        Assertions.assertEquals("name", index.getOptions().getName());
    }

    @Test
    public void testCreateOrUpdate_NewSchedule()
  {
        // Test creating a new schedule
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("new-schedule");
        scheduleInfo.setFrequency(7200L);
        scheduleInfo.setDisabled(true);

        ScheduleInfo created = store.createOrUpdate(scheduleInfo);

        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("new-schedule", created.getName());
        Assertions.assertEquals(7200L, created.getFrequency());
        Assertions.assertTrue(created.isDisabled());
    }

    @Test
    public void testCreateOrUpdate_UpdateExistingSchedule()
  {
        // Test updating an existing schedule
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        // Create initial schedule
        ScheduleInfo scheduleInfo = new ScheduleInfo("update-schedule");
        scheduleInfo.setFrequency(3600L);
        scheduleInfo.setDisabled(false);

        ScheduleInfo created = store.createOrUpdate(scheduleInfo);
        String originalId = created.getId();

        // Update the schedule
        ScheduleInfo updateInfo = new ScheduleInfo("update-schedule");
        updateInfo.setFrequency(7200L);
        updateInfo.setDisabled(true);
        updateInfo.setSingleInstance(true);

        ScheduleInfo updated = store.createOrUpdate(updateInfo);

        // Verify update
        Assertions.assertEquals("update-schedule", updated.getName());
        Assertions.assertEquals(7200L, updated.getFrequency());
        Assertions.assertTrue(updated.isDisabled());
        Assertions.assertTrue(updated.getSingleInstance());

        // Verify only one schedule exists
        List<ScheduleInfo> all = store.getAll();
        Assertions.assertEquals(1, all.size());
    }

    @Test
    public void testScheduleWithAllFields()
  {
        // Test schedule with all fields populated
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("full-schedule");
        scheduleInfo.setFrequency(1800L);
        scheduleInfo.setDisabled(true);
        scheduleInfo.setSingleInstance(false);
        scheduleInfo.setExternalTrigger(true);

        ScheduleInfo created = store.createOrUpdate(scheduleInfo);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("full-schedule", created.getName());
        Assertions.assertEquals(1800L, created.getFrequency());
        Assertions.assertTrue(created.isDisabled());
        Assertions.assertFalse(created.getSingleInstance());
        Assertions.assertTrue(created.getExternalTrigger());

        // Retrieve and verify
        Optional<ScheduleInfo> found = store.get("full-schedule");
        Assertions.assertTrue(found.isPresent());
        ScheduleInfo retrieved = found.get();
        Assertions.assertEquals("full-schedule", retrieved.getName());
        Assertions.assertEquals(1800L, retrieved.getFrequency());
        Assertions.assertTrue(retrieved.isDisabled());
        Assertions.assertFalse(retrieved.getSingleInstance());
        Assertions.assertTrue(retrieved.getExternalTrigger());
    }

    @Test
    public void testScheduleWithMinimalFields()
  {
        // Test schedule with only required name field
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        ScheduleInfo scheduleInfo = new ScheduleInfo("minimal-schedule");

        ScheduleInfo created = store.createOrUpdate(scheduleInfo);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("minimal-schedule", created.getName());
        Assertions.assertFalse(created.isDisabled()); // default value
    }

    @Test
    public void testMultipleCreateOrUpdateOperations()
  {
        // Test multiple create/update operations in sequence
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        // Create schedule
        ScheduleInfo schedule1 = new ScheduleInfo("multi-op-schedule");
        schedule1.setFrequency(1000L);
        store.createOrUpdate(schedule1);

        // Update schedule
        ScheduleInfo schedule2 = new ScheduleInfo("multi-op-schedule");
        schedule2.setFrequency(2000L);
        store.createOrUpdate(schedule2);

        // Update again
        ScheduleInfo schedule3 = new ScheduleInfo("multi-op-schedule");
        schedule3.setFrequency(3000L);
        store.createOrUpdate(schedule3);

        // Verify final state
        Optional<ScheduleInfo> found = store.get("multi-op-schedule");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(3000L, found.get().getFrequency());

        // Verify only one schedule exists
        List<ScheduleInfo> all = store.getAll();
        Assertions.assertEquals(1, all.size());
    }

    @Test
    public void testGetAllReturnsDistinctSchedules()
  {
        // Test that getAll returns each schedule once even after updates
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        // Create and update schedules
        ScheduleInfo schedule1 = new ScheduleInfo("schedule-1");
        store.createOrUpdate(schedule1);
        store.createOrUpdate(schedule1); // update

        ScheduleInfo schedule2 = new ScheduleInfo("schedule-2");
        store.createOrUpdate(schedule2);

        List<ScheduleInfo> all = store.getAll();
        Assertions.assertEquals(2, all.size());
    }

    @Test
    public void testDeleteAndRecreate()
  {
        // Test deleting and recreating a schedule with the same name
        SchedulesMongo store = new SchedulesMongo(this.mongoProvider);

        // Create schedule
        ScheduleInfo schedule1 = new ScheduleInfo("recreate-schedule");
        schedule1.setFrequency(1000L);
        ScheduleInfo created = store.createOrUpdate(schedule1);
        String firstId = created.getId();

        // Delete schedule
        store.delete("recreate-schedule");
        Assertions.assertFalse(store.get("recreate-schedule").isPresent());

        // Recreate schedule
        ScheduleInfo schedule2 = new ScheduleInfo("recreate-schedule");
        schedule2.setFrequency(2000L);
        ScheduleInfo recreated = store.createOrUpdate(schedule2);

        // Verify it's a new schedule
        Assertions.assertEquals("recreate-schedule", recreated.getName());
        Assertions.assertEquals(2000L, recreated.getFrequency());
        Assertions.assertNotNull(recreated.getId());
    }
}

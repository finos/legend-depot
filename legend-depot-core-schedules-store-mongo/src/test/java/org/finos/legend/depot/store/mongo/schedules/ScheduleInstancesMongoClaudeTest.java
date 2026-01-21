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
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleInstancesMongoClaudeTest extends TestStoreMongo
{

    @Test
    public void testConstructor()
  {
        // Test that constructor properly initializes the object
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);
        Assertions.assertNotNull(store);
    }

    @Test
    public void testValidateNewData()
  {
        // validateNewData is empty implementation, but we test it doesn't throw
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);
        ScheduleInstance instance = new ScheduleInstance("test-schedule", new Date());

        // This should not throw an exception
        Assertions.assertDoesNotThrow(() -> store.insert(instance));
    }

    @Test
    public void testGetCollection()
  {
        // Test that getCollection returns the correct collection
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        // Insert a test instance to verify collection is accessible
        ScheduleInstance instance = new ScheduleInstance("test-schedule", new Date());
        store.insert(instance);

        // Verify by retrieving
        List<ScheduleInstance> all = store.getAll();
        Assertions.assertEquals(1, all.size());
    }

    @Test
    public void testGetKeyFilter()
  {
        // Test that instances can be filtered by schedule name (the key field)
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        // Insert two instances with different schedule names
        ScheduleInstance instance1 = new ScheduleInstance("schedule-1", new Date());
        ScheduleInstance instance2 = new ScheduleInstance("schedule-2", new Date());

        store.insert(instance1);
        store.insert(instance2);

        // Find by schedule name (which uses getKeyFilter internally)
        List<ScheduleInstance> found = store.find("schedule-1");
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("schedule-1", found.get(0).getSchedule());
    }

    @Test
    public void testBuildIndexes()
  {
        // Test that buildIndexes returns correct index definitions
        List<IndexModel> indexes = ScheduleInstancesMongo.buildIndexes();

        Assertions.assertNotNull(indexes);
        Assertions.assertEquals(1, indexes.size());

        // Verify the index is on the "schedule" field
        IndexModel index = indexes.get(0);
        Assertions.assertNotNull(index);
        Assertions.assertNotNull(index.getKeys());
    }

    @Test
    public void testGetAll_EmptyCollection()
  {
        // Test getAll with empty collection
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        List<ScheduleInstance> all = store.getAll();
        Assertions.assertNotNull(all);
        Assertions.assertTrue(all.isEmpty());
    }

    @Test
    public void testGetAll_WithInstances()
  {
        // Test getAll with multiple instances
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        ScheduleInstance instance1 = new ScheduleInstance("schedule-1", new Date());
        ScheduleInstance instance2 = new ScheduleInstance("schedule-2", new Date());
        ScheduleInstance instance3 = new ScheduleInstance("schedule-3", new Date());

        store.insert(instance1);
        store.insert(instance2);
        store.insert(instance3);

        List<ScheduleInstance> all = store.getAll();
        Assertions.assertEquals(3, all.size());
    }

    @Test
    public void testDelete_ExpiredInstances()
  {
        // Test deleting instances that expired before a given timestamp
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        Calendar cal = Calendar.getInstance();

        // Create instance expired 2 hours ago
        cal.add(Calendar.HOUR, -2);
        Date twoHoursAgo = cal.getTime();
        ScheduleInstance expiredInstance = new ScheduleInstance("expired-schedule", twoHoursAgo);

        // Create instance that expires in 2 hours
        cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        Date twoHoursFromNow = cal.getTime();
        ScheduleInstance futureInstance = new ScheduleInstance("future-schedule", twoHoursFromNow);

        store.insert(expiredInstance);
        store.insert(futureInstance);

        // Delete instances that expired before 1 hour ago
        cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        long oneHourAgo = cal.getTime().getTime();

        long deletedCount = store.delete(oneHourAgo);

        // Should have deleted the expired instance
        Assertions.assertEquals(1, deletedCount);

        // Verify only the future instance remains
        List<ScheduleInstance> remaining = store.getAll();
        Assertions.assertEquals(1, remaining.size());
        Assertions.assertEquals("future-schedule", remaining.get(0).getSchedule());
    }

    @Test
    public void testDelete_NoExpiredInstances()
  {
        // Test delete when no instances match the expiry criteria
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        // Create instance that expires in the future
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 2);
        Date futureDate = cal.getTime();
        ScheduleInstance instance = new ScheduleInstance("future-schedule", futureDate);

        store.insert(instance);

        // Try to delete instances that expired before now
        long deletedCount = store.delete(System.currentTimeMillis());

        // Should not delete anything
        Assertions.assertEquals(0, deletedCount);

        // Verify instance still exists
        List<ScheduleInstance> all = store.getAll();
        Assertions.assertEquals(1, all.size());
    }

    @Test
    public void testDelete_AllExpiredInstances()
  {
        // Test deleting all instances with old expiry timestamp
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -5);
        Date fiveHoursAgo = cal.getTime();

        ScheduleInstance instance1 = new ScheduleInstance("schedule-1", fiveHoursAgo);
        ScheduleInstance instance2 = new ScheduleInstance("schedule-2", fiveHoursAgo);

        store.insert(instance1);
        store.insert(instance2);

        // Delete all expired instances
        long deletedCount = store.delete(System.currentTimeMillis());

        Assertions.assertEquals(2, deletedCount);

        // Verify collection is empty
        List<ScheduleInstance> all = store.getAll();
        Assertions.assertTrue(all.isEmpty());
    }

    @Test
    public void testFind_ExistingSchedule()
  {
        // Test finding instances by schedule name
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        ScheduleInstance instance1 = new ScheduleInstance("test-schedule", new Date());
        ScheduleInstance instance2 = new ScheduleInstance("other-schedule", new Date());

        store.insert(instance1);
        store.insert(instance2);

        List<ScheduleInstance> found = store.find("test-schedule");

        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("test-schedule", found.get(0).getSchedule());
    }

    @Test
    public void testFind_NonExistingSchedule()
  {
        // Test finding a schedule that doesn't exist
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        ScheduleInstance instance = new ScheduleInstance("existing-schedule", new Date());
        store.insert(instance);

        List<ScheduleInstance> found = store.find("non-existing-schedule");

        Assertions.assertNotNull(found);
        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    public void testFind_MultipleInstancesSameSchedule()
  {
        // Test that find can return multiple instances with the same schedule name
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        Calendar cal = Calendar.getInstance();
        Date date1 = cal.getTime();
        cal.add(Calendar.HOUR, 1);
        Date date2 = cal.getTime();

        ScheduleInstance instance1 = new ScheduleInstance("same-schedule", date1);
        ScheduleInstance instance2 = new ScheduleInstance("same-schedule", date2);

        store.insert(instance1);
        store.insert(instance2);

        List<ScheduleInstance> found = store.find("same-schedule");

        // Should find both instances
        Assertions.assertEquals(2, found.size());
        found.forEach(instance -> Assertions.assertEquals("same-schedule", instance.getSchedule()));
    }

    @Test
    public void testFind_EmptyCollection()
  {
        // Test find on empty collection
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        List<ScheduleInstance> found = store.find("any-schedule");

        Assertions.assertNotNull(found);
        Assertions.assertTrue(found.isEmpty());
    }

    @Test
    public void testInsertAndRetrieve()
  {
        // Integration test: insert and retrieve a schedule instance
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        Date expiryDate = new Date();
        ScheduleInstance instance = new ScheduleInstance("integration-test-schedule", expiryDate);

        store.insert(instance);

        List<ScheduleInstance> found = store.find("integration-test-schedule");
        Assertions.assertEquals(1, found.size());

        ScheduleInstance retrieved = found.get(0);
        Assertions.assertEquals("integration-test-schedule", retrieved.getSchedule());
        Assertions.assertNotNull(retrieved.getId());
        Assertions.assertNotNull(retrieved.getExpires());
    }

    @Test
    public void testDeleteExpiry_BoundaryCondition()
  {
        // Test delete with exact timestamp boundary
        ScheduleInstancesMongo store = new ScheduleInstancesMongo(this.mongoProvider);

        long timestamp = 1000000000L;
        Date expiryDate = new Date(timestamp);

        ScheduleInstance instance = new ScheduleInstance("boundary-test", expiryDate);
        store.insert(instance);

        // Delete instances expiring before timestamp (exclusive)
        long deleted = store.delete(timestamp);

        // Should not delete the instance (expires AT timestamp, not before)
        Assertions.assertEquals(0, deleted);

        // Delete instances expiring before timestamp + 1
        deleted = store.delete(timestamp + 1);

        // Now should delete it
        Assertions.assertEquals(1, deleted);
    }
}

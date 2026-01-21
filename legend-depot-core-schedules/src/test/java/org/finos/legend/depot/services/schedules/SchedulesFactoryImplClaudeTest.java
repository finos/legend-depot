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

package org.finos.legend.depot.services.schedules;

import org.finos.legend.depot.store.model.admin.schedules.ScheduleInfo;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.finos.legend.depot.domain.DatesHandler.toDate;

/**
 * Comprehensive test class for SchedulesFactoryImpl.
 * Tests all methods with good branch and condition coverage.
 */
public class SchedulesFactoryImplClaudeTest


{
    private SchedulesFactoryImpl schedulesFactory;
    private MockScheduleStore mockScheduleStore;
    private MockInstancesStore mockInstancesStore;

    @BeforeEach
    public void setUp()
  {
        mockScheduleStore = new MockScheduleStore();
        mockInstancesStore = new MockInstancesStore();
        schedulesFactory = new SchedulesFactoryImpl(mockScheduleStore, mockInstancesStore, false);
    }

    @AfterEach
    public void tearDown()
  {
        if (schedulesFactory != null)
        {
            schedulesFactory.deRegisterAll();
        }
    }

    // Test constructor with housekeeper disabled
    @Test
    public void testConstructorWithoutHouseKeeper()
  {
        SchedulesFactoryImpl factory = new SchedulesFactoryImpl(mockScheduleStore, mockInstancesStore, false);
        Assertions.assertNotNull(factory);
        Assertions.assertNotNull(factory.schedulesStore);
        Assertions.assertNotNull(factory.instancesStore);
        Assertions.assertNotNull(factory.timer);
        Assertions.assertNotNull(factory.tasksRegistry);
        Assertions.assertNotNull(factory.functions);
        Assertions.assertTrue(factory.tasksRegistry.isEmpty());
    }

    // Test constructor with housekeeper enabled
    @Test
    public void testConstructorWithHouseKeeper() throws InterruptedException
    {
        // Add an expired instance
        ScheduleInstance expired = new ScheduleInstance("expired", toDate(LocalDateTime.now().minusDays(1)));
        mockInstancesStore.insert(expired);
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());

        // Create factory with housekeeper enabled
        SchedulesFactoryImpl factory = new SchedulesFactoryImpl(mockScheduleStore, mockInstancesStore, true);

        // Wait for housekeeper to run (it runs at MINUTE interval, but we can't wait that long)
        // Instead, we'll manually trigger deleteExpired to verify the mechanism works
        factory.deleteExpired();

        Assertions.assertEquals(0, mockInstancesStore.getAll().size());
        factory.deRegisterAll();
    }

    // Test registerExternalTriggerSchedule
    @Test
    public void testRegisterExternalTriggerSchedule()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.registerExternalTriggerSchedule("externalJob", 5000L, counter::incrementAndGet);

        // Verify schedule was created with correct properties
        ScheduleInfo info = mockScheduleStore.get("externalJob").orElse(null);
        Assertions.assertNotNull(info);
        Assertions.assertEquals("externalJob", info.getName());
        Assertions.assertEquals(5000L, info.getFrequency());
        Assertions.assertTrue(info.getExternalTrigger());
        Assertions.assertNull(info.getSingleInstance());

        // Verify function was registered
        Assertions.assertTrue(schedulesFactory.functions.containsKey("externalJob"));

        // Verify timer task was NOT created for external trigger
        Assertions.assertFalse(schedulesFactory.tasksRegistry.containsKey("externalJob"));
    }

    // Test registerSingleInstance
    @Test
    public void testRegisterSingleInstance()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.registerSingleInstance("singleJob", 10000L, 20000L, counter::incrementAndGet);

        // Verify schedule was created with correct properties
        ScheduleInfo info = mockScheduleStore.get("singleJob").orElse(null);
        Assertions.assertNotNull(info);
        Assertions.assertEquals("singleJob", info.getName());
        Assertions.assertEquals(20000L, info.getFrequency());
        Assertions.assertTrue(info.getSingleInstance());

        // Verify function and timer task were registered
        Assertions.assertTrue(schedulesFactory.functions.containsKey("singleJob"));
        Assertions.assertTrue(schedulesFactory.tasksRegistry.containsKey("singleJob"));
    }

    // Test register (multi-instance)
    @Test
    public void testRegisterMultiInstance()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("multiJob", 5000L, 10000L, counter::incrementAndGet);

        // Verify schedule was created with correct properties
        ScheduleInfo info = mockScheduleStore.get("multiJob").orElse(null);
        Assertions.assertNotNull(info);
        Assertions.assertEquals("multiJob", info.getName());
        Assertions.assertEquals(10000L, info.getFrequency());
        Assertions.assertFalse(info.getSingleInstance());

        // Verify function and timer task were registered
        Assertions.assertTrue(schedulesFactory.functions.containsKey("multiJob"));
        Assertions.assertTrue(schedulesFactory.tasksRegistry.containsKey("multiJob"));
    }

    // Test canExecute with no existing instances
    @Test
    public void testCanExecuteWithNoInstances()
  {
        schedulesFactory.registerSingleInstance("testJob", 10000L, 20000L, () -> "test");
        boolean canExecute = schedulesFactory.canExecute("testJob");
        Assertions.assertTrue(canExecute);
    }

    // Test canExecute with non-expired instances
    @Test
    public void testCanExecuteWithNonExpiredInstances()
  {
        schedulesFactory.registerSingleInstance("testJob", 10000L, 20000L, () -> "test");

        // Insert a non-expired instance
        ScheduleInstance instance = new ScheduleInstance("testJob", toDate(LocalDateTime.now().plusMinutes(10)));
        mockInstancesStore.insert(instance);

        boolean canExecute = schedulesFactory.canExecute("testJob");
        Assertions.assertFalse(canExecute);
    }

    // Test canExecute with expired instances
    @Test
    public void testCanExecuteWithExpiredInstances()
  {
        schedulesFactory.registerSingleInstance("testJob", 10000L, 20000L, () -> "test");

        // Insert an expired instance
        ScheduleInstance instance = new ScheduleInstance("testJob", toDate(LocalDateTime.now().minusMinutes(10)));
        mockInstancesStore.insert(instance);

        boolean canExecute = schedulesFactory.canExecute("testJob");
        Assertions.assertTrue(canExecute);
    }

    // Test canExecute with mixed instances (one expired, one not)
    @Test
    public void testCanExecuteWithMixedInstances()
  {
        schedulesFactory.registerSingleInstance("testJob", 10000L, 20000L, () -> "test");

        // Insert an expired instance
        ScheduleInstance expired = new ScheduleInstance("testJob", toDate(LocalDateTime.now().minusMinutes(10)));
        mockInstancesStore.insert(expired);

        // Insert a non-expired instance
        ScheduleInstance active = new ScheduleInstance("testJob", toDate(LocalDateTime.now().plusMinutes(10)));
        mockInstancesStore.insert(active);

        boolean canExecute = schedulesFactory.canExecute("testJob");
        Assertions.assertFalse(canExecute);
    }

    // Test deleteExpired with no instances
    @Test
    public void testDeleteExpiredWithNoInstances()
  {
        long deleted = schedulesFactory.deleteExpired();
        Assertions.assertEquals(0, deleted);
    }

    // Test deleteExpired with only expired instances
    @Test
    public void testDeleteExpiredWithOnlyExpiredInstances()
  {
        ScheduleInstance expired1 = new ScheduleInstance("job1", toDate(LocalDateTime.now().minusDays(1)));
        ScheduleInstance expired2 = new ScheduleInstance("job2", toDate(LocalDateTime.now().minusHours(1)));
        mockInstancesStore.insert(expired1);
        mockInstancesStore.insert(expired2);

        long deleted = schedulesFactory.deleteExpired();
        Assertions.assertEquals(2, deleted);
        Assertions.assertEquals(0, mockInstancesStore.getAll().size());
    }

    // Test deleteExpired with only non-expired instances
    @Test
    public void testDeleteExpiredWithOnlyNonExpiredInstances()
  {
        ScheduleInstance future1 = new ScheduleInstance("job1", toDate(LocalDateTime.now().plusDays(1)));
        ScheduleInstance future2 = new ScheduleInstance("job2", toDate(LocalDateTime.now().plusHours(1)));
        mockInstancesStore.insert(future1);
        mockInstancesStore.insert(future2);

        long deleted = schedulesFactory.deleteExpired();
        Assertions.assertEquals(0, deleted);
        Assertions.assertEquals(2, mockInstancesStore.getAll().size());
    }

    // Test deleteExpired with mixed instances
    @Test
    public void testDeleteExpiredWithMixedInstances()
  {
        ScheduleInstance expired = new ScheduleInstance("job1", toDate(LocalDateTime.now().minusDays(1)));
        ScheduleInstance future = new ScheduleInstance("job2", toDate(LocalDateTime.now().plusDays(1)));
        mockInstancesStore.insert(expired);
        mockInstancesStore.insert(future);

        long deleted = schedulesFactory.deleteExpired();
        Assertions.assertEquals(1, deleted);
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
        Assertions.assertEquals("job2", mockInstancesStore.getAll().get(0).getSchedule());
    }

    // Test deRegister with existing schedule
    @Test
    public void testDeRegisterExistingSchedule()
  {
        schedulesFactory.register("testJob", 5000L, 10000L, () -> "test");

        Assertions.assertTrue(mockScheduleStore.get("testJob").isPresent());
        Assertions.assertTrue(schedulesFactory.tasksRegistry.containsKey("testJob"));

        schedulesFactory.deRegister("testJob");

        Assertions.assertFalse(mockScheduleStore.get("testJob").isPresent());
        Assertions.assertFalse(schedulesFactory.tasksRegistry.containsKey("testJob"));
    }

    // Test deRegister with non-existing schedule
    @Test
    public void testDeRegisterNonExistingSchedule()
  {
        // Should not throw exception
        schedulesFactory.deRegister("nonExistent");
        Assertions.assertFalse(mockScheduleStore.get("nonExistent").isPresent());
    }

    // Test deRegisterAll with multiple schedules
    @Test
    public void testDeRegisterAllWithMultipleSchedules()
  {
        schedulesFactory.register("job1", 5000L, 10000L, () -> "test1");
        schedulesFactory.register("job2", 5000L, 10000L, () -> "test2");
        schedulesFactory.register("job3", 5000L, 10000L, () -> "test3");

        Assertions.assertEquals(3, mockScheduleStore.getAll().size());
        Assertions.assertEquals(3, schedulesFactory.tasksRegistry.size());

        schedulesFactory.deRegisterAll();

        Assertions.assertEquals(0, mockScheduleStore.getAll().size());
        Assertions.assertEquals(0, schedulesFactory.tasksRegistry.size());
    }

    // Test deRegisterAll with no schedules
    @Test
    public void testDeRegisterAllWithNoSchedules()
  {
        schedulesFactory.deRegisterAll();
        Assertions.assertEquals(0, mockScheduleStore.getAll().size());
        Assertions.assertEquals(0, schedulesFactory.tasksRegistry.size());
    }

    // Test trigger with existing schedule and forceRun=true
    @Test
    public void testTriggerWithExistingScheduleAndForceRun()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);

        schedulesFactory.trigger("testJob", true);

        // Verify function was executed
        Assertions.assertEquals(1, counter.get());
        // Verify instance was created
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
    }

    // Test trigger with existing schedule and forceRun=false
    @Test
    public void testTriggerWithExistingScheduleAndNoForceRun()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);

        schedulesFactory.trigger("testJob", false);

        // Verify function was executed (schedule is not disabled by default)
        Assertions.assertEquals(1, counter.get());
        // Verify instance was created
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
    }

    // Test trigger with disabled schedule and forceRun=true
    @Test
    public void testTriggerWithDisabledScheduleAndForceRun()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);
        schedulesFactory.toggleDisable("testJob", true);

        schedulesFactory.trigger("testJob", true);

        // Verify function was executed even though schedule is disabled
        Assertions.assertEquals(1, counter.get());
        // Verify instance was created
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
    }

    // Test trigger with disabled schedule and forceRun=false
    @Test
    public void testTriggerWithDisabledScheduleAndNoForceRun()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);
        schedulesFactory.toggleDisable("testJob", true);

        schedulesFactory.trigger("testJob", false);

        // Verify function was NOT executed (schedule is disabled)
        Assertions.assertEquals(0, counter.get());
        // Verify no instance was created
        Assertions.assertEquals(0, mockInstancesStore.getAll().size());
    }

    // Test trigger with non-existing schedule
    @Test
    public void testTriggerWithNonExistingSchedule()
  {
        // Should not throw exception
        schedulesFactory.trigger("nonExistent", false);
    }

    // Test run with existing schedule
    @Test
    public void testRunWithExistingSchedule()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);

        schedulesFactory.run("testJob");

        // Verify function was executed
        Assertions.assertEquals(1, counter.get());
    }

    // Test run with disabled schedule
    @Test
    public void testRunWithDisabledSchedule()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);
        schedulesFactory.toggleDisable("testJob", true);

        schedulesFactory.run("testJob");

        // Verify function was NOT executed (schedule is disabled)
        Assertions.assertEquals(0, counter.get());
    }

    // Test run with single instance schedule when cannot execute
    @Test
    public void testRunWithSingleInstanceScheduleWhenCannotExecute()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.registerSingleInstance("testJob", 10000L, 20000L, counter::incrementAndGet);

        // Insert a non-expired instance
        ScheduleInstance instance = new ScheduleInstance("testJob", toDate(LocalDateTime.now().plusMinutes(10)));
        mockInstancesStore.insert(instance);

        schedulesFactory.run("testJob");

        // Verify function was NOT executed (instance is still active)
        Assertions.assertEquals(0, counter.get());
    }

    // Test run with single instance schedule when can execute
    @Test
    public void testRunWithSingleInstanceScheduleWhenCanExecute()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.registerSingleInstance("testJob", 10000L, 20000L, counter::incrementAndGet);

        // Insert an expired instance
        ScheduleInstance instance = new ScheduleInstance("testJob", toDate(LocalDateTime.now().minusMinutes(10)));
        mockInstancesStore.insert(instance);

        schedulesFactory.run("testJob");

        // Verify function was executed (instance has expired)
        Assertions.assertEquals(1, counter.get());
    }

    // Test run when schedule not in store
    @Test
    public void testRunWhenScheduleNotInStore()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("testJob", 10000L, 20000L, counter::incrementAndGet);

        // Remove from store but keep in registry
        mockScheduleStore.delete("testJob");

        schedulesFactory.run("testJob");

        // Schedule should be deregistered
        Assertions.assertFalse(schedulesFactory.tasksRegistry.containsKey("testJob"));
    }

    // Test toggleDisable to enable
    @Test
    public void testToggleDisableToEnable()
  {
        schedulesFactory.register("testJob", 10000L, 20000L, () -> "test");

        ScheduleInfo info = mockScheduleStore.get("testJob").orElse(null);
        Assertions.assertNotNull(info);
        Assertions.assertFalse(info.isDisabled());

        schedulesFactory.toggleDisable("testJob", false);

        info = mockScheduleStore.get("testJob").orElse(null);
        Assertions.assertNotNull(info);
        Assertions.assertFalse(info.isDisabled());
    }

    // Test toggleDisable to disable
    @Test
    public void testToggleDisableToDisable()
  {
        schedulesFactory.register("testJob", 10000L, 20000L, () -> "test");

        schedulesFactory.toggleDisable("testJob", true);

        ScheduleInfo info = mockScheduleStore.get("testJob").orElse(null);
        Assertions.assertNotNull(info);
        Assertions.assertTrue(info.isDisabled());
    }

    // Test toggleDisable with non-existing schedule
    @Test
    public void testToggleDisableWithNonExistingSchedule()
  {
        // Should not throw exception
        schedulesFactory.toggleDisable("nonExistent", true);
    }

    // Test toggleDisableAll to disable all
    @Test
    public void testToggleDisableAllToDisable()
  {
        schedulesFactory.register("job1", 5000L, 10000L, () -> "test1");
        schedulesFactory.register("job2", 5000L, 10000L, () -> "test2");
        schedulesFactory.register("job3", 5000L, 10000L, () -> "test3");

        schedulesFactory.toggleDisableAll(true);

        Assertions.assertTrue(mockScheduleStore.getAll().stream().allMatch(ScheduleInfo::isDisabled));
    }

    // Test toggleDisableAll to enable all
    @Test
    public void testToggleDisableAllToEnable()
  {
        schedulesFactory.register("job1", 5000L, 10000L, () -> "test1");
        schedulesFactory.register("job2", 5000L, 10000L, () -> "test2");

        // First disable them
        schedulesFactory.toggleDisableAll(true);
        Assertions.assertTrue(mockScheduleStore.getAll().stream().allMatch(ScheduleInfo::isDisabled));

        // Then enable them
        schedulesFactory.toggleDisableAll(false);
        Assertions.assertTrue(mockScheduleStore.getAll().stream().noneMatch(ScheduleInfo::isDisabled));
    }

    // Test toggleDisableAll with no schedules
    @Test
    public void testToggleDisableAllWithNoSchedules()
  {
        schedulesFactory.toggleDisableAll(true);
        Assertions.assertEquals(0, mockScheduleStore.getAll().size());
    }

    // Test run with function that throws exception
    @Test
    public void testRunWithFunctionThatThrowsException()
  {
        schedulesFactory.register("errorJob", 10000L, 20000L, () -> 
        {
            throw new RuntimeException("Test exception");
        });

        // Should not propagate exception
        schedulesFactory.run("errorJob");

        // Instance should still be created
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
    }

    // Test trigger with external trigger schedule
    @Test
    public void testTriggerWithExternalTriggerSchedule()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.registerExternalTriggerSchedule("externalJob", 5000L, counter::incrementAndGet);

        schedulesFactory.trigger("externalJob", true);

        // Verify function was executed
        Assertions.assertEquals(1, counter.get());
        // Verify instance was created
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
    }

    // Test run multiple times with multi-instance schedule
    @Test
    public void testRunMultipleTimesWithMultiInstanceSchedule()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.register("multiJob", 10000L, 20000L, counter::incrementAndGet);

        schedulesFactory.run("multiJob");
        schedulesFactory.run("multiJob");
        schedulesFactory.run("multiJob");

        // Verify function was executed multiple times
        Assertions.assertEquals(3, counter.get());
        // Verify multiple instances were created
        Assertions.assertEquals(3, mockInstancesStore.getAll().size());
    }

    // Test run multiple times with single-instance schedule
    @Test
    public void testRunMultipleTimesWithSingleInstanceSchedule()
  {
        AtomicInteger counter = new AtomicInteger(0);
        schedulesFactory.registerSingleInstance("singleJob", 10000L, 20000L, counter::incrementAndGet);

        schedulesFactory.run("singleJob");
        schedulesFactory.run("singleJob");
        schedulesFactory.run("singleJob");

        // Verify function was executed only once (subsequent runs blocked)
        Assertions.assertEquals(1, counter.get());
        // Verify only one instance was created
        Assertions.assertEquals(1, mockInstancesStore.getAll().size());
    }
}

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

package org.finos.legend.depot.store.resources.schedules;

import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.store.api.admin.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.api.admin.schedules.SchedulesStore;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInfo;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Comprehensive test class for ManageSchedulesResource.
 * Tests all methods with good branch and condition coverage.
 */
public class ManageSchedulesResourceClaudeTest


{
    private ManageSchedulesResource resource;
    private TestAuthorisationProvider authProvider;
    private TestPrincipalProvider principalProvider;
    private TestSchedulesFactory schedulesFactory;
    private TestSchedulesStore schedulesStore;
    private TestScheduleInstancesStore instancesStore;

    @BeforeEach
    public void setUp()
  {
        authProvider = new TestAuthorisationProvider();
        principalProvider = new TestPrincipalProvider();
        schedulesFactory = new TestSchedulesFactory();
        schedulesStore = new TestSchedulesStore();
        instancesStore = new TestScheduleInstancesStore();

        resource = new ManageSchedulesResource(
            authProvider,
            principalProvider,
            schedulesFactory,
            schedulesStore,
            instancesStore
        );
    }

    // Test constructor
    @Test
    public void testConstructor()
  {
        ManageSchedulesResource testResource = new ManageSchedulesResource(
            authProvider,
            principalProvider,
            schedulesFactory,
            schedulesStore,
            instancesStore
        );
        Assertions.assertNotNull(testResource);
    }

    // Test getResourceName
    @Test
    public void testGetResourceName()
  {
        String resourceName = resource.getResourceName();
        Assertions.assertEquals("Schedules", resourceName);
        Assertions.assertEquals(ManageSchedulesResource.SCHEDULES_RESOURCE, resourceName);
    }

    // Test getSchedulerStatus with default disabled=false
    @Test
    public void testGetSchedulerStatusWithDefaultDisabledFalse()
  {
        ScheduleInfo enabled1 = new ScheduleInfo("enabled1");
        enabled1.setDisabled(false);
        ScheduleInfo enabled2 = new ScheduleInfo("enabled2");
        enabled2.setDisabled(false);
        ScheduleInfo disabled1 = new ScheduleInfo("disabled1");
        disabled1.setDisabled(true);

        schedulesStore.schedules.put("enabled1", enabled1);
        schedulesStore.schedules.put("enabled2", enabled2);
        schedulesStore.schedules.put("disabled1", disabled1);

        List<ScheduleInfo> result = resource.getSchedulerStatus(false);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().allMatch(s -> !s.isDisabled()));
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getSchedulerStatus with disabled=true
    @Test
    public void testGetSchedulerStatusWithDisabledTrue()
  {
        ScheduleInfo enabled1 = new ScheduleInfo("enabled1");
        enabled1.setDisabled(false);
        ScheduleInfo disabled1 = new ScheduleInfo("disabled1");
        disabled1.setDisabled(true);
        ScheduleInfo disabled2 = new ScheduleInfo("disabled2");
        disabled2.setDisabled(true);

        schedulesStore.schedules.put("enabled1", enabled1);
        schedulesStore.schedules.put("disabled1", disabled1);
        schedulesStore.schedules.put("disabled2", disabled2);

        List<ScheduleInfo> result = resource.getSchedulerStatus(true);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().allMatch(ScheduleInfo::isDisabled));
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getSchedulerStatus with null disabled parameter
    @Test
    public void testGetSchedulerStatusWithNullDisabled()
  {
        ScheduleInfo enabled = new ScheduleInfo("enabled");
        enabled.setDisabled(false);
        ScheduleInfo disabled = new ScheduleInfo("disabled");
        disabled.setDisabled(true);

        schedulesStore.schedules.put("enabled", enabled);
        schedulesStore.schedules.put("disabled", disabled);

        List<ScheduleInfo> result = resource.getSchedulerStatus(null);

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getSchedulerStatus with empty store
    @Test
    public void testGetSchedulerStatusWithEmptyStore()
  {
        List<ScheduleInfo> result = resource.getSchedulerStatus(false);

        Assertions.assertEquals(0, result.size());
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getSchedulerStatus with authorization failure
    @Test
    public void testGetSchedulerStatusWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.getSchedulerStatus(false);
        });
    }

    // Test getSchedulerInstances
    @Test
    public void testGetSchedulerInstances()
  {
        ScheduleInstance instance1 = new ScheduleInstance("job1", new Date());
        ScheduleInstance instance2 = new ScheduleInstance("job2", new Date());
        instancesStore.instances.add(instance1);
        instancesStore.instances.add(instance2);

        List<ScheduleInstance> result = resource.getSchedulerInstances();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getSchedulerInstances with empty store
    @Test
    public void testGetSchedulerInstancesWithEmptyStore()
  {
        List<ScheduleInstance> result = resource.getSchedulerInstances();

        Assertions.assertEquals(0, result.size());
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test getSchedulerInstances with authorization failure
    @Test
    public void testGetSchedulerInstancesWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.getSchedulerInstances();
        });
    }

    // Test forceScheduler with forceRun=false
    @Test
    public void testForceSchedulerWithForceRunFalse()
  {
        Response response = resource.forceScheduler("testSchedule", false);

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.triggerCalled);
        Assertions.assertEquals("testSchedule", schedulesFactory.lastScheduleName);
        Assertions.assertFalse(schedulesFactory.lastForceRun);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test forceScheduler with forceRun=true
    @Test
    public void testForceSchedulerWithForceRunTrue()
  {
        Response response = resource.forceScheduler("testSchedule", true);

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.triggerCalled);
        Assertions.assertEquals("testSchedule", schedulesFactory.lastScheduleName);
        Assertions.assertTrue(schedulesFactory.lastForceRun);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test forceScheduler with authorization failure
    @Test
    public void testForceSchedulerWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.forceScheduler("testSchedule", false);
        });
        Assertions.assertFalse(schedulesFactory.triggerCalled);
    }

    // Test deleteScheduler
    @Test
    public void testDeleteScheduler()
  {
        Response response = resource.deleteScheduler("testSchedule");

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.deRegisterCalled);
        Assertions.assertEquals("testSchedule", schedulesFactory.lastScheduleName);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test deleteScheduler with authorization failure
    @Test
    public void testDeleteSchedulerWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.deleteScheduler("testSchedule");
        });
        Assertions.assertFalse(schedulesFactory.deRegisterCalled);
    }

    // Test deleteSchedules
    @Test
    public void testDeleteSchedules()
  {
        Response response = resource.deleteSchedules();

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.deRegisterAllCalled);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test deleteSchedules with authorization failure
    @Test
    public void testDeleteSchedulesWithAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.deleteSchedules();
        });
        Assertions.assertFalse(schedulesFactory.deRegisterAllCalled);
    }

    // Test toggleScheduler with scheduleName and toggle=true
    @Test
    public void testToggleSchedulerWithScheduleNameToggleTrue()
  {
        Response response = resource.toggleScheduler("testSchedule", true);

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.toggleDisableCalled);
        Assertions.assertEquals("testSchedule", schedulesFactory.lastScheduleName);
        Assertions.assertTrue(schedulesFactory.lastToggle);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test toggleScheduler with scheduleName and toggle=false
    @Test
    public void testToggleSchedulerWithScheduleNameToggleFalse()
  {
        Response response = resource.toggleScheduler("testSchedule", false);

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.toggleDisableCalled);
        Assertions.assertEquals("testSchedule", schedulesFactory.lastScheduleName);
        Assertions.assertFalse(schedulesFactory.lastToggle);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test toggleScheduler with scheduleName and authorization failure
    @Test
    public void testToggleSchedulerWithScheduleNameAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.toggleScheduler("testSchedule", true);
        });
        Assertions.assertFalse(schedulesFactory.toggleDisableCalled);
    }

    // Test toggleScheduler all schedules with toggle=true
    @Test
    public void testToggleSchedulerAllToggleTrue()
  {
        Response response = resource.toggleScheduler(true);

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.toggleDisableAllCalled);
        Assertions.assertTrue(schedulesFactory.lastToggleAll);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test toggleScheduler all schedules with toggle=false
    @Test
    public void testToggleSchedulerAllToggleFalse()
  {
        Response response = resource.toggleScheduler(false);

        Assertions.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        Assertions.assertTrue(schedulesFactory.toggleDisableAllCalled);
        Assertions.assertFalse(schedulesFactory.lastToggleAll);
        Assertions.assertTrue(authProvider.authoriseCalled);
    }

    // Test toggleScheduler all schedules with authorization failure
    @Test
    public void testToggleSchedulerAllAuthorizationFailure()
  {
        authProvider.shouldThrowException = true;

        Assertions.assertThrows(RuntimeException.class, () -> 
        {
            resource.toggleScheduler(true);
        });
        Assertions.assertFalse(schedulesFactory.toggleDisableAllCalled);
    }

    // Test that resource constants are properly defined
    @Test
    public void testResourceConstant()
  {
        Assertions.assertEquals("Schedules", ManageSchedulesResource.SCHEDULES_RESOURCE);
    }

    // Helper classes for testing

    private static class TestAuthorisationProvider implements AuthorisationProvider
    {
        boolean authoriseCalled = false;
        boolean shouldThrowException = false;
        String lastRole = null;

        @Override
        public void authorise(Provider<Principal> principalProvider, String role)
  {
            authoriseCalled = true;
            lastRole = role;
            if (shouldThrowException)
            {
                throw new RuntimeException("Authorization failed");
            }
        }
    }

    private static class TestPrincipalProvider implements Provider<Principal>
    {
        @Override
        public Principal get()
  {
            return () -> "testUser";
        }
    }

    private static class TestSchedulesFactory implements SchedulesFactory
    {
        boolean triggerCalled = false;
        boolean deRegisterCalled = false;
        boolean deRegisterAllCalled = false;
        boolean toggleDisableCalled = false;
        boolean toggleDisableAllCalled = false;
        String lastScheduleName = null;
        boolean lastForceRun = false;
        boolean lastToggle = false;
        boolean lastToggleAll = false;

        @Override
        public void register(String name, long delayStartInMilliseconds, long intervalInMilliseconds, Supplier<Object> task)
  {
            // Not used in the resource
        }

        @Override
        public void registerExternalTriggerSchedule(String name, long intervalInMilliseconds, Supplier<Object> function)
  {
            // Not used in the resource
        }

        @Override
        public void registerSingleInstance(String name, long delayStartInMilliseconds, long intervalInMilliseconds, Supplier<Object> function)
  {
            // Not used in the resource
        }

        @Override
        public void deRegister(String name)
  {
            deRegisterCalled = true;
            lastScheduleName = name;
        }

        @Override
        public void deRegisterAll()
  {
            deRegisterAllCalled = true;
        }

        @Override
        public void trigger(String scheduleName, boolean forceRun)
  {
            triggerCalled = true;
            lastScheduleName = scheduleName;
            lastForceRun = forceRun;
        }

        @Override
        public void run(String scheduleName)
  {
            // Not used in the resource
        }

        @Override
        public void toggleDisable(String scheduleName, boolean toggle)
  {
            toggleDisableCalled = true;
            lastScheduleName = scheduleName;
            lastToggle = toggle;
        }

        @Override
        public void toggleDisableAll(boolean toggle)
  {
            toggleDisableAllCalled = true;
            lastToggleAll = toggle;
        }
    }

    private static class TestSchedulesStore implements SchedulesStore
    {
        Map<String, ScheduleInfo> schedules = new HashMap<>();

        @Override
        public List<ScheduleInfo> getAll()
        {
            return new ArrayList<>(schedules.values());
        }

        @Override
        public Optional<ScheduleInfo> get(String name)
        {
            return Optional.ofNullable(schedules.get(name));
        }

        @Override
        public ScheduleInfo createOrUpdate(ScheduleInfo scheduleInfo)
  {
            schedules.put(scheduleInfo.getName(), scheduleInfo);
            return scheduleInfo;
        }

        @Override
        public void delete(String name)
  {
            schedules.remove(name);
        }
    }

    private static class TestScheduleInstancesStore implements ScheduleInstancesStore
    {
        List<ScheduleInstance> instances = new ArrayList<>();

        @Override
        public List<ScheduleInstance> getAll()
        {
            return instances;
        }

        @Override
        public List<ScheduleInstance> find(String scheduleName)
        {
            return instances.stream()
                .filter(i -> i.getSchedule().equals(scheduleName))
                .collect(Collectors.toList());
        }

        @Override
        public void insert(ScheduleInstance instance)
  {
            instances.add(instance);
        }

        @Override
        public long delete(long l)
  {
            List<ScheduleInstance> toDelete = instances.stream()
                .filter(ScheduleInstance::isExpired)
                .collect(Collectors.toList());
            instances.removeAll(toDelete);
            return toDelete.size();
        }
    }
}

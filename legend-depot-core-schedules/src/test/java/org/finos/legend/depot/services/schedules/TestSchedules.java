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

import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.finos.legend.depot.domain.DatesHandler.toDate;

public class TestSchedules
{
    private SchedulesFactoryImpl schedulesFactory;

    @BeforeEach
    public void setUp()
    {
        schedulesFactory = new SchedulesFactoryImpl(new MockScheduleStore(), new MockInstancesStore(),false);
        Assertions.assertTrue(schedulesFactory.tasksRegistry.isEmpty());
    }

    @AfterEach
    public void tearDown()
    {
        schedulesFactory.deRegisterAll();
    }


    @Test
    public void testDisabledAllToggle()
    {
        schedulesFactory.register("job3", 600000, 100000, () -> "hello toggles");
        schedulesFactory.register("job4", 600000, 100000, () -> "hello toggles again");
        schedulesFactory.toggleDisableAll(false);
        Assertions.assertTrue(schedulesFactory.schedulesStore.getAll().stream().allMatch(j -> !j.disabled));
        schedulesFactory.toggleDisableAll(true);
        Assertions.assertTrue(schedulesFactory.schedulesStore.getAll().stream().allMatch(j -> j.disabled));

        schedulesFactory.run("job3");
        Assertions.assertTrue(schedulesFactory.instancesStore.getAll().isEmpty());
    }

    @Test
    public void testDeregister()
    {
        schedulesFactory.register("job33", 600000, 100000, () -> "hello toggles");
        schedulesFactory.register("job34", 600000, 100000, () -> "hello toggles again");
        Assertions.assertEquals(2,schedulesFactory.schedulesStore.getAll().size());
        schedulesFactory.deRegister("job33");
        Assertions.assertEquals(1,schedulesFactory.schedulesStore.getAll().size());
        Assertions.assertFalse(schedulesFactory.tasksRegistry.contains("job33"));

    }

    @Test
    public void deleteExpired()
    {
        ScheduleInstance instance = new ScheduleInstance("job1", toDate(LocalDateTime.now().plusSeconds(10)));
        schedulesFactory.instancesStore.insert(instance);

        ScheduleInstance expired = new ScheduleInstance("expired", toDate(LocalDateTime.now().minusDays(10)));
        schedulesFactory.instancesStore.insert(expired);

        schedulesFactory.deleteExpired();

        Assertions.assertTrue(schedulesFactory.instancesStore.getAll().stream().noneMatch(i -> i.getSchedule().equals("expired")));
    }

    @Test
    public void canExecute()
    {
         schedulesFactory.register("multiInstance",10000000L,100000000L, () -> "happy run");
         schedulesFactory.registerSingleInstance("singleInstance",10000000L,100000000L, () -> "single run");
         Assertions.assertEquals(2, schedulesFactory.schedulesStore.getAll().size());

         schedulesFactory.run("multiInstance");
         schedulesFactory.run("multiInstance");
         schedulesFactory.run("multiInstance");

         Assertions.assertEquals(3,schedulesFactory.instancesStore.getAll().size());

         schedulesFactory.run("singleInstance");
         schedulesFactory.run("singleInstance");

        Assertions.assertEquals(4,schedulesFactory.instancesStore.getAll().size());

        Assertions.assertEquals(1,schedulesFactory.instancesStore.find("singleInstance").size());

        schedulesFactory.instancesStore.find("singleInstance").get(0).setExpires(toDate(LocalDateTime.now().minusMinutes(10)));
        schedulesFactory.run("singleInstance");

        Assertions.assertEquals(5,schedulesFactory.instancesStore.getAll().size());
    }

}

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


package org.finos.legend.depot.store.admin.services;

import org.finos.legend.depot.store.admin.api.ManageSchedulesService;
import org.finos.legend.depot.store.admin.services.schedules.ScheduleInfo;
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.admin.store.mongo.MongoSchedules;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;


public class TestSchedules extends TestStoreMongo
{
    private ManageSchedulesService schedulesService;
    private SchedulesFactory schedulesFactory;

    @Before
    public void setUp()
    {
        schedulesService = new MongoSchedules(mongoProvider);
        schedulesFactory = new SchedulesFactory(schedulesService);
        this.mongoProvider.drop();
        Assert.assertTrue(schedulesService.getAll().isEmpty());
    }

    @Test
    public void testSchedules()
    {

        Assert.assertTrue(schedulesService.getAll().isEmpty());
        schedulesFactory.register("joba", LocalDateTime.now().plusHours(1), 100000, false, () -> "hello");
        List<ScheduleInfo> scheduleInfoList = schedulesFactory.printStats();
        Assert.assertNotNull(scheduleInfoList);
        Assert.assertEquals(1, scheduleInfoList.size());
        Assert.assertEquals("joba", scheduleInfoList.get(0).getJobId());

        schedulesFactory.run("joba");
        Assert.assertEquals(1, schedulesFactory.printStats().size());
        Assert.assertEquals("hello", schedulesFactory.printStats().get(0).getMessage());
        Assert.assertEquals(100000, schedulesFactory.printStats().get(0).getFrequency());

        schedulesFactory.register("joba", LocalDateTime.now().plusHours(1), 100000, false, () -> "hello");
        Assert.assertEquals(1, schedulesFactory.printStats().size());
    }

    @Test
    public void testToggles()
    {
        schedulesFactory.register("joba", LocalDateTime.now(), 100000, false, () -> "hello");
        schedulesFactory.register("jobb", LocalDateTime.now(), 100000, false, () -> "hello again");
        Assert.assertEquals(2, schedulesFactory.printStats().size());
        schedulesFactory.toggleDisable("joba", true);
        Assert.assertTrue(schedulesService.get("joba").get().disabled.get());
        schedulesFactory.toggleDisable("joba", false);
        Assert.assertFalse(schedulesService.get("joba").get().disabled.get());

        schedulesFactory.toggleDisableAll(true);
        schedulesFactory.printStats().stream().forEach(s -> Assert.assertTrue(s.isDisabled()));
    }

    @Test
    public void testRunningToggles()
    {
        schedulesFactory.register("joba", LocalDateTime.now(), 100000, false, () -> "hello");
        schedulesFactory.toggleRunning("joba", true);
        Assert.assertTrue(schedulesService.get("joba").get().running.get());
        schedulesFactory.toggleRunning("joba", false);
        Assert.assertFalse(schedulesService.get("joba").get().running.get());

    }
}

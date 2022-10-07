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


package org.finos.legend.depot.store.admin.services.schedules;

import org.finos.legend.depot.store.admin.api.ManageSchedulesService;
import org.finos.legend.depot.store.admin.store.mongo.MongoSchedules;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.After;
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
        Assert.assertTrue(schedulesService.getAll().isEmpty());
        Assert.assertTrue(schedulesFactory.schedulesBuffer.isEmpty());
    }

    @After
    public void tearDown()
    {
        this.mongoProvider.drop();
        schedulesFactory.timer.cancel();
        schedulesFactory.timer.purge();
    }

    @Test
    public void testSchedules()
    {
        Assert.assertTrue(schedulesService.getAll().isEmpty());
        Assert.assertTrue(schedulesFactory.find().isEmpty());
        schedulesFactory.register("joba", LocalDateTime.now().plusHours(24), 100000, false, () -> "hello");
        List<ScheduleInfo> scheduleInfoList = schedulesFactory.find();
        Assert.assertNotNull(scheduleInfoList);
        Assert.assertTrue(scheduleInfoList.stream().anyMatch(s -> s.jobId.equals("joba")));

        schedulesFactory.run("joba");
        Assert.assertEquals(1, schedulesFactory.find().size());
        Assert.assertEquals("hello", schedulesFactory.find().get(0).getMessage());
        Assert.assertEquals(100000, schedulesFactory.find().get(0).getFrequency());

        schedulesFactory.register("joba", LocalDateTime.now().plusHours(12), 100000000, false, () -> "hello again");
        Assert.assertEquals(1, schedulesFactory.find().size());
    }

    @Test
    public void testRunningToggles()
    {
        schedulesFactory.register("job1", LocalDateTime.now().plusHours(24), 10000000, false, () -> "hello toggles");
        schedulesFactory.toggleRunning("job1", true);
        Assert.assertTrue(schedulesService.get("job1").get().running.get());
        schedulesFactory.toggleRunning("job1", false);
        Assert.assertFalse(schedulesService.get("job1").get().running.get());

    }

    @Test
    public void testDisabledAllToggle()
    {
        schedulesFactory.register("job1", LocalDateTime.now(), 100000, false, () -> "hello toggles");
        schedulesFactory.register("job2", LocalDateTime.now(), 100000, false, () -> "hello toggles again");
        Assert.assertEquals(2, schedulesService.find(null,Boolean.FALSE).size());
        schedulesFactory.toggleDisableAll(true);
        Assert.assertEquals(2, schedulesService.find(null,Boolean.TRUE).size());
    }
}

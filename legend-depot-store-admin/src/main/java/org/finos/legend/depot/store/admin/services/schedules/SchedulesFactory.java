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

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.depot.store.admin.api.ManageSchedulesService;
import org.slf4j.Logger;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

@Singleton
public final class SchedulesFactory
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SchedulesFactory.class);

    private Map<String, Pair<TimerTask, ScheduleInfo>> schedulesBuffer = Maps.mutable.empty();

    private final ManageSchedulesService manageSchedulesService;

    public SchedulesFactory(ManageSchedulesService manageSchedulesService)
    {
        this.manageSchedulesService = manageSchedulesService;
    }

    public List<ScheduleInfo> printStats()
    {
        return manageSchedulesService.getAll();
    }

    public void register(String name, LocalDateTime start, int interval, boolean parallelRun, Supplier function)
    {
        TimerTask task = createTask(name, function);
        schedulesBuffer.put(name, Tuples.pair(task, new ScheduleInfo(name, interval, parallelRun)));
        new Timer().scheduleAtFixedRate(task, java.sql.Date.from(start.atZone(ZoneId.systemDefault()).toInstant()), interval);
        Optional<ScheduleInfo> existingInfo = manageSchedulesService.get(name);

        ScheduleInfo info = existingInfo.isPresent() ? existingInfo.get() : new ScheduleInfo(name);
        info.alloyMultipleRuns = parallelRun;
        info.frequency = interval;
        manageSchedulesService.createOrUpdate(info);
    }

    public void run(String jobId)
    {
        schedulesBuffer.get(jobId).getOne().run();
    }

    public void toggle(String jobId, boolean toggle)
    {
        manageSchedulesService.toggle(jobId, toggle);
    }

    public void toggleAll(boolean toggle)
    {
        manageSchedulesService.toggleAll(toggle);
    }

    private TimerTask createTask(String id, Supplier<Object> f)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                if (schedulesBuffer.get(id) == null)
                {
                    LOGGER.info("cancelling timer for execution {}", id);
                    this.cancel();
                    return;
                }
                handleExecution(id, f);
            }
        };
    }

    private Object handleExecution(String jobId, Supplier<Object> fucntionToExecute)
    {
        ScheduleInfo scheduleInfo = get(jobId);
        if (scheduleInfo.disabled)
        {
            LOGGER.info("Job {} is disabled, skipping", jobId);
            return null;
        }
        if (!scheduleInfo.alloyMultipleRuns && scheduleInfo.running)
        {
            LOGGER.info("Other instance is running, skipping {}  ", jobId);
            return null;

        }
        long t = System.currentTimeMillis();
        try
        {
            this.manageSchedulesService.createOrUpdate(scheduleInfo.withRunning(true));
            scheduleInfo.message = fucntionToExecute.get();
        }
        catch (Exception e)
        {
            scheduleInfo.message = "ERROR: " + e.getMessage();
            LOGGER.error("Error executing {} {}", jobId, e);
        }
        finally
        {
            scheduleInfo.lastExecuted = new Date();
            scheduleInfo.lastExecutionDuration = System.currentTimeMillis() - t;
            this.manageSchedulesService.createOrUpdate(scheduleInfo.withRunning(false));
            LOGGER.info("Finished {} ", jobId);
        }
        return null;
    }

    private ScheduleInfo get(String jobId)
    {
        Optional<ScheduleInfo> scheduleInfo = this.manageSchedulesService.get(jobId);
        return scheduleInfo.isPresent() ? scheduleInfo.get() : new ScheduleInfo(jobId, schedulesBuffer.get(jobId).getTwo().frequency, false);
    }
}

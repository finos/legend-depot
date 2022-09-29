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
    public static final long ONE_HOUR   = 60 * 60 * 1000L;
    private final Map<String, Pair<TimerTask, ScheduleInfo>> schedulesBuffer = Maps.mutable.empty();

    private final ManageSchedulesService manageSchedulesService;

    public SchedulesFactory(ManageSchedulesService manageSchedulesService)
    {
        this.manageSchedulesService = manageSchedulesService;
    }

    public List<ScheduleInfo> find()
    {
        return find(null,null);
    }

    public List<ScheduleInfo> find(Boolean running, Boolean disabled)
    {
        return manageSchedulesService.find(running,disabled);
    }

    public void register(String name, LocalDateTime start, long intervalInMiliseconds, boolean parallelRun, Supplier<Object> function)
    {
        TimerTask task = createTask(name, function);
        schedulesBuffer.put(name, Tuples.pair(task, new ScheduleInfo(name, intervalInMiliseconds, parallelRun)));
        new Timer().scheduleAtFixedRate(task, java.sql.Date.from(start.atZone(ZoneId.systemDefault()).toInstant()), intervalInMiliseconds);
        Optional<ScheduleInfo> existingInfo = manageSchedulesService.get(name);

        ScheduleInfo info = existingInfo.orElseGet(() -> new ScheduleInfo(name));
        info.allowMultipleRuns = parallelRun;
        info.frequency = intervalInMiliseconds;
        manageSchedulesService.createOrUpdate(info);
    }

    public void deRegister(String name)
    {
        this.schedulesBuffer.remove(name);
        this.manageSchedulesService.delete(name);
    }

    public void run(String jobId)
    {
        schedulesBuffer.get(jobId).getOne().run();
    }

    public void toggleDisable(String jobId, boolean toggle)
    {
        manageSchedulesService.toggleDisable(jobId, toggle);
    }

    public void toggleRunning(String jobId, boolean toggle)
    {
        manageSchedulesService.toggleRunning(jobId, toggle);
    }

    public void toggleDisableAll(boolean toggle)
    {
        manageSchedulesService.toggleDisableAll(toggle);
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

    private void handleExecution(String jobId, Supplier<Object> functionToExecute)
    {
        ScheduleInfo scheduleInfo = get(jobId);
        if (scheduleInfo.disabled.get())
        {
            LOGGER.info("Job {} is disabled, skipping", jobId);
            return;
        }
        if (!scheduleInfo.allowMultipleRuns && scheduleInfo.running.get())
        {
            LOGGER.info("Other instance is running, skipping {}  ", jobId);
            return;

        }
        long t = System.currentTimeMillis();
        try
        {
            this.manageSchedulesService.createOrUpdate(scheduleInfo.withRunning(true));
            scheduleInfo.message = functionToExecute.get();
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
    }

    private ScheduleInfo get(String jobId)
    {
        Optional<ScheduleInfo> scheduleInfo = this.manageSchedulesService.get(jobId);
        return scheduleInfo.orElseGet(() -> schedulesBuffer.get(jobId).getTwo());
    }
}

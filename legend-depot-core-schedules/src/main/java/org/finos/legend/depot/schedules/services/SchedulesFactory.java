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

package org.finos.legend.depot.schedules.services;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.SynchronizedMutableMap;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.depot.store.admin.api.schedules.SchedulesStore;
import org.finos.legend.depot.store.admin.domain.schedules.ScheduleInfo;
import org.slf4j.Logger;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public final class SchedulesFactory
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SchedulesFactory.class);
    final SynchronizedMutableMap<String, Pair<TimerTask, ScheduleInfo>> schedulesBuffer;
    final Timer timer = new Timer();
    private final SchedulesStore schedulesStore;

    public SchedulesFactory(SchedulesStore manageSchedulesService)
    {
        this.schedulesStore = manageSchedulesService;
        this.schedulesBuffer = new SynchronizedMutableMap(Maps.mutable.empty());
    }

    public List<ScheduleInfo> find()
    {
        return find(null,null);
    }

    public List<ScheduleInfo> find(Boolean running, Boolean disabled)
    {
        return schedulesStore.find(running,disabled);
    }

    public void register(String name, LocalDateTime start, long intervalInMilliseconds, boolean parallelRun, Supplier<Object> function)
    {
        TimerTask task = createTask(name, function);
        schedulesBuffer.put(name, Tuples.pair(task, new ScheduleInfo(name, intervalInMilliseconds, parallelRun)));
        timer.scheduleAtFixedRate(task, java.sql.Date.from(start.atZone(ZoneId.systemDefault()).toInstant()), intervalInMilliseconds);
        Optional<ScheduleInfo> existingInfo = schedulesStore.get(name);

        ScheduleInfo info = existingInfo.orElseGet(() -> new ScheduleInfo(name));
        info.allowMultipleRuns = parallelRun;
        info.frequency = intervalInMilliseconds;
        schedulesStore.createOrUpdate(info);
    }

    public void deRegister(String name)
    {
        this.schedulesBuffer.remove(name);
        this.schedulesStore.delete(name);
    }

    public void run(String jobId)
    {
        schedulesBuffer.get(jobId).getOne().run();
    }


    private ScheduleInfo getScheduleInfo(String jobId)
    {
        Optional<ScheduleInfo> info = schedulesStore.get(jobId);
        ScheduleInfo scheduleInfo = new ScheduleInfo(jobId);
        if (info.isPresent())
        {
            scheduleInfo = info.get();
        }
        return scheduleInfo;
    }


    public void toggleDisable(String jobId, boolean toggle)
    {
        synchronized (ScheduleInfo.class)
        {
            ScheduleInfo info = getScheduleInfo(jobId);
            info.disabled.compareAndSet(!toggle,toggle);
            schedulesStore.createOrUpdate(info);
        }
    }


    public void toggleRunning(String jobId, boolean toggle)
    {
        synchronized (ScheduleInfo.class)
        {
            ScheduleInfo info = getScheduleInfo(jobId);
            info.running.compareAndSet(!toggle,toggle);
            schedulesStore.createOrUpdate(info);
        }
    }

    public void toggleDisableAll(boolean toggle)
    {
        synchronized (ScheduleInfo.class)
        {
            schedulesStore.getAll().stream().forEach(info ->
            {
                toggleDisable(info.jobId, toggle);
            });
        }
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
            LOGGER.info("Other instance is running, skipping {}", jobId);
            return;

        }
        long t = System.currentTimeMillis();
        try
        {
            LOGGER.info("Starting {} ", jobId);
            this.schedulesStore.createOrUpdate(scheduleInfo.startRunning(t));
            scheduleInfo.message = functionToExecute.get();
        }
        catch (Exception e)
        {
            scheduleInfo.message = "ERROR: " + e.getMessage();
            LOGGER.error("Error executing {} {}", jobId, e);
        }
        finally
        {
            this.schedulesStore.createOrUpdate(scheduleInfo.stopRunning());
            LOGGER.info("Finished {} ", jobId);
        }
    }

    private ScheduleInfo get(String jobId)
    {
        Optional<ScheduleInfo> scheduleInfo = this.schedulesStore.get(jobId);
        return scheduleInfo.orElseGet(() -> schedulesBuffer.get(jobId).getTwo());
    }
}

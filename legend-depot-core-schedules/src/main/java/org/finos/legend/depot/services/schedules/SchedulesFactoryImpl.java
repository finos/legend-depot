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

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.impl.map.mutable.SynchronizedMutableMap;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.store.api.admin.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.api.admin.schedules.SchedulesStore;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInfo;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.slf4j.Logger;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.DatesHandler.toDate;


@Singleton
public final class SchedulesFactoryImpl implements SchedulesFactory
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SchedulesFactoryImpl.class);

    final SynchronizedMutableMap<String, TimerTask> tasksRegistry = new SynchronizedMutableMap(Maps.mutable.empty());
    final SynchronizedMutableMap<String, Supplier<Object>> functions = new SynchronizedMutableMap(Maps.mutable.empty());
    final Timer timer = new Timer();
    final SchedulesStore schedulesStore;
    final ScheduleInstancesStore instancesStore;
    private final TimerTask houseKeeper;

    public SchedulesFactoryImpl(SchedulesStore manageSchedulesService, ScheduleInstancesStore instancesStore, boolean scheduleHouseKeeper)
    {
        this.schedulesStore = manageSchedulesService;
        this.instancesStore = instancesStore;
        if (scheduleHouseKeeper)
        {
            this.houseKeeper = new TimerTask()
            {
                @Override
                public void run()
                {
                    deleteExpired();
                }
            };
            timer.scheduleAtFixedRate(houseKeeper, MINUTE, MINUTE);
        }
        else
        {
            this.houseKeeper = null;
        }
    }


    public void registerExternalTriggerSchedule(String name, long intervalInMilliseconds, boolean isSingleInstance, Supplier<Object> function)
    {
        createScheduleInfo(name, intervalInMilliseconds, isSingleInstance, function);
    }

    public void registerSingleInstance(String name, long delayStartInMilliseconds, long intervalInMilliseconds, Supplier<Object> function)
    {
        register(name, delayStartInMilliseconds, intervalInMilliseconds, true, function);
    }

    public void register(String name, long delayStartInMilliseconds, long intervalInMilliseconds, Supplier<Object> task)
    {
        this.register(name, delayStartInMilliseconds, intervalInMilliseconds, false, task);
    }

    private void register(String name, long delayStartInMilliseconds, long intervalInMilliseconds, boolean singleInstance, Supplier<Object> function)
    {
        createScheduleInfo(name, intervalInMilliseconds, singleInstance, function);

        TimerTask timerTask = createTimerTask(name);
        tasksRegistry.put(name,timerTask);
        timer.scheduleAtFixedRate(timerTask, delayStartInMilliseconds, intervalInMilliseconds);
    }

    private void createScheduleInfo(String name, long intervalInMilliseconds, boolean singleInstance, Supplier<Object> function)
    {
        ScheduleInfo info = schedulesStore.get(name).orElse(new ScheduleInfo(name));
        info.frequency = intervalInMilliseconds;
        info.singleInstance = singleInstance;
        functions.put(name,function);
        schedulesStore.createOrUpdate(info);
    }

    private TimerTask createTimerTask(String name)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                Optional<ScheduleInfo> scheduleInfoInStore = schedulesStore.get(name);
                if (!scheduleInfoInStore.isPresent())
                {
                    LOGGER.info("Schedule {} not in store", name);
                    deRegister(name);
                    return;
                }
                ScheduleInfo schedule = scheduleInfoInStore.get();
                LOGGER.info("Found {} schedule: disabled {}, singleInstance {}", name, schedule.disabled, schedule.getSingleInstance());
                if (schedule.disabled)
                {
                    LOGGER.info("Schedule {} disabled, skipping", name);
                    return;
                }

                if (schedule.singleInstance && !canExecute(name))
                {
                    LOGGER.info("Skipping {} execution", name);
                    return;
                }
                execute(schedule, true);
            }
        };
    }

    boolean canExecute(String name)
    {
        return instancesStore.find(name).stream().allMatch(instance -> instance.isExpired());
    }

    long deleteExpired()
    {
        List<ScheduleInstance> expired = instancesStore.getAll().stream().filter(instance -> instance.isExpired()).collect(Collectors.toList());
        expired.forEach(instance -> this.instancesStore.delete(instance.getId()));
        LOGGER.info("Deleted {} expired schedule runs", expired.size());
        return expired.size();
    }

    public void deRegister(String name)
    {
        TimerTask task = this.tasksRegistry.remove(name);
        if (task != null)
        {
            task.cancel();
        }
        this.schedulesStore.delete(name);
        LOGGER.info("De-registering schedule {}", name);
    }

    public void deRegisterAll()
    {
        this.schedulesStore.getAll().forEach(scheduleInfo ->
        {
            this.deRegister(scheduleInfo.name);
        });
    }

    public void trigger(String scheduleName, boolean forceRun)
    {
        Optional<ScheduleInfo> scheduleInfo = schedulesStore.get(scheduleName);
        scheduleInfo.ifPresent(schedule -> execute(schedule, forceRun));
    }

    public void run(String scheduleName)
    {
        tasksRegistry.get(scheduleName).run();
    }

    public void toggleDisable(String scheduleName, boolean toggle)
    {
        schedulesStore.get(scheduleName).ifPresent(scheduleInfo ->
        {
            scheduleInfo.disabled = toggle;
            schedulesStore.createOrUpdate(scheduleInfo);
        });
    }

    public void toggleDisableAll(boolean toggle)
    {
        schedulesStore.getAll().forEach(info -> toggleDisable(info.name, toggle));
    }

    private void execute(ScheduleInfo schedule, boolean forceRun)
    {
        try
        {
            if (forceRun || !schedule.isDisabled())
            {
                if (functions.containsKey(schedule.name))
                {
                    this.instancesStore.insert(new ScheduleInstance(schedule.name,toDate(LocalDateTime.now().plusSeconds(schedule.frequency / 1000L))));
                    LOGGER.info("Starting schedule {} ", schedule.name);
                    Object result = functions.get(schedule.name).get();
                    LOGGER.info("Schedule {} result {}", schedule.name, result);
                }
                else
                {
                    LOGGER.warn("No function to execute {}", schedule.name);
                }
            }
            else
            {
                LOGGER.warn("Schedule {} is disabled and force run flag is false", schedule.name);
            }

        }
        catch (Exception e)
        {
            LOGGER.error("Error executing schedule {} {}", schedule.name, e.getMessage());
        }
    }
}

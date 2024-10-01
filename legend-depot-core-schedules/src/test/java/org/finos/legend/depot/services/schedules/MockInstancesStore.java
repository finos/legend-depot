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

import org.finos.legend.depot.store.api.admin.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MockInstancesStore implements ScheduleInstancesStore
{
    List<ScheduleInstance> instances = new ArrayList<>();
    int ids = 1;

    @Override
    public void insert(ScheduleInstance instance)
    {
         instance.setId(String.valueOf(ids));
         ids++;
         instances.add(instance);
    }

    @Override
    public long delete(long l)
    {
        List<ScheduleInstance> toDeletedInstances = instances.stream().filter(i -> i.isExpired()).collect(Collectors.toList());
        instances.removeAll(toDeletedInstances);
        return toDeletedInstances.size();
    }

    @Override
    public List<ScheduleInstance> find(String scheduleName)
    {
        return instances.stream().filter(i -> i.getSchedule().equals(scheduleName)).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleInstance> getAll()
    {
        return instances;
    }
}

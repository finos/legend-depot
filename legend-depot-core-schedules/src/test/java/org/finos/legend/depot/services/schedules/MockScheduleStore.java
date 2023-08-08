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

import org.finos.legend.depot.store.admin.api.schedules.SchedulesStore;
import org.finos.legend.depot.store.admin.domain.schedules.ScheduleInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class MockScheduleStore implements SchedulesStore
{
    Map<String, ScheduleInfo> schedules = new HashMap<>();

    @Override
    public Optional<ScheduleInfo> get(String name)
    {
        return schedules.containsKey(name) ? Optional.of(schedules.get(name)) : Optional.empty();
    }

    @Override
    public List<ScheduleInfo> getAll()
    {
        return schedules.values().stream().collect(Collectors.toList());
    }

    @Override
    public ScheduleInfo createOrUpdate(ScheduleInfo scheduleInfo)
    {
        schedules.put(scheduleInfo.name, scheduleInfo);
        return scheduleInfo;
    }

    @Override
    public void delete(String name)
    {
        schedules.remove(name);
    }
}

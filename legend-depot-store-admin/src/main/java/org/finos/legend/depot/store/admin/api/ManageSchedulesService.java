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


package org.finos.legend.depot.store.admin.api;

import org.finos.legend.depot.store.admin.services.schedules.ScheduleInfo;

import java.util.List;
import java.util.Optional;

public interface ManageSchedulesService
{
    Optional<ScheduleInfo> get(String jobId);

    List<ScheduleInfo> getAll();

    List<ScheduleInfo> find(Boolean running, Boolean disabled);

    void toggleDisable(String jobId, boolean toggle);

    void toggleRunning(String jobId, boolean toggle);

    void toggleDisableAll(boolean toggle);

    ScheduleInfo createOrUpdate(ScheduleInfo scheduleInfo);

    void delete(String jobId);
}

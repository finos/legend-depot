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

package org.finos.legend.depot.services.api.schedules;

import java.util.function.Supplier;

public interface SchedulesFactory
{
    long MINUTE = 6000L;
    long HOUR = 3600000L;

    void register(String name, long delayStartInMilliseconds, long intervalInMilliseconds, Supplier<Object> task);

    void registerExternalTriggerSchedule(String name, long intervalInMilliseconds, boolean isSingleInstance, Supplier<Object> function);

    void registerSingleInstance(String name, long delayStartInMilliseconds, long intervalInMilliseconds, Supplier<Object> function);

    void deRegister(String name);

    void deRegisterAll();

    void trigger(String scheduleName, boolean forceRun);

    void run(String scheduleName);

    void toggleDisable(String scheduleName, boolean toggle);

    void toggleDisableAll(boolean toggle);
}

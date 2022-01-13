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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.domain.HasIdentifier;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleInfo implements HasIdentifier
{
    @JsonProperty
    public String id;
    @JsonProperty
    public AtomicBoolean running = new AtomicBoolean();
    @JsonProperty
    public boolean allowMultipleRuns = false;
    @JsonProperty
    public String jobId;
    @JsonProperty
    public long frequency;
    @JsonProperty
    public Date lastExecuted;
    @JsonProperty
    public long lastExecutionDuration;
    @JsonProperty
    public AtomicBoolean disabled = new AtomicBoolean();
    @JsonProperty
    public Object message;

    public ScheduleInfo()
    {
    }

    public ScheduleInfo(String jobId)
    {
        this.jobId = jobId;
    }

    public ScheduleInfo(String id, long frequency, boolean parallelRun)
    {
        this.jobId = id;
        this.frequency = frequency;
        this.allowMultipleRuns = parallelRun;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public ScheduleInfo withRunning(boolean running)
    {
        this.running.getAndSet(running);
        return this;
    }

    public boolean isRunning()
    {
        return running.get();
    }

    public String getJobId()
    {
        return jobId;
    }

    public long getFrequency()
    {
        return frequency;
    }

    public Date getLastExecuted()
    {
        return lastExecuted;
    }

    public long getLastExecutionDuration()
    {
        return lastExecutionDuration;
    }

    public boolean isDisabled()
    {
        return disabled.get();
    }

    public Object getMessage()
    {
        return message;
    }

    public boolean isAllowMultipleRuns()
    {
        return allowMultipleRuns;
    }
}

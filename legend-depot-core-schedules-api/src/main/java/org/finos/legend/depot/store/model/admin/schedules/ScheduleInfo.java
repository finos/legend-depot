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

package org.finos.legend.depot.store.model.admin.schedules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.store.model.HasIdentifier;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleInfo implements HasIdentifier
{
    @JsonProperty
    public String id;
    @JsonProperty
    public String name;
    @JsonProperty
    public boolean disabled = false;
    @JsonProperty
    public Boolean singleInstance;
    @JsonProperty
    public Boolean externalTrigger;
    @JsonProperty
    public Long frequency;

    public ScheduleInfo()
    {
    }

    public ScheduleInfo(String name)
    {
        this.name = name;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled(boolean disabled)
    {
        this.disabled = disabled;
    }

    public Boolean getSingleInstance()
    {
        return singleInstance;
    }

    public void setSingleInstance(Boolean singleInstance)
    {
        this.singleInstance = singleInstance;
    }

    public Boolean getExternalTrigger()
    {
        return externalTrigger;
    }

    public void setExternalTrigger(Boolean externalTrigger)
    {
        this.externalTrigger = externalTrigger;
    }

    public Long getFrequency()
    {
        return frequency;
    }

    public void setFrequency(Long frequency)
    {
        this.frequency = frequency;
    }
}

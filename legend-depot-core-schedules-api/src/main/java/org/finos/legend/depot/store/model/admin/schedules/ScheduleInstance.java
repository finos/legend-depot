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

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleInstance implements HasIdentifier
{

    @JsonProperty
    private String id;
    @JsonProperty
    private String schedule;
    @JsonProperty
    private Date expires;

    public ScheduleInstance()
    {
    }

    public ScheduleInstance(String name, Date expires)
    {
        this.schedule = name;
        this.expires = expires;
    }


    @Override
    public String getId()
    {
        return id;
    }

    public Date getExpires()
    {
        return expires;
    }

    public void setExpires(Date expires)
    {
        this.expires = expires;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSchedule()
    {
        return schedule;
    }

    public void setSchedule(String schedule)
    {
        this.schedule = schedule;
    }

    public boolean isExpired()
    {
        return new Date().after(expires);
    }
}

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

package org.finos.legend.depot.store.admin.domain.artifacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.domain.api.MetadataEventResponse;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshStatus implements HasIdentifier
{
    private String id;
    @JsonProperty
    private String groupId;
    @JsonProperty
    private String artifactId;
    @JsonProperty
    private String versionId;
    @JsonProperty
    private String parentEventId;
    @JsonProperty
    private boolean running;
    @JsonProperty
    @EqualsExclude
    private MetadataEventResponse response;
    @JsonProperty
    private Date lastRun;
    @JsonProperty
    private Date startTime;
    @JsonProperty
    private long duration;

    public RefreshStatus()
    {
    }

    public RefreshStatus(String groupId, String artifactId, String version)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionId = version;
    }

    public String getId()
    {
        return id;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public MetadataEventResponse getResponse()
    {
        return response;
    }

    public void setResponse(MetadataEventResponse response)
    {
        this.response = response;
    }

    public Date getLastRun()
    {
        return lastRun;
    }

    public void setLastRun(Date lastRun)
    {
        this.lastRun = lastRun;
    }

    public String getVersionId()
    {
        return versionId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public String getParentEventId()
    {
        return parentEventId;
    }

    public RefreshStatus setParentEventId(String parentEventId)
    {
        this.parentEventId = parentEventId;
        return this;
    }

    public RefreshStatus withStartTime(Date startTime)
    {
        this.startTime = startTime;
        return this;
    }

    public RefreshStatus startRunning()
    {
        this.running = true;
        this.startTime = new Date();
        this.duration = 0;
        this.response = new MetadataEventResponse();
        return this;
    }

    public RefreshStatus stopRunning(MetadataEventResponse response)
    {
        this.running = false;
        this.lastRun = new Date();
        if (this.startTime != null)
        {
            this.duration = lastRun.getTime() - startTime.getTime();
        }
        this.response = this.response != null ? this.response.combine(response) : response;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

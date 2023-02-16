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
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.notifications.EventPriority;
import org.finos.legend.depot.domain.notifications.MetadataNotification;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshStatus extends MetadataNotification
{
    @JsonProperty
    @EqualsExclude
    private boolean running;
    @JsonProperty
    @EqualsExclude
    private Date startTime;
    @JsonProperty
    @EqualsExclude
    private Date lastRun;
    @JsonProperty
    @EqualsExclude
    private long duration;
    @JsonProperty
    @EqualsExclude
    private String traceId;


    public RefreshStatus()
    {
        super();
    }

    public RefreshStatus(String projectId, String groupId, String artifactId, String version, String eventId, String parentEventId, Boolean fullUpdate, Boolean transitive, Integer attempt, Integer maxAttempts, Map<Integer,MetadataEventResponse> responses, Date createdAt, Date lastUpdated, EventPriority eventPriority)
    {
        super(projectId, groupId, artifactId, version, eventId, parentEventId, fullUpdate, transitive, attempt, maxAttempts, responses, createdAt, lastUpdated, eventPriority);
    }

    public RefreshStatus(String projectId, String groupId, String artifactId, String versionId)
    {
        super(projectId, groupId, artifactId, versionId);
    }

    public RefreshStatus(String groupId, String artifactId, String version)
    {
        super(null,groupId, artifactId, version);
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public Date getLastRun()
    {
        return lastRun;
    }

    public void setLastRun(Date lastRun)
    {
        this.lastRun = lastRun;
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

    public String getTraceId()
    {
        return traceId;
    }

    public void setTraceId(String traceId)
    {
        this.traceId = traceId;
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
        return this;
    }

    public RefreshStatus stopRunning()
    {
        this.running = false;
        this.lastRun = new Date();
        if (this.startTime != null)
        {
            this.duration = lastRun.getTime() - startTime.getTime();
        }
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

    public RefreshStatus withParentEventId(String parentId)
    {
        setParentEventId(parentId);
        return this;
    }

    public static RefreshStatus from(MetadataNotification event)
    {
        return new RefreshStatus(event.getProjectId(),
                event.getGroupId(),
                event.getArtifactId(),
                event.getVersionId(),
                event.getEventId(),
                event.getParentEventId(),
                event.isFullUpdate(),
                event.isTransitive(),
                event.getAttempt(),
                event.getMaxAttempts(),
                event.getResponses(),
                event.getCreated(),
                event.getLastUpdated(),
                event.getEventPriority());
    }
}

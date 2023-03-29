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
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.notifications.EventPriority;
import org.finos.legend.depot.domain.notifications.MetadataNotification;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static org.finos.legend.depot.domain.DatesHandler.toDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshStatus extends MetadataNotification
{
    @JsonProperty
    private Date expires = toDate(LocalDateTime.now().plusMinutes(15));

    public RefreshStatus()
    {
        super();
    }

    public RefreshStatus(String projectId, String groupId, String artifactId, String version, String eventId, String parentEventId, Boolean fullUpdate, Boolean transitive, Integer attempt, Integer maxAttempts, Map<Integer,MetadataEventResponse> responses, Date createdAt, Date lastUpdated, EventPriority eventPriority)
    {
        super(projectId, groupId, artifactId, version, eventId, parentEventId, fullUpdate, transitive, attempt, maxAttempts, responses, createdAt, lastUpdated, null,eventPriority);
    }

    public RefreshStatus(String groupId, String artifactId, String version)
    {
        super(null,groupId, artifactId, version);
    }

    public Date getExpires()
    {
        return expires;
    }

    public void setExpires(Date expires)
    {
        this.expires = expires;
    }

    @JsonProperty
    public boolean isExpired()
    {
        return new Date().after(expires);
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
                event.getUpdated(),
                event.getEventPriority());
    }

}

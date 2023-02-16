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

package org.finos.legend.depot.domain.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataNotification extends VersionedData implements HasIdentifier
{
    private static final int DEFAULT_MAX_ATTEMPTS = 2;
    @EqualsExclude
    @JsonProperty
    private String id;
    @JsonProperty
    private String projectId;
    @JsonProperty
    private String eventId;
    @JsonProperty
    private String parentEventId;
    @JsonProperty
    private boolean fullUpdate;
    @JsonProperty
    private boolean transitive;
    @JsonProperty
    private int attempt;
    @JsonProperty
    private int maxAttempts;
    @JsonProperty
    private Date created;
    @JsonProperty
    private Date lastUpdated;
    @EqualsExclude
    @JsonProperty
    private Map<Integer,MetadataEventResponse> responses;
    @JsonProperty
    private EventPriority eventPriority;


    @JsonCreator
    public MetadataNotification(@JsonProperty(value = "projectId") String projectId,
                                @JsonProperty(value = "groupId") String groupId,
                                @JsonProperty(value = "artifactId") String artifactId,
                                @JsonProperty(value = "version") String version,
                                @JsonProperty(value = "eventId") String eventId,
                                @JsonProperty(value = "parentEvent") String parentEventId,
                                @JsonProperty(value = "fullUpdate") Boolean fullUpdate,
                                @JsonProperty(value = "transitive") Boolean transitive,
                                @JsonProperty(value = "attempt") Integer attempt,
                                @JsonProperty(value = "maxAttempts") Integer maxAttempts,
                                @JsonProperty(value = "responses") Map<Integer,MetadataEventResponse> responses,
                                @JsonProperty(value = "createdAt") Date createdAt,
                                @JsonProperty(value = "lastUpdated") Date lastUpdated,
                                @JsonProperty(value = "eventPriority") EventPriority eventPriority)
    {
        super(groupId,artifactId,version);
        this.projectId = projectId;
        this.eventId = eventId;
        this.parentEventId = parentEventId;
        this.lastUpdated = lastUpdated;
        this.created = createdAt;
        this.maxAttempts = maxAttempts != null ? maxAttempts : DEFAULT_MAX_ATTEMPTS;
        this.responses = responses != null ? responses : new HashMap<>();
        this.attempt = attempt != null ? attempt : 0;
        this.fullUpdate = fullUpdate != null ? fullUpdate : false;
        this.transitive = transitive != null ? transitive : false;
        this.eventPriority = eventPriority;
    }

    public MetadataNotification(String projectId, String groupId, String artifactId, String versionId)
    {
        this(projectId, groupId, artifactId, versionId, null, null, null, null, null, null, null, null, null, EventPriority.LOW);
    }

    public MetadataNotification(String projectId,String groupId, String artifactId, String versionId, Boolean fullUpdate,Boolean transitive, String parentEvent)
    {
        this(projectId, groupId, artifactId, versionId, null, parentEvent, fullUpdate, transitive, null, null, null, null, null, EventPriority.LOW);
    }

    public MetadataNotification(String projectId,String groupId, String artifactId, String versionId, Boolean fullUpdate,Boolean transitive, String parentEvent, EventPriority eventPriority)
    {
        this(projectId, groupId, artifactId, versionId, null, parentEvent, fullUpdate, transitive, null, null, null, null, null, eventPriority);
    }

    public MetadataNotification()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEventId()
    {
        return eventId;
    }

    public MetadataNotification setEventId(String eventID)
    {
        this.eventId = eventID;
        return this;
    }

    public boolean isFullUpdate()
    {
        return fullUpdate;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public MetadataNotification setProjectId(String projectId)
    {
        this.projectId = projectId;
        return this;
    }

    @JsonProperty("status")
    public MetadataEventStatus getStatus()
    {
        return this.responses.getOrDefault(this.attempt,new MetadataEventResponse()).getStatus();
    }

    public String getParentEventId()
    {
        return parentEventId;
    }

    public void setParentEventId(String parentEventId)
    {
        this.parentEventId = parentEventId;
    }

    public MetadataNotification setFullUpdate(boolean fullUpdate)
    {
        this.fullUpdate = fullUpdate;
        return this;
    }

    public void setTransitive(boolean transitive)
    {
        this.transitive = transitive;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public void setMaxAttempts(int maxAttempts)
    {
        this.maxAttempts = maxAttempts;
    }

    public boolean isTransitive()
    {
        return transitive;
    }


    public MetadataNotification increaseAttempts()
    {
        this.attempt++;
        return this;
    }

    public boolean retriesExceeded()
    {
        return attempt >= maxAttempts;
    }


    public Map<Integer, MetadataEventResponse> getResponses()
    {
        if (responses == null)
        {
            responses = new HashMap<>();
        }
        return responses;
    }

    public void setResponses(Map<Integer, MetadataEventResponse> responses)
    {
        this.responses = responses;
    }

    public MetadataNotification addError(String errorMessage)
    {
        getResponse(this.attempt).addError(errorMessage);
        return this;
    }

    public void setResponse(MetadataEventResponse response)
    {
       getResponses().put(this.attempt,response);
    }

    public MetadataNotification combineResponse(MetadataEventResponse response)
    {
        if (response != null)
        {
            getResponse(this.attempt).combine(response);
        }
        return this;
    }

    private MetadataEventResponse getResponse(int attempt)
    {
        getResponses().putIfAbsent(attempt,new MetadataEventResponse());
        return getResponses().get(attempt);
    }

    @JsonIgnore
    public MetadataEventResponse getCurrentResponse()
    {
        return getResponses().get(attempt);
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public MetadataNotification setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public int getAttempt()
    {
        return attempt;
    }

    public MetadataNotification setAttempt(int integer)
    {
        this.attempt = integer;
        return this;
    }

    public int getMaxAttempts()
    {
        return maxAttempts;
    }

    public EventPriority getEventPriority()
    {
        return eventPriority;
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

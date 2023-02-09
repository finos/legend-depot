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

package org.finos.legend.depot.store.notifications.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataNotification implements HasIdentifier
{
    public static final int DEFAULT_MAX_RETRIES = 2;
    @EqualsExclude
    @JsonProperty
    private String id;
    @EqualsExclude
    @JsonProperty
    private String eventId;
    @JsonProperty
    private String parentEventId;
    @NotNull
    @JsonProperty
    private String projectId;
    @NotNull
    @JsonProperty
    private String groupId;
    @NotNull
    @JsonProperty
    private String artifactId;
    @NotNull
    @JsonProperty
    private String versionId;
    @JsonProperty
    private boolean fullUpdate;
    @JsonProperty
    private boolean transitive;
    @JsonProperty
    private int retries;
    @JsonProperty
    private Date createdAt;
    @JsonProperty
    private Date lastUpdated;
    @JsonProperty
    private int maxRetries;
    @EqualsExclude
    @JsonProperty
    private MetadataEventResponse response;


    @JsonCreator
    public MetadataNotification(@JsonProperty(value = "projectId") String projectId,
                                @JsonProperty(value = "groupId") String groupId,
                                @JsonProperty(value = "artifactId") String artifactId,
                                @JsonProperty(value = "version") String version,
                                @JsonProperty(value = "lastUpdated") Date lastUpdated,
                                @JsonProperty(value = "createdAt") Date createdAt,
                                @JsonProperty(value = "eventId") String eventId,
                                @JsonProperty(value = "parentEvent") String parentEventId,
                                @JsonProperty(value = "response") MetadataEventResponse response,
                                @JsonProperty(value = "fullUpdate") Boolean fullUpdate,
                                @JsonProperty(value = "transitive") Boolean transitive,
                                @JsonProperty(value = "retries") Integer retries,
                                @JsonProperty(value = "maxRetries") Integer maxRetries)
    {
        this.projectId = projectId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionId = version;
        this.eventId = eventId;
        this.parentEventId = parentEventId;
        this.lastUpdated = lastUpdated;
        this.createdAt = createdAt;
        this.maxRetries = maxRetries != null ? maxRetries : DEFAULT_MAX_RETRIES;
        this.response = response != null ? response : new MetadataEventResponse();
        this.retries = retries != null ? retries : 0;
        this.fullUpdate = fullUpdate != null ? fullUpdate : false;
        this.transitive = transitive != null ? transitive : false;
    }

    public MetadataNotification(String projectId, String groupId, String artifactId, String versionId)
    {
        this(projectId, groupId, artifactId, versionId, null,null, null, null, null, null,null, null, null);
    }

    public MetadataNotification(String projectId,String groupId, String artifactId, String versionId, Boolean fullUpdate,Boolean transitive, String parentEvent)
    {
        this(projectId, groupId, artifactId, versionId, null,null, null, parentEvent,null,fullUpdate,transitive, null, null);
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

    public String getVersionId()
    {
        return versionId;
    }

    public MetadataNotification setVersionId(String version)
    {
        this.versionId = version;
        return this;
    }

    public boolean isFullUpdate()
    {
        return fullUpdate;
    }

    @JsonIgnore
    public String getGAVCoordinates()
    {
        return String.format("%s:%s:%s ", this.groupId, artifactId, versionId);
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

    public String getGroupId()
    {
        return groupId;
    }

    public MetadataNotification setGroupId(String groupId)
    {
        this.groupId = groupId;
        return this;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public MetadataNotification setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
        return this;
    }

    public MetadataEventStatus getStatus()
    {
        return this.response.getStatus();
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

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public void setMaxRetries(int maxRetries)
    {
        this.maxRetries = maxRetries;
    }

    public boolean isTransitive()
    {
        return transitive;
    }


    public MetadataNotification increaseRetries()
    {
        this.retries++;
        return this;
    }

    public boolean retriesExceeded()
    {
        return retries >= maxRetries;
    }

    @JsonIgnore
    public List<String> getErrors()
    {
        return this.response.getErrors();
    }

    public MetadataEventResponse getResponse()
    {
        return response;
    }

    public MetadataNotification setResponse(MetadataEventResponse response)
    {
        this.response = response;
        return this;
    }


    public MetadataNotification addError(String errorMessage)
    {
        if (this.response != null)
        {
            this.response.addError(errorMessage);
        }
        return this;
    }

    public MetadataNotification combineResponse(MetadataEventResponse response)
    {
        if (response != null)
        {
            this.response.combine(response);
        }
        return this;
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

    public int getRetries()
    {
        return retries;
    }

    public MetadataNotification setRetries(int integer)
    {
        this.retries = integer;
        return this;
    }

    public int getMaxRetries()
    {
        return maxRetries;
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

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
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataNotification implements HasIdentifier
{
    public static final int DEFAULT_MAX_RETRIES = 3;
    @EqualsExclude
    private String id;
    @EqualsExclude
    private String eventId;
    private String parentEventId;
    @NotNull
    private String projectId;
    @NotNull
    private String groupId;
    @NotNull
    private String artifactId;
    private String versionId;
    private boolean fullUpdate;
    private int retries;
    private Date lastUpdated;
    private int maxRetries;
    private MetadataEventStatus status;
    private List<String> errors;


    @JsonCreator
    public MetadataNotification(@JsonProperty(value = "projectId") String projectId,
                                @JsonProperty(value = "groupId") String groupId,
                                @JsonProperty(value = "artifactId") String artifactId,
                                @JsonProperty(value = "version") String version,
                                @JsonProperty(value = "lastUpdated") Date lastUpdated,
                                @JsonProperty(value = "eventId") String eventId,
                                @JsonProperty(value = "parentEvent") String parentEventId,
                                @JsonProperty(value = "status") MetadataEventStatus status,
                                @JsonProperty(value = "errors") List<String> errors,
                                @JsonProperty(value = "fullUpdate") boolean fullUpdate,
                                @JsonProperty(value = "retries") int retries,
                                @JsonProperty(value = "maxRetries") int maxRetries)
    {
        this.projectId = projectId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionId = version;
        this.maxRetries = maxRetries;
        this.eventId = eventId;
        this.parentEventId = parentEventId;
        this.lastUpdated = lastUpdated;
        this.status = status;
        this.errors = errors;
        this.retries = retries;
        this.fullUpdate = fullUpdate;
    }

    public MetadataNotification(String projectId,
                                String groupId,
                                String artifactId)
    {
        this(projectId, groupId, artifactId, null, new Date(), null, null, MetadataEventStatus.NEW, new ArrayList<>(), false, 0, DEFAULT_MAX_RETRIES);
    }


    public MetadataNotification(String projectId,
                                String groupId,
                                String artifactId,
                                String versionId,
                                boolean fullUpdate)
    {
        this(projectId, groupId, artifactId, versionId, new Date(), null, null,MetadataEventStatus.NEW, new ArrayList<>(), fullUpdate, 0, DEFAULT_MAX_RETRIES);
    }

    public MetadataNotification(String projectId, String groupId, String artifactId, String versionId)
    {
        this(projectId, groupId, artifactId, versionId, new Date(), null, null, MetadataEventStatus.NEW, new ArrayList<>(), false, 0, DEFAULT_MAX_RETRIES);
    }

    public MetadataNotification(String projectId, String groupId, String artifactId, String versionId, boolean fullUpdate,int maxRetries)
    {
        this(projectId, groupId, artifactId, versionId, new Date(), null, null,MetadataEventStatus.NEW, new ArrayList<>(), fullUpdate, 0, maxRetries);
    }

    public MetadataNotification(String projectId,String groupId, String artifactId, String versionId, boolean fullUpdate, String parentEvent)
    {
        this(projectId, groupId, artifactId, versionId, new Date(), null, parentEvent,MetadataEventStatus.NEW, new ArrayList<>(), fullUpdate, 0, DEFAULT_MAX_RETRIES);
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
        return status;
    }


    public MetadataNotification setStatus(MetadataEventStatus status)
    {
        this.status = status;
        return this;
    }

    public String getParentEventId()
    {
        return parentEventId;
    }

    public void setParentEventId(String parentEventId)
    {
        this.parentEventId = parentEventId;
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


    public MetadataNotification failEvent()
    {
        this.status = MetadataEventStatus.FAILED;
        return this;
    }

    public MetadataNotification completedSuccessfully()
    {
        this.status = MetadataEventStatus.SUCCESS;
        return this;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public MetadataNotification addErrors(List<String> errors)
    {
        if (this.errors == null)
        {
            this.errors = new ArrayList<>();
        }
        this.errors.addAll(errors);
        return this;
    }

    public MetadataNotification addError(String error)
    {
        if (this.errors == null)
        {
            this.errors = new ArrayList<>();
        }
        errors.add(error);
        return this;
    }

    public MetadataNotification failEvent(String error)
    {

        this.addError(error);
        this.status = MetadataEventStatus.FAILED;
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

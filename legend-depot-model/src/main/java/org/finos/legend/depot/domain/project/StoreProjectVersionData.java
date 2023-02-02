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

package org.finos.legend.depot.domain.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.HasIdentifier;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreProjectVersionData extends VersionedData implements HasIdentifier
{
    @JsonProperty
    private boolean evicted = false;
    @JsonProperty
    private Date creationDate;
    //TODO: understand how to populate last updated
    @JsonProperty
    private Date lastUpdated;
    @JsonProperty
    private ProjectVersionData versionData = new ProjectVersionData();

    public StoreProjectVersionData()
    {
        super();
    }

    public StoreProjectVersionData(String groupId, String artifactId,String versionId)
    {
        super(groupId, artifactId, versionId);
        setCreationDate(new Date());
    }

    public StoreProjectVersionData(String groupId,String artifactId,String versionId,boolean evicted,ProjectVersionData versionData)
    {
        super(groupId, artifactId, versionId);
        this.evicted = evicted;
        this.versionData = versionData;
        setCreationDate(new Date());
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public boolean isEvicted()
    {
        return evicted;
    }

    public void setEvicted(boolean evicted)
    {
        this.evicted = evicted;
    }

    public void setVersionData(ProjectVersionData versionData)
    {
        this.versionData = versionData;
    }

    public ProjectVersionData getVersionData()
    {
        return versionData;
    }

    @Override
    public String getId()
    {
        return "";
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

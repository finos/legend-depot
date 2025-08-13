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

package org.finos.legend.depot.store.model.projects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.store.model.HasIdentifier;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreProjectVersionData extends VersionedData implements HasIdentifier
{
    @JsonProperty
    private boolean evicted = false;
    @JsonProperty
    private Date created;
    @JsonProperty
    private Date updated;
    @JsonProperty
    private ProjectVersionData versionData = new ProjectVersionData();
    @JsonProperty
    private VersionDependencyReport transitiveDependenciesReport = new VersionDependencyReport();

    public StoreProjectVersionData()
    {
        super();
    }

    public StoreProjectVersionData(String groupId, String artifactId,String versionId)
    {
        super(groupId, artifactId, versionId);
        setCreated(new Date());
    }

    public StoreProjectVersionData(String groupId,String artifactId,String versionId,boolean evicted,ProjectVersionData versionData)
    {
        super(groupId, artifactId, versionId);
        this.evicted = evicted;
        this.versionData = versionData;
        setCreated(new Date());
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
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

    public VersionDependencyReport getTransitiveDependenciesReport()
    {
        return transitiveDependenciesReport;
    }

    public void setTransitiveDependenciesReport(VersionDependencyReport transitiveDependenciesReport)
    {
        this.transitiveDependenciesReport = transitiveDependenciesReport;
    }

    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }

    public Date getUpdated()
    {
        return updated;
    }

    @Override
    @JsonIgnore
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

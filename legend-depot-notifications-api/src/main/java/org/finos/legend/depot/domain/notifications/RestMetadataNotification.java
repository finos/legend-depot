//  Copyright 2025 Goldman Sachs
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestMetadataNotification extends VersionedData
{
    @JsonProperty("dependencies")
    List<ProjectVersion> dependencies = new ArrayList<>();

    @JsonProperty("entityDefinitionWithArtifacts")
    List<RestCuratedArtifacts> entityDefinitionWithArtifacts;

    public RestMetadataNotification(String groupId, String artifactId, String versionId)
    {
        super(groupId, artifactId, versionId);
    }

    @JsonCreator
    public RestMetadataNotification(@JsonProperty("groupId") String groupId, @JsonProperty("artifactId") String artifactId, @JsonProperty("versionId") String versionId, @JsonProperty("entityDefinitionWithArtifacts") List<RestCuratedArtifacts> entityDefinitionWithArtifacts, @JsonProperty(value = "dependencies") List<ProjectVersion> dependencies)
    {
        super(groupId, artifactId, versionId);
        this.setEntityDefinitionWithArtifacts(entityDefinitionWithArtifacts);
        this.setDependencies(dependencies != null ? dependencies : new ArrayList<>());
    }

    public List<ProjectVersion> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(List<ProjectVersion> dependencies)
    {
        this.dependencies = dependencies;
    }

    public List<RestCuratedArtifacts> getEntityDefinitionWithArtifacts()
    {
        return entityDefinitionWithArtifacts;
    }

    public void setEntityDefinitionWithArtifacts(List<RestCuratedArtifacts> entityDefinitionWithArtifacts)
    {
        this.entityDefinitionWithArtifacts = entityDefinitionWithArtifacts;
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

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

package org.finos.legend.depot.domain.artifacts.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactDependency
{
    private final String groupId;
    private final String artifactId;
    private final String versionId;
    private List<DependencyExclusion> exclusions;

    @JsonCreator
    public ArtifactDependency(@JsonProperty("groupId") String groupId, @JsonProperty("artifactId") String artifactId, @JsonProperty("version") String versionId, @JsonProperty("exclusions") List<DependencyExclusion> exclusions)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionId = versionId;
        this.exclusions = exclusions != null ? exclusions : Collections.emptyList();
    }


    public ArtifactDependency(String groupId, String artifactId, String versionId)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionId = versionId;
        this.exclusions = Collections.emptyList();
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersionId()
    {
        return versionId;
    }

    public List<DependencyExclusion> getExclusions()
    {
        return exclusions != null ? exclusions : Collections.emptyList();
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



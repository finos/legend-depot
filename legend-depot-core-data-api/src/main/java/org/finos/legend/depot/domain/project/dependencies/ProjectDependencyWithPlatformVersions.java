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

package org.finos.legend.depot.domain.project.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.Property;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDependencyWithPlatformVersions extends VersionedData
{
    @JsonProperty
    private List<Property> platformsVersion;
    @JsonProperty
    private ProjectVersion dependency;

    public ProjectDependencyWithPlatformVersions(String groupId, String artifactId, String versionId, ProjectVersion dependency, List<Property> platformsVersion)
    {
        super(groupId, artifactId, versionId);
        this.platformsVersion = platformsVersion;
        this.dependency = dependency;
    }

    public List<Property> getPlatformsVersion()
    {
        return platformsVersion;
    }

    public ProjectVersion getDependency()
    {
        return dependency;
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

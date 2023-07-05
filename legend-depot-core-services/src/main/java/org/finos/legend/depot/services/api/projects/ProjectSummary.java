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

package org.finos.legend.depot.services.api.projects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.compare;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectSummary implements Comparable
{
    @JsonProperty
    public String projectId;
    @JsonProperty
    public String groupId;
    @JsonProperty
    public String artifactId;
    @JsonProperty
    public long versions;

    public ProjectSummary(String projectId, String groupId, String artifactId, long versions)
    {
        this.projectId = projectId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versions = versions;
    }

    @JsonProperty(value = "mavenCoordinates")
    public String getMavenCoordinates()
    {

        return String.format("%s-%s", groupId, artifactId);
    }

    @Override
    public int compareTo(Object o)
    {
        return compare(((ProjectSummary) o).groupId + artifactId, this.groupId + artifactId);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ProjectSummary that = (ProjectSummary) o;
        return versions == that.versions;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(versions);
    }
}

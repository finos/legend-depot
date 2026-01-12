//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.services.api.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyConflict
{
    @JsonProperty
    private String groupId;

    @JsonProperty
    private String artifactId;

    @JsonProperty
    private List<ConflictingVersion> conflictingVersions;

    @JsonProperty
    private ProjectVersion suggestedOverride;

    public DependencyConflict(String groupId, String artifactId)
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.conflictingVersions = new ArrayList<>();
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public List<ConflictingVersion> getConflictingVersions()
    {
        return conflictingVersions;
    }

    public void setConflictingVersions(List<ConflictingVersion> conflictingVersions)
    {
        this.conflictingVersions = conflictingVersions;
    }

    public void addConflictingVersion(String version, List<ProjectVersion> requiredBy)
    {
        this.conflictingVersions.add(new ConflictingVersion(version, requiredBy));
    }

    public ProjectVersion getSuggestedOverride()
    {
        return suggestedOverride;
    }

    public void setSuggestedOverride(ProjectVersion suggestedOverride)
    {
        this.suggestedOverride = suggestedOverride;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConflictingVersion
    {
        @JsonProperty
        private String version;

        @JsonProperty
        private List<ProjectVersion> requiredBy;

        public ConflictingVersion(String version, List<ProjectVersion> requiredBy)
        {
            this.version = version;
            this.requiredBy = requiredBy != null ? requiredBy : new ArrayList<>();
        }

        public String getVersion()
        {
            return version;
        }

        public void setVersion(String version)
        {
            this.version = version;
        }

        public List<ProjectVersion> getRequiredBy()
        {
            return requiredBy;
        }

        public void setRequiredBy(List<ProjectVersion> requiredBy)
        {
            this.requiredBy = requiredBy;
        }

    }
}


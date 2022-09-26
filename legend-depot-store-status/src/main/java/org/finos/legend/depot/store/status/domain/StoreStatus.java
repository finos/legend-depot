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

package org.finos.legend.depot.store.status.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreStatus
{

    @JsonProperty
    public long totalProjects = 0;

    @JsonProperty
    public List<ProjectSummary> projects = new ArrayList<>();

    public void addProject(ProjectSummary projectId)
    {
        this.projects.add(projectId);
        totalProjects++;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProjectSummary implements Comparable
    {
        @JsonProperty
        public String projectId;
        @JsonProperty
        public String groupId;
        @JsonProperty
        public String artifactId;
        @JsonProperty
        public long versions;
        @JsonProperty
        public String url;

        public ProjectSummary(String projectId, String groupId, String artifactId, long versions, String url)
        {
            this.projectId = projectId;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.versions = versions;
            this.url = url;
        }

        @JsonProperty(value = "mavenCoordinates")
        public String getMavenCoordinates()
        {

            return String.format("%s-%s", groupId, artifactId);
        }

        @Override
        public int compareTo(Object o)
        {
            return compare(((ProjectSummary)o).groupId + artifactId, this.groupId + artifactId);
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
            ProjectSummary that = (ProjectSummary)o;
            return versions == that.versions;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(versions);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProjectStatus
    {
        @JsonProperty
        public List<VersionStatus> versions = new ArrayList<>();
        @JsonProperty
        public MasterRevisionStatus masterRevision;

        public void addVersionStatus(VersionStatus versionStatus)
        {
            this.versions.add(versionStatus);
        }

        public void setMasterRevisionStatus(MasterRevisionStatus masterRevisionStatus)
        {
            this.masterRevision = masterRevisionStatus;
        }

        @JsonProperty
        public long getTotalVersions()
        {
            return this.versions.size();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VersionStatus
    {
        @JsonProperty
        public final String groupId;
        @JsonProperty
        public final String artifactId;
        @JsonProperty
        public final String version;
        @JsonProperty
        public Date lastUpdated;
        @JsonProperty
        public boolean updating;
        @JsonProperty
        public String url;
        @JsonProperty
        public int queryCount;
        @JsonProperty
        public Date lastQueried;


        public VersionStatus(String groupId, String artifactId, String version)
        {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MasterRevisionStatus extends VersionStatus
    {

        public MasterRevisionStatus(String groupId, String artifactId)
        {
            super(groupId, artifactId, MASTER_SNAPSHOT);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocumentCounts
    {
        @JsonProperty
        public long totalVersionEntities;
        @JsonProperty
        public long totalRevisionEntities;

        public DocumentCounts(long totalVersionEntities, long totalRevisionEntities)
        {
            this.totalVersionEntities = totalVersionEntities;
            this.totalRevisionEntities = totalRevisionEntities;
        }
    }


}

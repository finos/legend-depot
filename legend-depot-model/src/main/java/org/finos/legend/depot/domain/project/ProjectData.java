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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.CoordinateData;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public final class ProjectData extends CoordinateData
{
    @JsonProperty
    private String projectId;
    @JsonProperty
    private List<String> versions = new ArrayList<>();
    @JsonProperty
    private List<ProjectVersionDependency> dependencies = new ArrayList<>();
    @JsonProperty
    private List<ProjectVersionProperty> properties = new ArrayList<>();

    public ProjectData()
    {
    }

    public ProjectData(String projectId, String groupId, String artifactId)
    {
        super(groupId, artifactId);
        this.projectId = projectId;
    }

    @JsonIgnore
    public String getId()
    {
        return "";
    }

    public String getProjectId()
    {
        return projectId;
    }

    public List<String> getVersions()
    {
        Collections.sort(versions);
        return versions;
    }

    public void addVersion(String versionId)
    {
        if (!versionId.equals(VersionValidator.MASTER_SNAPSHOT) && !this.getVersions().contains(versionId))
        {
            this.versions.add(versionId);
        }
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

    @JsonProperty("latestVersion")
    public String getLatestVersionAsString()
    {
        Optional<VersionId> latest = getLatestVersion();
        return latest.map(VersionId::toVersionIdString).orElse(null);
    }

    @JsonIgnore
    public Optional<VersionId> getLatestVersion()
    {
        if (versions != null && !versions.isEmpty())
        {
            List<VersionId> versionIds = versions.stream().map(VersionId::parseVersionId).collect(Collectors.toList());
            return versionIds.stream().max(VersionId::compareTo);
        }
        return Optional.empty();
    }

    public List<ProjectVersionDependency> getDependencies()
    {
        return dependencies;
    }

    @JsonIgnore
    public List<ProjectVersionDependency> getDependencies(String version)
    {
        return dependencies.stream().filter(dependency -> dependency.getVersionId().equals(version)).collect(Collectors.toList());
    }

    public List<ProjectVersionProperty> getPropertiesForProjectVersionID(String projectVersionId)
    {
        return properties.stream().filter(property -> property.getProjectVersionId().equals(projectVersionId)).collect(Collectors.toList());
    }

    public void addDependencies(List<ProjectVersionDependency> dependencies)
    {
        this.dependencies.addAll(dependencies);
    }

    public void setDependencies(List<ProjectVersionDependency> dependencies)
    {
        this.dependencies = dependencies;
    }


    public List<ProjectVersionProperty> getProperties()
    {
        return properties;
    }

    public void setProperties(List<ProjectVersionProperty> properties)
    {
        this.properties = properties;
    }

    public void addProperties(List<ProjectVersionProperty> propertyList)
    {
        propertyList.stream().filter(property -> !properties.contains(property)).forEach(property -> this.properties.add(property));
    }

    @JsonIgnore
    public Optional<String> getVersion(String versionId)
    {
        return this.versions.stream().filter(v -> v.equals(versionId)).findFirst();
    }

    public static class ProjectVersionDependency extends VersionedData
    {
        private ProjectVersion dependency;

        public ProjectVersionDependency()
        {
        }

        public ProjectVersionDependency(String groupid, String artifactId, String versionId, ProjectVersion dep)
        {
            super(groupid, artifactId, versionId);
            this.dependency = dep;
        }

        public ProjectVersion getDependency()
        {
            return dependency;
        }

        public void setDependency(ProjectVersion dependency)
        {
            this.dependency = dependency;
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
}

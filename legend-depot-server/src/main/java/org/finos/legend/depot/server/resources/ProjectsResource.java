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

package org.finos.legend.depot.server.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.CoordinateData;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("")
@Api("Projects")
@Deprecated
public class ProjectsResource extends BaseResource
{

    private final ProjectsService projectApi;

    @Inject
    public ProjectsResource(ProjectsService projectApi)
    {
        this.projectApi = projectApi;
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_BY_GA)
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Optional<ProjectData> getProject(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_BY_GA, ResourceLoggingAndTracing.GET_PROJECT_BY_GA + groupId + artifactId, () ->
        {
            List<StoreProjectVersionData> projectVersions = projectApi.find(groupId, artifactId);
            Optional<StoreProjectData> projectCoordinates = this.projectApi.findCoordinates(groupId, artifactId);
            if (!projectVersions.isEmpty() && projectCoordinates.isPresent())
            {
                return Optional.of(transformToProjectData(projectCoordinates.get().getProjectId(), groupId, artifactId, projectVersions));
            }
            return Optional.empty();
        });
    }

    @GET
    @Path("/projects/versions/all/projectData")
    @ApiOperation(ResourceLoggingAndTracing.GET_ALL_PROJECTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoreProjectData> getProjectsWithCoordinates()
    {
        return handle(ResourceLoggingAndTracing.GET_ALL_PROJECTS, () -> projectApi.getAllProjectCoordinates());
    }

    @GET
    @Path("/projects")
    @ApiOperation(ResourceLoggingAndTracing.GET_ALL_LEGACY_PROJECTS)
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<ProjectData> getProjects()
    {
        return handle(ResourceLoggingAndTracing.GET_ALL_LEGACY_PROJECTS, ResourceLoggingAndTracing.GET_ALL_LEGACY_PROJECTS, () ->
        {
            List<StoreProjectData> projectCoordinates = projectApi.getAllProjectCoordinates();
            if (!projectCoordinates.isEmpty())
            {
                return projectCoordinates.stream().map(pc ->
                {
                    List<StoreProjectVersionData> projectVersions = projectApi.find(pc.getGroupId(), pc.getArtifactId());
                    return projectVersions.isEmpty() ? new ProjectData(pc.getProjectId(), pc.getGroupId(), pc.getArtifactId()) : transformToProjectData(pc.getProjectId(), pc.getGroupId(), pc.getArtifactId(), projectVersions);
                }).collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
    }

    private ProjectData transformToProjectData(String projectId, String groupId, String artifactId, List<StoreProjectVersionData> projectVersionsData)
    {
        ProjectData projectData = new ProjectData(projectId, groupId, artifactId);
        projectVersionsData.stream().forEach(pv ->
        {
            List<ProjectData.ProjectVersionDependency> dependencies = pv.getVersionData().getDependencies().stream().map(dep -> new ProjectData.ProjectVersionDependency(groupId, artifactId, pv.getVersionId(), dep)).collect(Collectors.toList());
            projectData.addDependencies(dependencies);
            List<ProjectVersionProperty> projectProperties = pv.getVersionData().getProperties().stream().map(prop -> new ProjectVersionProperty(prop.getPropertyName(), prop.getValue(), pv.getVersionId())).collect(Collectors.toList());
            projectData.addProperties(projectProperties);
            projectData.addVersion(pv.getVersionId());
        });
        return projectData;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Deprecated
    public static final class ProjectData extends CoordinateData
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Deprecated
    public static final class ProjectVersionProperty
    {
        @JsonProperty
        private String propertyName;
        @JsonProperty
        private String value;
        @JsonProperty
        private String projectVersionId;

        public ProjectVersionProperty()
        {

        }

        public ProjectVersionProperty(String propertyName, String value, String projectVersionId)
        {
            this.propertyName = propertyName;
            this.value = value;
            this.projectVersionId = projectVersionId;
        }

        public String getProjectVersionId()
        {
            return projectVersionId;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        public String getValue()
        {
            return value;
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

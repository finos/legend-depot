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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.domain.CoordinateData;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersionProperty;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public List<ProjectDataDTO> getProjectsWithCoordinates()
    {
        return handle(ResourceLoggingAndTracing.GET_ALL_PROJECTS, () ->
            projectApi.getAllProjectCoordinates().stream().map(pc -> new ProjectDataDTO(pc.getProjectId(), pc.getGroupId(), pc.getArtifactId())).collect(Collectors.toList()));
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

    public class ProjectDataDTO extends CoordinateData
    {
        private String projectId;

        public ProjectDataDTO()
        {
            super();
        }

        public ProjectDataDTO(String projectId, String groupId, String artifactId)
        {
            super(groupId, artifactId);
            this.projectId = projectId;
        }

        public String getProjectId()
        {
            return projectId;
        }
    }
}

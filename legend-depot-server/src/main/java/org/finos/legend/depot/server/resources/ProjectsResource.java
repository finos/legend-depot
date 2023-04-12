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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("")
@Api("Projects")
public class ProjectsResource extends BaseResource
{

    private final ProjectsService projectApi;

    @Inject
    public ProjectsResource(ProjectsService projectApi)
    {
        this.projectApi = projectApi;
    }


    @GET
    @Path("/project-configurations")
    @ApiOperation(ResourceLoggingAndTracing.GET_ALL_PROJECTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoreProjectData> getProjectsWithCoordinates()
    {
        return handle(ResourceLoggingAndTracing.GET_ALL_PROJECTS, () -> projectApi.getAllProjectCoordinates());
    }

    @GET
    @Path("/project-configurations/{groupId}/{artifactId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CONFIG_BY_GA)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<StoreProjectData> getProjectCoordinates(@PathParam("groupId")String groupId, @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CONFIG_BY_GA, () -> projectApi.findCoordinates(groupId, artifactId));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getVersions(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.GET_VERSIONS, () -> projectApi.getVersions(groupId, artifactId));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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

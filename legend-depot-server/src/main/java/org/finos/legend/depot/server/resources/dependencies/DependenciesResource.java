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

package org.finos.legend.depot.server.resources.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.Property;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.server.resources.ProjectsResource;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.tracing.resources.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_DEPENDANT_PROJECTS;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_PROJECT_DEPENDENCIES;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_PROJECT_DEPENDENCY_TREE;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_DEPENDENCY_ENTITIES;

@Path("")
@Api("Dependencies")
public class DependenciesResource extends BaseResource
{
    private final EntitiesService entitiesService;
    private final ProjectsService projectApi;

    @Inject
    public DependenciesResource(EntitiesService entitiesService, ProjectsService projectApi)
    {
        this.entitiesService = entitiesService;
        this.projectApi = projectApi;
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/projectDependencies")
    @ApiOperation(GET_PROJECT_DEPENDENCIES)
    @Produces(MediaType.APPLICATION_JSON)
    public Set<ProjectVersion> getProjectDependencies(@PathParam("groupId") String groupId,
                                                      @PathParam("artifactId") String artifactId,
                                                      @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                                      @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to return transitive dependencies") boolean transitive)
    {
        return handle(GET_PROJECT_DEPENDENCIES, GET_PROJECT_DEPENDENCIES + groupId + artifactId, () -> this.projectApi.getDependencies(groupId, artifactId, versionId, transitive));
    }

    @POST
    @Path("/projects/analyzeDependencyTree")
    @ApiOperation(GET_PROJECT_DEPENDENCY_TREE)
    @Produces(MediaType.APPLICATION_JSON)
    public ProjectDependencyReport analyzeDependencyTree(@ApiParam("projectDependencies") List<ProjectVersion> projectDependencies)
    {
        return handle(GET_PROJECT_DEPENDENCY_TREE, GET_PROJECT_DEPENDENCY_TREE, () -> this.projectApi.getProjectDependencyReport(projectDependencies));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/dependantProjects")
    @ApiOperation(GET_DEPENDANT_PROJECTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectVersionPlatformDependency> getProjectDependencies(@PathParam("groupId") String groupId,
                                                                         @PathParam("artifactId") String artifactId,
                                                                         @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId
    )
    {
        return handle(GET_DEPENDANT_PROJECTS, GET_DEPENDANT_PROJECTS + groupId + artifactId, () -> transform(this.projectApi.getDependentProjects(groupId, artifactId, versionId)));
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/dependencies")
    @ApiOperation(GET_VERSION_DEPENDENCY_ENTITIES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectVersionEntities> getEntitiesFromDependencies(@PathParam("groupId") String groupId,
                                                                    @PathParam("artifactId") String artifactId,
                                                                    @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                                                    @QueryParam("versioned") @DefaultValue("false")
                                                                    @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned,
                                                                    @QueryParam("transitive") @DefaultValue("false")
                                                                    @ApiParam("Whether to return transitive dependencies") boolean transitive,
                                                                    @QueryParam("includeOrigin") @DefaultValue("false")
                                                                    @ApiParam("Whether to return start of dependency tree") boolean includeOrigin)
    {
        return handle(GET_VERSION_DEPENDENCY_ENTITIES, () -> this.entitiesService.getDependenciesEntities(groupId, artifactId, versionId, versioned, transitive, includeOrigin));
    }

    @POST
    @Path("/projects/dependencies")
    @ApiOperation(GET_VERSION_DEPENDENCY_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectVersionEntities> getAllEntitiesFromDependencies(@ApiParam("projectDependencies") List<ProjectVersion> projectDependencies,
                                                                       @QueryParam("versioned") @DefaultValue("false")
                                                                       @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned,
                                                                       @QueryParam("transitive") @DefaultValue("false")
                                                                       @ApiParam("Whether to return transitive dependencies") boolean transitive,
                                                                       @QueryParam("includeOrigin") @DefaultValue("false")
                                                                       @ApiParam("Whether to return start of dependency tree") boolean includeOrigin)
    {
        return handle(GET_VERSION_DEPENDENCY_ENTITIES, () -> this.entitiesService.getDependenciesEntities(projectDependencies, versioned, transitive, includeOrigin));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Deprecated
    private static final class ProjectVersionPlatformDependency extends VersionedData
    {
        @JsonProperty
        private List<ProjectsResource.ProjectVersionProperty> platformsVersion;
        @JsonProperty
        private ProjectVersion dependency;

        public ProjectVersionPlatformDependency(String groupId, String artifactId, String versionId, ProjectVersion dependency, List<ProjectsResource.ProjectVersionProperty> platformsVersion)
        {
            super(groupId, artifactId, versionId);
            this.platformsVersion = platformsVersion;
            this.dependency = dependency;
        }

        public List<ProjectsResource.ProjectVersionProperty> getPlatformsVersion()
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

    private List<ProjectVersionPlatformDependency> transform(List<ProjectDependencyWithPlatformVersions> dependentProjects)
    {
        return dependentProjects.stream().map(dep -> new ProjectVersionPlatformDependency(dep.getGroupId(),dep.getArtifactId(),dep.getVersionId(),dep.getDependency(),transformPropertyToProjectProperty(dep.getPlatformsVersion(),dep.getVersionId()))).collect(Collectors.toList());
    }

    private List<ProjectsResource.ProjectVersionProperty> transformPropertyToProjectProperty(List<Property> properties, String versionId)
    {
        return properties.stream().map(p -> new ProjectsResource.ProjectVersionProperty(p.getPropertyName(), p.getValue(), versionId)).collect(Collectors.toList());
    }
}

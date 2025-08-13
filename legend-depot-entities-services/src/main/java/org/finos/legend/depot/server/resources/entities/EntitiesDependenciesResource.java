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

package org.finos.legend.depot.server.resources.entities;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.finos.legend.depot.services.api.EtagBuilder;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSIONS_DEPENDENCY_ENTITIES;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_DEPENDENCY_ENTITIES;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_ENTITY_FROM_DEPENDENCIES;

@Path("")
@Api("Dependencies")
public class EntitiesDependenciesResource extends TracingResource
{
    private final EntitiesService entitiesService;

    @Inject
    public EntitiesDependenciesResource(EntitiesService entitiesService)
    {
        this.entitiesService = entitiesService;
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/dependencies")
    @ApiOperation(GET_VERSION_DEPENDENCY_ENTITIES)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntitiesFromDependencies(@PathParam("groupId") String groupId,
                                                @PathParam("artifactId") String artifactId,
                                                @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                                @QueryParam("transitive") @DefaultValue("false")
                                                @ApiParam("Whether to return transitive dependencies") boolean transitive,
                                                @QueryParam("includeOrigin") @DefaultValue("false")
                                                @ApiParam("Whether to return start of dependency tree") boolean includeOrigin,
                                                @Context Request request)
    {
        return handle(GET_VERSION_DEPENDENCY_ENTITIES, () -> this.entitiesService.getDependenciesEntities(groupId, artifactId, versionId, transitive, includeOrigin), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/classifiers/{classifier}/dependencies")
    @ApiOperation(value = GET_VERSION_DEPENDENCY_ENTITIES, hidden = true)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntitiesFromDependenciesByClassifier(@PathParam("groupId") String groupId,
                                                            @PathParam("artifactId") String artifactId,
                                                            @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                                            @PathParam("classifier") String classifier,
                                                            @QueryParam("transitive") @DefaultValue("false")
                                                            @ApiParam("Whether to return transitive dependencies") boolean transitive,
                                                            @QueryParam("includeOrigin") @DefaultValue("false")
                                                            @ApiParam("Whether to return start of dependency tree") boolean includeOrigin,
                                                            @Context Request request)
    {
        if (classifier == null)
        {
            Response.status(Response.Status.BAD_REQUEST).entity("Classifier is not valid").build();
        }
        return handle(GET_VERSION_DEPENDENCY_ENTITIES, () -> this.entitiesService.getDependenciesEntitiesByClassifier(groupId, artifactId, versionId, classifier, transitive, includeOrigin), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @POST
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/dependencies/paths")
    @ApiOperation(value = GET_VERSION_ENTITY_FROM_DEPENDENCIES, hidden = true)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntityFromDependencies(@PathParam("groupId") String groupId,
                                              @PathParam("artifactId") String artifactId,
                                              @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                              @ApiParam("entityPaths") List<String> entityPaths,
                                              @QueryParam("includeOrigin")
                                              @DefaultValue("false")
                                              @ApiParam("Whether to find entity in the GAV provided") boolean includeOrigin,
                                              @Context Request request)
    {
        return handle(GET_VERSION_ENTITY_FROM_DEPENDENCIES, GET_VERSION_ENTITY_FROM_DEPENDENCIES + StringUtils.join(entityPaths, ","), () -> this.entitiesService.getEntityFromDependencies(groupId, artifactId, versionId, entityPaths, includeOrigin), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @POST
    @Path("/projects/dependencies")
    @ApiOperation(GET_VERSIONS_DEPENDENCY_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntitiesFromDependencies(@ApiParam("projectDependencies") List<ProjectVersion> projectDependencies,
                                                                       @QueryParam("transitive") @DefaultValue("false")
                                                                       @ApiParam("Whether to return transitive dependencies") boolean transitive,
                                                                       @QueryParam("includeOrigin") @DefaultValue("false")
                                                                       @ApiParam("Whether to return start of dependency tree") boolean includeOrigin)
    {
        return handleResponse(GET_VERSIONS_DEPENDENCY_ENTITIES, () -> this.entitiesService.getDependenciesEntities(projectDependencies, transitive, includeOrigin));
    }
}

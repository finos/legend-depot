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
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_ENTITIES;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_ENTITIES_BY_PACKAGE;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_ENTITY;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_ENTITIES;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_ENTITIES_BY_PACKAGE;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_ENTITY;

@Path("")
@Api("Entities")
public class EntitiesResource extends BaseResource
{
    private final EntitiesService entitiesService;

    @Inject
    public EntitiesResource(EntitiesService entitiesService)
    {
        this.entitiesService = entitiesService;
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(GET_VERSION_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entity> getEntities(@PathParam("groupId") String groupId,
                                    @PathParam("artifactId") String artifactId,
                                    @PathParam("versionId") String versionId,
                                    @QueryParam("versioned")
                                    @DefaultValue("false")
                                    @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_ENTITIES, () -> this.entitiesService.getEntities(groupId, artifactId, versionId, versioned));
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/entities/{path}")
    @ApiOperation(GET_VERSION_ENTITY)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<Entity> getEntity(@PathParam("groupId") String groupId,
                                      @PathParam("artifactId") String artifactId,
                                      @PathParam("versionId") String versionId,
                                      @PathParam("path") String entityPath)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_ENTITY, GET_VERSION_ENTITY + entityPath, () -> this.entitiesService.getEntity(groupId, artifactId, versionId, entityPath));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/entities")
    @ApiOperation(GET_VERSION_ENTITIES_BY_PACKAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entity> getEntities(@PathParam("groupId") String groupId,
                                    @PathParam("artifactId") String artifactId,
                                    @PathParam("versionId") String versionId,
                                    @QueryParam("package") String packageName,
                                    @QueryParam("versioned")
                                    @DefaultValue("false")
                                    @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned,
                                    @QueryParam("classifierPath") @ApiParam("Only include ENTITIES with one of these classifier paths.") Set<String> classifierPaths,
                                    @QueryParam("includeSubPackages")
                                    @DefaultValue("true")
                                    @ApiParam("Whether to include ENTITIES from subpackages or only directly in one of the given packages") boolean includeSubPackages
    )
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_ENTITIES_BY_PACKAGE, GET_VERSION_ENTITIES_BY_PACKAGE + packageName, () -> entitiesService.getEntitiesByPackage(groupId, artifactId, versionId, packageName, versioned, classifierPaths, includeSubPackages));
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/revisions/latest")
    @ApiOperation(GET_REVISION_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entity> getLatestEntities(@PathParam("groupId") String groupId,
                                          @PathParam("artifactId") String artifactId,
                                          @QueryParam("versioned")
                                          @DefaultValue("false")
                                          @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned)
    {
        QueryMetricsContainer.record(groupId, artifactId, MASTER_SNAPSHOT);
        return handle(GET_REVISION_ENTITIES, () -> this.entitiesService.getLatestEntities(groupId, artifactId, versioned));
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/latest/entities/{path}")
    @ApiOperation(GET_REVISION_ENTITY)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<Entity> geLatestEntity(@PathParam("groupId") String groupId,
                                           @PathParam("artifactId") String artifactId,
                                           @PathParam("path") String entityPath)
    {
        QueryMetricsContainer.record(groupId, artifactId, MASTER_SNAPSHOT);
        return handle(GET_REVISION_ENTITY, GET_REVISION_ENTITY + entityPath, () -> this.entitiesService.getLatestEntity(groupId, artifactId, entityPath));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/latest/entities")
    @ApiOperation(GET_REVISION_ENTITIES_BY_PACKAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entity> getLatestEntities(@PathParam("groupId") String groupId,
                                          @PathParam("artifactId") String artifactId,
                                          @QueryParam("package") String packageName,
                                          @QueryParam("versioned")
                                          @DefaultValue("false")
                                          @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned,
                                          @QueryParam("classifierPath") @ApiParam("Only include ENTITIES with one of these classifier paths.") Set<String> classifierPaths,
                                          @QueryParam("includeSubPackages")
                                          @DefaultValue("true")
                                          @ApiParam("Whether to include ENTITIES from subpackages or only directly in one of the given packages") boolean includeSubPackages)
    {
        QueryMetricsContainer.record(groupId, artifactId, MASTER_SNAPSHOT);
        return handle(GET_REVISION_ENTITIES_BY_PACKAGE, GET_REVISION_ENTITIES_BY_PACKAGE + packageName, () -> this.entitiesService.getLatestEntitiesByPackage(groupId, artifactId, packageName, versioned, classifierPaths, includeSubPackages));
    }
}

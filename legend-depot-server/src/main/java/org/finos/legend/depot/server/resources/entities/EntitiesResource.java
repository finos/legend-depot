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
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.tracing.resources.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.EntityTag;
import java.util.Arrays;
import java.util.Set;

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
    public Response getEntities(@PathParam("groupId") String groupId,
                                @PathParam("artifactId") String artifactId,
                                @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                @Context Request request)
    {
        return handle(GET_VERSION_ENTITIES, () -> this.entitiesService.getEntities(groupId, artifactId, versionId), request, () -> this.generateETag(groupId, artifactId, versionId));
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/entities/{path}")
    @ApiOperation(GET_VERSION_ENTITY)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntity(@PathParam("groupId") String groupId,
                                      @PathParam("artifactId") String artifactId,
                                      @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                      @PathParam("path") String entityPath,
                                      @Context Request request)
    {
        return handle(GET_VERSION_ENTITY, GET_VERSION_ENTITY + entityPath, () -> this.entitiesService.getEntity(groupId, artifactId, versionId, entityPath), request, () -> this.generateETag(groupId, artifactId, versionId));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/entities")
    @ApiOperation(GET_VERSION_ENTITIES_BY_PACKAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntities(@PathParam("groupId") String groupId,
                                    @PathParam("artifactId") String artifactId,
                                    @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                    @QueryParam("package") String packageName,
                                    @QueryParam("classifierPath") @ApiParam("Only include ENTITIES with one of these classifier paths.") Set<String> classifierPaths,
                                    @QueryParam("includeSubPackages")
                                    @DefaultValue("true")
                                    @ApiParam("Whether to include ENTITIES from subpackages or only directly in one of the given packages") boolean includeSubPackages,
                                    @Context Request request
    )
    {
        return handle(GET_VERSION_ENTITIES_BY_PACKAGE, GET_VERSION_ENTITIES_BY_PACKAGE + packageName, () -> entitiesService.getEntitiesByPackage(groupId, artifactId, versionId, packageName, classifierPaths, includeSubPackages), request, () -> this.generateETag(groupId, artifactId, versionId));
    }

    private EntityTag generateETag(String groupId, String artifactId, String versionId)
    {
        if (VersionValidator.isSnapshotVersion(versionId) || VersionValidator.isVersionAlias(versionId))
        {
           return null;
        }
        return calculateEtag(Arrays.asList(groupId, artifactId, versionId));
    }
}

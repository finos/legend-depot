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

package org.finos.legend.depot.server.resources.deprecated;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.finos.legend.depot.services.api.entities.EntitiesService;
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

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

@Path("")
@Api("Deprecated")
public class DeprecatedEntitiesAPIsResource extends BaseResource
{

    private static final String GET_REVISION_ENTITIES = "get revision entities deprecated";
    private static final String GET_REVISION_ENTITY = "get revision entity deprecated";
    private static final String GET_REVISION_ENTITIES_BY_PACKAGE = "get revision entities by package deprecated";
    private final EntitiesService entitiesService;

    @Inject
    public DeprecatedEntitiesAPIsResource(EntitiesService entitiesService)
    {
        this.entitiesService = entitiesService;
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/revisions/latest")
    @ApiOperation(value = GET_REVISION_ENTITIES, notes = "replaced by: /projects/{groupId}/{artifactId}/versions/master-SNAPSHOT", tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<Entity> getLatestEntities(@PathParam("groupId") String groupId,
                                          @PathParam("artifactId") String artifactId)
    {
        return handle(GET_REVISION_ENTITIES, () -> this.entitiesService.getEntities(groupId, artifactId, BRANCH_SNAPSHOT("master")));
    }


    @GET
    @Path("/projects/{groupId}/{artifactId}/latest/entities/{path}")
    @ApiOperation(value = GET_REVISION_ENTITY,notes = "replaced by: /projects/{groupId}/{artifactId}/versions/master-SNAPSHOT/entities/{path}", tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Optional<Entity> geLatestEntity(@PathParam("groupId") String groupId,
                                           @PathParam("artifactId") String artifactId,
                                           @PathParam("path") String entityPath)
    {
        return handle(GET_REVISION_ENTITY, GET_REVISION_ENTITY + entityPath, () -> this.entitiesService.getEntity(groupId, artifactId,BRANCH_SNAPSHOT("master"), entityPath));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/latest/entities")
    @ApiOperation(value = GET_REVISION_ENTITIES_BY_PACKAGE, notes = "replaced by: /projects/{groupId}/{artifactId}/versions/master-SNAPSHOT/entities",tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<Entity> getLatestEntities(@PathParam("groupId") String groupId,
                                          @PathParam("artifactId") String artifactId,
                                          @QueryParam("package") String packageName,
                                          @QueryParam("classifierPath") @ApiParam("Only include ENTITIES with one of these classifier paths.") Set<String> classifierPaths,
                                          @QueryParam("includeSubPackages")
                                          @DefaultValue("true")
                                          @ApiParam("Whether to include ENTITIES from subpackages or only directly in one of the given packages") boolean includeSubPackages)
    {
        return handle(GET_REVISION_ENTITIES_BY_PACKAGE, GET_REVISION_ENTITIES_BY_PACKAGE + packageName, () -> this.entitiesService.getEntitiesByPackage(groupId, artifactId, BRANCH_SNAPSHOT("master"),packageName, classifierPaths, includeSubPackages));
    }
}

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
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.tracing.resources.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

@Path("")
@Api("Deprecated")
public class DeprecatedDependenciesAPIsResource extends BaseResource
{

    private static final String GET_REVISION_DEPENDENCY_ENTITIES = "get latest dependencies entities";
    private final EntitiesService entitiesService;


    @Inject
    public DeprecatedDependenciesAPIsResource(EntitiesService entitiesService)
    {
        this.entitiesService = entitiesService;
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/revisions/latest/dependants")
    @ApiOperation(value = GET_REVISION_DEPENDENCY_ENTITIES, notes = "replaced by: /projects/{groupId}/{artifactId}/versions/master-SNAPSHOT/dependantProjects", tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<ProjectVersionEntities> getLatestEntitiesFromDependencies(@PathParam("groupId") String groupId,
                                                                          @PathParam("artifactId") String artifactId,
                                                                          @QueryParam("versioned")
                                                                          @DefaultValue("false")
                                                                          @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned,
                                                                          @QueryParam("transitive") @DefaultValue("false")
                                                                          @ApiParam("Whether to return transitive dependencies") boolean transitive,
                                                                          @QueryParam("includeOrigin") @DefaultValue("false")
                                                                          @ApiParam("Whether to return start of dependency tree") boolean includeOrigin)
    {
        return handle(GET_REVISION_DEPENDENCY_ENTITIES, () -> this.entitiesService.getDependenciesEntities(groupId, artifactId,MASTER_SNAPSHOT, versioned, transitive, includeOrigin));
    }
}

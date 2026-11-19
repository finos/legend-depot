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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.domain.version.Scope;
import org.finos.legend.depot.services.api.entities.EntityClassifierService;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_ENTITIES_BY_CLASSIFIER_PATH;

@Path("")
@Tag(name = "Classifiers")
public class EntityClassifierResource extends TracingResource
{
    private final EntityClassifierService graphService;

    @Inject
    public EntityClassifierResource(EntityClassifierService graphService)
    {
        this.graphService = graphService;
    }

    @GET
    @Path("/entitiesByClassifierPath/{classifierPath}")
    // This API is built temporarily to address needs to query entities by classifier path
    // The bigger plan is to allow a generic execution endpoint to fire arbitrary query against the metadata
    // graph built in depot server.
    @Operation(summary = GET_ENTITIES_BY_CLASSIFIER_PATH, hidden = true)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEntities(@PathParam("classifierPath") @Parameter(description = "The classifier path of the entities") String classifierPath,
                                @QueryParam("search") @Parameter(description = "The search string that the entity path contains") String search,
                                @QueryParam("scope") @Parameter(description = "Whether to return entities for the latest released version or snapshot") @DefaultValue("RELEASES") Scope scope,
                                @QueryParam("limit") @Parameter(description = "Limit the number of entities returned") Integer limit)
    {
        return handleResponse(GET_ENTITIES_BY_CLASSIFIER_PATH, () -> this.graphService.getEntitiesByClassifierPath(classifierPath, search, limit, scope, true));
    }
}

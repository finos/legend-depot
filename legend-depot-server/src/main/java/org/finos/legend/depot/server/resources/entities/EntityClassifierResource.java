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
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.services.api.entities.EntityClassifierService;
import org.finos.legend.depot.tracing.resources.BaseResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_ENTITIES_BY_CLASSIFIER_PATH;

@Path("")
@Api("Entities")
public class EntityClassifierResource extends BaseResource
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
    @ApiOperation(value = GET_ENTITIES_BY_CLASSIFIER_PATH, hidden = true)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoredEntity> getEntities(@PathParam("classifierPath") @ApiParam("The classifier path of the entities") String classifierPath,
                                          @QueryParam("search") @ApiParam("The search string that the entity path contains") String search,
                                          @QueryParam("limit") @ApiParam("Limit the number of entities returned") Integer limit)
    {
        return handle(GET_ENTITIES_BY_CLASSIFIER_PATH, () -> this.graphService.getEntitiesByClassifierPath(classifierPath, search, limit));
    }
}

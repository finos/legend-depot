//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.server.pure.model.context.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.finos.legend.depot.server.pure.model.context.api.PureModelContextService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

@Path("")
@Api("Deprecated")
public class DeprecatedPureModelContextAPIsResource extends BaseResource
{

    private static final String GET_REVISION_ENTITIES_AS_PMCD = "get revision entities as PMCD deprecated";
    private final PureModelContextService service;

    @Inject
    public DeprecatedPureModelContextAPIsResource(PureModelContextService service)
    {
        this.service = service;
    }


    @GET
    @Path("projects/{groupId}/{artifactId}/revisions/latest/pureModelContextData")
    @ApiOperation(value = GET_REVISION_ENTITIES_AS_PMCD, notes = "replaced by: projects/{groupId}/{artifactId}/versions/master-SNAPSHOT/pureModelContextData", tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public PureModelContextData getPureModelContextData(@PathParam("groupId") String groupId,
                                                        @PathParam("artifactId") String artifactId,
                                                        @QueryParam("clientVersion") String clientVersion,
                                                        @QueryParam("versioned")
                                                        @DefaultValue("false")
                                                        @ApiParam("Whether to return ENTITIES with version in entity path") boolean versioned,
                                                        @QueryParam("getDependencies")
                                                        @DefaultValue("true")
                                                        @ApiParam("Whether to return ENTITIES with version in entity path") boolean getDependencies)
    {
        return handle(GET_REVISION_ENTITIES_AS_PMCD, () -> service.getPureModelContextData(groupId, artifactId,MASTER_SNAPSHOT, clientVersion, versioned, getDependencies));
    }
}

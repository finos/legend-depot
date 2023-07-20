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
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.server.pure.model.context.api.PureModelContextService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.engine.protocol.pure.PureClientVersions;

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

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_ENTITIES_AS_PMCD;

@Path("")
@Api("Pure Model Context Data")
public class PureModelContextResource extends BaseResource
{
    private final PureModelContextService service;
    private static final String HEAD_PROTOCOL_VERSION = "vX_X_X";

    @Inject
    public PureModelContextResource(PureModelContextService service)
    {
        this.service = service;
    }

    @GET
    @Path("projects/{groupId}/{artifactId}/versions/{versionId}/pureModelContextData")
    @ApiOperation(GET_VERSION_ENTITIES_AS_PMCD)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPureModelContextData(@PathParam("groupId") String groupId,
                                            @PathParam("artifactId") String artifactId,
                                            @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT)  String versionId,
                                            @QueryParam("clientVersion") String clientVersion,
                                            @QueryParam("getDependencies")
                                                        @DefaultValue("true")
                                                        @ApiParam("Whether to include entities from dependencies") boolean transitive,
                                                        @Context Request request)
    {
        return handle(GET_VERSION_ENTITIES_AS_PMCD, () -> service.getPureModelContextData(groupId, artifactId, versionId, clientVersion, transitive), request, () -> this.generateETag(groupId, artifactId, versionId, clientVersion));
    }

    private EntityTag generateETag(String groupId, String artifactId, String versionId, String clientVersion)
    {
        if (VersionValidator.isSnapshotVersion(versionId) || VersionValidator.isVersionAlias(versionId) || (clientVersion != null && clientVersion.equalsIgnoreCase(HEAD_PROTOCOL_VERSION)))
        {
           return null;
        }  
        return calculateEtag(Arrays.asList(groupId, artifactId, versionId, clientVersion == null ? PureClientVersions.production : clientVersion));
    }
}

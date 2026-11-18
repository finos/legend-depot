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

package org.finos.legend.depot.server.resources.pure.model.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSIONS_DEPENDENCY_ENTITIES_AS_PMCD;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_ENTITIES_AS_PMCD;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.EtagBuilder;
import org.finos.legend.depot.services.api.pure.model.context.PureModelContextService;

@Path("")
@Tag(name = "Pure Model Context Data")
public class PureModelContextResource extends TracingResource
{
    private final PureModelContextService service;


    @Inject
    public PureModelContextResource(PureModelContextService service)
    {
        this.service = service;
    }

    @GET
    @Path("projects/{groupId}/{artifactId}/versions/{versionId}/pureModelContextData")
    @Operation(summary = GET_VERSION_ENTITIES_AS_PMCD)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPureModelContextData(@PathParam("groupId")
                                            String groupId,
                                            @PathParam("artifactId")
                                            String artifactId,
                                            @PathParam("versionId") @Parameter(description = VersionValidator.VALID_VERSION_ID_TXT)
                                            String versionId,
                                            @QueryParam("clientVersion")
                                            String clientVersion,
                                            @QueryParam("getDependencies")
                                            @DefaultValue("true")
                                            @Parameter(description = "Whether to include entities from dependencies")
                                            boolean transitive,
                                            @QueryParam("convertToNewProtocol")
                                            @DefaultValue("true")
                                            @Parameter(description = "Whether to convert the protocol to latest or return the protocol as initially published")
                                            boolean convertToNewProtocol,
                                            @Context Request request)
    {
        return handle(GET_VERSION_ENTITIES_AS_PMCD, () -> service.getPureModelContextData(groupId, artifactId, versionId, clientVersion, transitive, convertToNewProtocol), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).withProtocolVersion(clientVersion).build());
    }

    @POST
    @Path("projects/dependencies/pureModelContextData")
    @Operation(summary = GET_VERSIONS_DEPENDENCY_ENTITIES_AS_PMCD)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPureModelContextData(@RequestBody(description = "projectDependencies")
                                            List<ProjectVersion> projectDependencies,
                                            @QueryParam("clientVersion")
                                            String clientVersion,
                                            @QueryParam("transitive") @DefaultValue("true")
                                            @Parameter(description = "Whether to return transitive dependencies")
                                            boolean transitive,
                                            @QueryParam("convertToNewProtocol")
                                            @DefaultValue("true")
                                            @Parameter(description = "Whether to convert the protocol to latest or return the protocol as initially published")
                                            boolean convertToNewProtocol,
                                            @Context Request request)
    {
        return handleResponse(GET_VERSIONS_DEPENDENCY_ENTITIES_AS_PMCD, () -> service.getPureModelContextData(projectDependencies, clientVersion, transitive, convertToNewProtocol));
    }
}

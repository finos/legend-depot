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

package org.finos.legend.depot.store.resources.generation.file;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

@Path("")
@Api("Generations")
public class ManageFileGenerationsResource extends BaseAuthorisedResource
{
    private final ManageFileGenerationsService generationsService;

    @Inject
    public ManageFileGenerationsResource(ManageFileGenerationsService fileGenerationsService, AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.generationsService = fileGenerationsService;
    }

    @Override
    protected String getResourceName()
    {
        return "Generations";
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest")
    @ApiOperation(ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoredFileGeneration> getFileGenerationEntities(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION, () -> this.generationsService.getStoredLatestFileGenerations(groupId, artifactId));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoredFileGeneration> getFileGenerations(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION, () -> this.generationsService.getStoredFileGenerations(groupId, artifactId, versionId));
    }
}

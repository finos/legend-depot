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

package org.finos.legend.depot.store.resources.artifacts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

@Path("")
@Api("Repository")
public class VersionsReconciliationResource extends BaseAuthorisedResource
{
    private static final String REPOSITORY = "Repository";
    private final VersionsReconciliationService reconciliationService;

    @Inject
    public VersionsReconciliationResource(VersionsReconciliationService repositoryService, AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.reconciliationService = repositoryService;

    }

    @Override
    protected String getResourceName()
    {
        return REPOSITORY;
    }

    @GET
    @Path("/versions/mismatch")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CACHE_MISMATCHES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<VersionMismatch> getVersionMissMatches()
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_MISMATCHES, () -> this.reconciliationService.findVersionsMismatches());
    }
}

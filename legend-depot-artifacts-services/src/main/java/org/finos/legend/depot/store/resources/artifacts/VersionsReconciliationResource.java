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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

@Path("")
@Tag(name = "Repository")
public class VersionsReconciliationResource extends AuthorisedResource
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
    @Operation(summary = ResourceLoggingAndTracing.GET_PROJECT_CACHE_MISMATCHES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<VersionMismatch> getVersionMissMatches()
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_MISMATCHES, () -> this.reconciliationService.findVersionsMismatches());
    }
}

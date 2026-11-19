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
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;


@Path("")
@Tag(name = "Artifacts Refresh")
public class ArtifactDependenciesRefreshResource extends AuthorisedResource
{
    public static final String ARTIFACTS_RESOURCE = "ArtifactsRefresh";
    private final RefreshDependenciesService refreshDependenciesService;

    @Inject
    public ArtifactDependenciesRefreshResource(RefreshDependenciesService refreshDependenciesService,
                                               AuthorisationProvider authorisationProvider,
                                               @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.refreshDependenciesService = refreshDependenciesService;
    }

    @PUT
    @Path("/artifactsRefresh/dependencies/{groupId}/{artifactId}/{versionId}")
    @Operation(summary = ResourceLoggingAndTracing.UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreProjectVersionData updateTransitiveDependencies(@PathParam("groupId") String groupId,
                                                                          @PathParam("artifactId") String artifactId,
                                                                          @PathParam("versionId") String versionId)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES, ResourceLoggingAndTracing.UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES + groupId + artifactId + versionId, () ->
                {
                    validateUser();
                    return refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId);
                });
    }

    @Override
    protected String getResourceName()
    {
        return ARTIFACTS_RESOURCE;
    }
}

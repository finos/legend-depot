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

package org.finos.legend.depot.store.resources.dependencies;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.dependencies.ManageDependenciesService;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;


@Path("")
@Api("Dependencies")
public class ManageDependenciesResource extends BaseAuthorisedResource
{
    public static final String DEPENDENCIES_RESOURCE = "Dependencies";
    private final ManageDependenciesService manageDependenciesService;

    @Inject
    public ManageDependenciesResource(ManageDependenciesService manageDependenciesService,
                                      AuthorisationProvider authorisationProvider,
                                      @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.manageDependenciesService = manageDependenciesService;
    }

    @PUT
    @Path("/dependencies/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreProjectVersionData updateTransitiveDependencies(@PathParam("groupId") String groupId,
                                                                          @PathParam("artifactId") String artifactId,
                                                                          @PathParam("versionId") String versionId)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES, ResourceLoggingAndTracing.UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES + groupId + artifactId + versionId, () ->
                {
                    validateUser();
                    return manageDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId);
                });
    }

    @Override
    protected String getResourceName()
    {
        return DEPENDENCIES_RESOURCE;
    }
}

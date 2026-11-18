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

package org.finos.legend.depot.store.resources.versions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

@Path("")
@Tag(name = "Versions")
public class ManageProjectsVersionsResource extends AuthorisedResource
{

    public static final String PROJECTS_VERSIONS_RESOURCE = "Versions";
    private final ManageProjectsService projectVersionApi;


    @Inject
    public ManageProjectsVersionsResource(ManageProjectsService projectVersionApi,  AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.projectVersionApi = projectVersionApi;
    }

    @Override
    protected String getResourceName()
    {
        return PROJECTS_VERSIONS_RESOURCE;
    }

    @GET
    @Path("/versions")
    @Operation(summary = ResourceLoggingAndTracing.FIND_PROJECT_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoreProjectVersionData> findProjectVersion(@QueryParam("excluded") Boolean excluded)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.FIND_PROJECT_VERSIONS, ResourceLoggingAndTracing.FIND_PROJECT_VERSIONS + excluded, () -> projectVersionApi.findVersion(excluded));
    }

    @PUT
    @Path("/versions/{groupId}/{artifactId}/{versionId}/{exclusionReason}")
    @Operation(summary = ResourceLoggingAndTracing.EXCLUDE_PROJECT_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreProjectVersionData excludeProjectVersion(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") @Parameter(description = "a valid version string: x.y.z, master-SNAPSHOT") String versionId, @PathParam("exclusionReason") String exclusionReason)
    {
        return handle(ResourceLoggingAndTracing.EXCLUDE_PROJECT_VERSION, ResourceLoggingAndTracing.EXCLUDE_PROJECT_VERSION + groupId + artifactId + versionId + exclusionReason, () ->
            projectVersionApi.excludeProjectVersion(groupId, artifactId, versionId, exclusionReason)
        );
    }



}

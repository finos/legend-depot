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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
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
@Api("Versions")
public class ManageProjectsVersionsResource extends BaseAuthorisedResource
{

    public static final String PROJECTS_VERSIONS_RESOURCE = "Versions";
    private final ManageProjectsService projectVersionApi;

    @Inject
    public ManageProjectsVersionsResource(ManageProjectsService projectVersionApi, AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
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
    @Path("/versions/{groupId}/{artifactId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_VERSIONS_BY_GA)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoreProjectVersionData> getProjectVersion(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.GET_PROJECT_VERSIONS_BY_GA, ResourceLoggingAndTracing.GET_PROJECT_VERSIONS_BY_GA + groupId + artifactId, () -> projectVersionApi.find(groupId, artifactId));
    }

}

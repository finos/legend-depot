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

package org.finos.legend.depot.store.resources.projects;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;

@Path("")
@Tag(name = "Projects")
public class ManageProjectsResource extends AuthorisedResource
{

    public static final String PROJECTS_RESOURCE = "Projects";
    private final ManageProjectsService projectApi;

    @Inject
    public ManageProjectsResource(ManageProjectsService projectApi, AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.projectApi = projectApi;

    }

    @Override
    protected String getResourceName()
    {
        return PROJECTS_RESOURCE;
    }

    @PUT
    @Path("/projects/{projectId}/{groupId}/{artifactId}")
    @Operation(summary = ResourceLoggingAndTracing.CREATE_UPDATE_PROJECT)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreProjectData updateProject(@PathParam("projectId") String projectId, @PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @QueryParam("defaultBranch") @Parameter String defaultBranch, @QueryParam("latestVersion") @Parameter String latestVersion)
    {
        return handle(
                ResourceLoggingAndTracing.CREATE_UPDATE_PROJECT,
                ResourceLoggingAndTracing.CREATE_UPDATE_PROJECT + projectId,
                () ->
                {
                    validateUser();
                    return projectApi.createOrUpdate(new StoreProjectData(projectId, groupId, artifactId, defaultBranch, latestVersion));
                });
    }

    @DELETE
    @Path("/projects/{groupId}/{artifactId}")
    @Operation(summary = ResourceLoggingAndTracing.DELETE_PROJECT)
    @Produces(MediaType.APPLICATION_JSON)
    public long deleteProject(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {

        return handle(
                ResourceLoggingAndTracing.DELETE_PROJECT,
                ResourceLoggingAndTracing.DELETE_PROJECT + groupId + artifactId,
                () ->
                {
                    validateUser();
                    return projectApi.delete(groupId, artifactId);
                });
    }

}

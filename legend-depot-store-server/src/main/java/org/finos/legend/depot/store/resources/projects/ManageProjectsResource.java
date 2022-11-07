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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;

@Path("")
@Api("Projects")
public class ManageProjectsResource extends BaseAuthorisedResource
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
    @ApiOperation(ResourceLoggingAndTracing.CREATE_EMPTY_PROJECT)
    @Produces(MediaType.APPLICATION_JSON)
    public ProjectData updateProject(@PathParam("projectId") String projectId, @PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        return handle(
                ResourceLoggingAndTracing.CREATE_EMPTY_PROJECT,
                ResourceLoggingAndTracing.CREATE_EMPTY_PROJECT + projectId,
                () ->
                {
                    validateUser();
                    return projectApi.createOrUpdate(new ProjectData(projectId, groupId, artifactId));
                });
    }

    @DELETE
    @Path("/projects/{groupId}/{artifactId}")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_PROJECT)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse deleteProject(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
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

    @DELETE
    @Path("/projects/{projectId}")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_PROJECT_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse deleteProject(@PathParam("projectId") String projectId)
    {

        return handle(
                ResourceLoggingAndTracing.DELETE_PROJECT_ID,
                ResourceLoggingAndTracing.DELETE_PROJECT + projectId,
                () ->
                {
                    validateUser();
                    return projectApi.delete(projectId);
                });
    }

}

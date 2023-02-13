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

package org.finos.legend.depot.store.artifacts.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Path("")
@Api("Artifacts Refresh")
public class ArtifactsRefreshResource extends BaseAuthorisedResource
{
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String ARTIFACTS_RESOURCE = "ArtifactsRefresh";
    private final ArtifactsRefreshService artifactsRefreshService;
    private final RefreshStatusStore refreshStatusService;


    @Inject
    public ArtifactsRefreshResource(ArtifactsRefreshService artifactsRefreshService,
                                    RefreshStatusStore updateStatusService,
                                    AuthorisationProvider authorisationProvider,
                                    @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.artifactsRefreshService = artifactsRefreshService;
        this.refreshStatusService = updateStatusService;
    }


    @GET
    @Path("/artifactsRefresh/status")
    @ApiOperation("get updateStatusService information")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RefreshStatus> getStatus(
            @QueryParam("groupId") String group,
            @QueryParam("artifactId") String artifact,
            @QueryParam("versionId") String version,
            @QueryParam("eventId") String eventId,
            @QueryParam("parentEventId")
            @ApiParam("refresh could be started by another event, eg refresh all cache versions") String parentId,
            @QueryParam("running") Boolean running,
            @QueryParam("success") Boolean success,
            @QueryParam("startTimeFrom")
            @ApiParam("entries that started refresh from this date yyyy-MM-dd HH:mm:ss") String startTimeFrom,
            @QueryParam("startTimeTo")
            @ApiParam("entries that started refresh to this date yyyy-MM-dd HH:mm:ss (default is now)") String startTimeTo
    )
    {
        LocalDateTime fromStatTime = startTimeFrom == null ? null : LocalDateTime.parse(startTimeFrom, DATE_TIME_FORMATTER);
        LocalDateTime toStartTime = startTimeTo == null ? LocalDateTime.now() : LocalDateTime.parse(startTimeTo, DATE_TIME_FORMATTER);
        return handle(ResourceLoggingAndTracing.STORE_STATUS, () -> refreshStatusService.find(group, artifact, version,eventId,parentId,running, success, fromStatTime,toStartTime));
    }

    @DELETE
    @Path("/artifactsRefreshStatus/{id}")
    @ApiOperation("reset by id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetStatus(@PathParam("id") String id
    )
    {
        return handle(ResourceLoggingAndTracing.TOGGLE_STORE_STATUS, () ->
        {
            validateUser();
            refreshStatusService.delete(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateProjectVersion(@PathParam("groupId") String groupId,
                                                      @PathParam("artifactId") String artifactId,
                                                      @PathParam("versionId") String versionId,
                                                      @QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to force refresh of processed jar files, versions ,etc") boolean fullUpdate,
                                                      @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh its dependencies") boolean transitive)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.UPDATE_VERSION, ResourceLoggingAndTracing.UPDATE_VERSION + groupId + artifactId + versionId, () -> artifactsRefreshService.refreshVersionForProject(groupId, artifactId, versionId, fullUpdate,transitive,groupId + "-" + artifactId + "_" + ResourceLoggingAndTracing.UPDATE_VERSION));
    }

    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/versions")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateProjectAllVersions(@PathParam("groupId") String groupId,
                                                       @PathParam("artifactId") String artifactId,
                                                       @QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to force refresh of processed jar files, versions ,etc") boolean fullUpdate,
                                                       @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh its dependencies") boolean transitive)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS, ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS + groupId + artifactId, () -> artifactsRefreshService.refreshAllVersionsForProject(groupId, artifactId, fullUpdate,transitive,groupId + "-" + artifactId + ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS));
    }

    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/latest")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateProjectRevision(@PathParam("groupId") String groupId,
                                                 @PathParam("artifactId") String artifactId,
                                                 @QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to force refresh of processed jar files, versions ,etc") boolean fullUpdate,
                                                 @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh its dependencies") boolean transitive)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION, ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION + groupId + artifactId, () -> artifactsRefreshService.refreshMasterSnapshotForProject(groupId, artifactId,fullUpdate,transitive,groupId + "-" + artifactId + ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION));
    }

    @PUT
    @Path("/artifactsRefresh/versions")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateAllProjectsAllVersions(@QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to force refresh of processed jar files, versions ,etc") boolean fullUpdate,
                                                    @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh its dependencies") boolean transitive)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS, () ->
        {
            validateUser();
            return artifactsRefreshService.refreshAllVersionsForAllProjects(fullUpdate,transitive,ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS);
        });
    }

    @Override
    protected String getResourceName()
    {
        return ARTIFACTS_RESOURCE;
    }


    @PUT
    @Path("/artifactsRefresh/versions/missing")
    @ApiOperation(ResourceLoggingAndTracing.FIX_MISSING_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateMissingVersions()
    {
        return handle(ResourceLoggingAndTracing.FIX_MISSING_VERSIONS, () ->
        {
            validateUser();
            return this.artifactsRefreshService.refreshProjectsWithMissingVersions(ResourceLoggingAndTracing.FIX_MISSING_VERSIONS);
        });

    }
}

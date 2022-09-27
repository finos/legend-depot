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
import org.finos.legend.depot.domain.entity.VersionRevision;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.artifacts.domain.status.VersionMismatch;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
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
@Api("Artifacts")
public class ArtifactsResource extends BaseAuthorisedResource
{
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String ARTIFACTS_RESOURCE = "Artifacts";
    private final ArtifactsRefreshService artifactsRefreshService;
    private final ManageRefreshStatusService refreshStatusService;


    @Inject
    public ArtifactsResource(ArtifactsRefreshService artifactsRefreshService,
                             ManageRefreshStatusService updateStatusService,
                             AuthorisationProvider authorisationProvider,
                             @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.artifactsRefreshService = artifactsRefreshService;
        this.refreshStatusService = updateStatusService;
    }


    @GET
    @Path("/artifactsRefreshStatus")
    @ApiOperation("get updateStatusService information")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RefreshStatus> getStatus(
            @QueryParam("entityType") VersionRevision entityType,
            @QueryParam("project") String project,
            @QueryParam("groupId") String group,
            @QueryParam("artifactId") String artifact,
            @QueryParam("versionId") String version,
            @QueryParam("running") Boolean running,
            @QueryParam("startTimeFrom")
            @ApiParam("entries that started refresh from this date yyyy-MM-dd HH:mm:ss") String startTimeFrom,
            @QueryParam("startTimeTo")
            @ApiParam("entries that started refresh to this date yyyy-MM-dd HH:mm:ss (default is now)") String startTimeTo
    )
    {
        LocalDateTime fromStatTime = startTimeFrom == null ? null : LocalDateTime.parse(startTimeFrom, DATE_TIME_FORMATTER);
        LocalDateTime toStartTime = startTimeTo == null ? LocalDateTime.now() : LocalDateTime.parse(startTimeTo, DATE_TIME_FORMATTER);
        return handle(ResourceLoggingAndTracing.STORE_STATUS, () -> refreshStatusService.find(entityType, group, artifact, version, running,fromStatTime,toStartTime));
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
                                                      @PathParam("versionId") String versionId)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_VERSION, ResourceLoggingAndTracing.UPDATE_VERSION + groupId + artifactId + versionId, () -> artifactsRefreshService.refreshProjectVersionArtifacts(groupId, artifactId, versionId, true));
    }

    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/versions")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateProjectVersions(@PathParam("groupId") String groupId,
                                                       @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS, ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS + groupId + artifactId, () -> artifactsRefreshService.refreshProjectVersionsArtifacts(groupId, artifactId, true));
    }

    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/latest")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse refreshRevision(@PathParam("groupId") String groupId,
                                                 @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION, ResourceLoggingAndTracing.UPDATE_LATEST_PROJECT_REVISION + groupId + artifactId, () -> artifactsRefreshService.refreshProjectRevisionArtifacts(groupId, artifactId));
    }

    @PUT
    @Path("/artifactsRefresh/versions")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse updateAllVersions()
    {
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS, () ->
        {
            validateUser();
            return artifactsRefreshService.refreshAllProjectsVersionsArtifacts(true);
        });
    }

    @PUT
    @Path("/artifactsRefresh/latest")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_MASTER_REVISIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse refreshAllLatestRevisions()
    {
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_MASTER_REVISIONS, () ->
        {
            validateUser();
            return artifactsRefreshService.refreshAllProjectRevisionsArtifacts();
        });
    }

    @DELETE
    @Path("/artifactDelete/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse purgeVersion(@PathParam("groupId") String groupId,
                                              @PathParam("artifactId") String artifactId,
                                              @PathParam("versionId") String versionId)
    {

        return handle(ResourceLoggingAndTracing.PURGE_ALL_VERSIONS, () ->
        {
            validateUser();
            artifactsRefreshService.delete(groupId, artifactId, versionId);
            return new MetadataEventResponse();
        });
    }

    @PUT
    @Path("/artifactRefresh/indexes")
    @ApiOperation("createIndexes if absent")
    public boolean createIndexesIfAbsent()
    {
        return handle("Create indexes", this::createIndexes);
    }

    private boolean createIndexes()
    {
        validateUser();
        return artifactsRefreshService.createIndexesIfAbsent()
                && refreshStatusService.createIndexesIfAbsent();
    }

    @Override
    protected String getResourceName()
    {
        return ARTIFACTS_RESOURCE;
    }

    @GET
    @Path("/artifacts/{groupId}/{artifactId}/versions")
    @ApiOperation(ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getRepositoryVersions(@PathParam("groupId") String groupId,
                                                       @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS, ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS + groupId + artifactId, () -> artifactsRefreshService.getRepositoryVersions(groupId, artifactId));
    }

    @GET
    @Path("/artifacts/versions/mismatch")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CACHE_MISMATCHES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<VersionMismatch> getVersionMissMatches()
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_MISMATCHES, () -> this.artifactsRefreshService.findVersionsMismatches());
    }

    @PUT
    @Path("/artifacts/versions/mismatch")
    @ApiOperation(ResourceLoggingAndTracing.FIX_PROJECT_CACHE_MISMATCHES)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse fixVersionMissMatches()
    {
        return handle(ResourceLoggingAndTracing.FIX_PROJECT_CACHE_MISMATCHES, () ->
        {
            validateUser();
            return this.artifactsRefreshService.refreshProjectsVersionMismatches();
        });

    }
}

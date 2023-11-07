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
import io.swagger.annotations.ApiParam;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.finos.legend.depot.services.api.artifacts.refresh.ParentEvent;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.security.Principal;


@Path("")
@Api("Artifacts Refresh")
public class ArtifactsRefreshResource extends AuthorisedResource
{

    public static final String ARTIFACTS_RESOURCE = "ArtifactsRefresh";
    private final ArtifactsRefreshService artifactsRefreshService;

    @Inject
    public ArtifactsRefreshResource(ArtifactsRefreshService artifactsRefreshService,
                                    AuthorisationProvider authorisationProvider,
                                    @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.artifactsRefreshService = artifactsRefreshService;
    }


    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse updateProjectVersion(@PathParam("groupId") String groupId,
                                                             @PathParam("artifactId") String artifactId,
                                                             @PathParam("versionId") @ApiParam("a valid version string: x.y.z, master-SNAPSHOT") String versionId,
                                                             @QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to re-process unchanged SNAPSHOT jars") boolean fullUpdate,
                                                             @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh dependencies") boolean transitive)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.UPDATE_VERSION, ResourceLoggingAndTracing.UPDATE_VERSION + groupId + artifactId + versionId,
                () -> artifactsRefreshService.refreshVersionForProject(groupId, artifactId,versionId,fullUpdate,transitive, ParentEvent.build(groupId,artifactId,versionId, ParentEvent.UPDATE_PROJECT_VERSION.name())));
    }

    @PUT
    @Path("/artifactsRefresh/{groupId}/{artifactId}/versions")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse updateProjectAllVersions(@PathParam("groupId") String groupId,
                                                                 @PathParam("artifactId") String artifactId,
                                                                 @QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to re-process unchanged SNAPSHOT jars") boolean fullUpdate,
                                                                 @QueryParam("allVersions") @DefaultValue("false") @ApiParam("Whether to refresh all versions or just new") boolean allVersions,
                                                                 @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh dependencies") boolean transitive)
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS, ResourceLoggingAndTracing.UPDATE_ALL_PROJECT_VERSIONS + groupId + artifactId,
                () -> artifactsRefreshService.refreshAllVersionsForProject(groupId, artifactId, fullUpdate,allVersions,transitive, ParentEvent.build(groupId,artifactId,"ALL", ParentEvent.UPDATE_PROJECT_ALL_VERSIONS.name())));
    }


    @PUT
    @Path("/artifactsRefresh/versions")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse updateAllProjectsAllVersions(@QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to re-process unchanged SNAPSHOT jars") boolean fullUpdate,
                                                                     @QueryParam("allVersions") @DefaultValue("false") @ApiParam("Whether to refresh all versions or just new ones") boolean allVersions,
                                                                     @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh dependencies") boolean transitive)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_VERSIONS, () ->
        {
            validateUser();
            return artifactsRefreshService.refreshAllVersionsForAllProjects(fullUpdate,allVersions,transitive, ParentEvent.UPDATE_ALL_PROJECT_ALL_VERSIONS.name());
        });
    }

    @PUT
    @Path("/artifactsRefresh/snapshots")
    @ApiOperation(ResourceLoggingAndTracing.UPDATE_ALL_SNAPSHOTS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse updateAllProjectsMaster(@QueryParam("fullUpdate") @DefaultValue("false") @ApiParam("Whether to re-process unchanged SNAPSHOT jars") boolean fullUpdate,
                                                                @QueryParam("transitive") @DefaultValue("false") @ApiParam("Whether to refresh dependencies") boolean transitive)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_ALL_SNAPSHOTS, () ->
        {
            validateUser();
            return artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(fullUpdate,transitive, ParentEvent.UPDATE_ALL_PROJECT_ALL_SNAPSHOTS.name());
        });
    }

    @Override
    protected String getResourceName()
    {
        return ARTIFACTS_RESOURCE;
    }
}

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
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.Arrays;


@Path("")
@Api("Artifacts Purge")
public class ArtifactsPurgeResource extends AuthorisedResource
{
    public static final String ARTIFACTS_RESOURCE = "ArtifactsPurge";
    private final ArtifactsPurgeService artifactsPurgeService;

    @Inject
    public ArtifactsPurgeResource(ArtifactsPurgeService artifactsPurgeService,
                                  AuthorisationProvider authorisationProvider,
                                  @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.artifactsPurgeService = artifactsPurgeService;
    }


    @DELETE
    @Path("/artifactEviction/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.EVICT_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse evictVersion(@PathParam("groupId") String groupId,
                                                     @PathParam("artifactId") String artifactId,
                                                     @PathParam("versionId") @ApiParam("a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {

        return handle(ResourceLoggingAndTracing.EVICT_VERSION, () ->
        {
            validateUser();
            artifactsPurgeService.evict(groupId, artifactId, versionId);
            return new MetadataNotificationResponse();
        });
    }

    @DELETE
    @Path("/artifactDelete/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse deleteVersion(@PathParam("groupId") String groupId,
                                                      @PathParam("artifactId") String artifactId,
                                                      @PathParam("versionId") @ApiParam("a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {

        return handle(ResourceLoggingAndTracing.DELETE_VERSION, () ->
        {
            validateUser();
            artifactsPurgeService.delete(groupId, artifactId, versionId);
            return new MetadataNotificationResponse();
        });
    }

    @GET
    @Path("/artifactDelete/{groupId}/{artifactId}/snapshotVersions/{versions}")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_SNAPSHOT_VERSIONS)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteSnapShotVersion(@PathParam("groupId") String groupId,
                                          @PathParam("artifactId") String artifactId,
                                          @PathParam("versions") @ApiParam("a list of valid snapshot versions separated by ,") String versions)
    {
        return handle(ResourceLoggingAndTracing.DELETE_SNAPSHOT_VERSIONS, () -> artifactsPurgeService.deleteSnapshotVersions(groupId, artifactId, Arrays.asList(versions.split(","))));
    }

    @DELETE
    @Path("/artifactEviction/{groupId}/{artifactId}/old/{keepVersions}")
    @ApiOperation(ResourceLoggingAndTracing.EVICT_OLD_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse evictOldVersions(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("keepVersions") int versionsToKeep)
    {

        return handle(ResourceLoggingAndTracing.EVICT_OLD_VERSIONS, () ->
        {
            validateUser();
            return artifactsPurgeService.evictOldestProjectVersions(groupId, artifactId,versionsToKeep);
        });
    }

    @DELETE
    @Path("/artifactDeprecate/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.DEPRECATE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse deprecateVersion(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("versionId") @ApiParam("a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {

        return handle(ResourceLoggingAndTracing.DEPRECATE_VERSION, () ->
        {
            validateUser();
            return artifactsPurgeService.deprecate(groupId, artifactId, versionId);
        });
    }

    @DELETE
    @Path("/artifactEviction/versions/notUsed")
    @ApiOperation(ResourceLoggingAndTracing.EVICT_VERSIONS_NOT_USED)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse evictVersionsNotUsed()
    {

        return handle(ResourceLoggingAndTracing.EVICT_VERSIONS_NOT_USED, () ->
        {
            validateUser();
            return artifactsPurgeService.evictVersionsNotUsed();
        });
    }

    @Override
    protected String getResourceName()
    {
        return ARTIFACTS_RESOURCE;
    }

}

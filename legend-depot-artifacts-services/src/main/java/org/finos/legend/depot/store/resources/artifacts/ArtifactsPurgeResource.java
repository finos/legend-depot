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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;


@Path("")
@Tag(name = "Artifacts Purge")
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
    @Operation(summary = ResourceLoggingAndTracing.EVICT_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse evictVersion(@PathParam("groupId") String groupId,
                                                     @PathParam("artifactId") String artifactId,
                                                     @PathParam("versionId") @Parameter(description = "a valid version string: x.y.z, master-SNAPSHOT") String versionId)
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
    @Operation(summary = ResourceLoggingAndTracing.DELETE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse deleteVersion(@PathParam("groupId") String groupId,
                                                      @PathParam("artifactId") String artifactId,
                                                      @PathParam("versionId") @Parameter(description = "a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {

        return handle(ResourceLoggingAndTracing.DELETE_VERSION, () ->
        {
            validateUser();
            artifactsPurgeService.delete(groupId, artifactId, versionId);
            return new MetadataNotificationResponse();
        });
    }

    @DELETE
    @Path("/artifactDelete/{groupId}/{artifactId}/snapshotVersions")
    @Operation(summary = ResourceLoggingAndTracing.DELETE_SNAPSHOT_VERSIONS)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteSnapshotVersion(@PathParam("groupId") String groupId,
                                        @PathParam("artifactId") String artifactId,
                                        @RequestBody(description = "snapshotVersions") List<String> snapshotVersions)
    {
        return handle(ResourceLoggingAndTracing.DELETE_SNAPSHOT_VERSIONS, () -> artifactsPurgeService.deleteSnapshotVersions(groupId, artifactId, snapshotVersions));
    }

    @DELETE
    @Path("/artifactEviction/{groupId}/{artifactId}/old/{keepVersions}")
    @Operation(summary = ResourceLoggingAndTracing.EVICT_OLD_VERSIONS)
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
    @Operation(summary = ResourceLoggingAndTracing.DEPRECATE_VERSION)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse deprecateVersion(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("versionId") @Parameter(description = "a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {

        return handle(ResourceLoggingAndTracing.DEPRECATE_VERSION, () ->
        {
            validateUser();
            return artifactsPurgeService.deprecate(groupId, artifactId, versionId);
        });
    }

    @DELETE
    @Path("/artifactEviction/versions/notUsed")
    @Operation(summary = ResourceLoggingAndTracing.EVICT_VERSIONS_NOT_USED)
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

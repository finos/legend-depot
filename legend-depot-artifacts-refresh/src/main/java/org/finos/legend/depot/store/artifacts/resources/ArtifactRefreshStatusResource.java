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
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.artifacts.api.ParentEvent;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

import static org.finos.legend.depot.store.artifacts.resources.ArtifactsRefreshResource.ARTIFACTS_RESOURCE;

@Path("")
@Api("Artifacts Refresh")
public class ArtifactRefreshStatusResource extends BaseAuthorisedResource
{
    private final RefreshStatusStore refreshStatusService;

    @Override
    protected String getResourceName()
    {
        return ARTIFACTS_RESOURCE;
    }

    @Inject
    public ArtifactRefreshStatusResource(RefreshStatusStore refreshStatusStore,
                                    AuthorisationProvider authorisationProvider,
                                    @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.refreshStatusService = refreshStatusStore;
    }

    @GET
    @Path("/artifactsRefresh/status")
    @ApiOperation("refresh status")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RefreshStatus> getRunningVersionsRefresh(
            @QueryParam("groupId") String group,
            @QueryParam("artifactId") String artifact,
            @QueryParam("versionId") @ApiParam("x.y.z/master-SNAPSHOT") String version,
            @QueryParam("eventId") String eventId,
            @QueryParam("parentEventId") @ApiParam("refresh could be started by another event") ParentEvent parentId
    )
    {
        return handle(ResourceLoggingAndTracing.STORE_STATUS, () -> refreshStatusService.find(group, artifact, version,eventId,parentId != null ? parentId.name() : null));
    }
}

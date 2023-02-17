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
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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

import static org.finos.legend.depot.store.artifacts.resources.ArtifactsRefreshResource.ARTIFACTS_RESOURCE;

@Path("")
@Api("Artifacts Refresh")
public class ArtifactRefreshStatusResource extends BaseAuthorisedResource
{
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
    @ApiOperation("delete by id")
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
}

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

package org.finos.legend.depot.store.status.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.store.status.domain.StoreStatus;
import org.finos.legend.depot.store.status.services.StoreStatusService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
@Api("Store Status")
public class StatusStoreResource extends BaseResource
{

    private final StoreStatusService statusService;

    @Inject
    public StatusStoreResource(StoreStatusService statusService)
    {
        this.statusService = statusService;
    }

    @GET
    @Path("/status")
    @ApiOperation(ResourceLoggingAndTracing.GET_CACHE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreStatus getStatus()
    {
        return handle(ResourceLoggingAndTracing.GET_CACHE_STATUS, this.statusService::getStatus);
    }

    @GET
    @Path("/status/counts")
    @ApiOperation(ResourceLoggingAndTracing.GET_CACHE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreStatus.DocumentCounts getCounts()
    {
        return handle(ResourceLoggingAndTracing.GET_CACHE_STATUS, this.statusService::getDocumentCounts);
    }

    @GET
    @Path("/status/counts/{groupId}/{artifactId}/version/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public StoreStatus.DocumentCounts getDeprecatedVersionCounts(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS, () -> this.statusService.getDocumentCounts(groupId, artifactId, versionId));
    }

    @GET
    @Path("/status/counts/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreStatus.DocumentCounts getVersionCounts(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS, () -> this.statusService.getDocumentCounts(groupId, artifactId, versionId));
    }

    @GET
    @Path("/status/counts/{groupId}/{artifactId}/latest")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreStatus.DocumentCounts getRevisionCounts(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS, () -> this.statusService.getRevisionDocumentCounts(groupId, artifactId));
    }

    @GET
    @Path("/status/{groupId}/{artifactId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public StoreStatus.ProjectStatus getProjectStatus(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_CACHE_STATUS, () -> this.statusService.getProjectStatus(groupId, artifactId));
    }



}

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

package org.finos.legend.depot.store.admin.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.store.admin.api.ManageSchedulesService;
import org.finos.legend.depot.store.admin.services.schedules.ScheduleInfo;
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
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
import java.util.List;

@Path("")
@Api("Schedules")
public class SchedulesResource extends BaseAuthorisedResource
{

    public static final String SCHEDULES_RESOURCE = "Schedules";
    private final SchedulesFactory schedulesFactory;
    private final ManageSchedulesService manageSchedulesService;

    @Inject
    protected SchedulesResource(AuthorisationProvider authorisationProvider,
                                @Named("requestPrincipal") Provider<Principal> principalProvider, SchedulesFactory schedulesFactory, ManageSchedulesService manageSchedulesService)
    {
        super(authorisationProvider, principalProvider);
        this.schedulesFactory = schedulesFactory;
        this.manageSchedulesService = manageSchedulesService;
    }

    @Override
    protected String getResourceName()
    {
        return SCHEDULES_RESOURCE;
    }

    @GET
    @Path("/schedules")
    @ApiOperation(ResourceLoggingAndTracing.SCHEDULES_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScheduleInfo> getSchedulerStatus(@QueryParam("running") Boolean running,
                                                 @QueryParam("disabled") Boolean disabled)
    {
        return handle(ResourceLoggingAndTracing.SCHEDULES_STATUS,() -> this.schedulesFactory.find(running,disabled));
    }

    @PUT
    @Path("/schedules/{jobId}")
    @ApiOperation(ResourceLoggingAndTracing.TRIGGER_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forceScheduler(@PathParam("jobId") String jobId)
    {
        return handle(ResourceLoggingAndTracing.TRIGGER_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.run(jobId);
            return Response.noContent().build();
        });
    }

    @DELETE
    @Path("/schedules/{jobId}")
    @ApiOperation(ResourceLoggingAndTracing.TRIGGER_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteScheduler(@PathParam("jobId") String jobId)
    {
        return handle(ResourceLoggingAndTracing.TRIGGER_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.deRegister(jobId);
            return Response.noContent().build();
        });
    }

    @PUT
    @Path("/schedules")
    @ApiOperation(ResourceLoggingAndTracing.TRIGGER_SCHEDULE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSchedule(ScheduleInfo info)
    {
        return handle(ResourceLoggingAndTracing.UPDATE_SCHEDULE, () ->
        {
            validateUser();
            manageSchedulesService.createOrUpdate(info);
            return Response.noContent().build();
        });
    }

    @PUT
    @Path("/schedules/{jobId}/disable/{toggle}")
    @ApiOperation(ResourceLoggingAndTracing.TOGGLE_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleScheduler(@PathParam("jobId") String jobId, @PathParam("toggle") boolean toggle)
    {
        return handle(ResourceLoggingAndTracing.TOGGLE_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.toggleDisable(jobId, toggle);
            return Response.noContent().build();
        });
    }

    @PUT
    @Path("/schedules/{jobId}/running/{toggle}")
    @ApiOperation(ResourceLoggingAndTracing.TOGGLE_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleRunningScheduler(@PathParam("jobId") String jobId, @PathParam("toggle") boolean toggle)
    {
        return handle(ResourceLoggingAndTracing.TOGGLE_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.toggleRunning(jobId, toggle);
            return Response.noContent().build();
        });
    }

    @PUT
    @Path("/schedules/all/disable/{toggle}")
    @ApiOperation(ResourceLoggingAndTracing.TOGGLE_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleScheduler(@PathParam("toggle") boolean toggle)
    {
        return handle(ResourceLoggingAndTracing.TOGGLE_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.toggleDisableAll(toggle);
            return Response.noContent().build();
        });
    }


}

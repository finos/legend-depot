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

package org.finos.legend.depot.schedules.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.store.admin.api.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.admin.api.schedules.SchedulesStore;
import org.finos.legend.depot.store.admin.domain.schedules.ScheduleInfo;
import org.finos.legend.depot.store.admin.domain.schedules.ScheduleInstance;
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
import java.util.List;
import java.util.stream.Collectors;

@Path("")
@Api("Schedules")
public class SchedulesResource extends BaseAuthorisedResource
{

    public static final String SCHEDULES_RESOURCE = "Schedules";
    private final SchedulesFactory schedulesFactory;
    private final SchedulesStore schedulesStore;
    private final ScheduleInstancesStore scheduleInstancesStore;

    @Inject
    protected SchedulesResource(AuthorisationProvider authorisationProvider,
                                @Named("requestPrincipal") Provider<Principal> principalProvider, SchedulesFactory schedulesFactory, SchedulesStore manageSchedulesService, ScheduleInstancesStore scheduleInstancesStore)
    {
        super(authorisationProvider, principalProvider);
        this.schedulesFactory = schedulesFactory;
        this.schedulesStore = manageSchedulesService;
        this.scheduleInstancesStore = scheduleInstancesStore;
    }

    @Override
    protected String getResourceName()
    {
        return SCHEDULES_RESOURCE;
    }

    @GET
    @Path("/schedules")
    @ApiOperation(value = ResourceLoggingAndTracing.SCHEDULES_STATUS, notes = "Toggle to true for checking disabled schedules or toggle to false for checking enabled schedules")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScheduleInfo> getSchedulerStatus(@QueryParam("disabled") @DefaultValue("false") Boolean disabled)
    {

        return handle(ResourceLoggingAndTracing.SCHEDULES_STATUS,() ->
        {
            validateUser();
            return this.schedulesStore.getAll().stream().filter(s -> disabled == null || s.disabled == disabled.booleanValue()).collect(Collectors.toList());
        });
    }


    @GET
    @Path("/scheduleInstances")
    @ApiOperation(ResourceLoggingAndTracing.SCHEDULES_RUNS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScheduleInstance> getSchedulerInstances()
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.SCHEDULES_RUNS,() -> this.scheduleInstancesStore.getAll());
    }

    @PUT
    @Path("/schedules/{scheduleName}")
    @ApiOperation(ResourceLoggingAndTracing.TRIGGER_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forceScheduler(@PathParam("scheduleName") String scheduleName)
    {
        return handle(ResourceLoggingAndTracing.TRIGGER_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.trigger(scheduleName);
            return Response.noContent().build();
        });
    }

    @DELETE
    @Path("/schedules/{scheduleName}")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteScheduler(@PathParam("scheduleName") String scheduleName)
    {
        return handle(ResourceLoggingAndTracing.DELETE_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.deRegister(scheduleName);
            return Response.noContent().build();
        });
    }

    @DELETE
    @Path("/schedules/")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_SCHEDULES)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSchedules()
    {
        return handle(ResourceLoggingAndTracing.DELETE_SCHEDULES, () ->
        {
            validateUser();
            schedulesFactory.deRegisterAll();
            return Response.noContent().build();
        });
    }

    @PUT
    @Path("/schedules/{scheduleName}/disable/{toggle}")
    @ApiOperation(value = ResourceLoggingAndTracing.TOGGLE_SCHEDULE, notes = "Toggle to true for disabling the schedule mentioned or toggle to false for enabling the schedule")
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleScheduler(@PathParam("scheduleName") String scheduleName, @PathParam("toggle") boolean toggle)
    {
        return handle(ResourceLoggingAndTracing.TOGGLE_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.toggleDisable(scheduleName, toggle);
            return Response.noContent().build();
        });
    }


    @PUT
    @Path("/schedules/all/disable/{toggle}")
    @ApiOperation(value = ResourceLoggingAndTracing.TOGGLE_SCHEDULES, notes = "Toggle to true for disabling all schedules or toggle to false for enabling all schedules")
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleScheduler(@PathParam("toggle") boolean toggle)
    {
        return handle(ResourceLoggingAndTracing.TOGGLE_SCHEDULES, () ->
        {
            validateUser();
            schedulesFactory.toggleDisableAll(toggle);
            return Response.noContent().build();
        });
    }

}

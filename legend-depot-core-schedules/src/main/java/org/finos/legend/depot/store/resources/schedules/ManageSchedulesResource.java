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

package org.finos.legend.depot.store.resources.schedules;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.store.api.admin.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.api.admin.schedules.SchedulesStore;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInfo;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Path("")
@Tag(name = "Schedules")
public class ManageSchedulesResource extends AuthorisedResource
{

    public static final String SCHEDULES_RESOURCE = "Schedules";
    private final SchedulesFactory schedulesFactory;
    private final SchedulesStore schedulesStore;
    private final ScheduleInstancesStore scheduleInstancesStore;

    @Inject
    protected ManageSchedulesResource(AuthorisationProvider authorisationProvider,
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
    @Operation(summary = ResourceLoggingAndTracing.SCHEDULES_STATUS, description = "Toggle to true for checking disabled schedules or toggle to false for checking enabled schedules")
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
    @Operation(summary = ResourceLoggingAndTracing.SCHEDULES_RUNS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ScheduleInstance> getSchedulerInstances()
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.SCHEDULES_RUNS,() -> this.scheduleInstancesStore.getAll());
    }

    @PUT
    @Path("/schedules/{scheduleName}")
    @Operation(summary = ResourceLoggingAndTracing.TRIGGER_SCHEDULE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forceScheduler(@PathParam("scheduleName") String scheduleName,
                                   @QueryParam("forceRun") @DefaultValue("false") @Parameter(description = "Whether to run the schedule if disabled") boolean forceRun)
    {
        return handle(ResourceLoggingAndTracing.TRIGGER_SCHEDULE, () ->
        {
            validateUser();
            schedulesFactory.trigger(scheduleName, forceRun);
            return Response.noContent().build();
        });
    }

    @DELETE
    @Path("/schedules/{scheduleName}")
    @Operation(summary = ResourceLoggingAndTracing.DELETE_SCHEDULE)
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
    @Operation(summary = ResourceLoggingAndTracing.DELETE_SCHEDULES)
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
    @Operation(summary = ResourceLoggingAndTracing.TOGGLE_SCHEDULE, description = "Toggle to true for disabling the schedule mentioned or toggle to false for enabling the schedule")
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
    @Operation(summary = ResourceLoggingAndTracing.TOGGLE_SCHEDULES, description = "Toggle to true for disabling all schedules or toggle to false for enabling all schedules")
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

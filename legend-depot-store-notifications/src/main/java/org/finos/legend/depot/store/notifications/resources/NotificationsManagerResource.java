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

package org.finos.legend.depot.store.notifications.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.notifications.api.Notifications;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.store.notifications.domain.RefreshAllMetadataNotification;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Path("")
@Api("Notifications")
public class NotificationsManagerResource extends BaseAuthorisedResource
{

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Notifications eventsApi;
    private final Queue queue;
    private final ManageProjectsService projectsService;

    @Inject
    protected NotificationsManagerResource(ManageProjectsService projectsService, Notifications events, Queue queue, AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.projectsService = projectsService;
        this.eventsApi = events;
        this.queue = queue;
    }

    @Override
    protected String getResourceName()
    {
        return "Notifications";
    }

    @GET
    @Path("/notifications")
    @ApiOperation(ResourceLoggingAndTracing.FIND_PAST_EVENTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getAllEvents(@QueryParam("from")
                                                   @ApiParam("query from this date yyyy-MM-dd HH:mm:ss (default is 30 minutes prior)") String from,
                                                   @QueryParam("to")
                                                   @ApiParam("include  up to this date yyyy-MM-dd HH:mm:ss (default is now)") String to)
    {
        return handle(ResourceLoggingAndTracing.FIND_PAST_EVENTS, () ->
                eventsApi.find(from == null ? null : LocalDateTime.parse(from, DATE_TIME_FORMATTER),
                        to == null ? null : LocalDateTime.parse(to, DATE_TIME_FORMATTER)));
    }

    @GET
    @Path("/queuesRefreshAllVersions")
    @ApiOperation(ResourceLoggingAndTracing.ENQUEUE_REFRESH_ALL_EVENT)
    @Produces(MediaType.TEXT_PLAIN)
    public String queueRefreshAllEvent()
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.ENQUEUE_REFRESH_ALL_EVENT, () -> queue.push(new RefreshAllMetadataNotification()));
    }

    @GET
    @Path("/queue")
    @ApiOperation(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getAllEventsInQueue()
    {
        return handle(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE, queue::getAll);
    }


    @GET
    @Path("/queue/{projectId}/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.ENQUEUE_EVENT)
    @Produces(MediaType.TEXT_PLAIN)
    public String queueEvent(@PathParam("projectId") String projectId,
                             @PathParam("groupId") String groupId,
                             @PathParam("artifactId") String artifactId,
                             @PathParam("versionId") String versionId,
                             @QueryParam("maxRetries")
                             @DefaultValue("5")
                             @ApiParam("Whether to retry operation if it fails") int maxRetries)
    {
        return handle(ResourceLoggingAndTracing.ENQUEUE_EVENT, () -> pushToQueue(projectId, groupId, artifactId, versionId, maxRetries));
    }

    private void validateMavenCoordinates(String projectId, String groupId, String artifactId)
    {
        Optional<ProjectData> project = projectsService.find(groupId, artifactId);
        if (project.isPresent() && !project.get().getProjectId().equals(projectId))
        {
            throw new IllegalArgumentException(String.format("%s:%s coordinates already registered with project %s", groupId, artifactId, project.get().getProjectId()));
        }
    }


    protected String pushToQueue(String projectId, String groupId, String artifactId, String versionId, int maxRetries)
    {
        validateMavenCoordinates(projectId, groupId, artifactId);
        MetadataNotification event = new MetadataNotification(projectId, groupId, artifactId, versionId, maxRetries);
        return queue.push(event);
    }


}

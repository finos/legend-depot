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
import org.finos.legend.depot.store.notifications.api.NotificationsManager;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Path("")
@Api("Notifications")
public class NotificationsManagerResource extends BaseResource
{

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final NotificationsManager notificationsManager;

    @Inject
    protected NotificationsManagerResource(NotificationsManager notificationsManager)
    {
        super();
        this.notificationsManager = notificationsManager;
    }

    @GET
    @Path("/notifications")
    @ApiOperation(ResourceLoggingAndTracing.FIND_PAST_EVENTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getPastEventNotifications(
                                                   @QueryParam("groupId") String group,
                                                   @QueryParam("artifactId") String artifact,
                                                   @QueryParam("versionId") String version,
                                                   @QueryParam("parentEventId") @ApiParam("refresh could be started by another event, eg refresh all cache versions") String parentId,
                                                   @QueryParam("success") Boolean success,
                                                   @QueryParam("from")
                                                   @ApiParam("query from this date yyyy-MM-dd HH:mm:ss (default is 30 minutes prior)") String from,
                                                   @QueryParam("to")
                                                   @ApiParam("include  up to this date yyyy-MM-dd HH:mm:ss (default is now)") String to)
    {
        return handle(ResourceLoggingAndTracing.FIND_PAST_EVENTS, () ->
                notificationsManager.findProcessedEvents(group,artifact,version,parentId,success,from == null ? null : LocalDateTime.parse(from, DATE_TIME_FORMATTER),
                        to == null ? null : LocalDateTime.parse(to, DATE_TIME_FORMATTER)));
    }

    @GET
    @Path("/notifications/{eventId}")
    @ApiOperation(ResourceLoggingAndTracing.FIND_EVENT_BY_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<MetadataNotification> getNotificationById(@PathParam("eventId") String eventId)
    {
        return handle(ResourceLoggingAndTracing.FIND_EVENT_BY_ID, () -> notificationsManager.getProcessedEvent(eventId));
    }

    @GET
    @Path("/queue")
    @ApiOperation(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getAllEventsInQueue()
    {
        return handle(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE, notificationsManager::getAllInQueue);
    }

    @GET
    @Path("/queue/{eventId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_EVENT_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<MetadataNotification> geEventsInQueue(@PathParam("eventId") String eventId)
    {
        return handle(ResourceLoggingAndTracing.GET_EVENT_IN_QUEUE, () -> this.notificationsManager.findInQueue(eventId));
    }


    @GET
    @Path("/queue/{projectId}/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.ENQUEUE_EVENT)
    @Produces(MediaType.TEXT_PLAIN)
    public String queueEvent(@PathParam("projectId") String projectId,
                             @PathParam("groupId") String groupId,
                             @PathParam("artifactId") String artifactId,
                             @PathParam("versionId") String versionId)
    {
        return handle(ResourceLoggingAndTracing.ENQUEUE_EVENT, () -> notificationsManager.notify(projectId, groupId, artifactId, versionId));
    }

}

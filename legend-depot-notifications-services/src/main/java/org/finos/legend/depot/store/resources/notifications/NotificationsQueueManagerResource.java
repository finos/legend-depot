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

package org.finos.legend.depot.store.resources.notifications;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;
import org.finos.legend.depot.domain.notifications.LakehouseMetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.notifications.NotificationsQueueManager;

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
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Path("")
@Api("Notifications Queue")
public class NotificationsQueueManagerResource extends AuthorisedResource
{

    private final NotificationsQueueManager notificationsManager;
    private final Queue queue;

    @Inject
    protected NotificationsQueueManagerResource(NotificationsQueueManager notificationsManager,
                                                AuthorisationProvider authorisationProvider,
                                                @Named("requestPrincipal") Provider<Principal> principalProvider, Queue queue)
    {
        super(authorisationProvider, principalProvider);
        this.notificationsManager = notificationsManager;

        this.queue = queue;
    }


    @Override
    protected String getResourceName()
    {
        return "Notifications";
    }


    @GET
    @Path("/notifications-queue")
    @ApiOperation(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getAllEventsInQueue()
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE, queue::getAll);
    }

    @GET
    @Path("/notifications-queue/count")
    @ApiOperation(ResourceLoggingAndTracing.GET_QUEUE_COUNT)
    @Produces(MediaType.APPLICATION_JSON)
    public long getAllEventsInQueueCount()
    {
        return handle(ResourceLoggingAndTracing.GET_QUEUE_COUNT, () -> this.queue.size());
    }

    @GET
    @Path("/notifications-queue/{eventId}")
    @ApiOperation(ResourceLoggingAndTracing.GET_EVENT_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<MetadataNotification> geEventsInQueue(@PathParam("eventId") String eventId)
    {
        return handle(ResourceLoggingAndTracing.GET_EVENT_IN_QUEUE, () -> this.queue.get(eventId));
    }


    @GET
    @Path("/queue/{projectId}/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(ResourceLoggingAndTracing.ENQUEUE_EVENT)
    @Produces(MediaType.TEXT_PLAIN)
    public String queueEvent(@PathParam("projectId") String projectId,
                             @PathParam("groupId") String groupId,
                             @PathParam("artifactId") String artifactId,
                             @PathParam("versionId") @ApiParam("a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {
        return handle(ResourceLoggingAndTracing.ENQUEUE_EVENT, () -> notificationsManager.notify(projectId, groupId, artifactId, versionId));
    }

    @PUT
    @Path("/queue/lakehouse")
    @ApiOperation("store lakehouse curated elements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse queueLakehouseNotification(LakehouseMetadataNotification notification)
    {
        return this.notificationsManager.handleLakehouseMetadataNotification(notification);
    }

    @DELETE
    @Path("/notifications-queue")
    @ApiOperation("purge queue")
    public long purgeQueue()
    {
        validateUser();
        return handle("purge queue", () -> this.queue.deleteAll());
    }
}

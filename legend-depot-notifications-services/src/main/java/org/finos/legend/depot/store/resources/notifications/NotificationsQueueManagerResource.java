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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;
import org.finos.legend.depot.domain.notifications.RestMetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.notifications.NotificationsQueueManager;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Path("")
@Tag(name = "Notifications Queue")
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
    @Operation(summary = ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getAllEventsInQueue()
    {
        validateUser();
        return handle(ResourceLoggingAndTracing.GET_ALL_EVENTS_IN_QUEUE, queue::getAll);
    }

    @GET
    @Path("/notifications-queue/count")
    @Operation(summary = ResourceLoggingAndTracing.GET_QUEUE_COUNT)
    @Produces(MediaType.APPLICATION_JSON)
    public long getAllEventsInQueueCount()
    {
        return handle(ResourceLoggingAndTracing.GET_QUEUE_COUNT, () -> this.queue.size());
    }

    @GET
    @Path("/notifications-queue/{eventId}")
    @Operation(summary = ResourceLoggingAndTracing.GET_EVENT_IN_QUEUE)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<MetadataNotification> geEventsInQueue(@PathParam("eventId") String eventId)
    {
        return handle(ResourceLoggingAndTracing.GET_EVENT_IN_QUEUE, () -> this.queue.get(eventId));
    }


    @GET
    @Path("/queue/{projectId}/{groupId}/{artifactId}/{versionId}")
    @Operation(summary = ResourceLoggingAndTracing.ENQUEUE_EVENT)
    @Produces(MediaType.TEXT_PLAIN)
    public String queueEvent(@PathParam("projectId") String projectId,
                             @PathParam("groupId") String groupId,
                             @PathParam("artifactId") String artifactId,
                             @PathParam("versionId") @Parameter(description = "a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {
        return handle(ResourceLoggingAndTracing.ENQUEUE_EVENT, () -> notificationsManager.notify(projectId, groupId, artifactId, versionId));
    }

    @PUT
    @Path("/queue/rest/metadata")
    @Operation(summary = "store rest curated elements")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataNotificationResponse queueRestNotification(RestMetadataNotification notification)
    {
        return this.notificationsManager.handleRestMetadataNotification(notification);
    }

    @DELETE
    @Path("/notifications-queue")
    @Operation(summary = "purge queue")
    public long purgeQueue()
    {
        validateUser();
        return handle("purge queue", () -> this.queue.deleteAll());
    }
}

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
import org.finos.legend.depot.domain.DatesHandler;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;
import org.finos.legend.depot.services.api.notifications.NotificationsService;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Path("")
@Tag(name = "Notifications")
public class NotificationsResource extends AuthorisedResource
{

    private final NotificationsService notificationsService;

    @Inject
    protected NotificationsResource(NotificationsService notificationsManager,
                                    AuthorisationProvider authorisationProvider,
                                    @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.notificationsService = notificationsManager;

    }

    NotificationsResource(NotificationsService notificationsManager)
    {
        super(null,null);
        this.notificationsService = notificationsManager;
    }

    @Override
    protected String getResourceName()
    {
        return "Notifications";
    }

    @GET
    @Path("/notifications")
    @Operation(summary = ResourceLoggingAndTracing.FIND_PAST_EVENTS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<MetadataNotification> getPastEventNotifications(
                                                   @QueryParam("groupId") String group,
                                                   @QueryParam("artifactId") String artifact,
                                                   @QueryParam("versionId") String version,
                                                   @QueryParam("eventId") String eventId,
                                                   @QueryParam("parentEventId") @Parameter(description = "refresh could be started by another event, eg refresh all store versions") String parentId,
                                                   @QueryParam("success") Boolean success,
                                                   @QueryParam("from")
                                                   @Parameter(description = "last updated from date: yyyy-MM-dd HH:mm:ss/unix epoc millis (default is 120 minutes prior)") String from,
                                                   @QueryParam("to")
                                                   @Parameter(description = "to date: yyyy-MM-dd HH:mm:ss/unix epoc millis (default is now)") String to)
    {
        return handle(ResourceLoggingAndTracing.FIND_PAST_EVENTS, () -> notificationsService.findProcessedEvents(group,artifact,version,eventId,parentId,success,
                from == null ?  LocalDateTime.now().minusMinutes(120) : DatesHandler.parseDate(from),
                to == null ? LocalDateTime.now() : DatesHandler.parseDate(to)));
    }


    @GET
    @Path("/notifications/{eventId}")
    @Operation(summary = ResourceLoggingAndTracing.FIND_EVENT_BY_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<MetadataNotification> getNotificationById(@PathParam("eventId") String eventId)
    {
        return handle(ResourceLoggingAndTracing.FIND_EVENT_BY_ID, () -> notificationsService.getProcessedEvent(eventId));
    }
}

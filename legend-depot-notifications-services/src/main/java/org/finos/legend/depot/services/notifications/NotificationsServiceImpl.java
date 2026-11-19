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

package org.finos.legend.depot.services.notifications;

import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.services.api.notifications.NotificationsService;
import org.finos.legend.depot.store.api.notifications.Notifications;
import org.slf4j.Logger;

import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class NotificationsServiceImpl implements NotificationsService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationsServiceImpl.class);

    private final Notifications notifications;

    @Inject
    public NotificationsServiceImpl(Notifications events)
    {
        this.notifications = events;
    }


    @Override
    public List<MetadataNotification> findProcessedEvents(String group, String artifact, String version, String eventId,String parentId, Boolean success, LocalDateTime from, LocalDateTime to)
    {
        return this.notifications.find(group,artifact,version,eventId,parentId,success,from,to);
    }

    @Override
    public Optional<MetadataNotification> getProcessedEvent(String eventId)
    {
        return this.notifications.get(eventId);
    }


    @Override
    public long deleteOldNotifications(long days)
    {
        LocalDateTime timeToLive = LocalDateTime.now().minusDays(days);
        List<MetadataNotification> notifications = this.notifications.find(null,null,null,null,null,null,null,timeToLive);
        notifications.forEach(notification -> this.notifications.delete(notification.getId()));
        LOGGER.info("deleted [{}] notifications older than [{}] days",notifications.size(),days);
        return notifications.size();
    }
}

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

package org.finos.legend.depot.store.notifications.api;

import org.finos.legend.depot.store.notifications.domain.MetadataNotification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface NotificationsManager
{
    int handle();

    List<MetadataNotification> findProcessedEvents(String group, String artifact, String version,String eventId, String parentId, Boolean success, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    Optional<MetadataNotification> getProcessedEvent(String eventId);

    List<MetadataNotification> getAllInQueue();

    Optional<MetadataNotification> findInQueue(String eventId);

    String notify(String projectId, String groupId, String artifactId, String versionId);

    long deleteOldNotifications(long days);

    long waitingInQueue();

    long purgeQueue();
}

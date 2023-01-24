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

public interface Notifications
{

    List<MetadataNotification> getAll();

    Optional<MetadataNotification> get(String notificationId);

    List<MetadataNotification> find(String group, String artifact, String version, String parentId, Boolean success, LocalDateTime fromDate, LocalDateTime toDate);

    void insert(MetadataNotification metadataEvent);

    void complete(MetadataNotification metadataEvent);

    void delete(String id);
}

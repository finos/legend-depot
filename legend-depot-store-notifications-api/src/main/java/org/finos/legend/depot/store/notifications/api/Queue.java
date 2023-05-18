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

import org.finos.legend.depot.domain.notifications.MetadataNotification;

import java.util.List;
import java.util.Optional;

public interface Queue
{

    List<MetadataNotification> getAll();

    List<MetadataNotification> pullAll();

    Optional<MetadataNotification> getFirstInQueue();

    Optional<MetadataNotification> get(String eventId);

    String push(MetadataNotification metadataEvent);

    long size();

    long deleteAll();
}

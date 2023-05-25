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

package org.finos.legend.depot.store.notifications.queue.store;

import com.google.inject.Inject;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.notifications.queue.api.Queue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VoidNotificationsQueueStore implements Queue
{

    @Inject
    public VoidNotificationsQueueStore()
    {
    }

    @Override
    public List<MetadataNotification> getAll()
    {
        return Collections.emptyList();
    }

    @Override
    public List<MetadataNotification> pullAll()
    {
        return Collections.emptyList();
    }

    @Override
    public Optional<MetadataNotification> getFirstInQueue()
    {
        return Optional.empty();
    }

    @Override
    public Optional<MetadataNotification> get(String eventId)
    {
        return Optional.empty();
    }

    @Override
    public String push(MetadataNotification metadataEvent)
    {
        return null;
    }

    @Override
    public long size()
    {
        return 0;
    }

    @Override
    public long deleteAll()
    {
        return 0;
    }
}

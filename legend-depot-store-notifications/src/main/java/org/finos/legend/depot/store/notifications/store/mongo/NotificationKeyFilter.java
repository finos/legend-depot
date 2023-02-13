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

package org.finos.legend.depot.store.notifications.store.mongo;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.finos.legend.depot.domain.notifications.MetadataNotification;

class NotificationKeyFilter
{
    private NotificationKeyFilter()
    {
    }

    public static Bson getFilter(MetadataNotification notification)
    {
        return notification.getEventId() != null ? Filters.eq(BaseMongo.ID_FIELD, new ObjectId(notification.getEventId())) :
                Filters.and(Filters.and(
                        Filters.eq(BaseMongo.GROUP_ID, notification.getGroupId())),
                        Filters.eq(BaseMongo.ARTIFACT_ID, notification.getArtifactId()),
                        Filters.eq(BaseMongo.VERSION_ID, notification.getVersionId()));
    }
}

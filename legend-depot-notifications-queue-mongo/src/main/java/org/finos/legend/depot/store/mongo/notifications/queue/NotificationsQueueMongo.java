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

package org.finos.legend.depot.store.mongo.notifications.queue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.util.function.Consumer;


public class NotificationsQueueMongo extends BaseMongo<MetadataNotification> implements Queue
{

    public static final String COLLECTION = "notifications-queue";
    private static final String EVENT_PRIORITY = "eventPriority";

    @Inject
    public NotificationsQueueMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, MetadataNotification.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(BaseMongo.buildIndex("eventPriority-created", "eventPriority","created"));
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected Bson getKeyFilter(MetadataNotification event)
    {
        return NotificationKeyFilter.getFilter(event);
    }

    @Override
    protected void validateNewData(MetadataNotification data)
    {
        //no specific validation
    }

    @Override
    public long size()
    {
        return getCollection().countDocuments();
    }

    public String push(MetadataNotification event)
    {
        MetadataNotification result = createOrUpdate(event);
        if (result.getEventId() == null)
        {
            createOrUpdate(result.setEventId(result.getId()));
        }
        return result.getEventId();
    }


    public List<MetadataNotification> pullAll()
    {
        List<MetadataNotification> nextEvents = new ArrayList<>();
        getCollection().find().forEach((Consumer<Document>)document ->
        {
            DeleteResult del = getCollection().deleteOne(document);
            if (del.getDeletedCount() != 0)
            { //todo: if it errors, it will get stuck in the queue?
                nextEvents.add(convert(document, MetadataNotification.class));
            }
        });
        return nextEvents;
    }

    @Override
    public Optional<MetadataNotification> getFirstInQueue()
    {
        Document first = (Document)getCollection().findOneAndDelete(Filters.exists(BaseMongo.ID_FIELD),new FindOneAndDeleteOptions().sort(Sorts.ascending(EVENT_PRIORITY, BaseMongo.CREATED)));
        if (first != null)
        {
            return Optional.of(convert(first, MetadataNotification.class));

        }
        return Optional.empty();
    }

    @Override
    public Optional<MetadataNotification> get(String eventId)
    {
        return findOne(Filters.eq(BaseMongo.ID_FIELD, new ObjectId(eventId)));
    }

    public List<MetadataNotification> getAll()
    {
        List<MetadataNotification> allInQueue = new ArrayList<>();
        getCollection().find().forEach((Consumer<Document>)document -> allInQueue.add(convert(document, MetadataNotification.class)));
        return allInQueue;
    }

    @Override
    public long deleteAll()
    {
        DeleteResult result = getCollection().deleteMany(Filters.exists(BaseMongo.GROUP_ID));
        return result.getDeletedCount();
    }
}

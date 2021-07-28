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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.store.mongo.BaseMongo;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class QueueMongo extends BaseMongo<MetadataNotification> implements Queue
{

    public static final String QUEUE = "events-queue";
    public static final String OBJECT_ID = "_id";

    @Inject
    public QueueMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, MetadataNotification.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(QUEUE);
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

    public String push(MetadataNotification event)
    {
        MetadataNotification result = createOrUpdate(event);
        if (result.getEventId() == null)
        {
            result.setEventId(result.getId());
            createOrUpdate(result);
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
        Document first = (Document)getCollection().findOneAndDelete(Filters.exists(GROUP_ID));
        if (first != null)
        {
            return Optional.of(convert(first, MetadataNotification.class));

        }
        return Optional.empty();
    }

    @Override
    public Optional<MetadataNotification> get(String eventId)
    {
        return findOne(Filters.eq(OBJECT_ID, new ObjectId(eventId)));
    }

    public List<MetadataNotification> getAll()
    {
        List<MetadataNotification> allInQueue = new ArrayList<>();
        getCollection().find().forEach((Consumer<Document>)document -> allInQueue.add(convert(document, MetadataNotification.class)));
        return allInQueue;
    }
}

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

package org.finos.legend.depot.store.mongo.schedules;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.api.admin.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.model.admin.schedules.ScheduleInstance;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;

public class ScheduleInstancesMongo extends BaseMongo<ScheduleInstance> implements ScheduleInstancesStore
{
    public static final String COLLECTION = "schedule-instances";
    public static final String SCHEDULE = "schedule";
    private static final String EXPIRES = "expires";


    @Inject
    public ScheduleInstancesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ScheduleInstance.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    protected void validateNewData(ScheduleInstance data)
    {

    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected Bson getKeyFilter(ScheduleInstance data)
    {
        return eq(SCHEDULE, data.getSchedule());
    }

    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("schedule", SCHEDULE));
    }

    @Override
    public List<ScheduleInstance> getAll()
    {
        return super.getAllStoredEntities();
    }

    @Override
    public long delete(long expiry)
    {
        return super.delete(lt(EXPIRES, expiry));
    }

    @Override
    public List<ScheduleInstance> find(String scheduleName)
    {
        return super.find(eq(SCHEDULE, scheduleName));
    }
}

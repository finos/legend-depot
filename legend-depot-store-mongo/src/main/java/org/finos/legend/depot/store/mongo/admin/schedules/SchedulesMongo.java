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

package org.finos.legend.depot.store.mongo.admin.schedules;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.admin.api.schedules.SchedulesStore;
import org.finos.legend.depot.store.admin.domain.schedules.ScheduleInfo;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class SchedulesMongo extends BaseMongo<ScheduleInfo> implements SchedulesStore
{
    public static final String COLLECTION = "schedules";
    public static final String JOB_ID = "jobId";
    private static final String RUNNING = "running";
    private static final String DISABLED = "disabled";


    @Inject
    public SchedulesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ScheduleInfo.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    protected void validateNewData(ScheduleInfo data)
    {
        //nothing to validate
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected Bson getKeyFilter(ScheduleInfo data)
    {
        return eq(JOB_ID, data.jobId);
    }


    @Override
    public Optional<ScheduleInfo> get(String jobId)
    {
        return findOne(eq(JOB_ID, jobId));
    }


    @Override
    public List<ScheduleInfo> getAll()
    {
        return super.getAllStoredEntities();
    }

    @Override
    public List<ScheduleInfo> find(Boolean running, Boolean disabled)
    {
        Bson filter = exists(JOB_ID);
        if (running != null)
        {
            filter = and(filter, eq(RUNNING, running));
        }
        if (disabled != null)
        {
            filter = and(filter, eq(DISABLED, disabled));
        }
        return super.find(filter);
    }

    @Override
    public void delete(String jobId)
    {
        super.delete(eq(JOB_ID, jobId));
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("jobid", JOB_ID));
    }
}

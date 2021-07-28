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

package org.finos.legend.depot.store.admin.store.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.admin.api.ManageSchedulesService;
import org.finos.legend.depot.store.admin.services.schedules.ScheduleInfo;
import org.finos.legend.depot.store.mongo.BaseMongo;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MongoSchedules extends BaseMongo<ScheduleInfo> implements ManageSchedulesService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MongoSchedules.class);
    private static final String SCHEDULES_COLLECTION = "schedules";
    public static final String JOB_ID = "jobId";


    @Inject
    public MongoSchedules(@Named("mongoDatabase") MongoDatabase databaseProvider)
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
        return getMongoCollection(SCHEDULES_COLLECTION);
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


    private ScheduleInfo getScheduleInfo(String jobId)
    {
        Optional<ScheduleInfo> info = findOne(eq(JOB_ID, jobId));

        ScheduleInfo scheduleInfo = new ScheduleInfo(jobId);
        if (info.isPresent())
        {
            scheduleInfo = info.get();
        }
        return scheduleInfo;
    }

    @Override
    public void toggle(String jobId, boolean toggle)
    {
        ScheduleInfo info = getScheduleInfo(jobId);
        info.disabled = toggle;
        createOrUpdate(info);
    }

    @Override
    public void toggleAll(boolean toggle)
    {
        getAll().stream().forEach(info ->
        {
            info.disabled = toggle;
            createOrUpdate(info);
        });
    }

}

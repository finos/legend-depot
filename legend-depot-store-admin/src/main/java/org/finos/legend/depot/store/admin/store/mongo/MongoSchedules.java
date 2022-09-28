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

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class MongoSchedules extends BaseMongo<ScheduleInfo> implements ManageSchedulesService
{
    private static final String SCHEDULES_COLLECTION = "schedules";
    public static final String JOB_ID = "jobId";
    private static final String RUNNING = "running";
    private static final String DISABLED = "disabled";


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
    public void toggleDisable(String jobId, boolean toggle)
    {
        synchronized (ScheduleInfo.class)
        {
            ScheduleInfo info = getScheduleInfo(jobId);
            info.disabled.getAndSet(toggle);
            createOrUpdate(info);
        }
    }

    @Override
    public void toggleRunning(String jobId, boolean toggle)
    {
        synchronized (ScheduleInfo.class)
        {
            ScheduleInfo info = getScheduleInfo(jobId);
            info.running.getAndSet(toggle);
            createOrUpdate(info);
        }
    }

    @Override
    public void toggleDisableAll(boolean toggle)
    {
            getAll().stream().forEach(info ->
            {
                toggleDisable(info.jobId,toggle);
            });
    }

}

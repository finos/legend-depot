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

package org.finos.legend.depot.store.artifacts.store.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.mongo.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;


public class MongoRefreshStatus extends BaseMongo<RefreshStatus> implements ManageRefreshStatusService
{

    private static final String STATUS_COLLECTION = "refresh_status";

    private static final String RUNNING = "running";
    private static final String STAR_TIME = "startTime";
    private static final String RESPONSE_STATUS = "response.status";
    private static final String PARENT_EVENT = "parentEventId";

    @Inject
    public MongoRefreshStatus(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, RefreshStatus.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    public MongoCollection getCollection()
    {
        return getMongoCollection(STATUS_COLLECTION);
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        createIndexIfAbsent("running",RUNNING);
        createIndexIfAbsent("parentId",PARENT_EVENT);
        createIndexIfAbsent("status",RESPONSE_STATUS);
        return createIndexIfAbsent("groupId-artifactId-versionId", GROUP_ID, ARTIFACT_ID, VERSION_ID);
    }

    @Override
    protected Bson getKeyFilter(RefreshStatus storeStatus)
    {
        return and(and(eq(GROUP_ID, storeStatus.getGroupId()), eq(ARTIFACT_ID, storeStatus.getArtifactId())), eq(VERSION_ID, storeStatus.getVersionId()));
    }

    @Override
    protected void validateNewData(RefreshStatus data)
    {
        //no specific validation
    }


    @Override
    public List<RefreshStatus> find(String groupId, String artifactId, String version, String parentEventId, Boolean running, Boolean success, LocalDateTime fromStartTime,LocalDateTime toStartTime)
    {
        Bson filter = exists(GROUP_ID);
        if (groupId != null)
        {
            filter = and(filter, eq(GROUP_ID, groupId));
        }
        if (artifactId != null)
        {
            filter = and(filter, eq(ARTIFACT_ID, artifactId));
        }
        if (version != null)
        {
            filter = and(filter, eq(VERSION_ID, version));
        }
        if (parentEventId != null)
        {
            filter = and(filter, eq(PARENT_EVENT, parentEventId));
        }
        if (running != null)
        {
            filter = and(filter, eq(RUNNING, running));
        }
        if (success != null)
        {
            filter = and(filter, eq(RESPONSE_STATUS, success ? MetadataEventStatus.SUCCESS.name() : MetadataEventStatus.FAILED.name()));
        }
        if (fromStartTime != null)
        {
            filter = and(filter, gte(STAR_TIME, toTime(fromStartTime)));
        }
        if (toStartTime != null)
        {
            filter = and(filter, lte(STAR_TIME, toTime(toStartTime)));
        }
        return find(filter);
    }

    @Override
    public RefreshStatus get(String groupId, String artifactId, String version)
    {
        Bson filter = and(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId)), eq(VERSION_ID, version));
        Optional<RefreshStatus> found = findOne(filter);
        return found.orElseGet(() -> new RefreshStatus(groupId, artifactId, version));
    }

    @Override
    public void delete(String id)
    {
        Bson filter = eq("_id", new ObjectId(id));
        getCollection().deleteOne(filter);
    }

    private long toTime(LocalDateTime date)
    {
        return Date.from(date.atZone(ZoneId.systemDefault())
                .toInstant()).getTime();
    }

}

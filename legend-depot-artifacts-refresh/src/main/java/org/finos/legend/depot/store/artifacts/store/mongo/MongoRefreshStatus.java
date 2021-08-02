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
import org.finos.legend.depot.domain.entity.VersionRevision;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.mongo.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;


public class MongoRefreshStatus extends BaseMongo<RefreshStatus> implements ManageRefreshStatusService
{

    private static final String STATUS_COLLECTION = "refresh_status";

    private static final String TYPE = "type";
    private static final String RUNNING = "running";

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
        return createIndexIfAbsent("type-groupId-artifactId-versionId", TYPE, GROUP_ID, ARTIFACT_ID, VERSION_ID);
    }

    @Override
    protected Bson getKeyFilter(RefreshStatus storeStatus)
    {
        return and(and(eq(GROUP_ID, storeStatus.getGroupId()), eq(ARTIFACT_ID, storeStatus.getArtifactId())), and(eq(TYPE, storeStatus.getType()), eq(VERSION_ID, storeStatus.getVersionId())));
    }

    @Override
    protected void validateNewData(RefreshStatus data)
    {
        //no specific validation
    }

    @Override
    public List<RefreshStatus> find(VersionRevision entityType, String groupId, String artifactId, String version, Boolean running)
    {
        Bson filter = exists(TYPE);
        if (entityType != null)
        {
            filter = eq(TYPE, entityType.getName());
        }
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
        if (running != null)
        {
            filter = and(filter, eq(RUNNING, running));
        }
        return find(filter);
    }

    @Override
    public RefreshStatus get(VersionRevision entitiesType, String groupId, String artifactId, String version)
    {
        Bson filter = and(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId)), and(eq(TYPE, entitiesType.getName()), eq(VERSION_ID, version)));
        Optional<RefreshStatus> found = findOne(filter);
        return found.orElseGet(() -> new RefreshStatus(entitiesType.getName(), groupId, artifactId, version));
    }

    @Override
    public void delete(String id)
    {
        Bson filter = eq("_id", new ObjectId(id));
        getCollection().deleteOne(filter);
    }


}

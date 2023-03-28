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

package org.finos.legend.depot.store.mongo.admin.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;


public class ArtifactsRefreshStatusMongo extends BaseMongo<RefreshStatus> implements RefreshStatusStore
{

    public static final String COLLECTION = "artifacts-refresh-status";
    private static final String PARENT_EVENT = "parentEventId";
    private static final String EVENT_ID = "eventId";

    @Inject
    public ArtifactsRefreshStatusMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, RefreshStatus.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    public MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(
        buildIndex("eventId",EVENT_ID),
        buildIndex("parentId",PARENT_EVENT),
        buildIndex("groupId-artifactId-versionId",true,GROUP_ID, ARTIFACT_ID, VERSION_ID));
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
    public RefreshStatus createOrUpdate(RefreshStatus data)
    {
        throw new UnsupportedOperationException("createOrUpdate Not supported for refresh status class");
    }

    @Override
    public List<RefreshStatus> getAll()
    {
        return super.getAllStoredEntities();
    }

    @Override
    public List<RefreshStatus> find(String groupId, String artifactId, String version,String eventId,String parentEventId)
    {
        Bson filter = groupId != null ? eq(GROUP_ID, groupId) : exists(GROUP_ID);
        filter = artifactId != null ? and(filter, eq(ARTIFACT_ID, artifactId)) : filter;
        filter = version != null ? and(filter, eq(VERSION_ID, version)) : filter;
        filter = eventId != null ? and(filter, eq(EVENT_ID, eventId)) : filter;
        filter = parentEventId != null ? and(filter, eq(PARENT_EVENT, parentEventId)) : filter;
        return find(filter);
    }

    @Override
    public Optional<RefreshStatus> get(String groupId, String artifactId, String version)
    {
        Bson filter = and(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId)), eq(VERSION_ID, version));
        return  findOne(filter);
    }


    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        super.delete(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }
}

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


package org.finos.legend.depot.store.mongo.admin.metrics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import com.mongodb.client.model.IndexModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class QueryMetricsMongo extends BaseMongo<VersionQueryMetric> implements QueryMetricsStore
{

    public static final String COLLECTION = "query-metrics";


    @Inject
    public QueryMetricsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, VersionQueryMetric.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    public MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    public List<VersionQueryMetric> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public List<VersionQueryMetric> get(String groupId, String artifactId, String versionId)
    {
        return find(getKeyFilter(groupId, artifactId, versionId));
    }

    @Override
    public void record(String groupId, String artifactId, String versionId)
    {
        VersionQueryMetric metric = new VersionQueryMetric(groupId, artifactId, versionId);
        getCollection().insertOne(buildDocument(metric));
    }

    @Override
    protected Bson getKeyFilter(VersionQueryMetric data)
    {
        return getKeyFilter(data.getGroupId(), data.getArtifactId(), data.getVersionId());
    }

    protected Bson getKeyFilter(String groupId, String artifactId, String versionId)
    {
        return and(and(eq(BaseMongo.ARTIFACT_ID, artifactId), eq(BaseMongo.VERSION_ID, versionId),
                eq(GROUP_ID, groupId)));
    }

    @Override
    protected void validateNewData(VersionQueryMetric data)
    {
        //no specific validation
    }

    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("group-artifact-version", GROUP_ID,ARTIFACT_ID,VERSION_ID));
    }
}

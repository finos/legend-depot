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
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryCounter;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class QueryMetricsMongo extends BaseMongo<VersionQueryCounter> implements QueryMetricsStore
{

    private static final String COLLECTION = "query-metrics";


    @Inject
    public QueryMetricsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, VersionQueryCounter.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    public MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    public List<VersionQueryCounter> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public List<VersionQueryCounter> get(String groupId, String artifactId, String versionId)
    {
        return find(getKeyFilter(groupId, artifactId, versionId));
    }

    @Override
    public void persistMetrics(List<VersionQueryCounter> metrics)
    {
        metrics.forEach(metric -> getCollection().insertOne(BaseMongo.buildDocument(metric)));
    }

    @Override
    protected Bson getKeyFilter(VersionQueryCounter data)
    {
        return new BsonDocument();
    }

    protected Bson getKeyFilter(String groupId, String artifactId, String versionId)
    {
        return and(and(eq(BaseMongo.ARTIFACT_ID, artifactId), eq(BaseMongo.VERSION_ID, versionId),
                eq(GROUP_ID, groupId)));
    }

    @Override
    protected void validateNewData(VersionQueryCounter data)
    {
        //no specific validation
    }


}

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
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import com.mongodb.client.model.IndexModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.regex;

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
    public List<ProjectVersion> getAllStoredEntitiesCoordinates()
    {
        List<ProjectVersion> result = new ArrayList<>();
        BasicDBList concat = new BasicDBList();
        concat.add("$groupId");
        concat.add(":");
        concat.add("$artifactId");
        concat.add(":");
        concat.add("$versionId");
        Bson allCoordinates = Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.include(GROUP_ID, ARTIFACT_ID, VERSION_ID),
                Projections.computed("coordinate", new BasicDBObject("$concat", concat))));

        getCollection().aggregate(Arrays.asList(allCoordinates, group("$coordinate"))).forEach((Consumer<Document>) document ->
                {
                    StringTokenizer tokenizer = new StringTokenizer(document.getString("_id"), ":");
                    result.add(new ProjectVersion(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
                }
        );
        return result;
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
        record(metric);
    }

    public void record(VersionQueryMetric metric)
    {
        getCollection().insertOne(buildDocument(metric));
    }

    @Override
    public long consolidate(VersionQueryMetric metric)
    {
        // We are deleting every metric with same gav except the one passed in the function
        return super.delete(and(getKeyFilter(metric),lt("lastQueryTime", metric.getLastQueryTime().getTime())));
    }

    @Override
    public List<VersionQueryMetric> findMetricsBefore(Date date)
    {
        return find(lte("lastQueryTime", date.getTime()));
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

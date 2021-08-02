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

package org.finos.legend.depot.store.metrics.store.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.store.metrics.api.ManageQueryMetrics;
import org.finos.legend.depot.store.metrics.domain.VersionQueryCounter;
import org.finos.legend.depot.store.metrics.domain.VersionQuerySummary;
import org.finos.legend.depot.store.mongo.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class MongoQueryMetrics extends BaseMongo<VersionQueryCounter> implements ManageQueryMetrics
{


    public static final Comparator<VersionQueryCounter> MOST_RECENTLY_QUERIED = (o1, o2) -> o2.getLastQueryTime().compareTo(o1.getLastQueryTime());
    private static final String STATUS_COLLECTION = "query_metrics";


    @Inject
    public MongoQueryMetrics(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, VersionQueryCounter.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    public MongoCollection getCollection()
    {
        return getMongoCollection(STATUS_COLLECTION);
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
    public void persistMetrics()
    {
        List<VersionQueryCounter> metrics = QueryMetricsContainer.getAll();
        QueryMetricsContainer.flush();
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


    @Override
    public Optional<VersionQuerySummary> getSummary(String groupId, String artifactId, String versionId)
    {
        List<VersionQueryCounter> queryCounters = find(getKeyFilter(groupId, artifactId, versionId));
        if (queryCounters.isEmpty())
        {
            return Optional.empty();
        }
        Optional<VersionQueryCounter> latest = queryCounters.stream().max(MOST_RECENTLY_QUERIED);
        return latest.map(versionQueryCounter -> new VersionQuerySummary(groupId, artifactId, versionId, versionQueryCounter.getLastQueryTime(), queryCounters.size()));
    }

    @Override
    public List<VersionQuerySummary> getSummaryByProjectVersion()
    {
        Map<MetricKey, VersionQuerySummary> all = new HashMap<>();
        getAllStoredEntities().forEach(m ->
        {
            MetricKey key = new MetricKey(m.getGroupId(), m.getArtifactId(), m.getVersionId());
            VersionQuerySummary inSummary = all.getOrDefault(key, new VersionQuerySummary(m.getGroupId(), m.getArtifactId(), m.getVersionId(), m.getLastQueryTime(), 0));
            inSummary.addToSummary(m);
            all.put(key, inSummary);
        });
        return all.values().stream().sorted(MOST_RECENTLY_QUERIED).collect(Collectors.toList());
    }

    private static class MetricKey
    {
        private String groupId;
        private String artifactId;
        private String versionId;

        public MetricKey(String groupId, String artifactId, String versionId)
        {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.versionId = versionId;
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        public String getGroupId()
        {
            return groupId;
        }

        public String getArtifactId()
        {
            return artifactId;
        }

        public String getVersionId()
        {
            return versionId;
        }
    }
}

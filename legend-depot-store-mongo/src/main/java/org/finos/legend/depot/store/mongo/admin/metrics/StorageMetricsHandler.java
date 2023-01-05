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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.finos.legend.depot.store.admin.api.metrics.StorageMetrics;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StorageMetricsHandler implements StorageMetrics
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StorageMetricsHandler.class);
    private static final int SCALE = 1024;
    public static final PrometheusMetricsHandler PROMETHEUS_METRICS_HANDLER = PrometheusMetricsFactory.getInstance();
    private final MongoDatabase mongoDatabase;

    @Inject
    public StorageMetricsHandler(@Named("mongoDatabase") MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public void reportMetrics()
    {
        DbStats dbStats = getDbStats();
         if (dbStats != null)
         {
             dbStats.logMetrics();
         }
         getCollectionsStats().forEach(stats -> stats.logMetrics());
    }

    private DbStats getDbStats()
    {
        Document document = mongoDatabase.runCommand(new Document("dbStats", 1).append("scale",SCALE));
        try
        {
            return new ObjectMapper().convertValue(document, DbStats.class);
        }
        catch (Exception e)
        {
            LOGGER.error("Error Logging DBStats  for " + mongoDatabase.getName(), e);
        }
        return null;
    }


    private List<CollectionStats> getCollectionsStats()
    {
        List<CollectionStats> results = new ArrayList<>();
        for (String collectionName : mongoDatabase.listCollectionNames())
        {
            try
            {
                Document document = mongoDatabase.runCommand(new Document("collStats", collectionName).append("scale",SCALE));
                CollectionStats stats = new ObjectMapper().convertValue(document, CollectionStats.class);
                if (stats != null)
                {
                    results.add(stats);
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Error Logging Collection Stats for " + collectionName, e);
            }
        }
        return results;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DbStats
    {
        @JsonProperty("objects")
        Integer objectCount;

        @JsonProperty("avgObjSize")
        Double averageDocSize;

        @JsonProperty("dataSize")
        Double uncompressedDataSize = Double.valueOf(0.0);

        @JsonProperty("storageSize")
        Double storageSize = Double.valueOf(0.0);

        @JsonProperty("indexes")
        Integer indexes;

        @JsonProperty("indexSize")
        Double indexSize = Double.valueOf(0.0);


        public void logMetrics()
        {

            PROMETHEUS_METRICS_HANDLER.setGauge("store_indexes_count",this.indexes);
            PROMETHEUS_METRICS_HANDLER.setGauge("store_object_count",this.objectCount);
            PROMETHEUS_METRICS_HANDLER.setGauge("store_avg_doc_size_kb", this.averageDocSize.intValue());
            PROMETHEUS_METRICS_HANDLER.setGauge("store_data_size_gb", this.uncompressedDataSize.intValue() / 1000000);
            PROMETHEUS_METRICS_HANDLER.setGauge("store_storage_size_gb", this.storageSize.intValue() / 1000000);
            PROMETHEUS_METRICS_HANDLER.setGauge("store_index_size_gb", this.indexSize.intValue() / 1000000);

        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CollectionStats
    {
        @JsonProperty("ns")
        String collectionName;

        @JsonProperty("size")
        Double collectionSize;

        @JsonProperty("count")
        Double documentCount;

        @JsonProperty("avgObjSize")
        Double averageDocSize = Double.valueOf(0.0);

        @JsonProperty("storageSize")
        Double storageSize = Double.valueOf(0.0);

        @JsonProperty("nindexes")
        Integer numberOfIndexes;

        @JsonProperty("totalIndexSize")
        Double indexSize = Double.valueOf(0.0);

        public void logMetrics()
        {
            PROMETHEUS_METRICS_HANDLER.setGauge(this.collectionName + "_collectionSize",collectionSize);
            PROMETHEUS_METRICS_HANDLER.setGauge(this.collectionName + "_objectCount",documentCount);
            PROMETHEUS_METRICS_HANDLER.setGauge(this.collectionName + "_indexCount",numberOfIndexes);
            PROMETHEUS_METRICS_HANDLER.setGauge(this.collectionName + "_indexSize",indexSize / 1000000000);
            PROMETHEUS_METRICS_HANDLER.setGauge(this.collectionName + "_storageSize",storageSize / 1000000000);
            PROMETHEUS_METRICS_HANDLER.setGauge(this.collectionName + "_avgSize",averageDocSize / 1000);
        }
    }

}

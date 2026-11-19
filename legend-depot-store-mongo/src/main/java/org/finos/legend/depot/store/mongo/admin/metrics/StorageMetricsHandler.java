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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.slf4j.Logger;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageMetricsHandler
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StorageMetricsHandler.class);
    private static final int SCALE = 1024;
    private final MongoAdminStore adminStore;
    private final PrometheusMetricsHandler metricsHandler;

    @Inject
    public StorageMetricsHandler(MongoAdminStore adminStore, PrometheusMetricsHandler metricsHandler)
    {
        this.adminStore = adminStore;
        this.metricsHandler = metricsHandler;
    }


    public void init()
    {

        this.metricsHandler.registerGauge("storage_data_size_kb", "data size");
        this.metricsHandler.registerGauge("storage_storage_size_kb", "storage size");
        this.metricsHandler.registerGauge("storage_index_size_kb", "index size");

        this.metricsHandler.registerGauge("storage_collectionSize","collection size",Arrays.asList("collectionName"));
        this.metricsHandler.registerGauge("storage_objectCount","object count",Arrays.asList("collectionName"));
        this.metricsHandler.registerGauge("storage_indexCount","index count",Arrays.asList("collectionName"));
        this.metricsHandler.registerGauge("storage_indexSize","index size",Arrays.asList("collectionName"));
        this.metricsHandler.registerGauge("storage_storageSize", "storage size",Arrays.asList("collectionName"));
        this.metricsHandler.registerGauge("storage_avgSize","avg size",Arrays.asList("collectionName"));
    }


    public Object reportMetrics()
    {
        StorageStats stats = new StorageStats();
        stats.dbStats = getDbStats();

         if (stats.dbStats != null)
         {
             logMetrics(stats.dbStats);
         }
         stats.collectionStats = getCollectionsStats();
         stats.collectionStats.forEach(collStats -> logMetrics(collStats));
         return stats;
    }

    private DbStats getDbStats()
    {
        Document document = adminStore.runCommand(new Document("dbStats", 1).append("scale",SCALE));
        try
        {
            return new ObjectMapper().convertValue(document, DbStats.class);
        }
        catch (Exception e)
        {
            LOGGER.error("Error Logging DBStats  for " + adminStore.getName(), e);
        }
        return null;
    }

    private List<CollectionStats> getCollectionsStats()
    {
        List<CollectionStats> results = new ArrayList<>();
        for (String collectionName : adminStore.getAllCollections())
        {
            try
            {
                Document document = adminStore.runCommand(new Document("collStats", collectionName).append("scale",SCALE));
                CollectionStats stats = new ObjectMapper().convertValue(document, CollectionStats.class);
                if (stats != null)
                {
                    stats.collectionName = collectionName;
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

    private void logMetrics(DbStats dbStats)
    {
        this.metricsHandler.setGauge("storage_data_size_kb", dbStats.uncompressedDataSize);
        this.metricsHandler.setGauge("storage_storage_size_kb", dbStats.storageSize);
        this.metricsHandler.setGauge("storage_index_size_kb", dbStats.indexSize);
    }

    private void logMetrics(CollectionStats stats)
    {
        this.metricsHandler.setGauge("storage_collectionSize",stats.collectionSize, Arrays.asList(stats.collectionName));
        this.metricsHandler.setGauge("storage_objectCount",stats.documentCount, Arrays.asList(stats.collectionName));
        this.metricsHandler.setGauge("storage_indexCount",stats.numberOfIndexes, Arrays.asList(stats.collectionName));
        this.metricsHandler.setGauge("storage_indexSize",stats.indexSize, Arrays.asList(stats.collectionName));
        this.metricsHandler.setGauge("storage_storageSize",stats.storageSize, Arrays.asList(stats.collectionName));
        this.metricsHandler.setGauge("storage_avgSize",stats.averageDocSize, Arrays.asList(stats.collectionName));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StorageStats
    {
        @JsonProperty()
        DbStats dbStats;
        @JsonProperty()
        List<CollectionStats> collectionStats;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DbStats
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

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CollectionStats
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

    }

}

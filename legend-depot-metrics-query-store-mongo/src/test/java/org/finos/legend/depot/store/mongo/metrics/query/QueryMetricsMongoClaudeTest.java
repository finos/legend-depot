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

package org.finos.legend.depot.store.mongo.metrics.query;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueryMetricsMongoClaudeTest extends TestStoreMongo
{
    private QueryMetricsMongo queryMetricsMongo;

    @BeforeEach
    public void setUpData()
  {
        queryMetricsMongo = new QueryMetricsMongo(getMongoDatabase());
    }

    @Test
    public void testConstructor()
  {
        QueryMetricsMongo store = new QueryMetricsMongo(getMongoDatabase());
        assertNotNull(store);
        assertNotNull(store.getCollection());
    }

    @Test
    public void testGetCollection()
  {
        MongoCollection collection = queryMetricsMongo.getCollection();
        assertNotNull(collection);
        assertEquals(QueryMetricsMongo.COLLECTION, collection.getNamespace().getCollectionName());
    }

    @Test
    public void testGetAllEmpty()
  {
        List<VersionQueryMetric> metrics = queryMetricsMongo.getAll();
        assertNotNull(metrics);
        assertTrue(metrics.isEmpty());
    }

    @Test
    public void testGetAllWithData()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group2", "artifact2", "2.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);

        List<VersionQueryMetric> metrics = queryMetricsMongo.getAll();
        assertNotNull(metrics);
        assertEquals(2, metrics.size());
    }

    @Test
    public void testGetAllStoredEntitiesCoordinatesEmpty()
  {
        List<ProjectVersion> coordinates = queryMetricsMongo.getAllStoredEntitiesCoordinates();
        assertNotNull(coordinates);
        assertTrue(coordinates.isEmpty());
    }

    @Test
    public void testGetAllStoredEntitiesCoordinatesWithData()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group2", "artifact2", "2.0.0");
        VersionQueryMetric metric3 = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);
        queryMetricsMongo.insert(metric3);

        List<ProjectVersion> coordinates = queryMetricsMongo.getAllStoredEntitiesCoordinates();
        assertNotNull(coordinates);
        assertEquals(2, coordinates.size());

        boolean hasGroup1Artifact1 = coordinates.stream()
                .anyMatch(pv -> "group1".equals(pv.getGroupId()) &&
                              "artifact1".equals(pv.getArtifactId()) &&
                              "1.0.0".equals(pv.getVersionId()));
        boolean hasGroup2Artifact2 = coordinates.stream()
                .anyMatch(pv -> "group2".equals(pv.getGroupId()) &&
                              "artifact2".equals(pv.getArtifactId()) &&
                              "2.0.0".equals(pv.getVersionId()));

        assertTrue(hasGroup1Artifact1);
        assertTrue(hasGroup2Artifact2);
    }

    @Test
    public void testGetWithSpecificVersion()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "2.0.0");
        VersionQueryMetric metric3 = new VersionQueryMetric("group2", "artifact2", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);
        queryMetricsMongo.insert(metric3);

        List<VersionQueryMetric> results = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("group1", results.get(0).getGroupId());
        assertEquals("artifact1", results.get(0).getArtifactId());
        assertEquals("1.0.0", results.get(0).getVersionId());
    }

    @Test
    public void testGetNotFound()
  {
        List<VersionQueryMetric> results = queryMetricsMongo.get("nonexistent", "artifact", "1.0.0");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testFindByGroupAndArtifact()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "2.0.0");
        VersionQueryMetric metric3 = new VersionQueryMetric("group2", "artifact2", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);
        queryMetricsMongo.insert(metric3);

        List<VersionQueryMetric> results = queryMetricsMongo.find("group1", "artifact1");
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(m -> "group1".equals(m.getGroupId()) && "artifact1".equals(m.getArtifactId())));
    }

    @Test
    public void testFindNotFound()
  {
        List<VersionQueryMetric> results = queryMetricsMongo.find("nonexistent", "artifact");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testInsert()
  {
        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        queryMetricsMongo.insert(metric);

        List<VersionQueryMetric> results = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(1, results.size());
        assertEquals("group1", results.get(0).getGroupId());
        assertEquals("artifact1", results.get(0).getArtifactId());
        assertEquals("1.0.0", results.get(0).getVersionId());
        assertNotNull(results.get(0).getLastQueryTime());
    }

    @Test
    public void testInsertMultiple()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "2.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);

        List<VersionQueryMetric> results = queryMetricsMongo.find("group1", "artifact1");
        assertEquals(2, results.size());
    }

    @Test
    public void testConsolidate() throws InterruptedException
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        Date oldDate = cal.getTime();

        VersionQueryMetric oldMetric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate);
        VersionQueryMetric oldMetric2 = new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate);

        Thread.sleep(10);
        VersionQueryMetric newMetric = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        queryMetricsMongo.insert(oldMetric1);
        queryMetricsMongo.insert(oldMetric2);
        queryMetricsMongo.insert(newMetric);

        List<VersionQueryMetric> beforeConsolidate = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(3, beforeConsolidate.size());

        long deletedCount = queryMetricsMongo.consolidate(newMetric);
        assertEquals(2, deletedCount);

        List<VersionQueryMetric> afterConsolidate = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(1, afterConsolidate.size());
        assertTrue(afterConsolidate.get(0).getLastQueryTime().getTime() >= newMetric.getLastQueryTime().getTime());
    }

    @Test
    public void testConsolidateWithDifferentVersions()
  {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        Date oldDate = cal.getTime();

        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate);
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "2.0.0", oldDate);
        VersionQueryMetric newMetric = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);

        long deletedCount = queryMetricsMongo.consolidate(newMetric);
        assertEquals(1, deletedCount);

        List<VersionQueryMetric> allMetrics = queryMetricsMongo.find("group1", "artifact1");
        assertEquals(1, allMetrics.size());
    }

    @Test
    public void testFindMetricsBeforeWithOldMetrics()
  {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        Date oldDate = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date cutoffDate = cal.getTime();

        VersionQueryMetric oldMetric = new VersionQueryMetric("group1", "artifact1", "1.0.0", oldDate);
        VersionQueryMetric newMetric = new VersionQueryMetric("group2", "artifact2", "2.0.0");

        queryMetricsMongo.insert(oldMetric);
        queryMetricsMongo.insert(newMetric);

        List<VersionQueryMetric> results = queryMetricsMongo.findMetricsBefore(cutoffDate);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("group1", results.get(0).getGroupId());
    }

    @Test
    public void testFindMetricsBeforeNoResults()
  {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        Date cutoffDate = cal.getTime();

        VersionQueryMetric newMetric = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        queryMetricsMongo.insert(newMetric);

        List<VersionQueryMetric> results = queryMetricsMongo.findMetricsBefore(cutoffDate);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testDelete()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "2.0.0");
        VersionQueryMetric metric3 = new VersionQueryMetric("group2", "artifact2", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);
        queryMetricsMongo.insert(metric3);

        long result = queryMetricsMongo.delete("group1", "artifact1", "1.0.0");
        assertEquals(1, result);

        List<VersionQueryMetric> remaining = queryMetricsMongo.getAll();
        assertEquals(2, remaining.size());

        List<VersionQueryMetric> deleted = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertTrue(deleted.isEmpty());
    }

    @Test
    public void testDeleteNonexistent()
  {
        long result = queryMetricsMongo.delete("nonexistent", "artifact", "1.0.0");
        assertEquals(1, result);
    }

    @Test
    public void testGetKeyFilterWithMetric()
  {
        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        Bson filter = queryMetricsMongo.getKeyFilter(metric);

        assertNotNull(filter);

        queryMetricsMongo.insert(metric);
        VersionQueryMetric metric2 = new VersionQueryMetric("group2", "artifact2", "2.0.0");
        queryMetricsMongo.insert(metric2);

        // Test that the filter works correctly by using the get method which uses the filter internally
        List<VersionQueryMetric> results = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(1, results.size());
        assertEquals("group1", results.get(0).getGroupId());
    }

    @Test
    public void testGetKeyFilterWithStrings()
  {
        Bson filter = queryMetricsMongo.getKeyFilter("group1", "artifact1", "1.0.0");

        assertNotNull(filter);

        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group2", "artifact2", "2.0.0");
        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);

        // Test that the filter is created correctly by verifying the get method uses it internally
        List<VersionQueryMetric> results = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(1, results.size());
        assertEquals("group1", results.get(0).getGroupId());
        assertEquals("artifact1", results.get(0).getArtifactId());
        assertEquals("1.0.0", results.get(0).getVersionId());
    }

    @Test
    public void testValidateNewData()
  {
        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        assertDoesNotThrow(() -> queryMetricsMongo.validateNewData(metric));
    }

    @Test
    public void testValidateNewDataWithNull()
  {
        assertDoesNotThrow(() -> queryMetricsMongo.validateNewData(null));
    }

    @Test
    public void testBuildIndexes()
  {
        List<IndexModel> indexes = QueryMetricsMongo.buildIndexes();

        assertNotNull(indexes);
        assertEquals(1, indexes.size());

        IndexModel index = indexes.get(0);
        assertNotNull(index);
        assertEquals("group-artifact-version", index.getOptions().getName());
    }

    @Test
    public void testInsertWithCustomDate()
  {
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 15, 10, 30, 0);
        Date customDate = cal.getTime();

        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0", customDate);
        queryMetricsMongo.insert(metric);

        List<VersionQueryMetric> results = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(1, results.size());
        assertNotNull(results.get(0).getLastQueryTime());
    }

    @Test
    public void testMultipleInsertsWithSameCoordinates()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);

        List<VersionQueryMetric> results = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertEquals(2, results.size());
    }

    @Test
    public void testGetAllStoredEntitiesCoordinatesWithMultipleSameCoordinates()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric3 = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);
        queryMetricsMongo.insert(metric3);

        List<ProjectVersion> coordinates = queryMetricsMongo.getAllStoredEntitiesCoordinates();
        assertEquals(1, coordinates.size());
        assertEquals("group1", coordinates.get(0).getGroupId());
        assertEquals("artifact1", coordinates.get(0).getArtifactId());
        assertEquals("1.0.0", coordinates.get(0).getVersionId());
    }

    @Test
    public void testFindMetricsBeforeExactDate()
  {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date exactDate = cal.getTime();

        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0.0", exactDate);
        queryMetricsMongo.insert(metric);

        List<VersionQueryMetric> results = queryMetricsMongo.findMetricsBefore(exactDate);
        assertEquals(1, results.size());
    }

    @Test
    public void testConsolidateNoOldMetrics()
  {
        VersionQueryMetric newMetric = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        long deletedCount = queryMetricsMongo.consolidate(newMetric);
        assertEquals(0, deletedCount);
    }

    @Test
    public void testDeleteMultipleMetrics()
  {
        VersionQueryMetric metric1 = new VersionQueryMetric("group1", "artifact1", "1.0.0");
        VersionQueryMetric metric2 = new VersionQueryMetric("group1", "artifact1", "1.0.0");

        queryMetricsMongo.insert(metric1);
        queryMetricsMongo.insert(metric2);

        long result = queryMetricsMongo.delete("group1", "artifact1", "1.0.0");
        assertEquals(1, result);

        List<VersionQueryMetric> remaining = queryMetricsMongo.get("group1", "artifact1", "1.0.0");
        assertTrue(remaining.isEmpty());
    }
}

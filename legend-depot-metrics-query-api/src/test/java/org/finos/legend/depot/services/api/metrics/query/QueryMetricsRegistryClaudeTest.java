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

package org.finos.legend.depot.services.api.metrics.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

class QueryMetricsRegistryClaudeTest 

{

    /**
     * Test implementation to verify the default method behavior.
     * This implementation stores all recorded metrics so we can verify them.
     */
    private static class TestQueryMetricsRegistry implements QueryMetricsRegistry {
        private final List<RecordedMetric> recordedMetrics = new ArrayList<>();

        static class RecordedMetric 

{
            final String groupId;
            final String artifactId;
            final String versionId;
            final Date date;

            RecordedMetric(String groupId, String artifactId, String versionId, Date date) {
                this.groupId = groupId;
                this.artifactId = artifactId;
                this.versionId = versionId;
                this.date = date;
            }
        }

        @Override
        public void record(String groupId, String artifactId, String versionId, Date date)
  {
            recordedMetrics.add(new RecordedMetric(groupId, artifactId, versionId, date));
        }

        @Override
        public Optional<VersionQueryMetric> findFirst() {
            if (recordedMetrics.isEmpty()) {
                return Optional.empty();
            }
            RecordedMetric first = recordedMetrics.get(0);
            return Optional.of(new VersionQueryMetric(first.groupId, first.artifactId, first.versionId, first.date));
        }

        List<RecordedMetric> getRecordedMetrics() {
            return recordedMetrics;
        }
    }

    /**
     * Test {@link QueryMetricsRegistry#record(String, String, String)}.
     *
     * <p>This test verifies that the default method calls the 4-parameter record method
     * with the current date.
     */
    @Test
    @DisplayName("Test record(String, String, String) default method")
    void testRecordDefaultMethod()
  {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Capture the time before and after the call to verify the date is current
        Date before = new Date();

        // Act
        registry.record(groupId, artifactId, versionId);

        Date after = new Date();

        // Assert
        assertEquals(1, registry.getRecordedMetrics().size(), "Should have recorded exactly one metric");

        TestQueryMetricsRegistry.RecordedMetric recorded = registry.getRecordedMetrics().get(0);
        assertEquals(groupId, recorded.groupId, "Group ID should match");
        assertEquals(artifactId, recorded.artifactId, "Artifact ID should match");
        assertEquals(versionId, recorded.versionId, "Version ID should match");
        assertNotNull(recorded.date, "Date should not be null");

        // Verify the date is between before and after (i.e., it's a current date)
        assertTrue(!recorded.date.before(before), "Date should not be before the test started");
        assertTrue(!recorded.date.after(after), "Date should not be after the test ended");
    }

    /**
     * Test {@link QueryMetricsRegistry#record(String, String, String)} with null values.
     *
     * <p>This test verifies that the default method handles null values.
     */
    @Test
    @DisplayName("Test record(String, String, String) with null values")
    void testRecordDefaultMethodWithNulls()
  {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();

        // Act
        registry.record(null, null, null);

        // Assert
        assertEquals(1, registry.getRecordedMetrics().size(), "Should have recorded exactly one metric");

        TestQueryMetricsRegistry.RecordedMetric recorded = registry.getRecordedMetrics().get(0);
        assertEquals(null, recorded.groupId, "Group ID should be null");
        assertEquals(null, recorded.artifactId, "Artifact ID should be null");
        assertEquals(null, recorded.versionId, "Version ID should be null");
        assertNotNull(recorded.date, "Date should not be null even when other parameters are null");
    }

    /**
     * Test {@link QueryMetricsRegistry#record(String, String, String)} with empty strings.
     *
     * <p>This test verifies that the default method handles empty string values.
     */
    @Test
    @DisplayName("Test record(String, String, String) with empty strings")
    void testRecordDefaultMethodWithEmptyStrings()
  {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();

        // Act
        registry.record("", "", "");

        // Assert
        assertEquals(1, registry.getRecordedMetrics().size(), "Should have recorded exactly one metric");

        TestQueryMetricsRegistry.RecordedMetric recorded = registry.getRecordedMetrics().get(0);
        assertEquals("", recorded.groupId, "Group ID should be empty string");
        assertEquals("", recorded.artifactId, "Artifact ID should be empty string");
        assertEquals("", recorded.versionId, "Version ID should be empty string");
        assertNotNull(recorded.date, "Date should not be null");
    }

    /**
     * Test {@link QueryMetricsRegistry#record(String, String, String)} with special characters.
     *
     * <p>This test verifies that the default method handles special characters in parameters.
     */
    @Test
    @DisplayName("Test record(String, String, String) with special characters")
    void testRecordDefaultMethodWithSpecialCharacters()
  {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();
        String groupId = "org.example.@#$";
        String artifactId = "test-artifact-!@#";
        String versionId = "1.0.0-SNAPSHOT+build";

        // Act
        registry.record(groupId, artifactId, versionId);

        // Assert
        assertEquals(1, registry.getRecordedMetrics().size(), "Should have recorded exactly one metric");

        TestQueryMetricsRegistry.RecordedMetric recorded = registry.getRecordedMetrics().get(0);
        assertEquals(groupId, recorded.groupId, "Group ID should match");
        assertEquals(artifactId, recorded.artifactId, "Artifact ID should match");
        assertEquals(versionId, recorded.versionId, "Version ID should match");
    }

    /**
     * Test multiple calls to {@link QueryMetricsRegistry#record(String, String, String)}.
     *
     * <p>This test verifies that multiple calls to the default method work correctly.
     */
    @Test
    @DisplayName("Test multiple calls to record(String, String, String)")
    void testRecordDefaultMethodMultipleCalls()
  {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();

        // Act
        registry.record("group1", "artifact1", "1.0.0");
        registry.record("group2", "artifact2", "2.0.0");
        registry.record("group3", "artifact3", "3.0.0");

        // Assert
        assertEquals(3, registry.getRecordedMetrics().size(), "Should have recorded three metrics");

        // Verify first metric
        TestQueryMetricsRegistry.RecordedMetric first = registry.getRecordedMetrics().get(0);
        assertEquals("group1", first.groupId);
        assertEquals("artifact1", first.artifactId);
        assertEquals("1.0.0", first.versionId);

        // Verify second metric
        TestQueryMetricsRegistry.RecordedMetric second = registry.getRecordedMetrics().get(1);
        assertEquals("group2", second.groupId);
        assertEquals("artifact2", second.artifactId);
        assertEquals("2.0.0", second.versionId);

        // Verify third metric
        TestQueryMetricsRegistry.RecordedMetric third = registry.getRecordedMetrics().get(2);
        assertEquals("group3", third.groupId);
        assertEquals("artifact3", third.artifactId);
        assertEquals("3.0.0", third.versionId);
    }

    /**
     * Test {@link QueryMetricsRegistry#record(String, String, String)} with long strings.
     *
     * <p>This test verifies that the default method handles very long string values.
     */
    @Test
    @DisplayName("Test record(String, String, String) with long strings")
    void testRecordDefaultMethodWithLongStrings()
  {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();
        String longString = "a".repeat(1000);

        // Act
        registry.record(longString, longString, longString);

        // Assert
        assertEquals(1, registry.getRecordedMetrics().size(), "Should have recorded exactly one metric");

        TestQueryMetricsRegistry.RecordedMetric recorded = registry.getRecordedMetrics().get(0);
        assertEquals(longString, recorded.groupId, "Group ID should match long string");
        assertEquals(longString, recorded.artifactId, "Artifact ID should match long string");
        assertEquals(longString, recorded.versionId, "Version ID should match long string");
    }

    /**
     * Test {@link QueryMetricsRegistry#record(String, String, String)} timing behavior.
     *
     * <p>This test verifies that sequential calls have increasing timestamps.
     */
    @Test
    @DisplayName("Test record(String, String, String) timing behavior")
    void testRecordDefaultMethodTimingBehavior() throws InterruptedException {
        // Arrange
        TestQueryMetricsRegistry registry = new TestQueryMetricsRegistry();

        // Act
        registry.record("group1", "artifact1", "1.0.0");
        Thread.sleep(10); // Small delay to ensure different timestamps
        registry.record("group2", "artifact2", "2.0.0");

        // Assert
        assertEquals(2, registry.getRecordedMetrics().size(), "Should have recorded two metrics");

        Date firstDate = registry.getRecordedMetrics().get(0).date;
        Date secondDate = registry.getRecordedMetrics().get(1).date;

        assertTrue(secondDate.getTime() >= firstDate.getTime(),
            "Second metric should have a timestamp equal to or after the first metric");
    }
}

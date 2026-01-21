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

package org.finos.legend.depot.store.model.metrics.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

class VersionQueryMetricClaudeTest 

{

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric()}.
     *
     * <p>Verifies that the no-arg constructor creates an instance with all fields set to null.
     */
    @Test
    @DisplayName("Test no-arg constructor initializes with null fields")
    void testNoArgConstructor()
  {
        // Act
        VersionQueryMetric metric = new VersionQueryMetric();

        // Assert
        assertNull(metric.getGroupId(), "Group ID should be null");
        assertNull(metric.getArtifactId(), "Artifact ID should be null");
        assertNull(metric.getVersionId(), "Version ID should be null");
        assertNull(metric.getLastQueryTime(), "Last query time should be null");
    }

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric(String, String, String)}.
     *
     * <p>Verifies that the 3-parameter constructor sets the fields correctly and
     * initializes lastQueryTime to the current date.
     */
    @Test
    @DisplayName("Test 3-parameter constructor sets fields and creates current date")
    void testThreeParameterConstructor()
  {
        // Arrange
        String groupId = "org.finos.legend";
        String artifactId = "legend-depot-core";
        String versionId = "1.0.0";
        Date before = new Date();

        // Act
        VersionQueryMetric metric = new VersionQueryMetric(groupId, artifactId, versionId);

        Date after = new Date();

        // Assert
        assertEquals(groupId, metric.getGroupId(), "Group ID should match");
        assertEquals(artifactId, metric.getArtifactId(), "Artifact ID should match");
        assertEquals(versionId, metric.getVersionId(), "Version ID should match");
        assertNotNull(metric.getLastQueryTime(), "Last query time should not be null");

        // Verify the lastQueryTime is between before and after (i.e., it's a current date)
        assertTrue(!metric.getLastQueryTime().before(before),
            "Last query time should not be before constructor was called");
        assertTrue(!metric.getLastQueryTime().after(after),
            "Last query time should not be after constructor completed");
    }

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric(String, String, String)} with null values.
     *
     * <p>Verifies that the 3-parameter constructor handles null values correctly.
     */
    @Test
    @DisplayName("Test 3-parameter constructor with null values")
    void testThreeParameterConstructorWithNulls()
  {
        // Act
        VersionQueryMetric metric = new VersionQueryMetric(null, null, null);

        // Assert
        assertNull(metric.getGroupId(), "Group ID should be null");
        assertNull(metric.getArtifactId(), "Artifact ID should be null");
        assertNull(metric.getVersionId(), "Version ID should be null");
        assertNotNull(metric.getLastQueryTime(), "Last query time should not be null");
    }

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric(String, String, String)} with empty strings.
     *
     * <p>Verifies that the 3-parameter constructor handles empty string values correctly.
     */
    @Test
    @DisplayName("Test 3-parameter constructor with empty strings")
    void testThreeParameterConstructorWithEmptyStrings()
  {
        // Act
        VersionQueryMetric metric = new VersionQueryMetric("", "", "");

        // Assert
        assertEquals("", metric.getGroupId(), "Group ID should be empty string");
        assertEquals("", metric.getArtifactId(), "Artifact ID should be empty string");
        assertEquals("", metric.getVersionId(), "Version ID should be empty string");
        assertNotNull(metric.getLastQueryTime(), "Last query time should not be null");
    }

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric(String, String, String, Date)}.
     *
     * <p>Verifies that the 4-parameter constructor sets all fields correctly.
     */
    @Test
    @DisplayName("Test 4-parameter constructor sets all fields")
    void testFourParameterConstructor()
  {
        // Arrange
        String groupId = "org.finos.legend";
        String artifactId = "legend-depot-core";
        String versionId = "2.0.0";
        Date lastQueryTime = new Date(1000000000L);

        // Act
        VersionQueryMetric metric = new VersionQueryMetric(groupId, artifactId, versionId, lastQueryTime);

        // Assert
        assertEquals(groupId, metric.getGroupId(), "Group ID should match");
        assertEquals(artifactId, metric.getArtifactId(), "Artifact ID should match");
        assertEquals(versionId, metric.getVersionId(), "Version ID should match");
        assertSame(lastQueryTime, metric.getLastQueryTime(), "Last query time should be the same instance");
    }

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric(String, String, String, Date)} with null date.
     *
     * <p>Verifies that the 4-parameter constructor handles null date correctly.
     */
    @Test
    @DisplayName("Test 4-parameter constructor with null date")
    void testFourParameterConstructorWithNullDate()
  {
        // Arrange
        String groupId = "org.finos.legend";
        String artifactId = "legend-depot-core";
        String versionId = "2.0.0";

        // Act
        VersionQueryMetric metric = new VersionQueryMetric(groupId, artifactId, versionId, null);

        // Assert
        assertEquals(groupId, metric.getGroupId(), "Group ID should match");
        assertEquals(artifactId, metric.getArtifactId(), "Artifact ID should match");
        assertEquals(versionId, metric.getVersionId(), "Version ID should match");
        assertNull(metric.getLastQueryTime(), "Last query time should be null");
    }

    /**
     * Test {@link VersionQueryMetric#VersionQueryMetric(String, String, String, Date)} with all nulls.
     *
     * <p>Verifies that the 4-parameter constructor handles all null values correctly.
     */
    @Test
    @DisplayName("Test 4-parameter constructor with all null values")
    void testFourParameterConstructorWithAllNulls()
  {
        // Act
        VersionQueryMetric metric = new VersionQueryMetric(null, null, null, null);

        // Assert
        assertNull(metric.getGroupId(), "Group ID should be null");
        assertNull(metric.getArtifactId(), "Artifact ID should be null");
        assertNull(metric.getVersionId(), "Version ID should be null");
        assertNull(metric.getLastQueryTime(), "Last query time should be null");
    }

    /**
     * Test {@link VersionQueryMetric#getGroupId()}.
     *
     * <p>Verifies that the getter returns the correct value.
     */
    @Test
    @DisplayName("Test getGroupId returns correct value")
    void testGetGroupId()
  {
        // Arrange
        String groupId = "com.example.test";
        VersionQueryMetric metric = new VersionQueryMetric(groupId, "artifact", "1.0");

        // Act & Assert
        assertEquals(groupId, metric.getGroupId(), "getGroupId should return the correct value");
    }

    /**
     * Test {@link VersionQueryMetric#getArtifactId()}.
     *
     * <p>Verifies that the getter returns the correct value.
     */
    @Test
    @DisplayName("Test getArtifactId returns correct value")
    void testGetArtifactId()
  {
        // Arrange
        String artifactId = "test-artifact";
        VersionQueryMetric metric = new VersionQueryMetric("group", artifactId, "1.0");

        // Act & Assert
        assertEquals(artifactId, metric.getArtifactId(), "getArtifactId should return the correct value");
    }

    /**
     * Test {@link VersionQueryMetric#getVersionId()}.
     *
     * <p>Verifies that the getter returns the correct value.
     */
    @Test
    @DisplayName("Test getVersionId returns correct value")
    void testGetVersionId()
  {
        // Arrange
        String versionId = "3.2.1-SNAPSHOT";
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", versionId);

        // Act & Assert
        assertEquals(versionId, metric.getVersionId(), "getVersionId should return the correct value");
    }

    /**
     * Test {@link VersionQueryMetric#getLastQueryTime()}.
     *
     * <p>Verifies that the getter returns the correct value.
     */
    @Test
    @DisplayName("Test getLastQueryTime returns correct value")
    void testGetLastQueryTime()
  {
        // Arrange
        Date queryTime = new Date(2000000000L);
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", "1.0", queryTime);

        // Act & Assert
        assertSame(queryTime, metric.getLastQueryTime(), "getLastQueryTime should return the same instance");
    }

    /**
     * Test {@link VersionQueryMetric#setLastQueryTime(Date)}.
     *
     * <p>Verifies that the setter updates the lastQueryTime field correctly.
     */
    @Test
    @DisplayName("Test setLastQueryTime updates the field")
    void testSetLastQueryTime()
  {
        // Arrange
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", "1.0");
        Date originalTime = metric.getLastQueryTime();
        Date newTime = new Date(3000000000L);

        // Act
        metric.setLastQueryTime(newTime);

        // Assert
        assertSame(newTime, metric.getLastQueryTime(), "Last query time should be updated to new value");
        assertTrue(newTime != originalTime, "New time should be different from original");
    }

    /**
     * Test {@link VersionQueryMetric#setLastQueryTime(Date)} with null.
     *
     * <p>Verifies that the setter can set the lastQueryTime to null.
     */
    @Test
    @DisplayName("Test setLastQueryTime with null value")
    void testSetLastQueryTimeWithNull()
  {
        // Arrange
        Date initialTime = new Date(1000000000L);
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", "1.0", initialTime);

        // Act
        metric.setLastQueryTime(null);

        // Assert
        assertNull(metric.getLastQueryTime(), "Last query time should be null after setting to null");
    }

    /**
     * Test {@link VersionQueryMetric#setLastQueryTime(Date)} multiple times.
     *
     * <p>Verifies that the setter can be called multiple times and always uses the latest value.
     */
    @Test
    @DisplayName("Test setLastQueryTime multiple times")
    void testSetLastQueryTimeMultipleTimes()
  {
        // Arrange
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", "1.0");
        Date time1 = new Date(1000000000L);
        Date time2 = new Date(2000000000L);
        Date time3 = new Date(3000000000L);

        // Act
        metric.setLastQueryTime(time1);
        assertSame(time1, metric.getLastQueryTime(), "Should have first time");

        metric.setLastQueryTime(time2);
        assertSame(time2, metric.getLastQueryTime(), "Should have second time");

        metric.setLastQueryTime(time3);
        assertSame(time3, metric.getLastQueryTime(), "Should have third time");
    }

    /**
     * Test {@link VersionQueryMetric#getId()}.
     *
     * <p>Verifies that getId always returns an empty string.
     * This is the implementation requirement from the HasIdentifier interface.
     */
    @Test
    @DisplayName("Test getId returns empty string")
    void testGetIdReturnsEmptyString()
  {
        // Arrange & Act
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", "1.0");

        // Assert
        assertEquals("", metric.getId(), "getId should always return an empty string");
    }

    /**
     * Test {@link VersionQueryMetric#getId()} with different field values.
     *
     * <p>Verifies that getId returns empty string regardless of field values.
     */
    @Test
    @DisplayName("Test getId returns empty string regardless of field values")
    void testGetIdWithDifferentFields()
  {
        // Arrange & Act
        VersionQueryMetric metric1 = new VersionQueryMetric();
        VersionQueryMetric metric2 = new VersionQueryMetric("g", "a", "v");
        VersionQueryMetric metric3 = new VersionQueryMetric(null, null, null, null);

        // Assert
        assertEquals("", metric1.getId(), "getId should return empty string for no-arg constructor");
        assertEquals("", metric2.getId(), "getId should return empty string for 3-param constructor");
        assertEquals("", metric3.getId(), "getId should return empty string for 4-param constructor with nulls");
    }

    /**
     * Test with special characters in Maven coordinates.
     *
     * <p>Verifies that the class handles special characters that might appear in Maven coordinates.
     */
    @Test
    @DisplayName("Test with special characters in Maven coordinates")
    void testWithSpecialCharacters()
  {
        // Arrange
        String groupId = "org.example.sub-group";
        String artifactId = "my-artifact_v2";
        String versionId = "1.0.0-SNAPSHOT+build.123";

        // Act
        VersionQueryMetric metric = new VersionQueryMetric(groupId, artifactId, versionId);

        // Assert
        assertEquals(groupId, metric.getGroupId(), "Group ID with hyphens should be preserved");
        assertEquals(artifactId, metric.getArtifactId(), "Artifact ID with hyphens and underscores should be preserved");
        assertEquals(versionId, metric.getVersionId(), "Version ID with hyphens and plus signs should be preserved");
    }

    /**
     * Test with long string values.
     *
     * <p>Verifies that the class handles very long string values.
     */
    @Test
    @DisplayName("Test with long string values")
    void testWithLongStrings()
  {
        // Arrange
        String longString = "a".repeat(1000);

        // Act
        VersionQueryMetric metric = new VersionQueryMetric(longString, longString, longString);

        // Assert
        assertEquals(longString, metric.getGroupId(), "Long group ID should be preserved");
        assertEquals(longString, metric.getArtifactId(), "Long artifact ID should be preserved");
        assertEquals(longString, metric.getVersionId(), "Long version ID should be preserved");
    }

    /**
     * Test that Date objects are stored by reference, not copied.
     *
     * <p>Verifies that modifications to the Date object after construction
     * affect the stored value (demonstrating reference semantics).
     */
    @Test
    @DisplayName("Test Date is stored by reference")
    void testDateStoredByReference()
  {
        // Arrange
        Date mutableDate = new Date(1000000000L);
        VersionQueryMetric metric = new VersionQueryMetric("group", "artifact", "1.0", mutableDate);

        // Act
        long originalTime = metric.getLastQueryTime().getTime();
        mutableDate.setTime(2000000000L);

        // Assert
        assertEquals(2000000000L, metric.getLastQueryTime().getTime(),
            "Changes to the Date object should affect the stored value");
        assertTrue(metric.getLastQueryTime().getTime() != originalTime,
            "Stored date should reflect external modifications");
    }

    /**
     * Test field independence.
     *
     * <p>Verifies that setting one field doesn't affect other fields.
     */
    @Test
    @DisplayName("Test field independence")
    void testFieldIndependence()
  {
        // Arrange
        Date initialDate = new Date(1000000000L);
        VersionQueryMetric metric = new VersionQueryMetric("group1", "artifact1", "1.0", initialDate);

        // Act
        Date newDate = new Date(2000000000L);
        metric.setLastQueryTime(newDate);

        // Assert - other fields should remain unchanged
        assertEquals("group1", metric.getGroupId(), "Group ID should remain unchanged");
        assertEquals("artifact1", metric.getArtifactId(), "Artifact ID should remain unchanged");
        assertEquals("1.0", metric.getVersionId(), "Version ID should remain unchanged");
        assertSame(newDate, metric.getLastQueryTime(), "Last query time should be updated");
    }
}

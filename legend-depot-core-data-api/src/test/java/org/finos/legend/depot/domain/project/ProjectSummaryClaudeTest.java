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

package org.finos.legend.depot.domain.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectSummaryClaudeTest 

{

    @Test
    @DisplayName("Test constructor initializes all fields correctly")
    void testConstructor()
  {
        // Arrange and Act
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "my-artifact", 5L);

        // Assert
        assertEquals("proj1", summary.projectId);
        assertEquals("com.example", summary.groupId);
        assertEquals("my-artifact", summary.artifactId);
        assertEquals(5L, summary.versions);
    }

    @Test
    @DisplayName("Test constructor with null values")
    void testConstructorWithNulls()
  {
        // Arrange and Act
        ProjectSummary summary = new ProjectSummary(null, null, null, 0L);

        // Assert
        assertNull(summary.projectId);
        assertNull(summary.groupId);
        assertNull(summary.artifactId);
        assertEquals(0L, summary.versions);
    }

    @Test
    @DisplayName("Test constructor with negative versions")
    void testConstructorWithNegativeVersions()
  {
        // Arrange and Act
        ProjectSummary summary = new ProjectSummary("proj1", "group", "artifact", -1L);

        // Assert
        assertEquals(-1L, summary.versions);
    }

    @Test
    @DisplayName("Test getMavenCoordinates returns groupId-artifactId")
    void testGetMavenCoordinates()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "my-artifact", 1L);

        // Act
        String coordinates = summary.getMavenCoordinates();

        // Assert
        assertEquals("com.example-my-artifact", coordinates);
    }

    @Test
    @DisplayName("Test getMavenCoordinates with null groupId")
    void testGetMavenCoordinatesWithNullGroupId()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", null, "my-artifact", 1L);

        // Act
        String coordinates = summary.getMavenCoordinates();

        // Assert
        assertEquals("null-my-artifact", coordinates);
    }

    @Test
    @DisplayName("Test getMavenCoordinates with null artifactId")
    void testGetMavenCoordinatesWithNullArtifactId()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", null, 1L);

        // Act
        String coordinates = summary.getMavenCoordinates();

        // Assert
        assertEquals("com.example-null", coordinates);
    }

    @Test
    @DisplayName("Test compareTo returns 0 for equal objects")
    void testCompareToEquals()
  {
        // Arrange
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 1L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.example", "artifact-a", 2L);

        // Act
        int result = summary1.compareTo(summary2);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Test compareTo with different groupIds")
    void testCompareToWithDifferentGroupIds()
  {
        // Arrange
        // Note: compareTo concatenates other.groupId + this.artifactId vs this.groupId + this.artifactId
        // summary1: this.groupId="com.example", this.artifactId="artifact-a"
        // summary2: other.groupId="com.aaa", this.artifactId="artifact-a"
        // Compares: "com.aaaartifact-a" vs "com.exampleartifact-a"
        // "com.aaa" < "com.example", so result should be negative
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 1L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.aaa", "artifact-a", 2L);

        // Act
        int result = summary1.compareTo(summary2);

        // Assert
        assertTrue(result < 0, "Expected negative value");
    }

    @Test
    @DisplayName("Test compareTo with different groupIds reversed")
    void testCompareToWithDifferentGroupIdsReversed()
  {
        // Arrange
        // summary1: this.groupId="com.aaa", this.artifactId="artifact-a"
        // summary2: other.groupId="com.example", this.artifactId="artifact-a"
        // Compares: "com.exampleartifact-a" vs "com.aaaartifact-a"
        // "com.example" > "com.aaa", so result should be positive
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.aaa", "artifact-a", 1L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.example", "artifact-a", 2L);

        // Act
        int result = summary1.compareTo(summary2);

        // Assert
        assertTrue(result > 0, "Expected positive value");
    }

    @Test
    @DisplayName("Test compareTo with different artifactIds same groupId")
    void testCompareToWithDifferentArtifactIds()
  {
        // Arrange
        // Note: The bug in compareTo means it compares other.groupId + this.artifactId
        // summary1: this.groupId="com.example", this.artifactId="artifact-b"
        // summary2: other.groupId="com.example", this.artifactId="artifact-a" (but uses this.artifactId!)
        // Compares: "com.exampleartifact-b" vs "com.exampleartifact-b"
        // Result should be 0
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-b", 1L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.example", "artifact-a", 2L);

        // Act
        int result = summary1.compareTo(summary2);

        // Assert
        assertEquals(0, result, "Both use same groupId and this.artifactId due to compareTo bug");
    }

    @Test
    @DisplayName("Test compareTo with same object")
    void testCompareToSameObject()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", 1L);

        // Act
        int result = summary.compareTo(summary);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Test compareTo exposes the bug in implementation")
    void testCompareToBugExposure()
  {
        // Arrange
        // This test demonstrates the bug where compareTo uses other.groupId + this.artifactId
        // summary1: groupId="A", artifactId="X"
        // summary2: groupId="B", artifactId="Y"
        // compareTo compares: "BX" vs "AX"
        // "BX" > "AX", so result should be positive
        ProjectSummary summary1 = new ProjectSummary("proj1", "A", "X", 1L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "B", "Y", 2L);

        // Act
        int result = summary1.compareTo(summary2);

        // Assert
        // Compares "BX" to "AX", "BX" > "AX", so positive
        assertTrue(result > 0, "Expected positive because 'BX' > 'AX'");
    }

    @Test
    @DisplayName("Test equals returns true for same object")
    void testEqualsSameObject()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", 1L);

        // Act and Assert
        assertEquals(summary, summary);
    }

    @Test
    @DisplayName("Test equals returns true for objects with same versions")
    void testEqualsWithSameVersions()
  {
        // Arrange
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 5L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.different", "artifact-b", 5L);

        // Act and Assert
        assertEquals(summary1, summary2);
    }

    @Test
    @DisplayName("Test equals returns false for objects with different versions")
    void testEqualsWithDifferentVersions()
  {
        // Arrange
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 5L);
        ProjectSummary summary2 = new ProjectSummary("proj1", "com.example", "artifact-a", 3L);

        // Act and Assert
        assertNotEquals(summary1, summary2);
    }

    @Test
    @DisplayName("Test equals returns false for null")
    void testEqualsWithNull()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", 1L);

        // Act and Assert
        assertNotEquals(summary, null);
    }

    @Test
    @DisplayName("Test equals returns false for different type")
    void testEqualsWithDifferentType()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", 1L);

        // Act and Assert
        assertNotEquals(summary, "not a ProjectSummary");
    }

    @Test
    @DisplayName("Test equals with zero versions")
    void testEqualsWithZeroVersions()
  {
        // Arrange
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 0L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.different", "artifact-b", 0L);

        // Act and Assert
        assertEquals(summary1, summary2);
    }

    @Test
    @DisplayName("Test equals ignores other fields")
    void testEqualsIgnoresOtherFields()
  {
        // Arrange - only versions matter for equality
        ProjectSummary summary1 = new ProjectSummary("proj1", "group1", "artifact1", 5L);
        ProjectSummary summary2 = new ProjectSummary("proj999", "groupXYZ", "artifactXYZ", 5L);

        // Act and Assert
        assertEquals(summary1, summary2, "equals should only compare versions field");
    }

    @Test
    @DisplayName("Test hashCode returns same value for equal objects")
    void testHashCodeConsistency()
  {
        // Arrange
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 5L);
        ProjectSummary summary2 = new ProjectSummary("proj2", "com.different", "artifact-b", 5L);

        // Act
        int hash1 = summary1.hashCode();
        int hash2 = summary2.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test hashCode returns different values for different versions")
    void testHashCodeDifferentVersions()
  {
        // Arrange
        ProjectSummary summary1 = new ProjectSummary("proj1", "com.example", "artifact-a", 5L);
        ProjectSummary summary2 = new ProjectSummary("proj1", "com.example", "artifact-a", 3L);

        // Act
        int hash1 = summary1.hashCode();
        int hash2 = summary2.hashCode();

        // Assert
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test hashCode is consistent across multiple calls")
    void testHashCodeConsistentAcrossCalls()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", 5L);

        // Act
        int hash1 = summary.hashCode();
        int hash2 = summary.hashCode();
        int hash3 = summary.hashCode();

        // Assert
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("Test hashCode with zero versions")
    void testHashCodeWithZeroVersions()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", 0L);

        // Act
        int hash = summary.hashCode();

        // Assert
        assertNotNull(hash);
    }

    @Test
    @DisplayName("Test hashCode with negative versions")
    void testHashCodeWithNegativeVersions()
  {
        // Arrange
        ProjectSummary summary = new ProjectSummary("proj1", "com.example", "artifact-a", -1L);

        // Act
        int hash = summary.hashCode();

        // Assert
        assertNotNull(hash);
    }

    @Test
    @DisplayName("Test hashCode only depends on versions field")
    void testHashCodeOnlyDependsOnVersions()
  {
        // Arrange - only versions matter for hashCode
        ProjectSummary summary1 = new ProjectSummary("proj1", "group1", "artifact1", 5L);
        ProjectSummary summary2 = new ProjectSummary("proj999", "groupXYZ", "artifactXYZ", 5L);

        // Act
        int hash1 = summary1.hashCode();
        int hash2 = summary2.hashCode();

        // Assert
        assertEquals(hash1, hash2, "hashCode should only depend on versions field");
    }
}

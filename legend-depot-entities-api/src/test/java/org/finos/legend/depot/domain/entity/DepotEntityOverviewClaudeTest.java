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

package org.finos.legend.depot.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DepotEntityOverviewClaudeTest 

{

    /**
     * Test constructor with null path parameter.
     *
     * <p>Method under test:
     * {@link DepotEntityOverview#DepotEntityOverview(String, String, String, String, String)}
     */
    @Test
    @DisplayName("Test constructor with null path")
    void testConstructorWithNullPath()
  {
        // Arrange and Act
        DepotEntityOverview overview = new DepotEntityOverview("group1", "artifact1", "version1", null, "classifier1");

        // Assert
        assertEquals("group1", overview.getGroupId());
        assertEquals("artifact1", overview.getArtifactId());
        assertEquals("version1", overview.getVersionId());
        assertNull(overview.getPath());
        assertEquals("classifier1", overview.getClassifierPath());
    }

    /**
     * Test constructor with null classifierPath parameter.
     *
     * <p>Method under test:
     * {@link DepotEntityOverview#DepotEntityOverview(String, String, String, String, String)}
     */
    @Test
    @DisplayName("Test constructor with null classifierPath")
    void testConstructorWithNullClassifierPath()
  {
        // Arrange and Act
        DepotEntityOverview overview = new DepotEntityOverview("group1", "artifact1", "version1", "path1", null);

        // Assert
        assertEquals("group1", overview.getGroupId());
        assertEquals("artifact1", overview.getArtifactId());
        assertEquals("version1", overview.getVersionId());
        assertEquals("path1", overview.getPath());
        assertNull(overview.getClassifierPath());
    }

    /**
     * Test constructor with all null parameters.
     *
     * <p>Method under test:
     * {@link DepotEntityOverview#DepotEntityOverview(String, String, String, String, String)}
     */
    @Test
    @DisplayName("Test constructor with all null parameters")
    void testConstructorWithAllNullParameters()
  {
        // Arrange and Act
        DepotEntityOverview overview = new DepotEntityOverview(null, null, null, null, null);

        // Assert
        assertNull(overview.getGroupId());
        assertNull(overview.getArtifactId());
        assertNull(overview.getVersionId());
        assertNull(overview.getPath());
        assertNull(overview.getClassifierPath());
    }

    /**
     * Test getPath returns the same instance passed to constructor.
     *
     * <p>Method under test: {@link DepotEntityOverview#getPath()}
     */
    @Test
    @DisplayName("Test getPath returns the same instance")
    void testGetPath_returnsSameInstance()
  {
        // Arrange
        String path = "test/path";
        DepotEntityOverview overview = new DepotEntityOverview("group1", "artifact1", "version1", path, "classifier1");

        // Act
        String retrievedPath = overview.getPath();

        // Assert
        assertSame(path, retrievedPath);
    }

    /**
     * Test getClassifierPath returns the same instance passed to constructor.
     *
     * <p>Method under test: {@link DepotEntityOverview#getClassifierPath()}
     */
    @Test
    @DisplayName("Test getClassifierPath returns the same instance")
    void testGetClassifierPath_returnsSameInstance()
  {
        // Arrange
        String classifierPath = "test/classifier";
        DepotEntityOverview overview = new DepotEntityOverview("group1", "artifact1", "version1", "path1", classifierPath);

        // Act
        String retrievedClassifierPath = overview.getClassifierPath();

        // Assert
        assertSame(classifierPath, retrievedClassifierPath);
    }

    /**
     * Test equals when path differs.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when path differs")
    void testEquals_whenPathDiffers_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path2", "classifier1");

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }

    /**
     * Test equals when classifierPath differs.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when classifierPath differs")
    void testEquals_whenClassifierPathDiffers_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier2");

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }

    /**
     * Test equals when one has null path and other doesn't.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when one has null path and other doesn't")
    void testEquals_whenOneHasNullPathAndOtherDoesNot_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", null, "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }

    /**
     * Test equals when one has null classifierPath and other doesn't.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when one has null classifierPath and other doesn't")
    void testEquals_whenOneHasNullClassifierPathAndOtherDoesNot_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", null);
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }

    /**
     * Test equals when both have null path.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when both have null path")
    void testEquals_whenBothHaveNullPath_thenReturnEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", null, "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", null, "classifier1");

        // Act and Assert
        assertEquals(overview1, overview2);
    }

    /**
     * Test equals when both have null classifierPath.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when both have null classifierPath")
    void testEquals_whenBothHaveNullClassifierPath_thenReturnEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", null);
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", null);

        // Act and Assert
        assertEquals(overview1, overview2);
    }

    /**
     * Test equals when all fields are null in both objects.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when all fields are null in both objects")
    void testEquals_whenAllFieldsAreNull_thenReturnEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview(null, null, null, null, null);
        DepotEntityOverview overview2 = new DepotEntityOverview(null, null, null, null, null);

        // Act and Assert
        assertEquals(overview1, overview2);
    }

    /**
     * Test hashCode when path differs.
     *
     * <p>Method under test: {@link DepotEntityOverview#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode when path differs")
    void testHashCode_whenPathDiffers_thenReturnDifferentHashCode()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path2", "classifier1");

        // Act
        int hashCode1 = overview1.hashCode();
        int hashCode2 = overview2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    /**
     * Test hashCode when classifierPath differs.
     *
     * <p>Method under test: {@link DepotEntityOverview#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode when classifierPath differs")
    void testHashCode_whenClassifierPathDiffers_thenReturnDifferentHashCode()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier2");

        // Act
        int hashCode1 = overview1.hashCode();
        int hashCode2 = overview2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    /**
     * Test hashCode when both have null path.
     *
     * <p>Method under test: {@link DepotEntityOverview#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode when both have null path")
    void testHashCode_whenBothHaveNullPath_thenReturnSameHashCode()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", null, "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", null, "classifier1");

        // Act and Assert
        assertEquals(overview1.hashCode(), overview2.hashCode());
    }

    /**
     * Test hashCode when both have null classifierPath.
     *
     * <p>Method under test: {@link DepotEntityOverview#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode when both have null classifierPath")
    void testHashCode_whenBothHaveNullClassifierPath_thenReturnSameHashCode()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", null);
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", null);

        // Act and Assert
        assertEquals(overview1.hashCode(), overview2.hashCode());
    }

    /**
     * Test equals with different versionId.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals with different versionId")
    void testEquals_whenVersionIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version2", "path1", "classifier1");

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }

    /**
     * Test equals with different artifactId.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals with different artifactId")
    void testEquals_whenArtifactIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "path1", "classifier1");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact2", "version1", "path1", "classifier1");

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }

    /**
     * Test constructor with empty strings.
     *
     * <p>Method under test:
     * {@link DepotEntityOverview#DepotEntityOverview(String, String, String, String, String)}
     */
    @Test
    @DisplayName("Test constructor with empty strings")
    void testConstructorWithEmptyStrings()
  {
        // Arrange and Act
        DepotEntityOverview overview = new DepotEntityOverview("", "", "", "", "");

        // Assert
        assertEquals("", overview.getGroupId());
        assertEquals("", overview.getArtifactId());
        assertEquals("", overview.getVersionId());
        assertEquals("", overview.getPath());
        assertEquals("", overview.getClassifierPath());
    }

    /**
     * Test equals with empty strings vs null.
     *
     * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
     */
    @Test
    @DisplayName("Test equals with empty strings vs null")
    void testEquals_whenEmptyStringVsNull_thenReturnNotEqual()
  {
        // Arrange
        DepotEntityOverview overview1 = new DepotEntityOverview("group1", "artifact1", "version1", "", "");
        DepotEntityOverview overview2 = new DepotEntityOverview("group1", "artifact1", "version1", null, null);

        // Act and Assert
        assertNotEquals(overview1, overview2);
    }
}

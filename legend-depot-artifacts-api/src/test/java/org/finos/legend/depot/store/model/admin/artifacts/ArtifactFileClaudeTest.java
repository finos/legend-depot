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

package org.finos.legend.depot.store.model.admin.artifacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArtifactFileClaudeTest 

{

    /**
     * Test {@link ArtifactFile#ArtifactFile()}.
     *
     * <p>Method under test: {@link ArtifactFile#ArtifactFile()}
     */
    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor()
  {
        // Arrange and Act
        ArtifactFile artifactFile = new ArtifactFile();

        // Assert
        assertNotNull(artifactFile);
        assertNull(artifactFile.getCheckSum());
        assertNull(artifactFile.getPath());
        assertNull(artifactFile.getId());
    }

    /**
     * Test {@link ArtifactFile#ArtifactFile(String, String)} with valid values.
     *
     * <p>Method under test: {@link ArtifactFile#ArtifactFile(String, String)}
     */
    @Test
    @DisplayName("Test parameterized constructor with valid values")
    void testParameterizedConstructorWithValidValues()
  {
        // Arrange
        String path = "/path/to/artifact.jar";
        String checkSum = "abc123def456";

        // Act
        ArtifactFile artifactFile = new ArtifactFile(path, checkSum);

        // Assert
        assertNotNull(artifactFile);
        assertEquals(path, artifactFile.getPath());
        assertEquals(checkSum, artifactFile.getCheckSum());
        assertNull(artifactFile.getId());
    }

    /**
     * Test {@link ArtifactFile#ArtifactFile(String, String)} with null values.
     *
     * <p>Method under test: {@link ArtifactFile#ArtifactFile(String, String)}
     */
    @Test
    @DisplayName("Test parameterized constructor with null values")
    void testParameterizedConstructorWithNullValues()
  {
        // Arrange and Act
        ArtifactFile artifactFile = new ArtifactFile(null, null);

        // Assert
        assertNotNull(artifactFile);
        assertNull(artifactFile.getPath());
        assertNull(artifactFile.getCheckSum());
        assertNull(artifactFile.getId());
    }

    /**
     * Test {@link ArtifactFile#ArtifactFile(String, String)} with empty strings.
     *
     * <p>Method under test: {@link ArtifactFile#ArtifactFile(String, String)}
     */
    @Test
    @DisplayName("Test parameterized constructor with empty strings")
    void testParameterizedConstructorWithEmptyStrings()
  {
        // Arrange
        String path = "";
        String checkSum = "";

        // Act
        ArtifactFile artifactFile = new ArtifactFile(path, checkSum);

        // Assert
        assertNotNull(artifactFile);
        assertEquals("", artifactFile.getPath());
        assertEquals("", artifactFile.getCheckSum());
    }

    /**
     * Test {@link ArtifactFile#getCheckSum()}.
     *
     * <p>Method under test: {@link ArtifactFile#getCheckSum()}
     */
    @Test
    @DisplayName("Test getCheckSum returns correct value")
    void testGetCheckSum()
  {
        // Arrange
        String checkSum = "sha256-checksum-value";
        ArtifactFile artifactFile = new ArtifactFile("/path/file.jar", checkSum);

        // Act
        String result = artifactFile.getCheckSum();

        // Assert
        assertEquals(checkSum, result);
    }

    /**
     * Test {@link ArtifactFile#getCheckSum()} returns null when not set.
     *
     * <p>Method under test: {@link ArtifactFile#getCheckSum()}
     */
    @Test
    @DisplayName("Test getCheckSum returns null when not set")
    void testGetCheckSumWhenNotSet()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();

        // Act
        String result = artifactFile.getCheckSum();

        // Assert
        assertNull(result);
    }

    /**
     * Test {@link ArtifactFile#setCheckSum(String)}.
     *
     * <p>Method under test: {@link ArtifactFile#setCheckSum(String)}
     */
    @Test
    @DisplayName("Test setCheckSum sets the value correctly")
    void testSetCheckSum()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();
        String checkSum = "new-checksum-value";

        // Act
        ArtifactFile result = artifactFile.setCheckSum(checkSum);

        // Assert
        assertEquals(checkSum, artifactFile.getCheckSum());
        assertSame(artifactFile, result);
    }

    /**
     * Test {@link ArtifactFile#setCheckSum(String)} with null value.
     *
     * <p>Method under test: {@link ArtifactFile#setCheckSum(String)}
     */
    @Test
    @DisplayName("Test setCheckSum with null value")
    void testSetCheckSumWithNull()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile("/path/file.jar", "initial-checksum");

        // Act
        ArtifactFile result = artifactFile.setCheckSum(null);

        // Assert
        assertNull(artifactFile.getCheckSum());
        assertSame(artifactFile, result);
    }

    /**
     * Test {@link ArtifactFile#setCheckSum(String)} method chaining.
     *
     * <p>Method under test: {@link ArtifactFile#setCheckSum(String)}
     */
    @Test
    @DisplayName("Test setCheckSum supports method chaining")
    void testSetCheckSumChaining()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();

        // Act
        ArtifactFile result = artifactFile.setCheckSum("checksum1").setCheckSum("checksum2");

        // Assert
        assertEquals("checksum2", artifactFile.getCheckSum());
        assertSame(artifactFile, result);
    }

    /**
     * Test {@link ArtifactFile#getPath()}.
     *
     * <p>Method under test: {@link ArtifactFile#getPath()}
     */
    @Test
    @DisplayName("Test getPath returns correct value")
    void testGetPath()
  {
        // Arrange
        String path = "/artifacts/my-artifact.jar";
        ArtifactFile artifactFile = new ArtifactFile(path, "checksum");

        // Act
        String result = artifactFile.getPath();

        // Assert
        assertEquals(path, result);
    }

    /**
     * Test {@link ArtifactFile#getPath()} returns null when not set.
     *
     * <p>Method under test: {@link ArtifactFile#getPath()}
     */
    @Test
    @DisplayName("Test getPath returns null when not set")
    void testGetPathWhenNotSet()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();

        // Act
        String result = artifactFile.getPath();

        // Assert
        assertNull(result);
    }

    /**
     * Test {@link ArtifactFile#setPath(String)}.
     *
     * <p>Method under test: {@link ArtifactFile#setPath(String)}
     */
    @Test
    @DisplayName("Test setPath sets the value correctly")
    void testSetPath()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();
        String path = "/new/path/to/artifact.jar";

        // Act
        ArtifactFile result = artifactFile.setPath(path);

        // Assert
        assertEquals(path, artifactFile.getPath());
        assertSame(artifactFile, result);
    }

    /**
     * Test {@link ArtifactFile#setPath(String)} with null value.
     *
     * <p>Method under test: {@link ArtifactFile#setPath(String)}
     */
    @Test
    @DisplayName("Test setPath with null value")
    void testSetPathWithNull()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile("/initial/path", "checksum");

        // Act
        ArtifactFile result = artifactFile.setPath(null);

        // Assert
        assertNull(artifactFile.getPath());
        assertSame(artifactFile, result);
    }

    /**
     * Test {@link ArtifactFile#setPath(String)} method chaining.
     *
     * <p>Method under test: {@link ArtifactFile#setPath(String)}
     */
    @Test
    @DisplayName("Test setPath supports method chaining")
    void testSetPathChaining()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();

        // Act
        ArtifactFile result = artifactFile.setPath("/path1").setPath("/path2");

        // Assert
        assertEquals("/path2", artifactFile.getPath());
        assertSame(artifactFile, result);
    }

    /**
     * Test {@link ArtifactFile#getId()}.
     *
     * <p>Method under test: {@link ArtifactFile#getId()}
     */
    @Test
    @DisplayName("Test getId always returns null")
    void testGetId()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile("/path/file.jar", "checksum");

        // Act
        String result = artifactFile.getId();

        // Assert
        assertNull(result);
    }

    /**
     * Test {@link ArtifactFile#getId()} with default constructor.
     *
     * <p>Method under test: {@link ArtifactFile#getId()}
     */
    @Test
    @DisplayName("Test getId returns null with default constructor")
    void testGetIdWithDefaultConstructor()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();

        // Act
        String result = artifactFile.getId();

        // Assert
        assertNull(result);
    }

    /**
     * Test fluent API with both setters.
     */
    @Test
    @DisplayName("Test fluent API with both setters")
    void testFluentApiWithBothSetters()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile();

        // Act
        ArtifactFile result = artifactFile
                .setPath("/fluent/path.jar")
                .setCheckSum("fluent-checksum");

        // Assert
        assertSame(artifactFile, result);
        assertEquals("/fluent/path.jar", artifactFile.getPath());
        assertEquals("fluent-checksum", artifactFile.getCheckSum());
    }

    /**
     * Test that setters overwrite previous values.
     */
    @Test
    @DisplayName("Test setters overwrite previous values")
    void testSettersOverwritePreviousValues()
  {
        // Arrange
        ArtifactFile artifactFile = new ArtifactFile("/initial/path", "initial-checksum");

        // Act
        artifactFile.setPath("/updated/path");
        artifactFile.setCheckSum("updated-checksum");

        // Assert
        assertEquals("/updated/path", artifactFile.getPath());
        assertEquals("updated-checksum", artifactFile.getCheckSum());
    }
}

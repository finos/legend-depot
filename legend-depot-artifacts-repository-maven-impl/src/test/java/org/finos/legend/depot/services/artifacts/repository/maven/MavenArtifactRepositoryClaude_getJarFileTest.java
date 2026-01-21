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

package org.finos.legend.depot.services.artifacts.repository.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

class MavenArtifactRepositoryClaude_getJarFileTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test getJarFile returns non-null File for valid coordinates")
    void testGetJarFileReturnsNonNullFile()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test getJarFile returns File that exists")
    void testGetJarFileReturnsExistingFile()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    @DisplayName("Test getJarFile returns jar file with correct name")
    void testGetJarFileReturnsCorrectFileName()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile works for different artifact")
    void testGetJarFileForDifferentArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile works for different version")
    void testGetJarFileForDifferentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "2.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile for file-generation artifact")
    void testGetJarFileForFileGenerationArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile returns null for non-existent artifact")
    void testGetJarFileReturnsNullForNonExistentArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "non-existent-artifact";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Test getJarFile returns null for non-existent version")
    void testGetJarFileReturnsNullForNonExistentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "99.99.99";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Test getJarFile returns null for non-existent group")
    void testGetJarFileReturnsNullForNonExistentGroup()
    {
        // Arrange
        String group = "non.existent.group";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Test getJarFile consistency across multiple calls")
    void testGetJarFileConsistencyAcrossMultipleCalls()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File result1 = repository.getJarFile(group, artifact, version);
        File result2 = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getAbsolutePath(), result2.getAbsolutePath());
    }

    @Test
    @DisplayName("Test getJarFile for test-dependencies-entities artifact")
    void testGetJarFileForTestDependenciesEntitiesArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile for test-dependencies-versioned-entities artifact")
    void testGetJarFileForTestDependenciesVersionedEntitiesArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-versioned-entities";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile for test-dependencies-file-generation artifact")
    void testGetJarFileForTestDependenciesFileGenerationArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-file-generation";
        String version = "1.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile for test-versioned-entities version 2.0.0")
    void testGetJarFileForTestVersionedEntitiesVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "2.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }

    @Test
    @DisplayName("Test getJarFile for test-file-generation version 2.0.0")
    void testGetJarFileForTestFileGenerationVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "2.0.0";

        // Act
        File result = repository.getJarFile(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result.getName());
    }
}

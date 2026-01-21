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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

class MavenArtifactRepositoryClaude_resolveArtifactFilesFromRepositoryTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository returns non-null array for valid coordinates")
    void testResolveArtifactFilesReturnsNonNullArray()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository returns non-empty array")
    void testResolveArtifactFilesReturnsNonEmptyArray()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository returns existing files")
    void testResolveArtifactFilesReturnsExistingFiles()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        for (File file : result)
        {
            assertNotNull(file);
            assertTrue(file.exists());
        }
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository returns correct file name")
    void testResolveArtifactFilesReturnsCorrectFileName()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-versioned-entities artifact")
    void testResolveArtifactFilesForVersionedEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-file-generation artifact")
    void testResolveArtifactFilesForFileGeneration()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for different version")
    void testResolveArtifactFilesForDifferentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "2.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-dependencies-entities")
    void testResolveArtifactFilesForDependenciesEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-dependencies-versioned-entities")
    void testResolveArtifactFilesForDependenciesVersionedEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-versioned-entities";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-dependencies-file-generation")
    void testResolveArtifactFilesForDependenciesFileGeneration()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-file-generation";
        String version = "1.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository throws exception for non-existent artifact")
    void testResolveArtifactFilesThrowsExceptionForNonExistentArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "non-existent-artifact";
        String version = "1.0.0";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolveArtifactFilesFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository throws exception for non-existent version")
    void testResolveArtifactFilesThrowsExceptionForNonExistentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "99.99.99";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolveArtifactFilesFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository throws exception for non-existent group")
    void testResolveArtifactFilesThrowsExceptionForNonExistentGroup()
    {
        // Arrange
        String group = "non.existent.group";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolveArtifactFilesFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository consistency across multiple calls")
    void testResolveArtifactFilesConsistencyAcrossMultipleCalls()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        File[] result1 = repository.resolveArtifactFilesFromRepository(group, artifact, version);
        File[] result2 = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.length, result2.length);
        assertEquals(result1[0].getAbsolutePath(), result2[0].getAbsolutePath());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-versioned-entities version 2.0.0")
    void testResolveArtifactFilesForVersionedEntitiesVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "2.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }

    @Test
    @DisplayName("Test resolveArtifactFilesFromRepository for test-file-generation version 2.0.0")
    void testResolveArtifactFilesForFileGenerationVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "2.0.0";

        // Act
        File[] result = repository.resolveArtifactFilesFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertTrue(result[0].exists());
        String expectedFileName = artifact + "-" + version + ".jar";
        assertEquals(expectedFileName, result[0].getName());
    }
}

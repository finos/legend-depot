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

import java.net.URL;

class MavenArtifactRepositoryClaude_resolvePOMFromRepositoryTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository returns non-null array for valid coordinates")
    void testResolvePOMReturnsNonNullArray()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository returns non-empty array")
    void testResolvePOMReturnsNonEmptyArray()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository returns valid URL")
    void testResolvePOMReturnsValidURL()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertNotNull(result[0]);
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository URL points to POM file")
    void testResolvePOMURLPointsToPOMFile()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        assertTrue(path.endsWith(".pom"));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository URL contains correct artifact name")
    void testResolvePOMURLContainsCorrectArtifactName()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-entities artifact")
    void testResolvePOMForTestEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-versioned-entities artifact")
    void testResolvePOMForTestVersionedEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-file-generation artifact")
    void testResolvePOMForTestFileGeneration()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test artifact version 2.0.0")
    void testResolvePOMForTestVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-entities version 2.0.0")
    void testResolvePOMForTestEntitiesVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-versioned-entities version 2.0.0")
    void testResolvePOMForTestVersionedEntitiesVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-file-generation version 2.0.0")
    void testResolvePOMForTestFileGenerationVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-dependencies artifact")
    void testResolvePOMForTestDependencies()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-dependencies-entities artifact")
    void testResolvePOMForTestDependenciesEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-dependencies-versioned-entities artifact")
    void testResolvePOMForTestDependenciesVersionedEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-versioned-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository for test-dependencies-file-generation artifact")
    void testResolvePOMForTestDependenciesFileGeneration()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-file-generation";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".pom";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository throws exception for non-existent artifact")
    void testResolvePOMThrowsExceptionForNonExistentArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "non-existent-artifact";
        String version = "1.0.0";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolvePOMFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository throws exception for non-existent version")
    void testResolvePOMThrowsExceptionForNonExistentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "99.99.99";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolvePOMFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository throws exception for non-existent group")
    void testResolvePOMThrowsExceptionForNonExistentGroup()
    {
        // Arrange
        String group = "non.existent.group";
        String artifact = "test";
        String version = "1.0.0";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolvePOMFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolvePOMFromRepository consistency across multiple calls")
    void testResolvePOMConsistencyAcrossMultipleCalls()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        URL[] result1 = repository.resolvePOMFromRepository(group, artifact, version);
        URL[] result2 = repository.resolvePOMFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.length, result2.length);
        assertEquals(result1[0].getPath(), result2[0].getPath());
    }
}

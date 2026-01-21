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

class MavenArtifactRepositoryClaude_resolveJarFromRepositoryTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test resolveJarFromRepository returns non-null array for valid coordinates")
    void testResolveJarReturnsNonNullArray()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test resolveJarFromRepository returns non-empty array")
    void testResolveJarReturnsNonEmptyArray()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("Test resolveJarFromRepository returns valid URL")
    void testResolveJarReturnsValidURL()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertNotNull(result[0]);
    }

    @Test
    @DisplayName("Test resolveJarFromRepository URL points to JAR file")
    void testResolveJarURLPointsToJarFile()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        assertTrue(path.endsWith(".jar"));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository URL contains correct artifact name")
    void testResolveJarURLContainsCorrectArtifactName()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-versioned-entities artifact")
    void testResolveJarForTestVersionedEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-file-generation artifact")
    void testResolveJarForTestFileGeneration()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-entities version 2.0.0")
    void testResolveJarForTestEntitiesVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-versioned-entities version 2.0.0")
    void testResolveJarForTestVersionedEntitiesVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-file-generation version 2.0.0")
    void testResolveJarForTestFileGenerationVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "2.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-dependencies-entities artifact")
    void testResolveJarForTestDependenciesEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-dependencies-versioned-entities artifact")
    void testResolveJarForTestDependenciesVersionedEntities()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-versioned-entities";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository for test-dependencies-file-generation artifact")
    void testResolveJarForTestDependenciesFileGeneration()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-file-generation";
        String version = "1.0.0";

        // Act
        URL[] result = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String path = result[0].getPath();
        String expectedFileName = artifact + "-" + version + ".jar";
        assertTrue(path.endsWith(expectedFileName));
    }

    @Test
    @DisplayName("Test resolveJarFromRepository throws exception for non-existent artifact")
    void testResolveJarThrowsExceptionForNonExistentArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "non-existent-artifact";
        String version = "1.0.0";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolveJarFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolveJarFromRepository throws exception for non-existent version")
    void testResolveJarThrowsExceptionForNonExistentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "99.99.99";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolveJarFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolveJarFromRepository throws exception for non-existent group")
    void testResolveJarThrowsExceptionForNonExistentGroup()
    {
        // Arrange
        String group = "non.existent.group";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            repository.resolveJarFromRepository(group, artifact, version);
        });
    }

    @Test
    @DisplayName("Test resolveJarFromRepository consistency across multiple calls")
    void testResolveJarConsistencyAcrossMultipleCalls()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        URL[] result1 = repository.resolveJarFromRepository(group, artifact, version);
        URL[] result2 = repository.resolveJarFromRepository(group, artifact, version);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.length, result2.length);
        assertEquals(result1[0].getPath(), result2[0].getPath());
    }
}

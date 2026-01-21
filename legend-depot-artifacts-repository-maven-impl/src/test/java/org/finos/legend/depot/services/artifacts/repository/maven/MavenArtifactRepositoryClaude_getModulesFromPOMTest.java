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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class MavenArtifactRepositoryClaude_getModulesFromPOMTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test getModulesFromPOM returns non-null list")
    void testGetModulesFromPOMReturnsNonNullList()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test getModulesFromPOM with ENTITIES artifact type")
    void testGetModulesFromPOMWithEntitiesType()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // The test artifact has modules, so it should return test-entities
        assertTrue(result.contains("test-entities"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM with VERSIONED_ENTITIES artifact type")
    void testGetModulesFromPOMWithVersionedEntitiesType()
    {
        // Arrange
        ArtifactType type = ArtifactType.VERSIONED_ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // The test artifact has modules, so it should return test-versioned-entities
        assertTrue(result.contains("test-versioned-entities"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM with FILE_GENERATIONS artifact type")
    void testGetModulesFromPOMWithFileGenerationsType()
    {
        // Arrange
        ArtifactType type = ArtifactType.FILE_GENERATIONS;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // The test artifact has modules, so it should return test-file-generation
        assertTrue(result.contains("test-file-generation"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM filters modules by artifact type")
    void testGetModulesFromPOMFiltersModulesByType()
    {
        // The method filters modules to match artifactId + "-" + type.getModuleName()

        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Result should only contain modules matching the pattern
        for (String module : result)
        {
            assertTrue(module.equals(artifactId + "-" + type.getModuleName()));
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM with multi-module artifact")
    void testGetModulesFromPOMWithMultiModuleArtifact()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("test-entities", result.get(0));
    }

    @Test
    @DisplayName("Test getModulesFromPOM with different version")
    void testGetModulesFromPOMWithDifferentVersion()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "2.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("test-entities"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM with test-dependencies artifact")
    void testGetModulesFromPOMWithTestDependenciesArtifact()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("test-dependencies-entities"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM consistency across multiple calls")
    void testGetModulesFromPOMConsistency()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result1 = repository.getModulesFromPOM(type, groupId, artifactId, versionId);
        List<String> result2 = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.size(), result2.size());
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("Test getModulesFromPOM with non-existent artifact")
    void testGetModulesFromPOMWithNonExistentArtifact()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "nonexistent.group";
        String artifactId = "nonexistent-artifact";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            repository.getModulesFromPOM(type, groupId, artifactId, versionId);
        }
        catch (RuntimeException e)
        {
            // TestMavenArtifactsRepository throws exception for missing POM
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM with empty groupId")
    void testGetModulesFromPOMWithEmptyGroupId()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            repository.getModulesFromPOM(type, groupId, artifactId, versionId);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM with empty artifactId")
    void testGetModulesFromPOMWithEmptyArtifactId()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            repository.getModulesFromPOM(type, groupId, artifactId, versionId);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM with empty versionId")
    void testGetModulesFromPOMWithEmptyVersionId()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "";

        // Act & Assert
        try
        {
            repository.getModulesFromPOM(type, groupId, artifactId, versionId);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM result is modifiable")
    void testGetModulesFromPOMResultIsModifiable()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        int originalSize = result.size();
        // Verify we can modify the list without throwing UnsupportedOperationException
        result.add("test-module");
        assertEquals(originalSize + 1, result.size());
    }

    @Test
    @DisplayName("Test getModulesFromPOM with invalid version")
    void testGetModulesFromPOMWithInvalidVersion()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "99.99.99";

        // Act & Assert
        try
        {
            repository.getModulesFromPOM(type, groupId, artifactId, versionId);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM uses correct module naming convention")
    void testGetModulesFromPOMUsesCorrectNamingConvention()
    {
        // The method constructs module name as artifactId + "-" + type.getModuleName()

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act - Test each artifact type
        List<String> entitiesModules = repository.getModulesFromPOM(ArtifactType.ENTITIES, groupId, artifactId, versionId);
        List<String> versionedModules = repository.getModulesFromPOM(ArtifactType.VERSIONED_ENTITIES, groupId, artifactId, versionId);
        List<String> fileGenModules = repository.getModulesFromPOM(ArtifactType.FILE_GENERATIONS, groupId, artifactId, versionId);

        // Assert
        assertNotNull(entitiesModules);
        assertNotNull(versionedModules);
        assertNotNull(fileGenModules);

        assertTrue(entitiesModules.contains("test-entities"));
        assertTrue(versionedModules.contains("test-versioned-entities"));
        assertTrue(fileGenModules.contains("test-file-generation"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM handles all artifact types")
    void testGetModulesFromPOMHandlesAllArtifactTypes()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act & Assert - Test each artifact type
        for (ArtifactType type : ArtifactType.values())
        {
            List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);
            assertNotNull(result, "Result should not be null for type: " + type);
            assertFalse(result.isEmpty(), "Result should not be empty for type: " + type);
        }
    }

    @Test
    @DisplayName("Test getModulesFromPOM with test-dependencies and VERSIONED_ENTITIES")
    void testGetModulesFromPOMWithTestDependenciesVersionedEntities()
    {
        // Arrange
        ArtifactType type = ArtifactType.VERSIONED_ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("test-dependencies-versioned-entities"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM with test-dependencies and FILE_GENERATIONS")
    void testGetModulesFromPOMWithTestDependenciesFileGenerations()
    {
        // Arrange
        ArtifactType type = ArtifactType.FILE_GENERATIONS;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("test-dependencies-file-generation"));
    }

    @Test
    @DisplayName("Test getModulesFromPOM returns only matching modules")
    void testGetModulesFromPOMReturnsOnlyMatchingModules()
    {
        // The method should filter to only include modules that match the expected pattern

        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        List<String> result = repository.getModulesFromPOM(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Should not contain other module types
        for (String module : result)
        {
            assertFalse(module.contains("versioned-entities"));
            assertFalse(module.contains("file-generation"));
        }
    }
}

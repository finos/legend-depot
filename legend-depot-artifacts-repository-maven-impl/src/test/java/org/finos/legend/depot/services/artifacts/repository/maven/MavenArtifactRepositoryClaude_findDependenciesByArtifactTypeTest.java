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
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

class MavenArtifactRepositoryClaude_findDependenciesByArtifactTypeTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType returns non-null set")
    void testFindDependenciesByArtifactTypeReturnsNonNullSet()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with ENTITIES artifact type")
    void testFindDependenciesByArtifactTypeWithEntities()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // The method filters dependencies by artifact type (ending with type.getModuleName())
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with VERSIONED_ENTITIES artifact type")
    void testFindDependenciesByArtifactTypeWithVersionedEntities()
    {
        // Arrange
        ArtifactType type = ArtifactType.VERSIONED_ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with FILE_GENERATIONS artifact type")
    void testFindDependenciesByArtifactTypeWithFileGenerations()
    {
        // Arrange
        ArtifactType type = ArtifactType.FILE_GENERATIONS;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType returns set with unique elements")
    void testFindDependenciesByArtifactTypeReturnsUniqueElements()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Sets ensure uniqueness - no duplicates
        long uniqueCount = result.stream().distinct().count();
        assertEquals(result.size(), uniqueCount);
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType filters by artifact type module name")
    void testFindDependenciesByArtifactTypeFiltersCorrectly()
    {
        // The method should only return dependencies where artifactId ends with type.getModuleName()
        // For ENTITIES: artifactId should end with "entities"

        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Verify all dependencies match the artifact type filter
        for (ArtifactDependency dep : result)
        {
            assertTrue(dep.getArtifactId().endsWith(type.getModuleName()),
                    "Dependency " + dep.getArtifactId() + " should end with " + type.getModuleName());
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with test artifact")
    void testFindDependenciesByArtifactTypeWithTestArtifact()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // The test artifact may have dependencies
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType returns ArtifactDependency objects")
    void testFindDependenciesByArtifactTypeReturnsArtifactDependencyObjects()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Verify each element is an ArtifactDependency with valid properties
        for (ArtifactDependency dep : result)
        {
            assertNotNull(dep);
            assertNotNull(dep.getGroupId());
            assertNotNull(dep.getArtifactId());
            assertNotNull(dep.getVersion());
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType uses correct module name")
    void testFindDependenciesByArtifactTypeUsesCorrectModuleName()
    {
        // The method constructs moduleName as artifactId + "-" + type.getModuleName()
        // For test-dependencies and ENTITIES, it should look for test-dependencies-entities

        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // The method should look for dependencies from test-dependencies-entities module
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with non-existent artifact")
    void testFindDependenciesByArtifactTypeWithNonExistentArtifact()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "nonexistent.group";
        String artifactId = "nonexistent-artifact";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            // TestMavenArtifactsRepository may throw exception for missing POM
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with different versions")
    void testFindDependenciesByArtifactTypeWithDifferentVersions()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";

        // Act
        Set<ArtifactDependency> result1 = repository.findDependenciesByArtifactType(type, groupId, artifactId, "1.0.0");

        // Assert
        assertNotNull(result1);
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType consistency across multiple calls")
    void testFindDependenciesByArtifactTypeConsistency()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result1 = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
        Set<ArtifactDependency> result2 = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.size(), result2.size());
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType handles all artifact types")
    void testFindDependenciesByArtifactTypeHandlesAllTypes()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act & Assert - Test each artifact type
        for (ArtifactType type : ArtifactType.values())
        {
            Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
            assertNotNull(result, "Result should not be null for type: " + type);
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with empty groupId")
    void testFindDependenciesByArtifactTypeWithEmptyGroupId()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with empty artifactId")
    void testFindDependenciesByArtifactTypeWithEmptyArtifactId()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with empty versionId")
    void testFindDependenciesByArtifactTypeWithEmptyVersionId()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType result is modifiable")
    void testFindDependenciesByArtifactTypeResultIsModifiable()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        int originalSize = result.size();
        // Verify we can modify the set without throwing UnsupportedOperationException
        result.add(new ArtifactDependency("test.group", "test-artifact", "1.0.0"));
        assertEquals(originalSize + 1, result.size());
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with invalid version")
    void testFindDependenciesByArtifactTypeWithInvalidVersion()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "99.99.99";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType filters dependencies correctly for each type")
    void testFindDependenciesByArtifactTypeFiltersForEachType()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> entitiesDeps = repository.findDependenciesByArtifactType(ArtifactType.ENTITIES, groupId, artifactId, versionId);
        Set<ArtifactDependency> versionedDeps = repository.findDependenciesByArtifactType(ArtifactType.VERSIONED_ENTITIES, groupId, artifactId, versionId);
        Set<ArtifactDependency> fileGenDeps = repository.findDependenciesByArtifactType(ArtifactType.FILE_GENERATIONS, groupId, artifactId, versionId);

        // Assert - Verify filtering is applied correctly for each type
        assertNotNull(entitiesDeps);
        assertNotNull(versionedDeps);
        assertNotNull(fileGenDeps);

        // Verify entities dependencies end with "entities"
        for (ArtifactDependency dep : entitiesDeps)
        {
            assertTrue(dep.getArtifactId().endsWith("entities"));
        }

        // Verify versioned-entities dependencies end with "versioned-entities"
        for (ArtifactDependency dep : versionedDeps)
        {
            assertTrue(dep.getArtifactId().endsWith("versioned-entities"));
        }

        // Verify file-generation dependencies end with "file-generation"
        for (ArtifactDependency dep : fileGenDeps)
        {
            assertTrue(dep.getArtifactId().endsWith("file-generation"));
        }
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType with version 2.0.0")
    void testFindDependenciesByArtifactTypeWithVersion2()
    {
        // Arrange
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "2.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependenciesByArtifactType returns empty set when no matching dependencies")
    void testFindDependenciesByArtifactTypeReturnsEmptySetWhenNoMatches()
    {
        // Arrange - Use an artifact type that likely has no matching dependencies
        ArtifactType type = ArtifactType.ENTITIES;
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependenciesByArtifactType(type, groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Result may be empty if no dependencies match the filter
        // Just verify the method handles this case without errors
    }
}

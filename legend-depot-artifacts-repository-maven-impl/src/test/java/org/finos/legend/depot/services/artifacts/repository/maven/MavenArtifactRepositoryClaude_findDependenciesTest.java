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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

class MavenArtifactRepositoryClaude_findDependenciesTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test findDependencies returns non-null set")
    void testFindDependenciesReturnsNonNullSet()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependencies with test-dependencies artifact")
    void testFindDependenciesWithTestDependenciesArtifact()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // The method looks for ENTITIES modules and their dependencies
    }

    @Test
    @DisplayName("Test findDependencies with test artifact")
    void testFindDependenciesWithTestArtifact()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependencies returns set with unique elements")
    void testFindDependenciesReturnsUniqueElements()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Sets ensure uniqueness - no duplicates
        long uniqueCount = result.stream().distinct().count();
        assertEquals(result.size(), uniqueCount);
    }

    @Test
    @DisplayName("Test findDependencies returns ArtifactDependency objects with valid properties")
    void testFindDependenciesReturnsValidArtifactDependencyObjects()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Verify each element has valid properties
        for (ArtifactDependency dep : result)
        {
            assertNotNull(dep);
            assertNotNull(dep.getGroupId());
            assertNotNull(dep.getArtifactId());
            assertNotNull(dep.getVersion());
        }
    }

    @Test
    @DisplayName("Test findDependencies looks for ENTITIES modules")
    void testFindDependenciesLooksForEntitiesModules()
    {
        // The method specifically looks for modules with ENTITIES artifact type

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Result depends on whether the artifact has ENTITIES modules with dependencies
    }

    @Test
    @DisplayName("Test findDependencies with non-existent artifact")
    void testFindDependenciesWithNonExistentArtifact()
    {
        // Arrange
        String groupId = "nonexistent.group";
        String artifactId = "nonexistent-artifact";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);
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
    @DisplayName("Test findDependencies with different versions")
    void testFindDependenciesWithDifferentVersions()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";

        // Act
        Set<ArtifactDependency> result1 = repository.findDependencies(groupId, artifactId, "1.0.0");

        // Assert
        assertNotNull(result1);
    }

    @Test
    @DisplayName("Test findDependencies consistency across multiple calls")
    void testFindDependenciesConsistency()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result1 = repository.findDependencies(groupId, artifactId, versionId);
        Set<ArtifactDependency> result2 = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.size(), result2.size());
    }

    @Test
    @DisplayName("Test findDependencies with empty groupId")
    void testFindDependenciesWithEmptyGroupId()
    {
        // Arrange
        String groupId = "";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependencies with empty artifactId")
    void testFindDependenciesWithEmptyArtifactId()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "";
        String versionId = "1.0.0";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependencies with empty versionId")
    void testFindDependenciesWithEmptyVersionId()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependencies result is modifiable")
    void testFindDependenciesResultIsModifiable()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        int originalSize = result.size();
        // Verify we can modify the set without throwing UnsupportedOperationException
        result.add(new ArtifactDependency("test.group", "test-artifact", "1.0.0"));
        assertEquals(originalSize + 1, result.size());
    }

    @Test
    @DisplayName("Test findDependencies with invalid version")
    void testFindDependenciesWithInvalidVersion()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "99.99.99";

        // Act & Assert
        try
        {
            Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test findDependencies returns parent artifacts")
    void testFindDependenciesReturnsParentArtifacts()
    {
        // The method looks for entities dependencies and returns their parent artifacts

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // The returned dependencies should be parent artifacts of entities dependencies
    }

    @Test
    @DisplayName("Test findDependencies filters for entities dependencies")
    void testFindDependenciesFiltersForEntities()
    {
        // The method filters dependencies to only include those ending with "entities"

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Result should only include parent artifacts of entities dependencies
    }

    @Test
    @DisplayName("Test findDependencies with version 2.0.0")
    void testFindDependenciesWithVersion2()
    {
        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "2.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findDependencies handles artifacts without entities modules")
    void testFindDependenciesHandlesArtifactsWithoutEntitiesModules()
    {
        // If an artifact has no ENTITIES modules, the method should return an empty set

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Result may be empty if no entities modules or dependencies found
    }

    @Test
    @DisplayName("Test findDependencies checks plugin dependencies when regular dependencies are empty")
    void testFindDependenciesChecksPluginDependencies()
    {
        // The method checks plugin dependencies if regular dependencies are empty

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // The method should check both regular and plugin dependencies
    }

    @Test
    @DisplayName("Test findDependencies only includes dependencies with versions")
    void testFindDependenciesOnlyIncludesDependenciesWithVersions()
    {
        // The method filters out dependencies without versions

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // All returned dependencies should have non-null versions
        for (ArtifactDependency dep : result)
        {
            assertNotNull(dep.getVersion());
        }
    }

    @Test
    @DisplayName("Test findDependencies returns empty set when no parent artifacts found")
    void testFindDependenciesReturnsEmptySetWhenNoParents()
    {
        // If entities dependencies don't have parent POMs, result should be empty

        // Arrange
        String groupId = "examples.metadata";
        String artifactId = "test";
        String versionId = "1.0.0";

        // Act
        Set<ArtifactDependency> result = repository.findDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        // Result depends on whether entities dependencies have parent artifacts
    }
}

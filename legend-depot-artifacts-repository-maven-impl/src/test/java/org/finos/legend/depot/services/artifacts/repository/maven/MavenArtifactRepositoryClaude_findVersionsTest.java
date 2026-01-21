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
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class MavenArtifactRepositoryClaude_findVersionsTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test findVersions returns non-null list for valid artifact")
    void testFindVersionsReturnsNonNullList()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findVersions returns non-empty list for artifact with versions")
    void testFindVersionsReturnsNonEmptyList()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Test findVersions returns correct number of versions for test artifact")
    void testFindVersionsReturnsCorrectNumberOfVersions()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test findVersions returns versions in correct order")
    void testFindVersionsReturnsVersionsInCorrectOrder()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("2.0.0", result.get(0).toVersionIdString());
        assertEquals("1.0.0", result.get(1).toVersionIdString());
    }

    @Test
    @DisplayName("Test findVersions returns VersionId objects")
    void testFindVersionsReturnsVersionIdObjects()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        for (VersionId versionId : result)
        {
            assertNotNull(versionId);
            assertTrue(versionId instanceof VersionId);
        }
    }

    @Test
    @DisplayName("Test findVersions for test-dependencies artifact")
    void testFindVersionsForTestDependencies()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1.0.0", result.get(0).toVersionIdString());
    }

    @Test
    @DisplayName("Test findVersions returns empty list for artifact with no release versions")
    void testFindVersionsReturnsEmptyListForNoReleaseVersions()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "art101";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test findVersions returns empty list for non-existent artifact")
    void testFindVersionsReturnsEmptyListForNonExistentArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "non-existent-artifact";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test findVersions returns empty list for non-existent group")
    void testFindVersionsReturnsEmptyListForNonExistentGroup()
    {
        // Arrange
        String group = "non.existent.group";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test findVersions does not include snapshot versions")
    void testFindVersionsDoesNotIncludeSnapshots()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        for (VersionId versionId : result)
        {
            String versionString = versionId.toVersionIdString();
            assertFalse(versionString.contains("SNAPSHOT"));
            assertFalse(versionString.contains("master"));
        }
    }

    @Test
    @DisplayName("Test findVersions consistency across multiple calls")
    void testFindVersionsConsistencyAcrossMultipleCalls()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result1 = repository.findVersions(group, artifact);
        List<VersionId> result2 = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.size(), result2.size());
        for (int i = 0; i < result1.size(); i++)
        {
            assertEquals(result1.get(i).toVersionIdString(), result2.get(i).toVersionIdString());
        }
    }

    @Test
    @DisplayName("Test findVersions with different group and artifact combinations")
    void testFindVersionsWithDifferentCombinations()
    {
        // Test 1: examples.metadata.test
        List<VersionId> result1 = repository.findVersions("examples.metadata", "test");
        assertNotNull(result1);
        assertEquals(2, result1.size());

        // Test 2: examples.metadata.test-dependencies
        List<VersionId> result2 = repository.findVersions("examples.metadata", "test-dependencies");
        assertNotNull(result2);
        assertEquals(1, result2.size());

        // Test 3: examples.metadata.art101
        List<VersionId> result3 = repository.findVersions("examples.metadata", "art101");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }

    @Test
    @DisplayName("Test findVersions version format is valid")
    void testFindVersionsVersionFormatIsValid()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        for (VersionId versionId : result)
        {
            String versionString = versionId.toVersionIdString();
            assertNotNull(versionString);
            assertFalse(versionString.isEmpty());
            assertTrue(versionString.matches("\\d+\\.\\d+\\.\\d+"));
        }
    }

    @Test
    @DisplayName("Test findVersions handles empty artifact name")
    void testFindVersionsHandlesEmptyArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test findVersions handles empty group name")
    void testFindVersionsHandlesEmptyGroup()
    {
        // Arrange
        String group = "";
        String artifact = "test";

        // Act
        List<VersionId> result = repository.findVersions(group, artifact);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

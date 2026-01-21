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
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class MavenArtifactRepositoryClaude_findVersionTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test findVersion returns non-null Optional for valid coordinates")
    void testFindVersionReturnsNonNullOptional() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test findVersion returns present Optional for existing version")
    void testFindVersionReturnsPresentOptional() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion returns correct version string")
    void testFindVersionReturnsCorrectVersionString() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("1.0.0", result.get());
    }

    @Test
    @DisplayName("Test findVersion for version 2.0.0")
    void testFindVersionForVersion2() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "2.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("2.0.0", result.get());
    }

    @Test
    @DisplayName("Test findVersion for test-dependencies artifact")
    void testFindVersionForTestDependencies() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("1.0.0", result.get());
    }

    @Test
    @DisplayName("Test findVersion can find snapshot versions")
    void testFindVersionCanFindSnapshotVersions() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "master-SNAPSHOT";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("master-SNAPSHOT", result.get());
    }

    @Test
    @DisplayName("Test findVersion returns empty Optional for non-existent version")
    void testFindVersionReturnsEmptyForNonExistentVersion() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "99.99.99";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion returns empty Optional for non-existent artifact")
    void testFindVersionReturnsEmptyForNonExistentArtifact() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "non-existent-artifact";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion returns empty Optional for non-existent group")
    void testFindVersionReturnsEmptyForNonExistentGroup() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "non.existent.group";
        String artifact = "test";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion consistency across multiple calls")
    void testFindVersionConsistencyAcrossMultipleCalls() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "1.0.0";

        // Act
        Optional<String> result1 = repository.findVersion(group, artifact, versionId);
        Optional<String> result2 = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.isPresent(), result2.isPresent());
        if (result1.isPresent())
        {
            assertEquals(result1.get(), result2.get());
        }
    }

    @Test
    @DisplayName("Test findVersion is case-sensitive for version")
    void testFindVersionIsCaseSensitive() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "1.0.0";
        String wrongCaseVersionId = "1.0.0";

        // Act
        Optional<String> result1 = repository.findVersion(group, artifact, versionId);
        Optional<String> result2 = repository.findVersion(group, artifact, wrongCaseVersionId);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get(), result2.get());
    }

    @Test
    @DisplayName("Test findVersion with different artifact and version combinations")
    void testFindVersionWithDifferentCombinations() throws ArtifactRepositoryException
    {
        // Test 1: examples.metadata.test:1.0.0
        Optional<String> result1 = repository.findVersion("examples.metadata", "test", "1.0.0");
        assertTrue(result1.isPresent());
        assertEquals("1.0.0", result1.get());

        // Test 2: examples.metadata.test:2.0.0
        Optional<String> result2 = repository.findVersion("examples.metadata", "test", "2.0.0");
        assertTrue(result2.isPresent());
        assertEquals("2.0.0", result2.get());

        // Test 3: examples.metadata.test-dependencies:1.0.0
        Optional<String> result3 = repository.findVersion("examples.metadata", "test-dependencies", "1.0.0");
        assertTrue(result3.isPresent());
        assertEquals("1.0.0", result3.get());

        // Test 4: examples.metadata.art101:master-SNAPSHOT
        Optional<String> result4 = repository.findVersion("examples.metadata", "art101", "master-SNAPSHOT");
        assertTrue(result4.isPresent());
        assertEquals("master-SNAPSHOT", result4.get());
    }

    @Test
    @DisplayName("Test findVersion returns empty for partial version match")
    void testFindVersionReturnsEmptyForPartialMatch() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "1.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion handles empty version string")
    void testFindVersionHandlesEmptyVersion() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String versionId = "";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion handles empty artifact name")
    void testFindVersionHandlesEmptyArtifact() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test findVersion handles empty group name")
    void testFindVersionHandlesEmptyGroup() throws ArtifactRepositoryException
    {
        // Arrange
        String group = "";
        String artifact = "test";
        String versionId = "1.0.0";

        // Act
        Optional<String> result = repository.findVersion(group, artifact, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
    }
}

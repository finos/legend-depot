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

package org.finos.legend.depot.store.resources.artifacts.repository;

import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepositoryResourceClaudeTest
{
    private ArtifactRepository artifactRepository;
    private RepositoryResource resource;

    @BeforeEach
    public void setUp()
    {
        artifactRepository = mock(ArtifactRepository.class);
        resource = new RepositoryResource(artifactRepository);
    }

    /**
     * Test constructor with ArtifactRepository dependency.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#RepositoryResource(ArtifactRepository)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with ArtifactRepository dependency")
    public void testConstructorWithArtifactRepository()
    {
        // Arrange and Act
        RepositoryResource actualResource = new RepositoryResource(artifactRepository);

        // Assert
        assertNotNull(actualResource);
    }

    /**
     * Test getRepositoryVersions with multiple versions.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersions(String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersions with multiple versions")
    public void testGetRepositoryVersionsWithMultipleVersions() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";

        VersionId version1 = mock(VersionId.class);
        VersionId version2 = mock(VersionId.class);
        VersionId version3 = mock(VersionId.class);

        when(version1.toVersionIdString()).thenReturn("1.0.0");
        when(version2.toVersionIdString()).thenReturn("1.1.0");
        when(version3.toVersionIdString()).thenReturn("2.0.0");

        List<VersionId> versions = Arrays.asList(version1, version2, version3);
        when(artifactRepository.findVersions(eq(groupId), eq(artifactId))).thenReturn(versions);

        // Act
        List<String> result = resource.getRepositoryVersions(groupId, artifactId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("1.0.0", result.get(0));
        assertEquals("1.1.0", result.get(1));
        assertEquals("2.0.0", result.get(2));
        verify(artifactRepository, times(1)).findVersions(eq(groupId), eq(artifactId));
    }

    /**
     * Test getRepositoryVersions with empty list.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersions(String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersions with empty list")
    public void testGetRepositoryVersionsWithEmptyList() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";

        when(artifactRepository.findVersions(eq(groupId), eq(artifactId))).thenReturn(Collections.emptyList());

        // Act
        List<String> result = resource.getRepositoryVersions(groupId, artifactId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(artifactRepository, times(1)).findVersions(eq(groupId), eq(artifactId));
    }

    /**
     * Test getRepositoryVersions with single version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersions(String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersions with single version")
    public void testGetRepositoryVersionsWithSingleVersion() throws Exception
    {
        // Arrange
        String groupId = "com.test";
        String artifactId = "my-app";

        VersionId version = mock(VersionId.class);
        when(version.toVersionIdString()).thenReturn("3.5.2");

        when(artifactRepository.findVersions(eq(groupId), eq(artifactId))).thenReturn(Collections.singletonList(version));

        // Act
        List<String> result = resource.getRepositoryVersions(groupId, artifactId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("3.5.2", result.get(0));
        verify(artifactRepository, times(1)).findVersions(eq(groupId), eq(artifactId));
    }

    /**
     * Test getRepositoryVersions throws RuntimeException when ArtifactRepositoryException occurs.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersions(String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersions throws RuntimeException on ArtifactRepositoryException")
    public void testGetRepositoryVersionsThrowsRuntimeExceptionOnArtifactRepositoryException() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String errorMessage = "Repository connection failed";

        when(artifactRepository.findVersions(eq(groupId), eq(artifactId)))
            .thenThrow(new ArtifactRepositoryException(errorMessage));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.getRepositoryVersions(groupId, artifactId)
        );

        assertTrue(exception.getMessage().contains(errorMessage));
        verify(artifactRepository, times(1)).findVersions(eq(groupId), eq(artifactId));
    }

    /**
     * Test getRepositoryVersions throws RuntimeException when generic Exception occurs.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersions(String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersions throws RuntimeException on generic Exception")
    public void testGetRepositoryVersionsThrowsRuntimeExceptionOnGenericException() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String errorMessage = "Unexpected error occurred";

        when(artifactRepository.findVersions(eq(groupId), eq(artifactId)))
            .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.getRepositoryVersions(groupId, artifactId)
        );

        assertTrue(exception.getMessage().contains(errorMessage));
        verify(artifactRepository, times(1)).findVersions(eq(groupId), eq(artifactId));
    }

    /**
     * Test getRepositoryVersion with existing version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersion with existing version")
    public void testGetRepositoryVersionWithExistingVersion() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String expectedVersionString = "1.0.0";

        when(artifactRepository.findVersion(eq(groupId), eq(artifactId), eq(versionId)))
            .thenReturn(Optional.of(expectedVersionString));

        // Act
        Optional<String> result = resource.getRepositoryVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expectedVersionString, result.get());
        verify(artifactRepository, times(1)).findVersion(eq(groupId), eq(artifactId), eq(versionId));
    }

    /**
     * Test getRepositoryVersion with non-existent version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersion with non-existent version")
    public void testGetRepositoryVersionWithNonExistentVersion() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "99.99.99";

        when(artifactRepository.findVersion(eq(groupId), eq(artifactId), eq(versionId)))
            .thenReturn(Optional.empty());

        // Act
        Optional<String> result = resource.getRepositoryVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(artifactRepository, times(1)).findVersion(eq(groupId), eq(artifactId), eq(versionId));
    }

    /**
     * Test getRepositoryVersion with ArtifactRepositoryException returns error message as Optional.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersion returns error message on ArtifactRepositoryException")
    public void testGetRepositoryVersionReturnsErrorMessageOnArtifactRepositoryException() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String errorMessage = "Version not found in repository";

        when(artifactRepository.findVersion(eq(groupId), eq(artifactId), eq(versionId)))
            .thenThrow(new ArtifactRepositoryException(errorMessage));

        // Act
        Optional<String> result = resource.getRepositoryVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(errorMessage, result.get());
        verify(artifactRepository, times(1)).findVersion(eq(groupId), eq(artifactId), eq(versionId));
    }

    /**
     * Test getRepositoryVersion with master-SNAPSHOT version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersion with master-SNAPSHOT version")
    public void testGetRepositoryVersionWithMasterSnapshot() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "master-SNAPSHOT";

        when(artifactRepository.findVersion(eq(groupId), eq(artifactId), eq(versionId)))
            .thenReturn(Optional.of(versionId));

        // Act
        Optional<String> result = resource.getRepositoryVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(versionId, result.get());
        verify(artifactRepository, times(1)).findVersion(eq(groupId), eq(artifactId), eq(versionId));
    }

    /**
     * Test getRepositoryVersions with snapshot versions.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link RepositoryResource#getRepositoryVersions(String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test getRepositoryVersions with snapshot versions")
    public void testGetRepositoryVersionsWithSnapshotVersions() throws Exception
    {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";

        VersionId version1 = mock(VersionId.class);
        VersionId version2 = mock(VersionId.class);

        when(version1.toVersionIdString()).thenReturn("1.0.0-SNAPSHOT");
        when(version2.toVersionIdString()).thenReturn("master-SNAPSHOT");

        List<VersionId> versions = Arrays.asList(version1, version2);
        when(artifactRepository.findVersions(eq(groupId), eq(artifactId))).thenReturn(versions);

        // Act
        List<String> result = resource.getRepositoryVersions(groupId, artifactId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1.0.0-SNAPSHOT", result.get(0));
        assertEquals("master-SNAPSHOT", result.get(1));
        verify(artifactRepository, times(1)).findVersions(eq(groupId), eq(artifactId));
    }
}

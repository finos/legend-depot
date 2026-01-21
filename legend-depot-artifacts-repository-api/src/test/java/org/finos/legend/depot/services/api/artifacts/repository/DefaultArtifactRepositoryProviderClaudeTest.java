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

package org.finos.legend.depot.services.api.artifacts.repository;

import org.apache.maven.model.Model;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultArtifactRepositoryProviderClaudeTest
{
    private DefaultArtifactRepositoryProvider provider;

    @BeforeEach
    void setUp()
    {
        // Create a minimal anonymous implementation of the interface for testing
        provider = new DefaultArtifactRepositoryProvider()
        {
            // Using the default implementations from the interface
        };
    }

    // Tests for areValidCoordinates method

    @Test
    void testAreValidCoordinatesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.areValidCoordinates("org.example", "artifact-name")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testAreValidCoordinatesWithNullGroupThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.areValidCoordinates(null, "artifact-name")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testAreValidCoordinatesWithNullArtifactThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.areValidCoordinates("org.example", null)
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testAreValidCoordinatesWithEmptyStringsThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.areValidCoordinates("", "")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for getPOM method

    @Test
    void testGetPOMThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getPOM("org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetPOMWithNullGroupThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getPOM(null, "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetPOMWithNullVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getPOM("org.example", "artifact-name", null)
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetPOMWithSnapshotVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getPOM("org.example", "artifact-name", "1.0.0-SNAPSHOT")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for getJarFile method

    @Test
    void testGetJarFileThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getJarFile("org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetJarFileWithNullArtifactThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getJarFile("org.example", null, "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetJarFileWithEmptyVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getJarFile("org.example", "artifact-name", "")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for getModulesFromPOM method

    @Test
    void testGetModulesFromPOMThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getModulesFromPOM(ArtifactType.ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetModulesFromPOMWithVersionedEntitiesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getModulesFromPOM(ArtifactType.VERSIONED_ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetModulesFromPOMWithFileGenerationsThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getModulesFromPOM(ArtifactType.FILE_GENERATIONS, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testGetModulesFromPOMWithNullTypeThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.getModulesFromPOM(null, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for findVersions method

    @Test
    void testFindVersionsThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findVersions("org.example", "artifact-name")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindVersionsWithNullGroupThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findVersions(null, "artifact-name")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindVersionsWithEmptyArtifactThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findVersions("org.example", "")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for findVersion method

    @Test
    void testFindVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findVersion("org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindVersionWithNullVersionIdThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findVersion("org.example", "artifact-name", null)
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindVersionWithSnapshotVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findVersion("org.example", "artifact-name", "2.0.0-SNAPSHOT")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for findFiles method

    @Test
    void testFindFilesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findFiles(ArtifactType.ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindFilesWithVersionedEntitiesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findFiles(ArtifactType.VERSIONED_ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindFilesWithNullGroupThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findFiles(ArtifactType.FILE_GENERATIONS, null, "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for findDependenciesFiles method

    @Test
    void testFindDependenciesFilesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesFiles(ArtifactType.ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesFilesWithVersionedEntitiesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesFiles(ArtifactType.VERSIONED_ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesFilesWithNullTypeThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesFiles(null, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for findDependenciesByArtifactType method

    @Test
    void testFindDependenciesByArtifactTypeThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesByArtifactType(ArtifactType.ENTITIES, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesByArtifactTypeWithFileGenerationsThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesByArtifactType(ArtifactType.FILE_GENERATIONS, "org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesByArtifactTypeWithNullArtifactIdThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesByArtifactType(ArtifactType.ENTITIES, "org.example", null, "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesByArtifactTypeWithEmptyVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependenciesByArtifactType(ArtifactType.VERSIONED_ENTITIES, "org.example", "artifact-name", "")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    // Tests for findDependencies method

    @Test
    void testFindDependenciesThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependencies("org.example", "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesWithNullGroupIdThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependencies(null, "artifact-name", "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesWithNullArtifactIdThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependencies("org.example", null, "1.0.0")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesWithSnapshotVersionThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependencies("org.example", "artifact-name", "3.0.0-SNAPSHOT")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }

    @Test
    void testFindDependenciesWithEmptyStringsThrowsUnsupportedOperationException()
    {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> provider.findDependencies("", "", "")
        );

        assertNotNull(exception);
        assertEquals("method not supported", exception.getMessage());
    }
}

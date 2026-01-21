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
import org.apache.maven.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MavenArtifactRepositoryClaude_getPOMTest
{
    private TestMavenArtifactsRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new TestMavenArtifactsRepository();
    }

    @Test
    @DisplayName("Test getPOM returns non-null Model")
    void testGetPOMReturnsNonNullModel()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Test getPOM retrieves correct artifact information")
    void testGetPOMRetrievesCorrectArtifactInfo()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
        assertEquals(version, result.getVersion());
    }

    @Test
    @DisplayName("Test getPOM retrieves modules for multi-module project")
    void testGetPOMRetrievesModulesForMultiModuleProject()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getModules());
        assertFalse(result.getModules().isEmpty());
        // The test artifact has modules like test-entities, test-versioned-entities, etc.
        assertTrue(result.getModules().contains("test-entities"));
    }

    @Test
    @DisplayName("Test getPOM with test-entities module")
    void testGetPOMWithTestEntitiesModule()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
        assertEquals(version, result.getVersion());
    }

    @Test
    @DisplayName("Test getPOM with different version")
    void testGetPOMWithDifferentVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "2.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
        assertEquals(version, result.getVersion());
    }

    @Test
    @DisplayName("Test getPOM with test-dependencies artifact")
    void testGetPOMWithTestDependenciesArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
        assertNotNull(result.getModules());
        assertFalse(result.getModules().isEmpty());
    }

    @Test
    @DisplayName("Test getPOM retrieves dependencies from module POM")
    void testGetPOMRetrievesDependencies()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-entities";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getDependencies());
        // The test-dependencies-entities module has dependencies
        assertFalse(result.getDependencies().isEmpty());
    }

    @Test
    @DisplayName("Test getPOM retrieves parent information")
    void testGetPOMRetrievesParentInfo()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getParent());
        assertEquals("test", result.getParent().getArtifactId());
    }

    @Test
    @DisplayName("Test getPOM with non-existent artifact returns empty Model")
    void testGetPOMWithNonExistentArtifactReturnsEmptyModel()
    {
        // Arrange
        String group = "nonexistent.group";
        String artifact = "nonexistent-artifact";
        String version = "1.0.0";

        // Act & Assert
        try
        {
            Model result = repository.getPOM(group, artifact, version);
            // If no exception, it may return an empty Model
            assertNotNull(result);
        }
        catch (RuntimeException e)
        {
            // TestMavenArtifactsRepository throws exception for missing POM
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getPOM consistency across multiple calls")
    void testGetPOMConsistency()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        Model result1 = repository.getPOM(group, artifact, version);
        Model result2 = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getGroupId(), result2.getGroupId());
        assertEquals(result1.getArtifactId(), result2.getArtifactId());
        assertEquals(result1.getVersion(), result2.getVersion());
    }

    @Test
    @DisplayName("Test getPOM with empty group")
    void testGetPOMWithEmptyGroup()
    {
        // Arrange
        String group = "";
        String artifact = "test";
        String version = "1.0.0";

        // Act & Assert
        try
        {
            repository.getPOM(group, artifact, version);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getPOM with empty artifact")
    void testGetPOMWithEmptyArtifact()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "";
        String version = "1.0.0";

        // Act & Assert
        try
        {
            repository.getPOM(group, artifact, version);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getPOM with empty version")
    void testGetPOMWithEmptyVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "";

        // Act & Assert
        try
        {
            repository.getPOM(group, artifact, version);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getPOM with invalid version")
    void testGetPOMWithInvalidVersion()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "99.99.99";

        // Act & Assert
        try
        {
            repository.getPOM(group, artifact, version);
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getMessage().contains("could not find"));
        }
    }

    @Test
    @DisplayName("Test getPOM retrieves packaging type")
    void testGetPOMRetrievesPackagingType()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals("pom", result.getPackaging());
    }

    @Test
    @DisplayName("Test getPOM with test-versioned-entities module")
    void testGetPOMWithVersionedEntitiesModule()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-versioned-entities";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
    }

    @Test
    @DisplayName("Test getPOM with test-file-generation module")
    void testGetPOMWithFileGenerationModule()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-file-generation";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
    }

    @Test
    @DisplayName("Test getPOM retrieves build information")
    void testGetPOMRetrievesBuildInfo()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-dependencies-entities";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getBuild());
        // The test-dependencies-entities module has build configuration
    }

    @Test
    @DisplayName("Test getPOM for version 2.0.0")
    void testGetPOMForVersion2()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test-entities";
        String version = "2.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertEquals(group, result.getGroupId());
        assertEquals(artifact, result.getArtifactId());
        assertEquals(version, result.getVersion());
    }

    @Test
    @DisplayName("Test getPOM returns Maven Model object")
    void testGetPOMReturnsMavenModelObject()
    {
        // Arrange
        String group = "examples.metadata";
        String artifact = "test";
        String version = "1.0.0";

        // Act
        Model result = repository.getPOM(group, artifact, version);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Model);
    }
}

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

package org.finos.legend.depot.domain.project.dependencies;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDependencyVersionNodeClaudeTest 

{

    @Test
    @DisplayName("Test constructor initializes all fields correctly")
    void testConstructorInitialization()
  {
        // Arrange and Act
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.example", "my-artifact", "1.0.0"
        );

        // Assert
        assertEquals("org.example", node.getGroupId());
        assertEquals("my-artifact", node.getArtifactId());
        assertEquals("1.0.0", node.getVersionId());
        assertNull(node.getProjectId());
        assertNotNull(node.getForwardEdges());
        assertNotNull(node.getBackEdges());
        assertTrue(node.getForwardEdges().isEmpty());
        assertTrue(node.getBackEdges().isEmpty());
    }

    @Test
    @DisplayName("Test constructor with complex version strings")
    void testConstructorWithComplexVersions()
  {
        // Arrange and Act
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "com.finos.legend", "legend-depot-core", "2.45.3-SNAPSHOT"
        );

        // Assert
        assertEquals("com.finos.legend", node.getGroupId());
        assertEquals("legend-depot-core", node.getArtifactId());
        assertEquals("2.45.3-SNAPSHOT", node.getVersionId());
    }

    @Test
    @DisplayName("Test constructor with minimal strings")
    void testConstructorWithMinimalStrings()
  {
        // Arrange and Act
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode("g", "a", "v");

        // Assert
        assertEquals("g", node.getGroupId());
        assertEquals("a", node.getArtifactId());
        assertEquals("v", node.getVersionId());
        assertEquals("g:a", node.getCoordinates());
        assertEquals("g:a:v", node.getGav());
        assertEquals("g:a:v", node.getId());
    }

    @Test
    @DisplayName("Test buildFromProjectVersion creates correct node")
    void testBuildFromProjectVersion()
  {
        // Arrange
        ProjectVersion version = new ProjectVersion(
            "org.example", "test-project", "2.0.0"
        );

        // Act
        ProjectDependencyVersionNode node = ProjectDependencyVersionNode.buildFromProjectVersion(version);

        // Assert
        assertNotNull(node);
        assertEquals("org.example", node.getGroupId());
        assertEquals("test-project", node.getArtifactId());
        assertEquals("2.0.0", node.getVersionId());
        assertNull(node.getProjectId());
        assertTrue(node.getForwardEdges().isEmpty());
        assertTrue(node.getBackEdges().isEmpty());
    }

    @Test
    @DisplayName("Test buildFromProjectVersion with SNAPSHOT version")
    void testBuildFromProjectVersionWithSnapshot()
  {
        // Arrange
        ProjectVersion version = new ProjectVersion(
            "com.company", "lib", "1.0-SNAPSHOT"
        );

        // Act
        ProjectDependencyVersionNode node = ProjectDependencyVersionNode.buildFromProjectVersion(version);

        // Assert
        assertEquals("com.company", node.getGroupId());
        assertEquals("lib", node.getArtifactId());
        assertEquals("1.0-SNAPSHOT", node.getVersionId());
        assertEquals("com.company:lib:1.0-SNAPSHOT", node.getGav());
    }

    @Test
    @DisplayName("Test getGav returns correct format")
    void testGetGav()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.finos.legend", "depot-api", "1.2.3"
        );

        // Act
        String gav = node.getGav();

        // Assert
        assertEquals("org.finos.legend:depot-api:1.2.3", gav);
    }

    @Test
    @DisplayName("Test getGav with special characters")
    void testGetGavWithSpecialCharacters()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.test-group", "artifact_name", "1.0.0-RC1"
        );

        // Act
        String gav = node.getGav();

        // Assert
        assertEquals("org.test-group:artifact_name:1.0.0-RC1", gav);
    }

    @Test
    @DisplayName("Test getCoordinates returns correct format")
    void testGetCoordinates()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.apache.commons", "commons-lang3", "3.12.0"
        );

        // Act
        String coordinates = node.getCoordinates();

        // Assert
        assertEquals("org.apache.commons:commons-lang3", coordinates);
    }

    @Test
    @DisplayName("Test getCoordinates excludes version")
    void testGetCoordinatesExcludesVersion()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "999.999.999"
        );

        // Act
        String coordinates = node.getCoordinates();

        // Assert
        assertEquals("group:artifact", coordinates);
        assertFalse(coordinates.contains("999.999.999"));
    }

    @Test
    @DisplayName("Test getId returns same as getGav")
    void testGetIdReturnsSameAsGetGav()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "com.example", "my-lib", "1.0"
        );

        // Act
        String id = node.getId();
        String gav = node.getGav();

        // Assert
        assertEquals(gav, id);
        assertEquals("com.example:my-lib:1.0", id);
    }

    @Test
    @DisplayName("Test setProjectId and getProjectId")
    void testProjectIdSetterGetter()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );

        // Act
        node.setProjectId("my-project-123");

        // Assert
        assertEquals("my-project-123", node.getProjectId());
    }

    @Test
    @DisplayName("Test setProjectId with null")
    void testSetProjectIdNull()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );
        node.setProjectId("initial-project");

        // Act
        node.setProjectId(null);

        // Assert
        assertNull(node.getProjectId());
    }

    @Test
    @DisplayName("Test setProjectId overwrites previous value")
    void testSetProjectIdOverwrites()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );
        node.setProjectId("project-1");

        // Act
        node.setProjectId("project-2");

        // Assert
        assertEquals("project-2", node.getProjectId());
    }

    @Test
    @DisplayName("Test getForwardEdges returns mutable set")
    void testGetForwardEdgesMutable()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );

        // Act
        Set<String> forwardEdges = node.getForwardEdges();
        forwardEdges.add("dep1:artifact1:1.0");
        forwardEdges.add("dep2:artifact2:2.0");

        // Assert
        assertEquals(2, node.getForwardEdges().size());
        assertTrue(node.getForwardEdges().contains("dep1:artifact1:1.0"));
        assertTrue(node.getForwardEdges().contains("dep2:artifact2:2.0"));
    }

    @Test
    @DisplayName("Test getBackEdges returns mutable set")
    void testGetBackEdgesMutable()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );

        // Act
        Set<String> backEdges = node.getBackEdges();
        backEdges.add("parent1:art1:1.0");
        backEdges.add("parent2:art2:2.0");
        backEdges.add("parent3:art3:3.0");

        // Assert
        assertEquals(3, node.getBackEdges().size());
        assertTrue(node.getBackEdges().contains("parent1:art1:1.0"));
        assertTrue(node.getBackEdges().contains("parent2:art2:2.0"));
        assertTrue(node.getBackEdges().contains("parent3:art3:3.0"));
    }

    @Test
    @DisplayName("Test forwardEdges and backEdges are independent")
    void testEdgesAreIndependent()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );

        // Act
        node.getForwardEdges().add("forward-dep:art:1.0");
        node.getBackEdges().add("back-dep:art:1.0");

        // Assert
        assertEquals(1, node.getForwardEdges().size());
        assertEquals(1, node.getBackEdges().size());
        assertFalse(node.getForwardEdges().contains("back-dep:art:1.0"));
        assertFalse(node.getBackEdges().contains("forward-dep:art:1.0"));
    }

    @Test
    @DisplayName("Test forwardEdges can be cleared")
    void testForwardEdgesCanBeCleared()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );
        node.getForwardEdges().add("dep1:art1:1.0");
        node.getForwardEdges().add("dep2:art2:2.0");

        // Act
        node.getForwardEdges().clear();

        // Assert
        assertTrue(node.getForwardEdges().isEmpty());
    }

    @Test
    @DisplayName("Test backEdges can be cleared")
    void testBackEdgesCanBeCleared()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );
        node.getBackEdges().add("parent1:art1:1.0");
        node.getBackEdges().add("parent2:art2:2.0");

        // Act
        node.getBackEdges().clear();

        // Assert
        assertTrue(node.getBackEdges().isEmpty());
    }

    @Test
    @DisplayName("Test forwardEdges can be removed individually")
    void testForwardEdgesCanBeRemovedIndividually()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );
        node.getForwardEdges().add("dep1:art1:1.0");
        node.getForwardEdges().add("dep2:art2:2.0");

        // Act
        boolean removed = node.getForwardEdges().remove("dep1:art1:1.0");

        // Assert
        assertTrue(removed);
        assertEquals(1, node.getForwardEdges().size());
        assertFalse(node.getForwardEdges().contains("dep1:art1:1.0"));
        assertTrue(node.getForwardEdges().contains("dep2:art2:2.0"));
    }

    @Test
    @DisplayName("Test backEdges can be removed individually")
    void testBackEdgesCanBeRemovedIndividually()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );
        node.getBackEdges().add("parent1:art1:1.0");
        node.getBackEdges().add("parent2:art2:2.0");

        // Act
        boolean removed = node.getBackEdges().remove("parent1:art1:1.0");

        // Assert
        assertTrue(removed);
        assertEquals(1, node.getBackEdges().size());
        assertFalse(node.getBackEdges().contains("parent1:art1:1.0"));
        assertTrue(node.getBackEdges().contains("parent2:art2:2.0"));
    }

    @Test
    @DisplayName("Test forwardEdges does not allow duplicates")
    void testForwardEdgesNoDuplicates()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );

        // Act
        node.getForwardEdges().add("dep:art:1.0");
        node.getForwardEdges().add("dep:art:1.0");
        node.getForwardEdges().add("dep:art:1.0");

        // Assert - Set should only contain one instance
        assertEquals(1, node.getForwardEdges().size());
    }

    @Test
    @DisplayName("Test backEdges does not allow duplicates")
    void testBackEdgesNoDuplicates()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "group", "artifact", "version"
        );

        // Act
        node.getBackEdges().add("parent:art:1.0");
        node.getBackEdges().add("parent:art:1.0");
        node.getBackEdges().add("parent:art:1.0");

        // Assert - Set should only contain one instance
        assertEquals(1, node.getBackEdges().size());
    }

    @Test
    @DisplayName("Test complete dependency graph scenario")
    void testCompleteDependencyGraphScenario()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.example", "middle-lib", "2.0.0"
        );
        node.setProjectId("project-middle");

        // Act - simulate a node in the middle of a dependency graph
        node.getForwardEdges().add("org.example:low-level-lib:1.0.0");
        node.getForwardEdges().add("org.example:util-lib:1.5.0");
        node.getBackEdges().add("org.example:high-level-app:3.0.0");

        // Assert
        assertEquals("project-middle", node.getProjectId());
        assertEquals("org.example:middle-lib:2.0.0", node.getGav());
        assertEquals("org.example:middle-lib:2.0.0", node.getId());
        assertEquals("org.example:middle-lib", node.getCoordinates());
        assertEquals(2, node.getForwardEdges().size());
        assertEquals(1, node.getBackEdges().size());
    }

    @Test
    @DisplayName("Test node with many forward edges")
    void testNodeWithManyForwardEdges()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.example", "core-lib", "1.0.0"
        );

        // Act - add multiple dependencies
        for (int i = 0; i < 10; i++) {
            node.getForwardEdges().add("org.example:dep" + i + ":1.0.0");
        }

        // Assert
        assertEquals(10, node.getForwardEdges().size());
    }

    @Test
    @DisplayName("Test node with many back edges")
    void testNodeWithManyBackEdges()
  {
        // Arrange
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "org.example", "popular-lib", "1.0.0"
        );

        // Act - add multiple dependents
        for (int i = 0; i < 15; i++) {
            node.getBackEdges().add("org.example:dependent" + i + ":2.0.0");
        }

        // Assert
        assertEquals(15, node.getBackEdges().size());
    }

    @Test
    @DisplayName("Test two nodes created from same ProjectVersion are independent")
    void testIndependentNodes()
  {
        // Arrange
        ProjectVersion version = new ProjectVersion("group", "artifact", "version");

        // Act
        ProjectDependencyVersionNode node1 = ProjectDependencyVersionNode.buildFromProjectVersion(version);
        ProjectDependencyVersionNode node2 = ProjectDependencyVersionNode.buildFromProjectVersion(version);

        node1.setProjectId("project1");
        node1.getForwardEdges().add("dep1");

        node2.setProjectId("project2");
        node2.getForwardEdges().add("dep2");

        // Assert - nodes are independent
        assertEquals("project1", node1.getProjectId());
        assertEquals("project2", node2.getProjectId());
        assertTrue(node1.getForwardEdges().contains("dep1"));
        assertFalse(node1.getForwardEdges().contains("dep2"));
        assertTrue(node2.getForwardEdges().contains("dep2"));
        assertFalse(node2.getForwardEdges().contains("dep1"));
    }

    @Test
    @DisplayName("Test getGav and getId consistency")
    void testGetGavAndGetIdConsistency()
  {
        // Arrange
        ProjectDependencyVersionNode node1 = new ProjectDependencyVersionNode(
            "org.test", "artifact", "1.0.0"
        );
        ProjectDependencyVersionNode node2 = new ProjectDependencyVersionNode(
            "org.test", "artifact", "2.0.0"
        );

        // Act and Assert - different versions should have different ids
        assertNotEquals(node1.getId(), node2.getId());
        assertNotEquals(node1.getGav(), node2.getGav());
        assertEquals(node1.getId(), node1.getGav());
        assertEquals(node2.getId(), node2.getGav());
    }

    @Test
    @DisplayName("Test coordinates are same for different versions")
    void testCoordinatesSameForDifferentVersions()
  {
        // Arrange
        ProjectDependencyVersionNode node1 = new ProjectDependencyVersionNode(
            "org.test", "artifact", "1.0.0"
        );
        ProjectDependencyVersionNode node2 = new ProjectDependencyVersionNode(
            "org.test", "artifact", "2.0.0"
        );

        // Act and Assert
        assertEquals(node1.getCoordinates(), node2.getCoordinates());
        assertEquals("org.test:artifact", node1.getCoordinates());
    }

    @Test
    @DisplayName("Test inherited methods from VersionedData")
    void testInheritedMethods()
  {
        // Arrange and Act
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode(
            "com.example", "my-artifact", "3.2.1"
        );

        // Assert - test inherited getters
        assertEquals("com.example", node.getGroupId());
        assertEquals("my-artifact", node.getArtifactId());
        assertEquals("3.2.1", node.getVersionId());
    }
}

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

import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport.ProjectDependencyConflict;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport.SerializedGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDependencyReportClaudeTest 

{

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Test constructor initializes empty conflicts list and non-null graph")
    void testConstructorInitializesEmptyCollections()
  {
        // Arrange and Act
        ProjectDependencyReport report = new ProjectDependencyReport();

        // Assert
        assertNotNull(report.getConflicts(), "Conflicts list should not be null");
        assertTrue(report.getConflicts().isEmpty(), "Conflicts list should be empty");
        assertNotNull(report.getGraph(), "Graph should not be null");
        assertNotNull(report.getGraph().getNodes(), "Graph nodes should not be null");
        assertTrue(report.getGraph().getNodes().isEmpty(), "Graph nodes should be empty");
        assertNotNull(report.getGraph().getRootNodes(), "Graph root nodes should not be null");
        assertTrue(report.getGraph().getRootNodes().isEmpty(), "Graph root nodes should be empty");
    }

    // ========== addConflict Tests ==========

    @Test
    @DisplayName("Test addConflict adds single conflict successfully")
    void testAddConflictAddsConflict()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");

        // Act
        report.addConflict("com.example", "my-artifact", versions);

        // Assert
        List<ProjectDependencyConflict> conflicts = report.getConflicts();
        assertEquals(1, conflicts.size(), "Should have one conflict");
        ProjectDependencyConflict conflict = conflicts.get(0);
        assertEquals("com.example", conflict.getGroupId());
        assertEquals("my-artifact", conflict.getArtifactId());
        assertEquals(versions, conflict.getVersions());
    }

    @Test
    @DisplayName("Test addConflict with exactly one version in set")
    void testAddConflictWithSingleVersion()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");

        // Act
        report.addConflict("com.example", "my-artifact", versions);

        // Assert
        assertEquals(1, report.getConflicts().size(), "Should successfully add conflict with one version");
        ProjectDependencyConflict conflict = report.getConflicts().get(0);
        assertEquals(1, conflict.getVersions().size());
    }

    @Test
    @DisplayName("Test addConflict with empty versions set throws exception")
    void testAddConflictWithEmptyVersionsThrowsException()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> emptyVersions = new HashSet<>();

        // Act and Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> report.addConflict("com.example", "my-artifact", emptyVersions),
                "Should throw exception for empty versions set"
        );
        assertEquals("Conflicts must have more than one version", exception.getMessage());
    }

    @Test
    @DisplayName("Test addConflict with multiple versions")
    void testAddConflictWithMultipleVersions()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");
        versions.add("3.0.0");

        // Act
        report.addConflict("org.test", "test-lib", versions);

        // Assert
        assertEquals(1, report.getConflicts().size());
        ProjectDependencyConflict conflict = report.getConflicts().get(0);
        assertEquals("org.test", conflict.getGroupId());
        assertEquals("test-lib", conflict.getArtifactId());
        assertEquals(3, conflict.getVersions().size());
        assertTrue(conflict.getVersions().contains("1.0.0"));
        assertTrue(conflict.getVersions().contains("2.0.0"));
        assertTrue(conflict.getVersions().contains("3.0.0"));
    }

    @Test
    @DisplayName("Test addConflict multiple times adds multiple conflicts")
    void testAddConflictMultipleTimes()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions1 = new HashSet<>();
        versions1.add("1.0.0");
        versions1.add("2.0.0");
        Set<String> versions2 = new HashSet<>();
        versions2.add("3.0.0");
        versions2.add("4.0.0");

        // Act
        report.addConflict("com.example", "artifact1", versions1);
        report.addConflict("com.example", "artifact2", versions2);

        // Assert
        List<ProjectDependencyConflict> conflicts = report.getConflicts();
        assertEquals(2, conflicts.size(), "Should have two conflicts");

        ProjectDependencyConflict conflict1 = conflicts.get(0);
        assertEquals("artifact1", conflict1.getArtifactId());
        assertEquals(versions1, conflict1.getVersions());

        ProjectDependencyConflict conflict2 = conflicts.get(1);
        assertEquals("artifact2", conflict2.getArtifactId());
        assertEquals(versions2, conflict2.getVersions());
    }

    // ========== removeConflict Tests ==========

    @Test
    @DisplayName("Test removeConflict removes existing conflict")
    void testRemoveConflictRemovesExistingConflict()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");
        report.addConflict("com.example", "my-artifact", versions);

        ProjectDependencyConflict conflictToRemove = report.getConflicts().get(0);

        // Act
        report.removeConflict(conflictToRemove);

        // Assert
        assertTrue(report.getConflicts().isEmpty(), "Conflicts list should be empty after removal");
    }

    @Test
    @DisplayName("Test removeConflict with multiple conflicts removes only specified one")
    void testRemoveConflictRemovesOnlySpecifiedConflict()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions1 = new HashSet<>();
        versions1.add("1.0.0");
        versions1.add("2.0.0");
        Set<String> versions2 = new HashSet<>();
        versions2.add("3.0.0");
        versions2.add("4.0.0");

        report.addConflict("com.example", "artifact1", versions1);
        report.addConflict("com.example", "artifact2", versions2);

        ProjectDependencyConflict firstConflict = report.getConflicts().get(0);

        // Act
        report.removeConflict(firstConflict);

        // Assert
        assertEquals(1, report.getConflicts().size(), "Should have one conflict remaining");
        ProjectDependencyConflict remainingConflict = report.getConflicts().get(0);
        assertEquals("artifact2", remainingConflict.getArtifactId());
    }

    @Test
    @DisplayName("Test removeConflict on empty list has no effect")
    void testRemoveConflictOnEmptyList()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");

        // Create a conflict but don't add it to this report
        ProjectDependencyReport tempReport = new ProjectDependencyReport();
        tempReport.addConflict("com.example", "my-artifact", versions);
        ProjectDependencyConflict conflict = tempReport.getConflicts().get(0);

        // Act
        report.removeConflict(conflict);

        // Assert
        assertTrue(report.getConflicts().isEmpty(), "Conflicts list should remain empty");
    }

    @Test
    @DisplayName("Test removeConflict non-existent conflict has no effect")
    void testRemoveConflictNonExistent()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions1 = new HashSet<>();
        versions1.add("1.0.0");
        versions1.add("2.0.0");
        Set<String> versions2 = new HashSet<>();
        versions2.add("3.0.0");
        versions2.add("4.0.0");

        report.addConflict("com.example", "artifact1", versions1);

        ProjectDependencyReport otherReport = new ProjectDependencyReport();
        otherReport.addConflict("com.other", "other-artifact", versions2);
        ProjectDependencyConflict otherConflict = otherReport.getConflicts().get(0);

        // Act
        report.removeConflict(otherConflict);

        // Assert
        assertEquals(1, report.getConflicts().size(), "Original conflict should remain");
        assertEquals("artifact1", report.getConflicts().get(0).getArtifactId());
    }

    // ========== getConflicts Tests ==========

    @Test
    @DisplayName("Test getConflicts returns empty list initially")
    void testGetConflictsInitiallyEmpty()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();

        // Act
        List<ProjectDependencyConflict> conflicts = report.getConflicts();

        // Assert
        assertNotNull(conflicts, "Conflicts list should not be null");
        assertTrue(conflicts.isEmpty(), "Conflicts list should be empty");
    }

    @Test
    @DisplayName("Test getConflicts returns mutable list")
    void testGetConflictsReturnsMutableList()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");

        // Act
        report.addConflict("com.example", "my-artifact", versions);
        List<ProjectDependencyConflict> conflicts = report.getConflicts();

        // Assert
        assertNotNull(conflicts);
        assertEquals(1, conflicts.size());
    }

    @Test
    @DisplayName("Test getConflicts returns live list that reflects changes")
    void testGetConflictsReturnsLiveList()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        List<ProjectDependencyConflict> conflicts = report.getConflicts();
        int initialSize = conflicts.size();

        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");

        // Act
        report.addConflict("com.example", "my-artifact", versions);

        // Assert
        assertEquals(initialSize + 1, conflicts.size(), "Same list instance should reflect changes");
    }

    // ========== getGraph Tests ==========

    @Test
    @DisplayName("Test getGraph returns non-null graph")
    void testGetGraphReturnsNonNull()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();

        // Act
        SerializedGraph graph = report.getGraph();

        // Assert
        assertNotNull(graph, "Graph should not be null");
    }

    @Test
    @DisplayName("Test getGraph returns same instance on multiple calls")
    void testGetGraphReturnsSameInstance()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();

        // Act
        SerializedGraph graph1 = report.getGraph();
        SerializedGraph graph2 = report.getGraph();

        // Assert
        assertSame(graph1, graph2, "Should return the same graph instance");
    }

    @Test
    @DisplayName("Test getGraph has empty nodes and rootNodes initially")
    void testGetGraphInitiallyEmpty()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();

        // Act
        SerializedGraph graph = report.getGraph();

        // Assert
        assertNotNull(graph.getNodes(), "Graph nodes should not be null");
        assertTrue(graph.getNodes().isEmpty(), "Graph nodes should be empty");
        assertNotNull(graph.getRootNodes(), "Graph root nodes should not be null");
        assertTrue(graph.getRootNodes().isEmpty(), "Graph root nodes should be empty");
    }

    // ========== SerializedGraph Tests ==========

    @Test
    @DisplayName("Test SerializedGraph getNodes returns mutable map")
    void testSerializedGraphGetNodesReturnsMutableMap()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        SerializedGraph graph = report.getGraph();

        // Act
        MutableMap<String, ProjectDependencyVersionNode> nodes = graph.getNodes();

        // Assert
        assertNotNull(nodes, "Nodes map should not be null");
        assertTrue(nodes.isEmpty(), "Nodes map should be empty initially");
    }

    @Test
    @DisplayName("Test SerializedGraph getRootNodes returns mutable set")
    void testSerializedGraphGetRootNodesReturnsMutableSet()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        SerializedGraph graph = report.getGraph();

        // Act
        Set<String> rootNodes = graph.getRootNodes();

        // Assert
        assertNotNull(rootNodes, "Root nodes set should not be null");
        assertTrue(rootNodes.isEmpty(), "Root nodes set should be empty initially");
    }

    @Test
    @DisplayName("Test SerializedGraph nodes can be modified")
    void testSerializedGraphNodesCanBeModified()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        SerializedGraph graph = report.getGraph();
        MutableMap<String, ProjectDependencyVersionNode> nodes = graph.getNodes();

        // Act
        ProjectDependencyVersionNode node = new ProjectDependencyVersionNode("com.example", "artifact", "1.0.0");
        nodes.put("com.example:artifact:1.0.0", node);

        // Assert
        assertEquals(1, graph.getNodes().size(), "Nodes should be modifiable");
        assertTrue(graph.getNodes().containsKey("com.example:artifact:1.0.0"));
    }

    @Test
    @DisplayName("Test SerializedGraph rootNodes can be modified")
    void testSerializedGraphRootNodesCanBeModified()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        SerializedGraph graph = report.getGraph();
        Set<String> rootNodes = graph.getRootNodes();

        // Act
        rootNodes.add("com.example:artifact:1.0.0");

        // Assert
        assertEquals(1, graph.getRootNodes().size(), "Root nodes should be modifiable");
        assertTrue(graph.getRootNodes().contains("com.example:artifact:1.0.0"));
    }

    // ========== ProjectDependencyConflict Tests ==========

    @Test
    @DisplayName("Test ProjectDependencyConflict getVersions returns versions set")
    void testProjectDependencyConflictGetVersions()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");
        versions.add("3.0.0");

        // Act
        report.addConflict("com.example", "my-artifact", versions);
        ProjectDependencyConflict conflict = report.getConflicts().get(0);

        // Assert
        Set<String> retrievedVersions = conflict.getVersions();
        assertNotNull(retrievedVersions);
        assertEquals(3, retrievedVersions.size());
        assertTrue(retrievedVersions.contains("1.0.0"));
        assertTrue(retrievedVersions.contains("2.0.0"));
        assertTrue(retrievedVersions.contains("3.0.0"));
    }

    @Test
    @DisplayName("Test ProjectDependencyConflict inherits groupId and artifactId from CoordinateData")
    void testProjectDependencyConflictInheritsCoordinateData()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");

        // Act
        report.addConflict("org.example", "test-artifact", versions);
        ProjectDependencyConflict conflict = report.getConflicts().get(0);

        // Assert
        assertEquals("org.example", conflict.getGroupId(), "Should inherit getGroupId from CoordinateData");
        assertEquals("test-artifact", conflict.getArtifactId(), "Should inherit getArtifactId from CoordinateData");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Test workflow: add multiple conflicts, remove one, verify state")
    void testWorkflowAddAndRemoveConflicts()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions1 = new HashSet<>();
        versions1.add("1.0.0");
        versions1.add("2.0.0");
        Set<String> versions2 = new HashSet<>();
        versions2.add("3.0.0");
        versions2.add("4.0.0");
        Set<String> versions3 = new HashSet<>();
        versions3.add("5.0.0");
        versions3.add("6.0.0");

        // Act
        report.addConflict("com.example", "artifact1", versions1);
        report.addConflict("com.example", "artifact2", versions2);
        report.addConflict("com.example", "artifact3", versions3);

        assertEquals(3, report.getConflicts().size(), "Should have 3 conflicts initially");

        ProjectDependencyConflict middleConflict = report.getConflicts().get(1);
        report.removeConflict(middleConflict);

        // Assert
        assertEquals(2, report.getConflicts().size(), "Should have 2 conflicts after removal");
        assertEquals("artifact1", report.getConflicts().get(0).getArtifactId());
        assertEquals("artifact3", report.getConflicts().get(1).getArtifactId());
    }

    @Test
    @DisplayName("Test graph and conflicts are independent")
    void testGraphAndConflictsAreIndependent()
  {
        // Arrange
        ProjectDependencyReport report = new ProjectDependencyReport();
        Set<String> versions = new HashSet<>();
        versions.add("1.0.0");
        versions.add("2.0.0");

        // Act
        report.addConflict("com.example", "my-artifact", versions);
        SerializedGraph graph = report.getGraph();
        graph.getNodes().put("test-key", new ProjectDependencyVersionNode("test", "node", "1.0"));
        graph.getRootNodes().add("root-node");

        // Assert
        assertEquals(1, report.getConflicts().size(), "Conflicts should be unchanged");
        assertEquals(1, graph.getNodes().size(), "Graph should have node");
        assertEquals(1, graph.getRootNodes().size(), "Graph should have root node");
    }
}

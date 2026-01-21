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
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDependencyGraphClaudeTest 

{

    @Test
    @DisplayName("Test default constructor initializes empty collections")
    void testDefaultConstructor()
  {
        // Arrange and Act
        ProjectDependencyGraph graph = new ProjectDependencyGraph();

        // Assert
        assertNotNull(graph.getNodes());
        assertTrue(graph.getNodes().isEmpty());
        assertNotNull(graph.getRootNodes());
        assertTrue(graph.getRootNodes().isEmpty());
        assertNotNull(graph.getForwardEdges());
        assertTrue(graph.getForwardEdges().isEmpty());
        assertNotNull(graph.getBackEdges());
        assertTrue(graph.getBackEdges().isEmpty());
    }

    @Test
    @DisplayName("Test getNodes returns the nodes set")
    void testGetNodes()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion node2 = new ProjectVersion("group2", "artifact2", "2.0.0");

        graph.addNode(node1, null);
        graph.addNode(node2, node1);

        // Act
        Set<ProjectVersion> nodes = graph.getNodes();

        // Assert
        assertNotNull(nodes);
        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
    }

    @Test
    @DisplayName("Test getRootNodes returns the root nodes set")
    void testGetRootNodes()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion root1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion root2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        ProjectVersion child = new ProjectVersion("group3", "artifact3", "3.0.0");

        graph.addNode(root1, null);
        graph.addNode(root2, null);
        graph.addNode(child, root1);

        // Act
        Set<ProjectVersion> rootNodes = graph.getRootNodes();

        // Assert
        assertNotNull(rootNodes);
        assertEquals(2, rootNodes.size());
        assertTrue(rootNodes.contains(root1));
        assertTrue(rootNodes.contains(root2));
        assertFalse(rootNodes.contains(child));
    }

    @Test
    @DisplayName("Test getForwardEdges returns the forward edges map")
    void testGetForwardEdges()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to = new ProjectVersion("group2", "artifact2", "2.0.0");

        graph.setEdges(from, to);

        // Act
        MutableMap<ProjectVersion, Set<ProjectVersion>> forwardEdges = graph.getForwardEdges();

        // Assert
        assertNotNull(forwardEdges);
        assertEquals(1, forwardEdges.size());
        assertTrue(forwardEdges.containsKey(from));
        assertTrue(forwardEdges.get(from).contains(to));
    }

    @Test
    @DisplayName("Test getBackEdges returns the back edges map")
    void testGetBackEdges()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to = new ProjectVersion("group2", "artifact2", "2.0.0");

        graph.setEdges(from, to);

        // Act
        MutableMap<ProjectVersion, Set<ProjectVersion>> backEdges = graph.getBackEdges();

        // Assert
        assertNotNull(backEdges);
        assertEquals(1, backEdges.size());
        assertTrue(backEdges.containsKey(to));
        assertTrue(backEdges.get(to).contains(from));
    }

    @Test
    @DisplayName("Test hasNode returns false for non-existent node")
    void testHasNodeReturnsFalse()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node = new ProjectVersion("group1", "artifact1", "1.0.0");

        // Act
        boolean result = graph.hasNode(node);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test hasNode returns true for existing node")
    void testHasNodeReturnsTrue()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node = new ProjectVersion("group1", "artifact1", "1.0.0");
        graph.addNode(node, null);

        // Act
        boolean result = graph.hasNode(node);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test hasNode returns true for node with same coordinates")
    void testHasNodeWithEqualNode()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion node2 = new ProjectVersion("group1", "artifact1", "1.0.0");
        graph.addNode(node1, null);

        // Act
        boolean result = graph.hasNode(node2);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test addNode with null parent adds node as root")
    void testAddNodeWithNullParent()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node = new ProjectVersion("group1", "artifact1", "1.0.0");

        // Act
        graph.addNode(node, null);

        // Assert
        assertTrue(graph.getNodes().contains(node));
        assertTrue(graph.getRootNodes().contains(node));
        assertEquals(1, graph.getNodes().size());
        assertEquals(1, graph.getRootNodes().size());
    }

    @Test
    @DisplayName("Test addNode with non-null parent does not add to root nodes")
    void testAddNodeWithNonNullParent()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion parent = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion child = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act
        graph.addNode(parent, null);
        graph.addNode(child, parent);

        // Assert
        assertTrue(graph.getNodes().contains(child));
        assertFalse(graph.getRootNodes().contains(child));
        assertEquals(2, graph.getNodes().size());
        assertEquals(1, graph.getRootNodes().size());
    }

    @Test
    @DisplayName("Test addNode multiple times with same node")
    void testAddNodeMultipleTimes()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node = new ProjectVersion("group1", "artifact1", "1.0.0");

        // Act
        graph.addNode(node, null);
        graph.addNode(node, null);

        // Assert
        assertEquals(1, graph.getNodes().size());
        assertEquals(1, graph.getRootNodes().size());
    }

    @Test
    @DisplayName("Test addNode multiple root nodes")
    void testAddNodeMultipleRootNodes()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion root1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion root2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        ProjectVersion root3 = new ProjectVersion("group3", "artifact3", "3.0.0");

        // Act
        graph.addNode(root1, null);
        graph.addNode(root2, null);
        graph.addNode(root3, null);

        // Assert
        assertEquals(3, graph.getNodes().size());
        assertEquals(3, graph.getRootNodes().size());
        assertTrue(graph.getRootNodes().contains(root1));
        assertTrue(graph.getRootNodes().contains(root2));
        assertTrue(graph.getRootNodes().contains(root3));
    }

    @Test
    @DisplayName("Test setEdges creates forward edge")
    void testSetEdgesCreatesForwardEdge()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act
        graph.setEdges(from, to);

        // Assert
        assertTrue(graph.getForwardEdges().containsKey(from));
        assertTrue(graph.getForwardEdges().get(from).contains(to));
    }

    @Test
    @DisplayName("Test setEdges creates back edge")
    void testSetEdgesCreatesBackEdge()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act
        graph.setEdges(from, to);

        // Assert
        assertTrue(graph.getBackEdges().containsKey(to));
        assertTrue(graph.getBackEdges().get(to).contains(from));
    }

    @Test
    @DisplayName("Test setEdges multiple times with same nodes")
    void testSetEdgesMultipleTimes()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act
        graph.setEdges(from, to);
        graph.setEdges(from, to);

        // Assert
        assertEquals(1, graph.getForwardEdges().size());
        assertEquals(1, graph.getForwardEdges().get(from).size());
        assertEquals(1, graph.getBackEdges().size());
        assertEquals(1, graph.getBackEdges().get(to).size());
    }

    @Test
    @DisplayName("Test setEdges with multiple targets from same source")
    void testSetEdgesMultipleTargets()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to1 = new ProjectVersion("group2", "artifact2", "2.0.0");
        ProjectVersion to2 = new ProjectVersion("group3", "artifact3", "3.0.0");
        ProjectVersion to3 = new ProjectVersion("group4", "artifact4", "4.0.0");

        // Act
        graph.setEdges(from, to1);
        graph.setEdges(from, to2);
        graph.setEdges(from, to3);

        // Assert
        assertEquals(1, graph.getForwardEdges().size());
        assertEquals(3, graph.getForwardEdges().get(from).size());
        assertTrue(graph.getForwardEdges().get(from).contains(to1));
        assertTrue(graph.getForwardEdges().get(from).contains(to2));
        assertTrue(graph.getForwardEdges().get(from).contains(to3));
    }

    @Test
    @DisplayName("Test setEdges with multiple sources to same target")
    void testSetEdgesMultipleSources()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion from2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        ProjectVersion from3 = new ProjectVersion("group3", "artifact3", "3.0.0");
        ProjectVersion to = new ProjectVersion("group4", "artifact4", "4.0.0");

        // Act
        graph.setEdges(from1, to);
        graph.setEdges(from2, to);
        graph.setEdges(from3, to);

        // Assert
        assertEquals(1, graph.getBackEdges().size());
        assertEquals(3, graph.getBackEdges().get(to).size());
        assertTrue(graph.getBackEdges().get(to).contains(from1));
        assertTrue(graph.getBackEdges().get(to).contains(from2));
        assertTrue(graph.getBackEdges().get(to).contains(from3));
    }

    @Test
    @DisplayName("Test complex graph with multiple nodes and edges")
    void testComplexGraph()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion root = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("group2", "artifact2", "2.0.0");
        ProjectVersion dep2 = new ProjectVersion("group3", "artifact3", "3.0.0");
        ProjectVersion subdep = new ProjectVersion("group4", "artifact4", "4.0.0");

        // Act - build graph: root -> dep1 -> subdep
        //                           -> dep2 -> subdep
        graph.addNode(root, null);
        graph.addNode(dep1, root);
        graph.addNode(dep2, root);
        graph.addNode(subdep, dep1);

        graph.setEdges(root, dep1);
        graph.setEdges(root, dep2);
        graph.setEdges(dep1, subdep);
        graph.setEdges(dep2, subdep);

        // Assert
        assertEquals(4, graph.getNodes().size());
        assertEquals(1, graph.getRootNodes().size());
        assertTrue(graph.getRootNodes().contains(root));

        assertEquals(2, graph.getForwardEdges().get(root).size());
        assertTrue(graph.getForwardEdges().get(root).contains(dep1));
        assertTrue(graph.getForwardEdges().get(root).contains(dep2));

        assertEquals(1, graph.getForwardEdges().get(dep1).size());
        assertTrue(graph.getForwardEdges().get(dep1).contains(subdep));

        assertEquals(2, graph.getBackEdges().get(subdep).size());
        assertTrue(graph.getBackEdges().get(subdep).contains(dep1));
        assertTrue(graph.getBackEdges().get(subdep).contains(dep2));
    }

    @Test
    @DisplayName("Test graph with single node and no edges")
    void testSingleNodeNoEdges()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node = new ProjectVersion("group1", "artifact1", "1.0.0");

        // Act
        graph.addNode(node, null);

        // Assert
        assertEquals(1, graph.getNodes().size());
        assertEquals(1, graph.getRootNodes().size());
        assertTrue(graph.getNodes().contains(node));
        assertTrue(graph.getRootNodes().contains(node));
        assertTrue(graph.getForwardEdges().isEmpty());
        assertTrue(graph.getBackEdges().isEmpty());
    }

    @Test
    @DisplayName("Test edges without adding nodes explicitly")
    void testEdgesWithoutExplicitNodes()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion from = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion to = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act
        graph.setEdges(from, to);

        // Assert - nodes are not added by setEdges
        assertTrue(graph.getNodes().isEmpty());
        assertFalse(graph.hasNode(from));
        assertFalse(graph.hasNode(to));
        // But edges are created
        assertTrue(graph.getForwardEdges().containsKey(from));
        assertTrue(graph.getBackEdges().containsKey(to));
    }

    @Test
    @DisplayName("Test addNode preserves parent parameter but does not create edge")
    void testAddNodeDoesNotCreateEdge()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion parent = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion child = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act
        graph.addNode(parent, null);
        graph.addNode(child, parent);

        // Assert - addNode does not create edges
        assertTrue(graph.getForwardEdges().isEmpty());
        assertTrue(graph.getBackEdges().isEmpty());
    }

    @Test
    @DisplayName("Test full workflow with nodes and edges")
    void testFullWorkflow()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion root = new ProjectVersion("com.example", "root", "1.0.0");
        ProjectVersion lib1 = new ProjectVersion("com.example", "lib1", "1.0.0");
        ProjectVersion lib2 = new ProjectVersion("com.example", "lib2", "1.0.0");
        ProjectVersion common = new ProjectVersion("com.example", "common", "1.0.0");

        // Act - add nodes
        graph.addNode(root, null);
        graph.addNode(lib1, root);
        graph.addNode(lib2, root);
        graph.addNode(common, null);

        // Act - set edges
        graph.setEdges(root, lib1);
        graph.setEdges(root, lib2);
        graph.setEdges(lib1, common);
        graph.setEdges(lib2, common);

        // Assert nodes
        assertEquals(4, graph.getNodes().size());
        assertTrue(graph.hasNode(root));
        assertTrue(graph.hasNode(lib1));
        assertTrue(graph.hasNode(lib2));
        assertTrue(graph.hasNode(common));

        // Assert root nodes
        assertEquals(2, graph.getRootNodes().size());
        assertTrue(graph.getRootNodes().contains(root));
        assertTrue(graph.getRootNodes().contains(common));

        // Assert forward edges
        assertEquals(3, graph.getForwardEdges().size());
        assertEquals(2, graph.getForwardEdges().get(root).size());
        assertEquals(1, graph.getForwardEdges().get(lib1).size());
        assertEquals(1, graph.getForwardEdges().get(lib2).size());

        // Assert back edges
        assertEquals(3, graph.getBackEdges().size());
        assertEquals(1, graph.getBackEdges().get(lib1).size());
        assertEquals(1, graph.getBackEdges().get(lib2).size());
        assertEquals(2, graph.getBackEdges().get(common).size());
    }

    @Test
    @DisplayName("Test getters return mutable collections that can modify graph state")
    void testGettersReturnMutableCollections()
  {
        // Arrange
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectVersion node1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion node2 = new ProjectVersion("group2", "artifact2", "2.0.0");

        // Act - modify graph through getter
        graph.getNodes().add(node1);
        graph.getRootNodes().add(node1);

        // Assert - modifications persist
        assertTrue(graph.hasNode(node1));
        assertTrue(graph.getRootNodes().contains(node1));
    }
}

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport.ProjectDependencyConflict;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport.SerializedGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectDependencyReportDiffblueTest 


{
  /**
   * Test new {@link ProjectDependencyReport} (default constructor).
   *
   * <p>Method under test: default or parameterless constructor of {@link ProjectDependencyReport}
   */
  @Test
  @DisplayName("Test new ProjectDependencyReport (default constructor)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectDependencyReport.<init>()"})
  void testNewProjectDependencyReport()
  {
    // Arrange and Act
    ProjectDependencyReport actualProjectDependencyReport = new ProjectDependencyReport();

    // Assert
    SerializedGraph graph = actualProjectDependencyReport.getGraph();
    assertTrue(graph.getNodes().toList().isEmpty());
    assertTrue(actualProjectDependencyReport.getConflicts().isEmpty());
    assertTrue(graph.getRootNodes().isEmpty());
  }

  /**
   * Test {@link ProjectDependencyReport#addConflict(String, String, Set)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>When {@link HashSet#HashSet()} add {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectDependencyReport#addConflict(String, String, Set)}
   */
  @Test
  @DisplayName("Test addConflict(String, String, Set); given '42'; when HashSet() add '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectDependencyReport.addConflict(String, String, Set)"})
  void testAddConflict_given42_whenHashSetAdd42()
  {
    // Arrange
    ProjectDependencyReport projectDependencyReport = new ProjectDependencyReport();

    HashSet<String> versions = new HashSet<>();
    versions.add("42");
    versions.add("Conflicts must have more than one version");

    // Act
    projectDependencyReport.addConflict("42", "42", versions);

    // Assert
    List<ProjectDependencyConflict> conflicts = projectDependencyReport.getConflicts();
    assertEquals(1, conflicts.size());
    ProjectDependencyConflict getResult = conflicts.get(0);
    assertEquals("42", getResult.getArtifactId());
    assertEquals("42", getResult.getGroupId());
    assertSame(versions, getResult.getVersions());
  }

  /**
   * Test {@link ProjectDependencyReport#addConflict(String, String, Set)}.
   *
   * <ul>
   *   <li>Then {@link ProjectDependencyReport} (default constructor) Conflicts size is one.
   * </ul>
   *
   * <p>Method under test: {@link ProjectDependencyReport#addConflict(String, String, Set)}
   */
  @Test
  @DisplayName(
      "Test addConflict(String, String, Set); then ProjectDependencyReport (default constructor) Conflicts size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectDependencyReport.addConflict(String, String, Set)"})
  void testAddConflict_thenProjectDependencyReportConflictsSizeIsOne()
  {
    // Arrange
    ProjectDependencyReport projectDependencyReport = new ProjectDependencyReport();

    HashSet<String> versions = new HashSet<>();
    versions.add("Conflicts must have more than one version");

    // Act
    projectDependencyReport.addConflict("42", "42", versions);

    // Assert
    List<ProjectDependencyConflict> conflicts = projectDependencyReport.getConflicts();
    assertEquals(1, conflicts.size());
    ProjectDependencyConflict getResult = conflicts.get(0);
    assertEquals("42", getResult.getArtifactId());
    assertEquals("42", getResult.getGroupId());
    assertSame(versions, getResult.getVersions());
  }

  /**
   * Test {@link ProjectDependencyReport#addConflict(String, String, Set)}.
   *
   * <ul>
   *   <li>When {@link HashSet#HashSet()}.
   *   <li>Then throw {@link UnsupportedOperationException}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectDependencyReport#addConflict(String, String, Set)}
   */
  @Test
  @DisplayName(
      "Test addConflict(String, String, Set); when HashSet(); then throw UnsupportedOperationException")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectDependencyReport.addConflict(String, String, Set)"})
  void testAddConflict_whenHashSet_thenThrowUnsupportedOperationException()
  {
    // Arrange
    ProjectDependencyReport projectDependencyReport = new ProjectDependencyReport();

    // Act and Assert
    assertThrows(
        UnsupportedOperationException.class,
        () -> projectDependencyReport.addConflict("42", "42", new HashSet<>()));
  }

  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectDependencyReport#getConflicts()}
   *   <li>{@link ProjectDependencyReport#getGraph()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "List ProjectDependencyReport.getConflicts()",
    "SerializedGraph ProjectDependencyReport.getGraph()"
  })
  void testGettersAndSetters()
  {
    // Arrange
    ProjectDependencyReport projectDependencyReport = new ProjectDependencyReport();

    // Act
    List<ProjectDependencyConflict> actualConflicts = projectDependencyReport.getConflicts();
    SerializedGraph actualGraph = projectDependencyReport.getGraph();

    // Assert
    assertTrue(actualConflicts.isEmpty());
    assertTrue(actualGraph.getRootNodes().isEmpty());
  }

  /**
   * Test SerializedGraph getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link SerializedGraph#getNodes()}
   *   <li>{@link SerializedGraph#getRootNodes()}
   * </ul>
   */
  @Test
  @DisplayName("Test SerializedGraph getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MutableMap SerializedGraph.getNodes()", "Set SerializedGraph.getRootNodes()"})
  void testSerializedGraphGettersAndSetters()
  {
    // Arrange
    SerializedGraph serializedGraph = new SerializedGraph();

    // Act
    MutableMap<String, ProjectDependencyVersionNode> actualNodes = serializedGraph.getNodes();
    Set<String> actualRootNodes = serializedGraph.getRootNodes();

    // Assert
    assertTrue(actualNodes.toList().isEmpty());
    assertTrue(actualRootNodes.isEmpty());
  }

  /**
   * Test SerializedGraph new {@link SerializedGraph} (default constructor).
   *
   * <p>Method under test: default or parameterless constructor of {@link SerializedGraph}
   */
  @Test
  @DisplayName("Test SerializedGraph new SerializedGraph (default constructor)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void SerializedGraph.<init>()"})
  void testSerializedGraphNewSerializedGraph()
  {
    // Arrange and Act
    SerializedGraph actualSerializedGraph = new SerializedGraph();

    // Assert
    assertTrue(actualSerializedGraph.getNodes().toList().isEmpty());
    assertTrue(actualSerializedGraph.getRootNodes().isEmpty());
  }
}

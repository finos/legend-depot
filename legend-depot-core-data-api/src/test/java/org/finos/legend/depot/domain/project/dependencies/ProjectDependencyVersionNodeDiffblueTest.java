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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.Set;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectDependencyVersionNodeDiffblueTest 


{
  /**
   * Test {@link ProjectDependencyVersionNode#ProjectDependencyVersionNode(String, String, String)}.
   *
   * <p>Method under test: {@link ProjectDependencyVersionNode#ProjectDependencyVersionNode(String,
   * String, String)}
   */
  @Test
  @DisplayName("Test new ProjectDependencyVersionNode(String, String, String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectDependencyVersionNode.<init>(String, String, String)"})
  void testNewProjectDependencyVersionNode()
  {
    // Arrange and Act
    ProjectDependencyVersionNode actualProjectDependencyVersionNode =
        new ProjectDependencyVersionNode("42", "42", "42");

    // Assert
    assertEquals("42", actualProjectDependencyVersionNode.getArtifactId());
    assertEquals("42", actualProjectDependencyVersionNode.getGroupId());
    assertEquals("42", actualProjectDependencyVersionNode.getVersionId());
    assertEquals("42:42", actualProjectDependencyVersionNode.getCoordinates());
    assertEquals("42:42:42", actualProjectDependencyVersionNode.getGav());
    assertEquals("42:42:42", actualProjectDependencyVersionNode.getId());
    assertNull(actualProjectDependencyVersionNode.getProjectId());
    assertTrue(actualProjectDependencyVersionNode.getBackEdges().isEmpty());
    assertTrue(actualProjectDependencyVersionNode.getForwardEdges().isEmpty());
  }

  /**
   * Test {@link ProjectDependencyVersionNode#buildFromProjectVersion(ProjectVersion)}.
   *
   * <ul>
   *   <li>Then return ArtifactId is {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link
   * ProjectDependencyVersionNode#buildFromProjectVersion(ProjectVersion)}
   */
  @Test
  @DisplayName("Test buildFromProjectVersion(ProjectVersion); then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "ProjectDependencyVersionNode ProjectDependencyVersionNode.buildFromProjectVersion(ProjectVersion)"
  })
  void testBuildFromProjectVersion_thenReturnArtifactIdIs42()
  {
    // Arrange
    ProjectVersion version = new ProjectVersion("42", "42", "42");

    // Act
    ProjectDependencyVersionNode actualBuildFromProjectVersionResult =
        ProjectDependencyVersionNode.buildFromProjectVersion(version);

    // Assert
    assertEquals("42", actualBuildFromProjectVersionResult.getArtifactId());
    assertEquals("42", actualBuildFromProjectVersionResult.getGroupId());
    assertEquals("42", actualBuildFromProjectVersionResult.getVersionId());
    assertEquals("42:42", actualBuildFromProjectVersionResult.getCoordinates());
    assertEquals("42:42:42", actualBuildFromProjectVersionResult.getGav());
    assertEquals("42:42:42", actualBuildFromProjectVersionResult.getId());
    assertNull(actualBuildFromProjectVersionResult.getProjectId());
    assertTrue(actualBuildFromProjectVersionResult.getBackEdges().isEmpty());
    assertTrue(actualBuildFromProjectVersionResult.getForwardEdges().isEmpty());
  }

  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectDependencyVersionNode#setProjectId(String)}
   *   <li>{@link ProjectDependencyVersionNode#getBackEdges()}
   *   <li>{@link ProjectDependencyVersionNode#getForwardEdges()}
   *   <li>{@link ProjectDependencyVersionNode#getProjectId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "Set ProjectDependencyVersionNode.getBackEdges()",
    "Set ProjectDependencyVersionNode.getForwardEdges()",
    "String ProjectDependencyVersionNode.getProjectId()",
    "void ProjectDependencyVersionNode.setProjectId(String)"
  })
  void testGettersAndSetters()
  {
    // Arrange
    ProjectDependencyVersionNode projectDependencyVersionNode =
        new ProjectDependencyVersionNode("42", "42", "42");

    // Act
    projectDependencyVersionNode.setProjectId("myproject");
    Set<String> actualBackEdges = projectDependencyVersionNode.getBackEdges();
    Set<String> actualForwardEdges = projectDependencyVersionNode.getForwardEdges();

    // Assert
    assertEquals("myproject", projectDependencyVersionNode.getProjectId());
    assertTrue(actualBackEdges.isEmpty());
    assertTrue(actualForwardEdges.isEmpty());
  }

  /**
   * Test {@link ProjectDependencyVersionNode#getGav()}.
   *
   * <p>Method under test: {@link ProjectDependencyVersionNode#getGav()}
   */
  @Test
  @DisplayName("Test getGav()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String ProjectDependencyVersionNode.getGav()"})
  void testGetGav()
  {
    // Arrange
    ProjectDependencyVersionNode projectDependencyVersionNode =
        new ProjectDependencyVersionNode("42", "42", "42");

    // Act and Assert
    assertEquals("42:42:42", projectDependencyVersionNode.getGav());
  }

  /**
   * Test {@link ProjectDependencyVersionNode#getCoordinates()}.
   *
   * <p>Method under test: {@link ProjectDependencyVersionNode#getCoordinates()}
   */
  @Test
  @DisplayName("Test getCoordinates()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String ProjectDependencyVersionNode.getCoordinates()"})
  void testGetCoordinates()
  {
    // Arrange
    ProjectDependencyVersionNode projectDependencyVersionNode =
        new ProjectDependencyVersionNode("42", "42", "42");

    // Act and Assert
    assertEquals("42:42", projectDependencyVersionNode.getCoordinates());
  }

  /**
   * Test {@link ProjectDependencyVersionNode#getId()}.
   *
   * <p>Method under test: {@link ProjectDependencyVersionNode#getId()}
   */
  @Test
  @DisplayName("Test getId()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String ProjectDependencyVersionNode.getId()"})
  void testGetId()
  {
    // Arrange
    ProjectDependencyVersionNode projectDependencyVersionNode =
        new ProjectDependencyVersionNode("42", "42", "42");

    // Act and Assert
    assertEquals("42:42:42", projectDependencyVersionNode.getId());
  }
}

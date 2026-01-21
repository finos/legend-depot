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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectDependencyWithPlatformVersionsDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link
   *       ProjectDependencyWithPlatformVersions#ProjectDependencyWithPlatformVersions(String,
   *       String, String, ProjectVersion, List)}
   *   <li>{@link ProjectDependencyWithPlatformVersions#getDependency()}
   *   <li>{@link ProjectDependencyWithPlatformVersions#getPlatformsVersion()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectDependencyWithPlatformVersions.<init>(String, String, String, ProjectVersion, List)",
    "ProjectVersion ProjectDependencyWithPlatformVersions.getDependency()",
    "List ProjectDependencyWithPlatformVersions.getPlatformsVersion()"
  })
  void testGettersAndSetters()
  {
    // Arrange
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");
    ArrayList<Property> platformsVersion = new ArrayList<>();

    // Act
    ProjectDependencyWithPlatformVersions actualProjectDependencyWithPlatformVersions =
        new ProjectDependencyWithPlatformVersions("42", "42", "42", dependency, platformsVersion);
    ProjectVersion actualDependency = actualProjectDependencyWithPlatformVersions.getDependency();
    List<Property> actualPlatformsVersion =
        actualProjectDependencyWithPlatformVersions.getPlatformsVersion();

    // Assert
    assertEquals("42", actualProjectDependencyWithPlatformVersions.getArtifactId());
    assertEquals("42", actualProjectDependencyWithPlatformVersions.getGroupId());
    assertEquals("42", actualProjectDependencyWithPlatformVersions.getVersionId());
    assertTrue(actualPlatformsVersion.isEmpty());
    assertSame(platformsVersion, actualPlatformsVersion);
    assertSame(dependency, actualDependency);
  }

  /**
   * Test {@link ProjectDependencyWithPlatformVersions#equals(Object)}, and {@link
   * ProjectDependencyWithPlatformVersions#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectDependencyWithPlatformVersions#equals(Object)}
   *   <li>{@link ProjectDependencyWithPlatformVersions#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectDependencyWithPlatformVersions.equals(Object)",
    "int ProjectDependencyWithPlatformVersions.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");
    ProjectDependencyWithPlatformVersions projectDependencyWithPlatformVersions =
        new ProjectDependencyWithPlatformVersions("42", "42", "42", dependency, new ArrayList<>());
    ProjectVersion dependency2 = new ProjectVersion("42", "42", "42");
    ProjectDependencyWithPlatformVersions projectDependencyWithPlatformVersions2 =
        new ProjectDependencyWithPlatformVersions("42", "42", "42", dependency2, new ArrayList<>());

    // Act and Assert
    assertEquals(projectDependencyWithPlatformVersions, projectDependencyWithPlatformVersions2);
    assertEquals(
        projectDependencyWithPlatformVersions.hashCode(),
        projectDependencyWithPlatformVersions2.hashCode());
  }

  /**
   * Test {@link ProjectDependencyWithPlatformVersions#equals(Object)}, and {@link
   * ProjectDependencyWithPlatformVersions#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectDependencyWithPlatformVersions#equals(Object)}
   *   <li>{@link ProjectDependencyWithPlatformVersions#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectDependencyWithPlatformVersions.equals(Object)",
    "int ProjectDependencyWithPlatformVersions.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");
    ProjectDependencyWithPlatformVersions projectDependencyWithPlatformVersions =
        new ProjectDependencyWithPlatformVersions("42", "42", "42", dependency, new ArrayList<>());

    // Act and Assert
    assertEquals(projectDependencyWithPlatformVersions, projectDependencyWithPlatformVersions);
    int expectedHashCodeResult = projectDependencyWithPlatformVersions.hashCode();
    assertEquals(expectedHashCodeResult, projectDependencyWithPlatformVersions.hashCode());
  }

  /**
   * Test {@link ProjectDependencyWithPlatformVersions#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectDependencyWithPlatformVersions#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectDependencyWithPlatformVersions.equals(Object)",
    "int ProjectDependencyWithPlatformVersions.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");
    ProjectDependencyWithPlatformVersions projectDependencyWithPlatformVersions =
        new ProjectDependencyWithPlatformVersions(
            "Group Id", "42", "42", dependency, new ArrayList<>());
    ProjectVersion dependency2 = new ProjectVersion("42", "42", "42");

    // Act and Assert
    assertNotEquals(
        projectDependencyWithPlatformVersions,
        new ProjectDependencyWithPlatformVersions(
            "42", "42", "42", dependency2, new ArrayList<>()));
  }

  /**
   * Test {@link ProjectDependencyWithPlatformVersions#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectDependencyWithPlatformVersions#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectDependencyWithPlatformVersions.equals(Object)",
    "int ProjectDependencyWithPlatformVersions.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");

    // Act and Assert
    assertNotEquals(
        new ProjectDependencyWithPlatformVersions("42", "42", "42", dependency, new ArrayList<>()),
        null);
  }

  /**
   * Test {@link ProjectDependencyWithPlatformVersions#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectDependencyWithPlatformVersions#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectDependencyWithPlatformVersions.equals(Object)",
    "int ProjectDependencyWithPlatformVersions.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");

    // Act and Assert
    assertNotEquals(
        new ProjectDependencyWithPlatformVersions("42", "42", "42", dependency, new ArrayList<>()),
        "Different type to ProjectDependencyWithPlatformVersions");
  }
}

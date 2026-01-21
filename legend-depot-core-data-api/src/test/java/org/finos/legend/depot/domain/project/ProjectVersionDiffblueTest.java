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

package org.finos.legend.depot.domain.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectVersionDiffblueTest 


{
  /**
   * Test {@link ProjectVersion#ProjectVersion()}.
   *
   * <ul>
   *   <li>Then return ArtifactId is {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersion#ProjectVersion()}
   */
  @Test
  @DisplayName("Test new ProjectVersion(); then return ArtifactId is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersion.<init>()",
    "void ProjectVersion.<init>(String, String, String)"
  })
  void testNewProjectVersion_thenReturnArtifactIdIsNull()
  {
    // Arrange and Act
    ProjectVersion actualProjectVersion = new ProjectVersion();

    // Assert
    assertNull(actualProjectVersion.getArtifactId());
    assertNull(actualProjectVersion.getGroupId());
    assertNull(actualProjectVersion.getVersionId());
  }

  /**
   * Test {@link ProjectVersion#ProjectVersion(String, String, String)}.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return ArtifactId is {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersion#ProjectVersion(String, String, String)}
   */
  @Test
  @DisplayName(
      "Test new ProjectVersion(String, String, String); when '42'; then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersion.<init>()",
    "void ProjectVersion.<init>(String, String, String)"
  })
  void testNewProjectVersion_when42_thenReturnArtifactIdIs42()
  {
    // Arrange and Act
    ProjectVersion actualProjectVersion = new ProjectVersion("42", "42", "42");

    // Assert
    assertEquals("42", actualProjectVersion.getArtifactId());
    assertEquals("42", actualProjectVersion.getGroupId());
    assertEquals("42", actualProjectVersion.getVersionId());
  }

  /**
   * Test {@link ProjectVersion#equals(Object)}, and {@link ProjectVersion#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersion#equals(Object)}
   *   <li>{@link ProjectVersion#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectVersion.equals(Object)", "int ProjectVersion.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    ProjectVersion projectVersion = new ProjectVersion("42", "42", "42");
    ProjectVersion projectVersion2 = new ProjectVersion("42", "42", "42");

    // Act and Assert
    assertEquals(projectVersion, projectVersion2);
    assertEquals(projectVersion.hashCode(), projectVersion2.hashCode());
  }

  /**
   * Test {@link ProjectVersion#equals(Object)}, and {@link ProjectVersion#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersion#equals(Object)}
   *   <li>{@link ProjectVersion#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectVersion.equals(Object)", "int ProjectVersion.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    ProjectVersion projectVersion = new ProjectVersion("42", "42", "42");

    // Act and Assert
    assertEquals(projectVersion, projectVersion);
    int expectedHashCodeResult = projectVersion.hashCode();
    assertEquals(expectedHashCodeResult, projectVersion.hashCode());
  }

  /**
   * Test {@link ProjectVersion#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersion#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectVersion.equals(Object)", "int ProjectVersion.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    ProjectVersion projectVersion = new ProjectVersion("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(projectVersion, new ProjectVersion("42", "42", "42"));
  }

  /**
   * Test {@link ProjectVersion#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersion#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectVersion.equals(Object)", "int ProjectVersion.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new ProjectVersion("42", "42", "42"), null);
  }

  /**
   * Test {@link ProjectVersion#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersion#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectVersion.equals(Object)", "int ProjectVersion.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new ProjectVersion("42", "42", "42"), "Different type to ProjectVersion");
  }

  /**
   * Test {@link ProjectVersion#getGav()}.
   *
   * <p>Method under test: {@link ProjectVersion#getGav()}
   */
  @Test
  @DisplayName("Test getGav()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String ProjectVersion.getGav()"})
  void testGetGav()
  {
    // Arrange
    ProjectVersion projectVersion = new ProjectVersion("42", "42", "42");

    // Act and Assert
    assertEquals("42:42:42", projectVersion.getGav());
  }
}

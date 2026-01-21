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

package org.finos.legend.depot.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectVersionEntitiesDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>Then return ArtifactId is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersionEntities#ProjectVersionEntities()}
   *   <li>{@link ProjectVersionEntities#getEntities()}
   *   <li>{@link ProjectVersionEntities#isVersionedEntity()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; then return ArtifactId is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersionEntities.<init>()",
    "void ProjectVersionEntities.<init>(String, String, String, List)",
    "List ProjectVersionEntities.getEntities()",
    "boolean ProjectVersionEntities.isVersionedEntity()"
  })
  void testGettersAndSetters_thenReturnArtifactIdIsNull()
  {
    // Arrange and Act
    ProjectVersionEntities actualProjectVersionEntities = new ProjectVersionEntities();
    List<Entity> actualEntities = actualProjectVersionEntities.getEntities();
    boolean actualIsVersionedEntityResult = actualProjectVersionEntities.isVersionedEntity();

    // Assert
    assertNull(actualProjectVersionEntities.getArtifactId());
    assertNull(actualProjectVersionEntities.getGroupId());
    assertNull(actualProjectVersionEntities.getVersionId());
    assertNull(actualEntities);
    assertFalse(actualIsVersionedEntityResult);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return ArtifactId is {@code 42}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersionEntities#ProjectVersionEntities(String, String, String, List)}
   *   <li>{@link ProjectVersionEntities#getEntities()}
   *   <li>{@link ProjectVersionEntities#isVersionedEntity()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersionEntities.<init>()",
    "void ProjectVersionEntities.<init>(String, String, String, List)",
    "List ProjectVersionEntities.getEntities()",
    "boolean ProjectVersionEntities.isVersionedEntity()"
  })
  void testGettersAndSetters_when42_thenReturnArtifactIdIs42()
  {
    // Arrange
    ArrayList<Entity> entities = new ArrayList<>();

    // Act
    ProjectVersionEntities actualProjectVersionEntities =
        new ProjectVersionEntities("42", "42", "42", entities);
    List<Entity> actualEntities = actualProjectVersionEntities.getEntities();
    boolean actualIsVersionedEntityResult = actualProjectVersionEntities.isVersionedEntity();

    // Assert
    assertEquals("42", actualProjectVersionEntities.getArtifactId());
    assertEquals("42", actualProjectVersionEntities.getGroupId());
    assertEquals("42", actualProjectVersionEntities.getVersionId());
    assertFalse(actualIsVersionedEntityResult);
    assertTrue(actualEntities.isEmpty());
    assertSame(entities, actualEntities);
  }

  /**
   * Test {@link ProjectVersionEntities#equals(Object)}, and {@link
   * ProjectVersionEntities#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersionEntities#equals(Object)}
   *   <li>{@link ProjectVersionEntities#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectVersionEntities.equals(Object)",
    "int ProjectVersionEntities.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    ProjectVersionEntities projectVersionEntities = new ProjectVersionEntities();
    ProjectVersionEntities projectVersionEntities2 = new ProjectVersionEntities();

    // Act and Assert
    assertEquals(projectVersionEntities, projectVersionEntities2);
    assertEquals(projectVersionEntities.hashCode(), projectVersionEntities2.hashCode());
  }

  /**
   * Test {@link ProjectVersionEntities#equals(Object)}, and {@link
   * ProjectVersionEntities#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersionEntities#equals(Object)}
   *   <li>{@link ProjectVersionEntities#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectVersionEntities.equals(Object)",
    "int ProjectVersionEntities.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    ProjectVersionEntities projectVersionEntities = new ProjectVersionEntities();

    // Act and Assert
    assertEquals(projectVersionEntities, projectVersionEntities);
    int expectedHashCodeResult = projectVersionEntities.hashCode();
    assertEquals(expectedHashCodeResult, projectVersionEntities.hashCode());
  }

  /**
   * Test {@link ProjectVersionEntities#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectVersionEntities.equals(Object)",
    "int ProjectVersionEntities.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    ProjectVersionEntities projectVersionEntities =
        new ProjectVersionEntities("42", "42", "42", new ArrayList<>());

    // Act and Assert
    assertNotEquals(projectVersionEntities, new ProjectVersionEntities());
  }

  /**
   * Test {@link ProjectVersionEntities#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectVersionEntities.equals(Object)",
    "int ProjectVersionEntities.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new ProjectVersionEntities(), null);
  }

  /**
   * Test {@link ProjectVersionEntities#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean ProjectVersionEntities.equals(Object)",
    "int ProjectVersionEntities.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new ProjectVersionEntities(), "Different type to ProjectVersionEntities");
  }
}

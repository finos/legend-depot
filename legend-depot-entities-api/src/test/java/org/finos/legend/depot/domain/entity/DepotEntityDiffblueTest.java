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
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.HashMap;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class DepotEntityDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotEntity#DepotEntity(String, String, String, Entity)}
   *   <li>{@link DepotEntity#getEntity()}
   *   <li>{@link DepotEntity#isVersionedEntity()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void DepotEntity.<init>()",
    "void DepotEntity.<init>(String, String, String)",
    "void DepotEntity.<init>(String, String, String, Entity)",
    "Entity DepotEntity.getEntity()",
    "boolean DepotEntity.isVersionedEntity()"
  })
  void testGettersAndSetters()
  {
    // Arrange
    EntityDefinition entity =
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>());

    // Act
    DepotEntity actualDepotEntity = new DepotEntity("42", "42", "42", entity);
    Entity actualEntity = actualDepotEntity.getEntity();
    boolean actualIsVersionedEntityResult = actualDepotEntity.isVersionedEntity();

    // Assert
    assertEquals("42", actualDepotEntity.getArtifactId());
    assertEquals("42", actualDepotEntity.getGroupId());
    assertEquals("42", actualDepotEntity.getVersionId());
    assertFalse(actualIsVersionedEntityResult);
    assertSame(entity, actualEntity);
  }

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
   *   <li>{@link DepotEntity#DepotEntity()}
   *   <li>{@link DepotEntity#getEntity()}
   *   <li>{@link DepotEntity#isVersionedEntity()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; then return ArtifactId is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void DepotEntity.<init>()",
    "void DepotEntity.<init>(String, String, String)",
    "void DepotEntity.<init>(String, String, String, Entity)",
    "Entity DepotEntity.getEntity()",
    "boolean DepotEntity.isVersionedEntity()"
  })
  void testGettersAndSetters_thenReturnArtifactIdIsNull()
  {
    // Arrange and Act
    DepotEntity actualDepotEntity = new DepotEntity();
    Entity actualEntity = actualDepotEntity.getEntity();
    boolean actualIsVersionedEntityResult = actualDepotEntity.isVersionedEntity();

    // Assert
    assertNull(actualDepotEntity.getArtifactId());
    assertNull(actualDepotEntity.getGroupId());
    assertNull(actualDepotEntity.getVersionId());
    assertNull(actualEntity);
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
   *   <li>{@link DepotEntity#DepotEntity(String, String, String)}
   *   <li>{@link DepotEntity#getEntity()}
   *   <li>{@link DepotEntity#isVersionedEntity()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void DepotEntity.<init>()",
    "void DepotEntity.<init>(String, String, String)",
    "void DepotEntity.<init>(String, String, String, Entity)",
    "Entity DepotEntity.getEntity()",
    "boolean DepotEntity.isVersionedEntity()"
  })
  void testGettersAndSetters_when42_thenReturnArtifactIdIs42()
  {
    // Arrange and Act
    DepotEntity actualDepotEntity = new DepotEntity("42", "42", "42");
    Entity actualEntity = actualDepotEntity.getEntity();
    boolean actualIsVersionedEntityResult = actualDepotEntity.isVersionedEntity();

    // Assert
    assertEquals("42", actualDepotEntity.getArtifactId());
    assertEquals("42", actualDepotEntity.getGroupId());
    assertEquals("42", actualDepotEntity.getVersionId());
    assertNull(actualEntity);
    assertFalse(actualIsVersionedEntityResult);
  }

  /**
   * Test {@link DepotEntity#equals(Object)}, and {@link DepotEntity#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotEntity#equals(Object)}
   *   <li>{@link DepotEntity#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotEntity.equals(Object)", "int DepotEntity.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    DepotEntity depotEntity = new DepotEntity("42", "42", "42");
    DepotEntity depotEntity2 = new DepotEntity("42", "42", "42");

    // Act and Assert
    assertEquals(depotEntity, depotEntity2);
    assertEquals(depotEntity.hashCode(), depotEntity2.hashCode());
  }

  /**
   * Test {@link DepotEntity#equals(Object)}, and {@link DepotEntity#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotEntity#equals(Object)}
   *   <li>{@link DepotEntity#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotEntity.equals(Object)", "int DepotEntity.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    DepotEntity depotEntity = new DepotEntity("42", "42", "42");

    // Act and Assert
    assertEquals(depotEntity, depotEntity);
    int expectedHashCodeResult = depotEntity.hashCode();
    assertEquals(expectedHashCodeResult, depotEntity.hashCode());
  }

  /**
   * Test {@link DepotEntity#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotEntity#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotEntity.equals(Object)", "int DepotEntity.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    DepotEntity depotEntity = new DepotEntity("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(depotEntity, new DepotEntity("42", "42", "42"));
  }

  /**
   * Test {@link DepotEntity#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotEntity#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotEntity.equals(Object)", "int DepotEntity.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new DepotEntity("42", "42", "42"), null);
  }

  /**
   * Test {@link DepotEntity#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotEntity#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotEntity.equals(Object)", "int DepotEntity.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new DepotEntity("42", "42", "42"), "Different type to DepotEntity");
  }
}

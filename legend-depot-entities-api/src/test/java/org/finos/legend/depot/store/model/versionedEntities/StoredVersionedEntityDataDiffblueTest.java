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

package org.finos.legend.depot.store.model.versionedEntities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.HashMap;
import java.util.Map;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoredVersionedEntityDataDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>Then return EntityAttributes Empty.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityData#StoredVersionedEntityData(String, String, String,
   *       EntityDefinition, Map)}
   *   <li>{@link StoredVersionedEntityData#getEntity()}
   *   <li>{@link StoredVersionedEntityData#getId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; then return EntityAttributes Empty")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredVersionedEntityData.<init>(String, String, String)",
    "void StoredVersionedEntityData.<init>(String, String, String, EntityDefinition, Map)",
    "EntityDefinition StoredVersionedEntityData.getEntity()",
    "String StoredVersionedEntityData.getId()"
  })
  void testGettersAndSetters_thenReturnEntityAttributesEmpty()
  {
    // Arrange
    EntityDefinition entity =
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>());
    HashMap<String, Object> entityAttributes = new HashMap<>();

    // Act
    StoredVersionedEntityData actualStoredVersionedEntityData =
        new StoredVersionedEntityData("42", "42", "42", entity, entityAttributes);
    EntityDefinition actualEntity = actualStoredVersionedEntityData.getEntity();

    // Assert
    assertEquals("", actualStoredVersionedEntityData.getId());
    assertEquals("42", actualStoredVersionedEntityData.getArtifactId());
    assertEquals("42", actualStoredVersionedEntityData.getGroupId());
    assertEquals("42", actualStoredVersionedEntityData.getVersionId());
    Map<String, ?> entityAttributes2 = actualStoredVersionedEntityData.getEntityAttributes();
    assertTrue(entityAttributes2.isEmpty());
    assertSame(entityAttributes, entityAttributes2);
    assertSame(entity, actualEntity);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return EntityAttributes is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityData#StoredVersionedEntityData(String, String, String)}
   *   <li>{@link StoredVersionedEntityData#getEntity()}
   *   <li>{@link StoredVersionedEntityData#getId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return EntityAttributes is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredVersionedEntityData.<init>(String, String, String)",
    "void StoredVersionedEntityData.<init>(String, String, String, EntityDefinition, Map)",
    "EntityDefinition StoredVersionedEntityData.getEntity()",
    "String StoredVersionedEntityData.getId()"
  })
  void testGettersAndSetters_when42_thenReturnEntityAttributesIsNull()
  {
    // Arrange and Act
    StoredVersionedEntityData actualStoredVersionedEntityData =
        new StoredVersionedEntityData("42", "42", "42");
    EntityDefinition actualEntity = actualStoredVersionedEntityData.getEntity();

    // Assert
    assertEquals("", actualStoredVersionedEntityData.getId());
    assertEquals("42", actualStoredVersionedEntityData.getArtifactId());
    assertEquals("42", actualStoredVersionedEntityData.getGroupId());
    assertEquals("42", actualStoredVersionedEntityData.getVersionId());
    assertNull(actualStoredVersionedEntityData.getEntityAttributes());
    assertNull(actualEntity);
  }

  /**
   * Test {@link StoredVersionedEntityData#equals(Object)}, and {@link
   * StoredVersionedEntityData#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityData#equals(Object)}
   *   <li>{@link StoredVersionedEntityData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityData.equals(Object)",
    "int StoredVersionedEntityData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    StoredVersionedEntityData storedVersionedEntityData =
        new StoredVersionedEntityData("42", "42", "42");
    StoredVersionedEntityData storedVersionedEntityData2 =
        new StoredVersionedEntityData("42", "42", "42");

    // Act and Assert
    assertEquals(storedVersionedEntityData, storedVersionedEntityData2);
    assertEquals(storedVersionedEntityData.hashCode(), storedVersionedEntityData2.hashCode());
  }

  /**
   * Test {@link StoredVersionedEntityData#equals(Object)}, and {@link
   * StoredVersionedEntityData#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityData#equals(Object)}
   *   <li>{@link StoredVersionedEntityData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityData.equals(Object)",
    "int StoredVersionedEntityData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    StoredVersionedEntityData storedVersionedEntityData =
        new StoredVersionedEntityData("42", "42", "42");

    // Act and Assert
    assertEquals(storedVersionedEntityData, storedVersionedEntityData);
    int expectedHashCodeResult = storedVersionedEntityData.hashCode();
    assertEquals(expectedHashCodeResult, storedVersionedEntityData.hashCode());
  }

  /**
   * Test {@link StoredVersionedEntityData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityData.equals(Object)",
    "int StoredVersionedEntityData.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    StoredVersionedEntityData storedVersionedEntityData =
        new StoredVersionedEntityData("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(storedVersionedEntityData, new StoredVersionedEntityData("42", "42", "42"));
  }

  /**
   * Test {@link StoredVersionedEntityData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityData.equals(Object)",
    "int StoredVersionedEntityData.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new StoredVersionedEntityData("42", "42", "42"), null);
  }

  /**
   * Test {@link StoredVersionedEntityData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityData.equals(Object)",
    "int StoredVersionedEntityData.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoredVersionedEntityData("42", "42", "42"),
        "Different type to StoredVersionedEntityData");
  }
}

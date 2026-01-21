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

package org.finos.legend.depot.store.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class EntityDefinitionDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link EntityDefinition#EntityDefinition(String, String, Map)}
   *   <li>{@link EntityDefinition#setClassifierPath(String)}
   *   <li>{@link EntityDefinition#getClassifierPath()}
   *   <li>{@link EntityDefinition#getContent()}
   *   <li>{@link EntityDefinition#getPath()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void EntityDefinition.<init>(String, String, Map)",
    "String EntityDefinition.getClassifierPath()",
    "Map EntityDefinition.getContent()",
    "String EntityDefinition.getPath()",
    "void EntityDefinition.setClassifierPath(String)"
  })
  void testGettersAndSetters()
  {
    // Arrange
    HashMap<String, Object> content = new HashMap<>();

    // Act
    EntityDefinition actualEntityDefinition =
        new EntityDefinition("/etc/config.properties", "Classifier Path", content);
    actualEntityDefinition.setClassifierPath("/etc/config.properties");
    String actualClassifierPath = actualEntityDefinition.getClassifierPath();
    Map<String, ?> actualContent = actualEntityDefinition.getContent();

    // Assert
    assertEquals("/etc/config.properties", actualClassifierPath);
    assertEquals("/etc/config.properties", actualEntityDefinition.getPath());
    assertTrue(actualContent.isEmpty());
    assertSame(content, actualContent);
  }

  /**
   * Test {@link EntityDefinition#equals(Object)}, and {@link EntityDefinition#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link EntityDefinition#equals(Object)}
   *   <li>{@link EntityDefinition#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean EntityDefinition.equals(Object)", "int EntityDefinition.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    EntityDefinition entityDefinition =
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>());
    EntityDefinition entityDefinition2 =
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>());

    // Act and Assert
    assertEquals(entityDefinition, entityDefinition2);
    assertEquals(entityDefinition.hashCode(), entityDefinition2.hashCode());
  }

  /**
   * Test {@link EntityDefinition#equals(Object)}, and {@link EntityDefinition#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link EntityDefinition#equals(Object)}
   *   <li>{@link EntityDefinition#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean EntityDefinition.equals(Object)", "int EntityDefinition.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    EntityDefinition entityDefinition =
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>());

    // Act and Assert
    assertEquals(entityDefinition, entityDefinition);
    int expectedHashCodeResult = entityDefinition.hashCode();
    assertEquals(expectedHashCodeResult, entityDefinition.hashCode());
  }

  /**
   * Test {@link EntityDefinition#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link EntityDefinition#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean EntityDefinition.equals(Object)", "int EntityDefinition.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    EntityDefinition entityDefinition =
        new EntityDefinition("/home/user/documents", "Classifier Path", new HashMap<>());

    // Act and Assert
    assertNotEquals(
        entityDefinition,
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>()));
  }

  /**
   * Test {@link EntityDefinition#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link EntityDefinition#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean EntityDefinition.equals(Object)", "int EntityDefinition.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>()), null);
  }

  /**
   * Test {@link EntityDefinition#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link EntityDefinition#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean EntityDefinition.equals(Object)", "int EntityDefinition.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>()),
        "Different type to EntityDefinition");
  }
}

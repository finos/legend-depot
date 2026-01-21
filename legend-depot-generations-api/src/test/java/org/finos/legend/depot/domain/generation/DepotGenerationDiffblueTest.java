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

package org.finos.legend.depot.domain.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class DepotGenerationDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotGeneration#DepotGeneration(String, String)}
   *   <li>{@link DepotGeneration#getContent()}
   *   <li>{@link DepotGeneration#getPath()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void DepotGeneration.<init>(String, String)",
    "String DepotGeneration.getContent()",
    "String DepotGeneration.getPath()"
  })
  void testGettersAndSetters()
  {
    // Arrange and Act
    DepotGeneration actualDepotGeneration =
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost");
    String actualContent = actualDepotGeneration.getContent();

    // Assert
    assertEquals("/etc/config.properties", actualDepotGeneration.getPath());
    assertEquals("Not all who wander are lost", actualContent);
  }

  /**
   * Test {@link DepotGeneration#equals(Object)}, and {@link DepotGeneration#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotGeneration#equals(Object)}
   *   <li>{@link DepotGeneration#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotGeneration.equals(Object)", "int DepotGeneration.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    DepotGeneration depotGeneration =
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost");
    DepotGeneration depotGeneration2 =
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost");

    // Act and Assert
    assertEquals(depotGeneration, depotGeneration2);
    assertEquals(depotGeneration.hashCode(), depotGeneration2.hashCode());
  }

  /**
   * Test {@link DepotGeneration#equals(Object)}, and {@link DepotGeneration#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotGeneration#equals(Object)}
   *   <li>{@link DepotGeneration#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotGeneration.equals(Object)", "int DepotGeneration.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    DepotGeneration depotGeneration =
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost");

    // Act and Assert
    assertEquals(depotGeneration, depotGeneration);
    int expectedHashCodeResult = depotGeneration.hashCode();
    assertEquals(expectedHashCodeResult, depotGeneration.hashCode());
  }

  /**
   * Test {@link DepotGeneration#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotGeneration#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotGeneration.equals(Object)", "int DepotGeneration.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    DepotGeneration depotGeneration =
        new DepotGeneration("/home/user/documents", "Not all who wander are lost");

    // Act and Assert
    assertNotEquals(
        depotGeneration,
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost"));
  }

  /**
   * Test {@link DepotGeneration#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotGeneration#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotGeneration.equals(Object)", "int DepotGeneration.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost"), null);
  }

  /**
   * Test {@link DepotGeneration#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotGeneration#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean DepotGeneration.equals(Object)", "int DepotGeneration.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost"),
        "Different type to DepotGeneration");
  }
}

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

package org.finos.legend.depot.store.model.generations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoredFileGenerationDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredFileGeneration#StoredFileGeneration(String, String, String, String, String,
   *       DepotGeneration)}
   *   <li>{@link StoredFileGeneration#getFile()}
   *   <li>{@link StoredFileGeneration#getId()}
   *   <li>{@link StoredFileGeneration#getPath()}
   *   <li>{@link StoredFileGeneration#getType()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredFileGeneration.<init>(String, String, String, String, String, DepotGeneration)",
    "DepotGeneration StoredFileGeneration.getFile()",
    "String StoredFileGeneration.getId()",
    "String StoredFileGeneration.getPath()",
    "String StoredFileGeneration.getType()"
  })
  void testGettersAndSetters()
  {
    // Arrange
    DepotGeneration fileGeneration =
        new DepotGeneration("/etc/config.properties", "Not all who wander are lost");

    // Act
    StoredFileGeneration actualStoredFileGeneration =
        new StoredFileGeneration(
            "42", "42", "42", "/etc/config.properties", "INITIAL", fileGeneration);
    DepotGeneration actualFile = actualStoredFileGeneration.getFile();
    String actualId = actualStoredFileGeneration.getId();
    String actualPath = actualStoredFileGeneration.getPath();
    String actualType = actualStoredFileGeneration.getType();

    // Assert
    assertEquals("", actualId);
    assertEquals("/etc/config.properties", actualPath);
    assertEquals("42", actualStoredFileGeneration.getArtifactId());
    assertEquals("42", actualStoredFileGeneration.getGroupId());
    assertEquals("42", actualStoredFileGeneration.getVersionId());
    assertEquals("INITIAL", actualType);
    assertSame(fileGeneration, actualFile);
  }

  /**
   * Test {@link StoredFileGeneration#equals(Object)}, and {@link StoredFileGeneration#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredFileGeneration#equals(Object)}
   *   <li>{@link StoredFileGeneration#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredFileGeneration.equals(Object)",
    "int StoredFileGeneration.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    StoredFileGeneration storedFileGeneration =
        new StoredFileGeneration(
            "42",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost"));
    StoredFileGeneration storedFileGeneration2 =
        new StoredFileGeneration(
            "42",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost"));

    // Act and Assert
    assertEquals(storedFileGeneration, storedFileGeneration2);
    assertEquals(storedFileGeneration.hashCode(), storedFileGeneration2.hashCode());
  }

  /**
   * Test {@link StoredFileGeneration#equals(Object)}, and {@link StoredFileGeneration#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredFileGeneration#equals(Object)}
   *   <li>{@link StoredFileGeneration#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredFileGeneration.equals(Object)",
    "int StoredFileGeneration.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    StoredFileGeneration storedFileGeneration =
        new StoredFileGeneration(
            "42",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost"));

    // Act and Assert
    assertEquals(storedFileGeneration, storedFileGeneration);
    int expectedHashCodeResult = storedFileGeneration.hashCode();
    assertEquals(expectedHashCodeResult, storedFileGeneration.hashCode());
  }

  /**
   * Test {@link StoredFileGeneration#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredFileGeneration#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredFileGeneration.equals(Object)",
    "int StoredFileGeneration.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    StoredFileGeneration storedFileGeneration =
        new StoredFileGeneration(
            "Group Id",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost"));

    // Act and Assert
    assertNotEquals(
        storedFileGeneration,
        new StoredFileGeneration(
            "42",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost")));
  }

  /**
   * Test {@link StoredFileGeneration#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredFileGeneration#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredFileGeneration.equals(Object)",
    "int StoredFileGeneration.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoredFileGeneration(
            "42",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost")),
        null);
  }

  /**
   * Test {@link StoredFileGeneration#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredFileGeneration#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredFileGeneration.equals(Object)",
    "int StoredFileGeneration.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoredFileGeneration(
            "42",
            "42",
            "42",
            "/etc/config.properties",
            "INITIAL",
            new DepotGeneration("/etc/config.properties", "Not all who wander are lost")),
        "Different type to StoredFileGeneration");
  }
}

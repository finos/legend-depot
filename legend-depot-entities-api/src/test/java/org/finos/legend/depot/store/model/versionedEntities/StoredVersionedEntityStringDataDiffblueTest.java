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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoredVersionedEntityStringDataDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return Data is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityStringData#StoredVersionedEntityStringData(String, String,
   *       String)}
   *   <li>{@link StoredVersionedEntityStringData#getData()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return Data is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredVersionedEntityStringData.<init>(String, String, String)",
    "void StoredVersionedEntityStringData.<init>(String, String, String, String, Map)",
    "String StoredVersionedEntityStringData.getData()"
  })
  void testGettersAndSetters_when42_thenReturnDataIsNull()
  {
    // Arrange and Act
    StoredVersionedEntityStringData actualStoredVersionedEntityStringData =
        new StoredVersionedEntityStringData("42", "42", "42");
    String actualData = actualStoredVersionedEntityStringData.getData();

    // Assert
    assertEquals("42", actualStoredVersionedEntityStringData.getArtifactId());
    assertEquals("42", actualStoredVersionedEntityStringData.getGroupId());
    assertEquals("42", actualStoredVersionedEntityStringData.getVersionId());
    assertNull(actualData);
    assertNull(actualStoredVersionedEntityStringData.getEntityAttributes());
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code Data}.
   *   <li>Then return {@code Data}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityStringData#StoredVersionedEntityStringData(String, String,
   *       String, String, Map)}
   *   <li>{@link StoredVersionedEntityStringData#getData()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'Data'; then return 'Data'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredVersionedEntityStringData.<init>(String, String, String)",
    "void StoredVersionedEntityStringData.<init>(String, String, String, String, Map)",
    "String StoredVersionedEntityStringData.getData()"
  })
  void testGettersAndSetters_whenData_thenReturnData()
  {
    // Arrange
    HashMap<String, Object> entityAttributes = new HashMap<>();

    // Act
    StoredVersionedEntityStringData actualStoredVersionedEntityStringData =
        new StoredVersionedEntityStringData("42", "42", "42", "Data", entityAttributes);
    String actualData = actualStoredVersionedEntityStringData.getData();

    // Assert
    assertEquals("42", actualStoredVersionedEntityStringData.getArtifactId());
    assertEquals("42", actualStoredVersionedEntityStringData.getGroupId());
    assertEquals("42", actualStoredVersionedEntityStringData.getVersionId());
    assertEquals("Data", actualData);
    Map<String, ?> entityAttributes2 = actualStoredVersionedEntityStringData.getEntityAttributes();
    assertTrue(entityAttributes2.isEmpty());
    assertSame(entityAttributes, entityAttributes2);
  }

  /**
   * Test {@link StoredVersionedEntityStringData#equals(Object)}, and {@link
   * StoredVersionedEntityStringData#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityStringData#equals(Object)}
   *   <li>{@link StoredVersionedEntityStringData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityStringData.equals(Object)",
    "int StoredVersionedEntityStringData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    StoredVersionedEntityStringData storedVersionedEntityStringData =
        new StoredVersionedEntityStringData("42", "42", "42");
    StoredVersionedEntityStringData storedVersionedEntityStringData2 =
        new StoredVersionedEntityStringData("42", "42", "42");

    // Act and Assert
    assertEquals(storedVersionedEntityStringData, storedVersionedEntityStringData2);
    assertEquals(
        storedVersionedEntityStringData.hashCode(), storedVersionedEntityStringData2.hashCode());
  }

  /**
   * Test {@link StoredVersionedEntityStringData#equals(Object)}, and {@link
   * StoredVersionedEntityStringData#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityStringData#equals(Object)}
   *   <li>{@link StoredVersionedEntityStringData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityStringData.equals(Object)",
    "int StoredVersionedEntityStringData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    StoredVersionedEntityStringData storedVersionedEntityStringData =
        new StoredVersionedEntityStringData("42", "42", "42");

    // Act and Assert
    assertEquals(storedVersionedEntityStringData, storedVersionedEntityStringData);
    int expectedHashCodeResult = storedVersionedEntityStringData.hashCode();
    assertEquals(expectedHashCodeResult, storedVersionedEntityStringData.hashCode());
  }

  /**
   * Test {@link StoredVersionedEntityStringData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityStringData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityStringData.equals(Object)",
    "int StoredVersionedEntityStringData.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    StoredVersionedEntityStringData storedVersionedEntityStringData =
        new StoredVersionedEntityStringData("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(
        storedVersionedEntityStringData, new StoredVersionedEntityStringData("42", "42", "42"));
  }

  /**
   * Test {@link StoredVersionedEntityStringData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityStringData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityStringData.equals(Object)",
    "int StoredVersionedEntityStringData.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new StoredVersionedEntityStringData("42", "42", "42"), null);
  }

  /**
   * Test {@link StoredVersionedEntityStringData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityStringData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityStringData.equals(Object)",
    "int StoredVersionedEntityStringData.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoredVersionedEntityStringData("42", "42", "42"),
        "Different type to StoredVersionedEntityStringData");
  }
}

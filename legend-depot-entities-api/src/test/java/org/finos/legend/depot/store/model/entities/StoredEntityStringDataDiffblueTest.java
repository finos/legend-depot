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

class StoredEntityStringDataDiffblueTest 


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
   *   <li>{@link StoredEntityStringData#StoredEntityStringData(String, String, String)}
   *   <li>{@link StoredEntityStringData#getData()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return Data is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredEntityStringData.<init>(String, String, String)",
    "void StoredEntityStringData.<init>(String, String, String, String, Map)",
    "String StoredEntityStringData.getData()"
  })
  void testGettersAndSetters_when42_thenReturnDataIsNull()
  {
    // Arrange and Act
    StoredEntityStringData actualStoredEntityStringData =
        new StoredEntityStringData("42", "42", "42");
    String actualData = actualStoredEntityStringData.getData();

    // Assert
    assertEquals("42", actualStoredEntityStringData.getArtifactId());
    assertEquals("42", actualStoredEntityStringData.getGroupId());
    assertEquals("42", actualStoredEntityStringData.getVersionId());
    assertNull(actualData);
    assertNull(actualStoredEntityStringData.getEntityAttributes());
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
   *   <li>{@link StoredEntityStringData#StoredEntityStringData(String, String, String, String,
   *       Map)}
   *   <li>{@link StoredEntityStringData#getData()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'Data'; then return 'Data'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredEntityStringData.<init>(String, String, String)",
    "void StoredEntityStringData.<init>(String, String, String, String, Map)",
    "String StoredEntityStringData.getData()"
  })
  void testGettersAndSetters_whenData_thenReturnData()
  {
    // Arrange
    HashMap<String, Object> entityAttributes = new HashMap<>();

    // Act
    StoredEntityStringData actualStoredEntityStringData =
        new StoredEntityStringData("42", "42", "42", "Data", entityAttributes);
    String actualData = actualStoredEntityStringData.getData();

    // Assert
    assertEquals("42", actualStoredEntityStringData.getArtifactId());
    assertEquals("42", actualStoredEntityStringData.getGroupId());
    assertEquals("42", actualStoredEntityStringData.getVersionId());
    assertEquals("Data", actualData);
    Map<String, ?> entityAttributes2 = actualStoredEntityStringData.getEntityAttributes();
    assertTrue(entityAttributes2.isEmpty());
    assertSame(entityAttributes, entityAttributes2);
  }

  /**
   * Test {@link StoredEntityStringData#equals(Object)}, and {@link
   * StoredEntityStringData#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredEntityStringData#equals(Object)}
   *   <li>{@link StoredEntityStringData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredEntityStringData.equals(Object)",
    "int StoredEntityStringData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    StoredEntityStringData storedEntityStringData = new StoredEntityStringData("42", "42", "42");
    StoredEntityStringData storedEntityStringData2 = new StoredEntityStringData("42", "42", "42");

    // Act and Assert
    assertEquals(storedEntityStringData, storedEntityStringData2);
    assertEquals(storedEntityStringData.hashCode(), storedEntityStringData2.hashCode());
  }

  /**
   * Test {@link StoredEntityStringData#equals(Object)}, and {@link
   * StoredEntityStringData#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredEntityStringData#equals(Object)}
   *   <li>{@link StoredEntityStringData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredEntityStringData.equals(Object)",
    "int StoredEntityStringData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    StoredEntityStringData storedEntityStringData = new StoredEntityStringData("42", "42", "42");

    // Act and Assert
    assertEquals(storedEntityStringData, storedEntityStringData);
    int expectedHashCodeResult = storedEntityStringData.hashCode();
    assertEquals(expectedHashCodeResult, storedEntityStringData.hashCode());
  }

  /**
   * Test {@link StoredEntityStringData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredEntityStringData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredEntityStringData.equals(Object)",
    "int StoredEntityStringData.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    StoredEntityStringData storedEntityStringData =
        new StoredEntityStringData("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(storedEntityStringData, new StoredEntityStringData("42", "42", "42"));
  }

  /**
   * Test {@link StoredEntityStringData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredEntityStringData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredEntityStringData.equals(Object)",
    "int StoredEntityStringData.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new StoredEntityStringData("42", "42", "42"), null);
  }

  /**
   * Test {@link StoredEntityStringData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredEntityStringData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredEntityStringData.equals(Object)",
    "int StoredEntityStringData.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoredEntityStringData("42", "42", "42"), "Different type to StoredEntityStringData");
  }
}

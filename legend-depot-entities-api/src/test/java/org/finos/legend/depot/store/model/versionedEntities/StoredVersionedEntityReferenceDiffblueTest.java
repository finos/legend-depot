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

class StoredVersionedEntityReferenceDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return Reference is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityReference#StoredVersionedEntityReference(String, String,
   *       String)}
   *   <li>{@link StoredVersionedEntityReference#getReference()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return Reference is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredVersionedEntityReference.<init>(String, String, String)",
    "void StoredVersionedEntityReference.<init>(String, String, String, String, Map)",
    "String StoredVersionedEntityReference.getReference()"
  })
  void testGettersAndSetters_when42_thenReturnReferenceIsNull()
  {
    // Arrange and Act
    StoredVersionedEntityReference actualStoredVersionedEntityReference =
        new StoredVersionedEntityReference("42", "42", "42");
    String actualReference = actualStoredVersionedEntityReference.getReference();

    // Assert
    assertEquals("42", actualStoredVersionedEntityReference.getArtifactId());
    assertEquals("42", actualStoredVersionedEntityReference.getGroupId());
    assertEquals("42", actualStoredVersionedEntityReference.getVersionId());
    assertNull(actualReference);
    assertNull(actualStoredVersionedEntityReference.getEntityAttributes());
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code Reference}.
   *   <li>Then return {@code Reference}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityReference#StoredVersionedEntityReference(String, String,
   *       String, String, Map)}
   *   <li>{@link StoredVersionedEntityReference#getReference()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'Reference'; then return 'Reference'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredVersionedEntityReference.<init>(String, String, String)",
    "void StoredVersionedEntityReference.<init>(String, String, String, String, Map)",
    "String StoredVersionedEntityReference.getReference()"
  })
  void testGettersAndSetters_whenReference_thenReturnReference()
  {
    // Arrange
    HashMap<String, Object> entityAttributes = new HashMap<>();

    // Act
    StoredVersionedEntityReference actualStoredVersionedEntityReference =
        new StoredVersionedEntityReference("42", "42", "42", "Reference", entityAttributes);
    String actualReference = actualStoredVersionedEntityReference.getReference();

    // Assert
    assertEquals("42", actualStoredVersionedEntityReference.getArtifactId());
    assertEquals("42", actualStoredVersionedEntityReference.getGroupId());
    assertEquals("42", actualStoredVersionedEntityReference.getVersionId());
    assertEquals("Reference", actualReference);
    Map<String, ?> entityAttributes2 = actualStoredVersionedEntityReference.getEntityAttributes();
    assertTrue(entityAttributes2.isEmpty());
    assertSame(entityAttributes, entityAttributes2);
  }

  /**
   * Test {@link StoredVersionedEntityReference#equals(Object)}, and {@link
   * StoredVersionedEntityReference#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityReference#equals(Object)}
   *   <li>{@link StoredVersionedEntityReference#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityReference.equals(Object)",
    "int StoredVersionedEntityReference.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    StoredVersionedEntityReference storedVersionedEntityReference =
        new StoredVersionedEntityReference("42", "42", "42");
    StoredVersionedEntityReference storedVersionedEntityReference2 =
        new StoredVersionedEntityReference("42", "42", "42");

    // Act and Assert
    assertEquals(storedVersionedEntityReference, storedVersionedEntityReference2);
    assertEquals(
        storedVersionedEntityReference.hashCode(), storedVersionedEntityReference2.hashCode());
  }

  /**
   * Test {@link StoredVersionedEntityReference#equals(Object)}, and {@link
   * StoredVersionedEntityReference#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredVersionedEntityReference#equals(Object)}
   *   <li>{@link StoredVersionedEntityReference#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityReference.equals(Object)",
    "int StoredVersionedEntityReference.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    StoredVersionedEntityReference storedVersionedEntityReference =
        new StoredVersionedEntityReference("42", "42", "42");

    // Act and Assert
    assertEquals(storedVersionedEntityReference, storedVersionedEntityReference);
    int expectedHashCodeResult = storedVersionedEntityReference.hashCode();
    assertEquals(expectedHashCodeResult, storedVersionedEntityReference.hashCode());
  }

  /**
   * Test {@link StoredVersionedEntityReference#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityReference#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityReference.equals(Object)",
    "int StoredVersionedEntityReference.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    StoredVersionedEntityReference storedVersionedEntityReference =
        new StoredVersionedEntityReference("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(
        storedVersionedEntityReference, new StoredVersionedEntityReference("42", "42", "42"));
  }

  /**
   * Test {@link StoredVersionedEntityReference#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityReference#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityReference.equals(Object)",
    "int StoredVersionedEntityReference.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new StoredVersionedEntityReference("42", "42", "42"), null);
  }

  /**
   * Test {@link StoredVersionedEntityReference#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredVersionedEntityReference#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoredVersionedEntityReference.equals(Object)",
    "int StoredVersionedEntityReference.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoredVersionedEntityReference("42", "42", "42"),
        "Different type to StoredVersionedEntityReference");
  }
}

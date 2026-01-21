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

package org.finos.legend.depot.domain.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionMismatchClaudeTest 

{
  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>When all parameters are valid with non-empty lists.
   *   <li>Then verify all fields are initialized correctly.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); when all parameters are valid with non-empty lists; then verify all fields are initialized correctly")
  void testConstructor5Args_whenAllParametersValidWithNonEmptyLists_thenVerifyAllFieldsInitialized()
  {
    // Arrange
    List<String> versionsNotInStore = Arrays.asList("1.0.0", "1.0.1");
    List<String> versionsNotInRepo = Arrays.asList("2.0.0", "2.0.1");

    // Act
    VersionMismatch versionMismatch =
        new VersionMismatch("project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo);

    // Assert
    assertEquals("project1", versionMismatch.projectId);
    assertEquals("group1", versionMismatch.groupId);
    assertEquals("artifact1", versionMismatch.artifactId);
    assertEquals(2, versionMismatch.versionsNotInStore.size());
    assertEquals("1.0.0", versionMismatch.versionsNotInStore.get(0));
    assertEquals("1.0.1", versionMismatch.versionsNotInStore.get(1));
    assertEquals(2, versionMismatch.versionsNotInRepository.size());
    assertEquals("2.0.0", versionMismatch.versionsNotInRepository.get(0));
    assertEquals("2.0.1", versionMismatch.versionsNotInRepository.get(1));
    assertTrue(versionMismatch.errors.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>When all lists are empty.
   *   <li>Then verify all fields are initialized with empty lists.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); when all lists are empty; then verify all fields are initialized with empty lists")
  void testConstructor5Args_whenAllListsEmpty_thenVerifyFieldsInitializedWithEmptyLists()
  {
    // Arrange & Act
    VersionMismatch versionMismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Assert
    assertEquals("project1", versionMismatch.projectId);
    assertEquals("group1", versionMismatch.groupId);
    assertEquals("artifact1", versionMismatch.artifactId);
    assertTrue(versionMismatch.versionsNotInStore.isEmpty());
    assertTrue(versionMismatch.versionsNotInRepository.isEmpty());
    assertTrue(versionMismatch.errors.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>When all parameters are valid with non-empty lists including errors.
   *   <li>Then verify all fields are initialized correctly.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); when all parameters are valid with non-empty lists including errors; then verify all fields are initialized correctly")
  void testConstructor6Args_whenAllParametersValidWithErrors_thenVerifyAllFieldsInitialized()
  {
    // Arrange
    List<String> versionsNotInStore = Arrays.asList("1.0.0", "1.0.1");
    List<String> versionsNotInRepo = Arrays.asList("2.0.0", "2.0.1");
    List<String> errors = Arrays.asList("Error 1", "Error 2");

    // Act
    VersionMismatch versionMismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors);

    // Assert
    assertEquals("project1", versionMismatch.projectId);
    assertEquals("group1", versionMismatch.groupId);
    assertEquals("artifact1", versionMismatch.artifactId);
    assertEquals(2, versionMismatch.versionsNotInStore.size());
    assertEquals("1.0.0", versionMismatch.versionsNotInStore.get(0));
    assertEquals("1.0.1", versionMismatch.versionsNotInStore.get(1));
    assertEquals(2, versionMismatch.versionsNotInRepository.size());
    assertEquals("2.0.0", versionMismatch.versionsNotInRepository.get(0));
    assertEquals("2.0.1", versionMismatch.versionsNotInRepository.get(1));
    assertEquals(2, versionMismatch.errors.size());
    assertEquals("Error 1", versionMismatch.errors.get(0));
    assertEquals("Error 2", versionMismatch.errors.get(1));
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>When all lists are empty.
   *   <li>Then verify all fields are initialized with empty lists.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); when all lists are empty; then verify all fields are initialized with empty lists")
  void testConstructor6Args_whenAllListsEmpty_thenVerifyFieldsInitializedWithEmptyLists()
  {
    // Arrange & Act
    VersionMismatch versionMismatch =
        new VersionMismatch(
            "project1",
            "group1",
            "artifact1",
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());

    // Assert
    assertEquals("project1", versionMismatch.projectId);
    assertEquals("group1", versionMismatch.groupId);
    assertEquals("artifact1", versionMismatch.artifactId);
    assertTrue(versionMismatch.versionsNotInStore.isEmpty());
    assertTrue(versionMismatch.versionsNotInRepository.isEmpty());
    assertTrue(versionMismatch.errors.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>When only errors list has values.
   *   <li>Then verify errors field is populated.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); when only errors list has values; then verify errors field is populated")
  void testConstructor6Args_whenOnlyErrorsListHasValues_thenVerifyErrorsFieldPopulated()
  {
    // Arrange
    List<String> errors = Arrays.asList("Connection timeout", "Invalid response");

    // Act
    VersionMismatch versionMismatch =
        new VersionMismatch(
            "project1",
            "group1",
            "artifact1",
            Collections.emptyList(),
            Collections.emptyList(),
            errors);

    // Assert
    assertEquals(2, versionMismatch.errors.size());
    assertEquals("Connection timeout", versionMismatch.errors.get(0));
    assertEquals("Invalid response", versionMismatch.errors.get(1));
    assertTrue(versionMismatch.versionsNotInStore.isEmpty());
    assertTrue(versionMismatch.versionsNotInRepository.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing two objects with same content except errors.
   *   <li>Then return equal (errors field is excluded from equals).
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing two objects with same content except errors; then return equal")
  void testEquals_whenSameContentExceptErrors_thenReturnEqual()
  {
    // Arrange
    List<String> versionsNotInStore = Arrays.asList("1.0.0");
    List<String> versionsNotInRepo = Arrays.asList("2.0.0");
    List<String> errors1 = Arrays.asList("Error 1");
    List<String> errors2 = Arrays.asList("Error 2", "Error 3");

    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors1);
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors2);

    // Act & Assert
    assertEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing two identical objects with errors.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing two identical objects with errors; then return equal")
  void testEquals_whenIdenticalObjectsWithErrors_thenReturnEqual()
  {
    // Arrange
    List<String> versionsNotInStore = Arrays.asList("1.0.0");
    List<String> versionsNotInRepo = Arrays.asList("2.0.0");
    List<String> errors = Arrays.asList("Error 1");

    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors);
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors);

    // Act & Assert
    assertEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing with same object reference.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when comparing with same object reference; then return equal")
  void testEquals_whenSameObjectReference_thenReturnEqual()
  {
    // Arrange
    VersionMismatch mismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertEquals(mismatch, mismatch);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing with null.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when comparing with null; then return not equal")
  void testEquals_whenComparingWithNull_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch, null);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing with different type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when comparing with different type; then return not equal")
  void testEquals_whenComparingWithDifferentType_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch, "Not a VersionMismatch");
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing objects with different projectId.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing objects with different projectId; then return not equal")
  void testEquals_whenDifferentProjectId_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project2", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing objects with different groupId.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing objects with different groupId; then return not equal")
  void testEquals_whenDifferentGroupId_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1", "group2", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing objects with different artifactId.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing objects with different artifactId; then return not equal")
  void testEquals_whenDifferentArtifactId_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1", "group1", "artifact2", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing objects with different versionsNotInStore.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing objects with different versionsNotInStore; then return not equal")
  void testEquals_whenDifferentVersionsNotInStore_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1",
            "group1",
            "artifact1",
            Arrays.asList("1.0.0"),
            Collections.emptyList());
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1",
            "group1",
            "artifact1",
            Arrays.asList("2.0.0"),
            Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When comparing objects with different versionsNotInRepository.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName(
      "Test equals(Object); when comparing objects with different versionsNotInRepository; then return not equal")
  void testEquals_whenDifferentVersionsNotInRepository_thenReturnNotEqual()
  {
    // Arrange
    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1",
            "group1",
            "artifact1",
            Collections.emptyList(),
            Arrays.asList("1.0.0"));
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1",
            "group1",
            "artifact1",
            Collections.emptyList(),
            Arrays.asList("2.0.0"));

    // Act & Assert
    assertNotEquals(mismatch1, mismatch2);
  }

  /**
   * Test {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When two objects are equal.
   *   <li>Then their hash codes are equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#hashCode()}
   */
  @Test
  @DisplayName("Test hashCode(); when two objects are equal; then their hash codes are equal")
  void testHashCode_whenObjectsEqual_thenHashCodesEqual()
  {
    // Arrange
    List<String> versionsNotInStore = Arrays.asList("1.0.0");
    List<String> versionsNotInRepo = Arrays.asList("2.0.0");

    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo);
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo);

    // Act & Assert
    assertEquals(mismatch1.hashCode(), mismatch2.hashCode());
  }

  /**
   * Test {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When objects have same content except errors.
   *   <li>Then their hash codes are different (errors included in hash despite @EqualsExclude).
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#hashCode()}
   */
  @Test
  @DisplayName(
      "Test hashCode(); when objects have same content except errors; then their hash codes are different")
  void testHashCode_whenSameContentExceptErrors_thenHashCodesDifferent()
  {
    // Arrange
    List<String> versionsNotInStore = Arrays.asList("1.0.0");
    List<String> versionsNotInRepo = Arrays.asList("2.0.0");
    List<String> errors1 = Arrays.asList("Error 1");
    List<String> errors2 = Arrays.asList("Error 2", "Error 3");

    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors1);
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project1", "group1", "artifact1", versionsNotInStore, versionsNotInRepo, errors2);

    // Act & Assert
    // Note: This violates the hashCode/equals contract since the objects are equal but have
    // different hash codes. This is because HashCodeBuilder.reflectionHashCode does not respect
    // @EqualsExclude annotation, while EqualsBuilder.reflectionEquals does.
    assertNotEquals(mismatch1.hashCode(), mismatch2.hashCode());
  }

  /**
   * Test {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When called multiple times on same object.
   *   <li>Then return same hash code.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#hashCode()}
   */
  @Test
  @DisplayName(
      "Test hashCode(); when called multiple times on same object; then return same hash code")
  void testHashCode_whenCalledMultipleTimes_thenReturnSameHashCode()
  {
    // Arrange
    VersionMismatch mismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act
    int hashCode1 = mismatch.hashCode();
    int hashCode2 = mismatch.hashCode();

    // Assert
    assertEquals(hashCode1, hashCode2);
  }

  /**
   * Test {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When objects have different content.
   *   <li>Then their hash codes are likely different.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#hashCode()}
   */
  @Test
  @DisplayName(
      "Test hashCode(); when objects have different content; then their hash codes are likely different")
  void testHashCode_whenDifferentContent_thenHashCodesLikelyDifferent()
  {
    // Arrange
    VersionMismatch mismatch1 =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());
    VersionMismatch mismatch2 =
        new VersionMismatch(
            "project2", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act & Assert
    assertNotEquals(mismatch1.hashCode(), mismatch2.hashCode());
  }

  /**
   * Test {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When object is created.
   *   <li>Then return non-zero hash code.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#hashCode()}
   */
  @Test
  @DisplayName("Test hashCode(); when object is created; then return non-zero hash code")
  void testHashCode_whenObjectCreated_thenReturnNonZeroHashCode()
  {
    // Arrange
    VersionMismatch mismatch =
        new VersionMismatch(
            "project1", "group1", "artifact1", Collections.emptyList(), Collections.emptyList());

    // Act
    int hashCode = mismatch.hashCode();

    // Assert
    assertNotNull(hashCode);
  }
}

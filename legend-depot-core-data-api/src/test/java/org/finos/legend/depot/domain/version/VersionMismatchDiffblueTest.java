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
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VersionMismatchDiffblueTest 


{
  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>Then return {@link VersionMismatch#versionsNotInRepository} is {@link
   *       ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); given '42'; then return versionsNotInRepository is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List)"})
  void testNewVersionMismatch_given42_thenReturnVersionsNotInRepositoryIsArrayList()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    ArrayList<String> versionsNotInRepo = new ArrayList<>();
    versionsNotInRepo.add("42");
    versionsNotInRepo.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, versionsNotInRepo);

    // Assert
    assertEquals(versionsNotInRepo, actualVersionMismatch.versionsNotInRepository);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>Then return {@link VersionMismatch#versionsNotInRepository} is {@link
   *       ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); given '42'; then return versionsNotInRepository is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_given42_thenReturnVersionsNotInRepositoryIsArrayList2()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    ArrayList<String> versionsNotInRepo = new ArrayList<>();
    versionsNotInRepo.add("42");
    versionsNotInRepo.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch(
            "myproject", "42", "42", versionsNotInCache, versionsNotInRepo, new ArrayList<>());

    // Assert
    assertEquals(versionsNotInRepo, actualVersionMismatch.versionsNotInRepository);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>Then return {@link VersionMismatch#versionsNotInStore} is {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); given '42'; then return versionsNotInStore is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List)"})
  void testNewVersionMismatch_given42_thenReturnVersionsNotInStoreIsArrayList()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    versionsNotInCache.add("42");
    versionsNotInCache.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>());

    // Assert
    assertEquals("42", actualVersionMismatch.artifactId);
    assertEquals("42", actualVersionMismatch.groupId);
    assertEquals("myproject", actualVersionMismatch.projectId);
    assertTrue(actualVersionMismatch.errors.isEmpty());
    assertTrue(actualVersionMismatch.versionsNotInRepository.isEmpty());
    assertEquals(versionsNotInCache, actualVersionMismatch.versionsNotInStore);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>Then return {@link VersionMismatch#versionsNotInStore} is {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); given '42'; then return versionsNotInStore is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_given42_thenReturnVersionsNotInStoreIsArrayList2()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    versionsNotInCache.add("42");
    versionsNotInCache.add("foo");
    ArrayList<String> versionsNotInRepo = new ArrayList<>();

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch(
            "myproject", "42", "42", versionsNotInCache, versionsNotInRepo, new ArrayList<>());

    // Assert
    assertEquals("42", actualVersionMismatch.artifactId);
    assertEquals("42", actualVersionMismatch.groupId);
    assertEquals("myproject", actualVersionMismatch.projectId);
    assertEquals(versionsNotInCache, actualVersionMismatch.versionsNotInStore);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>When {@link ArrayList#ArrayList()} add {@code 42}.
   *   <li>Then return {@link VersionMismatch#errors} is {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); given '42'; when ArrayList() add '42'; then return errors is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_given42_whenArrayListAdd42_thenReturnErrorsIsArrayList()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    ArrayList<String> versionsNotInRepo = new ArrayList<>();

    ArrayList<String> errors = new ArrayList<>();
    errors.add("42");
    errors.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, versionsNotInRepo, errors);

    // Assert
    assertEquals(errors, actualVersionMismatch.errors);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>Given {@code foo}.
   *   <li>Then return {@link VersionMismatch#versionsNotInRepository} size is one.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); given 'foo'; then return versionsNotInRepository size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List)"})
  void testNewVersionMismatch_givenFoo_thenReturnVersionsNotInRepositorySizeIsOne()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    ArrayList<String> versionsNotInRepo = new ArrayList<>();
    versionsNotInRepo.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, versionsNotInRepo);

    // Assert
    List<String> stringList = actualVersionMismatch.versionsNotInRepository;
    assertEquals(1, stringList.size());
    assertEquals("foo", stringList.get(0));
    assertTrue(actualVersionMismatch.versionsNotInStore.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>Given {@code foo}.
   *   <li>Then return {@link VersionMismatch#versionsNotInRepository} size is one.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); given 'foo'; then return versionsNotInRepository size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_givenFoo_thenReturnVersionsNotInRepositorySizeIsOne2()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    ArrayList<String> versionsNotInRepo = new ArrayList<>();
    versionsNotInRepo.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch(
            "myproject", "42", "42", versionsNotInCache, versionsNotInRepo, new ArrayList<>());

    // Assert
    List<String> stringList = actualVersionMismatch.versionsNotInRepository;
    assertEquals(1, stringList.size());
    assertEquals("foo", stringList.get(0));
    assertTrue(actualVersionMismatch.versionsNotInStore.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>Given {@code foo}.
   *   <li>Then return {@link VersionMismatch#versionsNotInStore} is {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); given 'foo'; then return versionsNotInStore is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List)"})
  void testNewVersionMismatch_givenFoo_thenReturnVersionsNotInStoreIsArrayList()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    versionsNotInCache.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>());

    // Assert
    assertEquals("42", actualVersionMismatch.artifactId);
    assertEquals("42", actualVersionMismatch.groupId);
    assertEquals("myproject", actualVersionMismatch.projectId);
    assertTrue(actualVersionMismatch.errors.isEmpty());
    assertTrue(actualVersionMismatch.versionsNotInRepository.isEmpty());
    assertEquals(versionsNotInCache, actualVersionMismatch.versionsNotInStore);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>Given {@code foo}.
   *   <li>Then return {@link VersionMismatch#versionsNotInStore} is {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); given 'foo'; then return versionsNotInStore is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_givenFoo_thenReturnVersionsNotInStoreIsArrayList2()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    versionsNotInCache.add("foo");
    ArrayList<String> versionsNotInRepo = new ArrayList<>();

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch(
            "myproject", "42", "42", versionsNotInCache, versionsNotInRepo, new ArrayList<>());

    // Assert
    assertEquals("42", actualVersionMismatch.artifactId);
    assertEquals("42", actualVersionMismatch.groupId);
    assertEquals("myproject", actualVersionMismatch.projectId);
    assertEquals(versionsNotInCache, actualVersionMismatch.versionsNotInStore);
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>Given {@code foo}.
   *   <li>When {@link ArrayList#ArrayList()} add {@code foo}.
   *   <li>Then return {@link VersionMismatch#errors} size is one.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); given 'foo'; when ArrayList() add 'foo'; then return errors size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_givenFoo_whenArrayListAddFoo_thenReturnErrorsSizeIsOne()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    ArrayList<String> versionsNotInRepo = new ArrayList<>();

    ArrayList<String> errors = new ArrayList<>();
    errors.add("foo");

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, versionsNotInRepo, errors);

    // Assert
    List<String> stringList = actualVersionMismatch.errors;
    assertEquals(1, stringList.size());
    assertEquals("foo", stringList.get(0));
    assertTrue(actualVersionMismatch.versionsNotInStore.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List, List)}.
   *
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.
   *   <li>Then return {@link VersionMismatch#artifactId} is {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List, List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List, List); when ArrayList(); then return artifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List, List)"})
  void testNewVersionMismatch_whenArrayList_thenReturnArtifactIdIs42()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    ArrayList<String> versionsNotInRepo = new ArrayList<>();

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch(
            "myproject", "42", "42", versionsNotInCache, versionsNotInRepo, new ArrayList<>());

    // Assert
    assertEquals("42", actualVersionMismatch.artifactId);
    assertEquals("42", actualVersionMismatch.groupId);
    assertEquals("myproject", actualVersionMismatch.projectId);
    assertTrue(actualVersionMismatch.versionsNotInStore.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#VersionMismatch(String, String, String, List, List)}.
   *
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.
   *   <li>Then return {@link VersionMismatch#versionsNotInStore} Empty.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#VersionMismatch(String, String, String, List,
   * List)}
   */
  @Test
  @DisplayName(
      "Test new VersionMismatch(String, String, String, List, List); when ArrayList(); then return versionsNotInStore Empty")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VersionMismatch.<init>(String, String, String, List, List)"})
  void testNewVersionMismatch_whenArrayList_thenReturnVersionsNotInStoreEmpty()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    // Act
    VersionMismatch actualVersionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>());

    // Assert
    assertEquals("42", actualVersionMismatch.artifactId);
    assertEquals("42", actualVersionMismatch.groupId);
    assertEquals("myproject", actualVersionMismatch.projectId);
    assertTrue(actualVersionMismatch.errors.isEmpty());
    assertTrue(actualVersionMismatch.versionsNotInRepository.isEmpty());
    assertTrue(actualVersionMismatch.versionsNotInStore.isEmpty());
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}, and {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionMismatch#equals(Object)}
   *   <li>{@link VersionMismatch#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionMismatch.equals(Object)", "int VersionMismatch.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    VersionMismatch versionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>());
    ArrayList<String> versionsNotInCache2 = new ArrayList<>();
    VersionMismatch versionMismatch2 =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache2, new ArrayList<>());

    // Act and Assert
    assertEquals(versionMismatch, versionMismatch2);
    assertEquals(versionMismatch.hashCode(), versionMismatch2.hashCode());
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}, and {@link VersionMismatch#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionMismatch#equals(Object)}
   *   <li>{@link VersionMismatch#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionMismatch.equals(Object)", "int VersionMismatch.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    VersionMismatch versionMismatch =
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>());

    // Act and Assert
    assertEquals(versionMismatch, versionMismatch);
    int expectedHashCodeResult = versionMismatch.hashCode();
    assertEquals(expectedHashCodeResult, versionMismatch.hashCode());
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionMismatch.equals(Object)", "int VersionMismatch.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();
    VersionMismatch versionMismatch =
        new VersionMismatch("42", "42", "42", versionsNotInCache, new ArrayList<>());
    ArrayList<String> versionsNotInCache2 = new ArrayList<>();

    // Act and Assert
    assertNotEquals(
        versionMismatch,
        new VersionMismatch("myproject", "42", "42", versionsNotInCache2, new ArrayList<>()));
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionMismatch.equals(Object)", "int VersionMismatch.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    // Act and Assert
    assertNotEquals(
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>()), null);
  }

  /**
   * Test {@link VersionMismatch#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionMismatch#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionMismatch.equals(Object)", "int VersionMismatch.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange
    ArrayList<String> versionsNotInCache = new ArrayList<>();

    // Act and Assert
    assertNotEquals(
        new VersionMismatch("myproject", "42", "42", versionsNotInCache, new ArrayList<>()),
        "Different type to VersionMismatch");
  }
}

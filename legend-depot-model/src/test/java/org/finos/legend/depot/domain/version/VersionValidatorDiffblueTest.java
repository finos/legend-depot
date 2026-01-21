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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VersionValidatorDiffblueTest



{
  /**
   * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)}.
   *
   * <p>Method under test: {@link VersionValidator#BRANCH_SNAPSHOT(String)}
   */
  @Test
  @DisplayName("Test BRANCH_SNAPSHOT(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String VersionValidator.BRANCH_SNAPSHOT(String)"})
  void testBRANCH_SNAPSHOT()
  {
    // Arrange, Act and Assert
    assertEquals(
        "janedoe/featurebranch-SNAPSHOT",
        VersionValidator.BRANCH_SNAPSHOT("janedoe/featurebranch"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when '42'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_when42_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValid("42"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.2Version Id}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when '1.0.2Version Id'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_when102VersionId_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValid("1.0.2Version Id"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.2}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when '1.0.2'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_when102_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(VersionValidator.isValid("1.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code 421.0.2}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when '421.0.2'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_when42102_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(VersionValidator.isValid("421.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.21.0.2}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when '1.0.21.0.2'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_when102102_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValid("1.0.21.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When empty string.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when empty string; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_whenEmptyString_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValid(""));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when 'null'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_whenNull_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValid(null));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code -SNAPSHOT}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when '-SNAPSHOT'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_whenSnapshot_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(VersionValidator.isValid("-SNAPSHOT"));
  }

  /**
   * Test {@link VersionValidator#isValid(String)}.
   *
   * <ul>
   *   <li>When {@code Version Id1.0.2}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValid(String)}
   */
  @Test
  @DisplayName("Test isValid(String); when 'Version Id1.0.2'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValid(String)"})
  void testIsValid_whenVersionId102_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValid("Version Id1.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName("Test isValidReleaseVersion(String); when '42'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_when42_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValidReleaseVersion("42"));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.2Invalid version string: "}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName(
      "Test isValidReleaseVersion(String); when '1.0.2Invalid version string: \"'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_when102InvalidVersionString_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValidReleaseVersion("1.0.2Invalid version string: \""));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.2}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName("Test isValidReleaseVersion(String); when '1.0.2'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_when102_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(VersionValidator.isValidReleaseVersion("1.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 421.0.2}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName("Test isValidReleaseVersion(String); when '421.0.2'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_when42102_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(VersionValidator.isValidReleaseVersion("421.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.21.0.2}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName("Test isValidReleaseVersion(String); when '1.0.21.0.2'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_when102102_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValidReleaseVersion("1.0.21.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code Invalid version string: "1.0.2}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName(
      "Test isValidReleaseVersion(String); when 'Invalid version string: \"1.0.2'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_whenInvalidVersionString102_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValidReleaseVersion("Invalid version string: \"1.0.2"));
  }

  /**
   * Test {@link VersionValidator#isValidReleaseVersion(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isValidReleaseVersion(String)}
   */
  @Test
  @DisplayName("Test isValidReleaseVersion(String); when 'null'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isValidReleaseVersion(String)"})
  void testIsValidReleaseVersion_whenNull_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isValidReleaseVersion(null));
  }

  /**
   * Test {@link VersionValidator#isSnapshotVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isSnapshotVersion(String)}
   */
  @Test
  @DisplayName("Test isSnapshotVersion(String); when '42'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isSnapshotVersion(String)"})
  void testIsSnapshotVersion_when42_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isSnapshotVersion("42"));
  }

  /**
   * Test {@link VersionValidator#isSnapshotVersion(String)}.
   *
   * <ul>
   *   <li>When {@code -SNAPSHOT}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link VersionValidator#isSnapshotVersion(String)}
   */
  @Test
  @DisplayName("Test isSnapshotVersion(String); when '-SNAPSHOT'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isSnapshotVersion(String)"})
  void testIsSnapshotVersion_whenSnapshot_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(VersionValidator.isSnapshotVersion("-SNAPSHOT"));
  }

  /**
   * Test {@link VersionValidator#isVersionAlias(String)}.
   *
   * <p>Method under test: {@link VersionValidator#isVersionAlias(String)}
   */
  @Test
  @DisplayName("Test isVersionAlias(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean VersionValidator.isVersionAlias(String)"})
  void testIsVersionAlias()
  {
    // Arrange, Act and Assert
    assertFalse(VersionValidator.isVersionAlias("42"));
  }
}

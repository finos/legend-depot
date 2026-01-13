package org.finos.legend.depot.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class CoordinateValidatorDiffblueTest {
  /**
   * Test {@link CoordinateValidator#isValidArtifactId(String)}.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidArtifactId(String)}
   */
  @Test
  @DisplayName("Test isValidArtifactId(String); when '42'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidArtifactId(String)"})
  void testIsValidArtifactId_when42_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(CoordinateValidator.isValidArtifactId("42"));
  }

  /**
   * Test {@link CoordinateValidator#isValidArtifactId(String)}.
   *
   * <ul>
   *   <li>When empty string.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidArtifactId(String)}
   */
  @Test
  @DisplayName("Test isValidArtifactId(String); when empty string; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidArtifactId(String)"})
  void testIsValidArtifactId_whenEmptyString_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(CoordinateValidator.isValidArtifactId(""));
  }

  /**
   * Test {@link CoordinateValidator#isValidArtifactId(String)}.
   *
   * <ul>
   *   <li>When {@code lll-lll-lll}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidArtifactId(String)}
   */
  @Test
  @DisplayName("Test isValidArtifactId(String); when 'lll-lll-lll'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidArtifactId(String)"})
  void testIsValidArtifactId_whenLllLllLll_thenReturnTrue() {
    // Arrange, Act and Assert
    assertTrue(CoordinateValidator.isValidArtifactId("lll-lll-lll"));
  }

  /**
   * Test {@link CoordinateValidator#isValidArtifactId(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidArtifactId(String)}
   */
  @Test
  @DisplayName("Test isValidArtifactId(String); when 'null'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidArtifactId(String)"})
  void testIsValidArtifactId_whenNull_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(CoordinateValidator.isValidArtifactId(null));
  }

  /**
   * Test {@link CoordinateValidator#isValidGroupId(String)}.
   *
   * <ul>
   *   <li>When {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidGroupId(String)}
   */
  @Test
  @DisplayName("Test isValidGroupId(String); when '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidGroupId(String)"})
  void testIsValidGroupId_when42() {
    // Arrange, Act and Assert
    assertFalse(CoordinateValidator.isValidGroupId("42"));
  }

  /**
   * Test {@link CoordinateValidator#isValidGroupId(String)}.
   *
   * <ul>
   *   <li>When empty string.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidGroupId(String)}
   */
  @Test
  @DisplayName("Test isValidGroupId(String); when empty string")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidGroupId(String)"})
  void testIsValidGroupId_whenEmptyString() {
    // Arrange, Act and Assert
    assertFalse(CoordinateValidator.isValidGroupId(""));
  }

  /**
   * Test {@link CoordinateValidator#isValidGroupId(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link CoordinateValidator#isValidGroupId(String)}
   */
  @Test
  @DisplayName("Test isValidGroupId(String); when 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean CoordinateValidator.isValidGroupId(String)"})
  void testIsValidGroupId_whenNull() {
    // Arrange, Act and Assert
    assertFalse(CoordinateValidator.isValidGroupId(null));
  }
}

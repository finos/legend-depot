package org.finos.legend.depot.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class DepotEntityOverviewDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotEntityOverview#DepotEntityOverview(String, String, String, String, String)}
   *   <li>{@link DepotEntityOverview#getClassifierPath()}
   *   <li>{@link DepotEntityOverview#getPath()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void DepotEntityOverview.<init>(String, String, String, String, String)",
    "String DepotEntityOverview.getClassifierPath()",
    "String DepotEntityOverview.getPath()"
  })
  void testGettersAndSetters() {
    // Arrange and Act
    DepotEntityOverview actualDepotEntityOverview =
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path");
    String actualClassifierPath = actualDepotEntityOverview.getClassifierPath();

    // Assert
    assertEquals("/etc/config.properties", actualDepotEntityOverview.getPath());
    assertEquals("42", actualDepotEntityOverview.getArtifactId());
    assertEquals("42", actualDepotEntityOverview.getGroupId());
    assertEquals("42", actualDepotEntityOverview.getVersionId());
    assertEquals("Classifier Path", actualClassifierPath);
    assertNull(actualDepotEntityOverview.getEntity());
    assertFalse(actualDepotEntityOverview.isVersionedEntity());
  }

  /**
   * Test {@link DepotEntityOverview#equals(Object)}, and {@link DepotEntityOverview#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotEntityOverview#equals(Object)}
   *   <li>{@link DepotEntityOverview#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean DepotEntityOverview.equals(Object)",
    "int DepotEntityOverview.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    DepotEntityOverview depotEntityOverview =
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path");
    DepotEntityOverview depotEntityOverview2 =
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path");

    // Act and Assert
    assertEquals(depotEntityOverview, depotEntityOverview2);
    assertEquals(depotEntityOverview.hashCode(), depotEntityOverview2.hashCode());
  }

  /**
   * Test {@link DepotEntityOverview#equals(Object)}, and {@link DepotEntityOverview#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link DepotEntityOverview#equals(Object)}
   *   <li>{@link DepotEntityOverview#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean DepotEntityOverview.equals(Object)",
    "int DepotEntityOverview.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    DepotEntityOverview depotEntityOverview =
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path");

    // Act and Assert
    assertEquals(depotEntityOverview, depotEntityOverview);
    int expectedHashCodeResult = depotEntityOverview.hashCode();
    assertEquals(expectedHashCodeResult, depotEntityOverview.hashCode());
  }

  /**
   * Test {@link DepotEntityOverview#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean DepotEntityOverview.equals(Object)",
    "int DepotEntityOverview.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    DepotEntityOverview depotEntityOverview =
        new DepotEntityOverview(
            "Group Id", "42", "42", "/etc/config.properties", "Classifier Path");

    // Act and Assert
    assertNotEquals(
        depotEntityOverview,
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path"));
  }

  /**
   * Test {@link DepotEntityOverview#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean DepotEntityOverview.equals(Object)",
    "int DepotEntityOverview.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path"),
        null);
  }

  /**
   * Test {@link DepotEntityOverview#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link DepotEntityOverview#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean DepotEntityOverview.equals(Object)",
    "int DepotEntityOverview.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(
        new DepotEntityOverview("42", "42", "42", "/etc/config.properties", "Classifier Path"),
        "Different type to DepotEntityOverview");
  }
}

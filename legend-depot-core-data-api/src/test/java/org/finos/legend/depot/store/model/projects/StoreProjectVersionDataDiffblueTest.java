package org.finos.legend.depot.store.model.projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoreProjectVersionDataDiffblueTest {
  /**
   * Test {@link StoreProjectVersionData#StoreProjectVersionData()}.
   *
   * <p>Method under test: {@link StoreProjectVersionData#StoreProjectVersionData()}
   */
  @Test
  @DisplayName("Test new StoreProjectVersionData()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void StoreProjectVersionData.<init>()"})
  void testNewStoreProjectVersionData() {
    // Arrange and Act
    StoreProjectVersionData actualStoreProjectVersionData = new StoreProjectVersionData();

    // Assert
    assertEquals("", actualStoreProjectVersionData.getId());
    assertNull(actualStoreProjectVersionData.getArtifactId());
    assertNull(actualStoreProjectVersionData.getGroupId());
    assertNull(actualStoreProjectVersionData.getVersionId());
    ProjectVersionData versionData = actualStoreProjectVersionData.getVersionData();
    assertNull(versionData.getExclusionReason());
    assertNull(actualStoreProjectVersionData.getCreated());
    assertNull(actualStoreProjectVersionData.getUpdated());
    assertNull(versionData.getManifestProperties());
    assertFalse(versionData.isDeprecated());
    assertFalse(versionData.isExcluded());
    assertFalse(actualStoreProjectVersionData.isEvicted());
    assertTrue(versionData.getDependencies().isEmpty());
    assertTrue(versionData.getProperties().isEmpty());
    VersionDependencyReport transitiveDependenciesReport =
        actualStoreProjectVersionData.getTransitiveDependenciesReport();
    assertTrue(transitiveDependenciesReport.getTransitiveDependencies().isEmpty());
    assertTrue(transitiveDependenciesReport.isValid());
  }

  /**
   * Test {@link StoreProjectVersionData#StoreProjectVersionData(String, String, String)}.
   *
   * <p>Method under test: {@link StoreProjectVersionData#StoreProjectVersionData(String, String,
   * String)}
   */
  @Test
  @DisplayName("Test new StoreProjectVersionData(String, String, String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void StoreProjectVersionData.<init>(String, String, String)"})
  void testNewStoreProjectVersionData2() {
    // Arrange and Act
    StoreProjectVersionData actualStoreProjectVersionData =
        new StoreProjectVersionData("42", "42", "42");

    // Assert
    assertEquals("", actualStoreProjectVersionData.getId());
    assertEquals("42", actualStoreProjectVersionData.getArtifactId());
    assertEquals("42", actualStoreProjectVersionData.getGroupId());
    assertEquals("42", actualStoreProjectVersionData.getVersionId());
    ProjectVersionData versionData = actualStoreProjectVersionData.getVersionData();
    assertNull(versionData.getExclusionReason());
    assertNull(actualStoreProjectVersionData.getUpdated());
    assertNull(versionData.getManifestProperties());
    assertFalse(versionData.isDeprecated());
    assertFalse(versionData.isExcluded());
    assertFalse(actualStoreProjectVersionData.isEvicted());
    assertTrue(versionData.getDependencies().isEmpty());
    assertTrue(versionData.getProperties().isEmpty());
    VersionDependencyReport transitiveDependenciesReport =
        actualStoreProjectVersionData.getTransitiveDependenciesReport();
    assertTrue(transitiveDependenciesReport.getTransitiveDependencies().isEmpty());
    assertTrue(transitiveDependenciesReport.isValid());
  }

  /**
   * Test {@link StoreProjectVersionData#StoreProjectVersionData(String, String, String, boolean,
   * ProjectVersionData)}.
   *
   * <p>Method under test: {@link StoreProjectVersionData#StoreProjectVersionData(String, String,
   * String, boolean, ProjectVersionData)}
   */
  @Test
  @DisplayName(
      "Test new StoreProjectVersionData(String, String, String, boolean, ProjectVersionData)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoreProjectVersionData.<init>(String, String, String, boolean, ProjectVersionData)"
  })
  void testNewStoreProjectVersionData3() {
    // Arrange
    ProjectVersionData versionData = new ProjectVersionData();

    // Act
    StoreProjectVersionData actualStoreProjectVersionData =
        new StoreProjectVersionData("42", "42", "42", true, versionData);

    // Assert
    assertEquals("", actualStoreProjectVersionData.getId());
    assertEquals("42", actualStoreProjectVersionData.getArtifactId());
    assertEquals("42", actualStoreProjectVersionData.getGroupId());
    assertEquals("42", actualStoreProjectVersionData.getVersionId());
    assertNull(actualStoreProjectVersionData.getUpdated());
    assertTrue(actualStoreProjectVersionData.isEvicted());
    assertSame(versionData, actualStoreProjectVersionData.getVersionData());
  }

  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectVersionData#setCreated(Date)}
   *   <li>{@link StoreProjectVersionData#setEvicted(boolean)}
   *   <li>{@link StoreProjectVersionData#setTransitiveDependenciesReport(VersionDependencyReport)}
   *   <li>{@link StoreProjectVersionData#setUpdated(Date)}
   *   <li>{@link StoreProjectVersionData#setVersionData(ProjectVersionData)}
   *   <li>{@link StoreProjectVersionData#getCreated()}
   *   <li>{@link StoreProjectVersionData#getId()}
   *   <li>{@link StoreProjectVersionData#getTransitiveDependenciesReport()}
   *   <li>{@link StoreProjectVersionData#getUpdated()}
   *   <li>{@link StoreProjectVersionData#getVersionData()}
   *   <li>{@link StoreProjectVersionData#isEvicted()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "Date StoreProjectVersionData.getCreated()",
    "String StoreProjectVersionData.getId()",
    "VersionDependencyReport StoreProjectVersionData.getTransitiveDependenciesReport()",
    "Date StoreProjectVersionData.getUpdated()",
    "ProjectVersionData StoreProjectVersionData.getVersionData()",
    "boolean StoreProjectVersionData.isEvicted()",
    "void StoreProjectVersionData.setCreated(Date)",
    "void StoreProjectVersionData.setEvicted(boolean)",
    "void StoreProjectVersionData.setTransitiveDependenciesReport(VersionDependencyReport)",
    "void StoreProjectVersionData.setUpdated(Date)",
    "void StoreProjectVersionData.setVersionData(ProjectVersionData)"
  })
  void testGettersAndSetters() {
    // Arrange
    StoreProjectVersionData storeProjectVersionData = new StoreProjectVersionData();
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    storeProjectVersionData.setCreated(created);
    storeProjectVersionData.setEvicted(true);
    VersionDependencyReport transitiveDependenciesReport = new VersionDependencyReport();
    storeProjectVersionData.setTransitiveDependenciesReport(transitiveDependenciesReport);
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    storeProjectVersionData.setUpdated(updated);
    ProjectVersionData versionData = new ProjectVersionData();
    storeProjectVersionData.setVersionData(versionData);
    Date actualCreated = storeProjectVersionData.getCreated();
    String actualId = storeProjectVersionData.getId();
    VersionDependencyReport actualTransitiveDependenciesReport =
        storeProjectVersionData.getTransitiveDependenciesReport();
    Date actualUpdated = storeProjectVersionData.getUpdated();
    ProjectVersionData actualVersionData = storeProjectVersionData.getVersionData();

    // Assert
    assertEquals("", actualId);
    assertTrue(storeProjectVersionData.isEvicted());
    assertSame(versionData, actualVersionData);
    assertSame(transitiveDependenciesReport, actualTransitiveDependenciesReport);
    assertSame(created, actualCreated);
    assertSame(updated, actualUpdated);
  }

  /**
   * Test {@link StoreProjectVersionData#equals(Object)}, and {@link
   * StoreProjectVersionData#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectVersionData#equals(Object)}
   *   <li>{@link StoreProjectVersionData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoreProjectVersionData.equals(Object)",
    "int StoreProjectVersionData.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    StoreProjectVersionData storeProjectVersionData = new StoreProjectVersionData();

    // Act and Assert
    assertEquals(storeProjectVersionData, storeProjectVersionData);
    int expectedHashCodeResult = storeProjectVersionData.hashCode();
    assertEquals(expectedHashCodeResult, storeProjectVersionData.hashCode());
  }

  /**
   * Test {@link StoreProjectVersionData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoreProjectVersionData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoreProjectVersionData.equals(Object)",
    "int StoreProjectVersionData.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    StoreProjectVersionData storeProjectVersionData = new StoreProjectVersionData();

    // Act and Assert
    assertNotEquals(storeProjectVersionData, new StoreProjectVersionData());
  }

  /**
   * Test {@link StoreProjectVersionData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoreProjectVersionData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoreProjectVersionData.equals(Object)",
    "int StoreProjectVersionData.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(new StoreProjectVersionData(), null);
  }

  /**
   * Test {@link StoreProjectVersionData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoreProjectVersionData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean StoreProjectVersionData.equals(Object)",
    "int StoreProjectVersionData.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(new StoreProjectVersionData(), "Different type to StoreProjectVersionData");
  }
}

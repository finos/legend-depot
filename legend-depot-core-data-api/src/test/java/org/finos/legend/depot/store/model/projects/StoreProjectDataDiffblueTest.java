package org.finos.legend.depot.store.model.projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoreProjectDataDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>Then return ArtifactId is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectData#StoreProjectData()}
   *   <li>{@link StoreProjectData#setDefaultBranch(String)}
   *   <li>{@link StoreProjectData#setLatestVersion(String)}
   *   <li>{@link StoreProjectData#getDefaultBranch()}
   *   <li>{@link StoreProjectData#getId()}
   *   <li>{@link StoreProjectData#getLatestVersion()}
   *   <li>{@link StoreProjectData#getProjectId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; then return ArtifactId is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoreProjectData.<init>()",
    "void StoreProjectData.<init>(String, String, String)",
    "void StoreProjectData.<init>(String, String, String, String, String)",
    "String StoreProjectData.getDefaultBranch()",
    "String StoreProjectData.getId()",
    "String StoreProjectData.getLatestVersion()",
    "String StoreProjectData.getProjectId()",
    "void StoreProjectData.setDefaultBranch(String)",
    "void StoreProjectData.setLatestVersion(String)"
  })
  void testGettersAndSetters_thenReturnArtifactIdIsNull() {
    // Arrange and Act
    StoreProjectData actualStoreProjectData = new StoreProjectData();
    actualStoreProjectData.setDefaultBranch("janedoe/featurebranch");
    actualStoreProjectData.setLatestVersion("1.0.2");
    String actualDefaultBranch = actualStoreProjectData.getDefaultBranch();
    String actualId = actualStoreProjectData.getId();
    String actualLatestVersion = actualStoreProjectData.getLatestVersion();
    String actualProjectId = actualStoreProjectData.getProjectId();

    // Assert
    assertEquals("", actualId);
    assertEquals("1.0.2", actualLatestVersion);
    assertEquals("janedoe/featurebranch", actualDefaultBranch);
    assertNull(actualStoreProjectData.getArtifactId());
    assertNull(actualStoreProjectData.getGroupId());
    assertNull(actualProjectId);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code janedoe/featurebranch}.
   *   <li>Then return ArtifactId is {@code 42}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectData#StoreProjectData(String, String, String, String, String)}
   *   <li>{@link StoreProjectData#setDefaultBranch(String)}
   *   <li>{@link StoreProjectData#setLatestVersion(String)}
   *   <li>{@link StoreProjectData#getDefaultBranch()}
   *   <li>{@link StoreProjectData#getId()}
   *   <li>{@link StoreProjectData#getLatestVersion()}
   *   <li>{@link StoreProjectData#getProjectId()}
   * </ul>
   */
  @Test
  @DisplayName(
      "Test getters and setters; when 'janedoe/featurebranch'; then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoreProjectData.<init>()",
    "void StoreProjectData.<init>(String, String, String)",
    "void StoreProjectData.<init>(String, String, String, String, String)",
    "String StoreProjectData.getDefaultBranch()",
    "String StoreProjectData.getId()",
    "String StoreProjectData.getLatestVersion()",
    "String StoreProjectData.getProjectId()",
    "void StoreProjectData.setDefaultBranch(String)",
    "void StoreProjectData.setLatestVersion(String)"
  })
  void testGettersAndSetters_whenJanedoeFeaturebranch_thenReturnArtifactIdIs42() {
    // Arrange and Act
    StoreProjectData actualStoreProjectData =
        new StoreProjectData("myproject", "42", "42", "janedoe/featurebranch", "1.0.2");
    actualStoreProjectData.setDefaultBranch("janedoe/featurebranch");
    actualStoreProjectData.setLatestVersion("1.0.2");
    String actualDefaultBranch = actualStoreProjectData.getDefaultBranch();
    String actualId = actualStoreProjectData.getId();
    String actualLatestVersion = actualStoreProjectData.getLatestVersion();
    String actualProjectId = actualStoreProjectData.getProjectId();

    // Assert
    assertEquals("", actualId);
    assertEquals("1.0.2", actualLatestVersion);
    assertEquals("42", actualStoreProjectData.getArtifactId());
    assertEquals("42", actualStoreProjectData.getGroupId());
    assertEquals("janedoe/featurebranch", actualDefaultBranch);
    assertEquals("myproject", actualProjectId);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code myproject}.
   *   <li>Then return ArtifactId is {@code 42}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectData#StoreProjectData(String, String, String)}
   *   <li>{@link StoreProjectData#setDefaultBranch(String)}
   *   <li>{@link StoreProjectData#setLatestVersion(String)}
   *   <li>{@link StoreProjectData#getDefaultBranch()}
   *   <li>{@link StoreProjectData#getId()}
   *   <li>{@link StoreProjectData#getLatestVersion()}
   *   <li>{@link StoreProjectData#getProjectId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'myproject'; then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoreProjectData.<init>()",
    "void StoreProjectData.<init>(String, String, String)",
    "void StoreProjectData.<init>(String, String, String, String, String)",
    "String StoreProjectData.getDefaultBranch()",
    "String StoreProjectData.getId()",
    "String StoreProjectData.getLatestVersion()",
    "String StoreProjectData.getProjectId()",
    "void StoreProjectData.setDefaultBranch(String)",
    "void StoreProjectData.setLatestVersion(String)"
  })
  void testGettersAndSetters_whenMyproject_thenReturnArtifactIdIs42() {
    // Arrange and Act
    StoreProjectData actualStoreProjectData = new StoreProjectData("myproject", "42", "42");
    actualStoreProjectData.setDefaultBranch("janedoe/featurebranch");
    actualStoreProjectData.setLatestVersion("1.0.2");
    String actualDefaultBranch = actualStoreProjectData.getDefaultBranch();
    String actualId = actualStoreProjectData.getId();
    String actualLatestVersion = actualStoreProjectData.getLatestVersion();
    String actualProjectId = actualStoreProjectData.getProjectId();

    // Assert
    assertEquals("", actualId);
    assertEquals("1.0.2", actualLatestVersion);
    assertEquals("42", actualStoreProjectData.getArtifactId());
    assertEquals("42", actualStoreProjectData.getGroupId());
    assertEquals("janedoe/featurebranch", actualDefaultBranch);
    assertEquals("myproject", actualProjectId);
  }

  /**
   * Test {@link StoreProjectData#evaluateLatestVersionAndUpdate(String)}.
   *
   * <p>Method under test: {@link StoreProjectData#evaluateLatestVersionAndUpdate(String)}
   */
  @Test
  @DisplayName("Test evaluateLatestVersionAndUpdate(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.evaluateLatestVersionAndUpdate(String)"})
  void testEvaluateLatestVersionAndUpdate() {
    // Arrange
    StoreProjectData storeProjectData = new StoreProjectData("myproject", "42", "42");

    // Act
    boolean actualEvaluateLatestVersionAndUpdateResult =
        storeProjectData.evaluateLatestVersionAndUpdate("2020-03-01");

    // Assert
    assertEquals("2020-03-01", storeProjectData.getLatestVersion());
    assertTrue(actualEvaluateLatestVersionAndUpdateResult);
  }

  /**
   * Test {@link StoreProjectData#evaluateLatestVersionAndUpdate(String)}.
   *
   * <p>Method under test: {@link StoreProjectData#evaluateLatestVersionAndUpdate(String)}
   */
  @Test
  @DisplayName("Test evaluateLatestVersionAndUpdate(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.evaluateLatestVersionAndUpdate(String)"})
  void testEvaluateLatestVersionAndUpdate2() {
    // Arrange
    StoreProjectData storeProjectData = new StoreProjectData("myproject", "42", "42");
    storeProjectData.setLatestVersion("foo");

    // Act
    boolean actualEvaluateLatestVersionAndUpdateResult =
        storeProjectData.evaluateLatestVersionAndUpdate("-SNAPSHOT");

    // Assert
    assertEquals("foo", storeProjectData.getLatestVersion());
    assertFalse(actualEvaluateLatestVersionAndUpdateResult);
  }

  /**
   * Test {@link StoreProjectData#evaluateLatestVersionAndUpdate(String)}.
   *
   * <p>Method under test: {@link StoreProjectData#evaluateLatestVersionAndUpdate(String)}
   */
  @Test
  @DisplayName("Test evaluateLatestVersionAndUpdate(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.evaluateLatestVersionAndUpdate(String)"})
  void testEvaluateLatestVersionAndUpdate3() {
    // Arrange
    StoreProjectData storeProjectData =
        new StoreProjectData("myproject", "42", "42", "janedoe/featurebranch", "1.0.2");

    // Act
    boolean actualEvaluateLatestVersionAndUpdateResult =
        storeProjectData.evaluateLatestVersionAndUpdate("1.0.2");

    // Assert
    assertEquals("1.0.2", storeProjectData.getLatestVersion());
    assertFalse(actualEvaluateLatestVersionAndUpdateResult);
  }

  /**
   * Test {@link StoreProjectData#equals(Object)}, and {@link StoreProjectData#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectData#equals(Object)}
   *   <li>{@link StoreProjectData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.equals(Object)", "int StoreProjectData.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    StoreProjectData storeProjectData = new StoreProjectData("myproject", "42", "42");
    StoreProjectData storeProjectData2 = new StoreProjectData("myproject", "42", "42");

    // Act and Assert
    assertEquals(storeProjectData, storeProjectData2);
    assertEquals(storeProjectData.hashCode(), storeProjectData2.hashCode());
  }

  /**
   * Test {@link StoreProjectData#equals(Object)}, and {@link StoreProjectData#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoreProjectData#equals(Object)}
   *   <li>{@link StoreProjectData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.equals(Object)", "int StoreProjectData.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    StoreProjectData storeProjectData = new StoreProjectData("myproject", "42", "42");

    // Act and Assert
    assertEquals(storeProjectData, storeProjectData);
    int expectedHashCodeResult = storeProjectData.hashCode();
    assertEquals(expectedHashCodeResult, storeProjectData.hashCode());
  }

  /**
   * Test {@link StoreProjectData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoreProjectData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.equals(Object)", "int StoreProjectData.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    StoreProjectData storeProjectData = new StoreProjectData("42", "42", "42");

    // Act and Assert
    assertNotEquals(storeProjectData, new StoreProjectData("myproject", "42", "42"));
  }

  /**
   * Test {@link StoreProjectData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoreProjectData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.equals(Object)", "int StoreProjectData.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(new StoreProjectData("myproject", "42", "42"), null);
  }

  /**
   * Test {@link StoreProjectData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoreProjectData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoreProjectData.equals(Object)", "int StoreProjectData.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(
        new StoreProjectData("myproject", "42", "42"), "Different type to StoreProjectData");
  }
}

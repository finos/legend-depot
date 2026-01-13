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

class StoredEntityDataDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>Then return EntityAttributes Empty.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredEntityData#StoredEntityData(String, String, String, EntityDefinition, Map)}
   *   <li>{@link StoredEntityData#getEntity()}
   *   <li>{@link StoredEntityData#getId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; then return EntityAttributes Empty")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredEntityData.<init>(String, String, String)",
    "void StoredEntityData.<init>(String, String, String, EntityDefinition, Map)",
    "EntityDefinition StoredEntityData.getEntity()",
    "String StoredEntityData.getId()"
  })
  void testGettersAndSetters_thenReturnEntityAttributesEmpty() {
    // Arrange
    EntityDefinition entity =
        new EntityDefinition("/etc/config.properties", "Classifier Path", new HashMap<>());
    HashMap<String, Object> entityAttributes = new HashMap<>();

    // Act
    StoredEntityData actualStoredEntityData =
        new StoredEntityData("42", "42", "42", entity, entityAttributes);
    EntityDefinition actualEntity = actualStoredEntityData.getEntity();

    // Assert
    assertEquals("", actualStoredEntityData.getId());
    assertEquals("42", actualStoredEntityData.getArtifactId());
    assertEquals("42", actualStoredEntityData.getGroupId());
    assertEquals("42", actualStoredEntityData.getVersionId());
    Map<String, ?> entityAttributes2 = actualStoredEntityData.getEntityAttributes();
    assertTrue(entityAttributes2.isEmpty());
    assertSame(entityAttributes, entityAttributes2);
    assertSame(entity, actualEntity);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return EntityAttributes is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredEntityData#StoredEntityData(String, String, String)}
   *   <li>{@link StoredEntityData#getEntity()}
   *   <li>{@link StoredEntityData#getId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return EntityAttributes is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void StoredEntityData.<init>(String, String, String)",
    "void StoredEntityData.<init>(String, String, String, EntityDefinition, Map)",
    "EntityDefinition StoredEntityData.getEntity()",
    "String StoredEntityData.getId()"
  })
  void testGettersAndSetters_when42_thenReturnEntityAttributesIsNull() {
    // Arrange and Act
    StoredEntityData actualStoredEntityData = new StoredEntityData("42", "42", "42");
    EntityDefinition actualEntity = actualStoredEntityData.getEntity();

    // Assert
    assertEquals("", actualStoredEntityData.getId());
    assertEquals("42", actualStoredEntityData.getArtifactId());
    assertEquals("42", actualStoredEntityData.getGroupId());
    assertEquals("42", actualStoredEntityData.getVersionId());
    assertNull(actualStoredEntityData.getEntityAttributes());
    assertNull(actualEntity);
  }

  /**
   * Test {@link StoredEntityData#equals(Object)}, and {@link StoredEntityData#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredEntityData#equals(Object)}
   *   <li>{@link StoredEntityData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoredEntityData.equals(Object)", "int StoredEntityData.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    StoredEntityData storedEntityData = new StoredEntityData("42", "42", "42");
    StoredEntityData storedEntityData2 = new StoredEntityData("42", "42", "42");

    // Act and Assert
    assertEquals(storedEntityData, storedEntityData2);
    assertEquals(storedEntityData.hashCode(), storedEntityData2.hashCode());
  }

  /**
   * Test {@link StoredEntityData#equals(Object)}, and {@link StoredEntityData#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link StoredEntityData#equals(Object)}
   *   <li>{@link StoredEntityData#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoredEntityData.equals(Object)", "int StoredEntityData.hashCode()"})
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    StoredEntityData storedEntityData = new StoredEntityData("42", "42", "42");

    // Act and Assert
    assertEquals(storedEntityData, storedEntityData);
    int expectedHashCodeResult = storedEntityData.hashCode();
    assertEquals(expectedHashCodeResult, storedEntityData.hashCode());
  }

  /**
   * Test {@link StoredEntityData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredEntityData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoredEntityData.equals(Object)", "int StoredEntityData.hashCode()"})
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    StoredEntityData storedEntityData = new StoredEntityData("Group Id", "42", "42");

    // Act and Assert
    assertNotEquals(storedEntityData, new StoredEntityData("42", "42", "42"));
  }

  /**
   * Test {@link StoredEntityData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredEntityData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoredEntityData.equals(Object)", "int StoredEntityData.hashCode()"})
  void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(new StoredEntityData("42", "42", "42"), null);
  }

  /**
   * Test {@link StoredEntityData#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link StoredEntityData#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean StoredEntityData.equals(Object)", "int StoredEntityData.hashCode()"})
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(new StoredEntityData("42", "42", "42"), "Different type to StoredEntityData");
  }
}

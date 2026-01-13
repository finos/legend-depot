package org.finos.legend.depot.domain.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VersionAliasDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionAlias#getDescription()}
   *   <li>{@link VersionAlias#getName()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "java.lang.String VersionAlias.getDescription()",
    "java.lang.String VersionAlias.getName()"
  })
  void testGettersAndSetters() {
    // Arrange
    VersionAlias valueOfResult = VersionAlias.valueOf("LATEST");

    // Act
    valueOfResult.getDescription();

    // Assert
    assertEquals("latest", valueOfResult.getName());
  }
}

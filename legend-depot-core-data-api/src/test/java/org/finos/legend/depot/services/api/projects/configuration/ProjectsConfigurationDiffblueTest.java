package org.finos.legend.depot.services.api.projects.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectsConfigurationDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectsConfiguration#ProjectsConfiguration(String)}
   *   <li>{@link ProjectsConfiguration#getDefaultBranch()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectsConfiguration.<init>(String)",
    "String ProjectsConfiguration.getDefaultBranch()"
  })
  void testGettersAndSetters() {
    // Arrange, Act and Assert
    assertEquals(
        "janedoe/featurebranch",
        new ProjectsConfiguration("janedoe/featurebranch").getDefaultBranch());
  }
}

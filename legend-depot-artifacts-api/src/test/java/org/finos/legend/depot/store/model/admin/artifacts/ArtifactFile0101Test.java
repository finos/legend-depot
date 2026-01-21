package org.finos.legend.depot.store.model.admin.artifacts;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ArtifactFile0101Test {
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ArtifactFile#ArtifactFile()}
   *   <li>{@link ArtifactFile#setCheckSum(String)}
   *   <li>{@link ArtifactFile#setPath(String)}
   *   <li>{@link ArtifactFile#getCheckSum()}
   *   <li>{@link ArtifactFile#getId()}
   *   <li>{@link ArtifactFile#getPath()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Disabled("TODO: Complete this test")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ArtifactFile.<init>()",
    "void ArtifactFile.<init>(String, String)",
    "String ArtifactFile.getCheckSum()",
    "String ArtifactFile.getId()",
    "String ArtifactFile.getPath()",
    "ArtifactFile ArtifactFile.setCheckSum(String)",
    "ArtifactFile ArtifactFile.setPath(String)"
  })
  void testGettersAndSetters() {
    // TODO: Diffblue Cover was only able to create a partial test for this method:
    //   Diffblue AI was unable to find a test

    // Arrange and Act
    // TODO: Populate arranged inputs
    ArtifactFile actualArtifactFile = new ArtifactFile();
    String checkSum = "";
    ArtifactFile actualSetCheckSumResult = actualArtifactFile.setCheckSum(checkSum);
    String newPath = "";
    ArtifactFile actualSetPathResult = actualArtifactFile.setPath(newPath);
    String actualCheckSum = actualArtifactFile.getCheckSum();
    String actualId = actualArtifactFile.getId();
    String actualPath = actualArtifactFile.getPath();

    // Assert
    // TODO: Add assertions on result
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When empty string.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ArtifactFile#ArtifactFile(String, String)}
   *   <li>{@link ArtifactFile#setCheckSum(String)}
   *   <li>{@link ArtifactFile#setPath(String)}
   *   <li>{@link ArtifactFile#getCheckSum()}
   *   <li>{@link ArtifactFile#getId()}
   *   <li>{@link ArtifactFile#getPath()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when empty string")
  @Disabled("TODO: Complete this test")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ArtifactFile.<init>()",
    "void ArtifactFile.<init>(String, String)",
    "String ArtifactFile.getCheckSum()",
    "String ArtifactFile.getId()",
    "String ArtifactFile.getPath()",
    "ArtifactFile ArtifactFile.setCheckSum(String)",
    "ArtifactFile ArtifactFile.setPath(String)"
  })
  void testGettersAndSetters_whenEmptyString() {
    // TODO: Diffblue Cover was only able to create a partial test for this method:
    //   Diffblue AI was unable to find a test

    // Arrange
    // TODO: Populate arranged inputs
    String path = "";
    String checkSum = "";

    // Act
    ArtifactFile actualArtifactFile = new ArtifactFile(path, checkSum);
    String checkSum2 = "";
    ArtifactFile actualSetCheckSumResult = actualArtifactFile.setCheckSum(checkSum2);
    String newPath = "";
    ArtifactFile actualSetPathResult = actualArtifactFile.setPath(newPath);
    String actualCheckSum = actualArtifactFile.getCheckSum();
    String actualId = actualArtifactFile.getId();
    String actualPath = actualArtifactFile.getPath();

    // Assert
    // TODO: Add assertions on result
  }
}

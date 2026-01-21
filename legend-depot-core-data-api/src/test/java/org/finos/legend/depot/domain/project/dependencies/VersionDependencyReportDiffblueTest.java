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

package org.finos.legend.depot.domain.project.dependencies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VersionDependencyReportDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionDependencyReport#VersionDependencyReport()}
   *   <li>{@link VersionDependencyReport#setTransitiveDependencies(List)}
   *   <li>{@link VersionDependencyReport#setValid(boolean)}
   *   <li>{@link VersionDependencyReport#getTransitiveDependencies()}
   *   <li>{@link VersionDependencyReport#isValid()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void VersionDependencyReport.<init>()",
    "void VersionDependencyReport.<init>(List, boolean)",
    "List VersionDependencyReport.getTransitiveDependencies()",
    "boolean VersionDependencyReport.isValid()",
    "void VersionDependencyReport.setTransitiveDependencies(List)",
    "void VersionDependencyReport.setValid(boolean)"
  })
  void testGettersAndSetters()
  {
    // Arrange and Act
    VersionDependencyReport actualVersionDependencyReport = new VersionDependencyReport();
    ArrayList<ProjectVersion> transitiveDependencies = new ArrayList<>();
    actualVersionDependencyReport.setTransitiveDependencies(transitiveDependencies);
    actualVersionDependencyReport.setValid(true);
    List<ProjectVersion> actualTransitiveDependencies =
        actualVersionDependencyReport.getTransitiveDependencies();
    boolean actualIsValidResult = actualVersionDependencyReport.isValid();

    // Assert
    assertTrue(actualTransitiveDependencies.isEmpty());
    assertTrue(actualIsValidResult);
    assertSame(transitiveDependencies, actualTransitiveDependencies);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionDependencyReport#VersionDependencyReport(List, boolean)}
   *   <li>{@link VersionDependencyReport#setTransitiveDependencies(List)}
   *   <li>{@link VersionDependencyReport#setValid(boolean)}
   *   <li>{@link VersionDependencyReport#getTransitiveDependencies()}
   *   <li>{@link VersionDependencyReport#isValid()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void VersionDependencyReport.<init>()",
    "void VersionDependencyReport.<init>(List, boolean)",
    "List VersionDependencyReport.getTransitiveDependencies()",
    "boolean VersionDependencyReport.isValid()",
    "void VersionDependencyReport.setTransitiveDependencies(List)",
    "void VersionDependencyReport.setValid(boolean)"
  })
  void testGettersAndSetters_whenArrayList()
  {
    // Arrange and Act
    VersionDependencyReport actualVersionDependencyReport =
        new VersionDependencyReport(new ArrayList<>(), true);
    ArrayList<ProjectVersion> transitiveDependencies = new ArrayList<>();
    actualVersionDependencyReport.setTransitiveDependencies(transitiveDependencies);
    actualVersionDependencyReport.setValid(true);
    List<ProjectVersion> actualTransitiveDependencies =
        actualVersionDependencyReport.getTransitiveDependencies();
    boolean actualIsValidResult = actualVersionDependencyReport.isValid();

    // Assert
    assertTrue(actualTransitiveDependencies.isEmpty());
    assertTrue(actualIsValidResult);
    assertSame(transitiveDependencies, actualTransitiveDependencies);
  }

  /**
   * Test {@link VersionDependencyReport#equals(Object)}, and {@link
   * VersionDependencyReport#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionDependencyReport#equals(Object)}
   *   <li>{@link VersionDependencyReport#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean VersionDependencyReport.equals(Object)",
    "int VersionDependencyReport.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual()
  {
    // Arrange
    VersionDependencyReport versionDependencyReport = new VersionDependencyReport();
    VersionDependencyReport versionDependencyReport2 = new VersionDependencyReport();

    // Act and Assert
    assertEquals(versionDependencyReport, versionDependencyReport2);
    assertEquals(versionDependencyReport.hashCode(), versionDependencyReport2.hashCode());
  }

  /**
   * Test {@link VersionDependencyReport#equals(Object)}, and {@link
   * VersionDependencyReport#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionDependencyReport#equals(Object)}
   *   <li>{@link VersionDependencyReport#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean VersionDependencyReport.equals(Object)",
    "int VersionDependencyReport.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual()
  {
    // Arrange
    VersionDependencyReport versionDependencyReport = new VersionDependencyReport();

    // Act and Assert
    assertEquals(versionDependencyReport, versionDependencyReport);
    int expectedHashCodeResult = versionDependencyReport.hashCode();
    assertEquals(expectedHashCodeResult, versionDependencyReport.hashCode());
  }

  /**
   * Test {@link VersionDependencyReport#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionDependencyReport#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean VersionDependencyReport.equals(Object)",
    "int VersionDependencyReport.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new VersionDependencyReport(), 4);
  }

  /**
   * Test {@link VersionDependencyReport#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionDependencyReport#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean VersionDependencyReport.equals(Object)",
    "int VersionDependencyReport.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new VersionDependencyReport(), null);
  }

  /**
   * Test {@link VersionDependencyReport#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link VersionDependencyReport#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean VersionDependencyReport.equals(Object)",
    "int VersionDependencyReport.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual()
  {
    // Arrange, Act and Assert
    assertNotEquals(new VersionDependencyReport(), "Different type to VersionDependencyReport");
  }
}

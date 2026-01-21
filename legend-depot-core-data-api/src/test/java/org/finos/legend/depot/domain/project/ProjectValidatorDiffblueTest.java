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

package org.finos.legend.depot.domain.project;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectValidatorDiffblueTest 


{
  /**
   * Test {@link ProjectValidator#isValid(StoreProjectData)}.
   *
   * <p>Method under test: {@link ProjectValidator#isValid(StoreProjectData)}
   */
  @Test
  @DisplayName("Test isValid(StoreProjectData)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValid(StoreProjectData)"})
  void testIsValid()
  {
    // Arrange
    StoreProjectData projectData = new StoreProjectData("myproject", "42", "42");

    // Act and Assert
    assertFalse(ProjectValidator.isValid(projectData));
  }

  /**
   * Test {@link ProjectValidator#isValid(StoreProjectData)}.
   *
   * <p>Method under test: {@link ProjectValidator#isValid(StoreProjectData)}
   */
  @Test
  @DisplayName("Test isValid(StoreProjectData)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValid(StoreProjectData)"})
  void testIsValid2()
  {
    // Arrange
    StoreProjectData projectData = new StoreProjectData(null, "", "");

    // Act and Assert
    assertFalse(ProjectValidator.isValid(projectData));
  }

  /**
   * Test {@link ProjectValidator#isValid(StoreProjectData)}.
   *
   * <p>Method under test: {@link ProjectValidator#isValid(StoreProjectData)}
   */
  @Test
  @DisplayName("Test isValid(StoreProjectData)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValid(StoreProjectData)"})
  void testIsValid3()
  {
    // Arrange
    StoreProjectData projectData = new StoreProjectData("PROD-9", "42", "42");

    // Act and Assert
    assertFalse(ProjectValidator.isValid(projectData));
  }

  /**
   * Test {@link ProjectValidator#isValid(StoreProjectData)}.
   *
   * <p>Method under test: {@link ProjectValidator#isValid(StoreProjectData)}
   */
  @Test
  @DisplayName("Test isValid(StoreProjectData)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValid(StoreProjectData)"})
  void testIsValid4()
  {
    // Arrange
    StoreProjectData projectData = new StoreProjectData("PROD-9", null, "42");

    // Act and Assert
    assertFalse(ProjectValidator.isValid(projectData));
  }

  /**
   * Test {@link ProjectValidator#isValid(StoreProjectData)}.
   *
   * <p>Method under test: {@link ProjectValidator#isValid(StoreProjectData)}
   */
  @Test
  @DisplayName("Test isValid(StoreProjectData)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValid(StoreProjectData)"})
  void testIsValid5()
  {
    // Arrange
    StoreProjectData projectData = new StoreProjectData("PROD-9", "", "42");

    // Act and Assert
    assertFalse(ProjectValidator.isValid(projectData));
  }

  /**
   * Test {@link ProjectValidator#isValidProjectId(String)}.
   *
   * <ul>
   *   <li>When {@code myproject}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectValidator#isValidProjectId(String)}
   */
  @Test
  @DisplayName("Test isValidProjectId(String); when 'myproject'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValidProjectId(String)"})
  void testIsValidProjectId_whenMyproject_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(ProjectValidator.isValidProjectId("myproject"));
  }

  /**
   * Test {@link ProjectValidator#isValidProjectId(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectValidator#isValidProjectId(String)}
   */
  @Test
  @DisplayName("Test isValidProjectId(String); when 'null'; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValidProjectId(String)"})
  void testIsValidProjectId_whenNull_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(ProjectValidator.isValidProjectId(null));
  }

  /**
   * Test {@link ProjectValidator#isValidProjectId(String)}.
   *
   * <ul>
   *   <li>When {@code PROD-9}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectValidator#isValidProjectId(String)}
   */
  @Test
  @DisplayName("Test isValidProjectId(String); when 'PROD-9'; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean ProjectValidator.isValidProjectId(String)"})
  void testIsValidProjectId_whenProd9_thenReturnTrue()
  {
    // Arrange, Act and Assert
    assertTrue(ProjectValidator.isValidProjectId("PROD-9"));
  }
}

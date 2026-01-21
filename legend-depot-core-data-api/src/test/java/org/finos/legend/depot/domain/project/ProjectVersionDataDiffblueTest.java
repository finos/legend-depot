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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProjectVersionDataDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersionData#ProjectVersionData()}
   *   <li>{@link ProjectVersionData#setDependencies(List)}
   *   <li>{@link ProjectVersionData#setDeprecated(boolean)}
   *   <li>{@link ProjectVersionData#setExcluded(boolean)}
   *   <li>{@link ProjectVersionData#setExclusionReason(String)}
   *   <li>{@link ProjectVersionData#setManifestProperties(Map)}
   *   <li>{@link ProjectVersionData#setProperties(List)}
   *   <li>{@link ProjectVersionData#getDependencies()}
   *   <li>{@link ProjectVersionData#getExclusionReason()}
   *   <li>{@link ProjectVersionData#getManifestProperties()}
   *   <li>{@link ProjectVersionData#getProperties()}
   *   <li>{@link ProjectVersionData#isDeprecated()}
   *   <li>{@link ProjectVersionData#isExcluded()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersionData.<init>()",
    "void ProjectVersionData.<init>(List, List)",
    "void ProjectVersionData.<init>(List, List, boolean, boolean)",
    "List ProjectVersionData.getDependencies()",
    "String ProjectVersionData.getExclusionReason()",
    "Map ProjectVersionData.getManifestProperties()",
    "List ProjectVersionData.getProperties()",
    "boolean ProjectVersionData.isDeprecated()",
    "boolean ProjectVersionData.isExcluded()",
    "void ProjectVersionData.setDependencies(List)",
    "void ProjectVersionData.setDeprecated(boolean)",
    "void ProjectVersionData.setExcluded(boolean)",
    "void ProjectVersionData.setExclusionReason(String)",
    "void ProjectVersionData.setManifestProperties(Map)",
    "void ProjectVersionData.setProperties(List)"
  })
  void testGettersAndSetters()
  {
    // Arrange and Act
    ProjectVersionData actualProjectVersionData = new ProjectVersionData();
    ArrayList<ProjectVersion> dependencies = new ArrayList<>();
    actualProjectVersionData.setDependencies(dependencies);
    actualProjectVersionData.setDeprecated(true);
    actualProjectVersionData.setExcluded(true);
    actualProjectVersionData.setExclusionReason("Just cause");
    HashMap<String, String> manifestProperties = new HashMap<>();
    actualProjectVersionData.setManifestProperties(manifestProperties);
    ArrayList<Property> properties = new ArrayList<>();
    actualProjectVersionData.setProperties(properties);
    List<ProjectVersion> actualDependencies = actualProjectVersionData.getDependencies();
    String actualExclusionReason = actualProjectVersionData.getExclusionReason();
    Map<String, String> actualManifestProperties = actualProjectVersionData.getManifestProperties();
    List<Property> actualProperties = actualProjectVersionData.getProperties();
    boolean actualIsDeprecatedResult = actualProjectVersionData.isDeprecated();
    boolean actualIsExcludedResult = actualProjectVersionData.isExcluded();

    // Assert
    assertEquals("Just cause", actualExclusionReason);
    assertTrue(actualDependencies.isEmpty());
    assertTrue(actualProperties.isEmpty());
    assertTrue(actualManifestProperties.isEmpty());
    assertTrue(actualIsDeprecatedResult);
    assertTrue(actualIsExcludedResult);
    assertSame(dependencies, actualDependencies);
    assertSame(properties, actualProperties);
    assertSame(manifestProperties, actualManifestProperties);
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
   *   <li>{@link ProjectVersionData#ProjectVersionData(List, List)}
   *   <li>{@link ProjectVersionData#setDependencies(List)}
   *   <li>{@link ProjectVersionData#setDeprecated(boolean)}
   *   <li>{@link ProjectVersionData#setExcluded(boolean)}
   *   <li>{@link ProjectVersionData#setExclusionReason(String)}
   *   <li>{@link ProjectVersionData#setManifestProperties(Map)}
   *   <li>{@link ProjectVersionData#setProperties(List)}
   *   <li>{@link ProjectVersionData#getDependencies()}
   *   <li>{@link ProjectVersionData#getExclusionReason()}
   *   <li>{@link ProjectVersionData#getManifestProperties()}
   *   <li>{@link ProjectVersionData#getProperties()}
   *   <li>{@link ProjectVersionData#isDeprecated()}
   *   <li>{@link ProjectVersionData#isExcluded()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersionData.<init>()",
    "void ProjectVersionData.<init>(List, List)",
    "void ProjectVersionData.<init>(List, List, boolean, boolean)",
    "List ProjectVersionData.getDependencies()",
    "String ProjectVersionData.getExclusionReason()",
    "Map ProjectVersionData.getManifestProperties()",
    "List ProjectVersionData.getProperties()",
    "boolean ProjectVersionData.isDeprecated()",
    "boolean ProjectVersionData.isExcluded()",
    "void ProjectVersionData.setDependencies(List)",
    "void ProjectVersionData.setDeprecated(boolean)",
    "void ProjectVersionData.setExcluded(boolean)",
    "void ProjectVersionData.setExclusionReason(String)",
    "void ProjectVersionData.setManifestProperties(Map)",
    "void ProjectVersionData.setProperties(List)"
  })
  void testGettersAndSetters_whenArrayList()
  {
    // Arrange
    ArrayList<ProjectVersion> dependencies = new ArrayList<>();

    // Act
    ProjectVersionData actualProjectVersionData =
        new ProjectVersionData(dependencies, new ArrayList<>());
    ArrayList<ProjectVersion> dependencies2 = new ArrayList<>();
    actualProjectVersionData.setDependencies(dependencies2);
    actualProjectVersionData.setDeprecated(true);
    actualProjectVersionData.setExcluded(true);
    actualProjectVersionData.setExclusionReason("Just cause");
    HashMap<String, String> manifestProperties = new HashMap<>();
    actualProjectVersionData.setManifestProperties(manifestProperties);
    ArrayList<Property> properties = new ArrayList<>();
    actualProjectVersionData.setProperties(properties);
    List<ProjectVersion> actualDependencies = actualProjectVersionData.getDependencies();
    String actualExclusionReason = actualProjectVersionData.getExclusionReason();
    Map<String, String> actualManifestProperties = actualProjectVersionData.getManifestProperties();
    List<Property> actualProperties = actualProjectVersionData.getProperties();
    boolean actualIsDeprecatedResult = actualProjectVersionData.isDeprecated();
    boolean actualIsExcludedResult = actualProjectVersionData.isExcluded();

    // Assert
    assertEquals("Just cause", actualExclusionReason);
    assertTrue(actualDependencies.isEmpty());
    assertTrue(actualProperties.isEmpty());
    assertTrue(actualManifestProperties.isEmpty());
    assertTrue(actualIsDeprecatedResult);
    assertTrue(actualIsExcludedResult);
    assertSame(dependencies2, actualDependencies);
    assertSame(properties, actualProperties);
    assertSame(manifestProperties, actualManifestProperties);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code true}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ProjectVersionData#ProjectVersionData(List, List, boolean, boolean)}
   *   <li>{@link ProjectVersionData#setDependencies(List)}
   *   <li>{@link ProjectVersionData#setDeprecated(boolean)}
   *   <li>{@link ProjectVersionData#setExcluded(boolean)}
   *   <li>{@link ProjectVersionData#setExclusionReason(String)}
   *   <li>{@link ProjectVersionData#setManifestProperties(Map)}
   *   <li>{@link ProjectVersionData#setProperties(List)}
   *   <li>{@link ProjectVersionData#getDependencies()}
   *   <li>{@link ProjectVersionData#getExclusionReason()}
   *   <li>{@link ProjectVersionData#getManifestProperties()}
   *   <li>{@link ProjectVersionData#getProperties()}
   *   <li>{@link ProjectVersionData#isDeprecated()}
   *   <li>{@link ProjectVersionData#isExcluded()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ProjectVersionData.<init>()",
    "void ProjectVersionData.<init>(List, List)",
    "void ProjectVersionData.<init>(List, List, boolean, boolean)",
    "List ProjectVersionData.getDependencies()",
    "String ProjectVersionData.getExclusionReason()",
    "Map ProjectVersionData.getManifestProperties()",
    "List ProjectVersionData.getProperties()",
    "boolean ProjectVersionData.isDeprecated()",
    "boolean ProjectVersionData.isExcluded()",
    "void ProjectVersionData.setDependencies(List)",
    "void ProjectVersionData.setDeprecated(boolean)",
    "void ProjectVersionData.setExcluded(boolean)",
    "void ProjectVersionData.setExclusionReason(String)",
    "void ProjectVersionData.setManifestProperties(Map)",
    "void ProjectVersionData.setProperties(List)"
  })
  void testGettersAndSetters_whenTrue()
  {
    // Arrange
    ArrayList<ProjectVersion> dependencies = new ArrayList<>();

    // Act
    ProjectVersionData actualProjectVersionData =
        new ProjectVersionData(dependencies, new ArrayList<>(), true, true);
    ArrayList<ProjectVersion> dependencies2 = new ArrayList<>();
    actualProjectVersionData.setDependencies(dependencies2);
    actualProjectVersionData.setDeprecated(true);
    actualProjectVersionData.setExcluded(true);
    actualProjectVersionData.setExclusionReason("Just cause");
    HashMap<String, String> manifestProperties = new HashMap<>();
    actualProjectVersionData.setManifestProperties(manifestProperties);
    ArrayList<Property> properties = new ArrayList<>();
    actualProjectVersionData.setProperties(properties);
    List<ProjectVersion> actualDependencies = actualProjectVersionData.getDependencies();
    String actualExclusionReason = actualProjectVersionData.getExclusionReason();
    Map<String, String> actualManifestProperties = actualProjectVersionData.getManifestProperties();
    List<Property> actualProperties = actualProjectVersionData.getProperties();
    boolean actualIsDeprecatedResult = actualProjectVersionData.isDeprecated();
    boolean actualIsExcludedResult = actualProjectVersionData.isExcluded();

    // Assert
    assertEquals("Just cause", actualExclusionReason);
    assertTrue(actualDependencies.isEmpty());
    assertTrue(actualProperties.isEmpty());
    assertTrue(actualManifestProperties.isEmpty());
    assertTrue(actualIsDeprecatedResult);
    assertTrue(actualIsExcludedResult);
    assertSame(dependencies2, actualDependencies);
    assertSame(properties, actualProperties);
    assertSame(manifestProperties, actualManifestProperties);
  }

  /**
   * Test {@link ProjectVersionData#addDependencies(List)}.
   *
   * <ul>
   *   <li>Then {@link ProjectVersionData#ProjectVersionData()} Dependencies is {@link
   *       ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addDependencies(List)}
   */
  @Test
  @DisplayName("Test addDependencies(List); then ProjectVersionData() Dependencies is ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addDependencies(List)"})
  void testAddDependencies_thenProjectVersionDataDependenciesIsArrayList()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();

    ArrayList<ProjectVersion> dependencies = new ArrayList<>();
    ProjectVersion projectVersion = new ProjectVersion("42", "42", "42");
    dependencies.add(projectVersion);
    ProjectVersion projectVersion2 = new ProjectVersion("42", "42", "42");
    dependencies.add(projectVersion2);

    // Act
    projectVersionData.addDependencies(dependencies);

    // Assert
    assertEquals(dependencies, projectVersionData.getDependencies());
  }

  /**
   * Test {@link ProjectVersionData#addDependencies(List)}.
   *
   * <ul>
   *   <li>Then {@link ProjectVersionData#ProjectVersionData()} Dependencies size is one.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addDependencies(List)}
   */
  @Test
  @DisplayName("Test addDependencies(List); then ProjectVersionData() Dependencies size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addDependencies(List)"})
  void testAddDependencies_thenProjectVersionDataDependenciesSizeIsOne()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();

    ArrayList<ProjectVersion> dependencies = new ArrayList<>();
    ProjectVersion projectVersion = new ProjectVersion("42", "42", "42");
    dependencies.add(projectVersion);

    // Act
    projectVersionData.addDependencies(dependencies);

    // Assert
    List<ProjectVersion> dependencies2 = projectVersionData.getDependencies();
    assertEquals(1, dependencies2.size());
    assertSame(projectVersion, dependencies2.get(0));
  }

  /**
   * Test {@link ProjectVersionData#addDependencies(List)}.
   *
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.
   *   <li>Then {@link ProjectVersionData#ProjectVersionData()} Dependencies Empty.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addDependencies(List)}
   */
  @Test
  @DisplayName(
      "Test addDependencies(List); when ArrayList(); then ProjectVersionData() Dependencies Empty")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addDependencies(List)"})
  void testAddDependencies_whenArrayList_thenProjectVersionDataDependenciesEmpty()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();

    // Act
    projectVersionData.addDependencies(new ArrayList<>());

    // Assert that nothing has changed
    assertTrue(projectVersionData.getDependencies().isEmpty());
  }

  /**
   * Test {@link ProjectVersionData#addDependency(ProjectVersion)}.
   *
   * <p>Method under test: {@link ProjectVersionData#addDependency(ProjectVersion)}
   */
  @Test
  @DisplayName("Test addDependency(ProjectVersion)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addDependency(ProjectVersion)"})
  void testAddDependency()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");
    projectVersionData.addDependency(dependency);
    ProjectVersion dependency2 = new ProjectVersion("42", "42", "42");

    // Act
    projectVersionData.addDependency(dependency2);

    // Assert that nothing has changed
    List<ProjectVersion> dependencies = projectVersionData.getDependencies();
    assertEquals(1, dependencies.size());
    assertSame(dependency, dependencies.get(0));
  }

  /**
   * Test {@link ProjectVersionData#addDependency(ProjectVersion)}.
   *
   * <ul>
   *   <li>Given {@link ProjectVersionData#ProjectVersionData()}.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addDependency(ProjectVersion)}
   */
  @Test
  @DisplayName("Test addDependency(ProjectVersion); given ProjectVersionData()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addDependency(ProjectVersion)"})
  void testAddDependency_givenProjectVersionData()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();
    ProjectVersion dependency = new ProjectVersion("42", "42", "42");

    // Act
    projectVersionData.addDependency(dependency);

    // Assert
    List<ProjectVersion> dependencies = projectVersionData.getDependencies();
    assertEquals(1, dependencies.size());
    assertSame(dependency, dependencies.get(0));
  }

  /**
   * Test {@link ProjectVersionData#addProperties(List)}.
   *
   * <ul>
   *   <li>Then {@link ProjectVersionData#ProjectVersionData()} Properties size is one.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addProperties(List)}
   */
  @Test
  @DisplayName("Test addProperties(List); then ProjectVersionData() Properties size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addProperties(List)"})
  void testAddProperties_thenProjectVersionDataPropertiesSizeIsOne()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();

    ArrayList<Property> propertyList = new ArrayList<>();
    Property property = new Property("Property Name", "42");
    propertyList.add(property);

    // Act
    projectVersionData.addProperties(propertyList);

    // Assert
    List<Property> properties = projectVersionData.getProperties();
    assertEquals(1, properties.size());
    assertSame(property, properties.get(0));
  }

  /**
   * Test {@link ProjectVersionData#addProperties(List)}.
   *
   * <ul>
   *   <li>Then {@link ProjectVersionData#ProjectVersionData()} Properties size is one.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addProperties(List)}
   */
  @Test
  @DisplayName("Test addProperties(List); then ProjectVersionData() Properties size is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addProperties(List)"})
  void testAddProperties_thenProjectVersionDataPropertiesSizeIsOne2()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();

    ArrayList<Property> propertyList = new ArrayList<>();
    Property property = new Property("Property Name", "42");
    propertyList.add(property);
    propertyList.add(new Property("Property Name", "42"));

    // Act
    projectVersionData.addProperties(propertyList);

    // Assert
    List<Property> properties = projectVersionData.getProperties();
    assertEquals(1, properties.size());
    assertSame(property, properties.get(0));
  }

  /**
   * Test {@link ProjectVersionData#addProperties(List)}.
   *
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.
   *   <li>Then {@link ProjectVersionData#ProjectVersionData()} Properties Empty.
   * </ul>
   *
   * <p>Method under test: {@link ProjectVersionData#addProperties(List)}
   */
  @Test
  @DisplayName(
      "Test addProperties(List); when ArrayList(); then ProjectVersionData() Properties Empty")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void ProjectVersionData.addProperties(List)"})
  void testAddProperties_whenArrayList_thenProjectVersionDataPropertiesEmpty()
  {
    // Arrange
    ProjectVersionData projectVersionData = new ProjectVersionData();

    // Act
    projectVersionData.addProperties(new ArrayList<>());

    // Assert that nothing has changed
    assertTrue(projectVersionData.getProperties().isEmpty());
  }
}

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProjectVersionDataClaudeTest 

{

    @Test
    @DisplayName("Test default constructor initializes empty lists")
    void testDefaultConstructor()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertNotNull(data.getDependencies());
        assertTrue(data.getDependencies().isEmpty());
        assertNotNull(data.getProperties());
        assertTrue(data.getProperties().isEmpty());
        assertNull(data.getManifestProperties());
        assertFalse(data.isDeprecated());
        assertFalse(data.isExcluded());
        assertNull(data.getExclusionReason());
    }

    @Test
    @DisplayName("Test constructor with dependencies and properties")
    void testConstructorWithDependenciesAndProperties()
  {
        // Arrange
        List<ProjectVersion> dependencies = new ArrayList<>();
        dependencies.add(new ProjectVersion("group1", "artifact1", "1.0.0"));
        dependencies.add(new ProjectVersion("group2", "artifact2", "2.0.0"));

        List<Property> properties = new ArrayList<>();
        properties.add(new Property("key1", "value1"));
        properties.add(new Property("key2", "value2"));

        // Act
        ProjectVersionData data = new ProjectVersionData(dependencies, properties);

        // Assert
        assertEquals(2, data.getDependencies().size());
        assertEquals(2, data.getProperties().size());
        assertSame(dependencies, data.getDependencies());
        assertSame(properties, data.getProperties());
        assertFalse(data.isDeprecated());
        assertFalse(data.isExcluded());
    }

    @Test
    @DisplayName("Test constructor with null dependencies and properties")
    void testConstructorWithNulls()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData(null, null);

        // Assert
        assertNull(data.getDependencies());
        assertNull(data.getProperties());
        assertFalse(data.isDeprecated());
        assertFalse(data.isExcluded());
    }

    @Test
    @DisplayName("Test constructor with all parameters")
    void testConstructorWithAllParameters()
  {
        // Arrange
        List<ProjectVersion> dependencies = new ArrayList<>();
        dependencies.add(new ProjectVersion("group1", "artifact1", "1.0.0"));

        List<Property> properties = new ArrayList<>();
        properties.add(new Property("key1", "value1"));

        // Act
        ProjectVersionData data = new ProjectVersionData(dependencies, properties, true, true);

        // Assert
        assertSame(dependencies, data.getDependencies());
        assertSame(properties, data.getProperties());
        assertTrue(data.isDeprecated());
        assertTrue(data.isExcluded());
    }

    @Test
    @DisplayName("Test constructor with false deprecated and excluded flags")
    void testConstructorWithFalseFlags()
  {
        // Arrange
        List<ProjectVersion> dependencies = new ArrayList<>();
        List<Property> properties = new ArrayList<>();

        // Act
        ProjectVersionData data = new ProjectVersionData(dependencies, properties, false, false);

        // Assert
        assertFalse(data.isDeprecated());
        assertFalse(data.isExcluded());
    }

    @Test
    @DisplayName("Test setDependencies replaces existing dependencies")
    void testSetDependencies()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        List<ProjectVersion> initialDeps = new ArrayList<>();
        initialDeps.add(new ProjectVersion("group1", "artifact1", "1.0.0"));
        data.addDependencies(initialDeps);

        List<ProjectVersion> newDeps = new ArrayList<>();
        newDeps.add(new ProjectVersion("group2", "artifact2", "2.0.0"));

        // Act
        data.setDependencies(newDeps);

        // Assert
        assertEquals(1, data.getDependencies().size());
        assertSame(newDeps, data.getDependencies());
        assertEquals("group2", data.getDependencies().get(0).getGroupId());
    }

    @Test
    @DisplayName("Test addDependencies adds to existing list")
    void testAddDependencies()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        List<ProjectVersion> firstBatch = new ArrayList<>();
        firstBatch.add(new ProjectVersion("group1", "artifact1", "1.0.0"));
        data.addDependencies(firstBatch);

        List<ProjectVersion> secondBatch = new ArrayList<>();
        secondBatch.add(new ProjectVersion("group2", "artifact2", "2.0.0"));
        secondBatch.add(new ProjectVersion("group3", "artifact3", "3.0.0"));

        // Act
        data.addDependencies(secondBatch);

        // Assert
        assertEquals(3, data.getDependencies().size());
    }

    @Test
    @DisplayName("Test addDependency adds single dependency")
    void testAddDependency()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        ProjectVersion dep = new ProjectVersion("group1", "artifact1", "1.0.0");

        // Act
        data.addDependency(dep);

        // Assert
        assertEquals(1, data.getDependencies().size());
        assertSame(dep, data.getDependencies().get(0));
    }

    @Test
    @DisplayName("Test addDependency does not add duplicate")
    void testAddDependencyNoDuplicate()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        ProjectVersion dep1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion dep2 = new ProjectVersion("group1", "artifact1", "1.0.0");

        // Act
        data.addDependency(dep1);
        data.addDependency(dep2);

        // Assert
        assertEquals(1, data.getDependencies().size());
    }

    @Test
    @DisplayName("Test addDependency adds different dependencies")
    void testAddDependencyDifferent()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        ProjectVersion dep1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion dep2 = new ProjectVersion("group1", "artifact1", "2.0.0");
        ProjectVersion dep3 = new ProjectVersion("group2", "artifact2", "1.0.0");

        // Act
        data.addDependency(dep1);
        data.addDependency(dep2);
        data.addDependency(dep3);

        // Assert
        assertEquals(3, data.getDependencies().size());
    }

    @Test
    @DisplayName("Test setProperties replaces existing properties")
    void testSetProperties()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        List<Property> initialProps = new ArrayList<>();
        initialProps.add(new Property("key1", "value1"));
        data.addProperties(initialProps);

        List<Property> newProps = new ArrayList<>();
        newProps.add(new Property("key2", "value2"));

        // Act
        data.setProperties(newProps);

        // Assert
        assertEquals(1, data.getProperties().size());
        assertSame(newProps, data.getProperties());
        assertEquals("key2", data.getProperties().get(0).getPropertyName());
    }

    @Test
    @DisplayName("Test addProperties adds to existing list")
    void testAddProperties()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        List<Property> firstBatch = new ArrayList<>();
        firstBatch.add(new Property("key1", "value1"));
        data.addProperties(firstBatch);

        List<Property> secondBatch = new ArrayList<>();
        secondBatch.add(new Property("key2", "value2"));
        secondBatch.add(new Property("key3", "value3"));

        // Act
        data.addProperties(secondBatch);

        // Assert
        assertEquals(3, data.getProperties().size());
    }

    @Test
    @DisplayName("Test addProperties filters duplicate properties")
    void testAddPropertiesFiltersDuplicates()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        Property prop1 = new Property("key1", "value1");
        data.addProperties(Arrays.asList(prop1));

        List<Property> newProps = new ArrayList<>();
        newProps.add(new Property("key1", "value1"));
        newProps.add(new Property("key2", "value2"));

        // Act
        data.addProperties(newProps);

        // Assert
        assertEquals(2, data.getProperties().size());
    }

    @Test
    @DisplayName("Test addProperties with multiple duplicates in list")
    void testAddPropertiesWithMultipleDuplicates()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        Property prop1 = new Property("key1", "value1");
        data.addProperties(Arrays.asList(prop1));

        List<Property> newProps = new ArrayList<>();
        newProps.add(new Property("key1", "value1"));
        newProps.add(new Property("key2", "value2"));
        newProps.add(new Property("key1", "value1"));

        // Act
        data.addProperties(newProps);

        // Assert
        assertEquals(2, data.getProperties().size());
    }

    @Test
    @DisplayName("Test setManifestProperties sets map")
    void testSetManifestProperties()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        Map<String, String> manifest = new HashMap<>();
        manifest.put("Implementation-Version", "1.0.0");
        manifest.put("Build-Time", "2021-01-01");

        // Act
        data.setManifestProperties(manifest);

        // Assert
        assertSame(manifest, data.getManifestProperties());
        assertEquals(2, data.getManifestProperties().size());
        assertEquals("1.0.0", data.getManifestProperties().get("Implementation-Version"));
    }

    @Test
    @DisplayName("Test setManifestProperties with null")
    void testSetManifestPropertiesNull()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        Map<String, String> manifest = new HashMap<>();
        manifest.put("key", "value");
        data.setManifestProperties(manifest);

        // Act
        data.setManifestProperties(null);

        // Assert
        assertNull(data.getManifestProperties());
    }

    @Test
    @DisplayName("Test setManifestProperties with empty map")
    void testSetManifestPropertiesEmpty()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        Map<String, String> manifest = new HashMap<>();

        // Act
        data.setManifestProperties(manifest);

        // Assert
        assertNotNull(data.getManifestProperties());
        assertTrue(data.getManifestProperties().isEmpty());
    }

    @Test
    @DisplayName("Test setDeprecated to true")
    void testSetDeprecatedTrue()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();

        // Act
        data.setDeprecated(true);

        // Assert
        assertTrue(data.isDeprecated());
    }

    @Test
    @DisplayName("Test setDeprecated to false")
    void testSetDeprecatedFalse()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        data.setDeprecated(true);

        // Act
        data.setDeprecated(false);

        // Assert
        assertFalse(data.isDeprecated());
    }

    @Test
    @DisplayName("Test setExcluded to true")
    void testSetExcludedTrue()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();

        // Act
        data.setExcluded(true);

        // Assert
        assertTrue(data.isExcluded());
    }

    @Test
    @DisplayName("Test setExcluded to false")
    void testSetExcludedFalse()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        data.setExcluded(true);

        // Act
        data.setExcluded(false);

        // Assert
        assertFalse(data.isExcluded());
    }

    @Test
    @DisplayName("Test setExclusionReason sets reason")
    void testSetExclusionReason()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();

        // Act
        data.setExclusionReason("Security vulnerability detected");

        // Assert
        assertEquals("Security vulnerability detected", data.getExclusionReason());
    }

    @Test
    @DisplayName("Test setExclusionReason with null")
    void testSetExclusionReasonNull()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        data.setExclusionReason("Some reason");

        // Act
        data.setExclusionReason(null);

        // Assert
        assertNull(data.getExclusionReason());
    }

    @Test
    @DisplayName("Test setExclusionReason with empty string")
    void testSetExclusionReasonEmpty()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();

        // Act
        data.setExclusionReason("");

        // Assert
        assertEquals("", data.getExclusionReason());
    }

    @Test
    @DisplayName("Test getExclusionReason returns null by default")
    void testGetExclusionReasonDefault()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertNull(data.getExclusionReason());
    }

    @Test
    @DisplayName("Test isDeprecated returns false by default")
    void testIsDeprecatedDefault()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertFalse(data.isDeprecated());
    }

    @Test
    @DisplayName("Test isExcluded returns false by default")
    void testIsExcludedDefault()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertFalse(data.isExcluded());
    }

    @Test
    @DisplayName("Test getDependencies returns empty list by default")
    void testGetDependenciesDefault()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertNotNull(data.getDependencies());
        assertTrue(data.getDependencies().isEmpty());
    }

    @Test
    @DisplayName("Test getProperties returns empty list by default")
    void testGetPropertiesDefault()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertNotNull(data.getProperties());
        assertTrue(data.getProperties().isEmpty());
    }

    @Test
    @DisplayName("Test getManifestProperties returns null by default")
    void testGetManifestPropertiesDefault()
  {
        // Arrange and Act
        ProjectVersionData data = new ProjectVersionData();

        // Assert
        assertNull(data.getManifestProperties());
    }

    @Test
    @DisplayName("Test full workflow with all operations")
    void testFullWorkflow()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();

        // Act - add dependencies
        data.addDependency(new ProjectVersion("group1", "artifact1", "1.0.0"));
        data.addDependency(new ProjectVersion("group2", "artifact2", "2.0.0"));

        // Act - add properties
        List<Property> props = new ArrayList<>();
        props.add(new Property("key1", "value1"));
        data.addProperties(props);

        // Act - set manifest
        Map<String, String> manifest = new HashMap<>();
        manifest.put("Build-Number", "123");
        data.setManifestProperties(manifest);

        // Act - set flags
        data.setDeprecated(true);
        data.setExcluded(true);
        data.setExclusionReason("End of life");

        // Assert
        assertEquals(2, data.getDependencies().size());
        assertEquals(1, data.getProperties().size());
        assertEquals(1, data.getManifestProperties().size());
        assertTrue(data.isDeprecated());
        assertTrue(data.isExcluded());
        assertEquals("End of life", data.getExclusionReason());
    }

    @Test
    @DisplayName("Test chaining multiple addDependency calls")
    void testChainingAddDependency()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();

        // Act
        data.addDependency(new ProjectVersion("g1", "a1", "1.0"));
        data.addDependency(new ProjectVersion("g2", "a2", "2.0"));
        data.addDependency(new ProjectVersion("g3", "a3", "3.0"));

        // Assert
        assertEquals(3, data.getDependencies().size());
    }

    @Test
    @DisplayName("Test addProperties with empty list")
    void testAddPropertiesEmptyList()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        data.addProperties(Arrays.asList(new Property("key1", "value1")));

        // Act
        data.addProperties(new ArrayList<>());

        // Assert
        assertEquals(1, data.getProperties().size());
    }

    @Test
    @DisplayName("Test addDependencies with empty list")
    void testAddDependenciesEmptyList()
  {
        // Arrange
        ProjectVersionData data = new ProjectVersionData();
        data.addDependency(new ProjectVersion("g1", "a1", "1.0"));

        // Act
        data.addDependencies(new ArrayList<>());

        // Assert
        assertEquals(1, data.getDependencies().size());
    }
}

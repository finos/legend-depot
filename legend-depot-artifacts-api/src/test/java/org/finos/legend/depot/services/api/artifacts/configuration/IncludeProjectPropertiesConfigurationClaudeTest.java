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

package org.finos.legend.depot.services.api.artifacts.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class IncludeProjectPropertiesConfigurationClaudeTest


{
    // Constructor tests

    @Test
    @DisplayName("Constructor with null properties and null manifestProperties")
    void testConstructorWithBothNull()
  {
        // Arrange & Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(null, null);

        // Assert
        assertNull(config.getProperties());
        assertNull(config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with non-null properties and null manifestProperties")
    void testConstructorWithPropertiesOnly()
  {
        // Arrange
        List<String> properties = Arrays.asList("property1", "property2");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, null);

        // Assert
        assertEquals(properties, config.getProperties());
        assertNull(config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with null properties and non-null manifestProperties")
    void testConstructorWithManifestPropertiesOnly()
  {
        // Arrange
        List<String> manifestProperties = Arrays.asList("manifest1", "manifest2");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(null, manifestProperties);

        // Assert
        assertNull(config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with both properties and manifestProperties")
    void testConstructorWithBothParameters()
  {
        // Arrange
        List<String> properties = Arrays.asList("property1", "property2", "property3");
        List<String> manifestProperties = Arrays.asList("manifest1", "manifest2");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with empty lists for both parameters")
    void testConstructorWithEmptyLists()
  {
        // Arrange
        List<String> properties = Collections.emptyList();
        List<String> manifestProperties = Collections.emptyList();

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with single element lists")
    void testConstructorWithSingleElementLists()
  {
        // Arrange
        List<String> properties = Collections.singletonList("singleProperty");
        List<String> manifestProperties = Collections.singletonList("singleManifest");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with large lists")
    void testConstructorWithLargeLists()
  {
        // Arrange
        List<String> properties = new ArrayList<>();
        List<String> manifestProperties = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            properties.add("property" + i);
            manifestProperties.add("manifest" + i);
        }

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
        assertEquals(100, config.getProperties().size());
        assertEquals(100, config.getManifestProperties().size());
    }

    @Test
    @DisplayName("Constructor with lists containing duplicate values")
    void testConstructorWithDuplicateValues()
  {
        // Arrange
        List<String> properties = Arrays.asList("prop", "prop", "prop");
        List<String> manifestProperties = Arrays.asList("manifest", "manifest");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
        assertEquals(3, config.getProperties().size());
        assertEquals(2, config.getManifestProperties().size());
    }

    @Test
    @DisplayName("Constructor with lists containing empty strings")
    void testConstructorWithEmptyStrings()
  {
        // Arrange
        List<String> properties = Arrays.asList("", "property1", "");
        List<String> manifestProperties = Arrays.asList("", "");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with lists containing null elements")
    void testConstructorWithNullElements()
  {
        // Arrange
        List<String> properties = Arrays.asList("property1", null, "property3");
        List<String> manifestProperties = Arrays.asList(null, "manifest1");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    @Test
    @DisplayName("Constructor with lists containing special characters")
    void testConstructorWithSpecialCharacters()
  {
        // Arrange
        List<String> properties = Arrays.asList("property-1", "property_2", "property.3", "property:4");
        List<String> manifestProperties = Arrays.asList("manifest@1", "manifest#2");

        // Act
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Assert
        assertEquals(properties, config.getProperties());
        assertEquals(manifestProperties, config.getManifestProperties());
    }

    // Getter method tests

    @Test
    @DisplayName("getProperties should return the same reference as passed to constructor")
    void testGetPropertiesReturnsSameReference()
  {
        // Arrange
        List<String> properties = Arrays.asList("property1", "property2");
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, null);

        // Act
        List<String> result = config.getProperties();

        // Assert
        assertSame(properties, result);
    }

    @Test
    @DisplayName("getManifestProperties should return the same reference as passed to constructor")
    void testGetManifestPropertiesReturnsSameReference()
  {
        // Arrange
        List<String> manifestProperties = Arrays.asList("manifest1", "manifest2");
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(null, manifestProperties);

        // Act
        List<String> result = config.getManifestProperties();

        // Assert
        assertSame(manifestProperties, result);
    }

    @Test
    @DisplayName("getProperties should return null when constructor parameter is null")
    void testGetPropertiesReturnsNull()
  {
        // Arrange
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(null, Arrays.asList("manifest1"));

        // Act
        List<String> result = config.getProperties();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getManifestProperties should return null when constructor parameter is null")
    void testGetManifestPropertiesReturnsNull()
  {
        // Arrange
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(Arrays.asList("property1"), null);

        // Act
        List<String> result = config.getManifestProperties();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Getters should consistently return same values")
    void testGettersConsistency()
  {
        // Arrange
        List<String> properties = Arrays.asList("property1", "property2");
        List<String> manifestProperties = Arrays.asList("manifest1", "manifest2");
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);

        // Act - call getters multiple times
        List<String> properties1 = config.getProperties();
        List<String> properties2 = config.getProperties();
        List<String> manifestProperties1 = config.getManifestProperties();
        List<String> manifestProperties2 = config.getManifestProperties();

        // Assert - should return same references
        assertSame(properties1, properties2);
        assertSame(manifestProperties1, manifestProperties2);
        assertEquals(properties, properties1);
        assertEquals(manifestProperties, manifestProperties1);
    }

    @Test
    @DisplayName("getProperties should return empty list when constructor parameter is empty")
    void testGetPropertiesReturnsEmptyList()
  {
        // Arrange
        List<String> properties = Collections.emptyList();
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, null);

        // Act
        List<String> result = config.getProperties();

        // Assert
        assertEquals(0, result.size());
        assertSame(properties, result);
    }

    @Test
    @DisplayName("getManifestProperties should return empty list when constructor parameter is empty")
    void testGetManifestPropertiesReturnsEmptyList()
  {
        // Arrange
        List<String> manifestProperties = Collections.emptyList();
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(null, manifestProperties);

        // Act
        List<String> result = config.getManifestProperties();

        // Assert
        assertEquals(0, result.size());
        assertSame(manifestProperties, result);
    }

    @Test
    @DisplayName("getProperties should preserve list order")
    void testGetPropertiesPreservesOrder()
  {
        // Arrange
        List<String> properties = Arrays.asList("z", "a", "m", "b");
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(properties, null);

        // Act
        List<String> result = config.getProperties();

        // Assert
        assertEquals("z", result.get(0));
        assertEquals("a", result.get(1));
        assertEquals("m", result.get(2));
        assertEquals("b", result.get(3));
    }

    @Test
    @DisplayName("getManifestProperties should preserve list order")
    void testGetManifestPropertiesPreservesOrder()
  {
        // Arrange
        List<String> manifestProperties = Arrays.asList("third", "first", "second");
        IncludeProjectPropertiesConfiguration config =
                new IncludeProjectPropertiesConfiguration(null, manifestProperties);

        // Act
        List<String> result = config.getManifestProperties();

        // Assert
        assertEquals("third", result.get(0));
        assertEquals("first", result.get(1));
        assertEquals("second", result.get(2));
    }
}

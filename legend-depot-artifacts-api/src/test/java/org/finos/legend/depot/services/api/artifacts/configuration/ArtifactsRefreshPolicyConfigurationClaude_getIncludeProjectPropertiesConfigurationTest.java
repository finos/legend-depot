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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ArtifactsRefreshPolicyConfigurationClaude_getIncludeProjectPropertiesConfigurationTest
{
    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return null when constructor receives null")
    void testGetIncludeProjectPropertiesConfigurationWithNull()
    {
        // Arrange
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, null);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return the configuration set in constructor")
    void testGetIncludeProjectPropertiesConfigurationWithValidConfiguration()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(
                        Arrays.asList("prop1", "prop2"),
                        Arrays.asList("manifest1")
                );
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return configuration with empty lists")
    void testGetIncludeProjectPropertiesConfigurationWithEmptyLists()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(
                        Collections.emptyList(),
                        Collections.emptyList()
                );
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
        assertNotNull(result.getProperties());
        assertNotNull(result.getManifestProperties());
        assertEquals(0, result.getProperties().size());
        assertEquals(0, result.getManifestProperties().size());
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return configuration with null properties list")
    void testGetIncludeProjectPropertiesConfigurationWithNullPropertiesList()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(null, Arrays.asList("manifest1"));
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
        assertNull(result.getProperties());
        assertNotNull(result.getManifestProperties());
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return configuration with null manifestProperties list")
    void testGetIncludeProjectPropertiesConfigurationWithNullManifestPropertiesList()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(Arrays.asList("prop1"), null);
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
        assertNotNull(result.getProperties());
        assertNull(result.getManifestProperties());
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return configuration with both lists null")
    void testGetIncludeProjectPropertiesConfigurationWithBothListsNull()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(null, null);
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
        assertNull(result.getProperties());
        assertNull(result.getManifestProperties());
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return same instance on multiple calls")
    void testGetIncludeProjectPropertiesConfigurationConsistency()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(
                        Arrays.asList("prop1", "prop2", "prop3"),
                        Arrays.asList("manifest1", "manifest2")
                );
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration firstCall = config.getIncludeProjectPropertiesConfiguration();
        IncludeProjectPropertiesConfiguration secondCall = config.getIncludeProjectPropertiesConfiguration();
        IncludeProjectPropertiesConfiguration thirdCall = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertSame(expectedConfig, firstCall);
        assertSame(expectedConfig, secondCall);
        assertSame(expectedConfig, thirdCall);
        assertSame(firstCall, secondCall);
        assertSame(secondCall, thirdCall);
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return configuration with multiple properties")
    void testGetIncludeProjectPropertiesConfigurationWithMultipleProperties()
    {
        // Arrange
        List<String> properties = Arrays.asList("property1", "property2", "property3", "property4");
        List<String> manifestProperties = Arrays.asList("manifestProp1", "manifestProp2", "manifestProp3");
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(properties, manifestProperties);
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(5000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
        assertEquals(4, result.getProperties().size());
        assertEquals(3, result.getManifestProperties().size());
        assertEquals("property1", result.getProperties().get(0));
        assertEquals("manifestProp1", result.getManifestProperties().get(0));
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should work correctly regardless of versionsUpdateIntervalInMillis")
    void testGetIncludeProjectPropertiesConfigurationWithVariousIntervals()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(
                        Arrays.asList("prop1"),
                        Arrays.asList("manifest1")
                );

        // Test with null interval
        ArtifactsRefreshPolicyConfiguration configWithNullInterval =
                new ArtifactsRefreshPolicyConfiguration(null, expectedConfig);

        // Test with zero interval
        ArtifactsRefreshPolicyConfiguration configWithZeroInterval =
                new ArtifactsRefreshPolicyConfiguration(0L, expectedConfig);

        // Test with large interval
        ArtifactsRefreshPolicyConfiguration configWithLargeInterval =
                new ArtifactsRefreshPolicyConfiguration(Long.MAX_VALUE, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration resultNull = configWithNullInterval.getIncludeProjectPropertiesConfiguration();
        IncludeProjectPropertiesConfiguration resultZero = configWithZeroInterval.getIncludeProjectPropertiesConfiguration();
        IncludeProjectPropertiesConfiguration resultLarge = configWithLargeInterval.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertSame(expectedConfig, resultNull);
        assertSame(expectedConfig, resultZero);
        assertSame(expectedConfig, resultLarge);
    }

    @Test
    @DisplayName("getIncludeProjectPropertiesConfiguration should return configuration with single-element lists")
    void testGetIncludeProjectPropertiesConfigurationWithSingleElements()
    {
        // Arrange
        IncludeProjectPropertiesConfiguration expectedConfig =
                new IncludeProjectPropertiesConfiguration(
                        Collections.singletonList("singleProperty"),
                        Collections.singletonList("singleManifest")
                );
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(1000L, expectedConfig);

        // Act
        IncludeProjectPropertiesConfiguration result = config.getIncludeProjectPropertiesConfiguration();

        // Assert
        assertNotNull(result);
        assertSame(expectedConfig, result);
        assertEquals(1, result.getProperties().size());
        assertEquals(1, result.getManifestProperties().size());
        assertEquals("singleProperty", result.getProperties().get(0));
        assertEquals("singleManifest", result.getManifestProperties().get(0));
    }
}

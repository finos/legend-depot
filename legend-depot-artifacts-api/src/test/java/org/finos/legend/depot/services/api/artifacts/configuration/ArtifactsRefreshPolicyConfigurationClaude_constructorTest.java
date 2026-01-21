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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArtifactsRefreshPolicyConfigurationClaude_constructorTest
{
    private static final long ONE_HOUR = 60 * 60 * 1000L;
    private static final long DEFAULT_INTERVAL = 2 * ONE_HOUR;

    @Test
    @DisplayName("Constructor with null versionsUpdateIntervalInMillis should use default value")
    void testConstructorWithNullInterval()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = null;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration =
                new IncludeProjectPropertiesConfiguration(Arrays.asList("prop1"), Arrays.asList("manifest1"));

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(DEFAULT_INTERVAL, config.getVersionsUpdateIntervalInMillis());
        assertNotNull(config.getIncludeProjectPropertiesConfiguration());
        assertEquals(includeProjectPropertiesConfiguration, config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with specific versionsUpdateIntervalInMillis should use provided value")
    void testConstructorWithSpecificInterval()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = 5000L;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration =
                new IncludeProjectPropertiesConfiguration(Arrays.asList("prop1", "prop2"), Arrays.asList("manifest1"));

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(5000L, config.getVersionsUpdateIntervalInMillis());
        assertNotNull(config.getIncludeProjectPropertiesConfiguration());
        assertEquals(includeProjectPropertiesConfiguration, config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with zero versionsUpdateIntervalInMillis should use zero")
    void testConstructorWithZeroInterval()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = 0L;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration =
                new IncludeProjectPropertiesConfiguration(Collections.emptyList(), Collections.emptyList());

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(0L, config.getVersionsUpdateIntervalInMillis());
        assertNotNull(config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with null includeProjectPropertiesConfiguration should set null")
    void testConstructorWithNullIncludeProjectPropertiesConfiguration()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = 10000L;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration = null;

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(10000L, config.getVersionsUpdateIntervalInMillis());
        assertNull(config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with both parameters null should use defaults")
    void testConstructorWithBothParametersNull()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = null;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration = null;

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(DEFAULT_INTERVAL, config.getVersionsUpdateIntervalInMillis());
        assertNull(config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with negative versionsUpdateIntervalInMillis should accept negative value")
    void testConstructorWithNegativeInterval()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = -1000L;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration =
                new IncludeProjectPropertiesConfiguration(Arrays.asList("prop1"), null);

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(-1000L, config.getVersionsUpdateIntervalInMillis());
        assertNotNull(config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with very large versionsUpdateIntervalInMillis should accept large value")
    void testConstructorWithLargeInterval()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = Long.MAX_VALUE;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration =
                new IncludeProjectPropertiesConfiguration(null, Arrays.asList("manifest1", "manifest2"));

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(Long.MAX_VALUE, config.getVersionsUpdateIntervalInMillis());
        assertNotNull(config.getIncludeProjectPropertiesConfiguration());
    }

    @Test
    @DisplayName("Constructor with both non-null parameters should set both values")
    void testConstructorWithBothParametersSet()
    {
        // Arrange
        Long versionsUpdateIntervalInMillis = 3600000L;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration =
                new IncludeProjectPropertiesConfiguration(
                        Arrays.asList("property1", "property2", "property3"),
                        Arrays.asList("manifestProp1", "manifestProp2")
                );

        // Act
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        assertEquals(3600000L, config.getVersionsUpdateIntervalInMillis());
        assertNotNull(config.getIncludeProjectPropertiesConfiguration());
        assertEquals(includeProjectPropertiesConfiguration, config.getIncludeProjectPropertiesConfiguration());
        assertEquals(3, config.getIncludeProjectPropertiesConfiguration().getProperties().size());
        assertEquals(2, config.getIncludeProjectPropertiesConfiguration().getManifestProperties().size());
    }
}

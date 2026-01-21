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

class ArtifactsRefreshPolicyConfigurationClaude_getVersionsUpdateIntervalInMillisTest
{
    private static final long ONE_HOUR = 60 * 60 * 1000L;
    private static final long DEFAULT_INTERVAL = 2 * ONE_HOUR;

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return default value when constructor receives null")
    void testGetVersionsUpdateIntervalInMillisWithDefaultValue()
    {
        // Arrange
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(null, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(DEFAULT_INTERVAL, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return explicitly set positive value")
    void testGetVersionsUpdateIntervalInMillisWithPositiveValue()
    {
        // Arrange
        long expectedValue = 5000L;
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(expectedValue, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return zero when set to zero")
    void testGetVersionsUpdateIntervalInMillisWithZero()
    {
        // Arrange
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(0L, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return negative value when set to negative")
    void testGetVersionsUpdateIntervalInMillisWithNegativeValue()
    {
        // Arrange
        long expectedValue = -1000L;
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(expectedValue, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return Long.MAX_VALUE when set to maximum")
    void testGetVersionsUpdateIntervalInMillisWithMaxValue()
    {
        // Arrange
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(Long.MAX_VALUE, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return Long.MIN_VALUE when set to minimum")
    void testGetVersionsUpdateIntervalInMillisWithMinValue()
    {
        // Arrange
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(Long.MIN_VALUE, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(Long.MIN_VALUE, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return correct value regardless of includeProjectPropertiesConfiguration")
    void testGetVersionsUpdateIntervalInMillisWithNonNullConfiguration()
    {
        // Arrange
        long expectedValue = 123456789L;
        IncludeProjectPropertiesConfiguration includeConfig =
                new IncludeProjectPropertiesConfiguration(
                        Arrays.asList("prop1", "prop2"),
                        Collections.singletonList("manifest1")
                );
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(expectedValue, includeConfig);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return same value on multiple calls")
    void testGetVersionsUpdateIntervalInMillisConsistency()
    {
        // Arrange
        long expectedValue = 999999L;
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(expectedValue, null);

        // Act
        long firstCall = config.getVersionsUpdateIntervalInMillis();
        long secondCall = config.getVersionsUpdateIntervalInMillis();
        long thirdCall = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(expectedValue, firstCall);
        assertEquals(expectedValue, secondCall);
        assertEquals(expectedValue, thirdCall);
        assertEquals(firstCall, secondCall);
        assertEquals(secondCall, thirdCall);
    }

    @Test
    @DisplayName("getVersionsUpdateIntervalInMillis should return one hour when set to one hour")
    void testGetVersionsUpdateIntervalInMillisWithOneHour()
    {
        // Arrange
        ArtifactsRefreshPolicyConfiguration config =
                new ArtifactsRefreshPolicyConfiguration(ONE_HOUR, null);

        // Act
        long result = config.getVersionsUpdateIntervalInMillis();

        // Assert
        assertEquals(ONE_HOUR, result);
        assertEquals(3600000L, result);
    }
}

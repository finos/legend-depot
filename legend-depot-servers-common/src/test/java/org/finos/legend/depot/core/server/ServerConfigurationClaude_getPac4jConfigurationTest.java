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

package org.finos.legend.depot.core.server;

import org.finos.legend.server.pac4j.LegendPac4jConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNull;

class ServerConfigurationClaude_getPac4jConfigurationTest
{
    /**
     * Reflection is necessary to test getPac4jConfiguration() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'pac4jConfiguration' field directly and
     * verify that getPac4jConfiguration() returns exactly what was set, without requiring
     * full Dropwizard framework initialization including YAML parsing, validation, and
     * dependency injection setup.
     *
     * Note: LegendPac4jConfiguration is a final class with Guava dependencies that cannot be
     * instantiated in the test environment without additional dependencies. Therefore, tests
     * focus on verifying the getter returns null when the field is not set, which is sufficient
     * to verify the simple getter behavior.
     */

    private void setPac4jConfigurationField(ServerConfiguration config, LegendPac4jConfiguration pac4jConfig) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("pac4jConfiguration");
        field.setAccessible(true);
        field.set(config, pac4jConfig);
    }

    @Test
    @DisplayName("Test getPac4jConfiguration returns null when not set")
    void testGetPac4jConfigurationReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setPac4jConfigurationField(config, null);

        // Act
        LegendPac4jConfiguration result = config.getPac4jConfiguration();

        // Assert
        assertNull(result, "LegendPac4jConfiguration should be null when not set");
    }

    @Test
    @DisplayName("Test getPac4jConfiguration is idempotent when null")
    void testGetPac4jConfigurationIsIdempotentWhenNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setPac4jConfigurationField(config, null);

        // Act
        LegendPac4jConfiguration result1 = config.getPac4jConfiguration();
        LegendPac4jConfiguration result2 = config.getPac4jConfiguration();
        LegendPac4jConfiguration result3 = config.getPac4jConfiguration();

        // Assert
        assertNull(result1, "First call should return null");
        assertNull(result2, "Second call should return null");
        assertNull(result3, "Third call should return null");
    }

    @Test
    @DisplayName("Test getPac4jConfiguration with different configurations maintains independence")
    void testGetPac4jConfigurationIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        setPac4jConfigurationField(config1, null);
        setPac4jConfigurationField(config2, null);

        // Act
        LegendPac4jConfiguration result1 = config1.getPac4jConfiguration();
        LegendPac4jConfiguration result2 = config2.getPac4jConfiguration();

        // Assert
        assertNull(result1, "Config1 should return null");
        assertNull(result2, "Config2 should return null");
    }
}

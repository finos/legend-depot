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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServerConfigurationClaude_getApplicationNameTest
{
    /**
     * Reflection is necessary to test getApplicationName() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'applicationName' field directly and verify
     * that getApplicationName() returns exactly what was set, without requiring full Dropwizard
     * framework initialization including YAML parsing, validation, and dependency injection setup.
     */

    private void setApplicationNameField(ServerConfiguration config, String applicationName) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("applicationName");
        field.setAccessible(true);
        field.set(config, applicationName);
    }

    @Test
    @DisplayName("Test getApplicationName returns configured value")
    void testGetApplicationNameReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String appName = "legend-depot";
        setApplicationNameField(config, appName);

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("legend-depot", result, "Application name should be 'legend-depot'");
        assertSame(appName, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getApplicationName returns null when not set")
    void testGetApplicationNameReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, null);

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNull(result, "Application name should be null when not set");
    }

    @Test
    @DisplayName("Test getApplicationName is idempotent")
    void testGetApplicationNameIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String appName = "depot-server";
        setApplicationNameField(config, appName);

        // Act
        String result1 = config.getApplicationName();
        String result2 = config.getApplicationName();
        String result3 = config.getApplicationName();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertEquals("depot-server", result1);
        assertEquals("depot-server", result2);
        assertEquals("depot-server", result3);
    }

    @Test
    @DisplayName("Test getApplicationName with simple name")
    void testGetApplicationNameWithSimpleName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "depot");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("depot", result, "Application name should be 'depot'");
    }

    @Test
    @DisplayName("Test getApplicationName with hyphenated name")
    void testGetApplicationNameWithHyphenatedName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "legend-depot-server");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("legend-depot-server", result, "Application name should be 'legend-depot-server'");
    }

    @Test
    @DisplayName("Test getApplicationName with name containing numbers")
    void testGetApplicationNameWithNumbers() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "depot-v2");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("depot-v2", result, "Application name should be 'depot-v2'");
    }

    @Test
    @DisplayName("Test getApplicationName with name containing underscores")
    void testGetApplicationNameWithUnderscores() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "legend_depot_server");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("legend_depot_server", result, "Application name should handle underscores");
    }

    @Test
    @DisplayName("Test getApplicationName with different names maintains independence")
    void testGetApplicationNameIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        setApplicationNameField(config1, "depot-server-1");
        setApplicationNameField(config2, "depot-server-2");

        // Act
        String result1 = config1.getApplicationName();
        String result2 = config2.getApplicationName();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertEquals("depot-server-1", result1, "Config1 should have 'depot-server-1'");
        assertEquals("depot-server-2", result2, "Config2 should have 'depot-server-2'");
    }

    @Test
    @DisplayName("Test getApplicationName with empty string")
    void testGetApplicationNameWithEmptyString() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("", result, "Application name should be empty string");
    }

    @Test
    @DisplayName("Test getApplicationName returns exact same object reference")
    void testGetApplicationNameReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String appName = "depot-application";
        setApplicationNameField(config, appName);

        // Act
        String result = config.getApplicationName();

        // Assert
        assertSame(appName, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getApplicationName with lowercase name")
    void testGetApplicationNameWithLowercaseName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "legenddepot");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("legenddepot", result, "Application name should preserve case");
    }

    @Test
    @DisplayName("Test getApplicationName with mixed case name")
    void testGetApplicationNameWithMixedCaseName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "LegendDepot");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("LegendDepot", result, "Application name should preserve case");
    }

    @Test
    @DisplayName("Test getApplicationName with uppercase name")
    void testGetApplicationNameWithUppercaseName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "LEGEND_DEPOT");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("LEGEND_DEPOT", result, "Application name should preserve case");
    }

    @Test
    @DisplayName("Test getApplicationName with long name")
    void testGetApplicationNameWithLongName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String longName = "legend-depot-artifact-repository-management-server-application";
        setApplicationNameField(config, longName);

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals(longName, result, "Application name should handle long names");
    }

    @Test
    @DisplayName("Test getApplicationName with name containing dots")
    void testGetApplicationNameWithDots() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setApplicationNameField(config, "org.finos.legend.depot");

        // Act
        String result = config.getApplicationName();

        // Assert
        assertNotNull(result, "Application name should not be null");
        assertEquals("org.finos.legend.depot", result, "Application name should handle dots");
    }
}

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

import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServerConfigurationClaude_getSwaggerBundleConfigurationTest
{
    /**
     * Reflection is necessary to test getSwaggerBundleConfiguration() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'swaggerBundleConfiguration' field directly
     * and verify that getSwaggerBundleConfiguration() returns exactly what was set, without
     * requiring full Dropwizard framework initialization including YAML parsing, validation,
     * and dependency injection setup.
     */

    private void setSwaggerBundleConfigurationField(ServerConfiguration config, SwaggerBundleConfiguration swaggerConfig) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("swaggerBundleConfiguration");
        field.setAccessible(true);
        field.set(config, swaggerConfig);
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration returns configured value")
    void testGetSwaggerBundleConfigurationReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);

        // Act
        SwaggerBundleConfiguration result = config.getSwaggerBundleConfiguration();

        // Assert
        assertNotNull(result, "SwaggerBundleConfiguration should not be null");
        assertSame(swaggerConfig, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration returns null when not set")
    void testGetSwaggerBundleConfigurationReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSwaggerBundleConfigurationField(config, null);

        // Act
        SwaggerBundleConfiguration result = config.getSwaggerBundleConfiguration();

        // Assert
        assertNull(result, "SwaggerBundleConfiguration should be null when not set");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration is idempotent")
    void testGetSwaggerBundleConfigurationIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);

        // Act
        SwaggerBundleConfiguration result1 = config.getSwaggerBundleConfiguration();
        SwaggerBundleConfiguration result2 = config.getSwaggerBundleConfiguration();
        SwaggerBundleConfiguration result3 = config.getSwaggerBundleConfiguration();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration with minimal configuration")
    void testGetSwaggerBundleConfigurationWithMinimalConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);

        // Act
        SwaggerBundleConfiguration result = config.getSwaggerBundleConfiguration();

        // Assert
        assertNotNull(result, "SwaggerBundleConfiguration should not be null");
        assertSame(swaggerConfig, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration with different configurations maintains independence")
    void testGetSwaggerBundleConfigurationIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        SwaggerBundleConfiguration swaggerConfig1 = Mockito.mock(SwaggerBundleConfiguration.class);
        SwaggerBundleConfiguration swaggerConfig2 = Mockito.mock(SwaggerBundleConfiguration.class);

        setSwaggerBundleConfigurationField(config1, swaggerConfig1);
        setSwaggerBundleConfigurationField(config2, swaggerConfig2);

        // Act
        SwaggerBundleConfiguration result1 = config1.getSwaggerBundleConfiguration();
        SwaggerBundleConfiguration result2 = config2.getSwaggerBundleConfiguration();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertSame(swaggerConfig1, result1, "Config1 should return its swagger config");
        assertSame(swaggerConfig2, result2, "Config2 should return its swagger config");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration returns exact same object reference")
    void testGetSwaggerBundleConfigurationReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);

        // Act
        SwaggerBundleConfiguration result = config.getSwaggerBundleConfiguration();

        // Assert
        assertSame(swaggerConfig, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration maintains reference to object")
    void testGetSwaggerBundleConfigurationMaintainsReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);

        // Act
        SwaggerBundleConfiguration retrieved = config.getSwaggerBundleConfiguration();

        // Assert
        assertSame(swaggerConfig, retrieved, "Should maintain the exact same reference");

        // Verify calling getter returns same instance
        SwaggerBundleConfiguration retrievedAgain = config.getSwaggerBundleConfiguration();
        assertSame(retrieved, retrievedAgain, "Multiple retrievals should return same instance");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration with multiple mocked configurations")
    void testGetSwaggerBundleConfigurationWithMultipleMocks() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Test with first mock
        SwaggerBundleConfiguration swaggerConfig1 = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig1);
        assertSame(swaggerConfig1, config.getSwaggerBundleConfiguration());

        // Test with second mock
        SwaggerBundleConfiguration swaggerConfig2 = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig2);
        assertSame(swaggerConfig2, config.getSwaggerBundleConfiguration());
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration null then non-null")
    void testGetSwaggerBundleConfigurationNullThenNonNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Initially null
        setSwaggerBundleConfigurationField(config, null);
        assertNull(config.getSwaggerBundleConfiguration(), "Should initially be null");

        // Then set to non-null
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);
        assertSame(swaggerConfig, config.getSwaggerBundleConfiguration(), "Should now return the mock");
    }

    @Test
    @DisplayName("Test getSwaggerBundleConfiguration non-null then null")
    void testGetSwaggerBundleConfigurationNonNullThenNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Initially non-null
        SwaggerBundleConfiguration swaggerConfig = Mockito.mock(SwaggerBundleConfiguration.class);
        setSwaggerBundleConfigurationField(config, swaggerConfig);
        assertSame(swaggerConfig, config.getSwaggerBundleConfiguration(), "Should initially return the mock");

        // Then set to null
        setSwaggerBundleConfigurationField(config, null);
        assertNull(config.getSwaggerBundleConfiguration(), "Should now be null");
    }
}

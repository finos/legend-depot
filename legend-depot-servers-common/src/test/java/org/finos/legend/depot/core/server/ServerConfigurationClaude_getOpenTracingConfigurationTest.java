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

import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.TracerProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_getOpenTracingConfigurationTest
{
    /**
     * Reflection is necessary to test getOpenTracingConfiguration() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'openTracingConfiguration' field directly
     * and verify that getOpenTracingConfiguration() returns exactly what was set, without
     * requiring full Dropwizard framework initialization including YAML parsing, validation,
     * and dependency injection setup.
     */

    private void setOpenTracingConfigurationField(ServerConfiguration config, OpenTracingConfiguration tracingConfig) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("openTracingConfiguration");
        field.setAccessible(true);
        field.set(config, tracingConfig);
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration returns configured value")
    void testGetOpenTracingConfigurationReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setEnabled(true);
        tracingConfig.setServiceName("test-service");
        tracingConfig.setOpenTracingUri("http://localhost:9411");
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertTrue(result.isEnabled(), "Tracing should be enabled");
        assertEquals("test-service", result.getServiceName(), "Service name should be 'test-service'");
        assertEquals("http://localhost:9411", result.getOpenTracingUri(), "URI should match");
        assertSame(tracingConfig, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration returns null when not set")
    void testGetOpenTracingConfigurationReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setOpenTracingConfigurationField(config, null);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNull(result, "OpenTracingConfiguration should be null when not set");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration with disabled tracing")
    void testGetOpenTracingConfigurationWithDisabledTracing() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setEnabled(false);
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertFalse(result.isEnabled(), "Tracing should be disabled");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration is idempotent")
    void testGetOpenTracingConfigurationIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setServiceName("idempotent-test");
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result1 = config.getOpenTracingConfiguration();
        OpenTracingConfiguration result2 = config.getOpenTracingConfiguration();
        OpenTracingConfiguration result3 = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertEquals("idempotent-test", result1.getServiceName());
        assertEquals("idempotent-test", result2.getServiceName());
        assertEquals("idempotent-test", result3.getServiceName());
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration with minimal configuration")
    void testGetOpenTracingConfigurationWithMinimalConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertFalse(result.isEnabled(), "Default should be disabled");
        assertNull(result.getServiceName(), "Service name should be null");
        assertNull(result.getOpenTracingUri(), "URI should be null");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration with complete configuration")
    void testGetOpenTracingConfigurationWithCompleteConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setEnabled(true);
        tracingConfig.setServiceName("depot-service");
        tracingConfig.setOpenTracingUri("http://jaeger:14268/api/traces");
        TracerProvider mockProvider = Mockito.mock(TracerProvider.class);
        tracingConfig.setTracerProvider(mockProvider);
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertTrue(result.isEnabled(), "Tracing should be enabled");
        assertEquals("depot-service", result.getServiceName());
        assertEquals("http://jaeger:14268/api/traces", result.getOpenTracingUri());
        assertSame(mockProvider, result.getTracerProvider(), "TracerProvider should be set");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration with different service names")
    void testGetOpenTracingConfigurationWithDifferentServiceNames() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        OpenTracingConfiguration tracingConfig1 = new OpenTracingConfiguration();
        tracingConfig1.setServiceName("service-1");

        OpenTracingConfiguration tracingConfig2 = new OpenTracingConfiguration();
        tracingConfig2.setServiceName("service-2");

        setOpenTracingConfigurationField(config1, tracingConfig1);
        setOpenTracingConfigurationField(config2, tracingConfig2);

        // Act
        OpenTracingConfiguration result1 = config1.getOpenTracingConfiguration();
        OpenTracingConfiguration result2 = config2.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertEquals("service-1", result1.getServiceName(), "Config1 should have 'service-1'");
        assertEquals("service-2", result2.getServiceName(), "Config2 should have 'service-2'");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration returns exact same object reference")
    void testGetOpenTracingConfigurationReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setServiceName("reference-test");
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertSame(tracingConfig, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration with various URI formats")
    void testGetOpenTracingConfigurationWithVariousURIFormats() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setOpenTracingUri("https://tracing.example.com:9411/api/v2/spans");
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertEquals("https://tracing.example.com:9411/api/v2/spans", result.getOpenTracingUri());
    }

    @Test
    @DisplayName("Test getOpenTracingConfiguration with empty service name")
    void testGetOpenTracingConfigurationWithEmptyServiceName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setServiceName("");
        setOpenTracingConfigurationField(config, tracingConfig);

        // Act
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();

        // Assert
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertEquals("", result.getServiceName(), "Service name should be empty string");
    }
}

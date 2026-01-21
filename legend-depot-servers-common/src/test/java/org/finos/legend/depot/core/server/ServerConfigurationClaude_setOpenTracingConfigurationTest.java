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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_setOpenTracingConfigurationTest
{
    @Test
    @DisplayName("Test setOpenTracingConfiguration sets the configuration")
    void testSetOpenTracingConfigurationSetsTheConfiguration() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setEnabled(true);
        tracingConfig.setServiceName("test-service");
        tracingConfig.setOpenTracingUri("http://localhost:9411");

        // Act
        config.setOpenTracingConfiguration(tracingConfig);

        // Assert
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();
        assertNotNull(result, "OpenTracingConfiguration should not be null after setting");
        assertTrue(result.isEnabled(), "Tracing should be enabled");
        assertEquals("test-service", result.getServiceName(), "Service name should match");
        assertEquals("http://localhost:9411", result.getOpenTracingUri(), "URI should match");
        assertSame(tracingConfig, result, "Should return the same instance that was set");
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with null")
    void testSetOpenTracingConfigurationWithNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act
        config.setOpenTracingConfiguration(null);

        // Assert
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();
        assertNull(result, "OpenTracingConfiguration should be null when set to null");
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration overwrites previous value")
    void testSetOpenTracingConfigurationOverwritesPreviousValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        OpenTracingConfiguration firstConfig = new OpenTracingConfiguration();
        firstConfig.setServiceName("first-service");
        firstConfig.setEnabled(true);

        OpenTracingConfiguration secondConfig = new OpenTracingConfiguration();
        secondConfig.setServiceName("second-service");
        secondConfig.setEnabled(false);

        // Act
        config.setOpenTracingConfiguration(firstConfig);
        OpenTracingConfiguration firstResult = config.getOpenTracingConfiguration();

        config.setOpenTracingConfiguration(secondConfig);
        OpenTracingConfiguration secondResult = config.getOpenTracingConfiguration();

        // Assert
        assertSame(firstConfig, firstResult, "First call should return first config");
        assertSame(secondConfig, secondResult, "Second call should return second config");
        assertEquals("first-service", firstResult.getServiceName());
        assertTrue(firstResult.isEnabled());
        assertEquals("second-service", secondResult.getServiceName());
        assertFalse(secondResult.isEnabled());
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with minimal configuration")
    void testSetOpenTracingConfigurationWithMinimalConfiguration() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();

        // Act
        config.setOpenTracingConfiguration(tracingConfig);

        // Assert
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertFalse(result.isEnabled(), "Default should be disabled");
        assertNull(result.getServiceName(), "Service name should be null");
        assertNull(result.getOpenTracingUri(), "URI should be null");
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with complete configuration")
    void testSetOpenTracingConfigurationWithCompleteConfiguration() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setEnabled(true);
        tracingConfig.setServiceName("depot-service");
        tracingConfig.setOpenTracingUri("http://jaeger:14268/api/traces");
        TracerProvider mockProvider = Mockito.mock(TracerProvider.class);
        tracingConfig.setTracerProvider(mockProvider);

        // Act
        config.setOpenTracingConfiguration(tracingConfig);

        // Assert
        OpenTracingConfiguration result = config.getOpenTracingConfiguration();
        assertNotNull(result, "OpenTracingConfiguration should not be null");
        assertTrue(result.isEnabled(), "Tracing should be enabled");
        assertEquals("depot-service", result.getServiceName());
        assertEquals("http://jaeger:14268/api/traces", result.getOpenTracingUri());
        assertSame(mockProvider, result.getTracerProvider(), "TracerProvider should be set");
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration can be called multiple times")
    void testSetOpenTracingConfigurationCanBeCalledMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - First call
        OpenTracingConfiguration first = new OpenTracingConfiguration();
        first.setServiceName("first");
        config.setOpenTracingConfiguration(first);
        assertEquals("first", config.getOpenTracingConfiguration().getServiceName());

        // Act & Assert - Second call
        OpenTracingConfiguration second = new OpenTracingConfiguration();
        second.setServiceName("second");
        config.setOpenTracingConfiguration(second);
        assertEquals("second", config.getOpenTracingConfiguration().getServiceName());

        // Act & Assert - Third call with null
        config.setOpenTracingConfiguration(null);
        assertNull(config.getOpenTracingConfiguration());

        // Act & Assert - Fourth call with new config
        OpenTracingConfiguration fourth = new OpenTracingConfiguration();
        fourth.setServiceName("fourth");
        config.setOpenTracingConfiguration(fourth);
        assertEquals("fourth", config.getOpenTracingConfiguration().getServiceName());
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with same reference multiple times")
    void testSetOpenTracingConfigurationWithSameReferenceMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setServiceName("same-reference");

        // Act
        config.setOpenTracingConfiguration(tracingConfig);
        config.setOpenTracingConfiguration(tracingConfig);
        config.setOpenTracingConfiguration(tracingConfig);

        // Assert
        assertSame(tracingConfig, config.getOpenTracingConfiguration(), "Should still reference the same config");
        assertEquals("same-reference", config.getOpenTracingConfiguration().getServiceName());
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration maintains reference to mutable object")
    void testSetOpenTracingConfigurationMaintainsReferenceToMutableObject() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        OpenTracingConfiguration tracingConfig = new OpenTracingConfiguration();
        tracingConfig.setServiceName("initial-name");

        // Act
        config.setOpenTracingConfiguration(tracingConfig);
        OpenTracingConfiguration retrieved = config.getOpenTracingConfiguration();

        // Assert
        assertSame(tracingConfig, retrieved, "Should maintain the exact same reference");

        // Verify that modifications to the original object affect the configuration
        tracingConfig.setServiceName("modified-name");
        assertEquals("modified-name", config.getOpenTracingConfiguration().getServiceName(),
                "Changes to original object should be reflected");
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with enabled and disabled states")
    void testSetOpenTracingConfigurationWithEnabledAndDisabledStates() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - Set enabled
        OpenTracingConfiguration enabledConfig = new OpenTracingConfiguration();
        enabledConfig.setEnabled(true);
        config.setOpenTracingConfiguration(enabledConfig);
        assertTrue(config.getOpenTracingConfiguration().isEnabled(), "Should be enabled");

        // Act & Assert - Set disabled
        OpenTracingConfiguration disabledConfig = new OpenTracingConfiguration();
        disabledConfig.setEnabled(false);
        config.setOpenTracingConfiguration(disabledConfig);
        assertFalse(config.getOpenTracingConfiguration().isEnabled(), "Should be disabled");
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with various URI formats")
    void testSetOpenTracingConfigurationWithVariousURIFormats() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Test HTTP URI
        OpenTracingConfiguration httpConfig = new OpenTracingConfiguration();
        httpConfig.setOpenTracingUri("http://localhost:9411");
        config.setOpenTracingConfiguration(httpConfig);
        assertEquals("http://localhost:9411", config.getOpenTracingConfiguration().getOpenTracingUri());

        // Test HTTPS URI
        OpenTracingConfiguration httpsConfig = new OpenTracingConfiguration();
        httpsConfig.setOpenTracingUri("https://tracing.example.com:9411/api/v2/spans");
        config.setOpenTracingConfiguration(httpsConfig);
        assertEquals("https://tracing.example.com:9411/api/v2/spans",
                config.getOpenTracingConfiguration().getOpenTracingUri());

        // Test with path
        OpenTracingConfiguration pathConfig = new OpenTracingConfiguration();
        pathConfig.setOpenTracingUri("http://jaeger:14268/api/traces");
        config.setOpenTracingConfiguration(pathConfig);
        assertEquals("http://jaeger:14268/api/traces",
                config.getOpenTracingConfiguration().getOpenTracingUri());
    }

    @Test
    @DisplayName("Test setOpenTracingConfiguration with empty and null service names")
    void testSetOpenTracingConfigurationWithEmptyAndNullServiceNames() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Test with null service name
        OpenTracingConfiguration nullNameConfig = new OpenTracingConfiguration();
        nullNameConfig.setServiceName(null);
        config.setOpenTracingConfiguration(nullNameConfig);
        assertNull(config.getOpenTracingConfiguration().getServiceName());

        // Test with empty service name
        OpenTracingConfiguration emptyNameConfig = new OpenTracingConfiguration();
        emptyNameConfig.setServiceName("");
        config.setOpenTracingConfiguration(emptyNameConfig);
        assertEquals("", config.getOpenTracingConfiguration().getServiceName());

        // Test with non-empty service name
        OpenTracingConfiguration normalNameConfig = new OpenTracingConfiguration();
        normalNameConfig.setServiceName("normal-service");
        config.setOpenTracingConfiguration(normalNameConfig);
        assertEquals("normal-service", config.getOpenTracingConfiguration().getServiceName());
    }
}

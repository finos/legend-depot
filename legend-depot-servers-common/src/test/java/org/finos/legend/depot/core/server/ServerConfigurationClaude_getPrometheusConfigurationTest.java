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

import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
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

class ServerConfigurationClaude_getPrometheusConfigurationTest
{
    /**
     * Reflection is necessary to test getPrometheusConfiguration() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'prometheusConfiguration' field directly
     * and verify that getPrometheusConfiguration() returns exactly what was set, without
     * requiring full Dropwizard framework initialization including YAML parsing, validation,
     * and dependency injection setup.
     */

    private void setPrometheusConfigurationField(ServerConfiguration config, PrometheusConfiguration prometheusConfig) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("prometheusConfiguration");
        field.setAccessible(true);
        field.set(config, prometheusConfig);
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration returns configured value")
    void testGetPrometheusConfigurationReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration result = config.getPrometheusConfiguration();

        // Assert
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertTrue(result.isEnabled(), "Prometheus should be enabled");
        assertSame(prometheusConfig, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration returns null when not set")
    void testGetPrometheusConfigurationReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setPrometheusConfigurationField(config, null);

        // Act
        PrometheusConfiguration result = config.getPrometheusConfiguration();

        // Assert
        assertNull(result, "PrometheusConfiguration should be null when not set");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration is idempotent")
    void testGetPrometheusConfigurationIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration result1 = config.getPrometheusConfiguration();
        PrometheusConfiguration result2 = config.getPrometheusConfiguration();
        PrometheusConfiguration result3 = config.getPrometheusConfiguration();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertTrue(result1.isEnabled());
        assertTrue(result2.isEnabled());
        assertTrue(result3.isEnabled());
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration with disabled configuration")
    void testGetPrometheusConfigurationWithDisabledConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(false);
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration result = config.getPrometheusConfiguration();

        // Assert
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertFalse(result.isEnabled(), "Prometheus should be disabled");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration with default configuration")
    void testGetPrometheusConfigurationWithDefaultConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration result = config.getPrometheusConfiguration();

        // Assert
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertFalse(result.isEnabled(), "Prometheus should be disabled by default");
        assertNull(result.getMetricsHandler(), "MetricsHandler should be null by default");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration with different configurations maintains independence")
    void testGetPrometheusConfigurationIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        PrometheusConfiguration prometheusConfig1 = new PrometheusConfiguration();
        prometheusConfig1.setEnabled(true);

        PrometheusConfiguration prometheusConfig2 = new PrometheusConfiguration();
        prometheusConfig2.setEnabled(false);

        setPrometheusConfigurationField(config1, prometheusConfig1);
        setPrometheusConfigurationField(config2, prometheusConfig2);

        // Act
        PrometheusConfiguration result1 = config1.getPrometheusConfiguration();
        PrometheusConfiguration result2 = config2.getPrometheusConfiguration();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertTrue(result1.isEnabled(), "Config1 should have Prometheus enabled");
        assertFalse(result2.isEnabled(), "Config2 should have Prometheus disabled");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration returns exact same object reference")
    void testGetPrometheusConfigurationReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration result = config.getPrometheusConfiguration();

        // Assert
        assertSame(prometheusConfig, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration maintains reference to mutable object")
    void testGetPrometheusConfigurationMaintainsReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(false);
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration retrieved = config.getPrometheusConfiguration();

        // Assert
        assertSame(prometheusConfig, retrieved, "Should maintain the exact same reference");
        assertFalse(retrieved.isEnabled(), "Initially should be disabled");

        // Verify that modifications to the original object affect the configuration
        prometheusConfig.setEnabled(true);
        assertTrue(config.getPrometheusConfiguration().isEnabled(),
                "Changes to original object should be reflected");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration with configuration using constructor")
    void testGetPrometheusConfigurationWithConstructor() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration(true, null);
        setPrometheusConfigurationField(config, prometheusConfig);

        // Act
        PrometheusConfiguration result = config.getPrometheusConfiguration();

        // Assert
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertTrue(result.isEnabled(), "Prometheus should be enabled");
        assertNull(result.getMetricsHandler(), "MetricsHandler should be null");
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration null then non-null")
    void testGetPrometheusConfigurationNullThenNonNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Initially null
        setPrometheusConfigurationField(config, null);
        assertNull(config.getPrometheusConfiguration(), "Should initially be null");

        // Then set to non-null
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);
        setPrometheusConfigurationField(config, prometheusConfig);
        assertSame(prometheusConfig, config.getPrometheusConfiguration(), "Should now return the config");
        assertTrue(config.getPrometheusConfiguration().isEnabled());
    }

    @Test
    @DisplayName("Test getPrometheusConfiguration non-null then null")
    void testGetPrometheusConfigurationNonNullThenNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Initially non-null
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);
        setPrometheusConfigurationField(config, prometheusConfig);
        assertSame(prometheusConfig, config.getPrometheusConfiguration(), "Should initially return the config");

        // Then set to null
        setPrometheusConfigurationField(config, null);
        assertNull(config.getPrometheusConfiguration(), "Should now be null");
    }
}

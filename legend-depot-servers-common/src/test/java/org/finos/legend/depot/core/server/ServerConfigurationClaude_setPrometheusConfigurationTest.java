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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_setPrometheusConfigurationTest
{
    @Test
    @DisplayName("Test setPrometheusConfiguration sets the configuration")
    void testSetPrometheusConfigurationSetsTheConfiguration() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);

        // Act
        config.setPrometheusConfiguration(prometheusConfig);

        // Assert
        PrometheusConfiguration result = config.getPrometheusConfiguration();
        assertNotNull(result, "PrometheusConfiguration should not be null after setting");
        assertTrue(result.isEnabled(), "Prometheus should be enabled");
        assertSame(prometheusConfig, result, "Should return the same instance that was set");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration with null")
    void testSetPrometheusConfigurationWithNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act
        config.setPrometheusConfiguration(null);

        // Assert
        PrometheusConfiguration result = config.getPrometheusConfiguration();
        assertNull(result, "PrometheusConfiguration should be null when set to null");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration overwrites previous value")
    void testSetPrometheusConfigurationOverwritesPreviousValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        PrometheusConfiguration firstConfig = new PrometheusConfiguration();
        firstConfig.setEnabled(true);

        PrometheusConfiguration secondConfig = new PrometheusConfiguration();
        secondConfig.setEnabled(false);

        // Act
        config.setPrometheusConfiguration(firstConfig);
        PrometheusConfiguration firstResult = config.getPrometheusConfiguration();

        config.setPrometheusConfiguration(secondConfig);
        PrometheusConfiguration secondResult = config.getPrometheusConfiguration();

        // Assert
        assertSame(firstConfig, firstResult, "First call should return first config");
        assertSame(secondConfig, secondResult, "Second call should return second config");
        assertTrue(firstResult.isEnabled());
        assertFalse(secondResult.isEnabled());
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration with enabled configuration")
    void testSetPrometheusConfigurationWithEnabledConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);

        // Act
        config.setPrometheusConfiguration(prometheusConfig);

        // Assert
        PrometheusConfiguration result = config.getPrometheusConfiguration();
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertTrue(result.isEnabled(), "Prometheus should be enabled");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration with disabled configuration")
    void testSetPrometheusConfigurationWithDisabledConfig() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(false);

        // Act
        config.setPrometheusConfiguration(prometheusConfig);

        // Assert
        PrometheusConfiguration result = config.getPrometheusConfiguration();
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertFalse(result.isEnabled(), "Prometheus should be disabled");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration can be called multiple times")
    void testSetPrometheusConfigurationCanBeCalledMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - First call
        PrometheusConfiguration first = new PrometheusConfiguration();
        first.setEnabled(true);
        config.setPrometheusConfiguration(first);
        assertTrue(config.getPrometheusConfiguration().isEnabled());

        // Act & Assert - Second call
        PrometheusConfiguration second = new PrometheusConfiguration();
        second.setEnabled(false);
        config.setPrometheusConfiguration(second);
        assertFalse(config.getPrometheusConfiguration().isEnabled());

        // Act & Assert - Third call with null
        config.setPrometheusConfiguration(null);
        assertNull(config.getPrometheusConfiguration());

        // Act & Assert - Fourth call with new config
        PrometheusConfiguration fourth = new PrometheusConfiguration();
        fourth.setEnabled(true);
        config.setPrometheusConfiguration(fourth);
        assertTrue(config.getPrometheusConfiguration().isEnabled());
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration with same reference multiple times")
    void testSetPrometheusConfigurationWithSameReferenceMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(true);

        // Act
        config.setPrometheusConfiguration(prometheusConfig);
        config.setPrometheusConfiguration(prometheusConfig);
        config.setPrometheusConfiguration(prometheusConfig);

        // Assert
        assertSame(prometheusConfig, config.getPrometheusConfiguration(), "Should still reference the same config");
        assertTrue(config.getPrometheusConfiguration().isEnabled());
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration maintains reference to mutable object")
    void testSetPrometheusConfigurationMaintainsReferenceToMutableObject() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        prometheusConfig.setEnabled(false);

        // Act
        config.setPrometheusConfiguration(prometheusConfig);
        PrometheusConfiguration retrieved = config.getPrometheusConfiguration();

        // Assert
        assertSame(prometheusConfig, retrieved, "Should maintain the exact same reference");

        // Verify that modifications to the original object affect the configuration
        prometheusConfig.setEnabled(true);
        assertTrue(config.getPrometheusConfiguration().isEnabled(),
                "Changes to original object should be reflected");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration toggles between enabled and disabled")
    void testSetPrometheusConfigurationToggles() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - Set enabled
        PrometheusConfiguration enabledConfig = new PrometheusConfiguration();
        enabledConfig.setEnabled(true);
        config.setPrometheusConfiguration(enabledConfig);
        assertTrue(config.getPrometheusConfiguration().isEnabled(), "Should be enabled");

        // Act & Assert - Set disabled
        PrometheusConfiguration disabledConfig = new PrometheusConfiguration();
        disabledConfig.setEnabled(false);
        config.setPrometheusConfiguration(disabledConfig);
        assertFalse(config.getPrometheusConfiguration().isEnabled(), "Should be disabled");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration with default constructor values")
    void testSetPrometheusConfigurationWithDefaultConstructorValues() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration();
        // Don't set any values, use defaults

        // Act
        config.setPrometheusConfiguration(prometheusConfig);

        // Assert
        PrometheusConfiguration result = config.getPrometheusConfiguration();
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertSame(prometheusConfig, result, "Should return the set instance");
        assertFalse(result.isEnabled(), "Default constructor should set enabled to false");
        assertNull(result.getMetricsHandler(), "Default constructor should set metricsHandler to null");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration with parameterized constructor")
    void testSetPrometheusConfigurationWithParameterizedConstructor() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        PrometheusConfiguration prometheusConfig = new PrometheusConfiguration(true, null);

        // Act
        config.setPrometheusConfiguration(prometheusConfig);

        // Assert
        PrometheusConfiguration result = config.getPrometheusConfiguration();
        assertNotNull(result, "PrometheusConfiguration should not be null");
        assertTrue(result.isEnabled(), "Should be enabled as per constructor");
        assertNull(result.getMetricsHandler(), "MetricsHandler should be null as per constructor");
    }

    @Test
    @DisplayName("Test setPrometheusConfiguration multiple times with different values")
    void testSetPrometheusConfigurationMultipleTimesWithDifferentValues() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Test cycle: true -> false -> true -> null -> false
        PrometheusConfiguration config1 = new PrometheusConfiguration();
        config1.setEnabled(true);
        config.setPrometheusConfiguration(config1);
        assertTrue(config.getPrometheusConfiguration().isEnabled());

        PrometheusConfiguration config2 = new PrometheusConfiguration();
        config2.setEnabled(false);
        config.setPrometheusConfiguration(config2);
        assertFalse(config.getPrometheusConfiguration().isEnabled());

        PrometheusConfiguration config3 = new PrometheusConfiguration();
        config3.setEnabled(true);
        config.setPrometheusConfiguration(config3);
        assertTrue(config.getPrometheusConfiguration().isEnabled());

        config.setPrometheusConfiguration(null);
        assertNull(config.getPrometheusConfiguration());

        PrometheusConfiguration config5 = new PrometheusConfiguration();
        config5.setEnabled(false);
        config.setPrometheusConfiguration(config5);
        assertFalse(config.getPrometheusConfiguration().isEnabled());
    }
}

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

import org.finos.legend.depot.core.server.error.configuration.ExceptionMapperConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_getExceptionMapperConfigurationTest
{
    /**
     * Reflection is necessary to test getExceptionMapperConfiguration() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * getter method. The method under test is an accessor with default value logic that returns
     * a new ExceptionMapperConfiguration() if the field is null.
     *
     * Using reflection allows us to set the private 'exceptionMapperConfiguration' field directly
     * and verify that getExceptionMapperConfiguration() returns exactly what was set when non-null,
     * or creates a new default instance when null, without requiring full Dropwizard framework
     * initialization including YAML parsing, validation, and dependency injection setup.
     */

    private void setExceptionMapperConfigurationField(ServerConfiguration config, ExceptionMapperConfiguration mapperConfig) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("exceptionMapperConfiguration");
        field.setAccessible(true);
        field.set(config, mapperConfig);
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration returns configured value when set")
    void testGetExceptionMapperConfigurationReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(true);
        setExceptionMapperConfigurationField(config, mapperConfig);

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result, "ExceptionMapperConfiguration should not be null");
        assertTrue(result.includeStackTrace(), "Stack trace should be included");
        assertSame(mapperConfig, result, "Should return the same instance that was set");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration returns default when field is null")
    void testGetExceptionMapperConfigurationReturnsDefaultWhenNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setExceptionMapperConfigurationField(config, null);

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result, "ExceptionMapperConfiguration should not be null even when field is null");
        assertFalse(result.includeStackTrace(), "Default should not include stack trace");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration creates new instance each time when null")
    void testGetExceptionMapperConfigurationCreatesNewInstanceEachTime() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setExceptionMapperConfigurationField(config, null);

        // Act
        ExceptionMapperConfiguration result1 = config.getExceptionMapperConfiguration();
        ExceptionMapperConfiguration result2 = config.getExceptionMapperConfiguration();
        ExceptionMapperConfiguration result3 = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertNotSame(result1, result2, "Should create new instances when field is null");
        assertNotSame(result2, result3, "Should create new instances when field is null");
        assertNotSame(result1, result3, "Should create new instances when field is null");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration is idempotent when field is set")
    void testGetExceptionMapperConfigurationIsIdempotentWhenSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(true);
        setExceptionMapperConfigurationField(config, mapperConfig);

        // Act
        ExceptionMapperConfiguration result1 = config.getExceptionMapperConfiguration();
        ExceptionMapperConfiguration result2 = config.getExceptionMapperConfiguration();
        ExceptionMapperConfiguration result3 = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance when field is set");
        assertSame(result2, result3, "Multiple calls should return the same instance when field is set");
        assertTrue(result1.includeStackTrace());
        assertTrue(result2.includeStackTrace());
        assertTrue(result3.includeStackTrace());
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration with includeStackTrace disabled")
    void testGetExceptionMapperConfigurationWithStackTraceDisabled() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(false);
        setExceptionMapperConfigurationField(config, mapperConfig);

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result, "ExceptionMapperConfiguration should not be null");
        assertFalse(result.includeStackTrace(), "Stack trace should not be included");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration with includeStackTrace enabled")
    void testGetExceptionMapperConfigurationWithStackTraceEnabled() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(true);
        setExceptionMapperConfigurationField(config, mapperConfig);

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result, "ExceptionMapperConfiguration should not be null");
        assertTrue(result.includeStackTrace(), "Stack trace should be included");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration returns same reference when field is set")
    void testGetExceptionMapperConfigurationReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        setExceptionMapperConfigurationField(config, mapperConfig);

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertSame(mapperConfig, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration with different configurations")
    void testGetExceptionMapperConfigurationWithDifferentConfigurations() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        ExceptionMapperConfiguration mapperConfig1 = new ExceptionMapperConfiguration();
        mapperConfig1.setIncludeStackTrace(true);

        ExceptionMapperConfiguration mapperConfig2 = new ExceptionMapperConfiguration();
        mapperConfig2.setIncludeStackTrace(false);

        setExceptionMapperConfigurationField(config1, mapperConfig1);
        setExceptionMapperConfigurationField(config2, mapperConfig2);

        // Act
        ExceptionMapperConfiguration result1 = config1.getExceptionMapperConfiguration();
        ExceptionMapperConfiguration result2 = config2.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertTrue(result1.includeStackTrace(), "Config1 should include stack trace");
        assertFalse(result2.includeStackTrace(), "Config2 should not include stack trace");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration default behavior matches ExceptionMapperConfiguration defaults")
    void testGetExceptionMapperConfigurationDefaultBehavior() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setExceptionMapperConfigurationField(config, null);
        ExceptionMapperConfiguration directDefault = new ExceptionMapperConfiguration();

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(directDefault.includeStackTrace(), result.includeStackTrace(),
                "Default from getter should match default from constructor");
        assertFalse(result.includeStackTrace(), "Default should be false");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration after setting field to null returns default")
    void testGetExceptionMapperConfigurationAfterSettingToNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // First set a non-null value
        ExceptionMapperConfiguration initialConfig = new ExceptionMapperConfiguration();
        initialConfig.setIncludeStackTrace(true);
        setExceptionMapperConfigurationField(config, initialConfig);

        ExceptionMapperConfiguration firstResult = config.getExceptionMapperConfiguration();
        assertTrue(firstResult.includeStackTrace(), "Initially should include stack trace");

        // Now set to null
        setExceptionMapperConfigurationField(config, null);

        // Act
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();

        // Assert
        assertNotNull(result, "Should return non-null default when field is null");
        assertFalse(result.includeStackTrace(), "Should return default with stack trace disabled");
        assertNotSame(initialConfig, result, "Should not return the previous instance");
    }

    @Test
    @DisplayName("Test getExceptionMapperConfiguration preserves mutable state when field is set")
    void testGetExceptionMapperConfigurationPreservesMutableState() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(false);
        setExceptionMapperConfigurationField(config, mapperConfig);

        // Act
        ExceptionMapperConfiguration retrieved = config.getExceptionMapperConfiguration();
        assertFalse(retrieved.includeStackTrace(), "Initially should be false");

        // Modify the original
        mapperConfig.setIncludeStackTrace(true);

        // Assert
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace(),
                "Changes to original object should be reflected");
    }
}

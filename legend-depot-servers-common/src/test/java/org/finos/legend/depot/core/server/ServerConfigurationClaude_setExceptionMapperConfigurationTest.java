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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_setExceptionMapperConfigurationTest
{
    @Test
    @DisplayName("Test setExceptionMapperConfiguration sets the configuration")
    void testSetExceptionMapperConfigurationSetsTheConfiguration() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(true);

        // Act
        config.setExceptionMapperConfiguration(mapperConfig);

        // Assert
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();
        assertNotNull(result, "ExceptionMapperConfiguration should not be null after setting");
        assertTrue(result.includeStackTrace(), "Stack trace should be included");
        assertSame(mapperConfig, result, "Should return the same instance that was set");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration with null")
    void testSetExceptionMapperConfigurationWithNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act
        config.setExceptionMapperConfiguration(null);

        // Assert
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();
        assertNotNull(result, "getExceptionMapperConfiguration should return default when field is null");
        assertFalse(result.includeStackTrace(), "Default should not include stack trace");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration overwrites previous value")
    void testSetExceptionMapperConfigurationOverwritesPreviousValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        ExceptionMapperConfiguration firstConfig = new ExceptionMapperConfiguration();
        firstConfig.setIncludeStackTrace(true);

        ExceptionMapperConfiguration secondConfig = new ExceptionMapperConfiguration();
        secondConfig.setIncludeStackTrace(false);

        // Act
        config.setExceptionMapperConfiguration(firstConfig);
        ExceptionMapperConfiguration firstResult = config.getExceptionMapperConfiguration();

        config.setExceptionMapperConfiguration(secondConfig);
        ExceptionMapperConfiguration secondResult = config.getExceptionMapperConfiguration();

        // Assert
        assertSame(firstConfig, firstResult, "First call should return first config");
        assertSame(secondConfig, secondResult, "Second call should return second config");
        assertTrue(firstResult.includeStackTrace());
        assertFalse(secondResult.includeStackTrace());
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration with includeStackTrace enabled")
    void testSetExceptionMapperConfigurationWithStackTraceEnabled() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(true);

        // Act
        config.setExceptionMapperConfiguration(mapperConfig);

        // Assert
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();
        assertNotNull(result, "ExceptionMapperConfiguration should not be null");
        assertTrue(result.includeStackTrace(), "Stack trace should be included");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration with includeStackTrace disabled")
    void testSetExceptionMapperConfigurationWithStackTraceDisabled() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(false);

        // Act
        config.setExceptionMapperConfiguration(mapperConfig);

        // Assert
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();
        assertNotNull(result, "ExceptionMapperConfiguration should not be null");
        assertFalse(result.includeStackTrace(), "Stack trace should not be included");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration can be called multiple times")
    void testSetExceptionMapperConfigurationCanBeCalledMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - First call
        ExceptionMapperConfiguration first = new ExceptionMapperConfiguration();
        first.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(first);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace());

        // Act & Assert - Second call
        ExceptionMapperConfiguration second = new ExceptionMapperConfiguration();
        second.setIncludeStackTrace(false);
        config.setExceptionMapperConfiguration(second);
        assertFalse(config.getExceptionMapperConfiguration().includeStackTrace());

        // Act & Assert - Third call with null
        config.setExceptionMapperConfiguration(null);
        ExceptionMapperConfiguration thirdResult = config.getExceptionMapperConfiguration();
        assertNotNull(thirdResult, "Should return default when set to null");
        assertFalse(thirdResult.includeStackTrace(), "Default should not include stack trace");

        // Act & Assert - Fourth call with new config
        ExceptionMapperConfiguration fourth = new ExceptionMapperConfiguration();
        fourth.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(fourth);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace());
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration with same reference multiple times")
    void testSetExceptionMapperConfigurationWithSameReferenceMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(true);

        // Act
        config.setExceptionMapperConfiguration(mapperConfig);
        config.setExceptionMapperConfiguration(mapperConfig);
        config.setExceptionMapperConfiguration(mapperConfig);

        // Assert
        assertSame(mapperConfig, config.getExceptionMapperConfiguration(), "Should still reference the same config");
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace());
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration maintains reference to mutable object")
    void testSetExceptionMapperConfigurationMaintainsReferenceToMutableObject() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        mapperConfig.setIncludeStackTrace(false);

        // Act
        config.setExceptionMapperConfiguration(mapperConfig);
        ExceptionMapperConfiguration retrieved = config.getExceptionMapperConfiguration();

        // Assert
        assertSame(mapperConfig, retrieved, "Should maintain the exact same reference");

        // Verify that modifications to the original object affect the configuration
        mapperConfig.setIncludeStackTrace(true);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace(),
                "Changes to original object should be reflected");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration toggles between enabled and disabled")
    void testSetExceptionMapperConfigurationToggles() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - Set enabled
        ExceptionMapperConfiguration enabledConfig = new ExceptionMapperConfiguration();
        enabledConfig.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(enabledConfig);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace(), "Should be enabled");

        // Act & Assert - Set disabled
        ExceptionMapperConfiguration disabledConfig = new ExceptionMapperConfiguration();
        disabledConfig.setIncludeStackTrace(false);
        config.setExceptionMapperConfiguration(disabledConfig);
        assertFalse(config.getExceptionMapperConfiguration().includeStackTrace(), "Should be disabled");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration after null returns new instance each time from getter")
    void testSetExceptionMapperConfigurationAfterNullReturnsNewInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Set to non-null
        ExceptionMapperConfiguration nonNullConfig = new ExceptionMapperConfiguration();
        nonNullConfig.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(nonNullConfig);
        assertSame(nonNullConfig, config.getExceptionMapperConfiguration());

        // Act - Set to null
        config.setExceptionMapperConfiguration(null);

        // Assert - Multiple calls to getter should return new instances
        ExceptionMapperConfiguration result1 = config.getExceptionMapperConfiguration();
        ExceptionMapperConfiguration result2 = config.getExceptionMapperConfiguration();

        assertNotNull(result1, "Should return non-null default");
        assertNotNull(result2, "Should return non-null default");
        assertNotSame(result1, result2, "Should create new instances each time when field is null");
        assertFalse(result1.includeStackTrace(), "Default should not include stack trace");
        assertFalse(result2.includeStackTrace(), "Default should not include stack trace");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration with default constructor values")
    void testSetExceptionMapperConfigurationWithDefaultConstructorValues() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ExceptionMapperConfiguration mapperConfig = new ExceptionMapperConfiguration();
        // Don't set any values, use defaults

        // Act
        config.setExceptionMapperConfiguration(mapperConfig);

        // Assert
        ExceptionMapperConfiguration result = config.getExceptionMapperConfiguration();
        assertNotNull(result, "ExceptionMapperConfiguration should not be null");
        assertSame(mapperConfig, result, "Should return the set instance");
        assertFalse(result.includeStackTrace(), "Default constructor should set includeStackTrace to false");
    }

    @Test
    @DisplayName("Test setExceptionMapperConfiguration multiple times with different values")
    void testSetExceptionMapperConfigurationMultipleTimesWithDifferentValues() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Test cycle: true -> false -> true -> null -> true
        ExceptionMapperConfiguration config1 = new ExceptionMapperConfiguration();
        config1.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(config1);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace());

        ExceptionMapperConfiguration config2 = new ExceptionMapperConfiguration();
        config2.setIncludeStackTrace(false);
        config.setExceptionMapperConfiguration(config2);
        assertFalse(config.getExceptionMapperConfiguration().includeStackTrace());

        ExceptionMapperConfiguration config3 = new ExceptionMapperConfiguration();
        config3.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(config3);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace());

        config.setExceptionMapperConfiguration(null);
        assertFalse(config.getExceptionMapperConfiguration().includeStackTrace());

        ExceptionMapperConfiguration config5 = new ExceptionMapperConfiguration();
        config5.setIncludeStackTrace(true);
        config.setExceptionMapperConfiguration(config5);
        assertTrue(config.getExceptionMapperConfiguration().includeStackTrace());
    }
}

//  Copyright 2024 Goldman Sachs
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

package org.finos.legend.depot.core.server.error.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExceptionMapperConfiguration class.
 *
 * This test class tests all methods in ExceptionMapperConfiguration including:
 * - Default constructor initialization
 * - includeStackTrace() getter method
 * - setIncludeStackTrace(boolean) setter method
 *
 * The class is a simple configuration POJO, so tests focus on ensuring
 * proper default values and state management through getter/setter methods.
 */
class ExceptionMapperConfigurationClaudeTest
{
    // Tests for constructor

    @Test
    @DisplayName("Test constructor initializes with default value false")
    void testConstructorInitializesWithDefaultValue()
    {
        // Act
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();

        // Assert
        assertNotNull(config, "Configuration should not be null");
        assertFalse(config.includeStackTrace(), "Default includeStackTrace should be false");
    }

    // Tests for includeStackTrace() getter

    @Test
    @DisplayName("Test includeStackTrace returns false by default")
    void testIncludeStackTraceReturnsFalseByDefault()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();

        // Act
        boolean result = config.includeStackTrace();

        // Assert
        assertFalse(result, "includeStackTrace should return false by default");
    }

    @Test
    @DisplayName("Test includeStackTrace returns true after being set to true")
    void testIncludeStackTraceReturnsTrueAfterSetToTrue()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();
        config.setIncludeStackTrace(true);

        // Act
        boolean result = config.includeStackTrace();

        // Assert
        assertTrue(result, "includeStackTrace should return true after being set to true");
    }

    @Test
    @DisplayName("Test includeStackTrace returns false after being set to false")
    void testIncludeStackTraceReturnsFalseAfterSetToFalse()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();
        config.setIncludeStackTrace(false);

        // Act
        boolean result = config.includeStackTrace();

        // Assert
        assertFalse(result, "includeStackTrace should return false after being set to false");
    }

    // Tests for setIncludeStackTrace(boolean) setter

    @Test
    @DisplayName("Test setIncludeStackTrace sets value to true")
    void testSetIncludeStackTraceSetsValueToTrue()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();

        // Act
        config.setIncludeStackTrace(true);

        // Assert
        assertTrue(config.includeStackTrace(), "includeStackTrace should be true after being set");
    }

    @Test
    @DisplayName("Test setIncludeStackTrace sets value to false")
    void testSetIncludeStackTraceSetsValueToFalse()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();
        config.setIncludeStackTrace(true); // Set to true first

        // Act
        config.setIncludeStackTrace(false); // Then set to false

        // Assert
        assertFalse(config.includeStackTrace(), "includeStackTrace should be false after being set");
    }

    @Test
    @DisplayName("Test setIncludeStackTrace can toggle value multiple times")
    void testSetIncludeStackTraceCanToggleMultipleTimes()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();

        // Act & Assert - Toggle multiple times
        config.setIncludeStackTrace(true);
        assertTrue(config.includeStackTrace(), "Should be true after first set");

        config.setIncludeStackTrace(false);
        assertFalse(config.includeStackTrace(), "Should be false after second set");

        config.setIncludeStackTrace(true);
        assertTrue(config.includeStackTrace(), "Should be true after third set");

        config.setIncludeStackTrace(true);
        assertTrue(config.includeStackTrace(), "Should remain true after setting to same value");

        config.setIncludeStackTrace(false);
        assertFalse(config.includeStackTrace(), "Should be false after final set");
    }

    // Tests for state consistency

    @Test
    @DisplayName("Test multiple instances are independent")
    void testMultipleInstancesAreIndependent()
    {
        // Arrange
        ExceptionMapperConfiguration config1 = new ExceptionMapperConfiguration();
        ExceptionMapperConfiguration config2 = new ExceptionMapperConfiguration();

        // Act
        config1.setIncludeStackTrace(true);
        config2.setIncludeStackTrace(false);

        // Assert
        assertTrue(config1.includeStackTrace(), "Config1 should be true");
        assertFalse(config2.includeStackTrace(), "Config2 should be false");
        assertNotEquals(config1.includeStackTrace(), config2.includeStackTrace(),
            "Different instances should maintain independent state");
    }

    @Test
    @DisplayName("Test setter does not affect other instances")
    void testSetterDoesNotAffectOtherInstances()
    {
        // Arrange
        ExceptionMapperConfiguration config1 = new ExceptionMapperConfiguration();
        ExceptionMapperConfiguration config2 = new ExceptionMapperConfiguration();
        ExceptionMapperConfiguration config3 = new ExceptionMapperConfiguration();

        // Act
        config1.setIncludeStackTrace(true);
        config2.setIncludeStackTrace(false);
        // config3 remains at default

        // Assert
        assertTrue(config1.includeStackTrace(), "Config1 should be true");
        assertFalse(config2.includeStackTrace(), "Config2 should be false");
        assertFalse(config3.includeStackTrace(), "Config3 should remain at default false");
    }

    // Edge case tests

    @Test
    @DisplayName("Test setting same value multiple times maintains state")
    void testSettingSameValueMultipleTimesMaintainsState()
    {
        // Arrange
        ExceptionMapperConfiguration config = new ExceptionMapperConfiguration();

        // Act - Set to true multiple times
        config.setIncludeStackTrace(true);
        config.setIncludeStackTrace(true);
        config.setIncludeStackTrace(true);

        // Assert
        assertTrue(config.includeStackTrace(), "Should remain true");

        // Act - Set to false multiple times
        config.setIncludeStackTrace(false);
        config.setIncludeStackTrace(false);
        config.setIncludeStackTrace(false);

        // Assert
        assertFalse(config.includeStackTrace(), "Should remain false");
    }
}

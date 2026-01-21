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

package org.finos.legend.depot.services.api.metrics.query;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

class VoidQueryMetricsRegistryClaudeTest 

{

    /**
     * Test {@link VoidQueryMetricsRegistry#VoidQueryMetricsRegistry()}.
     *
     * <p>This test verifies that the constructor creates a valid instance.
     */
    @Test
    @DisplayName("Test constructor creates valid instance")
    void testConstructor()
  {
        // Act
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Assert
        assertNotNull(registry, "Constructor should create a non-null instance");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that the record method executes without errors
     * with valid parameters.
     */
    @Test
    @DisplayName("Test record() with valid parameters")
    void testRecordWithValidParameters()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        Date date = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> registry.record(groupId, artifactId, versionId, date),
            "record() should not throw any exception with valid parameters");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that the record method handles null values without errors.
     */
    @Test
    @DisplayName("Test record() with null parameters")
    void testRecordWithNullParameters()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Act & Assert
        assertDoesNotThrow(() -> registry.record(null, null, null, null),
            "record() should not throw any exception with null parameters");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that the record method handles empty strings without errors.
     */
    @Test
    @DisplayName("Test record() with empty strings")
    void testRecordWithEmptyStrings()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        Date date = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> registry.record("", "", "", date),
            "record() should not throw any exception with empty string parameters");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that the record method handles special characters without errors.
     */
    @Test
    @DisplayName("Test record() with special characters")
    void testRecordWithSpecialCharacters()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        String groupId = "org.example.@#$%^&*()";
        String artifactId = "test-artifact-!@#$%";
        String versionId = "1.0.0-SNAPSHOT+build.123";
        Date date = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> registry.record(groupId, artifactId, versionId, date),
            "record() should not throw any exception with special character parameters");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that the record method handles very long strings without errors.
     */
    @Test
    @DisplayName("Test record() with long strings")
    void testRecordWithLongStrings()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        String longString = "a".repeat(10000);
        Date date = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> registry.record(longString, longString, longString, date),
            "record() should not throw any exception with very long string parameters");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that multiple calls to record do not cause errors.
     */
    @Test
    @DisplayName("Test multiple calls to record()")
    void testMultipleRecordCalls()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        Date date = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> 
        {
            for (int i = 0; i < 100; i++) {
                registry.record("group" + i, "artifact" + i, "version" + i, date);
            }
        }, "Multiple calls to record() should not throw any exception");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#record(String, String, String, Date)}.
     *
     * <p>This test verifies that the record method handles date with extreme values.
     */
    @Test
    @DisplayName("Test record() with extreme date values")
    void testRecordWithExtremeDates()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Act & Assert
        assertDoesNotThrow(() -> 
        {
            registry.record(groupId, artifactId, versionId, new Date(0)); // Epoch
            registry.record(groupId, artifactId, versionId, new Date(Long.MAX_VALUE)); // Far future
        }, "record() should not throw any exception with extreme date values");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#findFirst()}.
     *
     * <p>This test verifies that findFirst() always returns an empty Optional.
     */
    @Test
    @DisplayName("Test findFirst() returns empty Optional")
    void testFindFirstReturnsEmpty()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Act
        Optional<?> result = registry.findFirst();

        // Assert
        assertNotNull(result, "findFirst() should return a non-null Optional");
        assertFalse(result.isPresent(), "findFirst() should return an empty Optional");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#findFirst()}.
     *
     * <p>This test verifies that findFirst() returns empty even after record calls.
     */
    @Test
    @DisplayName("Test findFirst() returns empty after record calls")
    void testFindFirstReturnsEmptyAfterRecordCalls()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        Date date = new Date();

        // Act - record some data
        registry.record("org.example", "test-artifact", "1.0.0", date);
        registry.record("org.example", "test-artifact", "2.0.0", date);
        registry.record("org.example", "test-artifact", "3.0.0", date);

        Optional<?> result = registry.findFirst();

        // Assert
        assertNotNull(result, "findFirst() should return a non-null Optional");
        assertFalse(result.isPresent(), "findFirst() should return an empty Optional even after record calls");
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#findFirst()}.
     *
     * <p>This test verifies that multiple calls to findFirst() consistently return empty.
     */
    @Test
    @DisplayName("Test multiple calls to findFirst() return empty")
    void testMultipleFindFirstCallsReturnEmpty()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Act & Assert
        for (int i = 0; i < 10; i++) {
            Optional<?> result = registry.findFirst();
            assertFalse(result.isPresent(), "findFirst() should always return an empty Optional");
        }
    }

    /**
     * Test {@link VoidQueryMetricsRegistry#findFirst()}.
     *
     * <p>This test verifies that findFirst() does not throw any exceptions.
     */
    @Test
    @DisplayName("Test findFirst() does not throw exceptions")
    void testFindFirstDoesNotThrow()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Act & Assert
        assertDoesNotThrow(() -> registry.findFirst(),
            "findFirst() should not throw any exception");
    }

    /**
     * Test interaction between record and findFirst methods.
     *
     * <p>This test verifies that the void implementation maintains its contract
     * even when methods are called in different orders.
     */
    @Test
    @DisplayName("Test record and findFirst interaction")
    void testRecordAndFindFirstInteraction()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();
        Date date = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> 
        {
            // Test various call patterns
            registry.record("group1", "artifact1", "1.0.0", date);
            assertFalse(registry.findFirst().isPresent());

            registry.record("group2", "artifact2", "2.0.0", date);
            assertFalse(registry.findFirst().isPresent());

            assertFalse(registry.findFirst().isPresent());
            registry.record("group3", "artifact3", "3.0.0", date);
            assertFalse(registry.findFirst().isPresent());
        }, "Mixing record and findFirst calls should not cause any issues");
    }

    /**
     * Test that VoidQueryMetricsRegistry implements QueryMetricsRegistry correctly.
     *
     * <p>This test verifies that VoidQueryMetricsRegistry can be used as a QueryMetricsRegistry.
     */
    @Test
    @DisplayName("Test implements QueryMetricsRegistry interface")
    void testImplementsInterface()
  {
        // Arrange & Act
        QueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Assert
        assertNotNull(registry, "Should be able to assign to interface type");
        assertDoesNotThrow(() -> 
        {
            registry.record("group", "artifact", "version", new Date());
            registry.findFirst();
        }, "Should be able to call interface methods");
    }

    /**
     * Test that VoidQueryMetricsRegistry supports the default interface method.
     *
     * <p>This test verifies that the 3-parameter record method (from the interface default)
     * works correctly.
     */
    @Test
    @DisplayName("Test default interface method record()")
    void testDefaultInterfaceMethod()
  {
        // Arrange
        VoidQueryMetricsRegistry registry = new VoidQueryMetricsRegistry();

        // Act & Assert
        assertDoesNotThrow(() -> registry.record("group", "artifact", "version"),
            "Default interface method should work without errors");
    }
}

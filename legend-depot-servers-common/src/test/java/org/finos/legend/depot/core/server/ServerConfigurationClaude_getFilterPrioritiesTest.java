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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_getFilterPrioritiesTest
{
    /**
     * Reflection is necessary to test getFilterPriorities() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'filterPriorities' field directly and
     * verify that getFilterPriorities() returns exactly what was set, without requiring
     * full Dropwizard framework initialization including YAML parsing, validation, and
     * dependency injection setup.
     */

    private void setFilterPrioritiesField(ServerConfiguration config, Map<String, Integer> filterPriorities) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("filterPriorities");
        field.setAccessible(true);
        field.set(config, filterPriorities);
    }

    @Test
    @DisplayName("Test getFilterPriorities returns configured value")
    void testGetFilterPrioritiesReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("authentication", 1);
        priorities.put("authorization", 2);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(2, result.size(), "Should have 2 entries");
        assertEquals(1, result.get("authentication"), "Authentication priority should be 1");
        assertEquals(2, result.get("authorization"), "Authorization priority should be 2");
        assertSame(priorities, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getFilterPriorities returns null when not set")
    void testGetFilterPrioritiesReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setFilterPrioritiesField(config, null);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNull(result, "Filter priorities should be null when not set");
    }

    @Test
    @DisplayName("Test getFilterPriorities is idempotent")
    void testGetFilterPrioritiesIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("cors", 10);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result1 = config.getFilterPriorities();
        Map<String, Integer> result2 = config.getFilterPriorities();
        Map<String, Integer> result3 = config.getFilterPriorities();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertEquals(10, result1.get("cors"));
        assertEquals(10, result2.get("cors"));
        assertEquals(10, result3.get("cors"));
    }

    @Test
    @DisplayName("Test getFilterPriorities with empty map")
    void testGetFilterPrioritiesWithEmptyMap() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertTrue(result.isEmpty(), "Filter priorities should be empty");
    }

    @Test
    @DisplayName("Test getFilterPriorities with single entry")
    void testGetFilterPrioritiesWithSingleEntry() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("logging", 5);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(1, result.size(), "Should have 1 entry");
        assertEquals(5, result.get("logging"), "Logging priority should be 5");
    }

    @Test
    @DisplayName("Test getFilterPriorities with multiple entries")
    void testGetFilterPrioritiesWithMultipleEntries() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("authentication", 1);
        priorities.put("authorization", 2);
        priorities.put("cors", 3);
        priorities.put("logging", 4);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(4, result.size(), "Should have 4 entries");
        assertEquals(1, result.get("authentication"));
        assertEquals(2, result.get("authorization"));
        assertEquals(3, result.get("cors"));
        assertEquals(4, result.get("logging"));
    }

    @Test
    @DisplayName("Test getFilterPriorities with different configurations maintains independence")
    void testGetFilterPrioritiesIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        Map<String, Integer> priorities1 = new HashMap<>();
        priorities1.put("filter1", 10);

        Map<String, Integer> priorities2 = new HashMap<>();
        priorities2.put("filter2", 20);

        setFilterPrioritiesField(config1, priorities1);
        setFilterPrioritiesField(config2, priorities2);

        // Act
        Map<String, Integer> result1 = config1.getFilterPriorities();
        Map<String, Integer> result2 = config2.getFilterPriorities();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertEquals(10, result1.get("filter1"));
        assertEquals(20, result2.get("filter2"));
        assertNull(result1.get("filter2"), "Config1 should not have filter2");
        assertNull(result2.get("filter1"), "Config2 should not have filter1");
    }

    @Test
    @DisplayName("Test getFilterPriorities returns exact same object reference")
    void testGetFilterPrioritiesReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("test", 100);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertSame(priorities, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getFilterPriorities maintains reference to mutable map")
    void testGetFilterPrioritiesMaintainsReferenceToMutableMap() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("initial", 1);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> retrieved = config.getFilterPriorities();

        // Assert
        assertSame(priorities, retrieved, "Should maintain the exact same reference");

        // Verify that modifications to the original map affect the configuration
        priorities.put("added", 2);
        assertEquals(2, config.getFilterPriorities().size(), "Changes to original map should be reflected");
        assertEquals(2, config.getFilterPriorities().get("added"));
    }

    @Test
    @DisplayName("Test getFilterPriorities with negative priority values")
    void testGetFilterPrioritiesWithNegativePriorities() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("early", -10);
        priorities.put("late", 10);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(-10, result.get("early"), "Should handle negative priorities");
        assertEquals(10, result.get("late"), "Should handle positive priorities");
    }

    @Test
    @DisplayName("Test getFilterPriorities with zero priority values")
    void testGetFilterPrioritiesWithZeroPriorities() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("default", 0);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(0, result.get("default"), "Should handle zero priority");
    }

    @Test
    @DisplayName("Test getFilterPriorities with unmodifiable map")
    void testGetFilterPrioritiesWithUnmodifiableMap() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> mutableMap = new HashMap<>();
        mutableMap.put("filter", 5);
        Map<String, Integer> unmodifiableMap = Collections.unmodifiableMap(mutableMap);
        setFilterPrioritiesField(config, unmodifiableMap);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(5, result.get("filter"));
        assertSame(unmodifiableMap, result, "Should return the unmodifiable map");
    }

    @Test
    @DisplayName("Test getFilterPriorities with large priority values")
    void testGetFilterPrioritiesWithLargePriorities() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("highest", Integer.MAX_VALUE);
        priorities.put("lowest", Integer.MIN_VALUE);
        setFilterPrioritiesField(config, priorities);

        // Act
        Map<String, Integer> result = config.getFilterPriorities();

        // Assert
        assertNotNull(result, "Filter priorities should not be null");
        assertEquals(Integer.MAX_VALUE, result.get("highest"), "Should handle max integer");
        assertEquals(Integer.MIN_VALUE, result.get("lowest"), "Should handle min integer");
    }
}

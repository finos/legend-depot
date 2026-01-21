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

package org.finos.legend.depot.store.model.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StoredEntityClaudeTest 

{

    @Test
    @DisplayName("getEntityAttributes should return null when created with 3-arg constructor")
    void testGetEntityAttributes_withThreeArgConstructor_returnsNull()
  {
        // Arrange
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNull(result, "Entity attributes should be null when not provided in constructor");
    }

    @Test
    @DisplayName("getEntityAttributes should return empty map when created with empty map")
    void testGetEntityAttributes_withEmptyMap_returnsEmptyMap()
  {
        // Arrange
        Map<String, Object> emptyMap = new HashMap<>();
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, emptyMap);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result, "Entity attributes should not be null");
        assertTrue(result.isEmpty(), "Entity attributes should be empty");
        assertSame(emptyMap, result, "Should return the same map instance");
    }

    @Test
    @DisplayName("getEntityAttributes should return map with single entry")
    void testGetEntityAttributes_withSingleEntry_returnsSingleEntry()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result, "Entity attributes should not be null");
        assertEquals(1, result.size(), "Entity attributes should have one entry");
        assertEquals("value1", result.get("key1"), "Should contain the correct key-value pair");
    }

    @Test
    @DisplayName("getEntityAttributes should return map with multiple entries")
    void testGetEntityAttributes_withMultipleEntries_returnsAllEntries()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", 123);
        attributes.put("key3", true);
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result, "Entity attributes should not be null");
        assertEquals(3, result.size(), "Entity attributes should have three entries");
        assertEquals("value1", result.get("key1"));
        assertEquals(123, result.get("key2"));
        assertEquals(true, result.get("key3"));
    }

    @Test
    @DisplayName("getEntityAttributes should handle map with null values")
    void testGetEntityAttributes_withNullValues_handlesNullValues()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", null);
        attributes.put("key2", "value2");
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result, "Entity attributes should not be null");
        assertEquals(2, result.size(), "Entity attributes should have two entries");
        assertTrue(result.containsKey("key1"), "Should contain key1");
        assertNull(result.get("key1"), "key1 should have null value");
        assertEquals("value2", result.get("key2"));
    }

    @Test
    @DisplayName("getEntityAttributes should work with unmodifiable map")
    void testGetEntityAttributes_withUnmodifiableMap_returnsMap()
  {
        // Arrange
        Map<String, Object> attributes = Collections.singletonMap("key1", "value1");
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result, "Entity attributes should not be null");
        assertEquals(1, result.size(), "Entity attributes should have one entry");
        assertEquals("value1", result.get("key1"));
    }

    @Test
    @DisplayName("getEntityAttributes should work with StoredEntityReference")
    void testGetEntityAttributes_withStoredEntityReference_returnsAttributes()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("refKey", "refValue");
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "reference", attributes);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result, "Entity attributes should not be null");
        assertEquals(1, result.size(), "Entity attributes should have one entry");
        assertEquals("refValue", result.get("refKey"));
    }

    @Test
    @DisplayName("getEntityAttributes should return null for StoredEntityReference with 3-arg constructor")
    void testGetEntityAttributes_withStoredEntityReferenceThreeArgConstructor_returnsNull()
  {
        // Arrange
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNull(result, "Entity attributes should be null when not provided in constructor");
    }

    @Test
    @DisplayName("getId should return empty string for StoredEntityData")
    void testGetId_forStoredEntityData_returnsEmptyString()
  {
        // Arrange
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");

        // Act
        String result = entity.getId();

        // Assert
        assertNotNull(result, "getId should not return null");
        assertEquals("", result, "getId should return empty string");
        assertTrue(result.isEmpty(), "getId should return empty string");
    }

    @Test
    @DisplayName("getId should return empty string for StoredEntityReference")
    void testGetId_forStoredEntityReference_returnsEmptyString()
  {
        // Arrange
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        // Act
        String result = entity.getId();

        // Assert
        assertNotNull(result, "getId should not return null");
        assertEquals("", result, "getId should return empty string");
        assertTrue(result.isEmpty(), "getId should return empty string");
    }

    @Test
    @DisplayName("getId should return empty string regardless of entityAttributes")
    void testGetId_withEntityAttributes_returnsEmptyString()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "someId");
        attributes.put("key", "value");
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        String result = entity.getId();

        // Assert
        assertNotNull(result, "getId should not return null");
        assertEquals("", result, "getId should return empty string regardless of entityAttributes");
    }

    @Test
    @DisplayName("getId should return empty string for multiple calls")
    void testGetId_multipleCalls_consistentlyReturnsEmptyString()
  {
        // Arrange
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");

        // Act
        String result1 = entity.getId();
        String result2 = entity.getId();
        String result3 = entity.getId();

        // Assert
        assertEquals("", result1);
        assertEquals("", result2);
        assertEquals("", result3);
        assertSame(result1, result2, "Should return the same instance");
        assertSame(result2, result3, "Should return the same instance");
    }

    @Test
    @DisplayName("getEntityAttributes should return same instance on multiple calls")
    void testGetEntityAttributes_multipleCalls_returnsSameInstance()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key", "value");
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        Map<String, ?> result1 = entity.getEntityAttributes();
        Map<String, ?> result2 = entity.getEntityAttributes();

        // Assert
        assertSame(result1, result2, "Should return the same map instance on multiple calls");
        assertSame(attributes, result1, "Should return the original map instance");
    }

    @Test
    @DisplayName("getEntityAttributes should allow complex object values")
    void testGetEntityAttributes_withComplexValues_returnsComplexValues()
  {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        Map<String, String> nestedMap = new HashMap<>();
        nestedMap.put("nested", "value");
        attributes.put("nestedMap", nestedMap);
        attributes.put("list", java.util.Arrays.asList("item1", "item2"));

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        // Act
        Map<String, ?> result = entity.getEntityAttributes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get("nestedMap") instanceof Map);
        assertTrue(result.get("list") instanceof java.util.List);
    }
}

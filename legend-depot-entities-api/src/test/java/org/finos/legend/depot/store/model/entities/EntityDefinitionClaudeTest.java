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

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityDefinitionClaudeTest


{
    @Test
    void testConstructorWithValidValues()
  {
        Map<String, Object> content = new HashMap<>();
        content.put("key1", "value1");
        content.put("key2", 42);

        EntityDefinition entity = new EntityDefinition("test.path", "classifier.path", content);

        assertEquals("test.path", entity.getPath());
        assertEquals("classifier.path", entity.getClassifierPath());
        assertSame(content, entity.getContent());
        assertEquals(2, entity.getContent().size());
    }

    @Test
    void testConstructorWithNullValues()
  {
        EntityDefinition entity = new EntityDefinition(null, null, null);

        assertNull(entity.getPath());
        assertNull(entity.getClassifierPath());
        assertNull(entity.getContent());
    }

    @Test
    void testConstructorWithEmptyStringsAndEmptyMap()
  {
        Map<String, Object> content = new HashMap<>();
        EntityDefinition entity = new EntityDefinition("", "", content);

        assertEquals("", entity.getPath());
        assertEquals("", entity.getClassifierPath());
        assertSame(content, entity.getContent());
        assertTrue(entity.getContent().isEmpty());
    }

    @Test
    void testGetPath()
  {
        EntityDefinition entity = new EntityDefinition("my.path", "classifier", new HashMap<>());
        assertEquals("my.path", entity.getPath());
    }

    @Test
    void testGetPathWithNull()
  {
        EntityDefinition entity = new EntityDefinition(null, "classifier", new HashMap<>());
        assertNull(entity.getPath());
    }

    @Test
    void testGetClassifierPath()
  {
        EntityDefinition entity = new EntityDefinition("path", "my.classifier", new HashMap<>());
        assertEquals("my.classifier", entity.getClassifierPath());
    }

    @Test
    void testGetClassifierPathWithNull()
  {
        EntityDefinition entity = new EntityDefinition("path", null, new HashMap<>());
        assertNull(entity.getClassifierPath());
    }

    @Test
    void testSetClassifierPath()
  {
        EntityDefinition entity = new EntityDefinition("path", "original", new HashMap<>());
        assertEquals("original", entity.getClassifierPath());

        entity.setClassifierPath("modified");
        assertEquals("modified", entity.getClassifierPath());
    }

    @Test
    void testSetClassifierPathToNull()
  {
        EntityDefinition entity = new EntityDefinition("path", "original", new HashMap<>());
        entity.setClassifierPath(null);
        assertNull(entity.getClassifierPath());
    }

    @Test
    void testGetContent()
  {
        Map<String, Object> content = new HashMap<>();
        content.put("property", "value");

        EntityDefinition entity = new EntityDefinition("path", "classifier", content);
        Map<String, ?> retrievedContent = entity.getContent();

        assertSame(content, retrievedContent);
        assertEquals("value", retrievedContent.get("property"));
    }

    @Test
    void testGetContentWithNull()
  {
        EntityDefinition entity = new EntityDefinition("path", "classifier", null);
        assertNull(entity.getContent());
    }

    @Test
    void testEqualsWithSameInstance()
  {
        EntityDefinition entity = new EntityDefinition("path", "classifier", new HashMap<>());
        assertEquals(entity, entity);
    }

    @Test
    void testEqualsWithEqualInstances()
  {
        Map<String, Object> content1 = new HashMap<>();
        Map<String, Object> content2 = new HashMap<>();

        EntityDefinition entity1 = new EntityDefinition("path", "classifier", content1);
        EntityDefinition entity2 = new EntityDefinition("path", "classifier", content2);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentPath()
  {
        EntityDefinition entity1 = new EntityDefinition("path1", "classifier", new HashMap<>());
        EntityDefinition entity2 = new EntityDefinition("path2", "classifier", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentClassifierPath()
  {
        EntityDefinition entity1 = new EntityDefinition("path", "classifier1", new HashMap<>());
        EntityDefinition entity2 = new EntityDefinition("path", "classifier2", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentContent()
  {
        // Different content should result in different entities
        Map<String, Object> content1 = new HashMap<>();
        content1.put("key1", "value1");

        Map<String, Object> content2 = new HashMap<>();
        content2.put("key2", "value2");

        EntityDefinition entity1 = new EntityDefinition("path", "classifier", content1);
        EntityDefinition entity2 = new EntityDefinition("path", "classifier", content2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullContent()
  {
        // Null content vs non-null content should not be equal
        EntityDefinition entity1 = new EntityDefinition("path", "classifier", null);
        EntityDefinition entity2 = new EntityDefinition("path", "classifier", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithNull()
  {
        EntityDefinition entity = new EntityDefinition("path", "classifier", new HashMap<>());
        assertNotEquals(entity, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        EntityDefinition entity = new EntityDefinition("path", "classifier", new HashMap<>());
        assertNotEquals(entity, "String object");
    }

    @Test
    void testEqualsWithAllNullFields()
  {
        EntityDefinition entity1 = new EntityDefinition(null, null, null);
        EntityDefinition entity2 = new EntityDefinition(null, null, null);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithMixedNullFields()
  {
        EntityDefinition entity1 = new EntityDefinition(null, "classifier", new HashMap<>());
        EntityDefinition entity2 = new EntityDefinition("path", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testHashCodeConsistency()
  {
        EntityDefinition entity = new EntityDefinition("path", "classifier", new HashMap<>());
        int hashCode1 = entity.hashCode();
        int hashCode2 = entity.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeEqualityForEqualObjects()
  {
        EntityDefinition entity1 = new EntityDefinition("path", "classifier", new HashMap<>());
        EntityDefinition entity2 = new EntityDefinition("path", "classifier", new HashMap<>());

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentPaths()
  {
        EntityDefinition entity1 = new EntityDefinition("path1", "classifier", new HashMap<>());
        EntityDefinition entity2 = new EntityDefinition("path2", "classifier", new HashMap<>());

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithDifferentContent()
  {
        // HashCodeBuilder.reflectionHashCode appears to respect @EqualsExclude annotation
        // even though it's from a different package, so hash codes are the same
        Map<String, Object> content1 = new HashMap<>();
        content1.put("key1", "value1");

        Map<String, Object> content2 = new HashMap<>();
        content2.put("key2", "value2");

        EntityDefinition entity1 = new EntityDefinition("path", "classifier", content1);
        EntityDefinition entity2 = new EntityDefinition("path", "classifier", content2);

        // Hash codes should be equal because content field has @EqualsExclude
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields()
  {
        EntityDefinition entity = new EntityDefinition(null, null, null);
        // Should not throw an exception
        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testSetClassifierPathDoesNotAffectOtherFields()
  {
        Map<String, Object> content = new HashMap<>();
        content.put("key", "value");

        EntityDefinition entity = new EntityDefinition("path", "original", content);
        String originalPath = entity.getPath();
        Map<String, ?> originalContent = entity.getContent();

        entity.setClassifierPath("modified");

        assertEquals(originalPath, entity.getPath());
        assertSame(originalContent, entity.getContent());
        assertEquals("modified", entity.getClassifierPath());
    }
}

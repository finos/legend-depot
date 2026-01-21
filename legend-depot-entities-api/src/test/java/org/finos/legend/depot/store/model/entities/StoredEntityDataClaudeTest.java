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

class StoredEntityDataClaudeTest


{
    @Test
    void testFiveArgConstructorWithAllValidValues()
  {
        Map<String, Object> content = new HashMap<>();
        content.put("key", "value");
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", content);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertSame(entityDef, entity.getEntity());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullEntity()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getEntity());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullAttributes()
  {
        Map<String, Object> content = new HashMap<>();
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", content);

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, null);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertSame(entityDef, entity.getEntity());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithEmptyAttributes()
  {
        Map<String, Object> content = new HashMap<>();
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", content);
        Map<String, Object> attributes = new HashMap<>();

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, attributes);

        assertNotNull(entity.getEntityAttributes());
        assertTrue(entity.getEntityAttributes().isEmpty());
    }

    @Test
    void testFiveArgConstructorWithNullGroupId()
  {
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());

        StoredEntityData entity = new StoredEntityData(null, "artifactId", "1.0.0", entityDef, new HashMap<>());

        assertNull(entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
    }

    @Test
    void testFiveArgConstructorWithNullArtifactId()
  {
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());

        StoredEntityData entity = new StoredEntityData("groupId", null, "1.0.0", entityDef, new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
    }

    @Test
    void testFiveArgConstructorWithNullVersionId()
  {
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", null, entityDef, new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertNull(entity.getVersionId());
    }

    @Test
    void testFiveArgConstructorWithAllNullValues()
  {
        StoredEntityData entity = new StoredEntityData(null, null, null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getEntity());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithValidValues()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getEntity());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithEmptyStrings()
  {
        StoredEntityData entity = new StoredEntityData("", "", "");

        assertEquals("", entity.getGroupId());
        assertEquals("", entity.getArtifactId());
        assertEquals("", entity.getVersionId());
        assertNull(entity.getEntity());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithNullValues()
  {
        StoredEntityData entity = new StoredEntityData(null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getEntity());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testGetIdAlwaysReturnsEmptyString()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        assertEquals("", entity1.getId());

        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, new HashMap<>());
        assertEquals("", entity2.getId());
    }

    @Test
    void testGetIdConsistency()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");
        String id1 = entity.getId();
        String id2 = entity.getId();

        assertEquals(id1, id2);
        assertSame(id1, id2);
    }

    @Test
    void testGetEntityReturnsSetEntity()
  {
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, new HashMap<>());

        assertSame(entityDef, entity.getEntity());
    }

    @Test
    void testGetEntityReturnsNullWhenNotSet()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");
        assertNull(entity.getEntity());
    }

    @Test
    void testGetEntityReturnsNullWhenExplicitlySetToNull()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        assertNull(entity.getEntity());
    }

    @Test
    void testGetEntityConsistency()
  {
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, new HashMap<>());

        EntityDefinition result1 = entity.getEntity();
        EntityDefinition result2 = entity.getEntity();

        assertSame(result1, result2);
    }

    @Test
    void testEqualsWithSameInstance()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");
        assertEquals(entity, entity);
    }

    @Test
    void testEqualsWithEqualInstancesThreeArgConstructor()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithEqualInstancesFiveArgConstructor()
  {
        EntityDefinition entityDef1 = new EntityDefinition("test.path", "classifier.path", new HashMap<>());
        EntityDefinition entityDef2 = new EntityDefinition("test.path", "classifier.path", new HashMap<>());
        Map<String, Object> attributes1 = new HashMap<>();
        Map<String, Object> attributes2 = new HashMap<>();

        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef1, attributes1);
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef2, attributes2);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentGroupId()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId1", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentArtifactId()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId1", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentVersionId()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentEntity()
  {
        EntityDefinition entityDef1 = new EntityDefinition("path1", "classifier", new HashMap<>());
        EntityDefinition entityDef2 = new EntityDefinition("path2", "classifier", new HashMap<>());

        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef1, new HashMap<>());
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef2, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentAttributes()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key1", "value1");

        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key2", "value2");

        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes1);
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0", null, attributes2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullEntity()
  {
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", new HashMap<>());

        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, new HashMap<>());
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullAttributes()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithNull()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, "String object");
    }

    @Test
    void testEqualsWithAllNullFields()
  {
        StoredEntityData entity1 = new StoredEntityData(null, null, null, null, null);
        StoredEntityData entity2 = new StoredEntityData(null, null, null, null, null);

        assertEquals(entity1, entity2);
    }

    @Test
    void testHashCodeConsistency()
  {
        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0");
        int hashCode1 = entity.hashCode();
        int hashCode2 = entity.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeEqualityForEqualObjects()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentGroupId()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId1", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentArtifactId()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId1", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentVersionId()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields()
  {
        StoredEntityData entity = new StoredEntityData(null, null, null, null, null);
        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeWithComplexEntity()
  {
        Map<String, Object> content = new HashMap<>();
        content.put("key1", "value1");
        content.put("key2", 123);
        EntityDefinition entityDef = new EntityDefinition("test.path", "classifier.path", content);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "attrValue1");

        StoredEntityData entity = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef, attributes);

        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeEqualityForComplexEqualObjects()
  {
        Map<String, Object> content1 = new HashMap<>();
        content1.put("key", "value");
        EntityDefinition entityDef1 = new EntityDefinition("test.path", "classifier.path", content1);

        Map<String, Object> content2 = new HashMap<>();
        content2.put("key", "value");
        EntityDefinition entityDef2 = new EntityDefinition("test.path", "classifier.path", content2);

        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("attr", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("attr", "value");

        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef1, attributes1);
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0", entityDef2, attributes2);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testEqualsSymmetry()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity1);
    }

    @Test
    void testEqualsTransitivity()
  {
        StoredEntityData entity1 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity2 = new StoredEntityData("groupId", "artifactId", "1.0.0");
        StoredEntityData entity3 = new StoredEntityData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity3);
        assertEquals(entity1, entity3);
    }
}

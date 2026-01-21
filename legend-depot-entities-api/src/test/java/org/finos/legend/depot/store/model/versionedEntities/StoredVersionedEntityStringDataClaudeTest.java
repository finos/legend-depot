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

package org.finos.legend.depot.store.model.versionedEntities;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StoredVersionedEntityStringDataClaudeTest


{
    @Test
    void testFiveArgConstructorWithAllValidValues()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");

        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("test data", entity.getData());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullData()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");

        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", null, attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getData());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullAttributes()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", null);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("test data", entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithEmptyAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes);

        assertNotNull(entity.getEntityAttributes());
        assertTrue(entity.getEntityAttributes().isEmpty());
    }

    @Test
    void testFiveArgConstructorWithEmptyData()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "", attributes);

        assertEquals("", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithNullGroupId()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData(null, "artifactId", "1.0.0", "test data", new HashMap<>());

        assertNull(entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithNullArtifactId()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", null, "1.0.0", "test data", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithNullVersionId()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", null, "test data", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithAllNullValues()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData(null, null, null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithValidValues()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithEmptyStrings()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("", "", "");

        assertEquals("", entity.getGroupId());
        assertEquals("", entity.getArtifactId());
        assertEquals("", entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithNullValues()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData(null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testGetDataReturnsSetData()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", new HashMap<>());

        assertEquals("test data", entity.getData());
    }

    @Test
    void testGetDataReturnsNullWhenNotSet()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        assertNull(entity.getData());
    }

    @Test
    void testGetDataReturnsNullWhenExplicitlySetToNull()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        assertNull(entity.getData());
    }

    @Test
    void testGetDataConsistency()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", new HashMap<>());

        String result1 = entity.getData();
        String result2 = entity.getData();

        assertSame(result1, result2);
    }

    @Test
    void testGetDataWithComplexString()
  {
        String complexData = "{\"key\":\"value\",\"nested\":{\"array\":[1,2,3]}}";
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", complexData, new HashMap<>());

        assertEquals(complexData, entity.getData());
    }

    @Test
    void testEqualsWithSameInstance()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        assertEquals(entity, entity);
    }

    @Test
    void testEqualsWithEqualInstancesThreeArgConstructor()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithEqualInstancesFiveArgConstructor()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes1);
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes2);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentGroupId()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId1", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentArtifactId()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId1", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentVersionId()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentData()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "data1", new HashMap<>());
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "data2", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentAttributes()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key1", "value1");

        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key2", "value2");

        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", null, attributes1);
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", null, attributes2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullData()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", new HashMap<>());
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullAttributes()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithNull()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, "String object");
    }

    @Test
    void testEqualsWithAllNullFields()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData(null, null, null, null, null);
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData(null, null, null, null, null);

        assertEquals(entity1, entity2);
    }

    @Test
    void testHashCodeConsistency()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        int hashCode1 = entity.hashCode();
        int hashCode2 = entity.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeEqualityForEqualObjects()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentGroupId()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId1", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentArtifactId()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId1", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentVersionId()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentData()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "data1", new HashMap<>());
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "data2", new HashMap<>());

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields()
  {
        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData(null, null, null, null, null);
        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeWithComplexAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", 123);

        StoredVersionedEntityStringData entity = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes);

        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeEqualityForComplexEqualObjects()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes1);
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes2);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testEqualsSymmetry()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity1);
    }

    @Test
    void testEqualsTransitivity()
  {
        StoredVersionedEntityStringData entity1 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity2 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityStringData entity3 = new StoredVersionedEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity3);
        assertEquals(entity1, entity3);
    }
}

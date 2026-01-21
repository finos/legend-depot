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

class StoredEntityStringDataClaudeTest


{
    @Test
    void testFiveArgConstructorWithAllValidValues()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");
        attributes.put("attr2", "value2");

        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes);

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

        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", null, attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getData());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullAttributes()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "test data", null);

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

        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes);

        assertNotNull(entity.getEntityAttributes());
        assertTrue(entity.getEntityAttributes().isEmpty());
    }

    @Test
    void testFiveArgConstructorWithEmptyData()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "", attributes);

        assertEquals("", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithNullGroupId()
  {
        StoredEntityStringData entity = new StoredEntityStringData(null, "artifactId", "1.0.0", "test data", new HashMap<>());

        assertNull(entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithNullArtifactId()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", null, "1.0.0", "test data", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithNullVersionId()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", null, "test data", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testFiveArgConstructorWithAllNullValues()
  {
        StoredEntityStringData entity = new StoredEntityStringData(null, null, null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithLongData()
  {
        String longData = "a".repeat(10000);
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", longData, new HashMap<>());

        assertEquals(longData, entity.getData());
        assertEquals(10000, entity.getData().length());
    }

    @Test
    void testThreeArgConstructorWithValidValues()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithEmptyStrings()
  {
        StoredEntityStringData entity = new StoredEntityStringData("", "", "");

        assertEquals("", entity.getGroupId());
        assertEquals("", entity.getArtifactId());
        assertEquals("", entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithNullValues()
  {
        StoredEntityStringData entity = new StoredEntityStringData(null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getData());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testGetDataReturnsSetData()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "test data", new HashMap<>());
        assertEquals("test data", entity.getData());
    }

    @Test
    void testGetDataReturnsNullWhenNotSet()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        assertNull(entity.getData());
    }

    @Test
    void testGetDataReturnsNullWhenExplicitlySetToNull()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        assertNull(entity.getData());
    }

    @Test
    void testGetDataConsistency()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "test data", new HashMap<>());

        String result1 = entity.getData();
        String result2 = entity.getData();

        assertSame(result1, result2);
    }

    @Test
    void testGetDataWithSpecialCharacters()
  {
        String specialData = "data with \n newlines \t tabs and special chars: @#$%^&*()";
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", specialData, new HashMap<>());

        assertEquals(specialData, entity.getData());
    }

    @Test
    void testEqualsWithSameInstance()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        assertEquals(entity, entity);
    }

    @Test
    void testEqualsWithEqualInstancesThreeArgConstructor()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithEqualInstancesFiveArgConstructor()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", attributes1);
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", attributes2);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentGroupId()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId1", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentArtifactId()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId1", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentVersionId()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentData()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data1", new HashMap<>());
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data2", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentAttributes()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key1", "value1");

        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key2", "value2");

        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", attributes1);
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", attributes2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullData()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", new HashMap<>());
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullAttributes()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", new HashMap<>());
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithBothDataNull()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithNull()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, "String object");
    }

    @Test
    void testEqualsWithAllNullFields()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData(null, null, null, null, null);
        StoredEntityStringData entity2 = new StoredEntityStringData(null, null, null, null, null);

        assertEquals(entity1, entity2);
    }

    @Test
    void testHashCodeConsistency()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        int hashCode1 = entity.hashCode();
        int hashCode2 = entity.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeEqualityForEqualObjects()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentGroupId()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId1", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentArtifactId()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId1", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentVersionId()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentData()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data1", new HashMap<>());
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data2", new HashMap<>());

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields()
  {
        StoredEntityStringData entity = new StoredEntityStringData(null, null, null, null, null);
        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeWithComplexAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", 123);
        attributes.put("key3", true);

        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "test data", attributes);

        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeEqualityForComplexEqualObjects()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", attributes1);
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", attributes2);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testEqualsSymmetry()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity1);
    }

    @Test
    void testEqualsTransitivity()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        StoredEntityStringData entity3 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity3);
        assertEquals(entity1, entity3);
    }

    @Test
    void testEqualsTransitivityWithData()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", new HashMap<>());
        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", new HashMap<>());
        StoredEntityStringData entity3 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", new HashMap<>());

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity3);
        assertEquals(entity1, entity3);
    }

    @Test
    void testGetIdAlwaysReturnsEmptyString()
  {
        StoredEntityStringData entity1 = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        assertEquals("", entity1.getId());

        StoredEntityStringData entity2 = new StoredEntityStringData("groupId", "artifactId", "1.0.0", "data", new HashMap<>());
        assertEquals("", entity2.getId());
    }

    @Test
    void testGetIdConsistency()
  {
        StoredEntityStringData entity = new StoredEntityStringData("groupId", "artifactId", "1.0.0");
        String id1 = entity.getId();
        String id2 = entity.getId();

        assertEquals(id1, id2);
        assertSame(id1, id2);
    }
}

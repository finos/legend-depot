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

class StoredEntityReferenceClaudeTest


{
    @Test
    void testFiveArgConstructorWithAllValidValues()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");

        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref.path", entity.getReference());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullReference()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");

        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", null, attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getReference());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithEmptyReference()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "", attributes);

        assertEquals("", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithNullAttributes()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", null);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref.path", entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithEmptyAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes);

        assertNotNull(entity.getEntityAttributes());
        assertTrue(entity.getEntityAttributes().isEmpty());
    }

    @Test
    void testFiveArgConstructorWithNullGroupId()
  {
        StoredEntityReference entity = new StoredEntityReference(null, "artifactId", "1.0.0", "ref.path", new HashMap<>());

        assertNull(entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref.path", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithNullArtifactId()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", null, "1.0.0", "ref.path", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref.path", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithNullVersionId()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", null, "ref.path", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertEquals("ref.path", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithAllNullValues()
  {
        StoredEntityReference entity = new StoredEntityReference(null, null, null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithValidValues()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithEmptyStrings()
  {
        StoredEntityReference entity = new StoredEntityReference("", "", "");

        assertEquals("", entity.getGroupId());
        assertEquals("", entity.getArtifactId());
        assertEquals("", entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithNullValues()
  {
        StoredEntityReference entity = new StoredEntityReference(null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testGetReferenceReturnsSetReference()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", new HashMap<>());

        assertEquals("ref.path", entity.getReference());
    }

    @Test
    void testGetReferenceReturnsNullWhenNotSet()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertNull(entity.getReference());
    }

    @Test
    void testGetReferenceReturnsNullWhenExplicitlySetToNull()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNull(entity.getReference());
    }

    @Test
    void testGetReferenceConsistency()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", new HashMap<>());

        String result1 = entity.getReference();
        String result2 = entity.getReference();

        assertEquals(result1, result2);
        assertSame(result1, result2);
    }

    @Test
    void testEqualsWithSameInstance()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity, entity);
    }

    @Test
    void testEqualsWithEqualInstancesThreeArgConstructor()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithEqualInstancesFiveArgConstructor()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes1);
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes2);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentGroupId()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId1", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentArtifactId()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId1", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentVersionId()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentReference()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref1.path", new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref2.path", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentAttributes()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key1", "value1");

        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key2", "value2");

        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes1);
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullReference()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithBothNullReferences()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullAttributes()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithNull()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity, "String object");
    }

    @Test
    void testEqualsWithAllNullFields()
  {
        StoredEntityReference entity1 = new StoredEntityReference(null, null, null, null, null);
        StoredEntityReference entity2 = new StoredEntityReference(null, null, null, null, null);

        assertEquals(entity1, entity2);
    }

    @Test
    void testHashCodeConsistency()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        int hashCode1 = entity.hashCode();
        int hashCode2 = entity.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeEqualityForEqualObjects()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeEqualityForComplexEqualObjects()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes1);
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes2);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentGroupId()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId1", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentArtifactId()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId1", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentVersionId()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentReference()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref1.path", new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref2.path", new HashMap<>());

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields()
  {
        StoredEntityReference entity = new StoredEntityReference(null, null, null, null, null);

        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeWithComplexAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", 123);
        attributes.put("key3", true);

        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", attributes);

        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testEqualsSymmetry()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity1);
    }

    @Test
    void testEqualsTransitivity()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0");
        StoredEntityReference entity3 = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity3);
        assertEquals(entity1, entity3);
    }

    @Test
    void testEqualsWithEmptyReferenceVsNullReference()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "", new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithEmptyAttributesVsNullAttributes()
  {
        StoredEntityReference entity1 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", new HashMap<>());
        StoredEntityReference entity2 = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", null);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testGetIdInherited()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals("", entity.getId());
    }

    @Test
    void testGetIdWithFiveArgConstructor()
  {
        StoredEntityReference entity = new StoredEntityReference("groupId", "artifactId", "1.0.0", "ref.path", new HashMap<>());

        assertEquals("", entity.getId());
    }
}

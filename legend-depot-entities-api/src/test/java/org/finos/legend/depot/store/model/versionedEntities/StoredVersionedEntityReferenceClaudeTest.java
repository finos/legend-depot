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

class StoredVersionedEntityReferenceClaudeTest


{
    @Test
    void testFiveArgConstructorWithAllValidValues()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");

        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref::path", entity.getReference());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullReference()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attr1", "value1");

        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", null, attributes);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getReference());
        assertSame(attributes, entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithNullAttributes()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", null);

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref::path", entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testFiveArgConstructorWithEmptyAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes);

        assertNotNull(entity.getEntityAttributes());
        assertTrue(entity.getEntityAttributes().isEmpty());
    }

    @Test
    void testFiveArgConstructorWithEmptyReference()
  {
        Map<String, Object> attributes = new HashMap<>();

        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "", attributes);

        assertEquals("", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithNullGroupId()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference(null, "artifactId", "1.0.0", "ref::path", new HashMap<>());

        assertNull(entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref::path", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithNullArtifactId()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", null, "1.0.0", "ref::path", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertEquals("ref::path", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithNullVersionId()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", null, "ref::path", new HashMap<>());

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertEquals("ref::path", entity.getReference());
    }

    @Test
    void testFiveArgConstructorWithAllNullValues()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference(null, null, null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithValidValues()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals("groupId", entity.getGroupId());
        assertEquals("artifactId", entity.getArtifactId());
        assertEquals("1.0.0", entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithEmptyStrings()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("", "", "");

        assertEquals("", entity.getGroupId());
        assertEquals("", entity.getArtifactId());
        assertEquals("", entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testThreeArgConstructorWithNullValues()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference(null, null, null);

        assertNull(entity.getGroupId());
        assertNull(entity.getArtifactId());
        assertNull(entity.getVersionId());
        assertNull(entity.getReference());
        assertNull(entity.getEntityAttributes());
    }

    @Test
    void testGetReferenceReturnsSetReference()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", new HashMap<>());

        assertEquals("ref::path", entity.getReference());
    }

    @Test
    void testGetReferenceReturnsNullWhenNotSet()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        assertNull(entity.getReference());
    }

    @Test
    void testGetReferenceReturnsNullWhenExplicitlySetToNull()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        assertNull(entity.getReference());
    }

    @Test
    void testGetReferenceConsistency()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", new HashMap<>());

        String result1 = entity.getReference();
        String result2 = entity.getReference();

        assertSame(result1, result2);
    }

    @Test
    void testEqualsWithSameInstance()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        assertEquals(entity, entity);
    }

    @Test
    void testEqualsWithEqualInstancesThreeArgConstructor()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithEqualInstancesFiveArgConstructor()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes1);
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes2);

        assertEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentGroupId()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId1", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentArtifactId()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId1", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentVersionId()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentReference()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path1", new HashMap<>());
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path2", new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithDifferentAttributes()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key1", "value1");

        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key2", "value2");

        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", null, attributes1);
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", null, attributes2);

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullReference()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", new HashMap<>());
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithOneNullAttributes()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", null, new HashMap<>());
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");

        assertNotEquals(entity1, entity2);
    }

    @Test
    void testEqualsWithNull()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        assertNotEquals(entity, "String object");
    }

    @Test
    void testEqualsWithAllNullFields()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference(null, null, null, null, null);
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference(null, null, null, null, null);

        assertEquals(entity1, entity2);
    }

    @Test
    void testHashCodeConsistency()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        int hashCode1 = entity.hashCode();
        int hashCode2 = entity.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeEqualityForEqualObjects()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentGroupId()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId1", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId2", "artifactId", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentArtifactId()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId1", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId2", "1.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentVersionId()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "2.0.0");

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeDifferenceForDifferentReference()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path1", new HashMap<>());
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path2", new HashMap<>());

        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields()
  {
        StoredVersionedEntityReference entity = new StoredVersionedEntityReference(null, null, null, null, null);
        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeWithComplexAttributes()
  {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", 123);

        StoredVersionedEntityReference entity = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes);

        assertDoesNotThrow(() -> entity.hashCode());
    }

    @Test
    void testHashCodeEqualityForComplexEqualObjects()
  {
        Map<String, Object> attributes1 = new HashMap<>();
        attributes1.put("key", "value");
        Map<String, Object> attributes2 = new HashMap<>();
        attributes2.put("key", "value");

        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes1);
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0", "ref::path", attributes2);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testEqualsSymmetry()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity1);
    }

    @Test
    void testEqualsTransitivity()
  {
        StoredVersionedEntityReference entity1 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity2 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");
        StoredVersionedEntityReference entity3 = new StoredVersionedEntityReference("groupId", "artifactId", "1.0.0");

        assertEquals(entity1, entity2);
        assertEquals(entity2, entity3);
        assertEquals(entity1, entity3);
    }
}

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

package org.finos.legend.depot.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DepotEntityClaudeTest 

{

    /**
     * Test constructor with null entity parameter.
     *
     * <p>Method under test:
     * {@link DepotEntity#DepotEntity(String, String, String, Entity)}
     */
    @Test
    @DisplayName("Test constructor with null entity")
    void testConstructorWithNullEntity()
  {
        // Arrange and Act
        DepotEntity depotEntity = new DepotEntity("group1", "artifact1", "version1", null);

        // Assert
        assertEquals("group1", depotEntity.getGroupId());
        assertEquals("artifact1", depotEntity.getArtifactId());
        assertEquals("version1", depotEntity.getVersionId());
        assertNull(depotEntity.getEntity());
        assertFalse(depotEntity.isVersionedEntity());
    }

    /**
     * Test equals when entity field differs.
     *
     * <p>Method under test: {@link DepotEntity#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when entity field differs")
    void testEquals_whenEntityDiffers_thenReturnNotEqual()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path2", "classifier2", new HashMap<>());

        DepotEntity depotEntity1 = new DepotEntity("group1", "artifact1", "version1", entity1);
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact1", "version1", entity2);

        // Act and Assert
        assertNotEquals(depotEntity1, depotEntity2);
    }

    /**
     * Test equals when one has entity and other doesn't.
     *
     * <p>Method under test: {@link DepotEntity#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when one has entity and other doesn't")
    void testEquals_whenOneHasEntityAndOtherDoesNot_thenReturnNotEqual()
  {
        // Arrange
        Entity entity = new EntityDefinition("path1", "classifier1", new HashMap<>());

        DepotEntity depotEntity1 = new DepotEntity("group1", "artifact1", "version1", entity);
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact1", "version1");

        // Act and Assert
        assertNotEquals(depotEntity1, depotEntity2);
    }

    /**
     * Test hashCode when entity field differs.
     *
     * <p>Method under test: {@link DepotEntity#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode when entity field differs")
    void testHashCode_whenEntityDiffers_thenReturnDifferentHashCode()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path2", "classifier2", new HashMap<>());

        DepotEntity depotEntity1 = new DepotEntity("group1", "artifact1", "version1", entity1);
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact1", "version1", entity2);

        // Act
        int hashCode1 = depotEntity1.hashCode();
        int hashCode2 = depotEntity2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    /**
     * Test equals and hashCode with identical entities.
     *
     * <p>Methods under test:
     * <ul>
     *   <li>{@link DepotEntity#equals(Object)}
     *   <li>{@link DepotEntity#hashCode()}
     * </ul>
     */
    @Test
    @DisplayName("Test equals and hashCode with identical entities")
    void testEqualsAndHashCode_whenEntitiesAreIdentical_thenReturnEqual()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path1", "classifier1", new HashMap<>());

        DepotEntity depotEntity1 = new DepotEntity("group1", "artifact1", "version1", entity1);
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact1", "version1", entity2);

        // Act and Assert
        assertEquals(depotEntity1, depotEntity2);
        assertEquals(depotEntity1.hashCode(), depotEntity2.hashCode());
    }

    /**
     * Test that versionedEntity is always false in constructor.
     *
     * <p>Method under test: {@link DepotEntity#isVersionedEntity()}
     */
    @Test
    @DisplayName("Test that versionedEntity is always false in constructor")
    void testIsVersionedEntity_alwaysReturnsFalse()
  {
        // Arrange
        Entity entity = new EntityDefinition("path1", "classifier1", new HashMap<>());

        DepotEntity depotEntity1 = new DepotEntity();
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact1", "version1");
        DepotEntity depotEntity3 = new DepotEntity("group1", "artifact1", "version1", entity);

        // Act and Assert
        assertFalse(depotEntity1.isVersionedEntity());
        assertFalse(depotEntity2.isVersionedEntity());
        assertFalse(depotEntity3.isVersionedEntity());
    }

    /**
     * Test getEntity returns the same instance passed to constructor.
     *
     * <p>Method under test: {@link DepotEntity#getEntity()}
     */
    @Test
    @DisplayName("Test getEntity returns the same instance")
    void testGetEntity_returnsSameInstance()
  {
        // Arrange
        Entity entity = new EntityDefinition("path1", "classifier1", new HashMap<>());
        DepotEntity depotEntity = new DepotEntity("group1", "artifact1", "version1", entity);

        // Act
        Entity retrievedEntity = depotEntity.getEntity();

        // Assert
        assertSame(entity, retrievedEntity);
    }

    /**
     * Test equals with different versionId.
     *
     * <p>Method under test: {@link DepotEntity#equals(Object)}
     */
    @Test
    @DisplayName("Test equals with different versionId")
    void testEquals_whenVersionIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        DepotEntity depotEntity1 = new DepotEntity("group1", "artifact1", "version1");
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact1", "version2");

        // Act and Assert
        assertNotEquals(depotEntity1, depotEntity2);
    }

    /**
     * Test equals with different artifactId.
     *
     * <p>Method under test: {@link DepotEntity#equals(Object)}
     */
    @Test
    @DisplayName("Test equals with different artifactId")
    void testEquals_whenArtifactIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        DepotEntity depotEntity1 = new DepotEntity("group1", "artifact1", "version1");
        DepotEntity depotEntity2 = new DepotEntity("group1", "artifact2", "version1");

        // Act and Assert
        assertNotEquals(depotEntity1, depotEntity2);
    }
}

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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectVersionEntitiesClaudeTest 

{

    /**
     * Test default constructor initializes all fields to null/default values.
     *
     * <p>Method under test:
     * {@link ProjectVersionEntities#ProjectVersionEntities()}
     */
    @Test
    @DisplayName("Test default constructor initializes fields to null")
    void testDefaultConstructor_initializesFieldsToNull()
  {
        // Arrange and Act
        ProjectVersionEntities projectVersionEntities = new ProjectVersionEntities();

        // Assert
        assertNull(projectVersionEntities.getGroupId());
        assertNull(projectVersionEntities.getArtifactId());
        assertNull(projectVersionEntities.getVersionId());
        assertNull(projectVersionEntities.getEntities());
        assertFalse(projectVersionEntities.isVersionedEntity());
    }

    /**
     * Test parameterized constructor with non-empty entity list.
     *
     * <p>Method under test:
     * {@link ProjectVersionEntities#ProjectVersionEntities(String, String, String, List)}
     */
    @Test
    @DisplayName("Test parameterized constructor with non-empty entity list")
    void testParameterizedConstructor_withNonEmptyEntityList()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path2", "classifier2", new HashMap<>());
        List<Entity> entities = Arrays.asList(entity1, entity2);

        // Act
        ProjectVersionEntities projectVersionEntities =
            new ProjectVersionEntities("groupId", "artifactId", "versionId", entities);

        // Assert
        assertEquals("groupId", projectVersionEntities.getGroupId());
        assertEquals("artifactId", projectVersionEntities.getArtifactId());
        assertEquals("versionId", projectVersionEntities.getVersionId());
        assertSame(entities, projectVersionEntities.getEntities());
        assertEquals(2, projectVersionEntities.getEntities().size());
        assertFalse(projectVersionEntities.isVersionedEntity());
    }

    /**
     * Test parameterized constructor with null entity list.
     *
     * <p>Method under test:
     * {@link ProjectVersionEntities#ProjectVersionEntities(String, String, String, List)}
     */
    @Test
    @DisplayName("Test parameterized constructor with null entity list")
    void testParameterizedConstructor_withNullEntityList()
  {
        // Arrange and Act
        ProjectVersionEntities projectVersionEntities =
            new ProjectVersionEntities("groupId", "artifactId", "versionId", null);

        // Assert
        assertEquals("groupId", projectVersionEntities.getGroupId());
        assertEquals("artifactId", projectVersionEntities.getArtifactId());
        assertEquals("versionId", projectVersionEntities.getVersionId());
        assertNull(projectVersionEntities.getEntities());
        assertFalse(projectVersionEntities.isVersionedEntity());
    }

    /**
     * Test isVersionedEntity always returns false for parameterized constructor.
     *
     * <p>Method under test: {@link ProjectVersionEntities#isVersionedEntity()}
     */
    @Test
    @DisplayName("Test isVersionedEntity always returns false")
    void testIsVersionedEntity_alwaysReturnsFalse()
  {
        // Arrange
        List<Entity> entities = new ArrayList<>();
        ProjectVersionEntities projectVersionEntities1 = new ProjectVersionEntities();
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities);

        // Act and Assert
        assertFalse(projectVersionEntities1.isVersionedEntity());
        assertFalse(projectVersionEntities2.isVersionedEntity());
    }

    /**
     * Test getEntities returns the same instance passed to constructor.
     *
     * <p>Method under test: {@link ProjectVersionEntities#getEntities()}
     */
    @Test
    @DisplayName("Test getEntities returns the same instance")
    void testGetEntities_returnsSameInstance()
  {
        // Arrange
        List<Entity> entities = new ArrayList<>();
        entities.add(new EntityDefinition("path1", "classifier1", new HashMap<>()));
        ProjectVersionEntities projectVersionEntities =
            new ProjectVersionEntities("group", "artifact", "version", entities);

        // Act
        List<Entity> retrievedEntities = projectVersionEntities.getEntities();

        // Assert
        assertSame(entities, retrievedEntities);
    }

    /**
     * Test equals when entities list differs.
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when entities list differs")
    void testEquals_whenEntitiesListDiffers_thenReturnNotEqual()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path2", "classifier2", new HashMap<>());

        List<Entity> entities1 = Arrays.asList(entity1);
        List<Entity> entities2 = Arrays.asList(entity2);

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities1);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities2);

        // Act and Assert
        assertNotEquals(projectVersionEntities1, projectVersionEntities2);
    }

    /**
     * Test equals when entities list size differs.
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when entities list size differs")
    void testEquals_whenEntitiesListSizeDiffers_thenReturnNotEqual()
  {
        // Arrange
        Entity entity = new EntityDefinition("path1", "classifier1", new HashMap<>());

        List<Entity> entities1 = Arrays.asList(entity);
        List<Entity> entities2 = new ArrayList<>();

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities1);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities2);

        // Act and Assert
        assertNotEquals(projectVersionEntities1, projectVersionEntities2);
    }

    /**
     * Test equals when one has null entities and other has non-null entities.
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when one has null entities and other has non-null")
    void testEquals_whenOneHasNullEntitiesAndOtherHasNonNull_thenReturnNotEqual()
  {
        // Arrange
        List<Entity> entities = new ArrayList<>();

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", null);

        // Act and Assert
        assertNotEquals(projectVersionEntities1, projectVersionEntities2);
    }

    /**
     * Test equals and hashCode with identical entities lists.
     *
     * <p>Methods under test:
     * <ul>
     *   <li>{@link ProjectVersionEntities#equals(Object)}
     *   <li>{@link ProjectVersionEntities#hashCode()}
     * </ul>
     */
    @Test
    @DisplayName("Test equals and hashCode with identical entities lists")
    void testEqualsAndHashCode_whenEntitiesListsAreIdentical_thenReturnEqual()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path1", "classifier1", new HashMap<>());

        List<Entity> entities1 = Arrays.asList(entity1);
        List<Entity> entities2 = Arrays.asList(entity2);

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities1);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities2);

        // Act and Assert
        assertEquals(projectVersionEntities1, projectVersionEntities2);
        assertEquals(projectVersionEntities1.hashCode(), projectVersionEntities2.hashCode());
    }

    /**
     * Test hashCode when entities list differs.
     *
     * <p>Method under test: {@link ProjectVersionEntities#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode when entities list differs")
    void testHashCode_whenEntitiesListDiffers_thenReturnDifferentHashCode()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path2", "classifier2", new HashMap<>());

        List<Entity> entities1 = Arrays.asList(entity1);
        List<Entity> entities2 = Arrays.asList(entity2);

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities1);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities2);

        // Act
        int hashCode1 = projectVersionEntities1.hashCode();
        int hashCode2 = projectVersionEntities2.hashCode();

        // Assert
        assertNotEquals(hashCode1, hashCode2);
    }

    /**
     * Test equals when groupId differs.
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when groupId differs")
    void testEquals_whenGroupIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        List<Entity> entities = new ArrayList<>();

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group1", "artifact", "version", entities);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group2", "artifact", "version", entities);

        // Act and Assert
        assertNotEquals(projectVersionEntities1, projectVersionEntities2);
    }

    /**
     * Test equals when artifactId differs.
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when artifactId differs")
    void testEquals_whenArtifactIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        List<Entity> entities = new ArrayList<>();

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact1", "version", entities);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact2", "version", entities);

        // Act and Assert
        assertNotEquals(projectVersionEntities1, projectVersionEntities2);
    }

    /**
     * Test equals when versionId differs.
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals when versionId differs")
    void testEquals_whenVersionIdDiffers_thenReturnNotEqual()
  {
        // Arrange
        List<Entity> entities = new ArrayList<>();

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version1", entities);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version2", entities);

        // Act and Assert
        assertNotEquals(projectVersionEntities1, projectVersionEntities2);
    }

    /**
     * Test hashCode consistency across multiple calls.
     *
     * <p>Method under test: {@link ProjectVersionEntities#hashCode()}
     */
    @Test
    @DisplayName("Test hashCode consistency across multiple calls")
    void testHashCode_consistencyAcrossMultipleCalls()
  {
        // Arrange
        List<Entity> entities = Arrays.asList(
            new EntityDefinition("path1", "classifier1", new HashMap<>())
        );
        ProjectVersionEntities projectVersionEntities =
            new ProjectVersionEntities("group", "artifact", "version", entities);

        // Act
        int hashCode1 = projectVersionEntities.hashCode();
        int hashCode2 = projectVersionEntities.hashCode();
        int hashCode3 = projectVersionEntities.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
        assertEquals(hashCode2, hashCode3);
    }

    /**
     * Test equals with reflexivity (object equals itself).
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals reflexivity")
    void testEquals_reflexivity()
  {
        // Arrange
        List<Entity> entities = Arrays.asList(
            new EntityDefinition("path1", "classifier1", new HashMap<>())
        );
        ProjectVersionEntities projectVersionEntities =
            new ProjectVersionEntities("group", "artifact", "version", entities);

        // Act and Assert
        assertEquals(projectVersionEntities, projectVersionEntities);
    }

    /**
     * Test equals with symmetry (if a equals b, then b equals a).
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals symmetry")
    void testEquals_symmetry()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path1", "classifier1", new HashMap<>());

        List<Entity> entities1 = Arrays.asList(entity1);
        List<Entity> entities2 = Arrays.asList(entity2);

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities1);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities2);

        // Act and Assert
        assertTrue(projectVersionEntities1.equals(projectVersionEntities2));
        assertTrue(projectVersionEntities2.equals(projectVersionEntities1));
    }

    /**
     * Test equals with transitivity (if a equals b and b equals c, then a equals c).
     *
     * <p>Method under test: {@link ProjectVersionEntities#equals(Object)}
     */
    @Test
    @DisplayName("Test equals transitivity")
    void testEquals_transitivity()
  {
        // Arrange
        Entity entity1 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity2 = new EntityDefinition("path1", "classifier1", new HashMap<>());
        Entity entity3 = new EntityDefinition("path1", "classifier1", new HashMap<>());

        List<Entity> entities1 = Arrays.asList(entity1);
        List<Entity> entities2 = Arrays.asList(entity2);
        List<Entity> entities3 = Arrays.asList(entity3);

        ProjectVersionEntities projectVersionEntities1 =
            new ProjectVersionEntities("group", "artifact", "version", entities1);
        ProjectVersionEntities projectVersionEntities2 =
            new ProjectVersionEntities("group", "artifact", "version", entities2);
        ProjectVersionEntities projectVersionEntities3 =
            new ProjectVersionEntities("group", "artifact", "version", entities3);

        // Act and Assert
        assertTrue(projectVersionEntities1.equals(projectVersionEntities2));
        assertTrue(projectVersionEntities2.equals(projectVersionEntities3));
        assertTrue(projectVersionEntities1.equals(projectVersionEntities3));
    }
}

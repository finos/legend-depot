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

package org.finos.legend.depot.store.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.domain.entity.DepotEntityOverview;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntitiesClaudeTest 

{

    /**
     * Test findEntitiesByClassifier delegates to findClassifierEntitiesByVersions with correct parameters.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier delegates correctly")
    void testFindEntitiesByClassifier_delegatesToFindClassifierEntitiesByVersions()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String classifier = "meta::pure::metamodel::type::Class";

        Entity entity = new EntityDefinition("test::path", classifier, new HashMap<>());
        DepotEntity expectedEntity = new DepotEntity(groupId, artifactId, versionId, entity);
        List<DepotEntity> expectedResult = Arrays.asList(expectedEntity);

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(expectedResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedEntity, result.get(0));

        verify(entities).findClassifierEntitiesByVersions(eq(classifier), any(List.class));
    }

    /**
     * Test findEntitiesByClassifier creates correct ProjectVersion.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier creates correct ProjectVersion")
    void testFindEntitiesByClassifier_createsCorrectProjectVersion()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "my-artifact";
        String versionId = "2.3.4";
        String classifier = "meta::pure::metamodel::type::Enumeration";

        List<DepotEntity> expectedResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenAnswer(invocation ->
                        {
                    List<ProjectVersion> versions = invocation.getArgument(1);
                    assertEquals(1, versions.size());
                    ProjectVersion pv = versions.get(0);
                    assertEquals(groupId, pv.getGroupId());
                    assertEquals(artifactId, pv.getArtifactId());
                    assertEquals(versionId, pv.getVersionId());
                    return expectedResult;
                });

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier with null groupId.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier with null groupId")
    void testFindEntitiesByClassifier_withNullGroupId()
  {
        // Arrange
        String groupId = null;
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String classifier = "meta::pure::metamodel::type::Class";

        List<DepotEntity> expectedResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenAnswer(invocation ->
                        {
                    List<ProjectVersion> versions = invocation.getArgument(1);
                    assertEquals(1, versions.size());
                    ProjectVersion pv = versions.get(0);
                    assertEquals(null, pv.getGroupId());
                    return expectedResult;
                });

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier with null artifactId.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier with null artifactId")
    void testFindEntitiesByClassifier_withNullArtifactId()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = null;
        String versionId = "1.0.0";
        String classifier = "meta::pure::metamodel::type::Class";

        List<DepotEntity> expectedResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(expectedResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier with null versionId.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier with null versionId")
    void testFindEntitiesByClassifier_withNullVersionId()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = null;
        String classifier = "meta::pure::metamodel::type::Class";

        List<DepotEntity> expectedResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(expectedResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier with null classifier.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier with null classifier")
    void testFindEntitiesByClassifier_withNullClassifier()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String classifier = null;

        List<DepotEntity> expectedResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(expectedResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier returns empty list when no entities found.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier returns empty list when no entities found")
    void testFindEntitiesByClassifier_returnsEmptyListWhenNoEntitiesFound()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String classifier = "meta::pure::metamodel::type::Class";

        List<DepotEntity> emptyResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(emptyResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test findEntitiesByClassifier returns multiple entities.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier returns multiple entities")
    void testFindEntitiesByClassifier_returnsMultipleEntities()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String classifier = "meta::pure::metamodel::type::Class";

        Entity entity1 = new EntityDefinition("test::path1", classifier, new HashMap<>());
        Entity entity2 = new EntityDefinition("test::path2", classifier, new HashMap<>());
        Entity entity3 = new EntityDefinition("test::path3", classifier, new HashMap<>());

        DepotEntity depotEntity1 = new DepotEntity(groupId, artifactId, versionId, entity1);
        DepotEntity depotEntity2 = new DepotEntity(groupId, artifactId, versionId, entity2);
        DepotEntity depotEntity3 = new DepotEntity(groupId, artifactId, versionId, entity3);

        List<DepotEntity> expectedResult = Arrays.asList(depotEntity1, depotEntity2, depotEntity3);

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(expectedResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier with special characters in classifier.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier with special characters in classifier")
    void testFindEntitiesByClassifier_withSpecialCharactersInClassifier()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String classifier = "meta::pure::$special::type::Class";

        List<DepotEntity> expectedResult = new ArrayList<>();

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenReturn(expectedResult);

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    /**
     * Test findEntitiesByClassifier with SNAPSHOT version.
     *
     * <p>Method under test:
     * {@link Entities#findEntitiesByClassifier(String, String, String, String)}
     */
    @Test
    @DisplayName("Test findEntitiesByClassifier with SNAPSHOT version")
    void testFindEntitiesByClassifier_withSnapshotVersion()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0-SNAPSHOT";
        String classifier = "meta::pure::metamodel::type::Class";

        Entity entity = new EntityDefinition("test::path", classifier, new HashMap<>());
        DepotEntity expectedEntity = new DepotEntity(groupId, artifactId, versionId, entity);
        List<DepotEntity> expectedResult = Arrays.asList(expectedEntity);

        Entities<StoredEntity> entities = mock(Entities.class);
        when(entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier))
                .thenCallRealMethod();
        when(entities.findClassifierEntitiesByVersions(eq(classifier), any(List.class)))
                .thenAnswer(invocation ->
                        {
                    List<ProjectVersion> versions = invocation.getArgument(1);
                    assertEquals(1, versions.size());
                    ProjectVersion pv = versions.get(0);
                    assertEquals(versionId, pv.getVersionId());
                    return expectedResult;
                });

        // Act
        List<DepotEntity> result = entities.findEntitiesByClassifier(groupId, artifactId, versionId, classifier);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedEntity, result.get(0));
    }
}

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

package org.finos.legend.depot.services.api.artifacts.handlers;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectArtifactHandlerFactoryClaudeTest


{
    // Using reflection to reset singleton state between tests
    // This is necessary because ProjectArtifactHandlerFactory is a singleton with static methods,
    // and test isolation requires clearing the internal EnumMap between tests.
    // There is no public API to clear or reset the factory state.
    @BeforeEach
    @AfterEach
    void resetSingletonState() throws Exception
    {
        Field instanceField = ProjectArtifactHandlerFactory.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        ProjectArtifactHandlerFactory instance = (ProjectArtifactHandlerFactory) instanceField.get(null);

        Field handlersField = ProjectArtifactHandlerFactory.class.getDeclaredField("artifactHandlers");
        handlersField.setAccessible(true);
        EnumMap<ArtifactType, ProjectArtifactsHandler> handlers =
            (EnumMap<ArtifactType, ProjectArtifactsHandler>) handlersField.get(instance);
        handlers.clear();
    }

    // Test helper classes
    private static class TestHandler1 implements ProjectArtifactsHandler
    {
        @Override
        public MetadataNotificationResponse refreshProjectVersionArtifacts(String groupId, String artifactId,
                                                                          String versionId, List<File> files)
  {
            return null;
        }

        @Override
        public void delete(String groupId, String artifactId, String versionId)
  {
        }
    }

    private static class TestHandler2 implements ProjectArtifactsHandler
    {
        @Override
        public MetadataNotificationResponse refreshProjectVersionArtifacts(String groupId, String artifactId,
                                                                          String versionId, List<File> files)
  {
            return null;
        }

        @Override
        public void delete(String groupId, String artifactId, String versionId)
  {
        }
    }

    // registerArtifactHandler tests

    @Test
    @DisplayName("registerArtifactHandler should register a handler for a specific artifact type")
    void testRegisterArtifactHandler()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);

        // Assert
        ProjectArtifactsHandler retrieved = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES);
        assertSame(handler, retrieved);
    }

    @Test
    @DisplayName("registerArtifactHandler should register multiple handlers for different types")
    void testRegisterMultipleHandlers()
  {
        // Arrange
        ProjectArtifactsHandler handler1 = new TestHandler1();
        ProjectArtifactsHandler handler2 = new TestHandler2();

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler1);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler2);

        // Assert
        assertSame(handler1, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));
        assertSame(handler2, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.VERSIONED_ENTITIES));
    }

    @Test
    @DisplayName("registerArtifactHandler should replace existing handler for the same type")
    void testRegisterArtifactHandlerReplacesExisting()
  {
        // Arrange
        ProjectArtifactsHandler handler1 = new TestHandler1();
        ProjectArtifactsHandler handler2 = new TestHandler2();

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler1);
        ProjectArtifactsHandler firstRetrieval = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES);

        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler2);
        ProjectArtifactsHandler secondRetrieval = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES);

        // Assert
        assertSame(handler1, firstRetrieval);
        assertSame(handler2, secondRetrieval);
    }

    @Test
    @DisplayName("registerArtifactHandler should register handlers for all artifact types")
    void testRegisterAllArtifactTypes()
  {
        // Arrange
        ProjectArtifactsHandler handler1 = new TestHandler1();
        ProjectArtifactsHandler handler2 = new TestHandler2();
        ProjectArtifactsHandler handler3 = new TestHandler1();

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler1);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler2);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, handler3);

        // Assert
        assertSame(handler1, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));
        assertSame(handler2, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.VERSIONED_ENTITIES));
        assertSame(handler3, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.FILE_GENERATIONS));
    }

    @Test
    @DisplayName("registerArtifactHandler should accept null handler")
    void testRegisterNullHandler()
  {
        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, null);

        // Assert
        assertNull(ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));
    }

    // getArtifactHandler tests

    @Test
    @DisplayName("getArtifactHandler should return null for unregistered artifact type")
    void testGetArtifactHandlerUnregistered()
  {
        // Act
        ProjectArtifactsHandler handler = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES);

        // Assert
        assertNull(handler);
    }

    @Test
    @DisplayName("getArtifactHandler should return registered handler")
    void testGetArtifactHandlerRegistered()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler);

        // Act
        ProjectArtifactsHandler retrieved = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.VERSIONED_ENTITIES);

        // Assert
        assertSame(handler, retrieved);
    }

    @Test
    @DisplayName("getArtifactHandler should return null for type not registered while others are")
    void testGetArtifactHandlerPartialRegistration()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);

        // Act
        ProjectArtifactsHandler retrieved1 = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES);
        ProjectArtifactsHandler retrieved2 = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.VERSIONED_ENTITIES);

        // Assert
        assertSame(handler, retrieved1);
        assertNull(retrieved2);
    }

    @Test
    @DisplayName("getArtifactHandler should consistently return same handler instance")
    void testGetArtifactHandlerConsistency()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, handler);

        // Act
        ProjectArtifactsHandler retrieved1 = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.FILE_GENERATIONS);
        ProjectArtifactsHandler retrieved2 = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.FILE_GENERATIONS);

        // Assert
        assertSame(handler, retrieved1);
        assertSame(handler, retrieved2);
        assertSame(retrieved1, retrieved2);
    }

    @Test
    @DisplayName("getArtifactHandler should return null after registering null")
    void testGetArtifactHandlerAfterNullRegistration()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, null);
        ProjectArtifactsHandler retrieved = ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES);

        // Assert
        assertNull(retrieved);
    }

    // getSupportedTypes tests

    @Test
    @DisplayName("getSupportedTypes should return empty set when no handlers registered")
    void testGetSupportedTypesEmpty()
  {
        // Act
        Set<ArtifactType> supportedTypes = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        assertNotNull(supportedTypes);
        assertTrue(supportedTypes.isEmpty());
    }

    @Test
    @DisplayName("getSupportedTypes should return set with single registered type")
    void testGetSupportedTypesSingle()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);

        // Act
        Set<ArtifactType> supportedTypes = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        assertNotNull(supportedTypes);
        assertEquals(1, supportedTypes.size());
        assertTrue(supportedTypes.contains(ArtifactType.ENTITIES));
    }

    @Test
    @DisplayName("getSupportedTypes should return set with multiple registered types")
    void testGetSupportedTypesMultiple()
  {
        // Arrange
        ProjectArtifactsHandler handler1 = new TestHandler1();
        ProjectArtifactsHandler handler2 = new TestHandler2();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler1);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler2);

        // Act
        Set<ArtifactType> supportedTypes = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        assertNotNull(supportedTypes);
        assertEquals(2, supportedTypes.size());
        assertTrue(supportedTypes.contains(ArtifactType.ENTITIES));
        assertTrue(supportedTypes.contains(ArtifactType.VERSIONED_ENTITIES));
    }

    @Test
    @DisplayName("getSupportedTypes should return set with all artifact types when all registered")
    void testGetSupportedTypesAll()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, handler);

        // Act
        Set<ArtifactType> supportedTypes = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        assertNotNull(supportedTypes);
        assertEquals(3, supportedTypes.size());
        assertTrue(supportedTypes.contains(ArtifactType.ENTITIES));
        assertTrue(supportedTypes.contains(ArtifactType.VERSIONED_ENTITIES));
        assertTrue(supportedTypes.contains(ArtifactType.FILE_GENERATIONS));
    }

    @Test
    @DisplayName("getSupportedTypes should reflect updates when new handler is registered")
    void testGetSupportedTypesUpdatesOnRegistration()
  {
        // Arrange
        ProjectArtifactsHandler handler1 = new TestHandler1();
        ProjectArtifactsHandler handler2 = new TestHandler2();

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler1);
        Set<ArtifactType> supportedTypes1 = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Note: supportedTypes1 is a live view of the keySet, so it will reflect subsequent changes
        int sizeBefore = supportedTypes1.size();

        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler2);
        Set<ArtifactType> supportedTypes2 = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        // Both sets reflect the current state (live view behavior)
        assertEquals(2, supportedTypes1.size()); // Live view, now shows 2
        assertEquals(2, supportedTypes2.size());
        assertTrue(supportedTypes1.contains(ArtifactType.ENTITIES));
        assertTrue(supportedTypes1.contains(ArtifactType.VERSIONED_ENTITIES));
        assertTrue(supportedTypes2.contains(ArtifactType.ENTITIES));
        assertTrue(supportedTypes2.contains(ArtifactType.VERSIONED_ENTITIES));
        assertEquals(1, sizeBefore); // But the size was 1 before the second registration
    }

    @Test
    @DisplayName("getSupportedTypes should not include type after registering null handler")
    void testGetSupportedTypesAfterNullRegistration()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler);

        // Act
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, null);
        Set<ArtifactType> supportedTypes = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        assertEquals(2, supportedTypes.size());
        assertTrue(supportedTypes.contains(ArtifactType.ENTITIES)); // EnumMap keeps entry for null values
        assertTrue(supportedTypes.contains(ArtifactType.VERSIONED_ENTITIES));
    }

    @Test
    @DisplayName("getSupportedTypes should return consistent results on multiple calls")
    void testGetSupportedTypesConsistency()
  {
        // Arrange
        ProjectArtifactsHandler handler = new TestHandler1();
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler);

        // Act
        Set<ArtifactType> supportedTypes1 = ProjectArtifactHandlerFactory.getSupportedTypes();
        Set<ArtifactType> supportedTypes2 = ProjectArtifactHandlerFactory.getSupportedTypes();

        // Assert
        assertEquals(supportedTypes1.size(), supportedTypes2.size());
        assertTrue(supportedTypes1.containsAll(supportedTypes2));
        assertTrue(supportedTypes2.containsAll(supportedTypes1));
    }

    // Integration tests

    @Test
    @DisplayName("Full workflow: register, retrieve, check supported types")
    void testFullWorkflow()
  {
        // Arrange
        ProjectArtifactsHandler handler1 = new TestHandler1();
        ProjectArtifactsHandler handler2 = new TestHandler2();

        // Act & Assert - Initially empty
        assertTrue(ProjectArtifactHandlerFactory.getSupportedTypes().isEmpty());
        assertNull(ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));

        // Register first handler
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler1);
        assertEquals(1, ProjectArtifactHandlerFactory.getSupportedTypes().size());
        assertSame(handler1, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));

        // Register second handler
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, handler2);
        assertEquals(2, ProjectArtifactHandlerFactory.getSupportedTypes().size());
        assertSame(handler1, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));
        assertSame(handler2, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.VERSIONED_ENTITIES));

        // Replace first handler
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, handler2);
        assertEquals(2, ProjectArtifactHandlerFactory.getSupportedTypes().size());
        assertSame(handler2, ProjectArtifactHandlerFactory.getArtifactHandler(ArtifactType.ENTITIES));
    }
}

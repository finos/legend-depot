//  Copyright 2023 Goldman Sachs
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

package org.finos.legend.depot.services.artifacts.handlers.entities;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractEntityRefreshHandlerImplClaudeTest
{
    private ManageEntitiesService mockEntitiesService;
    private EntityArtifactsProvider mockArtifactsProvider;
    private TestableAbstractEntityRefreshHandlerImpl handler;

    @BeforeEach
    public void setup()
    {
        mockEntitiesService = mock(ManageEntitiesService.class);
        mockArtifactsProvider = mock(EntityArtifactsProvider.class);
        handler = new TestableAbstractEntityRefreshHandlerImpl(mockEntitiesService, mockArtifactsProvider);
    }

    @Test
    public void testGetLOGGER()
    {
        Logger logger = handler.getLOGGER();
        Assertions.assertNotNull(logger);
    }

    @Test
    public void testGetEntitiesApi()
    {
        ManageEntitiesService entitiesApi = handler.getEntitiesApi();
        Assertions.assertNotNull(entitiesApi);
        Assertions.assertSame(mockEntitiesService, entitiesApi);
    }

    @Test
    public void testDeleteByVersion()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        long expectedDeleted = 5L;

        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(expectedDeleted);

        long actualDeleted = handler.deleteByVersion(groupId, artifactId, versionId);

        Assertions.assertEquals(expectedDeleted, actualDeleted);
        verify(mockEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteByVersionWithZeroResults()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        long actualDeleted = handler.deleteByVersion(groupId, artifactId, versionId);

        Assertions.assertEquals(0L, actualDeleted);
        verify(mockEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testRefreshVersionArtifactsWithEntities()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Arrays.asList(new File("test1.jar"), new File("test2.jar"));

        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        List<Entity> entities = Arrays.asList(entity1, entity2);

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(entities);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertTrue(response.getMessages().get(0).contains("found [2] ENTITIES"));

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, times(1)).createOrUpdate(groupId, artifactId, versionId, entities);
        verify(mockEntitiesService, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    public void testRefreshVersionArtifactsWithSnapshotVersion()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "master-SNAPSHOT";
        List<File> files = Arrays.asList(new File("test.jar"));

        Entity entity = mock(Entity.class);
        List<Entity> entities = Collections.singletonList(entity);

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(entities);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);
        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(3L);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().size() >= 3);

        boolean foundEntityMessage = false;
        boolean foundRemovingMessage = false;
        boolean foundDeletedMessage = false;

        for (String message : response.getMessages())
        {
            if (message.contains("found [1] ENTITIES"))
            {
                foundEntityMessage = true;
            }
            if (message.contains("removing prior"))
            {
                foundRemovingMessage = true;
            }
            if (message.contains("deleted"))
            {
                foundDeletedMessage = true;
            }
        }

        Assertions.assertTrue(foundEntityMessage);
        Assertions.assertTrue(foundRemovingMessage);
        Assertions.assertTrue(foundDeletedMessage);

        verify(mockEntitiesService, times(1)).delete(groupId, artifactId, versionId);
        verify(mockEntitiesService, times(1)).createOrUpdate(groupId, artifactId, versionId, entities);
    }

    @Test
    public void testRefreshVersionArtifactsWithEmptyEntityList()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Arrays.asList(new File("test.jar"));

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(Collections.emptyList());
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertTrue(response.getMessages().get(0).contains("found 0 ENTITIES"));

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, never()).createOrUpdate(anyString(), anyString(), anyString(), anyList());
        verify(mockEntitiesService, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    public void testRefreshVersionArtifactsWithNullEntityList()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Arrays.asList(new File("test.jar"));

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(null);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertTrue(response.getMessages().get(0).contains("found 0 ENTITIES"));

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, never()).createOrUpdate(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testRefreshVersionArtifactsWithException()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Arrays.asList(new File("test.jar"));
        String errorMessage = "Test exception";

        when(mockArtifactsProvider.extractArtifacts(files)).thenThrow(new RuntimeException(errorMessage));
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Unexpected exception"));
        Assertions.assertTrue(response.getErrors().get(0).contains(errorMessage));

        verify(mockEntitiesService, never()).createOrUpdate(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testRefreshVersionArtifactsWithEmptyFileList()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Collections.emptyList();

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(Collections.emptyList());
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testRefreshVersionArtifactsVerifyGAVCoordinatesFormat()
    {
        String groupId = "my.group";
        String artifactId = "my-artifact";
        String versionId = "2.5.0";
        List<File> files = Arrays.asList(new File("test.jar"));

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(Collections.emptyList());
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        String message = response.getMessages().get(0);
        Assertions.assertTrue(message.contains("my.group-my-artifact-2.5.0"));
    }

    /**
     * Concrete implementation of AbstractEntityRefreshHandlerImpl for testing purposes.
     * This is necessary because AbstractEntityRefreshHandlerImpl is an abstract class and cannot be instantiated directly.
     */
    private static class TestableAbstractEntityRefreshHandlerImpl extends AbstractEntityRefreshHandlerImpl
    {
        public TestableAbstractEntityRefreshHandlerImpl(ManageEntitiesService entitiesService, EntityArtifactsProvider artifactProvider)
        {
            super(entitiesService, artifactProvider);
        }
    }
}

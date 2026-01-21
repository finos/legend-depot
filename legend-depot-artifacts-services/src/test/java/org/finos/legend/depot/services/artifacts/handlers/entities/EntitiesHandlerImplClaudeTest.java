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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntitiesHandlerImplClaudeTest
{
    private ManageEntitiesService mockEntitiesService;
    private EntityArtifactsProvider mockArtifactsProvider;
    private EntitiesHandlerImpl handler;

    @BeforeEach
    public void setup()
    {
        mockEntitiesService = mock(ManageEntitiesService.class);
        mockArtifactsProvider = mock(EntityArtifactsProvider.class);
        handler = new EntitiesHandlerImpl(mockEntitiesService, mockArtifactsProvider);
    }

    @Test
    public void testConstructor()
    {
        Assertions.assertNotNull(handler);
        Assertions.assertNotNull(handler.getEntitiesApi());
        Assertions.assertSame(mockEntitiesService, handler.getEntitiesApi());
    }

    @Test
    public void testConstructorWithNullServices()
    {
        EntitiesHandlerImpl handlerWithNulls = new EntitiesHandlerImpl(null, null);
        Assertions.assertNotNull(handlerWithNulls);
        Assertions.assertNull(handlerWithNulls.getEntitiesApi());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithEntities()
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

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().size() > 0);
        Assertions.assertTrue(response.getMessages().get(0).contains("found [2] ENTITIES"));

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, times(1)).createOrUpdate(groupId, artifactId, versionId, entities);
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithSnapshotVersion()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "master-SNAPSHOT";
        List<File> files = Collections.singletonList(new File("test.jar"));

        Entity entity = mock(Entity.class);
        List<Entity> entities = Collections.singletonList(entity);

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(entities);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);
        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(3L);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

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
    public void testRefreshProjectVersionArtifactsWithEmptyEntityList()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Collections.singletonList(new File("test.jar"));

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(Collections.emptyList());
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertTrue(response.getMessages().get(0).contains("found 0 ENTITIES"));

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, never()).createOrUpdate(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithNullEntityList()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Collections.singletonList(new File("test.jar"));

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(null);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertTrue(response.getMessages().get(0).contains("found 0 ENTITIES"));

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, never()).createOrUpdate(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithException()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Collections.singletonList(new File("test.jar"));
        String errorMessage = "Test exception during artifact extraction";

        when(mockArtifactsProvider.extractArtifacts(files)).thenThrow(new RuntimeException(errorMessage));
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Unexpected exception"));
        Assertions.assertTrue(response.getErrors().get(0).contains(errorMessage));

        verify(mockEntitiesService, never()).createOrUpdate(anyString(), anyString(), anyString(), anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithEmptyFileList()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        List<File> files = Collections.emptyList();

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(Collections.emptyList());
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().size() > 0);

        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithMultipleFiles()
    {
        String groupId = "org.example";
        String artifactId = "my-artifact";
        String versionId = "2.5.0";
        List<File> files = Arrays.asList(
            new File("file1.jar"),
            new File("file2.jar"),
            new File("file3.jar")
        );

        Entity entity = mock(Entity.class);
        List<Entity> entities = Collections.singletonList(entity);

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(entities);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockArtifactsProvider, times(1)).extractArtifacts(files);
        verify(mockEntitiesService, times(1)).createOrUpdate(groupId, artifactId, versionId, entities);
    }

    @Test
    public void testDelete()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(5L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithZeroResults()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithSnapshotVersion()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0-SNAPSHOT";

        when(mockEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(10L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithDifferentCoordinates()
    {
        String groupId1 = "org.example.group1";
        String artifactId1 = "artifact1";
        String versionId1 = "1.0.0";

        String groupId2 = "org.example.group2";
        String artifactId2 = "artifact2";
        String versionId2 = "2.0.0";

        when(mockEntitiesService.delete(groupId1, artifactId1, versionId1)).thenReturn(3L);
        when(mockEntitiesService.delete(groupId2, artifactId2, versionId2)).thenReturn(7L);

        handler.delete(groupId1, artifactId1, versionId1);
        handler.delete(groupId2, artifactId2, versionId2);

        verify(mockEntitiesService, times(1)).delete(groupId1, artifactId1, versionId1);
        verify(mockEntitiesService, times(1)).delete(groupId2, artifactId2, versionId2);
    }

    @Test
    public void testRefreshProjectVersionArtifactsVerifyCorrectDelegation()
    {
        String groupId = "com.example";
        String artifactId = "test-lib";
        String versionId = "3.0.0";
        List<File> files = Collections.singletonList(new File("artifact.jar"));

        Entity entity = mock(Entity.class);
        List<Entity> entities = Collections.singletonList(entity);

        when(mockArtifactsProvider.extractArtifacts(files)).thenReturn(entities);
        when(mockArtifactsProvider.getType()).thenReturn(ArtifactType.ENTITIES);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(groupId, artifactId, versionId, files);

        Assertions.assertNotNull(response);
        verify(mockEntitiesService).createOrUpdate(eq(groupId), eq(artifactId), eq(versionId), eq(entities));
    }
}

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

import org.finos.legend.depot.services.api.versionedEntities.ManageVersionedEntitiesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VersionedEntitiesHandlerImplClaudeTest
{
    private ManageVersionedEntitiesService mockVersionedEntitiesService;
    private VersionedEntityProvider mockArtifactsProvider;
    private VersionedEntitiesHandlerImpl handler;

    @BeforeEach
    public void setup()
    {
        mockVersionedEntitiesService = mock(ManageVersionedEntitiesService.class);
        mockArtifactsProvider = mock(VersionedEntityProvider.class);
        handler = new VersionedEntitiesHandlerImpl(mockVersionedEntitiesService, mockArtifactsProvider);
    }

    @Test
    public void testConstructor()
    {
        Assertions.assertNotNull(handler);
        Assertions.assertNotNull(handler.getEntitiesApi());
        Assertions.assertSame(mockVersionedEntitiesService, handler.getEntitiesApi());
    }

    @Test
    public void testConstructorWithNullServices()
    {
        VersionedEntitiesHandlerImpl handlerWithNulls = new VersionedEntitiesHandlerImpl(null, null);
        Assertions.assertNotNull(handlerWithNulls);
        Assertions.assertNull(handlerWithNulls.getEntitiesApi());
    }

    @Test
    public void testDelete()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(5L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithZeroResults()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithSnapshotVersion()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0-SNAPSHOT";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(10L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
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

        when(mockVersionedEntitiesService.delete(groupId1, artifactId1, versionId1)).thenReturn(3L);
        when(mockVersionedEntitiesService.delete(groupId2, artifactId2, versionId2)).thenReturn(7L);

        handler.delete(groupId1, artifactId1, versionId1);
        handler.delete(groupId2, artifactId2, versionId2);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId1, artifactId1, versionId1);
        verify(mockVersionedEntitiesService, times(1)).delete(groupId2, artifactId2, versionId2);
    }

    @Test
    public void testDeleteWithNullGroupId()
    {
        String groupId = null;
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithNullArtifactId()
    {
        String groupId = "test.group";
        String artifactId = null;
        String versionId = "1.0.0";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithNullVersionId()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = null;

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteWithEmptyStrings()
    {
        String groupId = "";
        String artifactId = "";
        String versionId = "";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(0L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(1)).delete(groupId, artifactId, versionId);
    }

    @Test
    public void testDeleteMultipleTimes()
    {
        String groupId = "test.group";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockVersionedEntitiesService.delete(groupId, artifactId, versionId)).thenReturn(5L);

        handler.delete(groupId, artifactId, versionId);
        handler.delete(groupId, artifactId, versionId);
        handler.delete(groupId, artifactId, versionId);

        verify(mockVersionedEntitiesService, times(3)).delete(groupId, artifactId, versionId);
    }
}

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

package org.finos.legend.depot.services.artifacts.handlers.generations;

import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.services.api.generations.ManageFileGenerationsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileGenerationHandlerImplClaude_deleteTest
{
    private ArtifactRepository mockRepository;
    private FileGenerationsArtifactsProvider mockProvider;
    private ManageFileGenerationsService mockGenerations;
    private FileGenerationHandlerImpl handler;

    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";
    private static final String TEST_VERSION_ID = "1.0.0";

    @BeforeEach
    public void setup()
    {
        mockRepository = mock(ArtifactRepository.class);
        mockProvider = mock(FileGenerationsArtifactsProvider.class);
        mockGenerations = mock(ManageFileGenerationsService.class);
        handler = new FileGenerationHandlerImpl(mockRepository, mockProvider, mockGenerations);
    }

    @Test
    public void testDeleteWithValidCoordinates()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(5L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteWithNoGenerationsFound()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(0L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteWithSnapshotVersion()
    {
        String snapshotVersion = "master-SNAPSHOT";
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion)).thenReturn(10L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);
    }

    @Test
    public void testDeleteWithDifferentVersions()
    {
        String version1 = "1.0.0";
        String version2 = "2.0.0";

        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, version1)).thenReturn(3L);
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, version2)).thenReturn(7L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, version1);
        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, version2);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, version1);
        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, version2);
    }

    @Test
    public void testDeleteWithException()
    {
        String errorMessage = "Database connection failed";
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID))
            .thenThrow(new RuntimeException(errorMessage));

        Assertions.assertThrows(RuntimeException.class, () -> {
            handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        });

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteWithNullGroupId()
    {
        when(mockGenerations.delete(null, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(0L);

        handler.delete(null, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockGenerations, times(1)).delete(null, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteWithNullArtifactId()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, null, TEST_VERSION_ID)).thenReturn(0L);

        handler.delete(TEST_GROUP_ID, null, TEST_VERSION_ID);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, null, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteWithNullVersionId()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, null)).thenReturn(0L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, null);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, null);
    }

    @Test
    public void testDeleteWithAllNullParameters()
    {
        when(mockGenerations.delete(null, null, null)).thenReturn(0L);

        handler.delete(null, null, null);

        verify(mockGenerations, times(1)).delete(null, null, null);
    }

    @Test
    public void testDeleteMultipleTimes()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID))
            .thenReturn(5L)
            .thenReturn(3L)
            .thenReturn(0L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockGenerations, times(3)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteVerifiesCorrectDelegation()
    {
        String groupId = "com.example";
        String artifactId = "test-lib";
        String versionId = "3.0.0";

        when(mockGenerations.delete(groupId, artifactId, versionId)).thenReturn(15L);

        handler.delete(groupId, artifactId, versionId);

        verify(mockGenerations).delete(eq(groupId), eq(artifactId), eq(versionId));
    }

    @Test
    public void testDeleteDoesNotCallOtherMethods()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(5L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockGenerations, never()).createOrUpdate(anyList());
        verify(mockGenerations, never()).getAll();
    }

    @Test
    public void testDeleteWithEmptyStrings()
    {
        when(mockGenerations.delete("", "", "")).thenReturn(0L);

        handler.delete("", "", "");

        verify(mockGenerations, times(1)).delete("", "", "");
    }

    @Test
    public void testDeleteWithSpecialCharactersInCoordinates()
    {
        String specialGroupId = "test.group-special_chars";
        String specialArtifactId = "artifact-with-dashes_and_underscores";
        String specialVersionId = "1.0.0-RC1";

        when(mockGenerations.delete(specialGroupId, specialArtifactId, specialVersionId)).thenReturn(2L);

        handler.delete(specialGroupId, specialArtifactId, specialVersionId);

        verify(mockGenerations, times(1)).delete(specialGroupId, specialArtifactId, specialVersionId);
    }

    @Test
    public void testDeleteWithLargeReturnValue()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(1000000L);

        handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteDoesNotThrowWhenSuccessful()
    {
        when(mockGenerations.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(5L);

        Assertions.assertDoesNotThrow(() -> {
            handler.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        });
    }

    @Test
    public void testDeleteWithDifferentArtifactIds()
    {
        String artifactId1 = "artifact1";
        String artifactId2 = "artifact2";
        String artifactId3 = "artifact3";

        when(mockGenerations.delete(TEST_GROUP_ID, artifactId1, TEST_VERSION_ID)).thenReturn(1L);
        when(mockGenerations.delete(TEST_GROUP_ID, artifactId2, TEST_VERSION_ID)).thenReturn(2L);
        when(mockGenerations.delete(TEST_GROUP_ID, artifactId3, TEST_VERSION_ID)).thenReturn(3L);

        handler.delete(TEST_GROUP_ID, artifactId1, TEST_VERSION_ID);
        handler.delete(TEST_GROUP_ID, artifactId2, TEST_VERSION_ID);
        handler.delete(TEST_GROUP_ID, artifactId3, TEST_VERSION_ID);

        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, artifactId1, TEST_VERSION_ID);
        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, artifactId2, TEST_VERSION_ID);
        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, artifactId3, TEST_VERSION_ID);
    }
}

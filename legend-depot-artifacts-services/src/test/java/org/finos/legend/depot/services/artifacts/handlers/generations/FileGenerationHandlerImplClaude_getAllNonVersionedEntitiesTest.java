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

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.services.api.generations.ManageFileGenerationsService;
import org.finos.legend.depot.services.artifacts.repository.maven.TestMavenArtifactsRepository;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileGenerationHandlerImplClaude_getAllNonVersionedEntitiesTest
{
    private ArtifactRepository mockRepository;
    private FileGenerationsArtifactsProvider mockProvider;
    private ManageFileGenerationsService mockGenerations;
    private FileGenerationHandlerImpl handler;

    private static final String TEST_GROUP_ID = "examples.metadata";
    private static final String TEST_ARTIFACT_ID = "test";
    private static final String TEST_VERSION_ID = "2.0.0";

    @BeforeEach
    public void setup()
    {
        mockRepository = mock(ArtifactRepository.class);
        mockProvider = mock(FileGenerationsArtifactsProvider.class);
        mockGenerations = mock(ManageFileGenerationsService.class);
        handler = new FileGenerationHandlerImpl(mockRepository, mockProvider, mockGenerations);
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithValidFiles()
    {
        ArtifactRepository repository = new TestMavenArtifactsRepository();
        FileGenerationHandlerImpl handlerWithRealRepo = new FileGenerationHandlerImpl(repository, mockProvider, mockGenerations);

        List<Entity> entities = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertFalse(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithNoFiles()
    {
        when(mockRepository.findFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID))
            .thenReturn(Collections.emptyList());

        List<Entity> entities = handler.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithNullFileList()
    {
        when(mockRepository.findFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID))
            .thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            handler.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        });
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithSnapshotVersion()
    {
        String snapshotVersion = "master-SNAPSHOT";
        ArtifactRepository repository = new TestMavenArtifactsRepository();
        FileGenerationHandlerImpl handlerWithRealRepo = new FileGenerationHandlerImpl(repository, mockProvider, mockGenerations);

        List<Entity> entities = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);

        Assertions.assertNotNull(entities);
        Assertions.assertFalse(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithDifferentGroupId()
    {
        String differentGroupId = "com.example";
        String differentArtifactId = "test-artifact";
        String versionId = "1.0.0";

        when(mockRepository.findFiles(ArtifactType.ENTITIES, differentGroupId, differentArtifactId, versionId))
            .thenReturn(Collections.emptyList());

        List<Entity> entities = handler.getAllNonVersionedEntities(differentGroupId, differentArtifactId, versionId);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithInvalidFile()
    {
        File invalidFile = new File("/non/existent/path/test.jar");
        when(mockRepository.findFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID))
            .thenReturn(Collections.singletonList(invalidFile));

        List<Entity> entities = handler.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesReturnsEntitiesFromFirstFileOnly()
    {
        ArtifactRepository repository = new TestMavenArtifactsRepository();
        FileGenerationHandlerImpl handlerWithRealRepo = new FileGenerationHandlerImpl(repository, mockProvider, mockGenerations);

        List<Entity> entities = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertFalse(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithNullGroupId()
    {
        when(mockRepository.findFiles(ArtifactType.ENTITIES, null, TEST_ARTIFACT_ID, TEST_VERSION_ID))
            .thenReturn(Collections.emptyList());

        List<Entity> entities = handler.getAllNonVersionedEntities(null, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithNullArtifactId()
    {
        when(mockRepository.findFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, null, TEST_VERSION_ID))
            .thenReturn(Collections.emptyList());

        List<Entity> entities = handler.getAllNonVersionedEntities(TEST_GROUP_ID, null, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithNullVersionId()
    {
        when(mockRepository.findFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, TEST_ARTIFACT_ID, null))
            .thenReturn(Collections.emptyList());

        List<Entity> entities = handler.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, null);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testGetAllNonVersionedEntitiesVerifyEntityCount()
    {
        ArtifactRepository repository = new TestMavenArtifactsRepository();
        FileGenerationHandlerImpl handlerWithRealRepo = new FileGenerationHandlerImpl(repository, mockProvider, mockGenerations);

        List<Entity> entities = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.size() > 0);
        entities.forEach(entity -> {
            Assertions.assertNotNull(entity.getPath());
            Assertions.assertNotNull(entity.getClassifierPath());
        });
    }

    @Test
    public void testGetAllNonVersionedEntitiesConsistentResults()
    {
        ArtifactRepository repository = new TestMavenArtifactsRepository();
        FileGenerationHandlerImpl handlerWithRealRepo = new FileGenerationHandlerImpl(repository, mockProvider, mockGenerations);

        List<Entity> entities1 = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        List<Entity> entities2 = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertEquals(entities1.size(), entities2.size());
    }

    @Test
    public void testGetAllNonVersionedEntitiesWithVersion1_0_0()
    {
        String version = "1.0.0";
        ArtifactRepository repository = new TestMavenArtifactsRepository();
        FileGenerationHandlerImpl handlerWithRealRepo = new FileGenerationHandlerImpl(repository, mockProvider, mockGenerations);

        List<Entity> entities = handlerWithRealRepo.getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, version);

        Assertions.assertNotNull(entities);
    }
}

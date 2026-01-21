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
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.services.api.generations.ManageFileGenerationsService;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileGenerationHandlerImplClaude_refreshProjectVersionArtifactsTest
{
    private FileGenerationsArtifactsProvider mockProvider;
    private ManageFileGenerationsService mockGenerations;
    private FileGenerationHandlerImpl handler;

    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";
    private static final String TEST_VERSION_ID = "1.0.0";

    @BeforeEach
    public void setup()
    {
        mockProvider = mock(FileGenerationsArtifactsProvider.class);
        mockGenerations = mock(ManageFileGenerationsService.class);
        handler = spy(new FileGenerationHandlerImpl(null, mockProvider, mockGenerations));
        when(mockProvider.getType()).thenReturn(ArtifactType.FILE_GENERATIONS);
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithException()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));
        String errorMessage = "Failed to extract artifacts";

        when(mockProvider.extractArtifacts(files)).thenThrow(new RuntimeException(errorMessage));
        doReturn(Collections.emptyList()).when(handler).getAllNonVersionedEntities(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error processing generations update"));
        Assertions.assertTrue(response.getErrors().get(0).contains(errorMessage));

        verify(mockGenerations, never()).createOrUpdate(anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithEmptyFiles()
    {
        List<File> files = Collections.emptyList();
        List<Entity> entities = Collections.emptyList();
        List<DepotGeneration> generations = Collections.emptyList();

        when(mockProvider.extractArtifacts(files)).thenReturn(generations);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().size() > 0);
        Assertions.assertTrue(response.getMessages().stream().anyMatch(m -> m.contains("new [0] generations")));
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithSnapshotVersionDeletesPriorArtifacts()
    {
        String snapshotVersion = "master-SNAPSHOT";
        List<File> files = Collections.singletonList(new File("test.jar"));
        List<Entity> entities = Collections.emptyList();
        List<DepotGeneration> generations = Collections.emptyList();

        when(mockProvider.extractArtifacts(files)).thenReturn(generations);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockGenerations, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);
        Assertions.assertTrue(response.getMessages().stream().anyMatch(m -> m.contains("removing prior")));
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithUnknownType()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));

        Map<String, Object> entityContent = new HashMap<>();
        Entity entity = new EntityDefinition("examples::test::MyClass", "", entityContent);
        List<Entity> entities = Collections.singletonList(entity);

        // Create a generated file that will result in UNKNOWN_TYPE
        // The path pattern should not match the expected format for extracting type
        DepotGeneration gen = new DepotGeneration("/examples/test/MyClass/file.txt", "content");
        List<DepotGeneration> generatedFiles = Collections.singletonList(gen);

        when(mockProvider.extractArtifacts(files)).thenReturn(generatedFiles);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("Generation type for file") && e.contains("is not present")));
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithFileGenerationEntities()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));

        Map<String, Object> content = new HashMap<>();
        content.put("type", "java");
        content.put("generationOutputPath", "/gen/output");

        Entity fileGenEntity = new EntityDefinition(
            "examples::metadata::test::FileGeneration",
            "meta::pure::generation::metamodel::GenerationConfiguration",
            content
        );
        List<Entity> entities = Collections.singletonList(fileGenEntity);

        DepotGeneration gen = new DepotGeneration("/gen/output/MyClass.java", "public class MyClass {}");
        List<DepotGeneration> generatedFiles = Collections.singletonList(gen);

        when(mockProvider.extractArtifacts(files)).thenReturn(generatedFiles);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockGenerations, times(1)).createOrUpdate(anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithMultipleEntitiesAndGenerations()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));

        Map<String, Object> content1 = new HashMap<>();
        content1.put("type", "java");

        Map<String, Object> content2 = new HashMap<>();
        content2.put("type", "avro");

        Entity entity1 = new EntityDefinition("examples::model::Class1", "", content1);
        Entity entity2 = new EntityDefinition("examples::model::Class2", "", content2);
        List<Entity> entities = Arrays.asList(entity1, entity2);

        DepotGeneration gen1 = new DepotGeneration("/examples/model/Class1/java/Class1.java", "class1");
        DepotGeneration gen2 = new DepotGeneration("/examples/model/Class2/avro/Class2.avro", "class2");
        List<DepotGeneration> generatedFiles = Arrays.asList(gen1, gen2);

        when(mockProvider.extractArtifacts(files)).thenReturn(generatedFiles);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockGenerations, times(1)).createOrUpdate(anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWhenCreateOrUpdateThrowsException()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));
        List<Entity> entities = Collections.emptyList();
        List<DepotGeneration> generations = Collections.emptyList();

        when(mockProvider.extractArtifacts(files)).thenReturn(generations);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        doThrow(new RuntimeException("Database error")).when(mockGenerations).createOrUpdate(anyList());

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error processing generations update"));
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithNullFiles()
    {
        List<File> files = null;

        when(mockProvider.extractArtifacts(files)).thenThrow(new NullPointerException("Files cannot be null"));
        doReturn(Collections.emptyList()).when(handler).getAllNonVersionedEntities(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithGenerationPathFromEntityPath()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));

        Map<String, Object> content = new HashMap<>();
        content.put("type", "protobuf");
        // No generationOutputPath - should use entity path

        Entity fileGenEntity = new EntityDefinition(
            "examples::metadata::test::ProtoGen",
            "meta::pure::generation::metamodel::GenerationConfiguration",
            content
        );
        List<Entity> entities = Collections.singletonList(fileGenEntity);

        // Generation should match the entity path converted to file path with underscores
        DepotGeneration gen = new DepotGeneration("/examples_metadata_test_ProtoGen/message.proto", "message Proto {}");
        List<DepotGeneration> generatedFiles = Collections.singletonList(gen);

        when(mockProvider.extractArtifacts(files)).thenReturn(generatedFiles);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockGenerations, times(1)).createOrUpdate(anyList());
    }

    @Test
    public void testRefreshProjectVersionArtifactsWithSnapshotAndException()
    {
        String snapshotVersion = "1.0.0-SNAPSHOT";
        List<File> files = Collections.singletonList(new File("test.jar"));

        doThrow(new RuntimeException("Delete failed")).when(mockGenerations).delete(anyString(), anyString(), anyString());
        doReturn(Collections.emptyList()).when(handler).getAllNonVersionedEntities(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion, files);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
    }

    @Test
    public void testRefreshProjectVersionArtifactsSuccessfullyCreatesStoredGenerations()
    {
        List<File> files = Collections.singletonList(new File("test.jar"));

        Map<String, Object> entityContent = new HashMap<>();
        Entity entity = new EntityDefinition("examples::test::Entity", "", entityContent);
        List<Entity> entities = Collections.singletonList(entity);

        DepotGeneration gen = new DepotGeneration("/examples/test/Entity/java/Entity.java", "class Entity {}");
        List<DepotGeneration> generatedFiles = Collections.singletonList(gen);

        when(mockProvider.extractArtifacts(files)).thenReturn(generatedFiles);
        doReturn(entities).when(handler).getAllNonVersionedEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(
            TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID, files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().stream().anyMatch(m -> m.contains("new [1] generations")));
        verify(mockGenerations, times(1)).createOrUpdate(anyList());
    }
}

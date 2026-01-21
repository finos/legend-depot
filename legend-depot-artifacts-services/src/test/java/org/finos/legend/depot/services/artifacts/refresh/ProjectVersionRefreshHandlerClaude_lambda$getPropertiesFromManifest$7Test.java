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

package org.finos.legend.depot.services.artifacts.refresh;

import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectVersionRefreshHandlerClaude_lambda$getPropertiesFromManifest$7Test
{
    private ManageProjectsService mockProjectsService;
    private ArtifactRepository mockArtifactRepository;
    private Queue mockQueue;
    private ArtifactsFilesStore mockArtifactsFilesStore;
    private IncludeProjectPropertiesConfiguration mockIncludeConfig;
    private RefreshDependenciesService mockRefreshDependenciesService;
    private ProjectVersionRefreshHandler handler;

    private static final String TEST_PROJECT_ID = "test-project";
    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";
    private static final String TEST_VERSION = "1.0.0";
    private static final int MAX_SNAPSHOTS_ALLOWED = 5;

    @BeforeEach
    public void setup()
    {
        mockProjectsService = mock(ManageProjectsService.class);
        mockArtifactRepository = mock(ArtifactRepository.class);
        mockQueue = mock(Queue.class);
        mockArtifactsFilesStore = mock(ArtifactsFilesStore.class);
        mockIncludeConfig = mock(IncludeProjectPropertiesConfiguration.class);
        mockRefreshDependenciesService = mock(RefreshDependenciesService.class);

        handler = new ProjectVersionRefreshHandler(
                mockProjectsService,
                mockArtifactRepository,
                mockQueue,
                mockArtifactsFilesStore,
                mockIncludeConfig,
                mockRefreshDependenciesService,
                MAX_SNAPSHOTS_ALLOWED
        );
    }

    @Test
    public void testLambdaExecutionWithManifestAttributesMatchingExactProperty() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        File testJarFile = createTestJarFileWithManifestAttributes();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Test-Key"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID + "-entities"), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjectsService).createOrUpdate(any(StoreProjectVersionData.class));

        testJarFile.delete();
    }

    @Test
    public void testLambdaExecutionWithManifestAttributesMatchingRegexPattern() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        File testJarFile = createTestJarFileWithMultipleAttributes();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Build-.*"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID + "-entities"), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjectsService).createOrUpdate(any(StoreProjectVersionData.class));

        testJarFile.delete();
    }

    @Test
    public void testLambdaExecutionWithMultipleManifestAttributesAndMixedMatching() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        File testJarFile = createTestJarFileWithMixedMatchingAttributes();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Exact-Match", "Prefix-.*"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID + "-entities"), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjectsService).createOrUpdate(any(StoreProjectVersionData.class));

        testJarFile.delete();
    }

    @Test
    public void testLambdaExecutionWithNoMatchingManifestAttributes() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        File testJarFile = createTestJarFileWithNonMatchingAttributes();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Non-Existing-Key"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID + "-entities"), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjectsService).createOrUpdate(any(StoreProjectVersionData.class));

        testJarFile.delete();
    }

    @Test
    public void testLambdaExecutionWithComplexRegexPatternMatching() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        File testJarFile = createTestJarFileWithComplexAttributes();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("(Implementation|Specification)-.*"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID + "-entities"), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjectsService).createOrUpdate(any(StoreProjectVersionData.class));

        testJarFile.delete();
    }

    private File createTestJarFileWithManifestAttributes() throws Exception
    {
        File tempJar = File.createTempFile("test-lambda-manifest", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Test-Key"), "Test-Value");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
        }

        return tempJar;
    }

    private File createTestJarFileWithMultipleAttributes() throws Exception
    {
        File tempJar = File.createTempFile("test-lambda-multi", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Build-Version"), "1.0.0");
        manifest.getMainAttributes().put(new Attributes.Name("Build-Timestamp"), "2024-01-01");
        manifest.getMainAttributes().put(new Attributes.Name("Build-User"), "test-user");
        manifest.getMainAttributes().put(new Attributes.Name("Other-Attribute"), "other-value");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
        }

        return tempJar;
    }

    private File createTestJarFileWithMixedMatchingAttributes() throws Exception
    {
        File tempJar = File.createTempFile("test-lambda-mixed", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Exact-Match"), "exact-value");
        manifest.getMainAttributes().put(new Attributes.Name("Prefix-One"), "prefix-value-1");
        manifest.getMainAttributes().put(new Attributes.Name("Prefix-Two"), "prefix-value-2");
        manifest.getMainAttributes().put(new Attributes.Name("No-Match"), "no-match-value");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
        }

        return tempJar;
    }

    private File createTestJarFileWithNonMatchingAttributes() throws Exception
    {
        File tempJar = File.createTempFile("test-lambda-nomatch", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Some-Key"), "some-value");
        manifest.getMainAttributes().put(new Attributes.Name("Another-Key"), "another-value");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
        }

        return tempJar;
    }

    private File createTestJarFileWithComplexAttributes() throws Exception
    {
        File tempJar = File.createTempFile("test-lambda-complex", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Implementation-Version"), "2.0.0");
        manifest.getMainAttributes().put(new Attributes.Name("Implementation-Title"), "Test Implementation");
        manifest.getMainAttributes().put(new Attributes.Name("Specification-Version"), "3.0");
        manifest.getMainAttributes().put(new Attributes.Name("Specification-Vendor"), "Test Vendor");
        manifest.getMainAttributes().put(new Attributes.Name("Unrelated-Attribute"), "unrelated");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
        }

        return tempJar;
    }
}

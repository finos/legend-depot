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

import org.apache.maven.model.Model;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
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
import static org.mockito.Mockito.when;

public class ProjectVersionRefreshHandlerClaude_updateProjectVersionDataTest
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
    public void testUpdateProjectVersionDataWithManifestProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        File testJarFile = createTestJarFileWithManifest();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Implementation-Version"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), anyString(), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());

        testJarFile.delete();
    }

    private File createTestJarFileWithManifest() throws Exception
    {
        File tempJar = File.createTempFile("test-manifest", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Implementation-Version"), "1.0.0");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
            // Empty jar with just manifest
        }

        return tempJar;
    }

    @Test
    public void testUpdateProjectVersionDataWithBothProjectAndManifestProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        java.util.Properties pomProperties = new java.util.Properties();
        pomProperties.setProperty("project.version", "1.0.0");
        pomProperties.setProperty("project.name", "test-project");

        File testJarFile = createTestJarFileWithManifest();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("project.version", "project.name"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Implementation-Version", "Build-Timestamp"));
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), anyString(), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());

        testJarFile.delete();
    }

    @Test
    public void testUpdateProjectVersionDataWithRegexPatternProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        java.util.Properties pomProperties = new java.util.Properties();
        pomProperties.setProperty("legend.version", "2.0.0");
        pomProperties.setProperty("legend.sdlc.version", "1.5.0");
        pomProperties.setProperty("other.property", "value");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("legend\\..*"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testUpdateProjectVersionDataWithNullPOM() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("some.property"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(null);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testUpdateProjectVersionDataWithNullManifest() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Implementation-Version"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), anyString(), eq(TEST_VERSION)))
                .thenReturn(null);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testUpdateProjectVersionDataWithRegexManifestProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        File testJarFile = createTestJarFileWithBundleManifest();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Arrays.asList("Bundle-.*"));
        when(mockArtifactRepository.getJarFile(eq(TEST_GROUP_ID), anyString(), eq(TEST_VERSION)))
                .thenReturn(testJarFile);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());

        testJarFile.delete();
    }

    private File createTestJarFileWithBundleManifest() throws Exception
    {
        File tempJar = File.createTempFile("test-bundle-manifest", ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Bundle-Version"), "1.0.0");
        manifest.getMainAttributes().put(new Attributes.Name("Bundle-Name"), "test-bundle");
        manifest.getMainAttributes().put(new Attributes.Name("Other-Attribute"), "value");

        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(tempJar), manifest))
        {
            // Empty jar with just manifest
        }

        return tempJar;
    }
}

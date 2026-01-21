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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectVersionRefreshHandlerClaude_calculateProjectPropertiesTest
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
    public void testCalculateProjectPropertiesWithSingleExactMatchProperty() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("legend.version", "1.0.0");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("legend.version"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithMultipleProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("legend.version", "1.0.0");
        pomProperties.setProperty("legend.sdlc.version", "2.0.0");
        pomProperties.setProperty("project.name", "test-project");
        pomProperties.setProperty("project.description", "A test project");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("legend.version", "legend.sdlc.version", "project.name", "project.description"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithRegexMatch() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("legend.version", "1.0.0");
        pomProperties.setProperty("legend.sdlc.version", "2.0.0");
        pomProperties.setProperty("legend.engine.version", "3.0.0");
        pomProperties.setProperty("other.property", "value");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
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
    public void testCalculateProjectPropertiesWithMixedExactAndRegexMatches() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("legend.version", "1.0.0");
        pomProperties.setProperty("legend.sdlc.version", "2.0.0");
        pomProperties.setProperty("project.version", "3.0.0");
        pomProperties.setProperty("other.property", "value");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("project.version", "legend\\..*"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithNoMatchingProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("other.property1", "value1");
        pomProperties.setProperty("other.property2", "value2");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("legend.version"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithEmptyPomProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("legend.version"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithComplexRegexPattern() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("project.build.sourceEncoding", "UTF-8");
        pomProperties.setProperty("project.reporting.outputEncoding", "UTF-8");
        pomProperties.setProperty("project.version", "1.0.0");
        pomProperties.setProperty("legend.version", "2.0.0");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("project\\.(build|reporting)\\..*"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithSpecialCharactersInPropertyNames() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("maven.compiler.source", "11");
        pomProperties.setProperty("maven.compiler.target", "11");
        pomProperties.setProperty("maven.test.skip", "false");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("maven.compiler.source", "maven.compiler.target"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testCalculateProjectPropertiesWithPropertyValuesContainingSpecialCharacters() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        Model mockModel = mock(Model.class);
        Properties pomProperties = new Properties();
        pomProperties.setProperty("project.url", "https://example.com/project");
        pomProperties.setProperty("project.description", "A project with special chars: @#$%^&*()");
        pomProperties.setProperty("project.scm.url", "git@github.com:org/repo.git");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("project.url", "project.description", "project.scm.url"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(pomProperties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }
}

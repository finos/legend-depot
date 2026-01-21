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
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectVersionRefreshHandlerClaudeTest
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
    private static final String INVALID_GROUP_ID = "invalid group id";
    private static final String INVALID_ARTIFACT_ID = "invalid artifact id";
    private static final String INVALID_VERSION = "invalid.version";
    private static final String SNAPSHOT_VERSION = "1.0.0-SNAPSHOT";
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
    public void testConstructorWithValidParameters()
    {
        Assertions.assertNotNull(handler);
    }

    @Test
    public void testConstructorWithNullIncludeConfig()
    {
        ProjectVersionRefreshHandler handlerWithNull = new ProjectVersionRefreshHandler(
                mockProjectsService,
                mockArtifactRepository,
                mockQueue,
                mockArtifactsFilesStore,
                null,
                mockRefreshDependenciesService,
                MAX_SNAPSHOTS_ALLOWED
        );
        Assertions.assertNotNull(handlerWithNull);
    }

    @Test
    public void testHandleNotificationWithNewProject() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData newProject = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(newProject));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.handleNotification(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjectsService, org.mockito.Mockito.atLeastOnce()).createOrUpdate(any(StoreProjectData.class));
    }

    @Test
    public void testHandleNotificationWithExistingProject() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData existingProject = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(existingProject));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.handleNotification(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testValidateWithValidNotification()
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

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertEquals(0, errors.size());
    }

    @Test
    public void testValidateWithInvalidGroupId()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                INVALID_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() > 0);
        Assertions.assertTrue(errors.stream().anyMatch(e -> e.contains("invalid groupId")));
    }

    @Test
    public void testValidateWithInvalidArtifactId()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                INVALID_ARTIFACT_ID,
                TEST_VERSION
        );

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() > 0);
        Assertions.assertTrue(errors.stream().anyMatch(e -> e.contains("invalid artifactId")));
    }

    @Test
    public void testValidateWithInvalidVersionId()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                INVALID_VERSION
        );

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() > 0);
        Assertions.assertTrue(errors.stream().anyMatch(e -> e.contains("invalid versionId")));
    }

    @Test
    public void testValidateWithMultipleInvalidFields()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                INVALID_GROUP_ID,
                INVALID_ARTIFACT_ID,
                INVALID_VERSION
        );

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() >= 3);
    }

    @Test
    public void testValidateWithMismatchedProjectId()
    {
        MetadataNotification notification = new MetadataNotification(
                "different-project-id",
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() > 0);
        Assertions.assertTrue(errors.stream().anyMatch(e -> e.contains("Invalid projectId")));
    }

    @Test
    public void testValidateWithSnapshotVersionWithinLimit()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                SNAPSHOT_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));

        List<StoreProjectVersionData> existingSnapshots = new ArrayList<>();
        for (int i = 0; i < MAX_SNAPSHOTS_ALLOWED - 1; i++)
        {
            StoreProjectVersionData versionData = new StoreProjectVersionData(
                    TEST_GROUP_ID,
                    TEST_ARTIFACT_ID,
                    "snapshot-" + i + "-SNAPSHOT"
            );
            versionData.setEvicted(false);
            existingSnapshots.add(versionData);
        }

        when(mockProjectsService.findSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(existingSnapshots);

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertEquals(0, errors.size());
    }

    @Test
    public void testValidateWithSnapshotVersionExceedingLimit()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                "new-SNAPSHOT"
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));

        List<StoreProjectVersionData> existingSnapshots = new ArrayList<>();
        for (int i = 0; i < MAX_SNAPSHOTS_ALLOWED; i++)
        {
            StoreProjectVersionData versionData = new StoreProjectVersionData(
                    TEST_GROUP_ID,
                    TEST_ARTIFACT_ID,
                    "snapshot-" + i + "-SNAPSHOT"
            );
            versionData.setEvicted(false);
            existingSnapshots.add(versionData);
        }

        when(mockProjectsService.findSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(existingSnapshots);

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() > 0);
        Assertions.assertTrue(errors.stream().anyMatch(e -> e.contains("reached the limit")));
    }

    @Test
    public void testValidateWithExistingSnapshotVersion()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                SNAPSHOT_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));

        List<StoreProjectVersionData> existingSnapshots = new ArrayList<>();
        for (int i = 0; i < MAX_SNAPSHOTS_ALLOWED; i++)
        {
            StoreProjectVersionData versionData = new StoreProjectVersionData(
                    TEST_GROUP_ID,
                    TEST_ARTIFACT_ID,
                    i == 0 ? SNAPSHOT_VERSION : "snapshot-" + i + "-SNAPSHOT"
            );
            versionData.setEvicted(false);
            existingSnapshots.add(versionData);
        }

        when(mockProjectsService.findSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(existingSnapshots);

        List<String> errors = handler.validate(notification);

        Assertions.assertNotNull(errors);
        Assertions.assertEquals(0, errors.size());
    }

    @Test
    public void testExecuteWithTraceExecutesSupplierSuccessfully()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Test message");

        MetadataNotificationResponse response = handler.executeWithTrace(
                "test-label",
                notification,
                () -> expectedResponse
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertEquals("Test message", response.getMessages().get(0));
    }

    @Test
    public void testDoRefreshWithValidNotificationAndNoDependencies() throws Exception
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
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testDoRefreshWithMissingVersion() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.empty());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("does not exist")));
    }

    @Test
    public void testDoRefreshWithMissingProject()
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.empty());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("No Project")));
    }

    @Test
    public void testDoRefreshWithRepositoryException() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenThrow(new ArtifactRepositoryException("Repository error"));

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("Repository error")));
    }

    @Test
    public void testDoRefreshWithDependencyValidationErrors() throws Exception
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
                .thenReturn(Arrays.asList("Dependency validation error"));

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("Dependency validation error")));
    }

    @Test
    public void testDoRefreshWithNonTransitiveDependencies() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                false,
                false,
                null
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        ProjectVersion dependency = new ProjectVersion("dep.group", "dep-artifact", "1.0.0");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Arrays.asList(dependency));
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockProjectsService.find("dep.group", "dep-artifact", "1.0.0"))
                .thenReturn(Optional.empty());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("not found in store")));
    }

    @Test
    public void testDoRefreshWithTransitiveDependencies() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                false,
                true,
                "parent-event-123"
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        StoreProjectData dependentProject = new StoreProjectData("dep-project", "dep.group", "dep-artifact");
        ProjectVersion dependency = new ProjectVersion("dep.group", "dep-artifact", "1.0.0");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.findCoordinates("dep.group", "dep-artifact"))
                .thenReturn(Optional.of(dependentProject));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Arrays.asList(dependency));
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockProjectsService.find("dep.group", "dep-artifact", "1.0.0"))
                .thenReturn(Optional.empty());
        when(mockQueue.push(any(MetadataNotification.class))).thenReturn("queue-id-123");
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockQueue).push(any(MetadataNotification.class));
    }

    @Test
    public void testDoRefreshWithException() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("Unexpected error")));
    }

    @Test
    public void testDoRefreshWithFullUpdate() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                true,
                false,
                null
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
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testDoRefreshWithTransitiveDependenciesAlreadyInStore() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                false,
                true,
                "parent-event-123"
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        StoreProjectData dependentProject = new StoreProjectData("dep-project", "dep.group", "dep-artifact");
        ProjectVersion dependency = new ProjectVersion("dep.group", "dep-artifact", "1.0.0");

        StoreProjectVersionData existingDependencyVersion = new StoreProjectVersionData("dep.group", "dep-artifact", "1.0.0");
        existingDependencyVersion.getVersionData().setExcluded(false);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.findCoordinates("dep.group", "dep-artifact"))
                .thenReturn(Optional.of(dependentProject));
        when(mockProjectsService.find("dep.group", "dep-artifact", "1.0.0"))
                .thenReturn(Optional.of(existingDependencyVersion));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Arrays.asList(dependency));
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().stream().anyMatch(m -> m.contains("Skipping update dependency")));
    }

    @Test
    public void testDoRefreshWithTransitiveDependenciesWithMissingDependentProject() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                false,
                true,
                "parent-event-123"
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        ProjectVersion dependency = new ProjectVersion("missing.group", "missing-artifact", "1.0.0");

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.findCoordinates("missing.group", "missing-artifact"))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Arrays.asList(dependency));
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.contains("Could not find dependent project")));
    }

    @Test
    public void testDoRefreshWithSnapshotVersionTransitiveDependencies() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                false,
                true,
                "parent-event-123"
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        StoreProjectData dependentProject = new StoreProjectData("dep-project", "dep.group", "dep-artifact");
        ProjectVersion snapshotDependency = new ProjectVersion("dep.group", "dep-artifact", "1.0.0-SNAPSHOT");

        StoreProjectVersionData existingSnapshotVersion = new StoreProjectVersionData("dep.group", "dep-artifact", "1.0.0-SNAPSHOT");
        existingSnapshotVersion.getVersionData().setExcluded(false);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.findCoordinates("dep.group", "dep-artifact"))
                .thenReturn(Optional.of(dependentProject));
        when(mockProjectsService.find("dep.group", "dep-artifact", "1.0.0-SNAPSHOT"))
                .thenReturn(Optional.of(existingSnapshotVersion));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Arrays.asList(snapshotDependency));
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockQueue.push(any(MetadataNotification.class))).thenReturn("queue-id-456");
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().stream().anyMatch(m -> m.contains("Processing dependency")));
        verify(mockQueue).push(any(MetadataNotification.class));
    }

    @Test
    public void testDoRefreshWithProjectProperties() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Model mockModel = mock(Model.class);
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("test.property", "test-value");

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
        when(mockIncludeConfig.getProperties()).thenReturn(Arrays.asList("test.property"));
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());
        when(mockArtifactRepository.getPOM(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(mockModel);
        when(mockModel.getProperties()).thenReturn(properties);

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testDoRefreshWithSnapshotVersionAndFullUpdate() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                SNAPSHOT_VERSION,
                true,
                false,
                null
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, SNAPSHOT_VERSION))
                .thenReturn(Optional.of(SNAPSHOT_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, SNAPSHOT_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(SNAPSHOT_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(SNAPSHOT_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testDoRefreshWithSnapshotVersionWithoutFullUpdate() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                SNAPSHOT_VERSION,
                false,
                false,
                null
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, SNAPSHOT_VERSION))
                .thenReturn(Optional.of(SNAPSHOT_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, SNAPSHOT_VERSION))
                .thenReturn(Collections.emptyList());
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(SNAPSHOT_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(SNAPSHOT_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testDoRefreshWithExcludedDependency() throws Exception
    {
        MetadataNotification notification = new MetadataNotification(
                TEST_PROJECT_ID,
                TEST_GROUP_ID,
                TEST_ARTIFACT_ID,
                TEST_VERSION,
                false,
                true,
                "parent-event-123"
        );

        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        StoreProjectData dependentProject = new StoreProjectData("dep-project", "dep.group", "dep-artifact");
        ProjectVersion dependency = new ProjectVersion("dep.group", "dep-artifact", "1.0.0");

        StoreProjectVersionData excludedVersion = new StoreProjectVersionData("dep.group", "dep-artifact", "1.0.0");
        excludedVersion.getVersionData().setExcluded(true);

        when(mockProjectsService.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID))
                .thenReturn(Optional.of(projectData));
        when(mockProjectsService.findCoordinates("dep.group", "dep-artifact"))
                .thenReturn(Optional.of(dependentProject));
        when(mockProjectsService.find("dep.group", "dep-artifact", "1.0.0"))
                .thenReturn(Optional.of(excludedVersion));
        when(mockArtifactRepository.findVersion(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Optional.of(TEST_VERSION));
        when(mockRefreshDependenciesService.retrieveDependenciesFromRepository(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION))
                .thenReturn(Arrays.asList(dependency));
        when(mockRefreshDependenciesService.validateDependencies(any(), eq(TEST_VERSION)))
                .thenReturn(Collections.emptyList());
        when(mockArtifactRepository.findFiles(any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(mockQueue.push(any(MetadataNotification.class))).thenReturn("queue-id-789");
        when(mockIncludeConfig.getProperties()).thenReturn(Collections.emptyList());
        when(mockIncludeConfig.getManifestProperties()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = handler.doRefresh(notification);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockQueue).push(any(MetadataNotification.class));
    }
}

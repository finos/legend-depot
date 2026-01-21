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

package org.finos.legend.depot.services.artifacts.reconciliation;

import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VersionsReconciliationServiceImplClaudeTest
{
    private ArtifactRepository mockRepository;
    private ManageProjectsService mockProjects;
    private VersionsReconciliationServiceImpl reconciliationService;

    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";
    private static final String TEST_PROJECT_ID = "test-project-id";
    private static final String TEST_VERSION_1 = "1.0.0";
    private static final String TEST_VERSION_2 = "2.0.0";
    private static final String TEST_VERSION_3 = "3.0.0";
    private static final String TEST_SNAPSHOT_VERSION = "master-SNAPSHOT";

    @BeforeEach
    public void setup()
    {
        mockRepository = mock(ArtifactRepository.class);
        mockProjects = mock(ManageProjectsService.class);
        reconciliationService = new VersionsReconciliationServiceImpl(mockRepository, mockProjects);
    }

    @Test
    public void testConstructor()
    {
        Assertions.assertNotNull(reconciliationService);
    }

    @Test
    public void testConstructorWithNullParameters()
    {
        VersionsReconciliationServiceImpl nullService = new VersionsReconciliationServiceImpl(null, null);
        Assertions.assertNotNull(nullService);
    }

    @Test
    public void testFindVersionsMismatchesWithNoProjects()
    {
        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.emptyList());

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(0, mismatches.size());
    }

    @Test
    public void testFindVersionsMismatchesWithMatchingVersions() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(
                VersionId.parseVersionId(TEST_VERSION_1),
                VersionId.parseVersionId(TEST_VERSION_2)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(0, mismatches.size());
    }

    @Test
    public void testFindVersionsMismatchesWithVersionsNotInStore() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(versionData1));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(
                VersionId.parseVersionId(TEST_VERSION_1),
                VersionId.parseVersionId(TEST_VERSION_2),
                VersionId.parseVersionId(TEST_VERSION_3)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(1, mismatches.size());

        VersionMismatch mismatch = mismatches.get(0);
        Assertions.assertEquals(TEST_PROJECT_ID, mismatch.projectId);
        Assertions.assertEquals(TEST_GROUP_ID, mismatch.groupId);
        Assertions.assertEquals(TEST_ARTIFACT_ID, mismatch.artifactId);
        Assertions.assertEquals(2, mismatch.versionsNotInStore.size());
        Assertions.assertTrue(mismatch.versionsNotInStore.contains(TEST_VERSION_2));
        Assertions.assertTrue(mismatch.versionsNotInStore.contains(TEST_VERSION_3));
        Assertions.assertEquals(0, mismatch.versionsNotInRepository.size());
    }

    @Test
    public void testFindVersionsMismatchesWithVersionsNotInRepository() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        StoreProjectVersionData versionData3 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_3);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2, versionData3));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(
                VersionId.parseVersionId(TEST_VERSION_1)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(1, mismatches.size());

        VersionMismatch mismatch = mismatches.get(0);
        Assertions.assertEquals(TEST_PROJECT_ID, mismatch.projectId);
        Assertions.assertEquals(TEST_GROUP_ID, mismatch.groupId);
        Assertions.assertEquals(TEST_ARTIFACT_ID, mismatch.artifactId);
        Assertions.assertEquals(0, mismatch.versionsNotInStore.size());
        Assertions.assertEquals(2, mismatch.versionsNotInRepository.size());
        Assertions.assertTrue(mismatch.versionsNotInRepository.contains(TEST_VERSION_2));
        Assertions.assertTrue(mismatch.versionsNotInRepository.contains(TEST_VERSION_3));
    }

    @Test
    public void testFindVersionsMismatchesWithBothMismatches() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(
                VersionId.parseVersionId(TEST_VERSION_1),
                VersionId.parseVersionId(TEST_VERSION_3)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(1, mismatches.size());

        VersionMismatch mismatch = mismatches.get(0);
        Assertions.assertEquals(1, mismatch.versionsNotInStore.size());
        Assertions.assertTrue(mismatch.versionsNotInStore.contains(TEST_VERSION_3));
        Assertions.assertEquals(1, mismatch.versionsNotInRepository.size());
        Assertions.assertTrue(mismatch.versionsNotInRepository.contains(TEST_VERSION_2));
    }

    @Test
    public void testFindVersionsMismatchesIgnoresSnapshotVersions() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData snapshotVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_SNAPSHOT_VERSION);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, snapshotVersionData));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(
                VersionId.parseVersionId(TEST_VERSION_1)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(0, mismatches.size());
    }

    @Test
    public void testFindVersionsMismatchesWithEvictedVersions() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData evictedVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        evictedVersionData.setEvicted(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, evictedVersionData));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(
                VersionId.parseVersionId(TEST_VERSION_1),
                VersionId.parseVersionId(TEST_VERSION_2)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(0, mismatches.size());
    }

    @Test
    public void testFindVersionsMismatchesWithExcludedVersions() throws Exception
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData excludedVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        excludedVersionData.getVersionData().setExcluded(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, excludedVersionData));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(
                VersionId.parseVersionId(TEST_VERSION_1),
                VersionId.parseVersionId(TEST_VERSION_2)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(0, mismatches.size());
    }

    @Test
    public void testFindVersionsMismatchesWithException()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenThrow(new RuntimeException("Test exception"));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(1, mismatches.size());

        VersionMismatch mismatch = mismatches.get(0);
        Assertions.assertEquals(TEST_PROJECT_ID, mismatch.projectId);
        Assertions.assertEquals(TEST_GROUP_ID, mismatch.groupId);
        Assertions.assertEquals(TEST_ARTIFACT_ID, mismatch.artifactId);
        Assertions.assertEquals(0, mismatch.versionsNotInStore.size());
        Assertions.assertEquals(0, mismatch.versionsNotInRepository.size());
        Assertions.assertEquals(1, mismatch.errors.size());
        Assertions.assertTrue(mismatch.errors.get(0).contains("Test exception"));
    }

    @Test
    public void testFindVersionsMismatchesWithMultipleProjects() throws Exception
    {
        StoreProjectData projectData1 = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        StoreProjectData projectData2 = new StoreProjectData("project-2", "group.2", "artifact-2");

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData("group.2", "artifact-2", TEST_VERSION_1);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Arrays.asList(projectData1, projectData2));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(versionData1));
        when(mockProjects.find("group.2", "artifact-2")).thenReturn(Collections.singletonList(versionData2));
        when(mockRepository.findVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(
                VersionId.parseVersionId(TEST_VERSION_1),
                VersionId.parseVersionId(TEST_VERSION_2)
        ));
        when(mockRepository.findVersions("group.2", "artifact-2")).thenReturn(Collections.singletonList(
                VersionId.parseVersionId(TEST_VERSION_1)
        ));

        List<VersionMismatch> mismatches = reconciliationService.findVersionsMismatches();

        Assertions.assertNotNull(mismatches);
        Assertions.assertEquals(1, mismatches.size());
        Assertions.assertEquals(TEST_PROJECT_ID, mismatches.get(0).projectId);
    }

    @Test
    public void testSyncLatestProjectVersionsWithNoProjects()
    {
        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.emptyList());

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(0, updatedProjects.size());
    }

    @Test
    public void testSyncLatestProjectVersionsWithNoVersions()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.emptyList());

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(0, updatedProjects.size());
        verify(mockProjects, times(0)).createOrUpdate((StoreProjectData) org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void testSyncLatestProjectVersionsWithSingleVersion()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(versionData1));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_1, updatedProjects.get(0).getLatestVersion());
        verify(mockProjects, times(1)).createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class));
    }

    @Test
    public void testSyncLatestProjectVersionsWithMultipleVersions()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        StoreProjectVersionData versionData3 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_3);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2, versionData3));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_3, updatedProjects.get(0).getLatestVersion());
    }

    @Test
    public void testSyncLatestProjectVersionsWithExistingLatestVersion()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(TEST_VERSION_2);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2));

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(0, updatedProjects.size());
        verify(mockProjects, times(0)).createOrUpdate((StoreProjectData) org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void testSyncLatestProjectVersionsWithNewerVersion()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(TEST_VERSION_1);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        StoreProjectVersionData versionData3 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_3);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2, versionData3));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_3, updatedProjects.get(0).getLatestVersion());
        verify(mockProjects, times(1)).createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class));
    }

    @Test
    public void testSyncLatestProjectVersionsIgnoresSnapshotVersions()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData snapshotVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_SNAPSHOT_VERSION);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, snapshotVersionData));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_1, updatedProjects.get(0).getLatestVersion());
    }

    @Test
    public void testSyncLatestProjectVersionsIgnoresEvictedVersions()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData evictedVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        evictedVersionData.setEvicted(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, evictedVersionData));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_1, updatedProjects.get(0).getLatestVersion());
    }

    @Test
    public void testSyncLatestProjectVersionsIgnoresExcludedVersions()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData excludedVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        excludedVersionData.getVersionData().setExcluded(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, excludedVersionData));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_1, updatedProjects.get(0).getLatestVersion());
    }

    @Test
    public void testSyncLatestProjectVersionsIgnoresDeprecatedVersions()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setLatestVersion(null);

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData deprecatedVersionData = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        deprecatedVersionData.getVersionData().setDeprecated(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, deprecatedVersionData));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenReturn(projectData);

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(1, updatedProjects.size());
        Assertions.assertEquals(TEST_VERSION_1, updatedProjects.get(0).getLatestVersion());
    }

    @Test
    public void testSyncLatestProjectVersionsWithException()
    {
        StoreProjectData projectData = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenThrow(new RuntimeException("Test exception"));

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(0, updatedProjects.size());
        verify(mockProjects, times(0)).createOrUpdate((StoreProjectData) org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void testSyncLatestProjectVersionsWithMultipleProjects()
    {
        StoreProjectData projectData1 = new StoreProjectData(TEST_PROJECT_ID, TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData1.setLatestVersion(null);

        StoreProjectData projectData2 = new StoreProjectData("project-2", "group.2", "artifact-2");
        projectData2.setLatestVersion("1.0.0");

        StoreProjectVersionData versionData1 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_1);
        StoreProjectVersionData versionData2 = createProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_2);
        StoreProjectVersionData versionData3 = createProjectVersionData("group.2", "artifact-2", "1.0.0");
        StoreProjectVersionData versionData4 = createProjectVersionData("group.2", "artifact-2", "2.0.0");

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Arrays.asList(projectData1, projectData2));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(versionData1, versionData2));
        when(mockProjects.find("group.2", "artifact-2")).thenReturn(Arrays.asList(versionData3, versionData4));
        when(mockProjects.createOrUpdate(org.mockito.ArgumentMatchers.any(StoreProjectData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<StoreProjectData> updatedProjects = reconciliationService.syncLatestProjectVersions();

        Assertions.assertNotNull(updatedProjects);
        Assertions.assertEquals(2, updatedProjects.size());
    }

    private StoreProjectVersionData createProjectVersionData(String groupId, String artifactId, String versionId)
    {
        StoreProjectVersionData versionData = new StoreProjectVersionData(groupId, artifactId, versionId);
        versionData.setEvicted(false);

        ProjectVersionData vData = new ProjectVersionData();
        vData.setDeprecated(false);
        vData.setExcluded(false);
        versionData.setVersionData(vData);

        return versionData;
    }
}

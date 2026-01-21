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

package org.finos.legend.depot.services.artifacts.purge;

import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactsPurgeServiceImplClaudeTest
{
    private ManageProjectsService mockProjects;
    private VersionsReconciliationService mockVersionsReconciliation;
    private QueryMetricsService mockMetrics;
    private ProjectsConfiguration mockProjectsConfiguration;
    private ArtifactsPurgeServiceImpl purgeService;

    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";
    private static final String TEST_VERSION_ID = "1.0.0";
    private static final String TEST_SNAPSHOT_VERSION = "master-SNAPSHOT";

    @BeforeEach
    public void setup()
    {
        mockProjects = mock(ManageProjectsService.class);
        mockVersionsReconciliation = mock(VersionsReconciliationService.class);
        mockMetrics = mock(QueryMetricsService.class);
        mockProjectsConfiguration = mock(ProjectsConfiguration.class);
        purgeService = new ArtifactsPurgeServiceImpl(mockProjects, mockVersionsReconciliation, mockMetrics, mockProjectsConfiguration);
    }

    @Test
    public void testConstructor()
    {
        Assertions.assertNotNull(purgeService);
        Assertions.assertNotNull(purgeService.getQueryMetricsService());
        Assertions.assertSame(mockMetrics, purgeService.getQueryMetricsService());
    }

    @Test
    public void testConstructorWithNullParameters()
    {
        ArtifactsPurgeServiceImpl nullService = new ArtifactsPurgeServiceImpl(null, null, null, null);
        Assertions.assertNotNull(nullService);
        Assertions.assertNull(nullService.getQueryMetricsService());
    }

    @Test
    public void testGetQueryMetricsService()
    {
        QueryMetricsService service = purgeService.getQueryMetricsService();
        Assertions.assertNotNull(service);
        Assertions.assertSame(mockMetrics, service);
    }

    @Test
    public void testDeleteWithValidCoordinates()
    {
        when(mockProjects.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(1L);

        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        verify(mockProjects, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testDeleteWithSnapshotVersion()
    {
        when(mockProjects.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_SNAPSHOT_VERSION)).thenReturn(1L);

        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_SNAPSHOT_VERSION);

        verify(mockProjects, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_SNAPSHOT_VERSION);
    }

    @Test
    public void testDeleteMultipleTimes()
    {
        when(mockProjects.delete(anyString(), anyString(), anyString())).thenReturn(1L);

        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");
        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "3.0.0");

        verify(mockProjects, times(3)).delete(anyString(), anyString(), anyString());
    }

    @Test
    public void testDeleteSnapshotVersionsWithValidSnapshotVersions()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setDefaultBranch("master");

        List<String> versions = Arrays.asList("feature-branch-SNAPSHOT", "develop-SNAPSHOT");

        when(mockProjectsConfiguration.getDefaultBranch()).thenReturn("master");
        when(mockProjects.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(projectData));
        when(mockProjects.delete(anyString(), anyString(), anyString())).thenReturn(1L);

        String result = purgeService.deleteSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, versions);

        Assertions.assertEquals("Deleted all snapshot versions", result);
        verify(mockProjects, times(2)).delete(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), anyString());
    }

    @Test
    public void testDeleteSnapshotVersionsWithNonSnapshotVersion()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setDefaultBranch("master");

        List<String> versions = Arrays.asList("1.0.0", "feature-SNAPSHOT");

        when(mockProjectsConfiguration.getDefaultBranch()).thenReturn("master");
        when(mockProjects.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(projectData));
        when(mockProjects.delete(anyString(), anyString(), anyString())).thenReturn(1L);

        String result = purgeService.deleteSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, versions);

        Assertions.assertTrue(result.contains("Version 1.0.0 can't be deleted as it is not a snapshot version"));
        Assertions.assertTrue(result.contains("Deleted remaining versions"));
        verify(mockProjects, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "feature-SNAPSHOT");
        verify(mockProjects, never()).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
    }

    @Test
    public void testDeleteSnapshotVersionsWithDefaultBranch()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setDefaultBranch("master");

        List<String> versions = Arrays.asList("master-SNAPSHOT", "feature-SNAPSHOT");

        when(mockProjectsConfiguration.getDefaultBranch()).thenReturn("master");
        when(mockProjects.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(projectData));
        when(mockProjects.delete(anyString(), anyString(), anyString())).thenReturn(1L);

        String result = purgeService.deleteSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, versions);

        Assertions.assertTrue(result.contains("Version master-SNAPSHOT can't be deleted as it is the project's default branch"));
        Assertions.assertTrue(result.contains("Deleted remaining versions"));
        verify(mockProjects, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "feature-SNAPSHOT");
        verify(mockProjects, never()).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT");
    }

    @Test
    public void testDeleteSnapshotVersionsWithNonExistentProject()
    {
        List<String> versions = Collections.singletonList("feature-SNAPSHOT");
        when(mockProjects.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            purgeService.deleteSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, versions);
        });
    }

    @Test
    public void testDeleteSnapshotVersionsWithEmptyList()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);
        projectData.setDefaultBranch("master");

        when(mockProjectsConfiguration.getDefaultBranch()).thenReturn("master");
        when(mockProjects.findCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(projectData));

        String result = purgeService.deleteSnapshotVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, Collections.emptyList());

        Assertions.assertEquals("Deleted all snapshot versions", result);
        verify(mockProjects, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    public void testEvictWithValidCoordinates()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertTrue(versionData.isEvicted());
        verify(mockProjects, times(1)).find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockProjects, times(1)).createOrUpdate(versionData);
        verify(mockMetrics, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testEvictWithNonExistentVersion()
    {
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        });

        verify(mockProjects, never()).createOrUpdate((StoreProjectVersionData) any());
        verify(mockMetrics, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    public void testEvictAlreadyEvictedVersion()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        versionData.setEvicted(true);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertTrue(versionData.isEvicted());
        verify(mockProjects, times(1)).createOrUpdate(versionData);
    }

    @Test
    public void testDeprecateWithValidCoordinates()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);

        MetadataNotificationResponse response = purgeService.deprecate(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(1, response.getMessages().size());
        Assertions.assertTrue(response.getMessages().get(0).contains("deprecated"));
        Assertions.assertTrue(versionData.getVersionData().isDeprecated());
        verify(mockProjects, times(1)).createOrUpdate(versionData);
    }

    @Test
    public void testDeprecateWithNonExistentVersion()
    {
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            purgeService.deprecate(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        });

        verify(mockProjects, never()).createOrUpdate((StoreProjectVersionData) any());
    }

    @Test
    public void testDeprecateAlreadyDeprecatedVersion()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        versionData.getVersionData().setDeprecated(true);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);

        MetadataNotificationResponse response = purgeService.deprecate(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(versionData.getVersionData().isDeprecated());
    }

    @Test
    public void testDeprecateVersionsNotInRepository()
    {
        VersionMismatch mismatch = new VersionMismatch("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID,
                Collections.emptyList(), Arrays.asList("1.0.0", "2.0.0"));

        StoreProjectVersionData versionData1 = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        StoreProjectVersionData versionData2 = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");

        when(mockVersionsReconciliation.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(versionData1));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0")).thenReturn(Optional.of(versionData2));
        when(mockProjects.createOrUpdate(any(StoreProjectVersionData.class))).thenReturn(versionData1);

        MetadataNotificationResponse response = purgeService.deprecateVersionsNotInRepository();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(2, response.getMessages().size());
    }

    @Test
    public void testDeprecateVersionsNotInRepositoryWithEmptyMismatches()
    {
        when(mockVersionsReconciliation.findVersionsMismatches()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = purgeService.deprecateVersionsNotInRepository();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(0, response.getMessages().size());
    }

    @Test
    public void testDeprecateVersionsNotInRepositoryWithEmptyVersionsList()
    {
        VersionMismatch mismatch = new VersionMismatch("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID,
                Collections.emptyList(), Collections.emptyList());

        when(mockVersionsReconciliation.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        MetadataNotificationResponse response = purgeService.deprecateVersionsNotInRepository();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjects, never()).find(anyString(), anyString(), anyString());
    }

    @Test
    public void testEvictOldestProjectVersionsWithMultipleVersions()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0", "4.0.0", "5.0.0"));
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);
        when(mockProjects.find(anyString(), anyString(), anyString())).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 2);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().size() >= 3);
    }

    @Test
    public void testEvictOldestProjectVersionsWithNoEviction()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0"));

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 5);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjects, never()).find(anyString(), anyString(), anyString());
    }

    @Test
    public void testEvictOldestProjectVersionsWithExactMatch()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0"));

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 3);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        verify(mockProjects, never()).find(anyString(), anyString(), anyString());
    }

    @Test
    public void testEvictLeastRecentlyUsedWithSnapshotVersions()
    {
        Date oldDate = new Date(System.currentTimeMillis() - 100L * 24 * 60 * 60 * 1000);
        VersionQueryMetric metric = createMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT", oldDate);
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT");

        when(mockMetrics.findSnapshotVersionMetricsBefore(any(Date.class))).thenReturn(Collections.singletonList(metric));
        when(mockMetrics.findReleasedVersionMetricsBefore(any(Date.class))).thenReturn(Collections.emptyList());
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT")).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = purgeService.evictLeastRecentlyUsed(30, 7);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertTrue(response.getMessages().size() > 0);
    }

    @Test
    public void testEvictLeastRecentlyUsedWithReleasedVersions()
    {
        Date oldDate = new Date(System.currentTimeMillis() - 100L * 24 * 60 * 60 * 1000);
        VersionQueryMetric metric = createMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", oldDate);
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");

        when(mockMetrics.findSnapshotVersionMetricsBefore(any(Date.class))).thenReturn(Collections.emptyList());
        when(mockMetrics.findReleasedVersionMetricsBefore(any(Date.class))).thenReturn(Collections.singletonList(metric));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = purgeService.evictLeastRecentlyUsed(30, 7);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testEvictLeastRecentlyUsedWithNoMetrics()
    {
        when(mockMetrics.findSnapshotVersionMetricsBefore(any(Date.class))).thenReturn(Collections.emptyList());
        when(mockMetrics.findReleasedVersionMetricsBefore(any(Date.class))).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = purgeService.evictLeastRecentlyUsed(30, 7);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(0, response.getMessages().size());
        verify(mockProjects, never()).find(anyString(), anyString(), anyString());
    }

    @Test
    public void testEvictLeastRecentlyUsedWithBothSnapshotAndReleased()
    {
        Date oldDate = new Date(System.currentTimeMillis() - 100L * 24 * 60 * 60 * 1000);
        VersionQueryMetric snapshotMetric = createMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT", oldDate);
        VersionQueryMetric releasedMetric = createMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", oldDate);

        StoreProjectVersionData snapshotVersionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT");
        StoreProjectVersionData releasedVersionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");

        when(mockMetrics.findSnapshotVersionMetricsBefore(any(Date.class))).thenReturn(Collections.singletonList(snapshotMetric));
        when(mockMetrics.findReleasedVersionMetricsBefore(any(Date.class))).thenReturn(Collections.singletonList(releasedMetric));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT")).thenReturn(Optional.of(snapshotVersionData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(releasedVersionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(snapshotVersionData, releasedVersionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = purgeService.evictLeastRecentlyUsed(30, 7);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testEvictVersionsNotUsedWithAllVersionsUsed()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        VersionQueryMetric metric = createMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", new Date());

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(versionData));
        when(mockMetrics.findMetricsForProjectCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(metric));

        MetadataNotificationResponse response = purgeService.evictVersionsNotUsed();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(0, response.getMessages().size());
    }

    @Test
    public void testEvictVersionsNotUsedWithUnusedVersions()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData usedVersion = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        StoreProjectVersionData unusedVersion = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");

        VersionQueryMetric metric = createMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0", new Date());

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Arrays.asList(usedVersion, unusedVersion));
        when(mockMetrics.findMetricsForProjectCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(metric));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0")).thenReturn(Optional.of(unusedVersion));
        when(mockProjects.createOrUpdate(any(StoreProjectVersionData.class))).thenReturn(unusedVersion);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = purgeService.evictVersionsNotUsed();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    @Test
    public void testEvictVersionsNotUsedWithEvictedVersions()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData evictedVersion = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        evictedVersion.setEvicted(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(evictedVersion));
        when(mockMetrics.findMetricsForProjectCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = purgeService.evictVersionsNotUsed();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(0, response.getMessages().size());
    }

    @Test
    public void testEvictVersionsNotUsedWithExcludedVersions()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData excludedVersion = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        excludedVersion.getVersionData().setExcluded(true);

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(excludedVersion));
        when(mockMetrics.findMetricsForProjectCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = purgeService.evictVersionsNotUsed();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(0, response.getMessages().size());
    }

    @Test
    public void testEvictVersionsNotUsedWithEmptyProjects()
    {
        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.emptyList());

        MetadataNotificationResponse response = purgeService.evictVersionsNotUsed();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        Assertions.assertEquals(0, response.getMessages().size());
    }

    @Test
    public void testEvictVersionsNotUsedWithNoMetrics()
    {
        StoreProjectData projectData = new StoreProjectData("project-id", TEST_GROUP_ID, TEST_ARTIFACT_ID);

        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");

        when(mockProjects.getAllProjectCoordinates()).thenReturn(Collections.singletonList(projectData));
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.singletonList(versionData));
        when(mockMetrics.findMetricsForProjectCoordinates(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Collections.emptyList());
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());

        MetadataNotificationResponse response = purgeService.evictVersionsNotUsed();

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
    }

    private StoreProjectVersionData createMockProjectVersionData(String groupId, String artifactId, String versionId)
    {
        StoreProjectVersionData versionData = new StoreProjectVersionData(groupId, artifactId, versionId);
        versionData.setEvicted(false);

        ProjectVersionData vData = new ProjectVersionData();
        vData.setDeprecated(false);
        vData.setExcluded(false);
        versionData.setVersionData(vData);

        return versionData;
    }

    private VersionQueryMetric createMetric(String groupId, String artifactId, String versionId, Date lastQueryTime)
    {
        return new VersionQueryMetric(groupId, artifactId, versionId, lastQueryTime);
    }
}

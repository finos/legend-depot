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
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactsPurgeServiceImplClaude_lambda$evictOldestProjectVersions$8Test
{
    private ManageProjectsService mockProjects;
    private VersionsReconciliationService mockVersionsReconciliation;
    private QueryMetricsService mockMetrics;
    private ProjectsConfiguration mockProjectsConfiguration;
    private ArtifactsPurgeServiceImpl purgeService;

    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";

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
    public void testEvictOldestProjectVersionsWithExceptionDuringEviction()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0", "4.0.0", "5.0.0"));
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(versionData));

        // Simulate exception when calling createOrUpdate
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 2);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error evicting old versions"));
        Assertions.assertTrue(response.getErrors().get(0).contains("Database connection failed"));
    }

    @Test
    public void testEvictOldestProjectVersionsWithExceptionDuringMetricsDelete()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0"));
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);

        // Simulate exception when deleting metrics
        doThrow(new RuntimeException("Metrics service unavailable"))
                .when(mockMetrics).delete(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq("1.0.0"));

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 1);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error evicting old versions"));
        Assertions.assertTrue(response.getErrors().get(0).contains("Metrics service unavailable"));
    }

    @Test
    public void testEvictOldestProjectVersionsWithExceptionDuringFind()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0", "4.0.0"));

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);

        // Simulate exception when finding version
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"))
                .thenThrow(new RuntimeException("Version not found in store"));

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 2);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error evicting old versions"));
        Assertions.assertTrue(response.getErrors().get(0).contains("Version not found in store"));
    }

    @Test
    public void testEvictOldestProjectVersionsWithRuntimeExceptionInEvict()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0"));

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);

        // Return empty optional to trigger exception in evict method
        when(mockProjects.find(anyString(), anyString(), anyString())).thenReturn(Optional.empty());

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 1);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error evicting old versions"));
    }

    @Test
    public void testEvictOldestProjectVersionsWithNullPointerException()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0"));

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);
        when(mockProjects.find(anyString(), anyString(), anyString()))
                .thenThrow(new NullPointerException("Null reference in data store"));

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 0);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertTrue(response.getErrors().get(0).contains("Error evicting old versions"));
        Assertions.assertTrue(response.getErrors().get(0).contains("Null reference in data store"));
    }

    @Test
    public void testEvictOldestProjectVersionsWithExceptionContainsCoordinates()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0"));

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);
        when(mockProjects.find(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Test exception"));

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 1);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertEquals(1, response.getErrors().size());
        String errorMessage = response.getErrors().get(0);
        Assertions.assertTrue(errorMessage.contains(TEST_GROUP_ID));
        Assertions.assertTrue(errorMessage.contains(TEST_ARTIFACT_ID));
    }

    @Test
    public void testEvictOldestProjectVersionsExceptionAfterPartialSuccess()
    {
        List<String> versions = new ArrayList<>(Arrays.asList("1.0.0", "2.0.0", "3.0.0", "4.0.0"));
        StoreProjectVersionData versionData1 = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0");
        StoreProjectVersionData versionData2 = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0");

        doNothing().when(mockProjects).checkExists(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        when(mockProjects.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(versions);

        // First eviction succeeds
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")).thenReturn(Optional.of(versionData1));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData1);
        doNothing().when(mockMetrics).delete(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq("1.0.0"));

        // Second eviction fails
        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0"))
                .thenThrow(new RuntimeException("Network timeout"));

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID, 2);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(response.getErrors().get(0).contains("Network timeout"));
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
}

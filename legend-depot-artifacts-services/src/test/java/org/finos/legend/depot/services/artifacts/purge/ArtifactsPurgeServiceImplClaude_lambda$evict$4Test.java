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

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactsHandler;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactsPurgeServiceImplClaude_lambda$evict$4Test
{
    private ManageProjectsService mockProjects;
    private VersionsReconciliationService mockVersionsReconciliation;
    private QueryMetricsService mockMetrics;
    private ProjectsConfiguration mockProjectsConfiguration;
    private ArtifactsPurgeServiceImpl purgeService;

    private ProjectArtifactsHandler mockEntitiesHandler;
    private ProjectArtifactsHandler mockFileGenerationsHandler;
    private ProjectArtifactsHandler mockVersionedEntitiesHandler;

    private static final String TEST_GROUP_ID = "test.group";
    private static final String TEST_ARTIFACT_ID = "test-artifact";
    private static final String TEST_VERSION_ID = "1.0.0";

    @BeforeEach
    public void setup()
    {
        mockProjects = mock(ManageProjectsService.class);
        mockVersionsReconciliation = mock(VersionsReconciliationService.class);
        mockMetrics = mock(QueryMetricsService.class);
        mockProjectsConfiguration = mock(ProjectsConfiguration.class);

        // Create mock handlers
        mockEntitiesHandler = mock(ProjectArtifactsHandler.class);
        mockFileGenerationsHandler = mock(ProjectArtifactsHandler.class);
        mockVersionedEntitiesHandler = mock(ProjectArtifactsHandler.class);

        // Register handlers in the factory
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, mockEntitiesHandler);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, mockFileGenerationsHandler);
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, mockVersionedEntitiesHandler);

        purgeService = new ArtifactsPurgeServiceImpl(mockProjects, mockVersionsReconciliation, mockMetrics, mockProjectsConfiguration);
    }

    @AfterEach
    public void tearDown()
    {
        // Note: We cannot easily clear the factory's registered handlers as there's no clear method
        // But for test isolation, each test registers the handlers it needs
    }

    @Test
    public void testEvictWithSingleArtifactTypeHandler()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(eq(TEST_GROUP_ID), eq(TEST_ARTIFACT_ID), eq(TEST_VERSION_ID));

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        // Verify that the handler's delete method was called (covers lines 178, 179, 181)
        verify(mockEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        Assertions.assertTrue(versionData.isEvicted());
    }

    @Test
    public void testEvictWithMultipleArtifactTypeHandlers()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockFileGenerationsHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockVersionedEntitiesHandler).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        // Verify all handlers were called (covers forEach iteration line 183)
        verify(mockEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockFileGenerationsHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockVersionedEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        Assertions.assertTrue(versionData.isEvicted());
    }

    @Test
    public void testEvictWithDifferentCoordinates()
    {
        String groupId = "org.example";
        String artifactId = "my-artifact";
        String versionId = "2.0.0";

        StoreProjectVersionData versionData = createMockProjectVersionData(groupId, artifactId, versionId);

        when(mockProjects.find(groupId, artifactId, versionId)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(eq(groupId), eq(artifactId), eq(versionId));
        doNothing().when(mockFileGenerationsHandler).delete(eq(groupId), eq(artifactId), eq(versionId));
        doNothing().when(mockVersionedEntitiesHandler).delete(eq(groupId), eq(artifactId), eq(versionId));

        purgeService.evict(groupId, artifactId, versionId);

        verify(mockEntitiesHandler, times(1)).delete(groupId, artifactId, versionId);
        verify(mockFileGenerationsHandler, times(1)).delete(groupId, artifactId, versionId);
        verify(mockVersionedEntitiesHandler, times(1)).delete(groupId, artifactId, versionId);
        verify(mockMetrics, times(1)).delete(groupId, artifactId, versionId);
        Assertions.assertTrue(versionData.isEvicted());
    }

    @Test
    public void testEvictWithSnapshotVersion()
    {
        String snapshotVersion = "master-SNAPSHOT";
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockFileGenerationsHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockVersionedEntitiesHandler).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);

        verify(mockEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);
        verify(mockFileGenerationsHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);
        verify(mockVersionedEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, snapshotVersion);
        Assertions.assertTrue(versionData.isEvicted());
    }

    @Test
    public void testEvictVerifiesHandlerNotNullCheck()
    {
        // This test specifically targets the null check on line 179
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockFileGenerationsHandler).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        // All registered handlers should be called
        verify(mockEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockFileGenerationsHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockVersionedEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testEvictHandlerDeleteCalledWithCorrectParameters()
    {
        String groupId = "com.test";
        String artifactId = "test-lib";
        String versionId = "3.5.0";

        StoreProjectVersionData versionData = createMockProjectVersionData(groupId, artifactId, versionId);

        when(mockProjects.find(groupId, artifactId, versionId)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(anyString(), anyString(), anyString());

        purgeService.evict(groupId, artifactId, versionId);

        // Verify handler delete is called with exact parameters (line 181)
        verify(mockEntitiesHandler, times(1)).delete(eq(groupId), eq(artifactId), eq(versionId));
    }

    @Test
    public void testEvictIteratesOverAllRegisteredHandlers()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockFileGenerationsHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockVersionedEntitiesHandler).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        // Verify forEach completes iteration over all types (line 183)
        // Each registered handler should be invoked exactly once
        verify(mockEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockFileGenerationsHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockVersionedEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        // Verify subsequent operations also complete
        verify(mockProjects, times(1)).createOrUpdate(versionData);
        verify(mockMetrics, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
    }

    @Test
    public void testEvictWithAlreadyEvictedVersion()
    {
        StoreProjectVersionData versionData = createMockProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        versionData.setEvicted(true);

        when(mockProjects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID)).thenReturn(Optional.of(versionData));
        when(mockProjects.createOrUpdate((StoreProjectVersionData) any())).thenReturn(versionData);
        doNothing().when(mockMetrics).delete(anyString(), anyString(), anyString());
        doNothing().when(mockEntitiesHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockFileGenerationsHandler).delete(anyString(), anyString(), anyString());
        doNothing().when(mockVersionedEntitiesHandler).delete(anyString(), anyString(), anyString());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);

        // Even if already evicted, handlers should still be called
        verify(mockEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockFileGenerationsHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        verify(mockVersionedEntitiesHandler, times(1)).delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, TEST_VERSION_ID);
        Assertions.assertTrue(versionData.isEvicted());
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

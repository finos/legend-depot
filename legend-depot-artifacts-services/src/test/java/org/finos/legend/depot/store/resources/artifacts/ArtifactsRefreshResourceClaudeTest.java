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

package org.finos.legend.depot.store.resources.artifacts;

import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactsRefreshResourceClaudeTest
{
    private ArtifactsRefreshService artifactsRefreshService;
    private AuthorisationProvider authorisationProvider;
    private Provider<Principal> principalProvider;
    private ArtifactsRefreshResource resource;

    @BeforeEach
    public void setUp()
    {
        artifactsRefreshService = mock(ArtifactsRefreshService.class);
        authorisationProvider = mock(AuthorisationProvider.class);
        principalProvider = mock(Provider.class);
    }

    /**
     * Test constructor with all dependencies.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#ArtifactsRefreshResource(ArtifactsRefreshService, AuthorisationProvider, Provider)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with all dependencies")
    public void testConstructorWithAllDependencies()
    {
        // Arrange and Act
        ArtifactsRefreshResource actualResource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        // Assert
        assertNotNull(actualResource);
    }

    /**
     * Test getResourceName returns correct resource name.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName returns 'ArtifactsRefresh'")
    public void testGetResourceName()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName = resource.getResourceName();

        // Assert
        assertEquals("ArtifactsRefresh", resourceName);
    }

    /**
     * Test getResourceName is consistent across multiple calls.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName is consistent across multiple calls")
    public void testGetResourceNameConsistency()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName1 = resource.getResourceName();
        String resourceName2 = resource.getResourceName();
        String resourceName3 = resource.getResourceName();

        // Assert
        assertEquals("ArtifactsRefresh", resourceName1);
        assertEquals(resourceName1, resourceName2);
        assertEquals(resourceName2, resourceName3);
    }

    /**
     * Test updateProjectVersion with valid parameters and default flags.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with valid parameters and default flags")
    public void testUpdateProjectVersionWithDefaultFlags()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Project version updated");

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        assertEquals("Project version updated", result.getMessages().get(0));
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion with fullUpdate flag enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with fullUpdate flag enabled")
    public void testUpdateProjectVersionWithFullUpdate()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "master-SNAPSHOT";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(true), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, true, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(true), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion with transitive flag enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with transitive flag enabled")
    public void testUpdateProjectVersionWithTransitive()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "2.5.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(true), anyString());
    }

    /**
     * Test updateProjectVersion with both flags enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with both flags enabled")
    public void testUpdateProjectVersionWithBothFlagsEnabled()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend";
        String artifactId = "depot";
        String versionId = "1.2.3-SNAPSHOT";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(true), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, true, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(true), eq(true), anyString());
    }

    /**
     * Test updateProjectVersion with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with authorization failure")
    public void testUpdateProjectVersionWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        assertThrows(SecurityException.class, () ->
            resource.updateProjectVersion(groupId, artifactId, versionId, false, false)
        );

        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, never()).refreshVersionForProject(
                anyString(), anyString(), anyString(), anyBoolean(), anyBoolean(), anyString());
    }

    /**
     * Test updateProjectAllVersions with default flags.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with default flags")
    public void testUpdateProjectAllVersionsWithDefaultFlags()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("All versions updated");

        when(artifactsRefreshService.refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectAllVersions(groupId, artifactId, false, false, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectAllVersions with fullUpdate enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with fullUpdate enabled")
    public void testUpdateProjectAllVersionsWithFullUpdate()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(true), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectAllVersions(groupId, artifactId, true, false, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(true), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectAllVersions with allVersions enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with allVersions enabled")
    public void testUpdateProjectAllVersionsWithAllVersionsEnabled()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(true), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectAllVersions(groupId, artifactId, false, true, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(true), eq(false), anyString());
    }

    /**
     * Test updateProjectAllVersions with transitive enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with transitive enabled")
    public void testUpdateProjectAllVersionsWithTransitive()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(false), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectAllVersions(groupId, artifactId, false, false, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(false), eq(true), anyString());
    }

    /**
     * Test updateProjectAllVersions with all flags enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with all flags enabled")
    public void testUpdateProjectAllVersionsWithAllFlagsEnabled()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend";
        String artifactId = "depot";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(true), eq(true), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectAllVersions(groupId, artifactId, true, true, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(true), eq(true), eq(true), anyString());
    }

    /**
     * Test updateProjectAllVersions with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with authorization failure")
    public void testUpdateProjectAllVersionsWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";

        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        assertThrows(SecurityException.class, () ->
            resource.updateProjectAllVersions(groupId, artifactId, false, false, false)
        );

        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, never()).refreshAllVersionsForProject(
                anyString(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyString());
    }

    /**
     * Test updateAllProjectsAllVersions with default flags.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with default flags")
    public void testUpdateAllProjectsAllVersionsWithDefaultFlags()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("All projects and versions updated");

        when(artifactsRefreshService.refreshAllVersionsForAllProjects(
                eq(false), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsAllVersions(false, false, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForAllProjects(
                eq(false), eq(false), eq(false), anyString());
    }

    /**
     * Test updateAllProjectsAllVersions with fullUpdate enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with fullUpdate enabled")
    public void testUpdateAllProjectsAllVersionsWithFullUpdate()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForAllProjects(
                eq(true), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsAllVersions(true, false, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForAllProjects(
                eq(true), eq(false), eq(false), anyString());
    }

    /**
     * Test updateAllProjectsAllVersions with allVersions enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with allVersions enabled")
    public void testUpdateAllProjectsAllVersionsWithAllVersionsEnabled()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForAllProjects(
                eq(false), eq(true), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsAllVersions(false, true, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForAllProjects(
                eq(false), eq(true), eq(false), anyString());
    }

    /**
     * Test updateAllProjectsAllVersions with transitive enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with transitive enabled")
    public void testUpdateAllProjectsAllVersionsWithTransitive()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForAllProjects(
                eq(false), eq(false), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsAllVersions(false, false, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForAllProjects(
                eq(false), eq(false), eq(true), anyString());
    }

    /**
     * Test updateAllProjectsAllVersions with all flags enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with all flags enabled")
    public void testUpdateAllProjectsAllVersionsWithAllFlagsEnabled()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshAllVersionsForAllProjects(
                eq(true), eq(true), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsAllVersions(true, true, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshAllVersionsForAllProjects(
                eq(true), eq(true), eq(true), anyString());
    }

    /**
     * Test updateAllProjectsAllVersions with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with authorization failure")
    public void testUpdateAllProjectsAllVersionsWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.updateAllProjectsAllVersions(false, false, false)
        );

        assertEquals(SecurityException.class, exception.getCause().getClass());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, never()).refreshAllVersionsForAllProjects(
                anyBoolean(), anyBoolean(), anyBoolean(), anyString());
    }

    /**
     * Test updateAllProjectsMaster with default flags.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsMaster(boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsMaster with default flags")
    public void testUpdateAllProjectsMasterWithDefaultFlags()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("All project snapshots updated");

        when(artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(
                eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsMaster(false, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, times(1)).refreshDefaultSnapshotsForAllProjects(
                eq(false), eq(false), anyString());
    }

    /**
     * Test updateAllProjectsMaster with fullUpdate enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsMaster(boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsMaster with fullUpdate enabled")
    public void testUpdateAllProjectsMasterWithFullUpdate()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(
                eq(true), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsMaster(true, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshDefaultSnapshotsForAllProjects(
                eq(true), eq(false), anyString());
    }

    /**
     * Test updateAllProjectsMaster with transitive enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsMaster(boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsMaster with transitive enabled")
    public void testUpdateAllProjectsMasterWithTransitive()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(
                eq(false), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsMaster(false, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshDefaultSnapshotsForAllProjects(
                eq(false), eq(true), anyString());
    }

    /**
     * Test updateAllProjectsMaster with both flags enabled.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsMaster(boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsMaster with both flags enabled")
    public void testUpdateAllProjectsMasterWithBothFlagsEnabled()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshDefaultSnapshotsForAllProjects(
                eq(true), eq(true), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsMaster(true, true);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshDefaultSnapshotsForAllProjects(
                eq(true), eq(true), anyString());
    }

    /**
     * Test updateAllProjectsMaster with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsMaster(boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsMaster with authorization failure")
    public void testUpdateAllProjectsMasterWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.updateAllProjectsMaster(false, false)
        );

        assertEquals(SecurityException.class, exception.getCause().getClass());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, never()).refreshDefaultSnapshotsForAllProjects(
                anyBoolean(), anyBoolean(), anyString());
    }

    /**
     * Test updateProjectVersion with snapshot version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with snapshot version")
    public void testUpdateProjectVersionWithSnapshotVersion()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend";
        String artifactId = "depot";
        String versionId = "master-SNAPSHOT";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion with groupId containing dots.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with groupId containing dots")
    public void testUpdateProjectVersionWithGroupIdContainingDots()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend.depot.store";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion with artifactId containing hyphens.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with artifactId containing hyphens")
    public void testUpdateProjectVersionWithArtifactIdContainingHyphens()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "my-test-artifact-name";
        String versionId = "2.5.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion with complex version string.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with complex version string")
    public void testUpdateProjectVersionWithComplexVersion()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.2.3-beta.4-SNAPSHOT";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        assertNotNull(result);
        verify(artifactsRefreshService, times(1)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion with response containing errors.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion with response containing errors")
    public void testUpdateProjectVersionWithErrors()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addError("Failed to update version");

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Failed to update version", result.getErrors().get(0));
    }

    /**
     * Test updateProjectAllVersions with response containing multiple messages.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectAllVersions(String, String, boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectAllVersions with response containing multiple messages")
    public void testUpdateProjectAllVersionsWithMultipleMessages()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Version 1.0.0 updated");
        expectedResponse.addMessage("Version 2.0.0 updated");
        expectedResponse.addMessage("Version 3.0.0 updated");

        when(artifactsRefreshService.refreshAllVersionsForProject(
                eq(groupId), eq(artifactId), eq(false), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateProjectAllVersions(groupId, artifactId, false, false, false);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getMessages().size());
    }

    /**
     * Test updateAllProjectsAllVersions with response containing errors.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateAllProjectsAllVersions(boolean, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateAllProjectsAllVersions with response containing errors")
    public void testUpdateAllProjectsAllVersionsWithErrors()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addError("Failed to update some projects");

        when(artifactsRefreshService.refreshAllVersionsForAllProjects(
                eq(false), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.updateAllProjectsAllVersions(false, false, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
    }

    /**
     * Test updateProjectVersion multiple times with same parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion multiple times with same parameters")
    public void testUpdateProjectVersionMultipleTimes()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        resource.updateProjectVersion(groupId, artifactId, versionId, false, false);
        resource.updateProjectVersion(groupId, artifactId, versionId, false, false);
        resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        verify(authorisationProvider, times(3)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(artifactsRefreshService, times(3)).refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString());
    }

    /**
     * Test updateProjectVersion verifies authorization is called first.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsRefreshResource#updateProjectVersion(String, String, String, boolean, boolean)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateProjectVersion verifies authorization is called first")
    public void testUpdateProjectVersionCallsAuthorizationFirst()
    {
        // Arrange
        resource = new ArtifactsRefreshResource(
                artifactsRefreshService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsRefreshService.refreshVersionForProject(
                eq(groupId), eq(artifactId), eq(versionId), eq(false), eq(false), anyString()))
                .thenReturn(expectedResponse);

        // Act
        resource.updateProjectVersion(groupId, artifactId, versionId, false, false);

        // Assert
        verify(authorisationProvider, times(1)).authorise(principalProvider, "ArtifactsRefresh");
    }
}

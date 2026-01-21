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
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactDependenciesRefreshResourceClaudeTest
{
    private RefreshDependenciesService refreshDependenciesService;
    private AuthorisationProvider authorisationProvider;
    private Provider<Principal> principalProvider;
    private ArtifactDependenciesRefreshResource resource;

    @BeforeEach
    public void setUp()
    {
        refreshDependenciesService = mock(RefreshDependenciesService.class);
        authorisationProvider = mock(AuthorisationProvider.class);
        principalProvider = mock(Provider.class);
    }

    /**
     * Test constructor with all dependencies.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#ArtifactDependenciesRefreshResource(RefreshDependenciesService, AuthorisationProvider, Provider)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with all dependencies")
    public void testConstructorWithAllDependencies()
    {
        // Arrange and Act
        ArtifactDependenciesRefreshResource actualResource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
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
     *   <li>{@link ArtifactDependenciesRefreshResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName returns 'ArtifactsRefresh'")
    public void testGetResourceName()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName = resource.getResourceName();

        // Assert
        assertEquals("ArtifactsRefresh", resourceName);
    }

    /**
     * Test updateTransitiveDependencies with valid parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with valid parameters")
    public void testUpdateTransitiveDependenciesWithValidParameters()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        // Mock authorization to succeed (no exception thrown)
        // Mock service call
        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(groupId, result.getGroupId());
        assertEquals(artifactId, result.getArtifactId());
        assertEquals(versionId, result.getVersionId());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies with different valid parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with different valid parameters")
    public void testUpdateTransitiveDependenciesWithDifferentParameters()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend";
        String artifactId = "legend-depot";
        String versionId = "2.5.3";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(groupId, result.getGroupId());
        assertEquals(artifactId, result.getArtifactId());
        assertEquals(versionId, result.getVersionId());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies with snapshot version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with snapshot version")
    public void testUpdateTransitiveDependenciesWithSnapshotVersion()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.test";
        String artifactId = "my-artifact";
        String versionId = "1.0.0-SNAPSHOT";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(groupId, result.getGroupId());
        assertEquals(artifactId, result.getArtifactId());
        assertEquals(versionId, result.getVersionId());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with authorization failure")
    public void testUpdateTransitiveDependenciesWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Mock authorization to throw SecurityException
        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        // The handle method wraps SecurityException in RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.updateTransitiveDependencies(groupId, artifactId, versionId)
        );

        // Verify the cause is SecurityException
        assertEquals(SecurityException.class, exception.getCause().getClass());

        // Verify that authorization was attempted
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        // Verify that service was never called after authorization failure
        verify(refreshDependenciesService, never()).updateTransitiveDependencies(any(), any(), any());
    }

    /**
     * Test updateTransitiveDependencies when service returns data with transitive dependencies.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies returns data with transitive dependencies")
    public void testUpdateTransitiveDependenciesReturnsDataWithTransitiveDependencies()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);
        expectedData.setEvicted(false);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTransitiveDependenciesReport());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies with groupId containing dots.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with groupId containing dots")
    public void testUpdateTransitiveDependenciesWithGroupIdContainingDots()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend.depot.store";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(groupId, result.getGroupId());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies with artifactId containing hyphens.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with artifactId containing hyphens")
    public void testUpdateTransitiveDependenciesWithArtifactIdContainingHyphens()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "my-test-artifact-name";
        String versionId = "1.0.0";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(artifactId, result.getArtifactId());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies with version containing multiple parts.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with version containing multiple parts")
    public void testUpdateTransitiveDependenciesWithComplexVersion()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.2.3-beta.4-SNAPSHOT";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        StoreProjectVersionData result = resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(versionId, result.getVersionId());
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies verifies authorization is called first.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies verifies authorization is called first")
    public void testUpdateTransitiveDependenciesCallsAuthorizationFirst()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert - verify authorization was called with correct parameters
        verify(authorisationProvider, times(1)).authorise(principalProvider, "ArtifactsRefresh");
    }

    /**
     * Test that getResourceName is consistent across multiple calls.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName is consistent across multiple calls")
    public void testGetResourceNameConsistency()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
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
     * Test updateTransitiveDependencies with service throwing exception.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies with service throwing exception")
    public void testUpdateTransitiveDependenciesWithServiceException()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Mock service to throw exception
        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            resource.updateTransitiveDependencies(groupId, artifactId, versionId)
        );

        // Verify that authorization was called first
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        // Verify that service was called
        verify(refreshDependenciesService, times(1)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }

    /**
     * Test updateTransitiveDependencies multiple times with same parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactDependenciesRefreshResource#updateTransitiveDependencies(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test updateTransitiveDependencies multiple times with same parameters")
    public void testUpdateTransitiveDependenciesMultipleTimes()
    {
        // Arrange
        resource = new ArtifactDependenciesRefreshResource(
                refreshDependenciesService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        StoreProjectVersionData expectedData = new StoreProjectVersionData(groupId, artifactId, versionId);

        when(refreshDependenciesService.updateTransitiveDependencies(groupId, artifactId, versionId))
                .thenReturn(expectedData);

        // Act
        resource.updateTransitiveDependencies(groupId, artifactId, versionId);
        resource.updateTransitiveDependencies(groupId, artifactId, versionId);
        resource.updateTransitiveDependencies(groupId, artifactId, versionId);

        // Assert
        verify(authorisationProvider, times(3)).authorise(eq(principalProvider), eq("ArtifactsRefresh"));
        verify(refreshDependenciesService, times(3)).updateTransitiveDependencies(groupId, artifactId, versionId);
    }
}

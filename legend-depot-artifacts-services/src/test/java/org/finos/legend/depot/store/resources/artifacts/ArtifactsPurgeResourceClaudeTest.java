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
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactsPurgeResourceClaudeTest
{
    private ArtifactsPurgeService artifactsPurgeService;
    private AuthorisationProvider authorisationProvider;
    private Provider<Principal> principalProvider;
    private ArtifactsPurgeResource resource;

    @BeforeEach
    public void setUp()
    {
        artifactsPurgeService = mock(ArtifactsPurgeService.class);
        authorisationProvider = mock(AuthorisationProvider.class);
        principalProvider = mock(Provider.class);
    }

    /**
     * Test constructor with all dependencies.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#ArtifactsPurgeResource(ArtifactsPurgeService, AuthorisationProvider, Provider)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with all dependencies")
    public void testConstructorWithAllDependencies()
    {
        // Arrange and Act
        ArtifactsPurgeResource actualResource = new ArtifactsPurgeResource(
                artifactsPurgeService,
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
     *   <li>{@link ArtifactsPurgeResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName returns 'ArtifactsPurge'")
    public void testGetResourceName()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName = resource.getResourceName();

        // Assert
        assertEquals("ArtifactsPurge", resourceName);
    }

    /**
     * Test getResourceName is consistent across multiple calls.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName is consistent across multiple calls")
    public void testGetResourceNameConsistency()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName1 = resource.getResourceName();
        String resourceName2 = resource.getResourceName();
        String resourceName3 = resource.getResourceName();

        // Assert
        assertEquals("ArtifactsPurge", resourceName1);
        assertEquals(resourceName1, resourceName2);
        assertEquals(resourceName2, resourceName3);
    }

    /**
     * Test evictVersion with valid parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion with valid parameters")
    public void testEvictVersionWithValidParameters()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Act
        MetadataNotificationResponse result = resource.evictVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).evict(groupId, artifactId, versionId);
    }

    /**
     * Test evictVersion with snapshot version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion with snapshot version")
    public void testEvictVersionWithSnapshotVersion()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "master-SNAPSHOT";

        // Act
        MetadataNotificationResponse result = resource.evictVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).evict(groupId, artifactId, versionId);
    }

    /**
     * Test evictVersion with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion with authorization failure")
    public void testEvictVersionWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
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
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.evictVersion(groupId, artifactId, versionId)
        );

        // Verify the cause is SecurityException
        assertEquals(SecurityException.class, exception.getCause().getClass());

        // Verify that authorization was attempted
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        // Verify that service was never called after authorization failure
        verify(artifactsPurgeService, never()).evict(any(), any(), any());
    }

    /**
     * Test deleteVersion with valid parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteVersion with valid parameters")
    public void testDeleteVersionWithValidParameters()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Act
        MetadataNotificationResponse result = resource.deleteVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).delete(groupId, artifactId, versionId);
    }

    /**
     * Test deleteVersion with snapshot version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteVersion with snapshot version")
    public void testDeleteVersionWithSnapshotVersion()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend";
        String artifactId = "depot";
        String versionId = "2.0.0-SNAPSHOT";

        // Act
        MetadataNotificationResponse result = resource.deleteVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).delete(groupId, artifactId, versionId);
    }

    /**
     * Test deleteVersion with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteVersion with authorization failure")
    public void testDeleteVersionWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
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
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.deleteVersion(groupId, artifactId, versionId)
        );

        // Verify the cause is SecurityException
        assertEquals(SecurityException.class, exception.getCause().getClass());

        // Verify that authorization was attempted
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        // Verify that service was never called after authorization failure
        verify(artifactsPurgeService, never()).delete(any(), any(), any());
    }

    /**
     * Test deleteSnapShotVersion with single version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteSnapShotVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteSnapShotVersion with single version")
    public void testDeleteSnapShotVersionWithSingleVersion()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versions = "1.0.0-SNAPSHOT";

        when(artifactsPurgeService.deleteSnapshotVersions(eq(groupId), eq(artifactId), anyList()))
                .thenReturn("Deleted 1 version");

        // Act
        String result = resource.deleteSnapShotVersion(groupId, artifactId, versions);

        // Assert
        assertNotNull(result);
        assertEquals("Deleted 1 version", result);
        verify(artifactsPurgeService, times(1)).deleteSnapshotVersions(
                eq(groupId),
                eq(artifactId),
                eq(Arrays.asList("1.0.0-SNAPSHOT"))
        );
    }

    /**
     * Test deleteSnapShotVersion with multiple versions.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteSnapShotVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteSnapShotVersion with multiple versions")
    public void testDeleteSnapShotVersionWithMultipleVersions()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versions = "1.0.0-SNAPSHOT,2.0.0-SNAPSHOT,3.0.0-SNAPSHOT";

        when(artifactsPurgeService.deleteSnapshotVersions(eq(groupId), eq(artifactId), anyList()))
                .thenReturn("Deleted 3 versions");

        // Act
        String result = resource.deleteSnapShotVersion(groupId, artifactId, versions);

        // Assert
        assertNotNull(result);
        assertEquals("Deleted 3 versions", result);
        verify(artifactsPurgeService, times(1)).deleteSnapshotVersions(
                eq(groupId),
                eq(artifactId),
                eq(Arrays.asList("1.0.0-SNAPSHOT", "2.0.0-SNAPSHOT", "3.0.0-SNAPSHOT"))
        );
    }

    /**
     * Test deleteSnapShotVersion with service exception.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteSnapShotVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteSnapShotVersion with service exception")
    public void testDeleteSnapShotVersionWithServiceException()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versions = "1.0.0-SNAPSHOT";

        when(artifactsPurgeService.deleteSnapshotVersions(eq(groupId), eq(artifactId), anyList()))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            resource.deleteSnapShotVersion(groupId, artifactId, versions)
        );

        verify(artifactsPurgeService, times(1)).deleteSnapshotVersions(
                eq(groupId),
                eq(artifactId),
                eq(Arrays.asList("1.0.0-SNAPSHOT"))
        );
    }

    /**
     * Test evictOldVersions with valid parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictOldVersions(String, String, int)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictOldVersions with valid parameters")
    public void testEvictOldVersionsWithValidParameters()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        int versionsToKeep = 5;

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Evicted old versions");

        when(artifactsPurgeService.evictOldestProjectVersions(groupId, artifactId, versionsToKeep))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.evictOldVersions(groupId, artifactId, versionsToKeep);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        assertEquals("Evicted old versions", result.getMessages().get(0));
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).evictOldestProjectVersions(groupId, artifactId, versionsToKeep);
    }

    /**
     * Test evictOldVersions with zero versions to keep.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictOldVersions(String, String, int)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictOldVersions with zero versions to keep")
    public void testEvictOldVersionsWithZeroVersionsToKeep()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        int versionsToKeep = 0;

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsPurgeService.evictOldestProjectVersions(groupId, artifactId, versionsToKeep))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.evictOldVersions(groupId, artifactId, versionsToKeep);

        // Assert
        assertNotNull(result);
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).evictOldestProjectVersions(groupId, artifactId, versionsToKeep);
    }

    /**
     * Test evictOldVersions with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictOldVersions(String, String, int)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictOldVersions with authorization failure")
    public void testEvictOldVersionsWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        int versionsToKeep = 5;

        // Mock authorization to throw SecurityException
        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.evictOldVersions(groupId, artifactId, versionsToKeep)
        );

        // Verify the cause is SecurityException
        assertEquals(SecurityException.class, exception.getCause().getClass());

        // Verify that authorization was attempted
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        // Verify that service was never called after authorization failure
        verify(artifactsPurgeService, never()).evictOldestProjectVersions(anyString(), anyString(), any(Integer.class));
    }

    /**
     * Test deprecateVersion with valid parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deprecateVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deprecateVersion with valid parameters")
    public void testDeprecateVersionWithValidParameters()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Version deprecated");

        when(artifactsPurgeService.deprecate(groupId, artifactId, versionId))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.deprecateVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        assertEquals("Version deprecated", result.getMessages().get(0));
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).deprecate(groupId, artifactId, versionId);
    }

    /**
     * Test deprecateVersion with snapshot version.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deprecateVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deprecateVersion with snapshot version")
    public void testDeprecateVersionWithSnapshotVersion()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "master-SNAPSHOT";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsPurgeService.deprecate(groupId, artifactId, versionId))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.deprecateVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).deprecate(groupId, artifactId, versionId);
    }

    /**
     * Test deprecateVersion with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deprecateVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deprecateVersion with authorization failure")
    public void testDeprecateVersionWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
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
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.deprecateVersion(groupId, artifactId, versionId)
        );

        // Verify the cause is SecurityException
        assertEquals(SecurityException.class, exception.getCause().getClass());

        // Verify that authorization was attempted
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        // Verify that service was never called after authorization failure
        verify(artifactsPurgeService, never()).deprecate(any(), any(), any());
    }

    /**
     * Test evictVersionsNotUsed with successful response.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersionsNotUsed()}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersionsNotUsed with successful response")
    public void testEvictVersionsNotUsedWithSuccessfulResponse()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Evicted unused versions");

        when(artifactsPurgeService.evictVersionsNotUsed())
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.evictVersionsNotUsed();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMessages().size());
        assertEquals("Evicted unused versions", result.getMessages().get(0));
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).evictVersionsNotUsed();
    }

    /**
     * Test evictVersionsNotUsed with authorization failure.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersionsNotUsed()}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersionsNotUsed with authorization failure")
    public void testEvictVersionsNotUsedWithAuthorizationFailure()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        // Mock authorization to throw SecurityException
        doThrow(new SecurityException("User not authorised"))
                .when(authorisationProvider).authorise(any(), any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            resource.evictVersionsNotUsed()
        );

        // Verify the cause is SecurityException
        assertEquals(SecurityException.class, exception.getCause().getClass());

        // Verify that authorization was attempted
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        // Verify that service was never called after authorization failure
        verify(artifactsPurgeService, never()).evictVersionsNotUsed();
    }

    /**
     * Test evictVersionsNotUsed with response containing errors.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersionsNotUsed()}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersionsNotUsed with response containing errors")
    public void testEvictVersionsNotUsedWithErrors()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addError("Failed to evict some versions");

        when(artifactsPurgeService.evictVersionsNotUsed())
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.evictVersionsNotUsed();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Failed to evict some versions", result.getErrors().get(0));
        verify(authorisationProvider, times(1)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(1)).evictVersionsNotUsed();
    }

    /**
     * Test evictVersion with groupId containing dots.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion with groupId containing dots")
    public void testEvictVersionWithGroupIdContainingDots()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "org.finos.legend.depot.store";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Act
        MetadataNotificationResponse result = resource.evictVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(artifactsPurgeService, times(1)).evict(groupId, artifactId, versionId);
    }

    /**
     * Test evictVersion with artifactId containing hyphens.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion with artifactId containing hyphens")
    public void testEvictVersionWithArtifactIdContainingHyphens()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "my-test-artifact-name";
        String versionId = "1.0.0";

        // Act
        MetadataNotificationResponse result = resource.evictVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(artifactsPurgeService, times(1)).evict(groupId, artifactId, versionId);
    }

    /**
     * Test evictVersion with complex version string.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion with complex version string")
    public void testEvictVersionWithComplexVersion()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.2.3-beta.4-SNAPSHOT";

        // Act
        MetadataNotificationResponse result = resource.evictVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        verify(artifactsPurgeService, times(1)).evict(groupId, artifactId, versionId);
    }

    /**
     * Test deleteVersion verifies authorization is called first.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteVersion verifies authorization is called first")
    public void testDeleteVersionCallsAuthorizationFirst()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Act
        resource.deleteVersion(groupId, artifactId, versionId);

        // Assert - verify authorization was called with correct parameters
        verify(authorisationProvider, times(1)).authorise(principalProvider, "ArtifactsPurge");
    }

    /**
     * Test evictOldVersions with large versionsToKeep value.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictOldVersions(String, String, int)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictOldVersions with large versionsToKeep value")
    public void testEvictOldVersionsWithLargeVersionsToKeep()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        int versionsToKeep = 100;

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();

        when(artifactsPurgeService.evictOldestProjectVersions(groupId, artifactId, versionsToKeep))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.evictOldVersions(groupId, artifactId, versionsToKeep);

        // Assert
        assertNotNull(result);
        verify(artifactsPurgeService, times(1)).evictOldestProjectVersions(groupId, artifactId, versionsToKeep);
    }

    /**
     * Test deleteSnapShotVersion with versions containing spaces after comma.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deleteSnapShotVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deleteSnapShotVersion parsing with spaces")
    public void testDeleteSnapShotVersionParsingWithSpaces()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versions = "1.0.0-SNAPSHOT, 2.0.0-SNAPSHOT";

        when(artifactsPurgeService.deleteSnapshotVersions(eq(groupId), eq(artifactId), anyList()))
                .thenReturn("Deleted 2 versions");

        // Act
        String result = resource.deleteSnapShotVersion(groupId, artifactId, versions);

        // Assert
        assertNotNull(result);
        verify(artifactsPurgeService, times(1)).deleteSnapshotVersions(
                eq(groupId),
                eq(artifactId),
                eq(Arrays.asList("1.0.0-SNAPSHOT", " 2.0.0-SNAPSHOT"))
        );
    }

    /**
     * Test evictVersion multiple times with same parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#evictVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test evictVersion multiple times with same parameters")
    public void testEvictVersionMultipleTimes()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        // Act
        resource.evictVersion(groupId, artifactId, versionId);
        resource.evictVersion(groupId, artifactId, versionId);
        resource.evictVersion(groupId, artifactId, versionId);

        // Assert
        verify(authorisationProvider, times(3)).authorise(eq(principalProvider), eq("ArtifactsPurge"));
        verify(artifactsPurgeService, times(3)).evict(groupId, artifactId, versionId);
    }

    /**
     * Test deprecateVersion with service returning response with messages.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ArtifactsPurgeResource#deprecateVersion(String, String, String)}
     * </ul>
     */
    @Test
    @DisplayName("Test deprecateVersion returns response with multiple messages")
    public void testDeprecateVersionReturnsResponseWithMultipleMessages()
    {
        // Arrange
        resource = new ArtifactsPurgeResource(
                artifactsPurgeService,
                authorisationProvider,
                principalProvider
        );

        String groupId = "com.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";

        MetadataNotificationResponse expectedResponse = new MetadataNotificationResponse();
        expectedResponse.addMessage("Version marked as deprecated");
        expectedResponse.addMessage("Dependencies updated");

        when(artifactsPurgeService.deprecate(groupId, artifactId, versionId))
                .thenReturn(expectedResponse);

        // Act
        MetadataNotificationResponse result = resource.deprecateVersion(groupId, artifactId, versionId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getMessages().size());
        verify(artifactsPurgeService, times(1)).deprecate(groupId, artifactId, versionId);
    }
}

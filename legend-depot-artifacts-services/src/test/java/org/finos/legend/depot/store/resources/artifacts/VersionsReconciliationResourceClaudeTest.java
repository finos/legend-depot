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
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VersionsReconciliationResourceClaudeTest
{
    private VersionsReconciliationService reconciliationService;
    private AuthorisationProvider authorisationProvider;
    private Provider<Principal> principalProvider;
    private VersionsReconciliationResource resource;

    @BeforeEach
    public void setUp()
    {
        reconciliationService = mock(VersionsReconciliationService.class);
        authorisationProvider = mock(AuthorisationProvider.class);
        principalProvider = mock(Provider.class);
    }

    /**
     * Test constructor with all dependencies.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#VersionsReconciliationResource(VersionsReconciliationService, AuthorisationProvider, Provider)}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor with all dependencies")
    public void testConstructorWithAllDependencies()
    {
        // Arrange and Act
        VersionsReconciliationResource actualResource = new VersionsReconciliationResource(
                reconciliationService,
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
     *   <li>{@link VersionsReconciliationResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName returns 'Repository'")
    public void testGetResourceName()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName = resource.getResourceName();

        // Assert
        assertEquals("Repository", resourceName);
    }

    /**
     * Test getResourceName is consistent across multiple calls.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getResourceName()}
     * </ul>
     */
    @Test
    @DisplayName("Test getResourceName is consistent across multiple calls")
    public void testGetResourceNameConsistency()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        // Act
        String resourceName1 = resource.getResourceName();
        String resourceName2 = resource.getResourceName();
        String resourceName3 = resource.getResourceName();

        // Assert
        assertEquals("Repository", resourceName1);
        assertEquals(resourceName1, resourceName2);
        assertEquals(resourceName2, resourceName3);
    }

    /**
     * Test getVersionMissMatches returns empty list when no mismatches exist.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches returns empty list")
    public void testGetVersionMissMatchesReturnsEmptyList()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.emptyList());

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches returns single mismatch.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches returns single mismatch")
    public void testGetVersionMissMatchesReturnsSingleMismatch()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<String> versionsNotInStore = Arrays.asList("1.0.0", "2.0.0");
        List<String> versionsNotInRepository = Arrays.asList("3.0.0");
        VersionMismatch mismatch = new VersionMismatch(
                "example.project",
                "com.example",
                "test-artifact",
                versionsNotInStore,
                versionsNotInRepository
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("example.project", result.get(0).projectId);
        assertEquals("com.example", result.get(0).groupId);
        assertEquals("test-artifact", result.get(0).artifactId);
        assertEquals(2, result.get(0).versionsNotInStore.size());
        assertEquals(1, result.get(0).versionsNotInRepository.size());
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches returns multiple mismatches.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches returns multiple mismatches")
    public void testGetVersionMissMatchesReturnsMultipleMismatches()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        VersionMismatch mismatch1 = new VersionMismatch(
                "project1",
                "com.example",
                "artifact1",
                Arrays.asList("1.0.0"),
                Collections.emptyList()
        );

        VersionMismatch mismatch2 = new VersionMismatch(
                "project2",
                "com.example",
                "artifact2",
                Collections.emptyList(),
                Arrays.asList("2.0.0")
        );

        VersionMismatch mismatch3 = new VersionMismatch(
                "project3",
                "org.finos",
                "artifact3",
                Arrays.asList("1.0.0", "1.1.0"),
                Arrays.asList("2.0.0", "2.1.0")
        );

        List<VersionMismatch> mismatches = Arrays.asList(mismatch1, mismatch2, mismatch3);
        when(reconciliationService.findVersionsMismatches()).thenReturn(mismatches);

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("project1", result.get(0).projectId);
        assertEquals("project2", result.get(1).projectId);
        assertEquals("project3", result.get(2).projectId);
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches with mismatches containing errors.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches with mismatches containing errors")
    public void testGetVersionMissMatchesWithErrors()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<String> errors = Arrays.asList("Failed to fetch versions", "Network timeout");
        VersionMismatch mismatch = new VersionMismatch(
                "project1",
                "com.example",
                "artifact1",
                Arrays.asList("1.0.0"),
                Collections.emptyList(),
                errors
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).errors.size());
        assertEquals("Failed to fetch versions", result.get(0).errors.get(0));
        assertEquals("Network timeout", result.get(0).errors.get(1));
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches when service throws exception.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches when service throws exception")
    public void testGetVersionMissMatchesWithServiceException()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        when(reconciliationService.findVersionsMismatches())
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            resource.getVersionMissMatches()
        );

        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches called multiple times.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches called multiple times")
    public void testGetVersionMissMatchesCalledMultipleTimes()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<VersionMismatch> mismatches = new ArrayList<>();
        when(reconciliationService.findVersionsMismatches()).thenReturn(mismatches);

        // Act
        resource.getVersionMissMatches();
        resource.getVersionMissMatches();
        resource.getVersionMissMatches();

        // Assert
        verify(reconciliationService, times(3)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches returns result with correct data.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches returns result with correct data")
    public void testGetVersionMissMatchesReturnsCorrectData()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<VersionMismatch> expectedMismatches = Collections.emptyList();
        when(reconciliationService.findVersionsMismatches()).thenReturn(expectedMismatches);

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertEquals(expectedMismatches, result);
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches with complex version strings.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches with complex version strings")
    public void testGetVersionMissMatchesWithComplexVersions()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<String> versionsNotInStore = Arrays.asList(
                "1.0.0-SNAPSHOT",
                "2.0.0-beta.1",
                "3.0.0-rc.2-SNAPSHOT",
                "master-SNAPSHOT"
        );

        VersionMismatch mismatch = new VersionMismatch(
                "complex.project",
                "org.finos.legend",
                "depot",
                versionsNotInStore,
                Collections.emptyList()
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).versionsNotInStore.size());
        assertTrue(result.get(0).versionsNotInStore.contains("master-SNAPSHOT"));
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches with groupId containing dots.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches with groupId containing dots")
    public void testGetVersionMissMatchesWithGroupIdContainingDots()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        VersionMismatch mismatch = new VersionMismatch(
                "project.id",
                "org.finos.legend.depot.store.resources",
                "artifact",
                Arrays.asList("1.0.0"),
                Collections.emptyList()
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("org.finos.legend.depot.store.resources", result.get(0).groupId);
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches with artifactId containing hyphens.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches with artifactId containing hyphens")
    public void testGetVersionMissMatchesWithArtifactIdContainingHyphens()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        VersionMismatch mismatch = new VersionMismatch(
                "project-id",
                "com.example",
                "my-test-artifact-name-with-hyphens",
                Arrays.asList("1.0.0"),
                Collections.emptyList()
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("my-test-artifact-name-with-hyphens", result.get(0).artifactId);
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches with mismatch in both directions.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches with mismatch in both directions")
    public void testGetVersionMissMatchesWithBidirectionalMismatch()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<String> versionsNotInStore = Arrays.asList("1.0.0", "1.1.0", "1.2.0");
        List<String> versionsNotInRepository = Arrays.asList("2.0.0", "2.1.0");

        VersionMismatch mismatch = new VersionMismatch(
                "bidirectional.project",
                "com.example",
                "artifact",
                versionsNotInStore,
                versionsNotInRepository
        );

        when(reconciliationService.findVersionsMismatches()).thenReturn(Collections.singletonList(mismatch));

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).versionsNotInStore.size());
        assertEquals(2, result.get(0).versionsNotInRepository.size());
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test getVersionMissMatches with large number of mismatches.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionsReconciliationResource#getVersionMissMatches()}
     * </ul>
     */
    @Test
    @DisplayName("Test getVersionMissMatches with large number of mismatches")
    public void testGetVersionMissMatchesWithLargeNumberOfMismatches()
    {
        // Arrange
        resource = new VersionsReconciliationResource(
                reconciliationService,
                authorisationProvider,
                principalProvider
        );

        List<VersionMismatch> mismatches = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            VersionMismatch mismatch = new VersionMismatch(
                    "project" + i,
                    "com.example",
                    "artifact" + i,
                    Arrays.asList("1.0." + i),
                    Collections.emptyList()
            );
            mismatches.add(mismatch);
        }

        when(reconciliationService.findVersionsMismatches()).thenReturn(mismatches);

        // Act
        List<VersionMismatch> result = resource.getVersionMissMatches();

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size());
        verify(reconciliationService, times(1)).findVersionsMismatches();
    }
}

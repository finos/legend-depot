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

import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.dependencies.DependencyOverride;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.dependencies.DependencyUtil;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

public class RefreshDependenciesServiceImplClaudeTest
{
    private ManageProjectsService mockProjectsService;
    private ArtifactRepository mockArtifactRepository;
    private DependencyOverride dependencyOverride;
    private RefreshDependenciesServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        mockProjectsService = mock(ManageProjectsService.class);
        mockArtifactRepository = mock(ArtifactRepository.class);
        dependencyOverride = new DependencyUtil();
        service = new RefreshDependenciesServiceImpl(mockProjectsService, mockArtifactRepository, dependencyOverride);
    }

    @Test
    public void testConstructor()
    {
        Assertions.assertNotNull(service);
    }

    @Test
    public void testRetrieveDependenciesFromRepository_WithNoDependencies()
    {
        when(mockArtifactRepository.findDependencies("com.example", "artifact1", "1.0.0"))
                .thenReturn(Collections.emptySet());

        List<ProjectVersion> result = service.retrieveDependenciesFromRepository("com.example", "artifact1", "1.0.0");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(mockArtifactRepository, times(1)).findDependencies("com.example", "artifact1", "1.0.0");
    }

    @Test
    public void testRetrieveDependenciesFromRepository_WithSingleDependency()
    {
        Set<ArtifactDependency> dependencies = new HashSet<>();
        dependencies.add(new ArtifactDependency("com.example", "dep1", "2.0.0"));

        when(mockArtifactRepository.findDependencies("com.example", "artifact1", "1.0.0"))
                .thenReturn(dependencies);

        List<ProjectVersion> result = service.retrieveDependenciesFromRepository("com.example", "artifact1", "1.0.0");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("com.example", result.get(0).getGroupId());
        Assertions.assertEquals("dep1", result.get(0).getArtifactId());
        Assertions.assertEquals("2.0.0", result.get(0).getVersionId());
    }

    @Test
    public void testRetrieveDependenciesFromRepository_WithMultipleDependencies()
    {
        Set<ArtifactDependency> dependencies = new HashSet<>();
        dependencies.add(new ArtifactDependency("com.example", "dep1", "2.0.0"));
        dependencies.add(new ArtifactDependency("com.example", "dep2", "3.0.0"));
        dependencies.add(new ArtifactDependency("com.other", "dep3", "1.5.0"));

        when(mockArtifactRepository.findDependencies("com.example", "artifact1", "1.0.0"))
                .thenReturn(dependencies);

        List<ProjectVersion> result = service.retrieveDependenciesFromRepository("com.example", "artifact1", "1.0.0");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void testValidateDependencies_ReleaseVersionWithNoDependencies()
    {
        List<ProjectVersion> dependencies = Collections.emptyList();
        List<String> errors = service.validateDependencies(dependencies, "1.0.0");

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateDependencies_ReleaseVersionWithReleaseDependencies()
    {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("com.example", "dep1", "2.0.0"),
                new ProjectVersion("com.example", "dep2", "3.1.5")
        );

        List<String> errors = service.validateDependencies(dependencies, "1.0.0");

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateDependencies_ReleaseVersionWithSnapshotDependency()
    {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("com.example", "dep1", "2.0.0"),
                new ProjectVersion("com.example", "dep2", "3.0.0-SNAPSHOT")
        );

        List<String> errors = service.validateDependencies(dependencies, "1.0.0");

        Assertions.assertNotNull(errors);
        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.get(0).contains("Snapshot dependency"));
        Assertions.assertTrue(errors.get(0).contains("com.example-dep2-3.0.0-SNAPSHOT"));
    }

    @Test
    public void testValidateDependencies_ReleaseVersionWithMultipleSnapshotDependencies()
    {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("com.example", "dep1", "2.0.0-SNAPSHOT"),
                new ProjectVersion("com.example", "dep2", "3.0.0-SNAPSHOT"),
                new ProjectVersion("com.example", "dep3", "4.0.0")
        );

        List<String> errors = service.validateDependencies(dependencies, "1.0.0");

        Assertions.assertNotNull(errors);
        Assertions.assertEquals(2, errors.size());
    }

    @Test
    public void testValidateDependencies_SnapshotVersionWithSnapshotDependencies()
    {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("com.example", "dep1", "2.0.0-SNAPSHOT"),
                new ProjectVersion("com.example", "dep2", "3.0.0-SNAPSHOT")
        );

        List<String> errors = service.validateDependencies(dependencies, "1.0.0-SNAPSHOT");

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateDependencies_SnapshotVersionWithReleaseDependencies()
    {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("com.example", "dep1", "2.0.0"),
                new ProjectVersion("com.example", "dep2", "3.0.0")
        );

        List<String> errors = service.validateDependencies(dependencies, "1.0.0-SNAPSHOT");

        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    public void testUpdateTransitiveDependencies_ProjectNotFound()
    {
        when(mockProjectsService.find("com.example", "artifact1", "1.0.0"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.updateTransitiveDependencies("com.example", "artifact1", "1.0.0")
        );

        Assertions.assertTrue(exception.getMessage().contains("project version not found"));
        Assertions.assertTrue(exception.getMessage().contains("com.example-artifact1-1.0.0"));
    }

    @Test
    public void testUpdateTransitiveDependencies_ExcludedProject()
    {
        StoreProjectVersionData excludedProject = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        excludedProject.getVersionData().setExcluded(true);

        when(mockProjectsService.find("com.example", "artifact1", "1.0.0"))
                .thenReturn(Optional.of(excludedProject));

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.updateTransitiveDependencies("com.example", "artifact1", "1.0.0")
        );

        Assertions.assertTrue(exception.getMessage().contains("project version not found"));
        Assertions.assertTrue(exception.getMessage().contains("com.example-artifact1-1.0.0"));
    }

    @Test
    public void testUpdateTransitiveDependencies_ReleaseVersionWithNoDependencies()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        project.getVersionData().setDependencies(Collections.emptyList());

        when(mockProjectsService.find("com.example", "artifact1", "1.0.0"))
                .thenReturn(Optional.of(project));
        when(mockProjectsService.createOrUpdate(any(StoreProjectVersionData.class)))
                .thenReturn(project);

        StoreProjectVersionData result = service.updateTransitiveDependencies("com.example", "artifact1", "1.0.0");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTransitiveDependenciesReport().isValid());
        Assertions.assertTrue(result.getTransitiveDependenciesReport().getTransitiveDependencies().isEmpty());
        verify(mockProjectsService, times(1)).createOrUpdate(any(StoreProjectVersionData.class));
        verify(mockProjectsService, never()).getDependantProjects(anyString(), anyString(), anyString());
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithNoDependencies()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        project.getVersionData().setDependencies(Collections.emptyList());

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().isEmpty());
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithDependencyNotInStore()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("com.example", "dep1", "2.0.0");
        project.getVersionData().setDependencies(Collections.singletonList(dep1));

        when(mockProjectsService.find("com.example", "dep1", "2.0.0"))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findDependencies("com.example", "dep1", "2.0.0"))
                .thenReturn(Collections.emptySet());

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertEquals(1, project.getTransitiveDependenciesReport().getTransitiveDependencies().size());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().contains(dep1));
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithDependencyInStore()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("com.example", "dep1", "2.0.0");
        project.getVersionData().setDependencies(Collections.singletonList(dep1));

        StoreProjectVersionData dep1Project = new StoreProjectVersionData("com.example", "dep1", "2.0.0");
        dep1Project.getVersionData().setDependencies(Collections.emptyList());
        dep1Project.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.emptyList(), true));

        when(mockProjectsService.find("com.example", "dep1", "2.0.0"))
                .thenReturn(Optional.of(dep1Project));

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertEquals(1, project.getTransitiveDependenciesReport().getTransitiveDependencies().size());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().contains(dep1));
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithExcludedDependency()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("com.example", "dep1", "2.0.0");
        project.getVersionData().setDependencies(Collections.singletonList(dep1));

        StoreProjectVersionData dep1Project = new StoreProjectVersionData("com.example", "dep1", "2.0.0");
        dep1Project.getVersionData().setExcluded(true);

        when(mockProjectsService.find("com.example", "dep1", "2.0.0"))
                .thenReturn(Optional.of(dep1Project));

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertFalse(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().isEmpty());
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithInvalidTransitiveDependencyReport()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("com.example", "dep1", "2.0.0");
        project.getVersionData().setDependencies(Collections.singletonList(dep1));

        StoreProjectVersionData dep1Project = new StoreProjectVersionData("com.example", "dep1", "2.0.0");
        dep1Project.getVersionData().setDependencies(Collections.emptyList());
        dep1Project.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.emptyList(), false));

        when(mockProjectsService.find("com.example", "dep1", "2.0.0"))
                .thenReturn(Optional.of(dep1Project));

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertFalse(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().isEmpty());
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithNestedDependencies()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("com.example", "dep1", "2.0.0");
        project.getVersionData().setDependencies(Collections.singletonList(dep1));

        ProjectVersion dep2 = new ProjectVersion("com.example", "dep2", "3.0.0");
        StoreProjectVersionData dep1Project = new StoreProjectVersionData("com.example", "dep1", "2.0.0");
        dep1Project.getVersionData().setDependencies(Collections.singletonList(dep2));
        dep1Project.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.singletonList(dep2), true));

        StoreProjectVersionData dep2Project = new StoreProjectVersionData("com.example", "dep2", "3.0.0");
        dep2Project.getVersionData().setDependencies(Collections.emptyList());
        dep2Project.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.emptyList(), true));

        when(mockProjectsService.find("com.example", "dep1", "2.0.0"))
                .thenReturn(Optional.of(dep1Project));
        when(mockProjectsService.find("com.example", "dep2", "3.0.0"))
                .thenReturn(Optional.of(dep2Project));

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertEquals(2, project.getTransitiveDependenciesReport().getTransitiveDependencies().size());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().contains(dep1));
        Assertions.assertTrue(project.getTransitiveDependenciesReport().getTransitiveDependencies().contains(dep2));
    }

    @Test
    public void testSetProjectDataTransitiveDependencies_WithInvalidNestedDependency()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        ProjectVersion dep1 = new ProjectVersion("com.example", "dep1", "2.0.0");
        project.getVersionData().setDependencies(Collections.singletonList(dep1));

        when(mockProjectsService.find("com.example", "dep1", "2.0.0"))
                .thenReturn(Optional.empty());

        ProjectVersion dep2 = new ProjectVersion("com.example", "dep2", "3.0.0");
        Set<ArtifactDependency> artifactDeps = new HashSet<>();
        artifactDeps.add(new ArtifactDependency("com.example", "dep2", "3.0.0"));

        when(mockArtifactRepository.findDependencies("com.example", "dep1", "2.0.0"))
                .thenReturn(artifactDeps);
        when(mockProjectsService.find("com.example", "dep2", "3.0.0"))
                .thenReturn(Optional.empty());
        when(mockArtifactRepository.findDependencies("com.example", "dep2", "3.0.0"))
                .thenReturn(Collections.emptySet());

        service.setProjectDataTransitiveDependencies(project);

        Assertions.assertNotNull(project.getTransitiveDependenciesReport());
        Assertions.assertTrue(project.getTransitiveDependenciesReport().isValid());
        Assertions.assertEquals(2, project.getTransitiveDependenciesReport().getTransitiveDependencies().size());
    }
}

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

package org.finos.legend.depot.services.projects;

import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.ProjectProperty;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestProjectsService extends TestBaseServices
{

    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsStore, new DependenciesCache(projectsStore));

    @Before
    public void setUpData()
    {
        super.setUpData();
        Assert.assertEquals(3, projectsService.getAll().size());
        Assert.assertEquals(2, projectsService.findByProjectId("PROD-A").get(0).getDependencies().size());

        ProjectData project1 = projectsService.findByProjectId("PROD-B").get(0);
        project1.addDependency(new ProjectVersionDependency("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0")));
        projectsStore.createOrUpdate(project1);
        Assert.assertEquals(1, projectsStore.findByProjectId("PROD-B").get(0).getDependencies().size());
        loadEntities("PROD-A", "2.3.1");
    }

    @Test
    public void canDeleteProjectByCoordinates()
    {
        Optional<ProjectData> project1 = projectsService.find("examples.metadata","test-dependencies");
        Assert.assertTrue(project1.isPresent());
        projectsService.delete("examples.metadata","test-dependencies");
        Assert.assertFalse(projectsService.find("examples.metadata","test-dependencies").isPresent());
    }

    @Test
    public void canDeleteProjectById()
    {
        ProjectData project1 = projectsService.findByProjectId("PROD-B").get(0);
        Assert.assertNotNull(project1);
        projectsService.delete("PROD-B");
        Assert.assertTrue(projectsService.findByProjectId("PROD-B").isEmpty());
    }


    @Test
    public void canGetProjectDependencies()
    {


        Set<ProjectVersion> dependencyList = projectsService.getDependencies("examples.metadata", "test", "2.3.1", false);
        Assert.assertFalse(dependencyList.isEmpty());
        Assert.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assert.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "1.0.0")));

        // Dependency Tree
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport("examples.metadata", "test", "2.3.1");
        ProjectDependencyReport.SerializedGraph graph = dependencyReport.getGraph();
        MutableMap<String, ProjectDependencyVersionNode> nodes = graph.getNodes();
        Assert.assertEquals(nodes.size(), 3);
        Assert.assertEquals(graph.getRootNodes().size(), 1);
        String rootId = graph.getRootNodes().iterator().next();
        Assert.assertEquals("examples.metadata:test:2.3.1", rootId);

        ProjectDependencyVersionNode  versionNodeA = nodes.get(rootId);
        Assert.assertNotNull(versionNodeA);
        Assert.assertEquals("examples.metadata", versionNodeA.getGroupId());
        Assert.assertEquals("test", versionNodeA.getArtifactId());
        Assert.assertEquals("2.3.1", versionNodeA.getVersionId());
        Assert.assertEquals("PROD-A", versionNodeA.getProjectId());
        Assert.assertEquals(versionNodeA.getForwardEdges().size(), 1);
        Assert.assertEquals(versionNodeA.getBackEdges().size(), 0);

        ProjectDependencyVersionNode versionNodeB = graph.getNodes().get(versionNodeA.getForwardEdges().iterator().next());
        Assert.assertNotNull(versionNodeB);
        Assert.assertEquals("examples.metadata", versionNodeB.getGroupId());
        Assert.assertEquals("test-dependencies", versionNodeB.getArtifactId());
        Assert.assertEquals("1.0.0", versionNodeB.getVersionId());
        Assert.assertEquals("PROD-B", versionNodeB.getProjectId());
        Assert.assertEquals(1, versionNodeB.getForwardEdges().size());
        Assert.assertEquals(1, versionNodeB.getBackEdges().size());
        Assert.assertEquals(versionNodeA.getId(), versionNodeB.getBackEdges().iterator().next());

        ProjectDependencyVersionNode versionNodeC = graph.getNodes().get(versionNodeB.getForwardEdges().iterator().next());
        Assert.assertNotNull(versionNodeC);
        Assert.assertEquals("example.services.test", versionNodeC.getGroupId());
        Assert.assertEquals("test", versionNodeC.getArtifactId());
        Assert.assertEquals("1.0.0", versionNodeC.getVersionId());
        Assert.assertEquals("PROD-C", versionNodeC.getProjectId());
        Assert.assertEquals(0, versionNodeC.getForwardEdges().size());
        Assert.assertEquals(1, versionNodeC.getBackEdges().size());
        Assert.assertEquals(versionNodeB.getId(), versionNodeC.getBackEdges().iterator().next());

        Assert.assertEquals(0, dependencyReport.getConflicts().size());
    }

    @Test
    public void canGetProjectDependenciesWithOutDuplicates()
    {

        // PROD-123 -> PROD-19481
        ProjectData newProject = new ProjectData("PROD-123", "example.group", "test-dups");
        newProject.addVersion("1.0.0");
        newProject.addDependency(new ProjectVersionDependency("examples.group", "test.dups", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0")));
        projectsStore.createOrUpdate(newProject);

        // PROD-10357 -> PROD-10855-> PROD-19481
        // PROD-10357 -> PROD-123 -> PROD-19481
        ProjectData project1 = projectsService.findByProjectId("PROD-A").get(0);
        project1.addDependency(new ProjectVersionDependency("examples.metadata", "test", "2.3.1", new ProjectVersion("example.group", "test.dups", "1.0.0")));
        projectsStore.createOrUpdate(project1);

        Set<ProjectVersion> dependencyList = projectsService.getDependencies("examples.metadata", "test", "2.3.1", false);
        Assert.assertFalse(dependencyList.isEmpty());
        Assert.assertEquals(2, dependencyList.size());
        Assert.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assert.assertTrue(dependencyList.contains(new ProjectVersion("example.group", "test.dups", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(3, dependencyList2.size());
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("example.group", "test.dups", "1.0.0")));

        Assert.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "1.0.0")));

    }

    @Test
    public void canGetDependantProjects()
    {

        List<ProjectVersionPlatformDependency> dependencyList = projectsService.getDependentProjects("examples.metadata", "test", "2.3.1");
        Assert.assertTrue(dependencyList.isEmpty());

        List<ProjectVersionPlatformDependency> dependencyList2 = projectsService.getDependentProjects("examples.metadata", "test-dependencies", "1.0.0");
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(2, dependencyList2.size());
        Assert.assertTrue(dependencyList2.contains(new ProjectVersionPlatformDependency("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersionPlatformDependency("examples.metadata", "test", MASTER_SNAPSHOT, new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectVersionPlatformDependency> dependencyList3 = projectsService.getDependentProjects("example.services.test", "test", "1.0.0");
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(1, dependencyList3.size());
        Assert.assertTrue(dependencyList3.contains(new ProjectVersionPlatformDependency("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0"), Collections.emptyList())));
        Assert.assertFalse(dependencyList3.contains(new ProjectVersionPlatformDependency("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

    }

    @Test
    public void canGetDependantProjectsWithAllVersions()
    {

        List<ProjectVersionPlatformDependency> dependencyList = projectsService.getDependentProjects("examples.metadata", "test", "all");
        Assert.assertTrue(dependencyList.isEmpty());

        List<ProjectVersionPlatformDependency> dependencyList2 = projectsService.getDependentProjects("examples.metadata", "test-dependencies", "all");
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(2, dependencyList2.size());
        Assert.assertTrue(dependencyList2.contains(new ProjectVersionPlatformDependency("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersionPlatformDependency("examples.metadata", "test", MASTER_SNAPSHOT, new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        ProjectData projectData = projectsService.find("examples.metadata", "test-dependencies").get();
        projectData.addDependency(new ProjectVersionDependency("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "2.0.0")));
        projectsStore.createOrUpdate(projectData);

        List<ProjectVersionPlatformDependency> dependencyList3 = projectsService.getDependentProjects("example.services.test", "test", "all");
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(2, dependencyList3.size());
        Assert.assertTrue(dependencyList3.contains(new ProjectVersionPlatformDependency("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList3.contains(new ProjectVersionPlatformDependency("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "2.0.0"), Collections.emptyList())));
        Assert.assertFalse(dependencyList3.contains(new ProjectVersionPlatformDependency("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

    }

    @Test
    public void canUpdateProjectWithProperties()
    {
        Optional<ProjectData> project = projectsService.find("examples.metadata", "test");
        Assert.assertNotNull(project);
        project.get().addProperties(Arrays.asList(new ProjectProperty("legend.version", "0.0.0", "2.0.1")));
        project.get().addProperties(Arrays.asList(new ProjectProperty("legend.version", "0.0.0", "2.3.1")));
        project.get().addProperties(Arrays.asList(new ProjectProperty("legend.version", "0.0.0", "2.0.1")));
        projectsService.createOrUpdate(project.get());
        Optional<ProjectData> updatedProject = projectsService.find("examples.metadata", "test");
        Assert.assertEquals(2, updatedProject.get().getProperties().size());
        Assert.assertEquals(1, updatedProject.get().getPropertiesForProjectVersionID("2.0.1").size());
        Assert.assertEquals(2, projectsService.getDependentProjects("examples.metadata", "test-dependencies", "1.0.0").size());

        updatedProject.get().addDependency(new ProjectVersionDependency("examples.metadata", "test", "2.0.1", new ProjectVersion("examples.metadata", "test-dependencies", "3.0.0")));
        projectsService.createOrUpdate(updatedProject.get());
        List<ProjectVersionPlatformDependency> dependantProjectsList = projectsService.getDependentProjects("examples.metadata", "test-dependencies", "all");
        Assert.assertEquals(3, dependantProjectsList.size());
        Assert.assertTrue(dependantProjectsList.contains(new ProjectVersionPlatformDependency("examples.metadata","test",  "2.0.1", new ProjectVersion("examples.metadata","test-dependencies", "3.0.0"), Arrays.asList(new ProjectProperty("legend.version", "0.0.0", "2.0.1")))));

    }

    @Test
    public void canGetLatestVersionForProject()
    {
        List<String> fullVersions = projectsService.find("examples.metadata", "test").get().getVersions();
        Assert.assertNotNull(fullVersions);
        Assert.assertEquals(2, fullVersions.size());

        Assert.assertTrue(projectsService.getLatestVersion("examples.metadata", "test").isPresent());
        Assert.assertEquals("2.3.1", projectsService.getLatestVersion("examples.metadata", "test").get().toVersionIdString());

    }
}

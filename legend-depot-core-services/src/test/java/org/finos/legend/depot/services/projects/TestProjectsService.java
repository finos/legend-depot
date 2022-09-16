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

import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectDependencyInfo;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependencies;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestProjectsService extends TestBaseServices
{

    protected ManageProjectsService projectsService = new ProjectsServiceImpl(projectsStore);

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
        ProjectDependencyInfo dependencyInfo = projectsService.getProjectDependencyInfo("examples.metadata", "test", "2.3.1");
        Set<ProjectVersionDependencies> dependencyTree = dependencyInfo.getTree();
        Assert.assertEquals(1, dependencyTree.size());
        Set<ProjectVersionDependencies> projectVersionDependencies = dependencyTree.iterator().next().getDependencies();
        Assert.assertEquals(1, projectVersionDependencies.size());
        ProjectVersionDependencies projectVersionDependencies1 = projectVersionDependencies.iterator().next();
        Assert.assertEquals(projectVersionDependencies1.getGroupId(), "examples.metadata");
        Assert.assertEquals(projectVersionDependencies1.getArtifactId(), "test-dependencies");
        Assert.assertEquals(projectVersionDependencies1.getVersionId(), "1.0.0");
        Set<ProjectVersionDependencies> dependencies1 = projectVersionDependencies1.getDependencies();
        Assert.assertEquals(1, dependencies1.size());
        ProjectVersionDependencies projectVersionDependencies2 = dependencies1.iterator().next();
        Assert.assertEquals(projectVersionDependencies2.getGroupId(), "example.services.test");
        Assert.assertEquals(projectVersionDependencies2.getArtifactId(), "test");
        Assert.assertEquals(projectVersionDependencies2.getVersionId(), "1.0.0");
    }

    @Test
    public void canGetProjectDependenciesWithOutDuplicates()
    {

        // PROD-123 -> PROD-19481
        ProjectData newProject = new ProjectData("PROD-123", "example.group", "test.dups");
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
}

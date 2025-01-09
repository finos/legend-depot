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

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.VersionAlias;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.store.mongo.notifications.queue.NotificationsQueueMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;

public class TestProjectsService extends TestBaseServices
{
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore, projectsStore, metrics, queue, new ProjectsConfiguration("master"));

    @BeforeEach
    public void setUpData()
    {
        super.setUpData();
        Assertions.assertEquals(6, projectsService.getAll().size());
        Assertions.assertEquals(0, projectsService.find("examples.metadata","test", "2.2.0").get().getVersionData().getDependencies().size());

        StoreProjectVersionData project1 = projectsService.find("examples.metadata", "test-dependencies", "1.0.0").get();
        ProjectVersion pv = new ProjectVersion("example.services.test", "test", "1.0.0");
        project1.getVersionData().addDependency(pv);
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("example.services.test", "test", "1.0.0"));
        projectsVersionsStore.createOrUpdate(project1);
        StoreProjectVersionData project2 = projectsService.find("examples.metadata","test", "2.3.1").get();
        Assertions.assertEquals(1, project2.getVersionData().getDependencies().size());
        project2.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.singletonList(pv), true));
        projectsVersionsStore.createOrUpdate(project2);
        Assertions.assertEquals(1, projectsVersionsStore.find("examples.metadata", "test-dependencies", "1.0.0").get().getVersionData().getDependencies().size());
    }

    @Test
    public void canDeleteProjectByCoordinates()
    {
        List<StoreProjectVersionData> project1 = projectsService.find("examples.metadata","test-dependencies");
        Assertions.assertFalse(project1.isEmpty());
        projectsService.delete("examples.metadata","test-dependencies");
        Assertions.assertTrue(projectsService.find("examples.metadata","test-dependencies").isEmpty());
    }

    @Test
    public void canDeleteProjectVersion()
    {
        Optional<StoreProjectVersionData> project1 = projectsService.find("examples.metadata","test-dependencies", "1.0.0");
        Assertions.assertTrue(project1.isPresent());
        projectsService.delete("examples.metadata","test-dependencies","1.0.0");
        Assertions.assertFalse(projectsService.find("examples.metadata","test-dependencies","1.0.0").isPresent());
    }


    @Test
    public void canGetProjectDependencies()
    {
        Set<ProjectVersion> dependencyList = projectsService.getDependencies("examples.metadata", "test", "2.3.1", false);
        Assertions.assertFalse(dependencyList.isEmpty());
        Assertions.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assertions.assertFalse(dependencyList2.isEmpty());
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assertions.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "2.0.1")));

        // Dependency Tree
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport("examples.metadata", "test", "2.3.1");
        ProjectDependencyReport.SerializedGraph graph = dependencyReport.getGraph();
        MutableMap<String, ProjectDependencyVersionNode> nodes = graph.getNodes();
        Assertions.assertEquals(nodes.size(), 3);
        Assertions.assertEquals(graph.getRootNodes().size(), 1);
        String rootId = graph.getRootNodes().iterator().next();
        Assertions.assertEquals("examples.metadata:test:2.3.1", rootId);

        ProjectDependencyVersionNode  versionNodeA = nodes.get(rootId);
        Assertions.assertNotNull(versionNodeA);
        Assertions.assertEquals("examples.metadata", versionNodeA.getGroupId());
        Assertions.assertEquals("test", versionNodeA.getArtifactId());
        Assertions.assertEquals("2.3.1", versionNodeA.getVersionId());
        Assertions.assertEquals(versionNodeA.getForwardEdges().size(), 1);
        Assertions.assertEquals(versionNodeA.getBackEdges().size(), 0);

        ProjectDependencyVersionNode versionNodeB = graph.getNodes().get(versionNodeA.getForwardEdges().iterator().next());
        Assertions.assertNotNull(versionNodeB);
        Assertions.assertEquals("examples.metadata", versionNodeB.getGroupId());
        Assertions.assertEquals("test-dependencies", versionNodeB.getArtifactId());
        Assertions.assertEquals("1.0.0", versionNodeB.getVersionId());
        Assertions.assertEquals(1, versionNodeB.getForwardEdges().size());
        Assertions.assertEquals(1, versionNodeB.getBackEdges().size());
        Assertions.assertEquals(versionNodeA.getId(), versionNodeB.getBackEdges().iterator().next());

        ProjectDependencyVersionNode versionNodeC = graph.getNodes().get(versionNodeB.getForwardEdges().iterator().next());
        Assertions.assertNotNull(versionNodeC);
        Assertions.assertEquals("example.services.test", versionNodeC.getGroupId());
        Assertions.assertEquals("test", versionNodeC.getArtifactId());
        Assertions.assertEquals("1.0.0", versionNodeC.getVersionId());
        Assertions.assertEquals(0, versionNodeC.getForwardEdges().size());
        Assertions.assertEquals(1, versionNodeC.getBackEdges().size());
        Assertions.assertEquals(versionNodeB.getId(), versionNodeC.getBackEdges().iterator().next());

        Assertions.assertEquals(0, dependencyReport.getConflicts().size());
    }

    @Test
    public void canGetProjectDependenciesWithOutDuplicates()
    {

        // PROD-123 -> PROD-19481
        StoreProjectVersionData newProject = new StoreProjectVersionData("example.group", "test-dups", "1.0.0");
        newProject.getVersionData().addDependency(new ProjectVersion("example.services.test", "test", "1.0.0"));
        projectsVersionsStore.createOrUpdate(newProject);

        // PROD-10357 -> PROD-10855-> PROD-19481
        // PROD-10357 -> PROD-123 -> PROD-19481
        StoreProjectVersionData project1 = projectsService.find("examples.metadata", "test", "2.3.1").get();
        project1.getVersionData().addDependency(new ProjectVersion("example.group", "test-dups", "1.0.0"));
        projectsVersionsStore.createOrUpdate(project1);

        Set<ProjectVersion> dependencyList = projectsService.getDependencies("examples.metadata", "test", "2.3.1", false);
        Assertions.assertFalse(dependencyList.isEmpty());
        Assertions.assertEquals(2, dependencyList.size());
        Assertions.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assertions.assertTrue(dependencyList.contains(new ProjectVersion("example.group", "test-dups", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assertions.assertFalse(dependencyList2.isEmpty());
        Assertions.assertEquals(3, dependencyList2.size());
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("example.group", "test-dups", "1.0.0")));

        Assertions.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "1.0.0")));

    }

    @Test
    public void canGetProjectDependenciesWithConflicts()
    {
        StoreProjectVersionData projectA = projectsService.find("examples.metadata", "test-dependencies", "1.0.0").get();
        ProjectVersion dependencyA = new ProjectVersion("example.services.test", "test", "1.0.0");
        projectA.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependencyA), true));
        projectsService.createOrUpdate(projectA);

        StoreProjectVersionData projectB = new StoreProjectVersionData("example.services.test", "test-dependencies", "1.0.0");
        projectsService.createOrUpdate(new StoreProjectData("PROD-72", "example.services.test", "test-dependencies"));
        ProjectVersion dependencyB = new ProjectVersion("example.services.test", "test", "2.0.0");
        projectB.getVersionData().addDependency(dependencyB);
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependencyB), true));
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(new StoreProjectVersionData("example.services.test", "test", "2.0.0"));

        // Dependency Tree
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport(Arrays.asList(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), new ProjectVersion("example.services.test", "test-dependencies", "1.0.0")));

        Assertions.assertEquals(1, dependencyReport.getConflicts().size());
        Assertions.assertEquals(dependencyReport.getConflicts().get(0).getVersions(), Sets.mutable.of("example.services.test:test:1.0.0","example.services.test:test:2.0.0"));
    }

    @Test
    public void canGetProjectDependenciesReportWithOverrides()
    {
        StoreProjectVersionData projectA = projectsService.find("examples.metadata", "test-dependencies", "1.0.0").get();
        ProjectVersion dependencyA = new ProjectVersion("example.services.test", "test", "1.0.0");
        projectA.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependencyA), true));
        projectsService.createOrUpdate(projectA);

        StoreProjectVersionData projectB = new StoreProjectVersionData("example.services.test", "test-dependencies", "1.0.0");
        projectsService.createOrUpdate(new StoreProjectData("PROD-72", "example.services.test", "test-dependencies"));
        ProjectVersion dependencyB = new ProjectVersion("example.services.test", "test", "2.0.0");
        projectB.getVersionData().addDependency(dependencyB);
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependencyB), true));
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(new StoreProjectVersionData("example.services.test", "test", "2.0.0"));

        // Dependency Tree
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport(Arrays.asList(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), new ProjectVersion("example.services.test", "test-dependencies", "1.0.0"), dependencyB));

        Assertions.assertEquals(0, dependencyReport.getConflicts().size());
    }

    @Test
    public void canGetProjectDependenciesReportWithOverriddenDependencies()
    {
        Set<ProjectVersion> dependencyList = projectsService.getDependencies("examples.metadata", "test", "2.3.1", false);
        Assertions.assertFalse(dependencyList.isEmpty());
        Assertions.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assertions.assertFalse(dependencyList2.isEmpty());
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assertions.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assertions.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "2.0.1")));
        StoreProjectVersionData projectA = projectsService.find("example.services.test", "test", "1.0.0").get();
        StoreProjectVersionData projectB = new StoreProjectVersionData("examples.metadata", "test-dependencies", "2.0.0");
        projectA.getVersionData().addDependency(new ProjectVersion("examples.metadata", "test-dependencies", "2.0.0"));
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(projectA);

        // Dependency Tree
        List<ProjectVersion> projectDependencyVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "2.3.1"), new ProjectVersion("examples.metadata", "test-dependencies", "2.0.0"));
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport(projectDependencyVersions);

        Assertions.assertEquals(0, dependencyReport.getConflicts().size());
    }


    @Test
    public void canGetDependantProjects()
    {

        List<ProjectDependencyWithPlatformVersions> dependencyList = projectsService.getDependantProjects("examples.metadata", "test", "2.3.1");
        Assertions.assertTrue(dependencyList.isEmpty());

        List<ProjectDependencyWithPlatformVersions> dependencyList2 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "1.0.0");
        Assertions.assertFalse(dependencyList2.isEmpty());
        Assertions.assertEquals(2, dependencyList2.size());
        Assertions.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assertions.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", BRANCH_SNAPSHOT("master"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectDependencyWithPlatformVersions> dependencyList3 = projectsService.getDependantProjects("example.services.test", "test", "1.0.0");
        Assertions.assertFalse(dependencyList3.isEmpty());
        Assertions.assertEquals(1, dependencyList3.size());
        Assertions.assertTrue(dependencyList3.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0"), Collections.emptyList())));
        Assertions.assertFalse(dependencyList3.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

    }

    @Test
    public void canExcludeProjectVersion()
    {
        final String EXCLUSION_REASON = "payload too big to be handled with mongo currently";
        StoreProjectVersionData storeProjectVersionData = projectsService.excludeProjectVersion("examples.metadata", "test", "2.3.1", EXCLUSION_REASON);
        Assertions.assertTrue(storeProjectVersionData.getVersionData().isExcluded());
        Assertions.assertTrue(storeProjectVersionData.getVersionData().getDependencies().size() == 0);
        Assertions.assertTrue(storeProjectVersionData.getVersionData().getProperties().size() == 0);
        Assertions.assertEquals(storeProjectVersionData.getVersionData().getExclusionReason(),EXCLUSION_REASON);
    }

    @Test
    public void canGetProjectCoordinatesByGA()
    {
        Optional<StoreProjectData> storeProjectData = projectsService.findCoordinates("examples.metadata", "test");
        Assertions.assertTrue(storeProjectData.isPresent());
        Assertions.assertEquals(storeProjectData.get(), new StoreProjectData("PROD-A", "examples.metadata", "test", null, "2.3.1"));

        Optional<StoreProjectData> storeProjectData1 = projectsService.findCoordinates("dummy.dep", "test");
        Assertions.assertFalse(storeProjectData1.isPresent());
    }

    @Test
    public void canGetProjectVersionWithLatestVersionNull()
    {
        projectsStore.createOrUpdate(new StoreProjectData("PROD-123", "dummy.project", "test"));
        Optional<StoreProjectData> storeProjectData = projectsService.findCoordinates("dummy.project", "test");
        Assertions.assertTrue(storeProjectData.isPresent());

        Optional<StoreProjectVersionData> projectVersionData = projectsService.find("dummy.project", "test", "latest");
        Assertions.assertFalse(projectVersionData.isPresent());
    }

    @Test
    public void canRestoreEvictedProjectVersion()
    {
        StoreProjectVersionData versionData = new StoreProjectVersionData("examples.metadata", "test", "1.0.0");
        versionData.setEvicted(true);
        projectsVersionsStore.createOrUpdate(versionData);

        Assertions.assertThrows(IllegalStateException.class, () -> projectsService.resolveAliasesAndCheckVersionExists("examples.metadata", "test", "1.0.0"), "Project version: examples.metadata-test-1.0.0 is being restored, please retry in 5 minutes");
        List<MetadataNotification> notifications = queue.getAll();
        Assertions.assertEquals(1, notifications.size());
    }

    @Test
    public void canGetDependantProjectsWithAllVersions()
    {
        List<ProjectDependencyWithPlatformVersions> dependencyList = projectsService.getDependantProjects("examples.metadata", "test", "all");
        Assertions.assertTrue(dependencyList.isEmpty());

        List<ProjectDependencyWithPlatformVersions> dependencyList2 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all");
        Assertions.assertFalse(dependencyList2.isEmpty());
        Assertions.assertEquals(2, dependencyList2.size());
        Assertions.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assertions.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", BRANCH_SNAPSHOT("master"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectDependencyWithPlatformVersions> dependencyList3 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all", true);
        Assertions.assertFalse(dependencyList3.isEmpty());
        Assertions.assertEquals(1, dependencyList3.size());
        Assertions.assertTrue(dependencyList3.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));


        StoreProjectVersionData projectData = projectsService.find("examples.metadata", "test-dependencies", "1.0.0").get();
        projectData.getVersionData().addDependency(new ProjectVersion("example.services.test", "test", "2.0.0"));
        projectsVersionsStore.createOrUpdate(projectData);

        List<ProjectDependencyWithPlatformVersions> dependencyList4 = projectsService.getDependantProjects("example.services.test", "test", "all");
        Assertions.assertFalse(dependencyList4.isEmpty());
        Assertions.assertEquals(2, dependencyList4.size());
        Assertions.assertTrue(dependencyList4.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0"), Collections.emptyList())));
        Assertions.assertTrue(dependencyList4.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "2.0.0"), Collections.emptyList())));
        Assertions.assertFalse(dependencyList4.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        StoreProjectVersionData projectData1 = projectsService.find("examples.metadata", "test", "3.0.0").get();
        projectData1.getVersionData().addDependency(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"));
        projectsVersionsStore.createOrUpdate(projectData1);

        List<ProjectDependencyWithPlatformVersions> dependencyList5 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all", true);
        Assertions.assertFalse(dependencyList5.isEmpty());
        Assertions.assertEquals(1, dependencyList5.size());
        Assertions.assertTrue(dependencyList5.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "3.0.0", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectDependencyWithPlatformVersions> dependencyList6 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all", false);
        Assertions.assertFalse(dependencyList6.isEmpty());
        Assertions.assertEquals(3, dependencyList6.size());
        Assertions.assertTrue(dependencyList6.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assertions.assertTrue(dependencyList6.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "master-SNAPSHOT", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assertions.assertTrue(dependencyList6.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "3.0.0", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
    }

    @Test
    public void canGetLatestVersionForProject()
    {
        List<String> fullVersions = projectsService.getVersions("examples.metadata", "test");
        Assertions.assertNotNull(fullVersions);
        Assertions.assertEquals(2, fullVersions.size());

        Assertions.assertTrue(projectsService.findCoordinates("examples.metadata", "test").isPresent());
        Assertions.assertEquals("2.3.1", projectsService.findCoordinates("examples.metadata", "test").get().getLatestVersion());

        Assertions.assertTrue(projectsService.find("examples.metadata", "test","latest").isPresent());
        Assertions.assertEquals("2.3.1", projectsService.findCoordinates("examples.metadata", "test").get().getLatestVersion());

        Assertions.assertFalse(projectsService.find("dont","exist","latest").isPresent());

        StoreProjectVersionData noVersions = new StoreProjectVersionData("noversion","examples",BRANCH_SNAPSHOT("master"));
        projectsService.createOrUpdate(noVersions);

        Assertions.assertFalse(projectsService.find("noversion","examples", VersionAlias.LATEST.getName()).isPresent());


    }

    @Test
    public void canGetVersionsWithExcludedVersionsInStore()
    {
        List<String> versions = projectsService.getVersions("examples.metadata", "test");
        Assertions.assertEquals(2, versions.size());
        Assertions.assertEquals(Arrays.asList("2.2.0", "2.3.1"), versions);
    }

    @Test
    public void canCheckExistsEvictedVersion()
    {
        StoreProjectVersionData versionData = new StoreProjectVersionData("examples.metadata", "art106", "1.0.0");
        versionData.setEvicted(true);
        try
        {
            projectsService.resolveAliasesAndCheckVersionExists("examples.metadata", "art106", "1.0.0");
            Assertions.assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testGetVersions()
    {
        Assertions.assertEquals(2, projectsService.getVersions("examples.metadata","test", false).size());
        Assertions.assertEquals(3, projectsService.getVersions("examples.metadata","test", true).size());
        projectsService.excludeProjectVersion("examples.metadata","test",BRANCH_SNAPSHOT("master"),"test");
        Assertions.assertEquals(2, projectsService.getVersions("examples.metadata","test", true).size());
        projectsService.excludeProjectVersion("examples.metadata","test","2.3.1","test");
        Assertions.assertEquals(1, projectsService.getVersions("examples.metadata","test", true).size());

    }

    @Test
    public void testCanGetLatestVersionIdUsingAlias()
    {
        Assertions.assertEquals("2.3.1", projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test", "latest"));
    }

    @Test
    public void testErrorThrownWhenIncorrectAliasIsUsed()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test", "lastest"), "project version not found for examples.metadata-test-lastest");
    }

    @Test
    public void testErrorThrownWhenNoProjectVersionFound()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test1", "1.0.0"), "project version not found for examples.metadata-test1-1.0.0");
    }

    @Test
    public void testCanGetMasterSnapshotVersionIdUsingAlias()
    {
        Assertions.assertEquals(BRANCH_SNAPSHOT("master"), projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test", "head"));
    }

    @Test
    public void canGetSnapshotVersions()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test", "branch1-SNAPSHOT"));
        StoreProjectVersionData projectVersionData = new StoreProjectVersionData("examples.metadata", "test", "branch2-SNAPSHOT");
        projectVersionData.getVersionData().setExcluded(true);
        projectsService.createOrUpdate(projectVersionData);
        List<StoreProjectVersionData> versionData = projectsService.findSnapshotVersions("examples.metadata", "test");
        Assertions.assertEquals(2, versionData.size());
        Assertions.assertEquals(Arrays.asList("master-SNAPSHOT", "branch1-SNAPSHOT"), versionData.stream().map(x -> x.getVersionId()).collect(Collectors.toList()));
    }

     @Test
    public void canOverrideDependencies()
    {
        // B -> CV1
        // CV2 -> D
        StoreProjectVersionData projectB = new StoreProjectVersionData("examples.metadata", "testb", "1.0.0");
        StoreProjectVersionData projectCv1 = new StoreProjectVersionData("examples.metadata", "testc", "1.0.0");
        ProjectVersion dependency1 = new ProjectVersion("examples.metadata", "testc", "1.0.0");
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency1), true));
        projectB.getVersionData().setDependencies(Arrays.asList(dependency1));
        StoreProjectVersionData projectCv2 = new StoreProjectVersionData("examples.metadata","testc", "2.0.0");
        StoreProjectVersionData projectD = new StoreProjectVersionData("examples.metadata","testd","1.0.0");
        ProjectVersion dependency2 = new ProjectVersion("examples.metadata", "testd", "1.0.0");
        projectCv2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency2), true));
        projectCv2.getVersionData().setDependencies(Arrays.asList(dependency2));

        projectsService.createOrUpdate(projectCv1);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(projectD);
        projectsService.createOrUpdate(projectCv2);

        ProjectVersion pv1 = new ProjectVersion("examples.metadata", "testb", "1.0.0");
        ProjectVersion pv2 = new ProjectVersion("examples.metadata", "testc", "2.0.0");

        // Cv1 will be overridden by CV2 and incoming dependency D will be part of the list
        List<ProjectVersion> dependencies = projectsService.getDependencies(Arrays.asList(pv1, pv2), true).stream().collect(Collectors.toList());
        Assertions.assertEquals(1, dependencies.size());
        Assertions.assertEquals(dependency2, dependencies.get(0));

    }

    @Test
    public void canGenerateReportForOverriddenDependenciesCase1()
    {
        // A -> B, B -> CV1 , CV1 -> DV1
        // E -> CV2, Cv2 -> DV2
        // override CV2

        StoreProjectData pd1 = new StoreProjectData("PROD-1","examples.metadata", "testb");
        StoreProjectData pd2 = new StoreProjectData("PROD-2","examples.metadata", "testc");
        StoreProjectData pd3 = new StoreProjectData("PROD-3","examples.metadata", "testd");
        StoreProjectData pd4 = new StoreProjectData("PROD-4","examples.metadata", "teste");

        projectsService.createOrUpdate(pd1);
        projectsService.createOrUpdate(pd2);
        projectsService.createOrUpdate(pd3);
        projectsService.createOrUpdate(pd4);

        StoreProjectVersionData projectB = new StoreProjectVersionData("examples.metadata", "testb", "1.0.0");
        StoreProjectVersionData projectCv1 = new StoreProjectVersionData("examples.metadata", "testc", "1.0.0");
        StoreProjectVersionData projectDv1 = new StoreProjectVersionData("examples.metadata", "testd", "1.0.0");
        ProjectVersion dependency1 = new ProjectVersion("examples.metadata", "testc", "1.0.0");
        ProjectVersion dependency2 = new ProjectVersion("examples.metadata", "testd", "1.0.0");
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency1, dependency2), true));
        projectB.getVersionData().setDependencies(Arrays.asList(dependency1));
        projectCv1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency2), true));
        projectCv1.getVersionData().setDependencies(Arrays.asList(dependency2));

        StoreProjectVersionData projectE = new StoreProjectVersionData("examples.metadata", "teste", "1.0.0");
        StoreProjectVersionData projectCv2 = new StoreProjectVersionData("examples.metadata","testc", "2.0.0");
        StoreProjectVersionData projectDv2 = new StoreProjectVersionData("examples.metadata","testd","2.0.0");
        ProjectVersion dependency3 = new ProjectVersion("examples.metadata", "testc", "2.0.0");
        ProjectVersion dependency4 = new ProjectVersion("examples.metadata", "testd", "2.0.0");
        projectE.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency3, dependency4), true));
        projectE.getVersionData().setDependencies(Arrays.asList(dependency3));
        projectCv2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency4), true));
        projectCv2.getVersionData().setDependencies(Arrays.asList(dependency4));

        projectsService.createOrUpdate(projectCv1);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(projectDv1);
        projectsService.createOrUpdate(projectCv2);
        projectsService.createOrUpdate(projectE);
        projectsService.createOrUpdate(projectDv2);

        ProjectVersion pv1 = new ProjectVersion("examples.metadata", "testb", "1.0.0");
        ProjectVersion pv2 = new ProjectVersion("examples.metadata", "teste", "1.0.0");

        // Cv1 will be overridden by CV2 and incoming dependency Dv2 will override Dv1
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport(Arrays.asList(pv1, pv2, dependency3));

        Assertions.assertEquals(0, dependencyReport.getConflicts().size());
    }

    @Test
    public void canGenerateReportForOverriddenDependenciesCase2()
    {
        // A -> B, B -> CV1 , A -> DV1
        // E -> CV2, Cv2 -> DV2
        // override CV2

        StoreProjectData pd1 = new StoreProjectData("PROD-1","examples.metadata", "testb");
        StoreProjectData pd2 = new StoreProjectData("PROD-2","examples.metadata", "testc");
        StoreProjectData pd3 = new StoreProjectData("PROD-3","examples.metadata", "testd");
        StoreProjectData pd4 = new StoreProjectData("PROD-4","examples.metadata", "teste");

        projectsService.createOrUpdate(pd1);
        projectsService.createOrUpdate(pd2);
        projectsService.createOrUpdate(pd3);
        projectsService.createOrUpdate(pd4);

        StoreProjectVersionData projectB = new StoreProjectVersionData("examples.metadata", "testb", "1.0.0");
        StoreProjectVersionData projectCv1 = new StoreProjectVersionData("examples.metadata", "testc", "1.0.0");
        StoreProjectVersionData projectDv1 = new StoreProjectVersionData("examples.metadata", "testd", "1.0.0");
        ProjectVersion dependency1 = new ProjectVersion("examples.metadata", "testc", "1.0.0");
        ProjectVersion dependency2 = new ProjectVersion("examples.metadata", "testd", "1.0.0");
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency1, dependency2), true));
        projectB.getVersionData().setDependencies(Arrays.asList(dependency1, dependency2));

        StoreProjectVersionData projectE = new StoreProjectVersionData("examples.metadata", "teste", "1.0.0");
        StoreProjectVersionData projectCv2 = new StoreProjectVersionData("examples.metadata","testc", "2.0.0");
        StoreProjectVersionData projectDv2 = new StoreProjectVersionData("examples.metadata","testd","2.0.0");
        ProjectVersion dependency3 = new ProjectVersion("examples.metadata", "testc", "2.0.0");
        ProjectVersion dependency4 = new ProjectVersion("examples.metadata", "testd", "2.0.0");
        projectE.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency3, dependency4), true));
        projectE.getVersionData().setDependencies(Arrays.asList(dependency3));
        projectCv2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(dependency4), true));
        projectCv2.getVersionData().setDependencies(Arrays.asList(dependency4));

        projectsService.createOrUpdate(projectCv1);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(projectDv1);
        projectsService.createOrUpdate(projectCv2);
        projectsService.createOrUpdate(projectE);
        projectsService.createOrUpdate(projectDv2);

        ProjectVersion pv1 = new ProjectVersion("examples.metadata", "testb", "1.0.0");
        ProjectVersion pv2 = new ProjectVersion("examples.metadata", "teste", "1.0.0");

        // Cv1 will be overridden by CV2 and incoming dependency Dv2 will be part of the list
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport(Arrays.asList(pv1, pv2, dependency3));

        Assertions.assertEquals(1, dependencyReport.getConflicts().size());
        Assertions.assertEquals(Sets.mutable.of("examples.metadata:testd:2.0.0", "examples.metadata:testd:1.0.0"),dependencyReport.getConflicts().get(0).getVersions());

    }
}

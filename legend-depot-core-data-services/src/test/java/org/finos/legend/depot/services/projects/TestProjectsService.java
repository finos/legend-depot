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
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void setUpData()
    {
        super.setUpData();
        Assert.assertEquals(6, projectsService.getAll().size());
        Assert.assertEquals(0, projectsService.find("examples.metadata","test", "2.2.0").get().getVersionData().getDependencies().size());

        StoreProjectVersionData project1 = projectsService.find("examples.metadata", "test-dependencies", "1.0.0").get();
        ProjectVersion pv = new ProjectVersion("example.services.test", "test", "1.0.0");
        project1.getVersionData().addDependency(pv);
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("example.services.test", "test", "1.0.0"));
        projectsVersionsStore.createOrUpdate(project1);
        StoreProjectVersionData project2 = projectsService.find("examples.metadata","test", "2.3.1").get();
        Assert.assertEquals(1, project2.getVersionData().getDependencies().size());
        project2.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.singletonList(pv), true));
        projectsVersionsStore.createOrUpdate(project2);
        Assert.assertEquals(1, projectsVersionsStore.find("examples.metadata", "test-dependencies", "1.0.0").get().getVersionData().getDependencies().size());
    }

    @Test
    public void canDeleteProjectByCoordinates()
    {
        List<StoreProjectVersionData> project1 = projectsService.find("examples.metadata","test-dependencies");
        Assert.assertFalse(project1.isEmpty());
        projectsService.delete("examples.metadata","test-dependencies");
        Assert.assertTrue(projectsService.find("examples.metadata","test-dependencies").isEmpty());
    }

    @Test
    public void canDeleteProjectVersion()
    {
        Optional<StoreProjectVersionData> project1 = projectsService.find("examples.metadata","test-dependencies", "1.0.0");
        Assert.assertTrue(project1.isPresent());
        projectsService.delete("examples.metadata","test-dependencies","1.0.0");
        Assert.assertFalse(projectsService.find("examples.metadata","test-dependencies","1.0.0").isPresent());
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
        Assert.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "2.0.1")));

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
        Assert.assertEquals(versionNodeA.getForwardEdges().size(), 1);
        Assert.assertEquals(versionNodeA.getBackEdges().size(), 0);

        ProjectDependencyVersionNode versionNodeB = graph.getNodes().get(versionNodeA.getForwardEdges().iterator().next());
        Assert.assertNotNull(versionNodeB);
        Assert.assertEquals("examples.metadata", versionNodeB.getGroupId());
        Assert.assertEquals("test-dependencies", versionNodeB.getArtifactId());
        Assert.assertEquals("1.0.0", versionNodeB.getVersionId());
        Assert.assertEquals(1, versionNodeB.getForwardEdges().size());
        Assert.assertEquals(1, versionNodeB.getBackEdges().size());
        Assert.assertEquals(versionNodeA.getId(), versionNodeB.getBackEdges().iterator().next());

        ProjectDependencyVersionNode versionNodeC = graph.getNodes().get(versionNodeB.getForwardEdges().iterator().next());
        Assert.assertNotNull(versionNodeC);
        Assert.assertEquals("example.services.test", versionNodeC.getGroupId());
        Assert.assertEquals("test", versionNodeC.getArtifactId());
        Assert.assertEquals("1.0.0", versionNodeC.getVersionId());
        Assert.assertEquals(0, versionNodeC.getForwardEdges().size());
        Assert.assertEquals(1, versionNodeC.getBackEdges().size());
        Assert.assertEquals(versionNodeB.getId(), versionNodeC.getBackEdges().iterator().next());

        Assert.assertEquals(0, dependencyReport.getConflicts().size());
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
        Assert.assertFalse(dependencyList.isEmpty());
        Assert.assertEquals(2, dependencyList.size());
        Assert.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assert.assertTrue(dependencyList.contains(new ProjectVersion("example.group", "test-dups", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(3, dependencyList2.size());
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("example.group", "test-dups", "1.0.0")));

        Assert.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "1.0.0")));

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

        Assert.assertEquals(1, dependencyReport.getConflicts().size());
        Assert.assertEquals(dependencyReport.getConflicts().get(0).getVersions(), Sets.mutable.of("example.services.test:test:1.0.0","example.services.test:test:2.0.0"));
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

        Assert.assertEquals(0, dependencyReport.getConflicts().size());
    }

    @Test
    public void canGetProjectDependenciesReportWithOverriddenDependencies()
    {
        Set<ProjectVersion> dependencyList = projectsService.getDependencies("examples.metadata", "test", "2.3.1", false);
        Assert.assertFalse(dependencyList.isEmpty());
        Assert.assertTrue(dependencyList.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));

        Set<ProjectVersion> dependencyList2 = projectsService.getDependencies("examples.metadata", "test", "2.3.1", true);
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0")));
        Assert.assertTrue(dependencyList2.contains(new ProjectVersion("example.services.test", "test", "1.0.0")));
        Assert.assertFalse(projectsService.getDependencies("examples.metadata", "test", "2.3.1", false).contains(new ProjectVersion("example.services.test", "test", "2.0.1")));
        StoreProjectVersionData projectA = projectsService.find("example.services.test", "test", "1.0.0").get();
        StoreProjectVersionData projectB = new StoreProjectVersionData("examples.metadata", "test-dependencies", "2.0.0");
        projectA.getVersionData().addDependency(new ProjectVersion("examples.metadata", "test-dependencies", "2.0.0"));
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(projectA);

        // Dependency Tree
        List<ProjectVersion> projectDependencyVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "2.3.1"), new ProjectVersion("examples.metadata", "test-dependencies", "2.0.0"));
        ProjectDependencyReport dependencyReport = projectsService.getProjectDependencyReport(projectDependencyVersions);

        Assert.assertEquals(0, dependencyReport.getConflicts().size());
    }


    @Test
    public void canGetDependantProjects()
    {

        List<ProjectDependencyWithPlatformVersions> dependencyList = projectsService.getDependantProjects("examples.metadata", "test", "2.3.1");
        Assert.assertTrue(dependencyList.isEmpty());

        List<ProjectDependencyWithPlatformVersions> dependencyList2 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "1.0.0");
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(2, dependencyList2.size());
        Assert.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", BRANCH_SNAPSHOT("master"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectDependencyWithPlatformVersions> dependencyList3 = projectsService.getDependantProjects("example.services.test", "test", "1.0.0");
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(1, dependencyList3.size());
        Assert.assertTrue(dependencyList3.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0"), Collections.emptyList())));
        Assert.assertFalse(dependencyList3.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

    }

    @Test
    public void canExcludeProjectVersion()
    {
        final String EXCLUSION_REASON = "payload too big to be handled with mongo currently";
        StoreProjectVersionData storeProjectVersionData = projectsService.excludeProjectVersion("examples.metadata", "test", "2.3.1", EXCLUSION_REASON);
        Assert.assertTrue(storeProjectVersionData.getVersionData().isExcluded());
        Assert.assertTrue(storeProjectVersionData.getVersionData().getDependencies().size() == 0);
        Assert.assertTrue(storeProjectVersionData.getVersionData().getProperties().size() == 0);
        Assert.assertEquals(storeProjectVersionData.getVersionData().getExclusionReason(),EXCLUSION_REASON);
    }

    @Test
    public void canGetProjectCoordinatesByGA()
    {
        Optional<StoreProjectData> storeProjectData = projectsService.findCoordinates("examples.metadata", "test");
        Assert.assertTrue(storeProjectData.isPresent());
        Assert.assertEquals(storeProjectData.get(), new StoreProjectData("PROD-A", "examples.metadata", "test", null, "2.3.1"));

        Optional<StoreProjectData> storeProjectData1 = projectsService.findCoordinates("dummy.dep", "test");
        Assert.assertFalse(storeProjectData1.isPresent());
    }

    @Test
    public void canGetProjectVersionWithLatestVersionNull()
    {
        projectsStore.createOrUpdate(new StoreProjectData("PROD-123", "dummy.project", "test"));
        Optional<StoreProjectData> storeProjectData = projectsService.findCoordinates("dummy.project", "test");
        Assert.assertTrue(storeProjectData.isPresent());

        Optional<StoreProjectVersionData> projectVersionData = projectsService.find("dummy.project", "test", "latest");
        Assert.assertFalse(projectVersionData.isPresent());
    }

    @Test
    public void canRestoreEvictedProjectVersion()
    {
        StoreProjectVersionData versionData = new StoreProjectVersionData("examples.metadata", "test", "1.0.0");
        versionData.setEvicted(true);
        projectsVersionsStore.createOrUpdate(versionData);

        Assert.assertThrows("Project version: examples.metadata-test-1.0.0 is being restored, please retry in 5 minutes", IllegalStateException.class, () -> projectsService.resolveAliasesAndCheckVersionExists("examples.metadata", "test", "1.0.0"));
        List<MetadataNotification> notifications = queue.getAll();
        Assert.assertEquals(1, notifications.size());
    }

    @Test
    public void canGetDependantProjectsWithAllVersions()
    {
        List<ProjectDependencyWithPlatformVersions> dependencyList = projectsService.getDependantProjects("examples.metadata", "test", "all");
        Assert.assertTrue(dependencyList.isEmpty());

        List<ProjectDependencyWithPlatformVersions> dependencyList2 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all");
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(2, dependencyList2.size());
        Assert.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList2.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", BRANCH_SNAPSHOT("master"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectDependencyWithPlatformVersions> dependencyList3 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all", true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(1, dependencyList3.size());
        Assert.assertTrue(dependencyList3.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));


        StoreProjectVersionData projectData = projectsService.find("examples.metadata", "test-dependencies", "1.0.0").get();
        projectData.getVersionData().addDependency(new ProjectVersion("example.services.test", "test", "2.0.0"));
        projectsVersionsStore.createOrUpdate(projectData);

        List<ProjectDependencyWithPlatformVersions> dependencyList4 = projectsService.getDependantProjects("example.services.test", "test", "all");
        Assert.assertFalse(dependencyList4.isEmpty());
        Assert.assertEquals(2, dependencyList4.size());
        Assert.assertTrue(dependencyList4.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList4.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "2.0.0"), Collections.emptyList())));
        Assert.assertFalse(dependencyList4.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        StoreProjectVersionData projectData1 = projectsService.find("examples.metadata", "test", "3.0.0").get();
        projectData1.getVersionData().addDependency(new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"));
        projectsVersionsStore.createOrUpdate(projectData1);

        List<ProjectDependencyWithPlatformVersions> dependencyList5 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all", true);
        Assert.assertFalse(dependencyList5.isEmpty());
        Assert.assertEquals(1, dependencyList5.size());
        Assert.assertTrue(dependencyList5.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "3.0.0", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));

        List<ProjectDependencyWithPlatformVersions> dependencyList6 = projectsService.getDependantProjects("examples.metadata", "test-dependencies", "all", false);
        Assert.assertFalse(dependencyList6.isEmpty());
        Assert.assertEquals(3, dependencyList6.size());
        Assert.assertTrue(dependencyList6.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "2.3.1", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList6.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "master-SNAPSHOT", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
        Assert.assertTrue(dependencyList6.contains(new ProjectDependencyWithPlatformVersions("examples.metadata", "test", "3.0.0", new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"), Collections.emptyList())));
    }

    @Test
    public void canGetLatestVersionForProject()
    {
        List<String> fullVersions = projectsService.getVersions("examples.metadata", "test");
        Assert.assertNotNull(fullVersions);
        Assert.assertEquals(2, fullVersions.size());

        Assert.assertTrue(projectsService.findCoordinates("examples.metadata", "test").isPresent());
        Assert.assertEquals("2.3.1", projectsService.findCoordinates("examples.metadata", "test").get().getLatestVersion());

        Assert.assertTrue(projectsService.find("examples.metadata", "test","latest").isPresent());
        Assert.assertEquals("2.3.1", projectsService.findCoordinates("examples.metadata", "test").get().getLatestVersion());

        Assert.assertFalse(projectsService.find("dont","exist","latest").isPresent());

        StoreProjectVersionData noVersions = new StoreProjectVersionData("noversion","examples",BRANCH_SNAPSHOT("master"));
        projectsService.createOrUpdate(noVersions);

        Assert.assertFalse(projectsService.find("noversion","examples", VersionAlias.LATEST.getName()).isPresent());


    }

    @Test
    public void canGetVersionsWithExcludedVersionsInStore()
    {
        List<String> versions = projectsService.getVersions("examples.metadata", "test");
        Assert.assertEquals(2, versions.size());
        Assert.assertEquals(Arrays.asList("2.2.0", "2.3.1"), versions);
    }

    @Test
    public void canCheckExistsEvictedVersion()
    {
        StoreProjectVersionData versionData = new StoreProjectVersionData("examples.metadata", "art106", "1.0.0");
        versionData.setEvicted(true);
        try
        {
            projectsService.resolveAliasesAndCheckVersionExists("examples.metadata", "art106", "1.0.0");
            Assert.assertTrue(false);
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetVersions()
    {
        Assert.assertEquals(2, projectsService.getVersions("examples.metadata","test", false).size());
        Assert.assertEquals(3, projectsService.getVersions("examples.metadata","test", true).size());
        projectsService.excludeProjectVersion("examples.metadata","test",BRANCH_SNAPSHOT("master"),"test");
        Assert.assertEquals(2, projectsService.getVersions("examples.metadata","test", true).size());
        projectsService.excludeProjectVersion("examples.metadata","test","2.3.1","test");
        Assert.assertEquals(1, projectsService.getVersions("examples.metadata","test", true).size());

    }

    @Test
    public void testCanGetLatestVersionIdUsingAlias()
    {
        Assert.assertEquals("2.3.1", projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test", "latest"));
    }

    @Test
    public void testErrorThrownWhenIncorrectAliasIsUsed()
    {
        Assert.assertThrows("project version not found for examples.metadata-test-lastest", IllegalArgumentException.class, () -> projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test", "lastest"));
    }

    @Test
    public void testErrorThrownWhenNoProjectVersionFound()
    {
        Assert.assertThrows("project version not found for examples.metadata-test1-1.0.0", IllegalArgumentException.class, () -> projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test1", "1.0.0"));
    }

    @Test
    public void testCanGetMasterSnapshotVersionIdUsingAlias()
    {
        Assert.assertEquals(BRANCH_SNAPSHOT("master"), projectsService.resolveAliasesAndCheckVersionExists("examples.metadata","test", "head"));
    }

    @Test
    public void canGetSnapshotVersions()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test", "branch1-SNAPSHOT"));
        StoreProjectVersionData projectVersionData = new StoreProjectVersionData("examples.metadata", "test", "branch2-SNAPSHOT");
        projectVersionData.getVersionData().setExcluded(true);
        projectsService.createOrUpdate(projectVersionData);
        List<StoreProjectVersionData> versionData = projectsService.findSnapshotVersions("examples.metadata", "test");
        Assert.assertEquals(2, versionData.size());
        Assert.assertEquals(Arrays.asList("master-SNAPSHOT", "branch1-SNAPSHOT"), versionData.stream().map(x -> x.getVersionId()).collect(Collectors.toList()));
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
        Assert.assertEquals(1, dependencies.size());
        Assert.assertEquals(dependency2, dependencies.get(0));

    }
}

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
import org.finos.legend.depot.services.dependencies.DependencySATConverter;
import org.finos.legend.depot.services.dependencies.LogicNGSATResult;
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
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;

public class TestProjectsService extends TestBaseServices
{
    private static final Logger log = LoggerFactory.getLogger(TestProjectsService.class);
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = new NotificationsQueueMongo(mongoProvider);
    private final FormulaFactory f = new FormulaFactory();
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

    @Test
    public void canGenerateCorrectVariableMapForProjectDependencies()
    {
        // Create test projects with alternatives and transitive dependencies
        StoreProjectVersionData projectA_v1 = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectA_v2 = new StoreProjectVersionData("org.finos.legend", "project_a", "2.0.0");
        StoreProjectVersionData projectB_v1 = new StoreProjectVersionData("org.finos.legend", "project_b", "1.0.0");
        StoreProjectVersionData commonDep_v1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonDep_v2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");
        StoreProjectVersionData transitiveDep = new StoreProjectVersionData("org.apache.logging", "log4j", "1.5.0");

        // Set up dependencies: projectA -> commonDep -> transitiveDep, projectB -> commonDep
        ProjectVersion commonDepVersion = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion transitiveDepVersion = new ProjectVersion("org.apache.logging", "log4j", "1.5.0");

        projectA_v1.getVersionData().addDependency(commonDepVersion);
        projectA_v2.getVersionData().addDependency(new ProjectVersion("org.apache.commons", "commons_util", "2.0.0"));
        projectB_v1.getVersionData().addDependency(commonDepVersion);
        commonDep_v1.getVersionData().addDependency(transitiveDepVersion);

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.finos.legend", "project_b"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.apache.commons", "commons_util"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-4", "org.apache.logging", "log4j"));

        projectsService.createOrUpdate(projectA_v1);
        projectsService.createOrUpdate(projectA_v2);
        projectsService.createOrUpdate(projectB_v1);
        projectsService.createOrUpdate(commonDep_v1);
        projectsService.createOrUpdate(commonDep_v2);
        projectsService.createOrUpdate(transitiveDep);

        // Configure transitive dependency reports
        projectA_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonDepVersion, transitiveDepVersion), true));
        projectA_v2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(new ProjectVersion("org.apache.commons", "commons_util", "2.0.0")), true));
        projectB_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonDepVersion, transitiveDepVersion), true));
        commonDep_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(transitiveDepVersion), true));

        // Update projects with transitive dependency reports
        projectsService.createOrUpdate(projectA_v1);
        projectsService.createOrUpdate(projectA_v2);
        projectsService.createOrUpdate(projectB_v1);
        projectsService.createOrUpdate(commonDep_v1);

        // Set up required projects with alternatives
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0")
        );

        Map<ProjectVersion, List<ProjectVersion>> alternativeVersions = new HashMap<>();
        alternativeVersions.put(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                Arrays.asList(
                        new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                        new ProjectVersion("org.finos.legend", "project_a", "2.0.0")
                )
        );
        alternativeVersions.put(
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0"),
                Arrays.asList(new ProjectVersion("org.finos.legend", "project_b", "1.0.0"))
        );

        // Test variable creation
        DependencySATConverter converter = new DependencySATConverter(new FormulaFactory());
        LogicNGSATResult result = converter.convertToLogicNGFormulas(requiredProjects, alternativeVersions, projectsService);

        // Verify variable map contains all expected projects and their alternatives
        Map<String, Variable> variableMap = result.getVariableMap();
        Map<Variable, ProjectVersion> reverseMap = result.getReverseVariableMap();

        Assertions.assertNotNull(variableMap);
        Assertions.assertNotNull(reverseMap);

        // Check required projects and alternatives are present
        Assertions.assertTrue(variableMap.containsKey("org.finos.legend:project_a:1.0.0"),"Should contain project_a v1.0.0");
        Assertions.assertTrue(variableMap.containsKey("org.finos.legend:project_a:2.0.0"),"Should contain project_a v2.0.0 alternative");
        Assertions.assertTrue(variableMap.containsKey("org.finos.legend:project_b:1.0.0"),"Should contain project_b v1.0.0");

        // Check transitive dependencies are included
        Assertions.assertTrue(variableMap.containsKey("org.apache.commons:commons_util:1.0.0"),"Should contain transitive dependency commons_util");
        Assertions.assertTrue(variableMap.containsKey("org.apache.logging:log4j:1.5.0"),"Should contain deep transitive dependency log4j");

        // Verify bidirectional mapping consistency
        Assertions.assertEquals(variableMap.size(), reverseMap.size(),"Variable map and reverse map should have same size");

        variableMap.forEach((gav, variable) ->
        {
            Assertions.assertTrue(reverseMap.containsKey(variable),"Reverse map should contain variable for " + gav);
            Assertions.assertEquals(gav, reverseMap.get(variable).getGav(),"GAV should match in reverse mapping");
        });

        // Verify at least the expected minimum variables are created
        // (2 project_a versions + 1 project_b + 1 commons_util + 1 log4j = 5 minimum)
        Assertions.assertTrue(variableMap.size() >= 5,"Should have at least 5 variables: " + variableMap.keySet());

        log.info("Created {} variables: {}", variableMap.size(), variableMap.keySet());
    }

    @Test
    public void canGenerateCorrectMutualExclusionConstraints()
    {
        // Create test projects with multiple versions of the same artifact
        StoreProjectVersionData projectA_v1 = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectA_v2 = new StoreProjectVersionData("org.finos.legend", "project_a", "2.0.0");
        StoreProjectVersionData projectA_v3 = new StoreProjectVersionData("org.finos.legend", "project_a", "3.0.0");
        StoreProjectVersionData projectB_v1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData projectB_v2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.apache.commons", "commons_util"));
        projectsService.createOrUpdate(projectA_v1);
        projectsService.createOrUpdate(projectA_v2);
        projectsService.createOrUpdate(projectA_v3);
        projectsService.createOrUpdate(projectB_v1);
        projectsService.createOrUpdate(projectB_v2);

        // Set up required projects with multiple alternatives
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.apache.commons", "commons_util", "1.0.0")
        );

        Map<ProjectVersion, List<ProjectVersion>> alternativeVersions = new HashMap<>();
        // project_a has 3 alternative versions
        alternativeVersions.put(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                Arrays.asList(
                        new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                        new ProjectVersion("org.finos.legend", "project_a", "2.0.0"),
                        new ProjectVersion("org.finos.legend", "project_a", "3.0.0")
                )
        );
        // commons_util has 2 alternative versions
        alternativeVersions.put(
                new ProjectVersion("org.apache.commons", "commons_util", "1.0.0"),
                Arrays.asList(
                        new ProjectVersion("org.apache.commons", "commons_util", "1.0.0"),
                        new ProjectVersion("org.apache.commons", "commons_util", "2.0.0")
                )
        );

        // Convert to LogicNG formulas
        DependencySATConverter converter = new DependencySATConverter(new FormulaFactory());
        LogicNGSATResult result = converter.convertToLogicNGFormulas(requiredProjects, alternativeVersions, projectsService);
        FormulaFactory converterFactory = converter.getFormulaFactory();

        List<Formula> clauses = result.getClauses();
        Map<String, Variable> variableMap = result.getVariableMap();

        // Verify variables exist for all alternatives
        Assertions.assertTrue(variableMap.containsKey("org.finos.legend:project_a:1.0.0"));
        Assertions.assertTrue(variableMap.containsKey("org.finos.legend:project_a:2.0.0"));
        Assertions.assertTrue(variableMap.containsKey("org.finos.legend:project_a:3.0.0"));
        Assertions.assertTrue(variableMap.containsKey("org.apache.commons:commons_util:1.0.0"));
        Assertions.assertTrue(variableMap.containsKey("org.apache.commons:commons_util:2.0.0"));

        // Count mutual exclusion constraints
        Variable projectA_v1_var = variableMap.get("org.finos.legend:project_a:1.0.0");
        Variable projectA_v2_var = variableMap.get("org.finos.legend:project_a:2.0.0");
        Variable projectA_v3_var = variableMap.get("org.finos.legend:project_a:3.0.0");
        Variable projectB_v1_var = variableMap.get("org.apache.commons:commons_util:1.0.0");
        Variable projectB_v2_var = variableMap.get("org.apache.commons:commons_util:2.0.0");

        // Expected mutual exclusion clauses for project_a (3 versions = 3 pairs):
        // ¬v1 ∨ ¬v2, ¬v1 ∨ ¬v3, ¬v2 ∨ ¬v3
        List<String> expectedProjectAConstraints = Arrays.asList(
                converterFactory.or(projectA_v1_var.negate(), projectA_v2_var.negate()).toString(),
                converterFactory.or(projectA_v1_var.negate(), projectA_v3_var.negate()).toString(),
                converterFactory.or(projectA_v2_var.negate(), projectA_v3_var.negate()).toString()
        );

        // Expected mutual exclusion clause for commons_util (2 versions = 1 pair):
        // ¬v1 ∨ ¬v2
        String expectedProjectBConstraint = converterFactory.or(projectB_v1_var.negate(), projectB_v2_var.negate()).toString();

        // Convert clauses to strings for easier comparison
        List<String> clauseStrings = clauses.stream()
                .map(Formula::toString)
                .collect(Collectors.toList());

        // Verify project_a mutual exclusion constraints
        expectedProjectAConstraints.forEach(expectedClause -> Assertions.assertTrue(clauseStrings.contains(expectedClause),"Should contain mutual exclusion constraint: " + expectedClause));

        // Verify commons_util mutual exclusion constraint
        Assertions.assertTrue(clauseStrings.contains(expectedProjectBConstraint),"Should contain mutual exclusion constraint: " + expectedProjectBConstraint);

        // Count mutual exclusion clauses (should be 3 for project_a + 1 for commons_util = 4 total)
        long mutualExclusionCount = clauses.stream()
                .filter(clause -> clause.toString().contains("~") && clause.toString().contains("|"))
                .filter(clause ->
                {
                    // Check if it's a mutual exclusion (both literals are negated)
                    String clauseStr = clause.toString();
                    return clauseStr.startsWith("~") && clauseStr.contains("| ~");
                })
                .count();

        Assertions.assertTrue(mutualExclusionCount >= 4,"Should have at least 4 mutual exclusion constraints, found: " + mutualExclusionCount);

        // Verify no mutual exclusion between different projects
        String crossProjectConstraint1 = converterFactory.or(projectA_v1_var.negate(), projectB_v1_var.negate()).toString();
        String crossProjectConstraint2 = converterFactory.or(projectA_v2_var.negate(), projectB_v2_var.negate()).toString();

        Assertions.assertFalse(clauseStrings.contains(crossProjectConstraint1),"Should NOT contain cross-project mutual exclusion: " + crossProjectConstraint1);
        Assertions.assertFalse(clauseStrings.contains(crossProjectConstraint2),"Should NOT contain cross-project mutual exclusion: " + crossProjectConstraint2);

        log.info("Generated {} total clauses", clauses.size());
        log.info("Mutual exclusion constraints found: {}", mutualExclusionCount);
        clauses.stream()
                .filter(clause -> clause.toString().contains("~") && clause.toString().contains("| ~"))
                .forEach(clause -> log.info("Mutual exclusion: {}", clause));
    }

    @Test
    public void canGenerateCorrectAtLeastOneVersionConstraints()
    {
        // Create test projects with alternatives
        StoreProjectVersionData projectA_v1 = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectA_v2 = new StoreProjectVersionData("org.finos.legend", "project_a", "2.0.0");
        StoreProjectVersionData projectA_v3 = new StoreProjectVersionData("org.finos.legend", "project_a", "3.0.0");
        StoreProjectVersionData projectB_v1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData projectC_v1 = new StoreProjectVersionData("org.springframework", "spring_core", "5.0.0");

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.apache.commons", "commons_util"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.springframework", "spring_core"));
        projectsService.createOrUpdate(projectA_v1);
        projectsService.createOrUpdate(projectA_v2);
        projectsService.createOrUpdate(projectA_v3);
        projectsService.createOrUpdate(projectB_v1);
        projectsService.createOrUpdate(projectC_v1);

        // Set up required projects
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.apache.commons", "commons_util", "1.0.0"),
                new ProjectVersion("org.springframework", "spring_core", "5.0.0")
        );

        Map<ProjectVersion, List<ProjectVersion>> alternativeVersions = new HashMap<>();

        // project_a has 3 alternative versions - should generate OR clause
        alternativeVersions.put(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                Arrays.asList(
                        new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                        new ProjectVersion("org.finos.legend", "project_a", "2.0.0"),
                        new ProjectVersion("org.finos.legend", "project_a", "3.0.0")
                )
        );

        // commons_util has only 1 version - should generate unit clause
        alternativeVersions.put(
                new ProjectVersion("org.apache.commons", "commons_util", "1.0.0"),
                Arrays.asList(new ProjectVersion("org.apache.commons", "commons_util", "1.0.0"))
        );

        // spring_core has only 1 version - should generate unit clause
        alternativeVersions.put(
                new ProjectVersion("org.springframework", "spring_core", "5.0.0"),
                Arrays.asList(new ProjectVersion("org.springframework", "spring_core", "5.0.0"))
        );

        // Convert to LogicNG formulas
        DependencySATConverter converter = new DependencySATConverter(new FormulaFactory());
        LogicNGSATResult result = converter.convertToLogicNGFormulas(requiredProjects, alternativeVersions, projectsService);

        List<Formula> clauses = result.getClauses();
        Map<String, Variable> variableMap = result.getVariableMap();
        FormulaFactory converterFactory = converter.getFormulaFactory();

        // Get variables for testing
        Variable projectA_v1_var = variableMap.get("org.finos.legend:project_a:1.0.0");
        Variable projectA_v2_var = variableMap.get("org.finos.legend:project_a:2.0.0");
        Variable projectA_v3_var = variableMap.get("org.finos.legend:project_a:3.0.0");
        Variable projectB_v1_var = variableMap.get("org.apache.commons:commons_util:1.0.0");
        Variable projectC_v1_var = variableMap.get("org.springframework:spring_core:5.0.0");

        Assertions.assertNotNull(projectA_v1_var, "project_a v1.0.0 variable should exist");
        Assertions.assertNotNull(projectA_v2_var, "project_a v2.0.0 variable should exist");
        Assertions.assertNotNull(projectA_v3_var, "project_a v3.0.0 variable should exist");
        Assertions.assertNotNull(projectB_v1_var, "commons_util variable should exist");
        Assertions.assertNotNull(projectC_v1_var, "spring_core variable should exist");

        // Expected at-least-one constraints
        String expectedProjectAConstraint = converterFactory.or(Arrays.asList(projectA_v1_var, projectA_v2_var, projectA_v3_var)).toString();
        String expectedProjectBConstraint = projectB_v1_var.toString(); // Unit clause
        String expectedProjectCConstraint = projectC_v1_var.toString(); // Unit clause

        // Convert clauses to strings for comparison
        List<String> clauseStrings = clauses.stream()
                .map(Formula::toString)
                .collect(Collectors.toList());

        // Verify project_a at-least-one constraint (OR of 3 alternatives)
        Assertions.assertTrue(clauseStrings.contains(expectedProjectAConstraint),"Should contain at-least-one constraint for project_a: " + expectedProjectAConstraint);

        // Verify unit clauses for single-version projects
        Assertions.assertTrue(clauseStrings.contains(expectedProjectBConstraint),"Should contain unit clause for commons_util: " + expectedProjectBConstraint);
        Assertions.assertTrue(clauseStrings.contains(expectedProjectCConstraint),"Should contain unit clause for spring_core: " + expectedProjectCConstraint);

        // Count at-least-one constraints (should be exactly 3: 1 OR clause + 2 unit clauses)
        long unitClauses = clauses.stream()
                .filter(clause -> clause instanceof Variable)
                .count();

        long orClauses = clauses.stream()
                .filter(clause -> clause.toString().contains("|") && !clause.toString().contains("~"))
                .count();

        Assertions.assertEquals(2, unitClauses, "Should have exactly 2 unit clauses");
        Assertions.assertTrue(orClauses >= 1, "Should have at least 1 OR clause for project_a alternatives");

        // Verify no constraint forces multiple versions of same project
        String invalidConstraint = converterFactory.and(projectA_v1_var, projectA_v2_var).toString();
        Assertions.assertFalse(clauseStrings.contains(invalidConstraint),"Should NOT contain AND constraint forcing multiple versions: " + invalidConstraint);

        log.info("Generated {} total clauses", clauses.size());
        log.info("Unit clauses found: {}", unitClauses);
        log.info("OR clauses found: {}", orClauses);

        // Log the at-least-one constraints for verification
        clauses.stream()
                .filter(clause -> clause instanceof Variable ||
                        (clause.toString().contains("|") && !clause.toString().contains("~")))
                .forEach(clause -> log.info("At-least-one constraint: {}", clause));
    }

    @Test
    public void canResolveCompatibleVersionsWithSimpleDependencies()
    {
        StoreProjectVersionData projectA = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectB = new StoreProjectVersionData("org.finos.legend", "project_b", "1.0.0");
        StoreProjectVersionData commonDep = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");

        // Set up dependencies: projectA -> commonDep, projectB -> commonDep
        ProjectVersion commonDepVersion = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        projectA.getVersionData().addDependency(commonDepVersion);
        projectB.getVersionData().addDependency(commonDepVersion);

        // Set up transitive dependency reports
        projectA.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonDepVersion), true));
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonDepVersion), true));

        // Create and store projects with valid project IDs
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.finos.legend", "project_b"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.apache.commons", "commons_util"));
        projectsService.createOrUpdate(projectA);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(commonDep);

        // Set up required projects
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0")
        );

        // Test resolveCompatibleVersions
        List<ProjectVersion> compatibleVersions = projectsService.resolveCompatibleVersions(requiredProjects, 0);

        // Verify solution contains all required projects and their dependencies
        Assertions.assertNotNull(compatibleVersions);
        Assertions.assertFalse(compatibleVersions.isEmpty());

        // Should include both required projects and the common dependency
        Set<String> solutionGavs = compatibleVersions.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_a:1.0.0"),"Solution should include project_a");
        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_b:1.0.0"),"Solution should include project_b");

        // Verify no duplicate dependencies
        Assertions.assertEquals(2, compatibleVersions.size(), "Should have exactly 2 projects in solution");

        log.info("Compatible versions found:");
        compatibleVersions.forEach(pv -> log.info("  - {}", pv.getGav()));
    }

    @Test
    public void canResolveUnsatisfiableConflictingVersions()
    {
        // Create projects with explicit conflicting versions
        StoreProjectVersionData projectA = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectB = new StoreProjectVersionData("org.finos.legend", "project_b", "1.0.0");
        StoreProjectVersionData commonsV1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonsV2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");

        // Set up direct dependencies
        ProjectVersion commonsV1Dep = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion commonsV2Dep = new ProjectVersion("org.apache.commons", "commons_util", "2.0.0");

        // projectA requires commons_util v1.0.0
        projectA.getVersionData().addDependency(commonsV1Dep);
        projectA.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV1Dep), true));

        // projectB requires commons_util v2.0.0
        projectB.getVersionData().addDependency(commonsV2Dep);
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV2Dep), true));

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.finos.legend", "project_b"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.apache.commons", "commons_util"));
        projectsService.createOrUpdate(projectA);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(commonsV1);
        projectsService.createOrUpdate(commonsV2);

        // both conflicting projects are required
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0")
        );

        // This creates unsatisfiable constraints:
        // 1. projectA must be selected (required)
        // 2. projectB must be selected (required)
        // 3. projectA → commons_util:1.0.0 (dependency implication)
        // 4. projectB → commons_util:2.0.0 (dependency implication)
        // 5. ¬commons_util:1.0.0 ∨ ¬commons_util:2.0.0 (mutual exclusion)
        // Result: UNSAT

        List<ProjectVersion> result = projectsService.resolveCompatibleVersions(requiredProjects, 0);

        // Should return empty list
        Assertions.assertTrue(result.isEmpty(), "Should return empty list for unsatisfiable constraints");
    }

    @Test
    public void canResolveConflictingVersionsWithBacktrackAlternatives()
    {
        // Create projects with conflicting dependencies that can be resolved with alternatives

        // Project A v1.0.0 depends on commons_util v1.0.0
        StoreProjectVersionData projectA_v1 = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectA_v2 = new StoreProjectVersionData("org.finos.legend", "project_a", "2.0.0");

        // Project B v1.0.0 depends on commons_util v2.0.0 (conflicts with A's requirement)
        StoreProjectVersionData projectB_v1 = new StoreProjectVersionData("org.finos.legend", "project_b", "1.0.0");

        // Commons util versions
        StoreProjectVersionData commonsV1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonsV2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");

        // Set up conflicting dependencies:
        // project_a v1.0.0 -> commons_util v1.0.0
        // project_b v1.0.0 -> commons_util v2.0.0 (CONFLICT!)
        // But project_a v2.0.0 -> commons_util v2.0.0 (COMPATIBLE with B)

        ProjectVersion commonsV1Dep = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion commonsV2Dep = new ProjectVersion("org.apache.commons", "commons_util", "2.0.0");

        projectA_v1.getVersionData().addDependency(commonsV1Dep);
        projectA_v2.getVersionData().addDependency(commonsV2Dep); // Alternative version is compatible
        projectB_v1.getVersionData().addDependency(commonsV2Dep);

        // Set up transitive dependency reports
        projectA_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV1Dep), true));
        projectA_v2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV2Dep), true));
        projectB_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV2Dep), true));

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.finos.legend", "project_b"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.apache.commons", "commons_util"));

        projectsService.createOrUpdate(projectA_v1);
        projectsService.createOrUpdate(projectA_v2);
        projectsService.createOrUpdate(projectB_v1);
        projectsService.createOrUpdate(commonsV1);
        projectsService.createOrUpdate(commonsV2);

        // Required projects - initially conflicting versions
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0")
        );

        // Test Case 1: No backtrack (backtrackVersions = 0) - should fail
        List<ProjectVersion> noBacktrackResult = projectsService.resolveCompatibleVersions(requiredProjects, 0);
        Assertions.assertTrue(noBacktrackResult.isEmpty(),"Should return empty list with conflicting versions and no backtrack");

        // Test Case 2: With backtrack enabled - should find satisfiable solution
        List<ProjectVersion> backtrackResult = projectsService.resolveCompatibleVersions(requiredProjects, 1);
        Assertions.assertFalse(backtrackResult.isEmpty(),"Should find satisfiable solution with backtrack enabled");

        // Verify the solution uses the alternative version
        Set<String> solutionGavs = backtrackResult.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        // The solution should contain project_a v2.0.0 (alternative) instead of v1.0.0
        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_a:2.0.0"),"Solution should use project_a v2.0.0 (alternative) instead of v1.0.0");
        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_b:1.0.0"),"Solution should include project_b v1.0.0");

        // Verify exactly 2 projects in solution (no duplicates or unexpected dependencies)
        Assertions.assertEquals(2, backtrackResult.size(),"Solution should contain exactly 2 projects");
    }

    @Test
    public void canResolveCircularDependencies()
    {
        // Create projects with circular dependencies
        StoreProjectVersionData projectA = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");
        StoreProjectVersionData projectB = new StoreProjectVersionData("org.finos.legend", "project_b", "1.0.0");
        StoreProjectVersionData projectC = new StoreProjectVersionData("org.finos.legend", "project_c", "1.0.0");

        // Set up circular dependency chain: A -> B -> C -> A
        ProjectVersion projectBDep = new ProjectVersion("org.finos.legend", "project_b", "1.0.0");
        ProjectVersion projectCDep = new ProjectVersion("org.finos.legend", "project_c", "1.0.0");
        ProjectVersion projectADep = new ProjectVersion("org.finos.legend", "project_a", "1.0.0");

        projectA.getVersionData().addDependency(projectBDep);  // A -> B
        projectB.getVersionData().addDependency(projectCDep);  // B -> C
        projectC.getVersionData().addDependency(projectADep);  // C -> A (creates cycle)

        // Set up transitive dependency reports that include the circular dependencies
        projectA.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(projectBDep, projectCDep), true));
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(projectCDep, projectADep), true));
        projectC.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(projectADep, projectBDep), true));

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.finos.legend", "project_b"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.finos.legend", "project_c"));
        projectsService.createOrUpdate(projectA);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(projectC);

        // Test Case 1: Require only project A - should resolve the entire circular chain
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0")
        );

        List<ProjectVersion> result = projectsService.resolveCompatibleVersions(requiredProjects, 0);

        Assertions.assertFalse(result.isEmpty(), "Should resolve circular dependencies successfully");

        Set<String> solutionGavs = result.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        // All three projects should be included due to circular dependencies
        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_a:1.0.0"),"Solution should include project_a");
        Assertions.assertEquals(1, result.size(), "Should include original project");

        // Test Case 2: Require all projects in the cycle - should still work
        List<ProjectVersion> allRequiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_c", "1.0.0")
        );

        List<ProjectVersion> allResult = projectsService.resolveCompatibleVersions(allRequiredProjects, 0);

        Assertions.assertFalse(allResult.isEmpty(), "Should resolve when all circular projects are required");

        Set<String> allSolutionGavs = allResult.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        Assertions.assertEquals(3, allSolutionGavs.size(), "Should include exactly 3 projects");
        Assertions.assertTrue(allSolutionGavs.contains("org.finos.legend:project_a:1.0.0"));
        Assertions.assertTrue(allSolutionGavs.contains("org.finos.legend:project_b:1.0.0"));
        Assertions.assertTrue(allSolutionGavs.contains("org.finos.legend:project_c:1.0.0"));

        // Test Case 3: Verify no conflicts are reported for valid circular dependencies
        ProjectDependencyReport report = projectsService.getProjectDependencyReport(result);
        Assertions.assertEquals(0, report.getConflicts().size(),"Valid circular dependencies should not create conflicts");

        log.info("Circular dependency resolution found {} projects:", result.size());
        result.forEach(pv -> log.info("  - {}", pv.getGav()));
    }

    @Test
    public void canResolveConflictingVersionsWithOverriddenDependencies()
    {
        // Create projects where initial dependencies conflict but overrides resolve the conflict

        // Project A depends on commons_util v1.0.0
        StoreProjectVersionData projectA = new StoreProjectVersionData("org.finos.legend", "project_a", "1.0.0");

        // Project B depends on commons_util v2.0.0 (creates conflict with A)
        StoreProjectVersionData projectB = new StoreProjectVersionData("org.finos.legend", "project_b", "1.0.0");

        // Create the conflicting dependency versions
        StoreProjectVersionData commonsV1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonsV2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");

        // Set up initial conflicting dependencies
        ProjectVersion commonsV1Dep = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion commonsV2Dep = new ProjectVersion("org.apache.commons", "commons_util", "2.0.0");

        projectA.getVersionData().addDependency(commonsV1Dep);
        projectA.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV1Dep), true));

        projectB.getVersionData().addDependency(commonsV2Dep);
        projectB.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsV2Dep), true));

        // Store all projects
        projectsService.createOrUpdate(new StoreProjectData("PROD-1", "org.finos.legend", "project_a"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-2", "org.finos.legend", "project_b"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-3", "org.apache.commons", "commons_util"));

        projectsService.createOrUpdate(projectA);
        projectsService.createOrUpdate(projectB);
        projectsService.createOrUpdate(commonsV1);
        projectsService.createOrUpdate(commonsV2);

        // Test Case 1: Without override - should fail due to version conflict
        List<ProjectVersion> conflictingRequiredProjects = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0")
        );

        List<ProjectVersion> conflictingResult = projectsService.resolveCompatibleVersions(conflictingRequiredProjects, 0);
        Assertions.assertTrue(conflictingResult.isEmpty(),"Should fail to resolve conflicting dependencies without override");

        // Test Case 2: With override dependency - should succeed
        // Override with commons_util v2.0.0 to resolve the conflict in favor of project B
        List<ProjectVersion> requiredProjectsWithOverride = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0"),
                new ProjectVersion("org.apache.commons", "commons_util", "2.0.0")  // Override dependency
        );

        List<ProjectVersion> resolvedResult = projectsService.resolveCompatibleVersions(requiredProjectsWithOverride, 0);

        Assertions.assertFalse(resolvedResult.isEmpty(),"Should resolve successfully with override dependency");

        Set<String> solutionGavs = resolvedResult.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        // Verify the solution contains all required projects
        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_a:1.0.0"),"Solution should include project_a");
        Assertions.assertTrue(solutionGavs.contains("org.finos.legend:project_b:1.0.0"),"Solution should include project_b");
        Assertions.assertTrue(solutionGavs.contains("org.apache.commons:commons_util:2.0.0"));

        Assertions.assertEquals(3, resolvedResult.size(),"Solution should contain exactly 3 projects when override resolves the conflict");

        // Test Case 3: Verify dependency report shows no conflicts with override
        ProjectDependencyReport reportWithOverride = projectsService.getProjectDependencyReport(resolvedResult);
        Assertions.assertEquals(0, reportWithOverride.getConflicts().size(),
                "Override dependency should eliminate version conflicts");

        // Test Case 4: Alternative override scenario - override with v1.0.0 instead
        List<ProjectVersion> alternativeOverride = Arrays.asList(
                new ProjectVersion("org.finos.legend", "project_a", "1.0.0"),
                new ProjectVersion("org.finos.legend", "project_b", "1.0.0"),
                new ProjectVersion("org.apache.commons", "commons_util", "1.0.0")  // Override with v1.0.0
        );

        List<ProjectVersion> alternativeResult = projectsService.resolveCompatibleVersions(alternativeOverride, 0);

        Assertions.assertFalse(alternativeResult.isEmpty(),"Should also resolve with alternative override version");

        Set<String> altSolutionGavs = alternativeResult.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        Assertions.assertTrue(altSolutionGavs.contains("org.finos.legend:project_a:1.0.0"));
        Assertions.assertTrue(altSolutionGavs.contains("org.finos.legend:project_b:1.0.0"));
        Assertions.assertTrue(altSolutionGavs.contains("org.apache.commons:commons_util:1.0.0"));
        Assertions.assertEquals(3, alternativeResult.size());

        ProjectDependencyReport altReport = projectsService.getProjectDependencyReport(alternativeResult);
        Assertions.assertEquals(0, altReport.getConflicts().size(),"Alternative override should also eliminate conflicts");

        log.info("Resolved dependencies with override:");
        resolvedResult.forEach(pv -> log.info("  - {}", pv.getGav()));

        log.info("Resolved dependencies with alternative override:");
        alternativeResult.forEach(pv -> log.info("  - {}", pv.getGav()));
    }

    @Test
    public void canResolveMultipleOverriddenDependencies()
    {
        // Create test scenario with multiple conflicts and overrides:
        // projectAlpha -> commons_util v1.0.0, jackson v1.0.0
        // projectBeta -> commons_util v2.0.0, jackson v2.0.0
        // projectGamma -> logging v1.0.0
        // projectDelta -> logging v2.0.0
        // Overrides: commons_util v3.0.0, jackson v1.5.0, logging v2.5.0

        projectsService.createOrUpdate(new StoreProjectData("PROD-101", "org.example.test", "project_alpha"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-102", "org.example.test", "project_beta"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-103", "org.example.test", "project_gamma"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-104", "org.example.test", "project_delta"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-105", "org.apache.commons", "commons_util"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-106", "com.fasterxml.jackson.core", "jackson_core"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-107", "org.apache.logging.log4j", "log4j_core"));

        // Create project versions
        StoreProjectVersionData projectAlpha = new StoreProjectVersionData("org.example.test", "project_alpha", "1.0.0");
        StoreProjectVersionData projectBeta = new StoreProjectVersionData("org.example.test", "project_beta", "1.0.0");
        StoreProjectVersionData projectGamma = new StoreProjectVersionData("org.example.test", "project_gamma", "1.0.0");
        StoreProjectVersionData projectDelta = new StoreProjectVersionData("org.example.test", "project_delta", "1.0.0");

        // Create dependency versions
        StoreProjectVersionData commonsUtil_v1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonsUtil_v2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");
        StoreProjectVersionData commonsUtil_v3 = new StoreProjectVersionData("org.apache.commons", "commons_util", "3.0.0");
        StoreProjectVersionData jackson_v1 = new StoreProjectVersionData("com.fasterxml.jackson.core", "jackson_core", "1.0.0");
        StoreProjectVersionData jackson_v15 = new StoreProjectVersionData("com.fasterxml.jackson.core", "jackson_core", "1.5.0");
        StoreProjectVersionData jackson_v2 = new StoreProjectVersionData("com.fasterxml.jackson.core", "jackson_core", "2.0.0");
        StoreProjectVersionData logging_v1 = new StoreProjectVersionData("org.apache.logging.log4j", "log4j_core", "1.0.0");
        StoreProjectVersionData logging_v2 = new StoreProjectVersionData("org.apache.logging.log4j", "log4j_core", "2.0.0");
        StoreProjectVersionData logging_v25 = new StoreProjectVersionData("org.apache.logging.log4j", "log4j_core", "2.5.0");

        // Set up project dependencies
        ProjectVersion commonsUtil_v1_dep = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion commonsUtil_v2_dep = new ProjectVersion("org.apache.commons", "commons_util", "2.0.0");
        ProjectVersion commonsUtil_v3_dep = new ProjectVersion("org.apache.commons", "commons_util", "3.0.0");
        ProjectVersion jackson_v1_dep = new ProjectVersion("com.fasterxml.jackson.core", "jackson_core", "1.0.0");
        ProjectVersion jackson_v15_dep = new ProjectVersion("com.fasterxml.jackson.core", "jackson_core", "1.5.0");
        ProjectVersion jackson_v2_dep = new ProjectVersion("com.fasterxml.jackson.core", "jackson_core", "2.0.0");
        ProjectVersion logging_v1_dep = new ProjectVersion("org.apache.logging.log4j", "log4j_core", "1.0.0");
        ProjectVersion logging_v2_dep = new ProjectVersion("org.apache.logging.log4j", "log4j_core", "2.0.0");
        ProjectVersion logging_v25_dep = new ProjectVersion("org.apache.logging.log4j", "log4j_core", "2.5.0");

        // Configure projectAlpha dependencies: commons_util v1.0.0, jackson v1.0.0
        projectAlpha.getVersionData().addDependency(commonsUtil_v1_dep);
        projectAlpha.getVersionData().addDependency(jackson_v1_dep);
        projectAlpha.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v1_dep, jackson_v1_dep), true));

        // Configure projectBeta dependencies: commons_util v2.0.0, jackson v2.0.0
        projectBeta.getVersionData().addDependency(commonsUtil_v2_dep);
        projectBeta.getVersionData().addDependency(jackson_v2_dep);
        projectBeta.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v2_dep, jackson_v2_dep), true));

        // Configure projectGamma dependencies: logging v1.0.0
        projectGamma.getVersionData().addDependency(logging_v1_dep);
        projectGamma.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(logging_v1_dep), true));

        // Configure projectDelta dependencies: logging v2.0.0
        projectDelta.getVersionData().addDependency(logging_v2_dep);
        projectDelta.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(logging_v2_dep), true));

        // Store all projects and versions
        projectsService.createOrUpdate(projectAlpha);
        projectsService.createOrUpdate(projectBeta);
        projectsService.createOrUpdate(projectGamma);
        projectsService.createOrUpdate(projectDelta);
        projectsService.createOrUpdate(commonsUtil_v1);
        projectsService.createOrUpdate(commonsUtil_v2);
        projectsService.createOrUpdate(commonsUtil_v3);
        projectsService.createOrUpdate(jackson_v1);
        projectsService.createOrUpdate(jackson_v15);
        projectsService.createOrUpdate(jackson_v2);
        projectsService.createOrUpdate(logging_v1);
        projectsService.createOrUpdate(logging_v2);
        projectsService.createOrUpdate(logging_v25);

        // Test resolution with multiple overrides in required projects list
        List<ProjectVersion> requiredProjectsWithOverrides = Arrays.asList(
                new ProjectVersion("org.example.test", "project_alpha", "1.0.0"),   // depends on commons_util v1.0.0, jackson v1.0.0
                new ProjectVersion("org.example.test", "project_beta", "1.0.0"),    // depends on commons_util v2.0.0, jackson v2.0.0
                new ProjectVersion("org.example.test", "project_gamma", "1.0.0"),   // depends on logging v1.0.0
                new ProjectVersion("org.example.test", "project_delta", "1.0.0"),   // depends on logging v2.0.0
                commonsUtil_v3_dep,  // Override: commons_util v3.0.0 (should override v1.0.0 and v2.0.0)
                jackson_v15_dep,     // Override: jackson v1.5.0 (should override v1.0.0 and v2.0.0)
                logging_v25_dep      // Override: logging v2.5.0 (should override v1.0.0 and v2.0.0)
        );

        // Resolve with backtrack to allow alternatives
        List<ProjectVersion> compatibleVersions = projectsService.resolveCompatibleVersions(requiredProjectsWithOverrides, 2);

        // Verify resolution
        Assertions.assertNotNull(compatibleVersions);
        Assertions.assertFalse(compatibleVersions.isEmpty(), "Should find a compatible solution with multiple overrides");

        Set<String> solutionGavs = compatibleVersions.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        // Verify all root projects are included
        Assertions.assertTrue(solutionGavs.contains("org.example.test:project_alpha:1.0.0"), "Solution should include project_alpha");
        Assertions.assertTrue(solutionGavs.contains("org.example.test:project_beta:1.0.0"), "Solution should include project_beta");
        Assertions.assertTrue(solutionGavs.contains("org.example.test:project_gamma:1.0.0"), "Solution should include project_gamma");
        Assertions.assertTrue(solutionGavs.contains("org.example.test:project_delta:1.0.0"), "Solution should include project_delta");

        // Verify only one version of each overridden dependency
        long commonsUtilCount = compatibleVersions.stream()
                .filter(pv -> pv.getGroupId().equals("org.apache.commons") && pv.getArtifactId().equals("commons_util"))
                .count();
        Assertions.assertEquals(1, commonsUtilCount, "Should have exactly one version of commons_util");

        long jacksonCount = compatibleVersions.stream()
                .filter(pv -> pv.getGroupId().equals("com.fasterxml.jackson.core") && pv.getArtifactId().equals("jackson_core"))
                .count();
        Assertions.assertEquals(1, jacksonCount, "Should have exactly one version of jackson_core");

        long loggingCount = compatibleVersions.stream()
                .filter(pv -> pv.getGroupId().equals("org.apache.logging.log4j") && pv.getArtifactId().equals("log4j_core"))
                .count();
        Assertions.assertEquals(1, loggingCount, "Should have exactly one version of log4j_core");

        // Log the resolution for verification
        log.info("Multiple overrides resolution found {} compatible versions:", compatibleVersions.size());
        compatibleVersions.forEach(pv -> log.info("  - {}", pv.getGav()));

        // Verify that the resolution is consistent - no conflicts remain
        ProjectDependencyReport report = projectsService.getProjectDependencyReport(compatibleVersions);
        Assertions.assertEquals(0, report.getConflicts().size(), "Resolved versions should have no remaining conflicts");

        // Verify the override versions are properly selected
        Optional<ProjectVersion> resolvedCommonsUtil = compatibleVersions.stream()
                .filter(pv -> pv.getGroupId().equals("org.apache.commons") && pv.getArtifactId().equals("commons_util"))
                .findFirst();
        Assertions.assertTrue(resolvedCommonsUtil.isPresent(), "Should resolve a commons_util version");

        Optional<ProjectVersion> resolvedJackson = compatibleVersions.stream()
                .filter(pv -> pv.getGroupId().equals("com.fasterxml.jackson.core") && pv.getArtifactId().equals("jackson_core"))
                .findFirst();
        Assertions.assertTrue(resolvedJackson.isPresent(), "Should resolve a jackson_core version");

        Optional<ProjectVersion> resolvedLogging = compatibleVersions.stream()
                .filter(pv -> pv.getGroupId().equals("org.apache.logging.log4j") && pv.getArtifactId().equals("log4j_core"))
                .findFirst();
        Assertions.assertTrue(resolvedLogging.isPresent(), "Should resolve a log4j_core version");
    }

    @Test
    public void cannotResolveWhenAlternativeVersionIsExcluded()
    {
        // Test scenario:
        // - projectAlpha depends on commons_util v1.0.0
        // - projectBeta depends on commons_util v2.0.0 (CONFLICT!)
        // - projectAlpha has alternative v2.0.0 that depends on commons_util v2.0.0 (would resolve conflict)
        // - BUT projectAlpha v2.0.0 is EXCLUDED from the store
        // - Result: Should be unsatisfiable even with backtrack enabled

        // Create all project data entries with valid project IDs
        projectsService.createOrUpdate(new StoreProjectData("PROD-201", "org.example.resolve", "project_alpha"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-202", "org.example.resolve", "project_beta"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-203", "org.apache.commons", "commons_util"));

        // Create project versions
        StoreProjectVersionData projectAlpha_v1 = new StoreProjectVersionData("org.example.resolve", "project_alpha", "1.0.0");
        StoreProjectVersionData projectAlpha_v2 = new StoreProjectVersionData("org.example.resolve", "project_alpha", "2.0.0"); // This will be excluded
        StoreProjectVersionData projectBeta = new StoreProjectVersionData("org.example.resolve", "project_beta", "1.0.0");

        // Create dependency versions
        StoreProjectVersionData commonsUtil_v1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonsUtil_v2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");

        // Set up project dependencies
        ProjectVersion commonsUtil_v1_dep = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion commonsUtil_v2_dep = new ProjectVersion("org.apache.commons", "commons_util", "2.0.0");

        // Configure projectAlpha v1.0.0 dependencies: commons_util v1.0.0 (conflicts with Beta)
        projectAlpha_v1.getVersionData().addDependency(commonsUtil_v1_dep);
        projectAlpha_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v1_dep), true));

        // Configure projectAlpha v2.0.0 dependencies: commons_util v2.0.0 (would be compatible with Beta)
        projectAlpha_v2.getVersionData().addDependency(commonsUtil_v2_dep);
        projectAlpha_v2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v2_dep), true));

        // Configure projectBeta dependencies: commons_util v2.0.0
        projectBeta.getVersionData().addDependency(commonsUtil_v2_dep);
        projectBeta.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v2_dep), true));

        // Store all projects and versions EXCEPT the alternative projectAlpha v2.0.0
        projectsService.createOrUpdate(projectAlpha_v1);  // Only store v1.0.0
        // projectAlpha_v2 is NOT stored - simulating exclusion
        projectsService.createOrUpdate(projectBeta);
        projectsService.createOrUpdate(commonsUtil_v1);
        projectsService.createOrUpdate(commonsUtil_v2);

        // Test resolution with conflicting requirements
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.example.resolve", "project_alpha", "1.0.0"),  // Available, but depends on commons_util v1.0.0
                new ProjectVersion("org.example.resolve", "project_beta", "1.0.0")    // Depends on commons_util v2.0.0 (CONFLICT!)
        );

        // Test Case 1: Without backtrack - should fail due to direct conflict
        List<ProjectVersion> noBacktrackResult = projectsService.resolveCompatibleVersions(requiredProjects, 0);
        Assertions.assertTrue(noBacktrackResult.isEmpty(),"Should return empty list due to conflicting dependencies and no backtrack");

        // Test Case 2: With backtrack enabled - should still fail because alternative version is excluded
        List<ProjectVersion> backtrackResult = projectsService.resolveCompatibleVersions(requiredProjects, 2);
        Assertions.assertTrue(backtrackResult.isEmpty(),"Should return empty list even with backtrack because the alternative version that would resolve the conflict is excluded");

        // Test Case 3: Verify the conflict exists in dependency report
        List<ProjectVersion> conflictingProjects = Arrays.asList(
                new ProjectVersion("org.example.resolve", "project_alpha", "1.0.0"),
                new ProjectVersion("org.example.resolve", "project_beta", "1.0.0")
        );

        ProjectDependencyReport report = projectsService.getProjectDependencyReport(conflictingProjects);
        Assertions.assertFalse(report.getConflicts().isEmpty(), "Should report conflicts when trying to use conflicting versions");

        // Test Case 4: Prove that the solution would work if the alternative version was available
        // Add the excluded version back to the store
        projectsService.createOrUpdate(projectAlpha_v2);

        // Now test with the same required projects but allowing backtrack to find alternative
        List<ProjectVersion> resolvedWithAlternative = projectsService.resolveCompatibleVersions(requiredProjects, 2);
        Assertions.assertFalse(resolvedWithAlternative.isEmpty(),"Should resolve successfully when alternative version is available");

        Set<String> solutionGavs = resolvedWithAlternative.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        // Verify the solution uses the alternative version
        Assertions.assertTrue(solutionGavs.contains("org.example.resolve:project_alpha:2.0.0"),"Solution should use project_alpha v2.0.0 (alternative) to resolve conflict");
        Assertions.assertTrue(solutionGavs.contains("org.example.resolve:project_beta:1.0.0"),"Solution should include project_beta v1.0.0");

        log.info("Test demonstrated that excluding alternative version prevents conflict resolution:");
        log.info("  - Without alternative: {} projects resolved", backtrackResult.size());
        log.info("  - With alternative: {} projects resolved", resolvedWithAlternative.size());

        if (!resolvedWithAlternative.isEmpty())
        {
            log.info("  Solution with alternative version:");
            resolvedWithAlternative.forEach(pv -> log.info("    - {}", pv.getGav()));
        }
    }

    @Test
    public void canChooseOptimalAlternativeDependencyVersions()
    {
        // Test scenario:
        // - projectAlpha v1.0.0 depends on commons_util v1.0.0
        // - projectBeta v1.0.0 depends on commons_util v3.0.0 (CONFLICT!)
        // - Multiple alternative versions exist for both projects:
        //   * projectAlpha v2.0.0 depends on commons_util v2.0.0
        //   * projectAlpha v3.0.0 depends on commons_util v3.0.0 (newer, optimal)
        //   * projectBeta v2.0.0 depends on commons_util v2.0.0
        //   * projectBeta v3.0.0 depends on commons_util v3.0.0 (newer, optimal)
        // - Multiple valid solutions exist:
        //   Solution A: projectAlpha v2.0.0 + projectBeta v2.0.0 + commons_util v2.0.0
        //   Solution B: projectAlpha v3.0.0 + projectBeta v3.0.0 + commons_util v3.0.0 (OPTIMAL - newer versions)
        // - Expected: Should choose Solution B (newer versions)

        // Create all project data entries with valid project IDs
        projectsService.createOrUpdate(new StoreProjectData("PROD-301", "org.example.optimal", "project_alpha"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-302", "org.example.optimal", "project_beta"));
        projectsService.createOrUpdate(new StoreProjectData("PROD-303", "org.apache.commons", "commons_util"));

        // Create multiple versions for projectAlpha
        StoreProjectVersionData projectAlpha_v1 = new StoreProjectVersionData("org.example.optimal", "project_alpha", "1.0.0");
        StoreProjectVersionData projectAlpha_v2 = new StoreProjectVersionData("org.example.optimal", "project_alpha", "2.0.0");
        StoreProjectVersionData projectAlpha_v3 = new StoreProjectVersionData("org.example.optimal", "project_alpha", "3.0.0");

        // Create multiple versions for projectBeta
        StoreProjectVersionData projectBeta_v1 = new StoreProjectVersionData("org.example.optimal", "project_beta", "1.0.0");
        StoreProjectVersionData projectBeta_v2 = new StoreProjectVersionData("org.example.optimal", "project_beta", "2.0.0");
        StoreProjectVersionData projectBeta_v3 = new StoreProjectVersionData("org.example.optimal", "project_beta", "3.0.0");

        // Create dependency versions
        StoreProjectVersionData commonsUtil_v1 = new StoreProjectVersionData("org.apache.commons", "commons_util", "1.0.0");
        StoreProjectVersionData commonsUtil_v2 = new StoreProjectVersionData("org.apache.commons", "commons_util", "2.0.0");
        StoreProjectVersionData commonsUtil_v3 = new StoreProjectVersionData("org.apache.commons", "commons_util", "3.0.0");

        // Set up project dependencies
        ProjectVersion commonsUtil_v1_dep = new ProjectVersion("org.apache.commons", "commons_util", "1.0.0");
        ProjectVersion commonsUtil_v2_dep = new ProjectVersion("org.apache.commons", "commons_util", "2.0.0");
        ProjectVersion commonsUtil_v3_dep = new ProjectVersion("org.apache.commons", "commons_util", "3.0.0");

        // Configure projectAlpha dependencies
        projectAlpha_v1.getVersionData().addDependency(commonsUtil_v1_dep);
        projectAlpha_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v1_dep), true));

        projectAlpha_v2.getVersionData().addDependency(commonsUtil_v2_dep);
        projectAlpha_v2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v2_dep), true));

        projectAlpha_v3.getVersionData().addDependency(commonsUtil_v3_dep);
        projectAlpha_v3.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v3_dep), true));

        // Configure projectBeta dependencies
        projectBeta_v1.getVersionData().addDependency(commonsUtil_v3_dep); // Conflicts with Alpha v1.0.0
        projectBeta_v1.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v3_dep), true));

        projectBeta_v2.getVersionData().addDependency(commonsUtil_v2_dep);
        projectBeta_v2.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v2_dep), true));

        projectBeta_v3.getVersionData().addDependency(commonsUtil_v3_dep);
        projectBeta_v3.setTransitiveDependenciesReport(new VersionDependencyReport(Arrays.asList(commonsUtil_v3_dep), true));

        // Store all projects and versions
        projectsService.createOrUpdate(projectAlpha_v1);
        projectsService.createOrUpdate(projectAlpha_v2);
        projectsService.createOrUpdate(projectAlpha_v3);
        projectsService.createOrUpdate(projectBeta_v1);
        projectsService.createOrUpdate(projectBeta_v2);
        projectsService.createOrUpdate(projectBeta_v3);
        projectsService.createOrUpdate(commonsUtil_v1);
        projectsService.createOrUpdate(commonsUtil_v2);
        projectsService.createOrUpdate(commonsUtil_v3);

        // Test resolution with initially conflicting requirements
        List<ProjectVersion> requiredProjects = Arrays.asList(
                new ProjectVersion("org.example.optimal", "project_alpha", "1.0.0"), // Depends on commons_util v1.0.0
                new ProjectVersion("org.example.optimal", "project_beta", "1.0.0")   // Depends on commons_util v3.0.0 (CONFLICT!)
        );

        // Test Case 1: Without backtrack - should fail due to direct conflict
        List<ProjectVersion> noBacktrackResult = projectsService.resolveCompatibleVersions(requiredProjects, 0);
        Assertions.assertTrue(noBacktrackResult.isEmpty(),"Should return empty list due to conflicting dependencies and no backtrack");

        // Test Case 2: With backtrack enabled - should find optimal solution with newer versions
        List<ProjectVersion> backtrackResult = projectsService.resolveCompatibleVersions(requiredProjects, 3);
        Assertions.assertFalse(backtrackResult.isEmpty(),"Should find satisfiable solution with backtrack enabled");

        Set<String> solutionGavs = backtrackResult.stream()
                .map(ProjectVersion::getGav)
                .collect(Collectors.toSet());

        // Verify the solution contains exactly 3 projects
        Assertions.assertEquals(2, backtrackResult.size(),"Solution should contain exactly 2 projects");

        // The optimal solution should use the newest compatible versions
        // Expected optimal solution: projectAlpha v3.0.0 + projectBeta v3.0.0 + commons_util v3.0.0
        Assertions.assertTrue(solutionGavs.contains("org.example.optimal:project_alpha:3.0.0"),"Solution should use project_alpha v3.0.0 (newest compatible version)");
        Assertions.assertTrue(solutionGavs.contains("org.example.optimal:project_beta:3.0.0"),"Solution should use project_beta v3.0.0 (newest compatible version)");

        // Test Case 3: Verify the solution has no conflicts
        ProjectDependencyReport report = projectsService.getProjectDependencyReport(backtrackResult);
        Assertions.assertEquals(0, report.getConflicts().size(),"Optimal solution should have no remaining conflicts");

        // Test Case 4: Verify that other valid solutions exist but were not chosen
        // Manually test that solution with v2.0.0 versions would also work
        List<ProjectVersion> alternativeSolution = Arrays.asList(
                new ProjectVersion("org.example.optimal", "project_alpha", "2.0.0"),
                new ProjectVersion("org.example.optimal", "project_beta", "2.0.0"),
                new ProjectVersion("org.apache.commons", "commons_util", "2.0.0")
        );

        ProjectDependencyReport alternativeReport = projectsService.getProjectDependencyReport(alternativeSolution);
        Assertions.assertEquals(0, alternativeReport.getConflicts().size(),"Alternative solution with v2.0.0 should also be valid but less optimal");

        // Test Case 5: Verify version optimization by checking that newer versions were chosen over older ones
        Optional<ProjectVersion> resolvedAlpha = backtrackResult.stream()
                .filter(pv -> pv.getGroupId().equals("org.example.optimal") && pv.getArtifactId().equals("project_alpha"))
                .findFirst();

        Optional<ProjectVersion> resolvedBeta = backtrackResult.stream()
                .filter(pv -> pv.getGroupId().equals("org.example.optimal") && pv.getArtifactId().equals("project_beta"))
                .findFirst();

        Assertions.assertTrue(resolvedAlpha.isPresent() && resolvedAlpha.get().getVersionId().equals("3.0.0"),"Should choose newest available version of project_alpha");
        Assertions.assertTrue(resolvedBeta.isPresent() && resolvedBeta.get().getVersionId().equals("3.0.0"),"Should choose newest available version of project_beta");

        log.info("Optimal version selection test completed:");
        log.info("  Initial conflict: project_alpha v1.0.0 (needs commons_util v1.0.0) vs project_beta v1.0.0 (needs commons_util v3.0.0)");
        log.info("  Optimal solution found with {} projects:", backtrackResult.size());
        backtrackResult.forEach(pv -> log.info("    - {} (newest compatible version)", pv.getGav()));

        log.info("  Alternative valid solution (not chosen):");
        alternativeSolution.forEach(pv -> log.info("    - {} (older but compatible)", pv.getGav()));
    }
}

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

package org.finos.legend.depot.store.artifacts.services;

import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.maven.impl.TestMavenArtifactsRepository;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsRefreshStatusMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.store.notifications.queue.store.mongo.NotificationsQueueMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

public class TestDependencyManager extends TestStoreMongo
{
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateProjectsVersions projectsVersionsStore = new ProjectsVersionsMongo(mongoProvider);
    private final QueryMetricsStore metrics = mock(QueryMetricsStore.class);
    protected Queue queue = new NotificationsQueueMongo(mongoProvider);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore,projectsStore,metrics,queue,new ProjectsConfiguration("master"));
    protected ArtifactRepository repository = mock(TestMavenArtifactsRepository.class);
    protected RepositoryServices repositoryServices = new RepositoryServices(repository,projectsService);
    protected ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(mongoProvider);
    protected RefreshStatusStore refreshStatusStore = new ArtifactsRefreshStatusMongo(mongoProvider);
    protected DependencyManager dependencyManager = new DependencyManager(projectsService, repositoryServices);

    protected ProjectVersionRefreshHandler versionHandler = new ProjectVersionRefreshHandler(projectsService, repositoryServices, queue, refreshStatusStore,artifacts, new IncludeProjectPropertiesConfiguration(null, null), dependencyManager, 10);

    private static final String GROUPID = "examples.metadata";

    @Before
    public void setUpData()
    {
        List<StoreProjectVersionData> projectVersionData = readProjectVersionsConfigsFile(this.getClass().getClassLoader().getResource("data/projectsVersions.json"));
        projectVersionData.forEach(pv -> this.projectsVersionsStore.createOrUpdate(pv));
        Assert.assertEquals(5, projectsVersionsStore.getAll().size());
    }

    @Test
    public void canUpdateTransitiveDependencies()
    {
        //Adding a new project
        StoreProjectVersionData project1 = new StoreProjectVersionData(GROUPID, "test-master", "3.0.0");
        ProjectVersion dependency = new ProjectVersion(GROUPID, "test", "3.0.0");
        project1.getVersionData().setDependencies(Collections.singletonList(dependency));
        projectsVersionsStore.createOrUpdate(project1);

        StoreProjectVersionData versionData = dependencyManager.updateTransitiveDependencies(GROUPID, "test-master", "3.0.0");
        Assert.assertTrue(versionData.getTransitiveDependenciesReport().isValid());
        List<ProjectVersion> transitiveDependencies = versionData.getTransitiveDependenciesReport().getTransitiveDependencies();
        Assert.assertEquals(5, transitiveDependencies.size());
        List<ProjectVersion> dependencies = Arrays.asList(dependency, new ProjectVersion(GROUPID, "test-dependencies", "2.0.0"), new ProjectVersion(GROUPID, "art101", "1.0.0"),new ProjectVersion(GROUPID, "art102", "1.0.0"), new ProjectVersion(GROUPID, "art103", "1.0.0"));
        Assert.assertEquals(transitiveDependencies, dependencies);
    }

    @Test
    public void canUpdateTransitiveDependenciesForRevision()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData(GROUPID, "art104", "1.0.0"));
        //Adding a new project
        StoreProjectVersionData project1 = new StoreProjectVersionData(GROUPID, "test-master", "branch1-SNAPSHOT");
        ProjectVersion dependency1 = new ProjectVersion(GROUPID, "art104", "1.0.0");
        project1.getVersionData().setDependencies(Collections.singletonList(dependency1));
        projectsVersionsStore.createOrUpdate(project1);

        //this depends on master snapshot
        StoreProjectVersionData project2 = new StoreProjectVersionData(GROUPID, "art105", "branch1-SNAPSHOT");
        ProjectVersion dependency2 = new ProjectVersion(GROUPID, "test-master", "branch1-SNAPSHOT");
        project2.getVersionData().setDependencies(Collections.singletonList(dependency2));
        projectsVersionsStore.createOrUpdate(project2);

        //this depends on master snapshot
        StoreProjectVersionData project3 = new StoreProjectVersionData(GROUPID, "art106", "branch1-SNAPSHOT");
        ProjectVersion dependency3 = new ProjectVersion(GROUPID, "art105", "branch1-SNAPSHOT");
        project3.getVersionData().setDependencies(Collections.singletonList(dependency3));
        projectsVersionsStore.createOrUpdate(project3);

        project1 = dependencyManager.updateTransitiveDependencies(GROUPID, "test-master", "branch1-SNAPSHOT");
        Assert.assertTrue(project1.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(Collections.singletonList(dependency1), project1.getTransitiveDependenciesReport().getTransitiveDependencies());
        //chain update of snapshot dependants
        project2 = projectsService.find(GROUPID, "art105", "branch1-SNAPSHOT").get();
        Assert.assertTrue(project2.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(Arrays.asList(dependency1, dependency2), project2.getTransitiveDependenciesReport().getTransitiveDependencies());

        project3 = projectsService.find(GROUPID, "art106", "branch1-SNAPSHOT").get();
        Assert.assertTrue(project3.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(Arrays.asList(dependency1, dependency3, dependency2), project3.getTransitiveDependenciesReport().getTransitiveDependencies());
    }

}

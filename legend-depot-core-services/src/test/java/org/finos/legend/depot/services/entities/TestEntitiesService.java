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

package org.finos.legend.depot.services.entities;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.depot.domain.entity.EntityDefinition;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestEntitiesService extends TestBaseServices
{
    private final QueryMetricsStore metrics = mock(QueryMetricsStore.class);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, new ProjectsServiceImpl(projectsVersionsStore, projectsStore, metrics));

    @Before
    public void setUpData()
    {
        super.setUpData();

        StoreProjectVersionData project1 = projectsVersionsStore.find("examples.metadata", "test-dependencies", "1.0.0").get();
        //"PROD-A" -> "PROD-B" -> "PROD-C"
        ProjectVersion pv = new ProjectVersion("example.services.test", "test", "2.0.1");
        project1.getVersionData().addDependency(pv);
        projectsVersionsStore.createOrUpdate(project1);
        StoreProjectVersionData project2 = projectsVersionsStore.find("examples.metadata","test", "2.3.1").get();
        project2.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.singletonList(pv), true));
        projectsVersionsStore.createOrUpdate(project2);
        loadEntities("PROD-A", "2.3.1");
        loadEntities("PROD-B", "1.0.0");
        loadEntities("PROD-C", "2.0.1");
    }

    @Test
    public void canGetDependencies()
    {

        List<Entity> entityList = entitiesService.getEntities("examples.metadata", "test", "2.3.1", false);
        Assert.assertFalse(entityList.isEmpty());
        Assert.assertEquals(7, entityList.size());


        List<Entity> entityList2 = entitiesService.getEntities("examples.metadata", "test-dependencies", "1.0.0", false);
        Assert.assertFalse(entityList2.isEmpty());
        Assert.assertEquals(1, entityList2.size());

        List<Entity> entityList3 = entitiesService.getEntities("example.services.test", "test", "2.0.1", false);
        Assert.assertFalse(entityList3.isEmpty());
        Assert.assertEquals(18, entityList3.size());

        List<ProjectVersionEntities> dependencyList = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", false, false, false);
        Assert.assertFalse(dependencyList.isEmpty());
        Assert.assertEquals(1, dependencyList.size());
        Assert.assertFalse(dependencyList.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().isPresent());
        Assert.assertEquals(1, dependencyList.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assert.assertFalse(dependencyList.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().isPresent());

        List<ProjectVersionEntities> dependencyList2 = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", false, true, false);
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(2, dependencyList2.size());
        Assert.assertFalse(dependencyList2.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().isPresent());
        Assert.assertEquals(1, dependencyList2.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList2.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());

        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", false, true, true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(3, dependencyList3.size());
        Assert.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().get().getEntities().size());
        Assert.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());
    }

    private Predicate<ProjectVersionEntities> projectToArtifactFilter(String groupId,String artifactId)
    {
        List<StoreProjectVersionData> p = projectsVersionsStore.find(groupId, artifactId);
        Assert.assertTrue(!p.isEmpty());
        return dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId);
    }

    @Test
    public void canGetDependenciesMap()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "2.3.1"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions, false, true, true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(3, dependencyList3.size());
        Assert.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().get().getEntities().size());
        Assert.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());

    }

    @Test
    public void canGetOrphanedEntities()
    {
        entitiesService.createOrUpdate(Arrays.asList(
                new StoredEntity("example.one", "orphaned", "1.0.0", false,new EntityDefinition("path::entity", "la", null)),
                new StoredEntity("example.two", "orphaned", "1.0.1", false,new EntityDefinition("path::lala", "la", null))
        ));

        List<Pair<String, String>> orphaned = entitiesService.getOrphanedStoredEntities();
        Assert.assertEquals(2, orphaned.size());
        Assert.assertTrue(orphaned.contains(Tuples.pair("example.one", "orphaned")));
        Assert.assertTrue(orphaned.contains(Tuples.pair("example.two", "orphaned")));
    }

    @Test
    public void canQueryEntitiesWithVersionInPackage()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata","test1","1.0.0"));
        loadEntities("PROD-D", "1.0.0");

        String pkgName = "examples::metadata::test::dependency::v1_2_3";
        Assert.assertEquals(4, entitiesService.getStoredEntities("examples.metadata","test1","1.0.0").size());
        entitiesService.getStoredEntities("examples.metadata","test1","1.0.0").stream().allMatch(e -> e.getEntity().getPath().startsWith(pkgName));;
        entitiesService.getStoredEntities("examples.metadata","test1","1.0.0").stream().allMatch(e -> ((String)e.getEntity().getContent().get("package")).startsWith(pkgName));;

        Assert.assertEquals(2, entitiesService.getEntities("examples.metadata","test1","1.0.0",true).size());
        Assert.assertEquals(2, entitiesService.getEntities("examples.metadata","test1","1.0.0",false).size());
        Assert.assertEquals(2, entitiesService.getEntitiesByPackage("examples.metadata","test1","1.0.0",pkgName,false, Collections.EMPTY_SET,true).size());
        Assert.assertEquals(0, entitiesService.getEntitiesByPackage("examples.metadata","test1","1.0.0",pkgName,true, Collections.EMPTY_SET,true).size());

    }

    @Test
    public void canQueryEntitiesWithLatestVersionAlias()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata","test1","1.0.0"));
        loadEntities("PROD-D", "1.0.0");

        String pkgName = "examples::metadata::test::dependency::v1_2_3";

        Assert.assertEquals(2, entitiesService.getEntities("examples.metadata","test1","latest",true).size());
        Assert.assertEquals(2, entitiesService.getEntities("examples.metadata","test1","latest",false).size());
        Assert.assertEquals(2, entitiesService.getEntitiesByPackage("examples.metadata","test1","latest",pkgName,false, Collections.EMPTY_SET,true).size());
        Assert.assertEquals(0, entitiesService.getEntitiesByPackage("examples.metadata","test1","latest",pkgName,true, Collections.EMPTY_SET,true).size());

    }

    @Test
    public void canGetDependenciesMapWithLatestAlias()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "latest"), new ProjectVersion("examples.metadata", "test-dependencies", "latest"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions, false, true, true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(4, dependencyList3.size());
        Assert.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().get().getEntities().size());
        Assert.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());
    }

    @Test
    public void canGetDependenciesMapWithHeadAlias()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "head"), new ProjectVersion("examples.metadata", "test-dependencies", "latest"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions, false, true, true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(4, dependencyList3.size());
        Assert.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());
    }

    @Test
    public void canQueryEntitiesWithHeadVersionAlias()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata","test", MASTER_SNAPSHOT));
        loadEntities("PROD-A", MASTER_SNAPSHOT);

        String pkgName = "examples::metadata::test::v2_3_1::examples::metadata::test";

        Assert.assertEquals(7, entitiesService.getEntities("examples.metadata","test","head",true).size());
        Assert.assertEquals(7, entitiesService.getEntities("examples.metadata","test","head",false).size());
        Assert.assertEquals(0, entitiesService.getEntitiesByPackage("examples.metadata","test","head",pkgName,false, Collections.EMPTY_SET,true).size());
        Assert.assertEquals(4, entitiesService.getEntitiesByPackage("examples.metadata","test","head",pkgName,true, Collections.EMPTY_SET,true).size());

    }
}

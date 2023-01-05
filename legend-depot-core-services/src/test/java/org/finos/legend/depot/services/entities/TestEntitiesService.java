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
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TestEntitiesService extends TestBaseServices
{

    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, new ProjectsServiceImpl(projectsStore));

    @Before
    public void setUpData()
    {
        super.setUpData();

        ProjectData project1 = projectsStore.findByProjectId("PROD-B").get(0);
        //"PROD-A" -> "PROD-B" -> "PROD-C"
        project1.addDependency(new ProjectVersionDependency("examples.metadata", "test-dependencies", "1.0.0", new ProjectVersion("example.services.test", "test", "2.0.1")));
        projectsStore.createOrUpdate(project1);
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
        Assert.assertFalse(dependencyList.stream().filter(projectToArtifactFilter("PROD-A")).findFirst().isPresent());
        Assert.assertEquals(1, dependencyList.stream().filter(projectToArtifactFilter("PROD-B")).findFirst().get().getEntities().size());
        Assert.assertFalse(dependencyList.stream().filter(projectToArtifactFilter("PROD-C")).findFirst().isPresent());

        List<ProjectVersionEntities> dependencyList2 = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", false, true, false);
        Assert.assertFalse(dependencyList2.isEmpty());
        Assert.assertEquals(2, dependencyList2.size());
        Assert.assertFalse(dependencyList2.stream().filter(projectToArtifactFilter("PROD-A")).findFirst().isPresent());
        Assert.assertEquals(1, dependencyList2.stream().filter(projectToArtifactFilter("PROD-B")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList2.stream().filter(projectToArtifactFilter("PROD-C")).findFirst().get().getEntities().size());

        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", false, true, true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(3, dependencyList3.size());
        Assert.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("PROD-A")).findFirst().get().getEntities().size());
        Assert.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("PROD-B")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("PROD-C")).findFirst().get().getEntities().size());
    }

    private Predicate<ProjectVersionEntities> projectToArtifactFilter(String projectId)
    {
        List<ProjectData> p = projectsStore.findByProjectId(projectId);
        Assert.assertEquals(1, p.size());
        return dep -> dep.getGroupId().equals(p.get(0).getGroupId()) && dep.getArtifactId().equals(p.get(0).getArtifactId());
    }

    @Test
    public void canGetDependenciesMap()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "2.3.1"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions, false, true, true);
        Assert.assertFalse(dependencyList3.isEmpty());
        Assert.assertEquals(3, dependencyList3.size());
        Assert.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("PROD-A")).findFirst().get().getEntities().size());
        Assert.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("PROD-B")).findFirst().get().getEntities().size());
        Assert.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("PROD-C")).findFirst().get().getEntities().size());

    }

    @Test
    public void canGetOrphanedEntities()
    {
        entitiesService.createOrUpdate(Arrays.asList(
                new StoredEntity("example.one", "orphaned", "1.0.0", false,new EntityDefinition("la", "la", null)),
                new StoredEntity("example.two", "orphaned", "1.0.1", false,new EntityDefinition("lala", "la", null))
        ));

        List<Pair<String, String>> orphaned = entitiesService.getOrphanedStoredEntities();
        Assert.assertEquals(2, orphaned.size());
        Assert.assertTrue(orphaned.contains(Tuples.pair("example.one", "orphaned")));
        Assert.assertTrue(orphaned.contains(Tuples.pair("example.two", "orphaned")));
    }

    @Test
    public void canQueryEntitiesWithVersionInPackage()
    {
        projectsStore.createOrUpdate(new ProjectData("PROD-D","examples.metadata","test1").withVersions("1.0.0"));
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
}

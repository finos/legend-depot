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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

public class TestDependencyCache extends TestBaseServices
{

    private static final String TEST_GROUP = "a.group";

    @Before
    public void setUpData()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void canInitialiseCacheEmptyStore()
    {
        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
        Assert.assertTrue(dependenciesCache.transitiveDependencies.isEmpty());
    }

    private  void seedTestData()
    {
        ProjectData projectA = new ProjectData("a", TEST_GROUP,"artifacta").withVersions("1.0.0","2.0.0");
        ProjectData projectB = new ProjectData("b", TEST_GROUP,"artifactb").withVersions("1.0.0");
        ProjectData projectC = new ProjectData("c", TEST_GROUP,"artifactc").withVersions("1.0.0");
        projectA.addDependency(new ProjectVersionDependency(TEST_GROUP, "artifacta", "2.0.0", new ProjectVersion(TEST_GROUP, "artifactb", "1.0.0")));
        projectB.addDependency(new ProjectVersionDependency(TEST_GROUP, "artifactb", "1.0.0", new ProjectVersion(TEST_GROUP, "artifactc", "1.0.0")));

        projectsStore.createOrUpdate(projectA);
        projectsStore.createOrUpdate(projectB);
        projectsStore.createOrUpdate(projectC);
    }

    @Test
    public void canInitialiseCache()
    {
        seedTestData();
        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
        Assert.assertFalse(dependenciesCache.transitiveDependencies.isEmpty());
        Assert.assertEquals(4, dependenciesCache.transitiveDependencies.size());
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(new ProjectVersion(TEST_GROUP,"artifactc","1.0.0"))));
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(new ProjectVersion(TEST_GROUP,"artifacta","1.0.0"))));
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(new ProjectVersion(TEST_GROUP,"artifacta","2.0.0"))));
        Assert.assertEquals(2,dependenciesCache.transitiveDependencies.get(new ProjectVersion(TEST_GROUP,"artifacta","2.0.0")).size());
        Assert.assertEquals(1,dependenciesCache.transitiveDependencies.get(new ProjectVersion(TEST_GROUP,"artifactb","1.0.0")).size());
        Assert.assertTrue(dependenciesCache.transitiveDependencies.get(new ProjectVersion(TEST_GROUP,"artifactc","1.0.0")).isEmpty());
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(new ProjectVersion(TEST_GROUP,"artifactb","1.0.0"))));
    }

    @Test
    public void getDependenciesForNewProjectVersion()
    {
        seedTestData();
        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
        ProjectData projectD = new ProjectData("d", TEST_GROUP,"artifactd").withVersions("1.0.0");
        projectsStore.createOrUpdate(projectD);
        dependenciesCache.getTransitiveDependencies(new ProjectVersion(TEST_GROUP,"artifactd","1.0.0"));

        Assert.assertFalse(dependenciesCache.transitiveDependencies.isEmpty());
        Assert.assertEquals(5, dependenciesCache.transitiveDependencies.size());
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(new ProjectVersion(TEST_GROUP,"artifactd","1.0.0"))));
    }

    @Test
    public void getDependenciesForUnknownProject()
    {
        seedTestData();
        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
        Assert.assertEquals(4, dependenciesCache.transitiveDependencies.size());
        ProjectVersion unknownProject = new ProjectVersion(TEST_GROUP, "artifactd", "1.0.0");
        dependenciesCache.getTransitiveDependencies(unknownProject);
        //we might end up with dirty entries for wrong projects or versions in the cache but we need speed
        //we must absolutely return empty dependencies
        Assert.assertFalse(dependenciesCache.transitiveDependencies.isEmpty());
        Assert.assertEquals(5, dependenciesCache.transitiveDependencies.size());
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(unknownProject)));
        Assert.assertTrue(dependenciesCache.transitiveDependencies.get(unknownProject).isEmpty());
    }

    @Test
    public void getDependenciesForUnknownVersion()
    {
        seedTestData();
        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
        Assert.assertEquals(4, dependenciesCache.transitiveDependencies.size());
        ProjectVersion unknownVersion = new ProjectVersion(TEST_GROUP, "artifacta", "10.0.0");
        dependenciesCache.getTransitiveDependencies(unknownVersion);

        Assert.assertFalse(dependenciesCache.transitiveDependencies.isEmpty());
        Assert.assertEquals(5, dependenciesCache.transitiveDependencies.size());
        Assert.assertTrue(dependenciesCache.transitiveDependencies.keySet().stream().anyMatch(pv -> pv.equals(unknownVersion)));
        Assert.assertTrue(dependenciesCache.transitiveDependencies.get(unknownVersion).isEmpty());
    }

    @Test
    public void getDependenciesForNewProjectVersionWithDependencies()
    {
        seedTestData();
        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
        Assert.assertEquals(4, dependenciesCache.transitiveDependencies.size());
        ProjectData projectD = new ProjectData("d", TEST_GROUP,"artifactd").withVersions("1.0.0");
        ProjectVersion projectDVersion1 = new ProjectVersion(TEST_GROUP, "artifactd", "1.0.0");
        ProjectVersion newProjectDependency = new ProjectVersion(TEST_GROUP, "artifactc", "1.0.0");
        projectD.addDependency(new ProjectVersionDependency(TEST_GROUP, "artifactd", "1.0.0", newProjectDependency));
        projectsStore.createOrUpdate(projectD);

        dependenciesCache.getTransitiveDependencies(projectDVersion1);

        Assert.assertFalse(dependenciesCache.transitiveDependencies.isEmpty());
        Assert.assertEquals(5,dependenciesCache.transitiveDependencies.size());
        Assert.assertEquals(1,dependenciesCache.transitiveDependencies.get(projectDVersion1).size());
    }

    @Test
    public void getDependenciesForChangingMasterSnapshotDependencies()
    {
        ProjectVersion masterSNAPSHOTVersion = new ProjectVersion(TEST_GROUP, "artifacta", "master-SNAPSHOT");

        seedTestData();
        ProjectData projectA = projectsStore.find(TEST_GROUP,"artifacta").get();
        projectA.addDependency(new ProjectVersionDependency(TEST_GROUP, "artifacta", "master-SNAPSHOT", new ProjectVersion(TEST_GROUP, "artifactb", "1.0.0")));
        projectsStore.createOrUpdate(projectA);

        DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);

        Assert.assertEquals(4, dependenciesCache.transitiveDependencies.size());

        dependenciesCache.getTransitiveDependencies(masterSNAPSHOTVersion);

        Assert.assertEquals(5,dependenciesCache.transitiveDependencies.size());
        Assert.assertEquals(2,dependenciesCache.transitiveDependencies.get(masterSNAPSHOTVersion).size());


        ProjectData projectD = new ProjectData("d", TEST_GROUP,"artifactd").withVersions("1.0.0");
        projectsStore.createOrUpdate(projectD);
        ProjectData changedProjectA = projectsStore.find(TEST_GROUP,"artifacta").get();
        changedProjectA.addDependency(new ProjectVersionDependency(TEST_GROUP, "artifacta", "master-SNAPSHOT", new ProjectVersion(TEST_GROUP, "artifactd", "1.0.0")));
        projectsStore.createOrUpdate(changedProjectA);

        dependenciesCache.getTransitiveDependencies(masterSNAPSHOTVersion);

        Assert.assertEquals(5,dependenciesCache.transitiveDependencies.size());
        Assert.assertEquals(3,dependenciesCache.transitiveDependencies.get(masterSNAPSHOTVersion).size());

    }

    @Test
    public void errorInitialisingDupProjects()
    {
        setUpProjectsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/dupProjects.json"));
        Assert.assertEquals(5, projectsStore.getAll().size());
        Assert.assertEquals("PROD-CCC", projectsStore.find("example.services.Test", "Test").get().getProjectId());
        try
        {

            Assert.assertEquals("PROD-A", projectsStore.find("examples.metadata", "test").get().getProjectId());
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }
        try
        {
            DependenciesCache dependenciesCache = new DependenciesCache(projectsStore);
            Assert.fail();
            Assert.assertNotNull(dependenciesCache);
        }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }

    }


    protected void setUpProjectsFromFile(URL projectConfigFile)
    {
        readProjectConfigsFile(projectConfigFile).forEach(project ->
        {
            try
            {
                getMongoClient().getDatabase(mongoProvider.getName()).getCollection(ProjectsMongo.MONGO_PROJECTS).insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
            }
            catch (JsonProcessingException e)
            {
                Assert.fail("an error has occurred loading test project " + e.getMessage());
            }
        });
    }
}

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

package org.finos.legend.depot.store.mongo.projects;

import org.bson.Document;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class TestUpdateProjectApi extends TestStoreMongo
{

    public static final String TEST_GROUP_ID = "some.examples";
    private ProjectsMongo projectsAPI = new ProjectsMongo(mongoProvider);

    @Before
    public void setUpTestData()
    {
        setUpProjectsFromFile(this.getClass().getClassLoader().getResource("data/projects.json"));
    }


    @Test
    public void canCreateAnewProjectConfiguration()
    {
        ProjectData projectConfiguration = new ProjectData("PROD-121", "some.examples", "test121");

        projectsAPI.createOrUpdate(projectConfiguration);

        List<ProjectData> newConfig = projectsAPI.findByProjectId("PROD-121");
        Assert.assertNotNull(newConfig);
        Assert.assertEquals(1, newConfig.size());
        Assert.assertEquals("PROD-121", newConfig.get(0).getProjectId());
        Assert.assertEquals("some.examples", newConfig.get(0).getGroupId());
        Assert.assertEquals("test121", newConfig.get(0).getArtifactId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void cantCreateAnewProjectWithBadConfiguration()
    {
        ProjectData projectConfiguration = new ProjectData("PROD-121", "example.bad this is bad", "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
    }

    @Test
    public void canCreateIndexesIfAbsent()
    {
        List<Document> indexes = new ArrayList<>();
        this.mongoProvider.getCollection(ProjectsMongo.MONGO_PROJECTS).listIndexes().forEach((Consumer<Document>)indexes::add);
        Assert.assertFalse(indexes.isEmpty());
        Assert.assertEquals(1, indexes.size());
        Assert.assertEquals("_id_", indexes.get(0).getString("name"));

        boolean result = projectsAPI.createIndexesIfAbsent();
        Assert.assertTrue(result);
        List indexes1 = new ArrayList();
        this.mongoProvider.getCollection(ProjectsMongo.MONGO_PROJECTS).listIndexes().forEach((Consumer<Document>)indexes1::add);
        Assert.assertFalse(indexes1.isEmpty());
        Assert.assertEquals(2, indexes1.size());
    }

    @Test
    public void canCreateUpdateProjectsWithSameCoordinates()
    {
        Assert.assertEquals(3, projectsAPI.getAll().size());

        ProjectData projectConfiguration = new ProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        ProjectData projectConfiguration2 = new ProjectData("PROD-124", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
            Assert.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }

        ProjectData newData = projectsAPI.createOrUpdate(new ProjectData("PROD-124", TEST_GROUP_ID, "test122"));
        newData.setArtifactId("test121");

        try
        {
            projectsAPI.createOrUpdate(newData);
            Assert.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }
    }


    @Test
    public void canUpdateProject()
    {

        List<ProjectData> updatedProject = projectsAPI.findByProjectId("PROD-A");
        Assert.assertNotNull(updatedProject);
        Assert.assertEquals(1, updatedProject.size());
        Assert.assertNotNull(updatedProject.get(0).getVersions());
        Assert.assertEquals(2, updatedProject.get(0).getVersions().size());
        updatedProject.get(0).getVersions().add("1.1.1");
        projectsAPI.createOrUpdate(updatedProject.get(0));
        List<ProjectData> after = projectsAPI.findByProjectId("PROD-A");
        Assert.assertEquals(3, after.get(0).getVersions().size());
        after.get(0).getVersions().add("1.2.0");
        projectsAPI.createOrUpdate(after.get(0));
        Assert.assertEquals(4, projectsAPI.findByProjectId("PROD-A").get(0).getVersions().size());

        after.get(0).removeVersion("1.1.1");
        projectsAPI.createOrUpdate(after.get(0));
        Assert.assertEquals(3, projectsAPI.findByProjectId("PROD-A").get(0).getVersions().size());
    }



    @Test
    public void onlyInsertIfAbsent()
    {

        Assert.assertEquals(3, projectsAPI.getAll().size());

        ProjectData projectConfiguration = new ProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        ProjectData projectConfiguration2 = new ProjectData("PROD-123", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }
        List<ProjectData> newConfig = projectsAPI.getAll();
        Assert.assertNotNull(newConfig);
        Assert.assertEquals(4, newConfig.size());

    }

    @Test
    public void canInsertIfAbsent()
    {

        Assert.assertEquals(3, projectsAPI.getAll().size());
        ProjectData projectConfiguration = new ProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        ProjectData projectConfiguration2 = new ProjectData("PROD-21111", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
            Assert.fail("duplicate coordinates");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }

        ProjectData projectConfiguration3 = new ProjectData("PROD-21111", TEST_GROUP_ID, "test12111");

        ProjectData res1 = projectsAPI.createOrUpdate(projectConfiguration3);
        Assert.assertNotNull(res1);
        Assert.assertEquals(5, projectsAPI.getAll().size());
    }

}

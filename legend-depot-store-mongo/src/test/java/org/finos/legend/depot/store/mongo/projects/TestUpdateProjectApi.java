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

import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;


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
        StoreProjectData projectConfiguration = new StoreProjectData("PROD-121", TEST_GROUP_ID, "test121");

        projectsAPI.createOrUpdate(projectConfiguration);

        Optional<StoreProjectData> newConfig = projectsAPI.find("some.examples", "test121");
        Assert.assertNotNull(newConfig);
        Assert.assertTrue(newConfig.isPresent());
        Assert.assertEquals("PROD-121", newConfig.get().getProjectId());
        Assert.assertEquals("some.examples", newConfig.get().getGroupId());
        Assert.assertEquals("test121", newConfig.get().getArtifactId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void cantCreateAnewProjectWithBadConfiguration()
    {
        StoreProjectData projectConfiguration = new StoreProjectData("PROD-121", "example.bad this is bad", "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
    }


    @Test
    public void canCreateUpdateProjectsWithSameCoordinates()
    {
        Assert.assertEquals(3, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-124", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
            Assert.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }

        StoreProjectData newData = projectsAPI.createOrUpdate(new StoreProjectData("PROD-124", TEST_GROUP_ID, "test122"));
        Assert.assertEquals("test122", newData.getArtifactId());
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
    public void coordinatesAreCaseSensitive()
    {
        Assert.assertEquals(3, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", "some.examples", "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-124", "some.Examples", "test121");
        try
        {
          Assert.assertNotNull(projectsAPI.createOrUpdate(projectConfiguration2));
          Assert.assertEquals(5, projectsAPI.getAll().size());
        }
        catch (Exception e)
        {
            Assert.fail("not duplicate coordinates, different in case");
        }

        try
        {
            projectsAPI.createOrUpdate(new StoreProjectData("PROD-124", "some.examples", "test121"));
            Assert.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }
    }

    @Test
    public void onlyInsertIfAbsent()
    {

        Assert.assertEquals(3, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }
        List<StoreProjectData> newConfig = projectsAPI.getAll();
        Assert.assertNotNull(newConfig);
        Assert.assertEquals(4, newConfig.size());

    }

    @Test
    public void canInsertIfAbsent()
    {

        Assert.assertEquals(3, projectsAPI.getAll().size());
        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assert.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-21111", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
            Assert.fail("duplicate coordinates");
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }

        StoreProjectData projectConfiguration3 = new StoreProjectData("PROD-21111", TEST_GROUP_ID, "test12111");

        StoreProjectData res1 = projectsAPI.createOrUpdate(projectConfiguration3);
        Assert.assertNotNull(res1);
        Assert.assertEquals(5, projectsAPI.getAll().size());
    }

}

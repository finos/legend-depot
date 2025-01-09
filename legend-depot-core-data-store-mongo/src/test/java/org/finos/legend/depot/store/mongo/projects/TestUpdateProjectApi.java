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

import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.mongo.CoreDataMongoStoreTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;


public class TestUpdateProjectApi extends CoreDataMongoStoreTests
{

    public static final String TEST_GROUP_ID = "some.examples";
    private ProjectsMongo projectsAPI = new ProjectsMongo(mongoProvider);

    @BeforeEach
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
        Assertions.assertNotNull(newConfig);
        Assertions.assertTrue(newConfig.isPresent());
        Assertions.assertEquals("PROD-121", newConfig.get().getProjectId());
        Assertions.assertEquals("some.examples", newConfig.get().getGroupId());
        Assertions.assertEquals("test121", newConfig.get().getArtifactId());

    }

    @Test
    public void cantCreateAnewProjectWithBadConfiguration()
    {
        StoreProjectData projectConfiguration = new StoreProjectData("PROD-121", "example.bad this is bad", "test121");
        Assertions.assertThrows(IllegalArgumentException.class, () -> projectsAPI.createOrUpdate(projectConfiguration));
    }


    @Test
    public void canCreateUpdateProjectsWithSameCoordinates()
    {
        Assertions.assertEquals(3, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assertions.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-124", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
            Assertions.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assertions.assertTrue(e.getMessage().contains("Duplicate"));
        }

        StoreProjectData newData = projectsAPI.createOrUpdate(new StoreProjectData("PROD-124", TEST_GROUP_ID, "test122"));
        Assertions.assertEquals("test122", newData.getArtifactId());
        newData.setArtifactId("test121");

        try
        {
            projectsAPI.createOrUpdate(newData);
            Assertions.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assertions.assertTrue(e.getMessage().contains("Duplicate"));
        }
    }

    @Test
    public void coordinatesAreCaseSensitive()
    {
        Assertions.assertEquals(3, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", "some.examples", "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assertions.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-124", "some.Examples", "test121");
        try
        {
          Assertions.assertNotNull(projectsAPI.createOrUpdate(projectConfiguration2));
          Assertions.assertEquals(5, projectsAPI.getAll().size());
        }
        catch (Exception e)
        {
            Assertions.fail("not duplicate coordinates, different in case");
        }

        try
        {
            projectsAPI.createOrUpdate(new StoreProjectData("PROD-124", "some.examples", "test121"));
            Assertions.fail("cant create duplicate coordinates");
        }
        catch (Exception e)
        {
            Assertions.assertTrue(e.getMessage().contains("Duplicate"));
        }
    }

    @Test
    public void onlyInsertIfAbsent()
    {

        Assertions.assertEquals(3, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assertions.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
        }
        catch (Exception e)
        {
            Assertions.assertTrue(e.getMessage().contains("Duplicate"));
        }
        List<StoreProjectData> newConfig = projectsAPI.getAll();
        Assertions.assertNotNull(newConfig);
        Assertions.assertEquals(4, newConfig.size());

    }

    @Test
    public void canInsertIfAbsent()
    {

        Assertions.assertEquals(3, projectsAPI.getAll().size());
        StoreProjectData projectConfiguration = new StoreProjectData("PROD-123", TEST_GROUP_ID, "test121");
        projectsAPI.createOrUpdate(projectConfiguration);
        Assertions.assertEquals(4, projectsAPI.getAll().size());

        StoreProjectData projectConfiguration2 = new StoreProjectData("PROD-21111", TEST_GROUP_ID, "test121");
        try
        {
            projectsAPI.createOrUpdate(projectConfiguration2);
            Assertions.fail("duplicate coordinates");
        }
        catch (Exception e)
        {
            Assertions.assertTrue(e.getMessage().contains("Duplicate"));
        }

        StoreProjectData projectConfiguration3 = new StoreProjectData("PROD-21111", TEST_GROUP_ID, "test12111");

        StoreProjectData res1 = projectsAPI.createOrUpdate(projectConfiguration3);
        Assertions.assertNotNull(res1);
        Assertions.assertEquals(5, projectsAPI.getAll().size());
    }

}

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

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class TestUpdateProjectVersionApi extends TestStoreMongo
{
    private ProjectsVersionsMongo projectsVersionsAPI = new ProjectsVersionsMongo(mongoProvider);

    @Before
    public void setUpTestData()
    {
        setUpProjectsVersionsFromFile(this.getClass().getClassLoader().getResource("data/projectsVersions.json"));
    }


    @Test
    public void canCreateAnewProjectConfiguration()
    {
        StoreProjectVersionData projectConfiguration = new StoreProjectVersionData("some.examples", "test121","2.0.0");

        projectsVersionsAPI.createOrUpdate(projectConfiguration);

        Optional<StoreProjectVersionData> newConfig = projectsVersionsAPI.find("some.examples", "test121","2.0.0");
        Assert.assertNotNull(newConfig);
        Assert.assertTrue(newConfig.isPresent());
        Assert.assertEquals("some.examples", newConfig.get().getGroupId());
        Assert.assertEquals("test121", newConfig.get().getArtifactId());
        Assert.assertEquals("2.0.0", newConfig.get().getVersionId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void cantCreateAnewProjectWithBadConfiguration()
    {
        StoreProjectVersionData projectConfiguration = new StoreProjectVersionData("example.bad this is bad", "test121","4.0.0");
        projectsVersionsAPI.createOrUpdate(projectConfiguration);
    }

    @Test
    public void canUpdateProjectVersion()
    {

        List<StoreProjectVersionData> project = projectsVersionsAPI.find("examples.metadata","test");
        Assert.assertFalse(project.isEmpty());
        Assert.assertEquals(4, project.size());
        Optional<StoreProjectVersionData> updatedProject = projectsVersionsAPI.find("examples.metadata","test", "2.2.0");
        Assert.assertTrue(updatedProject.isPresent());
        Assert.assertEquals(updatedProject.get().getVersionData().getDependencies().size(),0);
        updatedProject.get().getVersionData().setDependencies(Collections.singletonList(new ProjectVersion("examples.metadata","test-dependencies","1.0.0")));
        projectsVersionsAPI.createOrUpdate(updatedProject.get());
        updatedProject = projectsVersionsAPI.find("examples.metadata","test", "2.2.0");
        Assert.assertTrue(updatedProject.isPresent());
        Assert.assertEquals(updatedProject.get().getVersionData().getDependencies().size(),1);
    }

}

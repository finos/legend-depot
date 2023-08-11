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

import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.mongo.CoreDataMongoStoreTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;


public class TestQueryProjectVersionApi extends CoreDataMongoStoreTests
{
    private ProjectsVersions projectsVersionsAPI = new ProjectsVersionsMongo(mongoProvider);

    @Before
    public void setUpProjectData()
    {
        setUpProjectsVersionsFromFile(this.getClass().getClassLoader().getResource("data/projectsVersions.json"));
    }

    @Test
    public void canCollectAllProjectConfig()
    {
        List<StoreProjectVersionData> allConfigs = projectsVersionsAPI.getAll();
        Assert.assertNotNull(allConfigs);
        Assert.assertEquals(6, allConfigs.size());
    }

    @Test
    public void testFindingByProjectVersionCoordinates()
    {
        Optional<StoreProjectVersionData> projectConfig = projectsVersionsAPI.find("examples.metadata", "test", "2.2.0");
        Assert.assertTrue(projectConfig.isPresent());
        projectConfig = projectsVersionsAPI.find("examples.metadata", "test", "1.0.0");
        Assert.assertFalse(projectConfig.isPresent());
    }

    @Test
    public void canFindProjectByMavenCoordinates()
    {
        List<StoreProjectVersionData> project = projectsVersionsAPI.find("examples.metadata", "test");
        Assert.assertFalse(project.isEmpty());
        Assert.assertEquals(4, project.size());
    }

    @Test
    public void cannotFindProject()
    {
        List<StoreProjectVersionData> project = projectsVersionsAPI.find("PROD-9691231123", "lalal");
        Assert.assertTrue(project.isEmpty());
    }

    @Test
    public void canGetProjectVersionIfExcluded()
    {
        List<StoreProjectVersionData> storeProjectVersionData = projectsVersionsAPI.findVersion(true);
        Assert.assertFalse(storeProjectVersionData.isEmpty());
        Assert.assertEquals(1, storeProjectVersionData.size());
        Assert.assertEquals("examples.metadata", storeProjectVersionData.get(0).getGroupId());
        Assert.assertEquals("test", storeProjectVersionData.get(0).getArtifactId());
        Assert.assertEquals("3.0.0", storeProjectVersionData.get(0).getVersionId());
    }

}

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

import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;


public class TestQueryProjectApi extends TestStoreMongo
{
    private Projects projectsAPI = new ProjectsMongo(mongoProvider);

    @Before
    public void setUpProjectData()
    {
        setUpProjectsFromFile(this.getClass().getClassLoader().getResource("data/projects.json"));
    }

    @Test
    public void canCollectAllProjectConfig()
    {
        List<ProjectData> allConfigs = projectsAPI.getAll();
        Assert.assertNotNull(allConfigs);
        Assert.assertEquals(3, allConfigs.size());
    }

    @Test
    public void canCollectProjectConfigByIdMetadataFromMongo()
    {
        ProjectData projectConfig = projectsAPI.findByProjectId("PROD-C").get(0);
        Assert.assertNotNull(projectConfig);
        Assert.assertEquals("PROD-C", projectConfig.getProjectId());
        Assert.assertEquals("example.services.test", projectConfig.getGroupId());
        Assert.assertEquals("test", projectConfig.getArtifactId());
    }

    @Test
    public void cannotFindConfig()
    {
        List<ProjectData> projectConfig = projectsAPI.findByProjectId("PROD-9691231123");
        Assert.assertTrue(projectConfig.isEmpty());

    }

    @Test
    public void canFindByMavenCoordinates()
    {
        Optional<ProjectData> projectConfig = projectsAPI.find("examples.metadata", "test");
        Assert.assertTrue(projectConfig.isPresent());

    }

    @Test
    public void cannotFindProject()
    {
        Optional<ProjectData> project = projectsAPI.find("PROD-9691231123", "lalal");
        Assert.assertFalse(project.isPresent());

    }

    @Test
    public void canGetVersionsForProject()
    {
        List<String> fullVersions = projectsAPI.find("examples.metadata", "test").get().getVersions();
        Assert.assertNotNull(fullVersions);
        Assert.assertEquals(2, fullVersions.size());

    }


}

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
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.mongo.CoreDataMongoStoreTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;


public class TestQueryProjectApi extends CoreDataMongoStoreTests
{
    private Projects projectsAPI = new ProjectsMongo(mongoProvider);

    @BeforeEach
    public void setUpProjectData()
    {
        setUpProjectsFromFile(this.getClass().getClassLoader().getResource("data/projects.json"));
    }

    @Test
    public void canCollectAllProjectConfig()
    {
        List<StoreProjectData> allConfigs = projectsAPI.getAll();
        Assertions.assertNotNull(allConfigs);
        Assertions.assertEquals(3, allConfigs.size());
    }

    @Test
    public void canFindByMavenCoordinates()
    {
        Optional<StoreProjectData> projectConfig = projectsAPI.find("examples.metadata", "test");
        Assertions.assertTrue(projectConfig.isPresent());

    }

    @Test
    public void canFindByProjectId()
    {
        List<StoreProjectData> projectConfig = projectsAPI.findByProjectId("PROD-A");
        Assertions.assertTrue(!projectConfig.isEmpty());
        Assertions.assertEquals(projectConfig.size(), 1);
        Assertions.assertEquals(projectConfig.get(0).getGroupId(), "examples.metadata");
        Assertions.assertEquals(projectConfig.get(0).getArtifactId(), "test");

    }

    @Test
    public void cannotFindProject()
    {
        Optional<StoreProjectData> project = projectsAPI.find("PROD-9691231123", "lalal");
        Assertions.assertFalse(project.isPresent());

    }

}

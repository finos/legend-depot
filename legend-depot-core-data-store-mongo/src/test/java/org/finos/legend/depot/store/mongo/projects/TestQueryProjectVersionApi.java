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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.DatesHandler.toTime;


public class TestQueryProjectVersionApi extends CoreDataMongoStoreTests
{
    private ProjectsVersions projectsVersionsAPI = new ProjectsVersionsMongo(mongoProvider);

    @BeforeEach
    public void setUpProjectData()
    {
        setUpProjectsVersionsFromFile(this.getClass().getClassLoader().getResource("data/projectsVersions.json"));
    }

    @Test
    public void canCollectAllProjectConfig()
    {
        List<StoreProjectVersionData> allConfigs = projectsVersionsAPI.getAll();
        Assertions.assertNotNull(allConfigs);
        Assertions.assertEquals(6, allConfigs.size());
    }

    @Test
    public void canCollectAllProjectConfigUpdatedFrom()
    {
        List<StoreProjectVersionData> allConfigs = projectsVersionsAPI.findByUpdatedDate(1687227600000L,
                toTime(LocalDateTime.now()));
        Assertions.assertNotNull(allConfigs);
        Assertions.assertEquals(3, allConfigs.size());
    }

    @Test
    public void canCollectAllProjectConfigUpdatedFromTo()
    {
        List<StoreProjectVersionData> allConfigs = projectsVersionsAPI.findByUpdatedDate(1687219200000L, 1687219210000L);
        Assertions.assertNotNull(allConfigs);
        Assertions.assertEquals(1, allConfigs.size());
    }

    @Test
    public void testFindingByProjectVersionCoordinates()
    {
        Optional<StoreProjectVersionData> projectConfig = projectsVersionsAPI.find("examples.metadata", "test", "2.2.0");
        Assertions.assertTrue(projectConfig.isPresent());
        projectConfig = projectsVersionsAPI.find("examples.metadata", "test", "1.0.0");
        Assertions.assertFalse(projectConfig.isPresent());
    }

    @Test
    public void canFindProjectByMavenCoordinates()
    {
        List<StoreProjectVersionData> project = projectsVersionsAPI.find("examples.metadata", "test");
        Assertions.assertFalse(project.isEmpty());
        Assertions.assertEquals(4, project.size());
    }

    @Test
    public void cannotFindProject()
    {
        List<StoreProjectVersionData> project = projectsVersionsAPI.find("PROD-9691231123", "lalal");
        Assertions.assertTrue(project.isEmpty());
    }

    @Test
    public void canGetProjectVersionIfExcluded()
    {
        List<StoreProjectVersionData> storeProjectVersionData = projectsVersionsAPI.findVersion(true);
        Assertions.assertFalse(storeProjectVersionData.isEmpty());
        Assertions.assertEquals(1, storeProjectVersionData.size());
        Assertions.assertEquals("examples.metadata", storeProjectVersionData.get(0).getGroupId());
        Assertions.assertEquals("test", storeProjectVersionData.get(0).getArtifactId());
        Assertions.assertEquals("3.0.0", storeProjectVersionData.get(0).getVersionId());
    }

}

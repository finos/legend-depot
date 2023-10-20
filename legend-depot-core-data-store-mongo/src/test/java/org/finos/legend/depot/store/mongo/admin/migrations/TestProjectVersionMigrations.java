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

package org.finos.legend.depot.store.mongo.admin.migrations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.CoreDataMongoStoreTests;
import org.finos.legend.depot.store.mongo.admin.CoreDataMigrations;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.buildDocument;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.convert;

public class TestProjectVersionMigrations extends CoreDataMongoStoreTests
{
    CoreDataMigrations mongoAdminStore = new CoreDataMigrations(mongoProvider);

    @Before
    public void setupTestData()
    {
        setUpProjectDataFromFile(this.getClass().getClassLoader().getResource("data/projectVersionMigration/projectsData.json"));
        Assert.assertEquals(3,mongoProvider.getCollection("project-configurations").countDocuments());
    }

    protected static List<Document> readProjectDataConfigsFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();
            List<Document> projects = new ObjectMapper().readValue(jsonInput, new TypeReference<List<Document>>() {});
            Assert.assertNotNull("testing file" + fileName.getFile(), projects);
            return projects;
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
        return null;
    }

    private void setUpProjectDataFromFile(URL projectConfigFile)
    {
        try
        {
            Assert.assertNotNull(getMongoProjects());
            readProjectDataConfigsFile(projectConfigFile).forEach(project ->
            {
                try
                {
                    getMongoProjects().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test project " + e.getMessage());
                }
            });
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    @Test
    public void testProjectToProjectVersionMigration()
    {

        Assert.assertEquals(0,mongoProvider.getCollection("versions").countDocuments());
        mongoAdminStore.migrationToProjectVersions();
        Assert.assertEquals(7,mongoProvider.getCollection("versions").countDocuments());
        StoreProjectVersionData result = convert(new ObjectMapper(),mongoProvider.getCollection("versions").find().first(), StoreProjectVersionData.class);
        Assert.assertEquals(result.getGroupId(), "examples.metadata");
        Assert.assertEquals(result.getArtifactId(), "test");
        Assert.assertEquals(result.getVersionId(), BRANCH_SNAPSHOT("master"));
    }

    @Test
    public void testProjectDataCleanUp()
    {

        mongoAdminStore.cleanUpProjectData();
        Assert.assertEquals(3,mongoProvider.getCollection("project-configurations").countDocuments());
        StoreProjectData result = convert(new ObjectMapper(),mongoProvider.getCollection("project-configurations").find().first(), StoreProjectData.class);
        Assert.assertEquals(1,1);
        Assert.assertEquals(result.getProjectId(), "PROD-A");
        Assert.assertEquals(result.getGroupId(), "examples.metadata");
        Assert.assertEquals(result.getArtifactId(), "test");
    }

    @Test
    public void testProjectUpdatesWithLatestVersion()
    {
        Assert.assertEquals(0,mongoProvider.getCollection("versions").countDocuments());
        mongoAdminStore.migrationToProjectVersions();
        Assert.assertEquals(7,mongoProvider.getCollection("versions").countDocuments());
        mongoAdminStore.cleanUpProjectData();
        Assert.assertEquals(3,mongoProvider.getCollection("project-configurations").countDocuments());
        mongoAdminStore.addLatestVersionToProjectData();
        Assert.assertEquals(3,mongoProvider.getCollection("project-configurations").countDocuments());
        List<StoreProjectData> result = new ArrayList<>();
        mongoProvider.getCollection("project-configurations").find().forEach((Consumer<Document>) doc -> result.add(convert(new ObjectMapper(), doc, StoreProjectData.class)));
        Assert.assertEquals(result.get(0).getLatestVersion(), "2.3.1");
        Assert.assertEquals(result.get(1).getLatestVersion(), "1.0.0");
        Assert.assertEquals(result.get(2).getLatestVersion(), "2.0.1");
    }

    @Test
    public void testProjectUpdatesForLatestWithNoVersion()
    {
        mongoProvider.getCollection("project-configurations").drop();
        mongoProvider.getCollection("project-configurations").insertOne(buildDocument(new StoreProjectData("PROD-A", "examples.project", "metadata")));
        mongoProvider.getCollection("versions").insertOne(BaseMongo.buildDocument(new StoreProjectVersionData("examples.project", "metadata", "master-snapshot")));

        mongoAdminStore.addLatestVersionToProjectData();
        StoreProjectData result = convert(new ObjectMapper(),mongoProvider.getCollection("project-configurations").find().first(), StoreProjectData.class);

        Assert.assertNull(result.getLatestVersion());
    }
}

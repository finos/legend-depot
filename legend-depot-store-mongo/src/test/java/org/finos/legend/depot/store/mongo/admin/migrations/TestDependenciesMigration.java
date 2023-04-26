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
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.convert;

public class TestDependenciesMigration extends TestStoreMongo
{
    MongoAdminStore mongoAdminStore = new MongoAdminStore(mongoProvider);
    private static final String VERSIONS_COLLECTION = "versionsTemp";

    @Before
    public void setupTestData()
    {
        setUpProjectDataFromFile(this.getClass().getClassLoader().getResource("data/projectsVersions1.json"));
        Assert.assertEquals(9, mongoProvider.getCollection(ProjectsVersionsMongo.COLLECTION).countDocuments());
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
            readProjectDataConfigsFile(projectConfigFile).forEach(project ->
            {
                try
                {
                    getMongoProjectVersions().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assert.fail("an error has occurred loading test project " + e.getMessage());
                }
            });
            Assert.assertNotNull(getMongoProjects());
        }
        catch (Exception e)
        {
            Assert.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    @Test
    public void testStoringTransitiveDependencies()
    {
        mongoAdminStore.calculateTransitiveDependenciesForAllProjectVersions();

        StoreProjectVersionData result = convert(new ObjectMapper(),mongoProvider.getCollection(VERSIONS_COLLECTION).find().first(), StoreProjectVersionData.class);
        Assert.assertTrue(result.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(4, result.getTransitiveDependenciesReport().getTransitiveDependencies().size());
        ProjectVersion pv1 = new ProjectVersion("examples.metadata", "test-dependencies", "2.0.0");
        ProjectVersion pv2 = new ProjectVersion("examples.metadata", "art101", "1.0.0");
        ProjectVersion pv3 = new ProjectVersion("examples.metadata", "art102", "1.0.0");
        ProjectVersion pv4 = new ProjectVersion("examples.metadata", "art103", "1.0.0");
        Assert.assertEquals(result.getTransitiveDependenciesReport().getTransitiveDependencies(), Arrays.asList(pv1, pv2, pv3, pv4));

        //check for the excluded Dependency
        StoreProjectVersionData result1 = convert(new ObjectMapper(), mongoProvider.getCollection(VERSIONS_COLLECTION).find(Filters.and(Filters.eq("groupId", "examples.metadata"), Filters.eq("artifactId", "art104"))).first(), StoreProjectVersionData.class);
        Assert.assertFalse(result1.getTransitiveDependenciesReport().isValid());

        //check for other dependencies
        StoreProjectVersionData result2 = convert(new ObjectMapper(), mongoProvider.getCollection(VERSIONS_COLLECTION).find(Filters.and(Filters.eq("groupId", "examples.metadata"), Filters.eq("artifactId", "test-dependencies"))).first(), StoreProjectVersionData.class);
        Assert.assertTrue(result2.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(result2.getTransitiveDependenciesReport().getTransitiveDependencies(), Arrays.asList(pv2, pv3, pv4));

        //check for dependency not present in store
        StoreProjectVersionData result3 = convert(new ObjectMapper(), mongoProvider.getCollection(VERSIONS_COLLECTION).find(Filters.and(Filters.eq("groupId", "examples.metadata"), Filters.eq("artifactId", "art108"))).first(), StoreProjectVersionData.class);
        Assert.assertFalse(result3.getTransitiveDependenciesReport().isValid());
    }

    @Test
    public void testUpdatingVersionsCollection()
    {
        mongoAdminStore.calculateTransitiveDependenciesForAllProjectVersions();
        mongoAdminStore.addTransitiveDependenciesToVersionData();
        StoreProjectVersionData result = convert(new ObjectMapper(),mongoProvider.getCollection(ProjectsVersionsMongo.COLLECTION).find().first(), StoreProjectVersionData.class);
        Assert.assertTrue(result.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(4, result.getTransitiveDependenciesReport().getTransitiveDependencies().size());
        ProjectVersion pv1 = new ProjectVersion("examples.metadata", "test-dependencies", "2.0.0");
        ProjectVersion pv2 = new ProjectVersion("examples.metadata", "art101", "1.0.0");
        ProjectVersion pv3 = new ProjectVersion("examples.metadata", "art102", "1.0.0");
        ProjectVersion pv4 = new ProjectVersion("examples.metadata", "art103", "1.0.0");
        Assert.assertEquals(result.getTransitiveDependenciesReport().getTransitiveDependencies(), Arrays.asList(pv1, pv2, pv3, pv4));

        //check for the excluded Dependency
        StoreProjectVersionData result1 = convert(new ObjectMapper(), mongoProvider.getCollection(ProjectsVersionsMongo.COLLECTION).find(Filters.and(Filters.eq("groupId", "examples.metadata"), Filters.eq("artifactId", "art104"))).first(), StoreProjectVersionData.class);
        Assert.assertFalse(result1.getTransitiveDependenciesReport().isValid());

        //check for other dependencies
        StoreProjectVersionData result2 = convert(new ObjectMapper(), mongoProvider.getCollection(ProjectsVersionsMongo.COLLECTION).find(Filters.and(Filters.eq("groupId", "examples.metadata"), Filters.eq("artifactId", "test-dependencies"))).first(), StoreProjectVersionData.class);
        Assert.assertTrue(result2.getTransitiveDependenciesReport().isValid());
        Assert.assertEquals(result2.getTransitiveDependenciesReport().getTransitiveDependencies(), Arrays.asList(pv2, pv3, pv4));

        //check for dependency not present in store
        StoreProjectVersionData result3 = convert(new ObjectMapper(), mongoProvider.getCollection(ProjectsVersionsMongo.COLLECTION).find(Filters.and(Filters.eq("groupId", "examples.metadata"), Filters.eq("artifactId", "art108"))).first(), StoreProjectVersionData.class);
        Assert.assertFalse(result3.getTransitiveDependenciesReport().isValid());
    }
}

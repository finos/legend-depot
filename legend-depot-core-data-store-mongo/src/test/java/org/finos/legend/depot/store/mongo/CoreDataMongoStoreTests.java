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

package org.finos.legend.depot.store.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.junit.jupiter.api.Assertions;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public abstract class CoreDataMongoStoreTests extends  TestStoreMongo
{

    public static List<StoreProjectData> readProjectConfigsFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<StoreProjectData> projects = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoreProjectData>>()
            {
            });
            Assertions.assertNotNull(projects, "testing file" + fileName.getFile());
            return projects;
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
        return null;
    }

    public static List<StoreProjectVersionData> readProjectVersionsConfigsFile(URL fileName)
    {
        try
        {
            InputStream stream = fileName.openStream();
            String jsonInput = new java.util.Scanner(stream).useDelimiter("\\A").next();

            List<StoreProjectVersionData> projects = new ObjectMapper().readValue(jsonInput, new TypeReference<List<StoreProjectVersionData>>()
            {
            });
            Assertions.assertNotNull(projects, "testing file" + fileName.getFile());
            return projects;
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
        return null;
    }

    protected MongoCollection getMongoProjects()
    {
        return getMongoDatabase().getCollection(ProjectsMongo.COLLECTION);
    }

    protected void setUpProjectsFromFile(URL projectConfigFile)
    {
        try
        {
            readProjectConfigsFile(projectConfigFile).forEach(project ->
            {
                try
                {
                    getMongoProjects().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assertions.fail("an error has occurred loading test project " + e.getMessage());
                }
            });
            Assertions.assertNotNull(getMongoProjects());
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    protected void setUpProjectsVersionsFromFile(URL projectConfigFile)
    {
        try
        {
            readProjectVersionsConfigsFile(projectConfigFile).forEach(project ->
            {
                try
                {
                    getMongoProjectVersions().insertOne(Document.parse(new ObjectMapper().writeValueAsString(project)));
                }
                catch (JsonProcessingException e)
                {
                    Assertions.fail("an error has occurred loading test project " + e.getMessage());
                }
            });
            Assertions.assertNotNull(getMongoProjectVersions());
        }
        catch (Exception e)
        {
            Assertions.fail("an error has occurred loading test project metadata" + e.getMessage());
        }
    }

    protected MongoCollection getMongoProjectVersions()
    {
        return getMongoDatabase().getCollection(ProjectsVersionsMongo.COLLECTION);
    }

}

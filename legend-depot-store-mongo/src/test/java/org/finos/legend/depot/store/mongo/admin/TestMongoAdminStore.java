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

package org.finos.legend.depot.store.mongo.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bson.Document;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TestMongoAdminStore extends TestStoreMongo
{
    MongoAdminStore mongoAdminStore = new MongoAdminStore(mongoProvider);


    @Test
    public void canCreateEntitiesIndexesIfAbsent()
    {
        List<Document> indexes = new ArrayList<>();
        this.mongoProvider.getCollection(EntitiesMongo.COLLECTION).listIndexes().forEach((Consumer<Document>) indexes::add);
        List<String> result = BaseMongo.createIndexesIfAbsent(mongoProvider, EntitiesMongo.COLLECTION, EntitiesMongo.buildIndexes());
        Assert.assertFalse(result.isEmpty());
        List indexes1 = new ArrayList();
        this.mongoProvider.getCollection(EntitiesMongo.COLLECTION).listIndexes().forEach((Consumer<Document>) indexes1::add);
        Assert.assertFalse(indexes1.isEmpty());
        Assert.assertEquals(5, indexes1.size());
    }


    @Test
    public void canCreateProjectsIndexesIfAbsent()
    {
        List<Document> indexes = new ArrayList<>();
        this.mongoProvider.getCollection(ProjectsMongo.COLLECTION).listIndexes().forEach((Consumer<Document>) indexes::add);
        Assert.assertTrue(indexes.isEmpty());

        List<String> result = BaseMongo.createIndexesIfAbsent(mongoProvider, ProjectsMongo.COLLECTION, ProjectsMongo.buildIndexes());
        Assert.assertFalse(result.isEmpty());

        List indexes1 = new ArrayList();
        this.mongoProvider.getCollection(ProjectsMongo.COLLECTION).listIndexes().forEach((Consumer<Document>) indexes1::add);
        Assert.assertFalse(indexes1.isEmpty());
        Assert.assertEquals(2, indexes1.size());
    }

    @Test
    public void canRunPipelineAsJson() throws JsonProcessingException
    {
        String pipeline =  "[\n" +
                "  {\n" +
                "    \"$limit\": 1000\n" +
                "  }, {\n" +
                "  \"$project\": {\n" +
                "    \"groupId\": \"$groupId\",\n" +
                "    \"artifactId\": \"$artifactId\",\n" +
                "    \"count\": {\n" +
                "      \"$sum\": 1\n" +
                "    }\n" +
                "  }\n" +
                "}, {\n" +
                "  \"$group\": {\n" +
                "    \"_id\": {\n" +
                "      \"group\": \"$groupId\",\n" +
                "      \"artifact\": \"$artifactId\"\n" +
                "    },\n" +
                "    \"total\": {\n" +
                "      \"$sum\": 1\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "]";

        setUpEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/versioned-entities.json"));
        List<Document> result = mongoAdminStore.runPipeline("entities", pipeline);
        Assert.assertNotNull(result);
        Assert.assertEquals(1,result.size());

    }

    @Test
    public void canRunPipeline() throws JsonProcessingException
    {
       List<Document> pipeline = Arrays.asList(new Document("$limit", 1000L),
               new Document("$project",
                       new Document("groupId", "$groupId")
                               .append("artifactId", "$artifactId")
                               .append("count",
                                       new Document("$sum", 1L))),
               new Document("$group",
                       new Document("_id",
                               new Document("group", "$groupId")
                                       .append("artifact", "$artifactId"))
                               .append("total",
                                       new Document("$sum", 1L))));
        setUpEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/versioned-entities.json"));
        Assert.assertEquals(3,mongoProvider.getCollection("entities").countDocuments());
        List<Document> result = mongoAdminStore.runPipeline("entities", pipeline);
        Assert.assertNotNull(result);
        Assert.assertEquals(1,result.size());

    }

}

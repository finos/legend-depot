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
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestMongoAdminStore extends TestStoreMongo
{
    MongoAdminStore mongoAdminStore = new MongoAdminStore(mongoProvider);


    @Before
    public void setUp()
    {
        new EntitiesMongoTestUtils(mongoProvider).loadEntities(this.getClass().getClassLoader().getResource("data/versioned-entities.json"));
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

        Assert.assertEquals(3,mongoProvider.getCollection("entities").countDocuments());
        List<Document> result = mongoAdminStore.runPipeline("entities", pipeline);
        Assert.assertNotNull(result);
        Assert.assertEquals(1,result.size());
    }

}

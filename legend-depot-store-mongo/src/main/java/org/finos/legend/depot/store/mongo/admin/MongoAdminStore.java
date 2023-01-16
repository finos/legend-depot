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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsFilesMongo;
import org.finos.legend.depot.store.mongo.admin.artifacts.ArtifactsRefreshStatusMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.finos.legend.depot.store.mongo.core.BaseMongo.createIndexesIfAbsent;

public class MongoAdminStore
{
    private final MongoDatabase mongoDatabase;

    @Inject
    public MongoAdminStore(@Named("mongoDatabase") MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }


    public void deleteCollection(String collectionId)
    {
        mongoDatabase.getCollection(collectionId).drop();
    }

    public List<String> getAllCollections()
    {
        List<String> collections = new ArrayList<>();
        mongoDatabase.listCollections().forEach((Consumer<? super Document>)col -> collections.add(col.getString("name")));
        return collections;
    }

    public List<Document> getAllIndexes()
    {
        List<Document> result = new ArrayList<>();
        getAllCollections().forEach(col ->
        {
            List<Document> indexes = new ArrayList<>();
            mongoDatabase.getCollection(col).listIndexes().forEach((Consumer<Document>)indexes::add);
            result.addAll(indexes);
        });
        return result;
    }

    public void deleteIndex(String collectionId, String indexName)
    {
        mongoDatabase.getCollection(collectionId).dropIndex(indexName);
    }


    public List<String> createIndexes()
    {
        List<String> results = new ArrayList<>();
        results.addAll(createIndexesIfAbsent(mongoDatabase,ProjectsMongo.COLLECTION,ProjectsMongo.buildIndexes()));
        results.addAll(createIndexesIfAbsent(mongoDatabase,EntitiesMongo.COLLECTION,EntitiesMongo.buildIndexes()));
        results.addAll(createIndexesIfAbsent(mongoDatabase,FileGenerationsMongo.COLLECTION,FileGenerationsMongo.buildIndexes()));
        results.addAll(createIndexesIfAbsent(mongoDatabase,ArtifactsFilesMongo.COLLECTION,ArtifactsFilesMongo.buildIndexes()));
        results.addAll(createIndexesIfAbsent(mongoDatabase,ArtifactsRefreshStatusMongo.COLLECTION,ArtifactsRefreshStatusMongo.buildIndexes()));
        results.addAll(createIndexesIfAbsent(mongoDatabase,ArtifactsRefreshStatusMongo.COLLECTION,ArtifactsRefreshStatusMongo.buildIndexes()));
        return results;
    }

    public Document runCommand(Document document)
    {
        return mongoDatabase.runCommand(document);
    }

    public List<Document> runPipeline(String collectionName, List<Document> pipeline)
    {
        List<Document> documents = new ArrayList<>();
        mongoDatabase.getCollection(collectionName).aggregate(pipeline).forEach((Consumer<? super Document>) doc ->
        {
            documents.add(doc);
        });
        return documents;
    }

    public List<Document> runPipeline(String collectionName, String jsonPipeline) throws JsonProcessingException
    {
        List<Document> pipeline =  new ObjectMapper().readValue(jsonPipeline, new TypeReference<List<Document>>(){});;
         return runPipeline(collectionName,pipeline);
    }

    public String getName()
    {
        return mongoDatabase.getName();
    }
}

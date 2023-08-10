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
import com.google.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.bson.Document;
import org.eclipse.collections.api.factory.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.finos.legend.depot.store.mongo.core.BaseMongo.createIndexesIfAbsent;

@Singleton
public class MongoAdminStore
{
    protected final MongoDatabase mongoDatabase;
    private final Map<String,List<IndexModel>> collectionIndexes;


    public MongoAdminStore(MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
        this.collectionIndexes = Maps.mutable.empty();
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

    public Map<String,List<Document>> getAllIndexes()
    {
        Map<String,List<Document>> result = new HashMap<>();
        getAllCollections().forEach(col ->
        {
            List<Document> indexes = new ArrayList<>();
            mongoDatabase.getCollection(col).listIndexes().forEach((Consumer<Document>)indexes::add);
            result.put(col,indexes);
        });
        return result;
    }

    public void deleteIndex(String collectionId, String indexName)
    {
        mongoDatabase.getCollection(collectionId).dropIndex(indexName);
    }

    public void registerIndexes(String collectionName,List<IndexModel> indexes)
    {
        collectionIndexes.put(collectionName,indexes);
    }

    public List<String> createIndexes()
    {
        List<String> results = new ArrayList<>();
        collectionIndexes.keySet().forEach(collection -> results.addAll(createIndexesIfAbsent(mongoDatabase,collection,collectionIndexes.get(collection))));
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

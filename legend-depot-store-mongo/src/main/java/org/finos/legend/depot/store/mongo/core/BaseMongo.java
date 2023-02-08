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

package org.finos.legend.depot.store.mongo.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.store.StoreException;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public abstract class BaseMongo<T extends HasIdentifier>
{
    public static final String LAST_MODIFIED = "lastModified";
    public static final String GROUP_ID = "groupId";
    public static final String ARTIFACT_ID = "artifactId";
    public static final String VERSION_ID = "versionId";
    private static final String INDEX_NAME = "name";
    public static final String ID_FIELD = "_id";
    public static final String ID = "id";
    public static final UpdateOptions INSERT_IF_ABSENT = new UpdateOptions().upsert(true);
    public static final FindOneAndReplaceOptions FIND_ONE_AND_REPLACE_OPTIONS = new FindOneAndReplaceOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BaseMongo.class);
    private final ObjectMapper objectMapper;
    private final MongoDatabase mongoDatabase;
    private final Class<T> documentClass;


    public BaseMongo(MongoDatabase databaseProvider, Class<T> documentClass)
    {
        this.mongoDatabase = databaseProvider;
        this.documentClass = documentClass;
        objectMapper = new ObjectMapper();
    }

    public BaseMongo(MongoDatabase databaseProvider, Class<T> documentClass, ObjectMapper mapper)
    {
        this.mongoDatabase = databaseProvider;
        this.documentClass = documentClass;
        objectMapper = mapper;
    }

    public static <T extends HasIdentifier> Document buildDocument(T object)
    {
        try
        {
            Document doc = Document.parse(new ObjectMapper().writeValueAsString(object));
            doc.remove(ID_FIELD);
            doc.remove(ID);
            return doc;
        }
        catch (JsonProcessingException e)
        {
            LOGGER.error("Error serializing document to json", e);
            throw new StoreException("Error serializing dataset to json");
        }
    }

    public MongoDatabase getDatabase()
    {
        return mongoDatabase;
    }

    protected MongoCollection getMongoCollection(String col)
    {
        return getDatabase().getCollection(col);
    }

    private static List<String> getIndexes(MongoCollection collection)
    {
        List<Document> indexes = new ArrayList<>();
        collection.listIndexes().forEach((Consumer<Document>)indexes::add);
        return indexes.stream().map(i -> i.getString(INDEX_NAME)).collect(Collectors.toList());
    }

    public static List<String> createIndexesIfAbsent(MongoDatabase mongoDatabase,String collection,List<IndexModel> candidateIndexes)
    {
        MongoCollection mongoCollection = mongoDatabase.getCollection(collection);
        List<String> existingIndexes = getIndexes(mongoCollection);
        List<IndexModel> newIndexes =  candidateIndexes.stream().filter(i -> !existingIndexes.contains(i.getOptions().getName())).collect(Collectors.toList());
        if (!newIndexes.isEmpty())
        {
            try
            {
                return mongoCollection.createIndexes(newIndexes);
            }
            catch (Exception e)
            {
                return Arrays.asList(e.getMessage());
            }
        }
        return Collections.EMPTY_LIST;

    }

    protected abstract MongoCollection getCollection();


    protected Bson getArtifactAndVersionFilter(String groupId, String artifactId, String versionId)
    {
        return and(eq(VERSION_ID, versionId),
                and(eq(GROUP_ID, groupId),
                        eq(ARTIFACT_ID, artifactId)));
    }

    protected Bson getArtifactFilter(String groupId, String artifactId)
    {
        return and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId));
    }

    public T createOrUpdate(T data)
    {
        validateNewData(data);
        Bson keyFilter = getKeyFilter(data);
        Document result = (Document)getCollection().findOneAndReplace(keyFilter, buildDocument(data), FIND_ONE_AND_REPLACE_OPTIONS);
        return convert(result, documentClass);
    }

    public List<T> getAllStoredEntities()
    {
        List<T> result = new ArrayList<>();
        getCollection().find().forEach((Consumer<Document>)doc -> result.add(convert(doc, documentClass)));
        return result;
    }

    public List<T> getStoredEntitiesByPage(int page, int pageSize)
    {
        List<T> result = new ArrayList<>();
        getCollection().find().skip(Math.max(page - 1, 0) * pageSize).limit(pageSize).forEach((Consumer<Document>)doc -> result.add(convert(doc, documentClass)));
        return result;
    }

    public <T> T convert(Document document, Class<T> clazz)
    {
        if (document == null)
        {
            return null;
        }
        ObjectId id = document.getObjectId(ID_FIELD);
        if (id != null)
        {
            document.remove(ID_FIELD);
            document.put(ID, id.toHexString());
        }
        try
        {
            return this.objectMapper.convertValue(document, clazz);
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("error converting document (%s) to class %s. reason: %s", Objects.requireNonNull(id).toString(), clazz.getSimpleName(), e.getMessage()));
            return null;
        }
    }

    protected static IndexModel buildIndex(String indexName, String... fieldNames)
    {
        return buildIndex(indexName, false, fieldNames);
    }

    protected static IndexModel buildIndex(String indexName,boolean isUnique, String... fieldNames)
    {
        IndexOptions indexOptions = new IndexOptions().unique(isUnique).name(indexName);
        return buildIndex(indexName,indexOptions,fieldNames);
    }

    protected static IndexModel buildIndex(String indexName,IndexOptions indexOptions, String... fieldNames)
    {
        Bson index = Indexes.ascending(fieldNames);
        return new IndexModel(index,  indexOptions.name(indexName));
    }

    protected abstract Bson getKeyFilter(T data);

    protected abstract void validateNewData(T data);

    protected List<T> find(Bson filter)
    {
        return convert(getCollection().find(filter));
    }

    protected FindIterable executeFind(Bson filter)
    {
        return getCollection().find(filter);
    }


    protected List<T> convert(FindIterable iterable)
    {
        List<T> result = new ArrayList<>();
        iterable.forEach((Consumer<Document>)doc -> result.add(convert(doc, documentClass)));
        return result;
    }

    protected Optional<T> findOne(Bson filter)
    {
        List<T> result = convert(getCollection().find(filter));
        if (!result.isEmpty() && result.size() > 1)
        {
            throw new IllegalStateException(String.format(" Found more than one match %s in collection %s",filter,getCollection().getNamespace().getCollectionName()));
        }
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }


    protected boolean delete(Bson key)
    {
        DeleteResult deleteResult = getCollection().deleteMany(key);
        LOGGER.debug("delete result {} :{}",getCollection().getNamespace().getCollectionName(),deleteResult);
        return deleteResult.wasAcknowledged();
    }
}

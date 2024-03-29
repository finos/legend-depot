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

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.ARTIFACT_ID;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.GROUP_ID;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.VERSION_ID;

@Deprecated
public final class EntitiesMigration
{
    static final String ENTITY_LEGACY_PATH = "entity.path";
    static final String PACKAGE = "package";
    static final String ENTITY_CONTENT = "content";
    static final String ENTITY = "entity";
    static final String ENTITY_TYPE = "_type";
    static final String ENTITY_TYPE_DATA = "entityData";
    static final String ENTITY_ATTRIBUTES = "entityAttributes";
    static final String CLASSIFIER_PATH = "classifierPath";
    static final String PATH = "path";
    private final MongoDatabase mongoDatabase;
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EntitiesMigration.class);

    public EntitiesMigration(MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    @Deprecated
    public com.mongodb.client.result.DeleteResult versionedEntitiesDeletion()
    {
        MongoCollection<Document> collection = mongoDatabase.getCollection(EntitiesMongo.COLLECTION);
        return collection.deleteMany(Filters.eq("versionedEntity", true));
    }

    @Deprecated
    public void entitiesToStoredEntityDataMigration()
    {
        MongoCollection<Document> entitiesCollection = mongoDatabase.getCollection(EntitiesMongo.COLLECTION);
        AtomicInteger i = new AtomicInteger();
        getAllStoredEntitiesCoordinates().parallelStream().forEach(pv ->
        {
            entitiesCollection.find(and(and(eq(GROUP_ID, pv.getGroupId()), eq(ARTIFACT_ID, pv.getArtifactId())), eq(VERSION_ID, pv.getVersionId())))
                .forEach((Consumer<Document>) document ->
                {
                    String groupId = document.getString(GROUP_ID);
                    String artifactId = document.getString(ARTIFACT_ID);
                    String versionId = document.getString(VERSION_ID);
                    Map<String,?> entity = ((Document) document.get(ENTITY));
                    String path = ((Document) entity).getString(PATH);
                    Map<String,String> entityAttributes = new HashMap<>();
                    entityAttributes.put(PATH, path);
                    entityAttributes.put(CLASSIFIER_PATH, ((Document) entity).getString(CLASSIFIER_PATH));
                    entityAttributes.put(PACKAGE, ((Document) ((Document) entity).get(ENTITY_CONTENT)).getString(PACKAGE));

                    try
                    {
                        entitiesCollection
                        .updateOne(and(and(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId)), eq(VERSION_ID, versionId)), eq(ENTITY_LEGACY_PATH, path)),
                            Updates.combine(
                                Updates.set(ENTITY_TYPE, ENTITY_TYPE_DATA),
                                Updates.set(ENTITY_ATTRIBUTES, (Map<String,?>) entityAttributes)));
                        LOGGER.info(String.format("%s-%s-%s-%s update completed", groupId, artifactId, versionId, path));
                    }
                    catch (Exception e)
                    {
                        LOGGER.info("Error while updating entities: " + e);
                        LOGGER.info(String.format("%s-%s-%s-%s entity update could not be completed", groupId, artifactId, versionId, path));
                    }
            });
            i.incrementAndGet();
            LOGGER.info(String.format("[%s] versions updated", i.get()));
        });
        LOGGER.info(String.format("Entities update completed", i.get()));
    }

    private List<ProjectVersion> getAllStoredEntitiesCoordinates()
    {
        List<ProjectVersion> result = new ArrayList<>();
        BasicDBList concat = new BasicDBList();
        concat.add("$groupId");
        concat.add(":");
        concat.add("$artifactId");
        concat.add(":");
        concat.add("$versionId");
        Bson allCoordinates = Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.include(GROUP_ID, ARTIFACT_ID, VERSION_ID),
                Projections.computed("coordinate", new BasicDBObject("$concat", concat))));

        mongoDatabase.getCollection(EntitiesMongo.COLLECTION).aggregate(Arrays.asList(allCoordinates, group("$coordinate"))).forEach((Consumer<Document>) document ->
                {
                    StringTokenizer tokenizer = new StringTokenizer(document.getString("_id"), ":");
                    result.add(new ProjectVersion(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
                }
        );
        return result;
    }
}


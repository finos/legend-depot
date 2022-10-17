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

package org.finos.legend.depot.store.mongo.entities;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.depot.domain.EntityValidator;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.entity.StoredEntityOverview;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.status.StoreOperationResult;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.mongo.BaseMongo;
import org.finos.legend.depot.store.mongo.MongoStoreErrors;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;
import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class EntitiesMongo extends BaseMongo<StoredEntity> implements Entities, UpdateEntities
{
    public static final String ENTITIES_VERSIONS = "entities";

    public static final String ENTITY = "entity";
    public static final String ENTITY_CLASSIFIER_PATH = "entity.classifierPath";
    public static final String CLASSIFIER_PATH = "classifierPath";
    public static final String ENTITY_CONTENT = "entity.content";
    public static final String ENTITY_PATH = "entity.path";
    public static final String PATH = "path";
    public static final String ENTITY_PACKAGE = "entity.content.package";
    public static final String VERSIONED_ENTITY = "versionedEntity";
    private static final int MAX_NUMBER_ENTITIES = 100;


    public final boolean transactionMode;

    private final MongoClient mongoClient;


    @Inject
    public EntitiesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider, MongoClient mongoClient, @Named("transactionMode") boolean transactionMode)
    {
        super(databaseProvider, StoredEntity.class);
        this.mongoClient = mongoClient;
        this.transactionMode = transactionMode;
    }

    public EntitiesMongo(@Named("mongoDatabase") MongoDatabase mongoProvider, MongoClient mongoClient)
    {
        this(mongoProvider, mongoClient, false);
    }

    public EntitiesMongo(@Named("mongoDatabase") MongoDatabase mongoProvider)
    {
        this(mongoProvider, null, false);
    }

    public boolean createIndexesIfAbsent()
    {
        createIndexIfAbsent("groupId-artifactId-versionId", GROUP_ID, ARTIFACT_ID, VERSION_ID);
        createIndexIfAbsent("groupId-artifactId-versionId-entityPath", GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PATH);
        createIndexIfAbsent("groupId-artifactId-versionId-package", GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PACKAGE);
        createIndexIfAbsent("groupId-artifactId-hashed", GROUP_ID, ARTIFACT_ID);
        createIndexIfAbsent("entity-classifier", ENTITY_CLASSIFIER_PATH);
        return true;

    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(ENTITIES_VERSIONS);
    }

    @Override
    protected Bson getKeyFilter(StoredEntity data)
    {
        return and(getArtifactAndVersionFilter(data.getGroupId(), data.getArtifactId(), data.getVersionId()),
                eq(ENTITY_PATH, data.getEntity().getPath()));
    }


    private Bson getEntityPathFilter(StoredEntity entity)
    {
        return and(getArtifactAndVersionFilter(entity.getGroupId(), entity.getArtifactId(), entity.getVersionId()),
                eq(ENTITY_PATH, entity.getEntity().getPath()));
    }

    private Bson getEntityPathFilter(String groupId, String artifactId, String versionId, String path)
    {
        return and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(ENTITY_PATH, path));
    }

    protected Bson getArtifactWithVersionsFilter(String groupId, String artifactId, String versionId, boolean versioned)
    {
        return and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(VERSIONED_ENTITY, versioned));
    }

    @Override
    protected void validateNewData(StoredEntity data)
    {
        //no specific validation
    }


    protected Bson combineDocument(StoredEntity entity)
    {
        return combine(
                set(GROUP_ID, entity.getGroupId()),
                set(ARTIFACT_ID, entity.getArtifactId()),
                set(VERSION_ID, entity.getVersionId()),
                set(VERSIONED_ENTITY, entity.isVersionedEntity()),
                set(ENTITY_PATH, entity.getEntity().getPath()),
                set(ENTITY_CLASSIFIER_PATH, entity.getEntity().getClassifierPath()),
                set(ENTITY_CONTENT, entity.getEntity().getContent()),
                currentDate(LAST_MODIFIED));
    }


    public StoreOperationResult newOrUpdate(ClientSession clientSession, StoredEntity entity)
    {
        StoreOperationResult report = validateEntity(entity, new StoreOperationResult(0, 0, 0, new ArrayList<>()));
        if (report.hasErrors())
        {
            return report;
        }

        UpdateResult result;
        if (clientSession != null)
        {
            result = getCollection().updateOne(clientSession, getEntityPathFilter(entity), combineDocument(entity), INSERT_IF_ABSENT);
        }
        else
        {
            result = getCollection().updateOne(getEntityPathFilter(entity), combineDocument(entity), INSERT_IF_ABSENT);
        }
        if (result.getUpsertedId() != null)
        {
            report.addInsertedCount();
        }
        else
        {
            report.addModifiedCount();
        }
        return report;
    }


    public StoreOperationResult newOrUpdate(StoredEntity entity)
    {
        return newOrUpdate(null, entity);
    }

    private StoreOperationResult validateEntity(StoredEntity entity, StoreOperationResult result)
    {
        if (!EntityValidator.isValidGroupId(entity.getGroupId()))
        {
            throw new IllegalArgumentException(String.format(MongoStoreErrors.INVALID_GROUP_ID, entity.getGroupId()));
        }
        if (!EntityValidator.isValidArtifactId(entity.getArtifactId()))
        {
            throw new IllegalArgumentException(String.format(MongoStoreErrors.INVALID_ARTIFACT_ID, entity.getArtifactId()));
        }
        if (!VersionValidator.isValid(entity.getVersionId()))
        {
            result.logError(String.format(MongoStoreErrors.INVALID_VERSION_ID, entity.getVersionId()));
        }
        return result;
    }

    public StoreOperationResult newOrUpdate(ClientSession clientSession, List<StoredEntity> versionedEntities)
    {
        StoreOperationResult report = new StoreOperationResult(0, 0, 0, new ArrayList<>());
        for (StoredEntity versionedEntity : versionedEntities)
        {
            StoreOperationResult result = newOrUpdate(clientSession, versionedEntity);
            report.addInsertedCount();
            report.logErrors(result.getErrors());
        }
        return report;
    }

    public StoreOperationResult atomicNewOrUpdate(ClientSession clientSession, List<StoredEntity> versionedEntities)
    {

        StoreOperationResult report = new StoreOperationResult(0, 0, 0, new ArrayList<>());
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .readPreference(ReadPreference.primary()).build();
        try
        {
            clientSession.startTransaction(txnOptions);
            newOrUpdate(clientSession, versionedEntities);
            if (report.hasErrors())
            {
                clientSession.abortTransaction();
            }
            else
            {
                clientSession.commitTransaction();
            }
        }
        catch (RuntimeException e)
        {
            clientSession.abortTransaction();
            report.logError(e.getMessage());
            throw e;
        }
        finally
        {
            clientSession.close();
        }
        return report;
    }

    @Override
    public StoreOperationResult createOrUpdate(List<StoredEntity> versionedEntities)
    {
        if (transactionMode)
        {
            final ClientSession clientSession = mongoClient.startSession();
            return atomicNewOrUpdate(clientSession, versionedEntities);
        }
        return newOrUpdate(null, versionedEntities);
    }

    @Override
    public Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String path)
    {
        validateInput(groupId, artifactId, versionId);
        Bson filterByKey = getEntityPathFilter(groupId, artifactId, versionId, path);
        Optional<StoredEntity> found = findOne(filterByKey);
        return found.map(StoredEntity::getEntity);
    }

    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId)
    {
        return find(getArtifactFilter(groupId, artifactId));
    }

    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId, String versionId)
    {
        return find(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }

    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId, String versionId, boolean versioned)
    {
        return find(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(VERSIONED_ENTITY, versioned)));
    }

    @Override
    public List<Entity> getAllEntities(String groupId, String artifactId, String versionId)
    {
        validateInput(groupId, artifactId, versionId);
        return find(getArtifactAndVersionFilter(groupId, artifactId, versionId)).stream().map(StoredEntity::getEntity).collect(Collectors.toList());
    }


    private List<Entity> getAllEntities(String groupId, String artifactId, String versionId, boolean versioned)
    {
        validateInput(groupId, artifactId, versionId);
        return find(getArtifactWithVersionsFilter(groupId, artifactId, versionId, versioned)).stream().map(StoredEntity::getEntity).collect(Collectors.toList());
    }

    @Override
    public List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages)
    {
        validateInput(groupId, artifactId, versionId);
        Bson filter = getArtifactWithVersionsFilter(groupId, artifactId, versionId, versioned);
        if (includeSubPackages)
        {
            filter = and(filter, regex(ENTITY_PACKAGE, "^" + packageName + "*"));
        }
        else
        {
            filter = and(filter, eq(ENTITY_PACKAGE, packageName));
        }
        Stream<Entity> entities = find(filter).stream().map(StoredEntity::getEntity);
        if (classifierPaths != null && !classifierPaths.isEmpty())
        {
            entities = entities.filter(entity -> classifierPaths.contains(entity.getClassifierPath()));
        }
        return entities.collect(Collectors.toList());
    }

    protected List<StoredEntity> transform(boolean summary, FindIterable query)
    {
        if (!summary)
        {
            return convert(query);
        }
        List<StoredEntity> result = new ArrayList<>();
        query.forEach((Consumer<Document>) doc ->
        {
            Map<String, Object> entity = (Map<String, Object>) doc.get(ENTITY);
            result.add(new StoredEntityOverview(doc.getString(GROUP_ID), doc.getString(ARTIFACT_ID), doc.getString(VERSION_ID), doc.getBoolean(VERSIONED_ENTITY), (String) entity.get(PATH), (String) entity.get(CLASSIFIER_PATH)));
        });
        return result;
    }

    @Override
    public List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, String search, List<ProjectVersion> projectVersions, Integer limit, boolean summary, boolean versioned)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq(ENTITY_CLASSIFIER_PATH, classifier));
        filters.add(eq(VERSIONED_ENTITY, versioned));
        if (projectVersions != null && !projectVersions.isEmpty())
        {
            filters.add(or(ListIterate.collect(projectVersions, projectVersion -> getArtifactAndVersionFilter(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId()))));
        }
        if (search != null)
        {
            filters.add(Filters.regex(ENTITY_PATH, Pattern.quote(search), "i"));
        }
        return transform(summary, executeFind(and(filters)).limit(Math.min(MAX_NUMBER_ENTITIES, limit == null ? Integer.MAX_VALUE : limit)));
    }

    @Override
    public List<StoredEntity> findLatestEntitiesByClassifier(String classifier, String search, Integer limit, boolean summary, boolean versioned)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq(ENTITY_CLASSIFIER_PATH, classifier));
        filters.add(eq(VERSIONED_ENTITY, versioned));
        filters.add(eq(VERSION_ID, MASTER_SNAPSHOT));
        if (search != null)
        {
            filters.add(Filters.regex(ENTITY_PATH, Pattern.quote(search), "i"));
        }
        return transform(summary, executeFind(and(filters)).limit(Math.min(MAX_NUMBER_ENTITIES, limit == null ? Integer.MAX_VALUE : limit)));
    }

    public List<StoredEntity> findEntitiesByClassifier(String classifier, boolean summary, boolean versioned)
    {
        return transform(summary, executeFind(and(eq(ENTITY_CLASSIFIER_PATH, classifier), eq(VERSIONED_ENTITY, versioned))));
    }

    @Override
    public List<StoredEntity> findEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean summary, boolean versionedEntities)
    {
        return findByClassifier(groupId, artifactId, versionId, classifier, summary, versionedEntities);
    }

    private List<StoredEntity> findByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean summary, boolean versionedEntities)
    {
        return transform(summary, executeFind(and(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(ENTITY_CLASSIFIER_PATH, classifier)), eq(VERSIONED_ENTITY, versionedEntities))));
    }

    protected List<StoredEntity> getEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean versionedEntities)
    {
        return find(and(getArtifactWithVersionsFilter(groupId, artifactId, versionId, versionedEntities), eq(ENTITY_CLASSIFIER_PATH, classifier)));
    }

    private void validateInput(String groupId, String artifactId, String versionId)
    {
        if (!EntityValidator.isValidGroupId(groupId))
        {
            throw new IllegalArgumentException(String.format(MongoStoreErrors.INVALID_GROUP_ID, groupId));
        }
        if (!EntityValidator.isValidArtifactId(artifactId))
        {
            throw new IllegalArgumentException(String.format(MongoStoreErrors.INVALID_ARTIFACT_ID, artifactId));
        }
        if (!VersionValidator.isValid(versionId))
        {
            throw new IllegalArgumentException(String.format(MongoStoreErrors.INVALID_VERSION_ID, versionId));
        }
    }


    @Override
    public List<Entity> getEntities(String groupId, String artifactId, String version, boolean versioned)
    {
        validateInput(groupId, artifactId, version);
        return getAllEntities(groupId, artifactId, version, versioned);
    }


    @Override
    public StoreOperationResult delete(String groupId, String artifactId, String versionId, boolean versioned)
    {
        Bson filter = and(eq(VERSIONED_ENTITY, versioned), getArtifactAndVersionFilter(groupId, artifactId, versionId));
        DeleteResult result = getCollection().deleteMany(filter);
        return new StoreOperationResult(0, 0, result.getDeletedCount(), Collections.emptyList());
    }

    @Override
    public StoreOperationResult deleteAll(String groupId, String artifactId)
    {
        Bson filter = getArtifactFilter(groupId, artifactId);
        DeleteResult result = getCollection().deleteMany(filter);
        return new StoreOperationResult(0, 0, result.getDeletedCount(), Collections.emptyList());
    }

    public long getRevisionEntityCount()
    {
        return getCollection().countDocuments(eq(VERSION_ID, MASTER_SNAPSHOT));
    }


    public long getVersionEntityCount()
    {
        return getCollection().countDocuments(ne(VERSION_ID, MASTER_SNAPSHOT));
    }


    @Override
    public long getVersionEntityCount(String groupId, String artifactId, String versionId)
    {
        return getCollection().countDocuments(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }


    @Override
    public long getEntityCount(String groupId, String artifactId)
    {
        return getCollection().countDocuments(getArtifactFilter(groupId, artifactId));
    }

    @Override
    public List<Pair<String, String>> getStoredEntitiesCoordinates()
    {
        List<Pair<String, String>> result = new ArrayList<>();
        BasicDBList concat = new BasicDBList();
        concat.add("$groupId");
        concat.add(":");
        concat.add("$artifactId");
        Bson allCoordinates = Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.include(GROUP_ID, ARTIFACT_ID),
                Projections.computed("coordinate", new BasicDBObject("$concat", concat))));

        getCollection().aggregate(Arrays.asList(allCoordinates, group("$coordinate"))).forEach((Consumer<Document>) document ->
                {
                    StringTokenizer tokenizer = new StringTokenizer(document.getString("_id"), ":");
                    result.add(Tuples.pair(tokenizer.nextToken(), tokenizer.nextToken()));
                }
        );
        return result;
    }
}
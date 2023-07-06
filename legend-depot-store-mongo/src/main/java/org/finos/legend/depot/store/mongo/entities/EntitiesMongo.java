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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.CoordinateValidator;
import org.finos.legend.depot.domain.entity.EntityValidationErrors;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.entity.StoredEntityOverview;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.sdlc.tools.entity.EntityPaths;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;

public class EntitiesMongo<T extends StoredEntity> extends AbstractEntitiesMongo<T> implements Entities<T>, UpdateEntities<T>
{
    public static final String COLLECTION = "entities";
    public static final UpdateOptions INSERT_IF_ABSENT = new UpdateOptions().upsert(true);

    @Inject
    public EntitiesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider,StoredEntity.class);
    }

    public EntitiesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider, Class<T> documentClass)
    {
        super(databaseProvider, documentClass);
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("versioned-groupId-artifactId-versionId-versioned", VERSIONED_ENTITY,GROUP_ID, ARTIFACT_ID, VERSION_ID),
                buildIndex("groupId-artifactId-versionId-entityPath", true, GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PATH),
                buildIndex("groupId-artifactId-versionId-package", GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PACKAGE),
                buildIndex("versioned-entity-classifier", VERSIONED_ENTITY,ENTITY_CLASSIFIER_PATH)
        );
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected Bson getKeyFilter(StoredEntity data)
    {
        return and(getArtifactAndVersionFilter(data.getGroupId(), data.getArtifactId(), data.getVersionId()),
                eq(ENTITY_PATH, data.getEntity().getPath()));
    }

    @Override
    protected void validateNewData(StoredEntity entity)
    {
        List<String> errors = new ArrayList<>();
        if (!CoordinateValidator.isValidGroupId(entity.getGroupId()))
        {
            errors.add(String.format(EntityValidationErrors.INVALID_GROUP_ID, entity.getGroupId()));
        }
        if (!CoordinateValidator.isValidArtifactId(entity.getArtifactId()))
        {
            errors.add(String.format(EntityValidationErrors.INVALID_ARTIFACT_ID, entity.getArtifactId()));
        }
        if (!VersionValidator.isValid(entity.getVersionId()))
        {
            errors.add(String.format(EntityValidationErrors.INVALID_VERSION_ID, entity.getVersionId()));
        }
        if (!EntityPaths.isValidEntityPath(entity.getEntity().getPath()))
        {
            errors.add(String.format(EntityValidationErrors.INVALID_ENTITY_PATH, entity.getEntity().getPath()));
        }
        if (!errors.isEmpty())
        {
            throw new IllegalArgumentException("invalid data :" + String.join(",",errors));
        }
    }

    @Override
    public List<T> createOrUpdate(List<T> versionedEntities)
    {
        versionedEntities.forEach(item ->
                getCollection().updateOne(getEntityPathFilter(item.getGroupId(), item.getArtifactId(), item.getVersionId(), item.getEntity().getPath()), combineDocument(item), INSERT_IF_ABSENT));
        return versionedEntities;
    }

    private Bson combineDocument(StoredEntity entity)
    {
        return combine(
                set(GROUP_ID, entity.getGroupId()),
                set(ARTIFACT_ID, entity.getArtifactId()),
                set(VERSION_ID, entity.getVersionId()),
                set(ENTITY_PATH, entity.getEntity().getPath()),
                set(ENTITY_CLASSIFIER_PATH, entity.getEntity().getClassifierPath()),
                set(ENTITY_CONTENT, entity.getEntity().getContent()),
                set(VERSIONED_ENTITY, entity.isVersionedEntity()),
                currentDate(UPDATED));
    }

    @Override
    protected boolean isVersioned()
    {
        return false;
    }

    @Override
    public List<T> findReleasedEntitiesByClassifier(String classifier, String search, List<ProjectVersion> projectVersions, Integer limit, boolean summary)
    {
        FindIterable findIterable = super.findReleasedEntitiesByClassifier(classifier, search, projectVersions);
        if (limit != null)
        {
            return transform(summary, findIterable.limit(limit));
        }
        return transform(summary, findIterable);
    }

    @Override
    public List<T> findLatestEntitiesByClassifier(String classifier, String search, Integer limit, boolean summary)
    {
        FindIterable findIterable = super.findLatestEntitiesByClassifier(classifier, search);
        if (limit != null)
        {
            return transform(summary, findIterable.limit(limit));
        }
        return transform(summary, findIterable);
    }

    @Override
    public List<T> findReleasedEntitiesByClassifier(String classifier, boolean summary)
    {
        return transform(summary, super.findReleasedEntitiesByClassifier(classifier));
    }

    @Override
    public List<T> findLatestEntitiesByClassifier(String classifier, boolean summary)
    {
        return transform(summary, super.findLatestEntitiesByClassifier(classifier));
    }

    @Override
    public List<T> findEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean summary)
    {
        return transform(summary, super.findEntitiesByClassifier(groupId, artifactId, versionId, classifier));
    }

    protected List<T> transform(boolean summary, FindIterable query)
    {
        if (!summary)
        {
            return convert(query);
        }
        List<T> result = new ArrayList<>();
        query.forEach((Consumer<Document>) doc ->
        {
            Map<String, Object> entity = (Map<String, Object>) doc.get(ENTITY);
            result.add((T)new StoredEntityOverview(doc.getString(GROUP_ID), doc.getString(ARTIFACT_ID), doc.getString(VERSION_ID), (String) entity.get(PATH), (String) entity.get(CLASSIFIER_PATH)));
        });
        return result;
    }
}

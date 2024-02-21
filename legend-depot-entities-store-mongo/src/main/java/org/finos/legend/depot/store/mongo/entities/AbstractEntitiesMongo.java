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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
import static com.mongodb.client.model.Updates.set;
import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public abstract class AbstractEntitiesMongo<T extends StoredEntity> extends BaseMongo<T>
{
    static final String ENTITY = "entity";
    static final String ENTITY_TYPE = "_type";
    static final String ENTITY_DATA = "data";
    static final String ENTITY_TYPE_DATA = "entityData";
    static final String ENTITY_ATTRIBUTES = "entityAttributes";
    protected static final String ENTITY_CLASSIFIER_PATH = "entityAttributes.classifierPath";
    static final String CLASSIFIER_PATH = "classifierPath";
    protected static final String ENTITY_PATH = "entityAttributes.path";
    static final String PATH = "path";
    static final String PACKAGE = "package";
    protected static final String ENTITY_PACKAGE = "entityAttributes.package";
    static final String ENTITY_TYPE_STRING_DATA = "entityStringData";
    protected static final String VERSIONED_ENTITY_TYPE_STRING_DATA = "versionedEntityStringData";
    protected static final ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
    static final String RE_STRING_START = "^";
    static final String RE_STAR = "*";

    protected AbstractEntitiesMongo(MongoDatabase mongoDatabase, Class documentClass)
    {
        super(mongoDatabase, documentClass);
    }

    protected Bson getEntityPathFilter(String groupId, String artifactId, String versionId, String path)
    {
        return and(getArtifactAndVersionVersionedFilter(groupId, artifactId, versionId), eq(ENTITY_PATH, path));
    }

    protected Bson getArtifactAndVersionVersionedFilter(String groupId, String artifactId, String versionId)
    {
        return getArtifactAndVersionFilter(groupId, artifactId, versionId);
    }

    protected Bson getArtifactVersionedFilter(String groupId, String artifactId)
    {
        return getArtifactFilter(groupId, artifactId);
    }

    protected abstract Bson getKeyFilter(T data);

    protected abstract void validateNewData(T data);

    protected abstract Entity resolvedToEntityDefinition(T storedEntity);

    public Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String path)
    {
        Bson filterByKey = getEntityPathFilter(groupId, artifactId, versionId, path);
        return findOne(filterByKey).map(this::resolvedToEntityDefinition);
    }

    private List<Entity> getEntities(String groupId, String artifactId, String versionId, List<String> entityPaths)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(or(ListIterate.collect(entityPaths, path -> getEntityPathFilter(groupId, artifactId, versionId, path))));
        return find(and(filters)).stream().map(this::resolvedToEntityDefinition).collect(Collectors.toList());
    }

    public List<Entity> getEntityFromDependencies(Set<ProjectVersion> dependencies, List<String> entityPaths)
    {
        List<Entity> entity = new ArrayList<>();
        for (ProjectVersion dep : dependencies)
        {
            List<Entity> depEntity = this.getEntities(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), entityPaths);
            entity.addAll(depEntity);
            if (depEntity.size() == entityPaths.size())
            {
                break;
            }
        }
        return entity;
    }

    public List<T> getStoredEntities(String groupId, String artifactId)
    {
        return find(getArtifactVersionedFilter(groupId, artifactId));
    }

    public List<T> getStoredEntities(String groupId, String artifactId, String versionId)
    {
        return find(getArtifactAndVersionVersionedFilter(groupId, artifactId, versionId));
    }

    public List<Entity> getAllEntities(String groupId, String artifactId, String versionId)
    {
        return find(getArtifactAndVersionVersionedFilter(groupId, artifactId, versionId)).parallelStream().map(this::resolvedToEntityDefinition).collect(Collectors.toList());
    }

    public List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, Set<String> classifierPaths, boolean includeSubPackages)
    {
        Bson filter = getArtifactAndVersionVersionedFilter(groupId, artifactId, versionId);
        if (packageName != null && !packageName.trim().isEmpty() && includeSubPackages)
        {
            filter = and(filter, regex(ENTITY_PACKAGE, RE_STRING_START + packageName + RE_STAR));
        }
        else if (packageName != null && !packageName.trim().isEmpty())
        {
            filter = and(filter, eq(ENTITY_PACKAGE, packageName));
        }

        if (classifierPaths != null && !classifierPaths.isEmpty())
        {
            filter = and(filter, in(ENTITY_CLASSIFIER_PATH, classifierPaths));
        }

        return find(filter).parallelStream()
                .map(this::resolvedToEntityDefinition)
                .collect(Collectors.toList());
    }

    public FindIterable findReleasedEntitiesByClassifier(String classifier)
    {
        return executeFind(and(eq(ENTITY_CLASSIFIER_PATH, classifier), not(Filters.regex(BaseMongo.VERSION_ID, BRANCH_SNAPSHOT("")))));
    }

    public FindIterable findLatestEntitiesByClassifier(String classifier)
    {
        return executeFind(and(eq(ENTITY_CLASSIFIER_PATH, classifier), Filters.regex(BaseMongo.VERSION_ID, BRANCH_SNAPSHOT(""))));
    }

    public FindIterable findEntitiesByClassifierAndVersions(String classifier, List<ProjectVersion> projectVersions)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq(ENTITY_CLASSIFIER_PATH, classifier));
        if (projectVersions != null && !projectVersions.isEmpty())
        {
            filters.add(or(ListIterate.collect(projectVersions, projectVersion -> getArtifactAndVersionFilter(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId()))));
        }
        return executeFind(and(filters));
    }

    public FindIterable findReleasedEntitiesByClassifier(String classifier, String search)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq(ENTITY_CLASSIFIER_PATH, classifier));
        filters.add(Filters.not(Filters.regex(BaseMongo.VERSION_ID, BRANCH_SNAPSHOT(""))));
        if (search != null)
        {
            filters.add(Filters.regex(ENTITY_PATH, Pattern.quote(search), "i"));
        }
        return executeFind(and(filters));
    }

    public FindIterable findLatestEntitiesByClassifier(String classifier, String search)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq(ENTITY_CLASSIFIER_PATH, classifier));
        filters.add(Filters.regex(BaseMongo.VERSION_ID, BRANCH_SNAPSHOT("")));
        if (search != null)
        {
            filters.add(Filters.regex(ENTITY_PATH, Pattern.quote(search), "i"));
        }
        return executeFind(and(filters));
    }

    public FindIterable findEntitiesByClassifierAndVersions(String classifier, String search, List<ProjectVersion> projectVersions)
    {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq(ENTITY_CLASSIFIER_PATH, classifier));
        if (projectVersions != null && !projectVersions.isEmpty())
        {
            filters.add(or(ListIterate.collect(projectVersions, projectVersion -> getArtifactAndVersionFilter(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId()))));
        }
        else
        {
            filters.add(Filters.not(Filters.regex(BaseMongo.VERSION_ID, BRANCH_SNAPSHOT(""))));
        }
        if (search != null)
        {
            filters.add(Filters.regex(ENTITY_PATH, Pattern.quote(search), "i"));
        }
        return executeFind(and(filters));
    }

    public long delete(String groupId, String artifactId, String versionId)
    {
        return delete(getArtifactAndVersionVersionedFilter(groupId, artifactId, versionId));
    }

    public long delete(String groupId, String artifactId)
    {
        return delete(getArtifactVersionedFilter(groupId, artifactId));
    }

    public List<Pair<String, String>> getStoredEntitiesCoordinates()
    {
        List<Pair<String, String>> result = new ArrayList<>();
        BasicDBList concat = new BasicDBList();
        concat.add("$groupId");
        concat.add(":");
        concat.add("$artifactId");
        Bson allCoordinates = Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.include(BaseMongo.GROUP_ID, BaseMongo.ARTIFACT_ID),
                Projections.computed("coordinate", new BasicDBObject("$concat", concat))));

        getCollection().aggregate(Arrays.asList(allCoordinates, group("$coordinate"))).forEach((Consumer<Document>) document ->
                {
                    StringTokenizer tokenizer = new StringTokenizer(document.getString("_id"), ":");
                    result.add(Tuples.pair(tokenizer.nextToken(), tokenizer.nextToken()));
                }
        );
        return result;
    }

    protected String serializeEntity(Entity entity)
    {
        try
        {
            return objectMapper.writeValueAsString(entity);
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalStateException(String.format("Error: %s while storing entity: %s", e.getMessage(), entity.getPath()));
        }
    }

    protected Map<String, ?> buildEntityAttributes(Entity entity)
    {
        Map<String, String> entityAttributes = new HashMap<>();
        entityAttributes.put(PATH, entity.getPath());
        entityAttributes.put(CLASSIFIER_PATH, entity.getClassifierPath());
        if (entity.getContent() != null)
        {
            entityAttributes.put(PACKAGE, entity.getContent().get(PACKAGE).toString());
        }
        return entityAttributes;
    }


    protected Bson combineDocument(T storedEntity, Entity entity, String entityType)
    {
        return combine(
                set(BaseMongo.GROUP_ID, storedEntity.getGroupId()),
                set(BaseMongo.ARTIFACT_ID, storedEntity.getArtifactId()),
                set(BaseMongo.VERSION_ID, storedEntity.getVersionId()),
                set(ENTITY_ATTRIBUTES, buildEntityAttributes(entity)),
                set(ENTITY_TYPE, entityType),
                set(ENTITY_DATA, serializeEntity(entity)),
                currentDate(BaseMongo.UPDATED));
    }
}

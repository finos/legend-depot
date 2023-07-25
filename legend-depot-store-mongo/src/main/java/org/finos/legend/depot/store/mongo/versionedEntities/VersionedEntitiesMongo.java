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

package org.finos.legend.depot.store.mongo.versionedEntities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.depot.store.model.versionedEntities.StoredVersionedEntity;
import org.finos.legend.depot.store.api.versionedEntities.UpdateVersionedEntities;
import org.finos.legend.depot.store.api.versionedEntities.VersionedEntities;
import org.finos.legend.depot.store.model.versionedEntities.StoredVersionedEntityData;
import org.finos.legend.depot.store.model.versionedEntities.StoredVersionedEntityStringData;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionedEntitiesMongo extends EntitiesMongo<StoredVersionedEntity> implements VersionedEntities, UpdateVersionedEntities
{
    public static final String COLLECTION = "versioned-entities";

    @Inject
    public VersionedEntitiesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, StoredVersionedEntity.class);
    }

    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("groupId-artifactId-versionId", GROUP_ID, ARTIFACT_ID, VERSION_ID),
                buildIndex("groupId-artifactId-versionId-entityPath", true, GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PATH),
                buildIndex("groupId-artifactId-versionId-package", GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PACKAGE),
                buildIndex("entity-classifier", ENTITY_CLASSIFIER_PATH)
        );
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    public List<StoredVersionedEntity> createOrUpdate(String groupId, String artifactId, String versionId, List<Entity> entityDefinitions)
    {
        List<StoredVersionedEntity> versionedEntities = new ArrayList<>();
        entityDefinitions.parallelStream().forEach(item ->
        {
            StoredVersionedEntity storedEntity = new StoredVersionedEntityStringData(groupId, artifactId, versionId);
            getCollection().updateOne(getEntityPathFilter(groupId, artifactId, versionId, item.getPath()), combineDocument(storedEntity, item, VERSIONED_ENTITY_TYPE_STRING_DATA), INSERT_IF_ABSENT);
            versionedEntities.add(storedEntity);
        });
        return versionedEntities;
    }

    @Override
    protected Entity resolvedToEntityDefinition(StoredVersionedEntity storedEntity)
    {
        if (storedEntity instanceof StoredVersionedEntityData)
        {
            return ((StoredVersionedEntityData) storedEntity).getEntity();
        }
        else if (storedEntity instanceof StoredVersionedEntityStringData)
        {
            try
            {
                return objectMapper.readValue(((StoredVersionedEntityStringData)storedEntity).getData(), EntityDefinition.class);
            }
            catch (JsonProcessingException e)
            {
                throw new IllegalStateException(String.format("Error: %s while fetching entity: %s-%s-%s-%s", e.getMessage(), storedEntity.getGroupId(), storedEntity.getArtifactId(), storedEntity.getVersionId(), ((StoredVersionedEntityStringData)storedEntity).getEntityAttributes().get("path")));
            }
        }
        else
        {
            throw new IllegalStateException("Unknown stored entity type");
        }
    }

}

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

import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.finos.legend.depot.domain.entity.StoredVersionedEntity;
import org.finos.legend.depot.store.api.versionedEntities.UpdateVersionedEntities;
import org.finos.legend.depot.store.api.versionedEntities.VersionedEntities;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;

import javax.inject.Inject;
import javax.inject.Named;
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
        return Arrays.asList(buildIndex("groupId-artifactId-versionId-entityPath", true, GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PATH),
                buildIndex("groupId-artifactId-versionId-package", GROUP_ID, ARTIFACT_ID, VERSION_ID, ENTITY_PACKAGE)
        );
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }
}

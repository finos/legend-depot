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

package org.finos.legend.depot.store.artifacts.store.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.artifacts.domain.ArtifactDetail;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.mongo.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

public class ArtifactsMongo extends BaseMongo<ArtifactDetail> implements UpdateArtifacts
{
    private static final String MONGO_ARTIFACTS = "artifacts";
    private static final String PATH = "path";
    private static final String ARTIFACT_TYPE = "artifactType";

    @Inject
    public ArtifactsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ArtifactDetail.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return createIndexIfAbsent("type-groupId-artifactId-versionId", ARTIFACT_TYPE, GROUP_ID, ARTIFACT_ID, VERSION_ID);
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(MONGO_ARTIFACTS);
    }

    @Override
    protected Bson getKeyFilter(ArtifactDetail data)
    {
        return Filters.and(Filters.and(Filters.and(Filters.and(Filters.eq(ARTIFACT_TYPE, data.getArtifactType()), Filters.eq(BaseMongo.GROUP_ID, data.getGroupId()), Filters.eq(BaseMongo.ARTIFACT_ID, data.getArtifactId())), Filters.eq(BaseMongo.VERSION_ID, data.getVersionId())), Filters.eq(PATH, data.getPath())));
    }

    @Override
    protected void validateNewData(ArtifactDetail data)
    {
        //no specific validation
    }

    @Override
    public Optional<ArtifactDetail> find(String type, String groupId, String artifactId, String versionId, String path)
    {
        return findOne(getKeyFilter(new ArtifactDetail(type, groupId, artifactId, versionId, path)));
    }

    @Override
    public Optional<ArtifactDetail> find(String path)
    {
        return findOne(Filters.eq(PATH, path));
    }
}

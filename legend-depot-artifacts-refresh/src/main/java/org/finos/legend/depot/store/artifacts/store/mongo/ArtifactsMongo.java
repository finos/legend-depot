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

    @Inject
    public ArtifactsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ArtifactDetail.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return createIndexIfAbsent("path", PATH);
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(MONGO_ARTIFACTS);
    }

    @Override
    protected Bson getKeyFilter(ArtifactDetail data)
    {
        return Filters.eq(PATH, data.getPath());
    }

    @Override
    protected void validateNewData(ArtifactDetail data)
    {
        //no specific validation
    }

    @Override
    public Optional<ArtifactDetail> find(String path)
    {
        return findOne(Filters.eq(PATH, path));
    }
}

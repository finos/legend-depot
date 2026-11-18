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

package org.finos.legend.depot.store.mongo.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ArtifactsFilesMongo extends BaseMongo<ArtifactFile> implements ArtifactsFilesStore
{
    public static final String COLLECTION = "artifacts-files";
    private static final String PATH = "path";

    @Inject
    public ArtifactsFilesMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ArtifactFile.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("path", true,PATH));
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected Bson getKeyFilter(ArtifactFile data)
    {
        return Filters.eq(PATH, data.getPath());
    }

    @Override
    protected void validateNewData(ArtifactFile data)
    {
        //no specific validation
    }

    @Override
    public Optional<ArtifactFile> find(String path)
    {
        return findOne(Filters.eq(PATH, path));
    }
}

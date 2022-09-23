//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.store.mongo.generation.artifact;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.generation.artifact.StoredArtifactGeneration;
import org.finos.legend.depot.store.api.generation.artifact.ArtifactGenerations;
import org.finos.legend.depot.store.api.generation.artifact.UpdateArtifactGenerations;
import org.finos.legend.depot.store.mongo.BaseMongo;

public class ArtifactGenerationsMongo extends BaseMongo<StoredArtifactGeneration> implements ArtifactGenerations, UpdateArtifactGenerations
{

    public static final String COLLECTION = "artifact-generations";
    private static final String ARTIFACT_PATH = "artifact.path";
    private static final String GENERATOR = "generator";


    @Inject
    public ArtifactGenerationsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, StoredArtifactGeneration.class);
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    public List<StoredArtifactGeneration> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return createIndexIfAbsent("groupId-artifactId-versionId", GROUP_ID, ARTIFACT_ID, VERSION_ID);
    }


    @Override
    protected Bson getKeyFilter(StoredArtifactGeneration data)
    {
        return and(
            getArtifactAndVersionFilter(data.getGroupId(), data.getArtifactId(), data.getVersionId()),
            eq(ARTIFACT_PATH, data.getArtifact().getPath())
        );
    }

    @Override
    protected void validateNewData(StoredArtifactGeneration data)
    {
        // no
    }

    @Override
    public Optional<StoredArtifactGeneration> get(String groupId, String artifactId, String versionId, String filePath)
    {
        return findOne(
            and(
                getArtifactAndVersionFilter(groupId, artifactId, versionId),
                eq(ARTIFACT_PATH, filePath)
            )
        );
    }

    @Override
    public List<StoredArtifactGeneration> find(String groupId, String artifactId, String versionId)
    {
        return find(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }

    @Override
    public List<StoredArtifactGeneration> findByGenerator(String groupId, String artifactId, String versionId, String generatorPath)
    {
        return find(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(GENERATOR, generatorPath)));
    }

    @Override
    public boolean delete(String groupId, String artifactId, String versionId)
    {
        return delete(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }


}
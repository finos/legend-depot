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

package org.finos.legend.depot.store.mongo.generation.file;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.store.api.generation.file.FileGenerations;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.mongo.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class FileGenerationsMongo extends BaseMongo<StoredFileGeneration> implements FileGenerations, UpdateFileGenerations
{

    public static final String COLLECTION = "file-generations";
    private static final String FILE_PATH = "file.path";
    private static final String GENERATION_PATH = "path";
    private static final String GENERATION_TYPE = "type";

    @Inject
    public FileGenerationsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, StoredFileGeneration.class);
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    public List<StoredFileGeneration> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return createIndexIfAbsent("groupId-artifactId-versionId", GROUP_ID, ARTIFACT_ID, VERSION_ID);
    }

    @Override
    protected Bson getKeyFilter(StoredFileGeneration data)
    {
        return and(getArtifactAndVersionFilter(data.getGroupId(), data.getArtifactId(), data.getVersionId()),
                eq(FILE_PATH, data.getFile().getPath()));
    }

    @Override
    protected void validateNewData(StoredFileGeneration data)
    {
        //no specific validation
    }


    @Override
    public List<StoredFileGeneration> find(String groupId, String artifactId, String versionId)
    {
        return find(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }

    @Override
    public List<StoredFileGeneration> findByElementPath(String groupId, String artifactId, String versionId, String generationPath)
    {
        return find(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(GENERATION_PATH, generationPath)));
    }

    @Override
    public Optional<StoredFileGeneration> findByFilePath(String groupId, String artifactId, String versionId, String filePath)
    {
        return findOne(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(FILE_PATH, filePath)));
    }

    @Override
    public List<StoredFileGeneration> findByType(String groupId, String artifactId, String versionId, String type)
    {
        return find(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(GENERATION_TYPE, type)));
    }

    @Override
    public Optional<StoredFileGeneration> get(String groupId, String artifactId, String versionId, String generationFilePath)
    {
        return findOne(and(getArtifactAndVersionFilter(groupId, artifactId, versionId), eq(FILE_PATH, generationFilePath)));
    }

    @Override
    public boolean delete(String groupId, String artifactId, String versionId)
    {
        return delete(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }


}


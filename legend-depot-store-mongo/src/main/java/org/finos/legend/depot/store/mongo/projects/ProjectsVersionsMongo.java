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

package org.finos.legend.depot.store.mongo.projects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.CoordinateValidator;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProjectsVersionsMongo extends BaseMongo<StoreProjectVersionData> implements ProjectsVersions, UpdateProjectsVersions
{
    public static final String COLLECTION = "versions";
    private static final String VERSION_DATA_EXCLUDED = "versionData.excluded";

    @Inject
    public ProjectsVersionsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, StoreProjectVersionData.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("groupId-artifactId-versionId", true, GROUP_ID, ARTIFACT_ID, VERSION_ID));
    }

    @Override
    public List<StoreProjectVersionData> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public List<StoreProjectVersionData> find(String groupId, String artifactId)
    {
        return find(and(getArtifactFilter(groupId, artifactId)));
    }

    @Override
    public Optional<StoreProjectVersionData> find(String groupId, String artifactId, String versionId)
    {
        if (versionId == null || versionId.isEmpty())
        {
            throw new IllegalArgumentException("cannot find project version, versionId cannot be null");
        }
        return findOne(and(getArtifactAndVersionFilter(groupId, artifactId, versionId)));
    }

    @Override
    public List<StoreProjectVersionData> findVersion(Boolean excluded)
    {
        return find(and(eq(VERSION_DATA_EXCLUDED, excluded)));
    }

    @Override
    public long getVersionCount(String groupId, String artifactId)
    {
        return count(getArtifactFilter(groupId,artifactId));
    }

    @Override
    public long delete(String groupId, String artifactId)
    {
        return delete((getArtifactFilter(groupId, artifactId)));
    }

    @Override
    public long delete(String groupId, String artifactId, String versionId)
    {
        return delete(getArtifactAndVersionFilter(groupId, artifactId, versionId));
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected Bson getKeyFilter(StoreProjectVersionData data)
    {
        return and(eq(VERSION_ID, data.getVersionId()),
                and(eq(GROUP_ID, data.getGroupId()),
                        eq(ARTIFACT_ID, data.getArtifactId())));
    }

    @Override
    protected void validateNewData(StoreProjectVersionData data)
    {
        if (!CoordinateValidator.isValidGroupId(data.getGroupId()) || !CoordinateValidator.isValidArtifactId(data.getArtifactId()))
        {
            throw new IllegalArgumentException(String.format("invalid groupId [%s] or artifactId [%s]",data.getGroupId(),data.getArtifactId()));
        }
        if (!VersionValidator.isValid(data.getVersionId()))
        {
            throw new IllegalArgumentException(String.format("invalid versionId [%s]",data.getVersionId()));
        }
    }
}
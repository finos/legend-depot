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
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class ProjectsVersionsMongo extends BaseMongo<StoreProjectVersionData> implements ProjectsVersions, UpdateProjectsVersions
{
    public static final String COLLECTION = "versions";

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
        if (versionId == null)
        {
            throw new IllegalArgumentException("cannot find project version, versionId cannot be null");
        }
        return findOne(and(getArtifactAndVersionFilter(groupId, artifactId, versionId)));
    }

    @Override
    public List<VersionId> getVersions(String groupId, String artifactId)
    {
        List<StoreProjectVersionData> storeProjectsVersions = find(groupId, artifactId);
        return storeProjectsVersions.isEmpty() ? Collections.EMPTY_LIST : storeProjectsVersions.stream().filter(pv -> !pv.getVersionId().equals(MASTER_SNAPSHOT)).map(pv -> VersionId.parseVersionId(pv.getVersionId())).collect(Collectors.toList());
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getCollection().deleteMany(getArtifactFilter(groupId, artifactId));
        return response;
    }

    @Override
    public MetadataEventResponse deleteByVersionId(String groupId, String artifactId, String versionId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getCollection().findOneAndDelete(getArtifactAndVersionFilter(groupId, artifactId, versionId));
        return response;
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
        if (!MASTER_SNAPSHOT.equals(data.getVersionId()) && !VersionValidator.isValid(data.getVersionId()))
        {
            throw new IllegalArgumentException(String.format("invalid versionId [%s]",data.getVersionId()));
        }
    }
}
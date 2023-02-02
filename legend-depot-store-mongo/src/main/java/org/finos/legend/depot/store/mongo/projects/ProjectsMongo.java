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
import org.finos.legend.depot.domain.EntityValidator;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.store.StoreException;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProjectsMongo extends BaseMongo<StoreProjectData> implements Projects, UpdateProjects
{

    public static final String COLLECTION = "project-configurations";

    @Inject
    public ProjectsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, StoreProjectData.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(buildIndex("groupId-artifactId", true, GROUP_ID, ARTIFACT_ID));
    }

    @Override
    protected Bson getKeyFilter(StoreProjectData data)
    {
        return and(eq(GROUP_ID, data.getGroupId()),
                eq(ARTIFACT_ID, data.getArtifactId()));
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected void validateNewData(StoreProjectData data)
    {
        if (!EntityValidator.isValid(data))
        {
            throw new IllegalArgumentException(String.format("invalid groupId [%s] or artifactId [%s]",data.getGroupId(),data.getArtifactId()));
        }
        Optional<StoreProjectData> projectData = find(data.getGroupId(), data.getArtifactId());
        if (projectData.isPresent() && (data.getId() == null || !data.getId().equals(projectData.get().getId())))
        {
            throw new StoreException(String.format("Duplicate coordinates: Different project %s its already registered with this coordinates %s-%s", projectData.get().getProjectId(), data.getGroupId(), data.getArtifactId()));
        }
    }

    @Override
    public List<StoreProjectData> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public List<StoreProjectData> getProjects(int page, int pageSize)
    {
        return getStoredEntitiesByPage(page, pageSize);
    }

    @Override
    public Optional<StoreProjectData> find(String groupId, String artifactId)
    {
        return findOne(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId)));
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getCollection().findOneAndDelete(getArtifactFilter(groupId, artifactId));
        return response;
    }
}

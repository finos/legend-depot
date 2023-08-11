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
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.project.ProjectValidator;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.StoreException;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.core.BaseMongo;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class ProjectsMongo extends BaseMongo<StoreProjectData> implements Projects, UpdateProjects
{

    public static final String COLLECTION = "project-configurations";
    public static final String PROJECT_ID = "projectId";

    @Inject
    public ProjectsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, StoreProjectData.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }


    public static List<IndexModel> buildIndexes()
    {
        return Arrays.asList(BaseMongo.buildIndex("groupId-artifactId", true, BaseMongo.GROUP_ID, BaseMongo.ARTIFACT_ID));
    }

    @Override
    protected Bson getKeyFilter(StoreProjectData data)
    {
        return Filters.and(Filters.eq(BaseMongo.GROUP_ID, data.getGroupId()),
                Filters.eq(BaseMongo.ARTIFACT_ID, data.getArtifactId()));
    }

    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(COLLECTION);
    }

    @Override
    protected void validateNewData(StoreProjectData data)
    {
        if (!ProjectValidator.isValid(data))
        {
            throw new IllegalArgumentException(String.format("invalid project [%s] or invalid groupId [%s] or artifactId [%s]",data.getProjectId(),data.getGroupId(),data.getArtifactId()));
        }
        Optional<StoreProjectData> projectData = find(data.getGroupId(), data.getArtifactId());
        if (projectData.isPresent() && (!data.getProjectId().equals(projectData.get().getProjectId())))
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
    public List<StoreProjectData> findByProjectId(String projectId)
    {
        return find(Filters.eq(PROJECT_ID, projectId));
    }

    @Override
    public Optional<StoreProjectData> find(String groupId, String artifactId)
    {
        return findOne(Filters.and(Filters.eq(BaseMongo.GROUP_ID, groupId), Filters.eq(BaseMongo.ARTIFACT_ID, artifactId)));
    }

    @Override
    public long delete(String groupId, String artifactId)
    {
        return delete(getArtifactFilter(groupId, artifactId));
    }
}

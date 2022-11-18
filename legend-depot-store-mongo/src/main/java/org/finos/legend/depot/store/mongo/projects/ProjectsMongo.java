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
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.EntityValidator;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.BaseMongo;
import org.finos.legend.depot.store.mongo.StoreException;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProjectsMongo extends BaseMongo<ProjectData> implements Projects, UpdateProjects
{

    public static final String MONGO_PROJECTS = "project-configurations";

    public static final String PROJECT_ID = "projectId";

    @Inject
    public ProjectsMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ProjectData.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return createIndexIfAbsent("groupId-artifactId", GROUP_ID, ARTIFACT_ID);
    }

    @Override
    protected Bson getKeyFilter(ProjectData data)
    {
        return and(eq(GROUP_ID, data.getGroupId()),
                eq(ARTIFACT_ID, data.getArtifactId()));
    }

    protected Bson getProjectIdFilter(String projectId)
    {
        return eq(PROJECT_ID, projectId);
    }


    @Override
    protected MongoCollection getCollection()
    {
        return getMongoCollection(MONGO_PROJECTS);
    }

    @Override
    protected void validateNewData(ProjectData data)
    {
        if (!EntityValidator.isValid(data))
        {
            throw new IllegalArgumentException(String.format("invalid groupId [%s] or artifactId [%s]",data.getGroupId(),data.getArtifactId()));
        }
        Optional<ProjectData> projectData = find(data.getGroupId(), data.getArtifactId());
        if (projectData.isPresent() && (data.getId() == null || !data.getId().equals(projectData.get().getId())))
        {
            throw new StoreException(String.format("Duplicate coordinates: Different project %s its already registered with this coordinates %s-%s", projectData.get().getProjectId(), data.getGroupId(), data.getArtifactId()));
        }
    }

    public List<ProjectData> findByProjectId(String projectId)
    {
        return find(getProjectIdFilter(projectId));
    }

    @Override
    public List<ProjectData> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public List<ProjectData> getProjects(int page, int pageSize)
    {
        return getStoredEntitiesByPage(page, pageSize);
    }

    @Override
    public Optional<ProjectData> find(String groupId, String artifactId)
    {
        return findOne(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId)));
    }


    @Override
    public List<String> getVersions(String groupId, String artifactId)
    {
        Optional<ProjectData> projectData = findOne(getArtifactFilter(groupId, artifactId));
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format("not found project for %s-%s", groupId, artifactId));
        }
        return projectData.get().getVersionsOrdered().stream().map(VersionId::toVersionIdString).collect(Collectors.toList());
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getCollection().findOneAndDelete(getArtifactFilter(groupId, artifactId));
        return response;
    }

    @Override
    public MetadataEventResponse deleteByProjectId(String projectId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getCollection().deleteMany(eq(PROJECT_ID, projectId));
        return response;
    }
}

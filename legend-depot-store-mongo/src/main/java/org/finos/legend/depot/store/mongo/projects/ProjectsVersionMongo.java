package org.finos.legend.depot.store.mongo.projects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.EntityValidator;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.store.api.projects.ProjectVersions;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.UpdateProjectVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.BaseVersionMongo;
import org.finos.legend.depot.store.mongo.StoreException;

import javax.inject.Named;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProjectsVersionMongo extends BaseVersionMongo<ProjectVersionData> implements ProjectVersions, UpdateProjectVersions
{

    public static final String MONGO_PROJECTS = "project-configurations";

    public static final String PROJECT_ID = "projectId";

    @Inject
    public ProjectsVersionMongo(@Named("mongoDatabase") MongoDatabase databaseProvider)
    {
        super(databaseProvider, ProjectVersionData.class, new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY));
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return createIndexIfAbsent("groupId-artifactId-versionId", GROUP_ID, ARTIFACT_ID, VERSION_ID);
    }

    /*@Override
    protected Bson getKeyFilter(ProjectVersionData data)
    {
        return and(eq(GROUP_ID, data.getGroupId()),
                eq(ARTIFACT_ID, data.getArtifactId()));
    }*/
    @Override
    protected Bson getKeyFilter(ProjectVersionData data)
    {
        return and(eq(GROUP_ID, data.getGroupId()),
                eq(ARTIFACT_ID, data.getArtifactId()),
                eq(VERSION_ID, data.getVersionId()));
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
    protected void validateNewData(ProjectVersionData data)
    {
        if (!EntityValidator.isValid(data))
        {
            throw new IllegalArgumentException(String.format("invalid groupId [%s] or artifactId [%s]",data.getGroupId(),data.getArtifactId()));
        }
        Optional<ProjectVersionData> projectVersionData = find(data.getGroupId(), data.getArtifactId(), data.getVersionId());
        if (projectVersionData.isPresent() && (data.getId() == null || !data.getId().equals(projectVersionData.get().getId())))
        {
            throw new StoreException(String.format("Duplicate coordinates: Different project %s its already registered with this coordinates %s-%s", projectVersionData.get().getProjectId(), data.getGroupId(), data.getArtifactId()));
        }
    }

    public List<ProjectVersionData> findByProjectId(String projectId)
    {
        return find(getProjectIdFilter(projectId));
    }

    @Override
    public List<ProjectVersionData> getAll()
    {
        return getAllStoredEntities();
    }

    @Override
    public List<ProjectVersionData> getProjects(int page, int pageSize)
    {
        return getStoredEntitiesByPage(page, pageSize);
    }

    @Override
    public Optional<ProjectVersionData> find(String groupId, String artifactId, String versionId)
    {
        return findOne(and(eq(GROUP_ID, groupId), eq(ARTIFACT_ID, artifactId), eq(VERSION_ID, versionId)));
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId, String versionId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getCollection().findOneAndDelete(getArtifactFilter(groupId, artifactId, versionId));
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

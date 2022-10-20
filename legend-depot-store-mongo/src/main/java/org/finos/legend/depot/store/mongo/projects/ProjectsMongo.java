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
import java.util.Arrays;
import java.util.Collections;
import org.bson.conversions.Bson;
import org.finos.legend.depot.domain.EntityValidator;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectDependencyInfo;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionConflict;
import org.finos.legend.depot.domain.project.ProjectVersionDependencies;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.BaseMongo;
import org.finos.legend.depot.store.mongo.StoreException;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProjectsMongo extends BaseMongo<ProjectData> implements Projects, UpdateProjects
{

    public static final String MONGO_PROJECTS = "project-configurations";

    public static final String PROJECT_ID = "projectId";

    public static final String PATH_DELIMITER = ">";

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
            throw new IllegalArgumentException("invalid project " + data.getProjectId());
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

    @Override
    public Optional<VersionId> getLatestVersion(String groupId, String artifactId)
    {
        Optional<ProjectData> projectData = findOne(getArtifactFilter(groupId, artifactId));
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format("not found project for %s-%s", groupId, artifactId));
        }
        return projectData.get().getLatestVersion();
    }


    @Override
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectVersions.forEach(pv ->
        {
            ProjectData projectData = getProject(pv.getGroupId(), pv.getArtifactId());
            List<ProjectVersionDependency> projectVersionDependencies = projectData.getDependencies(pv.getVersionId());
            if (transitive)
            {
                projectVersionDependencies.forEach(dep -> dependencies.addAll(getDependencies(dep.getDependency().getGroupId(), dep.getDependency().getArtifactId(), dep.getDependency().getVersionId(), true)));
            }
            projectVersionDependencies.forEach(dep -> dependencies.add(dep.getDependency()));
        });
        return dependencies;
    }


    public Set<ProjectVersionDependencies> getDependencyTree(List<ProjectVersion> projectVersions, String parentPath, Set<ProjectVersionDependencies> fullDependencies)
    {
        Set<ProjectVersionDependencies> rootTree = new HashSet<>();
        projectVersions.forEach(projectVersion ->
        {
            ProjectVersionDependencies projectVersionDependencyTree = new ProjectVersionDependencies(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId());
            fullDependencies.add(projectVersionDependencyTree);
            String fullPath = (parentPath == null ? "" : parentPath + PATH_DELIMITER) + projectVersionDependencyTree.getGav();
            projectVersionDependencyTree.setPath(fullPath);
            ProjectData projectData = getProject(projectVersion.getGroupId(), projectVersion.getArtifactId());
            List<ProjectVersionDependency> projectVersionDependencies = projectData.getDependencies(projectVersion.getVersionId());
            projectVersionDependencies.forEach(dep ->
                projectVersionDependencyTree.getDependencies().addAll(
                    getDependencyTree(Collections.singletonList(new ProjectVersion(dep.getDependency().getGroupId(), dep.getDependency().getArtifactId(), dep.getDependency().getVersionId())), fullPath, fullDependencies)
                )
            );
            rootTree.add(projectVersionDependencyTree);
        });
        return rootTree;
    }

    public ProjectDependencyInfo getProjectDependencyInfo(List<ProjectVersion> projectVersions)
    {
        Set<ProjectVersionDependencies> dependencyLine = new HashSet<>();
        Set<ProjectVersionDependencies> dependencyTree = getDependencyTree(projectVersions, null, dependencyLine);

        // Calculate conflicts
        // 1.collect dependency projects
        Set<ProjectVersionConflict> projectVersionConflicts = new HashSet<>();
        for (ProjectVersionDependencies dependency : dependencyLine)
        {
            projectVersionConflicts.add(new ProjectVersionConflict(dependency.getGroupId(), dependency.getArtifactId()));
        }
        // 2. add conflicts if more than one versions
        for (ProjectVersionConflict projectVersionConflict : projectVersionConflicts)
        {
            Set<String> versions = new HashSet<>();
            Set<ProjectVersionDependencies> correspondingDependencies = new HashSet<>();
            dependencyLine.forEach(dependency ->
            {
                if (dependency.getGroupId().equals(projectVersionConflict.getGroupId()) && dependency.getArtifactId().equals(projectVersionConflict.getArtifactId()))
                {
                    versions.add(dependency.getVersionId());
                    correspondingDependencies.add(dependency);
                }
            });
            // Initialize Conflicts if more than one person per project
            if (versions.size() > 1)
            {
                projectVersionConflict.initConflicts();
                projectVersionConflict.initVersions();
                correspondingDependencies.forEach(dependency ->
                {
                    projectVersionConflict.getConflictPaths().add(dependency.getPath());
                    projectVersionConflict.getVersions().add(dependency.getVersionId());
                });
            }
        }
        projectVersionConflicts.removeIf(s -> s.getConflictPaths() == null || s.getConflictPaths().isEmpty());
        return new ProjectDependencyInfo(dependencyTree, projectVersionConflicts);
    }



    @Override
    public List<ProjectVersionPlatformDependency> getDependentProjects(String groupId, String artifactId, String versionId)
    {
        if (versionId.equalsIgnoreCase("ALL"))
        {
            return getAll().stream().map(projectData -> projectData.getDependencies().stream()
                    .filter(dep -> dep.getDependency().getGroupId().equals(groupId) && dep.getDependency().getArtifactId().equals(artifactId))
                    .map(dep -> new ProjectVersionPlatformDependency(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), dep.getDependency(), projectData.getPropertiesForProjectVersionID(dep.getVersionId())))
                    .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return getAll().stream().map(projectData -> projectData.getDependencies().stream()
                .filter(dep -> dep.getDependency().getGroupId().equals(groupId) && dep.getDependency().getArtifactId().equals(artifactId) && dep.getDependency().getVersionId().equals(versionId))
                .map(dep -> new ProjectVersionPlatformDependency(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), dep.getDependency(), projectData.getPropertiesForProjectVersionID(dep.getVersionId())))
                .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private ProjectData getProject(String groupId, String artifactId)
    {
        Optional<ProjectData> projectData = findOne(getArtifactFilter(groupId, artifactId));
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format("project not found for %s-%s", groupId, artifactId));
        }
        return projectData.get();
    }

}

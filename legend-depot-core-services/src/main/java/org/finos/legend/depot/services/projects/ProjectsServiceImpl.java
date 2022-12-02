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

package org.finos.legend.depot.services.projects;

import java.util.HashMap;
import java.util.Map;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectDependencyInfo;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionConflict;
import org.finos.legend.depot.domain.project.ProjectVersionDependencies;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class ProjectsServiceImpl implements ManageProjectsService
{
    private static final String PATH_DELIMITER = ">";

    private final UpdateProjects projects;

    private final DependenciesCache dependenciesCache;

    @Inject
    public ProjectsServiceImpl(UpdateProjects projects, @Named("dependencyCache") DependenciesCache dependenciesCache)
    {
        this.projects = projects;
        this.dependenciesCache = dependenciesCache;
    }

    public ProjectsServiceImpl(UpdateProjects projects)
    {
        this.projects = projects;
        this.dependenciesCache = new DependenciesCache(projects);
    }

    @Override
    public ProjectData createOrUpdate(ProjectData projectData)
    {
        return projects.createOrUpdate(projectData);
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        return projects.delete(groupId, artifactId);
    }

    @Override
    public MetadataEventResponse delete(String projectId)
    {
        return projects.deleteByProjectId(projectId);
    }

    @Override
    public List<ProjectData> getAll()
    {
        return projects.getAll();
    }

    @Override
    public List<ProjectData> getProjects(int page, int pageSize)
    {
        return projects.getProjects(page, pageSize);
    }

    @Override
    public List<ProjectData> findByProjectId(String id)
    {
        return projects.findByProjectId(id);
    }

    @Override
    public List<String> getVersions(String groupId, String artifactId)
    {
        return projects.getVersions(groupId, artifactId);
    }

    @Override
    public Optional<ProjectData> find(String groupId, String artifactId)
    {
        return projects.find(groupId, artifactId);
    }

    @Override
    public boolean exists(String groupId, String artifactId, String versionId)
    {
        return this.find(groupId,artifactId).orElse(new ProjectData()).getVersions().contains(versionId);
    }

    @Override
    public void checkExists(String groupId, String artifactId) throws IllegalArgumentException
    {
        checkExists(groupId, artifactId,null);
    }

    @Override
    public void checkExists(String groupId, String artifactId, String versionId) throws IllegalArgumentException
    {
        Optional<ProjectData> projectData = this.projects.find(groupId,artifactId);
        if (projectData.isPresent())
        {
            if (versionId != null && !versionId.equals(MASTER_SNAPSHOT) && !projectData.get().getVersions().stream().anyMatch(v -> v.equals(versionId)))
            {
                throw new IllegalArgumentException(String.format("No version found for %s-%s-%s",groupId,artifactId,versionId));
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("No project found for %s-%s",groupId,artifactId));
        }
    }

    @Override
    public Optional<VersionId> getLatestVersion(String groupId, String artifactId)
    {
        return getProject(groupId,artifactId).getLatestVersion();
    }

    @Override
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectVersions.forEach(pv ->
        {
            ProjectData projectData = getProject(pv.getGroupId(), pv.getArtifactId());
            List<ProjectVersionDependency> projectVersionDependencies = projectData.getDependencies(pv.getVersionId());
            projectVersionDependencies.forEach(dep -> dependencies.add(dep.getDependency()));
            if (transitive && !projectVersionDependencies.isEmpty())
            {
                dependencies.addAll(dependenciesCache.getTransitiveDependencies(pv));
            }
        });
        return dependencies;
    }

    public Set<ProjectVersionDependencies> getDependencyTree(List<ProjectVersion> projectVersions, String parentPath, Set<ProjectVersionDependencies> fullDependencies, Map<String, ProjectData> projectDataMap)
    {
        Set<ProjectVersionDependencies> rootTree = new HashSet<>();
        projectVersions.forEach(projectVersion ->
        {
            ProjectVersionDependencies projectVersionDependencyTree = new ProjectVersionDependencies(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId());
            fullDependencies.add(projectVersionDependencyTree);
            String fullPath = (parentPath == null ? "" : parentPath + PATH_DELIMITER) + projectVersionDependencyTree.getGav();
            projectVersionDependencyTree.setPath(fullPath);
            String projectCoordinates = projectVersion.getGroupId() + projectVersion.getArtifactId();
            if (!projectDataMap.containsKey(projectCoordinates))
            {
                // only fetch project if we haven't fetched it already
                ProjectData project = getProject(projectVersion.getGroupId(), projectVersion.getArtifactId());
                projectDataMap.put(projectCoordinates, project);
            }
            ProjectData projectData = projectDataMap.get(projectCoordinates);
            List<ProjectVersionDependency> projectVersionDependencies = projectData.getDependencies(projectVersion.getVersionId());
            projectVersionDependencies.forEach(dep ->
                    projectVersionDependencyTree.getDependencies().addAll(
                            getDependencyTree(Collections.singletonList(new ProjectVersion(dep.getDependency().getGroupId(), dep.getDependency().getArtifactId(), dep.getDependency().getVersionId())), fullPath, fullDependencies, projectDataMap)
                    )
            );
            rootTree.add(projectVersionDependencyTree);
        });
        return rootTree;
    }

    public ProjectDependencyInfo getProjectDependencyInfo(List<ProjectVersion> projectVersions)
    {
        Set<ProjectVersionDependencies> dependencyLine = new HashSet<>();
        Map<String, ProjectData> projectDataMap = new HashMap<>();
        Set<ProjectVersionDependencies> dependencyTree = getDependencyTree(projectVersions, null, dependencyLine, projectDataMap);

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
        Optional<ProjectData> projectData = find(groupId, artifactId);
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format("project not found for %s-%s", groupId, artifactId));
        }
        return projectData.get();
    }

}

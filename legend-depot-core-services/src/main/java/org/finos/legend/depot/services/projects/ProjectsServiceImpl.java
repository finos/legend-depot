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

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectDependencyInfo;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.projects.UpdateProjects;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProjectsServiceImpl implements ManageProjectsService
{
    private final UpdateProjects projects;

    @Inject
    public ProjectsServiceImpl(UpdateProjects projects)
    {
        this.projects = projects;
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
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        return projects.getDependencies(projectVersions, transitive);
    }

    @Override
    public ProjectDependencyInfo getProjectDependencyInfo(List<ProjectVersion> projectVersions)
    {
        return projects.getProjectDependencyInfo(projectVersions);
    }

    @Override
    public List<ProjectVersionPlatformDependency> getDependentProjects(String groupId, String artifactId, String versionId)
    {
        return projects.getDependentProjects(groupId, artifactId, versionId);
    }

}

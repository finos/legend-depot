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

package org.finos.legend.depot.services.api.projects;

import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectDependencyInfo;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectsService
{
    List<ProjectData> getAll();

    /**
     * NOTE: page starting from 1
     */
    List<ProjectData> getProjects(int page, int pageSize);

    List<ProjectData> findByProjectId(String projectId);

    List<String> getVersions(String groupId, String artifactId);

    Optional<VersionId> getLatestVersion(String groupId, String artifactId);

    Optional<ProjectData> find(String groupId, String artifactId);

    default Set<ProjectVersion> getDependencies(String groupId, String artifactId, String versionId, boolean transitive)
    {
        return getDependencies(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), transitive);
    }

    Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive);

    ProjectDependencyInfo getProjectDependencyInfo(List<ProjectVersion> projectVersions);

    default ProjectDependencyInfo getProjectDependencyInfo(String groupId, String artifactId, String versionId)
    {
        return getProjectDependencyInfo(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)));
    }

    List<ProjectVersionPlatformDependency> getDependentProjects(String groupId, String artifactId, String versionId);
}

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

import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectsService
{
    List<StoreProjectData> getAllProjectCoordinates();

    /**
     * NOTE: page starting from 1
     */
    List<StoreProjectData> getProjects(int page, int pageSize);

    default List<String> getVersions(String groupId, String artifactId)
    {
        return getVersions(groupId, artifactId,false);
    }

    List<String> getVersions(String groupId, String artifactId,boolean includeSnapshots);

    Optional<VersionId> getLatestVersion(String groupId, String artifactId);

    @Deprecated
    List<StoreProjectData> findByProjectId(String projectId);

    List<StoreProjectVersionData> find(String groupId, String artifactId);

    List<StoreProjectVersionData> findVersion(Boolean excluded);

    Optional<StoreProjectVersionData> find(String groupId, String artifactId, String versionId);

    Optional<StoreProjectData> findCoordinates(String groupId, String artifactId);

    default Set<ProjectVersion> getDependencies(String groupId, String artifactId, String versionId, boolean transitive)
    {
        return getDependencies(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), transitive);
    }

    Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive);

    ProjectDependencyReport getProjectDependencyReport(List<ProjectVersion> projectVersions);

    default ProjectDependencyReport getProjectDependencyReport(String groupId, String artifactId, String versionId)
    {
        return getProjectDependencyReport(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)));
    }

    List<ProjectDependencyWithPlatformVersions> getDependentProjects(String groupId, String artifactId, String versionId);

    void checkExists(String groupId, String artifactId) throws IllegalArgumentException;

    void checkExists(String groupId, String artifactId,String versionId) throws IllegalArgumentException;
}

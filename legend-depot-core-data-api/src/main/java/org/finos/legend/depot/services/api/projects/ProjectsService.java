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

import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.DependencyExclusion;
import org.finos.legend.depot.services.api.dependencies.DependencyResponseModel;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ProjectsService
{
    List<StoreProjectData> getAllProjectCoordinates();

    List<StoreProjectVersionData> findByUpdatedDate(long updatedFrom, long updatedTo);

    default List<String> getVersions(String groupId, String artifactId)
    {
        return getVersions(groupId, artifactId,false);
    }

    List<String> getVersions(String groupId, String artifactId,boolean includeSnapshots);

    @Deprecated
    List<StoreProjectData> findByProjectId(String projectId);

    List<StoreProjectVersionData> find(String groupId, String artifactId);

    List<StoreProjectVersionData> findVersion(Boolean excluded);

    List<StoreProjectVersionData> findSnapshotVersions(String groupId, String artifactId);

    Optional<StoreProjectVersionData> find(String groupId, String artifactId, String versionId);

    String resolveAliasesAndCheckVersionExists(String groupId, String artifactId, String versionId);

    Optional<StoreProjectData> findCoordinates(String groupId, String artifactId);

    default Set<ProjectVersion> getDependencies(String groupId, String artifactId, String versionId, boolean transitive)
    {
        return getDependencies(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), new HashMap<>(), transitive);
    }

    default Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        return getDependencies(projectVersions, new HashMap<>(), transitive);
    }

    Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, Map<String, List<ProjectVersion>> exclusionsMap, boolean transitive);

    ProjectDependencyReport getProjectDependencyReportFromProjectVersionList(List<ProjectVersion> projectDependencyVersions);

    ProjectDependencyReport getProjectDependencyReport(List<ArtifactDependency> projectDependencyVersions);

    default ProjectDependencyReport getProjectDependencyReport(String groupId, String artifactId, String versionId)
    {
        return getProjectDependencyReport(Arrays.asList(new ArtifactDependency(groupId, artifactId, versionId)));
    }

    default List<ProjectDependencyWithPlatformVersions> getDependantProjects(String groupId, String artifactId, String versionId)
    {
        return getDependantProjects(groupId, artifactId, versionId, false);
    }

    List<ProjectDependencyWithPlatformVersions> getDependantProjects(String groupId, String artifactId, String versionId, boolean latestOnly);

    void checkExists(String groupId, String artifactId) throws IllegalArgumentException;

    DependencyResponseModel resolveCompatibleVersions(List<ProjectVersion> projectDependencyVersions, int backtrackVersions);
}

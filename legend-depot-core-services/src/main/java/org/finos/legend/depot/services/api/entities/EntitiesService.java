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

package org.finos.legend.depot.services.api.entities;

import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.Scope;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public interface EntitiesService
{

    List<Entity> getEntities(String groupId, String artifactId, String versionId, boolean versioned);

    Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String entityPath);

    List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages);

    default List<Entity> getLatestEntities(String groupId, String artifactId, boolean versioned)
    {
        return getEntities(groupId, artifactId, MASTER_SNAPSHOT, versioned);
    }

    default Optional<Entity> getLatestEntity(String groupId, String artifactId, String entityPath)
    {
        return getEntity(groupId, artifactId, MASTER_SNAPSHOT, entityPath);
    }

    default List<Entity> getLatestEntitiesByPackage(String groupId, String artifactId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages)
    {
        return getEntitiesByPackage(groupId, artifactId, MASTER_SNAPSHOT, packageName, versioned, classifierPaths, includeSubPackages);
    }

    List<ProjectVersionEntities> getDependenciesEntities(List<ProjectVersion> projectDependencies, boolean versioned, boolean transitive, boolean includeOrigin);

    default List<ProjectVersionEntities> getDependenciesEntities(String groupId, String artifactId, String versionId, boolean versioned, boolean transitive, boolean includeOrigin)
    {
        return getDependenciesEntities(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), versioned, transitive, includeOrigin);
    }

    default List<ProjectVersionEntities> getLatestDependenciesEntities(String groupId, String artifactId, boolean versioned, boolean transitive, boolean includeOrigin)
    {
        return getDependenciesEntities(groupId, artifactId, MASTER_SNAPSHOT, versioned, transitive, includeOrigin);
    }

    List<StoredEntity> findLatestEntitiesByClassifier(String classifier, String search, Integer limit, boolean summary, boolean versioned);

    List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, String search, List<ProjectVersion> projectVersions, Integer limit, boolean summary, boolean versioned);
}

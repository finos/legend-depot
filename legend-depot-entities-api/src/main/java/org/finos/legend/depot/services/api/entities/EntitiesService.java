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

import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.domain.model.project.Project;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;


public interface EntitiesService<T extends StoredEntity>
{

    List<Entity> getEntities(String groupId, String artifactId, String versionId);

    List<Entity> getEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier);

    Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String entityPath);

    List<Entity> getEntityFromDependencies(String groupId, String artifactId, String versionId, List<String> entityPaths, boolean includeOrigin);

    List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, Set<String> classifierPaths, boolean includeSubPackages);

    List<ProjectVersionEntities> getDependenciesEntities(List<ProjectVersion> projectDependencies, boolean transitive, boolean includeOrigin);

    List<ProjectVersionEntities> getDependenciesEntities(String classifier, boolean includeOrigin, List<ProjectVersion> originProjects, Supplier<Set<ProjectVersion>> dependencyCalculator);

    List<ProjectVersionEntities> getDependenciesEntitiesFromArtifactDependencies(List<ArtifactDependency> projectDependencies, boolean transitive, boolean includeOrigin);

    List<ProjectVersionEntities> getDependenciesEntitiesByClassifier(List<ProjectVersion> projectDependencies, String classifier, boolean transitive, boolean includeOrigin);

    default List<ProjectVersionEntities> getDependenciesEntities(String groupId, String artifactId, String versionId, boolean transitive, boolean includeOrigin)
    {
        return getDependenciesEntities(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), transitive, includeOrigin);
    }

    default List<ProjectVersionEntities> getDependenciesEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean transitive, boolean includeOrigin)
    {
        return getDependenciesEntitiesByClassifier(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), classifier, transitive, includeOrigin);
    }
}

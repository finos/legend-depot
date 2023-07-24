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
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface EntitiesService<T extends StoredEntity>
{

    List<Entity> getEntities(String groupId, String artifactId, String versionId);

    Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String entityPath);

    List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, Set<String> classifierPaths, boolean includeSubPackages);

    List<ProjectVersionEntities> getDependenciesEntities(List<ProjectVersion> projectDependencies, boolean transitive, boolean includeOrigin);

    default List<ProjectVersionEntities> getDependenciesEntities(String groupId, String artifactId, String versionId, boolean transitive, boolean includeOrigin)
    {
        return getDependenciesEntities(Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)), transitive, includeOrigin);
    }
}

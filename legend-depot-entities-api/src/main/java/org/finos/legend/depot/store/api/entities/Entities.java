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

package org.finos.legend.depot.store.api.entities;

import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.domain.entity.DepotEntityOverview;
import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Entities<T extends StoredEntity>
{
    List<Entity> getAllEntities(String groupId, String artifactId, String versionId);

    Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String path);

    List<Entity> getEntityFromDependencies(Set<ProjectVersion> dependencies, List<String> entityPaths);

    List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, Set<String> classifierPaths, boolean includeSubPackages);

    default List<DepotEntity> findEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier)
    {
        return findClassifierEntitiesByVersions(classifier, Arrays.asList(new ProjectVersion(groupId, artifactId, versionId)));
    }

    List<DepotEntity> findReleasedClassifierEntities(String classifier);

    List<DepotEntity> findLatestClassifierEntities(String classifier);

    List<DepotEntity> findClassifierEntitiesByVersions(String classifier, List<ProjectVersion> projectVersions);

    List<DepotEntityOverview> findReleasedClassifierSummaries(String classifier);

    List<DepotEntityOverview> findLatestClassifierSummaries(String classifier);

    List<DepotEntityOverview> findClassifierSummariesByVersions(String classifier, List<ProjectVersion> projectVersions);

    List<DepotEntity> findReleasedClassifierEntities(String classifier, String search, Integer limit);

    List<DepotEntity> findLatestClassifierEntities(String classifier, String search, Integer limit);

    List<DepotEntity> findClassifierEntitiesByVersions(String classifier, List<ProjectVersion> projectVersions, String search, Integer limit);

    List<T> getStoredEntities(String groupId, String artifactId, String versionId);

}

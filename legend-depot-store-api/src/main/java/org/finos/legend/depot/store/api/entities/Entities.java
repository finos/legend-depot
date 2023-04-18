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

import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Entities
{
    List<Entity> getAllEntities(String groupId, String artifactId, String versionId);

    List<Entity> getEntities(String groupId, String artifactId, String versionId, boolean versionedEntities);

    Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String path);

    List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages);

    long getVersionEntityCount();

    long getEntityCount(String groupId, String artifactId);

    long getVersionEntityCount(String groupId, String artifactId, String versionId);

    List<StoredEntity> getAllStoredEntities();

    List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, String search, List<ProjectVersion> projectVersions, Integer limit, boolean summary, boolean versionedEntities);

    List<StoredEntity> findLatestEntitiesByClassifier(String classifier, String search, Integer limit, boolean summary, boolean versioned);

    List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, boolean summary, boolean versionedEntities);

    List<StoredEntity> findLatestEntitiesByClassifier(String classifier, boolean summary, boolean versionedEntities);

    List<StoredEntity> findEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean summary, boolean versionedEntities);

    List<StoredEntity> getStoredEntities(String groupId, String artifactId, String versionId, boolean versioned);

}

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
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public interface Entities
{

    List<Entity> getAllEntities(String groupId, String artifactId, String versionId);

    default List<Entity> getAllLatestEntities(String groupId, String artifactId)
    {
        return getAllEntities(groupId, artifactId, MASTER_SNAPSHOT);
    }


    List<Entity> getEntities(String groupId, String artifactId, String versionId, boolean versionedEntities);

    default List<Entity> getLatestEntities(String groupId, String artifactId, boolean versionedEntities)
    {
        return getEntities(groupId, artifactId, MASTER_SNAPSHOT, versionedEntities);
    }

    Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String path);

    default Optional<Entity> getLatestEntity(String groupId, String artifactId, String entityPath)
    {
        return getEntity(groupId, artifactId, MASTER_SNAPSHOT, entityPath);
    }

    List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages);

    default List<Entity> getLatestEntitiesByPackage(String groupId, String artifactId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages)
    {
        return getEntitiesByPackage(groupId, artifactId, MASTER_SNAPSHOT, packageName, versioned, classifierPaths, includeSubPackages);
    }

    long getRevisionEntityCount();

    long getVersionEntityCount();

    long getVersionEntityCount(String groupId, String artifactId, String versionId);

    default long getRevisionEntityCount(String groupId, String artifactId)
    {
        return getVersionEntityCount(groupId, artifactId, MASTER_SNAPSHOT);
    }

    List<StoredEntity> getAllStoredEntities();

    List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, boolean summary, boolean versionedEntities);

    List<StoredEntity> findLatestEntitiesByClassifier(String classifier, boolean summary, boolean versioned);

    List<StoredEntity> findEntitiesByClassifier(String groupId, String artifactId, String versionId, String classifier, boolean summary, boolean versionedEntities);


}

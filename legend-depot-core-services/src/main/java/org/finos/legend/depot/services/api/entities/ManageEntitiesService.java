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

import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public interface ManageEntitiesService extends EntitiesService
{

    List<StoredEntity> getStoredEntities(String groupId, String artifactId);

    List<StoredEntity> getStoredEntities(String groupId, String artifactId, String versionId);

    MetadataEventResponse deleteAll(String groupId, String artifactId);

    MetadataEventResponse delete(String groupId, String artifactId, String versionId,boolean versioned);

    MetadataEventResponse createOrUpdate(List<StoredEntity> versionedEntities);

    List<Pair<String, String>> getOrphanedStoredEntities();
}
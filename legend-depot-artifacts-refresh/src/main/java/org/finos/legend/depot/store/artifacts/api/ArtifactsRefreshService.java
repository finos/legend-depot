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

package org.finos.legend.depot.store.artifacts.api;

import org.finos.legend.depot.domain.api.MetadataEventResponse;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public interface ArtifactsRefreshService
{
    MetadataEventResponse refreshMasterSnapshotForAllProjects(boolean fullUpdate, boolean transitive,String parentEventId);

    default MetadataEventResponse refreshMasterSnapshotForProject(String groupId, String artifactId, boolean fullUpdate, boolean transitive,String parentEventId)
    {
        return refreshVersionForProject(groupId,artifactId,MASTER_SNAPSHOT,fullUpdate,transitive,parentEventId);
    }

    MetadataEventResponse refreshVersionForProject(String groupId, String artifactId, String versionId, boolean fullUpdate, boolean transitive, String parentEventId);

    MetadataEventResponse refreshAllVersionsForProject(String groupId, String artifactId, boolean fullUpdate,boolean transitive, String parentEventId);

    MetadataEventResponse refreshAllVersionsForAllProjects(boolean fullUpdate,boolean transitive, String parentEventId);

    MetadataEventResponse refreshProjectsWithMissingVersions(boolean fullUpdate,boolean transitive, String parentEventId);

    MetadataEventResponse retireLeastRecentlyUsedVersions(int numberOfDays);

    MetadataEventResponse retireOldProjectVersions(int versionsToKeep);

    void delete(String groupId, String artifactId, String versionId);

    boolean createIndexesIfAbsent();
  }

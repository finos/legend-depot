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

package org.finos.legend.depot.services.api.artifacts.purge;

import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;

import java.util.List;

public interface ArtifactsPurgeService
{
    MetadataNotificationResponse evictLeastRecentlyUsed(int ttlForVersionsInDays, int ttlForSnapshotsInDays);

    MetadataNotificationResponse evictVersionsNotUsed();

    MetadataNotificationResponse evictOldestProjectVersions(String groupId, String artifactId, int versionsToKeep);

    void evict(String groupId, String artifactId, String versionId);

    void delete(String groupId, String artifactId, String versionId);

    String deleteSnapshotVersions(String groupId, String artifactId, List<String> versions);

    MetadataNotificationResponse deprecate(String groupId, String artifactId, String version);

    MetadataNotificationResponse deprecateVersionsNotInRepository();

  }

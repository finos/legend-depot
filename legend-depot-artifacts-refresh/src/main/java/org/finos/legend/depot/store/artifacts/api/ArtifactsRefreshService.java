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
import org.finos.legend.depot.store.artifacts.domain.status.VersionMismatch;

import java.util.List;

public interface ArtifactsRefreshService
{

    MetadataEventResponse refreshProjectRevisionArtifacts(String groupId, String artifactId);

    MetadataEventResponse refreshAllProjectRevisionsArtifacts();

    MetadataEventResponse refreshAllProjectsVersionsArtifacts(boolean fullUpdate);

    MetadataEventResponse refreshProjectVersionArtifacts(String groupId, String artifactId, String versionId, boolean fullUpdate);

    MetadataEventResponse refreshProjectVersionsArtifacts(String groupId, String artifactId, boolean fullUpdate);

    default MetadataEventResponse refreshAllProjectsVersionsArtifacts()
    {
        return refreshAllProjectsVersionsArtifacts(false);
    }

    default MetadataEventResponse refreshProjectVersionsArtifacts(String groupId, String artifactId)
    {
        return refreshProjectVersionsArtifacts(groupId, artifactId, false);
    }

    default MetadataEventResponse refreshProjectVersionArtifacts(String groupId, String artifactId, String versionId)
    {
        return refreshProjectVersionArtifacts(groupId, artifactId, versionId, false);
    }

    MetadataEventResponse retireLeastRecentlyUsedVersions(int numberOfDays);

    MetadataEventResponse retireOldProjectVersions(int versionsToKeep);

    void delete(String groupId, String artifactId, String versionId);

    boolean createIndexesIfAbsent();

    List<String> getRepositoryVersions(String groupId, String artifactId);

    MetadataEventResponse refreshAllProjectArtifacts(String groupId, String artifactId);

    List<VersionMismatch> findVersionsMismatches();

    MetadataEventResponse refreshProjectsVersionMismatches();
}

//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.services.api.generation.artifact;

import java.util.List;
import java.util.Optional;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;


public interface ArtifactGenerationsService
{
    List<ArtifactGeneration> getArtifactGenerations(String groupId, String artifactId, String versionId);

    default List<ArtifactGeneration> getLatestArtifactGenerations(String groupId, String artifactId)
    {
        return getArtifactGenerations(groupId, artifactId, MASTER_SNAPSHOT);
    }

    Optional<ArtifactGeneration> getArtifactGenerationByPath(String groupId, String artifactId, String versionId, String path);

    default Optional<ArtifactGeneration> getLatestArtifactGenerationByPath(String groupId, String artifactId, String path)
    {
        return getArtifactGenerationByPath(groupId, artifactId, MASTER_SNAPSHOT, path);
    }

    default Optional<String> getArtifactGenerationContentByPath(String groupId, String artifactId, String versionId, String path)
    {
        return getArtifactGenerationByPath(groupId, artifactId, versionId, path).map(o -> o.getContent());
    }

    default Optional<String> getLatestArtifactGenerationContentByPath(String groupId, String artifactId, String path)
    {
        return getArtifactGenerationByPath(groupId, artifactId, MASTER_SNAPSHOT, path).map(o -> o.getContent());
    }

    List<ArtifactGeneration> getArtifactsGenerationsByGenerator(String groupId, String artifactId, String versionId, String entityPath);

    default List<ArtifactGeneration> getLatestArtifactsGenerationsByGenerator(String groupId, String artifactId, String entityPath)
    {
        return getArtifactsGenerationsByGenerator(groupId, artifactId, MASTER_SNAPSHOT, entityPath);
    }

}

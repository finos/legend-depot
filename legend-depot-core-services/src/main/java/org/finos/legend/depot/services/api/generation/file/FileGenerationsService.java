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

package org.finos.legend.depot.services.api.generation.file;

import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public interface FileGenerationsService
{

    default List<Entity> getLatestGenerations(String groupId, String artifactId)
    {
        return getGenerations(groupId, artifactId, MASTER_SNAPSHOT);
    }

    List<Entity> getGenerations(String groupId, String artifactId, String versionId);

    default List<FileGeneration> getLatestFileGenerations(String groupId, String artifactId)
    {
        return getFileGenerations(groupId, artifactId, MASTER_SNAPSHOT);
    }

    List<FileGeneration> getFileGenerations(String groupId, String artifactId, String versionId);

    default List<FileGeneration> getLatestFileGenerationsByElementPath(String groupId, String artifactId, String elementPath)
    {
        return getFileGenerationsByElementPath(groupId, artifactId, MASTER_SNAPSHOT, elementPath);
    }

    List<FileGeneration> getFileGenerationsByElementPath(String groupId, String artifactId, String versionId, String elementPath);

    default Optional<FileGeneration> getLatestFileGenerationsByFilePath(String groupId, String artifactId, String filePath)
    {
        return getFileGenerationsByFilePath(groupId, artifactId, MASTER_SNAPSHOT, filePath);
    }

    Optional<FileGeneration> getFileGenerationsByFilePath(String groupId, String artifactId, String versionsId, String filePath);

    default  Optional<String> getFileGenerationContentByFilePath(String groupId, String artifactId, String versionsId, String filePath)
    {
        return  getFileGenerationsByFilePath(groupId,artifactId,versionsId,filePath).map(o -> o.getContent());
    }

    default Optional<String> getLatestFileGenerationContentByFilePath(String groupId, String artifactId, String filePath)
    {
        return getFileGenerationContentByFilePath(groupId, artifactId, MASTER_SNAPSHOT, filePath);
    }

}

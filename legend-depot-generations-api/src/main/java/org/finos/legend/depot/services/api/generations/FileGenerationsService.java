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

package org.finos.legend.depot.services.api.generations;

import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.List;
import java.util.Optional;


public interface FileGenerationsService
{

    List<Entity> getGenerations(String groupId, String artifactId, String versionId);

    List<DepotGeneration> getFileGenerations(String groupId, String artifactId, String versionId);

    List<DepotGeneration> getFileGenerationsByElementPath(String groupId, String artifactId, String versionId, String elementPath);

    Optional<DepotGeneration> getFileGenerationsByFilePath(String groupId, String artifactId, String versionsId, String filePath);

    List<StoredFileGeneration> findByType(String groupId, String artifactId, String versionId, String type);

    default  Optional<String> getFileGenerationContentByFilePath(String groupId, String artifactId, String versionsId, String filePath)
    {
        return  getFileGenerationsByFilePath(groupId,artifactId,versionsId,filePath).map(o -> o.getContent());
    }

}

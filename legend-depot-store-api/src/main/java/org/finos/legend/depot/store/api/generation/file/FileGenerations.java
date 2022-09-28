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

package org.finos.legend.depot.store.api.generation.file;

import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;

import java.util.List;
import java.util.Optional;

public interface FileGenerations
{
    List<StoredFileGeneration> getAll();

    Optional<StoredFileGeneration> get(String groupId, String artifactId, String versionId, String generationFilePath);

    List<StoredFileGeneration> find(String groupId, String artifactId, String versionId);

    List<StoredFileGeneration> findByType(String groupId, String artifactId,String versionId, String type);

    List<StoredFileGeneration> findByElementPath(String groupId, String artifactId, String versionId, String generationPath);

    Optional<StoredFileGeneration> findByFilePath(String groupId, String artifactId, String versionId, String filePath);

}

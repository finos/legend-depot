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

package org.finos.legend.depot.store.api.generation.artifact;

import java.util.List;
import java.util.Optional;
import org.finos.legend.depot.domain.generation.artifact.StoredArtifactGeneration;

public interface ArtifactGenerations
{

    List<StoredArtifactGeneration> getAll();

    Optional<StoredArtifactGeneration> get(String groupId, String artifactId, String versionId, String filePath);

    List<StoredArtifactGeneration> find(String groupId, String artifactId, String versionId);

    List<StoredArtifactGeneration> findByGenerator(String groupId, String artifactId, String versionId, String entityPath);

    // TODO: We may want to add find by folder path

}

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

package org.finos.legend.depot.store.artifacts.services.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;
import org.finos.legend.depot.store.artifacts.api.generation.artifact.ArtifactGenerationProvider;

public class ArtifactGenerationProviderImpl implements ArtifactGenerationProvider
{

    public static final ArtifactType FILE_GENERATION = ArtifactType.FILE_GENERATIONS;

    @Override
    public ArtifactType getType()
    {
        return FILE_GENERATION;
    }

    @Override
    public List<ArtifactGeneration> loadArtifacts(List<File> files)
    {
        List<ArtifactGeneration> generations = new ArrayList<>();


        return generations;
    }
}

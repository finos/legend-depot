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

package org.finos.legend.depot.services.artifacts.handlers.generations;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.domain.notifications.RestCuratedArtifacts;
import org.finos.legend.depot.services.generations.loader.FileGenerationLoader;
import org.finos.legend.depot.services.api.artifacts.handlers.ArtifactLoadingException;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class FileGenerationsProvider implements FileGenerationsArtifactsProvider
{
    @Inject
    public FileGenerationsProvider()
    {
        super();
    }

    @Override
    public List<DepotGeneration> extractArtifactsForType(Stream<File> files)
    {
        List<DepotGeneration> generations = new ArrayList<>();
        files.forEach(f ->
        {
            try (FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(f))
            {
                List<DepotGeneration> fileGenerations = loader.getAllFileGenerations().collect(Collectors.toList());
                generations.addAll(fileGenerations);
            }
            catch (Exception e)
            {
                throw new ArtifactLoadingException(e.getMessage());
            }
        });
        return generations;
    }

    @Override
    public List<DepotGeneration> extractRestArtifactsForType(RestCuratedArtifacts elements)
    {
        return elements.getArtifacts().stream()
                .map(artifact -> new DepotGeneration(artifact.path, artifact.content)).collect(Collectors.toList());
    }

    @Override
    public boolean matchesArtifactType(File file)
    {
        return file.getName().contains(ArtifactType.FILE_GENERATIONS.getModuleName());
    }

    @Override
    public ArtifactType getType()
    {
        return ArtifactType.FILE_GENERATIONS;
    }

}

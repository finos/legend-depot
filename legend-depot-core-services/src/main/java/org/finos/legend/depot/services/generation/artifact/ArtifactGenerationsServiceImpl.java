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

package org.finos.legend.depot.services.generation.artifact;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;
import org.finos.legend.depot.domain.generation.artifact.StoredArtifactGeneration;
import org.finos.legend.depot.services.api.generation.artifact.ArtifactGenerationsService;
import org.finos.legend.depot.services.api.generation.artifact.ManageArtifactGenerationsService;
import org.finos.legend.depot.store.api.generation.artifact.UpdateArtifactGenerations;

public class ArtifactGenerationsServiceImpl implements ArtifactGenerationsService, ManageArtifactGenerationsService
{

    private final UpdateArtifactGenerations artifactGenerationsUpdater;

    @Inject
    public ArtifactGenerationsServiceImpl(UpdateArtifactGenerations artifactGenerations)
    {
        this.artifactGenerationsUpdater = artifactGenerations;
    }

    @Override
    public List<StoredArtifactGeneration> getAll()
    {
        return artifactGenerationsUpdater.getAll();
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        artifactGenerationsUpdater.delete(groupId, artifactId, versionId);
    }

    @Override
    public void createOrUpdate(StoredArtifactGeneration artifactGeneration)
    {
        artifactGenerationsUpdater.createOrUpdate(artifactGeneration);
    }

    @Override
    public List<ArtifactGeneration> getArtifactGenerations(String groupId, String artifactId, String versionId)
    {
        return artifactGenerationsUpdater.find(groupId, artifactId, versionId).stream().map(StoredArtifactGeneration::getArtifact).collect(Collectors.toList());
    }

    @Override
    public List<ArtifactGeneration> getArtifactsGenerationsByGenerator(String groupId, String artifactId, String versionId, String generatorPath)
    {
        return artifactGenerationsUpdater.findByGenerator(groupId, artifactId, versionId, generatorPath).stream().map(StoredArtifactGeneration::getArtifact).collect(Collectors.toList());
    }

    @Override
    public Optional<ArtifactGeneration> getArtifactGenerationByPath(String groupId, String artifactId, String versionId, String path)
    {
        return artifactGenerationsUpdater.get(groupId, artifactId, versionId, path).map(StoredArtifactGeneration::getArtifact);
    }


}

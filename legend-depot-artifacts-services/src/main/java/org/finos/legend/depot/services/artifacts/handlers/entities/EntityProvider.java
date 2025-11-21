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

package org.finos.legend.depot.services.artifacts.handlers.entities;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.notifications.RestCuratedArtifacts;
import org.finos.legend.depot.services.api.artifacts.handlers.ArtifactLoadingException;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.serialization.EntityLoader;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Singleton
public class EntityProvider implements EntityArtifactsProvider
{
    @Inject
    public EntityProvider()
    {
        super();
    }

    @Override
    public ArtifactType getType()
    {
        return ArtifactType.ENTITIES;
    }

    @Override
    public List<Entity> extractArtifactsForType(Stream<File> files)
    {
        List<Entity> entities = new ArrayList<>();
        files.forEach(f ->
        {
            try (EntityLoader loader = EntityLoader.newEntityLoader(f))
            {
                List<Entity> loadedEntities = loader.getAllEntities().collect(Collectors.toList());
                entities.addAll(loadedEntities);
            }
            catch (Exception e)
            {
                throw new ArtifactLoadingException(e.getMessage());
            }
        });
        return entities;
    }

    @Override
    public List<Entity> extractRestArtifactsForType(RestCuratedArtifacts elements)
    {
        return elements.getEntityDefinitions().stream().map(entity -> (Entity) entity).collect(Collectors.toList());
    }

    @Override
    public boolean matchesArtifactType(File file)
    {
        return file.getName().contains(getType().getModuleName()) && !file.getName().contains(ArtifactType.VERSIONED_ENTITIES.getModuleName());
    }
}
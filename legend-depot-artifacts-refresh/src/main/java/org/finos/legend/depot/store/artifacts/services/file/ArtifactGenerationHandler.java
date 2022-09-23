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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;
import org.finos.legend.depot.domain.generation.artifact.StoredArtifactGeneration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.generation.artifact.ManageArtifactGenerationsService;
import org.finos.legend.depot.store.artifacts.api.generation.artifact.ArtifactGenerationsVersionArtifactsHandler;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.serialization.EntityLoader;
import org.slf4j.Logger;

@Singleton
public class ArtifactGenerationHandler implements ArtifactGenerationsVersionArtifactsHandler
{

    public static final String VERSIONED_ENTITIES = "versioned-entities";
    public static final String PURE_PACKAGE_SEPARATOR = "::";
    public static final String PATH = "/";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactGenerationHandler.class);
    protected final ManageArtifactGenerationsService generations;
    private final ArtifactRepository repository;

    @Inject
    public ArtifactGenerationHandler(ArtifactRepository repository, ManageArtifactGenerationsService artifactGenerationService)
    {
        this.repository = repository;
        this.generations = artifactGenerationService;
    }


    @Override
    public MetadataEventResponse refreshProjectVersionArtifacts(ProjectData project, String versionId, List<ArtifactGeneration> artifactGenerations)
    {
        MetadataEventResponse response = new MetadataEventResponse();

        Map<String, Entity> entities = findEntitiesByPath(project.getGroupId(), project.getArtifactId(), versionId);
        Set<String> entityPaths = entities.keySet();
        String message = String.format(" %s version %s found %s artifact generations", project.getProjectId(), versionId, artifactGenerations.size());
        LOGGER.info(message);
        response.addMessage(message);
        artifactGenerations.forEach(generation ->
        {
            String generator = null;
            Optional<String> entityKey = entityPaths.stream().filter(s -> generation.getPath().startsWith(PATH + s)).findFirst();
            if (entityKey.isPresent())
            {
                generator = entities.get(entityKey.get()).getPath();
            }
            StoredArtifactGeneration storedArtifactGeneration = new StoredArtifactGeneration(project.getGroupId(), project.getArtifactId(), versionId, generator, generation);
            this.generations.createOrUpdate(storedArtifactGeneration);
        });
        return response;
    }

    private Map<String, Entity> findEntitiesByPath(String groupId, String artifactId, String versionId)
    {
        Map<String, Entity> entityMap = new HashMap<>();
        Optional<File> entitiesFiles = repository.findFiles(ArtifactType.ENTITIES, groupId, artifactId, versionId).stream().filter(file -> isEntitiesArtifactFile(versionId, file)).findFirst();
        entitiesFiles.map(file -> EntityLoader.newEntityLoader(file).getAllEntities().collect(Collectors.toList())).orElse(Collections.emptyList())
            .forEach(entity -> entityMap.put(entity.getPath().replace(PURE_PACKAGE_SEPARATOR, PATH), entity));
        return entityMap;
    }

    private boolean isEntitiesArtifactFile(String versionId, File file)
    {
        String entitiesFileName = "entities-" + versionId;
        return file.getName().contains(entitiesFileName) && !file.getName().contains(VERSIONED_ENTITIES);
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        this.generations.delete(groupId, artifactId, versionId);
    }


}

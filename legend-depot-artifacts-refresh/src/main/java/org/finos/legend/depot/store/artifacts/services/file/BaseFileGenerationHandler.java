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

package org.finos.legend.depot.store.artifacts.services.file;

import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.serialization.EntityLoader;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.generation.file.FileGeneration.GENERATION_CONFIGURATION;

public abstract class BaseFileGenerationHandler
{


    public static final String TYPE = "type";
    public static final String PATH = "/";
    public static final String GENERATION_OUTPUT_PATH = "generationOutputPath";
    public static final String PURE_PACKAGE_SEPARATOR = "::";
    public static final String UNDERSCORE = "_";
    public static final String VERSIONED_ENTITIES = "versioned-entities";
    public static final String BLANK = "";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BaseFileGenerationHandler.class);
    protected final ManageFileGenerationsService generations;
    private final FileGenerationsProvider provider;
    private final ArtifactRepository repository;


    protected BaseFileGenerationHandler(ArtifactRepository repository, FileGenerationsProvider provider, ManageFileGenerationsService generations)
    {
        this.repository = repository;
        this.provider = provider;
        this.generations = generations;
    }

    public MetadataEventResponse refreshProjectVersionArtifacts(ProjectData project, String versionId, List<File> files)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        List<Entity> fileGenerationEntities = findFileGenerationEntities(project.getGroupId(), project.getArtifactId(), versionId);
        if (fileGenerationEntities.isEmpty())
        {
            response.addMessage(String.format("%s no generations found for version %s", project.getProjectId(), versionId));
            return response;
        }
        List<FileGeneration> gens = provider.loadArtifacts(files);
        String message = String.format(" %s version %s found %s generations", project.getProjectId(), versionId, gens.size());
        LOGGER.info(message);
        response.addMessage(message);

        fileGenerationEntities.forEach(entity ->
        {
            String generationPath = (String)entity.getContent().get(GENERATION_OUTPUT_PATH);
            String path = PATH + (generationPath != null ? generationPath : entity.getPath().replace(PURE_PACKAGE_SEPARATOR, UNDERSCORE));
            String type = (String)entity.getContent().get(TYPE);

            gens.stream().filter(gen -> gen.getPath().startsWith(path)).forEach(gen ->
            {
                FileGeneration generation = new FileGeneration(gen.getPath().replace(path, BLANK), gen.getContent());
                generations.createOrUpdate(new StoredFileGeneration(project.getGroupId(), project.getArtifactId(), versionId, entity.getPath(), type, generation));
            });
        });
        return response;
    }


    private List<Entity> findFileGenerationEntities(String groupId, String artifactId, String versionId)
    {
        List<File> files = repository.findFiles(ArtifactType.ENTITIES, groupId, artifactId, versionId);
        Optional<File> entitiesFiles = files.stream().filter(file -> isEntitiesArtifactFile(versionId, file)).findFirst();
        return entitiesFiles.map(file -> EntityLoader.newEntityLoader(file).getAllEntities()
                .filter(en -> en.getClassifierPath().equalsIgnoreCase(GENERATION_CONFIGURATION)).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private boolean isEntitiesArtifactFile(String versionId, File file)
    {
        String entitiesFileName = "entities-" + versionId;
        return file.getName().contains(entitiesFileName) && !file.getName().contains(VERSIONED_ENTITIES);
    }
}

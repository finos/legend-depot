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

package org.finos.legend.depot.services.generation.file;

import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.services.api.generation.file.FileGenerationsService;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.generation.file.FileGeneration.GENERATION_CONFIGURATION;

public class FileGenerationsServiceImpl implements FileGenerationsService, ManageFileGenerationsService
{

    private final UpdateFileGenerations fileGenerations;
    private final Entities entities;


    @Inject
    public FileGenerationsServiceImpl(UpdateFileGenerations fileGenerations, Entities entities)
    {
        this.fileGenerations = fileGenerations;
        this.entities = entities;
    }

    @Override
    public List<StoredFileGeneration> getAll()
    {
        return fileGenerations.getAll();
    }

    @Override
    public void createOrUpdate(StoredFileGeneration storedFileGeneration)
    {
        fileGenerations.createOrUpdate(storedFileGeneration);
    }

    @Override
    public List<Entity> getGenerations(String groupId, String artifactId, String versionId)
    {
        List<StoredEntity> storedEntities = entities.findEntitiesByClassifier(groupId, artifactId, versionId, GENERATION_CONFIGURATION, false, false);
        return storedEntities.stream().map(StoredEntity::getEntity).collect(Collectors.toList());
    }

    @Override
    public List<FileGeneration> getFileGenerations(String groupId, String artifactId, String versionId)
    {

        return fileGenerations.find(groupId, artifactId, versionId).stream().map(StoredFileGeneration::getFile).collect(Collectors.toList());
    }

    @Override
    public List<FileGeneration> getFileGenerationsByElementPath(String groupId, String artifactId, String versionId, String elementPath)
    {
        return fileGenerations.findByElementPath(groupId, artifactId, versionId, elementPath).stream().map(StoredFileGeneration::getFile).collect(Collectors.toList());
    }

    @Override
    public Optional<FileGeneration> getFileGenerationsByFilePath(String groupId, String artifactId, String versionId, String filePath)
    {

        Optional<StoredFileGeneration> found = fileGenerations.findByFilePath(groupId, artifactId, versionId, filePath);
        return found.map(StoredFileGeneration::getFile);
    }

    @Override
    public List<StoredFileGeneration> getStoredFileGenerations(String groupId, String artifactId, String versionId)
    {
        return fileGenerations.find(groupId, artifactId, versionId);
    }

    @Override
    public List<StoredFileGeneration> findByType(String groupId, String artifactId, String versionId, String type)
    {
        return fileGenerations.findByType(groupId, artifactId, versionId, type);
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        fileGenerations.delete(groupId, artifactId, versionId);
    }
}

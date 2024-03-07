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

package org.finos.legend.depot.services.generations.impl;

import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.api.generations.FileGenerationsService;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.api.generations.FileGenerations;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.generation.DepotGeneration.GENERATION_CONFIGURATION;


public class FileGenerationsServiceImpl implements FileGenerationsService
{

    private final FileGenerations fileGenerations;
    private final Entities entities;
    protected final ProjectsService projects;


    @Inject
    public FileGenerationsServiceImpl(FileGenerations fileGenerations, Entities entities, ProjectsService projectsService)
    {
        this.fileGenerations = fileGenerations;
        this.entities = entities;
        this.projects = projectsService;
    }


    @Override
    public List<Entity> getGenerations(String groupId, String artifactId, String versionId)
    {
        String version = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        return ((List<DepotEntity>)entities.findEntitiesByClassifier(groupId, artifactId, version, GENERATION_CONFIGURATION)).stream()
                .map(depotEntity -> depotEntity.getEntity()).collect(Collectors.toList());
    }

    @Override
    public List<DepotGeneration> getFileGenerations(String groupId, String artifactId, String versionId)
    {
        String version = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        return fileGenerations.find(groupId, artifactId, version).stream().map(StoredFileGeneration::getFile).collect(Collectors.toList());
    }

    @Override
    public List<DepotGeneration> getFileGenerationsByElementPath(String groupId, String artifactId, String versionId, String elementPath)
    {
        String version = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        return fileGenerations.findByElementPath(groupId, artifactId, version, elementPath).stream().map(StoredFileGeneration::getFile).collect(Collectors.toList());
    }

    @Override
    public Optional<DepotGeneration> getFileGenerationsByFilePath(String groupId, String artifactId, String versionId, String filePath)
    {
        String version = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        Optional<StoredFileGeneration> found = fileGenerations.findByFilePath(groupId, artifactId, version, filePath);
        return found.map(StoredFileGeneration::getFile);
    }

    @Override
    public List<StoredFileGeneration> findByType(String groupId, String artifactId, String versionId, String type)
    {
        versionId = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        return fileGenerations.findByType(groupId, artifactId, versionId, type);
    }
}

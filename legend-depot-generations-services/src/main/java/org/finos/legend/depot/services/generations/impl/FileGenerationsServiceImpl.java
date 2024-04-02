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

import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.api.generations.FileGenerationsService;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.api.generations.FileGenerations;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileGenerationsServiceImpl implements FileGenerationsService
{

    private final FileGenerations fileGenerations;
    protected final ProjectsService projects;


    @Inject
    public FileGenerationsServiceImpl(FileGenerations fileGenerations, ProjectsService projectsService)
    {
        this.fileGenerations = fileGenerations;
        this.projects = projectsService;
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

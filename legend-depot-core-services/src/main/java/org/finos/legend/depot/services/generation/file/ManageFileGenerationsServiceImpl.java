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

import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;

import javax.inject.Inject;
import java.util.List;

public class ManageFileGenerationsServiceImpl extends  FileGenerationsServiceImpl implements ManageFileGenerationsService
{

    private final UpdateFileGenerations fileGenerations;


    @Inject
    public ManageFileGenerationsServiceImpl(UpdateFileGenerations fileGenerations, Entities entities, ProjectsService projectsService)
    {
        super(fileGenerations, entities, projectsService);
        this.fileGenerations = fileGenerations;
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
    public List<StoredFileGeneration> getStoredFileGenerations(String groupId, String artifactId, String versionId)
    {
        versionId = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        return fileGenerations.find(groupId, artifactId, versionId);
    }

    @Override
    public List<StoredFileGeneration> findByType(String groupId, String artifactId, String versionId, String type)
    {
        versionId = this.projects.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        return fileGenerations.findByType(groupId, artifactId, versionId, type);
    }

    @Override
    public long delete(String groupId, String artifactId, String versionId)
    {
        this.projects.checkExists(groupId, artifactId);
        return fileGenerations.delete(groupId, artifactId, versionId);
    }
}

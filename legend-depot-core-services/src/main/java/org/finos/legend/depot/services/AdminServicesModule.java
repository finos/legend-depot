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

package org.finos.legend.depot.services;

import com.google.inject.PrivateModule;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.generation.file.FileGenerationsService;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.generation.file.FileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;

public class AdminServicesModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(Projects.class).to(ProjectsMongo.class);
        bind(UpdateProjects.class).to(ProjectsMongo.class);
        bind(Entities.class).to(EntitiesMongo.class);
        bind(UpdateEntities.class).to(EntitiesMongo.class);
        bind(UpdateFileGenerations.class).to(FileGenerationsMongo.class);

        bind(ManageProjectsService.class).to(ProjectsServiceImpl.class);
        bind(ManageEntitiesService.class).to(EntitiesServiceImpl.class);
        bind(FileGenerationsService.class).to(FileGenerationsServiceImpl.class);
        bind(ManageFileGenerationsService.class).to(FileGenerationsServiceImpl.class);

        expose(ManageProjectsService.class);
        expose(ManageEntitiesService.class);
        expose(ManageFileGenerationsService.class);

        expose(UpdateEntities.class);
        expose(Entities.class);
        expose(UpdateFileGenerations.class);
        expose(Projects.class);
        expose(UpdateProjects.class);
    }
}

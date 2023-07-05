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

package org.finos.legend.depot.store.mongo;

import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.api.versionedEntities.UpdateVersionedEntities;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.store.mongo.versionedEntities.VersionedEntitiesMongo;

public class ManageDataStoreMongoModule extends DataStoreMongoModule
{
    @Override
    protected void configure()
    {
        super.configure();
        bind(UpdateEntities.class).to(EntitiesMongo.class);
        bind(UpdateVersionedEntities.class).to(VersionedEntitiesMongo.class);
        bind(UpdateProjects.class).to(ProjectsMongo.class);
        bind(UpdateProjectsVersions.class).to(ProjectsVersionsMongo.class);
        bind(UpdateFileGenerations.class).to(FileGenerationsMongo.class);

        expose(UpdateEntities.class);
        expose(UpdateVersionedEntities.class);
        expose(UpdateProjectsVersions.class);
        expose(UpdateFileGenerations.class);
        expose(UpdateProjects.class);
    }
}

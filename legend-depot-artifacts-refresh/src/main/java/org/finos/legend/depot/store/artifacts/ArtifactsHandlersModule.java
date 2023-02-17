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

package org.finos.legend.depot.store.artifacts;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.store.artifacts.api.entities.EntitiesArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntitiesArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.artifacts.services.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;

import javax.inject.Named;

public class ArtifactsHandlersModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(EntityArtifactsProvider.class).to(EntityProvider.class);
        bind(VersionedEntityArtifactsProvider.class).to(VersionedEntityProvider.class);
        bind(FileGenerationsArtifactsProvider.class).to(FileGenerationsProvider.class);

        bind(EntitiesArtifactsHandler.class).to(EntitiesHandlerImpl.class);
        bind(VersionedEntitiesArtifactsHandler.class).to(VersionedEntitiesHandlerImpl.class);
        bind(FileGenerationsArtifactsHandler.class).to(FileGenerationHandlerImpl.class);

        expose(EntitiesArtifactsHandler.class);
        expose(VersionedEntitiesArtifactsHandler.class);
        expose(FileGenerationsArtifactsHandler.class);
    }


    @Provides
    @Named("entityHandler")
    @Singleton
    boolean registerEntityHandler(EntitiesArtifactsHandler versionArtifactsHandler)
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, versionArtifactsHandler);
        return true;
    }

    @Provides
    @Named("versionedEntityHandler")
    @Singleton
    boolean registerVersionedEntityHandler(VersionedEntitiesArtifactsHandler versionedEntitiesArtifactsHandler)
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, versionedEntitiesArtifactsHandler);
        return true;
    }

    @Provides
    @Named("fileGenerationHandler")
    @Singleton
    boolean registerFileGenerationHandler(FileGenerationsArtifactsHandler versionArtifactsHandler)
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, versionArtifactsHandler);
        return true;
    }

}

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
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.entities.EntitiesArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntitiesArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.api.status.RefreshStatusService;
import org.finos.legend.depot.store.artifacts.resources.ArtifactsResource;
import org.finos.legend.depot.store.artifacts.services.ArtifactRefreshEventHandler;
import org.finos.legend.depot.store.artifacts.services.ArtifactResolverFactory;
import org.finos.legend.depot.store.artifacts.services.ArtifactsRefreshServiceImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.EntityProvider;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntitiesHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.entities.VersionedEntityProvider;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationHandlerImpl;
import org.finos.legend.depot.store.artifacts.services.file.FileGenerationsProvider;
import org.finos.legend.depot.store.artifacts.store.mongo.ArtifactsMongo;
import org.finos.legend.depot.store.artifacts.store.mongo.MongoRefreshStatus;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;

import javax.inject.Named;
import java.time.LocalDateTime;

public class ArtifactsModule extends PrivateModule
{
    private static final String UPDATE_MASTER_REVISIONS_SCHEDULER = "refreshVersionArtifacts-master-revisions-scheduler";
    private static final String UPDATE_VERSIONS_SCHEDULER = "refreshVersionArtifacts-versions-scheduler";
    private static final String FIX_MISSING_VERSIONS_SCHEDULE = "fix-missing-versions-schedule";

    @Override
    protected void configure()
    {
        bind(ArtifactsResource.class);
        bind(UpdateArtifacts.class).to(ArtifactsMongo.class);
        bind(ManageRefreshStatusService.class).to(MongoRefreshStatus.class);
        bind(RefreshStatusService.class).to(MongoRefreshStatus.class);

        bind(EntitiesArtifactsHandler.class).to(EntitiesHandlerImpl.class);
        bind(EntityArtifactsProvider.class).to(EntityProvider.class);

        bind(VersionedEntitiesArtifactsHandler.class).to(VersionedEntitiesHandlerImpl.class);
        bind(VersionedEntityArtifactsProvider.class).to(VersionedEntityProvider.class);

        bind(FileGenerationsArtifactsHandler.class).to(FileGenerationHandlerImpl.class);
        bind(FileGenerationsArtifactsProvider.class).to(FileGenerationsProvider.class);

        bind(ArtifactsRefreshService.class).to(ArtifactsRefreshServiceImpl.class);
        bind(NotificationEventHandler.class).to(ArtifactRefreshEventHandler.class);

        expose(EntityArtifactsProvider.class);
        expose(VersionedEntityArtifactsProvider.class);
        expose(ArtifactsRefreshService.class);

        expose(ManageRefreshStatusService.class);
        expose(RefreshStatusService.class);
        expose(NotificationEventHandler.class);
        expose(ArtifactsResource.class);
    }

    @Provides
    @Named("entityRefresh")
    @Singleton
    boolean registerEntityRefresh(EntitiesArtifactsHandler versionArtifactsHandler)
    {
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.ENTITIES, versionArtifactsHandler);
        return true;
    }

    @Provides
    @Named("versionedEntityRefresh")
    @Singleton
    boolean registerVersionedEntityRefresh(VersionedEntitiesArtifactsHandler versionedEntitiesArtifactsHandler)
    {
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.VERSIONED_ENTITIES, versionedEntitiesArtifactsHandler);
        return true;
    }

    @Provides
    @Named("fileGenerationRefresh")
    @Singleton
    boolean registerFileGenerationRefresh(FileGenerationsArtifactsHandler versionArtifactsHandler)
    {
        ArtifactResolverFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, versionArtifactsHandler);
        return true;
    }


    @Provides
    @Singleton
    @Named("update-versions")
    boolean initVersions(SchedulesFactory schedulesFactory, ArtifactsRefreshService artifactsRefreshService, ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(UPDATE_VERSIONS_SCHEDULER, LocalDateTime.now().plusHours(2), configuration.getVersionsUpdateIntervalInMillis(), false,() -> artifactsRefreshService.refreshAllVersionsForAllProjects(false));
        return true;
    }

    @Provides
    @Singleton
    @Named("update-revisions")
    boolean initRevisions(SchedulesFactory schedulesFactory,ArtifactsRefreshService artifactsRefreshService, ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(UPDATE_MASTER_REVISIONS_SCHEDULER, LocalDateTime.now().plusHours(1), configuration.getLatestUpdateIntervalInMillis(),false, () -> artifactsRefreshService.refreshMasterSnapshotForAllProjects(false));
        return true;
    }

    @Provides
    @Singleton
    @Named("update-missing-versions")
    boolean initFixVersionsMismatchDaemon(SchedulesFactory schedulesFactory, ArtifactsRefreshService artifactsRefreshService,ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(FIX_MISSING_VERSIONS_SCHEDULE, LocalDateTime.now().plusMinutes(5), configuration.getFixVersionsMismatchIntervalInMillis(), false,artifactsRefreshService::refreshProjectsWithMissingVersions);
        return true;
    }
}

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

package org.finos.legend.depot.services.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntityProvider;
import org.finos.legend.depot.services.artifacts.handlers.entities.VersionedEntitiesHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.entities.VersionedEntityProvider;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationsProvider;
import org.finos.legend.depot.services.artifacts.purge.ArtifactsPurgeServiceImpl;
import org.finos.legend.depot.services.artifacts.refresh.ArtifactsRefreshServiceImpl;
import org.finos.legend.depot.services.artifacts.refresh.ProjectVersionRefreshHandler;
import org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl;
import org.finos.legend.depot.services.artifacts.refresh.RefreshDependenciesServiceImpl;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntitiesArtifactsHandler;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.VersionedEntitiesArtifactsHandler;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.VersionedEntityArtifactsProvider;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsHandler;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;

import static org.finos.legend.depot.services.artifacts.refresh.ProjectVersionRefreshHandler.TOTAL_NUMBER_OF_VERSIONS_REFRESH;
import static org.finos.legend.depot.services.artifacts.refresh.ProjectVersionRefreshHandler.VERSION_REFRESH_COUNTER;
import static org.finos.legend.depot.services.artifacts.refresh.ProjectVersionRefreshHandler.VERSION_REFRESH_DURATION;
import static org.finos.legend.depot.services.artifacts.refresh.ProjectVersionRefreshHandler.VERSION_REFRESH_DURATION_HELP;

public class ArtifactsServicesModule extends PrivateModule
{

    @Override
    protected void configure()
    {
        configureHandlers();
        configureRefresh();
        configurePurge();
        configureVersionReconciliation();
    }

    protected void configureVersionReconciliation()
    {
        bind(VersionsReconciliationService.class).to(VersionsReconciliationServiceImpl.class);
        expose(VersionsReconciliationService.class);
    }

    protected void configurePurge()
    {
        bind(ArtifactsPurgeService.class).to(ArtifactsPurgeServiceImpl.class);
        expose(ArtifactsPurgeService.class);
    }

    protected void configureRefresh()
    {
        bind(ArtifactsRefreshService.class).to(ArtifactsRefreshServiceImpl.class);
        bind(NotificationEventHandler.class).to(ProjectVersionRefreshHandler.class);
        bind(RefreshDependenciesService.class).to(RefreshDependenciesServiceImpl.class);
        bind(ProjectVersionRefreshHandler.class);

        expose(ArtifactsRefreshService.class);
        expose(NotificationEventHandler.class);
        expose(RefreshDependenciesService.class);
    }


    protected void configureHandlers()
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
    @Named("fileGenerationHandler")
    @Singleton
    boolean registerFileGenerationHandler(FileGenerationsArtifactsHandler versionArtifactsHandler)
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, versionArtifactsHandler);
        return true;
    }


    @Provides
    @Named("artifact-refresh-metrics")
    @Singleton
    boolean registerMetrics(PrometheusMetricsHandler metricsHandler)
    {
        metricsHandler.registerCounter(VERSION_REFRESH_COUNTER, TOTAL_NUMBER_OF_VERSIONS_REFRESH);
        metricsHandler.registerHistogram(VERSION_REFRESH_DURATION, VERSION_REFRESH_DURATION_HELP);
        return true;
    }

    @Provides
    @Singleton
    @Named("maximumSnapshotsAllowed")
    int getNoOfSnapshotVersionsToRetain(ArtifactsRetentionPolicyConfiguration artifactsRetentionPolicyConfiguration)
    {
        return artifactsRetentionPolicyConfiguration.getMaximumSnapshotsAllowed();
    }

}

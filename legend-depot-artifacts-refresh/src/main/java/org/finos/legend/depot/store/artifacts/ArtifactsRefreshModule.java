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
import org.finos.legend.depot.store.admin.services.schedules.SchedulesFactory;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.api.status.RefreshStatusService;
import org.finos.legend.depot.store.artifacts.resources.ArtifactsRefreshResource;
import org.finos.legend.depot.store.artifacts.services.ArtifactRefreshEventHandler;
import org.finos.legend.depot.store.artifacts.services.ArtifactsRefreshServiceImpl;
import org.finos.legend.depot.store.artifacts.store.mongo.ArtifactsMongo;
import org.finos.legend.depot.store.artifacts.store.mongo.MongoRefreshStatus;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;

import javax.inject.Named;
import java.time.LocalDateTime;

public class ArtifactsRefreshModule extends PrivateModule
{
    private static final String UPDATE_MASTER_REVISIONS_SCHEDULER = "refreshVersionArtifacts-master-revisions-scheduler";
    private static final String UPDATE_VERSIONS_SCHEDULER = "refreshVersionArtifacts-versions-scheduler";
    private static final String FIX_MISSING_VERSIONS_SCHEDULE = "fix-missing-versions-schedule";

    @Override
    protected void configure()
    {
        bind(ArtifactsRefreshResource.class);

        bind(UpdateArtifacts.class).to(ArtifactsMongo.class);
        bind(ManageRefreshStatusService.class).to(MongoRefreshStatus.class);
        bind(RefreshStatusService.class).to(MongoRefreshStatus.class);

        bind(ArtifactsRefreshService.class).to(ArtifactsRefreshServiceImpl.class);
        bind(NotificationEventHandler.class).to(ArtifactRefreshEventHandler.class);

        expose(ArtifactsRefreshService.class);
        expose(ManageRefreshStatusService.class);
        expose(RefreshStatusService.class);
        expose(NotificationEventHandler.class);
        expose(ArtifactsRefreshResource.class);
    }


    @Provides
    @Named("artifact-refresh-metrics")
    @Singleton
    boolean registerMetrics(PrometheusMetricsHandler metricsHandler)
    {
        metricsHandler.registerCounter(ArtifactsRefreshServiceImpl.VERSION_REFRESH_COUNTER, ArtifactsRefreshServiceImpl.TOTAL_NUMBER_OF_VERSIONS_REFRESH);
        metricsHandler.registerHistogram(ArtifactsRefreshServiceImpl.VERSION_REFRESH_DURATION, ArtifactsRefreshServiceImpl.VERSION_REFRESH_DURATION_HELP);
        return true;
    }


    @Provides
    @Singleton
    @Named("update-versions")
    boolean initVersions(SchedulesFactory schedulesFactory, ArtifactsRefreshService artifactsRefreshService, ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(UPDATE_VERSIONS_SCHEDULER, LocalDateTime.now().plusSeconds(configuration.getVersionsUpdateIntervalInMillis() / 1000), configuration.getVersionsUpdateIntervalInMillis(), false,() -> artifactsRefreshService.refreshAllVersionsForAllProjects(false,false,UPDATE_VERSIONS_SCHEDULER));
        return true;
    }

    @Provides
    @Singleton
    @Named("update-revisions")
    boolean initRevisions(SchedulesFactory schedulesFactory,ArtifactsRefreshService artifactsRefreshService, ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(UPDATE_MASTER_REVISIONS_SCHEDULER, LocalDateTime.now().plusSeconds(configuration.getLatestUpdateIntervalInMillis() / 1000), configuration.getLatestUpdateIntervalInMillis(),false, () -> artifactsRefreshService.refreshMasterSnapshotForAllProjects(false,false,UPDATE_MASTER_REVISIONS_SCHEDULER));
        return true;
    }

    @Provides
    @Singleton
    @Named("update-missing-versions")
    boolean initFixVersionsMismatchDaemon(SchedulesFactory schedulesFactory, ArtifactsRefreshService artifactsRefreshService,ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(FIX_MISSING_VERSIONS_SCHEDULE, LocalDateTime.now().plusSeconds(configuration.getVersionsUpdateIntervalInMillis() / 1000), configuration.getFixVersionsMismatchIntervalInMillis(), false,() -> artifactsRefreshService.refreshProjectsWithMissingVersions(false,false,FIX_MISSING_VERSIONS_SCHEDULE));
        return true;
    }
}

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
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.ParentEvent;
import org.finos.legend.depot.store.artifacts.resources.ArtifactRefreshStatusResource;
import org.finos.legend.depot.store.artifacts.resources.ArtifactsRefreshResource;
import org.finos.legend.depot.store.artifacts.services.ArtifactsRefreshServiceImpl;
import org.finos.legend.depot.store.artifacts.services.ProjectVersionRefreshHandler;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;

import javax.inject.Named;

import static org.finos.legend.depot.store.artifacts.services.ProjectVersionRefreshHandler.TOTAL_NUMBER_OF_VERSIONS_REFRESH;
import static org.finos.legend.depot.store.artifacts.services.ProjectVersionRefreshHandler.VERSION_REFRESH_COUNTER;
import static org.finos.legend.depot.store.artifacts.services.ProjectVersionRefreshHandler.VERSION_REFRESH_DURATION;
import static org.finos.legend.depot.store.artifacts.services.ProjectVersionRefreshHandler.VERSION_REFRESH_DURATION_HELP;

public class ArtifactsRefreshModule extends PrivateModule
{

    private static final String CLEANUP_REFRESH_SCHEDULE = "clean-refresh-status-schedule";

    @Override
    protected void configure()
    {
        bind(ArtifactsRefreshResource.class);
        bind(ArtifactRefreshStatusResource.class);

        bind(ArtifactsRefreshService.class).to(ArtifactsRefreshServiceImpl.class);
        bind(NotificationEventHandler.class).to(ProjectVersionRefreshHandler.class);
        bind(ProjectVersionRefreshHandler.class);

        expose(ArtifactsRefreshService.class);
        expose(NotificationEventHandler.class);
        expose(ArtifactsRefreshResource.class);
        expose(ArtifactRefreshStatusResource.class);
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
    @Named("refresh-all-versions")
    boolean initVersions(SchedulesFactory schedulesFactory, ArtifactsRefreshService artifactsRefreshService, ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.registerSingleInstance(ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE.name(), configuration.getVersionsUpdateIntervalInMillis(), configuration.getVersionsUpdateIntervalInMillis(),() -> artifactsRefreshService.refreshAllVersionsForAllProjects(false,false,false, ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE.name()));
        return true;
    }

    @Provides
    @Singleton
    @Named("cleanup-refresh-status")
    boolean cleanUpSchedule(SchedulesFactory schedulesFactory, ProjectVersionRefreshHandler refreshHandler)
    {
        schedulesFactory.register(CLEANUP_REFRESH_SCHEDULE, SchedulesFactory.MINUTE,SchedulesFactory.MINUTE, () -> refreshHandler.deleteExpiredRefresh());
        return true;
    }
}

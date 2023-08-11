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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.services.VersionsMismatchService;
import org.finos.legend.depot.services.schedules.SchedulesFactory;
import org.finos.legend.depot.services.api.dependencies.ManageDependenciesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.dependencies.ManageDependenciesServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;
import org.finos.legend.depot.tracing.configuration.PrometheusConfiguration;

import javax.inject.Named;

import static org.finos.legend.depot.services.VersionsMismatchService.PROJECTS;
import static org.finos.legend.depot.services.VersionsMismatchService.REPO_VERSIONS;
import static org.finos.legend.depot.services.VersionsMismatchService.REPO_EXCEPTIONS;
import static org.finos.legend.depot.services.VersionsMismatchService.MISSING_REPO_VERSIONS;
import static org.finos.legend.depot.services.VersionsMismatchService.MISSING_STORE_VERSIONS;
import static org.finos.legend.depot.services.VersionsMismatchService.STORE_VERSIONS;

public class ManageCoreDataServicesModule extends CoreDataServicesModule
{
    private static final String REPOSITORY_METRICS_SCHEDULE = "repository-metrics-schedule";

    @Override
    protected void configure()
    {
        super.configure();

        bind(ManageProjectsService.class).to(ManageProjectsServiceImpl.class);
        bind(ManageDependenciesService.class).to(ManageDependenciesServiceImpl.class);
        bind(VersionsMismatchService.class);

        expose(ManageProjectsService.class);
        expose(ManageDependenciesService.class);
        expose(VersionsMismatchService.class);
    }

    @Provides
    @Named("repository-metrics")
    @Singleton
    boolean registerMetrics(PrometheusConfiguration prometheusConfiguration, SchedulesFactory schedulesFactory, VersionsMismatchService versionsMismatchService)
    {
        if (prometheusConfiguration.isEnabled())
        {
            PrometheusMetricsHandler metricsHandler = prometheusConfiguration.getMetricsHandler();
            metricsHandler.registerGauge(PROJECTS, PROJECTS);
            metricsHandler.registerGauge(REPO_VERSIONS, REPO_VERSIONS);
            metricsHandler.registerGauge(STORE_VERSIONS, STORE_VERSIONS);
            metricsHandler.registerGauge(MISSING_REPO_VERSIONS, MISSING_REPO_VERSIONS);
            metricsHandler.registerGauge(MISSING_STORE_VERSIONS, MISSING_STORE_VERSIONS);
            metricsHandler.registerGauge(REPO_EXCEPTIONS, REPO_EXCEPTIONS);
            schedulesFactory.register(REPOSITORY_METRICS_SCHEDULE, 5 * SchedulesFactory.MINUTE, 5 * SchedulesFactory.MINUTE, versionsMismatchService::findVersionsMismatches);
        }
        return true;
    }
}

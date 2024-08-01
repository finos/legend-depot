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
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;

import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.MISSING_REPO_VERSIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.MISSING_STORE_VERSIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.PROJECTS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.PROJECT_UPDATE_EXCEPTIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.REPO_EXCEPTIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.REPO_VERSIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.STORE_VERSIONS;

public class VersionReconciliationSchedulesModule extends PrivateModule
{
    public static final String REPOSITORY_METRICS_SCHEDULE = "repository-metrics-schedule";
    public static final String SYNC_PROJECT_LATEST_VERSIONS_SCHEDULE = "sync-project-latest-versions-schedule";

    @Override
    protected void configure()
    {
    }

    @Provides
    @Named("repository-metrics")
    @Singleton
    boolean registerMetrics(PrometheusConfiguration prometheusConfiguration, SchedulesFactory schedulesFactory, VersionsReconciliationService versionsReconciliationService)
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
            metricsHandler.registerGauge(PROJECT_UPDATE_EXCEPTIONS, PROJECT_UPDATE_EXCEPTIONS);
            schedulesFactory.register(REPOSITORY_METRICS_SCHEDULE, 5 * SchedulesFactory.MINUTE, 5 * SchedulesFactory.MINUTE, versionsReconciliationService::findVersionsMismatches);
            schedulesFactory.register(SYNC_PROJECT_LATEST_VERSIONS_SCHEDULE, 5 * SchedulesFactory.MINUTE, 5 * SchedulesFactory.MINUTE, versionsReconciliationService::syncLatestProjectVersions);
        }
        return true;
    }

}

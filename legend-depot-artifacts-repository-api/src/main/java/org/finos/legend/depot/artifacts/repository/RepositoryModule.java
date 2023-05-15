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

package org.finos.legend.depot.artifacts.repository;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.artifacts.repository.resources.RepositoryResource;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.tracing.api.PrometheusMetricsHandler;

import javax.inject.Named;

import java.util.Arrays;

import static org.finos.legend.depot.artifacts.repository.services.RepositoryServices.MISSING_REPO_VERSIONS;
import static org.finos.legend.depot.artifacts.repository.services.RepositoryServices.MISSING_STORE_VERSIONS;
import static org.finos.legend.depot.artifacts.repository.services.RepositoryServices.PROJECTS;
import static org.finos.legend.depot.artifacts.repository.services.RepositoryServices.REPO_EXCEPTIONS;
import static org.finos.legend.depot.artifacts.repository.services.RepositoryServices.REPO_VERSIONS;
import static org.finos.legend.depot.artifacts.repository.services.RepositoryServices.STORE_VERSIONS;

public class RepositoryModule extends PrivateModule
{
    private static final String REPOSITORY_METRICS_SCHEDULE = "repository-metrics-schedule";

    @Override
    protected void configure()
    {
       bind(RepositoryResource.class);
       bind(RepositoryServices.class);
       expose(RepositoryResource.class);
       expose(RepositoryServices.class);
    }


    @Provides
    @Named("repository-metrics")
    @Singleton
    boolean registerMetrics(SchedulesFactory schedulesFactory, PrometheusMetricsHandler metricsHandler, RepositoryServices repositoryServices)
    {
        metricsHandler.registerGauge(PROJECTS, PROJECTS);
        metricsHandler.registerGauge(REPO_VERSIONS, REPO_VERSIONS);
        metricsHandler.registerGauge(STORE_VERSIONS, STORE_VERSIONS);
        metricsHandler.registerGauge(MISSING_REPO_VERSIONS, MISSING_REPO_VERSIONS);
        metricsHandler.registerGauge(MISSING_STORE_VERSIONS, MISSING_STORE_VERSIONS);
        metricsHandler.registerGauge(REPO_EXCEPTIONS, REPO_EXCEPTIONS);
        schedulesFactory.register(REPOSITORY_METRICS_SCHEDULE, 5 * SchedulesFactory.MINUTE, 5 * SchedulesFactory.MINUTE,repositoryServices::findVersionsMismatches);
        return true;
    }
}

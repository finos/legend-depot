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

package org.finos.legend.depot.store.guice;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.artifacts.repository.api.VoidArtifactRepositoryProvider;
import org.finos.legend.depot.core.http.guice.BaseModule;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.store.admin.api.metrics.StorageMetrics;
import org.finos.legend.depot.store.notifications.domain.QueueManagerConfiguration;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;

import org.slf4j.Logger;

import javax.inject.Named;
import java.time.LocalDateTime;

public class DepotStoreServerModule extends BaseModule<DepotStoreServerConfiguration>
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DepotStoreServerModule.class);
    private ArtifactRepository artifactRepository;

    @Override
    public void configure(Binder binder)
    {
        super.configure(binder);
        binder.bind(ArtifactRepository.class).toProvider(this::getArtifactRepository);
        binder.bind(ArtifactRepositoryProviderConfiguration.class).toProvider(this::getArtifactRepositoryConfiguration);
        binder.bind(IncludeProjectPropertiesConfiguration.class).toProvider(this::getIncludePropertiesConfiguration);
        binder.bind(QueueManagerConfiguration.class).toProvider(this::getQueueManagerConfiguration);
    }

    private QueueManagerConfiguration getQueueManagerConfiguration()
    {
        return getConfiguration().getQueueManagerConfiguration() != null ? getConfiguration().getQueueManagerConfiguration() : new QueueManagerConfiguration();
    }

    private IncludeProjectPropertiesConfiguration getIncludePropertiesConfiguration()
    {
        return getConfiguration().getIncludeProjectPropertiesConfiguration();
    }

    private ArtifactRepositoryProviderConfiguration getArtifactRepositoryConfiguration()
    {
        ArtifactRepositoryProviderConfiguration configuration = getConfiguration().getArtifactRepositoryProviderConfiguration();
        return (configuration == null) ? ArtifactRepositoryProviderConfiguration.voidConfiguration() : configuration;
    }

    private ArtifactRepository getArtifactRepository()
    {
        if (artifactRepository == null)
        {
            LOGGER.info("resolving Artifact Repository provider");
            artifactRepository = resolveArtifactRepositoryProvider();
        }
        return artifactRepository;
    }

    private ArtifactRepository resolveArtifactRepositoryProvider()
    {
        ArtifactRepositoryProviderConfiguration configuration = getConfiguration().getArtifactRepositoryProviderConfiguration();
        if (configuration != null)
        {
            ArtifactRepository configuredProvider = configuration.initialiseArtifactRepositoryProvider();
            if (configuredProvider != null)
            {
                LOGGER.info("Artifact Repository from provider config [{}] : [{}]", configuration.getName(), configuration);
                return configuredProvider;
            }
        }
        LOGGER.error("Using void Artifact Repository provider, artifacts cant/wont be updated");
        return new VoidArtifactRepositoryProvider(configuration);
    }

    @Provides
    @Singleton
    @Named("storage-metrics")
    boolean scheduleStorageMetrics(SchedulesFactory schedulesFactory, StorageMetrics storageMetrics)
    {
        storageMetrics.init();
        schedulesFactory.register("storage-metrics", LocalDateTime.now().plusMinutes(5), 5 * 60 * 1000L, false, storageMetrics::reportMetrics);
        return true;
    }

}

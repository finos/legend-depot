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

package org.finos.legend.depot.store.server.guice;

import org.finos.legend.depot.core.server.guice.BaseServerModule;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;

public class DepotStoreServerModule extends BaseServerModule<DepotStoreServerConfiguration>
{

    @Override
    protected void configure()
    {
        super.configure();
        bind(ArtifactRepositoryProviderConfiguration.class).toProvider(this::getArtifactRepositoryConfiguration);
        bind(ArtifactsRefreshPolicyConfiguration.class).toProvider(this::getRefreshPolicyConfiguration);
        bind(IncludeProjectPropertiesConfiguration.class).toProvider(this::getIncludePropertiesConfiguration);
        bind(ArtifactsRetentionPolicyConfiguration.class).toProvider(this::getRetentionPolicyConfiguration);
        bind(QueueManagerConfiguration.class).toProvider(this::getQueueManagerConfiguration);
    }

    private QueueManagerConfiguration getQueueManagerConfiguration()
    {
        return configuration().getQueueManagerConfiguration() != null ? configuration().getQueueManagerConfiguration() : new QueueManagerConfiguration();
    }

    private IncludeProjectPropertiesConfiguration getIncludePropertiesConfiguration()
    {
        return configuration().getArtifactsRefreshPolicyConfiguration().getIncludeProjectPropertiesConfiguration();
    }

    private ArtifactsRefreshPolicyConfiguration getRefreshPolicyConfiguration()
    {
        return configuration().getArtifactsRefreshPolicyConfiguration();
    }

    private ArtifactsRetentionPolicyConfiguration getRetentionPolicyConfiguration()
    {
        return configuration().getRetentionPolicyConfiguration();
    }

    private ArtifactRepositoryProviderConfiguration getArtifactRepositoryConfiguration()
    {
        ArtifactRepositoryProviderConfiguration configuration = configuration().getArtifactRepositoryProviderConfiguration();
        return (configuration == null) ? ArtifactRepositoryProviderConfiguration.voidConfiguration() : configuration;
    }

}

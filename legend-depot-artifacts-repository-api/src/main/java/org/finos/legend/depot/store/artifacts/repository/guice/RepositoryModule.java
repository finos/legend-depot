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

package org.finos.legend.depot.store.artifacts.repository.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.store.artifacts.repository.api.VoidArtifactRepositoryProvider;
import org.finos.legend.depot.store.resources.artifacts.RepositoryResource;
import org.finos.legend.depot.store.artifacts.repository.services.RepositoryServices;
import org.slf4j.Logger;

public class RepositoryModule extends PrivateModule
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RepositoryModule.class);

    @Override
    protected void configure()
    {
       bind(RepositoryResource.class);
       bind(RepositoryServices.class);
       expose(RepositoryResource.class);
       expose(RepositoryServices.class);
       expose(ArtifactRepository.class);
    }


    @Provides
    @Singleton
    public ArtifactRepository getArtifactRepository(ArtifactRepositoryProviderConfiguration configuration)
    {
        if (configuration != null)
        {
            ArtifactRepository configuredProvider = configuration.initialiseArtifactRepositoryProvider();
            if (configuredProvider != null)
            {
                LOGGER.info("Artifact Repository from provider config [{}] : [{}]", configuration.getName(), configuration);
                return configuredProvider;
            }
        }
        LOGGER.info("Using void Artifact Repository provider, artifacts cant/wont be updated");
        return new VoidArtifactRepositoryProvider(configuration);
    }
}

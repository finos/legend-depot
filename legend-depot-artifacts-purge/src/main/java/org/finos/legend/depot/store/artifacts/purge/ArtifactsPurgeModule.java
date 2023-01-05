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

package org.finos.legend.depot.store.artifacts.purge;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.schedules.services.SchedulesFactory;
import org.finos.legend.depot.store.artifacts.purge.api.ArtifactsPurgeService;
import org.finos.legend.depot.store.artifacts.purge.resources.ArtifactsPurgeResource;
import org.finos.legend.depot.store.artifacts.purge.services.ArtifactsPurgeServiceImpl;

import java.time.LocalDateTime;

public class ArtifactsPurgeModule extends PrivateModule
{
    private static final String DELETE_VERSIONS_NOT_IN_REPO = "delete-versions-not-in-repository-schedule";

    @Override
    protected void configure()
    {
        bind(ArtifactsPurgeResource.class);
        expose(ArtifactsPurgeResource.class);

        bind(ArtifactsPurgeService.class).to(ArtifactsPurgeServiceImpl.class);
    }

    @Provides
    @Singleton
    @Named("delete-versions-not-in-repository")
    boolean initDeleteVersionsNotInRepository(SchedulesFactory schedulesFactory, ArtifactsPurgeService purgeService, ArtifactRepositoryProviderConfiguration configuration)
    {
        schedulesFactory.register(DELETE_VERSIONS_NOT_IN_REPO, LocalDateTime.now().plusSeconds(configuration.getDeleteVersionInRepoIntervalInMillis() / 1000), configuration.getDeleteVersionInRepoIntervalInMillis(), false,() -> purgeService.deleteVersionsNotInRepository());
        return true;
    }
}

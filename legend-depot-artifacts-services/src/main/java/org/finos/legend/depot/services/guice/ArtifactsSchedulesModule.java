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
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.services.api.artifacts.refresh.ParentEvent;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;

public class ArtifactsSchedulesModule extends PrivateModule
{

    @Override
    protected void configure()
    {

    }


    @Provides
    @Singleton
    @Named("refresh-all-versions")
    boolean initVersions(SchedulesFactory schedulesFactory, ArtifactsRefreshService artifactsRefreshService, ArtifactsRefreshPolicyConfiguration configuration)
    {
        schedulesFactory.registerExternalTriggerSchedule(ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE.name(), configuration.getVersionsUpdateIntervalInMillis(), true, () -> artifactsRefreshService.refreshAllVersionsForAllProjects(false,false,false, ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE.name()));
        return true;
    }


    @Provides
    @Singleton
    @Named("evict-LRU-project-versions")
    boolean scheduleEvictionOfProjectVersions(SchedulesFactory schedulesFactory, ArtifactsPurgeService artifactsPurgeService, ArtifactsRetentionPolicyConfiguration retentionPolicyConfiguration)
    {
        schedulesFactory.registerSingleInstance("evict-LRU-project-versions", SchedulesFactory.MINUTE, 24 * SchedulesFactory.HOUR, () ->
        {
            artifactsPurgeService.evictLeastRecentlyUsed(retentionPolicyConfiguration.getTtlForVersions(), retentionPolicyConfiguration.getTtlForSnapshots());
            return true;
        });
        return true;
    }

    @Provides
    @Singleton
    @Named("deprecate-versions-notInRepository")
    boolean scheduleDeprecationOfProjectVersions(SchedulesFactory schedulesFactory, ArtifactsPurgeService artifactsPurgeService)
    {
        schedulesFactory.registerSingleInstance("deprecate-versions-notInRepository", SchedulesFactory.MINUTE, 48 * SchedulesFactory.HOUR, () ->
        {
            artifactsPurgeService.deprecateVersionsNotInRepository();
            return true;
        });
        return true;
    }
}

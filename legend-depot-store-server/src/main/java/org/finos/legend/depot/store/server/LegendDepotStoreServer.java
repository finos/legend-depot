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

package org.finos.legend.depot.store.server;

import com.google.inject.Module;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import org.finos.legend.depot.core.authorisation.AuthorisationModule;
import org.finos.legend.depot.core.http.BaseServer;
import org.finos.legend.depot.core.http.resources.InfoPageModule;
import org.finos.legend.depot.services.guice.ArtifactsSchedulesModule;
import org.finos.legend.depot.services.guice.ArtifactsServicesModule;
import org.finos.legend.depot.services.guice.VersionReconciliationSchedulesModule;
import org.finos.legend.depot.services.guice.ManageCoreDataServicesModule;
import org.finos.legend.depot.services.guice.ManageEntitiesServicesModule;
import org.finos.legend.depot.services.guice.ManageGenerationsServicesModule;
import org.finos.legend.depot.services.guice.ManageQueryMetricsSchedulesModule;
import org.finos.legend.depot.services.guice.ManageSchedulesModule;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;

import org.finos.legend.depot.services.guice.QueryMetricsModule;
import org.finos.legend.depot.services.guice.RepositoryModule;
import org.finos.legend.depot.store.mongo.guice.ManageMongoStoreSchedulesModule;
import org.finos.legend.depot.store.mongo.guice.ManageQueryMetricsMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.ManageSchedulesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.ArtifactsStoreMongoModule;
import org.finos.legend.depot.store.mongo.core.MongoClientModule;
import org.finos.legend.depot.store.mongo.guice.CoreDataMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.EntitiesMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageCoreDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageEntitiesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageGenerationsStoreMongoModule;
import org.finos.legend.depot.store.notifications.NotificationsModule;
import org.finos.legend.depot.store.notifications.NotificationsSchedulesModule;
import org.finos.legend.depot.store.notifications.queue.ManageNotificationsQueueModule;
import org.finos.legend.depot.store.resources.guice.ArtifactsResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageCoreDataResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageEntitiesResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageSchedulesResourcesModule;
import org.finos.legend.depot.store.resources.guice.RepositoryResourcesModule;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.finos.legend.depot.store.server.guice.DepotStoreServerModule;
import org.finos.legend.depot.tracing.TracingModule;

import java.util.Arrays;
import java.util.List;

public class LegendDepotStoreServer extends BaseServer<DepotStoreServerConfiguration>
{
    public LegendDepotStoreServer()
    {
        super();
    }

    public static void main(String... args) throws Exception
    {
        new LegendDepotStoreServer().run(args);
    }

    @Override
    protected void configureObjectMapper(Bootstrap<DepotStoreServerConfiguration> bootstrap)
    {
        super.configureObjectMapper(bootstrap);
        ArtifactRepositoryProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
    }

    @Override
    public void registerJacksonJsonProvider(JerseyEnvironment jerseyEnvironment)
    {
        jerseyEnvironment.register(LegendDepotStoreServerJacksonJsonProvider.class);
    }

    @Override
    protected List<Module> getServerModules()
    {
        return Arrays.asList(
                new InfoPageModule(),
                new DepotStoreServerModule(),

                new ManageCoreDataResourcesModule(),
                new ManageCoreDataServicesModule(),
                new ManageCoreDataStoreMongoModule(),
                new VersionReconciliationSchedulesModule(),
                new CoreDataMigrationsStoreMongoModule(),

                new ManageEntitiesResourcesModule(),
                new ManageEntitiesServicesModule(),
                new ManageEntitiesStoreMongoModule(),
                new EntitiesMigrationsStoreMongoModule(),

                new ManageGenerationsServicesModule(),
                new ManageGenerationsStoreMongoModule(),

                new ArtifactsResourcesModule(),
                new ArtifactsServicesModule(),
                new ArtifactsSchedulesModule(),
                new ArtifactsStoreMongoModule(),

                new RepositoryResourcesModule(),
                new RepositoryModule(),

                new ManageSchedulesResourcesModule(),
                new ManageSchedulesModule(),
                new ManageSchedulesStoreMongoModule(),

                new NotificationsModule(),
                new NotificationsSchedulesModule(),
                new ManageNotificationsQueueModule(),

                new QueryMetricsModule(),
                new ManageQueryMetricsSchedulesModule(),
                new ManageQueryMetricsMongoStoreModule(),

                new AuthorisationModule(),

                new TracingModule(),

                new MongoClientModule(),
                new ManageMongoStoreSchedulesModule(),
                new ManageMongoStoreModule()
              );
    }

}

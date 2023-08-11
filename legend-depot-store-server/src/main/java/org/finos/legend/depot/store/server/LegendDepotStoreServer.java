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
import org.finos.legend.depot.services.guice.ManageCoreDataServicesModule;
import org.finos.legend.depot.services.guice.ManageEntitiesServicesModule;
import org.finos.legend.depot.services.guice.ManageGenerationsServicesModule;
import org.finos.legend.depot.services.schedules.guice.AdminSchedulesModule;
import org.finos.legend.depot.store.artifacts.guice.ArtifactsHandlersModule;
import org.finos.legend.depot.store.artifacts.guice.ArtifactsRefreshModule;
import org.finos.legend.depot.store.artifacts.purge.guice.ArtifactsPurgeModule;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.store.artifacts.repository.guice.RepositoryModule;
import org.finos.legend.depot.store.metrics.AdminMetricsModule;
import org.finos.legend.depot.store.mongo.admin.guice.AdminSchedulesStoreMongoModule;
import org.finos.legend.depot.store.mongo.admin.guice.ManageAdminDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.core.MongoClientModule;
import org.finos.legend.depot.store.mongo.guice.CoreDataMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.EntitiesMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageCoreDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageEntitiesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageGenerationsStoreMongoModule;
import org.finos.legend.depot.store.notifications.NotificationsModule;
import org.finos.legend.depot.store.notifications.queue.ManageNotificationsQueueModule;
import org.finos.legend.depot.store.resources.guice.ManageCoreDataResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageEntitiesResourcesModule;
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
                new CoreDataMigrationsStoreMongoModule(),


                new ManageEntitiesResourcesModule(),
                new ManageEntitiesServicesModule(),
                new ManageEntitiesStoreMongoModule(),
                new EntitiesMigrationsStoreMongoModule(),

                new ManageGenerationsServicesModule(),
                new ManageGenerationsStoreMongoModule(),

                new MongoClientModule(),

                new ManageAdminDataStoreMongoModule(),

                new AdminSchedulesModule(),
                new AdminSchedulesStoreMongoModule(),

                new AdminMetricsModule(),

                new AuthorisationModule(),

                new ArtifactsHandlersModule(),
                new ArtifactsRefreshModule(),
                new ArtifactsPurgeModule(),

                new RepositoryModule(),
                new TracingModule(),
                new NotificationsModule(),
                new ManageNotificationsQueueModule());
    }

}

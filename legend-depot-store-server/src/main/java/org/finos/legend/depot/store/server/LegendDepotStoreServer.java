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
import com.hubspot.dropwizard.guicier.GuiceBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.finos.legend.depot.artifacts.repository.RepositoryModule;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.core.authorisation.AuthorisationModule;
import org.finos.legend.depot.core.http.BaseServer;
import org.finos.legend.depot.core.http.resources.InfoPageModule;
import org.finos.legend.depot.services.ManageServicesModule;
import org.finos.legend.depot.schedules.AdminSchedulesModule;
import org.finos.legend.depot.store.api.StorageConfiguration;
import org.finos.legend.depot.store.artifacts.ArtifactsHandlersModule;
import org.finos.legend.depot.store.artifacts.ArtifactsRefreshModule;
import org.finos.legend.depot.store.artifacts.purge.ArtifactsPurgeModule;
import org.finos.legend.depot.store.guice.DepotStoreResourcesModule;
import org.finos.legend.depot.store.guice.DepotStoreServerModule;
import org.finos.legend.depot.store.metrics.AdminMetricsModule;
import org.finos.legend.depot.store.mongo.admin.ManageAdminDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.ManageDataStoreMongoModule;
import org.finos.legend.depot.store.notifications.NotificationsModule;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.finos.legend.depot.store.status.StoreStatusModule;
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
    public void initialize(Bootstrap<DepotStoreServerConfiguration> bootstrap)
    {
        super.initialize(bootstrap);
        ArtifactRepositoryProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
    }


    @Override
    protected List<Module> getServerModules()
    {
        return Arrays.asList(
                new InfoPageModule(),
                new DepotStoreServerModule(),
                new DepotStoreResourcesModule(),
                new ManageServicesModule(),
                new ManageDataStoreMongoModule(),
                new ManageAdminDataStoreMongoModule(),
                new StoreStatusModule(),
                new AdminSchedulesModule(),
                new AdminMetricsModule(),
                new AuthorisationModule(),
                new ArtifactsHandlersModule(),
                new ArtifactsRefreshModule(),
                new ArtifactsPurgeModule(),
                new RepositoryModule(),
                new TracingModule(),
                new NotificationsModule());
    }

    @Override
    protected GuiceBundle<DepotStoreServerConfiguration> buildGuiceBundle(List<Module> serverModules)
    {
        return GuiceBundle.defaultBuilder(DepotStoreServerConfiguration.class).modules(serverModules).build();
    }


    @Override
    public void run(DepotStoreServerConfiguration configuration, Environment environment)
    {
        super.run(configuration, environment);
        environment.jersey().register(LegendDepotStoreServerJacksonJsonProvider.class);
    }
}

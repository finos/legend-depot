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

import com.hubspot.dropwizard.guicier.GuiceBundle;
import io.dropwizard.setup.Bootstrap;
import org.finos.legend.depot.artifacts.repository.RepositoryModule;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.core.authorisation.AuthorisationModule;
import org.finos.legend.depot.core.http.BaseServer;
import org.finos.legend.depot.core.http.resources.InfoPageModule;
import org.finos.legend.depot.services.AdminServicesModule;
import org.finos.legend.depot.store.admin.StoreAdminModule;
import org.finos.legend.depot.store.artifacts.ArtifactsModule;
import org.finos.legend.depot.store.guice.DepotStoreResourcesModule;
import org.finos.legend.depot.store.guice.DepotStoreServerModule;
import org.finos.legend.depot.store.metrics.MetricsModule;
import org.finos.legend.depot.store.mongo.StoreMongoModule;
import org.finos.legend.depot.store.notifications.NotificationsModule;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.finos.legend.depot.store.status.StoreStatusModule;
import org.finos.legend.depot.tracing.TracingModule;

public class LegendDepotStoreServer extends BaseServer<DepotStoreServerConfiguration>
{
    public LegendDepotStoreServer()
    {
        this("/depot-store/api/*");
    }

    public LegendDepotStoreServer(String urlPattern)
    {
        super(urlPattern);
    }

    public static void main(String... args) throws Exception
    {
        new LegendDepotStoreServer().run(args);
    }

    @Override
    public void initialize(Bootstrap<DepotStoreServerConfiguration> bootstrap)
    {
        super.initialize(bootstrap);
        // artifact repo specific initialization
        ArtifactRepositoryProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
    }

    @Override
    protected GuiceBundle<DepotStoreServerConfiguration> buildGuiceBundle()
    {
        return GuiceBundle.defaultBuilder(DepotStoreServerConfiguration.class)
                .modules(new InfoPageModule())
                .modules(new StoreAdminModule())
                .modules(new AuthorisationModule())
                .modules(new StoreMongoModule())
                .modules(new AdminServicesModule())
                .modules(new DepotStoreServerModule())
                .modules(new DepotStoreResourcesModule())
                .modules(new StoreStatusModule())
                .modules(new ArtifactsModule())
                .modules(new RepositoryModule())
                .modules(new TracingModule())
                .modules(new MetricsModule())
                .modules(new NotificationsModule())
                .build();
    }

}

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
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.finos.legend.depot.core.server.BaseServer;
import org.finos.legend.depot.core.server.guice.ServerInfoModule;
import org.finos.legend.depot.core.services.guice.AuthorisationModule;
import org.finos.legend.depot.core.services.guice.MonitoringModule;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.services.guice.ArtifactsSchedulesModule;
import org.finos.legend.depot.services.guice.ArtifactsServicesModule;
import org.finos.legend.depot.services.guice.ManageCoreDataServicesModule;
import org.finos.legend.depot.services.guice.ManageEntitiesServicesModule;
import org.finos.legend.depot.services.guice.ManageGenerationsServicesModule;
import org.finos.legend.depot.services.guice.ManageQueryMetricsSchedulesModule;
import org.finos.legend.depot.services.guice.ManageSchedulesModule;
import org.finos.legend.depot.services.guice.ManageVersionedEntitiesServicesModule;
import org.finos.legend.depot.services.guice.NotificationsModule;
import org.finos.legend.depot.services.guice.NotificationsQueueSchedulesModule;
import org.finos.legend.depot.services.guice.NotificationsSchedulesModule;
import org.finos.legend.depot.services.guice.QueryMetricsModule;
import org.finos.legend.depot.services.guice.RepositoryModule;
import org.finos.legend.depot.services.guice.VersionReconciliationSchedulesModule;
import org.finos.legend.depot.store.mongo.core.MongoClientModule;
import org.finos.legend.depot.store.mongo.guice.ArtifactsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.CoreDataMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.EntitiesMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageCoreDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageEntitiesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageGenerationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.ManageMongoStoreSchedulesModule;
import org.finos.legend.depot.store.mongo.guice.ManageNotificationsQueueMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageQueryMetricsMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.ManageSchedulesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.NotificationsStoreMongoModule;
import org.finos.legend.depot.store.resources.guice.ArtifactsResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageCoreDataResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageSchedulesResourcesModule;
import org.finos.legend.depot.store.resources.guice.NotificationsResourcesModule;
import org.finos.legend.depot.store.resources.guice.RepositoryResourcesModule;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.finos.legend.depot.store.server.guice.DepotStoreServerModule;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.Arrays;
import java.util.EnumSet;
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
                new ServerInfoModule(),
                new DepotStoreServerModule(),

                new ManageCoreDataResourcesModule(),
                new ManageCoreDataServicesModule(),
                new ManageCoreDataStoreMongoModule(),
                new VersionReconciliationSchedulesModule(),
                new CoreDataMigrationsStoreMongoModule(),


                new ManageEntitiesServicesModule(),
                new ManageVersionedEntitiesServicesModule(),
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

                new NotificationsResourcesModule(),
                new NotificationsModule(),
                new NotificationsStoreMongoModule(),
                new NotificationsSchedulesModule(),

                new ManageNotificationsQueueMongoModule(),
                new NotificationsQueueSchedulesModule(),

                new QueryMetricsModule(),
                new ManageQueryMetricsSchedulesModule(),
                new ManageQueryMetricsMongoStoreModule(),

                new AuthorisationModule(),

                new MonitoringModule(),

                new MongoClientModule(),
                new ManageMongoStoreSchedulesModule(),
                new ManageMongoStoreModule()
              );
    }

    @Override
    protected void initialiseCors(Environment environment)
    {
        FilterRegistration.Dynamic corsFilter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_TIMING_ORIGINS_PARAM, "*");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Access-Control-Allow-Credentials,x-b3-parentspanid,x-b3-sampled,x-b3-spanid,x-b3-traceid");
        corsFilter.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, "false");
        corsFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "*");
    }
}

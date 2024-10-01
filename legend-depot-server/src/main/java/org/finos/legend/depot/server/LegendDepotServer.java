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

package org.finos.legend.depot.server;

import com.google.inject.Module;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.finos.legend.depot.core.server.BaseServer;
import org.finos.legend.depot.core.server.guice.ServerInfoModule;
import org.finos.legend.depot.server.configuration.DepotServerConfiguration;
import org.finos.legend.depot.server.guice.DepotServerModule;
import org.finos.legend.depot.server.resources.guice.CoreDataResourcesModule;
import org.finos.legend.depot.server.resources.guice.EntitiesResourcesModule;
import org.finos.legend.depot.server.resources.guice.GenerationsResourcesModule;
import org.finos.legend.depot.server.resources.guice.PureModelContextResourcesModule;
import org.finos.legend.depot.services.guice.EntitiesServicesModule;
import org.finos.legend.depot.services.guice.VersionedEntitiesServicesModule;
import org.finos.legend.depot.services.pure.model.context.guice.PureModelContextModule;
import org.finos.legend.depot.services.guice.CoreDataServicesModule;
import org.finos.legend.depot.services.guice.SchedulesModule;
import org.finos.legend.depot.services.guice.GenerationsServicesModule;
import org.finos.legend.depot.services.guice.QueryMetricsModule;
import org.finos.legend.depot.services.guice.QueryMetricsSchedulesModule;
import org.finos.legend.depot.store.mongo.guice.NotificationsQueueMongoModule;
import org.finos.legend.depot.store.mongo.guice.QueryMetricsMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.SchedulesStoreMongoModule;
import org.finos.legend.depot.store.mongo.core.MongoClientModule;
import org.finos.legend.depot.store.mongo.guice.CoreDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.EntitiesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.GenerationsStoreMongoModule;
import org.finos.legend.depot.core.services.guice.MonitoringModule;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class LegendDepotServer extends BaseServer<DepotServerConfiguration>
{
    public LegendDepotServer()
    {
        super();
    }

    public static void main(String... args) throws Exception
    {
        new LegendDepotServer().run(args);
    }

    protected List<Module> getServerModules()
    {
        return Arrays.asList(
                new ServerInfoModule(),
                new DepotServerModule(),

                new CoreDataResourcesModule(),
                new CoreDataServicesModule(),
                new CoreDataStoreMongoModule(),

                new EntitiesResourcesModule(),
                new EntitiesServicesModule(),
                new VersionedEntitiesServicesModule(),
                new EntitiesStoreMongoModule(),

                new GenerationsResourcesModule(),
                new GenerationsServicesModule(),
                new GenerationsStoreMongoModule(),

                new MongoClientModule(),

                new NotificationsQueueMongoModule(),

                new PureModelContextResourcesModule(),
                new PureModelContextModule(),

                new SchedulesModule(),
                new SchedulesStoreMongoModule(),

                new QueryMetricsModule(),
                new QueryMetricsMongoStoreModule(),
                new QueryMetricsSchedulesModule(),
                new MonitoringModule()
                );
    }

    @Override
    public void registerJacksonJsonProvider(JerseyEnvironment jerseyEnvironment)
    {
        jerseyEnvironment.register(new LegendDepotServerJacksonJsonProvider());
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

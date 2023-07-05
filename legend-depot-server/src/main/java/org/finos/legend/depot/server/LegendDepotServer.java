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
import com.hubspot.dropwizard.guicier.GuiceBundle;
import io.dropwizard.setup.Environment;
import org.finos.legend.depot.core.http.BaseServer;
import org.finos.legend.depot.core.http.resources.InfoPageModule;
import org.finos.legend.depot.server.configuration.DepotServerConfiguration;
import org.finos.legend.depot.server.guice.DepotServerModule;
import org.finos.legend.depot.server.guice.DepotServerResourcesModule;
import org.finos.legend.depot.server.pure.model.context.PureModelContextModule;
import org.finos.legend.depot.services.ReadDataServicesModule;
import org.finos.legend.depot.schedules.SchedulesModule;
import org.finos.legend.depot.store.metrics.MetricsModule;
import org.finos.legend.depot.store.mongo.DataStoreMongoModule;
import org.finos.legend.depot.store.mongo.admin.SchedulesStoreMongoModule;
import org.finos.legend.depot.store.notifications.queue.NotificationsQueueModule;
import org.finos.legend.depot.tracing.TracingModule;

import java.util.Arrays;
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
        return Arrays.asList(new InfoPageModule(),
                new DepotServerModule(),
                new DepotServerResourcesModule(),
                new ReadDataServicesModule(),
                new DataStoreMongoModule(),
                new PureModelContextModule(),
                new SchedulesModule(),
                new SchedulesStoreMongoModule(),
                new MetricsModule(),
                new TracingModule(),
                new NotificationsQueueModule());
    }

    @Override
    protected GuiceBundle<DepotServerConfiguration> buildGuiceBundle(List<Module> serverModules)
    {
        return GuiceBundle.defaultBuilder(DepotServerConfiguration.class).modules(serverModules).build();
    }

    @Override
    public void run(DepotServerConfiguration configuration, Environment environment)
    {
        super.run(configuration, environment);
        environment.jersey().register(LegendDepotServerJacksonJsonProvider.class);
    }
}

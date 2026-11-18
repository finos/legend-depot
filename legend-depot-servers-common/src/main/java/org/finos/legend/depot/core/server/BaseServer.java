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

package org.finos.legend.depot.core.server;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Module;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.servlet.jakarta.exporter.MetricsServlet;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.finos.legend.depot.core.server.error.CatchAllExceptionMapper;
import org.finos.legend.depot.core.server.error.DepotServerExceptionMapper;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;
import org.finos.legend.depot.store.StorageConfiguration;
import org.finos.legend.server.pac4j.LegendPac4jBundle;
import org.finos.legend.server.shared.bundles.ChainFixingFilterHandler;
import org.finos.legend.server.shared.bundles.HostnameHeaderBundle;
import org.finos.legend.server.shared.bundles.OpenTracingBundle;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import ru.vyarus.dropwizard.guice.GuiceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseServer<T extends ServerConfiguration> extends Application<T>
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BaseServer.class);
    public static final String SERVER_STARTED = "server started";

    protected BaseServer()
    {
    }


    @Override
    public void initialize(Bootstrap<T> bootstrap)
    {
        UnlimitedStreamConstraints.install(bootstrap.getObjectMapper());
        bootstrap.addBundle(new HostnameHeaderBundle());
        bootstrap.addBundle(new LegendPac4jBundle<>(ServerConfiguration::getPac4jConfiguration));
        bootstrap.addBundle(new ServerUrlSwaggerBundle<ServerConfiguration>()
        {
            @Override
            public void run(ServerConfiguration configuration, Environment environment) throws Exception
            {
                configuration.resolveSwaggerUriPrefix();
                super.run(configuration, environment);
                mountAssetsUnderJerseyPrefix(configuration, configuration.getSwaggerBundleConfiguration(), configuration.jerseyUriPrefix(), environment);
            }

            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ServerConfiguration configuration)
            {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(isEnvironmentVariableSubstitutionStrict())));
        bootstrap.addBundle(GuiceBundle.builder().modules(getServerModules().toArray(new Module[0])).build());
        configureObjectMapper(bootstrap);
    }

    protected boolean isEnvironmentVariableSubstitutionStrict()
    {
        return true;
    }

    protected void configureObjectMapper(Bootstrap<T> bootstrap)
    {
        StorageConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
        TracerProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
        PrometheusMetricsProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
    }

    protected abstract List<Module> getServerModules();

    @Override
    public void run(T configuration, Environment environment)
    {
        registerLifeCycleListener(configuration, environment);

        SessionHandler sessionHandler = new SessionHandler();
        if (configuration.getSessionCookie() != null)
        {
            sessionHandler.setSessionCookie(configuration.getSessionCookie());
        }
        environment.servlets().setSessionHandler(sessionHandler);

        if (configuration.getFilterPriorities() != null)
        {
            Map<String, Integer> filterPriorities = FilterPriorities.withCurrentFilterNames(configuration.getFilterPriorities());
            ChainFixingFilterHandler.apply(environment.getApplicationContext(), filterPriorities);
            warnOnUnknownFilterPriorities(configuration, environment, filterPriorities);
        }
        EncodedPathSeparators.allowOnApplicationConnectors(configuration.getServerFactory());
        EncodedPathSeparators.install(environment.getApplicationContext());

        if (configuration.getUrlPattern() != null && !configuration.getUrlPattern().isEmpty())
        {
            environment.jersey().setUrlPattern(configuration.getUrlPattern());
        }
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(new DepotServerExceptionMapper(configuration.getExceptionMapperConfiguration().includeStackTrace()));
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(new CatchAllExceptionMapper(configuration.getExceptionMapperConfiguration().includeStackTrace()));
        registerJacksonJsonProvider(environment.jersey());

        environment.healthChecks().register("HealthCheck", new HealthCheck()
        {
            @Override
            protected Result check()
            {
                return Result.healthy();
            }
        });


        initialiseCors(environment);
        initialisePrometheusMetrics(environment);
        initialiseOpenTracing(configuration, environment);
    }

    protected abstract void registerJacksonJsonProvider(JerseyEnvironment jerseyEnvironment);

    private void registerLifeCycleListener(T configuration, Environment environment)
    {
        environment.lifecycle().addEventListener(new LifeCycle.Listener()
        {
            private long shutDownStartTime = 0L;
            @Override
            public void lifeCycleStarting(LifeCycle event)
            {
                LOGGER.info("Starting {}", configuration.getApplicationName());
            }

            @Override
            public void lifeCycleStarted(LifeCycle event)
            {
                LOGGER.info("Started {} ", configuration.getApplicationName());
                PrometheusMetricsFactory.getInstance().incrementCount(SERVER_STARTED);
            }

            @Override
            public void lifeCycleFailure(LifeCycle event, Throwable cause)
            {
                LOGGER.error("Application {} failure : {}", configuration.getApplicationName(),cause.getMessage());
            }

            @Override
            public void lifeCycleStopping(LifeCycle event)
            {
                LOGGER.info("Stopping {}", configuration.getApplicationName());
                shutDownStartTime = System.currentTimeMillis();
            }

            @Override
            public void lifeCycleStopped(LifeCycle event)
            {
                LOGGER.info("Stopped {} in {} ", configuration.getApplicationName(), System.currentTimeMillis() - shutDownStartTime);
            }
        });
    }

    // every filter is registered by the time the server starts, and ChainFixingFilterHandler only reorders on the first request
    private void warnOnUnknownFilterPriorities(T configuration, Environment environment, Map<String, Integer> filterPriorities)
    {
        environment.lifecycle().addEventListener(new LifeCycle.Listener()
        {
            @Override
            public void lifeCycleStarting(LifeCycle event)
            {
                List<String> filterNames = Arrays.stream(environment.getApplicationContext().getServletHandler().getFilters()).map(FilterHolder::getName).collect(Collectors.toList());
                boolean tracingEnabled = configuration.getOpenTracingConfiguration() != null && configuration.getOpenTracingConfiguration().isEnabled();
                List<String> unknown = FilterPriorities.unknownFilterNames(filterPriorities.keySet(), filterNames, tracingEnabled);
                if (!unknown.isEmpty())
                {
                    LOGGER.warn("filterPriorities {} match no filter and are ignored; the filters are {}", unknown, filterNames);
                }
            }
        });
    }

    private void initialiseOpenTracing(T configuration, Environment environment)
    {
        new OpenTracingBundle().run(configuration, environment);
    }

    private void initialisePrometheusMetrics(Environment environment)
    {
        MetricRegistry metricRegistry = environment.metrics();
        CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        collectorRegistry.register(new DropwizardExports(metricRegistry));
        // simpleclient adds a <name>_created sample to every counter and summary, which depot does not export
        environment.admin().addServlet("prometheus", new MetricsServlet(collectorRegistry, name -> !name.endsWith("_created"))).addMapping("/prometheus");
    }

    protected abstract void initialiseCors(Environment environment);
}

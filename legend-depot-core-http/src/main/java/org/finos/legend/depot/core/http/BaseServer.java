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

package org.finos.legend.depot.core.http;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.hubspot.dropwizard.guicier.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.finos.legend.depot.tracing.configuration.PrometheusMetricsProviderConfiguration;
import org.finos.legend.depot.tracing.configuration.TracingAuthenticationProviderConfiguration;
import org.finos.legend.sdlc.server.error.LegendSDLCServerExceptionMapper;
import org.finos.legend.server.pac4j.LegendPac4jBundle;
import org.finos.legend.server.pac4j.LegendPac4jConfiguration;
import org.finos.legend.server.shared.bundles.ChainFixingFilterHandler;
import org.finos.legend.server.shared.bundles.HostnameHeaderBundle;
import org.finos.legend.server.shared.bundles.OpenTracingBundle;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.text.SimpleDateFormat;
import java.util.EnumSet;

public abstract class BaseServer<T extends ServersConfiguration> extends Application<T>
{

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final String urlPattern;

    protected BaseServer(String urlPattern)
    {
        this.urlPattern = urlPattern;
    }

    @Override
    public void initialize(Bootstrap<T> bootstrap)
    {

        bootstrap.addBundle(new HostnameHeaderBundle());
        bootstrap.addBundle(new LegendPac4jBundle<>(this::buildPac4jConfig));
        bootstrap.addBundle(new SwaggerBundle<ServersConfiguration>()
        {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ServersConfiguration configuration)
            {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(true)));

        bootstrap.getObjectMapper().setDateFormat(new SimpleDateFormat(SIMPLE_DATE_FORMAT));

        bootstrap.addBundle(buildGuiceBundle());

        TracingAuthenticationProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());

        PrometheusMetricsProviderConfiguration.configureObjectMapper(bootstrap.getObjectMapper());

    }

    protected abstract GuiceBundle<T> buildGuiceBundle();


    @Override
    public void run(T configuration, Environment environment)
    {
        SessionHandler sessionHandler = new SessionHandler();
        if (configuration.getSessionCookie() != null)
        {
            sessionHandler.setSessionCookie(configuration.getSessionCookie());
        }
        environment.servlets().setSessionHandler(sessionHandler);

        if (configuration.getFilterPriorities() != null)
        {
            ChainFixingFilterHandler.apply(environment.getApplicationContext(), configuration.getFilterPriorities());
        }

        if (urlPattern != null && !urlPattern.isEmpty())
        {
            environment.jersey().setUrlPattern(urlPattern);
        }
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(JacksonJaxbJsonProvider.class);
        environment.jersey().register(new LegendSDLCServerExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper(true));

        environment.healthChecks().register("Health Check", new HealthCheck()
        {
            @Override
            protected Result check()
            {
                return Result.healthy();
            }
        });

        initialiseCors(environment);

        initialisePrometheusMetrics(environment);

        initialiseOpenTracing(environment);

    }

    private void initialiseOpenTracing(Environment environment)
    {
        new OpenTracingBundle().run(environment);
    }

    private LegendPac4jConfiguration buildPac4jConfig(T config)
    {
        LegendPac4jConfiguration pac4j = config.getPac4jConfiguration();
        pac4j.setMongoDb(config.getMongoConfiguration().database);
        pac4j.setMongoUri(config.getMongoConfiguration().url);
        return pac4j;
    }


    private void initialisePrometheusMetrics(Environment environment)
    {
        MetricRegistry metricRegistry = environment.metrics();
        CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        collectorRegistry.register(new DropwizardExports(metricRegistry));
        environment.admin().addServlet("prometheus", new MetricsServlet(collectorRegistry)).addMapping("/prometheus");
    }

    private void initialiseCors(Environment environment)
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

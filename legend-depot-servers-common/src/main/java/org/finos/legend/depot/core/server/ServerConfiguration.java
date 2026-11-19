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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.server.DefaultServerFactory;
import io.dropwizard.core.server.ServerFactory;
import io.dropwizard.core.server.SimpleServerFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import org.finos.legend.depot.core.server.error.configuration.ExceptionMapperConfiguration;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.finos.legend.depot.store.StorageConfiguration;
import org.finos.legend.server.pac4j.LegendPac4jConfiguration;
import org.slf4j.Logger;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerConfiguration extends Configuration
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ServerConfiguration.class);

    // This can be set to avoid Jetty session cookie name collision between multiple servers running on `localhost` during development
    // See https://stackoverflow.com/questions/16789495/two-applications-on-the-same-server-use-the-same-jsessionid
    @JsonProperty("sessionCookie")
    private String sessionCookie;

    @NotNull
    @JsonProperty("applicationName")
    private String applicationName;

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @JsonProperty("pac4j")
    private LegendPac4jConfiguration pac4jConfiguration;

    @JsonProperty("filterPriorities")
    private Map<String, Integer> filterPriorities;

    @NotNull
    @JsonProperty("deployment")
    private String deployment;

    @NotNull
    @JsonProperty("storages")
    private List<StorageConfiguration> storages;

    @NotNull
    @JsonProperty("projects")
    private ProjectsConfiguration projects;

    @JsonProperty("openTracing")
    private OpenTracingConfiguration openTracingConfiguration;

    @JsonProperty("prometheus")
    private PrometheusConfiguration prometheusConfiguration;

    @JsonProperty("urlPattern")
    private String urlPattern;

    @JsonProperty("exceptionMapper")
    private ExceptionMapperConfiguration exceptionMapperConfiguration;

    public String getDeployment()
    {
        return deployment;
    }

    public List<StorageConfiguration> getStorageConfiguration()
    {
        return storages;
    }

    public ProjectsConfiguration getProjectsConfiguration()
    {
        return projects;
    }

    public void setStorage(List<StorageConfiguration> storages)
    {
        this.storages = storages;
    }

    public OpenTracingConfiguration getOpenTracingConfiguration()
    {
        return openTracingConfiguration;
    }

    public void setOpenTracingConfiguration(OpenTracingConfiguration openTracingConfiguration)
    {
        this.openTracingConfiguration = openTracingConfiguration;
    }

    public ExceptionMapperConfiguration getExceptionMapperConfiguration()
    {
        return exceptionMapperConfiguration != null ? exceptionMapperConfiguration : new ExceptionMapperConfiguration();
    }

    public void setExceptionMapperConfiguration(ExceptionMapperConfiguration exceptionMapperConfiguration)
    {
        this.exceptionMapperConfiguration = exceptionMapperConfiguration;
    }

    public String getSessionCookie()
    {
        return sessionCookie;
    }

    public String getApplicationName()
    {
        return this.applicationName;
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration()
    {
        return this.swaggerBundleConfiguration;
    }

    // urlPattern is applied to Jersey after the swagger bundle has run, so swagger only sees it through uriPrefix, and
    // swagger leaves the application context path out of an explicit uriPrefix. A configured uriPrefix that differs is
    // kept, as a reverse proxy that rewrites paths needs its public prefix there.
    void resolveSwaggerUriPrefix()
    {
        if (this.swaggerBundleConfiguration == null || this.urlPattern == null || this.urlPattern.isEmpty())
        {
            return;
        }
        String uriPrefix = withContextPath(getApplicationContextPath(), jerseyUriPrefix());
        String configuredUriPrefix = this.swaggerBundleConfiguration.getUriPrefix();
        if (configuredUriPrefix != null)
        {
            String configured = toUriPrefix(configuredUriPrefix);
            if (!uriPrefix.equals(configured))
            {
                LOGGER.warn("swagger uriPrefix {} differs from {}, where urlPattern {} is served; Swagger UI will send requests under {}", configuredUriPrefix, uriPrefix, this.urlPattern, configured);
            }
            uriPrefix = configured;
        }
        this.swaggerBundleConfiguration.setUriPrefix(uriPrefix);
    }

    String jerseyUriPrefix()
    {
        return this.urlPattern == null || this.urlPattern.isEmpty() ? null : toUriPrefix(this.urlPattern);
    }

    private String getApplicationContextPath()
    {
        ServerFactory serverFactory = getServerFactory();
        if (serverFactory instanceof DefaultServerFactory)
        {
            return ((DefaultServerFactory) serverFactory).getApplicationContextPath();
        }
        if (serverFactory instanceof SimpleServerFactory)
        {
            return ((SimpleServerFactory) serverFactory).getApplicationContextPath();
        }
        return null;
    }

    static String withContextPath(String applicationContextPath, String uriPrefix)
    {
        String contextPath = applicationContextPath == null ? "" : stripTrailingSlashes(applicationContextPath);
        if (contextPath.isEmpty())
        {
            return uriPrefix;
        }
        return "/".equals(uriPrefix) ? contextPath : contextPath + uriPrefix;
    }

    static String toUriPrefix(String urlPattern)
    {
        // Jersey treats "/api", "/api/" and "/api/*" alike
        String prefix = stripTrailingSlashes(urlPattern);
        prefix = stripTrailingSlashes(prefix.endsWith("/*") ? prefix.substring(0, prefix.length() - 2) : prefix);
        return prefix.isEmpty() ? "/" : prefix;
    }

    private static String stripTrailingSlashes(String path)
    {
        int end = path.length();
        while (end > 0 && path.charAt(end - 1) == '/')
        {
            end--;
        }
        return path.substring(0, end);
    }

    public LegendPac4jConfiguration getPac4jConfiguration()
    {
        return this.pac4jConfiguration;
    }

    public Map<String, Integer> getFilterPriorities()
    {
        return this.filterPriorities;
    }

    public PrometheusConfiguration getPrometheusConfiguration()
    {
        return prometheusConfiguration;
    }

    public void setPrometheusConfiguration(PrometheusConfiguration prometheusConfiguration)
    {
        this.prometheusConfiguration = prometheusConfiguration;
    }

    public String getUrlPattern()
    {
        return this.urlPattern;
    }
}

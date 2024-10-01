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
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import org.finos.legend.depot.core.server.error.configuration.ExceptionMapperConfiguration;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.finos.legend.depot.store.StorageConfiguration;
import org.finos.legend.server.pac4j.LegendPac4jConfiguration;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerConfiguration extends Configuration
{

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

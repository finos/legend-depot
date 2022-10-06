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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.finos.legend.depot.store.mongo.core.MongoConfiguration;
import org.finos.legend.depot.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.tracing.configuration.PrometheusConfiguration;
import org.finos.legend.server.pac4j.LegendPac4jConfiguration;

import javax.validation.constraints.NotNull;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServersConfiguration extends Configuration
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
    @JsonProperty("mongo")
    private MongoConfiguration mongo;

    @JsonProperty("openTracing")
    private OpenTracingConfiguration openTracingConfiguration;

    @JsonProperty("prometheus")
    private PrometheusConfiguration prometheusConfiguration;

    public String getDeployment()
    {
        return deployment;
    }

    public MongoConfiguration getMongoConfiguration()
    {
        return mongo;
    }

    public void setMongo(MongoConfiguration mongo)
    {
        this.mongo = mongo;
    }

    public OpenTracingConfiguration getOpenTracingConfiguration()
    {
        return openTracingConfiguration;
    }

    public void setOpenTracingConfiguration(OpenTracingConfiguration openTracingConfiguration)
    {
        this.openTracingConfiguration = openTracingConfiguration;
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
}

// Copyright 2026 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.depot.core.server;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.ConfigurationHelper;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.Collections;

/**
 * dropwizard-swagger 4's {@code SwaggerBundleConfiguration.build()} sets no {@code servers}, so
 * Swagger UI falls back to {@code /} and every "Try it out" request misses a non-root mount such as
 * {@code /depot/api}.
 *
 * <p>{@link #run} registers the context before {@code super.run}, whose {@code buildContext(true)}
 * then finds and reuses it, as {@code OpenApiResource} does.
 */
public abstract class ServerUrlSwaggerBundle<T extends Configuration> extends SwaggerBundle<T>
{
    @Override
    public void run(T configuration, Environment environment) throws Exception
    {
        SwaggerBundleConfiguration swaggerBundleConfiguration = getSwaggerBundleConfiguration(configuration);
        if (swaggerBundleConfiguration != null && swaggerBundleConfiguration.isEnabled())
        {
            new JaxrsOpenApiContextBuilder()
                    .openApiConfiguration(buildOpenApiConfiguration(configuration, swaggerBundleConfiguration))
                    .buildContext(true);
        }
        super.run(configuration, environment);
    }

    /**
     * {@code SwaggerBundle} mounts the Swagger UI assets under {@code uriPrefix} within the application context. Once
     * {@code uriPrefix} holds the application context path or a reverse proxy's public prefix, that misses where Jersey
     * serves its URL pattern, so the assets are also mounted under the Jersey prefix, as they are when the two agree.
     */
    static void mountAssetsUnderJerseyPrefix(Configuration configuration, SwaggerBundleConfiguration swaggerBundleConfiguration, String jerseyUriPrefix, Environment environment)
    {
        if (swaggerBundleConfiguration == null || !swaggerBundleConfiguration.isEnabled() || jerseyUriPrefix == null || jerseyUriPrefix.equals(swaggerBundleConfiguration.getUriPrefix()))
        {
            return;
        }
        String prefix = "/".equals(jerseyUriPrefix) ? "" : jerseyUriPrefix;
        new AssetsBundle("/swagger-static", prefix + "/swagger-static", null, "swagger-assets-jersey").run(configuration, environment);
        new AssetsBundle("/swagger-static/oauth2-redirect.html", prefix + "/oauth2-redirect.html", null, "swagger-oauth2-connect-jersey").run(configuration, environment);
    }

    static SwaggerConfiguration buildOpenApiConfiguration(Configuration configuration, SwaggerBundleConfiguration swaggerBundleConfiguration)
    {
        SwaggerConfiguration oasConfiguration = swaggerBundleConfiguration.build();
        String urlPattern = new ConfigurationHelper(configuration, swaggerBundleConfiguration).getUrlPattern();
        // ConfigurationHelper reports a root mount as "" or "/"; either way the default server is right
        if (!urlPattern.isEmpty() && !"/".equals(urlPattern))
        {
            oasConfiguration.getOpenAPI().setServers(Collections.singletonList(new Server().url(urlPattern)));
        }
        return oasConfiguration;
    }
}

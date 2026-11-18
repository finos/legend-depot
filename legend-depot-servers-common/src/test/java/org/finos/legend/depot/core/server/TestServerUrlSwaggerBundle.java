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

import io.dropwizard.core.Configuration;
import io.dropwizard.core.server.DefaultServerFactory;
import io.dropwizard.core.server.SimpleServerFactory;
import io.dropwizard.jackson.Jackson;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestServerUrlSwaggerBundle
{
    @Test
    public void uriPrefixBecomesTheServer()
    {
        SwaggerBundleConfiguration swagger = swagger();
        swagger.setUriPrefix("/api");
        Assertions.assertEquals(List.of("/api"), serverUrls(new Configuration(), swagger));
    }

    @Test
    public void jerseyRootPathBecomesTheServer()
    {
        Assertions.assertEquals(List.of("/api"), serverUrls(configuration("/", "/api/*"), swagger()));
    }

    @Test
    public void applicationContextPathIsIncluded()
    {
        Assertions.assertEquals(List.of("/ctx/api"), serverUrls(configuration("/ctx", "/api/*"), swagger()));
    }

    @Test
    public void rootMountAddsNoServer()
    {
        Assertions.assertTrue(serverUrls(new Configuration(), swagger()).isEmpty());
        Assertions.assertTrue(serverUrls(configuration("/", "/*"), swagger()).isEmpty());
    }

    @Test
    public void simpleServerWithUriPrefixBecomesTheServer()
    {
        SimpleServerFactory serverFactory = new SimpleServerFactory();
        serverFactory.setApplicationContextPath("/");
        Configuration configuration = new Configuration();
        configuration.setServerFactory(serverFactory);
        SwaggerBundleConfiguration swagger = swagger();
        swagger.setUriPrefix("/depot-store/api");
        Assertions.assertEquals(List.of("/depot-store/api"), serverUrls(configuration, swagger));
    }

    @Test
    public void urlPatternIsTheDefaultUriPrefix() throws Exception
    {
        ServerConfiguration configuration = serverConfiguration("{\"urlPattern\": \"/depot/api/*\", \"swagger\": {\"resourcePackage\": \"org.finos.legend\"}}");
        SwaggerBundleConfiguration swagger = configuration.getSwaggerBundleConfiguration();
        Assertions.assertNull(swagger.getUriPrefix(), "reading the configuration must not change it");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/depot/api", swagger.getUriPrefix());
        Assertions.assertEquals(List.of("/depot/api"), serverUrls(configuration, swagger));
    }

    @Test
    public void matchingUriPrefixIsNormalised() throws Exception
    {
        ServerConfiguration configuration = serverConfiguration("{\"urlPattern\": \"/depot/api/*\", \"swagger\": {\"resourcePackage\": \"org.finos.legend\", \"uriPrefix\": \"/depot/api/\"}}");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/depot/api", configuration.getSwaggerBundleConfiguration().getUriPrefix());
    }

    @Test
    public void uriPrefixThatDisagreesWithUrlPatternIsKept() throws Exception
    {
        // e.g. the public prefix behind a reverse proxy that rewrites paths
        ServerConfiguration configuration = serverConfiguration("{\"urlPattern\": \"/depot/api/*\", \"swagger\": {\"resourcePackage\": \"org.finos.legend\", \"uriPrefix\": \"/other/\"}}");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/other", configuration.getSwaggerBundleConfiguration().getUriPrefix());
    }

    @Test
    public void applicationContextPathIsPrefixedToTheDefaultUriPrefix() throws Exception
    {
        ServerConfiguration configuration = serverConfiguration("{\"server\": {\"type\": \"default\", \"applicationContextPath\": \"/legend\"}, \"urlPattern\": \"/depot/api/*\", \"swagger\": {\"resourcePackage\": \"org.finos.legend\"}}");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/legend/depot/api", configuration.getSwaggerBundleConfiguration().getUriPrefix());
        Assertions.assertEquals(List.of("/legend/depot/api"), serverUrls(configuration, configuration.getSwaggerBundleConfiguration()));
    }

    @Test
    public void simpleServerApplicationContextPathIsPrefixedToTheDefaultUriPrefix() throws Exception
    {
        ServerConfiguration configuration = serverConfiguration("{\"server\": {\"type\": \"simple\", \"applicationContextPath\": \"/legend/\"}, \"urlPattern\": \"/depot-store/api/*\", \"swagger\": {\"resourcePackage\": \"org.finos.legend\"}}");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/legend/depot-store/api", configuration.getSwaggerBundleConfiguration().getUriPrefix());
    }

    @Test
    public void uriPrefixThatIncludesTheApplicationContextPathIsKept() throws Exception
    {
        ServerConfiguration configuration = serverConfiguration("{\"server\": {\"type\": \"default\", \"applicationContextPath\": \"/legend\"}, \"urlPattern\": \"/depot/api/*\", \"swagger\": {\"resourcePackage\": \"org.finos.legend\", \"uriPrefix\": \"/legend/depot/api\"}}");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/legend/depot/api", configuration.getSwaggerBundleConfiguration().getUriPrefix());
    }

    @Test
    public void uriPrefixUnderAnApplicationContextPath()
    {
        Assertions.assertEquals("/depot/api", ServerConfiguration.withContextPath(null, "/depot/api"));
        Assertions.assertEquals("/depot/api", ServerConfiguration.withContextPath("/", "/depot/api"));
        Assertions.assertEquals("/legend/depot/api", ServerConfiguration.withContextPath("/legend", "/depot/api"));
        Assertions.assertEquals("/legend/depot/api", ServerConfiguration.withContextPath("/legend/", "/depot/api"));
        Assertions.assertEquals("/legend", ServerConfiguration.withContextPath("/legend", "/"));
        Assertions.assertEquals("/", ServerConfiguration.withContextPath("/", "/"));
    }

    @Test
    public void uriPrefixWithoutUrlPatternIsKept() throws Exception
    {
        ServerConfiguration configuration = serverConfiguration("{\"swagger\": {\"resourcePackage\": \"org.finos.legend\", \"uriPrefix\": \"/other\"}}");
        configuration.resolveSwaggerUriPrefix();
        Assertions.assertEquals("/other", configuration.getSwaggerBundleConfiguration().getUriPrefix());
    }

    @Test
    public void urlPatternToUriPrefix()
    {
        Assertions.assertEquals("/depot/api", ServerConfiguration.toUriPrefix("/depot/api/*"));
        Assertions.assertEquals("/depot/api", ServerConfiguration.toUriPrefix("/depot/api"));
        Assertions.assertEquals("/depot/api", ServerConfiguration.toUriPrefix("/depot/api/"));
        Assertions.assertEquals("/depot/api", ServerConfiguration.toUriPrefix("/depot/api//"));
        Assertions.assertEquals("/depot/api", ServerConfiguration.toUriPrefix("/depot/api/*/"));
        Assertions.assertEquals("/", ServerConfiguration.toUriPrefix("/*"));
        Assertions.assertEquals("/", ServerConfiguration.toUriPrefix("/"));
    }

    private static ServerConfiguration serverConfiguration(String json) throws Exception
    {
        return Jackson.newObjectMapper().readValue(json, ServerConfiguration.class);
    }

    private static SwaggerBundleConfiguration swagger()
    {
        SwaggerBundleConfiguration swagger = new SwaggerBundleConfiguration();
        swagger.setResourcePackage("org.finos.legend");
        return swagger;
    }

    private static Configuration configuration(String applicationContextPath, String jerseyRootPath)
    {
        DefaultServerFactory serverFactory = new DefaultServerFactory();
        serverFactory.setApplicationContextPath(applicationContextPath);
        serverFactory.setJerseyRootPath(jerseyRootPath);
        Configuration configuration = new Configuration();
        configuration.setServerFactory(serverFactory);
        return configuration;
    }

    private static List<String> serverUrls(Configuration configuration, SwaggerBundleConfiguration swagger)
    {
        List<Server> servers = ServerUrlSwaggerBundle.buildOpenApiConfiguration(configuration, swagger).getOpenAPI().getServers();
        return servers == null ? List.of() : servers.stream().map(Server::getUrl).collect(Collectors.toList());
    }
}

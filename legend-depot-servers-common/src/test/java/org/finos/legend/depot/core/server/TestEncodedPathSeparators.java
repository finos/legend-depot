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

import io.dropwizard.core.server.DefaultServerFactory;
import io.dropwizard.core.server.SimpleServerFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.finos.legend.server.shared.bundles.ChainFixingFilterHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestEncodedPathSeparators
{
    private static final String SECURITY_FILTER = "org.pac4j.jee.filter.SecurityFilter";
    private static final List<String> SEEN_BY_SECURITY = new CopyOnWriteArrayList<>();
    private static Server jetty;
    private static String base;

    @BeforeAll
    public static void startJetty() throws Exception
    {
        jetty = new Server();
        ServerConnector connector = new ServerConnector(jetty);
        connector.setPort(0);
        connector.getConnectionFactory(HttpConnectionFactory.class).getHttpConfiguration()
                .setUriCompliance(EncodedPathSeparators.allow(UriCompliance.DEFAULT));
        jetty.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        ChainFixingFilterHandler.apply(context, Collections.singletonMap(SECURITY_FILTER, 3));
        EncodedPathSeparators.install(context);
        Filter security = (request, response, chain) ->
        {
            SEEN_BY_SECURITY.add(((HttpServletRequest) request).getRequestURI());
            chain.doFilter(request, response);
        };
        FilterHolder securityHolder = new FilterHolder(security);
        securityHolder.setName(SECURITY_FILTER);
        context.addFilter(securityHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(new ServletHolder(new HttpServlet()
        {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
            {
                // Jersey's ServletContainer reads both, and Jetty answers 400 to them for an ambiguous URI unless the
                // servlet handler decodes it
                request.getServletPath();
                request.getPathInfo();
                response.getWriter().print(request.getRequestURI());
            }
        }), "/depot/api/*");
        jetty.setHandler(context);
        jetty.start();
        base = "http://localhost:" + connector.getLocalPort();
    }

    @AfterAll
    public static void stopJetty() throws Exception
    {
        jetty.stop();
    }

    @Test
    public void anEncodedFilePathPassesUnchanged() throws Exception
    {
        assertPassesUnchanged("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2Fmodel%2FThing.json");
        assertPassesUnchanged("/depot/api/generationFileContent/org.finos/test/versions/1.0.0/file/%2fmodel%2fThing.json");
        assertPassesUnchanged("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2Fmodel/%2FThing.json");
    }

    @Test
    public void anEncodedPathMayNotHoldDotSegments() throws Exception
    {
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2F..%2F..%2F..%2F..%2F..%2Finfo");
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/..%2F..%2Finfo");
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2Fmodel%2F.%2FThing.json");
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2Fmodel%2F..");
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2F%2E%2E%2Finfo");
        assertRejected("/depot/api/info%2F..%2F..%2Fprojects");
    }

    @Test
    public void otherAmbiguousPathsAreStillRejected() throws Exception
    {
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/%25model");
        assertRejected("//depot/api/info");
        assertRejected("/depot/api/generations/org.finos/test/versions/1.0.0/file/%2E%2E/info");
    }

    @Test
    public void pathsWithoutAnEncodedSeparatorAreUntouched() throws Exception
    {
        assertPassesUnchanged("/depot/api/info");
        assertPassesUnchanged("/depot/api/generations/org.finos/test/versions/1.0.0/file/Thing.json");
    }

    @Test
    public void theRuleItself()
    {
        Assertions.assertTrue(EncodedPathSeparators.isAllowed("/depot/api/info"));
        Assertions.assertTrue(EncodedPathSeparators.isAllowed("/depot/api/file/%2Fa%2Fb"));
        Assertions.assertTrue(EncodedPathSeparators.isAllowed("/depot/api/file/%2Fa%2F..b"));
        Assertions.assertTrue(EncodedPathSeparators.isAllowed("/depot/api/file/ABC%2f123"));
        Assertions.assertFalse(EncodedPathSeparators.isAllowed("/depot/api/file/%2Fa%2F..%2Fb"));
        Assertions.assertFalse(EncodedPathSeparators.isAllowed("/depot/api/file/%2Fa%2F%2e%2E%2Fb"));
        Assertions.assertFalse(EncodedPathSeparators.isAllowed("/depot/api/file/%2Fa%2"));
    }

    @Test
    public void theServletHandlerDecodesWithoutTheChainFixer()
    {
        ServletContextHandler context = new ServletContextHandler();
        EncodedPathSeparators.install(context);
        Assertions.assertTrue(context.getServletHandler().isDecodeAmbiguousURIs());
        Assertions.assertEquals(EncodedPathSeparators.GUARD_NAME, context.getServletHandler().getFilterMappings()[0].getFilterName());
    }

    @Test
    public void everyApplicationConnectorOfADefaultServerTakesAnEncodedSeparator()
    {
        DefaultServerFactory factory = new DefaultServerFactory();
        HttpConnectorFactory admin = (HttpConnectorFactory) factory.getAdminConnectors().get(0);
        UriCompliance adminBefore = admin.getUriCompliance();

        EncodedPathSeparators.allowOnApplicationConnectors(factory);

        HttpConnectorFactory application = (HttpConnectorFactory) factory.getApplicationConnectors().get(0);
        Assertions.assertTrue(application.getUriCompliance().allows(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR));
        Assertions.assertFalse(application.getUriCompliance().allows(UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING));
        Assertions.assertEquals(adminBefore, admin.getUriCompliance());
    }

    @Test
    public void theConnectorOfASimpleServerTakesAnEncodedSeparatorAndKeepsWhatWasConfigured()
    {
        SimpleServerFactory factory = new SimpleServerFactory();
        HttpConnectorFactory connector = (HttpConnectorFactory) factory.getConnector();
        connector.setUriCompliance(UriCompliance.DEFAULT.with("configured", UriCompliance.Violation.SUSPICIOUS_PATH_CHARACTERS));

        EncodedPathSeparators.allowOnApplicationConnectors(factory);

        Assertions.assertTrue(connector.getUriCompliance().allows(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR));
        Assertions.assertTrue(connector.getUriCompliance().allows(UriCompliance.Violation.SUSPICIOUS_PATH_CHARACTERS));
    }

    private static void assertPassesUnchanged(String path) throws Exception
    {
        HttpResponse<String> response = get(path);
        Assertions.assertEquals(200, response.statusCode(), path);
        Assertions.assertEquals(path, response.body());
        Assertions.assertTrue(SEEN_BY_SECURITY.contains(path), path);
    }

    private static void assertRejected(String path) throws Exception
    {
        Assertions.assertEquals(400, get(path).statusCode(), path);
        Assertions.assertFalse(SEEN_BY_SECURITY.contains(path), path);
    }

    private static HttpResponse<String> get(String path) throws Exception
    {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(base + path)).build(), HttpResponse.BodyHandlers.ofString());
    }
}

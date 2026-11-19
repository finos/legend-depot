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
import io.dropwizard.core.server.ServerFactory;
import io.dropwizard.core.server.SimpleServerFactory;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import jakarta.annotation.Priority;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.FilterMapping;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.http.UriCompliance;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Lets a path carry an encoded {@code /} ({@code %2F}) again, as Jetty 9.4 did. Jetty 12's
 * {@code UriCompliance.DEFAULT} and its servlet layer answer 400 to it, and clients such as legend-studio send
 * file generation paths, which start with {@code /}, as one encoded path segment.
 *
 * <p>{@link Guard} still answers 400, ahead of every other filter, to a {@code %2F} path whose decoded form has a
 * {@code .} or {@code ..} segment. Jetty 9.4 normalised those after decoding; Jetty 12 does not, so decoded they
 * would climb out of the route.
 */
final class EncodedPathSeparators
{
    private static final Pattern ENCODED_SEPARATOR = Pattern.compile("%2F", Pattern.CASE_INSENSITIVE);
    static final String GUARD_NAME = "EncodedPathSeparatorGuard";

    private EncodedPathSeparators()
    {
    }

    static UriCompliance allow(UriCompliance compliance)
    {
        return compliance.allows(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR)
                ? compliance
                : compliance.with(compliance.getName() + ",AMBIGUOUS_PATH_SEPARATOR", UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR);
    }

    /**
     * In code because a custom compliance string in the server config deserializes to null. Admin connectors are
     * left alone. Must run before Dropwizard builds the server, as {@code Application.run} does.
     */
    static void allowOnApplicationConnectors(ServerFactory serverFactory)
    {
        List<ConnectorFactory> connectors = serverFactory instanceof DefaultServerFactory
                ? ((DefaultServerFactory) serverFactory).getApplicationConnectors()
                : serverFactory instanceof SimpleServerFactory
                ? Collections.singletonList(((SimpleServerFactory) serverFactory).getConnector())
                : Collections.emptyList();
        for (ConnectorFactory connector : connectors)
        {
            if (connector instanceof HttpConnectorFactory)
            {
                HttpConnectorFactory http = (HttpConnectorFactory) connector;
                http.setUriCompliance(allow(http.getUriCompliance()));
            }
        }
    }

    static void install(ServletContextHandler context)
    {
        ServletHandler handler = context.getServletHandler();
        handler.setDecodeAmbiguousURIs(true);

        FilterHolder holder = new FilterHolder(new Guard());
        holder.setName(GUARD_NAME);
        handler.prependFilter(holder);
        FilterMapping mapping = new FilterMapping();
        mapping.setFilterName(GUARD_NAME);
        mapping.setPathSpec("/*");
        mapping.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
        handler.prependFilterMapping(mapping);
    }

    /**
     * @param requestUri the raw, still-encoded path
     */
    static boolean isAllowed(String requestUri)
    {
        return !ENCODED_SEPARATOR.matcher(requestUri).find() || hasNoDotSegment(requestUri);
    }

    private static boolean hasNoDotSegment(String encodedPath)
    {
        String decoded;
        try
        {
            decoded = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
        for (String segment : decoded.split("/", -1))
        {
            if (segment.equals(".") || segment.equals(".."))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Runs first: the highest priority for {@code ChainFixingFilterHandler}, and prepended when it is not applied.
     */
    @Priority(Integer.MAX_VALUE)
    static final class Guard implements Filter
    {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
        {
            if (!isAllowed(((HttpServletRequest) request).getRequestURI()))
            {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Ambiguous URI path separator");
                return;
            }
            chain.doFilter(request, response);
        }
    }
}

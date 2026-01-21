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

package org.finos.legend.depot.store.server;

import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LegendDepotStoreServerClaude_initialiseCorsTest
{
    @Test
    public void testInitialiseCorsAddsFilterToEnvironment()
    {
        // Test that initialiseCors adds a CORS filter to the servlet environment
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(eq("CORS"), eq(CrossOriginFilter.class))).thenReturn(corsFilter);

        // Act
        server.initialiseCors(environment);

        // Assert
        verify(servletEnvironment, times(1)).addFilter("CORS", CrossOriginFilter.class);
    }

    @Test
    public void testInitialiseCorsWithNonNullEnvironment()
    {
        // Test that initialiseCors works with a valid Environment
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        // Should not throw any exception
        Assertions.assertDoesNotThrow(() -> server.initialiseCors(environment));
    }

    @Test
    public void testInitialiseCorsSetsAllowedMethods()
    {
        // Test that initialiseCors sets the ALLOWED_METHODS_PARAM
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter, times(1)).setInitParameter(
            CrossOriginFilter.ALLOWED_METHODS_PARAM,
            "GET,PUT,POST,DELETE,OPTIONS"
        );
    }

    @Test
    public void testInitialiseCorsAllowsAllOrigins()
    {
        // Test that initialiseCors sets ALLOWED_ORIGINS_PARAM to allow all origins
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter, times(1)).setInitParameter(
            CrossOriginFilter.ALLOWED_ORIGINS_PARAM,
            "*"
        );
    }

    @Test
    public void testInitialiseCorsAllowsAllTimingOrigins()
    {
        // Test that initialiseCors sets ALLOWED_TIMING_ORIGINS_PARAM to allow all timing origins
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter, times(1)).setInitParameter(
            CrossOriginFilter.ALLOWED_TIMING_ORIGINS_PARAM,
            "*"
        );
    }

    @Test
    public void testInitialiseCorsConfiguresAllowedHeaders()
    {
        // Test that initialiseCors sets the ALLOWED_HEADERS_PARAM with all required headers
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter, times(1)).setInitParameter(
            CrossOriginFilter.ALLOWED_HEADERS_PARAM,
            "X-Requested-With,Content-Type,Accept,Origin,Access-Control-Allow-Credentials,x-b3-parentspanid,x-b3-sampled,x-b3-spanid,x-b3-traceid"
        );
    }

    @Test
    public void testInitialiseCorsDisablesPreflightChaining()
    {
        // Test that initialiseCors sets CHAIN_PREFLIGHT_PARAM to false
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter, times(1)).setInitParameter(
            CrossOriginFilter.CHAIN_PREFLIGHT_PARAM,
            "false"
        );
    }

    @Test
    public void testInitialiseCorsAddsMappingForAllUrls()
    {
        // Test that initialiseCors adds URL pattern mapping for all paths
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter, times(1)).addMappingForUrlPatterns(
            eq(EnumSet.of(DispatcherType.REQUEST)),
            eq(false),
            eq("*")
        );
    }

    @Test
    public void testInitialiseCorsCallsServletsMethod()
    {
        // Test that initialiseCors calls environment.servlets()
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(environment, times(1)).servlets();
    }

    @Test
    public void testInitialiseCorsConfiguresFilterWithCorrectName()
    {
        // Test that the filter is added with the name "CORS"
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(servletEnvironment).addFilter(nameCaptor.capture(), classCaptor.capture());
        Assertions.assertEquals("CORS", nameCaptor.getValue());
        Assertions.assertEquals(CrossOriginFilter.class, classCaptor.getValue());
    }

    @Test
    public void testInitialiseCorsConfiguresAllParameters()
    {
        // Test that initialiseCors sets all 5 init parameters
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        // Verify all 5 parameters were set
        verify(corsFilter, times(5)).setInitParameter(anyString(), anyString());
    }

    @Test
    public void testInitialiseCorsWithMultipleServerInstances()
    {
        // Test that different server instances can each initialize CORS
        LegendDepotStoreServer server1 = new LegendDepotStoreServer();
        LegendDepotStoreServer server2 = new LegendDepotStoreServer();

        Environment environment1 = Mockito.mock(Environment.class);
        Environment environment2 = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment1 = Mockito.mock(ServletEnvironment.class);
        ServletEnvironment servletEnvironment2 = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter1 = Mockito.mock(FilterRegistration.Dynamic.class);
        FilterRegistration.Dynamic corsFilter2 = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment1.servlets()).thenReturn(servletEnvironment1);
        when(environment2.servlets()).thenReturn(servletEnvironment2);
        when(servletEnvironment1.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter1);
        when(servletEnvironment2.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter2);

        server1.initialiseCors(environment1);
        server2.initialiseCors(environment2);

        verify(servletEnvironment1, times(1)).addFilter("CORS", CrossOriginFilter.class);
        verify(servletEnvironment2, times(1)).addFilter("CORS", CrossOriginFilter.class);
    }

    @Test
    public void testInitialiseCorsCanBeCalledMultipleTimes()
    {
        // Test that initialiseCors can be called multiple times on different environments
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        Environment environment1 = Mockito.mock(Environment.class);
        Environment environment2 = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment1 = Mockito.mock(ServletEnvironment.class);
        ServletEnvironment servletEnvironment2 = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter1 = Mockito.mock(FilterRegistration.Dynamic.class);
        FilterRegistration.Dynamic corsFilter2 = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment1.servlets()).thenReturn(servletEnvironment1);
        when(environment2.servlets()).thenReturn(servletEnvironment2);
        when(servletEnvironment1.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter1);
        when(servletEnvironment2.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter2);

        server.initialiseCors(environment1);
        server.initialiseCors(environment2);

        verify(servletEnvironment1, times(1)).addFilter("CORS", CrossOriginFilter.class);
        verify(servletEnvironment2, times(1)).addFilter("CORS", CrossOriginFilter.class);
    }

    @Test
    public void testInitialiseCorsAllowedHeadersIncludesStandardHeaders()
    {
        // Test that allowed headers includes standard headers like Content-Type and Accept
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter).setInitParameter(eq(CrossOriginFilter.ALLOWED_HEADERS_PARAM), valueCaptor.capture());
        String allowedHeaders = valueCaptor.getValue();

        Assertions.assertTrue(allowedHeaders.contains("Content-Type"));
        Assertions.assertTrue(allowedHeaders.contains("Accept"));
        Assertions.assertTrue(allowedHeaders.contains("Origin"));
    }

    @Test
    public void testInitialiseCorsAllowedHeadersIncludesTracingHeaders()
    {
        // Test that allowed headers includes distributed tracing headers (x-b3-*)
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter).setInitParameter(eq(CrossOriginFilter.ALLOWED_HEADERS_PARAM), valueCaptor.capture());
        String allowedHeaders = valueCaptor.getValue();

        Assertions.assertTrue(allowedHeaders.contains("x-b3-parentspanid"));
        Assertions.assertTrue(allowedHeaders.contains("x-b3-sampled"));
        Assertions.assertTrue(allowedHeaders.contains("x-b3-spanid"));
        Assertions.assertTrue(allowedHeaders.contains("x-b3-traceid"));
    }

    @Test
    public void testInitialiseCorsMethodIsProtected()
    {
        // Test that initialiseCors is a protected method (following BaseServer pattern)
        try
        {
            java.lang.reflect.Method method = LegendDepotStoreServer.class.getDeclaredMethod(
                "initialiseCors",
                Environment.class
            );
            int modifiers = method.getModifiers();

            // The method should be protected
            Assertions.assertTrue(java.lang.reflect.Modifier.isProtected(modifiers));
        }
        catch (NoSuchMethodException e)
        {
            Assertions.fail("initialiseCors method should exist");
        }
    }

    @Test
    public void testInitialiseCorsMethodIsVoid()
    {
        // Test that initialiseCors returns void
        try
        {
            java.lang.reflect.Method method = LegendDepotStoreServer.class.getDeclaredMethod(
                "initialiseCors",
                Environment.class
            );

            Assertions.assertEquals(void.class, method.getReturnType());
        }
        catch (NoSuchMethodException e)
        {
            Assertions.fail("initialiseCors method should exist");
        }
    }

    @Test
    public void testInitialiseCorsUsesRequestDispatcherType()
    {
        // Test that the URL mapping uses DispatcherType.REQUEST
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        ArgumentCaptor<EnumSet<DispatcherType>> dispatcherCaptor = ArgumentCaptor.forClass(EnumSet.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        server.initialiseCors(environment);

        verify(corsFilter).addMappingForUrlPatterns(
            dispatcherCaptor.capture(),
            anyBoolean(),
            anyString()
        );

        EnumSet<DispatcherType> dispatcherTypes = dispatcherCaptor.getValue();
        Assertions.assertTrue(dispatcherTypes.contains(DispatcherType.REQUEST));
        Assertions.assertEquals(1, dispatcherTypes.size());
    }

    @Test
    public void testInitialiseCorsDoesNotThrowException()
    {
        // Test that initialiseCors completes without throwing an exception
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Environment environment = Mockito.mock(Environment.class);
        ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);
        FilterRegistration.Dynamic corsFilter = Mockito.mock(FilterRegistration.Dynamic.class);

        when(environment.servlets()).thenReturn(servletEnvironment);
        when(servletEnvironment.addFilter(anyString(), any(Class.class))).thenReturn(corsFilter);

        Assertions.assertDoesNotThrow(() -> server.initialiseCors(environment));
    }
}

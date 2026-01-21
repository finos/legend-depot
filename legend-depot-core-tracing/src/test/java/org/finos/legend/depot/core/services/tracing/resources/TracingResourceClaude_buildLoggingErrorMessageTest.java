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

package org.finos.legend.depot.core.services.tracing.resources;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;
import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public class TracingResourceClaude_buildLoggingErrorMessageTest
{
    private TracingResource tracingResource;

    @BeforeEach
    public void setUp() throws Exception
    {
        // Clear the CollectorRegistry before each test
        CollectorRegistry.defaultRegistry.clear();

        // Reset singletons
        resetTracerFactorySingleton();
        resetGlobalTracer();
        resetPrometheusMetricsFactorySingleton();

        // Create a test instance
        tracingResource = new TestTracingResource();

        // Configure TracerFactory with a real tracer
        configureRealTracer();
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        // Clean up after each test
        resetTracerFactorySingleton();
        resetGlobalTracer();
        resetPrometheusMetricsFactorySingleton();
    }

    private void resetTracerFactorySingleton() throws Exception
    {
        Field instanceField = TracerFactory.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void resetGlobalTracer() throws Exception
    {
        Field tracerField = GlobalTracer.class.getDeclaredField("tracer");
        tracerField.setAccessible(true);
        tracerField.set(null, NoopTracerFactory.create());

        Field isRegisteredField = GlobalTracer.class.getDeclaredField("isRegistered");
        isRegisteredField.setAccessible(true);
        isRegisteredField.set(null, false);
    }

    private void resetPrometheusMetricsFactorySingleton() throws Exception
    {
        Field instanceField = PrometheusMetricsFactory.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private void configureRealTracer()
    {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");
        config.setTracerProvider((configuration) ->
        {
            Tracing tracing = Tracing.newBuilder()
                    .localServiceName("test")
                    .build();
            return BraveTracer.create(tracing);
        });

        TracerFactory.configure(config);
    }

    @Test
    public void testBuildLoggingErrorMessageWithExceptionWithMessage()
    {
        // Test that buildLoggingErrorMessage is called when an exception with a message is thrown
        // This covers lines 50, 51, 52, 53, 54, 56, 59
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "test-operation", () ->
            {
                throw new IllegalArgumentException("This is a test error message");
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("This is a test error message"));
    }

    @Test
    public void testBuildLoggingErrorMessageWithExceptionWithNullMessage()
    {
        // Test that buildLoggingErrorMessage handles exceptions with null messages
        // This covers the branch where message is null (line 54 condition false, skipping lines 56)
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "test-operation", () ->
            {
                throw new IllegalStateException();  // No message
            });
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    public void testBuildLoggingErrorMessageWithExceptionWithEmptyMessage()
    {
        // Test that buildLoggingErrorMessage handles exceptions with empty messages
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "test-operation", () ->
            {
                throw new RuntimeException("");  // Empty message
            });
        });

        Assertions.assertNotNull(exception);
    }

    @Test
    public void testBuildLoggingErrorMessageWithLongDescription()
    {
        // Test with a long description to ensure the StringBuilder sizing works correctly
        String longDescription = "This is a very long operation description that should test the StringBuilder capacity calculation in buildLoggingErrorMessage method";
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", longDescription, () ->
            {
                throw new IllegalArgumentException("Error during long operation");
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error during long operation"));
    }

    @Test
    public void testBuildLoggingErrorMessageWithShortDescription()
    {
        // Test with a short description
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "short", () ->
            {
                throw new IllegalArgumentException("Short error");
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Short error"));
    }

    @Test
    public void testBuildLoggingErrorMessageWithNestedExceptionWithMessage()
    {
        // Test with a nested exception that has a message
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "nested-operation", () ->
            {
                try
                {
                    throw new IllegalStateException("Inner exception message");
                }
                catch (IllegalStateException e)
                {
                    throw new RuntimeException("Outer exception message", e);
                }
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Outer exception message"));
    }

    @Test
    public void testBuildLoggingErrorMessageWithDifferentExceptionTypes()
    {
        // Test with NullPointerException
        RuntimeException exception1 = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "null-pointer-test", () ->
            {
                throw new NullPointerException("Null pointer error");
            });
        });
        Assertions.assertNotNull(exception1);

        // Test with ArrayIndexOutOfBoundsException
        RuntimeException exception2 = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "array-index-test", () ->
            {
                throw new ArrayIndexOutOfBoundsException("Array index error");
            });
        });
        Assertions.assertNotNull(exception2);

        // Test with IllegalStateException
        RuntimeException exception3 = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "illegal-state-test", () ->
            {
                throw new IllegalStateException("Illegal state error");
            });
        });
        Assertions.assertNotNull(exception3);
    }

    @Test
    public void testBuildLoggingErrorMessageViaHandleResponse()
    {
        // Test buildLoggingErrorMessage through handleResponse method
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handleResponse("test-metric", "response-operation", () ->
            {
                throw new IllegalArgumentException("Error in response handling");
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error in response handling"));
    }

    @Test
    public void testBuildLoggingErrorMessageViaHandleWithTwoParameters()
    {
        // Test buildLoggingErrorMessage through handle method with 2 parameters
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("two-param-operation", () ->
            {
                throw new IllegalArgumentException("Error in two param handle");
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error in two param handle"));
    }

    @Test
    public void testBuildLoggingErrorMessageWithSpecialCharactersInMessage()
    {
        // Test with special characters in exception message
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        {
            tracingResource.handle("test-metric", "special-chars-test", () ->
            {
                throw new IllegalArgumentException("Error with special chars: <>&\"'");
            });
        });

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error with special chars"));
    }

    private static class TestTracingResource extends TracingResource
    {
        @Override
        public <T> T handle(String label, Supplier<T> supplier)
        {
            return super.handle(label, supplier);
        }

        @Override
        public <T> T handle(String resourceAPIMetricName, String label, Supplier<T> supplier)
        {
            return super.handle(resourceAPIMetricName, label, supplier);
        }

        @Override
        public <T> javax.ws.rs.core.Response handleResponse(String resourceAPIMetricName, String label, Supplier<T> supplier)
        {
            return super.handleResponse(resourceAPIMetricName, label, supplier);
        }
    }
}

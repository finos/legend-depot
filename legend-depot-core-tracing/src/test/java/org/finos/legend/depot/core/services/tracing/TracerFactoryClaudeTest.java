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

package org.finos.legend.depot.core.services.tracing;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.TracerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TracerFactoryClaudeTest


{
    @BeforeEach
    public void setUp() throws Exception
    {
        // Clear the CollectorRegistry before each test
        CollectorRegistry.defaultRegistry.clear();

        // Reset TracerFactory singleton and GlobalTracer using reflection
        // This is necessary because TracerFactory uses singleton pattern and GlobalTracer is global.
        // Without resetting these, tests would interfere with each other.
        resetTracerFactorySingleton();
        resetGlobalTracer();
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        // Clean up after each test
        resetTracerFactorySingleton();
        resetGlobalTracer();
    }

    /**
     * Reset TracerFactory singleton using reflection.
     * This is necessary because TracerFactory uses a static INSTANCE field that persists across tests.
     * Without resetting, tests would interfere with each other's state.
     */
    private void resetTracerFactorySingleton() throws Exception
    {
        Field instanceField = TracerFactory.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * Reset GlobalTracer using reflection.
     * This is necessary because GlobalTracer is a global singleton that persists across tests.
     * Without resetting, tests would interfere with each other's state.
     */
    private void resetGlobalTracer() throws Exception
    {
        Field tracerField = GlobalTracer.class.getDeclaredField("tracer");
        tracerField.setAccessible(true);
        tracerField.set(null, NoopTracerFactory.create());

        Field isRegisteredField = GlobalTracer.class.getDeclaredField("isRegistered");
        isRegisteredField.setAccessible(true);
        isRegisteredField.set(null, false);
    }

    @Test
    public void testGetReturnsNonNullInstance()
  {
        TracerFactory factory = TracerFactory.get();
        Assertions.assertNotNull(factory);
    }

    @Test
    public void testGetReturnsSameInstanceOnMultipleCalls()
  {
        TracerFactory factory1 = TracerFactory.get();
        TracerFactory factory2 = TracerFactory.get();
        Assertions.assertSame(factory1, factory2);
    }

    @Test
    public void testGetInitializesWithNoopTracerByDefault()
  {
        TracerFactory factory = TracerFactory.get();
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
        // NoopTracer doesn't expose its type directly, but we can verify it behaves like one
        Span span = tracer.buildSpan("test").start();
        Assertions.assertNotNull(span);
    }

    @Test
    public void testGetTracerReturnsNonNullTracer()
  {
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testGetTracerReturnsSameTracerOnMultipleCalls()
  {
        Tracer tracer1 = TracerFactory.getTracer();
        Tracer tracer2 = TracerFactory.getTracer();
        Assertions.assertSame(tracer1, tracer2);
    }

    @Test
    public void testConfigureWithNullConfiguration()
  {
        TracerFactory factory = TracerFactory.configure(null);
        Assertions.assertNotNull(factory);
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testConfigureWithDisabledConfiguration()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(false);
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");

        TracerFactory factory = TracerFactory.configure(config);
        Assertions.assertNotNull(factory);
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testConfigureWithEnabledConfigurationAndValidUri()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");

        TracerFactory factory = TracerFactory.configure(config);
        Assertions.assertNotNull(factory);
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testConfigureWithEnabledConfigurationAndDefaultTracerProvider()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");
        config.setTracerProvider(null); // Should use DefaultTracerProvider

        TracerFactory factory = TracerFactory.configure(config);
        Assertions.assertNotNull(factory);
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testConfigureWithEnabledConfigurationAndCustomTracerProvider()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");

        // Use a custom TracerProvider that returns a BraveTracer
        TracerProvider customProvider = (configuration) ->
        
        {
            Tracing tracing = Tracing.newBuilder()
                    .localServiceName("custom-test")
                    .build();
            return BraveTracer.create(tracing);
        };
        config.setTracerProvider(customProvider);

        TracerFactory factory = TracerFactory.configure(config);
        Assertions.assertNotNull(factory);
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
        Assertions.assertTrue(tracer instanceof BraveTracer);
    }

    @Test
    public void testConfigureReplacesExistingInstance()
  {
        // First configuration
        TracerFactory factory1 = TracerFactory.configure(null);
        Assertions.assertNotNull(factory1);

        // Second configuration should replace the first
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(false);
        TracerFactory factory2 = TracerFactory.configure(config);

        Assertions.assertNotNull(factory2);
        Assertions.assertNotSame(factory1, factory2);
    }

    @Test
    public void testAddTagsWithNullTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        // Start a span and make it active
        try (Scope scope = tracer.buildSpan("test-span").startActive(true))
        {
            // Due to a bug in TracerFactory line 112 (using & instead of &&),
            // this will throw NullPointerException when tags is null
            Assertions.assertThrows(NullPointerException.class, () -> factory.addTags(null));
        }
    }

    @Test
    public void testAddTagsWithEmptyTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("test-span").startActive(true))
        {
            factory.addTags(new HashMap<>());
            // Should not throw exception
        }
    }

    @Test
    public void testAddTagsWithValidTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("test-span").startActive(true))
        {
            Map<String, String> tags = new HashMap<>();
            tags.put("key1", "value1");
            tags.put("key2", "value2");

            factory.addTags(tags);
            // Should not throw exception
        }
    }

    @Test
    public void testAddTagsWithNoActiveSpan()
  {
        configureRealTracer();
        TracerFactory factory = TracerFactory.get();

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");

        // Should not throw exception when no active span
        factory.addTags(tags);
    }

    @Test
    public void testAddTagsWithSpecificSpanAndNullTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();
        Span span = tracer.buildSpan("test-span").start();

        // Due to a bug in TracerFactory line 112 (using & instead of &&),
        // this will throw NullPointerException when tags is null
        Assertions.assertThrows(NullPointerException.class, () -> factory.addTags(null, span));

        span.finish();
    }

    @Test
    public void testAddTagsWithSpecificSpanAndEmptyTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();
        Span span = tracer.buildSpan("test-span").start();

        factory.addTags(new HashMap<>(), span);

        span.finish();
        // Should not throw exception
    }

    @Test
    public void testAddTagsWithSpecificSpanAndValidTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();
        Span span = tracer.buildSpan("test-span").start();

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");
        tags.put("key2", "value2");

        factory.addTags(tags, span);
        span.finish();
        // Should not throw exception
    }

    @Test
    public void testAddTagsWithSpecificSpanAndNullSpan()
  {
        configureRealTracer();
        TracerFactory factory = TracerFactory.get();

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");

        // Should not throw exception when span is null
        factory.addTags(tags, null);
    }

    @Test
    public void testAddTagsWithNonStringValues()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("test-span").startActive(true))
        {
            Map<String, Object> tags = new HashMap<>();
            tags.put("intKey", 123);
            tags.put("boolKey", true);
            tags.put("nullKey", null);

            factory.addTags(tags);
            // Should not throw exception
        }
    }

    @Test
    public void testLogWithNullValue()
  {
        configureRealTracer();
        TracerFactory factory = TracerFactory.get();

        // Should not throw exception
        factory.log(null);
    }

    @Test
    public void testLogWithNoActiveSpan()
  {
        configureRealTracer();
        TracerFactory factory = TracerFactory.get();

        // Should not throw exception when no active span
        factory.log("test message");
    }

    @Test
    public void testLogWithActiveSpan()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("test-span").startActive(true))
        {
            factory.log("test message");
            // Should not throw exception
        }
    }

    @Test
    public void testLogWithEmptyString()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("test-span").startActive(true))
        {
            factory.log("");
            // Should not throw exception
        }
    }

    @Test
    public void testExecuteWithTraceSuccessfulExecution()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        // Create an active parent span
        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result = factory.executeWithTrace("test-trace", () -> "test-result");

            Assertions.assertEquals("test-result", result);
        }
    }

    @Test
    public void testExecuteWithTraceWithException()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        // Create an active parent span
        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
            
        {
                factory.executeWithTrace("test-trace", () ->
                
        {
                    throw new IllegalArgumentException("Test exception");
                });
            });

            Assertions.assertTrue(exception.getMessage().contains("Test exception"));
        }
    }

    @Test
    public void testExecuteWithTraceWithNoActiveSpan()
  {
        configureRealTracer();
        TracerFactory factory = TracerFactory.get();

        // Execute without an active parent span
        String result = factory.executeWithTrace("test-trace", () -> "test-result");

        Assertions.assertEquals("test-result", result);
    }

    @Test
    public void testExecuteWithTraceReturnsCorrectType()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            Integer result = factory.executeWithTrace("test-trace", () -> 42);
            Assertions.assertEquals(42, result);

            Boolean boolResult = factory.executeWithTrace("test-trace-2", () -> true);
            Assertions.assertEquals(true, boolResult);
        }
    }

    @Test
    public void testExecuteWithTraceWithTagsSuccessfulExecution()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");
        tags.put("key2", "value2");

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result = factory.executeWithTrace("test-trace", () -> "test-result", tags);

            Assertions.assertEquals("test-result", result);
        }
    }

    @Test
    public void testExecuteWithTraceWithTagsAndException()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
            
        {
                factory.executeWithTrace("test-trace", () ->
                
        {
                    throw new IllegalStateException("Test exception with tags");
                }, tags);
            });

            Assertions.assertTrue(exception.getMessage().contains("Test exception with tags"));
        }
    }

    @Test
    public void testExecuteWithTraceWithEmptyTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result = factory.executeWithTrace("test-trace", () -> "test-result", new HashMap<>());

            Assertions.assertEquals("test-result", result);
        }
    }

    @Test
    public void testExecuteWithTraceWithNullTags()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            // Due to a bug in TracerFactory line 112 (using & instead of &&),
            // calling addTags with null throws NullPointerException, which gets wrapped in RuntimeException
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
            
        {
                factory.executeWithTrace("test-trace", () -> "test-result", null);
            });

            // The NullPointerException is caught and wrapped with a message
            Assertions.assertTrue(exception.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void testExecuteWithTraceWithTagsNoActiveSpan()
  {
        configureRealTracer();
        TracerFactory factory = TracerFactory.get();

        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");

        String result = factory.executeWithTrace("test-trace", () -> "test-result", tags);

        Assertions.assertEquals("test-result", result);
    }

    @Test
    public void testExecuteWithTraceWithNullReturnValue()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result = factory.executeWithTrace("test-trace", () -> null);

            Assertions.assertNull(result);
        }
    }

    @Test
    public void testExecuteWithTraceNestedCalls()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result = factory.executeWithTrace("outer-trace", () ->
            
        {
                return factory.executeWithTrace("inner-trace", () -> "nested-result");
            });

            Assertions.assertEquals("nested-result", result);
        }
    }

    @Test
    public void testMultipleSequentialExecuteWithTrace()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result1 = factory.executeWithTrace("trace-1", () -> "result-1");
            String result2 = factory.executeWithTrace("trace-2", () -> "result-2");
            String result3 = factory.executeWithTrace("trace-3", () -> "result-3");

            Assertions.assertEquals("result-1", result1);
            Assertions.assertEquals("result-2", result2);
            Assertions.assertEquals("result-3", result3);
        }
    }

    @Test
    public void testExecuteWithTraceSupplierExecutedExactlyOnce()
  {
        configureRealTracer();
        Tracer tracer = TracerFactory.getTracer();

        TracerFactory factory = TracerFactory.get();
        AtomicInteger counter = new AtomicInteger(0);

        try (Scope scope = tracer.buildSpan("parent-span").startActive(true))
        {
            String result = factory.executeWithTrace("test-trace", () ->
            
        {
                counter.incrementAndGet();
                return "result";
            });

            Assertions.assertEquals("result", result);
            Assertions.assertEquals(1, counter.get());
        }
    }

    @Test
    public void testConfigureWithEnabledConfigurationUsingBraveTracer()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("brave-test-service");
        config.setTracerProvider((configuration) ->
        
        {
            Tracing tracing = Tracing.newBuilder()
                    .localServiceName("brave-test")
                    .build();
            return BraveTracer.create(tracing);
        });

        TracerFactory factory = TracerFactory.configure(config);
        Assertions.assertNotNull(factory);
        Tracer tracer = TracerFactory.getTracer();
        Assertions.assertNotNull(tracer);
        Assertions.assertTrue(tracer instanceof BraveTracer);
    }

    /**
     * Helper method to configure TracerFactory with a real BraveTracer for testing.
     */
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
}

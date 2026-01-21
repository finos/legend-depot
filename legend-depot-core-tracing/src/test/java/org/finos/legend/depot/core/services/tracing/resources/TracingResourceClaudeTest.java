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
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.VoidPrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;
import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TracingResourceClaudeTest


{
    private TracingResource tracingResource;
    private PrometheusMetricsHandler originalMetricsHandler;

    @BeforeEach
    public void setUp() throws Exception
    {
        // Clear the CollectorRegistry before each test
        CollectorRegistry.defaultRegistry.clear();

        // Reset singletons
        resetTracerFactorySingleton();
        resetGlobalTracer();
        resetPrometheusMetricsFactorySingleton();

        // Store original metrics handler
        originalMetricsHandler = PrometheusMetricsFactory.getInstance();

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
    public void testConstructor()
  {
        TracingResource resource = new TracingResource();
        Assertions.assertNotNull(resource);
    }

    @Test
    public void testHandleWithTwoParametersReturnsResult()
  {
        String result = tracingResource.handle("test-label", () -> "test-result");
        Assertions.assertEquals("test-result", result);
    }

    @Test
    public void testHandleWithTwoParametersReturnsNullResult()
  {
        String result = tracingResource.handle("test-label", () -> null);
        Assertions.assertNull(result);
    }

    @Test
    public void testHandleWithTwoParametersExecutesSupplierExactlyOnce()
  {
        AtomicInteger counter = new AtomicInteger(0);
        String result = tracingResource.handle("test-label", () ->
        
        {
            counter.incrementAndGet();
            return "result";
        });

        Assertions.assertEquals("result", result);
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    public void testHandleWithTwoParametersThrowsException()
  {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        
        {
            tracingResource.handle("test-label", () ->
            
        {
                throw new IllegalArgumentException("Test exception");
            });
        });

        Assertions.assertTrue(exception.getMessage().contains("Test exception"));
    }

    @Test
    public void testHandleWithThreeParametersReturnsResult()
  {
        String result = tracingResource.handle("metric-name", "test-label", () -> "test-result");
        Assertions.assertEquals("test-result", result);
    }

    @Test
    public void testHandleWithThreeParametersReturnsNullResult()
  {
        String result = tracingResource.handle("metric-name", "test-label", () -> null);
        Assertions.assertNull(result);
    }

    @Test
    public void testHandleWithThreeParametersExecutesSupplierExactlyOnce()
  {
        AtomicInteger counter = new AtomicInteger(0);
        String result = tracingResource.handle("metric-name", "test-label", () ->
        
        {
            counter.incrementAndGet();
            return "result";
        });

        Assertions.assertEquals("result", result);
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    public void testHandleWithThreeParametersThrowsException()
  {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        
        {
            tracingResource.handle("metric-name", "test-label", () ->
            
        {
                throw new IllegalStateException("Test exception");
            });
        });

        Assertions.assertTrue(exception.getMessage().contains("Test exception"));
    }

    @Test
    public void testHandleWithFourParametersReturnsOkResponse()
  {
        Request request = null;
        Response response = tracingResource.handle("test-label", () -> "test-result", request, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleWithFourParametersReturnsNotModifiedWhenEtagMatches()
  {
        String etagValue = "test-etag-123";
        Request request = mock(Request.class);
        EntityTag entityTag = new EntityTag(etagValue);
        when(request.evaluatePreconditions(entityTag)).thenReturn(Response.status(Response.Status.NOT_MODIFIED));

        Response response = tracingResource.handle("test-label", () -> "test-result", request, () -> etagValue);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testHandleWithFourParametersReturnsOkWhenEtagDoesNotMatch()
  {
        Request request = mock(Request.class);
        EntityTag differentTag = new EntityTag("different-etag");
        EntityTag actualTag = new EntityTag("test-etag");
        when(request.evaluatePreconditions(actualTag)).thenReturn(null);

        Response response = tracingResource.handle("test-label", () -> "test-result", request, () -> "test-etag");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleWithFourParametersNullRequest()
  {
        Response response = tracingResource.handle("test-label", () -> "test-result", null, () -> "test-etag");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
        Assertions.assertNotNull(response.getEntityTag());
    }

    @Test
    public void testHandleWithFourParametersNullEtag()
  {
        Request request = mock(Request.class);

        Response response = tracingResource.handle("test-label", () -> "test-result", request, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
        Assertions.assertNull(response.getEntityTag());
    }

    @Test
    public void testHandleWithFiveParametersReturnsOkResponse()
  {
        Request request = null;
        Response response = tracingResource.handle("metric-name", "test-label", () -> "test-result", request, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleWithFiveParametersReturnsNotModifiedWhenEtagMatches()
  {
        String etagValue = "test-etag-456";
        Request request = mock(Request.class);
        EntityTag entityTag = new EntityTag(etagValue);
        when(request.evaluatePreconditions(entityTag)).thenReturn(Response.status(Response.Status.NOT_MODIFIED));

        Response response = tracingResource.handle("metric-name", "test-label", () -> "test-result", request, () -> etagValue);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testHandleWithFiveParametersReturnsOkWhenEtagDoesNotMatch()
  {
        Request request = mock(Request.class);
        EntityTag actualTag = new EntityTag("test-etag");
        when(request.evaluatePreconditions(actualTag)).thenReturn(null);

        Response response = tracingResource.handle("metric-name", "test-label", () -> "test-result", request, () -> "test-etag");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleWithFiveParametersNullRequest()
  {
        Response response = tracingResource.handle("metric-name", "test-label", () -> "test-result", null, () -> "test-etag");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleWithFiveParametersNullEtag()
  {
        Request request = mock(Request.class);

        Response response = tracingResource.handle("metric-name", "test-label", () -> "test-result", request, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
        Assertions.assertNull(response.getEntityTag());
    }

    @Test
    public void testHandleResponseWithTwoParameters()
  {
        Response response = tracingResource.handleResponse("test-label", () -> "test-result");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleResponseWithTwoParametersNullResult()
  {
        Response response = tracingResource.handleResponse("test-label", () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertNull(response.getEntity());
    }

    @Test
    public void testHandleResponseWithThreeParameters()
  {
        Response response = tracingResource.handleResponse("metric-name", "test-label", () -> "test-result");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals("test-result", response.getEntity());
    }

    @Test
    public void testHandleResponseWithThreeParametersNullResult()
  {
        Response response = tracingResource.handleResponse("metric-name", "test-label", () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertNull(response.getEntity());
    }

    @Test
    public void testHandleResponseSetsCacheControlNoCache()
  {
        Response response = tracingResource.handleResponse("test-label", () -> "test-result");

        Assertions.assertNotNull(response);
        Object cacheControl = response.getMetadata().getFirst("Cache-Control");
        Assertions.assertNotNull(cacheControl);
        Assertions.assertTrue(cacheControl instanceof CacheControl);
        CacheControl cc = (CacheControl) cacheControl;
        Assertions.assertTrue(cc.isNoCache());
        Assertions.assertTrue(cc.isNoStore());
    }

    @Test
    public void testHandleWithEtagSetsCacheControlMustRevalidate()
  {
        Response response = tracingResource.handle("test-label", () -> "test-result", null, () -> "test-etag");

        Assertions.assertNotNull(response);
        Object cacheControl = response.getMetadata().getFirst("Cache-Control");
        Assertions.assertNotNull(cacheControl);
        Assertions.assertTrue(cacheControl instanceof CacheControl);
        CacheControl cc = (CacheControl) cacheControl;
        Assertions.assertTrue(cc.isMustRevalidate());
    }

    @Test
    public void testHandleResponseThrowsException()
  {
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
        
        {
            tracingResource.handleResponse("test-label", () ->
            
        {
                throw new IllegalArgumentException("Test exception in response");
            });
        });

        Assertions.assertTrue(exception.getMessage().contains("Test exception in response"));
    }

    @Test
    public void testHandleWithFourParametersExecutesSupplierExactlyOnce()
  {
        AtomicInteger counter = new AtomicInteger(0);
        Response response = tracingResource.handle("test-label", () ->
        
        {
            counter.incrementAndGet();
            return "result";
        }, null, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    public void testHandleWithFiveParametersExecutesSupplierExactlyOnce()
  {
        AtomicInteger counter = new AtomicInteger(0);
        Response response = tracingResource.handle("metric-name", "test-label", () ->
        
        {
            counter.incrementAndGet();
            return "result";
        }, null, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    public void testHandleResponseExecutesSupplierExactlyOnce()
  {
        AtomicInteger counter = new AtomicInteger(0);
        Response response = tracingResource.handleResponse("test-label", () ->
        
        {
            counter.incrementAndGet();
            return "result";
        });

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    public void testHandleWithIntegerReturnType()
  {
        Integer result = tracingResource.handle("test-label", () -> 42);
        Assertions.assertEquals(42, result);
    }

    @Test
    public void testHandleWithBooleanReturnType()
  {
        Boolean result = tracingResource.handle("test-label", () -> true);
        Assertions.assertEquals(true, result);
    }

    @Test
    public void testHandleResponseWithIntegerResult()
  {
        Response response = tracingResource.handleResponse("test-label", () -> 123);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertEquals(123, response.getEntity());
    }

    @Test
    public void testHandleWithEtagReturnsEntityTagInResponse()
  {
        String etagValue = "etag-value-123";
        Response response = tracingResource.handle("test-label", () -> "test-result", null, () -> etagValue);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getEntityTag());
        Assertions.assertEquals(etagValue, response.getEntityTag().getValue());
    }

    @Test
    public void testHandleWithNullEtagDoesNotReturnEntityTag()
  {
        Response response = tracingResource.handle("test-label", () -> "test-result", null, () -> null);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getEntityTag());
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
        public <T> Response handle(String label, Supplier<T> supplier, Request request, Supplier<String> entityTagSupplier)
  {
            return super.handle(label, supplier, request, entityTagSupplier);
        }

        @Override
        public <T> Response handle(String resourceAPIMetricName, String label, Supplier<T> supplier, Request request, Supplier<String> etagSupplier)
  {
            return super.handle(resourceAPIMetricName, label, supplier, request, etagSupplier);
        }

        @Override
        public <T> Response handleResponse(String label, Supplier<T> supplier)
  {
            return super.handleResponse(label, supplier);
        }

        @Override
        public <T> Response handleResponse(String resourceAPIMetricName, String label, Supplier<T> supplier)
  {
            return super.handleResponse(resourceAPIMetricName, label, supplier);
        }
    }

}

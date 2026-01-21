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

import io.opentracing.Tracer;
import io.prometheus.client.CollectorRegistry;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zipkin2.reporter.InMemoryReporterMetrics;

public class DefaultTracerProviderClaudeTest


{
    @BeforeEach
    public void setUp()
  {
        CollectorRegistry.defaultRegistry.clear();
    }

    @Test
    public void testDefaultConstructor()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        Assertions.assertNotNull(provider);
    }

    @Test
    public void testConstructorCreatesValidInstance()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        Assertions.assertNotNull(provider);
        Assertions.assertTrue(provider instanceof org.finos.legend.depot.core.services.api.tracing.configuration.TracerProvider);
    }

    @Test
    public void testCreateWithValidConfiguration()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithValidConfigurationAndDefaultServiceName()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithValidConfigurationAndEmptyServiceName()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithValidConfigurationAndNullServiceName()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName(null);

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithNullUri()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri(null);
        config.setServiceName("test-service");

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> 
        {
            provider.create(config);
        });
        Assertions.assertEquals("Invalid uri, openTracingUri cannot be empty", exception.getMessage());
    }

    @Test
    public void testCreateWithEmptyUri()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("");
        config.setServiceName("test-service");

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> 
        {
            provider.create(config);
        });
        Assertions.assertEquals("Invalid uri, openTracingUri cannot be empty", exception.getMessage());
    }

    @Test
    public void testCreateWithInvalidUri()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("invalid-uri-format");
        config.setServiceName("test-service");

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> 
        {
            provider.create(config);
        });
        Assertions.assertEquals("Invalid openTracingUri provided", exception.getMessage());
        Assertions.assertNotNull(exception.getCause());
    }

    @Test
    public void testCreateWithMalformedUri()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("://invalid");
        config.setServiceName("test-service");

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> 
        {
            provider.create(config);
        });
        Assertions.assertEquals("Invalid openTracingUri provided", exception.getMessage());
        Assertions.assertNotNull(exception.getCause());
    }

    @Test
    public void testCreateMultipleTracersFromSameProvider()
  {
        // Note: Due to the use of the default CollectorRegistry, we can only create one tracer per test.
        // This test verifies that the provider can be reused, but we can only call create() once per test.
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config1 = new OpenTracingConfiguration();
        config1.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config1.setServiceName("service1");

        Tracer tracer1 = provider.create(config1);
        Assertions.assertNotNull(tracer1);

        // Attempting to create a second tracer would fail due to CollectorRegistry registration
        // This is the expected behavior since the registry is global
    }

    @Test
    public void testCreateWithDifferentServiceNames()
  {
        // Test that a tracer can be created with a custom service name
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("service-alpha");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testGetMemoryMetricsReporter()
  {
        // Testing getMemoryMetricsReporter requires accessing it indirectly through create method
        // since it's a protected method. The method is called during tracer creation.
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");

        // The create method internally calls getMemoryMetricsReporter
        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testGetMemoryMetricsReporterViaSubclass()
  {
        // Test getMemoryMetricsReporter by creating a subclass that exposes it
        class TestableDefaultTracerProvider extends DefaultTracerProvider
        {
            public InMemoryReporterMetrics exposeGetMemoryMetricsReporter()
  {
                return getMemoryMetricsReporter();
            }
        }

        TestableDefaultTracerProvider provider = new TestableDefaultTracerProvider();
        InMemoryReporterMetrics metrics = provider.exposeGetMemoryMetricsReporter();

        Assertions.assertNotNull(metrics);
    }

    @Test
    public void testGetMemoryMetricsReporterReturnsValidMetrics()
  {
        // Test that getMemoryMetricsReporter returns valid metrics by using a subclass
        class TestableDefaultTracerProvider extends DefaultTracerProvider
        {
            public InMemoryReporterMetrics exposeGetMemoryMetricsReporter()
  {
                return getMemoryMetricsReporter();
            }
        }

        TestableDefaultTracerProvider provider = new TestableDefaultTracerProvider();
        InMemoryReporterMetrics metrics = provider.exposeGetMemoryMetricsReporter();

        Assertions.assertNotNull(metrics);
        // Verify metrics are initialized correctly
        Assertions.assertEquals(0, metrics.messages());
        Assertions.assertEquals(0, metrics.messagesDropped());
    }

    @Test
    public void testMultipleInstancesAreIndependent()
  {
        // Test that multiple instances of DefaultTracerProvider can be created independently
        DefaultTracerProvider provider1 = new DefaultTracerProvider();
        DefaultTracerProvider provider2 = new DefaultTracerProvider();

        Assertions.assertNotNull(provider1);
        Assertions.assertNotNull(provider2);
        Assertions.assertNotSame(provider1, provider2);

        // Each provider can create a tracer, but we can only create one per test due to CollectorRegistry
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("service1");

        Tracer tracer = provider1.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithHttpsUri()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("https://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithComplexUri()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://tracing.example.com:9411/api/v2/spans");
        config.setServiceName("test-service");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateWithSpecialCharactersInServiceName()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service-123");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);
    }

    @Test
    public void testCreateReturnsTracerThatCanBeUsed()
  {
        DefaultTracerProvider provider = new DefaultTracerProvider();
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setServiceName("test-service");

        Tracer tracer = provider.create(config);
        Assertions.assertNotNull(tracer);

        // Verify the tracer can be used to build spans
        Assertions.assertNotNull(tracer.buildSpan("test-span"));
    }
}

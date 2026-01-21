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

package org.finos.legend.depot.core.services.api.tracing.configuration;

import io.opentracing.Tracer;
import org.finos.legend.depot.core.services.tracing.DefaultTracerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OpenTracingConfigurationClaudeTest


{
    @Test
    public void testDefaultConstructor()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    public void testDefaultConstructorSetsEnabledToFalse()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertFalse(config.isEnabled());
    }

    @Test
    public void testDefaultConstructorSetsOpenTracingUriToNull()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNull(config.getOpenTracingUri());
    }

    @Test
    public void testDefaultConstructorSetsServiceNameToNull()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNull(config.getServiceName());
    }

    @Test
    public void testDefaultConstructorSetsTracerProviderToNull()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNull(config.getTracerProvider());
    }

    @Test
    public void testGetOpenTracingUri()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNull(config.getOpenTracingUri());
    }

    @Test
    public void testSetOpenTracingUri()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String uri = "http://localhost:9411/api/v2/spans";
        config.setOpenTracingUri(uri);
        Assertions.assertEquals(uri, config.getOpenTracingUri());
    }

    @Test
    public void testSetOpenTracingUriToNull()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("http://localhost:9411/api/v2/spans");
        config.setOpenTracingUri(null);
        Assertions.assertNull(config.getOpenTracingUri());
    }

    @Test
    public void testSetOpenTracingUriToEmptyString()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setOpenTracingUri("");
        Assertions.assertEquals("", config.getOpenTracingUri());
    }

    @Test
    public void testSetOpenTracingUriMultipleTimes()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String uri1 = "http://localhost:9411/api/v2/spans";
        String uri2 = "http://example.com:9411/api/v2/spans";

        config.setOpenTracingUri(uri1);
        Assertions.assertEquals(uri1, config.getOpenTracingUri());

        config.setOpenTracingUri(uri2);
        Assertions.assertEquals(uri2, config.getOpenTracingUri());
    }

    @Test
    public void testGetServiceName()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNull(config.getServiceName());
    }

    @Test
    public void testSetServiceName()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String serviceName = "legend-depot";
        config.setServiceName(serviceName);
        Assertions.assertEquals(serviceName, config.getServiceName());
    }

    @Test
    public void testSetServiceNameToNull()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setServiceName("legend-depot");
        config.setServiceName(null);
        Assertions.assertNull(config.getServiceName());
    }

    @Test
    public void testSetServiceNameToEmptyString()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setServiceName("");
        Assertions.assertEquals("", config.getServiceName());
    }

    @Test
    public void testSetServiceNameMultipleTimes()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String name1 = "service-1";
        String name2 = "service-2";

        config.setServiceName(name1);
        Assertions.assertEquals(name1, config.getServiceName());

        config.setServiceName(name2);
        Assertions.assertEquals(name2, config.getServiceName());
    }

    @Test
    public void testIsEnabledReturnsFalseByDefault()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertFalse(config.isEnabled());
    }

    @Test
    public void testSetEnabledToTrue()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
    }

    @Test
    public void testSetEnabledToFalse()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        config.setEnabled(false);
        Assertions.assertFalse(config.isEnabled());
    }

    @Test
    public void testSetEnabledMultipleTimes()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
        config.setEnabled(false);
        Assertions.assertFalse(config.isEnabled());
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
    }

    @Test
    public void testGetTracerProvider()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        Assertions.assertNull(config.getTracerProvider());
    }

    @Test
    public void testSetTracerProvider()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        TracerProvider provider = new DefaultTracerProvider();
        config.setTracerProvider(provider);
        Assertions.assertSame(provider, config.getTracerProvider());
    }

    @Test
    public void testSetTracerProviderToNull()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        TracerProvider provider = new DefaultTracerProvider();
        config.setTracerProvider(provider);
        config.setTracerProvider(null);
        Assertions.assertNull(config.getTracerProvider());
    }

    @Test
    public void testSetTracerProviderMultipleTimes()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        TracerProvider provider1 = new DefaultTracerProvider();
        TracerProvider provider2 = new DefaultTracerProvider();

        config.setTracerProvider(provider1);
        Assertions.assertSame(provider1, config.getTracerProvider());

        config.setTracerProvider(provider2);
        Assertions.assertSame(provider2, config.getTracerProvider());
        Assertions.assertNotSame(provider1, config.getTracerProvider());
    }

    @Test
    public void testSetTracerProviderWithCustomImplementation()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        TracerProvider customProvider = new TracerProvider()
  {
            @Override
            public Tracer create(OpenTracingConfiguration configuration)
  {
                return null;
            }
        };
        config.setTracerProvider(customProvider);
        Assertions.assertSame(customProvider, config.getTracerProvider());
    }

    @Test
    public void testAllPropertiesSetAndGet()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String uri = "http://localhost:9411/api/v2/spans";
        String serviceName = "legend-depot";
        TracerProvider provider = new DefaultTracerProvider();

        config.setOpenTracingUri(uri);
        config.setServiceName(serviceName);
        config.setEnabled(true);
        config.setTracerProvider(provider);

        Assertions.assertEquals(uri, config.getOpenTracingUri());
        Assertions.assertEquals(serviceName, config.getServiceName());
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertSame(provider, config.getTracerProvider());
    }

    @Test
    public void testPropertiesAreIndependent()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String uri = "http://localhost:9411/api/v2/spans";

        // Set uri without affecting other properties
        config.setOpenTracingUri(uri);
        Assertions.assertEquals(uri, config.getOpenTracingUri());
        Assertions.assertNull(config.getServiceName());
        Assertions.assertFalse(config.isEnabled());
        Assertions.assertNull(config.getTracerProvider());

        // Set serviceName without affecting other properties
        String serviceName = "legend-depot";
        config.setServiceName(serviceName);
        Assertions.assertEquals(uri, config.getOpenTracingUri());
        Assertions.assertEquals(serviceName, config.getServiceName());
        Assertions.assertFalse(config.isEnabled());
        Assertions.assertNull(config.getTracerProvider());

        // Set enabled without affecting other properties
        config.setEnabled(true);
        Assertions.assertEquals(uri, config.getOpenTracingUri());
        Assertions.assertEquals(serviceName, config.getServiceName());
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertNull(config.getTracerProvider());

        // Set tracerProvider without affecting other properties
        TracerProvider provider = new DefaultTracerProvider();
        config.setTracerProvider(provider);
        Assertions.assertEquals(uri, config.getOpenTracingUri());
        Assertions.assertEquals(serviceName, config.getServiceName());
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertSame(provider, config.getTracerProvider());
    }

    @Test
    public void testMultipleInstancesAreIndependent()
  {
        OpenTracingConfiguration config1 = new OpenTracingConfiguration();
        OpenTracingConfiguration config2 = new OpenTracingConfiguration();

        String uri1 = "http://localhost:9411/api/v2/spans";
        String uri2 = "http://example.com:9411/api/v2/spans";
        String serviceName1 = "service-1";
        String serviceName2 = "service-2";
        TracerProvider provider1 = new DefaultTracerProvider();
        TracerProvider provider2 = new DefaultTracerProvider();

        config1.setOpenTracingUri(uri1);
        config1.setServiceName(serviceName1);
        config1.setEnabled(true);
        config1.setTracerProvider(provider1);

        config2.setOpenTracingUri(uri2);
        config2.setServiceName(serviceName2);
        config2.setEnabled(false);
        config2.setTracerProvider(provider2);

        Assertions.assertEquals(uri1, config1.getOpenTracingUri());
        Assertions.assertEquals(serviceName1, config1.getServiceName());
        Assertions.assertTrue(config1.isEnabled());
        Assertions.assertSame(provider1, config1.getTracerProvider());

        Assertions.assertEquals(uri2, config2.getOpenTracingUri());
        Assertions.assertEquals(serviceName2, config2.getServiceName());
        Assertions.assertFalse(config2.isEnabled());
        Assertions.assertSame(provider2, config2.getTracerProvider());
    }

    @Test
    public void testSettersReturnVoidAndCanBeChained()
  {
        OpenTracingConfiguration config = new OpenTracingConfiguration();
        String uri = "http://localhost:9411/api/v2/spans";
        String serviceName = "legend-depot";
        TracerProvider provider = new DefaultTracerProvider();

        // Setters don't return anything but can be called sequentially
        config.setOpenTracingUri(uri);
        config.setServiceName(serviceName);
        config.setEnabled(true);
        config.setTracerProvider(provider);

        Assertions.assertEquals(uri, config.getOpenTracingUri());
        Assertions.assertEquals(serviceName, config.getServiceName());
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertSame(provider, config.getTracerProvider());
    }
}

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

package org.finos.legend.depot.core.services.api.metrics.configuration;

import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.VoidPrometheusMetricsHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrometheusConfigurationClaudeTest


{
    @Test
    public void testDefaultConstructor()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    public void testDefaultConstructorSetsEnabledToFalse()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        Assertions.assertFalse(config.isEnabled());
    }

    @Test
    public void testDefaultConstructorSetsMetricsHandlerToNull()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        Assertions.assertNull(config.getMetricsHandler());
    }

    @Test
    public void testParameterizedConstructor()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        PrometheusConfiguration config = new PrometheusConfiguration(true, handler);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testParameterizedConstructorSetsEnabled()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        PrometheusConfiguration config = new PrometheusConfiguration(true, handler);
        Assertions.assertTrue(config.isEnabled());
    }

    @Test
    public void testParameterizedConstructorSetsMetricsHandler()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        PrometheusConfiguration config = new PrometheusConfiguration(true, handler);
        Assertions.assertSame(handler, config.getMetricsHandler());
    }

    @Test
    public void testParameterizedConstructorWithFalseEnabled()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        PrometheusConfiguration config = new PrometheusConfiguration(false, handler);
        Assertions.assertFalse(config.isEnabled());
        Assertions.assertSame(handler, config.getMetricsHandler());
    }

    @Test
    public void testParameterizedConstructorWithNullHandler()
  {
        PrometheusConfiguration config = new PrometheusConfiguration(true, null);
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertNull(config.getMetricsHandler());
    }

    @Test
    public void testIsEnabledReturnsFalseByDefault()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        Assertions.assertFalse(config.isEnabled());
    }

    @Test
    public void testIsEnabledReturnsTrueWhenSet()
  {
        PrometheusConfiguration config = new PrometheusConfiguration(true, null);
        Assertions.assertTrue(config.isEnabled());
    }

    @Test
    public void testSetEnabledToTrue()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
    }

    @Test
    public void testSetEnabledToFalse()
  {
        PrometheusConfiguration config = new PrometheusConfiguration(true, null);
        config.setEnabled(false);
        Assertions.assertFalse(config.isEnabled());
    }

    @Test
    public void testSetEnabledMultipleTimes()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
        config.setEnabled(false);
        Assertions.assertFalse(config.isEnabled());
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
    }

    @Test
    public void testGetMetricsHandlerReturnsNull()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        Assertions.assertNull(config.getMetricsHandler());
    }

    @Test
    public void testGetMetricsHandlerReturnsSetHandler()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        PrometheusConfiguration config = new PrometheusConfiguration(false, handler);
        Assertions.assertSame(handler, config.getMetricsHandler());
    }

    @Test
    public void testSetMetricsHandler()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        config.setMetricsHandler(handler);
        Assertions.assertSame(handler, config.getMetricsHandler());
    }

    @Test
    public void testSetMetricsHandlerToNull()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        PrometheusConfiguration config = new PrometheusConfiguration(false, handler);
        config.setMetricsHandler(null);
        Assertions.assertNull(config.getMetricsHandler());
    }

    @Test
    public void testSetMetricsHandlerMultipleTimes()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        PrometheusMetricsHandler handler1 = new VoidPrometheusMetricsHandler();
        PrometheusMetricsHandler handler2 = new VoidPrometheusMetricsHandler();

        config.setMetricsHandler(handler1);
        Assertions.assertSame(handler1, config.getMetricsHandler());

        config.setMetricsHandler(handler2);
        Assertions.assertSame(handler2, config.getMetricsHandler());
        Assertions.assertNotSame(handler1, config.getMetricsHandler());
    }

    @Test
    public void testSettersAndGettersCombined()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();

        config.setEnabled(true);
        config.setMetricsHandler(handler);

        Assertions.assertTrue(config.isEnabled());
        Assertions.assertSame(handler, config.getMetricsHandler());
    }

    @Test
    public void testIndependenceOfProperties()
  {
        PrometheusConfiguration config = new PrometheusConfiguration();
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();

        // Set enabled without handler
        config.setEnabled(true);
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertNull(config.getMetricsHandler());

        // Set handler without changing enabled
        config.setMetricsHandler(handler);
        Assertions.assertTrue(config.isEnabled());
        Assertions.assertSame(handler, config.getMetricsHandler());

        // Change enabled without affecting handler
        config.setEnabled(false);
        Assertions.assertFalse(config.isEnabled());
        Assertions.assertSame(handler, config.getMetricsHandler());
    }

    @Test
    public void testMultipleInstancesAreIndependent()
  {
        PrometheusMetricsHandler handler1 = new VoidPrometheusMetricsHandler();
        PrometheusMetricsHandler handler2 = new VoidPrometheusMetricsHandler();

        PrometheusConfiguration config1 = new PrometheusConfiguration(true, handler1);
        PrometheusConfiguration config2 = new PrometheusConfiguration(false, handler2);

        Assertions.assertTrue(config1.isEnabled());
        Assertions.assertFalse(config2.isEnabled());
        Assertions.assertSame(handler1, config1.getMetricsHandler());
        Assertions.assertSame(handler2, config2.getMetricsHandler());

        // Modify config1
        config1.setEnabled(false);
        Assertions.assertFalse(config1.isEnabled());
        // Verify config2 is unchanged
        Assertions.assertFalse(config2.isEnabled());
    }

    @Test
    public void testConstructorAndSettersProduceSameResult()
  {
        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();

        PrometheusConfiguration config1 = new PrometheusConfiguration(true, handler);

        PrometheusConfiguration config2 = new PrometheusConfiguration();
        config2.setEnabled(true);
        config2.setMetricsHandler(handler);

        Assertions.assertEquals(config1.isEnabled(), config2.isEnabled());
        Assertions.assertSame(config1.getMetricsHandler(), config2.getMetricsHandler());
    }
}

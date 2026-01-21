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

package org.finos.legend.depot.core.services.metrics;

import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.VoidPrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class PrometheusMetricsFactoryClaudeTest


{
    @BeforeEach
    public void setup() throws Exception
    {
        // Reset the singleton instance between tests using reflection
        // This is necessary because PrometheusMetricsFactory uses a static singleton pattern
        // and there is no public API to reset the instance for testing purposes.
        // Without this reset, tests would interfere with each other due to shared static state.
        resetFactoryInstance();
    }

    @AfterEach
    public void cleanup() throws Exception
    {
        // Clean up the singleton instance after tests
        resetFactoryInstance();
    }

    @Test
    public void testConstructor()
  {
        // The constructor is implicit and doesn't do anything special
        // Just verify we can instantiate it
        PrometheusMetricsFactory factory = new PrometheusMetricsFactory();
        Assertions.assertNotNull(factory);
    }

    @Test
    public void testGetInstanceReturnsNonNull()
  {
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.getInstance();
        Assertions.assertNotNull(handler);
    }

    @Test
    public void testGetInstanceReturnsVoidHandlerByDefault()
  {
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.getInstance();
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
    }

    @Test
    public void testGetInstanceReturnsSameInstanceOnMultipleCalls()
  {
        PrometheusMetricsHandler handler1 = PrometheusMetricsFactory.getInstance();
        PrometheusMetricsHandler handler2 = PrometheusMetricsFactory.getInstance();
        Assertions.assertSame(handler1, handler2);
    }

    @Test
    public void testConfigureWithNullConfigurationReturnsVoidHandler()
  {
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(null);
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
    }

    @Test
    public void testConfigureWithNullConfigurationSetsInstance()
  {
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(null);
        PrometheusMetricsHandler instance = PrometheusMetricsFactory.getInstance();
        Assertions.assertSame(handler, instance);
    }

    @Test
    public void testConfigureWithDisabledConfigurationReturnsVoidHandler()
  {
        PrometheusConfiguration config = new PrometheusConfiguration(false, null);
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(config);
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
    }

    @Test
    public void testConfigureWithEnabledConfigurationButNullHandlerReturnsVoidHandler()
  {
        PrometheusConfiguration config = new PrometheusConfiguration(true, null);
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(config);
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
    }

    @Test
    public void testConfigureWithEnabledConfigurationAndHandlerReturnsProvidedHandler()
  {
        DepotPrometheusMetricsHandler customHandler = new DepotPrometheusMetricsHandler("test");
        PrometheusConfiguration config = new PrometheusConfiguration(true, customHandler);
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(config);
        Assertions.assertSame(customHandler, handler);
    }

    @Test
    public void testConfigureWithEnabledConfigurationAndHandlerSetsInstance()
  {
        DepotPrometheusMetricsHandler customHandler = new DepotPrometheusMetricsHandler("test");
        PrometheusConfiguration config = new PrometheusConfiguration(true, customHandler);
        PrometheusMetricsFactory.configure(config);
        PrometheusMetricsHandler instance = PrometheusMetricsFactory.getInstance();
        Assertions.assertSame(customHandler, instance);
    }

    @Test
    public void testConfigureReplacesExistingInstance()
  {
        // First configure with a custom handler
        DepotPrometheusMetricsHandler customHandler1 = new DepotPrometheusMetricsHandler("test1");
        PrometheusConfiguration config1 = new PrometheusConfiguration(true, customHandler1);
        PrometheusMetricsFactory.configure(config1);

        // Then configure with a different handler
        DepotPrometheusMetricsHandler customHandler2 = new DepotPrometheusMetricsHandler("test2");
        PrometheusConfiguration config2 = new PrometheusConfiguration(true, customHandler2);
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(config2);

        Assertions.assertSame(customHandler2, handler);
        Assertions.assertNotSame(customHandler1, handler);
    }

    @Test
    public void testConfigureWithDisabledAfterEnabledReplacesWithVoidHandler()
  {
        // First configure with a custom handler
        DepotPrometheusMetricsHandler customHandler = new DepotPrometheusMetricsHandler("test");
        PrometheusConfiguration config1 = new PrometheusConfiguration(true, customHandler);
        PrometheusMetricsFactory.configure(config1);

        // Then configure with disabled config
        PrometheusConfiguration config2 = new PrometheusConfiguration(false, null);
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(config2);

        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
        Assertions.assertNotSame(customHandler, handler);
    }

    @Test
    public void testGetInstanceInitializesLazilyWithVoidHandler() throws Exception
    {
        // Verify instance is null initially
        Field instanceField = PrometheusMetricsFactory.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        Assertions.assertNull(instanceField.get(null));

        // Call getInstance
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.getInstance();

        // Verify it created a VoidPrometheusMetricsHandler
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);

        // Verify instance is now set
        Assertions.assertNotNull(instanceField.get(null));
    }

    @Test
    public void testGetInstanceDoesNotReinitializeIfAlreadySet()
  {
        // First call to initialize
        PrometheusMetricsHandler handler1 = PrometheusMetricsFactory.getInstance();

        // Second call should return the same instance
        PrometheusMetricsHandler handler2 = PrometheusMetricsFactory.getInstance();

        Assertions.assertSame(handler1, handler2);
    }

    @Test
    public void testConfigureWithDisabledConfigurationAndNonNullHandler()
  {
        // Configuration is disabled but has a handler - should ignore the handler
        DepotPrometheusMetricsHandler customHandler = new DepotPrometheusMetricsHandler("test");
        PrometheusConfiguration config = new PrometheusConfiguration(false, customHandler);
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(config);

        // Should return VoidHandler because config is disabled
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
        Assertions.assertNotSame(customHandler, handler);
    }

    @Test
    public void testMultipleConfigureCalls()
  {
        // Configure multiple times and verify the instance changes each time
        DepotPrometheusMetricsHandler handler1 = new DepotPrometheusMetricsHandler("test1");
        DepotPrometheusMetricsHandler handler2 = new DepotPrometheusMetricsHandler("test2");
        DepotPrometheusMetricsHandler handler3 = new DepotPrometheusMetricsHandler("test3");

        PrometheusConfiguration config1 = new PrometheusConfiguration(true, handler1);
        PrometheusConfiguration config2 = new PrometheusConfiguration(true, handler2);
        PrometheusConfiguration config3 = new PrometheusConfiguration(true, handler3);

        PrometheusMetricsHandler result1 = PrometheusMetricsFactory.configure(config1);
        Assertions.assertSame(handler1, result1);

        PrometheusMetricsHandler result2 = PrometheusMetricsFactory.configure(config2);
        Assertions.assertSame(handler2, result2);

        PrometheusMetricsHandler result3 = PrometheusMetricsFactory.configure(config3);
        Assertions.assertSame(handler3, result3);

        // Final getInstance should return the last configured handler
        Assertions.assertSame(handler3, PrometheusMetricsFactory.getInstance());
    }

    @Test
    public void testConfigureNullAfterCustomHandler()
  {
        // Configure with custom handler first
        DepotPrometheusMetricsHandler customHandler = new DepotPrometheusMetricsHandler("test");
        PrometheusConfiguration config = new PrometheusConfiguration(true, customHandler);
        PrometheusMetricsFactory.configure(config);
        Assertions.assertSame(customHandler, PrometheusMetricsFactory.getInstance());

        // Then configure with null
        PrometheusMetricsHandler handler = PrometheusMetricsFactory.configure(null);
        Assertions.assertTrue(handler instanceof VoidPrometheusMetricsHandler);
        Assertions.assertNotSame(customHandler, handler);
    }

    @Test
    public void testGetInstanceAfterConfigureReturnsConfiguredHandler()
  {
        DepotPrometheusMetricsHandler customHandler = new DepotPrometheusMetricsHandler("test");
        PrometheusConfiguration config = new PrometheusConfiguration(true, customHandler);
        PrometheusMetricsFactory.configure(config);

        PrometheusMetricsHandler instance = PrometheusMetricsFactory.getInstance();
        Assertions.assertSame(customHandler, instance);
    }

    // Helper method to reset the singleton instance using reflection
    // This is necessary because PrometheusMetricsFactory uses a static singleton pattern
    // and there is no public API to reset the instance for testing purposes.
    private void resetFactoryInstance() throws Exception
    {
        Field instanceField = PrometheusMetricsFactory.class.getDeclaredField("INSTANCE");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}

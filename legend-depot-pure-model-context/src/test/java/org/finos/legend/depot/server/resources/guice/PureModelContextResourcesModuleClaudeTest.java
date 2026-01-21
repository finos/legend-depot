//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.server.resources.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.server.resources.pure.model.context.PureModelContextResource;
import org.finos.legend.depot.services.api.pure.model.context.PureModelContextService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PureModelContextResourcesModuleClaudeTest


{
    @Test
    public void testConstructor()
  {
        // Test that the constructor works without throwing any exceptions
        PureModelContextResourcesModule module = new PureModelContextResourcesModule();
        assertNotNull(module);
    }

    @Test
    public void testModuleConfiguration()
  {
        // Test that the module configures the bindings correctly
        PureModelContextResourcesModule module = new PureModelContextResourcesModule();

        // Create mocks for dependency injection
        PureModelContextService mockService = mock(PureModelContextService.class);
        PrometheusMetricsHandler mockMetricsHandler = mock(PrometheusMetricsHandler.class);

        // Create injector with the module and bind the required dependencies
        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(PureModelContextService.class).toInstance(mockService);
            binder.bind(PrometheusMetricsHandler.class).toInstance(mockMetricsHandler);
        });

        // Verify that PureModelContextResource can be retrieved from the injector
        PureModelContextResource resource = injector.getInstance(PureModelContextResource.class);
        assertNotNull(resource);

        // Note: The registerResourceMetrics provider method is not automatically invoked
        // because its return type (boolean) is not requested and the binding is not exposed
        // from the PrivateModule. The metrics registration would happen in a real application
        // where something explicitly depends on or requests the boolean binding.
    }

    @Test
    public void testRegisterResourceMetrics()
  {
        // Create a mock PrometheusMetricsHandler
        PrometheusMetricsHandler mockMetricsHandler = mock(PrometheusMetricsHandler.class);

        // Create the module instance
        PureModelContextResourcesModule module = new PureModelContextResourcesModule();

        // Call the registerResourceMetrics method directly
        boolean result = module.registerResourceMetrics(mockMetricsHandler);

        // Verify that the method returns true
        assertTrue(result);

        // Verify that registerResourceSummaries was called with the correct class
        verify(mockMetricsHandler, times(1)).registerResourceSummaries(PureModelContextResource.class);
    }

    @Test
    public void testRegisterResourceMetricsWithMultipleCalls()
  {
        // Create a mock PrometheusMetricsHandler
        PrometheusMetricsHandler mockMetricsHandler = mock(PrometheusMetricsHandler.class);

        // Create the module instance
        PureModelContextResourcesModule module = new PureModelContextResourcesModule();

        // Call the registerResourceMetrics method multiple times
        boolean result1 = module.registerResourceMetrics(mockMetricsHandler);
        boolean result2 = module.registerResourceMetrics(mockMetricsHandler);

        // Verify that both calls return true
        assertTrue(result1);
        assertTrue(result2);

        // Verify that registerResourceSummaries was called twice
        verify(mockMetricsHandler, times(2)).registerResourceSummaries(PureModelContextResource.class);
    }

    @Test
    public void testRegisterResourceMetricsWithException()
  {
        // Create a mock PrometheusMetricsHandler that throws an exception
        PrometheusMetricsHandler mockMetricsHandler = mock(PrometheusMetricsHandler.class);
        doThrow(new RuntimeException("Test exception")).when(mockMetricsHandler).registerResourceSummaries(any());

        // Create the module instance
        PureModelContextResourcesModule module = new PureModelContextResourcesModule();

        // Verify that the exception is propagated
        assertThrows(RuntimeException.class, () -> 
        {
            module.registerResourceMetrics(mockMetricsHandler);
        });
    }

    @Test
    public void testModuleBindingsAreExposed()
  {
        // Test that the PureModelContextResource binding is properly exposed from the private module
        PureModelContextResourcesModule module = new PureModelContextResourcesModule();

        // Create mocks for dependency injection
        PureModelContextService mockService = mock(PureModelContextService.class);
        PrometheusMetricsHandler mockMetricsHandler = mock(PrometheusMetricsHandler.class);

        // Create injector with the module
        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(PureModelContextService.class).toInstance(mockService);
            binder.bind(PrometheusMetricsHandler.class).toInstance(mockMetricsHandler);
        });

        // Verify that PureModelContextResource is accessible from outside the private module
        PureModelContextResource resource = injector.getInstance(PureModelContextResource.class);
        assertNotNull(resource);

        // Verify that the resource was created with the correct dependencies
        // by checking it's not the same instance on multiple calls (not a singleton in the private module)
        PureModelContextResource resource2 = injector.getInstance(PureModelContextResource.class);
        assertNotNull(resource2);
    }

    @Test
    public void testConfigureMethodBindings()
  {
        // Test that the configure method sets up the correct bindings
        // This verifies the configure() method behavior by checking the exposed bindings

        PrometheusMetricsHandler mockMetricsHandler = mock(PrometheusMetricsHandler.class);
        PureModelContextService mockService = mock(PureModelContextService.class);

        PureModelContextResourcesModule module = new PureModelContextResourcesModule();

        // Create the injector
        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(PureModelContextService.class).toInstance(mockService);
            binder.bind(PrometheusMetricsHandler.class).toInstance(mockMetricsHandler);
        });

        // Verify the PureModelContextResource binding is exposed and can be retrieved
        PureModelContextResource resource1 = injector.getInstance(PureModelContextResource.class);
        PureModelContextResource resource2 = injector.getInstance(PureModelContextResource.class);

        // Both retrievals should work, confirming the binding is properly configured and exposed
        assertNotNull(resource1);
        assertNotNull(resource2);

        // Verify they are different instances since there's no @Singleton annotation on the class itself
        assertNotSame(resource1, resource2);
    }
}

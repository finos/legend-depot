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

package org.finos.legend.depot.services.pure.model.context.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.api.pure.model.context.PureModelContextService;
import org.finos.legend.depot.services.pure.model.context.PureModelContextServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PureModelContextModuleClaudeTest


{
    @Test
    public void testConstructor()
  {
        // Test that the constructor works without throwing any exceptions
        PureModelContextModule module = new PureModelContextModule();
        assertNotNull(module);
    }

    @Test
    public void testModuleConfiguration()
  {
        // Test that the module configures the bindings correctly
        PureModelContextModule module = new PureModelContextModule();

        // Create mocks for the dependencies required by PureModelContextServiceImpl
        EntitiesService mockEntitiesService = mock(EntitiesService.class);
        ProjectsService mockProjectsService = mock(ProjectsService.class);

        // Create injector with the module and bind the required dependencies
        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Verify that PureModelContextService can be retrieved from the injector
        PureModelContextService service = injector.getInstance(PureModelContextService.class);
        assertNotNull(service);

        // Verify that the service is an instance of PureModelContextServiceImpl
        assertTrue(service instanceof PureModelContextServiceImpl);
    }

    @Test
    public void testServiceBindingIsExposed()
  {
        // Test that the PureModelContextService binding is properly exposed from the private module
        PureModelContextModule module = new PureModelContextModule();

        // Create mocks for dependency injection
        EntitiesService mockEntitiesService = mock(EntitiesService.class);
        ProjectsService mockProjectsService = mock(ProjectsService.class);

        // Create injector with the module
        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Verify that PureModelContextService is accessible from outside the private module
        PureModelContextService service = injector.getInstance(PureModelContextService.class);
        assertNotNull(service);

        // Verify that multiple retrievals work (confirm the binding is properly configured and exposed)
        PureModelContextService service2 = injector.getInstance(PureModelContextService.class);
        assertNotNull(service2);
    }

    @Test
    public void testConfigureMethodBindings()
  {
        // Test that the configure method sets up the correct bindings
        // This verifies the configure() method behavior by checking the exposed bindings

        EntitiesService mockEntitiesService = mock(EntitiesService.class);
        ProjectsService mockProjectsService = mock(ProjectsService.class);

        PureModelContextModule module = new PureModelContextModule();

        // Create the injector
        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Verify the PureModelContextService binding is exposed and can be retrieved
        PureModelContextService service1 = injector.getInstance(PureModelContextService.class);
        PureModelContextService service2 = injector.getInstance(PureModelContextService.class);

        // Both retrievals should work, confirming the binding is properly configured and exposed
        assertNotNull(service1);
        assertNotNull(service2);

        // Verify both are PureModelContextServiceImpl instances
        assertTrue(service1 instanceof PureModelContextServiceImpl);
        assertTrue(service2 instanceof PureModelContextServiceImpl);
    }

    @Test
    public void testBindingToImplementationClass()
  {
        // Test that the binding correctly maps interface to implementation
        PureModelContextModule module = new PureModelContextModule();

        EntitiesService mockEntitiesService = mock(EntitiesService.class);
        ProjectsService mockProjectsService = mock(ProjectsService.class);

        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Get the service from the injector
        PureModelContextService service = injector.getInstance(PureModelContextService.class);

        // Verify it's the correct implementation
        assertEquals(PureModelContextServiceImpl.class, service.getClass());
    }

    @Test
    public void testModuleCanBeInstalledMultipleTimes()
  {
        // Test that the module can be installed in multiple injectors without issues
        PureModelContextModule module1 = new PureModelContextModule();
        PureModelContextModule module2 = new PureModelContextModule();

        EntitiesService mockEntitiesService = mock(EntitiesService.class);
        ProjectsService mockProjectsService = mock(ProjectsService.class);

        // Create first injector
        Injector injector1 = Guice.createInjector(binder ->
                {
            binder.install(module1);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Create second injector
        Injector injector2 = Guice.createInjector(binder ->
                {
            binder.install(module2);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Verify both injectors work independently
        PureModelContextService service1 = injector1.getInstance(PureModelContextService.class);
        PureModelContextService service2 = injector2.getInstance(PureModelContextService.class);

        assertNotNull(service1);
        assertNotNull(service2);

        // Services from different injectors should be different instances
        assertNotSame(service1, service2);
    }

    @Test
    public void testModuleWithRealDependencies()
  {
        // Test the module configuration with actual mock implementations
        // This ensures the configure() method properly sets up bindings that work with real dependencies

        PureModelContextModule module = new PureModelContextModule();

        EntitiesService mockEntitiesService = mock(EntitiesService.class);
        ProjectsService mockProjectsService = mock(ProjectsService.class);

        Injector injector = Guice.createInjector(binder ->
                {
            binder.install(module);
            binder.bind(EntitiesService.class).toInstance(mockEntitiesService);
            binder.bind(ProjectsService.class).toInstance(mockProjectsService);
        });

        // Get the service - this exercises both the binding and the expose() call in configure()
        PureModelContextService service = injector.getInstance(PureModelContextService.class);

        // Verify the service was created successfully
        assertNotNull(service);

        // Verify it's the correct type
        assertTrue(service instanceof PureModelContextServiceImpl);

        // Verify multiple retrievals work correctly
        PureModelContextService service2 = injector.getInstance(PureModelContextService.class);
        assertNotNull(service2);
        assertTrue(service2 instanceof PureModelContextServiceImpl);
    }
}

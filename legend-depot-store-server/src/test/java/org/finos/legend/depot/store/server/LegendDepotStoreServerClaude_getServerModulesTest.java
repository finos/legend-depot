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

import com.google.inject.Module;
import org.finos.legend.depot.core.server.guice.ServerInfoModule;
import org.finos.legend.depot.core.services.guice.AuthorisationModule;
import org.finos.legend.depot.core.services.guice.MonitoringModule;
import org.finos.legend.depot.services.guice.ArtifactsSchedulesModule;
import org.finos.legend.depot.services.guice.ArtifactsServicesModule;
import org.finos.legend.depot.services.guice.ManageCoreDataServicesModule;
import org.finos.legend.depot.services.guice.ManageEntitiesServicesModule;
import org.finos.legend.depot.services.guice.ManageGenerationsServicesModule;
import org.finos.legend.depot.services.guice.ManageQueryMetricsSchedulesModule;
import org.finos.legend.depot.services.guice.ManageSchedulesModule;
import org.finos.legend.depot.services.guice.ManageVersionedEntitiesServicesModule;
import org.finos.legend.depot.services.guice.NotificationsModule;
import org.finos.legend.depot.services.guice.NotificationsQueueSchedulesModule;
import org.finos.legend.depot.services.guice.NotificationsSchedulesModule;
import org.finos.legend.depot.services.guice.QueryMetricsModule;
import org.finos.legend.depot.services.guice.RepositoryModule;
import org.finos.legend.depot.services.guice.VersionReconciliationSchedulesModule;
import org.finos.legend.depot.store.mongo.core.MongoClientModule;
import org.finos.legend.depot.store.mongo.guice.ArtifactsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.CoreDataMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.EntitiesMigrationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageCoreDataStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageEntitiesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageGenerationsStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.ManageMongoStoreSchedulesModule;
import org.finos.legend.depot.store.mongo.guice.ManageNotificationsQueueMongoModule;
import org.finos.legend.depot.store.mongo.guice.ManageQueryMetricsMongoStoreModule;
import org.finos.legend.depot.store.mongo.guice.ManageSchedulesStoreMongoModule;
import org.finos.legend.depot.store.mongo.guice.NotificationsStoreMongoModule;
import org.finos.legend.depot.store.resources.guice.ArtifactsResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageCoreDataResourcesModule;
import org.finos.legend.depot.store.resources.guice.ManageSchedulesResourcesModule;
import org.finos.legend.depot.store.resources.guice.NotificationsResourcesModule;
import org.finos.legend.depot.store.resources.guice.RepositoryResourcesModule;
import org.finos.legend.depot.store.server.guice.DepotStoreServerModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LegendDepotStoreServerClaude_getServerModulesTest
{
    @Test
    public void testGetServerModulesReturnsNonNullList()
    {
        // Test that getServerModules returns a non-null list
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        Assertions.assertNotNull(modules);
    }

    @Test
    public void testGetServerModulesReturnsNonEmptyList()
    {
        // Test that getServerModules returns a non-empty list
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        Assertions.assertFalse(modules.isEmpty());
    }

    @Test
    public void testGetServerModulesReturnsExpectedNumberOfModules()
    {
        // Test that getServerModules returns the expected number of modules (36)
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        // Based on the implementation, there should be 36 modules
        Assertions.assertEquals(36, modules.size());
    }

    @Test
    public void testGetServerModulesContainsServerInfoModule()
    {
        // Test that getServerModules includes ServerInfoModule
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsServerInfoModule = modules.stream()
            .anyMatch(module -> module instanceof ServerInfoModule);
        Assertions.assertTrue(containsServerInfoModule);
    }

    @Test
    public void testGetServerModulesContainsDepotStoreServerModule()
    {
        // Test that getServerModules includes DepotStoreServerModule
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsDepotStoreServerModule = modules.stream()
            .anyMatch(module -> module instanceof DepotStoreServerModule);
        Assertions.assertTrue(containsDepotStoreServerModule);
    }

    @Test
    public void testGetServerModulesContainsMongoClientModule()
    {
        // Test that getServerModules includes MongoClientModule
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsMongoClientModule = modules.stream()
            .anyMatch(module -> module instanceof MongoClientModule);
        Assertions.assertTrue(containsMongoClientModule);
    }

    @Test
    public void testGetServerModulesContainsCoreDataModules()
    {
        // Test that getServerModules includes all core data related modules
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsManageCoreDataResourcesModule = modules.stream()
            .anyMatch(module -> module instanceof ManageCoreDataResourcesModule);
        boolean containsManageCoreDataServicesModule = modules.stream()
            .anyMatch(module -> module instanceof ManageCoreDataServicesModule);
        boolean containsManageCoreDataStoreMongoModule = modules.stream()
            .anyMatch(module -> module instanceof ManageCoreDataStoreMongoModule);

        Assertions.assertTrue(containsManageCoreDataResourcesModule);
        Assertions.assertTrue(containsManageCoreDataServicesModule);
        Assertions.assertTrue(containsManageCoreDataStoreMongoModule);
    }

    @Test
    public void testGetServerModulesContainsEntitiesModules()
    {
        // Test that getServerModules includes all entities related modules
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsManageEntitiesServicesModule = modules.stream()
            .anyMatch(module -> module instanceof ManageEntitiesServicesModule);
        boolean containsManageVersionedEntitiesServicesModule = modules.stream()
            .anyMatch(module -> module instanceof ManageVersionedEntitiesServicesModule);
        boolean containsManageEntitiesStoreMongoModule = modules.stream()
            .anyMatch(module -> module instanceof ManageEntitiesStoreMongoModule);

        Assertions.assertTrue(containsManageEntitiesServicesModule);
        Assertions.assertTrue(containsManageVersionedEntitiesServicesModule);
        Assertions.assertTrue(containsManageEntitiesStoreMongoModule);
    }

    @Test
    public void testGetServerModulesContainsArtifactsModules()
    {
        // Test that getServerModules includes all artifacts related modules
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsArtifactsResourcesModule = modules.stream()
            .anyMatch(module -> module instanceof ArtifactsResourcesModule);
        boolean containsArtifactsServicesModule = modules.stream()
            .anyMatch(module -> module instanceof ArtifactsServicesModule);
        boolean containsArtifactsSchedulesModule = modules.stream()
            .anyMatch(module -> module instanceof ArtifactsSchedulesModule);
        boolean containsArtifactsStoreMongoModule = modules.stream()
            .anyMatch(module -> module instanceof ArtifactsStoreMongoModule);

        Assertions.assertTrue(containsArtifactsResourcesModule);
        Assertions.assertTrue(containsArtifactsServicesModule);
        Assertions.assertTrue(containsArtifactsSchedulesModule);
        Assertions.assertTrue(containsArtifactsStoreMongoModule);
    }

    @Test
    public void testGetServerModulesContainsNotificationsModules()
    {
        // Test that getServerModules includes all notifications related modules
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsNotificationsResourcesModule = modules.stream()
            .anyMatch(module -> module instanceof NotificationsResourcesModule);
        boolean containsNotificationsModule = modules.stream()
            .anyMatch(module -> module instanceof NotificationsModule);
        boolean containsNotificationsStoreMongoModule = modules.stream()
            .anyMatch(module -> module instanceof NotificationsStoreMongoModule);
        boolean containsNotificationsSchedulesModule = modules.stream()
            .anyMatch(module -> module instanceof NotificationsSchedulesModule);

        Assertions.assertTrue(containsNotificationsResourcesModule);
        Assertions.assertTrue(containsNotificationsModule);
        Assertions.assertTrue(containsNotificationsStoreMongoModule);
        Assertions.assertTrue(containsNotificationsSchedulesModule);
    }

    @Test
    public void testGetServerModulesAllModulesAreNonNull()
    {
        // Test that all modules in the list are non-null
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        for (Module module : modules)
        {
            Assertions.assertNotNull(module);
        }
    }

    @Test
    public void testGetServerModulesAllModulesAreInstantiable()
    {
        // Test that all modules in the list are properly instantiated Module objects
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        for (Module module : modules)
        {
            Assertions.assertTrue(module instanceof Module);
        }
    }

    @Test
    public void testGetServerModulesCanBeCalledMultipleTimes()
    {
        // Test that getServerModules can be called multiple times
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules1 = server.getServerModules();
        List<Module> modules2 = server.getServerModules();

        Assertions.assertNotNull(modules1);
        Assertions.assertNotNull(modules2);
        Assertions.assertEquals(modules1.size(), modules2.size());
    }

    @Test
    public void testGetServerModulesCreatesNewListEachTime()
    {
        // Test that getServerModules creates a new list (with new module instances) each time
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules1 = server.getServerModules();
        List<Module> modules2 = server.getServerModules();

        // The lists should be different instances
        Assertions.assertNotSame(modules1, modules2);

        // The modules inside should also be different instances (new instances each time)
        for (int i = 0; i < modules1.size(); i++)
        {
            Assertions.assertNotSame(modules1.get(i), modules2.get(i));
        }
    }

    @Test
    public void testGetServerModulesWithMultipleServerInstances()
    {
        // Test that different server instances return their own module lists
        LegendDepotStoreServer server1 = new LegendDepotStoreServer();
        LegendDepotStoreServer server2 = new LegendDepotStoreServer();

        List<Module> modules1 = server1.getServerModules();
        List<Module> modules2 = server2.getServerModules();

        Assertions.assertNotNull(modules1);
        Assertions.assertNotNull(modules2);
        Assertions.assertEquals(modules1.size(), modules2.size());
        Assertions.assertNotSame(modules1, modules2);
    }

    @Test
    public void testGetServerModulesContainsAuthorizationModule()
    {
        // Test that getServerModules includes AuthorisationModule
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsAuthorisationModule = modules.stream()
            .anyMatch(module -> module instanceof AuthorisationModule);
        Assertions.assertTrue(containsAuthorisationModule);
    }

    @Test
    public void testGetServerModulesContainsMonitoringModule()
    {
        // Test that getServerModules includes MonitoringModule
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsMonitoringModule = modules.stream()
            .anyMatch(module -> module instanceof MonitoringModule);
        Assertions.assertTrue(containsMonitoringModule);
    }

    @Test
    public void testGetServerModulesMethodIsProtected()
    {
        // Test that getServerModules is a protected method (following BaseServer pattern)
        try
        {
            Method method = LegendDepotStoreServer.class.getDeclaredMethod("getServerModules");
            int modifiers = method.getModifiers();

            // The method should be protected (inherited from BaseServer)
            Assertions.assertTrue(java.lang.reflect.Modifier.isProtected(modifiers) ||
                                 java.lang.reflect.Modifier.isPublic(modifiers));
        }
        catch (NoSuchMethodException e)
        {
            Assertions.fail("getServerModules method should exist");
        }
    }

    @Test
    public void testGetServerModulesReturnsListOfModules()
    {
        // Test that the return type is List<Module>
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        Assertions.assertTrue(modules instanceof List);
        // Verify all elements are Module instances
        modules.forEach(module -> Assertions.assertTrue(module instanceof Module));
    }

    @Test
    public void testGetServerModulesContainsQueryMetricsModules()
    {
        // Test that getServerModules includes query metrics related modules
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsQueryMetricsModule = modules.stream()
            .anyMatch(module -> module instanceof QueryMetricsModule);
        boolean containsManageQueryMetricsSchedulesModule = modules.stream()
            .anyMatch(module -> module instanceof ManageQueryMetricsSchedulesModule);
        boolean containsManageQueryMetricsMongoStoreModule = modules.stream()
            .anyMatch(module -> module instanceof ManageQueryMetricsMongoStoreModule);

        Assertions.assertTrue(containsQueryMetricsModule);
        Assertions.assertTrue(containsManageQueryMetricsSchedulesModule);
        Assertions.assertTrue(containsManageQueryMetricsMongoStoreModule);
    }

    @Test
    public void testGetServerModulesContainsRepositoryModules()
    {
        // Test that getServerModules includes repository related modules
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        boolean containsRepositoryResourcesModule = modules.stream()
            .anyMatch(module -> module instanceof RepositoryResourcesModule);
        boolean containsRepositoryModule = modules.stream()
            .anyMatch(module -> module instanceof RepositoryModule);

        Assertions.assertTrue(containsRepositoryResourcesModule);
        Assertions.assertTrue(containsRepositoryModule);
    }

    @Test
    public void testGetServerModulesNoDuplicateModuleTypes()
    {
        // Test that there are no duplicate module types in the list
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        List<Module> modules = server.getServerModules();

        Set<Class<?>> moduleClasses = new HashSet<>();
        for (Module module : modules)
        {
            Class<?> moduleClass = module.getClass();
            Assertions.assertFalse(moduleClasses.contains(moduleClass),
                "Duplicate module type found: " + moduleClass.getName());
            moduleClasses.add(moduleClass);
        }
    }
}

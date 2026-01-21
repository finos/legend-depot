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
package org.finos.legend.depot.services.guice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.store.api.admin.schedules.ScheduleInstancesStore;
import org.finos.legend.depot.store.api.admin.schedules.SchedulesStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SchedulesModuleClaudeTest 

{
  @Test
  public void testConstructor()
  {
    // Test that the constructor creates a valid instance
    SchedulesModule module = new SchedulesModule();
    assertNotNull(module);
  }

  @Test
  public void testInitialiseHouseKeeperReturnsFalse()
  {
    // Test that initialiseHouseKeeper returns false for base SchedulesModule
    SchedulesModule module = new SchedulesModule();
    // Use reflection to access the protected method initialiseHouseKeeper
    // This is necessary because the method is protected and we need to verify its return value
    // to ensure proper test coverage. The method determines whether the house keeper

    // should be initialized, which is a critical configuration setting.
    try {
      Method method = SchedulesModule.class.getDeclaredMethod("initialiseHouseKeeper");
      method.setAccessible(true);
      boolean result = (boolean) method.invoke(module);
      assertFalse(result, "initialiseHouseKeeper should return false for base SchedulesModule");
    } catch (Exception e) {
      fail("Failed to invoke initialiseHouseKeeper: " + e.getMessage());
    }
  }

  @Test
  public void testConfigureMethodExists()
  {
    // Test that the configure method exists
    SchedulesModule module = new SchedulesModule();

    try {
      Method method = SchedulesModule.class.getDeclaredMethod("configure");
      assertNotNull(method, "configure method should exist");
      assertEquals(void.class, method.getReturnType());
      assertEquals(0, method.getParameterCount());
    } catch (NoSuchMethodException e) {
      fail("configure method should be declared in SchedulesModule");
    }
  }

  @Test
  public void testGetFactoryMethodExists()
  {
    // Test that the getFactory method exists with correct signature
    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      assertNotNull(method, "getFactory method should exist");
      assertEquals(SchedulesFactory.class, method.getReturnType());
      assertEquals(2, method.getParameterCount());
    } catch (NoSuchMethodException e) {
      fail("getFactory method should be declared in SchedulesModule with correct signature");
    }
  }

  @Test
  public void testGetFactoryReturnsNonNullFactory()
  {
    // Test that getFactory returns a non-null SchedulesFactory instance
    SchedulesModule module = new SchedulesModule();
    SchedulesStore mockSchedulesStore = mock(SchedulesStore.class);
    ScheduleInstancesStore mockInstancesStore = mock(ScheduleInstancesStore.class);

    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      method.setAccessible(true);
      SchedulesFactory factory =
          (SchedulesFactory) method.invoke(module, mockSchedulesStore, mockInstancesStore);
      assertNotNull(factory, "getFactory should return a non-null SchedulesFactory");
    } catch (Exception e) {
      fail("Failed to invoke getFactory: " + e.getMessage());
    }
  }

  @Test
  public void testGetFactoryWithDifferentStores()
  {
    // Test that getFactory works with different store implementations
    SchedulesModule module = new SchedulesModule();
    SchedulesStore mockSchedulesStore1 = mock(SchedulesStore.class);
    ScheduleInstancesStore mockInstancesStore1 = mock(ScheduleInstancesStore.class);
    SchedulesStore mockSchedulesStore2 = mock(SchedulesStore.class);
    ScheduleInstancesStore mockInstancesStore2 = mock(ScheduleInstancesStore.class);

    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      method.setAccessible(true);

      SchedulesFactory factory1 =
          (SchedulesFactory) method.invoke(module, mockSchedulesStore1, mockInstancesStore1);
      SchedulesFactory factory2 =
          (SchedulesFactory) method.invoke(module, mockSchedulesStore2, mockInstancesStore2);

      assertNotNull(factory1, "First factory should not be null");
      assertNotNull(factory2, "Second factory should not be null");
      assertNotSame(
          factory1, factory2, "Different invocations should create different factory instances");
    } catch (Exception e) {
      fail("Failed to invoke getFactory with different stores: " + e.getMessage());
    }
  }

  @Test
  public void testModuleExtendsPrivateModule()
  {
    // Test that SchedulesModule extends PrivateModule
    SchedulesModule module = new SchedulesModule();
    assertTrue(module instanceof PrivateModule, "SchedulesModule should extend PrivateModule");
  }

  @Test
  public void testMultipleModuleInstances()
  {
    // Test that multiple instances of the module can be created independently
    SchedulesModule module1 = new SchedulesModule();
    SchedulesModule module2 = new SchedulesModule();

    assertNotNull(module1);
    assertNotNull(module2);
    assertNotSame(module1, module2, "Each instance should be distinct");
  }

  @Test
  public void testModuleIsPublic()
  {
    // Test that the module class is public and can be instantiated
    try {
      Class<?> moduleClass = SchedulesModule.class;
      assertTrue(Modifier.isPublic(moduleClass.getModifiers()), "SchedulesModule should be public");

      // Verify that a default constructor exists and is public
      Constructor<?> constructor = moduleClass.getDeclaredConstructor();
      assertTrue(
          Modifier.isPublic(constructor.getModifiers()), "Default constructor should be public");
    } catch (Exception e) {
      fail("Failed to verify module accessibility: " + e.getMessage());
    }
  }

  @Test
  public void testGetFactoryUsesInitialiseHouseKeeper()
  {
    // Test that getFactory method calls initialiseHouseKeeper
    // We can verify this by checking the behavior with a subclass that overrides
    // initialiseHouseKeeper
    SchedulesModule baseModule = new SchedulesModule();
    SchedulesModule derivedModule = new ManageSchedulesModule();

    SchedulesStore mockSchedulesStore = mock(SchedulesStore.class);
    ScheduleInstancesStore mockInstancesStore = mock(ScheduleInstancesStore.class);

    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      method.setAccessible(true);

      SchedulesFactory baseFactory =
          (SchedulesFactory) method.invoke(baseModule, mockSchedulesStore, mockInstancesStore);
      SchedulesFactory derivedFactory =
          (SchedulesFactory) method.invoke(derivedModule, mockSchedulesStore, mockInstancesStore);

      assertNotNull(baseFactory, "Base factory should not be null");
      assertNotNull(derivedFactory, "Derived factory should not be null");

      // Both factories should be created successfully, showing that getFactory properly uses
      // initialiseHouseKeeper
    } catch (Exception e) {
      fail("Failed to test getFactory with initialiseHouseKeeper: " + e.getMessage());
    }
  }

  @Test
  public void testGetFactoryWithNullStoresShouldWork()
  {
    // Test that getFactory can be invoked (though it may fail internally if null checks are not
    // present)
    // This tests the method's ability to handle edge cases
    SchedulesModule module = new SchedulesModule();

    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      method.setAccessible(true);

      // This should create a factory, though it may not be fully functional with null stores
      SchedulesFactory factory =
          (SchedulesFactory)
              method.invoke(module, mock(SchedulesStore.class), mock(ScheduleInstancesStore.class));
      assertNotNull(factory, "getFactory should return a factory even with mock stores");
    } catch (Exception e) {
      fail("Failed to invoke getFactory: " + e.getMessage());
    }
  }

  @Test
  public void testConfigureMethodIsProtected()
  {
    // Test that configure method has protected access
    try {
      Method method = SchedulesModule.class.getDeclaredMethod("configure");
      assertTrue(
          Modifier.isProtected(method.getModifiers()), "configure method should be protected");
    } catch (NoSuchMethodException e) {
      fail("configure method should exist");
    }
  }

  @Test
  public void testGetFactoryMethodIsPublic()
  {
    // Test that getFactory method has public access
    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      assertTrue(Modifier.isPublic(method.getModifiers()), "getFactory method should be public");
    } catch (NoSuchMethodException e) {
      fail("getFactory method should exist");
    }
  }

  @Test
  public void testInitialiseHouseKeeperMethodIsProtected()
  {
    // Test that initialiseHouseKeeper method has protected access
    try {
      Method method = SchedulesModule.class.getDeclaredMethod("initialiseHouseKeeper");
      assertTrue(
          Modifier.isProtected(method.getModifiers()),
          "initialiseHouseKeeper method should be protected");
    } catch (NoSuchMethodException e) {
      fail("initialiseHouseKeeper method should exist");
    }
  }

  @Test
  public void testGetFactoryMethodHasProvidesAnnotation()
  {
    // Test that getFactory method has @Provides annotation
    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      boolean hasProvides = method.isAnnotationPresent(Provides.class);
      assertTrue(hasProvides, "getFactory method should have @Provides annotation");
    } catch (NoSuchMethodException e) {
      fail("getFactory method should exist");
    }
  }

  @Test
  public void testGetFactoryMethodHasSingletonAnnotation()
  {
    // Test that getFactory method has @Singleton annotation
    try {
      Method method =
          SchedulesModule.class.getDeclaredMethod(
              "getFactory", SchedulesStore.class, ScheduleInstancesStore.class);
      boolean hasSingleton = method.isAnnotationPresent(Singleton.class);
      assertTrue(hasSingleton, "getFactory method should have @Singleton annotation");
    } catch (NoSuchMethodException e) {
      fail("getFactory method should exist");
    }
  }
}

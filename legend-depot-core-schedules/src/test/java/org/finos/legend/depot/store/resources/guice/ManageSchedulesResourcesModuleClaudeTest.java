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

package org.finos.legend.depot.store.resources.guice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.inject.PrivateModule;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

public class ManageSchedulesResourcesModuleClaudeTest


{
    @Test
    public void testConstructor()
  {
        // Test that the constructor creates a valid instance
        ManageSchedulesResourcesModule module = new ManageSchedulesResourcesModule();
        assertNotNull(module);
    }

    @Test
    public void testModuleExtendsPrivateModule()
  {
        // Test that ManageSchedulesResourcesModule extends PrivateModule
        ManageSchedulesResourcesModule module = new ManageSchedulesResourcesModule();
        assertTrue(module instanceof PrivateModule,
            "ManageSchedulesResourcesModule should extend PrivateModule");
    }

    @Test
    public void testConfigureMethodExists()
  {
        // Test that the configure method exists
        try
        {
            Method method = ManageSchedulesResourcesModule.class.getDeclaredMethod("configure");
            assertNotNull(method, "configure method should exist");
            assertEquals(void.class, method.getReturnType());
            assertEquals(0, method.getParameterCount());
        }
        catch (NoSuchMethodException e)
        {
            fail("configure method should be declared in ManageSchedulesResourcesModule");
        }
    }

    @Test
    public void testConfigureMethodIsProtected()
  {
        // Test that configure method has protected access
        try
        {
            Method method = ManageSchedulesResourcesModule.class.getDeclaredMethod("configure");
            assertTrue(Modifier.isProtected(method.getModifiers()),
                "configure method should be protected");
        }
        catch (NoSuchMethodException e)
        {
            fail("configure method should exist");
        }
    }

    @Test
    public void testMultipleModuleInstances()
  {
        // Test that multiple instances of the module can be created independently
        ManageSchedulesResourcesModule module1 = new ManageSchedulesResourcesModule();
        ManageSchedulesResourcesModule module2 = new ManageSchedulesResourcesModule();

        assertNotNull(module1);
        assertNotNull(module2);
        assertNotSame(module1, module2, "Each instance should be distinct");
    }

    @Test
    public void testModuleIsPublic()
  {
        // Test that the module class is public and can be instantiated
        try
        {
            Class<?> moduleClass = ManageSchedulesResourcesModule.class;
            assertTrue(Modifier.isPublic(moduleClass.getModifiers()),
                "ManageSchedulesResourcesModule should be public");

            // Verify that a default constructor exists and is public
            Constructor<?> constructor = moduleClass.getDeclaredConstructor();
            assertTrue(Modifier.isPublic(constructor.getModifiers()),
                "Default constructor should be public");
        }
        catch (Exception e)
        {
            fail("Failed to verify module accessibility: " + e.getMessage());
        }
    }

    @Test
    public void testModuleStructure()
  {
        // Test that the module has the expected class structure
        ManageSchedulesResourcesModule module = new ManageSchedulesResourcesModule();

        Class<?> moduleClass = module.getClass();

        // Verify it extends PrivateModule
        assertTrue(PrivateModule.class.isAssignableFrom(moduleClass),
            "Should extend PrivateModule");

        // Verify it has the configure method
        boolean hasConfigureMethod = false;
        for (Method method : moduleClass.getDeclaredMethods())
        {
            if (method.getName().equals("configure") && method.getParameterCount() == 0)
            {
                hasConfigureMethod = true;
                break;
            }
        }
        assertTrue(hasConfigureMethod, "Should have configure() method");
    }

    @Test
    public void testDefaultConstructor()
  {
        // Test that the default constructor exists and can be invoked
        try
        {
            Constructor<ManageSchedulesResourcesModule> constructor =
                ManageSchedulesResourcesModule.class.getDeclaredConstructor();
            ManageSchedulesResourcesModule module = constructor.newInstance();
            assertNotNull(module, "Constructor should create a valid instance");
        }
        catch (Exception e)
        {
            fail("Failed to invoke default constructor: " + e.getMessage());
        }
    }

    @Test
    public void testModulePackage()
  {
        // Test that the module is in the correct package
        ManageSchedulesResourcesModule module = new ManageSchedulesResourcesModule();
        assertEquals("org.finos.legend.depot.store.resources.guice",
            module.getClass().getPackage().getName(),
            "Module should be in correct package");
    }
}

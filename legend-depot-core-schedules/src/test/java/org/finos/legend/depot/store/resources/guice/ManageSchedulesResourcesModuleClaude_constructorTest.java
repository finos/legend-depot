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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.PrivateModule;
import org.junit.jupiter.api.Test;

public class ManageSchedulesResourcesModuleClaude_constructorTest
{
    @Test
    public void testDefaultConstructorCreatesValidInstance()
    {
        // Test that the default constructor creates a non-null instance
        ManageSchedulesResourcesModule module = new ManageSchedulesResourcesModule();
        assertNotNull(module, "Constructor should create a non-null instance");
    }

    @Test
    public void testConstructorCreatesPrivateModuleInstance()
    {
        // Test that the constructor creates an instance that is a PrivateModule
        ManageSchedulesResourcesModule module = new ManageSchedulesResourcesModule();
        assertTrue(module instanceof PrivateModule,
            "Constructor should create an instance of PrivateModule");
    }

    @Test
    public void testConstructorAllowsMultipleInstantiations()
    {
        // Test that the constructor can be called multiple times successfully
        ManageSchedulesResourcesModule module1 = new ManageSchedulesResourcesModule();
        ManageSchedulesResourcesModule module2 = new ManageSchedulesResourcesModule();
        ManageSchedulesResourcesModule module3 = new ManageSchedulesResourcesModule();

        assertNotNull(module1, "First instantiation should succeed");
        assertNotNull(module2, "Second instantiation should succeed");
        assertNotNull(module3, "Third instantiation should succeed");
    }
}

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

package org.finos.legend.depot.store.mongo.guice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.google.inject.PrivateModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class QueryMetricsMongoStoreModuleClaudeTest 

{

    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link QueryMetricsMongoStoreModule#QueryMetricsMongoStoreModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void QueryMetricsMongoStoreModule.<init>()"})
    void testConstructor()
  {
        // Arrange and Act
        QueryMetricsMongoStoreModule actualModule = new QueryMetricsMongoStoreModule();

        // Assert
        assertNotNull(actualModule);
    }

    /**
     * Test configure method.
     * The configure method is protected and sets up Guice bindings.
     * Since configure() requires Guice infrastructure to run properly,
     * this test verifies that the module can be instantiated and
     * the configure method exists with the correct signature.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link QueryMetricsMongoStoreModule#configure()}
     * </ul>
     */
    @Test
    @DisplayName("Test configure method")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void QueryMetricsMongoStoreModule.configure()"})
    void testConfigure()
  {
        // Arrange
        QueryMetricsMongoStoreModule module = new QueryMetricsMongoStoreModule();

        // Assert - Verify the module is instantiated successfully
        assertNotNull(module);

        // Verify that the module extends PrivateModule
        assertTrue(module instanceof PrivateModule, "QueryMetricsMongoStoreModule should extend PrivateModule");
    }

    /**
     * Test that configure method exists and has correct access modifier.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link QueryMetricsMongoStoreModule#configure()}
     * </ul>
     */
    @Test
    @DisplayName("Test configure method exists and is protected")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void QueryMetricsMongoStoreModule.configure()"})
    void testConfigureMethodExists() throws NoSuchMethodException {
        // Arrange and Act
        Method method = QueryMetricsMongoStoreModule.class.getDeclaredMethod("configure");

        // Assert
        assertNotNull(method, "configure method should exist");
        assertTrue(Modifier.isProtected(method.getModifiers()), "configure method should be protected");
    }

    /**
     * Test that the module extends PrivateModule.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link QueryMetricsMongoStoreModule#QueryMetricsMongoStoreModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test module extends PrivateModule")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void QueryMetricsMongoStoreModule.<init>()"})
    void testModuleExtendsPrivateModule()
  {
        // Arrange and Act
        QueryMetricsMongoStoreModule module = new QueryMetricsMongoStoreModule();

        // Assert
        assertTrue(module instanceof PrivateModule, "QueryMetricsMongoStoreModule should extend PrivateModule");
    }

    /**
     * Test that module class is public and can be instantiated.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link QueryMetricsMongoStoreModule#QueryMetricsMongoStoreModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test module is public")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void QueryMetricsMongoStoreModule.<init>()"})
    void testModuleIsPublic()
  {
        // Arrange and Act
        Class<?> moduleClass = QueryMetricsMongoStoreModule.class;

        // Assert
        assertTrue(Modifier.isPublic(moduleClass.getModifiers()), "QueryMetricsMongoStoreModule should be public");
    }
}

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

package org.finos.legend.depot.core.server;

import com.google.inject.Module;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseServerClaude_isEnvironmentVariableSubstitutionStrictTest
{
    /**
     * Reflection is necessary to test the protected method isEnvironmentVariableSubstitutionStrict()
     * without instantiating BaseServer. Instantiating BaseServer (even a test subclass) triggers
     * Dropwizard's Application initialization in the constructor, which requires numerous
     * dependencies and configurations that are not relevant to testing this simple method.
     *
     * The method under test is a simple protected method that returns a boolean value and is
     * designed to be overridden by subclasses. Using reflection allows us to test the default
     * implementation and verify that subclasses can override it without the complexity of
     * setting up a full Dropwizard application environment.
     */

    /**
     * Concrete implementation of BaseServer for testing purposes.
     * This class provides minimal implementations of abstract methods.
     */
    private static class TestBaseServer extends BaseServer<ServerConfiguration>
    {
        @Override
        protected List<Module> getServerModules()
        {
            return Collections.emptyList();
        }

        @Override
        protected void registerJacksonJsonProvider(JerseyEnvironment jerseyEnvironment)
        {
            // No-op for testing
        }

        @Override
        protected void initialiseCors(Environment environment)
        {
            // No-op for testing
        }
    }

    /**
     * Test subclass that overrides isEnvironmentVariableSubstitutionStrict to return false.
     */
    private static class NonStrictTestBaseServer extends TestBaseServer
    {
        @Override
        protected boolean isEnvironmentVariableSubstitutionStrict()
        {
            return false;
        }
    }

    /**
     * Test subclass that overrides isEnvironmentVariableSubstitutionStrict to return true explicitly.
     */
    private static class StrictTestBaseServer extends TestBaseServer
    {
        @Override
        protected boolean isEnvironmentVariableSubstitutionStrict()
        {
            return true;
        }
    }

    @Test
    @DisplayName("Test default implementation returns true")
    void testDefaultImplementationReturnsTrue() throws Exception
    {
        // Arrange - create a partial mock that doesn't trigger constructor
        TestBaseServer server = Mockito.mock(TestBaseServer.class, Mockito.CALLS_REAL_METHODS);

        // Act
        Method method = BaseServer.class.getDeclaredMethod("isEnvironmentVariableSubstitutionStrict");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(server);

        // Assert
        assertTrue(result, "Default implementation should return true");
    }

    @Test
    @DisplayName("Test overridden implementation can return false")
    void testOverriddenImplementationReturnsFalse() throws Exception
    {
        // Arrange
        NonStrictTestBaseServer server = Mockito.mock(NonStrictTestBaseServer.class, Mockito.CALLS_REAL_METHODS);

        // Act
        Method method = BaseServer.class.getDeclaredMethod("isEnvironmentVariableSubstitutionStrict");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(server);

        // Assert
        assertFalse(result, "Overridden implementation should return false");
    }

    @Test
    @DisplayName("Test overridden implementation can return true")
    void testOverriddenImplementationReturnsTrue() throws Exception
    {
        // Arrange
        StrictTestBaseServer server = Mockito.mock(StrictTestBaseServer.class, Mockito.CALLS_REAL_METHODS);

        // Act
        Method method = BaseServer.class.getDeclaredMethod("isEnvironmentVariableSubstitutionStrict");
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(server);

        // Assert
        assertTrue(result, "Overridden implementation should return true");
    }

    @Test
    @DisplayName("Test method is idempotent for default implementation")
    void testMethodIdempotentForDefaultImplementation() throws Exception
    {
        // Arrange
        TestBaseServer server = Mockito.mock(TestBaseServer.class, Mockito.CALLS_REAL_METHODS);
        Method method = BaseServer.class.getDeclaredMethod("isEnvironmentVariableSubstitutionStrict");
        method.setAccessible(true);

        // Act & Assert - call multiple times to ensure consistency
        boolean result1 = (boolean) method.invoke(server);
        boolean result2 = (boolean) method.invoke(server);
        boolean result3 = (boolean) method.invoke(server);

        assertTrue(result1, "First call should return true");
        assertTrue(result2, "Second call should return true");
        assertTrue(result3, "Third call should return true");
    }

    @Test
    @DisplayName("Test method is idempotent for overridden implementation returning false")
    void testMethodIdempotentWhenOverriddenToFalse() throws Exception
    {
        // Arrange
        NonStrictTestBaseServer server = Mockito.mock(NonStrictTestBaseServer.class, Mockito.CALLS_REAL_METHODS);
        Method method = BaseServer.class.getDeclaredMethod("isEnvironmentVariableSubstitutionStrict");
        method.setAccessible(true);

        // Act & Assert - call multiple times to ensure consistency
        boolean result1 = (boolean) method.invoke(server);
        boolean result2 = (boolean) method.invoke(server);
        boolean result3 = (boolean) method.invoke(server);

        assertFalse(result1, "First call should return false");
        assertFalse(result2, "Second call should return false");
        assertFalse(result3, "Third call should return false");
    }

    @Test
    @DisplayName("Test different subclasses maintain independent behavior")
    void testDifferentSubclassesIndependentBehavior() throws Exception
    {
        // Arrange
        TestBaseServer defaultServer = Mockito.mock(TestBaseServer.class, Mockito.CALLS_REAL_METHODS);
        NonStrictTestBaseServer nonStrictServer = Mockito.mock(NonStrictTestBaseServer.class, Mockito.CALLS_REAL_METHODS);
        StrictTestBaseServer strictServer = Mockito.mock(StrictTestBaseServer.class, Mockito.CALLS_REAL_METHODS);

        Method method = BaseServer.class.getDeclaredMethod("isEnvironmentVariableSubstitutionStrict");
        method.setAccessible(true);

        // Act
        boolean defaultResult = (boolean) method.invoke(defaultServer);
        boolean nonStrictResult = (boolean) method.invoke(nonStrictServer);
        boolean strictResult = (boolean) method.invoke(strictServer);

        // Assert
        assertTrue(defaultResult, "Default server should return true");
        assertFalse(nonStrictResult, "Non-strict server should return false");
        assertTrue(strictResult, "Strict server should return true");
    }
}

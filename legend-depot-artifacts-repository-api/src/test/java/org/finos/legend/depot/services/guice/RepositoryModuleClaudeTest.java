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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.PrivateModule;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.VoidArtifactRepositoryConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.VoidArtifactRepositoryProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RepositoryModuleClaudeTest
{
    // Test helper: Concrete implementation for testing with non-null configuration
    private static class TestArtifactRepositoryConfiguration extends ArtifactRepositoryProviderConfiguration
    {
        private final ArtifactRepository repository;

        public TestArtifactRepositoryConfiguration(String name, ArtifactRepository repository)
        {
            super(name);
            this.repository = repository;
        }

        @Override
        public ArtifactRepository initialiseArtifactRepositoryProvider()
        {
            return repository;
        }
    }

    // Test helper: Mock repository implementation
    private static class MockArtifactRepository extends VoidArtifactRepositoryProvider
    {
        public MockArtifactRepository(ArtifactRepositoryProviderConfiguration configuration)
        {
            super(configuration);
        }
    }

    // Tests for constructor

    @Test
    void testConstructorCreatesNonNullInstance()
    {
        // Act
        RepositoryModule module = new RepositoryModule();

        // Assert
        assertNotNull(module);
    }

    @Test
    void testConstructorCreatesDistinctInstances()
    {
        // Act
        RepositoryModule module1 = new RepositoryModule();
        RepositoryModule module2 = new RepositoryModule();

        // Assert
        assertNotNull(module1);
        assertNotNull(module2);
        assertNotSame(module1, module2);
    }

    @Test
    void testModuleExtendsPrivateModule()
    {
        // Act
        RepositoryModule module = new RepositoryModule();

        // Assert
        assertTrue(module instanceof PrivateModule);
    }

    @Test
    void testModuleClassIsPublic()
    {
        // Act
        Class<?> moduleClass = RepositoryModule.class;

        // Assert
        assertTrue(Modifier.isPublic(moduleClass.getModifiers()));
    }

    @Test
    void testDefaultConstructorExists()
    {
        // Act & Assert
        try
        {
            Constructor<?> constructor = RepositoryModule.class.getDeclaredConstructor();
            assertNotNull(constructor);
            assertTrue(Modifier.isPublic(constructor.getModifiers()));
        }
        catch (NoSuchMethodException e)
        {
            fail("Default constructor should exist");
        }
    }

    // Tests for configure() method

    @Test
    void testConfigureMethodExists()
    {
        // Act & Assert
        try
        {
            Method method = RepositoryModule.class.getDeclaredMethod("configure");
            assertNotNull(method);
            assertEquals(void.class, method.getReturnType());
            assertEquals(0, method.getParameterCount());
        }
        catch (NoSuchMethodException e)
        {
            fail("configure method should exist");
        }
    }

    @Test
    void testConfigureMethodIsProtected()
    {
        // Act & Assert
        try
        {
            Method method = RepositoryModule.class.getDeclaredMethod("configure");
            assertTrue(Modifier.isProtected(method.getModifiers()));
        }
        catch (NoSuchMethodException e)
        {
            fail("configure method should exist");
        }
    }

    // Tests for getArtifactRepository() method

    @Test
    void testGetArtifactRepositoryMethodExists()
    {
        // Act & Assert
        try
        {
            Method method = RepositoryModule.class.getDeclaredMethod("getArtifactRepository", ArtifactRepositoryProviderConfiguration.class);
            assertNotNull(method);
            assertEquals(ArtifactRepository.class, method.getReturnType());
            assertEquals(1, method.getParameterCount());
        }
        catch (NoSuchMethodException e)
        {
            fail("getArtifactRepository method should exist with correct signature");
        }
    }

    @Test
    void testGetArtifactRepositoryMethodIsPublic()
    {
        // Act & Assert
        try
        {
            Method method = RepositoryModule.class.getDeclaredMethod("getArtifactRepository", ArtifactRepositoryProviderConfiguration.class);
            assertTrue(Modifier.isPublic(method.getModifiers()));
        }
        catch (NoSuchMethodException e)
        {
            fail("getArtifactRepository method should exist");
        }
    }

    @Test
    void testGetArtifactRepositoryMethodHasProvidesAnnotation()
    {
        // Act & Assert
        try
        {
            Method method = RepositoryModule.class.getDeclaredMethod("getArtifactRepository", ArtifactRepositoryProviderConfiguration.class);
            boolean hasProvides = method.isAnnotationPresent(Provides.class);
            assertTrue(hasProvides);
        }
        catch (NoSuchMethodException e)
        {
            fail("getArtifactRepository method should exist");
        }
    }

    @Test
    void testGetArtifactRepositoryMethodHasSingletonAnnotation()
    {
        // Act & Assert
        try
        {
            Method method = RepositoryModule.class.getDeclaredMethod("getArtifactRepository", ArtifactRepositoryProviderConfiguration.class);
            boolean hasSingleton = method.isAnnotationPresent(Singleton.class);
            assertTrue(hasSingleton);
        }
        catch (NoSuchMethodException e)
        {
            fail("getArtifactRepository method should exist");
        }
    }

    @Test
    void testGetArtifactRepositoryWithNullConfigurationReturnsVoidProvider()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();

        // Act
        ArtifactRepository result = module.getArtifactRepository(null);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof VoidArtifactRepositoryProvider);
    }

    @Test
    void testGetArtifactRepositoryWithVoidConfigurationReturnsVoidProvider()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Act
        ArtifactRepository result = module.getArtifactRepository(config);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof VoidArtifactRepositoryProvider);
    }

    @Test
    void testGetArtifactRepositoryWithConfigurationReturningNullReturnsVoidProvider()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        // VoidArtifactRepositoryConfiguration returns null from initialiseArtifactRepositoryProvider
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Act
        ArtifactRepository result = module.getArtifactRepository(config);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof VoidArtifactRepositoryProvider);
    }

    @Test
    void testGetArtifactRepositoryWithValidConfigurationReturnsConfiguredProvider()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        VoidArtifactRepositoryConfiguration baseConfig = new VoidArtifactRepositoryConfiguration();
        MockArtifactRepository mockRepository = new MockArtifactRepository(baseConfig);
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration("test-config", mockRepository);

        // Act
        ArtifactRepository result = module.getArtifactRepository(config);

        // Assert
        assertNotNull(result);
        assertEquals(mockRepository, result);
    }

    @Test
    void testGetArtifactRepositoryMultipleCallsWithSameConfigurationReturnSameInstance()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        VoidArtifactRepositoryConfiguration baseConfig = new VoidArtifactRepositoryConfiguration();
        MockArtifactRepository mockRepository = new MockArtifactRepository(baseConfig);
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration("test-config", mockRepository);

        // Act
        ArtifactRepository result1 = module.getArtifactRepository(config);
        ArtifactRepository result2 = module.getArtifactRepository(config);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
    }

    @Test
    void testGetArtifactRepositoryWithDifferentConfigurations()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();

        VoidArtifactRepositoryConfiguration baseConfig1 = new VoidArtifactRepositoryConfiguration();
        MockArtifactRepository mockRepository1 = new MockArtifactRepository(baseConfig1);
        TestArtifactRepositoryConfiguration config1 = new TestArtifactRepositoryConfiguration("test-config-1", mockRepository1);

        VoidArtifactRepositoryConfiguration baseConfig2 = new VoidArtifactRepositoryConfiguration();
        MockArtifactRepository mockRepository2 = new MockArtifactRepository(baseConfig2);
        TestArtifactRepositoryConfiguration config2 = new TestArtifactRepositoryConfiguration("test-config-2", mockRepository2);

        // Act
        ArtifactRepository result1 = module.getArtifactRepository(config1);
        ArtifactRepository result2 = module.getArtifactRepository(config2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(mockRepository1, result1);
        assertEquals(mockRepository2, result2);
        assertNotSame(result1, result2);
    }

    @Test
    void testGetArtifactRepositoryHandlesNullFromInitialise()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration("test-null", null);

        // Act
        ArtifactRepository result = module.getArtifactRepository(config);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof VoidArtifactRepositoryProvider);
    }

    @Test
    void testGetArtifactRepositoryBranchCoverageWithNullConfiguration()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();

        // Act - Test the null configuration branch
        ArtifactRepository result = module.getArtifactRepository(null);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof VoidArtifactRepositoryProvider);
    }

    @Test
    void testGetArtifactRepositoryBranchCoverageWithValidRepository()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        VoidArtifactRepositoryConfiguration baseConfig = new VoidArtifactRepositoryConfiguration();
        MockArtifactRepository mockRepository = new MockArtifactRepository(baseConfig);
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration("valid-config", mockRepository);

        // Act - Test the branch where initialiseArtifactRepositoryProvider returns non-null
        ArtifactRepository result = module.getArtifactRepository(config);

        // Assert - Should return the configured provider, not a VoidArtifactRepositoryProvider
        assertNotNull(result);
        assertEquals(mockRepository, result);
    }

    @Test
    void testGetArtifactRepositoryBranchCoverageWithNullInitialisation()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration("null-init", null);

        // Act - Test the branch where configuration is not null but initialiseArtifactRepositoryProvider returns null
        ArtifactRepository result = module.getArtifactRepository(config);

        // Assert - Should fall back to VoidArtifactRepositoryProvider
        assertNotNull(result);
        assertTrue(result instanceof VoidArtifactRepositoryProvider);
    }

    // Integration tests

    @Test
    void testModuleIntegrationWithMultipleConfigurations()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();

        VoidArtifactRepositoryConfiguration voidConfig = new VoidArtifactRepositoryConfiguration();
        VoidArtifactRepositoryConfiguration baseConfig = new VoidArtifactRepositoryConfiguration();
        MockArtifactRepository mockRepository = new MockArtifactRepository(baseConfig);
        TestArtifactRepositoryConfiguration testConfig = new TestArtifactRepositoryConfiguration("integration-test", mockRepository);

        // Act
        ArtifactRepository voidResult = module.getArtifactRepository(voidConfig);
        ArtifactRepository testResult = module.getArtifactRepository(testConfig);
        ArtifactRepository nullResult = module.getArtifactRepository(null);

        // Assert
        assertNotNull(voidResult);
        assertNotNull(testResult);
        assertNotNull(nullResult);
        assertTrue(voidResult instanceof VoidArtifactRepositoryProvider);
        assertEquals(mockRepository, testResult);
        assertTrue(nullResult instanceof VoidArtifactRepositoryProvider);
    }

    @Test
    void testModuleConsistencyAcrossMultipleInvocations()
    {
        // Arrange
        RepositoryModule module1 = new RepositoryModule();
        RepositoryModule module2 = new RepositoryModule();

        VoidArtifactRepositoryConfiguration config1 = new VoidArtifactRepositoryConfiguration();
        VoidArtifactRepositoryConfiguration config2 = new VoidArtifactRepositoryConfiguration();

        // Act
        ArtifactRepository result1 = module1.getArtifactRepository(config1);
        ArtifactRepository result2 = module2.getArtifactRepository(config2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1 instanceof VoidArtifactRepositoryProvider);
        assertTrue(result2 instanceof VoidArtifactRepositoryProvider);
        // Different module instances with different configs should produce different repository instances
        assertNotSame(result1, result2);
    }

    @Test
    void testModuleHandlesEdgeCasesConsistently()
    {
        // Arrange
        RepositoryModule module = new RepositoryModule();

        // Act & Assert - Multiple null configuration calls should return non-null VoidArtifactRepositoryProvider
        ArtifactRepository nullResult1 = module.getArtifactRepository(null);
        ArtifactRepository nullResult2 = module.getArtifactRepository(null);

        assertNotNull(nullResult1);
        assertNotNull(nullResult2);
        assertTrue(nullResult1 instanceof VoidArtifactRepositoryProvider);
        assertTrue(nullResult2 instanceof VoidArtifactRepositoryProvider);
    }
}

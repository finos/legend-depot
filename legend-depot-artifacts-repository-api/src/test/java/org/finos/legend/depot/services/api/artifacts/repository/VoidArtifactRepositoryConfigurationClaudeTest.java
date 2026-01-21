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

package org.finos.legend.depot.services.api.artifacts.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VoidArtifactRepositoryConfigurationClaudeTest
{
    // Tests for constructor VoidArtifactRepositoryConfiguration()

    @Test
    void testConstructorCreatesNonNullInstance()
    {
        // Act
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Assert
        assertNotNull(config);
    }

    @Test
    void testConstructorSetsCorrectName()
    {
        // Act
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Assert
        assertEquals("void configuration", config.getName());
    }

    @Test
    void testConstructorCreatesInstanceOfCorrectType()
    {
        // Act
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Assert
        assertTrue(config instanceof ArtifactRepositoryProviderConfiguration);
    }

    @Test
    void testMultipleConstructorCallsCreateDistinctInstances()
    {
        // Act
        VoidArtifactRepositoryConfiguration config1 = new VoidArtifactRepositoryConfiguration();
        VoidArtifactRepositoryConfiguration config2 = new VoidArtifactRepositoryConfiguration();

        // Assert
        assertNotNull(config1);
        assertNotNull(config2);
        assertTrue(config1 != config2);
    }

    @Test
    void testConstructorCreatesInstanceWithConsistentName()
    {
        // Act
        VoidArtifactRepositoryConfiguration config1 = new VoidArtifactRepositoryConfiguration();
        VoidArtifactRepositoryConfiguration config2 = new VoidArtifactRepositoryConfiguration();

        // Assert
        assertEquals(config1.getName(), config2.getName());
        assertEquals("void configuration", config1.getName());
        assertEquals("void configuration", config2.getName());
    }

    // Tests for initialiseArtifactRepositoryProvider()

    @Test
    void testInitialiseArtifactRepositoryProviderReturnsNull()
    {
        // Arrange
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Act
        ArtifactRepository repository = config.initialiseArtifactRepositoryProvider();

        // Assert
        assertNull(repository);
    }

    @Test
    void testInitialiseArtifactRepositoryProviderConsistentlyReturnsNull()
    {
        // Arrange
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Act
        ArtifactRepository repository1 = config.initialiseArtifactRepositoryProvider();
        ArtifactRepository repository2 = config.initialiseArtifactRepositoryProvider();

        // Assert
        assertNull(repository1);
        assertNull(repository2);
    }

    @Test
    void testInitialiseArtifactRepositoryProviderOnMultipleInstances()
    {
        // Arrange
        VoidArtifactRepositoryConfiguration config1 = new VoidArtifactRepositoryConfiguration();
        VoidArtifactRepositoryConfiguration config2 = new VoidArtifactRepositoryConfiguration();

        // Act
        ArtifactRepository repository1 = config1.initialiseArtifactRepositoryProvider();
        ArtifactRepository repository2 = config2.initialiseArtifactRepositoryProvider();

        // Assert
        assertNull(repository1);
        assertNull(repository2);
    }

    // Integration tests - testing the full behavior

    @Test
    void testVoidConfigurationBehaviorEndToEnd()
    {
        // Act
        VoidArtifactRepositoryConfiguration config = new VoidArtifactRepositoryConfiguration();
        String name = config.getName();
        ArtifactRepository repository = config.initialiseArtifactRepositoryProvider();

        // Assert
        assertNotNull(config);
        assertEquals("void configuration", name);
        assertNull(repository);
    }

    @Test
    void testVoidConfigurationAsArtifactRepositoryProviderConfiguration()
    {
        // Arrange
        ArtifactRepositoryProviderConfiguration config = new VoidArtifactRepositoryConfiguration();

        // Act
        String name = config.getName();
        ArtifactRepository repository = config.initialiseArtifactRepositoryProvider();

        // Assert
        assertEquals("void configuration", name);
        assertNull(repository);
    }

    @Test
    void testVoidConfigurationFactoryMethodConsistency()
    {
        // Arrange
        VoidArtifactRepositoryConfiguration directConfig = new VoidArtifactRepositoryConfiguration();
        ArtifactRepositoryProviderConfiguration factoryConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Assert
        assertTrue(factoryConfig instanceof VoidArtifactRepositoryConfiguration);
        assertEquals(directConfig.getName(), factoryConfig.getName());
        assertEquals("void configuration", directConfig.getName());
        assertEquals("void configuration", factoryConfig.getName());
    }
}

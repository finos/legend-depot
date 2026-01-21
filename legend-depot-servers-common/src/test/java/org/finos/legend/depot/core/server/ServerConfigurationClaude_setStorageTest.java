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

import org.finos.legend.depot.store.StorageConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerConfigurationClaude_setStorageTest
{
    /**
     * Concrete test implementation of StorageConfiguration for testing purposes.
     */
    private static class TestStorageConfiguration extends StorageConfiguration
    {
        private final String name;

        public TestStorageConfiguration(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    @Test
    @DisplayName("Test setStorage sets the storage configuration list")
    void testSetStorageSetsStorageConfigurationList() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> storages = Arrays.asList(
                new TestStorageConfiguration("storage1"),
                new TestStorageConfiguration("storage2")
        );

        // Act
        config.setStorage(storages);

        // Assert
        List<StorageConfiguration> result = config.getStorageConfiguration();
        assertNotNull(result, "Storage configuration should not be null after setting");
        assertEquals(2, result.size(), "Storage configuration should have 2 items");
        assertSame(storages, result, "Should return the same list instance that was set");
    }

    @Test
    @DisplayName("Test setStorage with single storage configuration")
    void testSetStorageWithSingleStorageConfiguration() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> storages = Collections.singletonList(
                new TestStorageConfiguration("single-storage")
        );

        // Act
        config.setStorage(storages);

        // Assert
        List<StorageConfiguration> result = config.getStorageConfiguration();
        assertNotNull(result, "Storage configuration should not be null");
        assertEquals(1, result.size(), "Storage configuration should have 1 item");
        assertEquals("single-storage", ((TestStorageConfiguration) result.get(0)).getName());
    }

    @Test
    @DisplayName("Test setStorage with empty list")
    void testSetStorageWithEmptyList() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> storages = new ArrayList<>();

        // Act
        config.setStorage(storages);

        // Assert
        List<StorageConfiguration> result = config.getStorageConfiguration();
        assertNotNull(result, "Storage configuration should not be null");
        assertTrue(result.isEmpty(), "Storage configuration should be empty");
    }

    @Test
    @DisplayName("Test setStorage with null")
    void testSetStorageWithNull() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act
        config.setStorage(null);

        // Assert
        List<StorageConfiguration> result = config.getStorageConfiguration();
        assertNull(result, "Storage configuration should be null when set to null");
    }

    @Test
    @DisplayName("Test setStorage overwrites previous value")
    void testSetStorageOverwritesPreviousValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> firstStorages = Collections.singletonList(
                new TestStorageConfiguration("first")
        );
        List<StorageConfiguration> secondStorages = Collections.singletonList(
                new TestStorageConfiguration("second")
        );

        // Act
        config.setStorage(firstStorages);
        List<StorageConfiguration> firstResult = config.getStorageConfiguration();

        config.setStorage(secondStorages);
        List<StorageConfiguration> secondResult = config.getStorageConfiguration();

        // Assert
        assertSame(firstStorages, firstResult, "First call should return first storages");
        assertSame(secondStorages, secondResult, "Second call should return second storages");
        assertEquals("first", ((TestStorageConfiguration) firstResult.get(0)).getName());
        assertEquals("second", ((TestStorageConfiguration) secondResult.get(0)).getName());
    }

    @Test
    @DisplayName("Test setStorage with multiple different storage configurations")
    void testSetStorageWithMultipleDifferentStorageConfigurations() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> storages = Arrays.asList(
                new TestStorageConfiguration("mongo"),
                new TestStorageConfiguration("postgres"),
                new TestStorageConfiguration("memory")
        );

        // Act
        config.setStorage(storages);

        // Assert
        List<StorageConfiguration> result = config.getStorageConfiguration();
        assertNotNull(result, "Storage configuration should not be null");
        assertEquals(3, result.size(), "Storage configuration should have 3 items");
        assertEquals("mongo", ((TestStorageConfiguration) result.get(0)).getName());
        assertEquals("postgres", ((TestStorageConfiguration) result.get(1)).getName());
        assertEquals("memory", ((TestStorageConfiguration) result.get(2)).getName());
    }

    @Test
    @DisplayName("Test setStorage maintains list reference")
    void testSetStorageMaintainsListReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> storages = new ArrayList<>();
        storages.add(new TestStorageConfiguration("test"));

        // Act
        config.setStorage(storages);
        List<StorageConfiguration> retrieved = config.getStorageConfiguration();

        // Assert
        assertSame(storages, retrieved, "Should maintain the exact same list reference");

        // Verify that modifications to the original list affect the configuration
        storages.add(new TestStorageConfiguration("additional"));
        assertEquals(2, config.getStorageConfiguration().size(), "Changes to original list should be reflected");
    }

    @Test
    @DisplayName("Test setStorage can be called multiple times")
    void testSetStorageCanBeCalledMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        // Act & Assert - First call
        List<StorageConfiguration> first = Collections.singletonList(new TestStorageConfiguration("first"));
        config.setStorage(first);
        assertEquals(1, config.getStorageConfiguration().size());

        // Act & Assert - Second call
        List<StorageConfiguration> second = Arrays.asList(
                new TestStorageConfiguration("second1"),
                new TestStorageConfiguration("second2")
        );
        config.setStorage(second);
        assertEquals(2, config.getStorageConfiguration().size());

        // Act & Assert - Third call with null
        config.setStorage(null);
        assertNull(config.getStorageConfiguration());

        // Act & Assert - Fourth call with empty list
        config.setStorage(Collections.emptyList());
        assertTrue(config.getStorageConfiguration().isEmpty());
    }

    @Test
    @DisplayName("Test setStorage with same list reference multiple times")
    void testSetStorageWithSameListReferenceMultipleTimes() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> storages = new ArrayList<>();
        storages.add(new TestStorageConfiguration("test"));

        // Act
        config.setStorage(storages);
        config.setStorage(storages);
        config.setStorage(storages);

        // Assert
        assertSame(storages, config.getStorageConfiguration(), "Should still reference the same list");
        assertEquals(1, config.getStorageConfiguration().size());
    }

    @Test
    @DisplayName("Test setStorage with unmodifiable list")
    void testSetStorageWithUnmodifiableList() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        List<StorageConfiguration> unmodifiableList = Collections.unmodifiableList(
                Arrays.asList(
                        new TestStorageConfiguration("storage1"),
                        new TestStorageConfiguration("storage2")
                )
        );

        // Act
        config.setStorage(unmodifiableList);

        // Assert
        List<StorageConfiguration> result = config.getStorageConfiguration();
        assertNotNull(result, "Storage configuration should not be null");
        assertEquals(2, result.size(), "Storage configuration should have 2 items");
        assertSame(unmodifiableList, result, "Should maintain reference to unmodifiable list");
    }
}

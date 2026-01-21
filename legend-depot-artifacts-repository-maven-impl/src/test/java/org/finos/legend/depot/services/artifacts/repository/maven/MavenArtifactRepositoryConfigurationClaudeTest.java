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

package org.finos.legend.depot.services.artifacts.repository.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MavenArtifactRepositoryConfigurationClaudeTest
{
    @Test
    @DisplayName("Test constructor with valid settings location")
    void testConstructorWithValidSettingsLocation()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";

        // Act
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Assert
        assertNotNull(config);
        assertEquals(settingsLocation, config.getSettingsLocation());
    }

    @Test
    @DisplayName("Test constructor with null settings location")
    void testConstructorWithNullSettingsLocation()
    {
        // Arrange
        String settingsLocation = null;

        // Act
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Assert
        assertNotNull(config);
        assertEquals(null, config.getSettingsLocation());
    }

    @Test
    @DisplayName("Test constructor with empty settings location")
    void testConstructorWithEmptySettingsLocation()
    {
        // Arrange
        String settingsLocation = "";

        // Act
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Assert
        assertNotNull(config);
        assertEquals("", config.getSettingsLocation());
    }

    @Test
    @DisplayName("Test constructor sets name to MavenArtifactRepositoryConfiguration")
    void testConstructorSetsCorrectName()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";

        // Act
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Assert
        assertEquals("MavenArtifactRepositoryConfiguration", config.getName());
    }

    @Test
    @DisplayName("Test getSettingsLocation returns the value set in constructor")
    void testGetSettingsLocationReturnsCorrectValue()
    {
        // Arrange
        String settingsLocation = "/home/user/.m2/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act
        String result = config.getSettingsLocation();

        // Assert
        assertEquals(settingsLocation, result);
    }

    @Test
    @DisplayName("Test getSettingsLocation with different paths")
    void testGetSettingsLocationWithDifferentPaths()
    {
        // Arrange & Act & Assert
        String path1 = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config1 = new MavenArtifactRepositoryConfiguration(path1);
        assertEquals(path1, config1.getSettingsLocation());

        String path2 = "relative/path/settings.xml";
        MavenArtifactRepositoryConfiguration config2 = new MavenArtifactRepositoryConfiguration(path2);
        assertEquals(path2, config2.getSettingsLocation());

        String path3 = "C:\\Windows\\settings.xml";
        MavenArtifactRepositoryConfiguration config3 = new MavenArtifactRepositoryConfiguration(path3);
        assertEquals(path3, config3.getSettingsLocation());
    }

    @Test
    @DisplayName("Test initialiseArtifactRepositoryProvider throws exception with invalid settings path")
    void testInitialiseArtifactRepositoryProviderThrowsExceptionWithInvalidPath()
    {
        // Arrange
        String settingsLocation = "/path/to/nonexistent/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act & Assert
        // The method creates a MavenArtifactRepository which tries to load the settings file
        // Since the file doesn't exist, it should throw a RuntimeException
        assertThrows(RuntimeException.class, () -> config.initialiseArtifactRepositoryProvider());
    }

    @Test
    @DisplayName("Test initialiseArtifactRepositoryProvider throws exception consistently")
    void testInitialiseArtifactRepositoryProviderThrowsExceptionConsistently()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act & Assert
        // Each call should throw an exception since the settings file doesn't exist
        assertThrows(RuntimeException.class, () -> config.initialiseArtifactRepositoryProvider());
        assertThrows(RuntimeException.class, () -> config.initialiseArtifactRepositoryProvider());
    }

    @Test
    @DisplayName("Test initialiseArtifactRepositoryProvider with null settings location throws exception")
    void testInitialiseArtifactRepositoryProviderWithNullSettingsThrowsException()
    {
        // Arrange
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(null);

        // Act & Assert
        // The method creates a MavenArtifactRepository which tries to load null settings file
        // This should throw a RuntimeException
        assertThrows(RuntimeException.class, () -> config.initialiseArtifactRepositoryProvider());
    }

    @Test
    @DisplayName("Test toString includes name and settings location")
    void testToStringIncludesNameAndSettings()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act
        String result = config.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("MavenArtifactRepositoryConfiguration"));
        assertTrue(result.contains("name="));
        assertTrue(result.contains("MavenArtifactRepositoryConfiguration"));
        assertTrue(result.contains("settings="));
        assertTrue(result.contains(settingsLocation));
    }

    @Test
    @DisplayName("Test toString format starts with class name")
    void testToStringFormatStartsWithClassName()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act
        String result = config.toString();

        // Assert
        assertTrue(result.startsWith("MavenArtifactRepositoryConfiguration{"));
    }

    @Test
    @DisplayName("Test toString format ends with closing brace")
    void testToStringFormatEndsWithClosingBrace()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act
        String result = config.toString();

        // Assert
        assertTrue(result.endsWith("}"));
    }

    @Test
    @DisplayName("Test toString with null settings location")
    void testToStringWithNullSettingsLocation()
    {
        // Arrange
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(null);

        // Act
        String result = config.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("MavenArtifactRepositoryConfiguration"));
        assertTrue(result.contains("settings="));
        assertTrue(result.contains("null"));
    }

    @Test
    @DisplayName("Test toString with empty settings location")
    void testToStringWithEmptySettingsLocation()
    {
        // Arrange
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration("");

        // Act
        String result = config.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("MavenArtifactRepositoryConfiguration"));
        assertTrue(result.contains("settings="));
        assertTrue(result.contains("''"));
    }

    @Test
    @DisplayName("Test toString with different settings locations")
    void testToStringWithDifferentSettingsLocations()
    {
        // Arrange & Act & Assert
        String path1 = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config1 = new MavenArtifactRepositoryConfiguration(path1);
        String result1 = config1.toString();
        assertTrue(result1.contains(path1));

        String path2 = "relative/path/settings.xml";
        MavenArtifactRepositoryConfiguration config2 = new MavenArtifactRepositoryConfiguration(path2);
        String result2 = config2.toString();
        assertTrue(result2.contains(path2));
    }

    @Test
    @DisplayName("Test multiple instances are independent")
    void testMultipleInstancesAreIndependent()
    {
        // Arrange
        String settings1 = "/path/to/settings1.xml";
        String settings2 = "/path/to/settings2.xml";

        // Act
        MavenArtifactRepositoryConfiguration config1 = new MavenArtifactRepositoryConfiguration(settings1);
        MavenArtifactRepositoryConfiguration config2 = new MavenArtifactRepositoryConfiguration(settings2);

        // Assert
        assertEquals(settings1, config1.getSettingsLocation());
        assertEquals(settings2, config2.getSettingsLocation());
        assertTrue(config1 != config2);
    }

    @Test
    @DisplayName("Test configuration can be created with special characters in path")
    void testConfigurationWithSpecialCharactersInPath()
    {
        // Arrange
        String settingsLocation = "/path/with spaces/and-dashes/settings.xml";

        // Act
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Assert
        assertNotNull(config);
        assertEquals(settingsLocation, config.getSettingsLocation());
    }

    @Test
    @DisplayName("Test getName inherited from parent class")
    void testGetNameInheritedFromParentClass()
    {
        // Arrange
        String settingsLocation = "/path/to/settings.xml";
        MavenArtifactRepositoryConfiguration config = new MavenArtifactRepositoryConfiguration(settingsLocation);

        // Act
        String name = config.getName();

        // Assert
        assertNotNull(name);
        assertEquals("MavenArtifactRepositoryConfiguration", name);
    }
}

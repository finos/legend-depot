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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtifactRepositoryProviderConfigurationClaudeTest
{
    // Test helper: Concrete implementation for testing
    private static class TestArtifactRepositoryConfiguration extends ArtifactRepositoryProviderConfiguration
    {
        public TestArtifactRepositoryConfiguration(String name)
        {
            super(name);
        }

        @Override
        public ArtifactRepository initialiseArtifactRepositoryProvider()
        {
            return null;
        }
    }

    // Tests for getName()

    @Test
    void testGetNameReturnsCorrectValue()
    {
        // Arrange
        String expectedName = "test-repository";
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration(expectedName);

        // Act
        String actualName = config.getName();

        // Assert
        assertEquals(expectedName, actualName);
    }

    @Test
    void testGetNameWithNullValue()
    {
        // Arrange
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration(null);

        // Act
        String actualName = config.getName();

        // Assert
        assertNull(actualName);
    }

    @Test
    void testGetNameWithEmptyString()
    {
        // Arrange
        String emptyName = "";
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration(emptyName);

        // Act
        String actualName = config.getName();

        // Assert
        assertEquals(emptyName, actualName);
    }

    @Test
    void testGetNameWithWhitespace()
    {
        // Arrange
        String nameWithWhitespace = "  repository name  ";
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration(nameWithWhitespace);

        // Act
        String actualName = config.getName();

        // Assert
        assertEquals(nameWithWhitespace, actualName);
    }

    @Test
    void testGetNameWithSpecialCharacters()
    {
        // Arrange
        String nameWithSpecialChars = "repo-name_123!@#";
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration(nameWithSpecialChars);

        // Act
        String actualName = config.getName();

        // Assert
        assertEquals(nameWithSpecialChars, actualName);
    }

    // Tests for voidConfiguration()

    @Test
    void testVoidConfigurationReturnsNonNull()
    {
        // Act
        ArtifactRepositoryProviderConfiguration config = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Assert
        assertNotNull(config);
    }

    @Test
    void testVoidConfigurationReturnsVoidArtifactRepositoryConfiguration()
    {
        // Act
        ArtifactRepositoryProviderConfiguration config = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Assert
        assertTrue(config instanceof VoidArtifactRepositoryConfiguration);
    }

    @Test
    void testVoidConfigurationHasCorrectName()
    {
        // Act
        ArtifactRepositoryProviderConfiguration config = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Assert
        assertEquals("void configuration", config.getName());
    }

    @Test
    void testVoidConfigurationInitialiseReturnsNull()
    {
        // Arrange
        ArtifactRepositoryProviderConfiguration config = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Act
        ArtifactRepository repository = config.initialiseArtifactRepositoryProvider();

        // Assert
        assertNull(repository);
    }

    @Test
    void testVoidConfigurationCreatesNewInstance()
    {
        // Act
        ArtifactRepositoryProviderConfiguration config1 = ArtifactRepositoryProviderConfiguration.voidConfiguration();
        ArtifactRepositoryProviderConfiguration config2 = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Assert
        assertNotNull(config1);
        assertNotNull(config2);
        // Each call should create a new instance
        assertTrue(config1 != config2);
    }

    // Tests for configureObjectMapper(ObjectMapper)

    @Test
    void testConfigureObjectMapperReturnsNonNull()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);

        // Assert
        assertNotNull(configuredMapper);
    }

    @Test
    void testConfigureObjectMapperReturnsSameInstance()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);

        // Assert
        assertSame(objectMapper, configuredMapper);
    }

    @Test
    void testConfigureObjectMapperAddsMixIn() throws JsonProcessingException
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        TestArtifactRepositoryConfiguration testConfig = new TestArtifactRepositoryConfiguration("test");

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json = configuredMapper.writeValueAsString(testConfig);

        // Assert
        assertNotNull(json);
        // The configured mapper should wrap the configuration with class information
        assertTrue(json.contains("TestArtifactRepositoryConfiguration") ||
                   json.contains("ArtifactRepositoryProviderConfigurationClaudeTest$TestArtifactRepositoryConfiguration"));
    }

    @Test
    void testConfigureObjectMapperCanSerializeVoidConfiguration() throws JsonProcessingException
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        ArtifactRepositoryProviderConfiguration voidConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json = configuredMapper.writeValueAsString(voidConfig);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("VoidArtifactRepositoryConfiguration"));
    }

    @Test
    void testConfigureObjectMapperCanDeserializeWithClassInfo() throws JsonProcessingException
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        TestArtifactRepositoryConfiguration originalConfig = new TestArtifactRepositoryConfiguration("test-config");

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json = configuredMapper.writeValueAsString(originalConfig);
        Object deserializedConfig = configuredMapper.readValue(json, Object.class);

        // Assert
        assertNotNull(deserializedConfig);
    }

    @Test
    void testConfigureObjectMapperAppliesJsonTypeInfoWithClassId() throws Exception
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        TestArtifactRepositoryConfiguration testConfig = new TestArtifactRepositoryConfiguration("my-repo");

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json = configuredMapper.writeValueAsString(testConfig);

        // Assert
        // Verify that JsonTypeInfo.Id.CLASS is being used (full class name in JSON)
        assertTrue(json.contains("org.finos.legend.depot.services.api.artifacts.repository"));
    }

    @Test
    void testConfigureObjectMapperAppliesWrapperObjectFormat() throws JsonProcessingException
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        TestArtifactRepositoryConfiguration testConfig = new TestArtifactRepositoryConfiguration("wrapper-test");

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json = configuredMapper.writeValueAsString(testConfig);

        // Assert
        // Verify that JsonTypeInfo.As.WRAPPER_OBJECT is being used (class name as wrapper key)
        assertTrue(json.startsWith("{\"org.finos.legend.depot.services.api.artifacts.repository"));
    }

    @Test
    void testConfigureObjectMapperWorksWithMultipleConfigurations() throws JsonProcessingException
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        TestArtifactRepositoryConfiguration config1 = new TestArtifactRepositoryConfiguration("config-1");
        TestArtifactRepositoryConfiguration config2 = new TestArtifactRepositoryConfiguration("config-2");

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json1 = configuredMapper.writeValueAsString(config1);
        String json2 = configuredMapper.writeValueAsString(config2);

        // Assert
        assertNotNull(json1);
        assertNotNull(json2);
        // Both should have type information
        assertTrue(json1.contains("TestArtifactRepositoryConfiguration"));
        assertTrue(json2.contains("TestArtifactRepositoryConfiguration"));
    }

    @Test
    void testConfigureObjectMapperPreservesNameInSerializedJson() throws JsonProcessingException
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedName = "name-preservation-test";
        TestArtifactRepositoryConfiguration originalConfig = new TestArtifactRepositoryConfiguration(expectedName);

        // Act
        ObjectMapper configuredMapper = ArtifactRepositoryProviderConfiguration.configureObjectMapper(objectMapper);
        String json = configuredMapper.writeValueAsString(originalConfig);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains(expectedName));
    }
}

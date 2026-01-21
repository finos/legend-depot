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

package org.finos.legend.depot.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageConfigurationClaude_configureObjectMapperTest
{
    /**
     * Test concrete implementation of StorageConfiguration for testing serialization
     */
    static class TestStorageConfig extends StorageConfiguration
    {
        @JsonProperty
        public String name;

        @JsonProperty
        public int port;

        @JsonCreator
        public TestStorageConfig(@JsonProperty("name") String name, @JsonProperty("port") int port)
        {
            super();
            this.name = name;
            this.port = port;
        }

        public TestStorageConfig()
        {
            super();
        }
    }

    /**
     * Test that configureObjectMapper returns the same ObjectMapper instance that was passed in
     */
    @Test
    void testConfigureObjectMapperReturnsSameInstance()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper result = StorageConfiguration.configureObjectMapper(objectMapper);

        assertSame(objectMapper, result);
    }

    /**
     * Test that configureObjectMapper is not null when passed a valid ObjectMapper
     */
    @Test
    void testConfigureObjectMapperNotNull()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper result = StorageConfiguration.configureObjectMapper(objectMapper);

        assertNotNull(result);
    }

    /**
     * Test that configureObjectMapper adds type information when serializing StorageConfiguration
     */
    @Test
    void testConfigureObjectMapperAddsTypeInformationDuringSerialization() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        StorageConfiguration.configureObjectMapper(objectMapper);

        TestStorageConfig config = new TestStorageConfig("testDB", 8080);
        String json = objectMapper.writeValueAsString(config);

        // The mixin adds @JsonTypeInfo with CLASS as WRAPPER_OBJECT
        // This should wrap the object with its class name
        assertTrue(json.contains(TestStorageConfig.class.getName()),
                "JSON should contain class name: " + json);
    }

    /**
     * Test that configureObjectMapper allows deserialization with type information
     */
    @Test
    void testConfigureObjectMapperAllowsDeserializationWithTypeInfo() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        StorageConfiguration.configureObjectMapper(objectMapper);
        objectMapper.registerSubtypes(new NamedType(TestStorageConfig.class));

        TestStorageConfig original = new TestStorageConfig("testDB", 8080);
        String json = objectMapper.writeValueAsString(original);

        StorageConfiguration deserialized = objectMapper.readValue(json, StorageConfiguration.class);

        assertNotNull(deserialized);
        assertInstanceOf(TestStorageConfig.class, deserialized);
        TestStorageConfig deserializedConfig = (TestStorageConfig) deserialized;
        assertEquals("testDB", deserializedConfig.name);
        assertEquals(8080, deserializedConfig.port);
    }

    /**
     * Test that the mixin is applied to the StorageConfiguration class
     */
    @Test
    void testConfigureObjectMapperAppliesMixin()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        StorageConfiguration.configureObjectMapper(objectMapper);

        // Verify that a mixin has been registered for StorageConfiguration
        assertTrue(objectMapper.getSerializationConfig().findMixInClassFor(StorageConfiguration.class) != null,
                "Mixin should be registered for StorageConfiguration");
    }

    /**
     * Test that configureObjectMapper can be called multiple times without error
     */
    @Test
    void testConfigureObjectMapperCanBeCalledMultipleTimes()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        assertDoesNotThrow(() -> {
            StorageConfiguration.configureObjectMapper(objectMapper);
            StorageConfiguration.configureObjectMapper(objectMapper);
            StorageConfiguration.configureObjectMapper(objectMapper);
        });
    }

    /**
     * Test that configureObjectMapper works with an ObjectMapper that has existing configuration
     */
    @Test
    void testConfigureObjectMapperWithExistingConfiguration()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        ObjectMapper result = StorageConfiguration.configureObjectMapper(objectMapper);

        assertNotNull(result);
        assertSame(objectMapper, result);
    }

    /**
     * Test serialization format with configured ObjectMapper
     */
    @Test
    void testSerializedFormatWithConfiguredObjectMapper() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        StorageConfiguration.configureObjectMapper(objectMapper);

        TestStorageConfig config = new TestStorageConfig("myDatabase", 27017);
        String json = objectMapper.writeValueAsString(config);

        // The JSON should be wrapped with the class name as the key
        assertTrue(json.startsWith("{\"" + TestStorageConfig.class.getName() + "\":"),
                "JSON should start with class name wrapper: " + json);
        assertTrue(json.contains("\"name\":\"myDatabase\""), "JSON should contain name field");
        assertTrue(json.contains("\"port\":27017"), "JSON should contain port field");
    }

    /**
     * Test that multiple StorageConfiguration subclasses can be distinguished
     */
    @Test
    void testMultipleSubclassesCanBeDistinguished() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        StorageConfiguration.configureObjectMapper(objectMapper);
        objectMapper.registerSubtypes(new NamedType(TestStorageConfig.class));
        objectMapper.registerSubtypes(new NamedType(AnotherTestStorageConfig.class));

        TestStorageConfig config1 = new TestStorageConfig("db1", 8080);
        AnotherTestStorageConfig config2 = new AnotherTestStorageConfig("localhost");

        String json1 = objectMapper.writeValueAsString(config1);
        String json2 = objectMapper.writeValueAsString(config2);

        assertTrue(json1.contains(TestStorageConfig.class.getName()));
        assertTrue(json2.contains(AnotherTestStorageConfig.class.getName()));

        StorageConfiguration deserialized1 = objectMapper.readValue(json1, StorageConfiguration.class);
        StorageConfiguration deserialized2 = objectMapper.readValue(json2, StorageConfiguration.class);

        assertInstanceOf(TestStorageConfig.class, deserialized1);
        assertInstanceOf(AnotherTestStorageConfig.class, deserialized2);
    }

    /**
     * Another test implementation of StorageConfiguration
     */
    static class AnotherTestStorageConfig extends StorageConfiguration
    {
        @JsonProperty
        public String host;

        @JsonCreator
        public AnotherTestStorageConfig(@JsonProperty("host") String host)
        {
            super();
            this.host = host;
        }

        public AnotherTestStorageConfig()
        {
            super();
        }
    }
}

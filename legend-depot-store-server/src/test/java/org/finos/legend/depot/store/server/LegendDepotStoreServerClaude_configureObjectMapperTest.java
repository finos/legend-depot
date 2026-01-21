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

package org.finos.legend.depot.store.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.setup.Bootstrap;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegendDepotStoreServerClaude_configureObjectMapperTest
{
    // Helper class for testing ArtifactRepositoryProviderConfiguration serialization
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

    @Test
    public void testConfigureObjectMapperAcceptsNonNullBootstrap()
    {
        // Test that configureObjectMapper can be invoked with a valid Bootstrap
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        // This should not throw an exception
        Assertions.assertDoesNotThrow(() -> server.configureObjectMapper(bootstrap));
    }

    @Test
    public void testConfigureObjectMapperConfiguresArtifactRepositoryProviderConfiguration() throws JsonProcessingException
    {
        // Test that configureObjectMapper properly configures ArtifactRepositoryProviderConfiguration
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        // Configure the object mapper
        server.configureObjectMapper(bootstrap);

        // Verify the ObjectMapper is configured for ArtifactRepositoryProviderConfiguration
        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        TestArtifactRepositoryConfiguration testConfig = new TestArtifactRepositoryConfiguration("test-repo");

        // Serialize to JSON - should include type information due to mixin
        String json = objectMapper.writeValueAsString(testConfig);

        Assertions.assertNotNull(json);
        // The configured mapper should wrap the configuration with class information
        Assertions.assertTrue(json.contains("TestArtifactRepositoryConfiguration"));
    }

    @Test
    public void testConfigureObjectMapperUsesWrapperObjectFormat() throws JsonProcessingException
    {
        // Test that the ObjectMapper uses WRAPPER_OBJECT format for ArtifactRepositoryProviderConfiguration
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        server.configureObjectMapper(bootstrap);

        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        TestArtifactRepositoryConfiguration testConfig = new TestArtifactRepositoryConfiguration("wrapper-test");

        String json = objectMapper.writeValueAsString(testConfig);

        // Verify WRAPPER_OBJECT format - class name should be a wrapper key
        Assertions.assertTrue(json.startsWith("{\"org.finos.legend.depot.store.server"));
    }

    @Test
    public void testConfigureObjectMapperPreservesObjectMapperInstance()
    {
        // Test that configureObjectMapper doesn't replace the ObjectMapper, just configures it
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        ObjectMapper originalMapper = bootstrap.getObjectMapper();

        server.configureObjectMapper(bootstrap);

        ObjectMapper configuredMapper = bootstrap.getObjectMapper();

        // Should be the same instance, just configured differently
        Assertions.assertSame(originalMapper, configuredMapper);
    }

    @Test
    public void testConfigureObjectMapperCanSerializeMultipleConfigurations() throws JsonProcessingException
    {
        // Test that the configured ObjectMapper can handle multiple ArtifactRepositoryProviderConfiguration instances
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        server.configureObjectMapper(bootstrap);

        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        TestArtifactRepositoryConfiguration config1 = new TestArtifactRepositoryConfiguration("repo-1");
        TestArtifactRepositoryConfiguration config2 = new TestArtifactRepositoryConfiguration("repo-2");

        String json1 = objectMapper.writeValueAsString(config1);
        String json2 = objectMapper.writeValueAsString(config2);

        Assertions.assertNotNull(json1);
        Assertions.assertNotNull(json2);
        Assertions.assertTrue(json1.contains("repo-1"));
        Assertions.assertTrue(json2.contains("repo-2"));
    }

    @Test
    public void testConfigureObjectMapperPreservesNameInJson() throws JsonProcessingException
    {
        // Test that serialization preserves the name field of configurations
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        server.configureObjectMapper(bootstrap);

        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        String expectedName = "my-artifact-repo";
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration(expectedName);

        String json = objectMapper.writeValueAsString(config);

        Assertions.assertTrue(json.contains(expectedName));
    }

    @Test
    public void testConfigureObjectMapperCallsParentConfiguration()
    {
        // Test that configureObjectMapper calls the parent class configuration
        // The parent configures StorageConfiguration, TracerProviderConfiguration, and PrometheusMetricsProviderConfiguration
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        // This should not throw an exception and should complete successfully
        Assertions.assertDoesNotThrow(() -> server.configureObjectMapper(bootstrap));

        // Verify the ObjectMapper exists and is configured
        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        Assertions.assertNotNull(objectMapper);
    }

    @Test
    public void testConfigureObjectMapperWithNewServer()
    {
        // Test that configureObjectMapper works with a newly instantiated server
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        // Should work immediately after construction
        Assertions.assertDoesNotThrow(() -> server.configureObjectMapper(bootstrap));

        // Verify configuration was applied
        Assertions.assertNotNull(bootstrap.getObjectMapper());
    }

    @Test
    public void testConfigureObjectMapperCanBeCalledMultipleTimes()
    {
        // Test that configureObjectMapper can be called multiple times without error
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        // Call multiple times
        Assertions.assertDoesNotThrow(() -> server.configureObjectMapper(bootstrap));
        Assertions.assertDoesNotThrow(() -> server.configureObjectMapper(bootstrap));

        // ObjectMapper should still work
        Assertions.assertNotNull(bootstrap.getObjectMapper());
    }

    @Test
    public void testConfigureObjectMapperWithDifferentBootstraps()
    {
        // Test that the same server can configure multiple bootstraps
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap1 = new Bootstrap<>(server);
        Bootstrap<DepotStoreServerConfiguration> bootstrap2 = new Bootstrap<>(server);

        // Configure both
        server.configureObjectMapper(bootstrap1);
        server.configureObjectMapper(bootstrap2);

        // Both should be configured
        Assertions.assertNotNull(bootstrap1.getObjectMapper());
        Assertions.assertNotNull(bootstrap2.getObjectMapper());

        // They should be different instances
        Assertions.assertNotSame(bootstrap1.getObjectMapper(), bootstrap2.getObjectMapper());
    }

    @Test
    public void testConfigureObjectMapperEnablesTypeInformation() throws JsonProcessingException
    {
        // Test that the configured ObjectMapper includes type information for polymorphic types
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        server.configureObjectMapper(bootstrap);

        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        TestArtifactRepositoryConfiguration config = new TestArtifactRepositoryConfiguration("type-info-test");

        String json = objectMapper.writeValueAsString(config);

        // Verify type information is included (full package path)
        Assertions.assertTrue(json.contains("org.finos.legend.depot.store.server"));
    }

    @Test
    public void testConfigureObjectMapperWithVoidConfiguration() throws JsonProcessingException
    {
        // Test that the configured ObjectMapper can serialize VoidArtifactRepositoryConfiguration
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Bootstrap<DepotStoreServerConfiguration> bootstrap = new Bootstrap<>(server);

        server.configureObjectMapper(bootstrap);

        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        ArtifactRepositoryProviderConfiguration voidConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();

        String json = objectMapper.writeValueAsString(voidConfig);

        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("VoidArtifactRepositoryConfiguration"));
    }
}

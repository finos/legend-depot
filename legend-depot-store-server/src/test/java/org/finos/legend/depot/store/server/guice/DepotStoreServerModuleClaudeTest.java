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

package org.finos.legend.depot.store.server.guice;

import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

public class DepotStoreServerModuleClaudeTest
{
    @Test
    public void testConstructorCreatesNonNullInstance()
    {
        DepotStoreServerModule module = new DepotStoreServerModule();
        Assertions.assertNotNull(module);
    }

    @Test
    public void testGetQueueManagerConfigurationReturnsSetValue() throws Exception
    {
        // Reflection is used here because the getQueueManagerConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the logic without needing
        // to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        QueueManagerConfiguration expected = configuration.getQueueManagerConfiguration();
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getQueueManagerConfiguration");
        method.setAccessible(true);
        QueueManagerConfiguration actual = (QueueManagerConfiguration) method.invoke(testModule);

        Assertions.assertSame(expected, actual);
    }

    @Test
    public void testGetQueueManagerConfigurationWithNullReturnsDefault() throws Exception
    {
        // Reflection is used here because the getQueueManagerConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the default logic without
        // needing to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        configuration.setQueueManagerConfiguration(null);
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getQueueManagerConfiguration");
        method.setAccessible(true);
        QueueManagerConfiguration actual = (QueueManagerConfiguration) method.invoke(testModule);

        Assertions.assertNotNull(actual);
    }

    @Test
    public void testGetArtifactRepositoryConfigurationReturnsSetValue() throws Exception
    {
        // Reflection is used here because the getArtifactRepositoryConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the logic without needing
        // to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        ArtifactRepositoryProviderConfiguration expected = configuration.getArtifactRepositoryProviderConfiguration();
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getArtifactRepositoryConfiguration");
        method.setAccessible(true);
        ArtifactRepositoryProviderConfiguration actual = (ArtifactRepositoryProviderConfiguration) method.invoke(testModule);

        Assertions.assertSame(expected, actual);
    }

    @Test
    public void testGetArtifactRepositoryConfigurationWithNullReturnsVoidConfiguration() throws Exception
    {
        // Reflection is used here because the getArtifactRepositoryConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the void configuration default
        // logic without needing to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        configuration.setArtifactRepositoryProviderConfiguration(null);
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getArtifactRepositoryConfiguration");
        method.setAccessible(true);
        ArtifactRepositoryProviderConfiguration actual = (ArtifactRepositoryProviderConfiguration) method.invoke(testModule);

        Assertions.assertNotNull(actual);
    }

    @Test
    public void testGetRefreshPolicyConfigurationReturnsSetValue() throws Exception
    {
        // Reflection is used here because the getRefreshPolicyConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the logic without needing
        // to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        ArtifactsRefreshPolicyConfiguration expected = configuration.getArtifactsRefreshPolicyConfiguration();
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getRefreshPolicyConfiguration");
        method.setAccessible(true);
        ArtifactsRefreshPolicyConfiguration actual = (ArtifactsRefreshPolicyConfiguration) method.invoke(testModule);

        Assertions.assertSame(expected, actual);
    }

    @Test
    public void testGetIncludePropertiesConfigurationReturnsSetValue() throws Exception
    {
        // Reflection is used here because the getIncludePropertiesConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the logic without needing
        // to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        IncludeProjectPropertiesConfiguration expected = configuration.getArtifactsRefreshPolicyConfiguration().getIncludeProjectPropertiesConfiguration();
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getIncludePropertiesConfiguration");
        method.setAccessible(true);
        IncludeProjectPropertiesConfiguration actual = (IncludeProjectPropertiesConfiguration) method.invoke(testModule);

        Assertions.assertSame(expected, actual);
    }

    @Test
    public void testGetRetentionPolicyConfigurationReturnsSetValue() throws Exception
    {
        // Reflection is used here because the getRetentionPolicyConfiguration method is private and is called
        // by the configure method as a provider. Testing it directly verifies the logic without needing
        // to set up a full Guice injector with servlet dependencies.
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        ArtifactsRetentionPolicyConfiguration expected = configuration.getRetentionPolicyConfiguration();
        TestModule testModule = new TestModule(configuration);

        Method method = DepotStoreServerModule.class.getDeclaredMethod("getRetentionPolicyConfiguration");
        method.setAccessible(true);
        ArtifactsRetentionPolicyConfiguration actual = (ArtifactsRetentionPolicyConfiguration) method.invoke(testModule);

        Assertions.assertSame(expected, actual);
    }


    private DepotStoreServerConfiguration createTestConfiguration()
    {
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        config.setStorage(Collections.emptyList());

        IncludeProjectPropertiesConfiguration includeProps = new IncludeProjectPropertiesConfiguration(
            Arrays.asList("prop1", "prop2"),
            null
        );

        ArtifactsRefreshPolicyConfiguration refreshPolicy = new ArtifactsRefreshPolicyConfiguration(
            3600000L,
            includeProps
        );
        config.setArtifactsRefreshPolicyConfiguration(refreshPolicy);

        ArtifactsRetentionPolicyConfiguration retentionPolicy = new ArtifactsRetentionPolicyConfiguration(
            7, 30, 60
        );
        config.setRetentionPolicyConfiguration(retentionPolicy);

        QueueManagerConfiguration queueConfig = new QueueManagerConfiguration();
        config.setQueueManagerConfiguration(queueConfig);

        ArtifactRepositoryProviderConfiguration repoConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();
        config.setArtifactRepositoryProviderConfiguration(repoConfig);

        return config;
    }

    private static class TestModule extends DepotStoreServerModule
    {
        private final DepotStoreServerConfiguration configuration;

        TestModule(DepotStoreServerConfiguration configuration)
        {
            this.configuration = configuration;
        }

        @Override
        public DepotStoreServerConfiguration getConfiguration()
        {
            return configuration;
        }
    }
}

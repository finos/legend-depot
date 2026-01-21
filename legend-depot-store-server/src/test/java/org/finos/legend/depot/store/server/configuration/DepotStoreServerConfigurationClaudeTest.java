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

package org.finos.legend.depot.store.server.configuration;

import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DepotStoreServerConfigurationClaudeTest
{
    @Test
    public void testConstructorCreatesNonNullInstance()
    {
        // Test that the constructor creates a valid instance
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    public void testConstructorInitializesFieldsToNull()
    {
        // Test that the constructor initializes all fields to null
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertNull(config.getQueueManagerConfiguration());
        Assertions.assertNull(config.getArtifactRepositoryProviderConfiguration());
        Assertions.assertNull(config.getArtifactsRefreshPolicyConfiguration());
        Assertions.assertNull(config.getRetentionPolicyConfiguration());
    }

    @Test
    public void testGetQueueManagerConfigurationReturnsNull()
    {
        // Test that getQueueManagerConfiguration returns null when not set
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertNull(config.getQueueManagerConfiguration());
    }

    @Test
    public void testSetQueueManagerConfigurationSetsValue()
    {
        // Test that setQueueManagerConfiguration sets the value correctly
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        QueueManagerConfiguration queueConfig = new QueueManagerConfiguration();
        config.setQueueManagerConfiguration(queueConfig);
        Assertions.assertSame(queueConfig, config.getQueueManagerConfiguration());
    }

    @Test
    public void testSetQueueManagerConfigurationWithNull()
    {
        // Test that setQueueManagerConfiguration accepts null
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        QueueManagerConfiguration queueConfig = new QueueManagerConfiguration();
        config.setQueueManagerConfiguration(queueConfig);
        config.setQueueManagerConfiguration(null);
        Assertions.assertNull(config.getQueueManagerConfiguration());
    }

    @Test
    public void testGetArtifactRepositoryProviderConfigurationReturnsNull()
    {
        // Test that getArtifactRepositoryProviderConfiguration returns null when not set
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertNull(config.getArtifactRepositoryProviderConfiguration());
    }

    @Test
    public void testSetArtifactRepositoryProviderConfigurationSetsValue()
    {
        // Test that setArtifactRepositoryProviderConfiguration sets the value correctly
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        ArtifactRepositoryProviderConfiguration repoConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();
        config.setArtifactRepositoryProviderConfiguration(repoConfig);
        Assertions.assertSame(repoConfig, config.getArtifactRepositoryProviderConfiguration());
    }

    @Test
    public void testSetArtifactRepositoryProviderConfigurationWithNull()
    {
        // Test that setArtifactRepositoryProviderConfiguration accepts null
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        ArtifactRepositoryProviderConfiguration repoConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();
        config.setArtifactRepositoryProviderConfiguration(repoConfig);
        config.setArtifactRepositoryProviderConfiguration(null);
        Assertions.assertNull(config.getArtifactRepositoryProviderConfiguration());
    }

    @Test
    public void testGetArtifactsRefreshPolicyConfigurationReturnsNull()
    {
        // Test that getArtifactsRefreshPolicyConfiguration returns null when not set
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertNull(config.getArtifactsRefreshPolicyConfiguration());
    }

    @Test
    public void testSetArtifactsRefreshPolicyConfigurationSetsValue()
    {
        // Test that setArtifactsRefreshPolicyConfiguration sets the value correctly
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        ArtifactsRefreshPolicyConfiguration refreshConfig = new ArtifactsRefreshPolicyConfiguration(1000L, null);
        config.setArtifactsRefreshPolicyConfiguration(refreshConfig);
        Assertions.assertSame(refreshConfig, config.getArtifactsRefreshPolicyConfiguration());
    }

    @Test
    public void testSetArtifactsRefreshPolicyConfigurationWithNull()
    {
        // Test that setArtifactsRefreshPolicyConfiguration accepts null
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        ArtifactsRefreshPolicyConfiguration refreshConfig = new ArtifactsRefreshPolicyConfiguration(1000L, null);
        config.setArtifactsRefreshPolicyConfiguration(refreshConfig);
        config.setArtifactsRefreshPolicyConfiguration(null);
        Assertions.assertNull(config.getArtifactsRefreshPolicyConfiguration());
    }

    @Test
    public void testGetRetentionPolicyConfigurationReturnsNull()
    {
        // Test that getRetentionPolicyConfiguration returns null when not set
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertNull(config.getRetentionPolicyConfiguration());
    }

    @Test
    public void testSetRetentionPolicyConfigurationSetsValue()
    {
        // Test that setRetentionPolicyConfiguration sets the value correctly
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        ArtifactsRetentionPolicyConfiguration retentionConfig = new ArtifactsRetentionPolicyConfiguration(10, 30, 60);
        config.setRetentionPolicyConfiguration(retentionConfig);
        Assertions.assertSame(retentionConfig, config.getRetentionPolicyConfiguration());
    }

    @Test
    public void testSetRetentionPolicyConfigurationWithNull()
    {
        // Test that setRetentionPolicyConfiguration accepts null
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        ArtifactsRetentionPolicyConfiguration retentionConfig = new ArtifactsRetentionPolicyConfiguration(10, 30, 60);
        config.setRetentionPolicyConfiguration(retentionConfig);
        config.setRetentionPolicyConfiguration(null);
        Assertions.assertNull(config.getRetentionPolicyConfiguration());
    }

    @Test
    public void testMultipleSettersWorkIndependently()
    {
        // Test that setting multiple configurations works independently
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();

        QueueManagerConfiguration queueConfig = new QueueManagerConfiguration();
        ArtifactRepositoryProviderConfiguration repoConfig = ArtifactRepositoryProviderConfiguration.voidConfiguration();
        ArtifactsRefreshPolicyConfiguration refreshConfig = new ArtifactsRefreshPolicyConfiguration(2000L, null);
        ArtifactsRetentionPolicyConfiguration retentionConfig = new ArtifactsRetentionPolicyConfiguration(5, 20, 40);

        config.setQueueManagerConfiguration(queueConfig);
        config.setArtifactRepositoryProviderConfiguration(repoConfig);
        config.setArtifactsRefreshPolicyConfiguration(refreshConfig);
        config.setRetentionPolicyConfiguration(retentionConfig);

        Assertions.assertSame(queueConfig, config.getQueueManagerConfiguration());
        Assertions.assertSame(repoConfig, config.getArtifactRepositoryProviderConfiguration());
        Assertions.assertSame(refreshConfig, config.getArtifactsRefreshPolicyConfiguration());
        Assertions.assertSame(retentionConfig, config.getRetentionPolicyConfiguration());
    }

    @Test
    public void testSettersOverwritePreviousValues()
    {
        // Test that setters properly overwrite previous values
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();

        QueueManagerConfiguration queueConfig1 = new QueueManagerConfiguration();
        QueueManagerConfiguration queueConfig2 = new QueueManagerConfiguration();

        config.setQueueManagerConfiguration(queueConfig1);
        Assertions.assertSame(queueConfig1, config.getQueueManagerConfiguration());

        config.setQueueManagerConfiguration(queueConfig2);
        Assertions.assertSame(queueConfig2, config.getQueueManagerConfiguration());
        Assertions.assertNotSame(queueConfig1, config.getQueueManagerConfiguration());
    }

    @Test
    public void testConfigurationIsInstanceOfServerConfiguration()
    {
        // Test that DepotStoreServerConfiguration extends ServerConfiguration
        DepotStoreServerConfiguration config = new DepotStoreServerConfiguration();
        Assertions.assertTrue(config instanceof org.finos.legend.depot.core.server.ServerConfiguration);
    }
}

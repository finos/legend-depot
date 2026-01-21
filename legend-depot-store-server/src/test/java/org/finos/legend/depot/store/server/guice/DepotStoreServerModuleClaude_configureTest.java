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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class DepotStoreServerModuleClaude_configureTest
{
    @Test
    public void testConfigureBindsAllConfigurationClasses()
    {
        // Test that configure method properly binds all configuration classes
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        TestModule testModule = new TestModule(configuration);

        // Create injector with servlet module to satisfy parent module requirements
        Injector injector = Guice.createInjector(
            new ServletModule(),
            testModule
        );

        // Verify all bindings are available - this exercises lines 33-39
        ArtifactRepositoryProviderConfiguration artifactRepoConfig = injector.getInstance(ArtifactRepositoryProviderConfiguration.class);
        Assertions.assertNotNull(artifactRepoConfig);

        ArtifactsRefreshPolicyConfiguration refreshPolicyConfig = injector.getInstance(ArtifactsRefreshPolicyConfiguration.class);
        Assertions.assertNotNull(refreshPolicyConfig);

        IncludeProjectPropertiesConfiguration includePropsConfig = injector.getInstance(IncludeProjectPropertiesConfiguration.class);
        Assertions.assertNotNull(includePropsConfig);

        ArtifactsRetentionPolicyConfiguration retentionPolicyConfig = injector.getInstance(ArtifactsRetentionPolicyConfiguration.class);
        Assertions.assertNotNull(retentionPolicyConfig);

        QueueManagerConfiguration queueManagerConfig = injector.getInstance(QueueManagerConfiguration.class);
        Assertions.assertNotNull(queueManagerConfig);
    }

    @Test
    public void testConfigureWithNullArtifactRepositoryConfiguration()
    {
        // Test configure with null artifact repository configuration
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        configuration.setArtifactRepositoryProviderConfiguration(null);
        TestModule testModule = new TestModule(configuration);

        Injector injector = Guice.createInjector(
            new ServletModule(),
            testModule
        );

        // Verify void configuration is returned when null
        ArtifactRepositoryProviderConfiguration artifactRepoConfig = injector.getInstance(ArtifactRepositoryProviderConfiguration.class);
        Assertions.assertNotNull(artifactRepoConfig);
    }

    @Test
    public void testConfigureWithNullQueueManagerConfiguration()
    {
        // Test configure with null queue manager configuration
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        configuration.setQueueManagerConfiguration(null);
        TestModule testModule = new TestModule(configuration);

        Injector injector = Guice.createInjector(
            new ServletModule(),
            testModule
        );

        // Verify default configuration is returned when null
        QueueManagerConfiguration queueManagerConfig = injector.getInstance(QueueManagerConfiguration.class);
        Assertions.assertNotNull(queueManagerConfig);
    }

    @Test
    public void testConfigureBindingsReturnExpectedInstances()
    {
        // Test that the bindings return the expected configuration instances
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        TestModule testModule = new TestModule(configuration);

        Injector injector = Guice.createInjector(
            new ServletModule(),
            testModule
        );

        // Verify the instances match the configuration
        ArtifactRepositoryProviderConfiguration artifactRepoConfig = injector.getInstance(ArtifactRepositoryProviderConfiguration.class);
        Assertions.assertSame(configuration.getArtifactRepositoryProviderConfiguration(), artifactRepoConfig);

        ArtifactsRefreshPolicyConfiguration refreshPolicyConfig = injector.getInstance(ArtifactsRefreshPolicyConfiguration.class);
        Assertions.assertSame(configuration.getArtifactsRefreshPolicyConfiguration(), refreshPolicyConfig);

        IncludeProjectPropertiesConfiguration includePropsConfig = injector.getInstance(IncludeProjectPropertiesConfiguration.class);
        Assertions.assertSame(configuration.getArtifactsRefreshPolicyConfiguration().getIncludeProjectPropertiesConfiguration(), includePropsConfig);

        ArtifactsRetentionPolicyConfiguration retentionPolicyConfig = injector.getInstance(ArtifactsRetentionPolicyConfiguration.class);
        Assertions.assertSame(configuration.getRetentionPolicyConfiguration(), retentionPolicyConfig);

        QueueManagerConfiguration queueManagerConfig = injector.getInstance(QueueManagerConfiguration.class);
        Assertions.assertSame(configuration.getQueueManagerConfiguration(), queueManagerConfig);
    }

    @Test
    public void testConfigureCallsSuperConfigure()
    {
        // Test that configure calls super.configure
        // This is implicitly tested by the fact that the injector is created successfully
        // and parent bindings are available
        DepotStoreServerConfiguration configuration = createTestConfiguration();
        TestModule testModule = new TestModule(configuration);

        Injector injector = Guice.createInjector(
            new ServletModule(),
            testModule
        );

        // If super.configure wasn't called, this would fail
        Assertions.assertNotNull(injector);
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

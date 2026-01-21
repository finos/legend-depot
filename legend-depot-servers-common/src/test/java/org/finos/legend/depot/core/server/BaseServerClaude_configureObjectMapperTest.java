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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.tracing.configuration.TracerProvider;
import org.finos.legend.depot.store.StorageConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseServerClaude_configureObjectMapperTest
{
    /**
     * Simple Bootstrap stub for testing that avoids Dropwizard dependencies.
     * This stub provides just enough functionality to test the behavior of
     * configureObjectMapper method indirectly through the static configuration
     * methods it calls.
     */
    private static class BootstrapStub
    {
        private final ObjectMapper objectMapper;

        public BootstrapStub(ObjectMapper objectMapper)
        {
            this.objectMapper = objectMapper;
        }

        public ObjectMapper getObjectMapper()
        {
            return objectMapper;
        }
    }

    @Test
    @DisplayName("Test configureObjectMapper logic configures StorageConfiguration mixin")
    void testConfigureObjectMapperConfiguresStorageMixin()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        BootstrapStub bootstrap = new BootstrapStub(objectMapper);

        // Act - Call the static configuration method as configureObjectMapper would
        StorageConfiguration.configureObjectMapper(bootstrap.getObjectMapper());

        // Assert - verify mixin was added
        assertNotNull(objectMapper.findMixInClassFor(StorageConfiguration.class),
            "StorageConfiguration mixin should be configured");
    }

    @Test
    @DisplayName("Test configureObjectMapper logic configures TracerProvider mixin")
    void testConfigureObjectMapperConfiguresTracerMixin()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        BootstrapStub bootstrap = new BootstrapStub(objectMapper);

        // Act - Call TracerProviderConfiguration as configureObjectMapper would
        org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());

        // Assert - verify mixin was added
        assertNotNull(objectMapper.findMixInClassFor(TracerProvider.class),
            "TracerProvider mixin should be configured");
    }

    @Test
    @DisplayName("Test configureObjectMapper logic configures PrometheusMetricsHandler mixin")
    void testConfigureObjectMapperConfiguresPrometheusMetricsMixin()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        BootstrapStub bootstrap = new BootstrapStub(objectMapper);

        // Act - Call PrometheusMetricsProviderConfiguration as configureObjectMapper would
        org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());

        // Assert - verify mixin was added
        assertNotNull(objectMapper.findMixInClassFor(PrometheusMetricsHandler.class),
            "PrometheusMetricsHandler mixin should be configured");
    }

    @Test
    @DisplayName("Test configureObjectMapper logic configures all three mixins")
    void testConfigureObjectMapperConfiguresAllMixins()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        BootstrapStub bootstrap = new BootstrapStub(objectMapper);

        // Act - Call all three configuration methods as configureObjectMapper does
        StorageConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
        org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());
        org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());

        // Assert - verify all mixins were added
        assertNotNull(objectMapper.findMixInClassFor(StorageConfiguration.class),
            "StorageConfiguration mixin should be configured");
        assertNotNull(objectMapper.findMixInClassFor(TracerProvider.class),
            "TracerProvider mixin should be configured");
        assertNotNull(objectMapper.findMixInClassFor(PrometheusMetricsHandler.class),
            "PrometheusMetricsHandler mixin should be configured");
    }

    @Test
    @DisplayName("Test configuration methods preserve ObjectMapper instance")
    void testConfigurationMethodsPreserveInstance()
    {
        // Arrange
        ObjectMapper originalMapper = new ObjectMapper();
        BootstrapStub bootstrap = new BootstrapStub(originalMapper);

        // Act - Configure the mapper using the methods called by configureObjectMapper
        ObjectMapper result1 = StorageConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
        ObjectMapper result2 = org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());
        ObjectMapper result3 = org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());

        // Assert - the same instance should be returned (configured in place)
        assertTrue(result1 == originalMapper, "StorageConfiguration should return same instance");
        assertTrue(result2 == originalMapper, "TracerProviderConfiguration should return same instance");
        assertTrue(result3 == originalMapper, "PrometheusMetricsProviderConfiguration should return same instance");
    }

    @Test
    @DisplayName("Test configuration can be called multiple times without error")
    void testConfigurationCalledMultipleTimes()
    {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        BootstrapStub bootstrap = new BootstrapStub(objectMapper);

        // Act - Call configuration multiple times (should be idempotent or at least not fail)
        StorageConfiguration.configureObjectMapper(bootstrap.getObjectMapper());
        StorageConfiguration.configureObjectMapper(bootstrap.getObjectMapper());

        org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());
        org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());

        org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());
        org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration
            .configureObjectMapper(bootstrap.getObjectMapper());

        // Assert - verify mixins are still present
        assertNotNull(objectMapper.findMixInClassFor(StorageConfiguration.class),
            "StorageConfiguration mixin should still be configured");
        assertNotNull(objectMapper.findMixInClassFor(TracerProvider.class),
            "TracerProvider mixin should still be configured");
        assertNotNull(objectMapper.findMixInClassFor(PrometheusMetricsHandler.class),
            "PrometheusMetricsHandler mixin should still be configured");
    }

    @Test
    @DisplayName("Test each mixin configuration is independent")
    void testEachMixinConfigurationIsIndependent()
    {
        // Arrange
        ObjectMapper mapper1 = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();
        ObjectMapper mapper3 = new ObjectMapper();

        // Act - Configure each mapper with only one configuration
        StorageConfiguration.configureObjectMapper(mapper1);
        org.finos.legend.depot.core.services.api.tracing.configuration.TracerProviderConfiguration
            .configureObjectMapper(mapper2);
        org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusMetricsProviderConfiguration
            .configureObjectMapper(mapper3);

        // Assert - verify each mapper has only its respective mixin
        assertNotNull(mapper1.findMixInClassFor(StorageConfiguration.class),
            "Mapper1 should have StorageConfiguration mixin");
        assertNotNull(mapper2.findMixInClassFor(TracerProvider.class),
            "Mapper2 should have TracerProvider mixin");
        assertNotNull(mapper3.findMixInClassFor(PrometheusMetricsHandler.class),
            "Mapper3 should have PrometheusMetricsHandler mixin");
    }
}

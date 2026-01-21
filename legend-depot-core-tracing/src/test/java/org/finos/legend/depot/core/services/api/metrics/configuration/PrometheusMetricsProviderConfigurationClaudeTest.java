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

package org.finos.legend.depot.core.services.api.metrics.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.VoidPrometheusMetricsHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrometheusMetricsProviderConfigurationClaudeTest


{
    @Test
    public void testConfigureObjectMapperReturnsNonNull()
  {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);
        Assertions.assertNotNull(configuredMapper);
    }

    @Test
    public void testConfigureObjectMapperReturnsSameInstance()
  {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);
        Assertions.assertSame(objectMapper, configuredMapper);
    }

    @Test
    public void testConfigureObjectMapperWithNullInput()
  {
        Assertions.assertThrows(NullPointerException.class, () -> 
        {
            PrometheusMetricsProviderConfiguration.configureObjectMapper(null);
        });
    }

    @Test
    public void testConfigureObjectMapperAddsMixinForPrometheusMetricsHandler() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);

        // Verify that the mixin was added by checking the mixin configuration
        Class<?> mixinClass = configuredMapper.findMixInClassFor(PrometheusMetricsHandler.class);
        Assertions.assertNotNull(mixinClass);
    }

    @Test
    public void testConfigureObjectMapperSerializationWithWrapperObject() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);

        PrometheusMetricsHandler handler = new VoidPrometheusMetricsHandler();
        String json = configuredMapper.writeValueAsString(handler);

        // The mixin adds @JsonTypeInfo with WRAPPER_OBJECT, so the JSON should contain the class name as wrapper
        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("VoidPrometheusMetricsHandler"));
    }

    @Test
    public void testConfigureObjectMapperCanBeCalledMultipleTimes()
  {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper firstCall = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);
        ObjectMapper secondCall = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);

        Assertions.assertSame(objectMapper, firstCall);
        Assertions.assertSame(objectMapper, secondCall);
        Assertions.assertSame(firstCall, secondCall);
    }

    @Test
    public void testConfigureObjectMapperWithDifferentInstances()
  {
        ObjectMapper objectMapper1 = new ObjectMapper();
        ObjectMapper objectMapper2 = new ObjectMapper();

        ObjectMapper configured1 = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper1);
        ObjectMapper configured2 = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper2);

        Assertions.assertSame(objectMapper1, configured1);
        Assertions.assertSame(objectMapper2, configured2);
        Assertions.assertNotSame(configured1, configured2);
    }

    @Test
    public void testConfigureObjectMapperPreservesExistingConfiguration() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);

        // Verify that the mapper still works with standard serialization
        String simpleJson = configuredMapper.writeValueAsString("test");
        Assertions.assertEquals("\"test\"", simpleJson);
    }

    @Test
    public void testConfigureObjectMapperDoesNotAffectOtherClasses() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);

        // Serialize a different class to ensure it's not affected by the mixin
        TestData testData = new TestData("value");
        String json = configuredMapper.writeValueAsString(testData);

        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("value"));
        // Should not have wrapper object for non-PrometheusMetricsHandler classes
        Assertions.assertFalse(json.contains("TestData"));
    }

    @Test
    public void testConfigureObjectMapperWithPreconfiguredMapper() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDefaultTyping(new StdTypeResolverBuilder().init(com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, null));

        ObjectMapper configuredMapper = PrometheusMetricsProviderConfiguration.configureObjectMapper(objectMapper);

        Assertions.assertNotNull(configuredMapper);
        Assertions.assertSame(objectMapper, configuredMapper);
    }

    // Helper class for testing
    private static class TestData
    

{
        public String data;

        public TestData(String data)
  {
            this.data = data;
        }
    }
}

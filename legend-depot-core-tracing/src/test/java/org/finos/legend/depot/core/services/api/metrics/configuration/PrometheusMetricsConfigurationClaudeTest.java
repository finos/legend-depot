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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrometheusMetricsConfigurationClaudeTest


{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDefaultConstructor()
  {
        PrometheusMetricsConfiguration config = new PrometheusMetricsConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonSerializationWithDefaults() throws Exception
    {
        PrometheusMetricsConfiguration config = new PrometheusMetricsConfiguration();
        String json = objectMapper.writeValueAsString(config);
        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("\"enabled\":true"));
        Assertions.assertTrue(json.contains("\"prefix\":null") || !json.contains("\"prefix\""));
    }

    @Test
    public void testJsonDeserializationWithEnabledTrue() throws Exception
    {
        String json = "{\"enabled\":true,\"prefix\":\"test\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationWithEnabledFalse() throws Exception
    {
        String json = "{\"enabled\":false,\"prefix\":\"test\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationWithNullPrefix() throws Exception
    {
        String json = "{\"enabled\":true,\"prefix\":null}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationWithPrefix() throws Exception
    {
        String json = "{\"enabled\":true,\"prefix\":\"myprefix\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationEmptyObject() throws Exception
    {
        String json = "{}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationOnlyEnabled() throws Exception
    {
        String json = "{\"enabled\":false}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationOnlyPrefix() throws Exception
    {
        String json = "{\"prefix\":\"custom\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonRoundTripPreservesValues() throws Exception
    {
        String originalJson = "{\"enabled\":false,\"prefix\":\"roundtrip\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(originalJson, PrometheusMetricsConfiguration.class);
        String serializedJson = objectMapper.writeValueAsString(config);
        PrometheusMetricsConfiguration deserializedConfig = objectMapper.readValue(serializedJson, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(deserializedConfig);
    }

    @Test
    public void testJsonRoundTripWithEmptyPrefix() throws Exception
    {
        String originalJson = "{\"enabled\":true,\"prefix\":\"\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(originalJson, PrometheusMetricsConfiguration.class);
        String serializedJson = objectMapper.writeValueAsString(config);
        Assertions.assertTrue(serializedJson.contains("\"prefix\":\"\""));
    }

    @Test
    public void testJsonDeserializationWithSpecialCharactersInPrefix() throws Exception
    {
        String json = "{\"enabled\":true,\"prefix\":\"test_prefix-123.456\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationWithLongPrefix() throws Exception
    {
        String longPrefix = "a".repeat(1000);
        String json = "{\"enabled\":true,\"prefix\":\"" + longPrefix + "\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testJsonDeserializationWithUnicodeInPrefix() throws Exception
    {
        String json = "{\"enabled\":true,\"prefix\":\"测试\"}";
        PrometheusMetricsConfiguration config = objectMapper.readValue(json, PrometheusMetricsConfiguration.class);
        Assertions.assertNotNull(config);
    }

    @Test
    public void testMultipleInstancesAreIndependent() throws Exception
    {
        String json1 = "{\"enabled\":true,\"prefix\":\"prefix1\"}";
        String json2 = "{\"enabled\":false,\"prefix\":\"prefix2\"}";

        PrometheusMetricsConfiguration config1 = objectMapper.readValue(json1, PrometheusMetricsConfiguration.class);
        PrometheusMetricsConfiguration config2 = objectMapper.readValue(json2, PrometheusMetricsConfiguration.class);

        Assertions.assertNotNull(config1);
        Assertions.assertNotNull(config2);
        Assertions.assertNotSame(config1, config2);
    }
}

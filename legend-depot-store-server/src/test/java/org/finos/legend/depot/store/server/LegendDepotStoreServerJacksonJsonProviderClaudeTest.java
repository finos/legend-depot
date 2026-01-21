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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LegendDepotStoreServerJacksonJsonProviderClaudeTest
{
    @Test
    public void testConstructorCreatesNonNullInstance()
    {
        // Test that the constructor creates a valid instance
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        Assertions.assertNotNull(provider);
    }

    @Test
    public void testGetContextReturnsNonNullObjectMapper()
    {
        // Test that getContext returns a non-null ObjectMapper
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);
        Assertions.assertNotNull(mapper);
    }

    @Test
    public void testGetContextReturnsSameInstanceForDifferentTypes()
    {
        // Test that getContext returns the same ObjectMapper instance regardless of the type parameter
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper1 = provider.getContext(String.class);
        ObjectMapper mapper2 = provider.getContext(Integer.class);
        ObjectMapper mapper3 = provider.getContext(Object.class);

        Assertions.assertSame(mapper1, mapper2);
        Assertions.assertSame(mapper2, mapper3);
    }

    @Test
    public void testGetContextWithNullType()
    {
        // Test that getContext works with null type parameter
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(null);
        Assertions.assertNotNull(mapper);
    }

    @Test
    public void testObjectMapperHasCorrectDateFormat() throws JsonProcessingException
    {
        // Test that the ObjectMapper uses the correct date format
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);

        // Create a known date
        Date testDate = new Date(1234567890000L); // Fri Feb 13 2009 23:31:30 GMT

        String json = mapper.writeValueAsString(testDate);

        // The JSON should be formatted according to SIMPLE_DATE_FORMAT pattern: "yyyy-MM-dd HH:mm:ss"
        // Verify it's not in default timestamp format
        Assertions.assertFalse(json.equals("1234567890000"));

        // Verify it's a quoted string (date formatted as string)
        Assertions.assertTrue(json.startsWith("\""));
        Assertions.assertTrue(json.endsWith("\""));

        // Verify the format matches the pattern (yyyy-MM-dd HH:mm:ss)
        String dateString = json.substring(1, json.length() - 1);
        Assertions.assertTrue(dateString.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    public void testObjectMapperUsesDefaultTimeZone()
    {
        // Test that the ObjectMapper is configured with the default timezone
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);

        TimeZone expectedTimeZone = TimeZone.getDefault();
        TimeZone actualTimeZone = mapper.getSerializationConfig().getTimeZone();

        Assertions.assertEquals(expectedTimeZone, actualTimeZone);
    }

    @Test
    public void testSimpleDateFormatConstant()
    {
        // Test that the SIMPLE_DATE_FORMAT constant has the correct pattern
        SimpleDateFormat format = LegendDepotStoreServerJacksonJsonProvider.SIMPLE_DATE_FORMAT;
        Assertions.assertNotNull(format);

        // Verify the pattern
        String pattern = format.toPattern();
        Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", pattern);
    }

    @Test
    public void testGetContextReturnsConfiguredObjectMapper() throws JsonProcessingException
    {
        // Test that the ObjectMapper returned by getContext is properly configured
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);

        // Test serialization of a simple object
        TestObject testObj = new TestObject("test", 123);
        String json = mapper.writeValueAsString(testObj);

        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("test"));
        Assertions.assertTrue(json.contains("123"));
    }

    @Test
    public void testMultipleCallsToGetContextReturnSameMapper()
    {
        // Test that multiple calls to getContext return the same ObjectMapper instance
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();

        ObjectMapper mapper1 = provider.getContext(Object.class);
        ObjectMapper mapper2 = provider.getContext(Object.class);
        ObjectMapper mapper3 = provider.getContext(Object.class);

        Assertions.assertSame(mapper1, mapper2);
        Assertions.assertSame(mapper2, mapper3);
    }

    @Test
    public void testDateFormatting() throws JsonProcessingException
    {
        // Test that dates are formatted correctly in serialized JSON
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);

        Date testDate = new Date(0); // Unix epoch: 1970-01-01 00:00:00 UTC
        String json = mapper.writeValueAsString(testDate);

        // Remove quotes
        String dateString = json.substring(1, json.length() - 1);

        // Parse it back using the same format to verify it's correct
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());

        Assertions.assertDoesNotThrow(() -> sdf.parse(dateString));
    }

    @Test
    public void testProviderExtendsJacksonJsonProvider()
    {
        // Test that LegendDepotStoreServerJacksonJsonProvider extends JacksonJsonProvider
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        Assertions.assertTrue(provider instanceof com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider);
    }

    @Test
    public void testProviderImplementsContextResolver()
    {
        // Test that LegendDepotStoreServerJacksonJsonProvider implements ContextResolver
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        Assertions.assertTrue(provider instanceof javax.ws.rs.ext.ContextResolver);
    }

    @Test
    public void testObjectMapperCanSerializeComplexObjects() throws JsonProcessingException
    {
        // Test that the ObjectMapper can serialize complex nested objects
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);

        ComplexTestObject complex = new ComplexTestObject(
            new TestObject("inner", 456),
            new Date(1000000000000L)
        );

        String json = mapper.writeValueAsString(complex);

        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("inner"));
        Assertions.assertTrue(json.contains("456"));
    }

    @Test
    public void testObjectMapperCanDeserializeObjects() throws JsonProcessingException
    {
        // Test that the ObjectMapper can deserialize JSON back to objects
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
        ObjectMapper mapper = provider.getContext(Object.class);

        String json = "{\"name\":\"test\",\"value\":789}";
        TestObject obj = mapper.readValue(json, TestObject.class);

        Assertions.assertNotNull(obj);
        Assertions.assertEquals("test", obj.name);
        Assertions.assertEquals(789, obj.value);
    }

    // Helper classes for testing
    public static class TestObject
    {
        public String name;
        public int value;

        public TestObject()
        {
        }

        public TestObject(String name, int value)
        {
            this.name = name;
            this.value = value;
        }
    }

    public static class ComplexTestObject
    {
        public TestObject nested;
        public Date timestamp;

        public ComplexTestObject()
        {
        }

        public ComplexTestObject(TestObject nested, Date timestamp)
        {
            this.nested = nested;
            this.timestamp = timestamp;
        }
    }
}

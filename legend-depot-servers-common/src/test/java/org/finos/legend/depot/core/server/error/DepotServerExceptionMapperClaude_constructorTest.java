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

package org.finos.legend.depot.core.server.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DepotServerExceptionMapper constructor.
 */
class DepotServerExceptionMapperClaude_constructorTest
{
    @Test
    @DisplayName("Test constructor with includeStackTrace=true initializes correctly")
    void testConstructorWithIncludeStackTraceTrue() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(true);

        // Assert
        assertNotNull(mapper, "Mapper should not be null");

        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTrace = (boolean) field.get(mapper);

        assertTrue(includeStackTrace, "includeStackTrace should be true when constructor is called with true");
    }

    @Test
    @DisplayName("Test constructor with includeStackTrace=false initializes correctly")
    void testConstructorWithIncludeStackTraceFalse() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);

        // Assert
        assertNotNull(mapper, "Mapper should not be null");

        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTrace = (boolean) field.get(mapper);

        assertFalse(includeStackTrace, "includeStackTrace should be false when constructor is called with false");
    }

    @Test
    @DisplayName("Test constructor creates instance that implements ExceptionMapper")
    void testConstructorCreatesExceptionMapper()
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(true);

        // Assert
        assertNotNull(mapper, "Mapper should not be null");
        assertTrue(mapper instanceof javax.ws.rs.ext.ExceptionMapper,
            "Mapper should implement ExceptionMapper interface");
    }

    @Test
    @DisplayName("Test constructor with true and false create independent instances")
    void testMultipleInstancesAreIndependent() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper1 = new DepotServerExceptionMapper(true);
        DepotServerExceptionMapper mapper2 = new DepotServerExceptionMapper(false);

        // Assert
        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTrace1 = (boolean) field.get(mapper1);
        boolean includeStackTrace2 = (boolean) field.get(mapper2);

        assertTrue(includeStackTrace1, "First mapper should have includeStackTrace=true");
        assertFalse(includeStackTrace2, "Second mapper should have includeStackTrace=false");
    }

    @Test
    @DisplayName("Test constructor can be called multiple times with same value")
    void testConstructorCanBeCalledMultipleTimes() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper1 = new DepotServerExceptionMapper(true);
        DepotServerExceptionMapper mapper2 = new DepotServerExceptionMapper(true);

        // Assert
        assertNotNull(mapper1, "First mapper should not be null");
        assertNotNull(mapper2, "Second mapper should not be null");
        assertNotSame(mapper1, mapper2, "Each constructor call should create a distinct instance");

        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTrace1 = (boolean) field.get(mapper1);
        boolean includeStackTrace2 = (boolean) field.get(mapper2);

        assertTrue(includeStackTrace1, "First mapper should have includeStackTrace=true");
        assertTrue(includeStackTrace2, "Second mapper should have includeStackTrace=true");
    }

    // Tests for default constructor (no arguments)

    @Test
    @DisplayName("Test default constructor initializes with includeStackTrace=false")
    void testDefaultConstructorInitializesWithFalse() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper();

        // Assert
        assertNotNull(mapper, "Mapper should not be null");

        // Use reflection to verify the includeStackTrace field is set to false by default
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTrace = (boolean) field.get(mapper);

        assertFalse(includeStackTrace, "includeStackTrace should be false when using default constructor");
    }

    @Test
    @DisplayName("Test default constructor creates instance that implements ExceptionMapper")
    void testDefaultConstructorCreatesExceptionMapper()
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper();

        // Assert
        assertNotNull(mapper, "Mapper should not be null");
        assertTrue(mapper instanceof javax.ws.rs.ext.ExceptionMapper,
            "Mapper should implement ExceptionMapper interface");
    }

    @Test
    @DisplayName("Test default constructor can be called multiple times")
    void testDefaultConstructorCanBeCalledMultipleTimes() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapper1 = new DepotServerExceptionMapper();
        DepotServerExceptionMapper mapper2 = new DepotServerExceptionMapper();

        // Assert
        assertNotNull(mapper1, "First mapper should not be null");
        assertNotNull(mapper2, "Second mapper should not be null");
        assertNotSame(mapper1, mapper2, "Each constructor call should create a distinct instance");

        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTrace1 = (boolean) field.get(mapper1);
        boolean includeStackTrace2 = (boolean) field.get(mapper2);

        assertFalse(includeStackTrace1, "First mapper should have includeStackTrace=false");
        assertFalse(includeStackTrace2, "Second mapper should have includeStackTrace=false");
    }

    @Test
    @DisplayName("Test default constructor is equivalent to constructor with false parameter")
    void testDefaultConstructorEquivalentToFalseParameter() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapperDefault = new DepotServerExceptionMapper();
        DepotServerExceptionMapper mapperFalse = new DepotServerExceptionMapper(false);

        // Assert
        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTraceDefault = (boolean) field.get(mapperDefault);
        boolean includeStackTraceFalse = (boolean) field.get(mapperFalse);

        assertEquals(includeStackTraceFalse, includeStackTraceDefault,
            "Default constructor should behave the same as constructor with false parameter");
        assertFalse(includeStackTraceDefault, "Both should have includeStackTrace=false");
    }

    @Test
    @DisplayName("Test mixing default constructor with parameterized constructor creates independent instances")
    void testMixingDefaultAndParameterizedConstructors() throws Exception
    {
        // Arrange & Act
        DepotServerExceptionMapper mapperDefault = new DepotServerExceptionMapper();
        DepotServerExceptionMapper mapperTrue = new DepotServerExceptionMapper(true);
        DepotServerExceptionMapper mapperFalse = new DepotServerExceptionMapper(false);

        // Assert
        // Use reflection to verify the includeStackTrace field is set correctly
        // Reflection is necessary because the includeStackTrace field is protected in the parent class
        // and there is no public getter method to access it. Testing constructor behavior requires
        // verifying that the field was properly initialized.
        java.lang.reflect.Field field = BaseExceptionMapper.class.getDeclaredField("includeStackTrace");
        field.setAccessible(true);
        boolean includeStackTraceDefault = (boolean) field.get(mapperDefault);
        boolean includeStackTraceTrue = (boolean) field.get(mapperTrue);
        boolean includeStackTraceFalse = (boolean) field.get(mapperFalse);

        assertFalse(includeStackTraceDefault, "Default constructor mapper should have includeStackTrace=false");
        assertTrue(includeStackTraceTrue, "Parameterized constructor with true should have includeStackTrace=true");
        assertFalse(includeStackTraceFalse, "Parameterized constructor with false should have includeStackTrace=false");
    }
}

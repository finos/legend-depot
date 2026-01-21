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

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LegendDepotServerException class.
 *
 * This test class tests all methods in LegendDepotServerException including:
 * - Constructors with various parameter combinations
 * - getStatus() method
 * - validateNonNull() static methods
 * - validate() static methods with predicates, messages, and functions
 *
 * Tests focus on branch and condition coverage, ensuring that all paths
 * through the code are exercised with meaningful test cases.
 */
class LegendDepotServerExceptionClaudeTest
{
    // Tests for constructor: LegendDepotServerException(String, Response.Status, Throwable)

    @Test
    @DisplayName("Test constructor with message, status, and cause - all non-null")
    void testConstructorWithMessageStatusAndCause()
    {
        // Arrange
        String message = "Test error message";
        Response.Status status = Response.Status.BAD_REQUEST;
        Throwable cause = new RuntimeException("Root cause");

        // Act
        LegendDepotServerException exception = new LegendDepotServerException(message, status, cause);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(status, exception.getStatus(), "Status should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    @Test
    @DisplayName("Test constructor with message and cause but null status - defaults to INTERNAL_SERVER_ERROR")
    void testConstructorWithNullStatusDefaultsToInternalServerError()
    {
        // Arrange
        String message = "Error with null status";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        LegendDepotServerException exception = new LegendDepotServerException(message, null, cause);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getStatus(),
            "Status should default to INTERNAL_SERVER_ERROR when null");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }

    @Test
    @DisplayName("Test constructor with various HTTP status codes")
    void testConstructorWithVariousStatusCodes()
    {
        // Test multiple status codes
        Response.Status[] statuses = {
            Response.Status.OK,
            Response.Status.BAD_REQUEST,
            Response.Status.NOT_FOUND,
            Response.Status.FORBIDDEN,
            Response.Status.INTERNAL_SERVER_ERROR,
            Response.Status.SERVICE_UNAVAILABLE
        };

        for (Response.Status status : statuses)
        {
            // Arrange & Act
            LegendDepotServerException exception = new LegendDepotServerException(
                "Error", status, new RuntimeException());

            // Assert
            assertEquals(status, exception.getStatus(),
                "Status should match for " + status);
        }
    }

    // Tests for constructor: LegendDepotServerException(String, Response.Status)

    @Test
    @DisplayName("Test constructor with message and status - no cause")
    void testConstructorWithMessageAndStatus()
    {
        // Arrange
        String message = "Test error message";
        Response.Status status = Response.Status.NOT_FOUND;

        // Act
        LegendDepotServerException exception = new LegendDepotServerException(message, status);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(status, exception.getStatus(), "Status should match");
        assertNull(exception.getCause(), "Cause should be null");
    }

    @Test
    @DisplayName("Test constructor with message and null status - defaults to INTERNAL_SERVER_ERROR")
    void testConstructorWithMessageAndNullStatus()
    {
        // Arrange
        String message = "Error with null status";

        // Act
        LegendDepotServerException exception = new LegendDepotServerException(message, (Response.Status) null);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getStatus(),
            "Status should default to INTERNAL_SERVER_ERROR when null");
        assertNull(exception.getCause(), "Cause should be null");
    }

    // Tests for constructor: LegendDepotServerException(String, Throwable)

    @Test
    @DisplayName("Test constructor with message and cause - no explicit status")
    void testConstructorWithMessageAndCause()
    {
        // Arrange
        String message = "Error with cause";
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // Act
        LegendDepotServerException exception = new LegendDepotServerException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getStatus(),
            "Status should default to INTERNAL_SERVER_ERROR");
    }

    // Tests for constructor: LegendDepotServerException(String)

    @Test
    @DisplayName("Test constructor with message only")
    void testConstructorWithMessageOnly()
    {
        // Arrange
        String message = "Simple error message";

        // Act
        LegendDepotServerException exception = new LegendDepotServerException(message);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertNull(exception.getCause(), "Cause should be null");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getStatus(),
            "Status should default to INTERNAL_SERVER_ERROR");
    }

    // Tests for getStatus()

    @Test
    @DisplayName("Test getStatus returns correct status")
    void testGetStatusReturnsCorrectStatus()
    {
        // Arrange
        Response.Status expectedStatus = Response.Status.UNAUTHORIZED;
        LegendDepotServerException exception = new LegendDepotServerException(
            "Unauthorized", expectedStatus);

        // Act
        Response.Status actualStatus = exception.getStatus();

        // Assert
        assertEquals(expectedStatus, actualStatus, "getStatus should return the correct status");
    }

    // Tests for validateNonNull(T, String)

    @Test
    @DisplayName("Test validateNonNull with non-null argument returns the argument")
    void testValidateNonNullWithNonNullArgument()
    {
        // Arrange
        String arg = "valid argument";
        String message = "Argument should not be null";

        // Act
        String result = LegendDepotServerException.validateNonNull(arg, message);

        // Assert
        assertEquals(arg, result, "validateNonNull should return the argument when it's not null");
    }

    @Test
    @DisplayName("Test validateNonNull with null argument throws exception with BAD_REQUEST status")
    void testValidateNonNullWithNullArgumentThrowsException()
    {
        // Arrange
        String message = "Argument cannot be null";

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validateNonNull(null, message),
            "validateNonNull should throw exception when argument is null");

        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST by default");
    }

    @Test
    @DisplayName("Test validateNonNull with various non-null types")
    void testValidateNonNullWithVariousTypes()
    {
        // Test with different types
        Integer intValue = 42;
        assertEquals(intValue, LegendDepotServerException.validateNonNull(intValue, "Error"));

        Double doubleValue = 3.14;
        assertEquals(doubleValue, LegendDepotServerException.validateNonNull(doubleValue, "Error"));

        Object objectValue = new Object();
        assertEquals(objectValue, LegendDepotServerException.validateNonNull(objectValue, "Error"));
    }

    // Tests for validateNonNull(T, String, Response.Status)

    @Test
    @DisplayName("Test validateNonNull with custom status and non-null argument returns the argument")
    void testValidateNonNullWithCustomStatusAndNonNullArgument()
    {
        // Arrange
        String arg = "valid argument";
        String message = "Argument should not be null";
        Response.Status status = Response.Status.FORBIDDEN;

        // Act
        String result = LegendDepotServerException.validateNonNull(arg, message, status);

        // Assert
        assertEquals(arg, result, "validateNonNull should return the argument when it's not null");
    }

    @Test
    @DisplayName("Test validateNonNull with custom status and null argument throws exception with custom status")
    void testValidateNonNullWithCustomStatusAndNullArgument()
    {
        // Arrange
        String message = "Forbidden resource";
        Response.Status status = Response.Status.FORBIDDEN;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validateNonNull(null, message, status),
            "validateNonNull should throw exception when argument is null");

        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(status, exception.getStatus(), "Status should match custom status");
    }

    @Test
    @DisplayName("Test validateNonNull with null status and null argument uses BAD_REQUEST")
    void testValidateNonNullWithNullStatusAndNullArgument()
    {
        // Arrange
        String message = "Error message";

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validateNonNull(null, message, null),
            "validateNonNull should throw exception when argument is null");

        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST when null status is provided");
    }

    // Tests for validate(T, Predicate, String)

    @Test
    @DisplayName("Test validate with predicate that passes returns the argument")
    void testValidateWithPredicateThatPasses()
    {
        // Arrange
        String arg = "valid";
        String message = "Validation failed";

        // Act
        String result = LegendDepotServerException.validate(
            arg, s -> s != null && s.length() > 0, message);

        // Assert
        assertEquals(arg, result, "validate should return the argument when predicate passes");
    }

    @Test
    @DisplayName("Test validate with predicate that fails throws exception")
    void testValidateWithPredicateThatFails()
    {
        // Arrange
        String arg = "";
        String message = "String must not be empty";

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg, s -> s != null && s.length() > 0, message),
            "validate should throw exception when predicate fails");

        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST by default");
    }

    @Test
    @DisplayName("Test validate with various predicates")
    void testValidateWithVariousPredicates()
    {
        // Test with number range validation
        Integer number = 10;
        Integer result = LegendDepotServerException.validate(
            number, n -> n >= 0 && n <= 100, "Number must be between 0 and 100");
        assertEquals(number, result);

        // Test with string length validation
        String text = "hello";
        String textResult = LegendDepotServerException.validate(
            text, s -> s.length() >= 5, "String too short");
        assertEquals(text, textResult);
    }

    // Tests for validate(T, Predicate, String, Response.Status)

    @Test
    @DisplayName("Test validate with custom status and passing predicate returns the argument")
    void testValidateWithCustomStatusAndPassingPredicate()
    {
        // Arrange
        Integer arg = 100;
        String message = "Value must be positive";
        Response.Status status = Response.Status.CONFLICT;

        // Act
        Integer result = LegendDepotServerException.validate(
            arg, n -> n > 0, message, status);

        // Assert
        assertEquals(arg, result, "validate should return the argument when predicate passes");
    }

    @Test
    @DisplayName("Test validate with custom status and failing predicate throws exception with custom status")
    void testValidateWithCustomStatusAndFailingPredicate()
    {
        // Arrange
        Integer arg = -5;
        String message = "Value must be positive";
        Response.Status status = Response.Status.CONFLICT;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(arg, n -> n > 0, message, status),
            "validate should throw exception when predicate fails");

        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(status, exception.getStatus(), "Status should match custom status");
    }

    @Test
    @DisplayName("Test validate with null status and failing predicate uses BAD_REQUEST")
    void testValidateWithNullStatusAndFailingPredicate()
    {
        // Arrange
        String arg = "";
        String message = "String cannot be empty";

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg, s -> !s.isEmpty(), message, null),
            "validate should throw exception when predicate fails");

        assertEquals(message, exception.getMessage(), "Exception message should match");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST when null status is provided");
    }

    // Tests for validate(T, Predicate, Function)

    @Test
    @DisplayName("Test validate with message function and passing predicate returns the argument")
    void testValidateWithMessageFunctionAndPassingPredicate()
    {
        // Arrange
        Integer arg = 50;

        // Act
        Integer result = LegendDepotServerException.validate(
            arg,
            n -> n >= 0 && n <= 100,
            n -> "Value " + n + " is out of range");

        // Assert
        assertEquals(arg, result, "validate should return the argument when predicate passes");
    }

    @Test
    @DisplayName("Test validate with message function and failing predicate throws exception with generated message")
    void testValidateWithMessageFunctionAndFailingPredicate()
    {
        // Arrange
        Integer arg = 150;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg,
                n -> n >= 0 && n <= 100,
                n -> "Value " + n + " is out of range"),
            "validate should throw exception when predicate fails");

        assertEquals("Value 150 is out of range", exception.getMessage(),
            "Exception message should be generated by function");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST by default");
    }

    @Test
    @DisplayName("Test validate with null message function and failing predicate throws exception with null message")
    void testValidateWithNullMessageFunctionAndFailingPredicate()
    {
        // Arrange
        Integer arg = -1;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg,
                n -> n >= 0,
                (java.util.function.Function<Integer, String>) null),
            "validate should throw exception when predicate fails");

        assertNull(exception.getMessage(),
            "Exception message should be null when message function is null");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST by default");
    }

    // Tests for validate(T, Predicate, Function, Response.Status)

    @Test
    @DisplayName("Test validate with message function and custom status - passing predicate returns the argument")
    void testValidateWithMessageFunctionAndCustomStatusPassingPredicate()
    {
        // Arrange
        String arg = "valid";
        Response.Status status = Response.Status.NOT_ACCEPTABLE;

        // Act
        String result = LegendDepotServerException.validate(
            arg,
            s -> s != null && !s.isEmpty(),
            s -> "Invalid string: " + s,
            status);

        // Assert
        assertEquals(arg, result, "validate should return the argument when predicate passes");
    }

    @Test
    @DisplayName("Test validate with message function and custom status - failing predicate throws exception")
    void testValidateWithMessageFunctionAndCustomStatusFailingPredicate()
    {
        // Arrange
        String arg = "";
        Response.Status status = Response.Status.NOT_ACCEPTABLE;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg,
                s -> s != null && !s.isEmpty(),
                s -> "Invalid string: '" + s + "'",
                status),
            "validate should throw exception when predicate fails");

        assertEquals("Invalid string: ''", exception.getMessage(),
            "Exception message should be generated by function");
        assertEquals(status, exception.getStatus(), "Status should match custom status");
    }

    @Test
    @DisplayName("Test validate with message function, null status, and failing predicate uses BAD_REQUEST")
    void testValidateWithMessageFunctionNullStatusAndFailingPredicate()
    {
        // Arrange
        Integer arg = 200;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg,
                n -> n <= 100,
                n -> "Value " + n + " exceeds maximum",
                null),
            "validate should throw exception when predicate fails");

        assertEquals("Value 200 exceeds maximum", exception.getMessage(),
            "Exception message should be generated by function");
        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus(),
            "Status should be BAD_REQUEST when null status is provided");
    }

    @Test
    @DisplayName("Test validate with null message function and custom status")
    void testValidateWithNullMessageFunctionAndCustomStatus()
    {
        // Arrange
        Integer arg = -1;
        Response.Status status = Response.Status.PRECONDITION_FAILED;

        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                arg,
                n -> n >= 0,
                (java.util.function.Function<Integer, String>) null,
                status),
            "validate should throw exception when predicate fails");

        assertNull(exception.getMessage(),
            "Exception message should be null when message function is null");
        assertEquals(status, exception.getStatus(), "Status should match custom status");
    }

    // Additional comprehensive tests

    @Test
    @DisplayName("Test validate with complex predicate logic")
    void testValidateWithComplexPredicateLogic()
    {
        // Arrange - validate an email-like string
        String validEmail = "user@example.com";
        String invalidEmail = "invalid-email";

        // Act - test with valid email
        String result = LegendDepotServerException.validate(
            validEmail,
            s -> s.contains("@") && s.contains("."),
            "Invalid email format");

        // Assert
        assertEquals(validEmail, result);

        // Act & Assert - test with invalid email
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                invalidEmail,
                s -> s.contains("@") && s.contains("."),
                "Invalid email format"));

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Test exception is RuntimeException and can be thrown unchecked")
    void testExceptionIsRuntimeException()
    {
        // Arrange & Act
        LegendDepotServerException exception = new LegendDepotServerException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException,
            "LegendDepotServerException should be a RuntimeException");
    }

    @Test
    @DisplayName("Test exception with null message")
    void testExceptionWithNullMessage()
    {
        // Act
        LegendDepotServerException exception = new LegendDepotServerException(null);

        // Assert
        assertNull(exception.getMessage(), "Message should be null");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getStatus(),
            "Status should default to INTERNAL_SERVER_ERROR");
    }

    @Test
    @DisplayName("Test validate with null argument and predicate that allows null")
    void testValidateWithNullArgumentAndPredicateAllowsNull()
    {
        // Act
        Object result = LegendDepotServerException.validate(
            null,
            obj -> obj == null,
            "Object must be null");

        // Assert
        assertNull(result, "validate should return null when predicate passes for null");
    }

    @Test
    @DisplayName("Test validate with null argument and predicate that rejects null")
    void testValidateWithNullArgumentAndPredicateRejectsNull()
    {
        // Act & Assert
        LegendDepotServerException exception = assertThrows(
            LegendDepotServerException.class,
            () -> LegendDepotServerException.validate(
                null,
                obj -> obj != null,
                "Object must not be null"));

        assertEquals("Object must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Test exception serialization compatibility")
    void testExceptionSerializationCompatibility()
    {
        // Arrange
        LegendDepotServerException exception = new LegendDepotServerException(
            "Serializable exception",
            Response.Status.INTERNAL_SERVER_ERROR,
            new RuntimeException("Cause"));

        // Assert - verify the exception has a serialVersionUID (indirectly through instanceof check)
        assertTrue(exception instanceof java.io.Serializable,
            "LegendDepotServerException should be Serializable");
    }
}

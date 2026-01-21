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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExtendedErrorMessage class.
 *
 * This test class tests all methods in ExtendedErrorMessage including:
 * - Factory methods (newExtendedErrorMessage, fromThrowable, fromLegendDepotServerException, fromWebApplicationException)
 * - Getter methods (getTimestamp, getStackTrace)
 * - Message extraction from exception chains
 * - Stack trace generation
 *
 * To test WebApplicationException handling, we use a custom RuntimeDelegate implementation
 * that provides minimal functionality needed for testing Response creation.
 */
class ExtendedErrorMessageClaudeTest
{
    private static RuntimeDelegate originalDelegate;

    @BeforeAll
    static void setupRuntimeDelegate()
    {
        // Save original delegate if it exists
        try
        {
            originalDelegate = RuntimeDelegate.getInstance();
        }
        catch (Exception e)
        {
            originalDelegate = null;
        }

        // Set a custom RuntimeDelegate that provides basic functionality for testing
        RuntimeDelegate.setInstance(new TestRuntimeDelegate());
    }

    @AfterAll
    static void restoreRuntimeDelegate()
    {
        if (originalDelegate != null)
        {
            RuntimeDelegate.setInstance(originalDelegate);
        }
    }
    // Tests for newExtendedErrorMessage (JSON creator)

    @Test
    @DisplayName("Test newExtendedErrorMessage creates message with all fields")
    void testNewExtendedErrorMessageWithAllFields()
    {
        // Arrange
        int code = 500;
        String message = "Test error message";
        String details = "Test error details";
        String stackTrace = "Stack trace string";
        Instant timestamp = Instant.now();

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.newExtendedErrorMessage(
            code, message, details, stackTrace, timestamp);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(code, errorMessage.getCode(), "Code should match");
        assertEquals(message, errorMessage.getMessage(), "Message should match");
        assertEquals(details, errorMessage.getDetails(), "Details should match");
        assertEquals(stackTrace, errorMessage.getStackTrace(), "Stack trace should match");
        assertEquals(timestamp, errorMessage.getTimestamp(), "Timestamp should match");
    }

    @Test
    @DisplayName("Test newExtendedErrorMessage with null stack trace")
    void testNewExtendedErrorMessageWithNullStackTrace()
    {
        // Arrange
        int code = 400;
        String message = "Bad request";
        String details = null;
        String stackTrace = null;
        Instant timestamp = Instant.now();

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.newExtendedErrorMessage(
            code, message, details, stackTrace, timestamp);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(code, errorMessage.getCode(), "Code should match");
        assertEquals(message, errorMessage.getMessage(), "Message should match");
        assertNull(errorMessage.getDetails(), "Details should be null");
        assertNull(errorMessage.getStackTrace(), "Stack trace should be null");
        assertEquals(timestamp, errorMessage.getTimestamp(), "Timestamp should match");
    }

    @Test
    @DisplayName("Test newExtendedErrorMessage with null message and details")
    void testNewExtendedErrorMessageWithNullMessageAndDetails()
    {
        // Arrange
        int code = 500;
        String message = null;
        String details = null;
        String stackTrace = "Stack trace";
        Instant timestamp = Instant.now();

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.newExtendedErrorMessage(
            code, message, details, stackTrace, timestamp);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(code, errorMessage.getCode(), "Code should match");
        assertNull(errorMessage.getMessage(), "Message should be null");
        assertNull(errorMessage.getDetails(), "Details should be null");
        assertEquals(stackTrace, errorMessage.getStackTrace(), "Stack trace should match");
    }

    // Tests for fromThrowable(Throwable, boolean) - general method

    @Test
    @DisplayName("Test fromThrowable with LegendDepotServerException delegates to specific method")
    void testFromThrowableWithLegendDepotServerException()
    {
        // Arrange
        LegendDepotServerException exception = new LegendDepotServerException(
            "Server exception message",
            Response.Status.INTERNAL_SERVER_ERROR);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(exception, true);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(500, errorMessage.getCode(), "Code should be 500");
        assertEquals("Server exception message", errorMessage.getMessage(), "Message should match");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included");
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    @DisplayName("Test fromThrowable with WebApplicationException delegates to specific method")
    void testFromThrowableWithWebApplicationException()
    {
        // Arrange
        WebApplicationException exception = new WebApplicationException(
            "Web app exception",
            Response.Status.BAD_REQUEST);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(400, errorMessage.getCode(), "Code should be 400");
        assertEquals("Web app exception", errorMessage.getMessage(), "Message should match");
        assertNull(errorMessage.getStackTrace(), "Stack trace should not be included");
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    @DisplayName("Test fromThrowable with generic exception defaults to 500")
    void testFromThrowableWithGenericException()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Generic runtime exception");

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(500, errorMessage.getCode(), "Code should default to 500");
        assertEquals("Generic runtime exception", errorMessage.getMessage(), "Message should match");
        assertNull(errorMessage.getStackTrace(), "Stack trace should not be included");
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    @DisplayName("Test fromThrowable with generic exception includes stack trace when requested")
    void testFromThrowableWithGenericExceptionIncludesStackTrace()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Error with stack trace");

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(exception, true);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(500, errorMessage.getCode(), "Code should default to 500");
        assertEquals("Error with stack trace", errorMessage.getMessage(), "Message should match");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included");
        assertTrue(errorMessage.getStackTrace().contains("Error with stack trace"),
            "Stack trace should contain exception message");
        assertTrue(errorMessage.getStackTrace().contains("RuntimeException"),
            "Stack trace should contain exception type");
    }

    // Tests for fromLegendDepotServerException

    @Test
    @DisplayName("Test fromLegendDepotServerException with BAD_REQUEST status")
    void testFromLegendDepotServerExceptionWithBadRequest()
    {
        // Arrange
        LegendDepotServerException exception = new LegendDepotServerException(
            "Bad request error",
            Response.Status.BAD_REQUEST);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromLegendDepotServerException(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(400, errorMessage.getCode(), "Code should be 400");
        assertEquals("Bad request error", errorMessage.getMessage(), "Message should match");
        assertNull(errorMessage.getStackTrace(), "Stack trace should not be included");
    }

    @Test
    @DisplayName("Test fromLegendDepotServerException with INTERNAL_SERVER_ERROR and stack trace")
    void testFromLegendDepotServerExceptionWithInternalServerError()
    {
        // Arrange
        LegendDepotServerException exception = new LegendDepotServerException(
            "Internal error",
            Response.Status.INTERNAL_SERVER_ERROR);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromLegendDepotServerException(
            exception, true);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(500, errorMessage.getCode(), "Code should be 500");
        assertEquals("Internal error", errorMessage.getMessage(), "Message should match");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included");
        assertTrue(errorMessage.getStackTrace().contains("Internal error"),
            "Stack trace should contain exception message");
    }

    @Test
    @DisplayName("Test fromLegendDepotServerException with NOT_FOUND status")
    void testFromLegendDepotServerExceptionWithNotFound()
    {
        // Arrange
        LegendDepotServerException exception = new LegendDepotServerException(
            "Resource not found",
            Response.Status.NOT_FOUND);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromLegendDepotServerException(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(404, errorMessage.getCode(), "Code should be 404");
        assertEquals("Resource not found", errorMessage.getMessage(), "Message should match");
    }

    // Tests for fromWebApplicationException

    @Test
    @DisplayName("Test fromWebApplicationException with BAD_REQUEST status")
    void testFromWebApplicationExceptionWithBadRequest()
    {
        // Arrange
        WebApplicationException exception = new WebApplicationException(
            "Bad request",
            Response.Status.BAD_REQUEST);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromWebApplicationException(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(400, errorMessage.getCode(), "Code should be 400");
        assertEquals("Bad request", errorMessage.getMessage(), "Message should match");
        assertNull(errorMessage.getStackTrace(), "Stack trace should not be included");
    }

    @Test
    @DisplayName("Test fromWebApplicationException with INTERNAL_SERVER_ERROR and stack trace")
    void testFromWebApplicationExceptionWithInternalServerError()
    {
        // Arrange
        WebApplicationException exception = new WebApplicationException(
            "Server error",
            Response.Status.INTERNAL_SERVER_ERROR);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromWebApplicationException(
            exception, true);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(500, errorMessage.getCode(), "Code should be 500");
        assertEquals("Server error", errorMessage.getMessage(), "Message should match");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included");
    }

    @Test
    @DisplayName("Test fromWebApplicationException with NOT_FOUND status")
    void testFromWebApplicationExceptionWithNotFound()
    {
        // Arrange
        WebApplicationException exception = new WebApplicationException(
            "Not found",
            Response.Status.NOT_FOUND);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromWebApplicationException(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(404, errorMessage.getCode(), "Code should be 404");
        assertEquals("Not found", errorMessage.getMessage(), "Message should match");
    }

    // Tests for fromThrowable(Throwable, Response.Status, String, String, boolean)

    @Test
    @DisplayName("Test fromThrowable with custom status, message, and details")
    void testFromThrowableWithCustomStatusMessageDetails()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Original exception message");
        Response.Status status = Response.Status.BAD_GATEWAY;
        String customMessage = "Custom error message";
        String customDetails = "Custom error details";

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, status, customMessage, customDetails, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(502, errorMessage.getCode(), "Code should be 502");
        assertEquals(customMessage, errorMessage.getMessage(), "Message should be custom message");
        assertEquals(customDetails, errorMessage.getDetails(), "Details should be custom details");
        assertNull(errorMessage.getStackTrace(), "Stack trace should not be included");
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    @DisplayName("Test fromThrowable with null status defaults to 500")
    void testFromThrowableWithNullStatusDefaultsTo500()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Exception message");
        Response.Status status = null;
        String customMessage = "Custom message";

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, status, customMessage, null, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(500, errorMessage.getCode(), "Code should default to 500 when status is null");
        assertEquals(customMessage, errorMessage.getMessage(), "Message should be custom message");
    }

    @Test
    @DisplayName("Test fromThrowable with null custom message uses exception message")
    void testFromThrowableWithNullCustomMessageUsesExceptionMessage()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Exception message");
        Response.Status status = Response.Status.BAD_REQUEST;

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, status, null, null, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(400, errorMessage.getCode(), "Code should be 400");
        assertEquals("Exception message", errorMessage.getMessage(), "Message should come from exception");
    }

    @Test
    @DisplayName("Test fromThrowable with custom status and stack trace enabled")
    void testFromThrowableWithCustomStatusAndStackTrace()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Error with trace");
        Response.Status status = Response.Status.SERVICE_UNAVAILABLE;
        String customMessage = "Service unavailable";

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, status, customMessage, null, true);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(503, errorMessage.getCode(), "Code should be 503");
        assertEquals(customMessage, errorMessage.getMessage(), "Message should be custom message");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included");
        assertTrue(errorMessage.getStackTrace().contains("Error with trace"),
            "Stack trace should contain original exception message");
    }

    // Tests for fromThrowable(Throwable, int, String, String, boolean)

    @Test
    @DisplayName("Test fromThrowable with custom status code")
    void testFromThrowableWithCustomStatusCode()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Exception");
        int statusCode = 403;
        String message = "Forbidden";
        String details = "Access denied";

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, statusCode, message, details, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(403, errorMessage.getCode(), "Code should be 403");
        assertEquals(message, errorMessage.getMessage(), "Message should match");
        assertEquals(details, errorMessage.getDetails(), "Details should match");
        assertNull(errorMessage.getStackTrace(), "Stack trace should not be included");
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    @DisplayName("Test fromThrowable with custom status code and stack trace")
    void testFromThrowableWithCustomStatusCodeAndStackTrace()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Error");
        int statusCode = 502;
        String message = "Bad Gateway";

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, statusCode, message, null, true);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(502, errorMessage.getCode(), "Code should be 502");
        assertEquals(message, errorMessage.getMessage(), "Message should match");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included");
    }

    @Test
    @DisplayName("Test fromThrowable with custom status code and null message extracts from exception")
    void testFromThrowableWithCustomStatusCodeNullMessage()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error");
        int statusCode = 409;

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, statusCode, null, null, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals(409, errorMessage.getCode(), "Code should be 409");
        assertEquals("Runtime error", errorMessage.getMessage(), "Message should come from exception");
    }

    // Tests for message extraction from exception chains

    @Test
    @DisplayName("Test message extraction with exception that has null message but has cause")
    void testMessageExtractionWithNullMessageAndCause()
    {
        // Arrange
        RuntimeException cause = new RuntimeException("Cause message");
        RuntimeException exception = new RuntimeException((String) null, cause);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals("Cause message", errorMessage.getMessage(),
            "Message should be extracted from cause");
    }

    @Test
    @DisplayName("Test message extraction with deep exception chain")
    void testMessageExtractionWithDeepExceptionChain()
    {
        // Arrange
        RuntimeException deepCause = new RuntimeException("Deep cause message");
        RuntimeException middleCause = new RuntimeException((String) null, deepCause);
        RuntimeException exception = new RuntimeException((String) null, middleCause);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals("Deep cause message", errorMessage.getMessage(),
            "Message should be extracted from deepest cause with message");
    }

    @Test
    @DisplayName("Test message extraction with all null messages in chain")
    void testMessageExtractionWithAllNullMessagesInChain()
    {
        // Arrange
        RuntimeException deepCause = new RuntimeException((String) null);
        RuntimeException middleCause = new RuntimeException((String) null, deepCause);
        RuntimeException exception = new RuntimeException((String) null, middleCause);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertNull(errorMessage.getMessage(), "Message should be null when all messages in chain are null");
    }

    @Test
    @DisplayName("Test message extraction when exception has message, ignores cause")
    void testMessageExtractionWhenExceptionHasMessage()
    {
        // Arrange
        RuntimeException cause = new RuntimeException("Cause message");
        RuntimeException exception = new RuntimeException("Exception message", cause);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, false);

        // Assert
        assertNotNull(errorMessage, "Error message should not be null");
        assertEquals("Exception message", errorMessage.getMessage(),
            "Message should come from exception, not cause");
    }

    // Tests for stack trace generation

    @Test
    @DisplayName("Test stack trace contains exception type and message")
    void testStackTraceContainsExceptionTypeAndMessage()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Test exception message");

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, true);

        // Assert
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should not be null");
        String stackTrace = errorMessage.getStackTrace();
        assertTrue(stackTrace.contains("RuntimeException"),
            "Stack trace should contain exception type");
        assertTrue(stackTrace.contains("Test exception message"),
            "Stack trace should contain exception message");
        assertTrue(stackTrace.contains("at "),
            "Stack trace should contain stack frames");
    }

    @Test
    @DisplayName("Test stack trace includes cause when present")
    void testStackTraceIncludesCause()
    {
        // Arrange
        RuntimeException cause = new RuntimeException("Cause exception");
        RuntimeException exception = new RuntimeException("Main exception", cause);

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, true);

        // Assert
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should not be null");
        String stackTrace = errorMessage.getStackTrace();
        assertTrue(stackTrace.contains("Main exception"),
            "Stack trace should contain main exception message");
        assertTrue(stackTrace.contains("Cause exception"),
            "Stack trace should contain cause exception message");
        assertTrue(stackTrace.contains("Caused by:"),
            "Stack trace should contain 'Caused by:' for cause");
    }

    @Test
    @DisplayName("Test no stack trace included when flag is false")
    void testNoStackTraceWhenFlagIsFalse()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Exception without stack trace");

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, false);

        // Assert
        assertNull(errorMessage.getStackTrace(), "Stack trace should be null when includeStackTrace is false");
    }

    // Tests for timestamp

    @Test
    @DisplayName("Test timestamp is set to current time")
    void testTimestampIsSetToCurrentTime()
    {
        // Arrange
        Instant before = Instant.now();
        RuntimeException exception = new RuntimeException("Test exception");

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, false);
        Instant after = Instant.now();

        // Assert
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should not be null");
        assertFalse(errorMessage.getTimestamp().isBefore(before),
            "Timestamp should not be before test started");
        assertFalse(errorMessage.getTimestamp().isAfter(after),
            "Timestamp should not be after test completed");
    }

    // Tests for getter methods

    @Test
    @DisplayName("Test getTimestamp returns correct timestamp")
    void testGetTimestampReturnsCorrectTimestamp()
    {
        // Arrange
        Instant timestamp = Instant.parse("2021-01-01T00:00:00Z");
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.newExtendedErrorMessage(
            500, "Message", null, null, timestamp);

        // Act
        Instant result = errorMessage.getTimestamp();

        // Assert
        assertEquals(timestamp, result, "getTimestamp should return the correct timestamp");
    }

    @Test
    @DisplayName("Test getStackTrace returns correct stack trace")
    void testGetStackTraceReturnsCorrectStackTrace()
    {
        // Arrange
        String stackTrace = "Stack trace content";
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.newExtendedErrorMessage(
            500, "Message", null, stackTrace, Instant.now());

        // Act
        String result = errorMessage.getStackTrace();

        // Assert
        assertEquals(stackTrace, result, "getStackTrace should return the correct stack trace");
    }

    @Test
    @DisplayName("Test getStackTrace returns null when stack trace is null")
    void testGetStackTraceReturnsNullWhenNull()
    {
        // Arrange
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.newExtendedErrorMessage(
            400, "Message", null, null, Instant.now());

        // Act
        String result = errorMessage.getStackTrace();

        // Assert
        assertNull(result, "getStackTrace should return null when stack trace is null");
    }

    // Additional edge case tests

    @Test
    @DisplayName("Test fromThrowable with null details preserves null")
    void testFromThrowableWithNullDetails()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Exception");

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, Response.Status.BAD_REQUEST, "Message", null, false);

        // Assert
        assertNull(errorMessage.getDetails(), "Details should be null");
    }

    @Test
    @DisplayName("Test fromThrowable with custom details preserves details")
    void testFromThrowableWithCustomDetails()
    {
        // Arrange
        RuntimeException exception = new RuntimeException("Exception");
        String details = "Custom error details";

        // Act
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            exception, Response.Status.BAD_REQUEST, "Message", details, false);

        // Assert
        assertEquals(details, errorMessage.getDetails(), "Details should be preserved");
    }

    @Test
    @DisplayName("Test fromThrowable with various HTTP status codes")
    void testFromThrowableWithVariousStatusCodes()
    {
        // Test several different status codes
        int[] statusCodes = {200, 201, 301, 302, 400, 401, 403, 404, 500, 502, 503};

        for (int statusCode : statusCodes)
        {
            // Arrange
            RuntimeException exception = new RuntimeException("Exception for status " + statusCode);

            // Act
            ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
                exception, statusCode, "Message", null, false);

            // Assert
            assertEquals(statusCode, errorMessage.getCode(),
                "Status code should match for " + statusCode);
        }
    }

    /**
     * Minimal RuntimeDelegate implementation for testing.
     * Provides only the functionality needed to create Response objects in tests.
     */
    private static class TestRuntimeDelegate extends RuntimeDelegate
    {
        @Override
        public <T> T createEndpoint(javax.ws.rs.core.Application application, Class<T> endpointType)
        {
            return null;
        }

        @Override
        public <T> javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate<T> createHeaderDelegate(Class<T> type)
        {
            return new javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate<T>()
            {
                @Override
                public T fromString(String value)
                {
                    return null;
                }

                @Override
                public String toString(T value)
                {
                    return value != null ? value.toString() : null;
                }
            };
        }

        @Override
        public Response.ResponseBuilder createResponseBuilder()
        {
            return new TestResponseBuilder();
        }

        @Override
        public javax.ws.rs.core.UriBuilder createUriBuilder()
        {
            return null;
        }

        @Override
        public javax.ws.rs.core.Variant.VariantListBuilder createVariantListBuilder()
        {
            return null;
        }

        @Override
        public javax.ws.rs.core.Link.Builder createLinkBuilder()
        {
            return null;
        }
    }

    /**
     * Minimal ResponseBuilder implementation for testing.
     */
    private static class TestResponseBuilder extends Response.ResponseBuilder
    {
        private int status;
        private Object entity;
        private MediaType mediaType;
        private URI location;

        @Override
        public Response build()
        {
            return new TestResponse(status, entity, mediaType, location);
        }

        @Override
        public Response.ResponseBuilder clone()
        {
            TestResponseBuilder clone = new TestResponseBuilder();
            clone.status = this.status;
            clone.entity = this.entity;
            clone.mediaType = this.mediaType;
            clone.location = this.location;
            return clone;
        }

        @Override
        public Response.ResponseBuilder status(int status)
        {
            this.status = status;
            return this;
        }

        @Override
        public Response.ResponseBuilder status(Response.Status status)
        {
            this.status = status.getStatusCode();
            return this;
        }

        @Override
        public Response.ResponseBuilder status(int status, String reasonPhrase)
        {
            this.status = status;
            return this;
        }

        @Override
        public Response.ResponseBuilder entity(Object entity)
        {
            this.entity = entity;
            return this;
        }

        @Override
        public Response.ResponseBuilder entity(Object entity, java.lang.annotation.Annotation[] annotations)
        {
            this.entity = entity;
            return this;
        }

        @Override
        public Response.ResponseBuilder type(MediaType type)
        {
            this.mediaType = type;
            return this;
        }

        @Override
        public Response.ResponseBuilder type(String type)
        {
            this.mediaType = MediaType.valueOf(type);
            return this;
        }

        @Override
        public Response.ResponseBuilder location(URI location)
        {
            this.location = location;
            return this;
        }

        @Override
        public Response.ResponseBuilder contentLocation(URI location)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder tag(javax.ws.rs.core.EntityTag tag)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder tag(String tag)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder lastModified(java.util.Date lastModified)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder cacheControl(javax.ws.rs.core.CacheControl cacheControl)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder expires(java.util.Date expires)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder header(String name, Object value)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder cookie(javax.ws.rs.core.NewCookie... cookies)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder language(String language)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder language(java.util.Locale language)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder variant(javax.ws.rs.core.Variant variant)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder variants(java.util.List<javax.ws.rs.core.Variant> variants)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder links(javax.ws.rs.core.Link... links)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder link(URI uri, String rel)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder link(String uri, String rel)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder allow(String... methods)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder allow(java.util.Set<String> methods)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder encoding(String encoding)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder replaceAll(javax.ws.rs.core.MultivaluedMap<String, Object> headers)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder variants(javax.ws.rs.core.Variant... variants)
        {
            return this;
        }
    }

    /**
     * Minimal Response implementation for testing.
     */
    private static class TestResponse extends Response
    {
        private final int status;
        private final Object entity;
        private final MediaType mediaType;
        private final URI location;

        public TestResponse(int status, Object entity, MediaType mediaType, URI location)
        {
            this.status = status;
            this.entity = entity;
            this.mediaType = mediaType;
            this.location = location;
        }

        @Override
        public int getStatus()
        {
            return status;
        }

        @Override
        public javax.ws.rs.core.Response.StatusType getStatusInfo()
        {
            return Response.Status.fromStatusCode(status);
        }

        @Override
        public Object getEntity()
        {
            return entity;
        }

        @Override
        public <T> T readEntity(Class<T> entityType)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public <T> T readEntity(javax.ws.rs.core.GenericType<T> entityType)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public <T> T readEntity(Class<T> entityType, java.lang.annotation.Annotation[] annotations)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public <T> T readEntity(javax.ws.rs.core.GenericType<T> entityType, java.lang.annotation.Annotation[] annotations)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public boolean hasEntity()
        {
            return entity != null;
        }

        @Override
        public boolean bufferEntity()
        {
            return false;
        }

        @Override
        public void close()
        {
        }

        @Override
        public MediaType getMediaType()
        {
            return mediaType;
        }

        @Override
        public java.util.Locale getLanguage()
        {
            return null;
        }

        @Override
        public int getLength()
        {
            return -1;
        }

        @Override
        public java.util.Set<String> getAllowedMethods()
        {
            return java.util.Collections.emptySet();
        }

        @Override
        public java.util.Map<String, javax.ws.rs.core.NewCookie> getCookies()
        {
            return java.util.Collections.emptyMap();
        }

        @Override
        public javax.ws.rs.core.EntityTag getEntityTag()
        {
            return null;
        }

        @Override
        public java.util.Date getDate()
        {
            return null;
        }

        @Override
        public java.util.Date getLastModified()
        {
            return null;
        }

        @Override
        public URI getLocation()
        {
            return location;
        }

        @Override
        public java.util.Set<javax.ws.rs.core.Link> getLinks()
        {
            return java.util.Collections.emptySet();
        }

        @Override
        public boolean hasLink(String relation)
        {
            return false;
        }

        @Override
        public javax.ws.rs.core.Link getLink(String relation)
        {
            return null;
        }

        @Override
        public javax.ws.rs.core.Link.Builder getLinkBuilder(String relation)
        {
            return null;
        }

        @Override
        public javax.ws.rs.core.MultivaluedMap<String, Object> getMetadata()
        {
            return new javax.ws.rs.core.MultivaluedHashMap<>();
        }

        @Override
        public javax.ws.rs.core.MultivaluedMap<String, String> getStringHeaders()
        {
            return new javax.ws.rs.core.MultivaluedHashMap<>();
        }

        @Override
        public String getHeaderString(String name)
        {
            return null;
        }
    }
}

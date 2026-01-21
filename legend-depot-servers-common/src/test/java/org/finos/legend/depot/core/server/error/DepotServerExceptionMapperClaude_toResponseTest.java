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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DepotServerExceptionMapper.toResponse method.
 *
 * This test class tests the toResponse(LegendDepotServerException) method which handles:
 * - CLIENT_ERROR responses (4xx status codes)
 * - SERVER_ERROR responses (5xx status codes)
 * - REDIRECTION responses (3xx status codes)
 * - Other status families (defaults to INTERNAL_SERVER_ERROR)
 *
 * To test Response creation, we use a custom RuntimeDelegate implementation
 * that provides minimal functionality needed for testing.
 */
class DepotServerExceptionMapperClaude_toResponseTest
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

    // Tests for CLIENT_ERROR status family (4xx)

    @Test
    @DisplayName("Test toResponse with CLIENT_ERROR BAD_REQUEST - never includes stack trace")
    void testToResponseWithClientErrorBadRequest()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(true); // includeStackTrace=true
        LegendDepotServerException exception = new LegendDepotServerException(
            "Bad request error",
            Response.Status.BAD_REQUEST
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(400, response.getStatus(), "Response status should be 400");
        assertNotNull(response.getEntity(), "Response entity should not be null");
        assertTrue(response.getEntity() instanceof ExtendedErrorMessage,
            "Response entity should be ExtendedErrorMessage");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(400, errorMessage.getCode(), "Error message code should be 400");
        assertEquals("Bad request error", errorMessage.getMessage(), "Error message should match exception message");
        assertNull(errorMessage.getStackTrace(), "Stack trace should be null for CLIENT_ERROR even when includeStackTrace=true");
    }

    @Test
    @DisplayName("Test toResponse with CLIENT_ERROR NOT_FOUND")
    void testToResponseWithClientErrorNotFound()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Resource not found",
            Response.Status.NOT_FOUND
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(404, response.getStatus(), "Response status should be 404");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(404, errorMessage.getCode(), "Error message code should be 404");
        assertEquals("Resource not found", errorMessage.getMessage(), "Error message should match exception message");
    }

    @Test
    @DisplayName("Test toResponse with CLIENT_ERROR UNAUTHORIZED")
    void testToResponseWithClientErrorUnauthorized()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Unauthorized access",
            Response.Status.UNAUTHORIZED
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(401, response.getStatus(), "Response status should be 401");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(401, errorMessage.getCode(), "Error message code should be 401");
    }

    @Test
    @DisplayName("Test toResponse with CLIENT_ERROR FORBIDDEN")
    void testToResponseWithClientErrorForbidden()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Access forbidden",
            Response.Status.FORBIDDEN
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(403, response.getStatus(), "Response status should be 403");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(403, errorMessage.getCode(), "Error message code should be 403");
    }

    @Test
    @DisplayName("Test toResponse with CLIENT_ERROR CONFLICT")
    void testToResponseWithClientErrorConflict()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Resource conflict",
            Response.Status.CONFLICT
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(409, response.getStatus(), "Response status should be 409");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(409, errorMessage.getCode(), "Error message code should be 409");
    }

    // Tests for SERVER_ERROR status family (5xx)

    @Test
    @DisplayName("Test toResponse with SERVER_ERROR INTERNAL_SERVER_ERROR without stack trace")
    void testToResponseWithServerErrorWithoutStackTrace()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Internal server error",
            Response.Status.INTERNAL_SERVER_ERROR
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(500, errorMessage.getCode(), "Error message code should be 500");
        assertEquals("Internal server error", errorMessage.getMessage(), "Error message should match exception message");
        assertNull(errorMessage.getStackTrace(), "Stack trace should be null when includeStackTrace=false");
    }

    @Test
    @DisplayName("Test toResponse with SERVER_ERROR INTERNAL_SERVER_ERROR with stack trace")
    void testToResponseWithServerErrorWithStackTrace()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(true);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Internal server error",
            Response.Status.INTERNAL_SERVER_ERROR
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(500, errorMessage.getCode(), "Error message code should be 500");
        assertEquals("Internal server error", errorMessage.getMessage(), "Error message should match exception message");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should not be null when includeStackTrace=true");
        assertTrue(errorMessage.getStackTrace().contains("Internal server error"),
            "Stack trace should contain exception message");
    }

    @Test
    @DisplayName("Test toResponse with SERVER_ERROR SERVICE_UNAVAILABLE")
    void testToResponseWithServerErrorServiceUnavailable()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Service unavailable",
            Response.Status.SERVICE_UNAVAILABLE
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(503, response.getStatus(), "Response status should be 503");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(503, errorMessage.getCode(), "Error message code should be 503");
    }

    @Test
    @DisplayName("Test toResponse with SERVER_ERROR BAD_GATEWAY")
    void testToResponseWithServerErrorBadGateway()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Bad gateway",
            Response.Status.BAD_GATEWAY
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(502, response.getStatus(), "Response status should be 502");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(502, errorMessage.getCode(), "Error message code should be 502");
    }

    // Tests for REDIRECTION status family (3xx)

    @Test
    @DisplayName("Test toResponse with REDIRECTION MOVED_PERMANENTLY with valid URI")
    void testToResponseWithRedirectionMovedPermanentlyValidUri()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        String redirectUrl = "http://example.com/new-location";
        LegendDepotServerException exception = new LegendDepotServerException(
            redirectUrl,
            Response.Status.MOVED_PERMANENTLY
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(301, response.getStatus(), "Response status should be 301");
        assertNotNull(response.getLocation(), "Location header should not be null");
        assertEquals(redirectUrl, response.getLocation().toString(), "Location should match redirect URL");
    }

    @Test
    @DisplayName("Test toResponse with REDIRECTION FOUND with valid URI")
    void testToResponseWithRedirectionFoundValidUri()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        String redirectUrl = "http://example.com/temporary-location";
        LegendDepotServerException exception = new LegendDepotServerException(
            redirectUrl,
            Response.Status.FOUND
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(302, response.getStatus(), "Response status should be 302");
        assertNotNull(response.getLocation(), "Location header should not be null");
        assertEquals(redirectUrl, response.getLocation().toString(), "Location should match redirect URL");
    }

    @Test
    @DisplayName("Test toResponse with REDIRECTION SEE_OTHER with valid URI")
    void testToResponseWithRedirectionSeeOtherValidUri()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        String redirectUrl = "http://example.com/see-other";
        LegendDepotServerException exception = new LegendDepotServerException(
            redirectUrl,
            Response.Status.SEE_OTHER
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(303, response.getStatus(), "Response status should be 303");
        assertNotNull(response.getLocation(), "Location header should not be null");
        assertEquals(redirectUrl, response.getLocation().toString(), "Location should match redirect URL");
    }

    @Test
    @DisplayName("Test toResponse with REDIRECTION TEMPORARY_REDIRECT with valid URI")
    void testToResponseWithRedirectionTemporaryRedirectValidUri()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        String redirectUrl = "http://example.com/temp-redirect";
        LegendDepotServerException exception = new LegendDepotServerException(
            redirectUrl,
            Response.Status.TEMPORARY_REDIRECT
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(307, response.getStatus(), "Response status should be 307");
        assertNotNull(response.getLocation(), "Location header should not be null");
        assertEquals(redirectUrl, response.getLocation().toString(), "Location should match redirect URL");
    }

    @Test
    @DisplayName("Test toResponse with REDIRECTION but null message - falls back to INTERNAL_SERVER_ERROR")
    void testToResponseWithRedirectionNullMessage()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            null,
            Response.Status.MOVED_PERMANENTLY
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500 (fallback for invalid redirect)");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(errorMessage, "Error message should not be null");
        // The ExtendedErrorMessage preserves the original exception status code
        assertEquals(301, errorMessage.getCode(), "Error message code should be 301 (original exception status)");
    }

    @Test
    @DisplayName("Test toResponse with REDIRECTION but invalid URI - falls back to INTERNAL_SERVER_ERROR")
    void testToResponseWithRedirectionInvalidUri()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "not a valid uri",
            Response.Status.MOVED_PERMANENTLY
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500 (fallback for invalid redirect)");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(errorMessage, "Error message should not be null");
        // The ExtendedErrorMessage preserves the original exception status code
        assertEquals(301, errorMessage.getCode(), "Error message code should be 301 (original exception status)");
    }

    // Tests for non-error status families (default case)

    @Test
    @DisplayName("Test toResponse with OK status - falls back to INTERNAL_SERVER_ERROR")
    void testToResponseWithOkStatus()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "OK but exception?",
            Response.Status.OK
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500 (fallback for non-error status)");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(errorMessage, "Error message should not be null");
        // The ExtendedErrorMessage preserves the original exception status code
        assertEquals(200, errorMessage.getCode(), "Error message code should be 200 (original exception status)");
    }

    @Test
    @DisplayName("Test toResponse with CREATED status - falls back to INTERNAL_SERVER_ERROR")
    void testToResponseWithCreatedStatus()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "Created but exception?",
            Response.Status.CREATED
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500 (fallback for non-error status)");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(errorMessage, "Error message should not be null");
        // The ExtendedErrorMessage preserves the original exception status code
        assertEquals(201, errorMessage.getCode(), "Error message code should be 201 (original exception status)");
    }

    @Test
    @DisplayName("Test toResponse with NO_CONTENT status - falls back to INTERNAL_SERVER_ERROR")
    void testToResponseWithNoContentStatus()
    {
        // Arrange
        DepotServerExceptionMapper mapper = new DepotServerExceptionMapper(false);
        LegendDepotServerException exception = new LegendDepotServerException(
            "No content but exception?",
            Response.Status.NO_CONTENT
        );

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500 (fallback for non-error status)");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(errorMessage, "Error message should not be null");
        // The ExtendedErrorMessage preserves the original exception status code
        assertEquals(204, errorMessage.getCode(), "Error message code should be 204 (original exception status)");
    }

    // Test that mapper respects includeStackTrace for SERVER_ERROR vs CLIENT_ERROR

    @Test
    @DisplayName("Test that includeStackTrace is respected for SERVER_ERROR but not CLIENT_ERROR")
    void testIncludeStackTraceRespectedForServerErrorOnly()
    {
        // Arrange
        DepotServerExceptionMapper mapperWithStackTrace = new DepotServerExceptionMapper(true);
        LegendDepotServerException serverError = new LegendDepotServerException(
            "Server error",
            Response.Status.INTERNAL_SERVER_ERROR
        );
        LegendDepotServerException clientError = new LegendDepotServerException(
            "Client error",
            Response.Status.BAD_REQUEST
        );

        // Act
        Response serverResponse = mapperWithStackTrace.toResponse(serverError);
        Response clientResponse = mapperWithStackTrace.toResponse(clientError);

        // Assert
        ExtendedErrorMessage serverErrorMessage = (ExtendedErrorMessage) serverResponse.getEntity();
        ExtendedErrorMessage clientErrorMessage = (ExtendedErrorMessage) clientResponse.getEntity();

        assertNotNull(serverErrorMessage.getStackTrace(),
            "Server error should include stack trace when includeStackTrace=true");
        assertNull(clientErrorMessage.getStackTrace(),
            "Client error should never include stack trace even when includeStackTrace=true");
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

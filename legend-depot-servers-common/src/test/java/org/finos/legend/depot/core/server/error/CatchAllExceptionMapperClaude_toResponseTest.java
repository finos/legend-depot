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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CatchAllExceptionMapper.toResponse method.
 *
 * This test class specifically targets the private toResponse(WebApplicationException) method
 * by calling it through the public toResponse(Throwable) method.
 *
 * To test WebApplicationException handling, we use a custom RuntimeDelegate implementation
 * that provides minimal functionality needed for testing Response creation.
 */
class CatchAllExceptionMapperClaude_toResponseTest
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

    // Tests for WebApplicationException with non-redirection responses (covers lines 43, 48-51)

    @Test
    @DisplayName("Test toResponse with WebApplicationException BAD_REQUEST - covers lines 43, 48-51")
    void testToResponseWithWebApplicationExceptionBadRequest()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.BAD_REQUEST).build();
        WebApplicationException exception = new WebApplicationException("Bad request error", originalResponse);

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
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException NOT_FOUND - covers lines 43, 48-51")
    void testToResponseWithWebApplicationExceptionNotFound()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.NOT_FOUND).build();
        WebApplicationException exception = new WebApplicationException("Resource not found", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(404, response.getStatus(), "Response status should be 404");
        assertTrue(response.getEntity() instanceof ExtendedErrorMessage,
            "Response entity should be ExtendedErrorMessage");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(404, errorMessage.getCode(), "Error message code should be 404");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException UNAUTHORIZED - covers lines 43, 48-51")
    void testToResponseWithWebApplicationExceptionUnauthorized()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.UNAUTHORIZED).build();
        WebApplicationException exception = new WebApplicationException("Unauthorized access", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(401, response.getStatus(), "Response status should be 401");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(401, errorMessage.getCode(), "Error message code should be 401");
        assertEquals("Unauthorized access", errorMessage.getMessage(), "Error message should match");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException INTERNAL_SERVER_ERROR - covers lines 43, 48-51")
    void testToResponseWithWebApplicationExceptionInternalServerError()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        WebApplicationException exception = new WebApplicationException("Server error", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(500, errorMessage.getCode(), "Error message code should be 500");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException includeStackTrace=true - covers lines 43, 48-51, line 50")
    void testToResponseWithWebApplicationExceptionWithStackTrace()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(true);
        Response originalResponse = Response.status(Response.Status.CONFLICT).build();
        WebApplicationException exception = new WebApplicationException("Conflict error", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(409, response.getStatus(), "Response status should be 409");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(409, errorMessage.getCode(), "Error message code should be 409");
        assertNotNull(errorMessage.getStackTrace(), "Stack trace should be included when includeStackTrace is true");
        assertTrue(errorMessage.getStackTrace().contains("Conflict error"),
            "Stack trace should contain exception message");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException sets media type to JSON - covers line 49")
    void testToResponseWithWebApplicationExceptionSetsJsonMediaType()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.FORBIDDEN).build();
        WebApplicationException exception = new WebApplicationException("Forbidden", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(403, response.getStatus(), "Response status should be 403");
        MediaType mediaType = response.getMediaType();
        assertNotNull(mediaType, "Media type should not be null");
        assertEquals("application", mediaType.getType(), "Media type should be application");
        assertEquals("json", mediaType.getSubtype(), "Media subtype should be json");
    }

    // Tests for WebApplicationException with redirection responses (covers lines 43, 44, 46)

    @Test
    @DisplayName("Test toResponse with WebApplicationException MOVED_PERMANENTLY returns original response - covers lines 43, 44, 46")
    void testToResponseWithWebApplicationExceptionMovedPermanently()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.MOVED_PERMANENTLY).build();
        WebApplicationException exception = new WebApplicationException("Moved permanently", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(301, response.getStatus(), "Response status should be 301");
        // For redirection responses, the original response should be returned as-is
        assertSame(originalResponse, response, "For redirection, original response should be returned");
        assertNull(response.getEntity(), "Redirection response should not have entity modified");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException FOUND (302) returns original response - covers lines 43, 44, 46")
    void testToResponseWithWebApplicationExceptionFound()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.FOUND).build();
        WebApplicationException exception = new WebApplicationException("Found", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(302, response.getStatus(), "Response status should be 302");
        assertSame(originalResponse, response, "For redirection, original response should be returned");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException SEE_OTHER returns original response - covers lines 43, 44, 46")
    void testToResponseWithWebApplicationExceptionSeeOther()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.SEE_OTHER).build();
        WebApplicationException exception = new WebApplicationException("See other", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(303, response.getStatus(), "Response status should be 303");
        assertSame(originalResponse, response, "For redirection, original response should be returned");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException TEMPORARY_REDIRECT returns original response - covers lines 43, 44, 46")
    void testToResponseWithWebApplicationExceptionTemporaryRedirect()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.TEMPORARY_REDIRECT).build();
        WebApplicationException exception = new WebApplicationException("Temporary redirect", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(307, response.getStatus(), "Response status should be 307");
        assertSame(originalResponse, response, "For redirection, original response should be returned");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException USE_PROXY returns original response - covers lines 43, 44, 46")
    void testToResponseWithWebApplicationExceptionUseProxy()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.USE_PROXY).build();
        WebApplicationException exception = new WebApplicationException("Use proxy", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(305, response.getStatus(), "Response status should be 305");
        assertSame(originalResponse, response, "For redirection, original response should be returned");
    }

    // Tests for edge cases

    @Test
    @DisplayName("Test toResponse with WebApplicationException SERVICE_UNAVAILABLE - covers lines 43, 48-51")
    void testToResponseWithWebApplicationExceptionServiceUnavailable()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        WebApplicationException exception = new WebApplicationException("Service unavailable", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(503, response.getStatus(), "Response status should be 503");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals(503, errorMessage.getCode(), "Error message code should be 503");
    }

    @Test
    @DisplayName("Test toResponse with WebApplicationException null message - covers lines 43, 48-51")
    void testToResponseWithWebApplicationExceptionNullMessage()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.BAD_REQUEST).build();
        WebApplicationException exception = new WebApplicationException((String) null, originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(400, response.getStatus(), "Response status should be 400");
        assertNotNull(response.getEntity(), "Response entity should not be null");
    }

    @Test
    @DisplayName("Test toResponse verifies ExtendedErrorMessage creation from WebApplicationException - covers line 50")
    void testToResponseCreatesExtendedErrorMessageFromWebApplicationException()
    {
        // Arrange
        CatchAllExceptionMapper mapper = new CatchAllExceptionMapper(false);
        Response originalResponse = Response.status(Response.Status.BAD_GATEWAY).build();
        WebApplicationException exception = new WebApplicationException("Bad gateway", originalResponse);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(502, response.getStatus(), "Response status should be 502");
        assertTrue(response.getEntity() instanceof ExtendedErrorMessage,
            "Response entity should be ExtendedErrorMessage");
        ExtendedErrorMessage errorMessage = (ExtendedErrorMessage) response.getEntity();
        assertEquals("Bad gateway", errorMessage.getMessage(), "Error message should match exception message");
        assertNotNull(errorMessage.getTimestamp(), "Timestamp should be set");
        assertNull(errorMessage.getStackTrace(), "Stack trace should be null when includeStackTrace is false");
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
     * Minimal Response.ResponseBuilder implementation for testing.
     */
    private static class TestResponseBuilder extends Response.ResponseBuilder
    {
        private int status;
        private Object entity;
        private MediaType mediaType;

        @Override
        public Response build()
        {
            return new TestResponse(status, entity, mediaType);
        }

        @Override
        public Response.ResponseBuilder clone()
        {
            TestResponseBuilder clone = new TestResponseBuilder();
            clone.status = this.status;
            clone.entity = this.entity;
            clone.mediaType = this.mediaType;
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
            this.mediaType = new MediaType("application", "json");
            return this;
        }

        @Override
        public Response.ResponseBuilder variant(javax.ws.rs.core.Variant variant)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder variants(java.util.List<javax.ws.rs.core.Variant> variants)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder language(String language)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder language(java.util.Locale language)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder location(java.net.URI location)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder contentLocation(java.net.URI location)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder tag(javax.ws.rs.core.EntityTag tag)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder tag(String tag)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder lastModified(java.util.Date lastModified)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder cacheControl(javax.ws.rs.core.CacheControl cacheControl)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder expires(java.util.Date expires)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder header(String name, Object value)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder cookie(javax.ws.rs.core.NewCookie... cookies)
        {
            throw new UnsupportedOperationException("Not implemented for testing");
        }

        @Override
        public Response.ResponseBuilder link(String uri, String rel)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder link(java.net.URI uri, String rel)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder links(javax.ws.rs.core.Link... links)
        {
            return this;
        }

        @Override
        public Response.ResponseBuilder replaceAll(javax.ws.rs.core.MultivaluedMap<String, Object> headers)
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

        public TestResponse(int status, Object entity, MediaType mediaType)
        {
            this.status = status;
            this.entity = entity;
            this.mediaType = mediaType;
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
        public java.net.URI getLocation()
        {
            return null;
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

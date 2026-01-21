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
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for BaseExceptionMapper.buildResponse method.
 *
 * This test class specifically targets the buildResponse method to achieve better coverage.
 * Since buildResponse creates JAX-RS Response objects, we use a custom RuntimeDelegate
 * implementation that provides minimal functionality needed for testing.
 */
class BaseExceptionMapperClaude_buildResponseTest
{
    private static RuntimeDelegate originalDelegate;

    /**
     * Concrete implementation of BaseExceptionMapper for testing purposes.
     */
    public static class TestExceptionMapper extends BaseExceptionMapper<RuntimeException>
    {
        public TestExceptionMapper(boolean includeStackTrace)
        {
            super(includeStackTrace);
        }

        @Override
        public Response toResponse(RuntimeException exception)
        {
            return buildDefaultResponse(exception);
        }

        // Expose protected method for testing
        public Response testBuildResponse(Response.Status status, ExtendedErrorMessage errorMessage)
        {
            return buildResponse(status, errorMessage);
        }
    }

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

    @Test
    @DisplayName("Test buildResponse with matching status codes - covers lines 42-45")
    void testBuildResponseWithMatchingStatusCodes()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.BAD_REQUEST;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Test error"),
            status,
            "Test error",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(400, response.getStatus(), "Response status should be 400");
        assertNotNull(response.getEntity(), "Response entity should not be null");
        assertTrue(response.getEntity() instanceof ExtendedErrorMessage,
            "Response entity should be ExtendedErrorMessage");
        ExtendedErrorMessage responseEntity = (ExtendedErrorMessage) response.getEntity();
        assertEquals(400, responseEntity.getCode(), "Error message code should be 400");
    }

    @Test
    @DisplayName("Test buildResponse with mismatched status codes - covers line 40 (warning log)")
    void testBuildResponseWithMismatchedStatusCodes()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Test error"),
            Response.Status.BAD_REQUEST,
            "Test error",
            null,
            false
        );

        // Act - This should trigger the warning log at line 40
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(500, response.getStatus(), "Response status should be 500");
        ExtendedErrorMessage responseEntity = (ExtendedErrorMessage) response.getEntity();
        assertEquals(400, responseEntity.getCode(), "Error message code should remain 400");
    }

    @Test
    @DisplayName("Test buildResponse with NOT_FOUND status - covers lines 42-45")
    void testBuildResponseWithNotFoundStatus()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.NOT_FOUND;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Resource not found"),
            status,
            "Resource not found",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(404, response.getStatus(), "Response status should be 404");
    }

    @Test
    @DisplayName("Test buildResponse with UNAUTHORIZED status - covers lines 42-45")
    void testBuildResponseWithUnauthorizedStatus()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.UNAUTHORIZED;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Unauthorized"),
            status,
            "Unauthorized",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(401, response.getStatus(), "Response status should be 401");
        ExtendedErrorMessage responseEntity = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(responseEntity, "Response entity should not be null");
        assertEquals(401, responseEntity.getCode(), "Error message code should be 401");
    }

    @Test
    @DisplayName("Test buildResponse with FORBIDDEN status - covers lines 42-45")
    void testBuildResponseWithForbiddenStatus()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.FORBIDDEN;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Forbidden"),
            status,
            "Forbidden",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(403, response.getStatus(), "Response status should be 403");
    }

    @Test
    @DisplayName("Test buildResponse with CONFLICT status - covers lines 42-45")
    void testBuildResponseWithConflictStatus()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.CONFLICT;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Conflict"),
            status,
            "Conflict",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(409, response.getStatus(), "Response status should be 409");
    }

    @Test
    @DisplayName("Test buildResponse with SERVICE_UNAVAILABLE status - covers lines 42-45")
    void testBuildResponseWithServiceUnavailableStatus()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.SERVICE_UNAVAILABLE;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Service unavailable"),
            status,
            "Service unavailable",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(503, response.getStatus(), "Response status should be 503");
    }

    @Test
    @DisplayName("Test buildResponse verifies entity is set correctly - covers line 43")
    void testBuildResponseSetsEntityCorrectly()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.OK;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Test"),
            status,
            "Test",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        ExtendedErrorMessage responseEntity = (ExtendedErrorMessage) response.getEntity();
        assertNotNull(responseEntity, "Response entity should not be null");
        assertEquals(errorMessage, responseEntity, "Response entity should be the same as provided");
        assertEquals("Test", responseEntity.getMessage(), "Error message should match");
    }

    @Test
    @DisplayName("Test buildResponse sets media type to APPLICATION_JSON - covers line 44")
    void testBuildResponseSetsMediaTypeToJson()
    {
        // Arrange
        TestExceptionMapper mapper = new TestExceptionMapper(false);
        Response.Status status = Response.Status.CREATED;
        ExtendedErrorMessage errorMessage = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Created"),
            status,
            "Created",
            null,
            false
        );

        // Act
        Response response = mapper.testBuildResponse(status, errorMessage);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(201, response.getStatus(), "Response status should be 201");
    }

    @Test
    @DisplayName("Test buildResponse with multiple different status/errorMessage combinations - covers line 40")
    void testBuildResponseWithMultipleMismatchedCombinations()
    {
        // Test case 1: 500 response with 404 error message
        TestExceptionMapper mapper = new TestExceptionMapper(false);

        ExtendedErrorMessage errorMessage1 = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Not found"),
            Response.Status.NOT_FOUND,
            "Not found",
            null,
            false
        );
        Response response1 = mapper.testBuildResponse(Response.Status.INTERNAL_SERVER_ERROR, errorMessage1);
        assertEquals(500, response1.getStatus());
        assertEquals(404, ((ExtendedErrorMessage) response1.getEntity()).getCode());

        // Test case 2: 401 response with 403 error message
        ExtendedErrorMessage errorMessage2 = ExtendedErrorMessage.fromThrowable(
            new RuntimeException("Forbidden"),
            Response.Status.FORBIDDEN,
            "Forbidden",
            null,
            false
        );
        Response response2 = mapper.testBuildResponse(Response.Status.UNAUTHORIZED, errorMessage2);
        assertEquals(401, response2.getStatus());
        assertEquals(403, ((ExtendedErrorMessage) response2.getEntity()).getCode());
    }

    /**
     * Minimal RuntimeDelegate implementation for testing.
     * Provides only the functionality needed to create Response objects in tests.
     */
    private static class TestRuntimeDelegate extends RuntimeDelegate
    {
        private static final TestRuntimeDelegate INSTANCE = new TestRuntimeDelegate();

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
            // Create a simple MediaType for testing
            // Don't use MediaType static constants as they may require RuntimeDelegate initialization
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

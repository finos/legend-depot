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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServerConfigurationClaude_getUrlPatternTest
{
    /**
     * Reflection is necessary to test getUrlPattern() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'urlPattern' field directly and verify
     * that getUrlPattern() returns exactly what was set, without requiring full Dropwizard
     * framework initialization including YAML parsing, validation, and dependency injection setup.
     */

    private void setUrlPatternField(ServerConfiguration config, String urlPattern) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("urlPattern");
        field.setAccessible(true);
        field.set(config, urlPattern);
    }

    @Test
    @DisplayName("Test getUrlPattern returns configured value")
    void testGetUrlPatternReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String urlPattern = "/api/*";
        setUrlPatternField(config, urlPattern);

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/*", result, "URL pattern should be '/api/*'");
        assertSame(urlPattern, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getUrlPattern returns null when not set")
    void testGetUrlPatternReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, null);

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNull(result, "URL pattern should be null when not set");
    }

    @Test
    @DisplayName("Test getUrlPattern is idempotent")
    void testGetUrlPatternIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String urlPattern = "/depot/*";
        setUrlPatternField(config, urlPattern);

        // Act
        String result1 = config.getUrlPattern();
        String result2 = config.getUrlPattern();
        String result3 = config.getUrlPattern();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertEquals("/depot/*", result1);
        assertEquals("/depot/*", result2);
        assertEquals("/depot/*", result3);
    }

    @Test
    @DisplayName("Test getUrlPattern with simple pattern")
    void testGetUrlPatternWithSimplePattern() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/*", result, "URL pattern should be '/*'");
    }

    @Test
    @DisplayName("Test getUrlPattern with complex path pattern")
    void testGetUrlPatternWithComplexPattern() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api/v1/projects/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/v1/projects/*", result, "URL pattern should be '/api/v1/projects/*'");
    }

    @Test
    @DisplayName("Test getUrlPattern with pattern containing wildcards")
    void testGetUrlPatternWithWildcards() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api/*/resources/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/*/resources/*", result, "URL pattern should handle multiple wildcards");
    }

    @Test
    @DisplayName("Test getUrlPattern with different patterns maintains independence")
    void testGetUrlPatternIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        setUrlPatternField(config1, "/api/v1/*");
        setUrlPatternField(config2, "/api/v2/*");

        // Act
        String result1 = config1.getUrlPattern();
        String result2 = config2.getUrlPattern();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertEquals("/api/v1/*", result1, "Config1 should have '/api/v1/*'");
        assertEquals("/api/v2/*", result2, "Config2 should have '/api/v2/*'");
    }

    @Test
    @DisplayName("Test getUrlPattern with empty string")
    void testGetUrlPatternWithEmptyString() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("", result, "URL pattern should be empty string");
    }

    @Test
    @DisplayName("Test getUrlPattern returns exact same object reference")
    void testGetUrlPatternReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String urlPattern = "/depot/api/*";
        setUrlPatternField(config, urlPattern);

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertSame(urlPattern, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getUrlPattern with pattern without leading slash")
    void testGetUrlPatternWithoutLeadingSlash() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "api/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("api/*", result, "URL pattern should preserve format without leading slash");
    }

    @Test
    @DisplayName("Test getUrlPattern with pattern containing special characters")
    void testGetUrlPatternWithSpecialCharacters() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api-v1/projects_*/resources-*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api-v1/projects_*/resources-*", result, "URL pattern should handle special characters");
    }

    @Test
    @DisplayName("Test getUrlPattern with pattern containing dots")
    void testGetUrlPatternWithDots() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api/v1.0/projects/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/v1.0/projects/*", result, "URL pattern should handle dots");
    }

    @Test
    @DisplayName("Test getUrlPattern with long pattern")
    void testGetUrlPatternWithLongPattern() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String longPattern = "/api/v1/legend/depot/artifact/repository/management/projects/versions/entities/*";
        setUrlPatternField(config, longPattern);

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals(longPattern, result, "URL pattern should handle long patterns");
    }

    @Test
    @DisplayName("Test getUrlPattern with pattern using curly braces")
    void testGetUrlPatternWithCurlyBraces() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api/{version}/projects/{id}/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/{version}/projects/{id}/*", result, "URL pattern should handle path parameters");
    }

    @Test
    @DisplayName("Test getUrlPattern with regex-style pattern")
    void testGetUrlPatternWithRegexPattern() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api/[0-9]+/projects/*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/[0-9]+/projects/*", result, "URL pattern should handle regex-style patterns");
    }

    @Test
    @DisplayName("Test getUrlPattern with query parameter pattern")
    void testGetUrlPatternWithQueryParameters() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setUrlPatternField(config, "/api/projects?filter=*");

        // Act
        String result = config.getUrlPattern();

        // Assert
        assertNotNull(result, "URL pattern should not be null");
        assertEquals("/api/projects?filter=*", result, "URL pattern should handle query parameters");
    }
}

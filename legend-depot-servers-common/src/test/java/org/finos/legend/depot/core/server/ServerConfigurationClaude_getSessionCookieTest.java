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

class ServerConfigurationClaude_getSessionCookieTest
{
    /**
     * Reflection is necessary to test getSessionCookie() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'sessionCookie' field directly and verify
     * that getSessionCookie() returns exactly what was set, without requiring full Dropwizard
     * framework initialization including YAML parsing, validation, and dependency injection setup.
     */

    private void setSessionCookieField(ServerConfiguration config, String sessionCookie) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("sessionCookie");
        field.setAccessible(true);
        field.set(config, sessionCookie);
    }

    @Test
    @DisplayName("Test getSessionCookie returns configured value")
    void testGetSessionCookieReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String cookieName = "DEPOT_SESSION_ID";
        setSessionCookieField(config, cookieName);

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("DEPOT_SESSION_ID", result, "Session cookie should be 'DEPOT_SESSION_ID'");
        assertSame(cookieName, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getSessionCookie returns null when not set")
    void testGetSessionCookieReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, null);

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNull(result, "Session cookie should be null when not set");
    }

    @Test
    @DisplayName("Test getSessionCookie is idempotent")
    void testGetSessionCookieIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String cookieName = "MY_SESSION";
        setSessionCookieField(config, cookieName);

        // Act
        String result1 = config.getSessionCookie();
        String result2 = config.getSessionCookie();
        String result3 = config.getSessionCookie();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertEquals("MY_SESSION", result1);
        assertEquals("MY_SESSION", result2);
        assertEquals("MY_SESSION", result3);
    }

    @Test
    @DisplayName("Test getSessionCookie with standard JSESSIONID")
    void testGetSessionCookieWithStandardJSESSIONID() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "JSESSIONID");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("JSESSIONID", result, "Session cookie should be 'JSESSIONID'");
    }

    @Test
    @DisplayName("Test getSessionCookie with custom cookie name")
    void testGetSessionCookieWithCustomCookieName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "CUSTOM_DEPOT_SESSION");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("CUSTOM_DEPOT_SESSION", result, "Session cookie should be 'CUSTOM_DEPOT_SESSION'");
    }

    @Test
    @DisplayName("Test getSessionCookie with cookie name containing numbers")
    void testGetSessionCookieWithNumbers() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "SESSION123");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("SESSION123", result, "Session cookie should be 'SESSION123'");
    }

    @Test
    @DisplayName("Test getSessionCookie with cookie name containing special characters")
    void testGetSessionCookieWithSpecialCharacters() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "SESSION_ID-V2");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("SESSION_ID-V2", result, "Session cookie should handle special characters");
    }

    @Test
    @DisplayName("Test getSessionCookie with different cookie names maintains independence")
    void testGetSessionCookieIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        setSessionCookieField(config1, "DEPOT_SESSION_1");
        setSessionCookieField(config2, "DEPOT_SESSION_2");

        // Act
        String result1 = config1.getSessionCookie();
        String result2 = config2.getSessionCookie();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertEquals("DEPOT_SESSION_1", result1, "Config1 should have 'DEPOT_SESSION_1'");
        assertEquals("DEPOT_SESSION_2", result2, "Config2 should have 'DEPOT_SESSION_2'");
    }

    @Test
    @DisplayName("Test getSessionCookie with empty string")
    void testGetSessionCookieWithEmptyString() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("", result, "Session cookie should be empty string");
    }

    @Test
    @DisplayName("Test getSessionCookie returns exact same object reference")
    void testGetSessionCookieReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String cookieName = "DEPOT_COOKIE";
        setSessionCookieField(config, cookieName);

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertSame(cookieName, result, "Should return exactly the same object reference");
    }

    @Test
    @DisplayName("Test getSessionCookie with lowercase cookie name")
    void testGetSessionCookieWithLowercaseName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "depot_session_id");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("depot_session_id", result, "Session cookie should preserve case");
    }

    @Test
    @DisplayName("Test getSessionCookie with mixed case cookie name")
    void testGetSessionCookieWithMixedCaseName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setSessionCookieField(config, "DepotSessionId");

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals("DepotSessionId", result, "Session cookie should preserve case");
    }

    @Test
    @DisplayName("Test getSessionCookie with long cookie name")
    void testGetSessionCookieWithLongName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        String longName = "VERY_LONG_SESSION_COOKIE_NAME_FOR_DEPOT_APPLICATION_SERVER";
        setSessionCookieField(config, longName);

        // Act
        String result = config.getSessionCookie();

        // Assert
        assertNotNull(result, "Session cookie should not be null");
        assertEquals(longName, result, "Session cookie should handle long names");
    }
}

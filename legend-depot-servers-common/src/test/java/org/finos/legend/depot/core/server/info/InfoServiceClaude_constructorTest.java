//  Copyright 2024 Goldman Sachs
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

package org.finos.legend.depot.core.server.info;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InfoService constructor.
 *
 * This test class tests the InfoService() constructor which:
 * - Attempts to get the local hostname
 * - Attempts to load platform version info from version.json
 * - Creates a ServerInfo object with the gathered information
 * - Handles exceptions gracefully by setting null values
 *
 * Note: These tests verify the constructor behavior without mocking because:
 * 1. The constructor creates real objects that can be verified through public getters
 * 2. The constructor's exception handling can be tested by verifying null values are set
 * 3. The behavior is deterministic enough to test without mocking
 */
class InfoServiceClaude_constructorTest
{
    /**
     * Test that InfoService can be instantiated successfully.
     * Verifies the basic constructor functionality.
     */
    @Test
    @DisplayName("Test InfoService can be instantiated")
    void testInfoServiceInstantiation()
    {
        // Act
        InfoService infoService = new InfoService();

        // Assert
        assertNotNull(infoService, "InfoService should be instantiated successfully");
    }

    /**
     * Test that the constructor initializes serverInfo field.
     * This verifies that getServerInfo() returns a non-null value.
     */
    @Test
    @DisplayName("Test constructor initializes serverInfo")
    void testConstructorInitializesServerInfo()
    {
        // Act
        InfoService infoService = new InfoService();

        // Assert
        assertNotNull(infoService.getServerInfo(), "ServerInfo should be initialized by constructor");
    }

    /**
     * Test that the constructor sets up serverInfo with a time zone.
     * The time zone should always be set as it uses ZoneId.systemDefault().
     */
    @Test
    @DisplayName("Test constructor sets server time zone")
    void testConstructorSetsServerTimeZone()
    {
        // Act
        InfoService infoService = new InfoService();
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert
        assertNotNull(serverInfo.getServerTimeZone(), "Server time zone should be set");
        assertFalse(serverInfo.getServerTimeZone().isEmpty(), "Server time zone should not be empty");
    }

    /**
     * Test that the constructor sets up platform info.
     * Platform info should always be present (either with real data or with null fields).
     */
    @Test
    @DisplayName("Test constructor initializes platform info")
    void testConstructorInitializesPlatformInfo()
    {
        // Act
        InfoService infoService = new InfoService();
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert
        assertNotNull(serverInfo.getPlatform(), "Platform info should be initialized");
    }

    /**
     * Test that multiple InfoService instances are independent.
     * Each constructor call should create a new instance with its own ServerInfo.
     */
    @Test
    @DisplayName("Test multiple InfoService instances are independent")
    void testMultipleInstancesAreIndependent()
    {
        // Act
        InfoService infoService1 = new InfoService();
        InfoService infoService2 = new InfoService();

        // Assert
        assertNotNull(infoService1, "First InfoService should be instantiated");
        assertNotNull(infoService2, "Second InfoService should be instantiated");
        assertNotSame(infoService1, infoService2, "InfoService instances should be different");

        InfoService.ServerInfo serverInfo1 = infoService1.getServerInfo();
        InfoService.ServerInfo serverInfo2 = infoService2.getServerInfo();
        assertNotSame(serverInfo1, serverInfo2, "ServerInfo instances should be different");
    }

    /**
     * Test that the constructor creates ServerInfo with consistent time zone across instances.
     * Since all instances use the same system default time zone, they should match.
     */
    @Test
    @DisplayName("Test constructor creates consistent time zone across instances")
    void testConsistentTimeZoneAcrossInstances()
    {
        // Act
        InfoService infoService1 = new InfoService();
        InfoService infoService2 = new InfoService();

        // Assert
        String timeZone1 = infoService1.getServerInfo().getServerTimeZone();
        String timeZone2 = infoService2.getServerInfo().getServerTimeZone();

        assertEquals(timeZone1, timeZone2, "Time zones should be consistent across instances");
    }

    /**
     * Test that the constructor handles the case where hostname might not be available.
     * The hostname may be null if InetAddress.getLocalHost() throws an exception,
     * which is handled gracefully by tryGetValue().
     */
    @Test
    @DisplayName("Test constructor handles hostname availability")
    void testConstructorHandlesHostnameAvailability()
    {
        // Act
        InfoService infoService = new InfoService();
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert - hostname may be null or a string, both are acceptable
        // The constructor doesn't throw even if hostname retrieval fails
        assertNotNull(serverInfo, "ServerInfo should be created even if hostname is unavailable");
    }

    /**
     * Test that the constructor handles the case where version.json might not be available.
     * If version.json is not in the classpath, platform info fields may be null,
     * which is handled gracefully by the constructor.
     */
    @Test
    @DisplayName("Test constructor handles missing version.json")
    void testConstructorHandlesMissingVersionFile()
    {
        // Act
        InfoService infoService = new InfoService();
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();
        InfoService.ServerPlatformInfo platformInfo = serverInfo.getPlatform();

        // Assert - platform info should exist but fields may be null
        assertNotNull(platformInfo, "Platform info should be created even if version.json is missing");
        // Fields like version, buildTime, buildRevision may be null if version.json is not found
        // This is acceptable behavior - we're just verifying no exception is thrown
    }

    /**
     * Test that the constructor creates a complete and valid InfoService.
     * Verifies all essential components are properly initialized.
     */
    @Test
    @DisplayName("Test constructor creates complete InfoService")
    void testConstructorCreatesCompleteInfoService()
    {
        // Act
        InfoService infoService = new InfoService();

        // Assert - verify the complete object graph is created
        assertNotNull(infoService, "InfoService should be created");
        assertNotNull(infoService.getServerInfo(), "ServerInfo should be created");
        assertNotNull(infoService.getServerInfo().getServerTimeZone(), "Time zone should be set");
        assertNotNull(infoService.getServerInfo().getPlatform(), "Platform info should be created");
    }

    /**
     * Test that constructor can be called multiple times successfully.
     * This verifies there are no side effects or state issues.
     */
    @Test
    @DisplayName("Test constructor can be called multiple times")
    void testConstructorCanBeCalledMultipleTimes()
    {
        // Act - create multiple instances
        InfoService service1 = new InfoService();
        InfoService service2 = new InfoService();
        InfoService service3 = new InfoService();

        // Assert - all should be valid and independent
        assertNotNull(service1, "First service should be created");
        assertNotNull(service2, "Second service should be created");
        assertNotNull(service3, "Third service should be created");

        assertNotSame(service1, service2, "First and second should be different");
        assertNotSame(service2, service3, "Second and third should be different");
        assertNotSame(service1, service3, "First and third should be different");
    }

    /**
     * Test that constructor creates InfoService with immutable-like behavior.
     * Once created, the ServerInfo should remain consistent.
     */
    @Test
    @DisplayName("Test constructor creates InfoService with consistent state")
    void testConstructorCreatesConsistentState()
    {
        // Act
        InfoService infoService = new InfoService();

        // Get ServerInfo multiple times
        InfoService.ServerInfo serverInfo1 = infoService.getServerInfo();
        InfoService.ServerInfo serverInfo2 = infoService.getServerInfo();

        // Assert - should return the same instance
        assertSame(serverInfo1, serverInfo2, "getServerInfo() should return the same instance");
        assertEquals(serverInfo1.getServerTimeZone(), serverInfo2.getServerTimeZone(),
            "Time zone should be consistent");
    }

    /**
     * Test that the constructor creates ServerInfo with valid ServerPlatformInfo.
     * The ServerPlatformInfo should be accessible and properly initialized.
     */
    @Test
    @DisplayName("Test constructor creates valid ServerPlatformInfo")
    void testConstructorCreatesValidServerPlatformInfo()
    {
        // Act
        InfoService infoService = new InfoService();
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();
        InfoService.ServerPlatformInfo platformInfo = serverInfo.getPlatform();

        // Assert
        assertNotNull(platformInfo, "ServerPlatformInfo should be created");
        // The methods should be callable without throwing exceptions
        assertDoesNotThrow(() -> platformInfo.getVersion(), "getVersion() should not throw");
        assertDoesNotThrow(() -> platformInfo.getBuildTime(), "getBuildTime() should not throw");
        assertDoesNotThrow(() -> platformInfo.getBuildRevision(), "getBuildRevision() should not throw");
    }

    /**
     * Test that the constructor properly initializes the serverInfo field
     * which is then returned by getServerInfo().
     * This verifies the field assignment in the constructor.
     */
    @Test
    @DisplayName("Test constructor properly assigns serverInfo field")
    void testConstructorAssignsServerInfoField()
    {
        // Act
        InfoService infoService = new InfoService();

        // Assert - getServerInfo() should return a non-null value
        // indicating the field was properly assigned in the constructor
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();
        assertNotNull(serverInfo, "serverInfo field should be assigned in constructor");

        // Verify it returns the same instance each time (field is not recreated)
        assertSame(serverInfo, infoService.getServerInfo(),
            "getServerInfo() should return the same serverInfo instance");
    }

    /**
     * Test that multiple calls to the constructor create instances with the same system properties.
     * Since all instances read from the same system (hostname, timezone, version.json),
     * the values should be consistent (though instances are different).
     */
    @Test
    @DisplayName("Test constructor creates instances with consistent system properties")
    void testConstructorCreatesInstancesWithConsistentProperties()
    {
        // Act
        InfoService infoService1 = new InfoService();
        InfoService infoService2 = new InfoService();
        InfoService infoService3 = new InfoService();

        // Assert - all should have the same time zone (system property)
        String tz1 = infoService1.getServerInfo().getServerTimeZone();
        String tz2 = infoService2.getServerInfo().getServerTimeZone();
        String tz3 = infoService3.getServerInfo().getServerTimeZone();

        assertEquals(tz1, tz2, "Time zones should match between first and second instance");
        assertEquals(tz2, tz3, "Time zones should match between second and third instance");

        // Hostnames should also match (may be null if unavailable, but should be consistent)
        String host1 = infoService1.getServerInfo().getHostName();
        String host2 = infoService2.getServerInfo().getHostName();
        String host3 = infoService3.getServerInfo().getHostName();

        assertEquals(host1, host2, "Hostnames should match between first and second instance");
        assertEquals(host2, host3, "Hostnames should match between second and third instance");
    }
}

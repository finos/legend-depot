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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InfoService.getServerInfo() method.
 *
 * This test class tests the getServerInfo() method which:
 * - Returns the ServerInfo instance that was initialized in the constructor
 * - Provides access to server information including hostname, platform info, and time zone
 *
 * Note: These tests verify the getter behavior without mocking because:
 * 1. The method is a simple getter that returns a final field
 * 2. The behavior can be fully verified through the public API
 * 3. The method has no side effects or complex logic requiring mocking
 */
class InfoServiceClaude_getServerInfoTest
{
    private InfoService infoService;

    @BeforeEach
    void setUp()
    {
        infoService = new InfoService();
    }

    /**
     * Test that getServerInfo() returns a non-null value.
     * The method should always return a valid ServerInfo instance.
     */
    @Test
    @DisplayName("Test getServerInfo returns non-null")
    void testGetServerInfoReturnsNonNull()
    {
        // Act
        InfoService.ServerInfo result = infoService.getServerInfo();

        // Assert
        assertNotNull(result, "getServerInfo() should return a non-null ServerInfo");
    }

    /**
     * Test that getServerInfo() returns the same instance on multiple calls.
     * This verifies the method returns the field without creating new instances.
     */
    @Test
    @DisplayName("Test getServerInfo returns same instance")
    void testGetServerInfoReturnsSameInstance()
    {
        // Act
        InfoService.ServerInfo first = infoService.getServerInfo();
        InfoService.ServerInfo second = infoService.getServerInfo();
        InfoService.ServerInfo third = infoService.getServerInfo();

        // Assert
        assertSame(first, second, "First and second calls should return the same instance");
        assertSame(second, third, "Second and third calls should return the same instance");
        assertSame(first, third, "First and third calls should return the same instance");
    }

    /**
     * Test that getServerInfo() returns a ServerInfo with a valid time zone.
     * The ServerInfo should always have a time zone set.
     */
    @Test
    @DisplayName("Test getServerInfo returns ServerInfo with time zone")
    void testGetServerInfoReturnsServerInfoWithTimeZone()
    {
        // Act
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert
        assertNotNull(serverInfo.getServerTimeZone(), "ServerInfo should have a time zone");
        assertFalse(serverInfo.getServerTimeZone().isEmpty(), "Time zone should not be empty");
    }

    /**
     * Test that getServerInfo() returns a ServerInfo with platform info.
     * The ServerInfo should always have platform info initialized.
     */
    @Test
    @DisplayName("Test getServerInfo returns ServerInfo with platform info")
    void testGetServerInfoReturnsServerInfoWithPlatformInfo()
    {
        // Act
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert
        assertNotNull(serverInfo.getPlatform(), "ServerInfo should have platform info");
    }

    /**
     * Test that getServerInfo() returns a fully initialized ServerInfo.
     * All getter methods should be callable without throwing exceptions.
     */
    @Test
    @DisplayName("Test getServerInfo returns fully initialized ServerInfo")
    void testGetServerInfoReturnsFullyInitializedServerInfo()
    {
        // Act
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert - all getters should be callable
        assertDoesNotThrow(() -> serverInfo.getHostName(), "getHostName() should not throw");
        assertDoesNotThrow(() -> serverInfo.getServerTimeZone(), "getServerTimeZone() should not throw");
        assertDoesNotThrow(() -> serverInfo.getPlatform(), "getPlatform() should not throw");
    }

    /**
     * Test that getServerInfo() returns consistent data across multiple calls.
     * The data in the returned ServerInfo should remain consistent.
     */
    @Test
    @DisplayName("Test getServerInfo returns consistent data")
    void testGetServerInfoReturnsConsistentData()
    {
        // Act
        InfoService.ServerInfo first = infoService.getServerInfo();
        InfoService.ServerInfo second = infoService.getServerInfo();

        // Assert - data should be consistent
        assertEquals(first.getHostName(), second.getHostName(), "Host names should match");
        assertEquals(first.getServerTimeZone(), second.getServerTimeZone(), "Time zones should match");
        assertSame(first.getPlatform(), second.getPlatform(), "Platform info should be the same instance");
    }

    /**
     * Test that getServerInfo() can be called immediately after construction.
     * There should be no delay or lazy initialization.
     */
    @Test
    @DisplayName("Test getServerInfo works immediately after construction")
    void testGetServerInfoWorksImmediatelyAfterConstruction()
    {
        // Act - create new instance and immediately call getServerInfo
        InfoService newService = new InfoService();
        InfoService.ServerInfo serverInfo = newService.getServerInfo();

        // Assert
        assertNotNull(serverInfo, "getServerInfo() should work immediately after construction");
        assertNotNull(serverInfo.getServerTimeZone(), "Time zone should be set immediately");
        assertNotNull(serverInfo.getPlatform(), "Platform should be set immediately");
    }

    /**
     * Test that different InfoService instances return different ServerInfo instances.
     * Each InfoService should have its own ServerInfo.
     */
    @Test
    @DisplayName("Test different InfoService instances return different ServerInfo")
    void testDifferentInfoServiceInstancesReturnDifferentServerInfo()
    {
        // Arrange
        InfoService service1 = new InfoService();
        InfoService service2 = new InfoService();

        // Act
        InfoService.ServerInfo info1 = service1.getServerInfo();
        InfoService.ServerInfo info2 = service2.getServerInfo();

        // Assert
        assertNotSame(info1, info2, "Different InfoService instances should have different ServerInfo");
    }

    /**
     * Test that getServerInfo() returns ServerInfo with accessible hostname.
     * The hostname getter should be callable (may return null if hostname unavailable).
     */
    @Test
    @DisplayName("Test getServerInfo returns ServerInfo with accessible hostname")
    void testGetServerInfoReturnsServerInfoWithAccessibleHostname()
    {
        // Act
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();
        String hostName = serverInfo.getHostName();

        // Assert - hostname may be null if unavailable, but getter should work
        assertDoesNotThrow(() -> serverInfo.getHostName(), "getHostName() should not throw");
        // If hostname is not null, it should be a non-empty string
        if (hostName != null)
        {
            assertFalse(hostName.isEmpty(), "If hostname is set, it should not be empty");
        }
    }

    /**
     * Test that getServerInfo() returns ServerInfo with accessible platform version info.
     * All platform info getters should be callable.
     */
    @Test
    @DisplayName("Test getServerInfo returns ServerInfo with accessible platform info")
    void testGetServerInfoReturnsServerInfoWithAccessiblePlatformInfo()
    {
        // Act
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();
        InfoService.ServerPlatformInfo platformInfo = serverInfo.getPlatform();

        // Assert - all platform getters should be callable
        assertDoesNotThrow(() -> platformInfo.getVersion(), "getVersion() should not throw");
        assertDoesNotThrow(() -> platformInfo.getBuildTime(), "getBuildTime() should not throw");
        assertDoesNotThrow(() -> platformInfo.getBuildRevision(), "getBuildRevision() should not throw");
    }

    /**
     * Test that getServerInfo() maintains consistency even with multiple InfoService instances.
     * While each InfoService has its own ServerInfo, they should reflect the same system state.
     */
    @Test
    @DisplayName("Test getServerInfo maintains consistency across instances")
    void testGetServerInfoMaintainsConsistencyAcrossInstances()
    {
        // Arrange
        InfoService service1 = new InfoService();
        InfoService service2 = new InfoService();
        InfoService service3 = new InfoService();

        // Act
        InfoService.ServerInfo info1 = service1.getServerInfo();
        InfoService.ServerInfo info2 = service2.getServerInfo();
        InfoService.ServerInfo info3 = service3.getServerInfo();

        // Assert - time zones should match (same system property)
        assertEquals(info1.getServerTimeZone(), info2.getServerTimeZone(),
            "Time zones should match between service1 and service2");
        assertEquals(info2.getServerTimeZone(), info3.getServerTimeZone(),
            "Time zones should match between service2 and service3");

        // Assert - hostnames should match (same system property)
        assertEquals(info1.getHostName(), info2.getHostName(),
            "Hostnames should match between service1 and service2");
        assertEquals(info2.getHostName(), info3.getHostName(),
            "Hostnames should match between service2 and service3");
    }

    /**
     * Test that getServerInfo() does not return null even if initialization had issues.
     * The method should always return a valid ServerInfo object.
     */
    @Test
    @DisplayName("Test getServerInfo never returns null")
    void testGetServerInfoNeverReturnsNull()
    {
        // Act - call multiple times to ensure consistency
        for (int i = 0; i < 10; i++)
        {
            InfoService.ServerInfo serverInfo = infoService.getServerInfo();

            // Assert
            assertNotNull(serverInfo, "getServerInfo() should never return null (iteration " + i + ")");
        }
    }

    /**
     * Test that getServerInfo() returns ServerInfo that provides stable data.
     * The data should not change between accesses.
     */
    @Test
    @DisplayName("Test getServerInfo returns ServerInfo with stable data")
    void testGetServerInfoReturnsServerInfoWithStableData()
    {
        // Act - get the same ServerInfo multiple times
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        String tz1 = serverInfo.getServerTimeZone();
        String tz2 = serverInfo.getServerTimeZone();
        String tz3 = serverInfo.getServerTimeZone();

        String host1 = serverInfo.getHostName();
        String host2 = serverInfo.getHostName();
        String host3 = serverInfo.getHostName();

        InfoService.ServerPlatformInfo platform1 = serverInfo.getPlatform();
        InfoService.ServerPlatformInfo platform2 = serverInfo.getPlatform();

        // Assert - data should remain stable
        assertEquals(tz1, tz2, "Time zone should be stable (call 1 vs 2)");
        assertEquals(tz2, tz3, "Time zone should be stable (call 2 vs 3)");

        assertEquals(host1, host2, "Hostname should be stable (call 1 vs 2)");
        assertEquals(host2, host3, "Hostname should be stable (call 2 vs 3)");

        assertSame(platform1, platform2, "Platform should return same instance");
    }

    /**
     * Test that getServerInfo() provides access to complete server information.
     * This is an integration-style test verifying the complete API surface.
     */
    @Test
    @DisplayName("Test getServerInfo provides complete server information")
    void testGetServerInfoProvidesCompleteServerInformation()
    {
        // Act
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();

        // Assert - verify we can access all parts of the API
        assertNotNull(serverInfo, "ServerInfo should exist");

        // Time zone is always available
        assertNotNull(serverInfo.getServerTimeZone(), "Time zone should be available");

        // Platform info is always available (though fields may be null)
        assertNotNull(serverInfo.getPlatform(), "Platform info should be available");
        InfoService.ServerPlatformInfo platform = serverInfo.getPlatform();

        // All platform getters should be callable
        assertDoesNotThrow(() -> {
            platform.getVersion();
            platform.getBuildTime();
            platform.getBuildRevision();
        }, "All platform getters should be callable");
    }

    /**
     * Test that getServerInfo() behaves correctly when called in rapid succession.
     * This tests for any potential concurrency or state issues.
     */
    @Test
    @DisplayName("Test getServerInfo handles rapid successive calls")
    void testGetServerInfoHandlesRapidSuccessiveCalls()
    {
        // Act - call getServerInfo many times in rapid succession
        InfoService.ServerInfo first = infoService.getServerInfo();

        for (int i = 0; i < 100; i++)
        {
            InfoService.ServerInfo current = infoService.getServerInfo();

            // Assert - should always return the same instance
            assertSame(first, current, "Rapid call " + i + " should return the same instance");
        }
    }
}

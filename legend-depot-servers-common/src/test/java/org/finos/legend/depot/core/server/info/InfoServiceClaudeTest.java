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
 * Tests for InfoService.ServerPlatformInfo methods.
 *
 * This test class focuses on the ServerPlatformInfo inner class and its getter methods:
 * - getVersion(): Returns the platform version string (may be null)
 * - getBuildTime(): Returns the build time string (may be null)
 * - getBuildRevision(): Returns the build revision/commit ID string (may be null)
 *
 * Note: Testing without reflection because:
 * 1. ServerPlatformInfo has private constructors but is accessible through InfoService's public API
 * 2. We can test all three getter methods through the InfoService.getServerInfo().getPlatform() chain
 * 3. The methods are simple getters returning final immutable fields
 * 4. Testing through InfoService provides realistic integration testing
 * 5. The class reads from version.json resource which may or may not exist in test environment
 */
class InfoServiceClaudeTest
{
    private InfoService.ServerPlatformInfo platformInfo;

    @BeforeEach
    void setUp()
    {
        InfoService infoService = new InfoService();
        platformInfo = infoService.getServerInfo().getPlatform();
    }

    // Tests for getVersion()

    /**
     * Test that getVersion() does not throw exceptions.
     * The method should be callable regardless of whether version data is available.
     */
    @Test
    @DisplayName("Test getVersion does not throw exceptions")
    void testGetVersionDoesNotThrow()
    {
        assertDoesNotThrow(() -> platformInfo.getVersion(),
            "getVersion() should not throw exceptions");
    }

    /**
     * Test that getVersion() returns consistent values.
     * Multiple calls should return the same value (either null or the same string).
     */
    @Test
    @DisplayName("Test getVersion returns consistent values")
    void testGetVersionReturnsConsistentValues()
    {
        String first = platformInfo.getVersion();
        String second = platformInfo.getVersion();
        String third = platformInfo.getVersion();

        assertEquals(first, second, "First and second calls should return same value");
        assertEquals(second, third, "Second and third calls should return same value");
    }

    /**
     * Test that getVersion() returns non-empty string when not null.
     * If version is not null, it should be a meaningful value.
     */
    @Test
    @DisplayName("Test getVersion returns non-empty string when not null")
    void testGetVersionReturnsNonEmptyWhenNotNull()
    {
        String version = platformInfo.getVersion();

        if (version != null)
        {
            assertFalse(version.isEmpty(), "Version should not be empty string when not null");
        }
    }

    /**
     * Test that getVersion() is immutable.
     * Multiple calls should return the exact same reference or equal strings.
     */
    @Test
    @DisplayName("Test getVersion returns immutable value")
    void testGetVersionReturnsImmutableValue()
    {
        String version1 = platformInfo.getVersion();
        String version2 = platformInfo.getVersion();

        if (version1 != null && version2 != null)
        {
            assertTrue(version1 == version2 || version1.equals(version2),
                "Version should be immutable - same reference or equal value");
        }
        else
        {
            assertEquals(version1, version2, "Both should be null");
        }
    }

    /**
     * Test that getVersion() consistency across multiple ServerPlatformInfo instances.
     * All instances created around the same time should report the same version.
     */
    @Test
    @DisplayName("Test getVersion consistent across instances")
    void testGetVersionConsistentAcrossInstances()
    {
        InfoService service1 = new InfoService();
        InfoService service2 = new InfoService();

        String version1 = service1.getServerInfo().getPlatform().getVersion();
        String version2 = service2.getServerInfo().getPlatform().getVersion();

        assertEquals(version1, version2, "Version should match across different instances");
    }

    // Tests for getBuildTime()

    /**
     * Test that getBuildTime() does not throw exceptions.
     * The method should be callable regardless of whether build time data is available.
     */
    @Test
    @DisplayName("Test getBuildTime does not throw exceptions")
    void testGetBuildTimeDoesNotThrow()
    {
        assertDoesNotThrow(() -> platformInfo.getBuildTime(),
            "getBuildTime() should not throw exceptions");
    }

    /**
     * Test that getBuildTime() returns consistent values.
     * Multiple calls should return the same value (either null or the same string).
     */
    @Test
    @DisplayName("Test getBuildTime returns consistent values")
    void testGetBuildTimeReturnsConsistentValues()
    {
        String first = platformInfo.getBuildTime();
        String second = platformInfo.getBuildTime();
        String third = platformInfo.getBuildTime();

        assertEquals(first, second, "First and second calls should return same value");
        assertEquals(second, third, "Second and third calls should return same value");
    }

    /**
     * Test that getBuildTime() returns non-empty string when not null.
     * If build time is not null, it should be a meaningful value.
     */
    @Test
    @DisplayName("Test getBuildTime returns non-empty string when not null")
    void testGetBuildTimeReturnsNonEmptyWhenNotNull()
    {
        String buildTime = platformInfo.getBuildTime();

        if (buildTime != null)
        {
            assertFalse(buildTime.isEmpty(), "Build time should not be empty string when not null");
        }
    }

    /**
     * Test that getBuildTime() is immutable.
     * Multiple calls should return the exact same reference or equal strings.
     */
    @Test
    @DisplayName("Test getBuildTime returns immutable value")
    void testGetBuildTimeReturnsImmutableValue()
    {
        String buildTime1 = platformInfo.getBuildTime();
        String buildTime2 = platformInfo.getBuildTime();

        if (buildTime1 != null && buildTime2 != null)
        {
            assertTrue(buildTime1 == buildTime2 || buildTime1.equals(buildTime2),
                "Build time should be immutable - same reference or equal value");
        }
        else
        {
            assertEquals(buildTime1, buildTime2, "Both should be null");
        }
    }

    /**
     * Test that getBuildTime() consistency across multiple ServerPlatformInfo instances.
     * All instances created around the same time should report the same build time.
     */
    @Test
    @DisplayName("Test getBuildTime consistent across instances")
    void testGetBuildTimeConsistentAcrossInstances()
    {
        InfoService service1 = new InfoService();
        InfoService service2 = new InfoService();

        String buildTime1 = service1.getServerInfo().getPlatform().getBuildTime();
        String buildTime2 = service2.getServerInfo().getPlatform().getBuildTime();

        assertEquals(buildTime1, buildTime2, "Build time should match across different instances");
    }

    // Tests for getBuildRevision()

    /**
     * Test that getBuildRevision() does not throw exceptions.
     * The method should be callable regardless of whether revision data is available.
     */
    @Test
    @DisplayName("Test getBuildRevision does not throw exceptions")
    void testGetBuildRevisionDoesNotThrow()
    {
        assertDoesNotThrow(() -> platformInfo.getBuildRevision(),
            "getBuildRevision() should not throw exceptions");
    }

    /**
     * Test that getBuildRevision() returns consistent values.
     * Multiple calls should return the same value (either null or the same string).
     */
    @Test
    @DisplayName("Test getBuildRevision returns consistent values")
    void testGetBuildRevisionReturnsConsistentValues()
    {
        String first = platformInfo.getBuildRevision();
        String second = platformInfo.getBuildRevision();
        String third = platformInfo.getBuildRevision();

        assertEquals(first, second, "First and second calls should return same value");
        assertEquals(second, third, "Second and third calls should return same value");
    }

    /**
     * Test that getBuildRevision() returns non-empty string when not null.
     * If build revision is not null, it should be a meaningful value.
     */
    @Test
    @DisplayName("Test getBuildRevision returns non-empty string when not null")
    void testGetBuildRevisionReturnsNonEmptyWhenNotNull()
    {
        String buildRevision = platformInfo.getBuildRevision();

        if (buildRevision != null)
        {
            assertFalse(buildRevision.isEmpty(), "Build revision should not be empty string when not null");
        }
    }

    /**
     * Test that getBuildRevision() is immutable.
     * Multiple calls should return the exact same reference or equal strings.
     */
    @Test
    @DisplayName("Test getBuildRevision returns immutable value")
    void testGetBuildRevisionReturnsImmutableValue()
    {
        String revision1 = platformInfo.getBuildRevision();
        String revision2 = platformInfo.getBuildRevision();

        if (revision1 != null && revision2 != null)
        {
            assertTrue(revision1 == revision2 || revision1.equals(revision2),
                "Build revision should be immutable - same reference or equal value");
        }
        else
        {
            assertEquals(revision1, revision2, "Both should be null");
        }
    }

    /**
     * Test that getBuildRevision() consistency across multiple ServerPlatformInfo instances.
     * All instances created around the same time should report the same build revision.
     */
    @Test
    @DisplayName("Test getBuildRevision consistent across instances")
    void testGetBuildRevisionConsistentAcrossInstances()
    {
        InfoService service1 = new InfoService();
        InfoService service2 = new InfoService();

        String revision1 = service1.getServerInfo().getPlatform().getBuildRevision();
        String revision2 = service2.getServerInfo().getPlatform().getBuildRevision();

        assertEquals(revision1, revision2, "Build revision should match across different instances");
    }

    // Integration tests for all ServerPlatformInfo getter methods

    /**
     * Test that all ServerPlatformInfo getter methods work together consistently.
     * This is an integration test verifying the complete API surface of ServerPlatformInfo.
     */
    @Test
    @DisplayName("Test all ServerPlatformInfo getters work together")
    void testAllGettersWorkTogether()
    {
        assertDoesNotThrow(() -> {
            String version = platformInfo.getVersion();
            String buildTime = platformInfo.getBuildTime();
            String buildRevision = platformInfo.getBuildRevision();

            // All three getters should be callable without exceptions
            // Their return values may be null or non-null depending on version.json availability
        }, "All ServerPlatformInfo getter methods should work together without exceptions");
    }

    /**
     * Test ServerPlatformInfo data integrity across multiple accesses.
     * Data should remain consistent when accessed multiple times.
     */
    @Test
    @DisplayName("Test ServerPlatformInfo data integrity across multiple accesses")
    void testDataIntegrityAcrossMultipleAccesses()
    {
        // First access
        String version1 = platformInfo.getVersion();
        String buildTime1 = platformInfo.getBuildTime();
        String revision1 = platformInfo.getBuildRevision();

        // Second access
        String version2 = platformInfo.getVersion();
        String buildTime2 = platformInfo.getBuildTime();
        String revision2 = platformInfo.getBuildRevision();

        // Third access
        String version3 = platformInfo.getVersion();
        String buildTime3 = platformInfo.getBuildTime();
        String revision3 = platformInfo.getBuildRevision();

        // All values should be consistent
        assertEquals(version1, version2, "Version should be consistent between first and second access");
        assertEquals(version2, version3, "Version should be consistent between second and third access");

        assertEquals(buildTime1, buildTime2, "Build time should be consistent between first and second access");
        assertEquals(buildTime2, buildTime3, "Build time should be consistent between second and third access");

        assertEquals(revision1, revision2, "Build revision should be consistent between first and second access");
        assertEquals(revision2, revision3, "Build revision should be consistent between second and third access");
    }

    /**
     * Test that ServerPlatformInfo handles rapid successive method calls.
     * This tests for any potential state or concurrency issues.
     */
    @Test
    @DisplayName("Test ServerPlatformInfo handles rapid successive calls")
    void testHandlesRapidSuccessiveCalls()
    {
        // Capture initial values
        String initialVersion = platformInfo.getVersion();
        String initialBuildTime = platformInfo.getBuildTime();
        String initialRevision = platformInfo.getBuildRevision();

        // Make many rapid calls
        for (int i = 0; i < 100; i++)
        {
            String version = platformInfo.getVersion();
            String buildTime = platformInfo.getBuildTime();
            String revision = platformInfo.getBuildRevision();

            assertEquals(initialVersion, version, "Version should remain stable (iteration " + i + ")");
            assertEquals(initialBuildTime, buildTime, "Build time should remain stable (iteration " + i + ")");
            assertEquals(initialRevision, revision, "Build revision should remain stable (iteration " + i + ")");
        }
    }

    /**
     * Test the nullability contract of ServerPlatformInfo getters.
     * All three methods may return null if version.json is not available.
     * This test documents the expected behavior.
     */
    @Test
    @DisplayName("Test ServerPlatformInfo getters nullability contract")
    void testGettersNullabilityContract()
    {
        String version = platformInfo.getVersion();
        String buildTime = platformInfo.getBuildTime();
        String revision = platformInfo.getBuildRevision();

        // All three should either all be null (no version.json) or all be non-null (version.json exists)
        // OR they could be a mix if version.json has partial data
        // This test simply documents that nulls are acceptable return values

        // If any value is non-null, it should be non-empty
        if (version != null)
        {
            assertFalse(version.isEmpty(), "Version should not be empty when not null");
        }

        if (buildTime != null)
        {
            assertFalse(buildTime.isEmpty(), "Build time should not be empty when not null");
        }

        if (revision != null)
        {
            assertFalse(revision.isEmpty(), "Build revision should not be empty when not null");
        }
    }

    /**
     * Test that ServerPlatformInfo maintains consistency across the entire InfoService chain.
     * Values should be the same whether accessed through cached reference or fresh lookups.
     */
    @Test
    @DisplayName("Test ServerPlatformInfo consistency through InfoService chain")
    void testConsistencyThroughInfoServiceChain()
    {
        InfoService service = new InfoService();

        // Access through different paths
        InfoService.ServerPlatformInfo platform1 = service.getServerInfo().getPlatform();
        InfoService.ServerPlatformInfo platform2 = service.getServerInfo().getPlatform();

        // Should be the same instance
        assertSame(platform1, platform2, "ServerInfo should return the same ServerPlatformInfo instance");

        // Values should be identical
        assertEquals(platform1.getVersion(), platform2.getVersion(), "Version should match");
        assertEquals(platform1.getBuildTime(), platform2.getBuildTime(), "Build time should match");
        assertEquals(platform1.getBuildRevision(), platform2.getBuildRevision(), "Build revision should match");
    }
}

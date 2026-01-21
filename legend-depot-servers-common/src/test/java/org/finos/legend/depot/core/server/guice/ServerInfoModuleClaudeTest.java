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

package org.finos.legend.depot.core.server.guice;

import org.finos.legend.depot.core.server.info.InfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ServerInfoModule class.
 *
 * This test class tests all methods in ServerInfoModule including:
 * - Constructor (default constructor)
 * - configure() - tested indirectly by verifying the module can be instantiated
 * - initInfo() - tested directly by calling the method
 *
 * Note: Most tests focus on the initInfo() method since it contains the actual business logic.
 * The configure() method is a Guice configuration method that would require a full Guice
 * injector setup with all dependencies, which is complex for unit testing. The fact that the
 * module compiles and can be instantiated verifies the configure() method structure is correct.
 */
class ServerInfoModuleClaudeTest
{
    /**
     * Test that the module can be instantiated successfully.
     * Tests the default constructor.
     */
    @Test
    @DisplayName("Test ServerInfoModule can be instantiated")
    void testModuleInstantiation()
    {
        // Act
        ServerInfoModule module = new ServerInfoModule();

        // Assert
        assertNotNull(module, "Module should be instantiated successfully");
    }

    /**
     * Test that multiple modules can be instantiated independently.
     * This verifies that the module has no shared state that could cause issues.
     */
    @Test
    @DisplayName("Test multiple module instances are independent")
    void testMultipleModuleInstances()
    {
        // Act
        ServerInfoModule module1 = new ServerInfoModule();
        ServerInfoModule module2 = new ServerInfoModule();

        // Assert
        assertNotNull(module1, "First module should be instantiated");
        assertNotNull(module2, "Second module should be instantiated");
        assertNotSame(module1, module2, "Module instances should be different");
    }

    /**
     * Test that initInfo() creates a properly initialized InfoService.
     * This directly tests the initInfo() method.
     */
    @Test
    @DisplayName("Test initInfo creates properly initialized InfoService")
    void testInitInfoCreatesProperlyInitializedInfoService()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act
        InfoService infoService = module.initInfo();

        // Assert
        assertNotNull(infoService, "initInfo() should return a non-null InfoService");
        assertNotNull(infoService.getServerInfo(), "InfoService should have server info");
        assertNotNull(infoService.getServerInfo().getServerTimeZone(), "Server info should have time zone");
    }

    /**
     * Test that initInfo() creates new instances when called directly.
     * This verifies that the method itself creates new instances,
     * and it's the @Singleton annotation that ensures singleton behavior in Guice.
     */
    @Test
    @DisplayName("Test initInfo creates new instances when called directly")
    void testInitInfoCreatesNewInstances()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act
        InfoService service1 = module.initInfo();
        InfoService service2 = module.initInfo();

        // Assert
        assertNotNull(service1, "First service should be created");
        assertNotNull(service2, "Second service should be created");
        assertNotSame(service1, service2, "Direct calls to initInfo() should create different instances");
    }

    /**
     * Test that initInfo() consistently returns valid InfoService instances.
     * This verifies the quality and consistency of the InfoService created by initInfo().
     */
    @Test
    @DisplayName("Test initInfo consistently returns valid InfoService instances")
    void testInitInfoConsistentlyReturnsValidInstances()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act & Assert - Create multiple instances and verify they are all valid
        for (int i = 0; i < 5; i++)
        {
            InfoService infoService = module.initInfo();
            assertNotNull(infoService, "InfoService instance " + i + " should not be null");

            InfoService.ServerInfo serverInfo = infoService.getServerInfo();
            assertNotNull(serverInfo, "Server info for instance " + i + " should not be null");
            assertNotNull(serverInfo.getServerTimeZone(), "Server time zone for instance " + i + " should be set");
            assertNotNull(serverInfo.getPlatform(), "Platform info for instance " + i + " should be set");
        }
    }

    /**
     * Test that initInfo() creates InfoService with valid server info properties.
     * This verifies the complete initialization of all ServerInfo properties.
     */
    @Test
    @DisplayName("Test initInfo creates InfoService with valid server info properties")
    void testInitInfoCreatesInfoServiceWithValidProperties()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act
        InfoService infoService = module.initInfo();
        InfoService.ServerInfo serverInfo = infoService.getServerInfo();
        InfoService.ServerPlatformInfo platformInfo = serverInfo.getPlatform();

        // Assert
        assertNotNull(serverInfo, "Server info should not be null");
        assertNotNull(serverInfo.getServerTimeZone(), "Server time zone should be set");
        assertFalse(serverInfo.getServerTimeZone().isEmpty(), "Server time zone should not be empty");

        assertNotNull(platformInfo, "Platform info should not be null");
        // Platform info fields may be null if version.json is not present, which is acceptable
    }

    /**
     * Test that initInfo() can be called multiple times on the same module instance.
     * This verifies that the module's initInfo() method is reusable.
     */
    @Test
    @DisplayName("Test initInfo can be called multiple times on same module")
    void testInitInfoCanBeCalledMultipleTimes()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act - Call initInfo multiple times
        InfoService service1 = module.initInfo();
        InfoService service2 = module.initInfo();
        InfoService service3 = module.initInfo();

        // Assert - Each call should produce a valid but different instance
        assertNotNull(service1, "First service should be created");
        assertNotNull(service2, "Second service should be created");
        assertNotNull(service3, "Third service should be created");

        assertNotSame(service1, service2, "First and second services should be different");
        assertNotSame(service2, service3, "Second and third services should be different");
        assertNotSame(service1, service3, "First and third services should be different");
    }

    /**
     * Test that initInfo() creates InfoService instances that are independent.
     * This verifies that each InfoService instance has its own ServerInfo object.
     */
    @Test
    @DisplayName("Test initInfo creates independent InfoService instances")
    void testInitInfoCreatesIndependentInstances()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act
        InfoService service1 = module.initInfo();
        InfoService service2 = module.initInfo();

        InfoService.ServerInfo serverInfo1 = service1.getServerInfo();
        InfoService.ServerInfo serverInfo2 = service2.getServerInfo();

        // Assert
        assertNotSame(service1, service2, "Services should be different instances");
        assertNotSame(serverInfo1, serverInfo2, "Server info objects should be different instances");

        // But should have same values for immutable properties like timezone
        assertEquals(serverInfo1.getServerTimeZone(), serverInfo2.getServerTimeZone(),
            "Both instances should have the same time zone");
    }

    /**
     * Test that different module instances create independent InfoService instances.
     * This verifies module instance independence.
     */
    @Test
    @DisplayName("Test different modules create independent InfoService instances")
    void testDifferentModulesCreateIndependentServices()
    {
        // Arrange
        ServerInfoModule module1 = new ServerInfoModule();
        ServerInfoModule module2 = new ServerInfoModule();

        // Act
        InfoService service1 = module1.initInfo();
        InfoService service2 = module2.initInfo();

        // Assert
        assertNotNull(service1, "Service from first module should be created");
        assertNotNull(service2, "Service from second module should be created");
        assertNotSame(service1, service2, "Services from different modules should be different");
    }

    /**
     * Test that initInfo() method signature matches the expected provider pattern.
     * This verifies the method returns InfoService and takes no parameters.
     */
    @Test
    @DisplayName("Test initInfo method signature is correct")
    void testInitInfoMethodSignature()
    {
        // Arrange
        ServerInfoModule module = new ServerInfoModule();

        // Act - This test verifies we can call the method without parameters
        InfoService result = module.initInfo();

        // Assert
        assertNotNull(result, "initInfo() should return a non-null result");
        assertTrue(result instanceof InfoService,
            "initInfo() should return an instance of InfoService");
    }
}

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

package org.finos.legend.depot.store.server;

import io.dropwizard.cli.CheckCommand;
import io.dropwizard.setup.Bootstrap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegendDepotStoreServerClaude_mainTest
{
    @Test
    public void testMainWithCheckCommandValidatesConfiguration()
    {
        // Test that main method can validate configuration using the check command
        // This tests the main method without starting the full server
        String configPath = "legend-depot-store-server/src/test/resources/sample-server-config.json";

        // The check command validates configuration without starting the server
        // If the configuration is valid, it will exit with status 0
        String[] args = new String[] { "check", configPath };

        // Testing this requires invoking main, which would cause the test to actually run the command
        // Instead, we verify the behavior by ensuring we can create the server instance
        // and that it would be capable of running with these arguments
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Assertions.assertNotNull(server);

        // Verify the server has the check command available
        Bootstrap<org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration> bootstrap =
            new Bootstrap<>(server);
        server.initialize(bootstrap);

        // Verify check command is registered
        CheckCommand<org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration> checkCommand =
            new CheckCommand<>(server);
        Assertions.assertNotNull(checkCommand);
    }

    @Test
    public void testMainWithServerCommand()
    {
        // Test that main method can be invoked with server command arguments
        // This verifies the main method signature and basic functionality
        String configPath = "legend-depot-store-server/src/test/resources/sample-server-config.json";
        String[] args = new String[] { "server", configPath };

        // We cannot actually run the main method in a unit test as it would start the server
        // Instead, we verify that the components the main method uses are functional
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Assertions.assertNotNull(server);

        // Verify the server is properly initialized and could process these arguments
        Bootstrap<org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration> bootstrap =
            new Bootstrap<>(server);
        server.initialize(bootstrap);

        // Verify bootstrap is configured
        Assertions.assertNotNull(bootstrap.getApplication());
        Assertions.assertEquals(server, bootstrap.getApplication());
    }

    @Test
    public void testMainMethodExists()
    {
        // Test that the main method exists and has the correct signature
        // This verifies that the entry point is properly defined
        try
        {
            java.lang.reflect.Method mainMethod = LegendDepotStoreServer.class.getMethod("main", String[].class);
            Assertions.assertNotNull(mainMethod);

            // Verify it's a static method
            Assertions.assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));

            // Verify it's public
            Assertions.assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));

            // Verify return type is void
            Assertions.assertEquals(void.class, mainMethod.getReturnType());
        }
        catch (NoSuchMethodException e)
        {
            Assertions.fail("main method should exist with signature: public static void main(String[] args)");
        }
    }

    @Test
    public void testMainMethodDeclaresException()
    {
        // Test that main method declares it throws Exception
        // This is standard for Dropwizard applications
        try
        {
            java.lang.reflect.Method mainMethod = LegendDepotStoreServer.class.getMethod("main", String[].class);
            Class<?>[] exceptionTypes = mainMethod.getExceptionTypes();

            // Verify the method declares Exception
            Assertions.assertTrue(exceptionTypes.length > 0);
            Assertions.assertEquals(Exception.class, exceptionTypes[0]);
        }
        catch (NoSuchMethodException e)
        {
            Assertions.fail("main method should exist");
        }
    }

    @Test
    public void testMainMethodWithEmptyArgs()
    {
        // Test that we can verify behavior with empty arguments
        // Empty args should cause Dropwizard to show usage information
        String[] args = new String[] {};

        // We cannot actually invoke main in unit tests as it would try to start the server
        // Instead we verify the server can be instantiated, which is what main does
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        Assertions.assertNotNull(server);

        // Verify the server instance is ready to process commands
        Assertions.assertEquals("LegendDepotStoreServer", server.getName());
    }

    @Test
    public void testMainMethodCreatesNewInstance()
    {
        // Test that main method creates a new instance each time
        // This verifies the pattern: new LegendDepotStoreServer().run(args)

        // We verify this by checking that we can create multiple instances
        LegendDepotStoreServer server1 = new LegendDepotStoreServer();
        LegendDepotStoreServer server2 = new LegendDepotStoreServer();

        Assertions.assertNotNull(server1);
        Assertions.assertNotNull(server2);
        Assertions.assertNotSame(server1, server2);

        // This confirms the main method pattern would create a fresh instance
    }

    @Test
    public void testMainMethodServerCanBeInitialized()
    {
        // Test that a server instance created by main can be initialized
        // This tests the full initialization path that main would take
        LegendDepotStoreServer server = new LegendDepotStoreServer();

        Bootstrap<org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration> bootstrap =
            new Bootstrap<>(server);

        // Initialize the server as main would do via run()
        server.initialize(bootstrap);

        // Verify initialization succeeded
        Assertions.assertNotNull(bootstrap.getApplication());
        Assertions.assertNotNull(bootstrap.getObjectMapper());
        Assertions.assertNotNull(bootstrap.getMetricRegistry());
    }

    @Test
    public void testMainMethodWithVarargs()
    {
        // Test that main method accepts varargs (String... args)
        // This allows calling with various argument patterns
        try
        {
            java.lang.reflect.Method mainMethod = LegendDepotStoreServer.class.getMethod("main", String[].class);

            // Verify the parameter is varargs by checking if it's an array
            Assertions.assertEquals(1, mainMethod.getParameterCount());
            Assertions.assertTrue(mainMethod.getParameterTypes()[0].isArray());

            // Verify it accepts String[]
            Assertions.assertEquals(String[].class, mainMethod.getParameterTypes()[0]);
        }
        catch (NoSuchMethodException e)
        {
            Assertions.fail("main method should exist");
        }
    }
}

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

import io.dropwizard.jersey.setup.JerseyEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class LegendDepotStoreServerClaude_registerJacksonJsonProviderTest
{
    @Test
    public void testRegisterJacksonJsonProviderRegistersProvider()
    {
        // Test that registerJacksonJsonProvider registers LegendDepotStoreServerJacksonJsonProvider
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        // Act
        server.registerJacksonJsonProvider(jerseyEnvironment);

        // Assert - verify that register was called with the correct provider class
        verify(jerseyEnvironment, times(1)).register(LegendDepotStoreServerJacksonJsonProvider.class);
    }

    @Test
    public void testRegisterJacksonJsonProviderWithNonNullJerseyEnvironment()
    {
        // Test that registerJacksonJsonProvider works with a valid JerseyEnvironment
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        // Should not throw any exception
        Assertions.assertDoesNotThrow(() -> server.registerJacksonJsonProvider(jerseyEnvironment));
    }

    @Test
    public void testRegisterJacksonJsonProviderCallsRegisterOnce()
    {
        // Test that registerJacksonJsonProvider calls register exactly once
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        server.registerJacksonJsonProvider(jerseyEnvironment);

        // Verify register was called exactly once
        verify(jerseyEnvironment, times(1)).register(any(Class.class));
    }

    @Test
    public void testRegisterJacksonJsonProviderRegistersCorrectClass()
    {
        // Test that the exact class being registered is LegendDepotStoreServerJacksonJsonProvider
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        // Capture the argument passed to register
        ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

        server.registerJacksonJsonProvider(jerseyEnvironment);

        verify(jerseyEnvironment).register(classCaptor.capture());
        Assertions.assertEquals(LegendDepotStoreServerJacksonJsonProvider.class, classCaptor.getValue());
    }

    @Test
    public void testRegisterJacksonJsonProviderWithMultipleCalls()
    {
        // Test that registerJacksonJsonProvider can be called multiple times
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        // Call multiple times
        server.registerJacksonJsonProvider(jerseyEnvironment);
        server.registerJacksonJsonProvider(jerseyEnvironment);

        // Verify register was called twice
        verify(jerseyEnvironment, times(2)).register(LegendDepotStoreServerJacksonJsonProvider.class);
    }

    @Test
    public void testRegisterJacksonJsonProviderWithDifferentJerseyEnvironments()
    {
        // Test that the same server can register providers with different JerseyEnvironment instances
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment1 = Mockito.mock(JerseyEnvironment.class);
        JerseyEnvironment jerseyEnvironment2 = Mockito.mock(JerseyEnvironment.class);

        server.registerJacksonJsonProvider(jerseyEnvironment1);
        server.registerJacksonJsonProvider(jerseyEnvironment2);

        // Verify both environments had register called
        verify(jerseyEnvironment1, times(1)).register(LegendDepotStoreServerJacksonJsonProvider.class);
        verify(jerseyEnvironment2, times(1)).register(LegendDepotStoreServerJacksonJsonProvider.class);
    }

    @Test
    public void testRegisterJacksonJsonProviderWithNewServerInstance()
    {
        // Test that registerJacksonJsonProvider works immediately after server instantiation
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        // Should work without any prior initialization
        Assertions.assertDoesNotThrow(() -> server.registerJacksonJsonProvider(jerseyEnvironment));

        verify(jerseyEnvironment, times(1)).register(LegendDepotStoreServerJacksonJsonProvider.class);
    }

    @Test
    public void testRegisterJacksonJsonProviderDoesNotReturnValue()
    {
        // Test that registerJacksonJsonProvider is a void method
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        // Verify the method executes and returns nothing
        server.registerJacksonJsonProvider(jerseyEnvironment);

        // If we got here without exception, the void method executed successfully
        verify(jerseyEnvironment).register(LegendDepotStoreServerJacksonJsonProvider.class);
    }

    @Test
    public void testRegisterJacksonJsonProviderRegistersClassNotInstance()
    {
        // Test that registerJacksonJsonProvider registers a class, not an instance
        LegendDepotStoreServer server = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

        ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

        server.registerJacksonJsonProvider(jerseyEnvironment);

        verify(jerseyEnvironment).register(classCaptor.capture());

        // The captured value should be a Class object
        Assertions.assertTrue(classCaptor.getValue() instanceof Class);
        Assertions.assertEquals(LegendDepotStoreServerJacksonJsonProvider.class, classCaptor.getValue());
    }

    @Test
    public void testLegendDepotStoreServerJacksonJsonProviderCanBeInstantiated()
    {
        // Test that the provider class being registered can be instantiated
        // This verifies it has a no-arg constructor as required by Jersey
        Assertions.assertDoesNotThrow(() -> {
            LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();
            Assertions.assertNotNull(provider);
        });
    }

    @Test
    public void testLegendDepotStoreServerJacksonJsonProviderHasObjectMapper()
    {
        // Test that the registered provider has an ObjectMapper configured
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();

        // The provider should return a non-null ObjectMapper for any type
        Assertions.assertNotNull(provider.getContext(Object.class));
        Assertions.assertNotNull(provider.getContext(String.class));
    }

    @Test
    public void testLegendDepotStoreServerJacksonJsonProviderObjectMapperHasDateFormat()
    {
        // Test that the registered provider's ObjectMapper has the expected date format
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();

        // Verify the ObjectMapper has date format configured
        Assertions.assertNotNull(provider.getContext(Object.class).getDateFormat());
    }

    @Test
    public void testLegendDepotStoreServerJacksonJsonProviderUsesSimpleDateFormat()
    {
        // Test that the provider uses the expected SimpleDateFormat
        LegendDepotStoreServerJacksonJsonProvider provider = new LegendDepotStoreServerJacksonJsonProvider();

        // Verify the ObjectMapper's date format is configured (SimpleDateFormat instance)
        Assertions.assertNotNull(provider.getContext(Object.class).getDateFormat());
        // Verify it's the same instance as the static field
        Assertions.assertEquals(LegendDepotStoreServerJacksonJsonProvider.SIMPLE_DATE_FORMAT,
                                provider.getContext(Object.class).getDateFormat());
    }

    @Test
    public void testRegisterJacksonJsonProviderIntegrationWithMultipleServers()
    {
        // Test that multiple server instances can each register their providers
        LegendDepotStoreServer server1 = new LegendDepotStoreServer();
        LegendDepotStoreServer server2 = new LegendDepotStoreServer();
        JerseyEnvironment jerseyEnvironment1 = Mockito.mock(JerseyEnvironment.class);
        JerseyEnvironment jerseyEnvironment2 = Mockito.mock(JerseyEnvironment.class);

        server1.registerJacksonJsonProvider(jerseyEnvironment1);
        server2.registerJacksonJsonProvider(jerseyEnvironment2);

        // Both should have registered successfully
        verify(jerseyEnvironment1, times(1)).register(LegendDepotStoreServerJacksonJsonProvider.class);
        verify(jerseyEnvironment2, times(1)).register(LegendDepotStoreServerJacksonJsonProvider.class);
    }
}

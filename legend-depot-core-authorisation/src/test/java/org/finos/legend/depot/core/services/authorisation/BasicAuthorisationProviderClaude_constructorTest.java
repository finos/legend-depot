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

package org.finos.legend.depot.core.services.authorisation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicAuthorisationProviderClaude_constructorTest
{
    @Test
    public void testConstructorSuccessfullyLoadsConfigFile()
    {
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider();

        // Verify the constructor completed without throwing an exception
        Assertions.assertNotNull(provider);

        // Verify the loaded configuration works by testing authorization
        // This indirectly confirms the constructor loaded the authorisedIdentities.json correctly
        provider.authorise(() -> () -> "test", "admin");
    }

    @Test
    public void testConstructorWithValidConfiguration()
    {
        // This test verifies that when authorisedIdentities.json exists and is valid,
        // the constructor successfully creates an instance
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider();

        Assertions.assertNotNull(provider);

        // Verify the configuration was loaded by checking that a known role works
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "test", "admin"));
    }

    @Test
    public void testConstructorLoadsMultipleUsers()
    {
        // Verify that multiple users in the configuration are loaded correctly
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider();

        // Both "test" and "user1" should be authorized for "admin" role
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "test", "admin"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "user1", "admin"));
    }

    @Test
    public void testMapConstructorWithEmptyMap()
    {
        // Test the package-private constructor with an empty map
        Map<String, List<String>> emptyMap = new HashMap<>();
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(emptyMap);

        Assertions.assertNotNull(provider);

        // Attempting to authorize with any role should throw SecurityException (unknown role)
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "user", "admin"));
    }

    @Test
    public void testMapConstructorWithSingleRoleAndUser()
    {
        // Test the constructor with a single role and single user
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("viewer", Collections.singletonList("alice"));

        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Assertions.assertNotNull(provider);

        // alice should be authorized for viewer role
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "viewer"));

        // bob should not be authorized for viewer role
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "bob", "viewer"));
    }

    @Test
    public void testMapConstructorWithMultipleRolesAndUsers()
    {
        // Test the constructor with multiple roles and users
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice", "bob"));
        authMap.put("viewer", Arrays.asList("charlie", "david", "eve"));
        authMap.put("editor", Collections.singletonList("frank"));

        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Assertions.assertNotNull(provider);

        // Verify admin role authorizations
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "admin"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "bob", "admin"));

        // Verify viewer role authorizations
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "charlie", "viewer"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "david", "viewer"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "eve", "viewer"));

        // Verify editor role authorization
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "frank", "editor"));

        // Verify unauthorized user
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "unauthorized", "admin"));
    }

    @Test
    public void testMapConstructorWithNullValues()
    {
        // Test the constructor with null list for a role
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice", "bob"));
        authMap.put("nullRole", null);

        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Assertions.assertNotNull(provider);

        // Verify admin role still works
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "admin"));

        // Attempting to authorize for nullRole should throw SecurityException (null check in authorise method)
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "anyuser", "nullRole"));
    }

    @Test
    public void testMapConstructorPreservesProvidedMap()
    {
        // Test that the constructor uses the provided map correctly
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("developer", Arrays.asList("john", "jane"));

        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // Verify the provider works with the provided configuration
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "john", "developer"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "jane", "developer"));

        // Verify unknown role throws exception
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "john", "unknown"));
    }

    @Test
    public void testMapConstructorWithEmptyUserList()
    {
        // Test the constructor with a role that has an empty user list
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice"));
        authMap.put("emptyRole", Collections.emptyList());

        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Assertions.assertNotNull(provider);

        // Role exists but has no authorized users
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "anyuser", "emptyRole"));

        // Admin role should still work
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "admin"));
    }
}

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

import javax.inject.Provider;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicAuthorisationProviderClaude_authoriseTest
{
    @Test
    public void testAuthoriseWithValidUserAndRole()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice", "bob"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Provider<Principal> principalProvider = () -> () -> "alice";

        // Should not throw any exception
        Assertions.assertDoesNotThrow(() -> provider.authorise(principalProvider, "admin"));
    }

    @Test
    public void testAuthoriseWithUnknownRoleThrowsSecurityException()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Provider<Principal> principalProvider = () -> () -> "alice";

        SecurityException exception = Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(principalProvider, "unknownRole"));

        Assertions.assertEquals("Unknown role [unknownRole]", exception.getMessage());
    }

    @Test
    public void testAuthoriseWithUnauthorizedUserThrowsSecurityException()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice", "bob"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Provider<Principal> principalProvider = () -> () -> "charlie";

        SecurityException exception = Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(principalProvider, "admin"));

        Assertions.assertEquals("User [charlie] not authorised for role [admin]", exception.getMessage());
    }

    @Test
    public void testAuthoriseWithMultipleAuthorizedUsers()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("viewer", Arrays.asList("alice", "bob", "charlie"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // All three users should be authorized
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "viewer"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "bob", "viewer"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "charlie", "viewer"));
    }

    @Test
    public void testAuthoriseWithDifferentRoles()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Collections.singletonList("alice"));
        authMap.put("viewer", Collections.singletonList("bob"));
        authMap.put("editor", Collections.singletonList("charlie"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // Each user should only be authorized for their specific role
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "admin"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "bob", "viewer"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "charlie", "editor"));

        // Users should not be authorized for other roles
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "alice", "viewer"));
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "bob", "admin"));
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "charlie", "admin"));
    }

    @Test
    public void testAuthoriseWithSingleUserInRole()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("superadmin", Collections.singletonList("root"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // root should be authorized
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "root", "superadmin"));

        // Other users should not be authorized
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "admin", "superadmin"));
    }

    @Test
    public void testAuthoriseWithEmptyRoleListThrowsSecurityException()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("emptyRole", Collections.emptyList());
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Provider<Principal> principalProvider = () -> () -> "anyuser";

        SecurityException exception = Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(principalProvider, "emptyRole"));

        Assertions.assertEquals("User [anyuser] not authorised for role [emptyRole]", exception.getMessage());
    }

    @Test
    public void testAuthoriseWithNullRoleListThrowsSecurityException()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("nullRole", null);
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Provider<Principal> principalProvider = () -> () -> "anyuser";

        SecurityException exception = Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(principalProvider, "nullRole"));

        Assertions.assertEquals("Unknown role [nullRole]", exception.getMessage());
    }

    @Test
    public void testAuthoriseCaseSensitiveUsername()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Collections.singletonList("Alice"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // Exact match should work
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "Alice", "admin"));

        // Different case should fail (case-sensitive)
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "alice", "admin"));
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "ALICE", "admin"));
    }

    @Test
    public void testAuthoriseCaseSensitiveRole()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("Admin", Collections.singletonList("alice"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // Exact match should work
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "Admin"));

        // Different case should fail (case-sensitive)
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "alice", "admin"));
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "alice", "ADMIN"));
    }

    @Test
    public void testAuthoriseWithSpecialCharactersInUsername()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("user@example.com", "user.name", "user-name"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "user@example.com", "admin"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "user.name", "admin"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "user-name", "admin"));
    }

    @Test
    public void testAuthoriseWithSpecialCharactersInRole()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("role-with-dash", Collections.singletonList("alice"));
        authMap.put("role.with.dot", Collections.singletonList("bob"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "role-with-dash"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "bob", "role.with.dot"));
    }

    @Test
    public void testAuthoriseMultipleCallsWithSamePrincipal()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice", "bob"));
        authMap.put("viewer", Arrays.asList("alice", "charlie"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        Provider<Principal> aliceProvider = () -> () -> "alice";

        // alice should be authorized for both admin and viewer
        Assertions.assertDoesNotThrow(() -> provider.authorise(aliceProvider, "admin"));
        Assertions.assertDoesNotThrow(() -> provider.authorise(aliceProvider, "viewer"));

        // alice should not be authorized for editor (doesn't exist)
        Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(aliceProvider, "editor"));
    }

    @Test
    public void testAuthoriseVerifiesExceptionMessages()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Collections.singletonList("alice"));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // Test unknown role message format
        SecurityException unknownRoleException = Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "alice", "unknownRole"));
        Assertions.assertTrue(unknownRoleException.getMessage().contains("Unknown role"));
        Assertions.assertTrue(unknownRoleException.getMessage().contains("[unknownRole]"));

        // Test unauthorized user message format
        SecurityException unauthorizedException = Assertions.assertThrows(SecurityException.class,
            () -> provider.authorise(() -> () -> "bob", "admin"));
        Assertions.assertTrue(unauthorizedException.getMessage().contains("User [bob] not authorised"));
        Assertions.assertTrue(unauthorizedException.getMessage().contains("role [admin]"));
    }

    @Test
    public void testAuthoriseWithEmptyUsername()
    {
        Map<String, List<String>> authMap = new HashMap<>();
        authMap.put("admin", Arrays.asList("alice", ""));
        BasicAuthorisationProvider provider = new BasicAuthorisationProvider(authMap);

        // Empty string as username should be supported
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "", "admin"));

        // Non-empty username should work as usual
        Assertions.assertDoesNotThrow(() -> provider.authorise(() -> () -> "alice", "admin"));
    }
}

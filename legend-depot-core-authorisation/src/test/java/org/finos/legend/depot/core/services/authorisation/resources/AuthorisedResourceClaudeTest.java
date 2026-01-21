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

package org.finos.legend.depot.core.services.authorisation.resources;

import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import java.security.Principal;

public class AuthorisedResourceClaudeTest


{
    // Concrete implementation of AuthorisedResource for testing purposes
    private static class TestAuthorisedResource extends AuthorisedResource
    {
        private final String resourceName;

        public TestAuthorisedResource(AuthorisationProvider authorisationProvider,
                                     Provider<Principal> principalProvider,
                                     String resourceName)
  {
            super(authorisationProvider, principalProvider);
            this.resourceName = resourceName;
        }

        @Override
        protected String getResourceName()
  {
            return resourceName;
        }

        // Expose validateUser as public for testing
        public void callValidateUser()
  {
            validateUser();
        }
    }

    // Simple AuthorisationProvider implementation for testing
    private static class TestAuthorisationProvider implements AuthorisationProvider
    {
        private final boolean shouldAuthorise;
        private final String expectedRole;
        private Provider<Principal> capturedPrincipalProvider;

        public TestAuthorisationProvider(boolean shouldAuthorise, String expectedRole)
  {
            this.shouldAuthorise = shouldAuthorise;
            this.expectedRole = expectedRole;
        }

        @Override
        public void authorise(Provider<Principal> principalProvider, String role)
  {
            this.capturedPrincipalProvider = principalProvider;

            if (!shouldAuthorise)
            {
                throw new SecurityException("User not authorised for role [" + role + "]");
            }

            if (expectedRole != null && !expectedRole.equals(role))
            {
                throw new SecurityException("Unexpected role [" + role + "], expected [" + expectedRole + "]");
            }
        }

        public Provider<Principal> getCapturedPrincipalProvider()
        {
            return capturedPrincipalProvider;
        }
    }

    @Test
    public void testValidateUserWithAuthorisedUser()
  {
        // Arrange
        String resourceName = "testResource";
        Provider<Principal> principalProvider = () -> () -> "testUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert - should not throw exception
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());

        // Verify the correct principal provider was passed
        Assertions.assertSame(principalProvider, authProvider.getCapturedPrincipalProvider());
    }

    @Test
    public void testValidateUserWithUnauthorisedUserThrowsSecurityException()
  {
        // Arrange
        String resourceName = "testResource";
        Provider<Principal> principalProvider = () -> () -> "unauthorisedUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(false, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert
        SecurityException exception = Assertions.assertThrows(SecurityException.class,
            () -> resource.callValidateUser());

        Assertions.assertTrue(exception.getMessage().contains("not authorised"));
    }

    @Test
    public void testValidateUserPassesCorrectResourceName()
  {
        // Arrange
        String resourceName = "adminResource";
        Provider<Principal> principalProvider = () -> () -> "adminUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert - should not throw exception
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
    }

    @Test
    public void testValidateUserWithDifferentResourceNames()
  {
        // Arrange
        Provider<Principal> principalProvider = () -> () -> "testUser";

        // Test with different resource names
        String[] resourceNames = {"resource1", "resource2", "adminResource", "viewerResource"};

        for (String resourceName : resourceNames)
        {
            TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
            TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

            // Should not throw exception
            Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
        }
    }

    @Test
    public void testValidateUserWithDifferentPrincipals()
  {
        // Arrange
        String resourceName = "testResource";
        String[] usernames = {"user1", "user2", "admin", "viewer"};

        for (String username : usernames)
        {
            Provider<Principal> principalProvider = () -> () -> username;
            TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
            TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

            // Should not throw exception
            Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
        }
    }

    @Test
    public void testValidateUserMultipleCalls()
  {
        // Arrange
        String resourceName = "testResource";
        Provider<Principal> principalProvider = () -> () -> "testUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert - multiple calls should all succeed
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
    }

    @Test
    public void testValidateUserWithSpecialCharactersInResourceName()
  {
        // Arrange
        String resourceName = "resource-with-dash.and.dot";
        Provider<Principal> principalProvider = () -> () -> "testUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert - should not throw exception
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
    }

    @Test
    public void testValidateUserWithEmptyResourceName()
  {
        // Arrange
        String resourceName = "";
        Provider<Principal> principalProvider = () -> () -> "testUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert - should not throw exception
        Assertions.assertDoesNotThrow(() -> resource.callValidateUser());
    }

    @Test
    public void testValidateUserInteractionBetweenProviders()
  {
        // Arrange
        String resourceName = "testResource";
        Principal testPrincipal = () -> "testUser";
        Provider<Principal> principalProvider = () -> testPrincipal;
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act
        resource.callValidateUser();

        // Assert - verify the principal provider was correctly passed to the authorisation provider
        Assertions.assertNotNull(authProvider.getCapturedPrincipalProvider());
        Assertions.assertSame(principalProvider, authProvider.getCapturedPrincipalProvider());
        Assertions.assertSame(testPrincipal, authProvider.getCapturedPrincipalProvider().get());
    }

    @Test
    public void testConstructorWithValidParameters()
  {
        // Arrange
        String resourceName = "testResource";
        Provider<Principal> principalProvider = () -> () -> "testUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, resourceName);

        // Act & Assert - constructor should work fine
        Assertions.assertDoesNotThrow(() ->
            new TestAuthorisedResource(authProvider, principalProvider, resourceName));
    }

    @Test
    public void testGetResourceNameReturnsCorrectValue()
  {
        // Arrange
        String expectedResourceName = "myResource";
        Provider<Principal> principalProvider = () -> () -> "testUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(true, expectedResourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, expectedResourceName);

        // Act
        String actualResourceName = resource.getResourceName();

        // Assert
        Assertions.assertEquals(expectedResourceName, actualResourceName);
    }

    @Test
    public void testValidateUserPropagatesSecurityException()
  {
        // Arrange
        String resourceName = "secureResource";
        Provider<Principal> principalProvider = () -> () -> "unauthorisedUser";
        TestAuthorisationProvider authProvider = new TestAuthorisationProvider(false, resourceName);
        TestAuthorisedResource resource = new TestAuthorisedResource(authProvider, principalProvider, resourceName);

        // Act & Assert - SecurityException should be propagated
        Assertions.assertThrows(SecurityException.class, () -> resource.callValidateUser());
    }
}

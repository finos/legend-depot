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

package org.finos.legend.depot.services.api.artifacts.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtifactNotFoundExceptionClaudeTest
{
    @Test
    void testConstructorWithMessage()
    {
        // Arrange
        String expectedMessage = "Artifact not found: com.example:artifact:1.0.0";

        // Act
        ArtifactNotFoundException exception = new ArtifactNotFoundException(expectedMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithNullMessage()
    {
        // Act
        ArtifactNotFoundException exception = new ArtifactNotFoundException(null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testConstructorWithEmptyMessage()
    {
        // Arrange
        String emptyMessage = "";

        // Act
        ArtifactNotFoundException exception = new ArtifactNotFoundException(emptyMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithLongMessage()
    {
        // Arrange
        String longMessage = "Artifact not found: The requested artifact with coordinates " +
                "com.example.very.long.package.name:artifact-with-a-very-long-name:1.0.0-SNAPSHOT " +
                "could not be located in any of the configured repositories.";

        // Act
        ArtifactNotFoundException exception = new ArtifactNotFoundException(longMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithSpecialCharactersInMessage()
    {
        // Arrange
        String messageWithSpecialChars = "Artifact not found: com.example:artifact:1.0.0 \n\t!@#$%^&*()";

        // Act
        ArtifactNotFoundException exception = new ArtifactNotFoundException(messageWithSpecialChars);

        // Assert
        assertNotNull(exception);
        assertEquals(messageWithSpecialChars, exception.getMessage());
    }

    @Test
    void testExceptionIsInstanceOfException()
    {
        // Arrange
        String message = "Test artifact not found";

        // Act
        ArtifactNotFoundException exception = new ArtifactNotFoundException(message);

        // Assert
        assertTrue(exception instanceof Exception);
    }

    @Test
    void testExceptionCanBeThrown()
    {
        // Arrange
        String expectedMessage = "Artifact not found in repository";

        // Act & Assert
        try
        {
            throw new ArtifactNotFoundException(expectedMessage);
        }
        catch (ArtifactNotFoundException e)
        {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void testExceptionCanBeCaught()
    {
        // Arrange
        String expectedMessage = "Missing artifact";

        // Act & Assert
        try
        {
            throw new ArtifactNotFoundException(expectedMessage);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof ArtifactNotFoundException);
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}

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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;

class ArtifactRepositoryExceptionClaudeTest
{
    // Tests for constructor with String message

    @Test
    void testConstructorWithMessage()
    {
        // Arrange
        String expectedMessage = "Repository connection failed";

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(expectedMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithNullMessage()
    {
        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException((String) null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithEmptyMessage()
    {
        // Arrange
        String emptyMessage = "";

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(emptyMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithLongMessage()
    {
        // Arrange
        String longMessage = "Failed to connect to artifact repository: The remote server at " +
                "https://repository.example.com/artifacts returned HTTP 500 Internal Server Error. " +
                "This could be due to network issues, server overload, or misconfiguration.";

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(longMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithSpecialCharactersInMessage()
    {
        // Arrange
        String messageWithSpecialChars = "Repository error: \n\tFailed to fetch artifact!\n@#$%^&*()";

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(messageWithSpecialChars);

        // Assert
        assertNotNull(exception);
        assertEquals(messageWithSpecialChars, exception.getMessage());
    }

    // Tests for constructor with Throwable cause

    @Test
    void testConstructorWithThrowable()
    {
        // Arrange
        IOException cause = new IOException("Network error");

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(cause);

        // Assert
        assertNotNull(exception);
        assertSame(cause, exception.getCause());
        assertEquals("java.io.IOException: Network error", exception.getMessage());
    }

    @Test
    void testConstructorWithNullThrowable()
    {
        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException((Throwable) null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithRuntimeException()
    {
        // Arrange
        RuntimeException cause = new IllegalArgumentException("Invalid artifact coordinates");

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(cause);

        // Assert
        assertNotNull(exception);
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructorWithNestedExceptions()
    {
        // Arrange
        IOException rootCause = new IOException("Connection timeout");
        RuntimeException nestedCause = new RuntimeException("Failed to download", rootCause);

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(nestedCause);

        // Assert
        assertNotNull(exception);
        assertSame(nestedCause, exception.getCause());
        assertSame(rootCause, exception.getCause().getCause());
    }

    @Test
    void testConstructorWithThrowableWithoutMessage()
    {
        // Arrange
        Throwable cause = new IOException();

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(cause);

        // Assert
        assertNotNull(exception);
        assertSame(cause, exception.getCause());
    }

    // Tests for exception behavior

    @Test
    void testExceptionIsInstanceOfException()
    {
        // Arrange
        String message = "Test repository error";

        // Act
        ArtifactRepositoryException exception = new ArtifactRepositoryException(message);

        // Assert
        assertTrue(exception instanceof Exception);
    }

    @Test
    void testExceptionCanBeThrownWithMessage()
    {
        // Arrange
        String expectedMessage = "Repository unavailable";

        // Act & Assert
        try
        {
            throw new ArtifactRepositoryException(expectedMessage);
        }
        catch (ArtifactRepositoryException e)
        {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void testExceptionCanBeThrownWithCause()
    {
        // Arrange
        IOException cause = new IOException("I/O error");

        // Act & Assert
        try
        {
            throw new ArtifactRepositoryException(cause);
        }
        catch (ArtifactRepositoryException e)
        {
            assertSame(cause, e.getCause());
        }
    }

    @Test
    void testExceptionCanBeCaughtAsException()
    {
        // Arrange
        String expectedMessage = "Repository error";

        // Act & Assert
        try
        {
            throw new ArtifactRepositoryException(expectedMessage);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof ArtifactRepositoryException);
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void testExceptionWithThrowableCanBeCaughtAsException()
    {
        // Arrange
        RuntimeException cause = new RuntimeException("Test cause");

        // Act & Assert
        try
        {
            throw new ArtifactRepositoryException(cause);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof ArtifactRepositoryException);
            assertSame(cause, e.getCause());
        }
    }
}

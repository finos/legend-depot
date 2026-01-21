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

package org.finos.legend.depot.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StoreExceptionClaudeTest
{
    @Test
    @DisplayName("Constructor sets message correctly with non-null string")
    void testConstructorWithMessage()
    {
        // Arrange
        String errorMessage = "An error occurred in the store";

        // Act
        StoreException exception = new StoreException(errorMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(0, exception.getSuppressed().length);
    }

    @Test
    @DisplayName("Constructor handles null message")
    void testConstructorWithNullMessage()
    {
        // Act
        StoreException exception = new StoreException(null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor handles empty string message")
    void testConstructorWithEmptyMessage()
    {
        // Act
        StoreException exception = new StoreException("");

        // Assert
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Exception can be thrown and caught")
    void testExceptionCanBeThrown()
    {
        // Arrange
        String errorMessage = "Test exception";

        // Act & Assert
        StoreException thrown = assertThrows(StoreException.class, () -> {
            throw new StoreException(errorMessage);
        });

        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    @DisplayName("Exception is instance of RuntimeException")
    void testExceptionIsRuntimeException()
    {
        // Act
        StoreException exception = new StoreException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Constructor with multiline message")
    void testConstructorWithMultilineMessage()
    {
        // Arrange
        String multilineMessage = "Line 1\nLine 2\nLine 3";

        // Act
        StoreException exception = new StoreException(multilineMessage);

        // Assert
        assertEquals(multilineMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with special characters in message")
    void testConstructorWithSpecialCharacters()
    {
        // Arrange
        String specialMessage = "Error: 'quotes' and \"double quotes\" with symbols !@#$%^&*()";

        // Act
        StoreException exception = new StoreException(specialMessage);

        // Assert
        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Constructor with very long message")
    void testConstructorWithLongMessage()
    {
        // Arrange
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++)
        {
            longMessage.append("x");
        }
        String message = longMessage.toString();

        // Act
        StoreException exception = new StoreException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(1000, exception.getMessage().length());
    }
}

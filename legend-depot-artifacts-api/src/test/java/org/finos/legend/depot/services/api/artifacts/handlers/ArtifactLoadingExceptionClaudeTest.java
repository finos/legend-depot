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

package org.finos.legend.depot.services.api.artifacts.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArtifactLoadingExceptionClaudeTest 

{

    /**
     * Test {@link ArtifactLoadingException#ArtifactLoadingException(String)}.
     *
     * <p>Method under test: {@link ArtifactLoadingException#ArtifactLoadingException(String)}
     */
    @Test
    @DisplayName("Test constructor with non-null message")
    void testConstructorWithMessage()
  {
        // Arrange and Act
        ArtifactLoadingException exception = new ArtifactLoadingException("Artifact loading failed");

        // Assert
        assertEquals("Artifact loading failed", exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(0, exception.getSuppressed().length);
    }

    /**
     * Test {@link ArtifactLoadingException#ArtifactLoadingException(String)} with null message.
     *
     * <p>Method under test: {@link ArtifactLoadingException#ArtifactLoadingException(String)}
     */
    @Test
    @DisplayName("Test constructor with null message")
    void testConstructorWithNullMessage()
  {
        // Arrange and Act
        ArtifactLoadingException exception = new ArtifactLoadingException(null);

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(0, exception.getSuppressed().length);
    }

    /**
     * Test {@link ArtifactLoadingException#ArtifactLoadingException(String)} with empty message.
     *
     * <p>Method under test: {@link ArtifactLoadingException#ArtifactLoadingException(String)}
     */
    @Test
    @DisplayName("Test constructor with empty message")
    void testConstructorWithEmptyMessage()
  {
        // Arrange and Act
        ArtifactLoadingException exception = new ArtifactLoadingException("");

        // Assert
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(0, exception.getSuppressed().length);
    }

    /**
     * Test that ArtifactLoadingException is a RuntimeException.
     */
    @Test
    @DisplayName("Test that exception is a RuntimeException")
    void testIsRuntimeException()
  {
        // Arrange and Act
        ArtifactLoadingException exception = new ArtifactLoadingException("Test message");

        // Assert
        assertNotNull(exception);
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }
}

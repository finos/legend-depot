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

package org.finos.legend.depot.domain.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionAliasClaudeTest


{
    /**
     * Test {@link VersionAlias#values()} returns all enum constants.
     */
    @Test
    @DisplayName("Test values() returns all enum constants")
    void testValues()
  {
        // Act
        VersionAlias[] values = VersionAlias.values();

        // Assert
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals(VersionAlias.LATEST, values[0]);
        assertEquals(VersionAlias.HEAD, values[1]);
    }

    /**
     * Test {@link VersionAlias#values()} returns a new array each time.
     */
    @Test
    @DisplayName("Test values() returns a new array each time")
    void testValuesReturnsNewArray()
  {
        // Act
        VersionAlias[] values1 = VersionAlias.values();
        VersionAlias[] values2 = VersionAlias.values();

        // Assert - arrays should be different instances
        assertTrue(values1 != values2);
        assertEquals(values1.length, values2.length);
    }

    /**
     * Test {@link VersionAlias#valueOf(String)} with valid constant name "LATEST".
     */
    @Test
    @DisplayName("Test valueOf with LATEST")
    void testValueOfLatest()
  {
        // Act
        VersionAlias result = VersionAlias.valueOf("LATEST");

        // Assert
        assertNotNull(result);
        assertEquals(VersionAlias.LATEST, result);
    }

    /**
     * Test {@link VersionAlias#valueOf(String)} with valid constant name "HEAD".
     */
    @Test
    @DisplayName("Test valueOf with HEAD")
    void testValueOfHead()
  {
        // Act
        VersionAlias result = VersionAlias.valueOf("HEAD");

        // Assert
        assertNotNull(result);
        assertEquals(VersionAlias.HEAD, result);
    }

    /**
     * Test {@link VersionAlias#valueOf(String)} throws IllegalArgumentException for invalid constant name.
     */
    @Test
    @DisplayName("Test valueOf throws IllegalArgumentException for invalid name")
    void testValueOfInvalidName()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> VersionAlias.valueOf("INVALID"));
    }

    /**
     * Test {@link VersionAlias#valueOf(String)} throws NullPointerException for null input.
     */
    @Test
    @DisplayName("Test valueOf throws NullPointerException for null")
    void testValueOfNull()
  {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> VersionAlias.valueOf(null));
    }

    /**
     * Test {@link VersionAlias#valueOf(String)} is case-sensitive.
     */
    @Test
    @DisplayName("Test valueOf is case-sensitive")
    void testValueOfCaseSensitive()
  {
        // Act & Assert - lowercase should throw exception
        assertThrows(IllegalArgumentException.class, () -> VersionAlias.valueOf("latest"));
        assertThrows(IllegalArgumentException.class, () -> VersionAlias.valueOf("head"));
    }

    /**
     * Test {@link VersionAlias#getName()} for LATEST constant.
     */
    @Test
    @DisplayName("Test getName for LATEST")
    void testGetNameLatest()
  {
        // Act
        String name = VersionAlias.LATEST.getName();

        // Assert
        assertNotNull(name);
        assertEquals("latest", name);
    }

    /**
     * Test {@link VersionAlias#getName()} for HEAD constant.
     */
    @Test
    @DisplayName("Test getName for HEAD")
    void testGetNameHead()
  {
        // Act
        String name = VersionAlias.HEAD.getName();

        // Assert
        assertNotNull(name);
        assertEquals("head", name);
    }

    /**
     * Test {@link VersionAlias#getDescription()} for LATEST constant.
     */
    @Test
    @DisplayName("Test getDescription for LATEST")
    void testGetDescriptionLatest()
  {
        // Act
        String description = VersionAlias.LATEST.getDescription();

        // Assert
        assertNotNull(description);
        assertEquals("last released version", description);
    }

    /**
     * Test {@link VersionAlias#getDescription()} for HEAD constant.
     */
    @Test
    @DisplayName("Test getDescription for HEAD")
    void testGetDescriptionHead()
  {
        // Act
        String description = VersionAlias.HEAD.getDescription();

        // Assert
        assertNotNull(description);
        assertEquals("latest unreleased revision", description);
    }

    /**
     * Test that enum constants maintain their state across multiple calls.
     */
    @Test
    @DisplayName("Test enum constants maintain state")
    void testEnumConstantsState()
  {
        // Act - call getters multiple times
        String name1 = VersionAlias.LATEST.getName();
        String name2 = VersionAlias.LATEST.getName();
        String desc1 = VersionAlias.HEAD.getDescription();
        String desc2 = VersionAlias.HEAD.getDescription();

        // Assert - should return same values
        assertEquals(name1, name2);
        assertEquals(desc1, desc2);
    }

    /**
     * Test enum ordinal values.
     */
    @Test
    @DisplayName("Test enum ordinal values")
    void testEnumOrdinals()
  {
        // Assert
        assertEquals(0, VersionAlias.LATEST.ordinal());
        assertEquals(1, VersionAlias.HEAD.ordinal());
    }

    /**
     * Test enum string representation.
     */
    @Test
    @DisplayName("Test enum toString")
    void testEnumToString()
  {
        // Assert - toString should return the enum constant name
        assertEquals("LATEST", VersionAlias.LATEST.toString());
        assertEquals("HEAD", VersionAlias.HEAD.toString());
    }
}

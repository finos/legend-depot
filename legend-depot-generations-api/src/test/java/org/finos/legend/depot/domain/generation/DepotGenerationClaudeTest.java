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

package org.finos.legend.depot.domain.generation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DepotGenerationClaudeTest


{
    @Test
    void testConstructorWithNullPath()
  {
        // Arrange and Act
        DepotGeneration generation = new DepotGeneration(null, "content");

        // Assert
        assertNull(generation.getPath());
        assertEquals("content", generation.getContent());
    }

    @Test
    void testConstructorWithNullContent()
  {
        // Arrange and Act
        DepotGeneration generation = new DepotGeneration("path", null);

        // Assert
        assertEquals("path", generation.getPath());
        assertNull(generation.getContent());
    }

    @Test
    void testConstructorWithBothNulls()
  {
        // Arrange and Act
        DepotGeneration generation = new DepotGeneration(null, null);

        // Assert
        assertNull(generation.getPath());
        assertNull(generation.getContent());
    }

    @Test
    void testConstructorWithEmptyStrings()
  {
        // Arrange and Act
        DepotGeneration generation = new DepotGeneration("", "");

        // Assert
        assertEquals("", generation.getPath());
        assertEquals("", generation.getContent());
    }

    @Test
    void testEqualsWithNullFields()
  {
        // Arrange
        DepotGeneration generation1 = new DepotGeneration(null, null);
        DepotGeneration generation2 = new DepotGeneration(null, null);

        // Act and Assert
        assertEquals(generation1, generation2);
        assertEquals(generation1.hashCode(), generation2.hashCode());
    }

    @Test
    void testEqualsWithDifferentContent()
  {
        // Arrange
        DepotGeneration generation1 = new DepotGeneration("path", "content1");
        DepotGeneration generation2 = new DepotGeneration("path", "content2");

        // Act and Assert
        assertNotEquals(generation1, generation2);
    }

    @Test
    void testEqualsWithDifferentPath()
  {
        // Arrange
        DepotGeneration generation1 = new DepotGeneration("path1", "content");
        DepotGeneration generation2 = new DepotGeneration("path2", "content");

        // Act and Assert
        assertNotEquals(generation1, generation2);
    }

    @Test
    void testEqualsWithOneNullPath()
  {
        // Arrange
        DepotGeneration generation1 = new DepotGeneration(null, "content");
        DepotGeneration generation2 = new DepotGeneration("path", "content");

        // Act and Assert
        assertNotEquals(generation1, generation2);
    }

    @Test
    void testEqualsWithOneNullContent()
  {
        // Arrange
        DepotGeneration generation1 = new DepotGeneration("path", null);
        DepotGeneration generation2 = new DepotGeneration("path", "content");

        // Act and Assert
        assertNotEquals(generation1, generation2);
    }

    @Test
    void testHashCodeConsistency()
  {
        // Arrange
        DepotGeneration generation = new DepotGeneration("path", "content");

        // Act
        int hashCode1 = generation.hashCode();
        int hashCode2 = generation.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCodeWithNullFields()
  {
        // Arrange
        DepotGeneration generation1 = new DepotGeneration(null, null);
        DepotGeneration generation2 = new DepotGeneration(null, null);

        // Act and Assert
        assertEquals(generation1.hashCode(), generation2.hashCode());
    }

    @Test
    void testConstructorWithSpecialCharacters()
  {
        // Arrange
        String specialPath = "/path/with/special/chars/@#$%^&*()";
        String specialContent = "Content with special chars: \n\t\r\"'\\";

        // Act
        DepotGeneration generation = new DepotGeneration(specialPath, specialContent);

        // Assert
        assertEquals(specialPath, generation.getPath());
        assertEquals(specialContent, generation.getContent());
    }

    @Test
    void testConstructorWithLongStrings()
  {
        // Arrange
        String longPath = "a".repeat(10000);
        String longContent = "b".repeat(10000);

        // Act
        DepotGeneration generation = new DepotGeneration(longPath, longContent);

        // Assert
        assertEquals(longPath, generation.getPath());
        assertEquals(longContent, generation.getContent());
    }
}

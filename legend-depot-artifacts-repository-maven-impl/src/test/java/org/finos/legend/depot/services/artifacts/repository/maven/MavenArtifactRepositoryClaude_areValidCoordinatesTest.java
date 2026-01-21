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

package org.finos.legend.depot.services.artifacts.repository.maven;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MavenArtifactRepositoryClaude_areValidCoordinatesTest
{
    private MavenArtifactRepository repository;

    @BeforeEach
    void setUp()
    {
        repository = new MavenArtifactRepository(null);
    }

    @Test
    @DisplayName("Test areValidCoordinates with valid group and artifact")
    void testAreValidCoordinatesWithValidInputs()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with null group")
    void testAreValidCoordinatesWithNullGroup()
    {
        // Arrange
        String group = null;
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with null artifact")
    void testAreValidCoordinatesWithNullArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = null;

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with both null")
    void testAreValidCoordinatesWithBothNull()
    {
        // Arrange
        String group = null;
        String artifact = null;

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon in group")
    void testAreValidCoordinatesWithColonInGroup()
    {
        // Arrange
        String group = "org.finos:legend";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon in artifact")
    void testAreValidCoordinatesWithColonInArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend:depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon in both group and artifact")
    void testAreValidCoordinatesWithColonInBoth()
    {
        // Arrange
        String group = "org:finos:legend";
        String artifact = "legend:depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with empty group")
    void testAreValidCoordinatesWithEmptyGroup()
    {
        // Arrange
        String group = "";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with empty artifact")
    void testAreValidCoordinatesWithEmptyArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with both empty strings")
    void testAreValidCoordinatesWithBothEmpty()
    {
        // Arrange
        String group = "";
        String artifact = "";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with complex valid group")
    void testAreValidCoordinatesWithComplexGroup()
    {
        // Arrange
        String group = "org.finos.legend.depot.services.artifacts";
        String artifact = "maven-repository";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with hyphens in artifact")
    void testAreValidCoordinatesWithHyphensInArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot-core-services";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with underscores in artifact")
    void testAreValidCoordinatesWithUnderscoresInArtifact()
    {
        // Arrange
        String group = "org.example";
        String artifact = "my_test_artifact";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with single character inputs")
    void testAreValidCoordinatesWithSingleCharacters()
    {
        // Arrange
        String group = "a";
        String artifact = "b";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with numbers in group and artifact")
    void testAreValidCoordinatesWithNumbers()
    {
        // Arrange
        String group = "org.example123";
        String artifact = "artifact456";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with multiple colons in group")
    void testAreValidCoordinatesWithMultipleColonsInGroup()
    {
        // Arrange
        String group = "org:finos:legend:depot";
        String artifact = "artifact";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with multiple colons in artifact")
    void testAreValidCoordinatesWithMultipleColonsInArtifact()
    {
        // Arrange
        String group = "org.example";
        String artifact = "my:test:artifact";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon at start of group")
    void testAreValidCoordinatesWithColonAtStartOfGroup()
    {
        // Arrange
        String group = ":org.finos.legend";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon at end of group")
    void testAreValidCoordinatesWithColonAtEndOfGroup()
    {
        // Arrange
        String group = "org.finos.legend:";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon at start of artifact")
    void testAreValidCoordinatesWithColonAtStartOfArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = ":legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with colon at end of artifact")
    void testAreValidCoordinatesWithColonAtEndOfArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot:";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with special characters excluding colon")
    void testAreValidCoordinatesWithSpecialCharacters()
    {
        // Arrange
        String group = "org.finos-legend";
        String artifact = "legend_depot@test";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with whitespace in group")
    void testAreValidCoordinatesWithWhitespaceInGroup()
    {
        // Arrange
        String group = "org finos legend";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with whitespace in artifact")
    void testAreValidCoordinatesWithWhitespaceInArtifact()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with only colon in group")
    void testAreValidCoordinatesWithOnlyColonInGroup()
    {
        // Arrange
        String group = ":";
        String artifact = "artifact";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with only colon in artifact")
    void testAreValidCoordinatesWithOnlyColonInArtifact()
    {
        // Arrange
        String group = "org.example";
        String artifact = ":";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates validates coordinates are usable in GAV format")
    void testAreValidCoordinatesValidatesGAVUsability()
    {
        // Valid coordinates should not contain colons since colons are used as GAV separators

        // Valid case
        assertTrue(repository.areValidCoordinates("org.example", "artifact"));

        // Invalid cases - would create ambiguous GAV strings
        assertFalse(repository.areValidCoordinates("org:example", "artifact"));
        assertFalse(repository.areValidCoordinates("org.example", "art:ifact"));
        assertFalse(repository.areValidCoordinates("org:example", "art:ifact"));
    }

    @Test
    @DisplayName("Test areValidCoordinates with very long valid group and artifact")
    void testAreValidCoordinatesWithVeryLongStrings()
    {
        // Arrange
        String group = "org.finos.legend.depot.services.artifacts.repository.maven.implementation.core";
        String artifact = "very-long-artifact-name-with-multiple-segments-and-parts";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with forward slashes")
    void testAreValidCoordinatesWithForwardSlashes()
    {
        // Arrange
        String group = "org/finos/legend";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test areValidCoordinates with backslashes")
    void testAreValidCoordinatesWithBackslashes()
    {
        // Arrange
        String group = "org\\finos\\legend";
        String artifact = "legend-depot";

        // Act
        boolean result = repository.areValidCoordinates(group, artifact);

        // Assert
        assertTrue(result);
    }
}

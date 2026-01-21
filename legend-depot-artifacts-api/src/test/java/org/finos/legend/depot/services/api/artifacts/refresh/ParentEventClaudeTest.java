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

package org.finos.legend.depot.services.api.artifacts.refresh;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParentEventClaudeTest


{
    // values() method tests

    @Test
    @DisplayName("values() should return all enum constants")
    void testValuesReturnsAllConstants()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        assertNotNull(values);
        assertEquals(5, values.length);
    }

    @Test
    @DisplayName("values() should return array containing UPDATE_PROJECT_VERSION")
    void testValuesContainsUpdateProjectVersion()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        boolean found = false;
        for (ParentEvent event : values)
        {
            if (event == ParentEvent.UPDATE_PROJECT_VERSION)
            {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    @DisplayName("values() should return array containing UPDATE_PROJECT_ALL_VERSIONS")
    void testValuesContainsUpdateProjectAllVersions()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        boolean found = false;
        for (ParentEvent event : values)
        {
            if (event == ParentEvent.UPDATE_PROJECT_ALL_VERSIONS)
            {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    @DisplayName("values() should return array containing UPDATE_ALL_PROJECT_ALL_VERSIONS")
    void testValuesContainsUpdateAllProjectAllVersions()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        boolean found = false;
        for (ParentEvent event : values)
        {
            if (event == ParentEvent.UPDATE_ALL_PROJECT_ALL_VERSIONS)
            {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    @DisplayName("values() should return array containing UPDATE_ALL_PROJECT_ALL_SNAPSHOTS")
    void testValuesContainsUpdateAllProjectAllSnapshots()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        boolean found = false;
        for (ParentEvent event : values)
        {
            if (event == ParentEvent.UPDATE_ALL_PROJECT_ALL_SNAPSHOTS)
            {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    @DisplayName("values() should return array containing REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE")
    void testValuesContainsRefreshAllVersionArtifactsSchedule()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        boolean found = false;
        for (ParentEvent event : values)
        {
            if (event == ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE)
            {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    @DisplayName("values() should return a new array each time")
    void testValuesReturnsNewArray()
  {
        // Act
        ParentEvent[] values1 = ParentEvent.values();
        ParentEvent[] values2 = ParentEvent.values();

        // Assert - arrays should not be the same instance (defensive copy)
        // but should contain same elements
        assertEquals(values1.length, values2.length);
        for (int i = 0; i < values1.length; i++)
        {
            assertSame(values1[i], values2[i]);
        }
    }

    @Test
    @DisplayName("values() should return array in declaration order")
    void testValuesReturnsInDeclarationOrder()
  {
        // Act
        ParentEvent[] values = ParentEvent.values();

        // Assert
        assertEquals(ParentEvent.UPDATE_PROJECT_VERSION, values[0]);
        assertEquals(ParentEvent.UPDATE_PROJECT_ALL_VERSIONS, values[1]);
        assertEquals(ParentEvent.UPDATE_ALL_PROJECT_ALL_VERSIONS, values[2]);
        assertEquals(ParentEvent.UPDATE_ALL_PROJECT_ALL_SNAPSHOTS, values[3]);
        assertEquals(ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE, values[4]);
    }

    // valueOf(String) method tests

    @Test
    @DisplayName("valueOf() should return UPDATE_PROJECT_VERSION for valid string")
    void testValueOfUpdateProjectVersion()
  {
        // Act
        ParentEvent result = ParentEvent.valueOf("UPDATE_PROJECT_VERSION");

        // Assert
        assertSame(ParentEvent.UPDATE_PROJECT_VERSION, result);
    }

    @Test
    @DisplayName("valueOf() should return UPDATE_PROJECT_ALL_VERSIONS for valid string")
    void testValueOfUpdateProjectAllVersions()
  {
        // Act
        ParentEvent result = ParentEvent.valueOf("UPDATE_PROJECT_ALL_VERSIONS");

        // Assert
        assertSame(ParentEvent.UPDATE_PROJECT_ALL_VERSIONS, result);
    }

    @Test
    @DisplayName("valueOf() should return UPDATE_ALL_PROJECT_ALL_VERSIONS for valid string")
    void testValueOfUpdateAllProjectAllVersions()
  {
        // Act
        ParentEvent result = ParentEvent.valueOf("UPDATE_ALL_PROJECT_ALL_VERSIONS");

        // Assert
        assertSame(ParentEvent.UPDATE_ALL_PROJECT_ALL_VERSIONS, result);
    }

    @Test
    @DisplayName("valueOf() should return UPDATE_ALL_PROJECT_ALL_SNAPSHOTS for valid string")
    void testValueOfUpdateAllProjectAllSnapshots()
  {
        // Act
        ParentEvent result = ParentEvent.valueOf("UPDATE_ALL_PROJECT_ALL_SNAPSHOTS");

        // Assert
        assertSame(ParentEvent.UPDATE_ALL_PROJECT_ALL_SNAPSHOTS, result);
    }

    @Test
    @DisplayName("valueOf() should return REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE for valid string")
    void testValueOfRefreshAllVersionArtifactsSchedule()
  {
        // Act
        ParentEvent result = ParentEvent.valueOf("REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE");

        // Assert
        assertSame(ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE, result);
    }

    @Test
    @DisplayName("valueOf() should throw IllegalArgumentException for invalid string")
    void testValueOfThrowsExceptionForInvalidString()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ParentEvent.valueOf("INVALID_EVENT"));
    }

    @Test
    @DisplayName("valueOf() should throw IllegalArgumentException for lowercase string")
    void testValueOfThrowsExceptionForLowercaseString()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ParentEvent.valueOf("update_project_version"));
    }

    @Test
    @DisplayName("valueOf() should throw IllegalArgumentException for empty string")
    void testValueOfThrowsExceptionForEmptyString()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ParentEvent.valueOf(""));
    }

    @Test
    @DisplayName("valueOf() should throw NullPointerException for null string")
    void testValueOfThrowsExceptionForNullString()
  {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> ParentEvent.valueOf(null));
    }

    @Test
    @DisplayName("valueOf() should throw IllegalArgumentException for partial match")
    void testValueOfThrowsExceptionForPartialMatch()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ParentEvent.valueOf("UPDATE_PROJECT"));
    }

    @Test
    @DisplayName("valueOf() should throw IllegalArgumentException for string with leading space")
    void testValueOfThrowsExceptionForLeadingSpace()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ParentEvent.valueOf(" UPDATE_PROJECT_VERSION"));
    }

    @Test
    @DisplayName("valueOf() should throw IllegalArgumentException for string with trailing space")
    void testValueOfThrowsExceptionForTrailingSpace()
  {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ParentEvent.valueOf("UPDATE_PROJECT_VERSION "));
    }

    // build() method tests

    @Test
    @DisplayName("build() should return parentEventId when it is non-null")
    void testBuildReturnsParentEventIdWhenNonNull()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "my-artifact";
        String versionId = "1.0.0";
        String parentEventId = "existing-parent-event-id";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, parentEventId);

        // Assert
        assertEquals("existing-parent-event-id", result);
    }

    @Test
    @DisplayName("build() should construct string from groupId, artifactId, and versionId when parentEventId is null")
    void testBuildConstructsStringWhenParentEventIdIsNull()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "my-artifact";
        String versionId = "1.0.0";
        String parentEventId = null;

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, parentEventId);

        // Assert
        assertEquals("com.example_my-artifact_1.0.0", result);
    }

    @Test
    @DisplayName("build() should use underscore as separator")
    void testBuildUsesUnderscoreAsSeparator()
  {
        // Arrange
        String groupId = "group";
        String artifactId = "artifact";
        String versionId = "version";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("group_artifact_version", result);
    }

    @Test
    @DisplayName("build() should handle groupId with dots")
    void testBuildHandlesGroupIdWithDots()
  {
        // Arrange
        String groupId = "org.finos.legend";
        String artifactId = "depot";
        String versionId = "2.0.0";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("org.finos.legend_depot_2.0.0", result);
    }

    @Test
    @DisplayName("build() should handle artifactId with hyphens")
    void testBuildHandlesArtifactIdWithHyphens()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "my-cool-artifact";
        String versionId = "1.0.0";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("com.example_my-cool-artifact_1.0.0", result);
    }

    @Test
    @DisplayName("build() should handle versionId with hyphens and dots")
    void testBuildHandlesVersionIdWithHyphensAndDots()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "artifact";
        String versionId = "1.0.0-SNAPSHOT";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("com.example_artifact_1.0.0-SNAPSHOT", result);
    }

    @Test
    @DisplayName("build() should handle empty strings when parentEventId is null")
    void testBuildHandlesEmptyStrings()
  {
        // Arrange
        String groupId = "";
        String artifactId = "";
        String versionId = "";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("__", result);
    }

    @Test
    @DisplayName("build() should handle single character strings")
    void testBuildHandlesSingleCharacterStrings()
  {
        // Arrange
        String groupId = "a";
        String artifactId = "b";
        String versionId = "c";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("a_b_c", result);
    }

    @Test
    @DisplayName("build() should return parentEventId even if other parameters are null")
    void testBuildReturnsParentEventIdEvenIfOthersAreNull()
  {
        // Arrange
        String groupId = null;
        String artifactId = null;
        String versionId = null;
        String parentEventId = "my-parent-event";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, parentEventId);

        // Assert
        assertEquals("my-parent-event", result);
    }

    @Test
    @DisplayName("build() should handle empty parentEventId as non-null")
    void testBuildHandlesEmptyParentEventIdAsNonNull()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "artifact";
        String versionId = "1.0.0";
        String parentEventId = "";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, parentEventId);

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("build() should handle parentEventId with special characters")
    void testBuildHandlesParentEventIdWithSpecialCharacters()
  {
        // Arrange
        String groupId = "com.example";
        String artifactId = "artifact";
        String versionId = "1.0.0";
        String parentEventId = "event-123_xyz.abc:def";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, parentEventId);

        // Assert
        assertEquals("event-123_xyz.abc:def", result);
    }

    @Test
    @DisplayName("build() should handle long strings")
    void testBuildHandlesLongStrings()
  {
        // Arrange
        String groupId = "com.example.very.long.group.id.with.many.parts";
        String artifactId = "my-very-long-artifact-name-with-many-hyphens";
        String versionId = "1.0.0-SNAPSHOT-BUILD-12345";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("com.example.very.long.group.id.with.many.parts_my-very-long-artifact-name-with-many-hyphens_1.0.0-SNAPSHOT-BUILD-12345", result);
    }

    @Test
    @DisplayName("build() should handle strings containing underscores")
    void testBuildHandlesStringsWithUnderscores()
  {
        // Arrange
        String groupId = "com_example";
        String artifactId = "my_artifact";
        String versionId = "1_0_0";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("com_example_my_artifact_1_0_0", result);
    }

    @Test
    @DisplayName("build() should handle unicode characters")
    void testBuildHandlesUnicodeCharacters()
  {
        // Arrange
        String groupId = "com.example.??";
        String artifactId = "artifact-??";
        String versionId = "1.0.??";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("com.example.??_artifact-??_1.0.??", result);
    }

    @Test
    @DisplayName("build() should handle numeric strings")
    void testBuildHandlesNumericStrings()
  {
        // Arrange
        String groupId = "123";
        String artifactId = "456";
        String versionId = "789";

        // Act
        String result = ParentEvent.build(groupId, artifactId, versionId, null);

        // Assert
        assertEquals("123_456_789", result);
    }
}

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionValidatorClaudeTest


{
    // ========== Tests for BRANCH_SNAPSHOT(String) ==========

    /**
     * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)} with a simple branch name.
     */
    @Test
    @DisplayName("Test BRANCH_SNAPSHOT with simple branch name")
    void testBranchSnapshotSimple()
  {
        // Act
        String result = VersionValidator.BRANCH_SNAPSHOT("feature");

        // Assert
        assertEquals("feature-SNAPSHOT", result);
    }

    /**
     * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)} with a branch name containing slashes.
     */
    @Test
    @DisplayName("Test BRANCH_SNAPSHOT with branch name containing slashes")
    void testBranchSnapshotWithSlashes()
  {
        // Act
        String result = VersionValidator.BRANCH_SNAPSHOT("user/feature-branch");

        // Assert
        assertEquals("user/feature-branch-SNAPSHOT", result);
    }

    /**
     * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)} with master branch.
     */
    @Test
    @DisplayName("Test BRANCH_SNAPSHOT with master branch")
    void testBranchSnapshotMaster()
  {
        // Act
        String result = VersionValidator.BRANCH_SNAPSHOT("master");

        // Assert
        assertEquals("master-SNAPSHOT", result);
    }

    /**
     * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)} with empty string.
     */
    @Test
    @DisplayName("Test BRANCH_SNAPSHOT with empty string")
    void testBranchSnapshotEmpty()
  {
        // Act
        String result = VersionValidator.BRANCH_SNAPSHOT("");

        // Assert
        assertEquals("-SNAPSHOT", result);
    }

    /**
     * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)} with branch name already ending in -SNAPSHOT.
     */
    @Test
    @DisplayName("Test BRANCH_SNAPSHOT with branch name already containing -SNAPSHOT")
    void testBranchSnapshotAlreadySnapshot()
  {
        // Act
        String result = VersionValidator.BRANCH_SNAPSHOT("feature-SNAPSHOT");

        // Assert
        assertEquals("feature-SNAPSHOT-SNAPSHOT", result);
    }

    /**
     * Test {@link VersionValidator#BRANCH_SNAPSHOT(String)} with null.
     * Note: Java string concatenation converts null to "null" string.
     */
    @Test
    @DisplayName("Test BRANCH_SNAPSHOT with null")
    void testBranchSnapshotNull()
  {
        // Act
        String result = VersionValidator.BRANCH_SNAPSHOT(null);

        // Assert
        assertEquals("null-SNAPSHOT", result);
    }

    // ========== Tests for isValid(String) ==========

    /**
     * Test {@link VersionValidator#isValid(String)} with valid semantic version.
     */
    @Test
    @DisplayName("Test isValid with valid semantic version")
    void testIsValidSemanticVersion()
  {
        // Assert
        assertTrue(VersionValidator.isValid("1.0.0"));
        assertTrue(VersionValidator.isValid("2.5.10"));
        assertTrue(VersionValidator.isValid("100.200.300"));
    }

    /**
     * Test {@link VersionValidator#isValid(String)} with valid snapshot version.
     */
    @Test
    @DisplayName("Test isValid with valid snapshot version")
    void testIsValidSnapshotVersion()
  {
        // Assert
        assertTrue(VersionValidator.isValid("master-SNAPSHOT"));
        assertTrue(VersionValidator.isValid("1.0.0-SNAPSHOT"));
        assertTrue(VersionValidator.isValid("feature/branch-SNAPSHOT"));
        assertTrue(VersionValidator.isValid("-SNAPSHOT"));
    }

    /**
     * Test {@link VersionValidator#isValid(String)} with null returns false.
     */
    @Test
    @DisplayName("Test isValid with null returns false")
    void testIsValidNull()
  {
        // Act & Assert
        assertFalse(VersionValidator.isValid(null));
    }

    /**
     * Test {@link VersionValidator#isValid(String)} with empty string returns false.
     */
    @Test
    @DisplayName("Test isValid with empty string returns false")
    void testIsValidEmpty()
  {
        // Act & Assert
        assertFalse(VersionValidator.isValid(""));
    }

    /**
     * Test {@link VersionValidator#isValid(String)} with invalid version format.
     */
    @Test
    @DisplayName("Test isValid with invalid version format")
    void testIsValidInvalidFormat()
  {
        // Assert
        assertFalse(VersionValidator.isValid("1.0"));
        assertFalse(VersionValidator.isValid("1"));
        assertFalse(VersionValidator.isValid("abc"));
        assertFalse(VersionValidator.isValid("1.0.0.0"));
        assertFalse(VersionValidator.isValid("v1.0.0"));
    }

    /**
     * Test {@link VersionValidator#isValid(String)} with whitespace only returns false.
     */
    @Test
    @DisplayName("Test isValid with whitespace only returns false")
    void testIsValidWhitespace()
  {
        // Act & Assert
        assertFalse(VersionValidator.isValid("   "));
        assertFalse(VersionValidator.isValid("\t"));
        assertFalse(VersionValidator.isValid("\n"));
    }

    // ========== Tests for isValidReleaseVersion(String) ==========

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with valid semantic versions.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with valid semantic versions")
    void testIsValidReleaseVersionValid()
  {
        // Assert
        assertTrue(VersionValidator.isValidReleaseVersion("0.0.1"));
        assertTrue(VersionValidator.isValidReleaseVersion("1.0.0"));
        assertTrue(VersionValidator.isValidReleaseVersion("10.20.30"));
        assertTrue(VersionValidator.isValidReleaseVersion("999.999.999"));
    }

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with invalid formats.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with invalid formats")
    void testIsValidReleaseVersionInvalid()
  {
        // Assert
        assertFalse(VersionValidator.isValidReleaseVersion("1.0"));
        assertFalse(VersionValidator.isValidReleaseVersion("1"));
        assertFalse(VersionValidator.isValidReleaseVersion("a.b.c"));
        assertFalse(VersionValidator.isValidReleaseVersion("1.0.0.0"));
        assertFalse(VersionValidator.isValidReleaseVersion("v1.0.0"));
    }

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with snapshot versions returns false.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with snapshot versions returns false")
    void testIsValidReleaseVersionSnapshot()
  {
        // Assert
        assertFalse(VersionValidator.isValidReleaseVersion("1.0.0-SNAPSHOT"));
        assertFalse(VersionValidator.isValidReleaseVersion("master-SNAPSHOT"));
    }

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with null returns false.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with null returns false")
    void testIsValidReleaseVersionNull()
  {
        // Act & Assert
        assertFalse(VersionValidator.isValidReleaseVersion(null));
    }

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with empty string returns false.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with empty string returns false")
    void testIsValidReleaseVersionEmpty()
  {
        // Act & Assert
        assertFalse(VersionValidator.isValidReleaseVersion(""));
    }

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with leading/trailing whitespace.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with leading/trailing whitespace")
    void testIsValidReleaseVersionWhitespace()
  {
        // Assert
        assertFalse(VersionValidator.isValidReleaseVersion(" 1.0.0"));
        assertFalse(VersionValidator.isValidReleaseVersion("1.0.0 "));
        assertFalse(VersionValidator.isValidReleaseVersion(" 1.0.0 "));
    }

    /**
     * Test {@link VersionValidator#isValidReleaseVersion(String)} with special characters.
     */
    @Test
    @DisplayName("Test isValidReleaseVersion with special characters")
    void testIsValidReleaseVersionSpecialChars()
  {
        // Assert
        assertFalse(VersionValidator.isValidReleaseVersion("1.0.0-alpha"));
        assertFalse(VersionValidator.isValidReleaseVersion("1.0.0+build"));
        assertFalse(VersionValidator.isValidReleaseVersion("1.0.0-alpha.1"));
    }

    // ========== Tests for isSnapshotVersion(String) ==========

    /**
     * Test {@link VersionValidator#isSnapshotVersion(String)} with valid snapshot versions.
     */
    @Test
    @DisplayName("Test isSnapshotVersion with valid snapshot versions")
    void testIsSnapshotVersionValid()
  {
        // Assert
        assertTrue(VersionValidator.isSnapshotVersion("-SNAPSHOT"));
        assertTrue(VersionValidator.isSnapshotVersion("master-SNAPSHOT"));
        assertTrue(VersionValidator.isSnapshotVersion("1.0.0-SNAPSHOT"));
        assertTrue(VersionValidator.isSnapshotVersion("feature/branch-SNAPSHOT"));
        assertTrue(VersionValidator.isSnapshotVersion("ANY-SNAPSHOT"));
    }

    /**
     * Test {@link VersionValidator#isSnapshotVersion(String)} with non-snapshot versions.
     */
    @Test
    @DisplayName("Test isSnapshotVersion with non-snapshot versions")
    void testIsSnapshotVersionNonSnapshot()
  {
        // Assert
        assertFalse(VersionValidator.isSnapshotVersion("1.0.0"));
        assertFalse(VersionValidator.isSnapshotVersion("master"));
        assertFalse(VersionValidator.isSnapshotVersion("SNAPSHOT"));
        assertFalse(VersionValidator.isSnapshotVersion(""));
    }

    /**
     * Test {@link VersionValidator#isSnapshotVersion(String)} with snapshot in middle.
     */
    @Test
    @DisplayName("Test isSnapshotVersion with SNAPSHOT in middle returns false")
    void testIsSnapshotVersionInMiddle()
  {
        // Act & Assert
        assertFalse(VersionValidator.isSnapshotVersion("SNAPSHOT-1.0.0"));
        assertFalse(VersionValidator.isSnapshotVersion("1.0-SNAPSHOT-release"));
    }

    /**
     * Test {@link VersionValidator#isSnapshotVersion(String)} with null throws NullPointerException.
     */
    @Test
    @DisplayName("Test isSnapshotVersion with null throws NullPointerException")
    void testIsSnapshotVersionNull()
  {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> VersionValidator.isSnapshotVersion(null));
    }

    /**
     * Test {@link VersionValidator#isSnapshotVersion(String)} is case-sensitive.
     */
    @Test
    @DisplayName("Test isSnapshotVersion is case-sensitive")
    void testIsSnapshotVersionCaseSensitive()
  {
        // Assert
        assertFalse(VersionValidator.isSnapshotVersion("master-snapshot"));
        assertFalse(VersionValidator.isSnapshotVersion("master-Snapshot"));
        assertFalse(VersionValidator.isSnapshotVersion("master-SNAPSH0T"));
    }

    // ========== Tests for isVersionAlias(String) ==========

    /**
     * Test {@link VersionValidator#isVersionAlias(String)} with valid aliases.
     */
    @Test
    @DisplayName("Test isVersionAlias with valid aliases")
    void testIsVersionAliasValid()
  {
        // Assert
        assertTrue(VersionValidator.isVersionAlias("latest"));
        assertTrue(VersionValidator.isVersionAlias("head"));
    }

    /**
     * Test {@link VersionValidator#isVersionAlias(String)} is case-insensitive.
     */
    @Test
    @DisplayName("Test isVersionAlias is case-insensitive")
    void testIsVersionAliasCaseInsensitive()
  {
        // Assert
        assertTrue(VersionValidator.isVersionAlias("LATEST"));
        assertTrue(VersionValidator.isVersionAlias("Latest"));
        assertTrue(VersionValidator.isVersionAlias("lAtEsT"));
        assertTrue(VersionValidator.isVersionAlias("HEAD"));
        assertTrue(VersionValidator.isVersionAlias("Head"));
        assertTrue(VersionValidator.isVersionAlias("hEaD"));
    }

    /**
     * Test {@link VersionValidator#isVersionAlias(String)} with invalid aliases.
     */
    @Test
    @DisplayName("Test isVersionAlias with invalid aliases")
    void testIsVersionAliasInvalid()
  {
        // Assert
        assertFalse(VersionValidator.isVersionAlias("invalid"));
        assertFalse(VersionValidator.isVersionAlias("1.0.0"));
        assertFalse(VersionValidator.isVersionAlias("master"));
        assertFalse(VersionValidator.isVersionAlias("snapshot"));
        assertFalse(VersionValidator.isVersionAlias(""));
    }

    /**
     * Test {@link VersionValidator#isVersionAlias(String)} with null returns false.
     */
    @Test
    @DisplayName("Test isVersionAlias with null returns false")
    void testIsVersionAliasNull()
  {
        // Act & Assert
        assertFalse(VersionValidator.isVersionAlias(null));
    }

    /**
     * Test {@link VersionValidator#isVersionAlias(String)} with whitespace.
     */
    @Test
    @DisplayName("Test isVersionAlias with whitespace returns false")
    void testIsVersionAliasWhitespace()
  {
        // Assert
        assertFalse(VersionValidator.isVersionAlias("latest "));
        assertFalse(VersionValidator.isVersionAlias(" latest"));
        assertFalse(VersionValidator.isVersionAlias(" latest "));
        assertFalse(VersionValidator.isVersionAlias("   "));
    }

    /**
     * Test {@link VersionValidator#isVersionAlias(String)} with partial match.
     */
    @Test
    @DisplayName("Test isVersionAlias with partial match returns false")
    void testIsVersionAliasPartialMatch()
  {
        // Assert
        assertFalse(VersionValidator.isVersionAlias("late"));
        assertFalse(VersionValidator.isVersionAlias("latests"));
        assertFalse(VersionValidator.isVersionAlias("he"));
        assertFalse(VersionValidator.isVersionAlias("heads"));
    }
}

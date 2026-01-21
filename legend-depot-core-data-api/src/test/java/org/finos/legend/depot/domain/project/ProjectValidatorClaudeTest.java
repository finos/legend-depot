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

package org.finos.legend.depot.domain.project;

import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectValidatorClaudeTest


{
    // Tests for isValidProjectId method

    @Test
    void testIsValidProjectId_validSingleDigit()
  {
        assertTrue(ProjectValidator.isValidProjectId("PROD-0"));
        assertTrue(ProjectValidator.isValidProjectId("PROD-1"));
        assertTrue(ProjectValidator.isValidProjectId("PROD-9"));
    }

    @Test
    void testIsValidProjectId_validMultipleDigits()
  {
        assertTrue(ProjectValidator.isValidProjectId("PROD-10"));
        assertTrue(ProjectValidator.isValidProjectId("PROD-123"));
        assertTrue(ProjectValidator.isValidProjectId("PROD-999999"));
    }

    @Test
    void testIsValidProjectId_null()
  {
        assertFalse(ProjectValidator.isValidProjectId(null));
    }

    @Test
    void testIsValidProjectId_emptyString()
  {
        assertFalse(ProjectValidator.isValidProjectId(""));
    }

    @Test
    void testIsValidProjectId_lowercase()
  {
        assertFalse(ProjectValidator.isValidProjectId("prod-123"));
    }

    @Test
    void testIsValidProjectId_mixedCase()
  {
        assertFalse(ProjectValidator.isValidProjectId("Prod-123"));
        assertFalse(ProjectValidator.isValidProjectId("PROD-123a"));
    }

    @Test
    void testIsValidProjectId_missingPrefix()
  {
        assertFalse(ProjectValidator.isValidProjectId("123"));
        assertFalse(ProjectValidator.isValidProjectId("-123"));
    }

    @Test
    void testIsValidProjectId_missingNumber()
  {
        assertFalse(ProjectValidator.isValidProjectId("PROD-"));
        assertFalse(ProjectValidator.isValidProjectId("PROD"));
    }

    @Test
    void testIsValidProjectId_extraCharacters()
  {
        assertFalse(ProjectValidator.isValidProjectId("PROD-123-"));
        assertFalse(ProjectValidator.isValidProjectId("PROD-123-456"));
        assertFalse(ProjectValidator.isValidProjectId("XPROD-123"));
        assertFalse(ProjectValidator.isValidProjectId("PROD-123X"));
    }

    @Test
    void testIsValidProjectId_withSpaces()
  {
        assertFalse(ProjectValidator.isValidProjectId("PROD- 123"));
        assertFalse(ProjectValidator.isValidProjectId("PROD -123"));
        assertFalse(ProjectValidator.isValidProjectId(" PROD-123"));
        assertFalse(ProjectValidator.isValidProjectId("PROD-123 "));
    }

    @Test
    void testIsValidProjectId_specialCharacters()
  {
        assertFalse(ProjectValidator.isValidProjectId("PROD_123"));
        assertFalse(ProjectValidator.isValidProjectId("PROD@123"));
        assertFalse(ProjectValidator.isValidProjectId("PROD#123"));
    }

    @Test
    void testIsValidProjectId_leadingZeros()
  {
        // Leading zeros should still be valid as they match the digit pattern
        assertTrue(ProjectValidator.isValidProjectId("PROD-001"));
        assertTrue(ProjectValidator.isValidProjectId("PROD-0000"));
    }

    // Tests for isValid method

    @Test
    void testIsValid_allFieldsValid()
  {
        StoreProjectData projectData = new StoreProjectData("PROD-123", "org.example", "my-artifact");
        assertTrue(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_invalidProjectId()
  {
        // Invalid project ID (missing PROD- prefix)
        StoreProjectData projectData = new StoreProjectData("123", "org.example", "my-artifact");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_nullProjectId()
  {
        StoreProjectData projectData = new StoreProjectData(null, "org.example", "my-artifact");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_invalidGroupId()
  {
        // Empty group ID
        StoreProjectData projectData = new StoreProjectData("PROD-123", "", "my-artifact");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_nullGroupId()
  {
        StoreProjectData projectData = new StoreProjectData("PROD-123", null, "my-artifact");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_invalidGroupIdFormat()
  {
        // Group ID with invalid characters
        StoreProjectData projectData = new StoreProjectData("PROD-123", "org-example", "my-artifact");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_invalidArtifactId()
  {
        // Empty artifact ID
        StoreProjectData projectData = new StoreProjectData("PROD-123", "org.example", "");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_nullArtifactId()
  {
        StoreProjectData projectData = new StoreProjectData("PROD-123", "org.example", null);
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_invalidArtifactIdFormat()
  {
        // Artifact ID starting with uppercase
        StoreProjectData projectData = new StoreProjectData("PROD-123", "org.example", "MyArtifact");
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_allFieldsNull()
  {
        StoreProjectData projectData = new StoreProjectData(null, null, null);
        assertFalse(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_validComplexGroupId()
  {
        StoreProjectData projectData = new StoreProjectData("PROD-456", "org.finos.legend.depot", "my-artifact");
        assertTrue(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_validComplexArtifactId()
  {
        StoreProjectData projectData = new StoreProjectData("PROD-789", "org.example", "my-complex-artifact-name");
        assertTrue(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_validArtifactIdWithUnderscores()
  {
        StoreProjectData projectData = new StoreProjectData("PROD-111", "org.example", "my_artifact_name");
        assertTrue(ProjectValidator.isValid(projectData));
    }

    @Test
    void testIsValid_edgeCaseValidProject()
  {
        // Minimum valid values for all fields
        StoreProjectData projectData = new StoreProjectData("PROD-0", "a", "a");
        assertTrue(ProjectValidator.isValid(projectData));
    }
}

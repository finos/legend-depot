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

package org.finos.legend.depot.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CoordinateValidatorClaudeTest


{
    // Tests for isValidArtifactId

    @Test
    public void testIsValidArtifactIdWithNull()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId(null));
    }

    @Test
    public void testIsValidArtifactIdWithEmptyString()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId(""));
    }

    @Test
    public void testIsValidArtifactIdWithSimpleLowercase()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact"));
    }

    @Test
    public void testIsValidArtifactIdWithLowercaseAndDigits()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact123"));
    }

    @Test
    public void testIsValidArtifactIdWithLowercaseAndUnderscore()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact_name"));
    }

    @Test
    public void testIsValidArtifactIdWithMultipleUnderscores()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact_name_test"));
    }

    @Test
    public void testIsValidArtifactIdWithHyphenatedParts()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact-name"));
    }

    @Test
    public void testIsValidArtifactIdWithMultipleHyphenatedParts()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact-name-test"));
    }

    @Test
    public void testIsValidArtifactIdWithHyphenAndUnderscore()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact_name-test_part"));
    }

    @Test
    public void testIsValidArtifactIdWithDigitsInMiddle()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact123name"));
    }

    @Test
    public void testIsValidArtifactIdWithDigitsAfterHyphen()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("artifact-test123"));
    }

    @Test
    public void testIsValidArtifactIdStartingWithUppercase()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("Artifact"));
    }

    @Test
    public void testIsValidArtifactIdWithUppercaseInMiddle()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artiFact"));
    }

    @Test
    public void testIsValidArtifactIdStartingWithDigit()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("123artifact"));
    }

    @Test
    public void testIsValidArtifactIdStartingWithUnderscore()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("_artifact"));
    }

    @Test
    public void testIsValidArtifactIdStartingWithHyphen()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("-artifact"));
    }

    @Test
    public void testIsValidArtifactIdEndingWithHyphen()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact-"));
    }

    @Test
    public void testIsValidArtifactIdWithConsecutiveHyphens()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact--name"));
    }

    @Test
    public void testIsValidArtifactIdWithSpecialCharacters()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact@name"));
    }

    @Test
    public void testIsValidArtifactIdWithDot()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact.name"));
    }

    @Test
    public void testIsValidArtifactIdWithSpace()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact name"));
    }

    @Test
    public void testIsValidArtifactIdPartAfterHyphenStartsWithDigit()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact-123"));
    }

    @Test
    public void testIsValidArtifactIdPartAfterHyphenStartsWithUnderscore()
  {
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("artifact-_test"));
    }

    @Test
    public void testIsValidArtifactIdSingleLetter()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("a"));
    }

    @Test
    public void testIsValidArtifactIdComplexValidPattern()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("legend_depot-model_test123-name456"));
    }

    @Test
    public void testIsValidArtifactIdWithOnlyDigitsAfterFirstChar()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("a123456"));
    }

    @Test
    public void testIsValidArtifactIdWithOnlyUnderscoresAfterFirstChar()
  {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("a______"));
    }

    // Tests for isValidGroupId

    @Test
    public void testIsValidGroupIdWithNull()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId(null));
    }

    @Test
    public void testIsValidGroupIdWithEmptyString()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId(""));
    }

    @Test
    public void testIsValidGroupIdWithSimpleName()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("group"));
    }

    @Test
    public void testIsValidGroupIdWithDottedNotation()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("com.example.group"));
    }

    @Test
    public void testIsValidGroupIdWithMultipleLevels()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("org.finos.legend.depot"));
    }

    @Test
    public void testIsValidGroupIdWithUnderscore()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("com.example_group"));
    }

    @Test
    public void testIsValidGroupIdWithDigits()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("com.example123"));
    }

    @Test
    public void testIsValidGroupIdWithMixedCase()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("com.Example.Group"));
    }

    @Test
    public void testIsValidGroupIdStartingWithDigit()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("123group"));
    }

    @Test
    public void testIsValidGroupIdWithDigitAfterDot()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.123example"));
    }

    @Test
    public void testIsValidGroupIdWithHyphen()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.example-group"));
    }

    @Test
    public void testIsValidGroupIdWithSpecialCharacters()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.example@group"));
    }

    @Test
    public void testIsValidGroupIdStartingWithDot()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId(".com.example"));
    }

    @Test
    public void testIsValidGroupIdEndingWithDot()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.example."));
    }

    @Test
    public void testIsValidGroupIdWithConsecutiveDots()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com..example"));
    }

    @Test
    public void testIsValidGroupIdWithJavaKeyword()
  {
        // Java keywords are not valid identifiers
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("public"));
    }

    @Test
    public void testIsValidGroupIdWithJavaKeywordInPath()
  {
        // Java keywords in qualified names are not valid
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.public.example"));
    }

    @Test
    public void testIsValidGroupIdWithSingleCharacter()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("a"));
    }

    @Test
    public void testIsValidGroupIdWithUnderscoreStart()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("_group"));
    }

    @Test
    public void testIsValidGroupIdWithDollarSign()
  {
        // Dollar signs are valid in Java identifiers
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("com.$example"));
    }

    @Test
    public void testIsValidGroupIdWithOnlyUnderscore()
  {
        // A single underscore is not a valid Java identifier in Java 9+
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("_"));
    }

    @Test
    public void testIsValidGroupIdWithOnlyDollar()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("$"));
    }

    @Test
    public void testIsValidGroupIdRealWorldExample()
  {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("org.finos.legend.depot.domain"));
    }

    @Test
    public void testIsValidGroupIdWithSpace()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.example group"));
    }

    @Test
    public void testIsValidGroupIdWithTab()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.example\tgroup"));
    }

    @Test
    public void testIsValidGroupIdWithNewline()
  {
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("com.example\ngroup"));
    }
}

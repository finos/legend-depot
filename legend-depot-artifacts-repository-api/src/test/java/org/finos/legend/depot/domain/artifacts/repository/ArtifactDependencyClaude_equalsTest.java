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

package org.finos.legend.depot.domain.artifacts.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArtifactDependencyClaude_equalsTest
{
    @Test
    public void testEqualsWithSameInstance()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertTrue(dependency.equals(dependency));
    }

    @Test
    public void testEqualsWithEqualInstances()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertTrue(dependency1.equals(dependency2));
        Assertions.assertTrue(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithNull()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency.equals(null));
    }

    @Test
    public void testEqualsWithDifferentClass()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        String notADependency = "com.example:test-artifact:1.0.0";

        Assertions.assertFalse(dependency.equals(notADependency));
    }

    @Test
    public void testEqualsWithDifferentGroupId()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("org.example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
        Assertions.assertFalse(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithDifferentArtifactId()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "other-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
        Assertions.assertFalse(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithDifferentVersion()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "2.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
        Assertions.assertFalse(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithBothGroupIdsNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency(null, "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency(null, "test-artifact", "1.0.0");

        Assertions.assertTrue(dependency1.equals(dependency2));
        Assertions.assertTrue(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithBothArtifactIdsNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", null, "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", null, "1.0.0");

        Assertions.assertTrue(dependency1.equals(dependency2));
        Assertions.assertTrue(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithBothVersionsNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", null);
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", null);

        Assertions.assertTrue(dependency1.equals(dependency2));
        Assertions.assertTrue(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithAllFieldsNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency(null, null, null);
        ArtifactDependency dependency2 = new ArtifactDependency(null, null, null);

        Assertions.assertTrue(dependency1.equals(dependency2));
        Assertions.assertTrue(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithOneGroupIdNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency(null, "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
        Assertions.assertFalse(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithOneArtifactIdNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", null, "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
        Assertions.assertFalse(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsWithOneVersionNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", null);
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
        Assertions.assertFalse(dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsIsSymmetric()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertEquals(dependency1.equals(dependency2), dependency2.equals(dependency1));
    }

    @Test
    public void testEqualsIsTransitive()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency3 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertTrue(dependency1.equals(dependency2));
        Assertions.assertTrue(dependency2.equals(dependency3));
        Assertions.assertTrue(dependency1.equals(dependency3));
    }

    @Test
    public void testEqualsIsConsistent()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        boolean firstCall = dependency1.equals(dependency2);
        boolean secondCall = dependency1.equals(dependency2);
        boolean thirdCall = dependency1.equals(dependency2);

        Assertions.assertEquals(firstCall, secondCall);
        Assertions.assertEquals(secondCall, thirdCall);
    }

    @Test
    public void testEqualsWithEmptyStrings()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("", "", "");
        ArtifactDependency dependency2 = new ArtifactDependency("", "", "");

        Assertions.assertTrue(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsWithEmptyStringVsNull()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("", "", "");
        ArtifactDependency dependency2 = new ArtifactDependency(null, null, null);

        Assertions.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsWithWhitespace()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example ", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsWithDifferentCasing()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.Example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsWithComplexEqualInstances()
    {
        ArtifactDependency dependency1 = new ArtifactDependency(
            "org.finos.legend.depot",
            "legend-depot-artifacts-repository-api",
            "2.65.1-SNAPSHOT"
        );
        ArtifactDependency dependency2 = new ArtifactDependency(
            "org.finos.legend.depot",
            "legend-depot-artifacts-repository-api",
            "2.65.1-SNAPSHOT"
        );

        Assertions.assertTrue(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsWithSnapshotVsReleaseVersion()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0-SNAPSHOT");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsHashCodeConsistency()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        if (dependency1.equals(dependency2))
        {
            Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
        }
    }

    @Test
    public void testEqualsWithMultipleDifferentFields()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("org.other", "different-artifact", "2.0.0");

        Assertions.assertFalse(dependency1.equals(dependency2));
    }

    @Test
    public void testEqualsWithSpecialCharacters()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example-test", "test_artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example-test", "test_artifact", "1.0.0");

        Assertions.assertTrue(dependency1.equals(dependency2));
    }
}

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArtifactDependencyClaude_hashCodeTest
{
    @Test
    public void testHashCodeReturnsConsistentValue()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        int firstCall = dependency.hashCode();
        int secondCall = dependency.hashCode();
        int thirdCall = dependency.hashCode();

        Assertions.assertEquals(firstCall, secondCall);
        Assertions.assertEquals(secondCall, thirdCall);
    }

    @Test
    public void testHashCodeForEqualObjects()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeForDifferentGroupId()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("org.example", "test-artifact", "1.0.0");

        Assertions.assertNotEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeForDifferentArtifactId()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "other-artifact", "1.0.0");

        Assertions.assertNotEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeForDifferentVersion()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "2.0.0");

        Assertions.assertNotEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeWithNullGroupId()
    {
        ArtifactDependency dependency = new ArtifactDependency(null, "test-artifact", "1.0.0");

        int hashCode = dependency.hashCode();
        Assertions.assertNotNull(hashCode);
    }

    @Test
    public void testHashCodeWithNullArtifactId()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", null, "1.0.0");

        int hashCode = dependency.hashCode();
        Assertions.assertNotNull(hashCode);
    }

    @Test
    public void testHashCodeWithNullVersion()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", null);

        int hashCode = dependency.hashCode();
        Assertions.assertNotNull(hashCode);
    }

    @Test
    public void testHashCodeWithAllNullFields()
    {
        ArtifactDependency dependency = new ArtifactDependency(null, null, null);

        int hashCode = dependency.hashCode();
        Assertions.assertNotNull(hashCode);
    }

    @Test
    public void testHashCodeForEqualObjectsWithNullFields()
    {
        ArtifactDependency dependency1 = new ArtifactDependency(null, "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency(null, "test-artifact", "1.0.0");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeInHashMap()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Map<ArtifactDependency, String> map = new HashMap<>();
        map.put(dependency1, "value1");

        Assertions.assertTrue(map.containsKey(dependency2));
        Assertions.assertEquals("value1", map.get(dependency2));
    }

    @Test
    public void testHashCodeInHashSet()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Set<ArtifactDependency> set = new HashSet<>();
        set.add(dependency1);

        Assertions.assertTrue(set.contains(dependency2));
        Assertions.assertEquals(1, set.size());

        set.add(dependency2);
        Assertions.assertEquals(1, set.size());
    }

    @Test
    public void testHashCodeForMultipleDistinctObjects()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "artifact1", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "artifact2", "1.0.0");
        ArtifactDependency dependency3 = new ArtifactDependency("com.example", "artifact3", "1.0.0");

        Set<ArtifactDependency> set = new HashSet<>();
        set.add(dependency1);
        set.add(dependency2);
        set.add(dependency3);

        Assertions.assertEquals(3, set.size());
    }

    @Test
    public void testHashCodeWithEmptyStrings()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("", "", "");
        ArtifactDependency dependency2 = new ArtifactDependency("", "", "");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeWithMixedNullAndEmptyFields()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("", null, "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("", null, "1.0.0");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeWithWhitespace()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example ", "test-artifact", "1.0.0");

        Assertions.assertNotEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeWithDifferentCasing()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.Example", "test-artifact", "1.0.0");

        Assertions.assertNotEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeWithSnapshotVersion()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0-SNAPSHOT");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0-SNAPSHOT");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeSnapshotVsRelease()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0-SNAPSHOT");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        Assertions.assertNotEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeWithSpecialCharacters()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example-test", "test_artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example-test", "test_artifact", "1.0.0");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeForComplexRealWorldExample()
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

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeDistributionInHashSet()
    {
        Set<ArtifactDependency> set = new HashSet<>();

        for (int i = 0; i < 100; i++)
        {
            set.add(new ArtifactDependency("com.example", "artifact" + i, "1.0.0"));
        }

        Assertions.assertEquals(100, set.size());
    }

    @Test
    public void testHashCodeWithLongValues()
    {
        ArtifactDependency dependency1 = new ArtifactDependency(
            "com.example.very.long.group.id.with.many.packages",
            "artifact-with-very-long-name-for-testing",
            "1.0.0.0.0.0.0.Final.Release.Special.Build.12345"
        );
        ArtifactDependency dependency2 = new ArtifactDependency(
            "com.example.very.long.group.id.with.many.packages",
            "artifact-with-very-long-name-for-testing",
            "1.0.0.0.0.0.0.Final.Release.Special.Build.12345"
        );

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeEqualityImpliesHashCodeEquality()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        if (dependency1.equals(dependency2))
        {
            Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
        }
    }

    @Test
    public void testHashCodeForPartiallyNullObjects()
    {
        ArtifactDependency dependency1 = new ArtifactDependency("com.example", null, "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", null, "1.0.0");

        Assertions.assertEquals(dependency1.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testHashCodeRemainsStableAcrossMultipleCalls()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", "1.0.0");

        int[] hashCodes = new int[10];
        for (int i = 0; i < 10; i++)
        {
            hashCodes[i] = dependency.hashCode();
        }

        for (int i = 1; i < 10; i++)
        {
            Assertions.assertEquals(hashCodes[0], hashCodes[i]);
        }
    }
}

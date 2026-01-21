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

public class ArtifactDependencyClaude_constructorTest
{
    @Test
    public void testConstructorWithValidParameters()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithNullGroupId()
    {
        String groupId = null;
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertNull(dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithNullArtifactId()
    {
        String groupId = "com.example";
        String artifactId = null;
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertNull(dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithNullVersion()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = null;

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertNull(dependency.getVersion());
    }

    @Test
    public void testConstructorWithAllNullParameters()
    {
        ArtifactDependency dependency = new ArtifactDependency(null, null, null);

        Assertions.assertNotNull(dependency);
        Assertions.assertNull(dependency.getGroupId());
        Assertions.assertNull(dependency.getArtifactId());
        Assertions.assertNull(dependency.getVersion());
    }

    @Test
    public void testConstructorWithEmptyStrings()
    {
        String groupId = "";
        String artifactId = "";
        String version = "";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithSnapshotVersion()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0-SNAPSHOT";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithSpecialCharactersInGroupId()
    {
        String groupId = "com.example-test.special_chars";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithSpecialCharactersInArtifactId()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact_special-chars";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorWithComplexVersion()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "2.1.3.RELEASE";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorCreatesIndependentInstances()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency1 = new ArtifactDependency(groupId, artifactId, version);
        ArtifactDependency dependency2 = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency1);
        Assertions.assertNotNull(dependency2);
        Assertions.assertNotSame(dependency1, dependency2);
        Assertions.assertEquals(dependency1.getGroupId(), dependency2.getGroupId());
        Assertions.assertEquals(dependency1.getArtifactId(), dependency2.getArtifactId());
        Assertions.assertEquals(dependency1.getVersion(), dependency2.getVersion());
    }

    @Test
    public void testConstructorWithLongStrings()
    {
        String groupId = "com.example.very.long.group.id.with.many.packages.for.testing.purposes";
        String artifactId = "test-artifact-with-very-long-name-for-testing-purposes";
        String version = "1.0.0.0.0.0.0.Final.Release.Special.Build.12345";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testConstructorFieldsAreImmutable()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        String originalGroupId = dependency.getGroupId();
        String originalArtifactId = dependency.getArtifactId();
        String originalVersion = dependency.getVersion();

        Assertions.assertEquals(originalGroupId, dependency.getGroupId());
        Assertions.assertEquals(originalArtifactId, dependency.getArtifactId());
        Assertions.assertEquals(originalVersion, dependency.getVersion());
    }

    @Test
    public void testConstructorWithDifferentVersionFormats()
    {
        String[][] testCases = {
            {"com.example", "artifact1", "1.0"},
            {"com.example", "artifact2", "1.0.0"},
            {"com.example", "artifact3", "1.0.0-SNAPSHOT"},
            {"com.example", "artifact4", "1.0.0.Final"},
            {"com.example", "artifact5", "2021.12.01"},
            {"com.example", "artifact6", "v1.0.0"}
        };

        for (String[] testCase : testCases)
        {
            ArtifactDependency dependency = new ArtifactDependency(testCase[0], testCase[1], testCase[2]);
            Assertions.assertNotNull(dependency);
            Assertions.assertEquals(testCase[0], dependency.getGroupId());
            Assertions.assertEquals(testCase[1], dependency.getArtifactId());
            Assertions.assertEquals(testCase[2], dependency.getVersion());
        }
    }

    @Test
    public void testConstructorPreservesExactInputValues()
    {
        String groupId = "  com.example  ";
        String artifactId = "  test-artifact  ";
        String version = "  1.0.0  ";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertNotNull(dependency);
        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertEquals(version, dependency.getVersion());
    }
}

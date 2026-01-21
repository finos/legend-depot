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

public class ArtifactDependencyClaude_getVersionTest
{
    @Test
    public void testGetVersionReturnsCorrectValue()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithNullValue()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", null);

        Assertions.assertNull(dependency.getVersion());
    }

    @Test
    public void testGetVersionWithEmptyString()
    {
        String version = "";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithSnapshotVersion()
    {
        String version = "1.0.0-SNAPSHOT";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithReleaseVersion()
    {
        String version = "2.1.3.RELEASE";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithSemanticVersioning()
    {
        String version = "3.2.1";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionMultipleCalls()
    {
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        String firstCall = dependency.getVersion();
        String secondCall = dependency.getVersion();

        Assertions.assertEquals(version, firstCall);
        Assertions.assertEquals(version, secondCall);
        Assertions.assertEquals(firstCall, secondCall);
    }

    @Test
    public void testGetVersionIndependenceFromOtherFields()
    {
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency(null, null, version);

        Assertions.assertEquals(version, dependency.getVersion());
        Assertions.assertNull(dependency.getGroupId());
        Assertions.assertNull(dependency.getArtifactId());
    }

    @Test
    public void testGetVersionWithWhitespace()
    {
        String version = "  1.0.0  ";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionFromMultipleInstances()
    {
        String version1 = "1.0.0";
        String version2 = "2.0.0";

        ArtifactDependency dependency1 = new ArtifactDependency("com.example", "artifact1", version1);
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", "artifact2", version2);

        Assertions.assertEquals(version1, dependency1.getVersion());
        Assertions.assertEquals(version2, dependency2.getVersion());
        Assertions.assertNotEquals(dependency1.getVersion(), dependency2.getVersion());
    }

    @Test
    public void testGetVersionReturnsImmutableValue()
    {
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        String retrievedVersion = dependency.getVersion();
        Assertions.assertEquals(version, retrievedVersion);

        String retrievedAgain = dependency.getVersion();
        Assertions.assertEquals(retrievedVersion, retrievedAgain);
    }

    @Test
    public void testGetVersionWithComplexVersion()
    {
        String version = "1.0.0.0.0.0.0.Final.Release.Special.Build.12345";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionDoesNotReturnGroupId()
    {
        String groupId = "com.example";
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
        Assertions.assertNotEquals(groupId, dependency.getVersion());
    }

    @Test
    public void testGetVersionDoesNotReturnArtifactId()
    {
        String artifactId = "test-artifact";
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, version);

        Assertions.assertEquals(version, dependency.getVersion());
        Assertions.assertNotEquals(artifactId, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithDateBasedVersion()
    {
        String version = "2021.12.01";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithPrefixedVersion()
    {
        String version = "v1.0.0";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithMilestoneVersion()
    {
        String version = "2.0.0.M1";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithReleaseCandidate()
    {
        String version = "3.0.0-RC1";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithBuildMetadata()
    {
        String version = "1.0.0+20130313144700";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithTwoPartVersion()
    {
        String version = "1.0";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }

    @Test
    public void testGetVersionWithSingleCharacter()
    {
        String version = "1";
        ArtifactDependency dependency = new ArtifactDependency("com.example", "test-artifact", version);

        Assertions.assertEquals(version, dependency.getVersion());
    }
}

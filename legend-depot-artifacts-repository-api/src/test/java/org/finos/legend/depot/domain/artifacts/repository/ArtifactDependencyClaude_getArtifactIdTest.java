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

public class ArtifactDependencyClaude_getArtifactIdTest
{
    @Test
    public void testGetArtifactIdReturnsCorrectValue()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithNullValue()
    {
        ArtifactDependency dependency = new ArtifactDependency("com.example", null, "1.0.0");

        Assertions.assertNull(dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithEmptyString()
    {
        String artifactId = "";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithHyphens()
    {
        String artifactId = "test-artifact-name";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithUnderscores()
    {
        String artifactId = "test_artifact_name";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdMultipleCalls()
    {
        String artifactId = "test-artifact";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        String firstCall = dependency.getArtifactId();
        String secondCall = dependency.getArtifactId();

        Assertions.assertEquals(artifactId, firstCall);
        Assertions.assertEquals(artifactId, secondCall);
        Assertions.assertEquals(firstCall, secondCall);
    }

    @Test
    public void testGetArtifactIdIndependenceFromOtherFields()
    {
        String artifactId = "test-artifact";
        ArtifactDependency dependency = new ArtifactDependency(null, artifactId, null);

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertNull(dependency.getGroupId());
        Assertions.assertNull(dependency.getVersion());
    }

    @Test
    public void testGetArtifactIdWithWhitespace()
    {
        String artifactId = "  test-artifact  ";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdFromMultipleInstances()
    {
        String artifactId1 = "artifact-one";
        String artifactId2 = "artifact-two";

        ArtifactDependency dependency1 = new ArtifactDependency("com.example", artifactId1, "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency("com.example", artifactId2, "2.0.0");

        Assertions.assertEquals(artifactId1, dependency1.getArtifactId());
        Assertions.assertEquals(artifactId2, dependency2.getArtifactId());
        Assertions.assertNotEquals(dependency1.getArtifactId(), dependency2.getArtifactId());
    }

    @Test
    public void testGetArtifactIdReturnsImmutableValue()
    {
        String artifactId = "test-artifact";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        String retrievedArtifactId = dependency.getArtifactId();
        Assertions.assertEquals(artifactId, retrievedArtifactId);

        String retrievedAgain = dependency.getArtifactId();
        Assertions.assertEquals(retrievedArtifactId, retrievedAgain);
    }

    @Test
    public void testGetArtifactIdWithLongName()
    {
        String artifactId = "test-artifact-with-very-long-name-for-testing-purposes-and-validation";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithNumericCharacters()
    {
        String artifactId = "test-artifact-123";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdDoesNotReturnGroupId()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertNotEquals(groupId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdDoesNotReturnVersion()
    {
        String artifactId = "test-artifact";
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, version);

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
        Assertions.assertNotEquals(version, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithSingleCharacter()
    {
        String artifactId = "a";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }

    @Test
    public void testGetArtifactIdWithMixedCaseAndSpecialCharacters()
    {
        String artifactId = "Test-Artifact_Name.Special";
        ArtifactDependency dependency = new ArtifactDependency("com.example", artifactId, "1.0.0");

        Assertions.assertEquals(artifactId, dependency.getArtifactId());
    }
}

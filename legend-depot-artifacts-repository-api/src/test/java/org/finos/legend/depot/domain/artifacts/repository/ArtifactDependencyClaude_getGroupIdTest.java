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

public class ArtifactDependencyClaude_getGroupIdTest
{
    @Test
    public void testGetGroupIdReturnsCorrectValue()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        String version = "1.0.0";

        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, version);

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdWithNullValue()
    {
        ArtifactDependency dependency = new ArtifactDependency(null, "test-artifact", "1.0.0");

        Assertions.assertNull(dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdWithEmptyString()
    {
        String groupId = "";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdWithComplexGroupId()
    {
        String groupId = "org.finos.legend.depot";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdWithSpecialCharacters()
    {
        String groupId = "com.example-test.special_chars";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdMultipleCalls()
    {
        String groupId = "com.example";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        String firstCall = dependency.getGroupId();
        String secondCall = dependency.getGroupId();

        Assertions.assertEquals(groupId, firstCall);
        Assertions.assertEquals(groupId, secondCall);
        Assertions.assertEquals(firstCall, secondCall);
    }

    @Test
    public void testGetGroupIdIndependenceFromOtherFields()
    {
        String groupId = "com.example";
        ArtifactDependency dependency = new ArtifactDependency(groupId, null, null);

        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertNull(dependency.getArtifactId());
        Assertions.assertNull(dependency.getVersion());
    }

    @Test
    public void testGetGroupIdWithWhitespace()
    {
        String groupId = "  com.example  ";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdFromMultipleInstances()
    {
        String groupId1 = "com.example.one";
        String groupId2 = "com.example.two";

        ArtifactDependency dependency1 = new ArtifactDependency(groupId1, "artifact1", "1.0.0");
        ArtifactDependency dependency2 = new ArtifactDependency(groupId2, "artifact2", "2.0.0");

        Assertions.assertEquals(groupId1, dependency1.getGroupId());
        Assertions.assertEquals(groupId2, dependency2.getGroupId());
        Assertions.assertNotEquals(dependency1.getGroupId(), dependency2.getGroupId());
    }

    @Test
    public void testGetGroupIdReturnsImmutableValue()
    {
        String groupId = "com.example";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        String retrievedGroupId = dependency.getGroupId();
        Assertions.assertEquals(groupId, retrievedGroupId);

        String retrievedAgain = dependency.getGroupId();
        Assertions.assertEquals(retrievedGroupId, retrievedAgain);
    }

    @Test
    public void testGetGroupIdWithLongGroupId()
    {
        String groupId = "com.example.very.long.group.id.with.many.packages.for.testing.purposes";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdWithNumericCharacters()
    {
        String groupId = "com.example123.test456";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdDoesNotReturnArtifactId()
    {
        String groupId = "com.example";
        String artifactId = "test-artifact";
        ArtifactDependency dependency = new ArtifactDependency(groupId, artifactId, "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertNotEquals(artifactId, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdDoesNotReturnVersion()
    {
        String groupId = "com.example";
        String version = "1.0.0";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", version);

        Assertions.assertEquals(groupId, dependency.getGroupId());
        Assertions.assertNotEquals(version, dependency.getGroupId());
    }

    @Test
    public void testGetGroupIdWithSingleCharacter()
    {
        String groupId = "a";
        ArtifactDependency dependency = new ArtifactDependency(groupId, "test-artifact", "1.0.0");

        Assertions.assertEquals(groupId, dependency.getGroupId());
    }
}

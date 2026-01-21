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

package org.finos.legend.depot.services.artifacts.repository.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MavenArtifactRepositoryClaude_gavCoordinatesTest
{
    private MavenArtifactRepositoryTestable repository;

    @BeforeEach
    void setUp()
    {
        repository = new MavenArtifactRepositoryTestable();
    }

    @Test
    @DisplayName("Test gavCoordinates with all parameters including PackagingType")
    void testGavCoordinatesWithPackagingType()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        PackagingType type = PackagingType.JAR;
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:jar:1.0.0", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with POM packaging type")
    void testGavCoordinatesWithPomPackagingType()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        PackagingType type = PackagingType.POM;
        String version = "2.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:pom:2.0.0", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with null PackagingType")
    void testGavCoordinatesWithNullPackagingType()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        PackagingType type = null;
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:1.0.0", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with snapshot version")
    void testGavCoordinatesWithSnapshotVersion()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        PackagingType type = PackagingType.JAR;
        String version = "1.0.0-SNAPSHOT";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:jar:1.0.0-SNAPSHOT", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with complex group ID")
    void testGavCoordinatesWithComplexGroupId()
    {
        // Arrange
        String group = "org.finos.legend.depot.services";
        String artifact = "legend-depot-core";
        PackagingType type = PackagingType.JAR;
        String version = "1.2.3";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend.depot.services:legend-depot-core:jar:1.2.3", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with artifact containing hyphens")
    void testGavCoordinatesWithHyphenatedArtifact()
    {
        // Arrange
        String group = "org.example";
        String artifact = "my-test-artifact-name";
        PackagingType type = PackagingType.POM;
        String version = "3.4.5";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.example:my-test-artifact-name:pom:3.4.5", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with empty string version")
    void testGavCoordinatesWithEmptyVersion()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        PackagingType type = PackagingType.JAR;
        String version = "";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:jar:", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with version containing special characters")
    void testGavCoordinatesWithVersionSpecialCharacters()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        PackagingType type = PackagingType.JAR;
        String version = "1.0.0-beta.1";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:jar:1.0.0-beta.1", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with minimal group and artifact")
    void testGavCoordinatesWithMinimalValues()
    {
        // Arrange
        String group = "a";
        String artifact = "b";
        PackagingType type = PackagingType.JAR;
        String version = "1";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("a:b:jar:1", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with WAR packaging type")
    void testGavCoordinatesWithWarPackagingType()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot-web";
        PackagingType type = PackagingType.WAR;
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, type, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot-web:war:1.0.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates method delegates to four-parameter method")
    void testGavCoordinatesThreeParameterMethod()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:1.0.0", result);
    }

    @Test
    @DisplayName("Test gavCoordinates with three parameters and snapshot version")
    void testGavCoordinatesThreeParametersSnapshot()
    {
        // Arrange
        String group = "org.example";
        String artifact = "test-artifact";
        String version = "2.0.0-SNAPSHOT";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.example:test-artifact:2.0.0-SNAPSHOT", result);
    }

    @Test
    @DisplayName("Test gavCoordinates consistency between three and four parameter methods")
    void testGavCoordinatesConsistencyBetweenMethods()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "1.5.0";

        // Act
        String resultThreeParam = repository.gavCoordinates(group, artifact, version);
        String resultFourParam = repository.gavCoordinates(group, artifact, null, version);

        // Assert
        assertEquals(resultThreeParam, resultFourParam);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with complex group ID")
    void testThreeParameterGavCoordinatesWithComplexGroupId()
    {
        // Arrange
        String group = "org.finos.legend.depot.services.artifacts";
        String artifact = "artifact-repository";
        String version = "2.5.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend.depot.services.artifacts:artifact-repository:2.5.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with single character inputs")
    void testThreeParameterGavCoordinatesWithSingleCharacters()
    {
        // Arrange
        String group = "x";
        String artifact = "y";
        String version = "z";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("x:y:z", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with empty group")
    void testThreeParameterGavCoordinatesWithEmptyGroup()
    {
        // Arrange
        String group = "";
        String artifact = "test-artifact";
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals(":test-artifact:1.0.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with empty artifact")
    void testThreeParameterGavCoordinatesWithEmptyArtifact()
    {
        // Arrange
        String group = "org.example";
        String artifact = "";
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.example::1.0.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with empty version")
    void testThreeParameterGavCoordinatesWithEmptyVersion()
    {
        // Arrange
        String group = "org.example";
        String artifact = "test-artifact";
        String version = "";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.example:test-artifact:", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with version range syntax")
    void testThreeParameterGavCoordinatesWithVersionRange()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "[1.0,2.0)";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:[1.0,2.0)", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with version containing build metadata")
    void testThreeParameterGavCoordinatesWithBuildMetadata()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "1.0.0+20130313144700";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:1.0.0+20130313144700", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with artifact containing underscores")
    void testThreeParameterGavCoordinatesWithUnderscores()
    {
        // Arrange
        String group = "org.example";
        String artifact = "my_test_artifact";
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.example:my_test_artifact:1.0.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with numeric version")
    void testThreeParameterGavCoordinatesWithNumericVersion()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "20250120";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:20250120", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with RC version")
    void testThreeParameterGavCoordinatesWithRCVersion()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "1.0.0-RC1";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:1.0.0-RC1", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with milestone version")
    void testThreeParameterGavCoordinatesWithMilestoneVersion()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "1.0.0-M3";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:1.0.0-M3", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with long group ID")
    void testThreeParameterGavCoordinatesWithLongGroupId()
    {
        // Arrange
        String group = "org.finos.legend.depot.services.artifacts.repository.maven.impl";
        String artifact = "core";
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend.depot.services.artifacts.repository.maven.impl:core:1.0.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with long artifact ID")
    void testThreeParameterGavCoordinatesWithLongArtifactId()
    {
        // Arrange
        String group = "org.example";
        String artifact = "very-long-artifact-name-with-multiple-parts-and-components";
        String version = "1.0.0";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.example:very-long-artifact-name-with-multiple-parts-and-components:1.0.0", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with alpha version")
    void testThreeParameterGavCoordinatesWithAlphaVersion()
    {
        // Arrange
        String group = "org.finos.legend";
        String artifact = "legend-depot";
        String version = "1.0.0-alpha.1";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.finos.legend:legend-depot:1.0.0-alpha.1", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates with all empty strings")
    void testThreeParameterGavCoordinatesWithAllEmptyStrings()
    {
        // Arrange
        String group = "";
        String artifact = "";
        String version = "";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("::", result);
    }

    @Test
    @DisplayName("Test three-parameter gavCoordinates format matches Maven GAV standard")
    void testThreeParameterGavCoordinatesFormatMatchesMavenStandard()
    {
        // Arrange
        String group = "org.apache.maven";
        String artifact = "maven-core";
        String version = "3.8.1";

        // Act
        String result = repository.gavCoordinates(group, artifact, version);

        // Assert
        assertEquals("org.apache.maven:maven-core:3.8.1", result);
        // Verify format: groupId:artifactId:version (3 colon-separated parts)
        String[] parts = result.split(":");
        assertEquals(3, parts.length);
        assertEquals(group, parts[0]);
        assertEquals(artifact, parts[1]);
        assertEquals(version, parts[2]);
    }

    /**
     * Test helper class that extends MavenArtifactRepository to expose protected methods.
     * This approach is used instead of reflection because the gavCoordinates method can be tested
     * without reflection by simply extending the class and making the protected method accessible.
     */
    private static class MavenArtifactRepositoryTestable extends MavenArtifactRepository
    {
        public MavenArtifactRepositoryTestable()
        {
            super(null);
        }

        @Override
        public String gavCoordinates(String group, String artifact, PackagingType type, String version)
        {
            return super.gavCoordinates(group, artifact, type, version);
        }

        @Override
        public String gavCoordinates(String group, String artifact, String version)
        {
            return super.gavCoordinates(group, artifact, version);
        }
    }
}

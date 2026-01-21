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

package org.finos.legend.depot.domain.project.dependencies;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDependencyWithPlatformVersionsClaudeTest 

{

    @Test
    @DisplayName("Test constructor with all valid parameters")
    void testConstructorWithValidParameters()
  {
        // Arrange
        String groupId = "org.example";
        String artifactId = "my-artifact";
        String versionId = "1.0.0";
        ProjectVersion dependency = new ProjectVersion("dep.group", "dep-artifact", "2.0.0");
        List<Property> platformsVersion = Arrays.asList(
            new Property("platform1", "v1.0"),
            new Property("platform2", "v2.0")
        );

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            groupId, artifactId, versionId, dependency, platformsVersion
        );

        // Assert
        assertEquals(groupId, result.getGroupId());
        assertEquals(artifactId, result.getArtifactId());
        assertEquals(versionId, result.getVersionId());
        assertSame(dependency, result.getDependency());
        assertSame(platformsVersion, result.getPlatformsVersion());
        assertEquals(2, result.getPlatformsVersion().size());
    }

    @Test
    @DisplayName("Test constructor with empty platformsVersion list")
    void testConstructorWithEmptyPlatformsList()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g1", "a1", "v1");
        List<Property> emptyList = new ArrayList<>();

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, emptyList
        );

        // Assert
        assertNotNull(result.getPlatformsVersion());
        assertTrue(result.getPlatformsVersion().isEmpty());
        assertSame(emptyList, result.getPlatformsVersion());
    }

    @Test
    @DisplayName("Test constructor with null platformsVersion")
    void testConstructorWithNullPlatformsVersion()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g1", "a1", "v1");

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, null
        );

        // Assert
        assertNull(result.getPlatformsVersion());
    }

    @Test
    @DisplayName("Test constructor with null dependency")
    void testConstructorWithNullDependency()
  {
        // Arrange
        List<Property> platformsVersion = Arrays.asList(new Property("p1", "v1"));

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", null, platformsVersion
        );

        // Assert
        assertNull(result.getDependency());
        assertNotNull(result.getPlatformsVersion());
    }

    @Test
    @DisplayName("Test constructor with null groupId")
    void testConstructorWithNullGroupId()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g1", "a1", "v1");
        List<Property> platformsVersion = new ArrayList<>();

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            null, "artifact", "version", dependency, platformsVersion
        );

        // Assert
        assertNull(result.getGroupId());
        assertEquals("artifact", result.getArtifactId());
        assertEquals("version", result.getVersionId());
    }

    @Test
    @DisplayName("Test constructor with null artifactId")
    void testConstructorWithNullArtifactId()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g1", "a1", "v1");
        List<Property> platformsVersion = new ArrayList<>();

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            "group", null, "version", dependency, platformsVersion
        );

        // Assert
        assertEquals("group", result.getGroupId());
        assertNull(result.getArtifactId());
        assertEquals("version", result.getVersionId());
    }

    @Test
    @DisplayName("Test constructor with null versionId")
    void testConstructorWithNullVersionId()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g1", "a1", "v1");
        List<Property> platformsVersion = new ArrayList<>();

        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", null, dependency, platformsVersion
        );

        // Assert
        assertEquals("group", result.getGroupId());
        assertEquals("artifact", result.getArtifactId());
        assertNull(result.getVersionId());
    }

    @Test
    @DisplayName("Test constructor with all null parameters")
    void testConstructorWithAllNullParameters()
  {
        // Act
        ProjectDependencyWithPlatformVersions result = new ProjectDependencyWithPlatformVersions(
            null, null, null, null, null
        );

        // Assert
        assertNull(result.getGroupId());
        assertNull(result.getArtifactId());
        assertNull(result.getVersionId());
        assertNull(result.getDependency());
        assertNull(result.getPlatformsVersion());
    }

    @Test
    @DisplayName("Test getDependency returns correct dependency")
    void testGetDependency()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("dep.group", "dep-artifact", "1.5.0");
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "g", "a", "v", dependency, new ArrayList<>()
        );

        // Act
        ProjectVersion result = obj.getDependency();

        // Assert
        assertSame(dependency, result);
        assertEquals("dep.group", result.getGroupId());
        assertEquals("dep-artifact", result.getArtifactId());
        assertEquals("1.5.0", result.getVersionId());
    }

    @Test
    @DisplayName("Test getPlatformsVersion returns correct list")
    void testGetPlatformsVersion()
  {
        // Arrange
        List<Property> platformsVersion = Arrays.asList(
            new Property("java", "11"),
            new Property("spring", "5.3.0")
        );
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "g", "a", "v", dependency, platformsVersion
        );

        // Act
        List<Property> result = obj.getPlatformsVersion();

        // Assert
        assertSame(platformsVersion, result);
        assertEquals(2, result.size());
        assertEquals("java", result.get(0).getPropertyName());
        assertEquals("11", result.get(0).getValue());
    }

    @Test
    @DisplayName("Test equals returns true for same object")
    void testEqualsSameObject()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, new ArrayList<>()
        );

        // Act and Assert
        assertEquals(obj, obj);
    }

    @Test
    @DisplayName("Test equals returns true for equal objects")
    void testEqualsEqualObjects()
  {
        // Arrange
        ProjectVersion dependency1 = new ProjectVersion("g", "a", "v");
        List<Property> platforms1 = Arrays.asList(new Property("p1", "v1"));
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency1, platforms1
        );

        ProjectVersion dependency2 = new ProjectVersion("g", "a", "v");
        List<Property> platforms2 = Arrays.asList(new Property("p1", "v1"));
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency2, platforms2
        );

        // Act and Assert
        assertEquals(obj1, obj2);
        assertEquals(obj2, obj1); // Test symmetry
    }

    @Test
    @DisplayName("Test equals returns false for different groupId")
    void testEqualsDifferentGroupId()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group1", "artifact", "version", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group2", "artifact", "version", dependency, platforms
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals returns false for different artifactId")
    void testEqualsDifferentArtifactId()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact1", "version", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact2", "version", dependency, platforms
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals returns false for different versionId")
    void testEqualsDifferentVersionId()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "1.0.0", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "2.0.0", dependency, platforms
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals returns false for different dependency")
    void testEqualsDifferentDependency()
  {
        // Arrange
        ProjectVersion dependency1 = new ProjectVersion("g1", "a1", "v1");
        ProjectVersion dependency2 = new ProjectVersion("g2", "a2", "v2");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency1, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency2, platforms
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals returns false for different platformsVersion")
    void testEqualsDifferentPlatformsVersion()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms1 = Arrays.asList(new Property("p1", "v1"));
        List<Property> platforms2 = Arrays.asList(new Property("p2", "v2"));
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms1
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms2
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals returns false when comparing to null")
    void testEqualsNull()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, new ArrayList<>()
        );

        // Act and Assert
        assertNotEquals(obj, null);
    }

    @Test
    @DisplayName("Test equals returns false for different type")
    void testEqualsDifferentType()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, new ArrayList<>()
        );
        String differentType = "not a ProjectDependencyWithPlatformVersions";

        // Act and Assert
        assertNotEquals(obj, differentType);
    }

    @Test
    @DisplayName("Test equals with null dependency in both objects")
    void testEqualsWithBothNullDependencies()
  {
        // Arrange
        List<Property> platforms = Arrays.asList(new Property("p1", "v1"));
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", null, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", null, platforms
        );

        // Act and Assert
        assertEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals with null platformsVersion in both objects")
    void testEqualsWithBothNullPlatformsVersions()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, null
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, null
        );

        // Act and Assert
        assertEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals with one null dependency")
    void testEqualsWithOneNullDependency()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", null, platforms
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals with one null platformsVersion")
    void testEqualsWithOneNullPlatformsVersion()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = Arrays.asList(new Property("p1", "v1"));
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, null
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test equals transitivity")
    void testEqualsTransitivity()
  {
        // Arrange
        ProjectVersion dependency1 = new ProjectVersion("g", "a", "v");
        ProjectVersion dependency2 = new ProjectVersion("g", "a", "v");
        ProjectVersion dependency3 = new ProjectVersion("g", "a", "v");
        List<Property> platforms1 = Arrays.asList(new Property("p1", "v1"));
        List<Property> platforms2 = Arrays.asList(new Property("p1", "v1"));
        List<Property> platforms3 = Arrays.asList(new Property("p1", "v1"));

        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency1, platforms1
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency2, platforms2
        );
        ProjectDependencyWithPlatformVersions obj3 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency3, platforms3
        );

        // Act and Assert
        assertEquals(obj1, obj2);
        assertEquals(obj2, obj3);
        assertEquals(obj1, obj3); // Transitivity
    }

    @Test
    @DisplayName("Test hashCode returns same value for equal objects")
    void testHashCodeConsistency()
  {
        // Arrange
        ProjectVersion dependency1 = new ProjectVersion("g", "a", "v");
        ProjectVersion dependency2 = new ProjectVersion("g", "a", "v");
        List<Property> platforms1 = Arrays.asList(new Property("p1", "v1"));
        List<Property> platforms2 = Arrays.asList(new Property("p1", "v1"));

        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency1, platforms1
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency2, platforms2
        );

        // Act
        int hash1 = obj1.hashCode();
        int hash2 = obj2.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test hashCode is consistent across multiple calls")
    void testHashCodeConsistentAcrossCalls()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = Arrays.asList(new Property("p1", "v1"));
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms
        );

        // Act
        int hash1 = obj.hashCode();
        int hash2 = obj.hashCode();
        int hash3 = obj.hashCode();

        // Assert
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("Test hashCode with null fields")
    void testHashCodeWithNullFields()
  {
        // Arrange
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            null, null, null, null, null
        );

        // Act and Assert
        assertDoesNotThrow(() -> obj.hashCode());
    }

    @Test
    @DisplayName("Test hashCode with empty platformsVersion list")
    void testHashCodeWithEmptyPlatformsList()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, new ArrayList<>()
        );

        // Act
        int hash = obj.hashCode();

        // Assert
        assertNotNull(hash);
    }

    @Test
    @DisplayName("Test hashCode differs for different groupIds")
    void testHashCodeDifferentGroupIds()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group1", "artifact", "version", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group2", "artifact", "version", dependency, platforms
        );

        // Act
        int hash1 = obj1.hashCode();
        int hash2 = obj2.hashCode();

        // Assert
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test equals and hashCode contract - equal objects have same hashCode")
    void testEqualsHashCodeContract()
  {
        // Arrange
        ProjectVersion dependency1 = new ProjectVersion("g", "a", "v");
        ProjectVersion dependency2 = new ProjectVersion("g", "a", "v");
        List<Property> platforms1 = Arrays.asList(new Property("p1", "v1"));
        List<Property> platforms2 = Arrays.asList(new Property("p1", "v1"));

        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency1, platforms1
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency2, platforms2
        );

        // Act and Assert
        assertEquals(obj1, obj2);
        assertEquals(obj1.hashCode(), obj2.hashCode());
    }

    @Test
    @DisplayName("Test with multiple properties in platformsVersion")
    void testWithMultiplePlatformProperties()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = Arrays.asList(
            new Property("java", "11"),
            new Property("spring", "5.3.0"),
            new Property("maven", "3.8.0")
        );

        // Act
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms
        );

        // Assert
        assertEquals(3, obj.getPlatformsVersion().size());
        assertEquals("java", obj.getPlatformsVersion().get(0).getPropertyName());
        assertEquals("spring", obj.getPlatformsVersion().get(1).getPropertyName());
        assertEquals("maven", obj.getPlatformsVersion().get(2).getPropertyName());
    }

    @Test
    @DisplayName("Test equals with different number of platform properties")
    void testEqualsWithDifferentNumberOfPlatforms()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms1 = Arrays.asList(new Property("p1", "v1"));
        List<Property> platforms2 = Arrays.asList(
            new Property("p1", "v1"),
            new Property("p2", "v2")
        );
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms1
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms2
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test constructor with empty strings")
    void testConstructorWithEmptyStrings()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("", "", "");
        List<Property> platforms = new ArrayList<>();

        // Act
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "", "", "", dependency, platforms
        );

        // Assert
        assertEquals("", obj.getGroupId());
        assertEquals("", obj.getArtifactId());
        assertEquals("", obj.getVersionId());
    }

    @Test
    @DisplayName("Test equals distinguishes empty string from null")
    void testEqualsDistinguishesEmptyFromNull()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = new ArrayList<>();
        ProjectDependencyWithPlatformVersions obj1 = new ProjectDependencyWithPlatformVersions(
            "", "artifact", "version", dependency, platforms
        );
        ProjectDependencyWithPlatformVersions obj2 = new ProjectDependencyWithPlatformVersions(
            null, "artifact", "version", dependency, platforms
        );

        // Act and Assert
        assertNotEquals(obj1, obj2);
    }

    @Test
    @DisplayName("Test with immutable list for platformsVersion")
    void testWithImmutablePlatformsList()
  {
        // Arrange
        ProjectVersion dependency = new ProjectVersion("g", "a", "v");
        List<Property> platforms = Collections.singletonList(new Property("p1", "v1"));

        // Act
        ProjectDependencyWithPlatformVersions obj = new ProjectDependencyWithPlatformVersions(
            "group", "artifact", "version", dependency, platforms
        );

        // Assert
        assertEquals(1, obj.getPlatformsVersion().size());
        assertSame(platforms, obj.getPlatformsVersion());
    }
}

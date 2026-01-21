//  Copyright 2023 Goldman Sachs
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

package org.finos.legend.depot.services.api.projects.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectsConfigurationClaudeTest


{
    @Test
    void testConstructorWithNonNullValue()
  {
        // Arrange
        String branch = "main";

        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(branch);

        // Assert
        assertNotNull(config);
        assertEquals(branch, config.getDefaultBranch());
    }

    @Test
    void testConstructorWithNull()
  {
        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(null);

        // Assert
        assertNotNull(config);
        assertNull(config.getDefaultBranch());
    }

    @Test
    void testConstructorWithEmptyString()
  {
        // Arrange
        String branch = "";

        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(branch);

        // Assert
        assertNotNull(config);
        assertEquals(branch, config.getDefaultBranch());
    }

    @Test
    void testConstructorWithWhitespaceString()
  {
        // Arrange
        String branch = "   ";

        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(branch);

        // Assert
        assertNotNull(config);
        assertEquals(branch, config.getDefaultBranch());
    }

    @Test
    void testConstructorWithSlashInBranchName()
  {
        // Arrange
        String branch = "feature/my-feature";

        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(branch);

        // Assert
        assertEquals(branch, config.getDefaultBranch());
    }

    @Test
    void testConstructorWithSpecialCharacters()
  {
        // Arrange
        String branch = "feature_branch-123";

        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(branch);

        // Assert
        assertEquals(branch, config.getDefaultBranch());
    }

    @Test
    void testGetDefaultBranchReturnsCorrectValue()
  {
        // Arrange
        String expectedBranch = "develop";
        ProjectsConfiguration config = new ProjectsConfiguration(expectedBranch);

        // Act
        String actualBranch = config.getDefaultBranch();

        // Assert
        assertEquals(expectedBranch, actualBranch);
    }

    @Test
    void testGetDefaultBranchWithMaster()
  {
        // Arrange
        ProjectsConfiguration config = new ProjectsConfiguration("master");

        // Act
        String branch = config.getDefaultBranch();

        // Assert
        assertEquals("master", branch);
    }

    @Test
    void testGetDefaultBranchWithMain()
  {
        // Arrange
        ProjectsConfiguration config = new ProjectsConfiguration("main");

        // Act
        String branch = config.getDefaultBranch();

        // Assert
        assertEquals("main", branch);
    }

    @Test
    void testGetDefaultBranchCalledMultipleTimes()
  {
        // Arrange
        String branch = "stable";
        ProjectsConfiguration config = new ProjectsConfiguration(branch);

        // Act
        String firstCall = config.getDefaultBranch();
        String secondCall = config.getDefaultBranch();

        // Assert
        assertEquals(firstCall, secondCall);
        assertEquals(branch, firstCall);
    }

    @Test
    void testJsonSerialization() throws Exception
    {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ProjectsConfiguration config = new ProjectsConfiguration("main");

        // Act
        String json = mapper.writeValueAsString(config);

        // Assert
        assertTrue(json.contains("\"defaultBranch\""));
        assertTrue(json.contains("\"main\""));
    }

    @Test
    void testJsonDeserialization() throws Exception
    {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"defaultBranch\":\"develop\"}";

        // Act
        ProjectsConfiguration config = mapper.readValue(json, ProjectsConfiguration.class);

        // Assert
        assertNotNull(config);
        assertEquals("develop", config.getDefaultBranch());
    }

    @Test
    void testJsonDeserializationWithNull() throws Exception
    {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"defaultBranch\":null}";

        // Act
        ProjectsConfiguration config = mapper.readValue(json, ProjectsConfiguration.class);

        // Assert
        assertNotNull(config);
        assertNull(config.getDefaultBranch());
    }

    @Test
    void testJsonRoundTrip() throws Exception
    {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ProjectsConfiguration original = new ProjectsConfiguration("feature/test");

        // Act
        String json = mapper.writeValueAsString(original);
        ProjectsConfiguration deserialized = mapper.readValue(json, ProjectsConfiguration.class);

        // Assert
        assertEquals(original.getDefaultBranch(), deserialized.getDefaultBranch());
    }

    @Test
    void testDifferentInstancesWithSameValueAreNotSame()
  {
        // Arrange
        String branch = "main";

        // Act
        ProjectsConfiguration config1 = new ProjectsConfiguration(branch);
        ProjectsConfiguration config2 = new ProjectsConfiguration(branch);

        // Assert
        assertNotSame(config1, config2);
        assertEquals(config1.getDefaultBranch(), config2.getDefaultBranch());
    }

    @Test
    void testLongBranchName()
  {
        // Arrange
        String longBranch = "feature/very-long-branch-name-with-many-characters-to-test-handling-of-long-strings";

        // Act
        ProjectsConfiguration config = new ProjectsConfiguration(longBranch);

        // Assert
        assertEquals(longBranch, config.getDefaultBranch());
    }
}

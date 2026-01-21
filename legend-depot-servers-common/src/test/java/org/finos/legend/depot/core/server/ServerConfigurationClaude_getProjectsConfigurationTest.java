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

package org.finos.legend.depot.core.server;

import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServerConfigurationClaude_getProjectsConfigurationTest
{
    /**
     * Reflection is necessary to test getProjectsConfiguration() because ServerConfiguration
     * extends io.dropwizard.Configuration which has complex initialization requirements
     * (Guava collections, Dropwizard utilities, etc.) that are not relevant to testing this
     * simple getter method. The method under test is a straightforward accessor that returns
     * a field value without any transformation or logic.
     *
     * Using reflection allows us to set the private 'projects' field directly and verify
     * that getProjectsConfiguration() returns exactly what was set, without requiring full
     * Dropwizard framework initialization including YAML parsing, validation, and dependency
     * injection setup.
     */

    private void setProjectsField(ServerConfiguration config, ProjectsConfiguration projects) throws Exception
    {
        Field field = ServerConfiguration.class.getDeclaredField("projects");
        field.setAccessible(true);
        field.set(config, projects);
    }

    @Test
    @DisplayName("Test getProjectsConfiguration returns configured value")
    void testGetProjectsConfigurationReturnsConfiguredValue() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("main");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertNotNull(result, "ProjectsConfiguration should not be null");
        assertEquals("main", result.getDefaultBranch(), "Default branch should be 'main'");
        assertSame(projectsConfig, result, "Should return the same instance");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration returns value with master branch")
    void testGetProjectsConfigurationReturnsMasterBranch() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("master");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertNotNull(result, "ProjectsConfiguration should not be null");
        assertEquals("master", result.getDefaultBranch(), "Default branch should be 'master'");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration returns value with custom branch")
    void testGetProjectsConfigurationReturnsCustomBranch() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("develop");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertNotNull(result, "ProjectsConfiguration should not be null");
        assertEquals("develop", result.getDefaultBranch(), "Default branch should be 'develop'");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration returns null when not set")
    void testGetProjectsConfigurationReturnsNullWhenNotSet() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        setProjectsField(config, null);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertNull(result, "ProjectsConfiguration should be null when not set");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration is idempotent")
    void testGetProjectsConfigurationIsIdempotent() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("main");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result1 = config.getProjectsConfiguration();
        ProjectsConfiguration result2 = config.getProjectsConfiguration();
        ProjectsConfiguration result3 = config.getProjectsConfiguration();

        // Assert
        assertNotNull(result1, "First call should return non-null");
        assertNotNull(result2, "Second call should return non-null");
        assertNotNull(result3, "Third call should return non-null");
        assertSame(result1, result2, "Multiple calls should return the same instance");
        assertSame(result2, result3, "Multiple calls should return the same instance");
        assertEquals("main", result1.getDefaultBranch());
        assertEquals("main", result2.getDefaultBranch());
        assertEquals("main", result3.getDefaultBranch());
    }

    @Test
    @DisplayName("Test getProjectsConfiguration with special characters in branch name")
    void testGetProjectsConfigurationWithSpecialCharacters() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("feature/user-123");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertNotNull(result, "ProjectsConfiguration should not be null");
        assertEquals("feature/user-123", result.getDefaultBranch(), "Default branch should handle special characters");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration with different branch names maintains independence")
    void testGetProjectsConfigurationIndependentInstances() throws Exception
    {
        // Arrange
        ServerConfiguration config1 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ServerConfiguration config2 = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);

        ProjectsConfiguration projectsConfig1 = new ProjectsConfiguration("main");
        ProjectsConfiguration projectsConfig2 = new ProjectsConfiguration("develop");

        setProjectsField(config1, projectsConfig1);
        setProjectsField(config2, projectsConfig2);

        // Act
        ProjectsConfiguration result1 = config1.getProjectsConfiguration();
        ProjectsConfiguration result2 = config2.getProjectsConfiguration();

        // Assert
        assertNotNull(result1, "Config1 should return non-null");
        assertNotNull(result2, "Config2 should return non-null");
        assertEquals("main", result1.getDefaultBranch(), "Config1 should have 'main' branch");
        assertEquals("develop", result2.getDefaultBranch(), "Config2 should have 'develop' branch");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration with empty branch name")
    void testGetProjectsConfigurationWithEmptyBranchName() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertNotNull(result, "ProjectsConfiguration should not be null");
        assertEquals("", result.getDefaultBranch(), "Default branch should be empty string");
    }

    @Test
    @DisplayName("Test getProjectsConfiguration returns exact same object reference")
    void testGetProjectsConfigurationReturnsSameReference() throws Exception
    {
        // Arrange
        ServerConfiguration config = Mockito.mock(ServerConfiguration.class, Mockito.CALLS_REAL_METHODS);
        ProjectsConfiguration projectsConfig = new ProjectsConfiguration("release");
        setProjectsField(config, projectsConfig);

        // Act
        ProjectsConfiguration result = config.getProjectsConfiguration();

        // Assert
        assertSame(projectsConfig, result, "Should return exactly the same object reference");
    }
}

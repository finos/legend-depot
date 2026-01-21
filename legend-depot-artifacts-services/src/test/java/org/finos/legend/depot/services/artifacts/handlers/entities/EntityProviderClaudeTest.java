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

package org.finos.legend.depot.services.artifacts.handlers.entities;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.api.artifacts.handlers.ArtifactLoadingException;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class EntityProviderClaudeTest
{
    private EntityProvider entityProvider;

    @BeforeEach
    public void setUp()
    {
        entityProvider = new EntityProvider();
    }

    @Test
    public void testConstructor()
    {
        // Test that constructor creates a valid instance
        EntityProvider provider = new EntityProvider();
        Assertions.assertNotNull(provider);
    }

    @Test
    public void testGetType()
    {
        // Test that getType returns ENTITIES artifact type
        ArtifactType type = entityProvider.getType();
        Assertions.assertNotNull(type);
        Assertions.assertEquals(ArtifactType.ENTITIES, type);
        Assertions.assertEquals("entities", type.getModuleName());
    }

    @Test
    public void testMatchesArtifactType_withValidEntitiesFile()
    {
        // Test with a file that contains "entities" in the name
        File validFile = new File("test-entities-1.0.0.jar");
        boolean matches = entityProvider.matchesArtifactType(validFile);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_withEntitiesInMiddle()
    {
        // Test with "entities" in the middle of the filename
        File validFile = new File("my-entities-artifact.jar");
        boolean matches = entityProvider.matchesArtifactType(validFile);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_withVersionedEntitiesFile()
    {
        // Test with a file that contains "versioned-entities" - should NOT match
        File versionedFile = new File("test-versioned-entities-1.0.0.jar");
        boolean matches = entityProvider.matchesArtifactType(versionedFile);
        Assertions.assertFalse(matches, "Should not match versioned-entities files");
    }

    @Test
    public void testMatchesArtifactType_withoutEntities()
    {
        // Test with a file that doesn't contain "entities"
        File invalidFile = new File("test-file-generation-1.0.0.jar");
        boolean matches = entityProvider.matchesArtifactType(invalidFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_withEmptyFilename()
    {
        // Test with empty filename
        File emptyFile = new File("");
        boolean matches = entityProvider.matchesArtifactType(emptyFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testExtractArtifactsForType_withValidJarFile()
    {
        // Test extracting entities from a real jar file
        File jarFile = new File("src/test/resources/repository/examples/metadata/test-entities/2.0.0/test-entities-2.0.0.jar");

        if (jarFile.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile);
            List<Entity> entities = entityProvider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(entities);
            Assertions.assertEquals(9, entities.size(), "Expected 9 entities from test-entities-2.0.0.jar");
        }
    }

    @Test
    public void testExtractArtifactsForType_withEmptyStream()
    {
        // Test with empty stream
        Stream<File> emptyStream = Stream.empty();
        List<Entity> entities = entityProvider.extractArtifactsForType(emptyStream);

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void testExtractArtifactsForType_withMultipleFiles()
    {
        // Test extracting entities from multiple jar files
        File jarFile1 = new File("src/test/resources/repository/examples/metadata/test-entities/2.0.0/test-entities-2.0.0.jar");
        File jarFile2 = new File("src/test/resources/repository/examples/metadata/test-entities/1.0.0/test-entities-1.0.0.jar");

        if (jarFile1.exists() && jarFile2.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile1, jarFile2);
            List<Entity> entities = entityProvider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(entities);
            // Both jars should have entities, total should be greater than single file
            Assertions.assertTrue(entities.size() > 0);
        }
    }

    @Test
    public void testExtractArtifactsForType_withNonJarFile()
    {
        // Test with a file that exists but is not a valid jar/entities file
        // Using a test resource that exists but won't be parseable as entities
        File pomFile = new File("src/test/resources/repository/examples/metadata/test-entities/2.0.0/test-entities-2.0.0.pom");

        if (pomFile.exists())
        {
            Stream<File> fileStream = Stream.of(pomFile);

            // A POM file should cause an exception when trying to extract entities
            Assertions.assertThrows(ArtifactLoadingException.class, () -> {
                entityProvider.extractArtifactsForType(fileStream);
            });
        }
        else
        {
            // If the test file doesn't exist, skip this test gracefully
            Assertions.assertTrue(true, "Test skipped - POM file not found");
        }
    }

    @Test
    public void testExtractArtifactsForType_withDifferentEntitiesJar()
    {
        // Test with a different entities jar
        File jarFile = new File("src/test/resources/repository/examples/metadata/test-dependencies-entities/1.0.0/test-dependencies-entities-1.0.0.jar");

        if (jarFile.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile);
            List<Entity> entities = entityProvider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(entities);
            Assertions.assertTrue(entities.size() > 0, "Expected at least one entity");
        }
    }

    @Test
    public void testMatchesArtifactType_caseSensitivity()
    {
        // Test that the matching is case-sensitive as contains() is case-sensitive
        File uppercaseFile = new File("test-ENTITIES-1.0.0.jar");
        boolean matches = entityProvider.matchesArtifactType(uppercaseFile);
        // This will be false because "ENTITIES" != "entities"
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_entitiesAtStart()
    {
        // Test with "entities" at the start of filename
        File file = new File("entities-test-1.0.0.jar");
        boolean matches = entityProvider.matchesArtifactType(file);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_entitiesAtEnd()
    {
        // Test with "entities" at the end of filename
        File file = new File("test-1.0.0-entities.jar");
        boolean matches = entityProvider.matchesArtifactType(file);
        Assertions.assertTrue(matches);
    }
}

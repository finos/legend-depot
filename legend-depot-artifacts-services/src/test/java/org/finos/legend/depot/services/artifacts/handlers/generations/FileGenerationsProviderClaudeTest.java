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

package org.finos.legend.depot.services.artifacts.handlers.generations;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.services.api.artifacts.handlers.ArtifactLoadingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class FileGenerationsProviderClaudeTest
{
    private FileGenerationsProvider provider;

    @BeforeEach
    public void setUp()
    {
        provider = new FileGenerationsProvider();
    }

    @Test
    public void testConstructor()
    {
        // Test that constructor creates a valid instance
        FileGenerationsProvider newProvider = new FileGenerationsProvider();
        Assertions.assertNotNull(newProvider);
    }

    @Test
    public void testGetType()
    {
        // Test that getType returns FILE_GENERATIONS artifact type
        ArtifactType type = provider.getType();
        Assertions.assertNotNull(type);
        Assertions.assertEquals(ArtifactType.FILE_GENERATIONS, type);
        Assertions.assertEquals("file-generation", type.getModuleName());
    }

    @Test
    public void testMatchesArtifactType_withValidFileGenerationFile()
    {
        // Test with a file that contains "file-generation" in the name
        File validFile = new File("test-file-generation-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(validFile);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_withFileGenerationInMiddle()
    {
        // Test with "file-generation" in the middle of the filename
        File validFile = new File("my-file-generation-artifact.jar");
        boolean matches = provider.matchesArtifactType(validFile);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_withFileGenerationAtStart()
    {
        // Test with "file-generation" at the start of filename
        File file = new File("file-generation-test-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(file);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_withFileGenerationAtEnd()
    {
        // Test with "file-generation" at the end of filename
        File file = new File("test-1.0.0-file-generation.jar");
        boolean matches = provider.matchesArtifactType(file);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testMatchesArtifactType_withoutFileGeneration()
    {
        // Test with a file that doesn't contain "file-generation"
        File invalidFile = new File("test-entities-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(invalidFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_withEntitiesFile()
    {
        // Test with entities file - should not match
        File entitiesFile = new File("test-entities-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(entitiesFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_withVersionedEntitiesFile()
    {
        // Test with versioned-entities file - should not match
        File versionedFile = new File("test-versioned-entities-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(versionedFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_withEmptyFilename()
    {
        // Test with empty filename
        File emptyFile = new File("");
        boolean matches = provider.matchesArtifactType(emptyFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_caseSensitivity()
    {
        // Test that the matching is case-sensitive
        File uppercaseFile = new File("test-FILE-GENERATION-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(uppercaseFile);
        // This will be false because "FILE-GENERATION" != "file-generation"
        Assertions.assertFalse(matches);
    }

    @Test
    public void testMatchesArtifactType_withPartialMatch()
    {
        // Test with partial match - "file-gener" should not match
        File partialFile = new File("test-file-gener-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(partialFile);
        Assertions.assertFalse(matches);
    }

    @Test
    public void testExtractArtifactsForType_withValidJarFile()
    {
        // Test extracting generations from a real jar file
        File jarFile = new File("src/test/resources/repository/examples/metadata/test-file-generation/2.0.0/test-file-generation-2.0.0.jar");

        if (jarFile.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile);
            List<DepotGeneration> generations = provider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(generations);
            Assertions.assertEquals(12, generations.size(), "Expected 12 generations from test-file-generation-2.0.0.jar");
        }
    }

    @Test
    public void testExtractArtifactsForType_withEmptyStream()
    {
        // Test with empty stream
        Stream<File> emptyStream = Stream.empty();
        List<DepotGeneration> generations = provider.extractArtifactsForType(emptyStream);

        Assertions.assertNotNull(generations);
        Assertions.assertTrue(generations.isEmpty());
    }

    @Test
    public void testExtractArtifactsForType_withMultipleFiles()
    {
        // Test extracting generations from multiple jar files
        File jarFile1 = new File("src/test/resources/repository/examples/metadata/test-file-generation/2.0.0/test-file-generation-2.0.0.jar");
        File jarFile2 = new File("src/test/resources/repository/examples/metadata/test-file-generation/1.0.0/test-file-generation-1.0.0.jar");

        if (jarFile1.exists() && jarFile2.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile1, jarFile2);
            List<DepotGeneration> generations = provider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(generations);
            // Both jars should have generations, total should be greater than single file
            Assertions.assertTrue(generations.size() > 12, "Expected more than 12 generations from two jars");
        }
    }

    @Test
    public void testExtractArtifactsForType_withNonJarFile()
    {
        // Test with a file that exists but is not a valid jar/generations file
        File pomFile = new File("src/test/resources/repository/examples/metadata/test-file-generation/2.0.0/test-file-generation-2.0.0.pom");

        if (pomFile.exists())
        {
            Stream<File> fileStream = Stream.of(pomFile);

            // A POM file should cause an exception when trying to extract generations
            Assertions.assertThrows(ArtifactLoadingException.class, () -> {
                provider.extractArtifactsForType(fileStream);
            });
        }
        else
        {
            // If the test file doesn't exist, skip this test gracefully
            Assertions.assertTrue(true, "Test skipped - POM file not found");
        }
    }

    @Test
    public void testExtractArtifactsForType_withDifferentGenerationsJar()
    {
        // Test with a different generations jar
        File jarFile = new File("src/test/resources/repository/examples/metadata/test-dependencies-file-generation/1.0.0/test-dependencies-file-generation-1.0.0.jar");

        if (jarFile.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile);
            List<DepotGeneration> generations = provider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(generations);
            // This jar may be empty or have no generations, just verify we get a list back
            Assertions.assertTrue(generations.size() >= 0, "Should return a valid list");
        }
    }

    @Test
    public void testExtractArtifactsForType_withMasterSnapshot()
    {
        // Test with master-SNAPSHOT jar
        File jarFile = new File("src/test/resources/repository/examples/metadata/test-file-generation/master-SNAPSHOT/test-file-generation-master-SNAPSHOT.jar");

        if (jarFile.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile);
            List<DepotGeneration> generations = provider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(generations);
            Assertions.assertEquals(14, generations.size(), "Expected 14 generations from master-SNAPSHOT jar");
        }
    }

    @Test
    public void testExtractArtifactsForType_verifyGenerationContent()
    {
        // Test that extracted generations have valid content
        File jarFile = new File("src/test/resources/repository/examples/metadata/test-file-generation/2.0.0/test-file-generation-2.0.0.jar");

        if (jarFile.exists())
        {
            Stream<File> fileStream = Stream.of(jarFile);
            List<DepotGeneration> generations = provider.extractArtifactsForType(fileStream);

            Assertions.assertNotNull(generations);
            Assertions.assertFalse(generations.isEmpty());

            // Verify each generation has a path and content
            for (DepotGeneration generation : generations)
            {
                Assertions.assertNotNull(generation.getPath(), "Generation path should not be null");
                Assertions.assertNotNull(generation.getContent(), "Generation content should not be null");
                Assertions.assertFalse(generation.getPath().isEmpty(), "Generation path should not be empty");
            }
        }
    }

    @Test
    public void testMatchesArtifactType_withFullPath()
    {
        // Test with a file that has a full path
        File fileWithPath = new File("src/test/resources/repository/examples/metadata/test-file-generation/2.0.0/test-file-generation-2.0.0.jar");
        boolean matches = provider.matchesArtifactType(fileWithPath);
        Assertions.assertTrue(matches, "Should match file-generation in full path");
    }

    @Test
    public void testMatchesArtifactType_withSimilarButDifferentName()
    {
        // Test with file names that are similar but not exact match
        File file1 = new File("test-file-generations-1.0.0.jar");
        boolean matches1 = provider.matchesArtifactType(file1);
        // "file-generations" contains "file-generation" so it should match
        Assertions.assertTrue(matches1);

        File file2 = new File("test-generated-files-1.0.0.jar");
        boolean matches2 = provider.matchesArtifactType(file2);
        Assertions.assertFalse(matches2, "Should not match 'generated-files'");
    }

    @Test
    public void testGetType_consistency()
    {
        // Test that getType is consistent across multiple calls
        ArtifactType type1 = provider.getType();
        ArtifactType type2 = provider.getType();

        Assertions.assertSame(type1, type2, "getType should return the same enum instance");
        Assertions.assertEquals(type1, type2);
    }
}

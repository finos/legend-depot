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

package org.finos.legend.depot.services.generations.loader;

import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class FileGenerationLoaderClaudeTest


{
    private static final URL JAR_FILE_PATH = FileGenerationLoaderClaudeTest.class.getClassLoader()
            .getResource("generations/test-file-generation-master-SNAPSHOT.jar");

    @Test
    public void testNewFileGenerationsLoaderWithPathOnJarFile() throws URISyntaxException
    {
        // Test that we can create a loader from a JAR file path
        assertNotNull(JAR_FILE_PATH, "Test JAR file should exist in resources");

        Path jarPath = Paths.get(JAR_FILE_PATH.toURI());
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(jarPath);

        assertNotNull(loader, "Loader should not be null");

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertFalse(generations.isEmpty(), "Should have loaded generations from JAR");
        assertEquals(14, generations.size(), "Expected 14 generations from test JAR");
    }

    @Test
    public void testNewFileGenerationsLoaderWithFileOnJarFile() throws URISyntaxException
    {
        // Test that we can create a loader from a JAR file
        assertNotNull(JAR_FILE_PATH, "Test JAR file should exist in resources");

        File jarFile = new File(JAR_FILE_PATH.toURI());
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(jarFile);

        assertNotNull(loader, "Loader should not be null");

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertFalse(generations.isEmpty(), "Should have loaded generations from JAR");
        assertEquals(14, generations.size(), "Expected 14 generations from test JAR");
    }

    @Test
    public void testNewFileGenerationsLoaderWithPathOnDirectory(@TempDir Path tempDir) throws IOException
    {
        // Create a temporary directory with test files
        Path testFile1 = tempDir.resolve("test1.txt");
        Path testFile2 = tempDir.resolve("test2.txt");

        Files.write(testFile1, "Test content 1".getBytes());
        Files.write(testFile2, "Test content 2".getBytes());

        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

        assertNotNull(loader, "Loader should not be null");

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertEquals(2, generations.size(), "Should have loaded 2 generations from directory");

        // Verify content of loaded generations
        List<String> contents = generations.stream()
                .map(DepotGeneration::getContent)
                .collect(Collectors.toList());
        assertTrue(contents.contains("Test content 1"), "Should contain first test content");
        assertTrue(contents.contains("Test content 2"), "Should contain second test content");
    }

    @Test
    public void testNewFileGenerationsLoaderWithFileOnDirectory(@TempDir Path tempDir) throws IOException
    {
        // Create a temporary directory with test files
        Path testFile = tempDir.resolve("testfile.txt");
        Files.write(testFile, "Sample content".getBytes());

        File directory = tempDir.toFile();
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(directory);

        assertNotNull(loader, "Loader should not be null");

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertEquals(1, generations.size(), "Should have loaded 1 generation from directory");
        assertEquals("Sample content", generations.get(0).getContent());
    }

    @Test
    public void testNewFileGenerationsLoaderWithNonExistentPath()
  {
        // Test loading from a non-existent path
        Path nonExistentPath = Paths.get("/non/existent/path/to/nowhere");
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(nonExistentPath);

        assertNotNull(loader, "Loader should not be null even for non-existent path");

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertTrue(generations.isEmpty(), "Should have no generations for non-existent path");
    }

    @Test
    public void testNewFileGenerationsLoaderWithNonExistentFile()
  {
        // Test loading from a non-existent file
        File nonExistentFile = new File("/non/existent/file.jar");
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(nonExistentFile);

        assertNotNull(loader, "Loader should not be null even for non-existent file");

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertTrue(generations.isEmpty(), "Should have no generations for non-existent file");
    }

    @Test
    public void testGetAllFileGenerationsReturnsStream() throws URISyntaxException
    {
        // Verify that getAllFileGenerations returns a proper stream
        assertNotNull(JAR_FILE_PATH, "Test JAR file should exist in resources");

        Path jarPath = Paths.get(JAR_FILE_PATH.toURI());
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(jarPath);

        // Verify we can use stream operations
        long count = loader.getAllFileGenerations().count();
        assertEquals(14, count, "Stream should have 14 elements");
    }

    @Test
    public void testGetAllFileGenerationsWithEmptyDirectory(@TempDir Path tempDir)
  {
        // Test loading from an empty directory
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertNotNull(generations, "Generations list should not be null");
        assertTrue(generations.isEmpty(), "Should have no generations from empty directory");
    }

    @Test
    public void testGetAllFileGenerationsWithNestedDirectories(@TempDir Path tempDir) throws IOException
    {
        // Create nested directory structure
        Path subDir1 = tempDir.resolve("subdir1");
        Path subDir2 = tempDir.resolve("subdir2");
        Files.createDirectories(subDir1);
        Files.createDirectories(subDir2);

        Path file1 = tempDir.resolve("root.txt");
        Path file2 = subDir1.resolve("nested1.txt");
        Path file3 = subDir2.resolve("nested2.txt");

        Files.write(file1, "Root content".getBytes());
        Files.write(file2, "Nested content 1".getBytes());
        Files.write(file3, "Nested content 2".getBytes());

        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertEquals(3, generations.size(), "Should load files from nested directories");

        List<String> contents = generations.stream()
                .map(DepotGeneration::getContent)
                .collect(Collectors.toList());
        assertTrue(contents.contains("Root content"));
        assertTrue(contents.contains("Nested content 1"));
        assertTrue(contents.contains("Nested content 2"));
    }

    @Test
    public void testCloseWithJarFileLoader() throws Exception
    {
        // Test that close() properly closes resources when loading from a JAR file
        assertNotNull(JAR_FILE_PATH, "Test JAR file should exist in resources");

        Path jarPath = Paths.get(JAR_FILE_PATH.toURI());
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(jarPath);

        // Use the loader first
        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
        assertFalse(generations.isEmpty(), "Should have loaded generations");

        // Close should not throw an exception
        assertDoesNotThrow(() -> loader.close(), "close() should not throw an exception");
    }

    @Test
    public void testCloseWithDirectoryLoader() throws Exception
    {
        // Test that close() works properly with a directory-based loader
        Path tempDir = Files.createTempDirectory("test-loader");
        try
        {
            Path testFile = tempDir.resolve("test.txt");
            Files.write(testFile, "Test content".getBytes());

            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            // Use the loader
            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
            assertEquals(1, generations.size());

            // Close should not throw an exception
            assertDoesNotThrow(() -> loader.close(), "close() should not throw an exception");
        }
        finally
        {
            // Clean up
            Files.deleteIfExists(tempDir.resolve("test.txt"));
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testCloseWithNonExistentPath() throws Exception
    {
        // Test that close() works even when created with non-existent path
        Path nonExistentPath = Paths.get("/non/existent/path");
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(nonExistentPath);

        // Close should not throw an exception even for empty loader
        assertDoesNotThrow(() -> loader.close(), "close() should not throw an exception for non-existent path");
    }

    @Test
    public void testCloseCanBeCalledMultipleTimes() throws Exception
    {
        // Test that close() is idempotent and can be called multiple times
        assertNotNull(JAR_FILE_PATH, "Test JAR file should exist in resources");

        Path jarPath = Paths.get(JAR_FILE_PATH.toURI());
        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(jarPath);

        // Close multiple times
        assertDoesNotThrow(() -> loader.close(), "First close() should not throw");
        assertDoesNotThrow(() -> loader.close(), "Second close() should not throw");
        assertDoesNotThrow(() -> loader.close(), "Third close() should not throw");
    }

    @Test
    public void testLoaderWithTryWithResources() throws Exception
    {
        // Test that loader works properly with try-with-resources
        assertNotNull(JAR_FILE_PATH, "Test JAR file should exist in resources");

        Path jarPath = Paths.get(JAR_FILE_PATH.toURI());
        List<DepotGeneration> generations;

        try (FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(jarPath))
        {
            generations = loader.getAllFileGenerations().collect(Collectors.toList());
        }

        assertNotNull(generations, "Generations should be loaded inside try-with-resources");
        assertEquals(14, generations.size(), "Should have loaded all generations");
    }

    @Test
    public void testGetAllFileGenerationsFiltersMETAINFFiles(@TempDir Path tempDir) throws IOException
    {
        // Test that files in META-INF are NOT filtered out when loading from a directory
        // The META-INF filter only applies when the path starts with /META-INF/
        // In directory mode, paths are absolute and don't start with /META-INF/
        Path metaInfDir = tempDir.resolve("META-INF");
        Files.createDirectories(metaInfDir);

        Path regularFile = tempDir.resolve("regular.txt");
        Path metaInfFile = metaInfDir.resolve("manifest.txt");

        Files.write(regularFile, "Regular content".getBytes());
        Files.write(metaInfFile, "META-INF content".getBytes());

        FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

        List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());

        // When loading from a file system directory, META-INF files are included
        // because the filter checks if path.startsWith("/META-INF/"), which doesn't match absolute paths
        assertEquals(2, generations.size(), "Should load both regular and META-INF files from directory");

        List<String> contents = generations.stream()
                .map(DepotGeneration::getContent)
                .collect(Collectors.toList());
        assertTrue(contents.contains("Regular content"));
        assertTrue(contents.contains("META-INF content"));
    }

    @Test
    public void testGetAllFileGenerationsContentAndPath() throws URISyntaxException, IOException
    {
        // Test that DepotGeneration objects have proper path and content
        Path tempDir = Files.createTempDirectory("test-gen");
        try
        {
            Path testFile = tempDir.resolve("test.txt");
            String expectedContent = "Test generation content";
            Files.write(testFile, expectedContent.getBytes());

            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());
            assertEquals(1, generations.size());

            DepotGeneration generation = generations.get(0);
            assertNotNull(generation.getPath(), "Path should not be null");
            assertTrue(generation.getPath().contains("test.txt"), "Path should contain filename");
            assertEquals(expectedContent, generation.getContent(), "Content should match");
        }
        finally
        {
            Files.deleteIfExists(tempDir.resolve("test.txt"));
            Files.deleteIfExists(tempDir);
        }
    }
}

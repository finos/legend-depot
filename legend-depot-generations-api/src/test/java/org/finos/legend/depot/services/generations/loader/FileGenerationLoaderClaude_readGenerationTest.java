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
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to cover the private readGeneration method through public API calls.
 * This class specifically targets coverage of lines 77, 79, 80 in FileGenerationLoader
 * which handle IOException during file reading.
 */
public class FileGenerationLoaderClaude_readGenerationTest
{
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetAllFileGenerationsWithUnreadableFile(@TempDir Path tempDir) throws IOException
    {
        // This test covers lines 77, 79, 80 by creating a file without read permissions
        // which causes IOException when readGeneration tries to open it

        Path readableFile = tempDir.resolve("readable.txt");
        Path unreadableFile = tempDir.resolve("unreadable.txt");

        Files.write(readableFile, "Readable content".getBytes());
        Files.write(unreadableFile, "Unreadable content".getBytes());

        // Remove all read permissions from the unreadable file
        Set<PosixFilePermission> noReadPermissions = new HashSet<>();
        noReadPermissions.add(PosixFilePermission.OWNER_WRITE);
        noReadPermissions.add(PosixFilePermission.OWNER_EXECUTE);

        try
        {
            Files.setPosixFilePermissions(unreadableFile, noReadPermissions);

            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());

            // The unreadable file should be filtered out (returns null and is filtered by Objects::nonNull)
            // Only the readable file should be in the results
            assertEquals(1, generations.size(), "Should only load the readable file");

            DepotGeneration generation = generations.get(0);
            assertEquals("Readable content", generation.getContent());
            assertTrue(generation.getPath().contains("readable.txt"));
        }
        finally
        {
            // Restore permissions for cleanup
            try
            {
                Set<PosixFilePermission> restorePermissions = new HashSet<>();
                restorePermissions.add(PosixFilePermission.OWNER_READ);
                restorePermissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(unreadableFile, restorePermissions);
            }
            catch (Exception e)
            {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetAllFileGenerationsWithMultipleUnreadableFiles(@TempDir Path tempDir) throws IOException
    {
        // Test with multiple unreadable files to ensure error handling works consistently

        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Path file3 = tempDir.resolve("file3.txt");

        Files.write(file1, "Content 1".getBytes());
        Files.write(file2, "Content 2".getBytes());
        Files.write(file3, "Content 3".getBytes());

        Set<PosixFilePermission> noReadPermissions = new HashSet<>();
        noReadPermissions.add(PosixFilePermission.OWNER_WRITE);

        try
        {
            // Make file1 and file3 unreadable
            Files.setPosixFilePermissions(file1, noReadPermissions);
            Files.setPosixFilePermissions(file3, noReadPermissions);

            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());

            // Only file2 should be readable
            assertEquals(1, generations.size(), "Should only load the one readable file");
            assertEquals("Content 2", generations.get(0).getContent());
        }
        finally
        {
            // Restore permissions for cleanup
            try
            {
                Set<PosixFilePermission> restorePermissions = new HashSet<>();
                restorePermissions.add(PosixFilePermission.OWNER_READ);
                restorePermissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(file1, restorePermissions);
                Files.setPosixFilePermissions(file3, restorePermissions);
            }
            catch (Exception e)
            {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetAllFileGenerationsWithAllUnreadableFiles(@TempDir Path tempDir) throws IOException
    {
        // Test when all files are unreadable - should return empty list

        Path file1 = tempDir.resolve("unreadable1.txt");
        Path file2 = tempDir.resolve("unreadable2.txt");

        Files.write(file1, "Content 1".getBytes());
        Files.write(file2, "Content 2".getBytes());

        Set<PosixFilePermission> noReadPermissions = new HashSet<>();
        noReadPermissions.add(PosixFilePermission.OWNER_WRITE);

        try
        {
            Files.setPosixFilePermissions(file1, noReadPermissions);
            Files.setPosixFilePermissions(file2, noReadPermissions);

            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());

            // All files are unreadable, so should get empty list
            assertTrue(generations.isEmpty(), "Should have no readable files");
        }
        finally
        {
            // Restore permissions for cleanup
            try
            {
                Set<PosixFilePermission> restorePermissions = new HashSet<>();
                restorePermissions.add(PosixFilePermission.OWNER_READ);
                restorePermissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(file1, restorePermissions);
                Files.setPosixFilePermissions(file2, restorePermissions);
            }
            catch (Exception e)
            {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void testGetAllFileGenerationsWithUnreadableFileInNestedDirectory(@TempDir Path tempDir) throws IOException
    {
        // Test that error handling works in nested directory structures

        Path subDir = tempDir.resolve("subdir");
        Files.createDirectories(subDir);

        Path readableFile = tempDir.resolve("root.txt");
        Path unreadableFile = subDir.resolve("nested_unreadable.txt");
        Path readableNestedFile = subDir.resolve("nested_readable.txt");

        Files.write(readableFile, "Root content".getBytes());
        Files.write(unreadableFile, "Nested unreadable content".getBytes());
        Files.write(readableNestedFile, "Nested readable content".getBytes());

        Set<PosixFilePermission> noReadPermissions = new HashSet<>();
        noReadPermissions.add(PosixFilePermission.OWNER_WRITE);

        try
        {
            Files.setPosixFilePermissions(unreadableFile, noReadPermissions);

            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());

            // Should get 2 readable files
            assertEquals(2, generations.size(), "Should load 2 readable files");

            List<String> contents = generations.stream()
                    .map(DepotGeneration::getContent)
                    .collect(Collectors.toList());

            assertTrue(contents.contains("Root content"));
            assertTrue(contents.contains("Nested readable content"));
            assertFalse(contents.contains("Nested unreadable content"));
        }
        finally
        {
            // Restore permissions for cleanup
            try
            {
                Set<PosixFilePermission> restorePermissions = new HashSet<>();
                restorePermissions.add(PosixFilePermission.OWNER_READ);
                restorePermissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(unreadableFile, restorePermissions);
            }
            catch (Exception e)
            {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testGetAllFileGenerationsWithReadOnlyFileOnWindows(@TempDir Path tempDir) throws IOException
    {
        // Windows version - use read-only attribute instead of POSIX permissions
        // Note: Windows read-only doesn't prevent reading, so this test ensures
        // the method works correctly with read-only files (which should still be readable)

        Path readOnlyFile = tempDir.resolve("readonly.txt");
        Path normalFile = tempDir.resolve("normal.txt");

        Files.write(readOnlyFile, "Read-only content".getBytes());
        Files.write(normalFile, "Normal content".getBytes());

        // Set file to read-only
        File file = readOnlyFile.toFile();
        file.setReadOnly();

        try
        {
            FileGenerationLoader loader = FileGenerationLoader.newFileGenerationsLoader(tempDir);

            List<DepotGeneration> generations = loader.getAllFileGenerations().collect(Collectors.toList());

            // On Windows, read-only files are still readable
            assertEquals(2, generations.size(), "Should load both files on Windows");

            List<String> contents = generations.stream()
                    .map(DepotGeneration::getContent)
                    .collect(Collectors.toList());

            assertTrue(contents.contains("Read-only content"));
            assertTrue(contents.contains("Normal content"));
        }
        finally
        {
            // Restore write permissions for cleanup
            file.setWritable(true);
        }
    }
}

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

package org.finos.legend.depot.services.api.artifacts.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ArtifactsExtractorClaudeTest 

{

    /**
     * Test implementation of ArtifactsExtractor for testing purposes.
     * This extractor matches files that contain "entities" in their name.
     */
    private static class TestArtifactsExtractor implements ArtifactsExtractor<String> {
        private final ArtifactType artifactType;
        private final String matchPattern;

        public TestArtifactsExtractor(ArtifactType artifactType, String matchPattern)
  {
            this.artifactType = artifactType;
            this.matchPattern = matchPattern;
        }

        @Override
        public ArtifactType getType()
  {
            return artifactType;
        }

        @Override
        public boolean matchesArtifactType(File file)
  {
            return file != null && file.getName().contains(matchPattern);
        }

        @Override
        public List<String> extractArtifactsForType(Stream<File> files) {
            return files.map(File::getName).collect(Collectors.toList());
        }
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with matching files.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with matching files")
    void testExtractArtifactsWithMatchingFiles(@TempDir Path tempDir) throws IOException {
        // Arrange
        File entitiesFile1 = tempDir.resolve("test-entities-1.jar").toFile();
        File entitiesFile2 = tempDir.resolve("test-entities-2.jar").toFile();
        File otherFile = tempDir.resolve("test-other.jar").toFile();

        entitiesFile1.createNewFile();
        entitiesFile2.createNewFile();
        otherFile.createNewFile();

        List<File> files = Arrays.asList(entitiesFile1, entitiesFile2, otherFile);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("test-entities-1.jar"));
        assertTrue(result.contains("test-entities-2.jar"));
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with no matching files.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with no matching files")
    void testExtractArtifactsWithNoMatchingFiles(@TempDir Path tempDir) throws IOException {
        // Arrange
        File file1 = tempDir.resolve("test-other-1.jar").toFile();
        File file2 = tempDir.resolve("test-other-2.jar").toFile();

        file1.createNewFile();
        file2.createNewFile();

        List<File> files = Arrays.asList(file1, file2);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with empty list.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with empty list")
    void testExtractArtifactsWithEmptyList()
  {
        // Arrange
        List<File> files = Collections.emptyList();
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with all matching files.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with all matching files")
    void testExtractArtifactsWithAllMatchingFiles(@TempDir Path tempDir) throws IOException {
        // Arrange
        File file1 = tempDir.resolve("entities-1.jar").toFile();
        File file2 = tempDir.resolve("entities-2.jar").toFile();
        File file3 = tempDir.resolve("versioned-entities-1.jar").toFile();

        file1.createNewFile();
        file2.createNewFile();
        file3.createNewFile();

        List<File> files = Arrays.asList(file1, file2, file3);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("entities-1.jar"));
        assertTrue(result.contains("entities-2.jar"));
        assertTrue(result.contains("versioned-entities-1.jar"));
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with single matching file.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with single matching file")
    void testExtractArtifactsWithSingleMatchingFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        File entitiesFile = tempDir.resolve("test-entities.jar").toFile();
        entitiesFile.createNewFile();

        List<File> files = Collections.singletonList(entitiesFile);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-entities.jar", result.get(0));
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with different artifact types.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with file-generation artifact type")
    void testExtractArtifactsWithFileGenerationType(@TempDir Path tempDir) throws IOException {
        // Arrange
        File generationFile = tempDir.resolve("test-file-generation.jar").toFile();
        File entitiesFile = tempDir.resolve("test-entities.jar").toFile();

        generationFile.createNewFile();
        entitiesFile.createNewFile();

        List<File> files = Arrays.asList(generationFile, entitiesFile);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(
            ArtifactType.FILE_GENERATIONS, "file-generation"
        );

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-file-generation.jar", result.get(0));
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with versioned entities.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with versioned-entities artifact type")
    void testExtractArtifactsWithVersionedEntitiesType(@TempDir Path tempDir) throws IOException {
        // Arrange
        File versionedFile = tempDir.resolve("test-versioned-entities.jar").toFile();
        File entitiesFile = tempDir.resolve("test-entities.jar").toFile();

        versionedFile.createNewFile();
        entitiesFile.createNewFile();

        List<File> files = Arrays.asList(versionedFile, entitiesFile);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(
            ArtifactType.VERSIONED_ENTITIES, "versioned-entities"
        );

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-versioned-entities.jar", result.get(0));
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with mixed file order.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts preserves order of matching files")
    void testExtractArtifactsPreservesOrder(@TempDir Path tempDir) throws IOException {
        // Arrange
        File file1 = tempDir.resolve("a-entities.jar").toFile();
        File file2 = tempDir.resolve("other.jar").toFile();
        File file3 = tempDir.resolve("b-entities.jar").toFile();
        File file4 = tempDir.resolve("c-entities.jar").toFile();

        file1.createNewFile();
        file2.createNewFile();
        file3.createNewFile();
        file4.createNewFile();

        List<File> files = Arrays.asList(file1, file2, file3, file4);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("a-entities.jar", result.get(0));
        assertEquals("b-entities.jar", result.get(1));
        assertEquals("c-entities.jar", result.get(2));
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} with large list of files.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts with large list of files")
    void testExtractArtifactsWithLargeList(@TempDir Path tempDir) throws IOException {
        // Arrange
        List<File> files = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String fileName = i % 2 == 0 ? "entities-" + i + ".jar" : "other-" + i + ".jar";
            File file = tempDir.resolve(fileName).toFile();
            file.createNewFile();
            files.add(file);
        }

        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(50, result.size());
        for (String artifact : result) {
            assertTrue(artifact.contains("entities"));
        }
    }

    /**
     * Test {@link ArtifactsExtractor#extractArtifacts(List)} filtering behavior.
     * Verifies that the default implementation correctly filters using matchesArtifactType.
     *
     * <p>Method under test: {@link ArtifactsExtractor#extractArtifacts(List)}
     */
    @Test
    @DisplayName("Test extractArtifacts correctly filters files")
    void testExtractArtifactsFiltering(@TempDir Path tempDir) throws IOException {
        // Arrange
        File matchingFile = tempDir.resolve("my-entities-artifact.jar").toFile();
        File nonMatchingFile1 = tempDir.resolve("generation.jar").toFile();
        File nonMatchingFile2 = tempDir.resolve("test.jar").toFile();

        matchingFile.createNewFile();
        nonMatchingFile1.createNewFile();
        nonMatchingFile2.createNewFile();

        List<File> files = Arrays.asList(nonMatchingFile1, matchingFile, nonMatchingFile2);
        TestArtifactsExtractor extractor = new TestArtifactsExtractor(ArtifactType.ENTITIES, "entities");

        // Act
        List<String> result = extractor.extractArtifacts(files);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("my-entities-artifact.jar", result.get(0));
    }
}

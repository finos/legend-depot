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

package org.finos.legend.depot.store.mongo.artifacts;

import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ArtifactsFilesMongoClaude_findTest extends TestStoreMongo
{
    @Test
    public void testFindReturnsEmptyWhenNoMatch()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        Optional<ArtifactFile> result = artifactsFilesMongo.find("non-existent/path.jar");

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void testFindReturnsArtifactWhenExists()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store an artifact
        String testPath = "test/find/path.jar";
        ArtifactFile artifactFile = new ArtifactFile(testPath, "checksum123");
        artifactsFilesMongo.createOrUpdate(artifactFile);

        // Find it
        Optional<ArtifactFile> result = artifactsFilesMongo.find(testPath);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(testPath, result.get().getPath());
        Assertions.assertEquals("checksum123", result.get().getCheckSum());
    }

    @Test
    public void testFindWithExactPathMatch()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store multiple artifacts
        artifactsFilesMongo.createOrUpdate(new ArtifactFile("test/path1.jar", "checksum1"));
        artifactsFilesMongo.createOrUpdate(new ArtifactFile("test/path2.jar", "checksum2"));
        artifactsFilesMongo.createOrUpdate(new ArtifactFile("test/path3.jar", "checksum3"));

        // Find specific one
        Optional<ArtifactFile> result = artifactsFilesMongo.find("test/path2.jar");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("test/path2.jar", result.get().getPath());
        Assertions.assertEquals("checksum2", result.get().getCheckSum());
    }

    @Test
    public void testFindWithNullPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store an artifact with null path
        ArtifactFile artifactFile = new ArtifactFile(null, "checksum123");
        artifactsFilesMongo.createOrUpdate(artifactFile);

        // Try to find with null path
        Optional<ArtifactFile> result = artifactsFilesMongo.find(null);

        // Should find the artifact with null path
        Assertions.assertTrue(result.isPresent());
        Assertions.assertNull(result.get().getPath());
        Assertions.assertEquals("checksum123", result.get().getCheckSum());
    }

    @Test
    public void testFindWithEmptyPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store an artifact with empty path
        ArtifactFile artifactFile = new ArtifactFile("", "checksum123");
        artifactsFilesMongo.createOrUpdate(artifactFile);

        // Find with empty path
        Optional<ArtifactFile> result = artifactsFilesMongo.find("");

        Assertions.assertTrue(result.isPresent());
        // Empty string paths may be stored as null depending on MongoDB behavior
        String foundPath = result.get().getPath();
        Assertions.assertTrue(foundPath == null || foundPath.equals(""));
    }

    @Test
    public void testFindIsCaseSensitive()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store artifact with specific case
        String testPath = "Test/Path.jar";
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(testPath, "checksum123"));

        // Try to find with different case
        Optional<ArtifactFile> result = artifactsFilesMongo.find("test/path.jar");

        // Should not find (case-sensitive)
        Assertions.assertFalse(result.isPresent());

        // Find with exact case should work
        Optional<ArtifactFile> correctResult = artifactsFilesMongo.find(testPath);
        Assertions.assertTrue(correctResult.isPresent());
    }

    @Test
    public void testFindDoesNotMatchPartialPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store artifact
        artifactsFilesMongo.createOrUpdate(new ArtifactFile("test/path/to/artifact.jar", "checksum123"));

        // Try to find with partial path
        Optional<ArtifactFile> result1 = artifactsFilesMongo.find("test/path");
        Optional<ArtifactFile> result2 = artifactsFilesMongo.find("artifact.jar");
        Optional<ArtifactFile> result3 = artifactsFilesMongo.find("path/to");

        // Should not match partial paths
        Assertions.assertFalse(result1.isPresent());
        Assertions.assertFalse(result2.isPresent());
        Assertions.assertFalse(result3.isPresent());

        // Only exact match should work
        Optional<ArtifactFile> exactMatch = artifactsFilesMongo.find("test/path/to/artifact.jar");
        Assertions.assertTrue(exactMatch.isPresent());
    }

    @Test
    public void testFindAfterUpdate()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        String testPath = "test/updated/path.jar";

        // Store initial artifact
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(testPath, "checksum1"));

        // Update it
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(testPath, "checksum2"));

        // Find should return updated version
        Optional<ArtifactFile> result = artifactsFilesMongo.find(testPath);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(testPath, result.get().getPath());
        Assertions.assertEquals("checksum2", result.get().getCheckSum());
    }

    @Test
    public void testFindWithSpecialCharactersInPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store artifact with special characters
        String specialPath = "test/path-with_special@chars#2.0.jar";
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(specialPath, "checksum123"));

        // Find it
        Optional<ArtifactFile> result = artifactsFilesMongo.find(specialPath);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(specialPath, result.get().getPath());
    }

    @Test
    public void testFindReturnsOptionalEmpty()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        Optional<ArtifactFile> result = artifactsFilesMongo.find("not-found.jar");

        // Should return Optional.empty(), not null
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    public void testFindWithWhitespaceInPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Store artifact with whitespace
        String pathWithSpaces = "test/path with spaces/artifact.jar";
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(pathWithSpaces, "checksum123"));

        // Find it
        Optional<ArtifactFile> result = artifactsFilesMongo.find(pathWithSpaces);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(pathWithSpaces, result.get().getPath());
    }

    @Test
    public void testFindMultipleTimes()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        String testPath = "test/repeated/path.jar";

        // Store artifact
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(testPath, "checksum123"));

        // Find multiple times should return consistent results
        Optional<ArtifactFile> result1 = artifactsFilesMongo.find(testPath);
        Optional<ArtifactFile> result2 = artifactsFilesMongo.find(testPath);
        Optional<ArtifactFile> result3 = artifactsFilesMongo.find(testPath);

        Assertions.assertTrue(result1.isPresent());
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertTrue(result3.isPresent());

        // All should have same data
        Assertions.assertEquals(result1.get().getPath(), result2.get().getPath());
        Assertions.assertEquals(result1.get().getCheckSum(), result2.get().getCheckSum());
        Assertions.assertEquals(result2.get().getPath(), result3.get().getPath());
    }

    @Test
    public void testFindWithVeryLongPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Create very long path
        StringBuilder longPath = new StringBuilder("test/");
        for (int i = 0; i < 50; i++) {
            longPath.append("nested/directory/");
        }
        longPath.append("artifact.jar");

        String testPath = longPath.toString();
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(testPath, "checksum123"));

        // Find it
        Optional<ArtifactFile> result = artifactsFilesMongo.find(testPath);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(testPath, result.get().getPath());
    }

    @Test
    public void testFindFromMultipleInstances()
    {
        ArtifactsFilesMongo instance1 = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactsFilesMongo instance2 = new ArtifactsFilesMongo(this.mongoProvider);

        String testPath = "test/shared/path.jar";

        // Store through instance1
        instance1.createOrUpdate(new ArtifactFile(testPath, "checksum123"));

        // Find through instance2
        Optional<ArtifactFile> result = instance2.find(testPath);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(testPath, result.get().getPath());
        Assertions.assertEquals("checksum123", result.get().getCheckSum());
    }

    @Test
    public void testFindReturnsCompleteArtifactFile()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        String testPath = "test/complete/path.jar";
        String testChecksum = "abc123def456";

        // Store complete artifact
        ArtifactFile original = new ArtifactFile(testPath, testChecksum);
        artifactsFilesMongo.createOrUpdate(original);

        // Find and verify all fields
        Optional<ArtifactFile> result = artifactsFilesMongo.find(testPath);

        Assertions.assertTrue(result.isPresent());
        ArtifactFile found = result.get();
        Assertions.assertEquals(testPath, found.getPath());
        Assertions.assertEquals(testChecksum, found.getCheckSum());
        // Note: ArtifactFile.getId() always returns null per its implementation
    }
}

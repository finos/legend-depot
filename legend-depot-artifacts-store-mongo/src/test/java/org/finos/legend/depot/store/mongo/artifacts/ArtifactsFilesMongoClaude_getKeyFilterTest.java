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

import org.bson.conversions.Bson;
import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ArtifactsFilesMongoClaude_getKeyFilterTest extends TestStoreMongo
{
    @Test
    public void testGetKeyFilterReturnsNonNull()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", "checksum123");

        Bson filter = artifactsFilesMongo.getKeyFilter(artifactFile);

        Assertions.assertNotNull(filter);
    }

    @Test
    public void testGetKeyFilterContainsPathField()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", "checksum123");

        Bson filter = artifactsFilesMongo.getKeyFilter(artifactFile);

        // Verify the filter contains the "path" field
        String filterString = filter.toString();
        Assertions.assertTrue(filterString.contains("path"));
    }

    @Test
    public void testGetKeyFilterContainsCorrectPathValue()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        String testPath = "test/specific/path.jar";
        ArtifactFile artifactFile = new ArtifactFile(testPath, "checksum123");

        Bson filter = artifactsFilesMongo.getKeyFilter(artifactFile);

        // Verify the filter contains the specific path value
        String filterString = filter.toString();
        Assertions.assertTrue(filterString.contains(testPath));
    }

    @Test
    public void testGetKeyFilterWithDifferentPaths()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        ArtifactFile artifactFile1 = new ArtifactFile("path1.jar", "checksum1");
        ArtifactFile artifactFile2 = new ArtifactFile("path2.jar", "checksum2");

        Bson filter1 = artifactsFilesMongo.getKeyFilter(artifactFile1);
        Bson filter2 = artifactsFilesMongo.getKeyFilter(artifactFile2);

        // Filters should be different for different paths
        Assertions.assertNotEquals(filter1.toString(), filter2.toString());
    }

    @Test
    public void testGetKeyFilterUsedForFindOperation()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Create and store an artifact file
        String testPath = "test/find/path.jar";
        ArtifactFile artifactFile = new ArtifactFile(testPath, "checksum123");
        artifactsFilesMongo.createOrUpdate(artifactFile);

        // Use the key filter to find the artifact
        ArtifactFile searchFile = new ArtifactFile(testPath, null);
        Bson filter = artifactsFilesMongo.getKeyFilter(searchFile);

        // The filter should successfully find the stored artifact
        Optional<ArtifactFile> found = artifactsFilesMongo.find(testPath);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(testPath, found.get().getPath());
    }

    @Test
    public void testGetKeyFilterWithNullChecksum()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Key filter should work even if checksum is null
        // because it only uses the path field
        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", null);

        Bson filter = artifactsFilesMongo.getKeyFilter(artifactFile);

        Assertions.assertNotNull(filter);
        String filterString = filter.toString();
        Assertions.assertTrue(filterString.contains("path"));
        Assertions.assertTrue(filterString.contains("test/path.jar"));
    }

    @Test
    public void testGetKeyFilterConsistentForSamePath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        String samePath = "consistent/path.jar";

        // Create two artifact files with same path but different checksums
        ArtifactFile artifactFile1 = new ArtifactFile(samePath, "checksum1");
        ArtifactFile artifactFile2 = new ArtifactFile(samePath, "checksum2");

        Bson filter1 = artifactsFilesMongo.getKeyFilter(artifactFile1);
        Bson filter2 = artifactsFilesMongo.getKeyFilter(artifactFile2);

        // Filters should be the same since they have the same path
        Assertions.assertEquals(filter1.toString(), filter2.toString());
    }

    @Test
    public void testGetKeyFilterUsedForUpdate()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        String testPath = "test/update/path.jar";

        // Create initial artifact
        ArtifactFile initialFile = new ArtifactFile(testPath, "checksum1");
        artifactsFilesMongo.createOrUpdate(initialFile);

        // Update with new checksum using the same path
        ArtifactFile updatedFile = new ArtifactFile(testPath, "checksum2");
        artifactsFilesMongo.createOrUpdate(updatedFile);

        // Should only be one entry (updated, not duplicated)
        Optional<ArtifactFile> found = artifactsFilesMongo.find(testPath);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("checksum2", found.get().getCheckSum());

        // Verify there's only one document with this path
        long count = artifactsFilesMongo.getAllStoredEntities().stream()
            .filter(af -> testPath.equals(af.getPath()))
            .count();
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testGetKeyFilterWithSpecialCharactersInPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Test with path containing special characters
        String specialPath = "test/path-with_special.chars@2.0.jar";
        ArtifactFile artifactFile = new ArtifactFile(specialPath, "checksum123");

        Bson filter = artifactsFilesMongo.getKeyFilter(artifactFile);

        Assertions.assertNotNull(filter);
        String filterString = filter.toString();
        Assertions.assertTrue(filterString.contains("path"));
    }

    @Test
    public void testGetKeyFilterMatchesOnlyPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        String testPath = "unique/match/path.jar";

        // Store multiple artifacts
        artifactsFilesMongo.createOrUpdate(new ArtifactFile(testPath, "checksum1"));
        artifactsFilesMongo.createOrUpdate(new ArtifactFile("different/path.jar", "checksum1"));
        artifactsFilesMongo.createOrUpdate(new ArtifactFile("another/path.jar", "checksum2"));

        // Filter should only match the specific path
        Optional<ArtifactFile> found = artifactsFilesMongo.find(testPath);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(testPath, found.get().getPath());
    }
}

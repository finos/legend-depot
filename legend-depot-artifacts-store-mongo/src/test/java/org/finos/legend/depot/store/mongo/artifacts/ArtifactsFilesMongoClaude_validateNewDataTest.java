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

public class ArtifactsFilesMongoClaude_validateNewDataTest extends TestStoreMongo
{
    @Test
    public void testValidateNewDataDoesNotThrow()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", "checksum123");

        // validateNewData has no specific validation, should not throw
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataWithNullChecksum()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", null);

        // Should not throw even with null checksum
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataWithNullPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile(null, "checksum123");

        // Should not throw even with null path (no validation logic)
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataWithBothFieldsNull()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile(null, null);

        // Should not throw even with all null fields
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataWithEmptyStrings()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("", "");

        // Should not throw with empty strings
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataWithSpecialCharacters()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile(
            "test/path-with_special@chars#2.0.jar",
            "checksum!@#$%^&*()"
        );

        // Should not throw with special characters
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataCalledDuringInsert()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/insert/path.jar", "checksum123");

        // validateNewData is called internally during insert, should not throw
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.insert(artifactFile));

        // Verify the artifact was inserted successfully
        Assertions.assertEquals(1, artifactsFilesMongo.getAllStoredEntities().size());
    }

    @Test
    public void testValidateNewDataCalledDuringCreateOrUpdate()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/create/path.jar", "checksum123");

        // validateNewData is called internally during createOrUpdate, should not throw
        ArtifactFile result = Assertions.assertDoesNotThrow(
            () -> artifactsFilesMongo.createOrUpdate(artifactFile)
        );

        // Verify the operation succeeded
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test/create/path.jar", result.getPath());
    }

    @Test
    public void testValidateNewDataWithVeryLongPath()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Create a very long path
        StringBuilder longPath = new StringBuilder("test/");
        for (int i = 0; i < 100; i++) {
            longPath.append("very/long/nested/directory/");
        }
        longPath.append("artifact.jar");

        ArtifactFile artifactFile = new ArtifactFile(longPath.toString(), "checksum123");

        // Should not throw even with very long path
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataWithVeryLongChecksum()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);

        // Create a very long checksum
        StringBuilder longChecksum = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longChecksum.append("a1b2c3d4");
        }

        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", longChecksum.toString());

        // Should not throw even with very long checksum
        Assertions.assertDoesNotThrow(() -> artifactsFilesMongo.validateNewData(artifactFile));
    }

    @Test
    public void testValidateNewDataMultipleTimes()
    {
        ArtifactsFilesMongo artifactsFilesMongo = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", "checksum123");

        // Should be safe to call multiple times
        Assertions.assertDoesNotThrow(() -> {
            artifactsFilesMongo.validateNewData(artifactFile);
            artifactsFilesMongo.validateNewData(artifactFile);
            artifactsFilesMongo.validateNewData(artifactFile);
        });
    }

    @Test
    public void testValidateNewDataWithDifferentInstances()
    {
        ArtifactsFilesMongo instance1 = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactsFilesMongo instance2 = new ArtifactsFilesMongo(this.mongoProvider);

        ArtifactFile artifactFile = new ArtifactFile("test/path.jar", "checksum123");

        // Should work with different instances
        Assertions.assertDoesNotThrow(() -> {
            instance1.validateNewData(artifactFile);
            instance2.validateNewData(artifactFile);
        });
    }
}

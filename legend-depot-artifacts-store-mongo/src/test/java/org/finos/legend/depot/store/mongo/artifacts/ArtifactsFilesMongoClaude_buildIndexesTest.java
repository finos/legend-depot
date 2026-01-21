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

import com.mongodb.client.model.IndexModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ArtifactsFilesMongoClaude_buildIndexesTest
{
    @Test
    public void testBuildIndexesReturnsNonNullList()
    {
        List<IndexModel> indexes = ArtifactsFilesMongo.buildIndexes();

        Assertions.assertNotNull(indexes);
    }

    @Test
    public void testBuildIndexesReturnsCorrectNumberOfIndexes()
    {
        List<IndexModel> indexes = ArtifactsFilesMongo.buildIndexes();

        // The method returns Arrays.asList(buildIndex("path", true, PATH))
        // which creates a single index
        Assertions.assertEquals(1, indexes.size());
    }

    @Test
    public void testBuildIndexesContainsPathIndex()
    {
        List<IndexModel> indexes = ArtifactsFilesMongo.buildIndexes();

        // Verify that the index has the name "path"
        IndexModel index = indexes.get(0);
        Assertions.assertNotNull(index);
        Assertions.assertNotNull(index.getOptions());
        Assertions.assertEquals("path", index.getOptions().getName());
    }

    @Test
    public void testBuildIndexesPathIndexIsUnique()
    {
        List<IndexModel> indexes = ArtifactsFilesMongo.buildIndexes();

        // Verify that the path index is marked as unique
        IndexModel index = indexes.get(0);
        Assertions.assertTrue(index.getOptions().isUnique());
    }

    @Test
    public void testBuildIndexesReturnsImmutableList()
    {
        List<IndexModel> indexes = ArtifactsFilesMongo.buildIndexes();

        // Arrays.asList returns a fixed-size list
        // Attempting to add should throw UnsupportedOperationException
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            indexes.add(null);
        });
    }

    @Test
    public void testBuildIndexesConsistentResults()
    {
        // Test that calling buildIndexes multiple times returns consistent results
        List<IndexModel> indexes1 = ArtifactsFilesMongo.buildIndexes();
        List<IndexModel> indexes2 = ArtifactsFilesMongo.buildIndexes();

        Assertions.assertEquals(indexes1.size(), indexes2.size());
        Assertions.assertEquals(
            indexes1.get(0).getOptions().getName(),
            indexes2.get(0).getOptions().getName()
        );
        Assertions.assertEquals(
            indexes1.get(0).getOptions().isUnique(),
            indexes2.get(0).getOptions().isUnique()
        );
    }

    @Test
    public void testBuildIndexesHasKeys()
    {
        List<IndexModel> indexes = ArtifactsFilesMongo.buildIndexes();

        // Verify that the index has keys defined
        IndexModel index = indexes.get(0);
        Assertions.assertNotNull(index.getKeys());

        // The keys should be an ascending index on the "path" field
        String keysString = index.getKeys().toString();
        Assertions.assertTrue(keysString.contains("path"));
    }
}

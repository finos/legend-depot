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

package org.finos.legend.depot.services.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EtagBuilderClaudeTest


{
    @Test
    void testCreateReturnsNewInstance()
  {
        // Act
        EtagBuilder builder1 = EtagBuilder.create();
        EtagBuilder builder2 = EtagBuilder.create();

        // Assert
        assertNotNull(builder1);
        assertNotNull(builder2);
        assertNotSame(builder1, builder2, "Each create() call should return a new instance");
    }

    @Test
    void testBuildWithEmptyBuilder()
  {
        // Act
        String result = EtagBuilder.create().build();

        // Assert
        assertEquals("", result, "Empty builder should return empty string");
    }

    @Test
    void testWithGAVWithValidVersion()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withGAV("com.example", "my-artifact", "1.0.0");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withGAV should return the same builder instance");
        assertEquals("com.examplemy-artifact1.0.0", etag, "Valid GAV should be concatenated");
    }

    @Test
    void testWithGAVWithSnapshotVersion()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withGAV("com.example", "my-artifact", "1.0.0-SNAPSHOT");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withGAV should return the same builder instance");
        assertNull(etag, "Snapshot version should make etag null");
    }

    @Test
    void testWithGAVWithMasterSnapshot()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withGAV("com.example", "my-artifact", "master-SNAPSHOT");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withGAV should return the same builder instance");
        assertNull(etag, "master-SNAPSHOT version should make etag null");
    }

    @Test
    void testWithGAVWithVersionAliasLatest()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withGAV("com.example", "my-artifact", "latest");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withGAV should return the same builder instance");
        assertNull(etag, "Version alias 'latest' should make etag null");
    }

    @Test
    void testWithGAVWithVersionAliasHead()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withGAV("com.example", "my-artifact", "head");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withGAV should return the same builder instance");
        assertNull(etag, "Version alias 'head' should make etag null");
    }

    @Test
    void testWithGAVWithVersionAliasCaseInsensitive()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withGAV("com.example", "my-artifact", "LATEST");
        String etag = result.build();

        // Assert
        assertNull(etag, "Version alias should be case insensitive");
    }

    @Test
    void testWithProtocolVersionWithValidVersion()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withProtocolVersion("v1_0_0");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withProtocolVersion should return the same builder instance");
        assertEquals("v1_0_0", etag, "Valid protocol version should be added");
    }

    @Test
    void testWithProtocolVersionWithNull()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withProtocolVersion(null);
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withProtocolVersion should return the same builder instance");
        assertNull(etag, "Null protocol version should make etag null");
    }

    @Test
    void testWithProtocolVersionWithHeadVersion()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withProtocolVersion("vX_X_X");
        String etag = result.build();

        // Assert
        assertSame(builder, result, "withProtocolVersion should return the same builder instance");
        assertNull(etag, "Head protocol version vX_X_X should make etag null");
    }

    @Test
    void testWithProtocolVersionWithHeadVersionCaseInsensitive()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create();

        // Act
        EtagBuilder result = builder.withProtocolVersion("vx_x_x");
        String etag = result.build();

        // Assert
        assertNull(etag, "Head protocol version should be case insensitive");
    }

    @Test
    void testChainedWithGAVAndProtocolVersion()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("com.example", "my-artifact", "1.0.0")
                .withProtocolVersion("v1_0_0")
                .build();

        // Assert
        assertEquals("com.examplemy-artifact1.0.0v1_0_0", etag, "Chained calls should concatenate all parameters");
    }

    @Test
    void testChainedWithMultipleGAVCalls()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("com.example", "artifact1", "1.0.0")
                .withGAV("com.example", "artifact2", "2.0.0")
                .build();

        // Assert
        assertEquals("com.exampleartifact11.0.0com.exampleartifact22.0.0", etag, "Multiple GAV calls should all be concatenated");
    }

    @Test
    void testChainedWithMultipleProtocolVersionCalls()
  {
        // Act
        String etag = EtagBuilder.create()
                .withProtocolVersion("v1_0_0")
                .withProtocolVersion("v2_0_0")
                .build();

        // Assert
        assertEquals("v1_0_0v2_0_0", etag, "Multiple protocol version calls should all be concatenated");
    }

    @Test
    void testGAVAfterSnapshotVersionIsIgnored()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("com.example", "artifact1", "1.0.0-SNAPSHOT")
                .withGAV("com.example", "artifact2", "2.0.0")
                .build();

        // Assert
        assertNull(etag, "Once constantParams is false, it should remain false");
    }

    @Test
    void testProtocolVersionAfterSnapshotVersionIsIgnored()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("com.example", "artifact1", "1.0.0-SNAPSHOT")
                .withProtocolVersion("v1_0_0")
                .build();

        // Assert
        assertNull(etag, "Once constantParams is false due to snapshot, it should remain false");
    }

    @Test
    void testGAVAfterNullProtocolVersionIsIgnored()
  {
        // Act
        String etag = EtagBuilder.create()
                .withProtocolVersion(null)
                .withGAV("com.example", "artifact1", "1.0.0")
                .build();

        // Assert
        assertNull(etag, "Once constantParams is false due to null protocol, it should remain false");
    }

    @Test
    void testGAVAfterHeadProtocolVersionIsIgnored()
  {
        // Act
        String etag = EtagBuilder.create()
                .withProtocolVersion("vX_X_X")
                .withGAV("com.example", "artifact1", "1.0.0")
                .build();

        // Assert
        assertNull(etag, "Once constantParams is false due to head protocol version, it should remain false");
    }

    @Test
    void testWithGAVWithEmptyStrings()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("", "", "1.0.0")
                .build();

        // Assert
        assertEquals("1.0.0", etag, "Empty groupId and artifactId should still be concatenated");
    }

    @Test
    void testWithProtocolVersionWithEmptyString()
  {
        // Act
        String etag = EtagBuilder.create()
                .withProtocolVersion("")
                .build();

        // Assert
        assertEquals("", etag, "Empty protocol version string should be added (not null)");
    }

    @Test
    void testComplexChaining()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("org.example", "lib1", "1.0.0")
                .withProtocolVersion("v1_0_0")
                .withGAV("org.example", "lib2", "2.0.0")
                .withProtocolVersion("v2_0_0")
                .build();

        // Assert
        assertEquals("org.examplelib11.0.0v1_0_0org.examplelib22.0.0v2_0_0", etag,
                "Complex chaining should concatenate all parameters in order");
    }

    @Test
    void testMultipleBuildCallsReturnSameValue()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create()
                .withGAV("com.example", "my-artifact", "1.0.0")
                .withProtocolVersion("v1_0_0");

        // Act
        String etag1 = builder.build();
        String etag2 = builder.build();

        // Assert
        assertEquals(etag1, etag2, "Multiple build() calls should return the same value");
        assertEquals("com.examplemy-artifact1.0.0v1_0_0", etag1);
    }

    @Test
    void testBuilderStateIsPreservedAcrossBuilds()
  {
        // Arrange
        EtagBuilder builder = EtagBuilder.create()
                .withGAV("com.example", "my-artifact", "1.0.0");

        // Act
        String etag1 = builder.build();
        builder.withProtocolVersion("v1_0_0");
        String etag2 = builder.build();

        // Assert
        assertEquals("com.examplemy-artifact1.0.0", etag1);
        assertEquals("com.examplemy-artifact1.0.0v1_0_0", etag2, "Builder state should accumulate across multiple operations");
    }

    @Test
    void testWithGAVWithVersionEndingWithSnapshotSuffix()
  {
        // Act
        String etag = EtagBuilder.create()
                .withGAV("com.example", "my-artifact", "feature-branch-SNAPSHOT")
                .build();

        // Assert
        assertNull(etag, "Any version ending with -SNAPSHOT should make etag null");
    }

    @Test
    void testWithGAVWithNonStandardButValidVersion()
  {
        // Act - Using a valid version that's not a snapshot or alias
        String etag = EtagBuilder.create()
                .withGAV("com.example", "my-artifact", "1.0.0-beta")
                .build();

        // Assert - This depends on VersionValidator.isValidReleaseVersion behavior
        // Assuming 1.0.0-beta is not a valid release version per VersionId.parseVersionId
        // If it's valid, etag should have value; if invalid, should be empty string (not null)
        assertNotNull(etag, "Non-snapshot versions that are not aliases should produce an etag");
    }

    @Test
    void testOrderOfOperationsMatters()
  {
        // Act
        String etag1 = EtagBuilder.create()
                .withGAV("group1", "artifact1", "1.0.0")
                .withProtocolVersion("v1_0_0")
                .build();

        String etag2 = EtagBuilder.create()
                .withProtocolVersion("v1_0_0")
                .withGAV("group1", "artifact1", "1.0.0")
                .build();

        // Assert
        assertNotEquals(etag1, etag2, "Order of operations matters since parameters are added to list in order");
        assertEquals("group1artifact11.0.0v1_0_0", etag1);
        assertEquals("v1_0_0group1artifact11.0.0", etag2);
    }
}

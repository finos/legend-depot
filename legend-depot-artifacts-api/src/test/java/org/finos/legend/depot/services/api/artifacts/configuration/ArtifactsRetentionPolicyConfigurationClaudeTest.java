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

package org.finos.legend.depot.services.api.artifacts.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtifactsRetentionPolicyConfigurationClaudeTest


{
    private static final int DEFAULT_TTL_FOR_SNAPSHOTS = 30;
    private static final int DEFAULT_TTL_FOR_VERSIONS = 365;
    private static final int DEFAULT_MAX_SNAPSHOTS_ALLOWED = 5;

    // Constructor tests

    @Test
    @DisplayName("Constructor with all null parameters should use default values")
    void testConstructorWithAllNullParameters()
  {
        // Arrange & Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, null, null);

        // Assert
        assertEquals(DEFAULT_MAX_SNAPSHOTS_ALLOWED, config.getMaximumSnapshotsAllowed());
        assertEquals(DEFAULT_TTL_FOR_VERSIONS, config.getTtlForVersions());
        assertEquals(DEFAULT_TTL_FOR_SNAPSHOTS, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with all parameters specified should use provided values")
    void testConstructorWithAllParametersSpecified()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = 10;
        Integer ttlForVersionsInDays = 180;
        Integer ttlForSnapshotsInDays = 15;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(10, config.getMaximumSnapshotsAllowed());
        assertEquals(180, config.getTtlForVersions());
        assertEquals(15, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with only maximumSnapshotsAllowed specified should use default for others")
    void testConstructorWithOnlyMaximumSnapshotsAllowedSpecified()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = 20;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, null, null);

        // Assert
        assertEquals(20, config.getMaximumSnapshotsAllowed());
        assertEquals(DEFAULT_TTL_FOR_VERSIONS, config.getTtlForVersions());
        assertEquals(DEFAULT_TTL_FOR_SNAPSHOTS, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with only ttlForVersionsInDays specified should use default for others")
    void testConstructorWithOnlyTtlForVersionsInDaysSpecified()
  {
        // Arrange
        Integer ttlForVersionsInDays = 90;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, ttlForVersionsInDays, null);

        // Assert
        assertEquals(DEFAULT_MAX_SNAPSHOTS_ALLOWED, config.getMaximumSnapshotsAllowed());
        assertEquals(90, config.getTtlForVersions());
        assertEquals(DEFAULT_TTL_FOR_SNAPSHOTS, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with only ttlForSnapshotsInDays specified should use default for others")
    void testConstructorWithOnlyTtlForSnapshotsInDaysSpecified()
  {
        // Arrange
        Integer ttlForSnapshotsInDays = 60;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, null, ttlForSnapshotsInDays);

        // Assert
        assertEquals(DEFAULT_MAX_SNAPSHOTS_ALLOWED, config.getMaximumSnapshotsAllowed());
        assertEquals(DEFAULT_TTL_FOR_VERSIONS, config.getTtlForVersions());
        assertEquals(60, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with maximumSnapshotsAllowed and ttlForVersionsInDays specified")
    void testConstructorWithMaximumSnapshotsAllowedAndTtlForVersionsInDaysSpecified()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = 8;
        Integer ttlForVersionsInDays = 200;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, null);

        // Assert
        assertEquals(8, config.getMaximumSnapshotsAllowed());
        assertEquals(200, config.getTtlForVersions());
        assertEquals(DEFAULT_TTL_FOR_SNAPSHOTS, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with maximumSnapshotsAllowed and ttlForSnapshotsInDays specified")
    void testConstructorWithMaximumSnapshotsAllowedAndTtlForSnapshotsInDaysSpecified()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = 12;
        Integer ttlForSnapshotsInDays = 45;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, null, ttlForSnapshotsInDays);

        // Assert
        assertEquals(12, config.getMaximumSnapshotsAllowed());
        assertEquals(DEFAULT_TTL_FOR_VERSIONS, config.getTtlForVersions());
        assertEquals(45, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with ttlForVersionsInDays and ttlForSnapshotsInDays specified")
    void testConstructorWithTtlForVersionsInDaysAndTtlForSnapshotsInDaysSpecified()
  {
        // Arrange
        Integer ttlForVersionsInDays = 100;
        Integer ttlForSnapshotsInDays = 20;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(DEFAULT_MAX_SNAPSHOTS_ALLOWED, config.getMaximumSnapshotsAllowed());
        assertEquals(100, config.getTtlForVersions());
        assertEquals(20, config.getTtlForSnapshots());
    }

    // Edge case tests

    @Test
    @DisplayName("Constructor with zero values should accept zero")
    void testConstructorWithZeroValues()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = 0;
        Integer ttlForVersionsInDays = 0;
        Integer ttlForSnapshotsInDays = 0;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(0, config.getMaximumSnapshotsAllowed());
        assertEquals(0, config.getTtlForVersions());
        assertEquals(0, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with negative values should accept negative")
    void testConstructorWithNegativeValues()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = -5;
        Integer ttlForVersionsInDays = -100;
        Integer ttlForSnapshotsInDays = -10;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(-5, config.getMaximumSnapshotsAllowed());
        assertEquals(-100, config.getTtlForVersions());
        assertEquals(-10, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with very large values should accept large values")
    void testConstructorWithLargeValues()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = Integer.MAX_VALUE;
        Integer ttlForVersionsInDays = Integer.MAX_VALUE;
        Integer ttlForSnapshotsInDays = Integer.MAX_VALUE;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(Integer.MAX_VALUE, config.getMaximumSnapshotsAllowed());
        assertEquals(Integer.MAX_VALUE, config.getTtlForVersions());
        assertEquals(Integer.MAX_VALUE, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with very small values should accept small values")
    void testConstructorWithVerySmallValues()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = Integer.MIN_VALUE;
        Integer ttlForVersionsInDays = Integer.MIN_VALUE;
        Integer ttlForSnapshotsInDays = Integer.MIN_VALUE;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(Integer.MIN_VALUE, config.getMaximumSnapshotsAllowed());
        assertEquals(Integer.MIN_VALUE, config.getTtlForVersions());
        assertEquals(Integer.MIN_VALUE, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Constructor with value 1 for all parameters")
    void testConstructorWithValueOne()
  {
        // Arrange
        Integer maximumSnapshotsAllowed = 1;
        Integer ttlForVersionsInDays = 1;
        Integer ttlForSnapshotsInDays = 1;

        // Act
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(maximumSnapshotsAllowed, ttlForVersionsInDays, ttlForSnapshotsInDays);

        // Assert
        assertEquals(1, config.getMaximumSnapshotsAllowed());
        assertEquals(1, config.getTtlForVersions());
        assertEquals(1, config.getTtlForSnapshots());
    }

    // Getter method tests

    @Test
    @DisplayName("getMaximumSnapshotsAllowed should return correct value")
    void testGetMaximumSnapshotsAllowed()
  {
        // Arrange
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(15, null, null);

        // Act
        int result = config.getMaximumSnapshotsAllowed();

        // Assert
        assertEquals(15, result);
    }

    @Test
    @DisplayName("getTtlForVersions should return correct value")
    void testGetTtlForVersions()
  {
        // Arrange
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, 500, null);

        // Act
        int result = config.getTtlForVersions();

        // Assert
        assertEquals(500, result);
    }

    @Test
    @DisplayName("getTtlForSnapshots should return correct value")
    void testGetTtlForSnapshots()
  {
        // Arrange
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, null, 75);

        // Act
        int result = config.getTtlForSnapshots();

        // Assert
        assertEquals(75, result);
    }

    @Test
    @DisplayName("Getters should return default values when constructor parameters are null")
    void testGettersWithDefaultValues()
  {
        // Arrange
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(null, null, null);

        // Act & Assert
        assertEquals(DEFAULT_MAX_SNAPSHOTS_ALLOWED, config.getMaximumSnapshotsAllowed());
        assertEquals(DEFAULT_TTL_FOR_VERSIONS, config.getTtlForVersions());
        assertEquals(DEFAULT_TTL_FOR_SNAPSHOTS, config.getTtlForSnapshots());
    }

    @Test
    @DisplayName("Getters should consistently return same values")
    void testGettersConsistency()
  {
        // Arrange
        ArtifactsRetentionPolicyConfiguration config =
                new ArtifactsRetentionPolicyConfiguration(25, 400, 50);

        // Act - call getters multiple times
        int maxSnapshots1 = config.getMaximumSnapshotsAllowed();
        int maxSnapshots2 = config.getMaximumSnapshotsAllowed();
        int ttlVersions1 = config.getTtlForVersions();
        int ttlVersions2 = config.getTtlForVersions();
        int ttlSnapshots1 = config.getTtlForSnapshots();
        int ttlSnapshots2 = config.getTtlForSnapshots();

        // Assert - should return same values
        assertEquals(maxSnapshots1, maxSnapshots2);
        assertEquals(ttlVersions1, ttlVersions2);
        assertEquals(ttlSnapshots1, ttlSnapshots2);
        assertEquals(25, maxSnapshots1);
        assertEquals(400, ttlVersions1);
        assertEquals(50, ttlSnapshots1);
    }
}

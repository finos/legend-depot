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

package org.finos.legend.depot.domain.notifications;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for MetadataNotificationStatus enum.
 * Tests the enum's values() and valueOf() methods for completeness and correctness.
 */
class MetadataNotificationStatusClaudeTest 

{

    // ========== values() Method Tests ==========

    @Test
    @DisplayName("values() returns array containing all enum constants")
    void testValuesReturnsAllConstants()
  {
        MetadataNotificationStatus[] values = MetadataNotificationStatus.values();

        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals(MetadataNotificationStatus.SUCCESS, values[0]);
        assertEquals(MetadataNotificationStatus.FAILED, values[1]);
    }

    @Test
    @DisplayName("values() returns new array instance on each call")
    void testValuesReturnsNewArrayInstance()
  {
        MetadataNotificationStatus[] values1 = MetadataNotificationStatus.values();
        MetadataNotificationStatus[] values2 = MetadataNotificationStatus.values();

        // Arrays should contain same elements but be different instances
        assertArrayEquals(values1, values2);
        // Note: We can't use assertNotSame for arrays in a meaningful way,
        // but we can verify that modifying one doesn't affect the other
        values1[0] = null;
        assertEquals(MetadataNotificationStatus.SUCCESS, values2[0]);
    }

    @Test
    @DisplayName("values() returns constants in declaration order")
    void testValuesReturnsInDeclarationOrder()
  {
        MetadataNotificationStatus[] values = MetadataNotificationStatus.values();

        // SUCCESS is declared first, FAILED second
        assertEquals(MetadataNotificationStatus.SUCCESS, values[0]);
        assertEquals(MetadataNotificationStatus.FAILED, values[1]);
    }

    @Test
    @DisplayName("values() array can be iterated")
    void testValuesArrayCanBeIterated()
  {
        int count = 0;
        for (MetadataNotificationStatus status : MetadataNotificationStatus.values()) {
            assertNotNull(status);
            count++;
        }
        assertEquals(2, count);
    }

    // ========== valueOf(String) Method Tests ==========

    @Test
    @DisplayName("valueOf() returns SUCCESS for 'SUCCESS' string")
    void testValueOfSuccess()
  {
        MetadataNotificationStatus status = MetadataNotificationStatus.valueOf("SUCCESS");

        assertNotNull(status);
        assertEquals(MetadataNotificationStatus.SUCCESS, status);
        assertSame(MetadataNotificationStatus.SUCCESS, status);
    }

    @Test
    @DisplayName("valueOf() returns FAILED for 'FAILED' string")
    void testValueOfFailed()
  {
        MetadataNotificationStatus status = MetadataNotificationStatus.valueOf("FAILED");

        assertNotNull(status);
        assertEquals(MetadataNotificationStatus.FAILED, status);
        assertSame(MetadataNotificationStatus.FAILED, status);
    }

    @Test
    @DisplayName("valueOf() returns same instance on multiple calls")
    void testValueOfReturnsSameInstance()
  {
        MetadataNotificationStatus status1 = MetadataNotificationStatus.valueOf("SUCCESS");
        MetadataNotificationStatus status2 = MetadataNotificationStatus.valueOf("SUCCESS");

        assertSame(status1, status2);
    }

    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for invalid name")
    void testValueOfThrowsForInvalidName()
  {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf("INVALID")
        );

        assertNotNull(exception);
    }

    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for null")
    void testValueOfThrowsForNull()
  {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> MetadataNotificationStatus.valueOf(null)
        );

        assertNotNull(exception);
    }

    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for empty string")
    void testValueOfThrowsForEmptyString()
  {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf("")
        );

        assertNotNull(exception);
    }

    @Test
    @DisplayName("valueOf() is case-sensitive")
    void testValueOfIsCaseSensitive()
  {
        // Lowercase should fail
        assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf("success")
        );

        // Mixed case should fail
        assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf("Success")
        );

        // Only exact match should work
        assertEquals(MetadataNotificationStatus.SUCCESS,
                     MetadataNotificationStatus.valueOf("SUCCESS"));
    }

    @Test
    @DisplayName("valueOf() throws for whitespace variations")
    void testValueOfThrowsForWhitespace()
  {
        assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf(" SUCCESS")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf("SUCCESS ")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> MetadataNotificationStatus.valueOf(" SUCCESS ")
        );
    }

    // ========== Enum Behavior Tests ==========

    @Test
    @DisplayName("Enum constants have correct names")
    void testEnumConstantNames()
  {
        assertEquals("SUCCESS", MetadataNotificationStatus.SUCCESS.name());
        assertEquals("FAILED", MetadataNotificationStatus.FAILED.name());
    }

    @Test
    @DisplayName("Enum constants have correct ordinals")
    void testEnumConstantOrdinals()
  {
        assertEquals(0, MetadataNotificationStatus.SUCCESS.ordinal());
        assertEquals(1, MetadataNotificationStatus.FAILED.ordinal());
    }

    @Test
    @DisplayName("Enum constants can be compared")
    void testEnumComparison()
  {
        // SUCCESS comes before FAILED in declaration order
        assertTrue(MetadataNotificationStatus.SUCCESS.compareTo(MetadataNotificationStatus.FAILED) < 0);
        assertTrue(MetadataNotificationStatus.FAILED.compareTo(MetadataNotificationStatus.SUCCESS) > 0);
        assertEquals(0, MetadataNotificationStatus.SUCCESS.compareTo(MetadataNotificationStatus.SUCCESS));
    }

    @Test
    @DisplayName("Enum constants can be used in switch statements")
    void testEnumInSwitchStatement()
  {
        String result1 = getStatusMessage(MetadataNotificationStatus.SUCCESS);
        String result2 = getStatusMessage(MetadataNotificationStatus.FAILED);

        assertEquals("Operation succeeded", result1);
        assertEquals("Operation failed", result2);
    }

    @Test
    @DisplayName("Enum constants can be used in equality checks")
    void testEnumEquality()
  {
        MetadataNotificationStatus status1 = MetadataNotificationStatus.SUCCESS;
        MetadataNotificationStatus status2 = MetadataNotificationStatus.SUCCESS;
        MetadataNotificationStatus status3 = MetadataNotificationStatus.FAILED;

        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
        assertSame(status1, status2);
    }

    @Test
    @DisplayName("All enum values can be retrieved via valueOf")
    void testAllValuesAccessibleViaValueOf()
  {
        for (MetadataNotificationStatus status : MetadataNotificationStatus.values()) {
            MetadataNotificationStatus retrieved = MetadataNotificationStatus.valueOf(status.name());
            assertSame(status, retrieved);
        }
    }

    @Test
    @DisplayName("toString() returns name for enum constants")
    void testToString()
  {
        assertEquals("SUCCESS", MetadataNotificationStatus.SUCCESS.toString());
        assertEquals("FAILED", MetadataNotificationStatus.FAILED.toString());
    }

    // ========== Helper Methods ==========

    private String getStatusMessage(MetadataNotificationStatus status)
  {
        switch (status) {
            case SUCCESS:
                return "Operation succeeded";
            case FAILED:
                return "Operation failed";
            default:
                return "Unknown status";
        }
    }

    private void assertTrue(boolean condition)
  {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private void assertNotEquals(Object unexpected, Object actual)
  {
        if (unexpected == null ? actual == null : unexpected.equals(actual)) {
            throw new AssertionError("Values should not be equal");
        }
    }
}

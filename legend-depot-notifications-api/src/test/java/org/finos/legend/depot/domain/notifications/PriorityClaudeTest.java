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
 * Comprehensive tests for Priority enum.
 * Tests the enum's values() and valueOf() methods for completeness and correctness.
 */
class PriorityClaudeTest 

{

    // ========== values() Method Tests ==========

    @Test
    @DisplayName("values() returns array containing all enum constants")
    void testValuesReturnsAllConstants()
  {
        Priority[] values = Priority.values();

        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals(Priority.HIGH, values[0]);
        assertEquals(Priority.LOW, values[1]);
    }

    @Test
    @DisplayName("values() returns new array instance on each call")
    void testValuesReturnsNewArrayInstance()
  {
        Priority[] values1 = Priority.values();
        Priority[] values2 = Priority.values();

        // Arrays should contain same elements but be different instances
        assertArrayEquals(values1, values2);
        // Note: We can't use assertNotSame for arrays in a meaningful way,
        // but we can verify that modifying one doesn't affect the other
        values1[0] = null;
        assertEquals(Priority.HIGH, values2[0]);
    }

    @Test
    @DisplayName("values() returns constants in declaration order")
    void testValuesReturnsInDeclarationOrder()
  {
        Priority[] values = Priority.values();

        // HIGH is declared first, LOW second
        assertEquals(Priority.HIGH, values[0]);
        assertEquals(Priority.LOW, values[1]);
    }

    @Test
    @DisplayName("values() array can be iterated")
    void testValuesArrayCanBeIterated()
  {
        int count = 0;
        for (Priority priority : Priority.values()) {
            assertNotNull(priority);
            count++;
        }
        assertEquals(2, count);
    }

    // ========== valueOf(String) Method Tests ==========

    @Test
    @DisplayName("valueOf() returns HIGH for 'HIGH' string")
    void testValueOfHigh()
  {
        Priority priority = Priority.valueOf("HIGH");

        assertNotNull(priority);
        assertEquals(Priority.HIGH, priority);
        assertSame(Priority.HIGH, priority);
    }

    @Test
    @DisplayName("valueOf() returns LOW for 'LOW' string")
    void testValueOfLow()
  {
        Priority priority = Priority.valueOf("LOW");

        assertNotNull(priority);
        assertEquals(Priority.LOW, priority);
        assertSame(Priority.LOW, priority);
    }

    @Test
    @DisplayName("valueOf() returns same instance on multiple calls")
    void testValueOfReturnsSameInstance()
  {
        Priority priority1 = Priority.valueOf("HIGH");
        Priority priority2 = Priority.valueOf("HIGH");

        assertSame(priority1, priority2);
    }

    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for invalid name")
    void testValueOfThrowsForInvalidName()
  {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Priority.valueOf("INVALID")
        );

        assertNotNull(exception);
    }

    @Test
    @DisplayName("valueOf() throws NullPointerException for null")
    void testValueOfThrowsForNull()
  {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Priority.valueOf(null)
        );

        assertNotNull(exception);
    }

    @Test
    @DisplayName("valueOf() throws IllegalArgumentException for empty string")
    void testValueOfThrowsForEmptyString()
  {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Priority.valueOf("")
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
            () -> Priority.valueOf("high")
        );

        // Mixed case should fail
        assertThrows(
            IllegalArgumentException.class,
            () -> Priority.valueOf("High")
        );

        // Only exact match should work
        assertEquals(Priority.HIGH, Priority.valueOf("HIGH"));
    }

    @Test
    @DisplayName("valueOf() throws for whitespace variations")
    void testValueOfThrowsForWhitespace()
  {
        assertThrows(
            IllegalArgumentException.class,
            () -> Priority.valueOf(" HIGH")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> Priority.valueOf("HIGH ")
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> Priority.valueOf(" HIGH ")
        );
    }

    // ========== Enum Behavior Tests ==========

    @Test
    @DisplayName("Enum constants have correct names")
    void testEnumConstantNames()
  {
        assertEquals("HIGH", Priority.HIGH.name());
        assertEquals("LOW", Priority.LOW.name());
    }

    @Test
    @DisplayName("Enum constants have correct ordinals")
    void testEnumConstantOrdinals()
  {
        assertEquals(0, Priority.HIGH.ordinal());
        assertEquals(1, Priority.LOW.ordinal());
    }

    @Test
    @DisplayName("Enum constants can be compared")
    void testEnumComparison()
  {
        // HIGH comes before LOW in declaration order
        assertTrue(Priority.HIGH.compareTo(Priority.LOW) < 0);
        assertTrue(Priority.LOW.compareTo(Priority.HIGH) > 0);
        assertEquals(0, Priority.HIGH.compareTo(Priority.HIGH));
    }

    @Test
    @DisplayName("Enum constants can be used in switch statements")
    void testEnumInSwitchStatement()
  {
        String result1 = getPriorityMessage(Priority.HIGH);
        String result2 = getPriorityMessage(Priority.LOW);

        assertEquals("High priority", result1);
        assertEquals("Low priority", result2);
    }

    @Test
    @DisplayName("Enum constants can be used in equality checks")
    void testEnumEquality()
  {
        Priority priority1 = Priority.HIGH;
        Priority priority2 = Priority.HIGH;
        Priority priority3 = Priority.LOW;

        assertEquals(priority1, priority2);
        assertNotEquals(priority1, priority3);
        assertSame(priority1, priority2);
    }

    @Test
    @DisplayName("All enum values can be retrieved via valueOf")
    void testAllValuesAccessibleViaValueOf()
  {
        for (Priority priority : Priority.values()) {
            Priority retrieved = Priority.valueOf(priority.name());
            assertSame(priority, retrieved);
        }
    }

    @Test
    @DisplayName("toString() returns name for enum constants")
    void testToString()
  {
        assertEquals("HIGH", Priority.HIGH.toString());
        assertEquals("LOW", Priority.LOW.toString());
    }

    // ========== Helper Methods ==========

    private String getPriorityMessage(Priority priority)
  {
        switch (priority) {
            case HIGH:
                return "High priority";
            case LOW:
                return "Low priority";
            default:
                return "Unknown priority";
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

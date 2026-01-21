package org.finos.legend.depot.domain.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests specifically targeting the static initializer (<clinit>) of Priority enum.
 * The static initializer creates the enum constants HIGH and LOW (lines 18, 20, 21).
 */
class PriorityClaude_clinitTest {

    @Test
    @DisplayName("Static initializer creates HIGH constant")
    void testStaticInitializerCreatesHighConstant() {
        // Accessing the HIGH constant triggers the static initializer if not already loaded
        Priority high = Priority.HIGH;

        assertNotNull(high);
        assertEquals("HIGH", high.name());
        assertEquals(0, high.ordinal());
    }

    @Test
    @DisplayName("Static initializer creates LOW constant")
    void testStaticInitializerCreatesLowConstant() {
        // Accessing the LOW constant triggers the static initializer if not already loaded
        Priority low = Priority.LOW;

        assertNotNull(low);
        assertEquals("LOW", low.name());
        assertEquals(1, low.ordinal());
    }

    @Test
    @DisplayName("Static initializer creates all enum constants accessible via values()")
    void testStaticInitializerCreatesAllConstants() {
        // Calling values() ensures all enum constants created by static initializer are accessible
        Priority[] allValues = Priority.values();

        assertNotNull(allValues);
        assertEquals(2, allValues.length);

        // Verify both constants exist and are properly initialized
        assertEquals(Priority.HIGH, allValues[0]);
        assertEquals(Priority.LOW, allValues[1]);
    }

    @Test
    @DisplayName("Static initializer allows valueOf to retrieve HIGH")
    void testStaticInitializerAllowsValueOfHigh() {
        // valueOf uses the enum constants created by the static initializer
        Priority high = Priority.valueOf("HIGH");

        assertNotNull(high);
        assertEquals(Priority.HIGH, high);
    }

    @Test
    @DisplayName("Static initializer allows valueOf to retrieve LOW")
    void testStaticInitializerAllowsValueOfLow() {
        // valueOf uses the enum constants created by the static initializer
        Priority low = Priority.valueOf("LOW");

        assertNotNull(low);
        assertEquals(Priority.LOW, low);
    }

    @Test
    @DisplayName("Both enum constants are singleton instances")
    void testEnumConstantsAreSingletons() {
        // The static initializer creates singleton instances
        Priority high1 = Priority.HIGH;
        Priority high2 = Priority.HIGH;
        Priority low1 = Priority.LOW;
        Priority low2 = Priority.LOW;

        // Verify they are the same instances (reference equality)
        assertEquals(high1, high2);
        assertEquals(low1, low2);
        assertSame(high1, high2);
        assertSame(low1, low2);
    }

    @Test
    @DisplayName("Enum constants initialized in correct declaration order")
    void testEnumConstantsInDeclarationOrder() {
        // The static initializer creates constants in declaration order
        // HIGH declared first (line 20), LOW second (line 21)
        assertEquals(0, Priority.HIGH.ordinal());
        assertEquals(1, Priority.LOW.ordinal());

        // Verify order in values() array
        Priority[] values = Priority.values();
        assertEquals(Priority.HIGH, values[0]);
        assertEquals(Priority.LOW, values[1]);
    }

    @Test
    @DisplayName("Direct reference to HIGH constant triggers initialization")
    void testDirectReferenceToHigh() {
        // Direct field access to HIGH (line 20)
        assertNotNull(Priority.HIGH);
    }

    @Test
    @DisplayName("Direct reference to LOW constant triggers initialization")
    void testDirectReferenceToLow() {
        // Direct field access to LOW (line 21)
        assertNotNull(Priority.LOW);
    }

    @Test
    @DisplayName("Enum class initialization provides type information")
    void testEnumClassInitialization() {
        // Accessing the class triggers static initialization (line 18)
        Class<Priority> enumClass = Priority.class;

        assertNotNull(enumClass);
        assertEquals("Priority", enumClass.getSimpleName());
        assertEquals(true, enumClass.isEnum());

        // Verify the enum has exactly 2 constants
        Priority[] constants = enumClass.getEnumConstants();
        assertNotNull(constants);
        assertEquals(2, constants.length);
    }

    // Helper method for assertSame
    private void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected same instance but were different");
        }
    }
}

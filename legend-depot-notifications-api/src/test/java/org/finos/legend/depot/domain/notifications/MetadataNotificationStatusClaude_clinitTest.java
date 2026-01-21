package org.finos.legend.depot.domain.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests specifically targeting the static initializer (<clinit>) of MetadataNotificationStatus enum.
 * The static initializer creates the enum constants SUCCESS and FAILED (lines 18, 20, 21).
 */
class MetadataNotificationStatusClaude_clinitTest {

    @Test
    @DisplayName("Static initializer creates SUCCESS constant")
    void testStaticInitializerCreatesSuccessConstant() {
        // Accessing the SUCCESS constant triggers the static initializer if not already loaded
        MetadataNotificationStatus success = MetadataNotificationStatus.SUCCESS;

        assertNotNull(success);
        assertEquals("SUCCESS", success.name());
        assertEquals(0, success.ordinal());
    }

    @Test
    @DisplayName("Static initializer creates FAILED constant")
    void testStaticInitializerCreatesFailedConstant() {
        // Accessing the FAILED constant triggers the static initializer if not already loaded
        MetadataNotificationStatus failed = MetadataNotificationStatus.FAILED;

        assertNotNull(failed);
        assertEquals("FAILED", failed.name());
        assertEquals(1, failed.ordinal());
    }

    @Test
    @DisplayName("Static initializer creates all enum constants accessible via values()")
    void testStaticInitializerCreatesAllConstants() {
        // Calling values() ensures all enum constants created by static initializer are accessible
        MetadataNotificationStatus[] allValues = MetadataNotificationStatus.values();

        assertNotNull(allValues);
        assertEquals(2, allValues.length);

        // Verify both constants exist and are properly initialized
        assertEquals(MetadataNotificationStatus.SUCCESS, allValues[0]);
        assertEquals(MetadataNotificationStatus.FAILED, allValues[1]);
    }

    @Test
    @DisplayName("Static initializer allows valueOf to retrieve SUCCESS")
    void testStaticInitializerAllowsValueOfSuccess() {
        // valueOf uses the enum constants created by the static initializer
        MetadataNotificationStatus success = MetadataNotificationStatus.valueOf("SUCCESS");

        assertNotNull(success);
        assertEquals(MetadataNotificationStatus.SUCCESS, success);
    }

    @Test
    @DisplayName("Static initializer allows valueOf to retrieve FAILED")
    void testStaticInitializerAllowsValueOfFailed() {
        // valueOf uses the enum constants created by the static initializer
        MetadataNotificationStatus failed = MetadataNotificationStatus.valueOf("FAILED");

        assertNotNull(failed);
        assertEquals(MetadataNotificationStatus.FAILED, failed);
    }

    @Test
    @DisplayName("Both enum constants are singleton instances")
    void testEnumConstantsAreSingletons() {
        // The static initializer creates singleton instances
        MetadataNotificationStatus success1 = MetadataNotificationStatus.SUCCESS;
        MetadataNotificationStatus success2 = MetadataNotificationStatus.SUCCESS;
        MetadataNotificationStatus failed1 = MetadataNotificationStatus.FAILED;
        MetadataNotificationStatus failed2 = MetadataNotificationStatus.FAILED;

        // Verify they are the same instances (reference equality)
        assertEquals(success1, success2);
        assertEquals(failed1, failed2);
        assertSame(success1, success2);
        assertSame(failed1, failed2);
    }

    @Test
    @DisplayName("Enum constants initialized in correct declaration order")
    void testEnumConstantsInDeclarationOrder() {
        // The static initializer creates constants in declaration order
        // SUCCESS declared first (line 20), FAILED second (line 21)
        assertEquals(0, MetadataNotificationStatus.SUCCESS.ordinal());
        assertEquals(1, MetadataNotificationStatus.FAILED.ordinal());

        // Verify order in values() array
        MetadataNotificationStatus[] values = MetadataNotificationStatus.values();
        assertEquals(MetadataNotificationStatus.SUCCESS, values[0]);
        assertEquals(MetadataNotificationStatus.FAILED, values[1]);
    }

    @Test
    @DisplayName("Direct reference to SUCCESS constant triggers initialization")
    void testDirectReferenceToSuccess() {
        // Direct field access to SUCCESS (line 20)
        assertNotNull(MetadataNotificationStatus.SUCCESS);
    }

    @Test
    @DisplayName("Direct reference to FAILED constant triggers initialization")
    void testDirectReferenceToFailed() {
        // Direct field access to FAILED (line 21)
        assertNotNull(MetadataNotificationStatus.FAILED);
    }

    @Test
    @DisplayName("Enum class initialization provides type information")
    void testEnumClassInitialization() {
        // Accessing the class triggers static initialization (line 18)
        Class<MetadataNotificationStatus> enumClass = MetadataNotificationStatus.class;

        assertNotNull(enumClass);
        assertEquals("MetadataNotificationStatus", enumClass.getSimpleName());
        assertEquals(true, enumClass.isEnum());

        // Verify the enum has exactly 2 constants
        MetadataNotificationStatus[] constants = enumClass.getEnumConstants();
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

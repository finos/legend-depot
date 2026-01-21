package org.finos.legend.depot.store.mongo.guice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class SchedulesStoreMongoModuleClaude_constructorTest {
    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link SchedulesStoreMongoModule#SchedulesStoreMongoModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void SchedulesStoreMongoModule.<init>()"})
    void testConstructor() {
        // Arrange and Act
        SchedulesStoreMongoModule actualSchedulesStoreMongoModule = new SchedulesStoreMongoModule();

        // Assert
        assertNotNull(actualSchedulesStoreMongoModule);
    }
}

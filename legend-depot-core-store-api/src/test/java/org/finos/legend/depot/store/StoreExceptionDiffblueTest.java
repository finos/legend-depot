package org.finos.legend.depot.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoreExceptionDiffblueTest {
  /**
   * Test {@link StoreException#StoreException(String)}.
   *
   * <p>Method under test: {@link StoreException#StoreException(String)}
   */
  @Test
  @DisplayName("Test new StoreException(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void StoreException.<init>(String)"})
  void testNewStoreException() {
    // Arrange and Act
    StoreException actualStoreException = new StoreException("An error occurred");

    // Assert
    assertEquals("An error occurred", actualStoreException.getMessage());
    assertNull(actualStoreException.getCause());
    assertEquals(0, actualStoreException.getSuppressed().length);
  }
}

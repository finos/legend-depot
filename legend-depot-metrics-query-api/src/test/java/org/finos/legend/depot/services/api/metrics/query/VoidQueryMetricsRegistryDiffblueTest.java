package org.finos.legend.depot.services.api.metrics.query;

import static org.junit.jupiter.api.Assertions.assertFalse;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VoidQueryMetricsRegistryDiffblueTest {
  /**
   * Test {@link VoidQueryMetricsRegistry#findFirst()}.
   *
   * <p>Method under test: {@link VoidQueryMetricsRegistry#findFirst()}
   */
  @Test
  @DisplayName("Test findFirst()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.util.Optional VoidQueryMetricsRegistry.findFirst()"})
  void testFindFirst() {
    // Arrange, Act and Assert
    assertFalse(new VoidQueryMetricsRegistry().findFirst().isPresent());
  }
}

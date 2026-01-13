package org.finos.legend.depot.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class DatesHandlerDiffblueTest {
  /**
   * Test {@link DatesHandler#toTime(LocalDateTime)}.
   *
   * <p>Method under test: {@link DatesHandler#toTime(LocalDateTime)}
   */
  @Test
  @DisplayName("Test toTime(LocalDateTime)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"long DatesHandler.toTime(LocalDateTime)"})
  void testToTime() {
    // Arrange, Act and Assert
    assertEquals(-3600000L, DatesHandler.toTime(LocalDate.of(1970, 1, 1).atStartOfDay()));
  }

  /**
   * Test {@link DatesHandler#toDate(Date)} with {@code Date}.
   *
   * <ul>
   *   <li>Then return toLocalTime toString is {@code 01:00}.
   * </ul>
   *
   * <p>Method under test: {@link DatesHandler#toDate(Date)}
   */
  @Test
  @DisplayName("Test toDate(Date) with 'Date'; then return toLocalTime toString is '01:00'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"LocalDateTime DatesHandler.toDate(Date)"})
  void testToDateWithDate_thenReturnToLocalTimeToStringIs0100() {
    // Arrange and Act
    LocalDateTime actualToDateResult =
        DatesHandler.toDate(
            Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

    // Assert
    assertEquals("01:00", actualToDateResult.toLocalTime().toString());
    assertEquals("1970-01-01", actualToDateResult.toLocalDate().toString());
  }

  /**
   * Test {@link DatesHandler#toDate(long)} with {@code long}.
   *
   * <p>Method under test: {@link DatesHandler#toDate(long)}
   */
  @Test
  @DisplayName("Test toDate(long) with 'long'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"LocalDateTime DatesHandler.toDate(long)"})
  void testToDateWithLong() {
    // Arrange and Act
    LocalDateTime actualToDateResult = DatesHandler.toDate(10L);

    // Assert
    assertEquals("01:00:00.010", actualToDateResult.toLocalTime().toString());
    assertEquals("1970-01-01", actualToDateResult.toLocalDate().toString());
  }

  /**
   * Test {@link DatesHandler#parseDate(String)}.
   *
   * <ul>
   *   <li>When {@code 20200301}.
   *   <li>Then return toLocalTime toString is {@code 06:36:40.301}.
   * </ul>
   *
   * <p>Method under test: {@link DatesHandler#parseDate(String)}
   */
  @Test
  @DisplayName(
      "Test parseDate(String); when '20200301'; then return toLocalTime toString is '06:36:40.301'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"LocalDateTime DatesHandler.parseDate(String)"})
  void testParseDate_when20200301_thenReturnToLocalTimeToStringIs063640301() {
    // Arrange and Act
    LocalDateTime actualParseDateResult = DatesHandler.parseDate("20200301");

    // Assert
    assertEquals("06:36:40.301", actualParseDateResult.toLocalTime().toString());
    assertEquals("1970-01-01", actualParseDateResult.toLocalDate().toString());
  }
}

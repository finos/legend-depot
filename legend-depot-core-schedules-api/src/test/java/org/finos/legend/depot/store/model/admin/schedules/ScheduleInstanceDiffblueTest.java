package org.finos.legend.depot.store.model.admin.schedules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ScheduleInstanceDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ScheduleInstance#ScheduleInstance()}
   *   <li>{@link ScheduleInstance#setExpires(Date)}
   *   <li>{@link ScheduleInstance#setId(String)}
   *   <li>{@link ScheduleInstance#setSchedule(String)}
   *   <li>{@link ScheduleInstance#getExpires()}
   *   <li>{@link ScheduleInstance#getId()}
   *   <li>{@link ScheduleInstance#getSchedule()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ScheduleInstance.<init>()",
    "void ScheduleInstance.<init>(String, Date)",
    "Date ScheduleInstance.getExpires()",
    "String ScheduleInstance.getId()",
    "String ScheduleInstance.getSchedule()",
    "void ScheduleInstance.setExpires(Date)",
    "void ScheduleInstance.setId(String)",
    "void ScheduleInstance.setSchedule(String)"
  })
  void testGettersAndSetters() {
    // Arrange and Act
    ScheduleInstance actualScheduleInstance = new ScheduleInstance();
    Date expires =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualScheduleInstance.setExpires(expires);
    actualScheduleInstance.setId("42");
    actualScheduleInstance.setSchedule("Schedule");
    Date actualExpires = actualScheduleInstance.getExpires();
    String actualId = actualScheduleInstance.getId();

    // Assert
    assertEquals("42", actualId);
    assertEquals("Schedule", actualScheduleInstance.getSchedule());
    assertSame(expires, actualExpires);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code Name}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ScheduleInstance#ScheduleInstance(String, Date)}
   *   <li>{@link ScheduleInstance#setExpires(Date)}
   *   <li>{@link ScheduleInstance#setId(String)}
   *   <li>{@link ScheduleInstance#setSchedule(String)}
   *   <li>{@link ScheduleInstance#getExpires()}
   *   <li>{@link ScheduleInstance#getId()}
   *   <li>{@link ScheduleInstance#getSchedule()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'Name'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ScheduleInstance.<init>()",
    "void ScheduleInstance.<init>(String, Date)",
    "Date ScheduleInstance.getExpires()",
    "String ScheduleInstance.getId()",
    "String ScheduleInstance.getSchedule()",
    "void ScheduleInstance.setExpires(Date)",
    "void ScheduleInstance.setId(String)",
    "void ScheduleInstance.setSchedule(String)"
  })
  void testGettersAndSetters_whenName() {
    // Arrange
    Date expires =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    ScheduleInstance actualScheduleInstance = new ScheduleInstance("Name", expires);
    Date expires2 =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualScheduleInstance.setExpires(expires2);
    actualScheduleInstance.setId("42");
    actualScheduleInstance.setSchedule("Schedule");
    Date actualExpires = actualScheduleInstance.getExpires();
    String actualId = actualScheduleInstance.getId();

    // Assert
    assertEquals("42", actualId);
    assertEquals("Schedule", actualScheduleInstance.getSchedule());
    assertSame(expires2, actualExpires);
  }
}

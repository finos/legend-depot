package org.finos.legend.depot.domain.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class MetadataNotificationDiffblueTest {
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link MetadataNotification#MetadataNotification()}
   *   <li>{@link MetadataNotification#setAttempt(int)}
   *   <li>{@link MetadataNotification#setCompleted(Date)}
   *   <li>{@link MetadataNotification#setCreated(Date)}
   *   <li>{@link MetadataNotification#setEventId(String)}
   *   <li>{@link MetadataNotification#setEventPriority(Priority)}
   *   <li>{@link MetadataNotification#setFullUpdate(boolean)}
   *   <li>{@link MetadataNotification#setId(String)}
   *   <li>{@link MetadataNotification#setMaxAttempts(int)}
   *   <li>{@link MetadataNotification#setParentEventId(String)}
   *   <li>{@link MetadataNotification#setProjectId(String)}
   *   <li>{@link MetadataNotification#setResponses(Map)}
   *   <li>{@link MetadataNotification#setTransitive(boolean)}
   *   <li>{@link MetadataNotification#setUpdated(Date)}
   *   <li>{@link MetadataNotification#increaseAttempts()}
   *   <li>{@link MetadataNotification#getAttempt()}
   *   <li>{@link MetadataNotification#getCompleted()}
   *   <li>{@link MetadataNotification#getCreated()}
   *   <li>{@link MetadataNotification#getEventId()}
   *   <li>{@link MetadataNotification#getEventPriority()}
   *   <li>{@link MetadataNotification#getId()}
   *   <li>{@link MetadataNotification#getMaxAttempts()}
   *   <li>{@link MetadataNotification#getParentEventId()}
   *   <li>{@link MetadataNotification#getProjectId()}
   *   <li>{@link MetadataNotification#getUpdated()}
   *   <li>{@link MetadataNotification#isFullUpdate()}
   *   <li>{@link MetadataNotification#isTransitive()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>()",
    "int MetadataNotification.getAttempt()",
    "Date MetadataNotification.getCompleted()",
    "Date MetadataNotification.getCreated()",
    "String MetadataNotification.getEventId()",
    "Priority MetadataNotification.getEventPriority()",
    "String MetadataNotification.getId()",
    "int MetadataNotification.getMaxAttempts()",
    "String MetadataNotification.getParentEventId()",
    "String MetadataNotification.getProjectId()",
    "Date MetadataNotification.getUpdated()",
    "MetadataNotification MetadataNotification.increaseAttempts()",
    "boolean MetadataNotification.isFullUpdate()",
    "boolean MetadataNotification.isTransitive()",
    "MetadataNotification MetadataNotification.setAttempt(int)",
    "void MetadataNotification.setCompleted(Date)",
    "void MetadataNotification.setCreated(Date)",
    "MetadataNotification MetadataNotification.setEventId(String)",
    "void MetadataNotification.setEventPriority(Priority)",
    "MetadataNotification MetadataNotification.setFullUpdate(boolean)",
    "void MetadataNotification.setId(String)",
    "void MetadataNotification.setMaxAttempts(int)",
    "void MetadataNotification.setParentEventId(String)",
    "MetadataNotification MetadataNotification.setProjectId(String)",
    "void MetadataNotification.setResponses(Map)",
    "void MetadataNotification.setTransitive(boolean)",
    "MetadataNotification MetadataNotification.setUpdated(Date)"
  })
  void testGettersAndSetters() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification = new MetadataNotification();
    MetadataNotification actualSetAttemptResult = actualMetadataNotification.setAttempt(1);
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualMetadataNotification.setCompleted(completed);
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualMetadataNotification.setCreated(created);
    MetadataNotification actualSetEventIdResult = actualMetadataNotification.setEventId("Event ID");
    actualMetadataNotification.setEventPriority(Priority.HIGH);
    MetadataNotification actualSetFullUpdateResult = actualMetadataNotification.setFullUpdate(true);
    actualMetadataNotification.setId("42");
    actualMetadataNotification.setMaxAttempts(3);
    actualMetadataNotification.setParentEventId("42");
    MetadataNotification actualSetProjectIdResult =
        actualMetadataNotification.setProjectId("myproject");
    HashMap<Integer, MetadataNotificationResponse> responses = new HashMap<>();
    actualMetadataNotification.setResponses(responses);
    actualMetadataNotification.setTransitive(true);
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    MetadataNotification actualSetUpdatedResult = actualMetadataNotification.setUpdated(updated);
    MetadataNotification actualIncreaseAttemptsResult =
        actualMetadataNotification.increaseAttempts();
    int actualAttempt = actualMetadataNotification.getAttempt();
    Date actualCompleted = actualMetadataNotification.getCompleted();
    Date actualCreated = actualMetadataNotification.getCreated();
    String actualEventId = actualMetadataNotification.getEventId();
    Priority actualEventPriority = actualMetadataNotification.getEventPriority();
    String actualId = actualMetadataNotification.getId();
    int actualMaxAttempts = actualMetadataNotification.getMaxAttempts();
    String actualParentEventId = actualMetadataNotification.getParentEventId();
    String actualProjectId = actualMetadataNotification.getProjectId();
    Date actualUpdated = actualMetadataNotification.getUpdated();
    boolean actualIsFullUpdateResult = actualMetadataNotification.isFullUpdate();
    boolean actualIsTransitiveResult = actualMetadataNotification.isTransitive();

    // Assert
    assertEquals("42", actualId);
    assertEquals("42", actualParentEventId);
    assertEquals("Event ID", actualEventId);
    assertEquals("myproject", actualProjectId);
    assertNull(actualMetadataNotification.getArtifactId());
    assertNull(actualMetadataNotification.getGroupId());
    assertNull(actualMetadataNotification.getVersionId());
    assertEquals(2, actualAttempt);
    assertEquals(3, actualMaxAttempts);
    assertEquals(Priority.HIGH, actualEventPriority);
    Map<Integer, MetadataNotificationResponse> responses2 =
        actualMetadataNotification.getResponses();
    assertTrue(responses2.isEmpty());
    assertTrue(actualIsFullUpdateResult);
    assertTrue(actualIsTransitiveResult);
    assertSame(responses, responses2);
    assertSame(actualMetadataNotification, actualIncreaseAttemptsResult);
    assertSame(actualMetadataNotification, actualSetAttemptResult);
    assertSame(actualMetadataNotification, actualSetEventIdResult);
    assertSame(actualMetadataNotification, actualSetFullUpdateResult);
    assertSame(actualMetadataNotification, actualSetProjectIdResult);
    assertSame(actualMetadataNotification, actualSetUpdatedResult);
    assertSame(completed, actualCompleted);
    assertSame(created, actualCreated);
    assertSame(updated, actualUpdated);
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String)}.
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String)}
   */
  @Test
  @DisplayName("Test new MetadataNotification(String, String, String, String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void MetadataNotification.<init>(String, String, String, String)"})
  void testNewMetadataNotification() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getParentEventId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.LOW, actualMetadataNotification.getEventPriority());
    assertFalse(actualMetadataNotification.isFullUpdate());
    assertFalse(actualMetadataNotification.isTransitive());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, String,
   * String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return Attempt is zero.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority); when 'null'; then return Attempt is zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)"
  })
  void testNewMetadataNotification_whenNull_thenReturnAttemptIsZero() {
    // Arrange
    HashMap<Integer, MetadataNotificationResponse> responses = new HashMap<>();
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject",
            "42",
            "42",
            "1.0.2",
            "42",
            "42",
            true,
            true,
            null,
            3,
            responses,
            created,
            updated,
            completed,
            Priority.HIGH);

    // Assert
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(3, actualMetadataNotification.getMaxAttempts());
    assertTrue(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, Boolean,
   * Boolean, String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return not FullUpdate.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, Boolean, Boolean, String)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, Boolean, Boolean, String); when 'null'; then return not FullUpdate")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, Boolean, Boolean, String)"
  })
  void testNewMetadataNotification_whenNull_thenReturnNotFullUpdate() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification("myproject", "42", "42", "42", null, true, "Parent Event");

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("Parent Event", actualMetadataNotification.getParentEventId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.LOW, actualMetadataNotification.getEventPriority());
    assertFalse(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, Boolean,
   * Boolean, String, Priority)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return not FullUpdate.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, Boolean, Boolean, String, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, Boolean, Boolean, String, Priority); when 'null'; then return not FullUpdate")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, Boolean, Boolean, String, Priority)"
  })
  void testNewMetadataNotification_whenNull_thenReturnNotFullUpdate2() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject", "42", "42", "42", null, true, "Parent Event", Priority.HIGH);

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("Parent Event", actualMetadataNotification.getParentEventId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.HIGH, actualMetadataNotification.getEventPriority());
    assertFalse(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, String,
   * String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return not FullUpdate.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority); when 'null'; then return not FullUpdate")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)"
  })
  void testNewMetadataNotification_whenNull_thenReturnNotFullUpdate3() {
    // Arrange
    HashMap<Integer, MetadataNotificationResponse> responses = new HashMap<>();
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject",
            "42",
            "42",
            "1.0.2",
            "42",
            "42",
            null,
            true,
            1,
            3,
            responses,
            created,
            updated,
            completed,
            Priority.HIGH);

    // Assert
    assertEquals(1, actualMetadataNotification.getAttempt());
    assertEquals(3, actualMetadataNotification.getMaxAttempts());
    assertFalse(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, Boolean,
   * Boolean, String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return not Transitive.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, Boolean, Boolean, String)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, Boolean, Boolean, String); when 'null'; then return not Transitive")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, Boolean, Boolean, String)"
  })
  void testNewMetadataNotification_whenNull_thenReturnNotTransitive() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification("myproject", "42", "42", "42", true, null, "Parent Event");

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("Parent Event", actualMetadataNotification.getParentEventId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.LOW, actualMetadataNotification.getEventPriority());
    assertFalse(actualMetadataNotification.isTransitive());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
    assertTrue(actualMetadataNotification.isFullUpdate());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, Boolean,
   * Boolean, String, Priority)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return not Transitive.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, Boolean, Boolean, String, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, Boolean, Boolean, String, Priority); when 'null'; then return not Transitive")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, Boolean, Boolean, String, Priority)"
  })
  void testNewMetadataNotification_whenNull_thenReturnNotTransitive2() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject", "42", "42", "42", true, null, "Parent Event", Priority.HIGH);

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("Parent Event", actualMetadataNotification.getParentEventId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.HIGH, actualMetadataNotification.getEventPriority());
    assertFalse(actualMetadataNotification.isTransitive());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
    assertTrue(actualMetadataNotification.isFullUpdate());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, String,
   * String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then return not Transitive.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority); when 'null'; then return not Transitive")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)"
  })
  void testNewMetadataNotification_whenNull_thenReturnNotTransitive3() {
    // Arrange
    HashMap<Integer, MetadataNotificationResponse> responses = new HashMap<>();
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject",
            "42",
            "42",
            "1.0.2",
            "42",
            "42",
            true,
            null,
            1,
            3,
            responses,
            created,
            updated,
            completed,
            Priority.HIGH);

    // Assert
    assertEquals(1, actualMetadataNotification.getAttempt());
    assertEquals(3, actualMetadataNotification.getMaxAttempts());
    assertFalse(actualMetadataNotification.isTransitive());
    assertTrue(actualMetadataNotification.isFullUpdate());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, String,
   * String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return Attempt is one.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority); when one; then return Attempt is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)"
  })
  void testNewMetadataNotification_whenOne_thenReturnAttemptIsOne() {
    // Arrange
    HashMap<Integer, MetadataNotificationResponse> responses = new HashMap<>();
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject",
            "42",
            "42",
            "1.0.2",
            "42",
            "42",
            true,
            true,
            1,
            3,
            responses,
            created,
            updated,
            completed,
            Priority.HIGH);

    // Assert
    assertEquals(1, actualMetadataNotification.getAttempt());
    assertEquals(3, actualMetadataNotification.getMaxAttempts());
    assertTrue(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, String,
   * String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return Attempt is one.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority); when one; then return Attempt is one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)"
  })
  void testNewMetadataNotification_whenOne_thenReturnAttemptIsOne2() {
    // Arrange
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject",
            "42",
            "42",
            "1.0.2",
            "42",
            "42",
            true,
            true,
            1,
            3,
            null,
            created,
            updated,
            completed,
            Priority.HIGH);

    // Assert
    assertEquals(1, actualMetadataNotification.getAttempt());
    assertEquals(3, actualMetadataNotification.getMaxAttempts());
    assertTrue(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, String,
   * String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return MaxAttempts is two.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority); when one; then return MaxAttempts is two")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, String, String, Boolean, Boolean, Integer, Integer, Map, Date, Date, Date, Priority)"
  })
  void testNewMetadataNotification_whenOne_thenReturnMaxAttemptsIsTwo() {
    // Arrange
    HashMap<Integer, MetadataNotificationResponse> responses = new HashMap<>();
    Date created =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date updated =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    Date completed =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject",
            "42",
            "42",
            "1.0.2",
            "42",
            "42",
            true,
            true,
            1,
            null,
            responses,
            created,
            updated,
            completed,
            Priority.HIGH);

    // Assert
    assertEquals(1, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertTrue(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, Boolean,
   * Boolean, String)}.
   *
   * <ul>
   *   <li>When {@code Parent Event}.
   *   <li>Then return FullUpdate.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, Boolean, Boolean, String)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, Boolean, Boolean, String); when 'Parent Event'; then return FullUpdate")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, Boolean, Boolean, String)"
  })
  void testNewMetadataNotification_whenParentEvent_thenReturnFullUpdate() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification("myproject", "42", "42", "42", true, true, "Parent Event");

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("Parent Event", actualMetadataNotification.getParentEventId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.LOW, actualMetadataNotification.getEventPriority());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
    assertTrue(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#MetadataNotification(String, String, String, String, Boolean,
   * Boolean, String, Priority)}.
   *
   * <ul>
   *   <li>When {@code Parent Event}.
   *   <li>Then return FullUpdate.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#MetadataNotification(String, String, String,
   * String, Boolean, Boolean, String, Priority)}
   */
  @Test
  @DisplayName(
      "Test new MetadataNotification(String, String, String, String, Boolean, Boolean, String, Priority); when 'Parent Event'; then return FullUpdate")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotification.<init>(String, String, String, String, Boolean, Boolean, String, Priority)"
  })
  void testNewMetadataNotification_whenParentEvent_thenReturnFullUpdate2() {
    // Arrange and Act
    MetadataNotification actualMetadataNotification =
        new MetadataNotification(
            "myproject", "42", "42", "42", true, true, "Parent Event", Priority.HIGH);

    // Assert
    assertEquals("42", actualMetadataNotification.getArtifactId());
    assertEquals("42", actualMetadataNotification.getGroupId());
    assertEquals("42", actualMetadataNotification.getVersionId());
    assertEquals("Parent Event", actualMetadataNotification.getParentEventId());
    assertEquals("myproject", actualMetadataNotification.getProjectId());
    assertNull(actualMetadataNotification.getEventId());
    assertNull(actualMetadataNotification.getId());
    assertNull(actualMetadataNotification.getCompleted());
    assertNull(actualMetadataNotification.getCreated());
    assertNull(actualMetadataNotification.getUpdated());
    assertNull(actualMetadataNotification.getCurrentResponse());
    assertEquals(0, actualMetadataNotification.getAttempt());
    assertEquals(2, actualMetadataNotification.getMaxAttempts());
    assertEquals(MetadataNotificationStatus.SUCCESS, actualMetadataNotification.getStatus());
    assertEquals(Priority.HIGH, actualMetadataNotification.getEventPriority());
    assertTrue(actualMetadataNotification.getResponses().isEmpty());
    assertTrue(actualMetadataNotification.isFullUpdate());
    assertTrue(actualMetadataNotification.isTransitive());
  }

  /**
   * Test {@link MetadataNotification#getStatus()}.
   *
   * <ul>
   *   <li>Then return {@code FAILED}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#getStatus()}
   */
  @Test
  @DisplayName("Test getStatus(); then return 'FAILED'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationStatus MetadataNotification.getStatus()"})
  void testGetStatus_thenReturnFailed() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.addError("An error occurred");

    // Act and Assert
    assertEquals(MetadataNotificationStatus.FAILED, metadataNotification.getStatus());
  }

  /**
   * Test {@link MetadataNotification#getStatus()}.
   *
   * <ul>
   *   <li>Then return {@code SUCCESS}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#getStatus()}
   */
  @Test
  @DisplayName("Test getStatus(); then return 'SUCCESS'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationStatus MetadataNotification.getStatus()"})
  void testGetStatus_thenReturnSuccess() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act and Assert
    assertEquals(MetadataNotificationStatus.SUCCESS, metadataNotification.getStatus());
  }

  /**
   * Test {@link MetadataNotification#complete()}.
   *
   * <p>Method under test: {@link MetadataNotification#complete()}
   */
  @Test
  @DisplayName("Test complete()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotification MetadataNotification.complete()"})
  void testComplete() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act
    MetadataNotification actualCompleteResult = metadataNotification.complete();

    // Assert
    assertSame(metadataNotification, actualCompleteResult);
  }

  /**
   * Test {@link MetadataNotification#retriesExceeded()}.
   *
   * <ul>
   *   <li>Given {@link MetadataNotification#MetadataNotification()}.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#retriesExceeded()}
   */
  @Test
  @DisplayName("Test retriesExceeded(); given MetadataNotification(); then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean MetadataNotification.retriesExceeded()"})
  void testRetriesExceeded_givenMetadataNotification_thenReturnTrue() {
    // Arrange, Act and Assert
    assertTrue(new MetadataNotification().retriesExceeded());
  }

  /**
   * Test {@link MetadataNotification#retriesExceeded()}.
   *
   * <ul>
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#retriesExceeded()}
   */
  @Test
  @DisplayName("Test retriesExceeded(); then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean MetadataNotification.retriesExceeded()"})
  void testRetriesExceeded_thenReturnFalse() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act and Assert
    assertFalse(metadataNotification.retriesExceeded());
  }

  /**
   * Test {@link MetadataNotification#getResponses()}.
   *
   * <p>Method under test: {@link MetadataNotification#getResponses()}
   */
  @Test
  @DisplayName("Test getResponses()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"Map MetadataNotification.getResponses()"})
  void testGetResponses() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act and Assert
    assertTrue(metadataNotification.getResponses().isEmpty());
  }

  /**
   * Test {@link MetadataNotification#getResponses()}.
   *
   * <p>Method under test: {@link MetadataNotification#getResponses()}
   */
  @Test
  @DisplayName("Test getResponses()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"Map MetadataNotification.getResponses()"})
  void testGetResponses2() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.setResponses(null);

    // Act and Assert
    assertTrue(metadataNotification.getResponses().isEmpty());
  }

  /**
   * Test {@link MetadataNotification#addError(String)}.
   *
   * <p>Method under test: {@link MetadataNotification#addError(String)}
   */
  @Test
  @DisplayName("Test addError(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotification MetadataNotification.addError(String)"})
  void testAddError() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act
    MetadataNotification actualAddErrorResult = metadataNotification.addError("An error occurred");

    // Assert
    assertEquals(MetadataNotificationStatus.FAILED, metadataNotification.getStatus());
    assertSame(metadataNotification, actualAddErrorResult);
  }

  /**
   * Test {@link MetadataNotification#addError(String)}.
   *
   * <p>Method under test: {@link MetadataNotification#addError(String)}
   */
  @Test
  @DisplayName("Test addError(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotification MetadataNotification.addError(String)"})
  void testAddError2() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.setResponses(null);

    // Act
    MetadataNotification actualAddErrorResult = metadataNotification.addError("An error occurred");

    // Assert
    assertEquals(MetadataNotificationStatus.FAILED, metadataNotification.getStatus());
    assertSame(metadataNotification, actualAddErrorResult);
  }

  /**
   * Test {@link MetadataNotification#setResponse(MetadataNotificationResponse)}.
   *
   * <p>Method under test: {@link MetadataNotification#setResponse(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName("Test setResponse(MetadataNotificationResponse)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void MetadataNotification.setResponse(MetadataNotificationResponse)"})
  void testSetResponse() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    MetadataNotificationResponse response = new MetadataNotificationResponse();

    // Act
    metadataNotification.setResponse(response);

    // Assert
    Map<Integer, MetadataNotificationResponse> responses = metadataNotification.getResponses();
    assertEquals(1, responses.size());
    assertSame(response, responses.get(0));
    assertSame(response, metadataNotification.getCurrentResponse());
  }

  /**
   * Test {@link MetadataNotification#setResponse(MetadataNotificationResponse)}.
   *
   * <p>Method under test: {@link MetadataNotification#setResponse(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName("Test setResponse(MetadataNotificationResponse)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void MetadataNotification.setResponse(MetadataNotificationResponse)"})
  void testSetResponse2() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.setResponses(null);
    MetadataNotificationResponse response = new MetadataNotificationResponse();

    // Act
    metadataNotification.setResponse(response);

    // Assert
    Map<Integer, MetadataNotificationResponse> responses = metadataNotification.getResponses();
    assertEquals(1, responses.size());
    assertSame(response, responses.get(0));
    assertSame(response, metadataNotification.getCurrentResponse());
  }

  /**
   * Test {@link MetadataNotification#combineResponse(MetadataNotificationResponse)}.
   *
   * <p>Method under test: {@link
   * MetadataNotification#combineResponse(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName("Test combineResponse(MetadataNotificationResponse)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "MetadataNotification MetadataNotification.combineResponse(MetadataNotificationResponse)"
  })
  void testCombineResponse() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act
    MetadataNotification actualCombineResponseResult =
        metadataNotification.combineResponse(new MetadataNotificationResponse());

    // Assert
    Map<Integer, MetadataNotificationResponse> responses =
        actualCombineResponseResult.getResponses();
    assertEquals(1, responses.size());
    MetadataNotificationResponse currentResponse = actualCombineResponseResult.getCurrentResponse();
    assertEquals(MetadataNotificationStatus.SUCCESS, currentResponse.getStatus());
    assertFalse(currentResponse.hasErrors());
    assertTrue(currentResponse.getErrors().isEmpty());
    assertTrue(currentResponse.getMessages().isEmpty());
    assertSame(currentResponse, responses.get(0));
  }

  /**
   * Test {@link MetadataNotification#combineResponse(MetadataNotificationResponse)}.
   *
   * <p>Method under test: {@link
   * MetadataNotification#combineResponse(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName("Test combineResponse(MetadataNotificationResponse)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "MetadataNotification MetadataNotification.combineResponse(MetadataNotificationResponse)"
  })
  void testCombineResponse2() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.setResponses(new HashMap<>());

    // Act
    MetadataNotification actualCombineResponseResult = metadataNotification.combineResponse(null);

    // Assert
    assertSame(metadataNotification, actualCombineResponseResult);
  }

  /**
   * Test {@link MetadataNotification#combineResponse(MetadataNotificationResponse)}.
   *
   * <p>Method under test: {@link
   * MetadataNotification#combineResponse(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName("Test combineResponse(MetadataNotificationResponse)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "MetadataNotification MetadataNotification.combineResponse(MetadataNotificationResponse)"
  })
  void testCombineResponse3() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.setResponses(null);

    // Act
    MetadataNotification actualCombineResponseResult =
        metadataNotification.combineResponse(new MetadataNotificationResponse());

    // Assert
    Map<Integer, MetadataNotificationResponse> responses =
        actualCombineResponseResult.getResponses();
    assertEquals(1, responses.size());
    MetadataNotificationResponse currentResponse = actualCombineResponseResult.getCurrentResponse();
    assertEquals(MetadataNotificationStatus.SUCCESS, currentResponse.getStatus());
    assertFalse(currentResponse.hasErrors());
    assertTrue(currentResponse.getErrors().isEmpty());
    assertTrue(currentResponse.getMessages().isEmpty());
    assertSame(currentResponse, responses.get(0));
  }

  /**
   * Test {@link MetadataNotification#getCurrentResponse()}.
   *
   * <p>Method under test: {@link MetadataNotification#getCurrentResponse()}
   */
  @Test
  @DisplayName("Test getCurrentResponse()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationResponse MetadataNotification.getCurrentResponse()"})
  void testGetCurrentResponse() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act and Assert
    assertNull(metadataNotification.getCurrentResponse());
  }

  /**
   * Test {@link MetadataNotification#getCurrentResponse()}.
   *
   * <p>Method under test: {@link MetadataNotification#getCurrentResponse()}
   */
  @Test
  @DisplayName("Test getCurrentResponse()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationResponse MetadataNotification.getCurrentResponse()"})
  void testGetCurrentResponse2() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    metadataNotification.setResponses(null);

    // Act and Assert
    assertNull(metadataNotification.getCurrentResponse());
  }

  /**
   * Test {@link MetadataNotification#equals(Object)}, and {@link MetadataNotification#hashCode()}.
   *
   * <ul>
   *   <li>When other is equal.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link MetadataNotification#equals(Object)}
   *   <li>{@link MetadataNotification#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is equal; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean MetadataNotification.equals(Object)",
    "int MetadataNotification.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");
    MetadataNotification metadataNotification2 =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act and Assert
    assertEquals(metadataNotification, metadataNotification2);
    assertEquals(metadataNotification.hashCode(), metadataNotification2.hashCode());
  }

  /**
   * Test {@link MetadataNotification#equals(Object)}, and {@link MetadataNotification#hashCode()}.
   *
   * <ul>
   *   <li>When other is same.
   *   <li>Then return equal.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link MetadataNotification#equals(Object)}
   *   <li>{@link MetadataNotification#hashCode()}
   * </ul>
   */
  @Test
  @DisplayName("Test equals(Object), and hashCode(); when other is same; then return equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean MetadataNotification.equals(Object)",
    "int MetadataNotification.hashCode()"
  })
  void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    MetadataNotification metadataNotification =
        new MetadataNotification("myproject", "42", "42", "42");

    // Act and Assert
    assertEquals(metadataNotification, metadataNotification);
    int expectedHashCodeResult = metadataNotification.hashCode();
    assertEquals(expectedHashCodeResult, metadataNotification.hashCode());
  }

  /**
   * Test {@link MetadataNotification#equals(Object)}.
   *
   * <ul>
   *   <li>When other is different.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is different; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean MetadataNotification.equals(Object)",
    "int MetadataNotification.hashCode()"
  })
  void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    MetadataNotification metadataNotification = new MetadataNotification("42", "42", "42", "42");

    // Act and Assert
    assertNotEquals(metadataNotification, new MetadataNotification("myproject", "42", "42", "42"));
  }

  /**
   * Test {@link MetadataNotification#equals(Object)}.
   *
   * <ul>
   *   <li>When other is {@code null}.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is 'null'; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean MetadataNotification.equals(Object)",
    "int MetadataNotification.hashCode()"
  })
  void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(new MetadataNotification("myproject", "42", "42", "42"), null);
  }

  /**
   * Test {@link MetadataNotification#equals(Object)}.
   *
   * <ul>
   *   <li>When other is wrong type.
   *   <li>Then return not equal.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotification#equals(Object)}
   */
  @Test
  @DisplayName("Test equals(Object); when other is wrong type; then return not equal")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "boolean MetadataNotification.equals(Object)",
    "int MetadataNotification.hashCode()"
  })
  void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(
        new MetadataNotification("myproject", "42", "42", "42"),
        "Different type to MetadataNotification");
  }
}

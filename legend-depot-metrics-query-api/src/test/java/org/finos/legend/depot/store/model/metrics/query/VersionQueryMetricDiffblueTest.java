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

package org.finos.legend.depot.store.model.metrics.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VersionQueryMetricDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionQueryMetric#VersionQueryMetric(String, String, String, Date)}
   *   <li>{@link VersionQueryMetric#setLastQueryTime(Date)}
   *   <li>{@link VersionQueryMetric#getArtifactId()}
   *   <li>{@link VersionQueryMetric#getGroupId()}
   *   <li>{@link VersionQueryMetric#getId()}
   *   <li>{@link VersionQueryMetric#getLastQueryTime()}
   *   <li>{@link VersionQueryMetric#getVersionId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void VersionQueryMetric.<init>()",
    "void VersionQueryMetric.<init>(String, String, String)",
    "void VersionQueryMetric.<init>(String, String, String, Date)",
    "String VersionQueryMetric.getArtifactId()",
    "String VersionQueryMetric.getGroupId()",
    "String VersionQueryMetric.getId()",
    "Date VersionQueryMetric.getLastQueryTime()",
    "String VersionQueryMetric.getVersionId()",
    "void VersionQueryMetric.setLastQueryTime(Date)"
  })
  void testGettersAndSetters()
  {
    // Arrange
    Date lastQueryTime =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());

    // Act
    VersionQueryMetric actualVersionQueryMetric =
        new VersionQueryMetric("42", "42", "42", lastQueryTime);
    Date time =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualVersionQueryMetric.setLastQueryTime(time);
    String actualArtifactId = actualVersionQueryMetric.getArtifactId();
    String actualGroupId = actualVersionQueryMetric.getGroupId();
    String actualId = actualVersionQueryMetric.getId();
    Date actualLastQueryTime = actualVersionQueryMetric.getLastQueryTime();

    // Assert
    assertEquals("", actualId);
    assertEquals("42", actualArtifactId);
    assertEquals("42", actualGroupId);
    assertEquals("42", actualVersionQueryMetric.getVersionId());
    assertSame(time, actualLastQueryTime);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>Then return ArtifactId is {@code null}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionQueryMetric#VersionQueryMetric()}
   *   <li>{@link VersionQueryMetric#setLastQueryTime(Date)}
   *   <li>{@link VersionQueryMetric#getArtifactId()}
   *   <li>{@link VersionQueryMetric#getGroupId()}
   *   <li>{@link VersionQueryMetric#getId()}
   *   <li>{@link VersionQueryMetric#getLastQueryTime()}
   *   <li>{@link VersionQueryMetric#getVersionId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; then return ArtifactId is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void VersionQueryMetric.<init>()",
    "void VersionQueryMetric.<init>(String, String, String)",
    "void VersionQueryMetric.<init>(String, String, String, Date)",
    "String VersionQueryMetric.getArtifactId()",
    "String VersionQueryMetric.getGroupId()",
    "String VersionQueryMetric.getId()",
    "Date VersionQueryMetric.getLastQueryTime()",
    "String VersionQueryMetric.getVersionId()",
    "void VersionQueryMetric.setLastQueryTime(Date)"
  })
  void testGettersAndSetters_thenReturnArtifactIdIsNull()
  {
    // Arrange and Act
    VersionQueryMetric actualVersionQueryMetric = new VersionQueryMetric();
    Date time =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualVersionQueryMetric.setLastQueryTime(time);
    String actualArtifactId = actualVersionQueryMetric.getArtifactId();
    String actualGroupId = actualVersionQueryMetric.getGroupId();
    String actualId = actualVersionQueryMetric.getId();
    Date actualLastQueryTime = actualVersionQueryMetric.getLastQueryTime();

    // Assert
    assertEquals("", actualId);
    assertNull(actualArtifactId);
    assertNull(actualGroupId);
    assertNull(actualVersionQueryMetric.getVersionId());
    assertSame(time, actualLastQueryTime);
  }

  /**
   * Test getters and setters.
   *
   * <ul>
   *   <li>When {@code 42}.
   *   <li>Then return ArtifactId is {@code 42}.
   * </ul>
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link VersionQueryMetric#VersionQueryMetric(String, String, String)}
   *   <li>{@link VersionQueryMetric#setLastQueryTime(Date)}
   *   <li>{@link VersionQueryMetric#getArtifactId()}
   *   <li>{@link VersionQueryMetric#getGroupId()}
   *   <li>{@link VersionQueryMetric#getId()}
   *   <li>{@link VersionQueryMetric#getLastQueryTime()}
   *   <li>{@link VersionQueryMetric#getVersionId()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when '42'; then return ArtifactId is '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void VersionQueryMetric.<init>()",
    "void VersionQueryMetric.<init>(String, String, String)",
    "void VersionQueryMetric.<init>(String, String, String, Date)",
    "String VersionQueryMetric.getArtifactId()",
    "String VersionQueryMetric.getGroupId()",
    "String VersionQueryMetric.getId()",
    "Date VersionQueryMetric.getLastQueryTime()",
    "String VersionQueryMetric.getVersionId()",
    "void VersionQueryMetric.setLastQueryTime(Date)"
  })
  void testGettersAndSetters_when42_thenReturnArtifactIdIs42()
  {
    // Arrange and Act
    VersionQueryMetric actualVersionQueryMetric = new VersionQueryMetric("42", "42", "42");
    Date time =
        Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualVersionQueryMetric.setLastQueryTime(time);
    String actualArtifactId = actualVersionQueryMetric.getArtifactId();
    String actualGroupId = actualVersionQueryMetric.getGroupId();
    String actualId = actualVersionQueryMetric.getId();
    Date actualLastQueryTime = actualVersionQueryMetric.getLastQueryTime();

    // Assert
    assertEquals("", actualId);
    assertEquals("42", actualArtifactId);
    assertEquals("42", actualGroupId);
    assertEquals("42", actualVersionQueryMetric.getVersionId());
    assertSame(time, actualLastQueryTime);
  }
}

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

package org.finos.legend.depot.store.model.admin.schedules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ScheduleInfoDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>{@link ScheduleInfo#ScheduleInfo()}
   *   <li>{@link ScheduleInfo#setDisabled(boolean)}
   *   <li>{@link ScheduleInfo#setExternalTrigger(Boolean)}
   *   <li>{@link ScheduleInfo#setFrequency(Long)}
   *   <li>{@link ScheduleInfo#setId(String)}
   *   <li>{@link ScheduleInfo#setName(String)}
   *   <li>{@link ScheduleInfo#setSingleInstance(Boolean)}
   *   <li>{@link ScheduleInfo#getExternalTrigger()}
   *   <li>{@link ScheduleInfo#getFrequency()}
   *   <li>{@link ScheduleInfo#getId()}
   *   <li>{@link ScheduleInfo#getName()}
   *   <li>{@link ScheduleInfo#getSingleInstance()}
   *   <li>{@link ScheduleInfo#isDisabled()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ScheduleInfo.<init>()",
    "void ScheduleInfo.<init>(String)",
    "Boolean ScheduleInfo.getExternalTrigger()",
    "Long ScheduleInfo.getFrequency()",
    "String ScheduleInfo.getId()",
    "String ScheduleInfo.getName()",
    "Boolean ScheduleInfo.getSingleInstance()",
    "boolean ScheduleInfo.isDisabled()",
    "void ScheduleInfo.setDisabled(boolean)",
    "void ScheduleInfo.setExternalTrigger(Boolean)",
    "void ScheduleInfo.setFrequency(Long)",
    "void ScheduleInfo.setId(String)",
    "void ScheduleInfo.setName(String)",
    "void ScheduleInfo.setSingleInstance(Boolean)"
  })
  void testGettersAndSetters()
  {
    // Arrange and Act
    ScheduleInfo actualScheduleInfo = new ScheduleInfo();
    actualScheduleInfo.setDisabled(true);
    actualScheduleInfo.setExternalTrigger(true);
    actualScheduleInfo.setFrequency(1L);
    actualScheduleInfo.setId("42");
    actualScheduleInfo.setName("Name");
    actualScheduleInfo.setSingleInstance(true);
    Boolean actualExternalTrigger = actualScheduleInfo.getExternalTrigger();
    Long actualFrequency = actualScheduleInfo.getFrequency();
    String actualId = actualScheduleInfo.getId();
    String actualName = actualScheduleInfo.getName();
    Boolean actualSingleInstance = actualScheduleInfo.getSingleInstance();
    boolean actualIsDisabledResult = actualScheduleInfo.isDisabled();

    // Assert
    assertEquals("42", actualId);
    assertEquals("Name", actualName);
    assertEquals(1L, actualFrequency.longValue());
    assertTrue(actualExternalTrigger);
    assertTrue(actualSingleInstance);
    assertTrue(actualIsDisabledResult);
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
   *   <li>{@link ScheduleInfo#ScheduleInfo(String)}
   *   <li>{@link ScheduleInfo#setDisabled(boolean)}
   *   <li>{@link ScheduleInfo#setExternalTrigger(Boolean)}
   *   <li>{@link ScheduleInfo#setFrequency(Long)}
   *   <li>{@link ScheduleInfo#setId(String)}
   *   <li>{@link ScheduleInfo#setName(String)}
   *   <li>{@link ScheduleInfo#setSingleInstance(Boolean)}
   *   <li>{@link ScheduleInfo#getExternalTrigger()}
   *   <li>{@link ScheduleInfo#getFrequency()}
   *   <li>{@link ScheduleInfo#getId()}
   *   <li>{@link ScheduleInfo#getName()}
   *   <li>{@link ScheduleInfo#getSingleInstance()}
   *   <li>{@link ScheduleInfo#isDisabled()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters; when 'Name'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void ScheduleInfo.<init>()",
    "void ScheduleInfo.<init>(String)",
    "Boolean ScheduleInfo.getExternalTrigger()",
    "Long ScheduleInfo.getFrequency()",
    "String ScheduleInfo.getId()",
    "String ScheduleInfo.getName()",
    "Boolean ScheduleInfo.getSingleInstance()",
    "boolean ScheduleInfo.isDisabled()",
    "void ScheduleInfo.setDisabled(boolean)",
    "void ScheduleInfo.setExternalTrigger(Boolean)",
    "void ScheduleInfo.setFrequency(Long)",
    "void ScheduleInfo.setId(String)",
    "void ScheduleInfo.setName(String)",
    "void ScheduleInfo.setSingleInstance(Boolean)"
  })
  void testGettersAndSetters_whenName()
  {
    // Arrange and Act
    ScheduleInfo actualScheduleInfo = new ScheduleInfo("Name");
    actualScheduleInfo.setDisabled(true);
    actualScheduleInfo.setExternalTrigger(true);
    actualScheduleInfo.setFrequency(1L);
    actualScheduleInfo.setId("42");
    actualScheduleInfo.setName("Name");
    actualScheduleInfo.setSingleInstance(true);
    Boolean actualExternalTrigger = actualScheduleInfo.getExternalTrigger();
    Long actualFrequency = actualScheduleInfo.getFrequency();
    String actualId = actualScheduleInfo.getId();
    String actualName = actualScheduleInfo.getName();
    Boolean actualSingleInstance = actualScheduleInfo.getSingleInstance();
    boolean actualIsDisabledResult = actualScheduleInfo.isDisabled();

    // Assert
    assertEquals("42", actualId);
    assertEquals("Name", actualName);
    assertEquals(1L, actualFrequency.longValue());
    assertTrue(actualExternalTrigger);
    assertTrue(actualSingleInstance);
    assertTrue(actualIsDisabledResult);
  }
}

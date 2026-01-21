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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleInfoClaudeTest 

{

    @Test
    @DisplayName("Test default constructor initializes fields correctly")
    void testDefaultConstructor()
  {
        // Act
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Assert - default constructor should leave most fields null
        assertNull(scheduleInfo.getId());
        assertNull(scheduleInfo.getName());
        assertFalse(scheduleInfo.isDisabled()); // disabled defaults to false
        assertNull(scheduleInfo.getSingleInstance());
        assertNull(scheduleInfo.getExternalTrigger());
        assertNull(scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test constructor with name parameter")
    void testConstructorWithName()
  {
        // Arrange
        String expectedName = "TestSchedule";

        // Act
        ScheduleInfo scheduleInfo = new ScheduleInfo(expectedName);

        // Assert
        assertEquals(expectedName, scheduleInfo.getName());
        assertNull(scheduleInfo.getId());
        assertFalse(scheduleInfo.isDisabled());
        assertNull(scheduleInfo.getSingleInstance());
        assertNull(scheduleInfo.getExternalTrigger());
        assertNull(scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test constructor with null name")
    void testConstructorWithNullName()
  {
        // Act
        ScheduleInfo scheduleInfo = new ScheduleInfo(null);

        // Assert
        assertNull(scheduleInfo.getName());
    }

    @Test
    @DisplayName("Test getId and setId with valid string")
    void testGetAndSetId()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        String expectedId = "schedule-123";

        // Act
        scheduleInfo.setId(expectedId);

        // Assert
        assertEquals(expectedId, scheduleInfo.getId());
    }

    @Test
    @DisplayName("Test setId with null value")
    void testSetIdWithNull()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setId("initial-id");

        // Act
        scheduleInfo.setId(null);

        // Assert
        assertNull(scheduleInfo.getId());
    }

    @Test
    @DisplayName("Test setId with empty string")
    void testSetIdWithEmptyString()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setId("");

        // Assert
        assertEquals("", scheduleInfo.getId());
    }

    @Test
    @DisplayName("Test getName and setName with valid string")
    void testGetAndSetName()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        String expectedName = "Daily Backup";

        // Act
        scheduleInfo.setName(expectedName);

        // Assert
        assertEquals(expectedName, scheduleInfo.getName());
    }

    @Test
    @DisplayName("Test setName overwrites constructor name")
    void testSetNameOverwritesConstructorName()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo("InitialName");
        String newName = "UpdatedName";

        // Act
        scheduleInfo.setName(newName);

        // Assert
        assertEquals(newName, scheduleInfo.getName());
    }

    @Test
    @DisplayName("Test setName with null value")
    void testSetNameWithNull()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo("SomeName");

        // Act
        scheduleInfo.setName(null);

        // Assert
        assertNull(scheduleInfo.getName());
    }

    @Test
    @DisplayName("Test isDisabled and setDisabled with true")
    void testIsDisabledAndSetDisabledTrue()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setDisabled(true);

        // Assert
        assertTrue(scheduleInfo.isDisabled());
    }

    @Test
    @DisplayName("Test isDisabled and setDisabled with false")
    void testIsDisabledAndSetDisabledFalse()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setDisabled(true);

        // Act
        scheduleInfo.setDisabled(false);

        // Assert
        assertFalse(scheduleInfo.isDisabled());
    }

    @Test
    @DisplayName("Test disabled field default value is false")
    void testDisabledDefaultValue()
  {
        // Act
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Assert
        assertFalse(scheduleInfo.isDisabled());
    }

    @Test
    @DisplayName("Test getSingleInstance and setSingleInstance with true")
    void testGetAndSetSingleInstanceTrue()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setSingleInstance(true);

        // Assert
        assertEquals(Boolean.TRUE, scheduleInfo.getSingleInstance());
        assertTrue(scheduleInfo.getSingleInstance());
    }

    @Test
    @DisplayName("Test getSingleInstance and setSingleInstance with false")
    void testGetAndSetSingleInstanceFalse()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setSingleInstance(false);

        // Assert
        assertEquals(Boolean.FALSE, scheduleInfo.getSingleInstance());
        assertFalse(scheduleInfo.getSingleInstance());
    }

    @Test
    @DisplayName("Test setSingleInstance with null value")
    void testSetSingleInstanceWithNull()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setSingleInstance(true);

        // Act
        scheduleInfo.setSingleInstance(null);

        // Assert
        assertNull(scheduleInfo.getSingleInstance());
    }

    @Test
    @DisplayName("Test getExternalTrigger and setExternalTrigger with true")
    void testGetAndSetExternalTriggerTrue()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setExternalTrigger(true);

        // Assert
        assertEquals(Boolean.TRUE, scheduleInfo.getExternalTrigger());
        assertTrue(scheduleInfo.getExternalTrigger());
    }

    @Test
    @DisplayName("Test getExternalTrigger and setExternalTrigger with false")
    void testGetAndSetExternalTriggerFalse()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setExternalTrigger(false);

        // Assert
        assertEquals(Boolean.FALSE, scheduleInfo.getExternalTrigger());
        assertFalse(scheduleInfo.getExternalTrigger());
    }

    @Test
    @DisplayName("Test setExternalTrigger with null value")
    void testSetExternalTriggerWithNull()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setExternalTrigger(true);

        // Act
        scheduleInfo.setExternalTrigger(null);

        // Assert
        assertNull(scheduleInfo.getExternalTrigger());
    }

    @Test
    @DisplayName("Test getFrequency and setFrequency with positive value")
    void testGetAndSetFrequencyPositive()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        Long expectedFrequency = 3600L;

        // Act
        scheduleInfo.setFrequency(expectedFrequency);

        // Assert
        assertEquals(expectedFrequency, scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test setFrequency with zero")
    void testSetFrequencyWithZero()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setFrequency(0L);

        // Assert
        assertEquals(0L, scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test setFrequency with negative value")
    void testSetFrequencyWithNegative()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setFrequency(-1L);

        // Assert
        assertEquals(-1L, scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test setFrequency with null value")
    void testSetFrequencyWithNull()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequency(1000L);

        // Act
        scheduleInfo.setFrequency(null);

        // Assert
        assertNull(scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test setFrequency with maximum Long value")
    void testSetFrequencyWithMaxValue()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setFrequency(Long.MAX_VALUE);

        // Assert
        assertEquals(Long.MAX_VALUE, scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test complete workflow with all fields set")
    void testCompleteWorkflow()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo("Weekly Report");

        // Act
        scheduleInfo.setId("report-weekly-001");
        scheduleInfo.setDisabled(false);
        scheduleInfo.setSingleInstance(true);
        scheduleInfo.setExternalTrigger(false);
        scheduleInfo.setFrequency(604800L); // 1 week in seconds

        // Assert
        assertEquals("report-weekly-001", scheduleInfo.getId());
        assertEquals("Weekly Report", scheduleInfo.getName());
        assertFalse(scheduleInfo.isDisabled());
        assertTrue(scheduleInfo.getSingleInstance());
        assertFalse(scheduleInfo.getExternalTrigger());
        assertEquals(604800L, scheduleInfo.getFrequency());
    }

    @Test
    @DisplayName("Test multiple updates to same field")
    void testMultipleUpdatesToSameField()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act & Assert - multiple updates to id
        scheduleInfo.setId("id1");
        assertEquals("id1", scheduleInfo.getId());

        scheduleInfo.setId("id2");
        assertEquals("id2", scheduleInfo.getId());

        scheduleInfo.setId("id3");
        assertEquals("id3", scheduleInfo.getId());

        // Act & Assert - multiple updates to disabled
        scheduleInfo.setDisabled(true);
        assertTrue(scheduleInfo.isDisabled());

        scheduleInfo.setDisabled(false);
        assertFalse(scheduleInfo.isDisabled());

        scheduleInfo.setDisabled(true);
        assertTrue(scheduleInfo.isDisabled());
    }

    @Test
    @DisplayName("Test all fields with null values")
    void testAllFieldsWithNullValues()
  {
        // Arrange
        ScheduleInfo scheduleInfo = new ScheduleInfo();

        // Act
        scheduleInfo.setId(null);
        scheduleInfo.setName(null);
        scheduleInfo.setSingleInstance(null);
        scheduleInfo.setExternalTrigger(null);
        scheduleInfo.setFrequency(null);

        // Assert
        assertNull(scheduleInfo.getId());
        assertNull(scheduleInfo.getName());
        assertNull(scheduleInfo.getSingleInstance());
        assertNull(scheduleInfo.getExternalTrigger());
        assertNull(scheduleInfo.getFrequency());
        // disabled is primitive boolean, so it cannot be null
        assertFalse(scheduleInfo.isDisabled());
    }
}

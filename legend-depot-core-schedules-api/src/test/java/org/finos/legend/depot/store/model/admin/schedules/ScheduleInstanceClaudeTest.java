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

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleInstanceClaudeTest


{
    @Test
    void testDefaultConstructor()
  {
        // Act
        ScheduleInstance instance = new ScheduleInstance();

        // Assert
        assertNull(instance.getId());
        assertNull(instance.getSchedule());
        assertNull(instance.getExpires());
    }

    @Test
    void testParameterizedConstructor()
  {
        // Arrange
        String scheduleName = "test-schedule";
        Date expiryDate = new Date();

        // Act
        ScheduleInstance instance = new ScheduleInstance(scheduleName, expiryDate);

        // Assert
        assertNull(instance.getId()); // id is not set by constructor
        assertEquals(scheduleName, instance.getSchedule());
        assertSame(expiryDate, instance.getExpires());
    }

    @Test
    void testParameterizedConstructorWithNullValues()
  {
        // Act
        ScheduleInstance instance = new ScheduleInstance(null, null);

        // Assert
        assertNull(instance.getId());
        assertNull(instance.getSchedule());
        assertNull(instance.getExpires());
    }

    @Test
    void testGetAndSetId()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        String testId = "test-id-123";

        // Act
        instance.setId(testId);

        // Assert
        assertEquals(testId, instance.getId());
    }

    @Test
    void testSetIdWithNull()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        instance.setId("initial-id");

        // Act
        instance.setId(null);

        // Assert
        assertNull(instance.getId());
    }

    @Test
    void testGetAndSetSchedule()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        String scheduleName = "daily-backup";

        // Act
        instance.setSchedule(scheduleName);

        // Assert
        assertEquals(scheduleName, instance.getSchedule());
    }

    @Test
    void testSetScheduleWithNull()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        instance.setSchedule("initial-schedule");

        // Act
        instance.setSchedule(null);

        // Assert
        assertNull(instance.getSchedule());
    }

    @Test
    void testGetAndSetExpires()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        Date expiryDate = new Date();

        // Act
        instance.setExpires(expiryDate);

        // Assert
        assertSame(expiryDate, instance.getExpires());
    }

    @Test
    void testSetExpiresWithNull()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        instance.setExpires(new Date());

        // Act
        instance.setExpires(null);

        // Assert
        assertNull(instance.getExpires());
    }

    @Test
    void testIsExpiredReturnsTrueForPastDate()
  {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1); // 1 hour in the past
        Date pastDate = calendar.getTime();

        ScheduleInstance instance = new ScheduleInstance("test", pastDate);

        // Act
        boolean result = instance.isExpired();

        // Assert
        assertTrue(result, "Instance should be expired when expiry date is in the past");
    }

    @Test
    void testIsExpiredReturnsFalseForFutureDate()
  {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1); // 1 hour in the future
        Date futureDate = calendar.getTime();

        ScheduleInstance instance = new ScheduleInstance("test", futureDate);

        // Act
        boolean result = instance.isExpired();

        // Assert
        assertFalse(result, "Instance should not be expired when expiry date is in the future");
    }

    @Test
    void testIsExpiredWithVeryOldDate()
  {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        Date oldDate = calendar.getTime();

        ScheduleInstance instance = new ScheduleInstance("test", oldDate);

        // Act
        boolean result = instance.isExpired();

        // Assert
        assertTrue(result, "Instance should be expired for dates many years in the past");
    }

    @Test
    void testIsExpiredWithFarFutureDate()
  {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.set(2100, Calendar.DECEMBER, 31, 23, 59, 59);
        Date futureDate = calendar.getTime();

        ScheduleInstance instance = new ScheduleInstance("test", futureDate);

        // Act
        boolean result = instance.isExpired();

        // Assert
        assertFalse(result, "Instance should not be expired for dates far in the future");
    }

    @Test
    void testMultipleSettersAndGetters()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance();
        String id = "instance-1";
        String schedule = "weekly-cleanup";
        Date expires = new Date();

        // Act
        instance.setId(id);
        instance.setSchedule(schedule);
        instance.setExpires(expires);

        // Assert
        assertEquals(id, instance.getId());
        assertEquals(schedule, instance.getSchedule());
        assertSame(expires, instance.getExpires());
    }

    @Test
    void testOverwritingValues()
  {
        // Arrange
        ScheduleInstance instance = new ScheduleInstance("initial", new Date());
        instance.setId("initial-id");

        String newId = "new-id";
        String newSchedule = "new-schedule";
        Date newExpires = new Date();

        // Act
        instance.setId(newId);
        instance.setSchedule(newSchedule);
        instance.setExpires(newExpires);

        // Assert
        assertEquals(newId, instance.getId());
        assertEquals(newSchedule, instance.getSchedule());
        assertSame(newExpires, instance.getExpires());
    }
}

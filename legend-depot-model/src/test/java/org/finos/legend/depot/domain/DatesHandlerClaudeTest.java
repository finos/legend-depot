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

package org.finos.legend.depot.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DatesHandlerClaudeTest


{
    // Tests for constructor
    @Test
    public void testConstructor()
  {
        // Test that the class can be instantiated
        DatesHandler datesHandler = new DatesHandler();
        Assertions.assertNotNull(datesHandler);
    }

    @Test
    public void testZoneIdIsSystemDefault()
  {
        // Test that ZONE_ID is set to system default
        Assertions.assertEquals(ZoneId.systemDefault(), DatesHandler.ZONE_ID);
    }

    // Tests for toTime(LocalDateTime)

    @Test
    public void testToTimeWithSpecificDateTime()
  {
        LocalDateTime dateTime = LocalDateTime.of(2023, 3, 21, 14, 30, 45);
        long timestamp = DatesHandler.toTime(dateTime);

        // Verify the timestamp is positive and reasonable
        Assertions.assertTrue(timestamp > 0);

        // Verify we can convert back and get the same date
        LocalDateTime converted = DatesHandler.toDate(timestamp);
        Assertions.assertEquals(dateTime, converted);
    }

    @Test
    public void testToTimeWithEpochStart()
  {
        LocalDateTime epochStart = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        long timestamp = DatesHandler.toTime(epochStart);

        // Should be close to 0, adjusted for timezone
        LocalDateTime converted = DatesHandler.toDate(timestamp);
        Assertions.assertEquals(epochStart, converted);
    }

    @Test
    public void testToTimeWithFutureDate()
  {
        LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
        long timestamp = DatesHandler.toTime(futureDate);

        // Should be a large positive number
        Assertions.assertTrue(timestamp > System.currentTimeMillis());

        // Verify roundtrip conversion
        LocalDateTime converted = DatesHandler.toDate(timestamp);
        Assertions.assertEquals(futureDate, converted);
    }

    @Test
    public void testToTimeWithPastDate()
  {
        LocalDateTime pastDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        long timestamp = DatesHandler.toTime(pastDate);

        // Should be positive but less than current time
        Assertions.assertTrue(timestamp > 0);
        Assertions.assertTrue(timestamp < System.currentTimeMillis());
    }

    @Test
    public void testToTimeWithMilliseconds()
  {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 10, 20, 30, 123456789);
        long timestamp = DatesHandler.toTime(dateTime);

        // Verify milliseconds are preserved (nanoseconds are truncated to millis)
        LocalDateTime converted = DatesHandler.toDate(timestamp);
        Assertions.assertEquals(dateTime.withNano((dateTime.getNano() / 1000000) * 1000000), converted);
    }

    // Tests for toDate(Date)

    @Test
    public void testToDateFromDateObject()
  {
        Date date = new Date(1679411706436L);
        LocalDateTime result = DatesHandler.toDate(date);

        Assertions.assertNotNull(result);

        // Verify we can convert back
        Date converted = DatesHandler.toDate(result);
        Assertions.assertEquals(date.getTime(), converted.getTime());
    }

    @Test
    public void testToDateFromCurrentDate()
  {
        Date now = new Date();
        LocalDateTime result = DatesHandler.toDate(now);

        Assertions.assertNotNull(result);

        // Verify the conversion preserves the timestamp
        long originalTime = now.getTime();
        long convertedTime = DatesHandler.toTime(result);
        Assertions.assertEquals(originalTime, convertedTime);
    }

    @Test
    public void testToDateFromEpochDate()
  {
        Date epochDate = new Date(0);
        LocalDateTime result = DatesHandler.toDate(epochDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1970, result.getYear());
        Assertions.assertEquals(1, result.getMonthValue());
        Assertions.assertEquals(1, result.getDayOfMonth());
    }

    // Tests for toDate(long)

    @Test
    public void testToDateFromLongZero()
  {
        LocalDateTime result = DatesHandler.toDate(0L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1970, result.getYear());
    }

    @Test
    public void testToDateFromLongPositive()
  {
        long timestamp = 1679411706436L; // 2023-03-21
        LocalDateTime result = DatesHandler.toDate(timestamp);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2023, result.getYear());
        Assertions.assertEquals(3, result.getMonthValue());
    }

    @Test
    public void testToDateFromLongSmallValue()
  {
        long timestamp = 1000L;
        LocalDateTime result = DatesHandler.toDate(timestamp);

        Assertions.assertNotNull(result);
        // Should be very close to epoch
        Assertions.assertEquals(1970, result.getYear());
    }

    @Test
    public void testToDateFromLongRoundtrip()
  {
        long originalTimestamp = 1609459200000L; // 2021-01-01 00:00:00
        LocalDateTime dateTime = DatesHandler.toDate(originalTimestamp);
        long convertedTimestamp = DatesHandler.toTime(dateTime);

        Assertions.assertEquals(originalTimestamp, convertedTimestamp);
    }

    // Tests for toDate(LocalDateTime) - returns Date

    @Test
    public void testToDateFromLocalDateTime()
  {
        LocalDateTime dateTime = LocalDateTime.of(2023, 3, 21, 14, 30, 45);
        Date result = DatesHandler.toDate(dateTime);

        Assertions.assertNotNull(result);

        // Verify we can convert back
        LocalDateTime converted = DatesHandler.toDate(result);
        Assertions.assertEquals(dateTime, converted);
    }

    @Test
    public void testToDateFromLocalDateTimeWithNanoseconds()
  {
        LocalDateTime dateTime = LocalDateTime.of(2023, 6, 15, 10, 20, 30, 500000000);
        Date result = DatesHandler.toDate(dateTime);

        Assertions.assertNotNull(result);

        // Date only has millisecond precision, so nanoseconds get truncated
        LocalDateTime converted = DatesHandler.toDate(result);
        Assertions.assertEquals(dateTime.withNano(500000000), converted);
    }

    @Test
    public void testToDateFromLocalDateTimeEpoch()
  {
        LocalDateTime epochDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        Date result = DatesHandler.toDate(epochDateTime);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testToDateFromLocalDateTimeWithMinValues()
  {
        LocalDateTime minDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0, 1);
        Date result = DatesHandler.toDate(minDateTime);

        Assertions.assertNotNull(result);
    }

    // Tests for parseDate(String)

    @Test
    public void testParseDateWithIsoDateTime()
  {
        String dateString = "2023-03-21T14:30:45";
        LocalDateTime result = DatesHandler.parseDate(dateString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2023, result.getYear());
        Assertions.assertEquals(3, result.getMonthValue());
        Assertions.assertEquals(21, result.getDayOfMonth());
        Assertions.assertEquals(14, result.getHour());
        Assertions.assertEquals(30, result.getMinute());
        Assertions.assertEquals(45, result.getSecond());
    }

    @Test
    public void testParseDateWithIsoDateTimeWithMillis()
  {
        String dateString = "2023-03-21T14:30:45.123";
        LocalDateTime result = DatesHandler.parseDate(dateString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2023, result.getYear());
        Assertions.assertEquals(3, result.getMonthValue());
        Assertions.assertEquals(21, result.getDayOfMonth());
        Assertions.assertEquals(14, result.getHour());
        Assertions.assertEquals(30, result.getMinute());
        Assertions.assertEquals(45, result.getSecond());
    }

    @Test
    public void testParseDateWithEpochMillis()
  {
        String dateString = "1679411706436";
        LocalDateTime result = DatesHandler.parseDate(dateString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2023, result.getYear());
    }

    @Test
    public void testParseDateWithZeroEpochMillis()
  {
        String dateString = "0";
        LocalDateTime result = DatesHandler.parseDate(dateString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1970, result.getYear());
    }

    @Test
    public void testParseDateWithSmallEpochMillis()
  {
        String dateString = "1000";
        LocalDateTime result = DatesHandler.parseDate(dateString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1970, result.getYear());
    }

    @Test
    public void testParseDateWithLargeEpochMillis()
  {
        String dateString = "1893456000000"; // 2030-01-01
        LocalDateTime result = DatesHandler.parseDate(dateString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2030, result.getYear());
    }

    @Test
    public void testParseDateRoundtripWithIsoFormat()
  {
        LocalDateTime original = LocalDateTime.of(2023, 3, 21, 14, 30, 45);
        long timestamp = DatesHandler.toTime(original);
        String dateString = String.valueOf(timestamp);
        LocalDateTime parsed = DatesHandler.parseDate(dateString);

        Assertions.assertEquals(original, parsed);
    }

    // Integration tests - testing combinations of methods

    @Test
    public void testCompleteRoundtripLocalDateTimeToTimestampAndBack()
  {
        LocalDateTime original = LocalDateTime.of(2023, 6, 15, 10, 20, 30, 123000000);

        // Convert to timestamp
        long timestamp = DatesHandler.toTime(original);

        // Convert back to LocalDateTime
        LocalDateTime result = DatesHandler.toDate(timestamp);

        Assertions.assertEquals(original, result);
    }

    @Test
    public void testCompleteRoundtripDateToLocalDateTimeAndBack()
  {
        Date original = new Date(1679411706436L);

        // Convert to LocalDateTime
        LocalDateTime localDateTime = DatesHandler.toDate(original);

        // Convert back to Date
        Date result = DatesHandler.toDate(localDateTime);

        Assertions.assertEquals(original.getTime(), result.getTime());
    }

    @Test
    public void testParseAndConvertToDate()
  {
        String dateString = "2023-03-21T14:30:45";
        LocalDateTime parsed = DatesHandler.parseDate(dateString);
        Date date = DatesHandler.toDate(parsed);

        Assertions.assertNotNull(date);

        // Convert back and verify
        LocalDateTime converted = DatesHandler.toDate(date);
        Assertions.assertEquals(parsed, converted);
    }

    @Test
    public void testMultipleConversionsPreserveData()
  {
        LocalDateTime original = LocalDateTime.of(2023, 3, 21, 14, 30, 45);

        // Multiple conversions
        long timestamp1 = DatesHandler.toTime(original);
        LocalDateTime intermediate1 = DatesHandler.toDate(timestamp1);
        Date dateObject = DatesHandler.toDate(intermediate1);
        LocalDateTime intermediate2 = DatesHandler.toDate(dateObject);
        long timestamp2 = DatesHandler.toTime(intermediate2);

        // All should be equal
        Assertions.assertEquals(original, intermediate1);
        Assertions.assertEquals(original, intermediate2);
        Assertions.assertEquals(timestamp1, timestamp2);
    }
}

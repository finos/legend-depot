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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DatesHandler
{
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static long toTime(LocalDateTime date)
    {
        return Date.from(date.atZone(ZONE_ID).toInstant()).getTime();
    }

    public static LocalDateTime toDate(Date date)
    {
        return toDate(date.getTime());
    }

    public static LocalDateTime toDate(long time)
    {
        return Instant.ofEpochMilli(time).atZone(ZONE_ID).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime date)
    {
        return Date.from(date.atZone(ZONE_ID).toInstant());
    }


    public static LocalDateTime parseDate(String dateString)
    {
        try
        {
            return LocalDateTime.parse(dateString,DateTimeFormatter.ISO_DATE_TIME);
        }
        catch (DateTimeParseException exception)
        {
            return LocalDateTime.from(Instant.ofEpochMilli(Long.parseLong(dateString)).atZone(ZONE_ID));
        }
    }
}

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
import java.time.format.DateTimeFormatter;

public class DatesHandlerTest
{
    @Test
    public void canGetEventByEpocMillis()
    {

        LocalDateTime date = DatesHandler.parseDate("1679411706436");
        LocalDateTime lunchTime = LocalDateTime.parse("2023-03-21T14:02:49", DateTimeFormatter.ISO_DATE_TIME);
        Assertions.assertNotNull(date);

    }
}

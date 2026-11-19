//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.core.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestFilterPriorities
{
    @Test
    public void legacyNamesAreRenamed()
    {
        Map<String, Integer> legacy = new LinkedHashMap<>();
        legacy.put("Username", 1);
        legacy.put("OpenTracingFilter", 2);
        legacy.put("org.pac4j.j2e.filter.SecurityFilter", 3);
        legacy.put("org.pac4j.j2e.filter.CallbackFilter", 4);
        legacy.put("CORS", 5);
        Assertions.assertEquals(Map.of("Username", 1, "OpenTracing", 2, "org.pac4j.jee.filter.SecurityFilter", 3, "org.pac4j.jee.filter.CallbackFilter", 4, "CORS", 5),
                FilterPriorities.withCurrentFilterNames(legacy));
    }

    @Test
    public void currentNamesAreKept()
    {
        Map<String, Integer> current = Map.of("Username", 1, "OpenTracing", 2, "org.pac4j.jee.filter.SecurityFilter", 3);
        Assertions.assertEquals(current, FilterPriorities.withCurrentFilterNames(current));
    }

    @Test
    public void currentNameWinsOverItsLegacyName()
    {
        Map<String, Integer> both = new LinkedHashMap<>();
        both.put("org.pac4j.j2e.filter.SecurityFilter", 9);
        both.put("org.pac4j.jee.filter.SecurityFilter", 3);
        Assertions.assertEquals(Map.of("org.pac4j.jee.filter.SecurityFilter", 3), FilterPriorities.withCurrentFilterNames(both));
    }

    @Test
    public void unknownNamesAreReported()
    {
        List<String> filters = List.of("CORS", "Username", "org.pac4j.jee.filter.SecurityFilter");
        Assertions.assertEquals(List.of("Typo"), FilterPriorities.unknownFilterNames(List.of("CORS", "Typo", "Username"), filters, false));
    }

    @Test
    public void openTracingIsOnlyExpectedWhenTracingIsEnabled()
    {
        List<String> filters = List.of("CORS");
        Assertions.assertEquals(List.of(), FilterPriorities.unknownFilterNames(List.of("CORS", "OpenTracing"), filters, false));
        Assertions.assertEquals(List.of("OpenTracing"), FilterPriorities.unknownFilterNames(List.of("CORS", "OpenTracing"), filters, true));
    }
}

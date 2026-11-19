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

import org.slf4j.Logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ChainFixingFilterHandler drops priorities for names that match no filter without a word, so a config that still
 * names a filter by its pre-Jakarta name would quietly lose its place in the chain. With pac4j's filters that puts the
 * Username and Guice filters ahead of the SecurityFilter, before the request carries the pac4j profile.
 */
final class FilterPriorities
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(FilterPriorities.class);

    static final String OPEN_TRACING = "OpenTracing";
    private static final String LEGACY_OPEN_TRACING = "OpenTracingFilter";
    private static final String LEGACY_PAC4J_PREFIX = "org.pac4j.j2e.";
    private static final String PAC4J_PREFIX = "org.pac4j.jee.";

    private FilterPriorities()
    {
    }

    static Map<String, Integer> withCurrentFilterNames(Map<String, Integer> priorities)
    {
        Map<String, Integer> current = new LinkedHashMap<>();
        priorities.forEach((name, priority) ->
        {
            if (currentName(name).equals(name))
            {
                current.put(name, priority);
            }
        });
        priorities.forEach((name, priority) ->
        {
            String currentName = currentName(name);
            if (!currentName.equals(name))
            {
                if (current.containsKey(currentName))
                {
                    LOGGER.warn("filterPriorities: ignoring {}, the old name of {}, which is also configured", name, currentName);
                }
                else
                {
                    LOGGER.warn("filterPriorities: {} is the old name of {}; applying its priority to {}. Rename it in the configuration", name, currentName, currentName);
                    current.put(currentName, priority);
                }
            }
        });
        return current;
    }

    private static String currentName(String name)
    {
        if (LEGACY_OPEN_TRACING.equals(name))
        {
            return OPEN_TRACING;
        }
        return name.startsWith(LEGACY_PAC4J_PREFIX) ? PAC4J_PREFIX + name.substring(LEGACY_PAC4J_PREFIX.length()) : name;
    }

    /**
     * The tracing bundle registers its OpenTracing filter only when tracing is enabled, so that name is expected to be
     * missing otherwise.
     */
    static List<String> unknownFilterNames(Collection<String> priorityNames, Collection<String> filterNames, boolean tracingEnabled)
    {
        return priorityNames.stream()
                .filter(name -> !filterNames.contains(name))
                .filter(name -> tracingEnabled || !OPEN_TRACING.equals(name))
                .collect(Collectors.toList());
    }
}

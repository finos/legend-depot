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

package org.finos.legend.depot.core.services.api.metrics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VoidPrometheusMetricsHandlerClaude_incrementErrorCountTest
{
    private VoidPrometheusMetricsHandler handler;

    @BeforeEach
    public void setUp()
    {
        handler = new VoidPrometheusMetricsHandler();
    }

    @Test
    public void testIncrementErrorCountWithValidCounter()
    {
        // Test that incrementErrorCount can be called without throwing exceptions
        Assertions.assertDoesNotThrow(() -> handler.incrementErrorCount("testErrorCounter"));
    }

    @Test
    public void testIncrementErrorCountWithEmptyString()
    {
        // Test with empty string counter name
        Assertions.assertDoesNotThrow(() -> handler.incrementErrorCount(""));
    }

    @Test
    public void testIncrementErrorCountWithNull()
    {
        // Test with null counter name - void implementation should handle this gracefully
        Assertions.assertDoesNotThrow(() -> handler.incrementErrorCount(null));
    }

    @Test
    public void testIncrementErrorCountMultipleTimes()
    {
        // Test calling incrementErrorCount multiple times on same counter
        Assertions.assertDoesNotThrow(() ->
                {
            handler.incrementErrorCount("errorCounter1");
            handler.incrementErrorCount("errorCounter1");
            handler.incrementErrorCount("errorCounter1");
        });
    }

    @Test
    public void testIncrementErrorCountWithDifferentCounters()
    {
        // Test with different error counter names
        Assertions.assertDoesNotThrow(() ->
                {
            handler.incrementErrorCount("errorCounter1");
            handler.incrementErrorCount("errorCounter2");
            handler.incrementErrorCount("errorCounter3");
        });
    }

    @Test
    public void testIncrementErrorCountWithSpecialCharacters()
    {
        // Test with special characters in counter name
        Assertions.assertDoesNotThrow(() ->
                {
            handler.incrementErrorCount("error_counter_underscore");
            handler.incrementErrorCount("error-counter-dash");
            handler.incrementErrorCount("error.counter.dot");
            handler.incrementErrorCount("error:counter:colon");
        });
    }

    @Test
    public void testIncrementErrorCountWithLongCounterName()
    {
        // Test with a very long counter name
        String longName = "error_" + "a".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.incrementErrorCount(longName));
    }

    @Test
    public void testIncrementErrorCountWithNumericCounterName()
    {
        // Test with numeric counter name
        Assertions.assertDoesNotThrow(() -> handler.incrementErrorCount("12345"));
    }

    @Test
    public void testIncrementErrorCountMixedWithIncrementCount()
    {
        // Test that incrementErrorCount can be called alongside incrementCount
        Assertions.assertDoesNotThrow(() ->
                {
            handler.incrementCount("counter1");
            handler.incrementErrorCount("counter1");
            handler.incrementCount("counter2");
            handler.incrementErrorCount("counter2");
        });
    }
}

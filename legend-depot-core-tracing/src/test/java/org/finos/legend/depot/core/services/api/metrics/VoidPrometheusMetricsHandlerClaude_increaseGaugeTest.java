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

public class VoidPrometheusMetricsHandlerClaude_increaseGaugeTest
{
    private VoidPrometheusMetricsHandler handler;

    @BeforeEach
    public void setUp()
    {
        handler = new VoidPrometheusMetricsHandler();
    }

    @Test
    public void testIncreaseGaugeWithValidParameters()
    {
        // Test that increaseGauge can be called without throwing exceptions
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", 10));
    }

    @Test
    public void testIncreaseGaugeWithZeroValue()
    {
        // Test with zero value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", 0));
    }

    @Test
    public void testIncreaseGaugeWithNegativeValue()
    {
        // Test with negative value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", -50));
    }

    @Test
    public void testIncreaseGaugeWithPositiveValue()
    {
        // Test with positive value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", 123));
    }

    @Test
    public void testIncreaseGaugeWithMaxIntValue()
    {
        // Test with maximum int value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", Integer.MAX_VALUE));
    }

    @Test
    public void testIncreaseGaugeWithMinIntValue()
    {
        // Test with minimum int value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", Integer.MIN_VALUE));
    }

    @Test
    public void testIncreaseGaugeWithEmptyGaugeName()
    {
        // Test with empty string gauge name
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("", 50));
    }

    @Test
    public void testIncreaseGaugeWithNullGaugeName()
    {
        // Test with null gauge name
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge(null, 50));
    }

    @Test
    public void testIncreaseGaugeMultipleTimes()
    {
        // Test increasing the same gauge multiple times
        Assertions.assertDoesNotThrow(() ->
                {
            handler.increaseGauge("gauge1", 10);
            handler.increaseGauge("gauge1", 20);
            handler.increaseGauge("gauge1", 30);
        });
    }

    @Test
    public void testIncreaseGaugeWithDifferentGauges()
    {
        // Test increasing different gauges
        Assertions.assertDoesNotThrow(() ->
                {
            handler.increaseGauge("gauge1", 10);
            handler.increaseGauge("gauge2", 20);
            handler.increaseGauge("gauge3", 30);
        });
    }

    @Test
    public void testIncreaseGaugeWithSpecialCharactersInName()
    {
        // Test with special characters in gauge name
        Assertions.assertDoesNotThrow(() ->
                {
            handler.increaseGauge("gauge_with_underscore", 10);
            handler.increaseGauge("gauge-with-dash", 20);
            handler.increaseGauge("gauge.with.dot", 30);
            handler.increaseGauge("gauge:with:colon", 40);
        });
    }

    @Test
    public void testIncreaseGaugeWithLongGaugeName()
    {
        // Test with a very long gauge name
        String longName = "gauge_" + "a".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge(longName, 50));
    }

    @Test
    public void testIncreaseGaugeAfterRegisterGauge()
    {
        // Test that increaseGauge can be called after registerGauge
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge");
            handler.increaseGauge("gauge1", 100);
        });
    }

    @Test
    public void testIncreaseGaugeAfterSetGauge()
    {
        // Test that increaseGauge can be called after setGauge
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge1", 50.0);
            handler.increaseGauge("gauge1", 25);
        });
    }

    @Test
    public void testIncreaseGaugeWithOne()
    {
        // Test with value of 1 (common use case for incrementing)
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", 1));
    }

    @Test
    public void testIncreaseGaugeWithMinusOne()
    {
        // Test with value of -1 (common use case for decrementing)
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", -1));
    }

    @Test
    public void testIncreaseGaugeWithLargePositiveValue()
    {
        // Test with a large positive value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", 999999));
    }

    @Test
    public void testIncreaseGaugeWithLargeNegativeValue()
    {
        // Test with a large negative value
        Assertions.assertDoesNotThrow(() -> handler.increaseGauge("testGauge", -999999));
    }

    @Test
    public void testIncreaseGaugeMixedWithOtherGaugeOperations()
    {
        // Test that increaseGauge can be called alongside other gauge operations
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge");
            handler.setGauge("gauge1", 50.0);
            handler.increaseGauge("gauge1", 10);
            handler.setGauge("gauge1", 100.0);
            handler.increaseGauge("gauge1", -5);
        });
    }
}

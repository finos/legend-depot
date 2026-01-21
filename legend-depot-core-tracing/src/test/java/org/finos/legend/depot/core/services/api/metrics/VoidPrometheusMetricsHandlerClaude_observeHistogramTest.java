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

public class VoidPrometheusMetricsHandlerClaude_observeHistogramTest
{
    private VoidPrometheusMetricsHandler handler;

    @BeforeEach
    public void setUp()
    {
        handler = new VoidPrometheusMetricsHandler();
    }

    @Test
    public void testObserveHistogramWithValidParameters()
    {
        // Test that observeHistogram can be called without throwing exceptions
        long start = System.currentTimeMillis();
        long end = start + 1000;
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", start, end));
    }

    @Test
    public void testObserveHistogramWithZeroValues()
    {
        // Test with zero start and end times
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 0L));
    }

    @Test
    public void testObserveHistogramWithSameStartAndEnd()
    {
        // Test with identical start and end times
        long time = System.currentTimeMillis();
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", time, time));
    }

    @Test
    public void testObserveHistogramWithEndBeforeStart()
    {
        // Test with end time before start time (negative duration)
        long start = 1000L;
        long end = 500L;
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", start, end));
    }

    @Test
    public void testObserveHistogramWithNegativeValues()
    {
        // Test with negative start and end times
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", -1000L, -500L));
    }

    @Test
    public void testObserveHistogramWithMaxLongValues()
    {
        // Test with maximum long values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Long.MAX_VALUE, Long.MAX_VALUE));
    }

    @Test
    public void testObserveHistogramWithMinLongValues()
    {
        // Test with minimum long values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Long.MIN_VALUE, Long.MIN_VALUE));
    }

    @Test
    public void testObserveHistogramWithEmptyHistogramName()
    {
        // Test with empty string histogram name
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("", 0L, 1000L));
    }

    @Test
    public void testObserveHistogramWithNullHistogramName()
    {
        // Test with null histogram name
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram(null, 0L, 1000L));
    }

    @Test
    public void testObserveHistogramMultipleTimes()
    {
        // Test calling observeHistogram multiple times on the same histogram
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 0L, 100L);
            handler.observeHistogram("histogram1", 100L, 200L);
            handler.observeHistogram("histogram1", 200L, 300L);
        });
    }

    @Test
    public void testObserveHistogramWithDifferentHistograms()
    {
        // Test observing different histograms
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 0L, 100L);
            handler.observeHistogram("histogram2", 0L, 200L);
            handler.observeHistogram("histogram3", 0L, 300L);
        });
    }

    @Test
    public void testObserveHistogramWithSpecialCharactersInName()
    {
        // Test with special characters in histogram name
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram_with_underscore", 0L, 100L);
            handler.observeHistogram("histogram-with-dash", 0L, 100L);
            handler.observeHistogram("histogram.with.dot", 0L, 100L);
            handler.observeHistogram("histogram:with:colon", 0L, 100L);
        });
    }

    @Test
    public void testObserveHistogramWithLongHistogramName()
    {
        // Test with a very long histogram name
        String longName = "histogram_" + "a".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram(longName, 0L, 100L));
    }

    @Test
    public void testObserveHistogramWithLargeTimeDifference()
    {
        // Test with very large time difference
        long start = 0L;
        long end = Long.MAX_VALUE;
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", start, end));
    }

    @Test
    public void testObserveHistogramAfterRegisterHistogram()
    {
        // Test that observeHistogram can be called after registering a histogram
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("testHistogram", "Test histogram");
            handler.observeHistogram("testHistogram", 0L, 100L);
        });
    }

    @Test
    public void testObserveHistogramWithRealisticTimestamps()
    {
        // Test with realistic timestamp values
        long start = 1609459200000L; // January 1, 2021
        long end = 1640995200000L;   // January 1, 2022
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("annualHistogram", start, end));
    }

    @Test
    public void testObserveHistogramWithMixedPositiveAndNegative()
    {
        // Test with mixed positive and negative values
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", -100L, 100L);
            handler.observeHistogram("histogram2", 100L, -100L);
        });
    }

    @Test
    public void testObserveHistogramWithSmallTimestamps()
    {
        // Test with small timestamp values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 1L, 2L));
    }

    @Test
    public void testObserveHistogramWithNumericHistogramName()
    {
        // Test with numeric histogram name
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("12345", 0L, 100L));
    }

    // Tests for the overloaded observeHistogram method with String... labelValues parameter

    @Test
    public void testObserveHistogramWithLabelsValidParameters()
    {
        // Test that observeHistogram with label values can be called without throwing exceptions
        long start = System.currentTimeMillis();
        long end = start + 1000;
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", start, end, "value1", "value2"));
    }

    @Test
    public void testObserveHistogramWithLabelsNoLabels()
    {
        // Test with no label values (empty varargs)
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L));
    }

    @Test
    public void testObserveHistogramWithLabelsSingleLabel()
    {
        // Test with single label value
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "value1"));
    }

    @Test
    public void testObserveHistogramWithLabelsMultipleLabels()
    {
        // Test with multiple label values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "value1", "value2", "value3"));
    }

    @Test
    public void testObserveHistogramWithLabelsNullArray()
    {
        // Test with null varargs array
        String[] nullLabels = null;
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, nullLabels));
    }

    @Test
    public void testObserveHistogramWithLabelsContainingNull()
    {
        // Test with label values containing null
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "value1", null, "value3"));
    }

    @Test
    public void testObserveHistogramWithLabelsAllNull()
    {
        // Test with all label values as null
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, null, null, null));
    }

    @Test
    public void testObserveHistogramWithLabelsEmptyStrings()
    {
        // Test with empty string label values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "", "", ""));
    }

    @Test
    public void testObserveHistogramWithLabelsNullHistogramName()
    {
        // Test with null histogram name and labels
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram(null, 0L, 100L, "value1", "value2"));
    }

    @Test
    public void testObserveHistogramWithLabelsEmptyHistogramName()
    {
        // Test with empty histogram name and labels
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("", 0L, 100L, "value1", "value2"));
    }

    @Test
    public void testObserveHistogramWithLabelsMultipleTimes()
    {
        // Test calling observeHistogram with labels multiple times
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 0L, 100L, "value1", "value2");
            handler.observeHistogram("histogram1", 100L, 200L, "value3", "value4");
            handler.observeHistogram("histogram1", 200L, 300L, "value5", "value6");
        });
    }

    @Test
    public void testObserveHistogramWithLabelsDifferentLabelSets()
    {
        // Test with different label sets for the same histogram
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 0L, 100L, "value1", "value2");
            handler.observeHistogram("histogram1", 100L, 200L, "value3");
            handler.observeHistogram("histogram1", 200L, 300L);
        });
    }

    @Test
    public void testObserveHistogramWithLabelsZeroTimestamps()
    {
        // Test with zero timestamps and labels
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 0L, "value1", "value2"));
    }

    @Test
    public void testObserveHistogramWithLabelsNegativeTimestamps()
    {
        // Test with negative timestamps and labels
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", -1000L, -500L, "value1", "value2"));
    }

    @Test
    public void testObserveHistogramWithLabelsMaxLongValues()
    {
        // Test with maximum long values and labels
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Long.MAX_VALUE, Long.MAX_VALUE, "value1", "value2"));
    }

    @Test
    public void testObserveHistogramWithLabelsSpecialCharacters()
    {
        // Test with special characters in label values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "value_with_underscore", "value-with-dash", "value.with.dot"));
    }

    @Test
    public void testObserveHistogramWithLabelsLongLabelValues()
    {
        // Test with very long label values
        String longValue = "value_" + "a".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, longValue, "normalValue"));
    }

    @Test
    public void testObserveHistogramWithLabelsManyLabels()
    {
        // Test with many label values
        String[] manyLabels = new String[100];
        for (int i = 0; i < 100; i++)
        {
            manyLabels[i] = "value" + i;
        }
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, manyLabels));
    }

    @Test
    public void testObserveHistogramWithLabelsAfterRegisterHistogram()
    {
        // Test that observeHistogram with labels can be called after registering a histogram with labels
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Test histogram");
            handler.observeHistogram("histogram1", 0L, 100L, "value1", "value2");
        });
    }

    @Test
    public void testObserveHistogramMixedWithAndWithoutLabels()
    {
        // Test that both versions of observeHistogram can be used interchangeably
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 0L, 100L);
            handler.observeHistogram("histogram1", 100L, 200L, "value1", "value2");
            handler.observeHistogram("histogram1", 200L, 300L);
            handler.observeHistogram("histogram1", 300L, 400L, "value3");
        });
    }

    @Test
    public void testObserveHistogramWithLabelsUnicodeCharacters()
    {
        // Test with unicode characters in label values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "标签1", "étiquette2", "лейбл3", "🏷️"));
    }

    @Test
    public void testObserveHistogramWithLabelsWhitespaceOnly()
    {
        // Test with whitespace-only label values
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0L, 100L, "   ", "\t", "\n"));
    }

    // Tests for the overloaded observeHistogram method with double amount parameter

    @Test
    public void testObserveHistogramWithAmountValidParameters()
    {
        // Test that observeHistogram with amount can be called without throwing exceptions
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 100.5));
    }

    @Test
    public void testObserveHistogramWithAmountZero()
    {
        // Test with zero amount
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0.0));
    }

    @Test
    public void testObserveHistogramWithAmountNegative()
    {
        // Test with negative amount
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", -50.5));
    }

    @Test
    public void testObserveHistogramWithAmountPositive()
    {
        // Test with positive amount
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 123.456));
    }

    @Test
    public void testObserveHistogramWithAmountMaxDouble()
    {
        // Test with maximum double value
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Double.MAX_VALUE));
    }

    @Test
    public void testObserveHistogramWithAmountMinDouble()
    {
        // Test with minimum double value
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Double.MIN_VALUE));
    }

    @Test
    public void testObserveHistogramWithAmountPositiveInfinity()
    {
        // Test with positive infinity
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Double.POSITIVE_INFINITY));
    }

    @Test
    public void testObserveHistogramWithAmountNegativeInfinity()
    {
        // Test with negative infinity
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testObserveHistogramWithAmountNaN()
    {
        // Test with NaN (Not a Number)
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", Double.NaN));
    }

    @Test
    public void testObserveHistogramWithAmountEmptyHistogramName()
    {
        // Test with empty string histogram name
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("", 50.0));
    }

    @Test
    public void testObserveHistogramWithAmountNullHistogramName()
    {
        // Test with null histogram name
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram(null, 50.0));
    }

    @Test
    public void testObserveHistogramWithAmountMultipleTimes()
    {
        // Test calling observeHistogram with amount multiple times
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 10.0);
            handler.observeHistogram("histogram1", 20.0);
            handler.observeHistogram("histogram1", 30.0);
        });
    }

    @Test
    public void testObserveHistogramWithAmountDifferentHistograms()
    {
        // Test observing different histograms with amount
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 10.0);
            handler.observeHistogram("histogram2", 20.0);
            handler.observeHistogram("histogram3", 30.0);
        });
    }

    @Test
    public void testObserveHistogramWithAmountVerySmall()
    {
        // Test with very small amount
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 0.000000001));
    }

    @Test
    public void testObserveHistogramWithAmountVeryLarge()
    {
        // Test with very large amount
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", 999999999999.999));
    }

    @Test
    public void testObserveHistogramWithAmountAfterRegisterHistogram()
    {
        // Test that observeHistogram with amount can be called after registering a histogram
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Test histogram");
            handler.observeHistogram("histogram1", 50.5);
        });
    }

    @Test
    public void testObserveHistogramWithAmountSpecialCharactersInName()
    {
        // Test with special characters in histogram name
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram_with_underscore", 10.0);
            handler.observeHistogram("histogram-with-dash", 20.0);
            handler.observeHistogram("histogram.with.dot", 30.0);
            handler.observeHistogram("histogram:with:colon", 40.0);
        });
    }

    @Test
    public void testObserveHistogramWithAmountLongHistogramName()
    {
        // Test with very long histogram name
        String longName = "histogram_" + "a".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram(longName, 50.0));
    }

    @Test
    public void testObserveHistogramWithAmountNegativeZero()
    {
        // Test with negative zero
        Assertions.assertDoesNotThrow(() -> handler.observeHistogram("testHistogram", -0.0));
    }

    @Test
    public void testObserveHistogramMixedAllVersions()
    {
        // Test that all versions of observeHistogram can be used interchangeably
        Assertions.assertDoesNotThrow(() ->
                {
            handler.observeHistogram("histogram1", 0L, 100L);
            handler.observeHistogram("histogram1", 50.5);
            handler.observeHistogram("histogram1", 100L, 200L, "value1", "value2");
            handler.observeHistogram("histogram1", 75.25);
        });
    }
}

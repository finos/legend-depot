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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VoidPrometheusMetricsHandlerClaude_setGaugeTest
{
    private VoidPrometheusMetricsHandler handler;

    @BeforeEach
    public void setUp()
    {
        handler = new VoidPrometheusMetricsHandler();
    }

    @Test
    public void testSetGaugeWithValidParameters()
    {
        // Test that setGauge can be called without throwing exceptions
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 100.0));
    }

    @Test
    public void testSetGaugeWithZeroValue()
    {
        // Test with zero value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 0.0));
    }

    @Test
    public void testSetGaugeWithNegativeValue()
    {
        // Test with negative value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", -50.5));
    }

    @Test
    public void testSetGaugeWithPositiveValue()
    {
        // Test with positive value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 123.456));
    }

    @Test
    public void testSetGaugeWithMaxDoubleValue()
    {
        // Test with maximum double value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", Double.MAX_VALUE));
    }

    @Test
    public void testSetGaugeWithMinDoubleValue()
    {
        // Test with minimum double value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", Double.MIN_VALUE));
    }

    @Test
    public void testSetGaugeWithPositiveInfinity()
    {
        // Test with positive infinity
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", Double.POSITIVE_INFINITY));
    }

    @Test
    public void testSetGaugeWithNegativeInfinity()
    {
        // Test with negative infinity
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testSetGaugeWithNaN()
    {
        // Test with NaN (Not a Number)
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", Double.NaN));
    }

    @Test
    public void testSetGaugeWithEmptyGaugeName()
    {
        // Test with empty string gauge name
        Assertions.assertDoesNotThrow(() -> handler.setGauge("", 50.0));
    }

    @Test
    public void testSetGaugeWithNullGaugeName()
    {
        // Test with null gauge name
        Assertions.assertDoesNotThrow(() -> handler.setGauge(null, 50.0));
    }

    @Test
    public void testSetGaugeMultipleTimes()
    {
        // Test setting the same gauge multiple times
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge1", 10.0);
            handler.setGauge("gauge1", 20.0);
            handler.setGauge("gauge1", 30.0);
        });
    }

    @Test
    public void testSetGaugeWithDifferentGauges()
    {
        // Test setting different gauges
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge1", 10.0);
            handler.setGauge("gauge2", 20.0);
            handler.setGauge("gauge3", 30.0);
        });
    }

    @Test
    public void testSetGaugeWithSpecialCharactersInName()
    {
        // Test with special characters in gauge name
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge_with_underscore", 10.0);
            handler.setGauge("gauge-with-dash", 20.0);
            handler.setGauge("gauge.with.dot", 30.0);
            handler.setGauge("gauge:with:colon", 40.0);
        });
    }

    @Test
    public void testSetGaugeWithLongGaugeName()
    {
        // Test with a very long gauge name
        String longName = "gauge_" + "a".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.setGauge(longName, 50.0));
    }

    @Test
    public void testSetGaugeWithVerySmallValue()
    {
        // Test with very small value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 0.000000001));
    }

    @Test
    public void testSetGaugeWithVeryLargeValue()
    {
        // Test with very large value
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 999999999999.999));
    }

    @Test
    public void testSetGaugeAfterRegisterGauge()
    {
        // Test that setGauge can be called after registerGauge
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge");
            handler.setGauge("gauge1", 100.0);
        });
    }

    @Test
    public void testSetGaugeWithIntegerValue()
    {
        // Test with integer value (which should be auto-converted to double)
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 42.0));
    }

    @Test
    public void testSetGaugeWithNegativeZero()
    {
        // Test with negative zero
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", -0.0));
    }

    // Tests for the overloaded setGauge method with List<String> labelValues parameter

    @Test
    public void testSetGaugeWithLabelsValidParameters()
    {
        // Test that setGauge with labels can be called without throwing exceptions
        List<String> labels = Arrays.asList("label1", "label2");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 100.0, labels));
    }

    @Test
    public void testSetGaugeWithLabelsNullList()
    {
        // Test with null label values list
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, null));
    }

    @Test
    public void testSetGaugeWithLabelsEmptyList()
    {
        // Test with empty label values list
        List<String> emptyLabels = Collections.emptyList();
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, emptyLabels));
    }

    @Test
    public void testSetGaugeWithLabelsSingleLabel()
    {
        // Test with single label value
        List<String> singleLabel = Collections.singletonList("value1");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 75.0, singleLabel));
    }

    @Test
    public void testSetGaugeWithLabelsMultipleLabels()
    {
        // Test with multiple label values
        List<String> labels = Arrays.asList("value1", "value2", "value3");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 200.0, labels));
    }

    @Test
    public void testSetGaugeWithLabelsContainingNull()
    {
        // Test with list containing null values
        List<String> labelsWithNull = new ArrayList<>();
        labelsWithNull.add("value1");
        labelsWithNull.add(null);
        labelsWithNull.add("value3");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, labelsWithNull));
    }

    @Test
    public void testSetGaugeWithLabelsContainingEmptyStrings()
    {
        // Test with list containing empty strings
        List<String> labelsWithEmpty = Arrays.asList("value1", "", "value3");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, labelsWithEmpty));
    }

    @Test
    public void testSetGaugeWithLabelsAllNull()
    {
        // Test with list containing only null values
        List<String> allNullLabels = Arrays.asList(null, null, null);
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, allNullLabels));
    }

    @Test
    public void testSetGaugeWithLabelsNullGaugeName()
    {
        // Test with null gauge name and labels
        List<String> labels = Arrays.asList("label1", "label2");
        Assertions.assertDoesNotThrow(() -> handler.setGauge(null, 50.0, labels));
    }

    @Test
    public void testSetGaugeWithLabelsEmptyGaugeName()
    {
        // Test with empty gauge name and labels
        List<String> labels = Arrays.asList("label1", "label2");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("", 50.0, labels));
    }

    @Test
    public void testSetGaugeWithLabelsMultipleTimes()
    {
        // Test setting the same gauge with labels multiple times
        List<String> labels = Arrays.asList("label1", "label2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge1", 10.0, labels);
            handler.setGauge("gauge1", 20.0, labels);
            handler.setGauge("gauge1", 30.0, labels);
        });
    }

    @Test
    public void testSetGaugeWithLabelsDifferentLabelSets()
    {
        // Test with different label sets for the same gauge
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge1", 10.0, Arrays.asList("label1", "label2"));
            handler.setGauge("gauge1", 20.0, Arrays.asList("label3", "label4"));
            handler.setGauge("gauge1", 30.0, null);
        });
    }

    @Test
    public void testSetGaugeWithLabelsSpecialCharacters()
    {
        // Test with special characters in label values
        List<String> labels = Arrays.asList("label_with_underscore", "label-with-dash", "label.with.dot");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, labels));
    }

    @Test
    public void testSetGaugeWithLabelsLongLabelValues()
    {
        // Test with very long label values
        String longLabel = "label_" + "a".repeat(1000);
        List<String> labels = Arrays.asList(longLabel, "normalLabel");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, labels));
    }

    @Test
    public void testSetGaugeWithLabelsManyLabels()
    {
        // Test with many label values
        List<String> manyLabels = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            manyLabels.add("label" + i);
        }
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, manyLabels));
    }

    @Test
    public void testSetGaugeWithLabelsExtremeDoubleValues()
    {
        // Test with extreme double values and labels
        List<String> labels = Arrays.asList("label1", "label2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("testGauge", Double.MAX_VALUE, labels);
            handler.setGauge("testGauge", Double.MIN_VALUE, labels);
            handler.setGauge("testGauge", Double.POSITIVE_INFINITY, labels);
            handler.setGauge("testGauge", Double.NEGATIVE_INFINITY, labels);
            handler.setGauge("testGauge", Double.NaN, labels);
        });
    }

    @Test
    public void testSetGaugeWithLabelsAfterRegisterGauge()
    {
        // Test that setGauge with labels can be called after registerGauge with labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        List<String> labelValues = Arrays.asList("value1", "value2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge", labelNames);
            handler.setGauge("gauge1", 100.0, labelValues);
        });
    }

    @Test
    public void testSetGaugeMixedWithAndWithoutLabels()
    {
        // Test that both versions of setGauge can be used interchangeably
        List<String> labels = Arrays.asList("label1", "label2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.setGauge("gauge1", 10.0);
            handler.setGauge("gauge1", 20.0, labels);
            handler.setGauge("gauge1", 30.0);
            handler.setGauge("gauge1", 40.0, null);
        });
    }

    @Test
    public void testSetGaugeWithLabelsUnicodeCharacters()
    {
        // Test with unicode characters in label values
        List<String> labels = Arrays.asList("标签1", "étiquette2", "лейбл3", "🏷️");
        Assertions.assertDoesNotThrow(() -> handler.setGauge("testGauge", 50.0, labels));
    }
}

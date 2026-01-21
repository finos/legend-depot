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

public class VoidPrometheusMetricsHandlerClaude_registerGaugeTest
{
    private VoidPrometheusMetricsHandler handler;

    @BeforeEach
    public void setUp()
    {
        handler = new VoidPrometheusMetricsHandler();
    }

    @Test
    public void testRegisterGaugeWithValidParameters()
    {
        // Test that registerGauge can be called without throwing exceptions
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Test gauge help message"));
    }

    @Test
    public void testRegisterGaugeWithEmptyGaugeName()
    {
        // Test with empty string gauge name
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("", "Help message"));
    }

    @Test
    public void testRegisterGaugeWithEmptyHelpMessage()
    {
        // Test with empty string help message
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", ""));
    }

    @Test
    public void testRegisterGaugeWithBothEmpty()
    {
        // Test with both parameters as empty strings
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("", ""));
    }

    @Test
    public void testRegisterGaugeWithNullGaugeName()
    {
        // Test with null gauge name
        Assertions.assertDoesNotThrow(() -> handler.registerGauge(null, "Help message"));
    }

    @Test
    public void testRegisterGaugeWithNullHelpMessage()
    {
        // Test with null help message
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", null));
    }

    @Test
    public void testRegisterGaugeWithBothNull()
    {
        // Test with both parameters as null
        Assertions.assertDoesNotThrow(() -> handler.registerGauge(null, null));
    }

    @Test
    public void testRegisterGaugeMultipleTimes()
    {
        // Test registering the same gauge multiple times
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "First registration");
            handler.registerGauge("gauge1", "Second registration");
            handler.registerGauge("gauge1", "Third registration");
        });
    }

    @Test
    public void testRegisterGaugeWithDifferentGauges()
    {
        // Test registering different gauges
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Gauge 1 help");
            handler.registerGauge("gauge2", "Gauge 2 help");
            handler.registerGauge("gauge3", "Gauge 3 help");
        });
    }

    @Test
    public void testRegisterGaugeWithSpecialCharacters()
    {
        // Test with special characters in both parameters
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge_with_underscore", "Help with underscore_here");
            handler.registerGauge("gauge-with-dash", "Help with dash-here");
            handler.registerGauge("gauge.with.dot", "Help with dot.here");
            handler.registerGauge("gauge:with:colon", "Help with colon:here");
        });
    }

    @Test
    public void testRegisterGaugeWithLongParameters()
    {
        // Test with very long parameter values
        String longGaugeName = "gauge_" + "a".repeat(1000);
        String longHelpMessage = "Help message " + "b".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.registerGauge(longGaugeName, longHelpMessage));
    }

    @Test
    public void testRegisterGaugeWithNumericValues()
    {
        // Test with numeric values
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("12345", "67890"));
    }

    @Test
    public void testRegisterGaugeFollowedBySetGauge()
    {
        // Test that gauge can be registered and then set
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge");
            handler.setGauge("gauge1", 100.0);
        });
    }

    @Test
    public void testRegisterGaugeFollowedByIncreaseGauge()
    {
        // Test that gauge can be registered and then increased
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge");
            handler.increaseGauge("gauge1", 10);
        });
    }

    @Test
    public void testRegisterGaugeWithMultilineHelpMessage()
    {
        // Test with multiline help message
        String multilineHelp = "Line 1\nLine 2\nLine 3";
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", multilineHelp));
    }

    @Test
    public void testRegisterGaugeWithUnicodeCharacters()
    {
        // Test with unicode characters
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("gauge_测试", "帮助消息 📏"));
    }

    @Test
    public void testRegisterGaugeWithWhitespaceOnly()
    {
        // Test with whitespace-only strings
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("   ", "   ");
            handler.registerGauge("\t", "\n");
        });
    }

    @Test
    public void testRegisterGaugeMixedWithOtherRegistrations()
    {
        // Test that registerGauge can be called alongside other register methods
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerCounter("counter1", "Counter help");
            handler.registerGauge("gauge1", "Gauge help");
            handler.registerSummary("summary1", "Summary help");
            handler.registerGauge("gauge2", "Gauge help 2");
        });
    }

    // Tests for the overloaded registerGauge method with List<String> labelNames parameter

    @Test
    public void testRegisterGaugeWithLabelsValidParameters()
    {
        // Test that registerGauge with label names can be called without throwing exceptions
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Test gauge help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsNullList()
    {
        // Test with null label names list
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", null));
    }

    @Test
    public void testRegisterGaugeWithLabelsEmptyList()
    {
        // Test with empty label names list
        List<String> emptyLabels = Collections.emptyList();
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", emptyLabels));
    }

    @Test
    public void testRegisterGaugeWithLabelsSingleLabel()
    {
        // Test with single label name
        List<String> singleLabel = Collections.singletonList("labelName1");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", singleLabel));
    }

    @Test
    public void testRegisterGaugeWithLabelsMultipleLabels()
    {
        // Test with multiple label names
        List<String> labelNames = Arrays.asList("labelName1", "labelName2", "labelName3");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsContainingNull()
    {
        // Test with list containing null values
        List<String> labelsWithNull = new ArrayList<>();
        labelsWithNull.add("labelName1");
        labelsWithNull.add(null);
        labelsWithNull.add("labelName3");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelsWithNull));
    }

    @Test
    public void testRegisterGaugeWithLabelsContainingEmptyStrings()
    {
        // Test with list containing empty strings
        List<String> labelsWithEmpty = Arrays.asList("labelName1", "", "labelName3");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelsWithEmpty));
    }

    @Test
    public void testRegisterGaugeWithLabelsAllNullInList()
    {
        // Test with list containing only null values
        List<String> allNullLabels = Arrays.asList(null, null, null);
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", allNullLabels));
    }

    @Test
    public void testRegisterGaugeWithLabelsNullGaugeName()
    {
        // Test with null gauge name and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge(null, "Help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsNullHelpMessage()
    {
        // Test with null help message and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", null, labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsAllParametersNull()
    {
        // Test with all parameters null
        Assertions.assertDoesNotThrow(() -> handler.registerGauge(null, null, null));
    }

    @Test
    public void testRegisterGaugeWithLabelsEmptyGaugeName()
    {
        // Test with empty gauge name and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("", "Help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsEmptyHelpMessage()
    {
        // Test with empty help message and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsMultipleTimes()
    {
        // Test registering the same gauge with labels multiple times
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "First registration", labelNames);
            handler.registerGauge("gauge1", "Second registration", labelNames);
            handler.registerGauge("gauge1", "Third registration", labelNames);
        });
    }

    @Test
    public void testRegisterGaugeWithLabelsDifferentLabelSets()
    {
        // Test with different label sets for the same gauge
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Help", Arrays.asList("label1", "label2"));
            handler.registerGauge("gauge1", "Help", Arrays.asList("label3", "label4", "label5"));
            handler.registerGauge("gauge1", "Help", null);
        });
    }

    @Test
    public void testRegisterGaugeWithLabelsSpecialCharacters()
    {
        // Test with special characters in label names
        List<String> labelNames = Arrays.asList("label_with_underscore", "label-with-dash", "label.with.dot");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsLongLabelNames()
    {
        // Test with very long label names
        String longLabel = "labelName_" + "a".repeat(1000);
        List<String> labelNames = Arrays.asList(longLabel, "normalLabel");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsManyLabels()
    {
        // Test with many label names
        List<String> manyLabels = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            manyLabels.add("labelName" + i);
        }
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", manyLabels));
    }

    @Test
    public void testRegisterGaugeWithLabelsFollowedBySetGauge()
    {
        // Test that gauge with labels can be registered and then set
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        List<String> labelValues = Arrays.asList("value1", "value2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Test gauge", labelNames);
            handler.setGauge("gauge1", 100.0, labelValues);
        });
    }

    @Test
    public void testRegisterGaugeMixedWithAndWithoutLabels()
    {
        // Test that both versions of registerGauge can be used interchangeably
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerGauge("gauge1", "Help");
            handler.registerGauge("gauge2", "Help", labelNames);
            handler.registerGauge("gauge3", "Help");
            handler.registerGauge("gauge4", "Help", null);
        });
    }

    @Test
    public void testRegisterGaugeWithLabelsUnicodeCharacters()
    {
        // Test with unicode characters in label names
        List<String> labelNames = Arrays.asList("标签名1", "nom_étiquette2", "имя_метки3", "🏷️_name");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelNames));
    }

    @Test
    public void testRegisterGaugeWithLabelsWhitespaceOnly()
    {
        // Test with whitespace-only strings
        List<String> labelNames = Arrays.asList("   ", "\t", "\n");
        Assertions.assertDoesNotThrow(() -> handler.registerGauge("testGauge", "Help message", labelNames));
    }
}

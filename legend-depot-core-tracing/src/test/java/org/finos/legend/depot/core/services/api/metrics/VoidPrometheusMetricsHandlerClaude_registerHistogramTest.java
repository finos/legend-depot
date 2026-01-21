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

public class VoidPrometheusMetricsHandlerClaude_registerHistogramTest
{
    private VoidPrometheusMetricsHandler handler;

    @BeforeEach
    public void setUp()
    {
        handler = new VoidPrometheusMetricsHandler();
    }

    @Test
    public void testRegisterHistogramWithValidParameters()
    {
        // Test that registerHistogram can be called without throwing exceptions
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Test histogram help message"));
    }

    @Test
    public void testRegisterHistogramWithEmptyHistogramName()
    {
        // Test with empty string histogram name
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("", "Help message"));
    }

    @Test
    public void testRegisterHistogramWithEmptyHelpMessage()
    {
        // Test with empty string help message
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", ""));
    }

    @Test
    public void testRegisterHistogramWithBothEmpty()
    {
        // Test with both parameters as empty strings
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("", ""));
    }

    @Test
    public void testRegisterHistogramWithNullHistogramName()
    {
        // Test with null histogram name
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram(null, "Help message"));
    }

    @Test
    public void testRegisterHistogramWithNullHelpMessage()
    {
        // Test with null help message
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", null));
    }

    @Test
    public void testRegisterHistogramWithBothNull()
    {
        // Test with both parameters as null
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram(null, null));
    }

    @Test
    public void testRegisterHistogramMultipleTimes()
    {
        // Test registering the same histogram multiple times
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "First registration");
            handler.registerHistogram("histogram1", "Second registration");
            handler.registerHistogram("histogram1", "Third registration");
        });
    }

    @Test
    public void testRegisterHistogramWithDifferentHistograms()
    {
        // Test registering different histograms
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Histogram 1 help");
            handler.registerHistogram("histogram2", "Histogram 2 help");
            handler.registerHistogram("histogram3", "Histogram 3 help");
        });
    }

    @Test
    public void testRegisterHistogramWithSpecialCharacters()
    {
        // Test with special characters in both parameters
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram_with_underscore", "Help with underscore_here");
            handler.registerHistogram("histogram-with-dash", "Help with dash-here");
            handler.registerHistogram("histogram.with.dot", "Help with dot.here");
            handler.registerHistogram("histogram:with:colon", "Help with colon:here");
        });
    }

    @Test
    public void testRegisterHistogramWithLongParameters()
    {
        // Test with very long parameter values
        String longHistogramName = "histogram_" + "a".repeat(1000);
        String longHelpMessage = "Help message " + "b".repeat(1000);
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram(longHistogramName, longHelpMessage));
    }

    @Test
    public void testRegisterHistogramWithNumericValues()
    {
        // Test with numeric values
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("12345", "67890"));
    }

    @Test
    public void testRegisterHistogramFollowedByObserveHistogram()
    {
        // Test that histogram can be registered and then observed
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Test histogram");
            handler.observeHistogram("histogram1", 0L, 100L);
        });
    }

    @Test
    public void testRegisterHistogramFollowedByObserveHistogramWithAmount()
    {
        // Test that histogram can be registered and then observed with amount
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Test histogram");
            handler.observeHistogram("histogram1", 50.5);
        });
    }

    @Test
    public void testRegisterHistogramWithMultilineHelpMessage()
    {
        // Test with multiline help message
        String multilineHelp = "Line 1\nLine 2\nLine 3";
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", multilineHelp));
    }

    @Test
    public void testRegisterHistogramWithUnicodeCharacters()
    {
        // Test with unicode characters
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("histogram_测试", "帮助消息 📊"));
    }

    @Test
    public void testRegisterHistogramWithWhitespaceOnly()
    {
        // Test with whitespace-only strings
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("   ", "   ");
            handler.registerHistogram("\t", "\n");
        });
    }

    @Test
    public void testRegisterHistogramMixedWithOtherRegistrations()
    {
        // Test that registerHistogram can be called alongside other register methods
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerCounter("counter1", "Counter help");
            handler.registerHistogram("histogram1", "Histogram help");
            handler.registerSummary("summary1", "Summary help");
            handler.registerHistogram("histogram2", "Histogram help 2");
        });
    }

    // Tests for the overloaded registerHistogram method with List<String> labelNames parameter

    @Test
    public void testRegisterHistogramWithLabelsValidParameters()
    {
        // Test that registerHistogram with label names can be called without throwing exceptions
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Test histogram help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsNullList()
    {
        // Test with null label names list
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", null));
    }

    @Test
    public void testRegisterHistogramWithLabelsEmptyList()
    {
        // Test with empty label names list
        List<String> emptyLabels = Collections.emptyList();
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", emptyLabels));
    }

    @Test
    public void testRegisterHistogramWithLabelsSingleLabel()
    {
        // Test with single label name
        List<String> singleLabel = Collections.singletonList("labelName1");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", singleLabel));
    }

    @Test
    public void testRegisterHistogramWithLabelsMultipleLabels()
    {
        // Test with multiple label names
        List<String> labelNames = Arrays.asList("labelName1", "labelName2", "labelName3");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsContainingNull()
    {
        // Test with list containing null values
        List<String> labelsWithNull = new ArrayList<>();
        labelsWithNull.add("labelName1");
        labelsWithNull.add(null);
        labelsWithNull.add("labelName3");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelsWithNull));
    }

    @Test
    public void testRegisterHistogramWithLabelsContainingEmptyStrings()
    {
        // Test with list containing empty strings
        List<String> labelsWithEmpty = Arrays.asList("labelName1", "", "labelName3");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelsWithEmpty));
    }

    @Test
    public void testRegisterHistogramWithLabelsAllNullInList()
    {
        // Test with list containing only null values
        List<String> allNullLabels = Arrays.asList(null, null, null);
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", allNullLabels));
    }

    @Test
    public void testRegisterHistogramWithLabelsNullHistogramName()
    {
        // Test with null histogram name and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram(null, "Help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsNullHelpMessage()
    {
        // Test with null help message and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", null, labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsAllParametersNull()
    {
        // Test with all parameters null
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram(null, null, null));
    }

    @Test
    public void testRegisterHistogramWithLabelsEmptyHistogramName()
    {
        // Test with empty histogram name and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("", "Help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsEmptyHelpMessage()
    {
        // Test with empty help message and labels
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsMultipleTimes()
    {
        // Test registering the same histogram with labels multiple times
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "First registration", labelNames);
            handler.registerHistogram("histogram1", "Second registration", labelNames);
            handler.registerHistogram("histogram1", "Third registration", labelNames);
        });
    }

    @Test
    public void testRegisterHistogramWithLabelsDifferentLabelSets()
    {
        // Test with different label sets for the same histogram
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Help", Arrays.asList("label1", "label2"));
            handler.registerHistogram("histogram1", "Help", Arrays.asList("label3", "label4", "label5"));
            handler.registerHistogram("histogram1", "Help", null);
        });
    }

    @Test
    public void testRegisterHistogramWithLabelsSpecialCharacters()
    {
        // Test with special characters in label names
        List<String> labelNames = Arrays.asList("label_with_underscore", "label-with-dash", "label.with.dot");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsLongLabelNames()
    {
        // Test with very long label names
        String longLabel = "labelName_" + "a".repeat(1000);
        List<String> labelNames = Arrays.asList(longLabel, "normalLabel");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsManyLabels()
    {
        // Test with many label names
        List<String> manyLabels = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            manyLabels.add("labelName" + i);
        }
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", manyLabels));
    }

    @Test
    public void testRegisterHistogramWithLabelsFollowedByObserveHistogram()
    {
        // Test that histogram with labels can be registered and then observed
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Test histogram", labelNames);
            handler.observeHistogram("histogram1", 0L, 100L, "value1", "value2");
        });
    }

    @Test
    public void testRegisterHistogramMixedWithAndWithoutLabels()
    {
        // Test that both versions of registerHistogram can be used interchangeably
        List<String> labelNames = Arrays.asList("labelName1", "labelName2");
        Assertions.assertDoesNotThrow(() ->
                {
            handler.registerHistogram("histogram1", "Help");
            handler.registerHistogram("histogram2", "Help", labelNames);
            handler.registerHistogram("histogram3", "Help");
            handler.registerHistogram("histogram4", "Help", null);
        });
    }

    @Test
    public void testRegisterHistogramWithLabelsUnicodeCharacters()
    {
        // Test with unicode characters in label names
        List<String> labelNames = Arrays.asList("标签名1", "nom_étiquette2", "имя_метки3", "🏷️_name");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelNames));
    }

    @Test
    public void testRegisterHistogramWithLabelsWhitespaceOnly()
    {
        // Test with whitespace-only strings
        List<String> labelNames = Arrays.asList("   ", "\t", "\n");
        Assertions.assertDoesNotThrow(() -> handler.registerHistogram("testHistogram", "Help message", labelNames));
    }
}

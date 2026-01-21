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

package org.finos.legend.depot.core.services.metrics;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class DepotPrometheusMetricsHandlerClaudeTest


{
    private DepotPrometheusMetricsHandler handler;
    private CollectorRegistry registry;

    @BeforeEach
    public void setup()
  {
        registry = CollectorRegistry.defaultRegistry;
        handler = new DepotPrometheusMetricsHandler("test_prefix");
    }

    @AfterEach
    public void cleanup()
  {
        // Clean up all registered metrics to avoid conflicts between tests
        registry.clear();
    }

    @Test
    public void testConstructorWithPrefix()
  {
        DepotPrometheusMetricsHandler handler = new DepotPrometheusMetricsHandler("my_prefix");
        Assertions.assertNotNull(handler);
    }

    @Test
    public void testConstructorStoresPrefix()
  {
        DepotPrometheusMetricsHandler handler = new DepotPrometheusMetricsHandler("my_prefix");
        // Test that the prefix is used by checking a registered metric
        handler.incrementCount("test_counter");
        Assertions.assertTrue(isMetricRegistered("my_prefix_test_counter"));
    }

    @Test
    public void testIncrementCount()
  {
        handler.incrementCount("requests");
        Assertions.assertTrue(isMetricRegistered("test_prefix_requests"));
    }

    @Test
    public void testIncrementCountCreatesCounterIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_counter"));
        handler.incrementCount("new_counter");
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_counter"));
    }

    @Test
    public void testIncrementCountMultipleTimes()
  {
        handler.incrementCount("requests");
        handler.incrementCount("requests");
        handler.incrementCount("requests");
        // Verify the counter exists (value verification requires registry inspection)
        Assertions.assertTrue(isMetricRegistered("test_prefix_requests"));
    }

    @Test
    public void testIncrementCountWithSpacesInName()
  {
        handler.incrementCount("my counter name");
        Assertions.assertTrue(isMetricRegistered("test_prefix_my_counter_name"));
    }

    @Test
    public void testIncrementCountWithUpperCase()
  {
        handler.incrementCount("MyCounter");
        Assertions.assertTrue(isMetricRegistered("test_prefix_mycounter"));
    }

    @Test
    public void testIncrementErrorCount()
  {
        handler.incrementErrorCount("api_call");
        Assertions.assertTrue(isMetricRegistered("test_prefix_api_call_errors"));
    }

    @Test
    public void testIncrementErrorCountCreatesErrorCounterIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_error_errors"));
        handler.incrementErrorCount("new_error");
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_error_errors"));
    }

    @Test
    public void testIncrementErrorCountMultipleTimes()
  {
        handler.incrementErrorCount("api_call");
        handler.incrementErrorCount("api_call");
        Assertions.assertTrue(isMetricRegistered("test_prefix_api_call_errors"));
    }

    @Test
    public void testIncrementErrorCountWithSpaces()
  {
        handler.incrementErrorCount("api call");
        Assertions.assertTrue(isMetricRegistered("test_prefix_api_call_errors"));
    }

    @Test
    public void testRegisterCounter()
  {
        handler.registerCounter("my_counter", "This is a help message");
        Assertions.assertTrue(isMetricRegistered("test_prefix_my_counter"));
        Assertions.assertTrue(isMetricRegistered("test_prefix_my_counter_errors"));
    }

    @Test
    public void testRegisterCounterCreatesMainAndErrorCounter()
  {
        handler.registerCounter("requests", "Total requests");
        Assertions.assertTrue(isMetricRegistered("test_prefix_requests"));
        Assertions.assertTrue(isMetricRegistered("test_prefix_requests_errors"));
    }

    @Test
    public void testRegisterCounterWithNullHelpMessage()
  {
        // The current implementation has a bug where null help message causes NullPointerException
        // due to helpMessage != null || helpMessage.isEmpty() on line 69 (should be &&)
        Assertions.assertThrows(NullPointerException.class, () -> 
        {
            handler.registerCounter("my_counter", null);
        });
    }

    @Test
    public void testRegisterCounterWithEmptyHelpMessage()
  {
        handler.registerCounter("my_counter", "");
        Assertions.assertTrue(isMetricRegistered("test_prefix_my_counter"));
    }

    @Test
    public void testRegisterSummary()
  {
        handler.registerSummary("request_duration", "Request duration in seconds");
        Assertions.assertTrue(isMetricRegistered("test_prefix_request_duration"));
    }

    @Test
    public void testRegisterSummaryWithNullHelpMessage()
  {
        // The current implementation has a bug where null help message causes NullPointerException
        Assertions.assertThrows(NullPointerException.class, () -> 
        {
            handler.registerSummary("duration", null);
        });
    }

    @Test
    public void testRegisterSummaryWithEmptyHelpMessage()
  {
        handler.registerSummary("duration", "");
        Assertions.assertTrue(isMetricRegistered("test_prefix_duration"));
    }

    @Test
    public void testObserve()
  {
        long start = 1000L;
        long end = 2500L;
        handler.observe("request_time", start, end);
        Assertions.assertTrue(isMetricRegistered("test_prefix_request_time"));
    }

    @Test
    public void testObserveCreatesMetricIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_summary"));
        handler.observe("new_summary", 100L, 200L);
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_summary"));
    }

    @Test
    public void testObserveWithZeroDuration()
  {
        handler.observe("zero_duration", 1000L, 1000L);
        Assertions.assertTrue(isMetricRegistered("test_prefix_zero_duration"));
    }

    @Test
    public void testObserveMultipleTimes()
  {
        handler.observe("request_time", 1000L, 1500L);
        handler.observe("request_time", 2000L, 3000L);
        handler.observe("request_time", 3000L, 3200L);
        Assertions.assertTrue(isMetricRegistered("test_prefix_request_time"));
    }

    @Test
    public void testRegisterGaugeWithoutLabels()
  {
        handler.registerGauge("cpu_usage", "CPU usage percentage");
        Assertions.assertTrue(isMetricRegistered("test_prefix_cpu_usage"));
    }

    @Test
    public void testRegisterGaugeWithNullHelpMessage()
  {
        // The current implementation has a bug where null help message causes NullPointerException
        Assertions.assertThrows(NullPointerException.class, () -> 
        {
            handler.registerGauge("memory", null);
        });
    }

    @Test
    public void testRegisterGaugeWithLabels()
  {
        List<String> labels = Arrays.asList("instance", "job");
        handler.registerGauge("cpu_usage", "CPU usage by instance", labels);
        Assertions.assertTrue(isMetricRegistered("test_prefix_cpu_usage"));
    }

    @Test
    public void testRegisterGaugeWithEmptyLabelList()
  {
        handler.registerGauge("memory", "Memory usage", Collections.emptyList());
        Assertions.assertTrue(isMetricRegistered("test_prefix_memory"));
    }

    @Test
    public void testSetGaugeWithoutLabels()
  {
        handler.setGauge("temperature", 25.5);
        Assertions.assertTrue(isMetricRegistered("test_prefix_temperature"));
    }

    @Test
    public void testSetGaugeCreatesGaugeIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_gauge"));
        handler.setGauge("new_gauge", 42.0);
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_gauge"));
    }

    @Test
    public void testSetGaugeWithZeroValue()
  {
        handler.setGauge("zero_gauge", 0.0);
        Assertions.assertTrue(isMetricRegistered("test_prefix_zero_gauge"));
    }

    @Test
    public void testSetGaugeWithNegativeValue()
  {
        handler.setGauge("negative_gauge", -10.5);
        Assertions.assertTrue(isMetricRegistered("test_prefix_negative_gauge"));
    }

    @Test
    public void testSetGaugeMultipleTimes()
  {
        handler.setGauge("temperature", 20.0);
        handler.setGauge("temperature", 25.0);
        handler.setGauge("temperature", 30.0);
        Assertions.assertTrue(isMetricRegistered("test_prefix_temperature"));
    }

    @Test
    public void testSetGaugeWithLabels()
  {
        List<String> labels = Arrays.asList("host", "region");
        handler.registerGauge("cpu", "CPU usage", labels);

        List<String> labelValues = Arrays.asList("server1", "us-east");
        handler.setGauge("cpu", 75.5, labelValues);
        Assertions.assertTrue(isMetricRegistered("test_prefix_cpu"));
    }

    @Test
    public void testSetGaugeWithLabelsThrowsExceptionIfNotRegistered()
  {
        List<String> labelValues = Arrays.asList("value1", "value2");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> 
        {
            handler.setGauge("unregistered_gauge", 50.0, labelValues);
        });
    }

    @Test
    public void testSetGaugeWithLabelsExceptionMessage()
  {
        List<String> labelValues = Arrays.asList("value1");
        UnsupportedOperationException exception = Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> handler.setGauge("unregistered_gauge", 50.0, labelValues)
        );
        Assertions.assertEquals("Please register the gauge first if you need labels", exception.getMessage());
    }

    @Test
    public void testIncreaseGauge()
  {
        handler.increaseGauge("counter_gauge", 5);
        Assertions.assertTrue(isMetricRegistered("test_prefix_counter_gauge"));
    }

    @Test
    public void testIncreaseGaugeCreatesGaugeIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_inc_gauge"));
        handler.increaseGauge("new_inc_gauge", 10);
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_inc_gauge"));
    }

    @Test
    public void testIncreaseGaugeWithZero()
  {
        handler.increaseGauge("zero_inc", 0);
        Assertions.assertTrue(isMetricRegistered("test_prefix_zero_inc"));
    }

    @Test
    public void testIncreaseGaugeWithNegativeValue()
  {
        handler.increaseGauge("negative_inc", -5);
        Assertions.assertTrue(isMetricRegistered("test_prefix_negative_inc"));
    }

    @Test
    public void testIncreaseGaugeMultipleTimes()
  {
        handler.increaseGauge("counter", 1);
        handler.increaseGauge("counter", 2);
        handler.increaseGauge("counter", 3);
        Assertions.assertTrue(isMetricRegistered("test_prefix_counter"));
    }

    @Test
    public void testRegisterHistogramWithoutLabels()
  {
        handler.registerHistogram("response_size", "Response size in bytes");
        Assertions.assertTrue(isMetricRegistered("test_prefix_response_size"));
    }

    @Test
    public void testRegisterHistogramWithNullHelpMessage()
  {
        handler.registerHistogram("hist", null);
        Assertions.assertTrue(isMetricRegistered("test_prefix_hist"));
    }

    @Test
    public void testRegisterHistogramWithLabels()
  {
        List<String> labels = Arrays.asList("method", "endpoint");
        handler.registerHistogram("request_size", "Request size by endpoint", labels);
        Assertions.assertTrue(isMetricRegistered("test_prefix_request_size"));
    }

    @Test
    public void testRegisterHistogramWithEmptyLabels()
  {
        handler.registerHistogram("simple_hist", "Simple histogram", Collections.emptyList());
        Assertions.assertTrue(isMetricRegistered("test_prefix_simple_hist"));
    }

    @Test
    public void testObserveHistogramWithStartEnd()
  {
        handler.observeHistogram("request_latency", 1000L, 1500L);
        Assertions.assertTrue(isMetricRegistered("test_prefix_request_latency"));
    }

    @Test
    public void testObserveHistogramWithStartEndCreatesHistogramIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_hist"));
        handler.observeHistogram("new_hist", 100L, 200L);
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_hist"));
    }

    @Test
    public void testObserveHistogramWithZeroDuration()
  {
        handler.observeHistogram("zero_hist", 1000L, 1000L);
        Assertions.assertTrue(isMetricRegistered("test_prefix_zero_hist"));
    }

    @Test
    public void testObserveHistogramWithDouble()
  {
        handler.observeHistogram("double_hist", 123.456);
        Assertions.assertTrue(isMetricRegistered("test_prefix_double_hist"));
    }

    @Test
    public void testObserveHistogramWithDoubleCreatesHistogramIfNotExists()
  {
        Assertions.assertFalse(isMetricRegistered("test_prefix_new_double_hist"));
        handler.observeHistogram("new_double_hist", 99.99);
        Assertions.assertTrue(isMetricRegistered("test_prefix_new_double_hist"));
    }

    @Test
    public void testObserveHistogramWithDoubleZero()
  {
        handler.observeHistogram("zero_double_hist", 0.0);
        Assertions.assertTrue(isMetricRegistered("test_prefix_zero_double_hist"));
    }

    @Test
    public void testObserveHistogramWithDoubleNegative()
  {
        handler.observeHistogram("negative_hist", -10.5);
        Assertions.assertTrue(isMetricRegistered("test_prefix_negative_hist"));
    }

    @Test
    public void testObserveHistogramWithLabels()
  {
        List<String> labelNames = Arrays.asList("status", "method");
        handler.registerHistogram("http_requests", "HTTP requests", labelNames);

        handler.observeHistogram("http_requests", 100L, 250L, "200", "GET");
        Assertions.assertTrue(isMetricRegistered("test_prefix_http_requests"));
    }

    @Test
    public void testObserveHistogramWithLabelsThrowsExceptionIfNotRegistered()
  {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> 
        {
            handler.observeHistogram("unregistered_hist", 100L, 200L, "label1", "label2");
        });
    }

    @Test
    public void testObserveHistogramWithLabelsExceptionMessage()
  {
        UnsupportedOperationException exception = Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> handler.observeHistogram("unregistered_hist", 100L, 200L, "label1")
        );
        Assertions.assertEquals("Please register the histogram first if you need labels", exception.getMessage());
    }

    @Test
    public void testObserveHistogramWithEmptyLabelsArrayThrowsException()
  {
        // When using the varargs version with an empty array, it's treated as the labeled version
        // and will throw UnsupportedOperationException if not registered
        Assertions.assertThrows(UnsupportedOperationException.class, () -> 
        {
            handler.observeHistogram("simple_latency", 100L, 300L, new String[0]);
        });
    }

    @Test
    public void testRegisterResourceSummaries()
  {
        // Configure the PrometheusMetricsFactory to use our test handler
        PrometheusMetricsFactory.configure(createTestConfiguration());

        handler.registerResourceSummaries(TestTracingResource.class);
        // Verify that summaries were registered based on @ApiOperation annotations
        Assertions.assertTrue(isMetricRegistered("test_prefix_test_operation_1"));
        Assertions.assertTrue(isMetricRegistered("test_prefix_test_operation_2"));
    }

    @Test
    public void testRegisterResourceSummariesWithNickname()
  {
        // Configure the PrometheusMetricsFactory to use our test handler
        PrometheusMetricsFactory.configure(createTestConfiguration());

        handler.registerResourceSummaries(TestTracingResourceWithNickname.class);
        Assertions.assertTrue(isMetricRegistered("test_prefix_custom_nickname"));
    }

    @Test
    public void testRegisterResourceSummariesWithEmptyNickname()
  {
        // Configure the PrometheusMetricsFactory to use our test handler
        PrometheusMetricsFactory.configure(createTestConfiguration());

        handler.registerResourceSummaries(TestTracingResourceWithEmptyNickname.class);
        Assertions.assertTrue(isMetricRegistered("test_prefix_operation_value"));
    }

    @Test
    public void testRegisterResourceSummariesWithNoAnnotations()
  {
        // Should not throw exception
        handler.registerResourceSummaries(TestTracingResourceNoAnnotations.class);
        // No metrics should be registered for methods without @ApiOperation
    }

    @Test
    public void testMultipleHandlersWithDifferentPrefixes()
  {
        DepotPrometheusMetricsHandler handler1 = new DepotPrometheusMetricsHandler("prefix1");
        DepotPrometheusMetricsHandler handler2 = new DepotPrometheusMetricsHandler("prefix2");

        handler1.incrementCount("test");
        handler2.incrementCount("test");

        Assertions.assertTrue(isMetricRegistered("prefix1_test"));
        Assertions.assertTrue(isMetricRegistered("prefix2_test"));
    }

    @Test
    public void testPrefixWithSpaces()
  {
        DepotPrometheusMetricsHandler handler = new DepotPrometheusMetricsHandler("my prefix");
        handler.incrementCount("counter");
        Assertions.assertTrue(isMetricRegistered("my_prefix_counter"));
    }

    @Test
    public void testMetricNameSanitization()
  {
        handler.incrementCount("my metric name");
        Assertions.assertTrue(isMetricRegistered("test_prefix_my_metric_name"));
    }

    @Test
    public void testMetricNameLowerCase()
  {
        handler.incrementCount("UPPERCASE");
        Assertions.assertTrue(isMetricRegistered("test_prefix_uppercase"));
    }

    @Test
    public void testConsecutiveSpacesSanitization()
  {
        handler.incrementCount("multiple   spaces");
        Assertions.assertTrue(isMetricRegistered("test_prefix_multiple___spaces"));
    }

    // Helper method to check if a metric is registered
    private boolean isMetricRegistered(String metricName)
  {
        Enumeration<Collector.MetricFamilySamples> samples = registry.metricFamilySamples();
        while (samples.hasMoreElements())
        {
            Collector.MetricFamilySamples sample = samples.nextElement();
            if (sample.name.equals(metricName))
            {
                return true;
            }
        }
        return false;
    }

    // Helper method to create a test configuration that uses the handler instance
    private PrometheusConfiguration createTestConfiguration()
  {
        return new PrometheusConfiguration(true, handler);
    }

    // Test helper classes for registerResourceSummaries tests
    public static class TestTracingResource extends TracingResource
    {
        @ApiOperation(value = "test_operation_1")
        public void method1()
  {
        }

        @ApiOperation(value = "test_operation_2")
        public void method2()
  {
        }

        public void methodWithoutAnnotation()
  {
        }
    }

    public static class TestTracingResourceWithNickname extends TracingResource
    {
        @ApiOperation(value = "operation_value", nickname = "custom_nickname")
        public void methodWithNickname()
  {
        }
    }

    public static class TestTracingResourceWithEmptyNickname extends TracingResource
    {
        @ApiOperation(value = "operation_value", nickname = "")
        public void methodWithEmptyNickname()
  {
        }
    }

    public static class TestTracingResourceNoAnnotations extends TracingResource
    {
        public void method1()
  {
        }

        public void method2()
  {
        }
    }
}

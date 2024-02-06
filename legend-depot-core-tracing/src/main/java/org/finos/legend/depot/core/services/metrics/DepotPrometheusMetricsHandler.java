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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DepotPrometheusMetricsHandler implements  PrometheusMetricsHandler
{
    private static final String METRIC_SEPARATOR = "_";
    private static final String ERRORS = "_errors";
    private static final String ERRORS_HELP = " errors";
    private static final String DURATION = " duration";
    private static final String GAUGE = " gauge";
    private static final String HISTOGRAM = " histogram";
    private static final String WHITE_SPACE = "\\s";

    final ConcurrentHashMap<String, Counter> allCounters = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, Counter> allErrorCounters = new ConcurrentHashMap();
    final ConcurrentHashMap<String, Summary> allSummaries = new ConcurrentHashMap();
    final ConcurrentHashMap<String, Gauge> allGauges = new ConcurrentHashMap();
    final ConcurrentHashMap<String, Histogram> allHistograms = new ConcurrentHashMap();

    @JsonProperty
    @NotNull
    private final String prefix;

    @JsonCreator
    public DepotPrometheusMetricsHandler(@JsonProperty("prefix")String prefix)
    {
        this.prefix = prefix;
    }

    private String getKeyName(String name)
    {
        return sanitise(new StringBuffer().append(this.prefix).append(METRIC_SEPARATOR).append(StringUtils.lowerCase(name)).toString());
    }

    private String getHelpMessage(String metricName, String helpMessage)
    {
        return helpMessage != null || helpMessage.isEmpty() ? metricName : helpMessage;
    }

    private String sanitise(String name)
    {
        return name.replaceAll(WHITE_SPACE, METRIC_SEPARATOR);
    }

    private String buildErrorCounterName(String counterName)
    {
        return getKeyName(counterName) + ERRORS;
    }

    private Counter buildCounter(String key, String helpMessage)
    {
        return Counter.build(key,getHelpMessage(key,helpMessage)).register();
    }

    private  Summary buildSummary(String name, String helpMessage)
    {
        return Summary.build(getKeyName(name),getHelpMessage(name, helpMessage)).quantile(0.5D, 0.05D).quantile(0.9D, 0.01D).quantile(0.99D, 0.001D).register();
    }

    private Gauge buildGauge(String name, String helpMessage, List<String> labelNames)
    {
        Gauge.Builder builder = Gauge.build(getKeyName(name),getHelpMessage(name, helpMessage));
        if (!labelNames.isEmpty())
        {
            builder.labelNames(labelNames.toArray(new String[0]));
        }
        return builder.register();
    }

    private Histogram buildHistogram(String name, String helpMessage, List<String> labelNames)
    {
        Histogram.Builder builder = Histogram.build(getKeyName(name),getHelpMessage(name, helpMessage));
        if (!labelNames.isEmpty())
        {
            builder.labelNames(labelNames.toArray(new String[0]));
        }
        return builder.register();
    }


    @Override
    public void incrementCount(String counter)
    {
        allCounters.getIfAbsentPutWithKey(getKeyName(counter),(key) -> buildCounter(getKeyName(counter),counter)).inc();
    }

    @Override
    public void incrementErrorCount(String counter)
    {
        allErrorCounters.getIfAbsentPutWithKey(buildErrorCounterName(counter), (key) -> buildCounter(buildErrorCounterName(counter),counter + ERRORS_HELP)).inc();
    }

    @Override
    public void registerCounter(String counterName, String helpMessage)
    {
        allCounters.getIfAbsentPutWithKey(getKeyName(counterName),(key) -> buildCounter(getKeyName(counterName),helpMessage));
        allErrorCounters.getIfAbsentPutWithKey(buildErrorCounterName(counterName),(key) -> buildCounter(buildErrorCounterName(counterName),helpMessage + ERRORS_HELP));
    }

    @Override
    public void registerSummary(String summaryName,String helpMessage)
    {
        this.allSummaries.getIfAbsentPutWithKey(getKeyName(summaryName),(key) -> buildSummary(summaryName, getHelpMessage(summaryName,helpMessage)));
    }

    @Override
    public void observe(String summaryName, long start, long end)
    {
        this.allSummaries.getIfAbsentPutWithKey(getKeyName(summaryName),(key) -> buildSummary(summaryName, summaryName + DURATION)).observe((end - start) / 1000f);
    }



    @Override
    public void observeHistogram(String name, long start, long end, String... labelValues)
    {
        if (this.allHistograms.get(getKeyName(name)) == null)
        {
            throw new UnsupportedOperationException("Please register the histogram first if you need labels");
        }
        this.allHistograms.get(getKeyName(name)).labels(labelValues).observe(end - start);
    }


    @Override
    public void registerGauge(String gaugeName,String helpMessage)
    {
        this.allGauges.getIfAbsentPutWithKey(getKeyName(gaugeName),(key) -> buildGauge(gaugeName,helpMessage, Collections.emptyList()));
    }


    @Override
    public void registerGauge(String gaugeName, String help, List<String> labelNames)
    {
        this.allGauges.getIfAbsentPutWithKey(getKeyName(gaugeName),(key) -> buildGauge(gaugeName,help,labelNames));
    }

    @Override
    public void setGauge(String gaugeName, double value)
    {
        this.allGauges.getIfAbsentPutWithKey(getKeyName(gaugeName),(key) -> buildGauge(gaugeName,gaugeName + GAUGE,Collections.emptyList())).set(value);
    }

    @Override
    public void setGauge(String gaugeName, double value, List<String> labelValues)
    {
        if (this.allGauges.get(getKeyName(gaugeName)) == null)
        {
            throw new UnsupportedOperationException("Please register the gauge first if you need labels");
        }
        this.allGauges.get(getKeyName(gaugeName)).labels(labelValues.toArray(new String[0])).set(value);
    }

    @Override
    public void increaseGauge(String gaugeName, int value)
    {
        this.allGauges.getIfAbsentPutWithKey(getKeyName(gaugeName),(key) -> buildGauge(gaugeName,gaugeName + GAUGE,Collections.emptyList())).inc(value);
    }

    @Override
    public void registerHistogram(String name, String helpMessage)
    {
       this.allHistograms.getIfAbsentPutWithKey(getKeyName(name),(key) -> buildHistogram(name,name + HISTOGRAM,Collections.emptyList()));
    }

    @Override
    public void registerHistogram(String name, String helpMessage, List<String> labelNames)
    {
        this.allHistograms.getIfAbsentPutWithKey(getKeyName(name),(key) -> buildHistogram(name,name + HISTOGRAM,labelNames));
    }

    @Override
    public void observeHistogram(String name, long start, long end)
    {
       this.allHistograms.getIfAbsentPutWithKey(getKeyName(name),(key) -> buildHistogram(name,name + HISTOGRAM,Collections.emptyList())).observe(end - start);
    }

    @Override
    public void observeHistogram(String name, double amount)
    {
        this.allHistograms.getIfAbsentPutWithKey(getKeyName(name),(key) -> buildHistogram(name,name + HISTOGRAM,Collections.emptyList())).observe(amount);
    }

    @Override
    public void registerResourceSummaries(Class<? extends TracingResource> baseResource)
    {
            Arrays.stream(baseResource.getMethods()).forEach(m ->
            {
                if (m.isAnnotationPresent(ApiOperation.class))
                {
                    ApiOperation val = m.getAnnotation(ApiOperation.class);
                    String metricName = (val.nickname() != null && !val.nickname().isEmpty() ? val.nickname() : val.value());
                    PrometheusMetricsFactory.getInstance().registerSummary(metricName, metricName);
                }
            });
    }
}

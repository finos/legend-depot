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

package org.finos.legend.depot.store.metrics.services;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryMetric;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryMetricsHandler
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(QueryMetricsHandler.class);
    private final QueryMetricsStore metricsStore;

    @Inject
    public QueryMetricsHandler(QueryMetricsStore metricsStore)
    {
        this.metricsStore = metricsStore;
    }

    public Optional<VersionQueryMetric> getSummary(String groupId, String artifactId, String versionId)
    {
        List<VersionQueryMetric> queryCounters = metricsStore.get(groupId, artifactId, versionId);
        if (queryCounters.isEmpty())
        {
            return Optional.empty();
        }
        Optional<VersionQueryMetric> latest = queryCounters.stream().max(Comparator.comparing(VersionQueryMetric::getLastQueryTime));
        return latest;
    }


    public List<VersionQueryMetric> getSummaryByProjectVersion()
    {
        Stream<VersionQueryMetric> all = metricsStore.getAll().stream()
                .collect(Collectors.groupingBy(versionQueryMetric -> new ProjectVersion(versionQueryMetric.getGroupId(), versionQueryMetric.getArtifactId(), versionQueryMetric.getVersionId()),
                         Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(VersionQueryMetric::getLastQueryTime)), Optional::get))).values().stream();
        return all.collect(Collectors.toList());
    }

    public void consolidateMetrics()
    {
        List<VersionQueryMetric> versionQueryMetric = getSummaryByProjectVersion();
        LOGGER.info("Started consolidating metrics for all project versions");
        versionQueryMetric.forEach(metric ->
        {
            try
            {
                long deletedResult = metricsStore.consolidate(metric);
                LOGGER.info(String.format("Deleted [%s] records for project version: %s-%s-%s", deletedResult, metric.getGroupId(), metric.getArtifactId(), metric.getVersionId()));
            }
            catch (Exception e)
            {
                LOGGER.error(String.format("Error consolidating metrics for %s-%s-%s with error: %s", metric.getGroupId(), metric.getArtifactId(), metric.getVersionId(), e.getMessage()));
            }
        });
        LOGGER.info("Completed consolidating metrics for all project version");
    }
}

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

package org.finos.legend.depot.services.metrics.query;

import com.google.inject.name.Named;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.store.api.metrics.query.QueryMetrics;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryMetricsServiceImpl implements QueryMetricsService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(QueryMetricsServiceImpl.class);
    private final QueryMetrics metricsStore;


    //TODO: will require different implementation for recording and consolidating query metrics for different stores

    @Inject
    public QueryMetricsServiceImpl(QueryMetrics metricsStore)
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
        return metricsStore.getAllStoredEntitiesCoordinates().parallelStream()
                .map(pv -> getSummary(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()).get())
                .collect(Collectors.toList());

    }

    public List<VersionQueryMetric> findMetricsForProjectCoordinates(String groupId, String artifactId)
    {
        return metricsStore.find(groupId, artifactId);
    }

    public List<VersionQueryMetric> findReleasedVersionMetricsBefore(Date date)
    {
        return metricsStore.findMetricsBefore(date).parallelStream().filter(metric -> !VersionValidator.isSnapshotVersion(metric.getVersionId())).collect(Collectors.toList());
    }

    public List<VersionQueryMetric> findSnapshotVersionMetricsBefore(Date date)
    {
        return metricsStore.findMetricsBefore(date).parallelStream().filter(metric -> VersionValidator.isSnapshotVersion(metric.getVersionId())).collect(Collectors.toList());
    }


    @Override
    public void persist(QueryMetricsRegistry registry)
    {
        Optional<VersionQueryMetric> versionQueryMetric = registry.findFirst();
        while (versionQueryMetric.isPresent())
        {
            metricsStore.insert(versionQueryMetric.get());
            versionQueryMetric = registry.findFirst();
        }

    }

    public void consolidateMetrics()
    {
        LOGGER.info("Started consolidating metrics for all project versions");
        metricsStore.getAllStoredEntitiesCoordinates().parallelStream().forEach(pv ->
        {
            try
            {
                VersionQueryMetric metric = getSummary(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()).get();
                long deletedResult = metricsStore.consolidate(metric);
                LOGGER.info(String.format("Deleted [%s] records for project version: %s-%s-%s", deletedResult, pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
            }
            catch (Exception e)
            {
                LOGGER.error(String.format("Error consolidating metrics for %s-%s-%s with error: %s", pv.getGroupId(), pv.getArtifactId(), pv.getVersionId(), e.getMessage()));
            }
        });
        LOGGER.info("Completed consolidating metrics for all project version");
    }
}

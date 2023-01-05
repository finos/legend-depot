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

import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryCounter;
import org.finos.legend.depot.store.metrics.domain.MetricKey;
import org.finos.legend.depot.store.metrics.domain.VersionQuerySummary;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryMetricsHandler
{
    public static final Comparator<VersionQueryCounter> MOST_RECENTLY_QUERIED = (o1, o2) -> o2.getLastQueryTime().compareTo(o1.getLastQueryTime());

    private final QueryMetricsStore metricsStore;

    @Inject
    public QueryMetricsHandler(QueryMetricsStore metricsStore)
    {
        this.metricsStore = metricsStore;
    }

    public void persistMetrics()
    {
        metricsStore.persistMetrics(QueryMetricsContainer.getAll());
        QueryMetricsContainer.flush();
    }


    public Optional<VersionQuerySummary> getSummary(String groupId, String artifactId, String versionId)
    {
        List<VersionQueryCounter> queryCounters = metricsStore.get(groupId, artifactId, versionId);
        if (queryCounters.isEmpty())
        {
            return Optional.empty();
        }
        Optional<VersionQueryCounter> latest = queryCounters.stream().max(MOST_RECENTLY_QUERIED);
        return latest.map(versionQueryCounter -> new VersionQuerySummary(groupId, artifactId, versionId, versionQueryCounter.getLastQueryTime(), queryCounters.size()));
    }


    public List<VersionQuerySummary> getSummaryByProjectVersion()
    {
        Map<MetricKey, VersionQuerySummary> all = new HashMap<>();
        metricsStore.getAll().forEach(m ->
        {
            MetricKey key = new MetricKey(m.getGroupId(), m.getArtifactId(), m.getVersionId());
            VersionQuerySummary inSummary = all.getOrDefault(key, new VersionQuerySummary(m.getGroupId(), m.getArtifactId(), m.getVersionId(), m.getLastQueryTime(), 0));
            inSummary.addToSummary(m);
            all.put(key, inSummary);
        });
        return all.values().stream().sorted(MOST_RECENTLY_QUERIED).collect(Collectors.toList());
    }
}

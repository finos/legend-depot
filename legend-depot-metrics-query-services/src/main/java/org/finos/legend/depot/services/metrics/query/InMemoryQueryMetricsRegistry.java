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

import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;

import jakarta.inject.Inject;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryQueryMetricsRegistry implements QueryMetricsRegistry
{
    private ConcurrentLinkedQueue<VersionQueryMetric> metrics;

    @Inject
    public InMemoryQueryMetricsRegistry()
    {
        this.metrics = new ConcurrentLinkedQueue<>();
    }

    public void record(String groupId, String artifactId, String versionId, Date date)
    {
        metrics.offer(new VersionQueryMetric(groupId, artifactId, versionId, date));
    }

    @Override
    public Optional<VersionQueryMetric> findFirst()
    {
        VersionQueryMetric versionQueryMetric = metrics.poll();
        return versionQueryMetric == null ? Optional.empty() : Optional.of(versionQueryMetric);
    }
}

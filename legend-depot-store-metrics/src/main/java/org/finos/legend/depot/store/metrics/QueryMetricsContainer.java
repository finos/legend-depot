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

package org.finos.legend.depot.store.metrics;

import org.finos.legend.depot.store.metrics.domain.VersionQueryCounter;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class QueryMetricsContainer
{

    private static final QueryMetricsContainer instance = new QueryMetricsContainer();
    List<VersionQueryCounter> metrics = new ArrayList<>();

    private QueryMetricsContainer()
    {
    }

    public static QueryMetricsContainer getInstance()
    {
        return instance;
    }

    public static List<VersionQueryCounter> getAll()
    {
        return getInstance().metrics;
    }

    public static List<VersionQueryCounter> getMetrics(String groupId, String artifactId, String versionId)
    {
        return getInstance().metrics.stream().filter(m -> m.getGroupId().equals(groupId) && m.getArtifactId().equals(artifactId) && m.getVersionId().equals(versionId)).collect(Collectors.toList());
    }

    public static void record(String groupId, String artifactid, String versionId)
    {
        getInstance().metrics.add(new VersionQueryCounter(groupId, artifactid, versionId));
    }

    public static void flush()
    {
        getInstance().metrics = new ArrayList<>();
    }
}

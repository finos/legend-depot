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

package org.finos.legend.depot.store.mongo.admin.metrics;

import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.tracing.services.prometheus.VoidPrometheusMetricsHandler;
import org.junit.Test;

public class TestStorageMetrics extends TestStoreMongo
{
    private final StorageMetricsHandler metricsHandler = new StorageMetricsHandler(new MongoAdminStore(mongoProvider),new VoidPrometheusMetricsHandler());

    @Test
    public void canGetDbMetrics()
    {
        metricsHandler.reportMetrics();
    }
}


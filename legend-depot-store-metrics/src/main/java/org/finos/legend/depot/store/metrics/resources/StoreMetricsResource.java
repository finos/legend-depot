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

package org.finos.legend.depot.store.metrics.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.store.metrics.api.ManageQueryMetrics;
import org.finos.legend.depot.store.metrics.domain.VersionQuerySummary;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("")
@Api("Metrics")
public class StoreMetricsResource extends BaseResource
{

    private final ManageQueryMetrics queryMetrics;

    @Inject
    public StoreMetricsResource(ManageQueryMetrics queryMetrics)
    {
        this.queryMetrics = queryMetrics;
    }

    @GET
    @Path("/metrics/lastQuery")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSIONS_BY_LAST_USED)
    @Produces(MediaType.APPLICATION_JSON)
    public List<VersionQuerySummary> getVersionsByLastUsed()
    {
        return handle(ResourceLoggingAndTracing.GET_VERSIONS_BY_LAST_USED, this.queryMetrics::getSummaryByProjectVersion);
    }
}

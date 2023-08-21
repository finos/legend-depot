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

package org.finos.legend.depot.store.resources.metrics;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.metrics.VersionQueryMetric;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

@Path("")
@Api("Metrics")
public class ManageMetricsResourceResource extends BaseAuthorisedResource
{
    private final QueryMetricsHandler queryMetrics;

    @Inject
    protected ManageMetricsResourceResource(AuthorisationProvider authorisationProvider,
                                            @Named("requestPrincipal") Provider<Principal> principalProvider, QueryMetricsHandler queryMetrics)
    {
        super(authorisationProvider, principalProvider);
        this.queryMetrics = queryMetrics;
    }

    @Override
    protected String getResourceName()
    {
        return "Metrics";
    }

    @GET
    @Path("/metrics/lastQuery")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSIONS_BY_LAST_USED)
    @Produces(MediaType.APPLICATION_JSON)
    public List<VersionQueryMetric> getVersionsByLastUsed()
    {
        return handle(ResourceLoggingAndTracing.GET_VERSIONS_BY_LAST_USED, () -> this.queryMetrics.getSummaryByProjectVersion());
    }

}

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
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.store.metrics.store.api.QueryMetrics;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.security.Principal;
import java.util.List;

@Path("")
@Api("Metrics")
public class StoreMetricsManagerResource extends BaseAuthorisedResource
{
    private final QueryMetrics metricsStore;

    @Inject
    protected StoreMetricsManagerResource(QueryMetrics metricsStore, AuthorisationProvider authorisationProvider,
                                       @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.metricsStore = metricsStore;
    }

    @Override
    protected String getResourceName()
    {
        return "Metrics";
    }

    @PUT
    @Path("/metrics/indexes")
    @ApiOperation("createIndexes if absent")
    public List<String> createIndexesIfAbsent()
    {
        validateUser();
        return handle("Create indexes", this::createIndexes);
    }

    private List<String> createIndexes()
    {
        return metricsStore.createIndexes();
    }
}

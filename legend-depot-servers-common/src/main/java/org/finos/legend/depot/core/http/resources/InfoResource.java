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

package org.finos.legend.depot.core.http.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.http.ServersConfiguration;
import org.finos.legend.depot.services.serverInfo.InfoService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api("Info")
@Path("")
public class InfoResource
{
    private final InfoService infoService;
    private final ServersConfiguration configuration;

    @Inject
    public InfoResource(InfoService infoService, ServersConfiguration configuration)
    {
        this.infoService = infoService;
        this.configuration = configuration;
    }

    @GET
    @Path("/info")
    @Produces({"application/json"})
    @ApiOperation("Provides server information")
    public InfoService.ServerInfo getServerInfo()
    {
        return this.infoService.getServerInfo();
    }


    @GET
    @Path("/config")
    @ApiOperation("Provides server config")
    public String getServerConfig() throws JsonProcessingException
    {
        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).setSerializationInclusion(JsonInclude.Include.NON_EMPTY).writeValueAsString(configuration);
    }
}

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

package org.finos.legend.depot.core.server.resources.info;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.jackson.Jackson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.server.ServerConfiguration;
import org.finos.legend.depot.core.server.info.InfoService;
import org.pac4j.core.config.Config;
import org.slf4j.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Tag(name = "Info")
@Path("")
public class InfoResource
{
    // Dropwizard's mapper handles the Optional getters on Configuration
    private static final ObjectMapper CONFIG_MAPPER = Jackson.newObjectMapper()
            .addMixIn(Logger.class, IgnoredType.class)
            .addMixIn(Config.class, IgnoredType.class)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    private final InfoService infoService;
    private final ServerConfiguration configuration;

    @Inject
    public InfoResource(InfoService infoService, ServerConfiguration configuration)
    {
        this.infoService = infoService;
        this.configuration = configuration;
    }

    @GET
    @Path("/info")
    @Produces({"application/json"})
    @Operation(summary = "Provides server information")
    public InfoService.ServerInfo getServerInfo()
    {
        return this.infoService.getServerInfo();
    }


    @GET
    @Path("/config")
    @Operation(summary = "Provides server config")
    public String getServerConfig() throws JsonProcessingException
    {
        return CONFIG_MAPPER.writeValueAsString(configuration);
    }

    // pac4j clients expose their Logger, and once the server is running each client refers back
    // to the pac4j Config that holds it (client -> config -> clients -> client)
    @JsonIgnoreType
    private abstract static class IgnoredType
    {
    }
}

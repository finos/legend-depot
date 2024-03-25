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

package org.finos.legend.depot.server.resources.generations;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.generations.FileGenerationsService;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.finos.legend.depot.services.api.EtagBuilder;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_BY_FILEPATH;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_CONTENT;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_ENTITIES;

@Path("")
@Api("Generations")
public class FileGenerationsResource extends TracingResource
{

    private final FileGenerationsService generationsService;

    @Inject
    public FileGenerationsResource(FileGenerationsService generationsService)
    {
        this.generationsService = generationsService;
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(GET_VERSION_FILE_GENERATION)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileGenerations(@PathParam("groupId") String groupId,
                                                   @PathParam("artifactId") String artifactId,
                                                   @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                                   @Context Request request)
    {
        return handle(GET_VERSION_FILE_GENERATION, () -> this.generationsService.getFileGenerations(groupId, artifactId, versionId), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}/{elementPath}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileGenerationsByElementPath(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT)String versionId,
                                                                @PathParam("elementPath") String elementPath,
                                                                @Context Request request)
    {
        return handle(GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH, () -> this.generationsService.getFileGenerationsByElementPath(groupId, artifactId, versionId, elementPath), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}/file/{filePath}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileGenerationsByFilePath(@PathParam("groupId") String groupId,
                                                             @PathParam("artifactId") String artifactId,
                                                             @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId, @PathParam("filePath") String filePath, @Context Request request)
    {
        return handle(GET_VERSION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getFileGenerationsByFilePath(groupId, artifactId, versionId, filePath), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @GET
    @Path("/generationFileContent/{groupId}/{artifactId}/versions/{versionId}/file/{filePath}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_CONTENT)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFileGenerationContentByFilePath(@PathParam("groupId") String groupId,
                                                           @PathParam("artifactId") String artifactId,
                                                           @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId, @PathParam("filePath") String filePath, @Context Request request)
    {
        return handle(GET_VERSION_FILE_GENERATION_CONTENT, () -> this.generationsService.getFileGenerationContentByFilePath(groupId, artifactId, versionId, filePath), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/{versionId}/types/{type}")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_BY_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFileGenerations(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") @ApiParam("a valid version string: x.y.z, master-SNAPSHOT") String versionId, @PathParam("type") String type, @Context Request request)
    {
        return handle(ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_BY_TYPE, () -> this.generationsService.findByType(groupId, artifactId, versionId, type), request, () -> EtagBuilder.create().withGAV(groupId, artifactId, versionId).build());
    }

}

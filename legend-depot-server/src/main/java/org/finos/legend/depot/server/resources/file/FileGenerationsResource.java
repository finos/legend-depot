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

package org.finos.legend.depot.server.resources.file;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.generation.file.FileGenerationsService;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION_BY_FILEPATH;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION_ENTITIES;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_BY_FILEPATH;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_VERSION_FILE_GENERATION_ENTITIES;

@Path("")
@Api("Generations")
public class FileGenerationsResource extends BaseResource
{

    private final FileGenerationsService generationsService;

    @Inject
    public FileGenerationsResource(FileGenerationsService generationsService)
    {
        this.generationsService = generationsService;
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/latest/generations")
    @ApiOperation(GET_REVISION_FILE_GENERATION_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entity> getLatestGenerations(@PathParam("groupId") String groupId,
                                             @PathParam("artifactId") String artifactId)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION_ENTITIES, () -> this.generationsService.getLatestGenerations(groupId, artifactId));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/{versionId}/generations")
    @ApiOperation(GET_VERSION_FILE_GENERATION_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Entity> getVersionGenerationsEntities(@PathParam("groupId") String groupId,
                                                     @PathParam("artifactId") String artifactId,
                                                     @PathParam("versionId") String versionId)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_ENTITIES, () -> this.generationsService.getGenerations(groupId, artifactId, versionId));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest")
    @ApiOperation(GET_REVISION_FILE_GENERATION)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileGeneration> getLatestFileGenerations(@PathParam("groupId") String groupId,
                                                          @PathParam("artifactId") String artifactId)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION, () -> this.generationsService.getLatestFileGenerations(groupId, artifactId));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest/{elementPath}")
    @ApiOperation(GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileGeneration> getLatestFileGenerationsByElementPath(@PathParam("groupId") String groupId,
                                                               @PathParam("artifactId") String artifactId,
                                                               @PathParam("elementPath") String elementPath)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH, () -> this.generationsService.getLatestFileGenerationsByElementPath(groupId, artifactId, elementPath));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest/file/{filePath}")
    @ApiOperation(GET_REVISION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<FileGeneration> getLatestFileGenerationsByFilePath(@PathParam("groupId") String groupId,
                                                                   @PathParam("artifactId") String artifactId,
                                                                   @PathParam("filePath") String filePath)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getLatestFileGenerationsByFilePath(groupId, artifactId, filePath));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}")
    @ApiOperation(GET_VERSION_FILE_GENERATION)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileGeneration> getFileGenerations(@PathParam("groupId") String groupId,
                                                   @PathParam("artifactId") String artifactId,
                                                   @PathParam("versionId") String versionId)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION, () -> this.generationsService.getFileGenerations(groupId, artifactId, versionId));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}/{elementPath}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileGeneration> getFileGenerationsByElementPath(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("versionId") String versionId, @PathParam("elementPath") String elementPath)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH, () -> this.generationsService.getFileGenerationsByElementPath(groupId, artifactId, versionId, elementPath));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}/file/{filePath}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<FileGeneration> getFileGenerationsByFilePath(@PathParam("groupId") String groupId,
                                                             @PathParam("artifactId") String artifactId,
                                                             @PathParam("versionId") String versionId, @PathParam("filePath") String filePath)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getFileGenerationsByFilePath(groupId, artifactId, versionId, filePath));
    }

    @GET
    @Path("/generationFileContent/{groupId}/{artifactId}/versions/{versionId}/file/{filePath}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> getFileGenerationContentByFilePath(@PathParam("groupId") String groupId,
                                                           @PathParam("artifactId") String artifactId,
                                                           @PathParam("versionId") String versionId, @PathParam("filePath") String filePath)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getFileGenerationContentByFilePath(groupId, artifactId, versionId, filePath));
    }

    @GET
    @Path("/generationFileContent/{groupId}/{artifactId}/latest/file/{filePath}")
    @ApiOperation(GET_REVISION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> getLatestFileGenerationContentByFilePath(@PathParam("groupId") String groupId,
                                                                 @PathParam("artifactId") String artifactId,
                                                                 @PathParam("filePath") String filePath)
    {
        QueryMetricsContainer.record(groupId, artifactId,VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getLatestFileGenerationContentByFilePath(groupId, artifactId, filePath));
    }


}

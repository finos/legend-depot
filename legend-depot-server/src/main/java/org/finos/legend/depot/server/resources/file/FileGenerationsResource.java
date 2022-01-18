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

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.*;

@Path("")
@Api("File Generations")
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
    public List<Entity> getLatestGenerationsEntities(@PathParam("groupId") String groupId,
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
    public List<FileGeneration> getFileGenerationEntities(@PathParam("groupId") String groupId,
                                                          @PathParam("artifactId") String artifactId)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION, () -> this.generationsService.getLatestFileGenerations(groupId, artifactId));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest/{path}")
    @ApiOperation(GET_REVISION_FILE_GENERATION_BY_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileGeneration> getLatestFileGenerationsByPath(@PathParam("groupId") String groupId,
                                                               @PathParam("artifactId") String artifactId,
                                                               @PathParam("path") String path)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION_BY_PATH, () -> this.generationsService.getLatestFileGenerationsByPath(groupId, artifactId, path));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest/file/{file}")
    @ApiOperation(GET_REVISION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<FileGeneration> getLatestFileGenerationsByFile(@PathParam("groupId") String groupId,
                                                                   @PathParam("artifactId") String artifactId,
                                                                   @PathParam("file") String file)
    {
        QueryMetricsContainer.record(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_REVISION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getLatestFileGenerationsByFile(groupId, artifactId, file));
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
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}/{path}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileGeneration> getFileGenerationsByPath(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("versionId") String versionId, @PathParam("path") String path)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_BY_PATH, () -> this.generationsService.getFileGenerationsByPath(groupId, artifactId, versionId, path));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/versions/{versionId}/file/{file}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<FileGeneration> getFileGenerationsByFile(@PathParam("groupId") String groupId,
                                                             @PathParam("artifactId") String artifactId,
                                                             @PathParam("versionId") String versionId, @PathParam("file") String file)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getFileGenerationsByFile(groupId, artifactId, versionId, file));
    }

    @GET
    @Path("/generationFileContent/{groupId}/{artifactId}/versions/{versionId}/file/{file}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> getFileGenerationContentByFile(@PathParam("groupId") String groupId,
                                                           @PathParam("artifactId") String artifactId,
                                                           @PathParam("versionId") String versionId, @PathParam("file") String file)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_VERSION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getFileGenerationContentByFile(groupId, artifactId, versionId, file));
    }

    @GET
    @Path("/generationFileContent/{groupId}/{artifactId}/latest/file/{file}")
    @ApiOperation(GET_VERSION_FILE_GENERATION_BY_FILEPATH)
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> getLatestFileGenerationContentByFile(@PathParam("groupId") String groupId,
                                                                 @PathParam("artifactId") String artifactId,
                                                                 @PathParam("file") String file)
    {
        QueryMetricsContainer.record(groupId, artifactId,VersionValidator.MASTER_SNAPSHOT);
        return handle(GET_VERSION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getLatestFileGenerationContentByFile(groupId, artifactId, file));
    }


}
